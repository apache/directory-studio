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


import java.util.LinkedHashSet;

import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.internal.model.BaseDNEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.DirectoryMetadataEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.RootDSE;
import org.apache.directory.ldapstudio.browser.core.jobs.MoveEntriesJob;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.ui.dialogs.MoveEntriesDialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


public class MoveAction extends BrowserAction implements ModelModifier
{

    public MoveAction()
    {
        super();
    }


    public String getText()
    {

        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IBookmark[] bookmarks = getBookmarks();

        if ( entries.length > 0 && searches.length == 0 && bookmarks.length == 0 )
        {
            return entries.length == 1 ? "Move Entry..." : "Move Entries...";
        }
        // else if(searches.length > 0 && entries.length==0 &&
        // bookmarks.length==0) {
        // return entries.length == 1 ? "Move Search..." : "Move Searches...";
        // }
        // else if(bookmarks.length > 0 && entries.length==0 &&
        // searches.length==0) {
        // return entries.length == 1 ? "Move Bookmark..." : "Move
        // Bookmarks...";
        // }
        else
        {
            return "Move...";
        }
    }


    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.MOVE;
    }


    public void run()
    {

        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IBookmark[] bookmarks = getBookmarks();

        if ( entries.length > 0 && searches.length == 0 && bookmarks.length == 0 )
        {
            moveEntries( entries );
        }
        else if ( searches.length > 0 && entries.length == 0 && bookmarks.length == 0 )
        {
            // tbd
        }
        else if ( bookmarks.length > 0 && entries.length == 0 && searches.length == 0 )
        {
            // tbd
        }
    }


    public boolean isEnabled()
    {

        try
        {
            IEntry[] entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();

            return entries.length > 0 && searches.length == 0 && bookmarks.length == 0;

        }
        catch ( Exception e )
        {
            return false;
        }
    }


    protected IEntry[] getEntries()
    {

        if ( getSelectedConnections().length + getSelectedBookmarks().length + getSelectedSearches().length
            + getSelectedAttributes().length + getSelectedValues().length == 0
            && getSelectedEntries().length + getSelectedSearchResults().length > 0 )
        {

            LinkedHashSet entriesSet = new LinkedHashSet();
            for ( int i = 0; i < getSelectedEntries().length; i++ )
            {
                entriesSet.add( getSelectedEntries()[i] );
            }
            for ( int i = 0; i < this.getSelectedSearchResults().length; i++ )
            {
                entriesSet.add( this.getSelectedSearchResults()[i].getEntry() );
            }
            IEntry[] entries = ( IEntry[] ) entriesSet.toArray( new IEntry[entriesSet.size()] );
            for ( int i = 0; i < entries.length; i++ )
            {
                if ( entries[i] == null || entries[i] instanceof RootDSE
                    || entries[i] instanceof DirectoryMetadataEntry || entries[i] instanceof BaseDNEntry )
                {
                    return new IEntry[0];
                }
            }
            return entries;
        }
        else
        {
            return new IEntry[0];
        }

    }


    protected void moveEntries( final IEntry[] entries )
    {
        MoveEntriesDialog moveDialog = new MoveEntriesDialog( getShell(), entries );
        if ( moveDialog.open() == Dialog.OK )
        {
            DN newParentDn = moveDialog.getParentDn();
            if ( newParentDn != null /* && !newRdn.equals(entry.getRdn()) */)
            {
                IEntry newParentEntry = entries[0].getConnection().getEntryFromCache( newParentDn );
                if ( newParentEntry != null )
                {
                    new MoveEntriesJob( entries, newParentEntry ).execute();
                }
            }
        }
    }


    protected ISearch[] getSearches()
    {
        if ( getSelectedSearches().length == 1 )
        {
            return getSelectedSearches();
        }
        else
        {
            return new ISearch[0];
        }
    }


    protected IBookmark[] getBookmarks()
    {
        if ( getSelectedBookmarks().length == 1 )
        {
            return getSelectedBookmarks();
        }
        else
        {
            return new IBookmark[0];
        }
    }

}
