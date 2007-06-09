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

package org.apache.directory.ldapstudio.ldifeditor.editor.reconciler;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifFile;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifPart;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContainer;
import org.apache.directory.ldapstudio.ldifeditor.editor.ILdifEditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;


class LdifAnnotationUpdater
{

    private static final String ERROR_ANNOTATION_TYPE = "org.eclipse.ui.workbench.texteditor.error";

    private ILdifEditor editor;


    public LdifAnnotationUpdater( ILdifEditor editor )
    {
        this.editor = editor;
    }


    public void dispose()
    {
    }


    public void updateAnnotations( LdifContainer[] containers )
    {

    }


    public void updateAnnotations()
    {

        LdifFile model = editor.getLdifModel();
        ISourceViewer viewer = ( ISourceViewer ) editor.getAdapter( ISourceViewer.class );
        if ( viewer == null )
            return;

        IDocument document = viewer.getDocument();
        IAnnotationModel annotationModel = viewer.getAnnotationModel();
        if ( document == null || annotationModel == null || model == null )
            return;

        if ( annotationModel instanceof IAnnotationModelExtension )
        {
            ( ( IAnnotationModelExtension ) annotationModel ).removeAllAnnotations();

            List positionList = new ArrayList();

            LdifContainer[] containers = model.getContainers();
            for ( int i = 0; i < containers.length; i++ )
            {
                LdifContainer container = containers[i];

                // LdifPart errorPart = null;
                int errorOffset = -1;
                int errorLength = -1;
                StringBuffer errorText = null;

                LdifPart[] parts = container.getParts();
                for ( int k = 0; k < parts.length; k++ )
                {
                    LdifPart part = parts[k];
                    if ( !part.isValid() )
                    {
                        if ( errorOffset == -1 )
                        {
                            // errorPart = part;
                            errorOffset = part.getOffset();
                            errorLength = part.getLength();
                            errorText = new StringBuffer();
                            errorText.append( part.toRawString() );
                        }
                        else
                        {
                            errorLength += part.getLength();
                            errorText.append( part.toRawString() );
                        }
                    }
                }

                if ( errorOffset == -1 && !container.isValid() )
                {
                    errorOffset = container.getOffset();
                    errorLength = container.getLength();
                    errorText = new StringBuffer();
                    errorText.append( container.toRawString() );
                }

                if ( errorOffset > -1 )
                {
                    // Annotation annotation = new Annotation("DEFAULT",
                    // true,
                    // invalidFilters[i].toString());
                    // if(errorPart instanceof LdifUnknownPart) {
                    // errorOffset = container.getOffset();
                    // errorLength = container.getLength();
                    // errorText = new StringBuffer(container.toString());
                    // }
                    Annotation annotation = new Annotation( ERROR_ANNOTATION_TYPE, true, errorText.toString() );
                    Position position = new Position( errorOffset, errorLength );
                    positionList.add( position );
                    viewer.getAnnotationModel().addAnnotation( annotation, position );
                }

            }
        }
    }

}