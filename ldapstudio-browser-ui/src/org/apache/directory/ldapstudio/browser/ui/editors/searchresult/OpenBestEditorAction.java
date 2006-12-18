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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProvider;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProviderManager;
import org.eclipse.jface.viewers.TableViewer;


public class OpenBestEditorAction extends AbstractOpenEditorAction
{

    private ValueProvider valueProvider;


    public OpenBestEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        SearchResultEditorActionGroup actionGroup, ValueProviderManager valueProviderManager )
    {
        super( viewer, cursor, actionGroup, valueProviderManager );
    }


    public ValueProvider getBestValueProvider()
    {
        return this.valueProvider;
    }


    protected void updateEnabledState()
    {

        if ( viewer.getCellModifier().canModify( this.selectedSearchResult, this.selectedProperty ) )
        {

            if ( this.selectedAttributeHierarchie == null )
            {
                this.valueProvider = this.valueProviderManager.getCurrentValueProvider( this.selectedSearchResult
                    .getEntry(), this.selectedProperty );
            }
            else
            {
                this.valueProvider = this.valueProviderManager
                    .getCurrentValueProvider( this.selectedAttributeHierarchie );
            }

            super.cellEditor = this.valueProvider.getCellEditor();
            this.setEnabled( true );
            this.setText( "" + this.valueProvider.getCellEditorName() );
            this.setToolTipText( "" + this.valueProvider.getCellEditorName() );
            this.setImageDescriptor( this.valueProvider.getCellEditorImageDescriptor() );

        }
        else
        {
            this.setEnabled( false );
            this.cellEditor = null;
            this.setText( "Best Editor" );
            this.setToolTipText( "Best Editor" );
            this.setImageDescriptor( null );
        }
    }

}
