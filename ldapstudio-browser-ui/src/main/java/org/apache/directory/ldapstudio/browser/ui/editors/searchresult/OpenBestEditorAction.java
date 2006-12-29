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


import org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager;
import org.eclipse.jface.viewers.TableViewer;


public class OpenBestEditorAction extends AbstractOpenEditorAction
{

    private IValueEditor valueEditor;


    public OpenBestEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        SearchResultEditorActionGroup actionGroup, ValueEditorManager valueEditorManager )
    {
        super( viewer, cursor, actionGroup, valueEditorManager );
    }


    public IValueEditor getBestValueEditor()
    {
        return this.valueEditor;
    }


    protected void updateEnabledState()
    {

        if ( viewer.getCellModifier().canModify( this.selectedSearchResult, this.selectedProperty ) )
        {

            if ( this.selectedAttributeHierarchie == null )
            {
                this.valueEditor = this.valueEditorManager.getCurrentValueEditor( this.selectedSearchResult
                    .getEntry(), this.selectedProperty );
            }
            else
            {
                this.valueEditor = this.valueEditorManager
                    .getCurrentValueEditor( this.selectedAttributeHierarchie );
            }

            super.cellEditor = this.valueEditor.getCellEditor();
            this.setEnabled( true );
            this.setText( "" + this.valueEditor.getValueEditorName() );
            this.setToolTipText( "" + this.valueEditor.getValueEditorName() );
            this.setImageDescriptor( this.valueEditor.getValueEditorImageDescriptor() );

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
