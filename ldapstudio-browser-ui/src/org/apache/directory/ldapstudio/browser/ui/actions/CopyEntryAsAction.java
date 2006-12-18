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

package org.apache.directory.ldapstudio.browser.ui.actions;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.jobs.InitializeAttributesJob;
import org.apache.directory.ldapstudio.browser.core.jobs.ReadEntryJob;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.jobs.RunnableContextJobAdapter;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;


public abstract class CopyEntryAsAction extends BrowserAction
{

    public static final int MODE_DN_ONLY = 1;

    public static final int MODE_RETURNING_ATTRIBUTES_ONLY = 2;

    public static final int MODE_NORMAL = 3;

    public static final int MODE_INCLUDE_OPERATIONAL_ATTRIBUTES = 4;

    protected int mode;

    protected String type;

    protected String appendix;


    public CopyEntryAsAction( String type, int mode )
    {
        super();
        this.type = type;
        this.mode = mode;
        if ( this.mode == MODE_DN_ONLY )
        {
            this.appendix = " (DN only)";
        }
        else if ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY )
        {
            this.appendix = " (returning attributes only)";
        }
        else if ( this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES )
        {
            this.appendix = " (include operational attributes)";
        }
        else if ( this.mode == MODE_NORMAL )
        {
            this.appendix = " (all user attributes)";
        }
        else
        {
            appendix = "";
        }
    }


    public String getText()
    {
        if ( getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length > 0
            && getSelectedSearches().length == 0 )
        {
            String text = ( getSelectedEntries().length + getSelectedSearchResults().length
                + getSelectedBookmarks().length > 1 ? "Copy Entries as " + type : "Copy Entry as " + type )
                + appendix;
            return text;
        }
        else if ( getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length == 0
            && getSelectedSearches().length == 1 && getSelectedSearches()[0].getSearchResults() != null
            && getSelectedSearches()[0].getSearchResults().length > 0 )
        {
            String text = ( getSelectedSearches()[0].getSearchResults().length > 1 ? "Copy Search Results as " + type
                : "Copy Search Result as " + type )
                + appendix;
            return text;
        }

        return "Copy Entry as " + type + appendix;
    }


    public String getCommandId()
    {
        return null;
    }


    public void run()
    {

        // entries to copy
        List entryList = new ArrayList();
        for ( int i = 0; i < getSelectedEntries().length; i++ )
        {
            entryList.add( getSelectedEntries()[i] );
        }
        for ( int i = 0; i < getSelectedSearchResults().length; i++ )
        {
            entryList.add( getSelectedSearchResults()[i].getEntry() );
        }
        for ( int i = 0; i < getSelectedBookmarks().length; i++ )
        {
            IEntry entry = getSelectedBookmarks()[0].getConnection().getEntryFromCache(
                getSelectedBookmarks()[0].getDn() );
            if ( entry == null )
            {
                ReadEntryJob job = new ReadEntryJob( getSelectedBookmarks()[0].getConnection(),
                    getSelectedBookmarks()[0].getDn() );
                RunnableContextJobAdapter.execute( job );
                entry = job.getReadEntry();
            }
            entryList.add( entry );
        }
        if ( getSelectedSearches().length == 1 )
        {
            ISearchResult[] results = getSelectedSearches()[0].getSearchResults();
            for ( int k = 0; k < results.length; k++ )
            {
                entryList.add( results[k].getEntry() );
            }
        }
        IEntry[] entries = ( IEntry[] ) entryList.toArray( new IEntry[entryList.size()] );

        // check uninitialized entries
        List uninitializedEntryList = new ArrayList();
        for ( int i = 0; entries != null && i < entries.length; i++ )
        {
            if ( !entries[i].isAttributesInitialized() )
            {
                uninitializedEntryList.add( entries[i] );
            }
        }
        if ( uninitializedEntryList.size() > 0
            && ( this.mode == MODE_NORMAL || this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES ) )
        {
            IEntry[] uninitializedEntries = ( IEntry[] ) uninitializedEntryList
                .toArray( new IEntry[uninitializedEntryList.size()] );

            InitializeAttributesJob job = new InitializeAttributesJob( uninitializedEntries, false );
            RunnableContextJobAdapter.execute( job );

            // SyncInitializeEntryJob job = new
            // SyncInitializeEntryJob(uninitializedEntries,
            // InitializeEntryJob.INIT_ATTRIBUTES_MODE, null);
            // job.execute();
        }

        // serialize
        StringBuffer text = new StringBuffer();
        serialializeEntries( entries, text );
        copyToClipboard( text.toString() );
    }


    protected abstract void serialializeEntries( IEntry[] entries, StringBuffer text );


    public boolean isEnabled()
    {
        boolean showOperational = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES );

        if ( getSelectedSearchResults().length > 0
            && getSelectedEntries().length + getSelectedBookmarks().length + getSelectedSearches().length == 0 )
        {
            return ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY || this.mode == MODE_NORMAL
                || this.mode == MODE_DN_ONLY || ( this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES && showOperational ) );
        }
        if ( getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length > 0
            && getSelectedSearches().length == 0 )
        {
            return ( this.mode == MODE_NORMAL || this.mode == MODE_DN_ONLY || ( this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES && showOperational ) );
        }
        if ( getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length == 0
            && getSelectedSearches().length == 1 && getSelectedSearches()[0].getSearchResults() != null
            && getSelectedSearches()[0].getSearchResults().length > 0 )
        {
            return ( this.mode != MODE_INCLUDE_OPERATIONAL_ATTRIBUTES || ( this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES && showOperational ) );
        }
        return false;
    }


    protected void copyToClipboard( String text )
    {
        Clipboard clipboard = null;
        try
        {
            clipboard = new Clipboard( Display.getCurrent() );
            clipboard.setContents( new Object[]
                { text }, new Transfer[]
                { TextTransfer.getInstance() } );
        }
        finally
        {
            if ( clipboard != null )
                clipboard.dispose();
        }
    }

}
