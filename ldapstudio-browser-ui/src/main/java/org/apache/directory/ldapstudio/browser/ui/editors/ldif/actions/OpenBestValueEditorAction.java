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


import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.LdifEditor;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.AbstractDialogCellEditor;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProvider;


public class OpenBestValueEditorAction extends AbstractOpenValueEditorAction
{

    public OpenBestValueEditorAction( LdifEditor editor )
    {
        super( editor );
    }


    public void update()
    {

        super.setEnabled( isEditableLineSelected() );

        // determine value provider
        Schema schema = getSchema();
        String attributeDescription = getAttributeDescription();
        Object oldValue = getValue();

        if ( attributeDescription != null )
        {
            valueProvider = manager.getCurrentValueProvider( schema, attributeDescription );
            Object rawValue = valueProvider.getRawValue( null, schema, oldValue );
            if ( !( valueProvider instanceof AbstractDialogCellEditor ) || rawValue == null )
            {
                ValueProvider[] vps = manager.getAlternativeValueProvider( schema, attributeDescription );
                for ( int i = 0; i < vps.length
                    && ( !( valueProvider instanceof AbstractDialogCellEditor ) || rawValue == null ); i++ )
                {
                    valueProvider = vps[i];
                    rawValue = valueProvider.getRawValue( null, schema, oldValue );
                }
            }
        }

        if ( valueProvider != null )
        {
            setText( valueProvider.getCellEditorName() );
            setImageDescriptor( valueProvider.getCellEditorImageDescriptor() );
        }
    }

}
