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

import javax.naming.ldap.Control;
import javax.naming.ldap.ManageReferralControl;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
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

    private Set<ISearch> updatedSearches = new HashSet<ISearch>();


    /**
     * Creates a new instance of MoveEntriesJob.
     * 
     * @param entries the entries to move
     * @param newParent the new parent
     */
    public MoveEntriesJob( IEntry[] entries, IEntry newParent )
    {
        this.browserConnection = newParent.getBrowserConnection();
        this.oldEntries = entries;
        this.newParent = newParent;

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
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.bind(
            oldEntries.length == 1 ? BrowserCoreMessages.jobs__move_entry_task_1
                : BrowserCoreMessages.jobs__move_entry_task_n, new String[]
                {} ), 3 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        newEntries = new IEntry[oldEntries.length];
        for ( int i = 0; i < oldEntries.length; i++ )
        {
            newEntries[i] = oldEntries[i];
        }

        for ( int i = 0; i < oldEntries.length; i++ )
        {
            IEntry oldEntry = oldEntries[i];
            IEntry oldParent = oldEntry.getParententry();
            LdapDN newDn = DnUtils.composeDn( oldEntry.getRdn(), newParent.getDn() );

            // move in directory
            // TODO: use manual/simulated move, if move of subtree is not supported (JNDI)
            int errorStatusSize1 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$
            moveEntry( browserConnection, oldEntry, newDn, monitor );
            //connection.move( oldEntry, newParent.getDn(), monitor );
            int errorStatusSize2 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$

            if ( errorStatusSize1 == errorStatusSize2 )
            {
                // move in parent
                oldParent.deleteChild( oldEntry );
                IEntry newEntry = ReadEntryJob.getEntry( browserConnection, newDn, monitor );
                newEntries[i] = newEntry;
                newParent.addChild( newEntry );
                newParent.setHasMoreChildren( false );

                newEntry.setHasChildrenHint( oldEntry.hasChildren() );
                if ( oldEntry.isChildrenInitialized() )
                {
                    InitializeChildrenJob.initializeChildren( newEntry, monitor );
                }

                // move in searches
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
                                updatedSearches.add( search );
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
        for ( ISearch search : updatedSearches )
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


    /**
     * Moves an entry.
     * 
     * @param browserConnection the browser connection
     * @param oldEntry the old entry
     * @param newDn the new DN
     * @param monitor the progress monitor
     */
    private void moveEntry( IBrowserConnection browserConnection, IEntry oldEntry, LdapDN newDn,
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

        browserConnection.getConnection().getJNDIConnectionWrapper().rename( oldDnString, newDnString, false, controls,
            monitor );
    }

}
