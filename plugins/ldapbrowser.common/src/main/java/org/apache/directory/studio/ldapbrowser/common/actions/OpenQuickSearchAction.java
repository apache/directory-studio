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

package org.apache.directory.studio.ldapbrowser.common.actions;


import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IQuickSearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.impl.QuickSearch;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * This class implements the Open Quick Seach Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenQuickSearchAction extends BrowserAction
{
    /** The browser widget */
    private BrowserWidget widget;


    /**
     * Creates a new instance of OpenQuickSearchAction.
     */
    public OpenQuickSearchAction( BrowserWidget widget )
    {
        this.widget = widget;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IBrowserConnection browserConnection = getBrowserConnection();

        if ( browserConnection != null )
        {
            // Getting the current quick search
            IQuickSearch quickSearch = widget.getQuickSearch();

            // Creating a new quick search with the currently selected entry
            // if there's no current quick search or quick search isn't selected
            if ( ( quickSearch == null ) || !isQuickSearchSelected() )
            {
                // Setting a default search base on Root DSE
                IEntry searchBase = browserConnection.getRootDSE();

                // Getting the selected entry
                IEntry selectedEntry = getSelectedEntry();

                if ( selectedEntry != null )
                {
                    // Setting the selected entry as search base
                    searchBase = selectedEntry;
                }

                // Creating a new quick search
                quickSearch = new QuickSearch( searchBase, browserConnection );
                widget.setQuickSearch( quickSearch );
            }

            // Creating and opening the dialog
            PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn( getShell(), quickSearch,
                BrowserCommonConstants.PROP_SEARCH, null, null );
            dialog.getShell().setText(
                NLS.bind( Messages.getString( "PropertiesAction.PropertiesForX" ), //$NON-NLS-1$
                    Utils.shorten( quickSearch.getName(), 30 ) ) );
            if ( dialog.open() == PreferenceDialog.OK )
            {
                // Performing the quick search if it has not been performed before
                // (ie. the quick search was not modified at in the dialog)
                if ( quickSearch.getSearchResults() == null )
                {
                    new StudioBrowserJob( new SearchRunnable( new ISearch[]
                        { quickSearch } ) ).execute();
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "OpenQuickSearchAction.OpenQuickSearch" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_QUICKSEARCH );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return getBrowserConnection() != null;
    }


    /**
     * Gets the browser connection.
     *
     * @return the browser connection
     */
    private IBrowserConnection getBrowserConnection()
    {
        if ( getInput() instanceof IBrowserConnection )
        {
            return ( IBrowserConnection ) getInput();
        }
        else if ( getSelectedSearchResults().length > 0 )
        {
            return getSelectedSearchResults()[0].getEntry().getBrowserConnection();
        }
        else if ( getSelectedEntries().length > 0 )
        {
            return getSelectedEntries()[0].getBrowserConnection();
        }
        else if ( getSelectedSearches().length > 0 )
        {
            return getSelectedSearches()[0].getBrowserConnection();
        }

        return null;
    }


    /**
     * Gets the selected entry.
     *
     * @return the selected entry
     */
    private IEntry getSelectedEntry()
    {
        if ( getSelectedEntries().length == 1 )
        {
            return getSelectedEntries()[0];
        }

        return null;
    }


    /**
     * Indicates if quick search is currently selected object.
     *
     * @return <code>true</code> if quick search is the currently selected object,
     *         <code>false</code> if not.
     */
    private boolean isQuickSearchSelected()
    {
        if ( getSelectedSearches().length == 1 )
        {
            return getSelectedSearches()[0].equals( widget.getQuickSearch() );
        }

        return false;
    }
}
