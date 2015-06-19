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
import java.util.List;

import org.apache.directory.studio.ldifparser.model.LdifEOFPart;
import org.apache.directory.studio.ldifparser.model.LdifEnumeration;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.LdifInvalidPart;
import org.apache.directory.studio.ldifparser.model.LdifPart;
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
        scanner = new LdifScanner();
    }


    /**
     * Parse a Ldif String. It will be stored in a LdifFile.
     * 
     * @param ldif The String to parse
     * @return The resulting LdifFile
     */
    public LdifFile parse( String ldif )
    {
        LdifFile model = new LdifFile();
        
        if ( ldif != null )
        {
            LdifEnumeration enumeration = parse( new StringReader( ldif ) );
            
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
        scanner.setLdif( ldifReader );

        LdifEnumeration enumeration = new LdifEnumeration()
        {

            private List<LdifContainer> containerList = new ArrayList<LdifContainer>();

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
                    bodyParsed = ( headerParsed && 
                        !bodyParsed && 
                        !checkAndParseComment( model ) &&   // parse comment lines
                        !checkAndParseRecord( model ) &&    // parse record
                        !checkAndParseOther( model ) );      // parse unknown

                    // parse footer
                    if ( headerParsed && bodyParsed && !footerParsed )
                    {
                        checkAndParseComment( model );
                        footerParsed = true;
                    }

                    List<LdifContainer> containers = model.getContainers();
                    containerList.addAll( containers );
                    
                    return !containerList.isEmpty() && !( containers.get( 0 ) instanceof LdifEOFContainer );
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
                    return containerList.remove( 0 );
                }
                else
                {
                    return null;
                }
            }
        };

        return enumeration;
    }


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
        LdifToken dnSpecToken = scanner.matchDnSpec();
        
        if ( dnSpecToken == null )
        {
            return false;
        }

        // get Dn
        LdifToken dnValueTypeToken = null;
        LdifToken dnToken = null;
        LdifToken dnSepToken = null;
        dnValueTypeToken = scanner.matchValueType();
        
        if ( dnValueTypeToken != null )
        {
            dnToken = scanner.matchValue();
            
            if ( dnToken != null )
            {
                dnSepToken = scanner.matchSep();
            }
        }
        
        LdifDnLine dnLine = new LdifDnLine( dnSpecToken.getOffset(), getValueOrNull( dnSpecToken ),
            getValueOrNull( dnValueTypeToken ), getValueOrNull( dnToken ), getValueOrNull( dnSepToken ) );
        LdifToken dnErrorToken = null;
        
        if ( dnSepToken == null )
        {
            dnErrorToken = scanner.matchCleanupLine();
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
            List<LdifPart> partList = new ArrayList<LdifPart>();
            
            if ( dnErrorToken != null )
            {
                partList.add( new LdifInvalidPart( dnErrorToken.getOffset(), dnErrorToken.getValue() ) );
            }
            
            for ( LdifCommentLine ldifCommentLine : commentLines )
            {
                partList.add( ldifCommentLine );
            }
            
            if ( controlLine != null )
            {
                partList.add( controlLine );
                
                if ( !controlLine.isValid() )
                {
                    LdifToken errorToken = cleanupLine();
                    
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
                
                for ( LdifCommentLine ldifCommentLine : commentLines )
                {
                    partList.add( ldifCommentLine );
                }

                controlLine = getControlLine();
                
                if ( controlLine != null )
                {
                    partList.add( controlLine );
                    
                    if ( !controlLine.isValid() )
                    {
                        LdifToken errorToken = cleanupLine();
                        
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
            
            for ( LdifCommentLine ldifCommentLine : commentLines )
            {
                record.addComment( ldifCommentLine );
            }
            
            parseAttrValRecord( record );
            model.addContainer( record );
        }

        return true;
    }


    private void append( LdifChangeRecord record, List<LdifPart> partList )
    {
        for ( LdifPart ldifPart : partList )
        {
            if ( ldifPart instanceof LdifCommentLine )
            { 
                record.addComment( ( LdifCommentLine ) ldifPart );
            }
            
            if ( ldifPart instanceof LdifControlLine )
            {
                record.addControl( ( LdifControlLine ) ldifPart );
            }
            
            if ( ldifPart instanceof LdifInvalidPart )
            {
                record.addInvalid( ( LdifInvalidPart ) ldifPart );
            }
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
                newrdnSpecToken = scanner.matchNewrdnSpec();
            }
            
            if ( !deleteoldrdnRead && newrdnSpecToken == null )
            {
                deleteoldrdnSpecToken = scanner.matchDeleteoldrdnSpec();
            }
            
            if ( !newsuperiorRead && newrdnSpecToken == null && newsuperiorSpecToken == null )
            {
                newsuperiorSpecToken = scanner.matchNewsuperiorSpec();
            }
            

            if ( newrdnSpecToken != null )
            {
                // read newrdn line
                newrdnRead = true;
                LdifToken newrdnValueTypeToken = scanner.matchValueType();
                LdifToken newrdnValueToken = scanner.matchValue();
                LdifToken newrdnSepToken = null;
                
                if ( newrdnValueTypeToken != null || newrdnValueToken != null )
                {
                    newrdnSepToken = scanner.matchSep();
                }

                LdifNewrdnLine newrdnLine = new LdifNewrdnLine( newrdnSpecToken.getOffset(),
                    getValueOrNull( newrdnSpecToken ), getValueOrNull( newrdnValueTypeToken ),
                    getValueOrNull( newrdnValueToken ), getValueOrNull( newrdnSepToken ) );
                record.setNewrdn( newrdnLine );

                if ( newrdnSepToken == null )
                {
                    cleanupLine( record );
                }
            }
            else if ( deleteoldrdnSpecToken != null )
            {
                // read deleteoldrdnline
                deleteoldrdnRead = true;
                LdifToken deleteoldrdnValueTypeToken = scanner.matchValueType();
                LdifToken deleteoldrdnValueToken = scanner.matchValue();
                LdifToken deleteoldrdnSepToken = null;
                
                if ( deleteoldrdnValueTypeToken != null || deleteoldrdnValueToken != null )
                {
                    deleteoldrdnSepToken = scanner.matchSep();
                }

                LdifDeloldrdnLine deloldrdnLine = new LdifDeloldrdnLine( deleteoldrdnSpecToken.getOffset(),
                    getValueOrNull( deleteoldrdnSpecToken ), getValueOrNull( deleteoldrdnValueTypeToken ),
                    getValueOrNull( deleteoldrdnValueToken ), getValueOrNull( deleteoldrdnSepToken ) );
                record.setDeloldrdn( deloldrdnLine );

                if ( deleteoldrdnSepToken == null )
                {
                    cleanupLine( record );
                }
            }
            else if ( newsuperiorSpecToken != null )
            {
                // read newsuperior line
                newsuperiorRead = true;
                LdifToken newsuperiorValueTypeToken = scanner.matchValueType();
                LdifToken newsuperiorValueToken = scanner.matchValue();
                LdifToken newsuperiorSepToken = null;
                
                if ( newsuperiorValueTypeToken != null || newsuperiorValueToken != null )
                {
                    newsuperiorSepToken = scanner.matchSep();
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
            LdifToken modSpecTypeSpecToken = scanner.matchModTypeSpec();
            
            if ( modSpecTypeSpecToken != null )
            {
                // read mod type line
                LdifToken modSpecTypeValueTypeToken = null;
                LdifToken modSpecTypeAttributeDescriptionToken = null;
                LdifToken sepToken = null;
                modSpecTypeValueTypeToken = scanner.matchValueType();
                
                if ( modSpecTypeValueTypeToken != null )
                {
                    modSpecTypeAttributeDescriptionToken = scanner.matchAttributeDescription();
                    
                    if ( modSpecTypeAttributeDescriptionToken != null )
                    {
                        sepToken = scanner.matchSep();
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
                        if ( "".equals( line.getRawNewLine() ) ) //$NON-NLS-1$
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
                LdifToken modSpecSepToken = scanner.matchModSep();
                
                if ( modSpecSepToken != null )
                {
                    LdifToken modSpecSepSepToken = scanner.matchSep();
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
                if ( "".equals( line.getRawNewLine() ) ) //$NON-NLS-1$
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
        }
        while ( true );
    }


    private boolean checkAndParseEndOfRecord( LdifRecord record )
    {
        // check end of record
        LdifToken eorSepToken = scanner.matchSep();
        
        if ( eorSepToken != null )
        {
            record.finish( new LdifSepLine( eorSepToken.getOffset(), getValueOrNull( eorSepToken ) ) );
            
            return true;
        }

        // check end of file
        LdifToken eofToken = scanner.matchEOF();
        
        if ( eofToken != null )
        {
            record.finish( new LdifEOFPart( eofToken.getOffset() ) );
            return true;
        }
        
        return false;
    }


    private boolean checkAndParseComment( LdifRecord record )
    {
        LdifToken commentToken = scanner.matchComment();
        
        if ( commentToken != null )
        {
            while ( commentToken != null )
            {
                LdifToken sepToken = scanner.matchSep();
                record.addComment( new LdifCommentLine( commentToken.getOffset(), getValueOrNull( commentToken ),
                    getValueOrNull( sepToken ) ) );
                commentToken = scanner.matchComment();
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }


    private boolean checkAndParseOther( LdifRecord record )
    {
        LdifToken otherToken = scanner.matchOther();
        
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
        LdifToken versionSpecToken = scanner.matchVersionSpec();
        
        if ( versionSpecToken != null )
        {

            LdifToken versionTypeToken = null;
            LdifToken versionToken = null;
            LdifToken sepToken = null;
            versionTypeToken = scanner.matchValueType();
            
            if ( versionTypeToken != null )
            {
                versionToken = scanner.matchNumber();
                if ( versionToken != null )
                {
                    sepToken = scanner.matchSep();
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
        LdifToken sepToken = scanner.matchSep();
        LdifToken commentToken = scanner.matchComment();

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
                        LdifToken commentSepToken = scanner.matchSep();
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

                        commentToken = scanner.matchComment();
                    }
                    
                    model.addContainer( commentContainer );
                }

                sepToken = scanner.matchSep();
                commentToken = scanner.matchComment();
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
        LdifToken token = scanner.matchOther();
        
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
        LdifToken controlSpecToken = scanner.matchControlSpec();
        
        if ( controlSpecToken != null )
        {
            LdifToken controlTypeToken = null;
            LdifToken oidToken = null;
            LdifToken criticalityToken = null;
            LdifToken valueTypeToken = null;
            LdifToken valueToken = null;
            LdifToken sepToken = null;
            controlTypeToken = scanner.matchValueType();
            
            if ( controlTypeToken != null )
            {
                oidToken = scanner.matchOid();
                if ( oidToken != null )
                {
                    criticalityToken = scanner.matchCriticality();
                    valueTypeToken = scanner.matchValueType();
                    
                    if ( valueTypeToken != null )
                    {
                        valueToken = scanner.matchValue();
                    }
                    
                    sepToken = scanner.matchSep();
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
        LdifToken changeTypeSpecToken = scanner.matchChangeTypeSpec();
        
        if ( changeTypeSpecToken != null )
        {
            LdifToken changeTypeTypeToken = null;
            LdifToken changeTypeToken = null;
            LdifToken sepToken = null;
            changeTypeTypeToken = scanner.matchValueType();
            
            if ( changeTypeTypeToken != null )
            {
                changeTypeToken = scanner.matchChangeType();
                
                if ( changeTypeToken != null )
                {
                    sepToken = scanner.matchSep();
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
        LdifToken attrToken = scanner.matchAttributeDescription();
        
        if ( attrToken != null )
        {
            LdifToken valueTypeToken = null;
            LdifToken valueToken = null;
            LdifToken sepToken = null;
            valueTypeToken = scanner.matchValueType();
            
            if ( valueTypeToken != null )
            {
                valueToken = scanner.matchValue();
                
                if ( valueToken != null )
                {
                    sepToken = scanner.matchSep();
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
        List<LdifCommentLine> list = new ArrayList<LdifCommentLine>( 1 );
        LdifToken commentToken = scanner.matchComment();
        
        while ( commentToken != null )
        {
            LdifToken sepToken = scanner.matchSep();
            list
                .add( new LdifCommentLine( commentToken.getOffset(), commentToken.getValue(), getValueOrNull( sepToken ) ) );

            commentToken = scanner.matchComment();
        }
        
        return list.toArray( new LdifCommentLine[list.size()] );
    }


    private void cleanupLine( LdifContainer container )
    {
        LdifToken errorToken = scanner.matchCleanupLine();
        
        if ( errorToken != null )
        {
            container.addInvalid( new LdifInvalidPart( errorToken.getOffset(), errorToken.getValue() ) );
        }
    }


    private LdifToken cleanupLine()
    {
        LdifToken errorToken = scanner.matchCleanupLine();
        return errorToken;
    }


    private static String getValueOrNull( LdifToken token )
    {
        return token == null ? null : token.getValue();
    }

}
