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

package org.apache.directory.ldapstudio.browser.common.actions;


import org.apache.directory.ldapstudio.browser.common.dialogs.RenameEntryDialog;
import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.internal.model.RootDSE;
import org.apache.directory.ldapstudio.browser.core.jobs.RenameEntryJob;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.RDN;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This Action renames Connections, Entries, Searches, or Bookmarks.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RenameAction extends BrowserAction
{
    /**
     * Creates a new instance of RenameAction.
     *
     */
    public RenameAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {

        IConnection[] connections = getConnections();
        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IBookmark[] bookmarks = getBookmarks();

        if ( connections.length == 1 && entries.length == 0 && searches.length == 0 && bookmarks.length == 0 )
        {
            return "Rename Connection...";
        }
        else if ( entries.length == 1 && connections.length == 0 && searches.length == 0 && bookmarks.length == 0 )
        {
            return "Rename Entry...";
        }
        else if ( searches.length == 1 && connections.length == 0 && entries.length == 0 && bookmarks.length == 0 )
        {
            return "Rename Search...";
        }
        else if ( bookmarks.length == 1 && connections.length == 0 && entries.length == 0 && searches.length == 0 )
        {
            return "Rename Bookmark...";
        }
        else
        {
            return "Rename";
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.RENAME;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IConnection[] connections = getConnections();
        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IBookmark[] bookmarks = getBookmarks();

        if ( connections.length == 1 && entries.length == 0 && searches.length == 0 && bookmarks.length == 0 )
        {
            renameConnection( connections[0] );
        }
        else if ( entries.length == 1 && connections.length == 0 && searches.length == 0 && bookmarks.length == 0 )
        {
            renameEntry( entries[0] );
        }
        else if ( searches.length == 1 && connections.length == 0 && entries.length == 0 && bookmarks.length == 0 )
        {
            renameSearch( searches[0] );
        }
        else if ( bookmarks.length == 1 && connections.length == 0 && entries.length == 0 && searches.length == 0 )
        {
            renameBookmark( bookmarks[0] );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        try
        {
            IConnection[] connections = getConnections();
            IEntry[] entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();

            return connections.length + entries.length + searches.length + bookmarks.length == 1;

        }
        catch ( Exception e )
        {
            return false;
        }
    }


    /**
     * Gets the Connections
     * 
     * @return
     *      the Connections
     */
    protected IConnection[] getConnections()
    {
        if ( getSelectedConnections().length == 1 )
        {
            return getSelectedConnections();
        }
        else
        {
            return new IConnection[0];
        }
    }


    /**
     * Renames a Connection.
     *
     * @param connection
     *      the Connection to rename
     */
    protected void renameConnection( final IConnection connection )
    {
        IInputValidator validator = new IInputValidator()
        {
            public String isValid( String newName )
            {
                if ( connection.getName().equals( newName ) )
                    return null;
                else if ( BrowserCorePlugin.getDefault().getConnectionManager().getConnection( newName ) != null )
                    return "A connection with this name already exists.";
                else
                    return null;
            }
        };

        InputDialog dialog = new InputDialog( getShell(), "Rename Connection", "New name:", connection.getName(),
            validator );

        dialog.open();
        String newName = dialog.getValue();
        if ( newName != null )
        {
            connection.setName( newName );
        }
    }


    /**
     * Gets the Entries
     *
     * @return
     *      the Entries
     */
    protected IEntry[] getEntries()
    {

        IEntry entry = null;

        if ( getSelectedEntries().length == 1 )
        {
            entry = getSelectedEntries()[0];
        }
        else if ( getSelectedSearchResults().length == 1 )
        {
            entry = getSelectedSearchResults()[0].getEntry();
        }
        else if ( getSelectedValues().length == 1 && getSelectedValues()[0].isRdnPart() )
        {
            entry = getSelectedValues()[0].getAttribute().getEntry();
        }

        if ( entry != null && !( entry instanceof RootDSE ) )
        {
            return new IEntry[]
                { entry };
        }
        else
        {
            return new IEntry[0];
        }
    }


    /**
     * Renames an Entry.
     *
     * @param entry
     *      the Entry to rename
     */
    protected void renameEntry( final IEntry entry )
    {
        RenameEntryDialog renameDialog = new RenameEntryDialog( getShell(), entry );
        if ( renameDialog.open() == Dialog.OK )
        {
            RDN newRdn = renameDialog.getRdn();
            boolean deleteOldRdn = renameDialog.isDeleteOldRdn();
            if ( newRdn != null && !newRdn.equals( entry.getRdn() ) )
            {
                new RenameEntryJob( entry, newRdn, deleteOldRdn ).execute();
            }
        }
    }


    /**
     * Get the Searches.
     *
     * @return
     *      the Searches
     */
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


    /**
     * Renames a Search.
     *
     * @param search
     *      the Search to rename
     */
    protected void renameSearch( final ISearch search )
    {
        IInputValidator validator = new IInputValidator()
        {
            public String isValid( String newName )
            {
                if ( search.getName().equals( newName ) )
                    return null;
                else if ( search.getConnection().getSearchManager().getSearch( newName ) != null )
                    return "A connection with this name already exists.";
                else
                    return null;
            }
        };

        InputDialog dialog = new InputDialog( getShell(), "Rename Search", "New name:", search.getName(), validator );

        dialog.open();
        String newName = dialog.getValue();
        if ( newName != null )
        {
            search.setName( newName );
        }
    }


    /**
     * Get the Bookmarks
     *
     * @return
     *      the Bookmarks
     */
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


    /**
     * Renames a Bookmark
     *
     * @param bookmark
     *      the Bookmark to rename
     */
    protected void renameBookmark( final IBookmark bookmark )
    {
        IInputValidator validator = new IInputValidator()
        {
            public String isValid( String newName )
            {
                if ( bookmark.getName().equals( newName ) )
                    return null;
                else if ( bookmark.getConnection().getBookmarkManager().getBookmark( newName ) != null )
                    return "A bookmark with this name already exists.";
                else
                    return null;
            }
        };

        InputDialog dialog = new InputDialog( getShell(), "Rename Bookmark", "New name:", bookmark.getName(), validator );

        dialog.open();
        String newName = dialog.getValue();
        if ( newName != null )
        {
            bookmark.setName( newName );
        }
    }
}
