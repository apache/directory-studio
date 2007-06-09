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

package org.apache.directory.studio.ldifeditor.editor.text;


import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifFile;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifPart;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldifeditor.editor.ILdifEditor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;


public class LdifAnnotationHover implements IAnnotationHover
{

    private ILdifEditor editor;


    public LdifAnnotationHover( ILdifEditor editor )
    {
        this.editor = editor;
    }


    public String getHoverInfo( ISourceViewer sourceViewer, int lineNumber )
    {

        try
        {
            if ( this.editor != null )
            {

                int offset = sourceViewer.getDocument().getLineOffset( lineNumber );
                LdifContainer container = LdifFile.getContainer( this.editor.getLdifModel(), offset );
                if ( container != null )
                {
                    LdifPart part = LdifFile.getContainerContent( container, offset );
                    if ( part != null )
                    {
                        // return container.getClass().getName() + " - " +
                        // part.getClass().getName();
                        return container.getInvalidString() + " - " + part.getInvalidString();
                    }
                }
            }
        }
        catch ( BadLocationException e )
        {
        }

        return null;
    }

}
