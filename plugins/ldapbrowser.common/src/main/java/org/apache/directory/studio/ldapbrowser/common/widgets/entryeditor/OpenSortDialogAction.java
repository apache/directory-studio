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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;


public class OpenSortDialogAction extends Action
{
    private EntryEditorWidgetPreferences preferences;


    public OpenSortDialogAction( EntryEditorWidgetPreferences preferences )
    {
        setText( Messages.getString( "OpenSortDialogAction.Sorting" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "OpenSortDialogAction.Sorting" ) ); //$NON-NLS-1$
        setImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor(
            BrowserCommonConstants.IMG_SORT ) );
        setEnabled( true );

        this.preferences = preferences;
    }


    public void run()
    {
        EntryEditorWidgetSorterDialog dlg = new EntryEditorWidgetSorterDialog( PlatformUI.getWorkbench().getDisplay()
            .getActiveShell(), preferences );
        dlg.open();
    }
}
