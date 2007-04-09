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


import java.util.Arrays;

import org.apache.directory.ldapstudio.ldifeditor.editor.LdifEditor;
import org.apache.directory.ldapstudio.valueeditors.IValueEditor;


public class OpenValueEditorAction extends AbstractOpenValueEditorAction
{

    public OpenValueEditorAction( LdifEditor editor, IValueEditor valueEditor )
    {
        super( editor );
        super.valueEditor = valueEditor;
    }


    public void update()
    {
        String attributeDescription = getAttributeDescription();
        Object rawValue = getValueEditorRawValue();

        if ( isEditableLineSelected() )
        {
            IValueEditor[] alternativeVps = this.editor.getValueEditorManager().getAlternativeValueEditors(
                getConnection().getSchema(), attributeDescription );
            super.setEnabled( Arrays.asList( alternativeVps ).contains( this.valueEditor ) && rawValue != null );
        }
        else
        {
            super.setEnabled( false );
        }

        setText( valueEditor.getValueEditorName() );
        setImageDescriptor( valueEditor.getValueEditorImageDescriptor() );
    }

}
