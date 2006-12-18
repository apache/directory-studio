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


import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProvider;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProviderManager;
import org.eclipse.jface.viewers.TreeViewer;


public class OpenBestEditorAction extends AbstractOpenEditorAction
{

    private ValueProvider bestValueProvider;


    public OpenBestEditorAction( TreeViewer viewer, EntryEditorWidgetActionGroup actionGroup,
        ValueProviderManager valueProviderManager )
    {
        super( viewer, actionGroup, valueProviderManager );
    }


    public ValueProvider getBestValueProvider()
    {
        return this.bestValueProvider;
    }


    protected void updateEnabledState()
    {

        if ( this.selectedValues.length == 1
            && this.selectedAttributes.length == 0
            && this.viewer.getCellModifier().canModify( this.selectedValues[0],
                EntryEditorWidgetTableMetadata.VALUE_COLUMN_NAME ) )
        {
            this.bestValueProvider = this.valueProviderManager.getCurrentValueProvider( this.selectedValues[0] );
            super.cellEditor = this.bestValueProvider.getCellEditor();
            this.setEnabled( true );
            this.setText( "" + this.bestValueProvider.getCellEditorName() );
            this.setToolTipText( "" + this.bestValueProvider.getCellEditorName() );
            this.setImageDescriptor( this.bestValueProvider.getCellEditorImageDescriptor() );
        }
        else
        {
            // super.cellEditor = null;
            this.setEnabled( false );
            this.setText( "Best Editor" );
            this.setToolTipText( "Best Editor" );
            this.setImageDescriptor( null );
        }
    }


    public void dispose()
    {
        this.bestValueProvider = null;
        super.dispose();
    }

}
