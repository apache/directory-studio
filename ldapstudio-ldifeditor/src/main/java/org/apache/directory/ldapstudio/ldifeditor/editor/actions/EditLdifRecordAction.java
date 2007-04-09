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

package org.apache.directory.ldapstudio.ldifeditor.editor.actions;


import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifChangeAddRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContainer;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifRecord;
import org.apache.directory.ldapstudio.ldifeditor.LdifEditorConstants;
import org.apache.directory.ldapstudio.ldifeditor.dialogs.LdifEntryEditorDialog;
import org.apache.directory.ldapstudio.ldifeditor.editor.LdifEditor;
import org.eclipse.jface.text.IDocument;


public class EditLdifRecordAction extends AbstractLdifAction
{

    public EditLdifRecordAction( LdifEditor editor )
    {
        super( "Edit Record", editor );
        super.setActionDefinitionId( LdifEditorConstants.ACTION_ID_EDIT_RECORD );
    }


    protected void doRun()
    {

        LdifContainer[] containers = getSelectedLdifContainers();
        if ( containers.length == 1
            && ( containers[0] instanceof LdifContentRecord || containers[0] instanceof LdifChangeAddRecord ) )
        {

            LdifContainer container = containers[0];

            LdifEntryEditorDialog dialog = null;
            if ( container instanceof LdifContentRecord )
            {
                dialog = new LdifEntryEditorDialog( editor.getEditorSite().getShell(), editor.getConnection(),
                    ( LdifContentRecord ) container );
            }
            else
            {
                dialog = new LdifEntryEditorDialog( editor.getEditorSite().getShell(), editor.getConnection(),
                    ( LdifChangeAddRecord ) container );
            }

            editor.deactivateGlobalActionHandlers();
            if ( dialog.open() == LdifEntryEditorDialog.OK )
            {
                LdifRecord record = dialog.getLdifRecord();

                IDocument document = editor.getDocumentProvider().getDocument( editor.getEditorInput() );
                String old = document.get();
                StringBuffer sb = new StringBuffer();
                sb.append( old.substring( 0, container.getOffset() ) );
                sb.append( record.toFormattedString() );
                sb.append( old.substring( container.getOffset() + container.getLength(), old.length() ) );
                document.set( sb.toString() );
            }
            editor.activateGlobalActionHandlers();
        }
    }


    public void update()
    {
        LdifContainer[] containers = getSelectedLdifContainers();
        super.setEnabled( containers.length == 1
            && ( containers[0] instanceof LdifContentRecord || containers[0] instanceof LdifChangeAddRecord ) );
    }

}
