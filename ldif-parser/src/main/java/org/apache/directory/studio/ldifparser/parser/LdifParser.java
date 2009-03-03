/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.studio.ldifparser.parser;


import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldifparser.model.LdifEOFPart;
import org.apache.directory.studio.ldifparser.model.LdifEnumeration;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.LdifInvalidPart;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeDeleteRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifCommentContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifEOFContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifInvalidContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifSepContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifVersionContainer;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifCommentLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDeloldrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewsuperiorLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifVersionLine;


public class LdifParser
{

    private LdifScanner scanner;


    public LdifParser()
    {
        this.scanner = new LdifScanner();
    }


    public LdifFile parse( String ldif )
    {
        LdifFile model = new LdifFile();
        if ( ldif != null )
        {
            LdifEnumeration enumeration = this.parse( new StringReader( ldif ) );
            try
            {
                while ( enumeration.hasNext() )
                {
                    LdifContainer container = enumeration.next();
                    model.addContainer( container );
                }
            }
            catch ( Exception e )
            {
            }
        }
        return model;
    }


    public LdifEnumeration parse( Reader ldifReader )
    {

        this.scanner.setLdif( ldifReader );

        LdifEnumeration enumeration = new LdifEnumeration()
        {

            private List containerList = new ArrayList();

            private boolean headerParsed = false;

            private boolean bodyParsed = false;

            private boolean footerParsed = false;


            public boolean hasNext()
            {
                if ( containerList.isEmpty() )
                {

                    LdifFile model = new LdifFile();

                    // parse header
                    if ( !headerParsed )
                    {
                        checkAndParseComment( model );
                        checkAndParseVersion( model );
                        checkAndParseComment( model );
                        headerParsed = true;
                    }

                    // parse body (in a loop)
                    if ( headerParsed && !bodyParsed )
                    {
                        // parse comment lines
                        if ( !checkAndParseComment( model ) )
                        {
                            // parse record
                            if ( !checkAndParseRecord( model ) )
                            {
                                // parse unknown
                                if ( !checkAndParseOther( model ) )
                                {
                                    // end of body
                                    bodyParsed = true;
                                }
                            }
                        }
                    }

                    // parse footer
                    if ( headerParsed && bodyParsed && !footerParsed )
                    {
                        checkAndParseComment( model );
                        footerParsed = true;
                    }

                    LdifContainer[] containers = model.getContainers();
                    this.containerList.addAll( Arrays.asList( containers ) );
                    return !containerList.isEmpty() && !( containers[0] instanceof LdifEOFContainer );

                }
                else
                {
                    return true;
                }
            }


            public LdifContainer next()
            {
                if ( hasNext() )
                {
                    return ( LdifContainer ) this.containerList.remove( 0 );
                }
                else
                {
                    return null;
                }
            }
        };

        return enumeration;
    }


    // public LdifEnumeration parse(Reader ldifReader) {
    //		
    // this.scanner.setLdif(ldifReader);
    //		
    // LdifEnumeration enumeration = new LdifEnumeration(){
    //
    // private List containerList = new ArrayList();
    //			
    // public boolean
    // hasNext(org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor
    // monitor) {
    // if(containerList.isEmpty()) {
    // LdifFile model = parseFile();
    // LdifContainer[] containers = model.getContainers();
    // this.containerList.addAll(Arrays.asList(containers));
    // return !containerList.isEmpty() && !(containers[0] instanceof
    // LdifEOFContainer);
    // }
    // else {
    // return true;
    // }
    // }
    //
    // public LdifContainer
    // next(org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor
    // monitor) {
    // if(hasNext(monitor)) {
    // return (LdifContainer)this.containerList.remove(0);
    // }
    // else {
    // return null;
    // }
    // }
    // };
    //		
    // return enumeration;
    // }
    //
    // private LdifFile parseFile() {
    //
    // LdifFile model = new LdifFile();
    //		
    // // start comment-version-comment
    // checkAndParseComment(model);
    // checkAndParseVersion(model);
    // checkAndParseComment(model);
    //		
    // parseRecords(model);
    //		
    // checkAndParseComment(model);
    //		
    // return model;
    // }
    //
    // private void parseRecords(LdifFile model) {
    // do {
    // // parse comment lines
    // if(!checkAndParseComment(model)) {
    // // parse record
    // if(!checkAndParseRecord(model)) {
    // // parse unknown
    // if(!checkAndParseOther(model)) {
    // return;
    // }
    // }
    // }
    // }
    // while(true);
    //		
    // }

    /**
     * Checks for version line. If version line is present it is parsed and
     * added to the given model.
     * 
     * @param model
     *                the model
     * @return true if version line was added to the model, false otherwise
     */
    private boolean checkAndParseRecord( LdifFile model )
    {

        // record starts with dn-spec
        LdifToken dnSpecToken = this.scanner.matchDnSpec();
        if ( dnSpecToken == null )
        {
            return false;
        }

        // get DN
        LdifToken dnValueTypeToken = null;
        LdifToken dnToken = null;
        LdifToken dnSepToken = null;
        dnValueTypeToken = this.scanner.matchValueType();
        if ( dnValueTypeToken != null )
        {
            dnToken = this.scanner.matchValue();
            if ( dnToken != null )
            {
                dnSepToken = this.scanner.matchSep();
            }
        }
        LdifDnLine dnLine = new LdifDnLine( dnSpecToken.getOffset(), getValueOrNull( dnSpecToken ),
            getValueOrNull( dnValueTypeToken ), getValueOrNull( dnToken ), getValueOrNull( dnSepToken ) );
        LdifToken dnErrorToken = null;
        if ( dnSepToken == null )
        {
            dnErrorToken = this.scanner.matchCleanupLine();
        }

        // save comment lines after dns
        LdifCommentLine[] commentLines = getCommentLines();

        // check record type: to decide the record type we need the next token
        // first check keywords 'control' and 'changetype'
        LdifControlLine controlLine = getControlLine();
        LdifChangeTypeLine changeTypeLine = getChangeTypeLine();
        if ( controlLine != null || changeTypeLine != null )
        {

            LdifChangeRecord record = null;

            // save all parts before changetype line
            List partList = new ArrayList();
            if ( dnErrorToken != null )
            {
                partList.add( new LdifInvalidPart( dnErrorToken.getOffset(), dnErrorToken.getValue() ) );
            }
            for ( int i = 0; i < commentLines.length; i++ )
            {
                partList.add( commentLines[i] );
            }
            if ( controlLine != null )
            {
                partList.add( controlLine );
                if ( !controlLine.isValid() )
                {
                    LdifToken errorToken = this.cleanupLine();
                    if ( errorToken != null )
                    {
                        partList.add( new LdifInvalidPart( errorToken.getOffset(), errorToken.getValue() ) );
                    }
                }
            }

            // save comments and controls before changetype line
            while ( changeTypeLine == null && ( commentLines.length > 0 || controlLine != null ) )
            {

                commentLines = getCommentLines();
                for ( int i = 0; i < commentLines.length; i++ )
                {
                    partList.add( commentLines[i] );
                }

                controlLine = getControlLine();
                if ( controlLine != null )
                {
                    partList.add( controlLine );
                    if ( !controlLine.isValid() )
                    {
                        LdifToken errorToken = this.cleanupLine();
                        if ( errorToken != null )
                        {
                            partList.add( new LdifInvalidPart( errorToken.getOffset(), errorToken.getValue() ) );
                        }
                    }
                }

                changeTypeLine = getChangeTypeLine();
            }

            if ( changeTypeLine != null )
            {

                if ( changeTypeLine.isAdd() )
                {
                    record = new LdifChangeAddRecord( dnLine );
                    append( record, partList );
                    record.setChangeType( changeTypeLine );
                    if ( !changeTypeLine.isValid() )
                    {
                        this.cleanupLine( record );
                    }
                    parseAttrValRecord( record );
                }
                else if ( changeTypeLine.isDelete() )
                {
                    record = new LdifChangeDeleteRecord( dnLine );
                    append( record, partList );
                    record.setChangeType( changeTypeLine );
                    if ( !changeTypeLine.isValid() )
                    {
                        this.cleanupLine( record );
                    }
                    parseChangeDeleteRecord( record );
                }
                else if ( changeTypeLine.isModify() )
                {
                    record = new LdifChangeModifyRecord( dnLine );
                    append( record, partList );
                    record.setChangeType( changeTypeLine );
                    if ( !changeTypeLine.isValid() )
                    {
                        this.cleanupLine( record );
                    }
                    parseChangeModifyRecord( ( LdifChangeModifyRecord ) record );
                }
                else if ( changeTypeLine.isModDn() )
                {
                    record = new LdifChangeModDnRecord( dnLine );
                    append( record, partList );
                    record.setChangeType( changeTypeLine );
                    if ( !changeTypeLine.isValid() )
                    {
                        this.cleanupLine( record );
                    }
                    parseChangeModDnRecord( ( LdifChangeModDnRecord ) record );
                }
                else
                {
                    record = new LdifChangeRecord( dnLine );
                    append( record, partList );
                    record.setChangeType( changeTypeLine );
                    if ( !changeTypeLine.isValid() )
                    {
                        this.cleanupLine( record );
                    }
                }
            }
            else
            {
                record = new LdifChangeRecord( dnLine );
                append( record, partList );
            }

            model.addContainer( record );
        }
        else
        {
            // match attr-val-record
            LdifContentRecord record = new LdifContentRecord( dnLine );
            if ( dnErrorToken != null )
            {
                record.addInvalid( new LdifInvalidPart( dnErrorToken.getOffset(), dnErrorToken.getValue() ) );
            }
            for ( int i = 0; i < commentLines.length; i++ )
            {
                record.addComment( commentLines[i] );
            }
            parseAttrValRecord( record );
            model.addContainer( record );
        }

        return true;
    }


    private void append( LdifChangeRecord record, List partList )
    {
        for ( Iterator it = partList.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifCommentLine )
                record.addComment( ( LdifCommentLine ) o );
            if ( o instanceof LdifControlLine )
                record.addControl( ( LdifControlLine ) o );
            if ( o instanceof LdifInvalidPart )
                record.addInvalid( ( LdifInvalidPart ) o );
        }
    }


    private void parseChangeDeleteRecord( LdifRecord record )
    {
        do
        {
            if ( checkAndParseEndOfRecord( record ) )
            {
                return;
            }

            if ( !checkAndParseComment( record ) && !checkAndParseOther( record ) )
            {
                return;
            }
        }
        while ( true );
    }


    private void parseChangeModDnRecord( LdifChangeModDnRecord record )
    {
        boolean newrdnRead = false;
        boolean deleteoldrdnRead = false;
        boolean newsuperiorRead = false;

        do
        {
            if ( checkAndParseEndOfRecord( record ) )
            {
                return;
            }

            // comments
            checkAndParseComment( record );

            LdifToken newrdnSpecToken = null;
            LdifToken deleteoldrdnSpecToken = null;
            LdifToken newsuperiorSpecToken = null;
            if ( !newrdnRead )
            {
                newrdnSpecToken = this.scanner.matchNewrdnSpec();
            }
            if ( !deleteoldrdnRead && newrdnSpecToken == null )
            {
                deleteoldrdnSpecToken = this.scanner.matchDeleteoldrdnSpec();
            }
            if ( !newsuperiorRead && newrdnSpecToken == null && newsuperiorSpecToken == null )
            {
                newsuperiorSpecToken = this.scanner.matchNewsuperiorSpec();
            }

            if ( newrdnSpecToken != null )
            {
                // read newrdn line
                newrdnRead = true;
                LdifToken newrdnValueTypeToken = this.scanner.matchValueType();
                LdifToken newrdnValueToken = this.scanner.matchValue();
                LdifToken newrdnSepToken = null;
                if ( newrdnValueTypeToken != null || newrdnValueToken != null )
                {
                    newrdnSepToken = this.scanner.matchSep();
                }

                LdifNewrdnLine newrdnLine = new LdifNewrdnLine( newrdnSpecToken.getOffset(),
                    getValueOrNull( newrdnSpecToken ), getValueOrNull( newrdnValueTypeToken ),
                    getValueOrNull( newrdnValueToken ), getValueOrNull( newrdnSepToken ) );
                record.setNewrdn( newrdnLine );

                if ( newrdnSepToken == null )
                {
                    this.cleanupLine( record );
                }
            }
            else if ( deleteoldrdnSpecToken != null )
            {
                // read deleteoldrdnline
                deleteoldrdnRead = true;
                LdifToken deleteoldrdnValueTypeToken = this.scanner.matchValueType();
                LdifToken deleteoldrdnValueToken = this.scanner.matchValue();
                LdifToken deleteoldrdnSepToken = null;
                if ( deleteoldrdnValueTypeToken != null || deleteoldrdnValueToken != null )
                {
                    deleteoldrdnSepToken = this.scanner.matchSep();
                }

                LdifDeloldrdnLine deloldrdnLine = new LdifDeloldrdnLine( deleteoldrdnSpecToken.getOffset(),
                    getValueOrNull( deleteoldrdnSpecToken ), getValueOrNull( deleteoldrdnValueTypeToken ),
                    getValueOrNull( deleteoldrdnValueToken ), getValueOrNull( deleteoldrdnSepToken ) );
                record.setDeloldrdn( deloldrdnLine );

                if ( deleteoldrdnSepToken == null )
                {
                    this.cleanupLine( record );
                }
            }
            else if ( newsuperiorSpecToken != null )
            {
                // read newsuperior line
                newsuperiorRead = true;
                LdifToken newsuperiorValueTypeToken = this.scanner.matchValueType();
                LdifToken newsuperiorValueToken = this.scanner.matchValue();
                LdifToken newsuperiorSepToken = null;
                if ( newsuperiorValueTypeToken != null || newsuperiorValueToken != null )
                {
                    newsuperiorSepToken = this.scanner.matchSep();
                }

                LdifNewsuperiorLine newsuperiorLine = new LdifNewsuperiorLine( newsuperiorSpecToken.getOffset(),
                    getValueOrNull( newsuperiorSpecToken ), getValueOrNull( newsuperiorValueTypeToken ),
                    getValueOrNull( newsuperiorValueToken ), getValueOrNull( newsuperiorSepToken ) );
                record.setNewsuperior( newsuperiorLine );

                if ( newsuperiorSepToken == null )
                {
                    this.cleanupLine( record );
                }
            }
            else
            {
                if ( !checkAndParseComment( record ) && !checkAndParseOther( record ) )
                {
                    return;
                }
            }

            // comments
            checkAndParseComment( record );

            // eor
            checkAndParseEndOfRecord( record );
        }
        while ( true );
    }


    private void parseChangeModifyRecord( LdifChangeModifyRecord record )
    {

        do
        {
            if ( checkAndParseEndOfRecord( record ) )
            {
                return;
            }

            // match mod type
            LdifToken modSpecTypeSpecToken = this.scanner.matchModTypeSpec();
            if ( modSpecTypeSpecToken != null )
            {
                // read mod type line
                LdifToken modSpecTypeValueTypeToken = null;
                LdifToken modSpecTypeAttributeDescriptionToken = null;
                LdifToken sepToken = null;
                modSpecTypeValueTypeToken = this.scanner.matchValueType();
                if ( modSpecTypeValueTypeToken != null )
                {
                    modSpecTypeAttributeDescriptionToken = this.scanner.matchAttributeDescription();
                    if ( modSpecTypeAttributeDescriptionToken != null )
                    {
                        sepToken = this.scanner.matchSep();
                    }
                }
                LdifModSpecTypeLine modSpecTypeLine = new LdifModSpecTypeLine( modSpecTypeSpecToken.getOffset(),
                    getValueOrNull( modSpecTypeSpecToken ), getValueOrNull( modSpecTypeValueTypeToken ),
                    getValueOrNull( modSpecTypeAttributeDescriptionToken ), getValueOrNull( sepToken ) );
                LdifModSpec modSpec = new LdifModSpec( modSpecTypeLine );
                record.addModSpec( modSpec );

                // clean line
                if ( sepToken == null )
                {
                    this.cleanupLine( modSpec );
                }

                // comment
                checkAndParseComment( record );

                // read attr-val lines
                do
                {
                    LdifAttrValLine line = this.getAttrValLine();
                    if ( line != null )
                    {
                        modSpec.addAttrVal( line );

                        // clean line
                        if ( "".equals( line.getRawNewLine() ) )
                        {
                            this.cleanupLine( record );
                        }
                    }
                    else
                    {
                        if ( !checkAndParseComment( record ) )
                        {
                            break;
                        }
                    }
                }
                while ( true );

                // comments
                checkAndParseComment( record );

                // read sep line
                LdifToken modSpecSepToken = this.scanner.matchModSep();
                if ( modSpecSepToken != null )
                {
                    LdifToken modSpecSepSepToken = this.scanner.matchSep();
                    LdifModSpecSepLine modSpecSepLine = new LdifModSpecSepLine( modSpecSepToken.getOffset(),
                        getValueOrNull( modSpecSepToken ), getValueOrNull( modSpecSepSepToken ) );
                    modSpec.finish( modSpecSepLine );
                }
            }

            if ( modSpecTypeSpecToken == null )
            {
                if ( !checkAndParseComment( record ) && !checkAndParseOther( record ) )
                {
                    return;
                }
            }
        }
        while ( true );
    }


    private void parseAttrValRecord( LdifRecord record )
    {

        do
        {
            if ( checkAndParseEndOfRecord( record ) )
            {
                return;
            }

            // check attr-val line
            LdifAttrValLine line = this.getAttrValLine();
            if ( line != null )
            {
                if ( record instanceof LdifContentRecord )
                {
                    ( ( LdifContentRecord ) record ).addAttrVal( line );
                }
                else if ( record instanceof LdifChangeAddRecord )
                {
                    ( ( LdifChangeAddRecord ) record ).addAttrVal( line );
                }

                // clean line
                if ( "".equals( line.getRawNewLine() ) )
                {
                    this.cleanupLine( record );
                }
            }
            else
            {
                if ( !checkAndParseComment( record ) && !checkAndParseOther( record ) )
                {
                    return;
                }
            }

            //			
            // // check comment line
            // if(lineStartToken == null) {
            // lineStartToken = this.scanner.matchComment();
            // if(lineStartToken != null) {
            // LdifToken sepToken = this.scanner.matchSep();
            // record.addComment(new
            // LdifCommentLine(lineStartToken.getOffset(),
            // getValueOrNull(lineStartToken), getValueOrNull(sepToken)));
            // }
            // }
            //			
            // // unknown line
            // if(lineStartToken == null) {
            // lineStartToken = this.scanner.matchOther();
            // if(lineStartToken != null) {
            // record.addOther(new
            // LdifInvalidPart(lineStartToken.getOffset(),
            // lineStartToken.getValue()));
            // }
            // }

            // // end of file
            // if(lineStartToken == null) {
            // return;
            // }
        }
        while ( true );
    }


    private boolean checkAndParseEndOfRecord( LdifRecord record )
    {
        // check end of record
        LdifToken eorSepToken = this.scanner.matchSep();
        if ( eorSepToken != null )
        {
            record.finish( new LdifSepLine( eorSepToken.getOffset(), getValueOrNull( eorSepToken ) ) );
            return true;
        }

        // check end of file
        LdifToken eofToken = this.scanner.matchEOF();
        if ( eofToken != null )
        {
            record.finish( new LdifEOFPart( eofToken.getOffset() ) );
            return true;
        }
        return false;
    }


    private boolean checkAndParseComment( LdifRecord record )
    {
        LdifToken commentToken = this.scanner.matchComment();
        if ( commentToken != null )
        {
            LdifToken sepToken = this.scanner.matchSep();
            record.addComment( new LdifCommentLine( commentToken.getOffset(), getValueOrNull( commentToken ),
                getValueOrNull( sepToken ) ) );
            return true;
        }
        else
        {
            return false;
        }
    }


    private boolean checkAndParseOther( LdifRecord record )
    {
        LdifToken otherToken = this.scanner.matchOther();
        if ( otherToken != null )
        {
            record.addInvalid( new LdifInvalidPart( otherToken.getOffset(), otherToken.getValue() ) );
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Checks for version line. If version line is present it is parsed and
     * added to the given model.
     * 
     * @param model
     *                the model
     * @return true if version line was added to the model, false otherwise
     */
    private boolean checkAndParseVersion( LdifFile model )
    {
        LdifToken versionSpecToken = this.scanner.matchVersionSpec();
        if ( versionSpecToken != null )
        {

            LdifToken versionTypeToken = null;
            LdifToken versionToken = null;
            LdifToken sepToken = null;
            versionTypeToken = this.scanner.matchValueType();
            if ( versionTypeToken != null )
            {
                versionToken = this.scanner.matchNumber();
                if ( versionToken != null )
                {
                    sepToken = this.scanner.matchSep();
                }
            }

            LdifVersionContainer container = new LdifVersionContainer( new LdifVersionLine( versionSpecToken
                .getOffset(), getValueOrNull( versionSpecToken ), getValueOrNull( versionTypeToken ),
                getValueOrNull( versionToken ), getValueOrNull( sepToken ) ) );
            model.addContainer( container );

            // clean line
            if ( sepToken == null )
            {
                this.cleanupLine( container );
            }

            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Checks for comment lines or empty lines. If such lines are present
     * they are parsed and added to the given model.
     * 
     * @param model
     *                the model
     * @return true if comment or empty lines were added to the model, false
     *         otherwise
     */
    private boolean checkAndParseComment( LdifFile model )
    {
        LdifToken sepToken = this.scanner.matchSep();
        LdifToken commentToken = this.scanner.matchComment();

        if ( sepToken != null || commentToken != null )
        {

            while ( sepToken != null || commentToken != null )
            {

                if ( sepToken != null )
                {
                    LdifSepLine sepLine = new LdifSepLine( sepToken.getOffset(), getValueOrNull( sepToken ) );
                    LdifSepContainer sepContainer = new LdifSepContainer( sepLine );
                    model.addContainer( sepContainer );
                }

                if ( commentToken != null )
                {
                    LdifCommentContainer commentContainer = null;
                    while ( commentToken != null )
                    {
                        LdifToken commentSepToken = this.scanner.matchSep();
                        LdifCommentLine commentLine = new LdifCommentLine( commentToken.getOffset(),
                            getValueOrNull( commentToken ), getValueOrNull( commentSepToken ) );

                        if ( commentContainer == null )
                        {
                            commentContainer = new LdifCommentContainer( commentLine );
                        }
                        else
                        {
                            commentContainer.addComment( commentLine );
                        }

                        commentToken = this.scanner.matchComment();
                    }
                    model.addContainer( commentContainer );
                }

                sepToken = this.scanner.matchSep();
                commentToken = this.scanner.matchComment();
            }

            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Checks for other line. If such line is present it is parsed and added
     * to the given model.
     * 
     * @param model
     *                the model
     * @return always true except if EOF reached
     */
    private boolean checkAndParseOther( LdifFile model )
    {
        LdifToken token = this.scanner.matchOther();
        if ( token != null )
        {
            LdifInvalidPart unknownLine = new LdifInvalidPart( token.getOffset(), getValueOrNull( token ) );
            LdifInvalidContainer otherContainer = new LdifInvalidContainer( unknownLine );
            model.addContainer( otherContainer );
            return true;
        }
        else
        {
            return false;
        }
    }


    private LdifControlLine getControlLine()
    {

        LdifToken controlSpecToken = this.scanner.matchControlSpec();
        if ( controlSpecToken != null )
        {
            LdifToken controlTypeToken = null;
            LdifToken oidToken = null;
            LdifToken criticalityToken = null;
            LdifToken valueTypeToken = null;
            LdifToken valueToken = null;
            LdifToken sepToken = null;
            controlTypeToken = this.scanner.matchValueType();
            if ( controlTypeToken != null )
            {
                oidToken = this.scanner.matchOid();
                if ( oidToken != null )
                {
                    criticalityToken = this.scanner.matchCriticality();
                    valueTypeToken = this.scanner.matchValueType();
                    if ( valueTypeToken != null )
                    {
                        valueToken = this.scanner.matchValue();
                    }
                    sepToken = this.scanner.matchSep();
                }
            }

            LdifControlLine controlLine = new LdifControlLine( controlSpecToken.getOffset(),
                getValueOrNull( controlSpecToken ), getValueOrNull( controlTypeToken ), getValueOrNull( oidToken ),
                getValueOrNull( criticalityToken ), getValueOrNull( valueTypeToken ), getValueOrNull( valueToken ),
                getValueOrNull( sepToken ) );

            return controlLine;
        }

        return null;
    }


    private LdifChangeTypeLine getChangeTypeLine()
    {

        LdifToken changeTypeSpecToken = this.scanner.matchChangeTypeSpec();
        if ( changeTypeSpecToken != null )
        {
            LdifToken changeTypeTypeToken = null;
            LdifToken changeTypeToken = null;
            LdifToken sepToken = null;
            changeTypeTypeToken = this.scanner.matchValueType();
            if ( changeTypeTypeToken != null )
            {
                changeTypeToken = this.scanner.matchChangeType();
                if ( changeTypeToken != null )
                {
                    sepToken = this.scanner.matchSep();
                }
            }

            LdifChangeTypeLine ctLine = new LdifChangeTypeLine( changeTypeSpecToken.getOffset(),
                getValueOrNull( changeTypeSpecToken ), getValueOrNull( changeTypeTypeToken ),
                getValueOrNull( changeTypeToken ), getValueOrNull( sepToken ) );

            return ctLine;
        }

        return null;
    }


    private LdifAttrValLine getAttrValLine()
    {
        LdifToken attrToken = this.scanner.matchAttributeDescription();
        if ( attrToken != null )
        {
            LdifToken valueTypeToken = null;
            LdifToken valueToken = null;
            LdifToken sepToken = null;
            valueTypeToken = this.scanner.matchValueType();
            if ( valueTypeToken != null )
            {
                valueToken = this.scanner.matchValue();
                if ( valueToken != null )
                {
                    sepToken = this.scanner.matchSep();
                }
            }

            LdifAttrValLine line = new LdifAttrValLine( attrToken.getOffset(), getValueOrNull( attrToken ),
                getValueOrNull( valueTypeToken ), getValueOrNull( valueToken ), getValueOrNull( sepToken ) );

            return line;
        }

        return null;
    }


    private LdifCommentLine[] getCommentLines()
    {
        List list = new ArrayList( 1 );
        LdifToken commentToken = this.scanner.matchComment();
        while ( commentToken != null )
        {
            LdifToken sepToken = this.scanner.matchSep();
            list
                .add( new LdifCommentLine( commentToken.getOffset(), commentToken.getValue(), getValueOrNull( sepToken ) ) );

            commentToken = this.scanner.matchComment();
        }
        return ( LdifCommentLine[] ) list.toArray( new LdifCommentLine[list.size()] );
    }


    private void cleanupLine( LdifContainer container )
    {
        LdifToken errorToken = this.scanner.matchCleanupLine();
        if ( errorToken != null )
        {
            container.addInvalid( new LdifInvalidPart( errorToken.getOffset(), errorToken.getValue() ) );
        }
    }


    private LdifToken cleanupLine()
    {
        LdifToken errorToken = this.scanner.matchCleanupLine();
        return errorToken;
    }


    private static String getValueOrNull( LdifToken token )
    {
        return token == null ? null : token.getValue();
    }

}
