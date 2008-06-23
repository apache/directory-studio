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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.ContextNotEmptyException;
import javax.naming.directory.SearchControls;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.EntryMovedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;


/**
 * Job to move entries.
 * 
 * First it tries to move an entry using an moddn operation. If
 * that operation fails with an LDAP error 66 (ContextNotEmptyException)
 * the use is asked if s/he wants to simulate such a move by recursively
 * searching/creating/deleting entries.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MoveEntriesJob extends AbstractNotificationJob
{

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The entries to move. */
    private IEntry[] oldEntries;

    /** The new parent. */
    private IEntry newParent;

    /** The moved entries. */
    private IEntry[] newEntries;

    /** The searches to update. */
    private Set<ISearch> searchesToUpdateSet = new HashSet<ISearch>();

    /** The dialog to ask for simulated renaming */
    private SimulateRenameDialog dialog;


    /**
     * Creates a new instance of MoveEntriesJob.
     * 
     * @param entries the entries to move
     * @param newParent the new parent
     * @param dialog the dialog
     */
    public MoveEntriesJob( IEntry[] entries, IEntry newParent, SimulateRenameDialog dialog )
    {
        this.browserConnection = newParent.getBrowserConnection();
        this.oldEntries = entries;
        this.newParent = newParent;
        this.dialog = dialog;
        this.newEntries = new IEntry[oldEntries.length];

        setName( entries.length == 1 ? BrowserCoreMessages.jobs__move_entry_name_1
            : BrowserCoreMessages.jobs__move_entry_name_n );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        List<IEntry> l = new ArrayList<IEntry>();
        l.add( newParent );
        l.addAll( Arrays.asList( oldEntries ) );
        return l.toArray();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.bind(
            oldEntries.length == 1 ? BrowserCoreMessages.jobs__move_entry_task_1
                : BrowserCoreMessages.jobs__move_entry_task_n, new String[]
                {} ), 3 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        // use a dummy monitor to be able to handle exceptions
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );

        int numAdd = 0;
        int numDel = 0;
        boolean isSimulatedRename = false;
        LdapDN parentDn = newParent.getDn();

        for ( int i = 0; i < oldEntries.length; i++ )
        {
            dummyMonitor.reset();

            IEntry oldEntry = oldEntries[i];
            LdapDN oldDn = oldEntry.getDn();
            LdapDN newDn = DnUtils.composeDn( oldDn.getRdn(), parentDn );

            // try to move entry
            RenameEntryJob.renameEntry( browserConnection, oldEntry, newDn, dummyMonitor );

            // do a simulated rename, if renaming of a non-leaf entry is not supported.
            if ( dummyMonitor.errorsReported() )
            {
                if ( dialog != null && dummyMonitor.getException() instanceof ContextNotEmptyException )
                {
                    // open dialog
                    if ( numAdd == 0 )
                    {
                        dialog.setEntryInfo( browserConnection, oldDn, newDn );
                        dialog.open();
                        isSimulatedRename = dialog.isSimulateRename();
                    }

                    if ( isSimulatedRename )
                    {
                        // do simulated rename operation
                        dummyMonitor.reset();

                        numAdd = CopyEntriesJob.copyEntry( oldEntry, newParent, null, SearchControls.SUBTREE_SCOPE,
                            numAdd, null, dummyMonitor, monitor );

                        if ( !dummyMonitor.errorsReported() )
                        {
                            dummyMonitor.reset();
                            numDel = DeleteEntriesJob.optimisticDeleteEntryRecursive( browserConnection, oldDn, numDel,
                                dummyMonitor, monitor );
                        }
                    }
                    else
                    {
                        // no simulated rename operation
                        // report the exception to the real monitor
                        Throwable exception = dummyMonitor.getException();
                        monitor.reportError( exception );
                    }
                }
                else
                {
                    // we have another exception
                    // report it to the real monitor
                    Throwable exception = dummyMonitor.getException();
                    monitor.reportError( exception );
                }
            }

            // update model
            if ( !dummyMonitor.errorsReported() )
            {
                // uncache old entry
                browserConnection.uncacheEntryRecursive( oldEntry );

                // remove old entry from old parent
                oldEntry.getParententry().deleteChild( oldEntry );

                // add new entry to new parent
                boolean hasMoreChildren = newParent.hasMoreChildren() || !newParent.isChildrenInitialized();
                IEntry newEntry = ReadEntryRunnable.getEntry( browserConnection, newDn, monitor );
                newEntries[i] = newEntry;
                newParent.addChild( newEntry );
                newParent.setHasMoreChildren( hasMoreChildren );

                // reset searches, if the moved entry is a result of a search
                ISearch[] searches = browserConnection.getSearchManager().getSearches();
                for ( ISearch search : searches )
                {
                    if ( search.getSearchResults() != null )
                    {
                        ISearchResult[] searchResults = search.getSearchResults();
                        for ( ISearchResult result : searchResults )
                        {
                            if ( oldEntry.equals( result.getEntry() ) )
                            {
                                search.setSearchResults( null );
                                searchesToUpdateSet.add( search );
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        for ( int i = 0; i < newEntries.length; i++ )
        {
            if ( oldEntries[i] != null && newEntries[i] != null )
            {
                EventRegistry.fireEntryUpdated( new EntryMovedEvent( oldEntries[i], newEntries[i] ), this );
            }
        }
        for ( ISearch search : searchesToUpdateSet )
        {
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search,
                SearchUpdateEvent.EventDetail.SEARCH_PERFORMED ), this );
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return oldEntries.length == 1 ? BrowserCoreMessages.jobs__move_entry_error_1
            : BrowserCoreMessages.jobs__move_entry_error_n;
    }

}
