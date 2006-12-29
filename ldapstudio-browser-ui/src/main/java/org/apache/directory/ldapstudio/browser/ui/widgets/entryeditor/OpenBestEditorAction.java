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


import org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager;
import org.eclipse.jface.viewers.TreeViewer;


public class OpenBestEditorAction extends AbstractOpenEditorAction
{

    private IValueEditor bestValueEditor;


    public OpenBestEditorAction( TreeViewer viewer, EntryEditorWidgetActionGroup actionGroup,
        ValueEditorManager valueEditorManager )
    {
        super( viewer, actionGroup, valueEditorManager );
    }


    public IValueEditor getBestValueEditor()
    {
        return this.bestValueEditor;
    }


    protected void updateEnabledState()
    {

        if ( this.selectedValues.length == 1
            && this.selectedAttributes.length == 0
            && this.viewer.getCellModifier().canModify( this.selectedValues[0],
                EntryEditorWidgetTableMetadata.VALUE_COLUMN_NAME ) )
        {
            this.bestValueEditor = this.valueEditorManager.getCurrentValueEditor( this.selectedValues[0] );
            super.cellEditor = this.bestValueEditor.getCellEditor();
            this.setEnabled( true );
            this.setText( "" + this.bestValueEditor.getValueEditorName() );
            this.setToolTipText( "" + this.bestValueEditor.getValueEditorName() );
            this.setImageDescriptor( this.bestValueEditor.getValueEditorImageDescriptor() );
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
        this.bestValueEditor = null;
        super.dispose();
    }

}
