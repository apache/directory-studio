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

package org.apache.directory.studio.entryeditors;


import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgressAdapter;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IContinuation;
import org.apache.directory.studio.ldapbrowser.core.model.IContinuation.State;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * Runnable to open an entry editor. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenEntryEditorRunnable extends StudioConnectionRunnableWithProgressAdapter implements
    StudioConnectionBulkRunnableWithProgress
{
    private IEntry[] entries;
    private ISearchResult[] searchResults;
    private IBookmark[] bookmarks;
    private EntryEditorExtension extension;


    /**
     * Creates a new instance of OpenEntryEditorRunnable.
     * <p>
     * Opens an entry editor from one of the given entries, 
     * search results or bookmarks.
     * 
     * @param entries
     *      an array of entries
     * @param searchResults
     *      an array of search results
     * @param bookmarks
     *      an arrays of bookmarks
     */
    public OpenEntryEditorRunnable( IEntry[] entries, ISearchResult[] searchResults, IBookmark[] bookmarks )
    {
        super();
        this.entries = entries;
        this.searchResults = searchResults;
        this.bookmarks = bookmarks;
    }


    /**
     * Creates a new instance of OpenEntryEditorRunnable.
     * <p>
     * Opens an entry editor with the given entry editor extension and one of 
     * the given entries, search results or bookmarks.
     * 
     * @param extension
     *      the entry editor extension
     * @param entries
     *      an array of entries
     * @param searchResults
     *      an array of search results
     * @param bookmarks
     *      an arrays of bookmarks
     */
    public OpenEntryEditorRunnable( EntryEditorExtension extension, IEntry[] entries, ISearchResult[] searchResults,
        IBookmark[] bookmarks )
    {
        super();
        this.extension = extension;
        this.entries = entries;
        this.searchResults = searchResults;
        this.bookmarks = bookmarks;
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return Messages.getString( "OpenEntryEditorRunnable.OpenEntryEditor" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        if ( entries.length == 1 )
        {
            return new Object[]
                { entries[0] };
        }
        else if ( searchResults.length == 1 )
        {
            return new Object[]
                { searchResults[0].getEntry() };
        }
        else if ( bookmarks.length == 1 )
        {
            return new Object[]
                { bookmarks[0].getEntry() };
        }
        else
        {
            return new Object[0];
        }
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        if ( entries.length == 1 )
        {
            return new Connection[]
                { entries[0].getBrowserConnection().getConnection() };
        }
        else if ( searchResults.length == 1 )
        {
            return new Connection[]
                { searchResults[0].getEntry().getBrowserConnection().getConnection() };
        }
        else if ( bookmarks.length == 1 )
        {
            return new Connection[]
                { bookmarks[0].getEntry().getBrowserConnection().getConnection() };
        }
        else
        {
            return new Connection[0];
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.setTaskName( Messages.getString( "OpenEntryEditorRunnable.OpeningEntryEditor" ) ); //$NON-NLS-1$

        // Getting the entry to open
        IEntry entry = null;

        if ( entries.length == 1 )
        {
            entry = entries[0];
        }
        else if ( searchResults.length == 1 )
        {
            entry = searchResults[0].getEntry();
        }
        else if ( bookmarks.length == 1 )
        {
            entry = bookmarks[0].getEntry();
        }

        if ( entry != null )
        {
            if ( entry instanceof IContinuation )
            {
                IContinuation continuation = ( IContinuation ) entry;
                if ( continuation.getState() == State.UNRESOLVED )
                {
                    continuation.resolve();
                }
            }
            else
            {
                // Making sure attributes are initialized
                if ( !entry.isAttributesInitialized() )
                {
                    InitializeAttributesRunnable.initializeAttributes( entry, monitor );
                }
            }
        }

        // If no entry editor was provided, find the correct one
        if ( extension == null )
        {
            // Looking for the correct entry editor
            for ( EntryEditorExtension entryEditorExtension : BrowserUIPlugin.getDefault().getEntryEditorManager()
                .getSortedEntryEditorExtensions() )
            {
                // Verifying that the editor can handle the entry
                if ( entryEditorExtension.getEditorInstance().canHandle( entry ) )
                {
                    extension = entryEditorExtension;
                    break;
                }
            }
        }

        // Getting the editor's ID and creating the proper editor input
        final String editorId = extension.getEditorId();
        final EntryEditorInput editorInput;
        if ( entries.length == 1 )
        {
            editorInput = new EntryEditorInput( entries[0], extension );
        }
        else if ( searchResults.length == 1 )
        {
            editorInput = new EntryEditorInput( searchResults[0], extension );
        }
        else if ( bookmarks.length == 1 )
        {
            editorInput = new EntryEditorInput( bookmarks[0], extension );
        }
        else
        {
            editorInput = new EntryEditorInput( ( IEntry ) null, extension );
        }

        // Opening the editor
        Display.getDefault().syncExec( new Runnable()
        {
            public void run()
            {
                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( editorInput,
                        editorId, false );
                }
                catch ( PartInitException e )
                {
                    throw new RuntimeException( e );
                }
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        // Nothing to notify
    }
}
