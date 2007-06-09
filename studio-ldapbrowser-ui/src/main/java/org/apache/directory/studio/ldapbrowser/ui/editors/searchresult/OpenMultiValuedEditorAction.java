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


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.viewers.TableViewer;


public class OpenMultiValuedEditorAction extends AbstractOpenEditorAction
{

    public OpenMultiValuedEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        SearchResultEditorActionGroup actionGroup, ValueEditorManager valueEditorManager )
    {
        super( viewer, cursor, actionGroup, valueEditorManager );
        this.cellEditor = this.valueEditorManager.getMultiValuedValueEditor().getCellEditor();
        this.setText( "Multivalued Editor" );
        this.setToolTipText( "Multivalued Editor" );
        this.setImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor(
            BrowserCommonConstants.IMG_MULTIVALUEDEDITOR ) );
    }


    protected void updateEnabledState()
    {

        if ( viewer.getCellModifier().canModify( this.selectedSearchResult, this.selectedProperty ) )
        {
            this.setEnabled( true );
        }
        else
        {
            this.setEnabled( false );
        }
    }

}
