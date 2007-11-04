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

import javax.naming.ldap.Control;
import javax.naming.ldap.ManageReferralControl;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.EntryRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.utils.DnUtils;


/**
 * Job to rename an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RenameEntryJob extends AbstractNotificationJob
{

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The old entry. */
    private IEntry oldEntry;

    /** The new rdn. */
    private Rdn newRdn;

    /** The delete old rdn flag. */
    private boolean deleteOldRdn;

    /** The new entry. */
    private IEntry newEntry;

    /** The updated searches. */
    private Set<ISearch> updatedSearchesSet = new HashSet<ISearch>();


    /**
     * Creates a new instance of RenameEntryJob.
     * 
     * @param entry the entry to rename
     * @param newRdn the new rdn
     * @param deleteOldRdn the delete old rdn flag
     */
    public RenameEntryJob( IEntry entry, Rdn newRdn, boolean deleteOldRdn )
    {
        this.browserConnection = entry.getBrowserConnection();
        this.oldEntry = entry;
        this.newEntry = entry;
        this.newRdn = newRdn;
        this.deleteOldRdn = deleteOldRdn;

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
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__rename_entry_task, new String[]
            { oldEntry.getDn().getUpName() } ), 3 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        IEntry parent = oldEntry.getParententry();
        LdapDN newDn = DnUtils.composeDn( newRdn, parent.getDn() );

        // rename in directory
        // TODO: use manual/simulated rename, if rename of subtree is not
        // supported
        renameEntry( browserConnection, oldEntry, newDn, deleteOldRdn, monitor );

        if ( !monitor.errorsReported() )
        {
            // uncache old entry
            browserConnection.uncacheEntryRecursive( oldEntry );

            // rename in parent
            parent.deleteChild( oldEntry );
            newEntry = ReadEntryJob.getEntry( browserConnection, newDn, monitor );
            parent.addChild( newEntry );
            parent.setHasMoreChildren( false );

            newEntry.setHasChildrenHint( oldEntry.hasChildren() );
            if ( oldEntry.isChildrenInitialized() )
            {
                InitializeChildrenJob.initializeChildren( newEntry, monitor );
            }

            // rename in searches
            ISearch[] searches = browserConnection.getSearchManager().getSearches();
            for ( int j = 0; j < searches.length; j++ )
            {
                ISearch search = searches[j];
                if ( search.getSearchResults() != null )
                {
                    ISearchResult[] searchResults = search.getSearchResults();
                    for ( int k = 0; k < searchResults.length; k++ )
                    {
                        ISearchResult result = searchResults[k];
                        if ( oldEntry.equals( result.getEntry() ) )
                        {
                            ISearchResult[] newsrs = new ISearchResult[searchResults.length - 1];
                            System.arraycopy( searchResults, 0, newsrs, 0, k );
                            System.arraycopy( searchResults, k + 1, newsrs, k, searchResults.length - k - 1 );
                            search.setSearchResults( newsrs );
                            searchResults = newsrs;
                            k--;
                            updatedSearchesSet.add( search );
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
        }
        for ( ISearch search : updatedSearchesSet )
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
        return BrowserCoreMessages.jobs__rename_entry_error;
    }


    /**
     * Renames the entry.
     * 
     * @param browserConnection the browser connection
     * @param oldEntry the old entry
     * @param newDn the new DN
     * @param deleteOldRdn the delete old RDN flag
     * @param monitor the progress monitor
     */
    static void renameEntry( IBrowserConnection browserConnection, IEntry oldEntry, LdapDN newDn, boolean deleteOldRdn,
        StudioProgressMonitor monitor )
    {
        // dn
        String oldDnString = oldEntry.getDn().getUpName();
        String newDnString = newDn.getUpName();

        // controls
        Control[] controls = null;
        if ( oldEntry.isReferral() )
        {
            controls = new Control[]
                { new ManageReferralControl() };
        }

        browserConnection.getConnection().getJNDIConnectionWrapper().rename( oldDnString, newDnString, deleteOldRdn, controls,
            monitor );
    }

}
