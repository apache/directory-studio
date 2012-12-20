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


import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.studio.ldapbrowser.common.dialogs.RenameEntryDialog;
import org.apache.directory.studio.ldapbrowser.common.dialogs.SimulateRenameDialogImpl;
import org.apache.directory.studio.ldapbrowser.core.jobs.RenameEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IQuickSearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.impl.RootDSE;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This Action renames Connections, Entries, Searches, or Bookmarks.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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

        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IBookmark[] bookmarks = getBookmarks();

        if ( entries.length == 1 && searches.length == 0 && bookmarks.length == 0 )
        {
            return Messages.getString( "RenameAction.RenameEntry" ); //$NON-NLS-1$
        }
        else if ( searches.length == 1 && entries.length == 0 && bookmarks.length == 0 )
        {
            return Messages.getString( "RenameAction.RenameSearch" ); //$NON-NLS-1$
        }
        else if ( bookmarks.length == 1 && entries.length == 0 && searches.length == 0 )
        {
            return Messages.getString( "RenameAction.RenameBookmark" ); //$NON-NLS-1$
        }
        else
        {
            return Messages.getString( "RenameAction.Rename" ); //$NON-NLS-1$
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
        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IBookmark[] bookmarks = getBookmarks();

        if ( entries.length == 1 && searches.length == 0 && bookmarks.length == 0 )
        {
            renameEntry( entries[0] );
        }
        else if ( searches.length == 1 && entries.length == 0 && bookmarks.length == 0 )
        {
            renameSearch( searches[0] );
        }
        else if ( bookmarks.length == 1 && entries.length == 0 && searches.length == 0 )
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
            IEntry[] entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();

            return entries.length + searches.length + bookmarks.length == 1;

        }
        catch ( Exception e )
        {
            return false;
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
            Rdn newRdn = renameDialog.getRdn();
            if ( newRdn != null && !newRdn.equals( entry.getRdn() ) )
            {
                new StudioBrowserJob(
                    new RenameEntryRunnable( entry, newRdn, new SimulateRenameDialogImpl( getShell() ) ) ).execute();
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
        if ( getSelectedSearches().length == 1 && !( getSelectedSearches()[0] instanceof IQuickSearch ) )
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
                else if ( search.getBrowserConnection().getSearchManager().getSearch( newName ) != null )
                    return Messages.getString( "RenameAction.ConnectionWithThisNameAlreadyExists" ); //$NON-NLS-1$
                else
                    return null;
            }
        };

        InputDialog dialog = new InputDialog(
            getShell(),
            Messages.getString( "RenameAction.RenameSearchDialog" ), Messages.getString( "RenameAction.RenameSearchNewName" ), search.getName(), validator ); //$NON-NLS-1$ //$NON-NLS-2$

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
                else if ( bookmark.getBrowserConnection().getBookmarkManager().getBookmark( newName ) != null )
                    return Messages.getString( "RenameAction.BookmarkWithThisNameAlreadyExists" ); //$NON-NLS-1$
                else
                    return null;
            }
        };

        InputDialog dialog = new InputDialog(
            getShell(),
            Messages.getString( "RenameAction.RenameBookmarkDialog" ), Messages.getString( "RenameAction.RenameBookmarkNewName" ), bookmark.getName(), validator ); //$NON-NLS-1$ //$NON-NLS-2$

        dialog.open();
        String newName = dialog.getValue();
        if ( newName != null )
        {
            bookmark.setName( newName );
        }
    }
}
