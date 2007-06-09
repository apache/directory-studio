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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import java.util.Arrays;

import org.apache.directory.ldapstudio.valueeditors.IValueEditor;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorManager;
import org.eclipse.jface.viewers.TableViewer;


public class OpenEditorAction extends AbstractOpenEditorAction
{

    private IValueEditor valueEditor;


    public OpenEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        SearchResultEditorActionGroup actionGroup, ValueEditorManager valueEditorManager,
        IValueEditor valueEditor )
    {
        super( viewer, cursor, actionGroup, valueEditorManager );
        super.cellEditor = valueEditor.getCellEditor();
        this.valueEditor = valueEditor;
        this.setText( "" + this.valueEditor.getValueEditorName() );
        this.setToolTipText( "" + this.valueEditor.getValueEditorName() );
        this.setImageDescriptor( this.valueEditor.getValueEditorImageDescriptor() );
    }


    public IValueEditor getValueEditor()
    {
        return this.valueEditor;
    }


    protected void updateEnabledState()
    {

        if ( viewer.getCellModifier().canModify( this.selectedSearchResult, this.selectedProperty ) )
        {

            IValueEditor[] alternativeVps;
            if ( this.selectedAttributeHierarchie == null )
            {
                this.setEnabled( false );
            }
            else
            {
                alternativeVps = this.valueEditorManager
                    .getAlternativeValueEditors( this.selectedAttributeHierarchie );
                this.setEnabled( Arrays.asList( alternativeVps ).contains( this.valueEditor )
                    && this.valueEditor.getRawValue( this.selectedAttributeHierarchie ) != null );
            }
        }
        else
        {
            this.setEnabled( false );
        }
    }


    public void run()
    {
        this.valueEditorManager.setUserSelectedValueEditor( this.valueEditor );
        super.run();
    }

}
