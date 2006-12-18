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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifFile;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContainer;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.parser.LdifParser;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.text.LdifExternalAnnotationModel;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;


public class LdifDocumentProvider extends TextFileDocumentProvider implements IDocumentListener
{

    private final LdifParser ldifParser;

    private final LdifDocumentSetupParticipant ldifDocumentSetupParticipant;

    private LdifFile ldifModel;


    public LdifDocumentProvider()
    {
        super();
        this.ldifParser = new LdifParser();
        this.ldifDocumentSetupParticipant = new LdifDocumentSetupParticipant();
    }


    public IDocument getDocument( Object element )
    {
        IDocument document = super.getDocument( element );
        return document;
    }


    protected FileInfo createFileInfo( Object element ) throws CoreException
    {
        FileInfo info = super.createFileInfo( element );

        if ( info != null )
        {
            // setup document partitioning
            IDocument document = info.fTextFileBuffer.getDocument();
            ldifDocumentSetupParticipant.setup( document );

            // initial parsing of whole document
            this.ldifModel = this.ldifParser.parse( document.get() );

            // add listener for incremental parsing
            document.addDocumentListener( this );

            IAnnotationModel annotationModel = info.fTextFileBuffer.getAnnotationModel();
            if ( annotationModel == null )
            {
                annotationModel = new LdifExternalAnnotationModel();
                info.fModel = annotationModel;
            }
        }

        return info;
    }


    protected void disposeFileInfo( Object element, FileInfo info )
    {
        IDocument document = info.fTextFileBuffer.getDocument();
        document.removeDocumentListener( this );

        super.disposeFileInfo( element, info );
    }


    public LdifFile getLdifModel()
    {
        return ldifModel;
    }


    public void documentAboutToBeChanged( DocumentEvent event )
    {
    }


    /**
     * Update the LDIF Model.
     */
    public void documentChanged( DocumentEvent event )
    {

        try
        {

            int changeOffset = event.getOffset();
            int replacedTextLength = event.getLength();
            int insertedTextLength = event.getText() != null ? event.getText().length() : 0;
            IDocument document = event.getDocument();
            // Region changeRegion = new Region(changeOffset,
            // replacedTextLength);
            Region changeRegion = new Region( changeOffset - BrowserCoreConstants.LINE_SEPARATOR.length(),
                replacedTextLength + ( 2 * BrowserCoreConstants.LINE_SEPARATOR.length() ) );

            // get containers to replace (from changeOffset till
            // changeOffset+replacedTextLength, check end of record)
            List oldContainerList = new ArrayList();
            LdifContainer[] containers = this.ldifModel.getContainers();
            for ( int i = 0; i < containers.length; i++ )
            {

                Region containerRegion = new Region( containers[i].getOffset(), containers[i].getLength() );

                boolean changeOffsetAtEOF = i == containers.length - 1
                    && changeOffset >= containerRegion.getOffset() + containerRegion.getLength();

                if ( TextUtilities.overlaps( containerRegion, changeRegion ) || changeOffsetAtEOF )
                {

                    // remember index
                    int index = i;

                    // add invalid containers and non-records before overlap
                    i--;
                    for ( ; i >= 0; i-- )
                    {
                        if ( !containers[i].isValid() || !( containers[i] instanceof LdifRecord ) )
                        {
                            oldContainerList.add( 0, containers[i] );
                        }
                        else
                        {
                            break;
                        }
                    }

                    // add all overlapping containers
                    i = index;
                    for ( ; i < containers.length; i++ )
                    {
                        containerRegion = new Region( containers[i].getOffset(), containers[i].getLength() );
                        if ( TextUtilities.overlaps( containerRegion, changeRegion ) || changeOffsetAtEOF )
                        {
                            oldContainerList.add( containers[i] );
                        }
                        else
                        {
                            break;
                        }
                    }

                    // add invalid containers and non-records after overlap
                    for ( ; i < containers.length; i++ )
                    {
                        if ( !containers[i].isValid() || !( containers[i] instanceof LdifRecord )
                            || !( oldContainerList.get( oldContainerList.size() - 1 ) instanceof LdifRecord ) )
                        {
                            oldContainerList.add( containers[i] );
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            }
            LdifContainer[] oldContainers = ( LdifContainer[] ) oldContainerList
                .toArray( new LdifContainer[oldContainerList.size()] );
            int oldCount = oldContainers.length;
            int oldOffset = oldCount > 0 ? oldContainers[0].getOffset() : 0;
            int oldLength = oldCount > 0 ? ( oldContainers[oldContainers.length - 1].getOffset()
                + oldContainers[oldContainers.length - 1].getLength() - oldContainers[0].getOffset() ) : 0;

            // get new content
            int newOffset = oldOffset;
            int newLength = oldLength - replacedTextLength + insertedTextLength;
            String textToParse = document.get( newOffset, newLength );

            // parse partion content to containers (offset=0)
            LdifFile newModel = this.ldifParser.parse( textToParse );
            LdifContainer[] newContainers = newModel.getContainers();

            // replace old containers with new containers
            // must adjust offsets of all following containers in model
            this.ldifModel.replace( oldContainers, newContainers );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

    }

}