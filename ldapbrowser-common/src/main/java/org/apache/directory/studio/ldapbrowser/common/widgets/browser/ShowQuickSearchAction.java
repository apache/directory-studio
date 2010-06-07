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

package org.apache.directory.studio.ldapbrowser.common.widgets.browser;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * The ShowQuickSearchAction is used to select whether the quick search widget
 * should be visible in the browser view or not.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ShowQuickSearchAction extends Action
{

    /** The quick search widget. */
    private BrowserQuickSearchWidget quickSearchWidget;


    /**
     * Creates a new instance of ShowQuickSearchAction.
     */
    public ShowQuickSearchAction( BrowserQuickSearchWidget quickSearchWidget )
    {
        super( Messages.getString( "ShowQuickSearchAction.ShowQuickSearch" ), IAction.AS_CHECK_BOX ); //$NON-NLS-1$
        this.quickSearchWidget = quickSearchWidget;
        setActionDefinitionId( IWorkbenchActionDefinitionIds.FIND_REPLACE );
        setEnabled( true );
        setChecked( BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_QUICK_SEARCH ) );
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        BrowserCommonActivator.getDefault().getPreferenceStore().setValue(
            BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_QUICK_SEARCH, isChecked() );

        if ( quickSearchWidget != null )
        {
            quickSearchWidget.setActive( isChecked() );
        }
    }


    /**
     * Disposes this action.
     */
    public void dispose()
    {
        quickSearchWidget = null;
    }

}
