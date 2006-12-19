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

package org.apache.directory.ldapstudio.browser.ui.widgets.browser;


import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;


public class BrowserPreferences implements IPropertyChangeListener, Preferences.IPropertyChangeListener
{

    protected TreeViewer viewer;


    public BrowserPreferences()
    {
        BrowserUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener( this );
        BrowserCorePlugin.getDefault().getPluginPreferences().addPropertyChangeListener( this );
    }


    public void connect( TreeViewer viewer )
    {
        this.viewer = viewer;
    }


    public void dispose()
    {
        BrowserUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener( this );
        BrowserCorePlugin.getDefault().getPluginPreferences().removePropertyChangeListener( this );
        this.viewer = null;
    }


    public int getSortBy()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt( BrowserUIConstants.PREFERENCE_BROWSER_SORT_BY );
    }


    public int getSortOrder()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_BROWSER_SORT_ORDER );
    }


    public int getSortLimit()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_BROWSER_SORT_LIMIT );
    }


    public boolean isLeafEntriesFirst()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_LEAF_ENTRIES_FIRST );
    }


    public boolean isMetaEntriesLast()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_META_ENTRIES_LAST );
    }


    public boolean isShowBookmarks()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_SHOW_BOOKMARKS );
    }


    public boolean isShowDIT()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_SHOW_DIT );
    }


    public boolean isShowSearches()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_SHOW_SEARCHES );
    }


    public int getFoldingSize()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_BROWSER_FOLDING_SIZE );
    }


    public boolean isUseFolding()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_ENABLE_FOLDING );
    }


    public boolean isShowDirectoryMetaEntries()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_SHOW_DIRECTORY_META_ENTRIES );
    }


    public boolean isEntryAbbreviate()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE );
    }


    public int getEntryAbbreviateMaxLength()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH );
    }


    public int getEntryLabel()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_BROWSER_ENTRY_LABEL );
    }


    public boolean isSearchResultAbbreviate()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE );
    }


    public int getSearchResultAbbreviateMaxLength()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH );
    }


    public int getSearchResultLabel()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_BROWSER_SEARCH_RESULT_LABEL );
    }


    public boolean isExpandBaseEntries()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES );
    }


    public boolean isCheckForChildren()
    {
        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        return coreStore.getBoolean( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN );
    }


    public boolean isDerefAliasWhileBrowsing()
    {
        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        return coreStore.getBoolean( BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS );
    }


    public boolean isFetchSubentries()
    {
        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        return coreStore.getBoolean( BrowserCoreConstants.PREFERENCE_FETCH_SUBENTRIES );
    }


    public void propertyChange( PropertyChangeEvent event )
    {
        if ( this.viewer != null )
        {
            this.viewer.refresh();
        }
    }


    public void propertyChange( org.eclipse.core.runtime.Preferences.PropertyChangeEvent event )
    {
        if ( this.viewer != null )
        {
            this.viewer.refresh();
        }
    }

}
