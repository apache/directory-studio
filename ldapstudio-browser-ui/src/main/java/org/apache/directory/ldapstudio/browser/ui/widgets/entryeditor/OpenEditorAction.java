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

package org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor;


import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProvider;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProviderManager;
import org.eclipse.jface.viewers.TreeViewer;


public class OpenEditorAction extends AbstractOpenEditorAction
{

    private ValueProvider valueProvider;


    public OpenEditorAction( TreeViewer viewer, EntryEditorWidgetActionGroup actionGroup,
        ValueProviderManager valueProviderManager, ValueProvider valueProvider )
    {
        super( viewer, actionGroup, valueProviderManager );
        super.cellEditor = valueProvider.getCellEditor();
        this.valueProvider = valueProvider;

        this.setText( "" + this.valueProvider.getCellEditorName() );
        this.setToolTipText( "" + this.valueProvider.getCellEditorName() );
        this.setImageDescriptor( this.valueProvider.getCellEditorImageDescriptor() );
    }


    public ValueProvider getValueProvider()
    {
        return this.valueProvider;
    }


    protected void updateEnabledState()
    {
        if ( this.selectedValues.length == 1
            && this.selectedAttributes.length == 0
            && this.viewer.getCellModifier().canModify( this.selectedValues[0],
                EntryEditorWidgetTableMetadata.VALUE_COLUMN_NAME ) )
        {
            ValueProvider[] alternativeVps = this.valueProviderManager
                .getAlternativeValueProvider( this.selectedValues[0] );
            this.setEnabled( Arrays.asList( alternativeVps ).contains( this.valueProvider )
                && this.valueProvider.getRawValue( this.selectedValues[0] ) != null );
        }
        else
        {
            this.setEnabled( false );
        }

    }


    public void run()
    {
        this.valueProviderManager.setUserSelectedValueProvider( this.valueProvider );
        super.run();
    }


    public void dispose()
    {
        this.valueProvider = null;
        super.dispose();
    }

}
