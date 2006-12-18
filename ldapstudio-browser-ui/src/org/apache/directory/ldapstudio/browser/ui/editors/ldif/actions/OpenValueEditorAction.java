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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif.actions;


import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.ui.editors.ldif.LdifEditor;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProvider;


public class OpenValueEditorAction extends AbstractOpenValueEditorAction
{

    public OpenValueEditorAction( LdifEditor editor, ValueProvider valueProvider )
    {
        super( editor );
        super.valueProvider = valueProvider;
    }


    public void update()
    {
        String attributeDescription = getAttributeDescription();
        Object rawValue = getValueProviderRawValue();

        if ( isEditableLineSelected() )
        {
            ValueProvider[] alternativeVps = this.editor.getValueProviderManager().getAlternativeValueProvider(
                getSchema(), attributeDescription );
            super.setEnabled( Arrays.asList( alternativeVps ).contains( this.valueProvider ) && rawValue != null );
        }
        else
        {
            super.setEnabled( false );
        }

        setText( valueProvider.getCellEditorName() );
        setImageDescriptor( valueProvider.getCellEditorImageDescriptor() );
    }

}
