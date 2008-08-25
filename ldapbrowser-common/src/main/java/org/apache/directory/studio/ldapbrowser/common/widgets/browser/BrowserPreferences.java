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
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * This class is a wrapper for the preferences of the browser widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserPreferences implements IPropertyChangeListener, Preferences.IPropertyChangeListener
{

    /** The tree viewer */
    protected TreeViewer viewer;


    /**
     * Creates a new instance of BrowserPreferences.
     */
    public BrowserPreferences()
    {
        BrowserCommonActivator.getDefault().getPreferenceStore().addPropertyChangeListener( this );
        BrowserCorePlugin.getDefault().getPluginPreferences().addPropertyChangeListener( this );
    }


    /**
     * Connects the tree viewer to this preferences.
     *
     * @param viewer the tree viewer
     */
    public void connect( TreeViewer viewer )
    {
        this.viewer = viewer;
    }


    /**
     * Disposes this preferences.
     */
    public void dispose()
    {
        BrowserCommonActivator.getDefault().getPreferenceStore().removePropertyChangeListener( this );
        BrowserCorePlugin.getDefault().getPluginPreferences().removePropertyChangeListener( this );
        viewer = null;
    }


    /**
     * Gets the sort by, one of BrowserCoreConstants.SORT_BY_NONE, 
     * BrowserCoreConstants.SORT_BY_RDN or BrowserCoreConstants.SORT_BY_RDN_VALUE.
     * 
     * @return the sort by
     */
    public int getSortBy()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_BROWSER_SORT_BY );
    }


    /**
     * Gets the sort order, one of one of BrowserCoreConstants.SORT_ORDER_NONE, 
     * BrowserCoreConstants.SORT_ORDER_ASCENDING or BrowserCoreConstants.SORT_ORDER_DESCENDING.
     * 
     * @return the sort order
     */
    public int getSortOrder()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_BROWSER_SORT_ORDER );
    }


    /**
     * Gets the sort limit.
     * 
     * @return the sort limit
     */
    public int getSortLimit()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_BROWSER_SORT_LIMIT );
    }


    /**
     * Returns true if leaf entries should be shown before non-leaf entries.
     * 
     * @return true, if leaf entries should be shown first
     */
    public boolean isLeafEntriesFirst()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_LEAF_ENTRIES_FIRST );
    }


    /**
     * Returns true if container entries should be shown before leaf entries.
     * 
     * @return true, if container entries should be shown first
     */
    public boolean isContainerEntriesFirst()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_CONTAINER_ENTRIES_FIRST );
    }


    /**
     * Returns true if meta entries should be shown after non-meta entries.
     * 
     * @return true, if meta entries should be shown first
     */
    public boolean isMetaEntriesLast()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_META_ENTRIES_LAST );
    }


    /**
     * Returns true if the bookmark category should be visible.
     *
     * @return true if the bookmark category should be visible
     */
    public boolean isShowBookmarks()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_BOOKMARKS );
    }


    /**
     * Returns true if the DIT category should be visible.
     *
     * @return true if the DIT category should be visible
     */
    public boolean isShowDIT()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_DIT );
    }


    /**
     * Returns true if the searches category should be visible.
     *
     * @return true if the searches category should be visible
     */
    public boolean isShowSearches()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_SEARCHES );
    }


    /**
     * Gets the folding size.
     * 
     * @return the folding size
     */
    public int getFoldingSize()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_BROWSER_FOLDING_SIZE );
    }


    /**
     * Returns true if folding is enabled.
     *
     * @return true if folding is enabled
     */
    public boolean isUseFolding()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENABLE_FOLDING );
    }


    /**
     * Returns true if meta entries should be visible.
     *
     * @return true if meta entries should be visible
     */
    public boolean isShowDirectoryMetaEntries()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_DIRECTORY_META_ENTRIES );
    }


    /**
     * Returns true if entry lables should be abbreviated.
     *
     * @return true if entry lables should be abbreviated
     */
    public boolean isEntryAbbreviate()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE );
    }


    /**
     * Gets the entry's maximum label length.
     * 
     * @return the entry's maximum label length
     */
    public int getEntryAbbreviateMaxLength()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH );
    }


    /**
     * Gets the entry label, one of BrowserWidgetsConstants.SHOW_DN, 
     * BrowserWidgetsConstants.SHOW_RDN or BrowserWidgetsConstants.SHOW_RDN_VALUE.
     * 
     * @return the entry label
     */
    public int getEntryLabel()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_LABEL );
    }


    /**
     * Returns true if search result lables should be abbreviated.
     *
     * @return true if search result lables should be abbreviated
     */
    public boolean isSearchResultAbbreviate()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE );
    }


    /**
     * Gets the search result's maximum label length.
     * 
     * @return the search result's maximum label length
     */
    public int getSearchResultAbbreviateMaxLength()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH );
    }


    /**
     * Gets the search result label, one of BrowserWidgetsConstants.SHOW_DN, 
     * BrowserWidgetsConstants.SHOW_RDN or BrowserWidgetsConstants.SHOW_RDN_VALUE.
     * 
     * @return the entry label
     */
    public int getSearchResultLabel()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_LABEL );
    }


    /**
     * Returns true if the base entries should be expanded when
     * opening connection.
     *
     * @return true if the base entries should be expanded
     */
    public boolean isExpandBaseEntries()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES );
    }


    /**
     * Returns true if the browser should check for children
     * while browsing the directory.
     *
     * @return true if the browser should check for children
     */
    public boolean isCheckForChildren()
    {
        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        return coreStore.getBoolean( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN );
    }


    /**
     * Returns true if subentries should be fetched while browsing.
     * 
     *
     * @return true if subentries should be fetched while browsing
     */
    public boolean isFetchSubentries()
    {
        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        return coreStore.getBoolean( BrowserCoreConstants.PREFERENCE_FETCH_SUBENTRIES );
    }


    /**
     * {@inheritDoc}
     */
    public void propertyChange( PropertyChangeEvent event )
    {
        if ( viewer != null )
        {
            viewer.refresh();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void propertyChange( org.eclipse.core.runtime.Preferences.PropertyChangeEvent event )
    {
        if ( viewer != null )
        {
            viewer.refresh();
        }
    }

}
