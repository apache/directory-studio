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


import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IContinuation;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IContinuation.State;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * Job to open an entry editor. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenEntryEditorJob extends Job
{
    private IEntry[] entries;
    private ISearchResult[] searchResults;
    private IBookmark[] bookmarks;
    private EntryEditorExtension extension;


    /**
     * Creates a new instance of OpenEntryEditorJob.
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
    public OpenEntryEditorJob( IEntry[] entries, ISearchResult[] searchResults, IBookmark[] bookmarks )
    {
        super( "" ); //$NON-NLS-1$
        this.entries = entries;
        this.searchResults = searchResults;
        this.bookmarks = bookmarks;

        // Using a high priority for this job
        setPriority( Job.INTERACTIVE );
    }


    /**
     * Creates a new instance of OpenEntryEditorJob.
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
    public OpenEntryEditorJob( EntryEditorExtension extension, IEntry[] entries, ISearchResult[] searchResults,
        IBookmark[] bookmarks )
    {
        super( "" ); //$NON-NLS-1$
        this.extension = extension;
        this.entries = entries;
        this.searchResults = searchResults;
        this.bookmarks = bookmarks;

        // Using a high priority for this job
        setPriority( Job.INTERACTIVE );
    }


    /**
     * {@inheritDoc}
     */
    protected IStatus run( IProgressMonitor monitor )
    {
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
                try
                {
                    // Making sure attributes are initialized
                    StudioBrowserJob job = EntryEditorUtils.ensureAttributesInitialized( entry );
                    if ( job != null )
                    {
                        // Waiting for the entry's attributes to be initialized
                        job.join();
                    }
                }
                catch ( InterruptedException e )
                {
                    // Nothing to do
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
        Display.getDefault().asyncExec( new Runnable()
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

        return Status.OK_STATUS;
    }
}
