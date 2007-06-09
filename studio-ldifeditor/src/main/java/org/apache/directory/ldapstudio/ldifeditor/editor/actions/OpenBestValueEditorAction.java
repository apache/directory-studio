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


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.ldifeditor.editor.LdifEditor;
import org.apache.directory.ldapstudio.valueeditors.AbstractDialogValueEditor;
import org.apache.directory.ldapstudio.valueeditors.IValueEditor;


public class OpenBestValueEditorAction extends AbstractOpenValueEditorAction
{

    public OpenBestValueEditorAction( LdifEditor editor )
    {
        super( editor );
    }


    public void update()
    {

        super.setEnabled( isEditableLineSelected() );

        // determine value editor
        IConnection connection = getConnection();
        String attributeDescription = getAttributeDescription();
        Object oldValue = getValue();

        if ( attributeDescription != null )
        {
            valueEditor = valueEditorManager.getCurrentValueEditor( connection.getSchema(), attributeDescription );
            Object rawValue = valueEditor.getRawValue( connection, oldValue );
            if ( !( valueEditor instanceof AbstractDialogValueEditor ) || rawValue == null )
            {
                IValueEditor[] vps = valueEditorManager.getAlternativeValueEditors( connection.getSchema(), attributeDescription );
                for ( int i = 0; i < vps.length
                    && ( !( valueEditor instanceof AbstractDialogValueEditor ) || rawValue == null ); i++ )
                {
                    valueEditor = vps[i];
                    rawValue = valueEditor.getRawValue( connection, oldValue );
                }
            }
        }

        if ( valueEditor != null )
        {
            setText( valueEditor.getValueEditorName() );
            setImageDescriptor( valueEditor.getValueEditorImageDescriptor() );
        }
    }

}
