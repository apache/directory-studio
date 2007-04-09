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


import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


public class ShowQuickFilterAction extends Action
{

    public static final String SHOW_QUICKFILTER_DIALOGSETTING_KEY = ShowQuickFilterAction.class.getName()
        + ".showQuickFilter";

    private SearchResultEditorQuickFilterWidget quickFilterWidget;


    public ShowQuickFilterAction( SearchResultEditorQuickFilterWidget quickFilterWidget )
    {
        super( "Show Quick Filter", AS_CHECK_BOX );
        super.setToolTipText( "Show Quick Filter" );
        super.setImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_FILTER ) );
        super.setActionDefinitionId( IWorkbenchActionDefinitionIds.FIND_REPLACE );
        super.setEnabled( true );

        this.quickFilterWidget = quickFilterWidget;

        if ( BrowserUIPlugin.getDefault().getDialogSettings().get( SHOW_QUICKFILTER_DIALOGSETTING_KEY ) == null )
        {
            BrowserUIPlugin.getDefault().getDialogSettings().put( SHOW_QUICKFILTER_DIALOGSETTING_KEY, false );
        }
        super.setChecked( BrowserUIPlugin.getDefault().getDialogSettings().getBoolean(
            SHOW_QUICKFILTER_DIALOGSETTING_KEY ) );
        this.quickFilterWidget.setActive( super.isChecked() );
    }


    public void run()
    {

        boolean checked = super.isChecked();
        super.setChecked( !checked );

        BrowserUIPlugin.getDefault().getDialogSettings().put( SHOW_QUICKFILTER_DIALOGSETTING_KEY, super.isChecked() );

        if ( this.quickFilterWidget != null )
        {
            this.quickFilterWidget.setActive( super.isChecked() );
        }
    }


    public void setChecked( boolean checked )
    {
        // super.setChecked(checked);
    }


    public boolean isChecked()
    {
        return super.isChecked();
    }


    public void dispose()
    {
        this.quickFilterWidget = null;
    }

}
