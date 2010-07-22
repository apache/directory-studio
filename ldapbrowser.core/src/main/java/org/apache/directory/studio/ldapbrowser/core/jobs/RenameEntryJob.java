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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.ContextNotEmptyException;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.Control;
import javax.naming.ldap.ManageReferralControl;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.EntryRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;


/**
 * Job to rename an entry.
 *
 * First it tries to rename an entry using an modrdn operation. If
 * that operation fails with an LDAP error 66 (ContextNotEmptyException)
 * the use is asked if s/he wants to simulate such a rename by recursively
 * searching/creating/deleting entries.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameEntryJob extends AbstractNotificationJob
{

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The old entry. */
    private IEntry oldEntry;

    /** The new RDN. */
    private Rdn newRdn;

    /** The new entry. */
    private IEntry newEntry;

    /** The updated searches. */
    private Set<ISearch> searchesToUpdateSet = new HashSet<ISearch>();

    /** The dialog to ask for simulated renaming */
    private SimulateRenameDialog dialog;


    /**
     * Creates a new instance of RenameEntryJob.
     * 
     * @param entry the entry to rename
     * @param newRdn the new RDN
     * @param dialog the dialog
     */
    public RenameEntryJob( IEntry entry, Rdn newRdn, SimulateRenameDialog dialog )
    {
        this.browserConnection = entry.getBrowserConnection();
        this.oldEntry = entry;
        this.newEntry = null;
        this.newRdn = newRdn;
        this.dialog = dialog;

        setName( BrowserCoreMessages.jobs__rename_entry_name );
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
        List<Object> l = new ArrayList<Object>();
        l.add( oldEntry.getParententry() );
        return l.toArray();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__rename_entry_task, new String[]
            { oldEntry.getDn().getUpName() } ), 3 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        LdapDN oldDn = oldEntry.getDn();
        LdapDN parentDn = DnUtils.getParent( oldDn );
        LdapDN newDn = DnUtils.composeDn( newRdn, parentDn );

        // use a dummy monitor to be able to handle exceptions
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );

        // try to rename entry
        renameEntry( browserConnection, oldEntry, newDn, dummyMonitor );

        // do a simulated rename, if renaming of a non-leaf entry is not supported.
        if ( dummyMonitor.errorsReported() && !monitor.isCanceled() )
        {
            if ( dialog != null && dummyMonitor.getException() instanceof ContextNotEmptyException )
            {
                // open dialog
                dialog.setEntryInfo( browserConnection, oldDn, newDn );
                dialog.open();
                boolean isSimulatedRename = dialog.isSimulateRename();

                if ( isSimulatedRename )
                {
                    // do simulated rename operation
                    dummyMonitor.reset();
                    CopyEntriesJob.copyEntry( oldEntry, oldEntry.getParententry(), newRdn,
                        SearchControls.SUBTREE_SCOPE, 0, null, dummyMonitor, monitor );

                    if ( !dummyMonitor.errorsReported() )
                    {
                        dummyMonitor.reset();
                        DeleteEntriesJob.optimisticDeleteEntryRecursive( browserConnection, oldDn, oldEntry
                            .isReferral(), false, 0, dummyMonitor, monitor );
                    }
                }
                else
                {
                    // no simulated rename operation
                    // report the exception to the real monitor
                    Exception exception = dummyMonitor.getException();
                    monitor.reportError( exception );
                }
            }
            else
            {
                // we have another exception
                // report it to the real monitor
                Exception exception = dummyMonitor.getException();
                monitor.reportError( exception );
            }
        }

        // update model
        if ( !monitor.errorsReported() && !monitor.isCanceled() )
        {
            // uncache old entry
            browserConnection.uncacheEntryRecursive( oldEntry );

            // remove old entry and add new entry to parent
            IEntry parent = oldEntry.getParententry();
            boolean hasMoreChildren = parent.hasMoreChildren();
            parent.deleteChild( oldEntry );

            List<StudioControl> controls = new ArrayList<StudioControl>();
            if ( oldEntry.isReferral() )
            {
                controls.add( StudioControl.MANAGEDSAIT_CONTROL );
            }

            // Here we try to read the renamed entry to be able to send the right event notification.
            // In some cases this don't work:
            // - if there was a referral and the entry was created on another (master) server and not yet sync'ed to the current server
            // So we use a dummy monitor to no bother the user with an error message.
            dummyMonitor.reset();
            newEntry = ReadEntryRunnable.getEntry( browserConnection, newDn, controls, dummyMonitor );
            dummyMonitor.done();
            if ( newEntry != null )
            {
                parent.addChild( newEntry );
            }
            parent.setHasMoreChildren( hasMoreChildren );

            // reset searches, if the renamed entry is a result of a search
            List<ISearch> searches = browserConnection.getSearchManager().getSearches();
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


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        if ( oldEntry != null && newEntry != null )
        {
            EventRegistry.fireEntryUpdated( new EntryRenamedEvent( oldEntry, newEntry ), this );

            for ( ISearch search : searchesToUpdateSet )
            {
                EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search,
                    SearchUpdateEvent.EventDetail.SEARCH_PERFORMED ), this );
            }
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__rename_entry_error;
    }


    /**
     * Moves/Renames an entry.
     * 
     * @param browserConnection the browser connection
     * @param entry the entry to move/rename
     * @param newDn the new DN
     * @param monitor the progress monitor
     */
    static void renameEntry( IBrowserConnection browserConnection, IEntry entry, LdapDN newDn,
        StudioProgressMonitor monitor )
    {
        // DNs
        String oldDnString = entry.getDn().getUpName();
        String newDnString = newDn.getUpName();

        // ManageDsaIT control
        Control[] controls = null;
        if ( entry.isReferral() )
        {
            controls = new Control[]
                { new ManageReferralControl( false ) };
        }

        if ( browserConnection.getConnection() != null )
        {
            browserConnection.getConnection().getJNDIConnectionWrapper().renameEntry( oldDnString, newDnString, true,
                controls, monitor, null );
        }
    }

}
