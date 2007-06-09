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
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifModSpec;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifModSpecTypeLine;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.apache.directory.studio.ldifeditor.editor.ILdifEditor;

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;


public class LdifAutoEditStrategy implements IAutoEditStrategy
{

    private ILdifEditor editor;


    public LdifAutoEditStrategy( ILdifEditor editor )
    {
        this.editor = editor;
    }


    public void customizeDocumentCommand( IDocument d, DocumentCommand c )
    {

        LdifFile model = editor.getLdifModel();
        LdifContainer container = LdifFile.getContainer( model, c.offset );
        LdifContainer innerContainer = container != null ? LdifFile.getInnerContainer( container, c.offset ) : null;
        LdifPart part = container != null ? LdifFile.getContainerContent( container, c.offset ) : null;

        boolean smartInsertAttributeInModSpec = LdifEditorActivator.getDefault().getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_SMARTINSERTATTRIBUTEINMODSPEC );
        if ( smartInsertAttributeInModSpec )
        {
            if ( c.length == 0 && c.text != null && TextUtilities.endsWith( d.getLegalLineDelimiters(), c.text ) != -1 )
            {

                if ( container instanceof LdifChangeModifyRecord && innerContainer instanceof LdifModSpec
                    && ( part instanceof LdifAttrValLine || part instanceof LdifModSpecTypeLine ) )
                {
                    LdifModSpec modSpec = ( LdifModSpec ) innerContainer;
                    String att = modSpec.getModSpecType().getUnfoldedAttributeDescription();
                    c.text += att + ": ";
                }
            }
        }

        boolean autoWrap = LdifEditorActivator.getDefault().getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_FORMATTER_AUTOWRAP );
        
        if ( autoWrap )
        {

        }

    }

}
