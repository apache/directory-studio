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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.ContextNotEmptyException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;
import javax.naming.ldap.ManageReferralControl;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BulkModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.utils.JNDIUtils;


/**
 * Job to delete entries.
 * 
 * Deletes the entry recursively in a optimistic way:
 * <ol>
 * <li>Delete the entry
 * <li>If that fails with error code 66 then perform a one-level search
 *     and start from 1. for each entry. 
 * </ol>
 *
 * TODO: delete subentries?
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteEntriesJob extends AbstractNotificationJob
{

    /** The entries to delete. */
    private Collection<IEntry> entriesToDelete;

    /** The deleted entries. */
    private Set<IEntry> deletedEntriesSet;

    /** The searches to update. */
    private Set<ISearch> searchesToUpdateSet;

    /** The use tree delete control flag. */
    private boolean useTreeDeleteControl;


    /**
     * Creates a new instance of DeleteEntriesJob. 
     * 
     * @param entriesToDelete the entries to delete
     */
    public DeleteEntriesJob( final Collection<IEntry> entriesToDelete, boolean useTreeDeleteControl )
    {
        this.entriesToDelete = entriesToDelete;
        this.useTreeDeleteControl = useTreeDeleteControl;

        this.deletedEntriesSet = new HashSet<IEntry>();
        this.searchesToUpdateSet = new HashSet<ISearch>();

        setName( entriesToDelete.size() == 1 ? BrowserCoreMessages.jobs__delete_entries_name_1
            : BrowserCoreMessages.jobs__delete_entries_name_n );
    }


    protected Connection[] getConnections()
    {
        Connection[] connections = new Connection[entriesToDelete.size()];
        int i = 0;
        for ( IEntry entry : entriesToDelete )
        {
            connections[i] = entry.getBrowserConnection().getConnection();
            i++;
        }
        return connections;
    }


    protected Object[] getLockedObjects()
    {
        List<IEntry> l = new ArrayList<IEntry>();
        l.addAll( entriesToDelete );
        return l.toArray();
    }


    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( entriesToDelete.size() == 1 ? BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__delete_entries_task_1, new String[]
                { entriesToDelete.iterator().next().getDn().getUpName() } ) : BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__delete_entries_task_n, new String[]
                { Integer.toString( entriesToDelete.size() ) } ), 2 + entriesToDelete.size() );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        int num = 0;
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );
        for ( Iterator<IEntry> iterator = entriesToDelete.iterator(); !monitor.isCanceled()
            && !monitor.errorsReported() && iterator.hasNext(); )
        {
            IEntry entryToDelete = iterator.next();
            IBrowserConnection browserConnection = entryToDelete.getBrowserConnection();

            // delete from directory
            int errorStatusSize1 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$
            num = optimisticDeleteEntryRecursive( browserConnection, entryToDelete.getDn(), entryToDelete.isReferral(),
                useTreeDeleteControl, num, dummyMonitor, monitor );
            int errorStatusSize2 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$

            if ( !monitor.isCanceled() )
            {
                if ( errorStatusSize1 == errorStatusSize2 )
                {
                    // delete
                    deletedEntriesSet.add( entryToDelete );
                    //entryToDelete.setChildrenInitialized( false );

                    // delete from parent entry
                    entryToDelete.getParententry().setChildrenInitialized( false );
                    entryToDelete.getParententry().deleteChild( entryToDelete );

                    // delete from searches
                    List<ISearch> searches = browserConnection.getSearchManager().getSearches();
                    for ( ISearch search : searches )
                    {
                        if ( search.getSearchResults() != null )
                        {
                            ISearchResult[] searchResults = search.getSearchResults();
                            List<ISearchResult> searchResultList = new ArrayList<ISearchResult>();
                            searchResultList.addAll( Arrays.asList( searchResults ) );
                            for ( Iterator<ISearchResult> it = searchResultList.iterator(); it.hasNext(); )
                            {
                                ISearchResult result = it.next();
                                if ( entryToDelete.equals( result.getEntry() ) )
                                {
                                    it.remove();
                                    searchesToUpdateSet.add( search );
                                }
                            }
                            if ( searchesToUpdateSet.contains( search ) )
                            {
                                search.setSearchResults( searchResultList.toArray( new ISearchResult[searchResultList
                                    .size()] ) );
                            }
                        }
                    }

                    // delete from cache
                    browserConnection.uncacheEntryRecursive( entryToDelete );
                }
            }
            else
            {
                entryToDelete.setChildrenInitialized( false );
            }

            monitor.worked( 1 );
        }
    }


    /**
     * Deletes the entry recursively in a optimistic way:
     * <ol>
     * <li>Deletes the entry
     * <li>If that fails then perform a one-level search and call the 
     * method for each found entry
     * </ol>
     * 
     * @param browserConnection the browser connection
     * @param dn the DN to delete
     * @param useManageDsaItControl true to use the ManageDsaIT control
     * @param useTreeDeleteControl true to use the tree delete control
     * @param numberOfDeletedEntries the number of deleted entries
     * @param dummyMonitor the dummy monitor
     * @param monitor the progress monitor
     * 
     * @return the cumulative number of deleted entries
     */
    static int optimisticDeleteEntryRecursive( IBrowserConnection browserConnection, LdapDN dn,
        boolean useManageDsaItControl, boolean useTreeDeleteControl, int numberOfDeletedEntries,
        StudioProgressMonitor dummyMonitor, StudioProgressMonitor monitor )
    {
        // try to delete entry
        dummyMonitor.reset();
        deleteEntry( browserConnection, dn, useManageDsaItControl, useTreeDeleteControl, dummyMonitor );

        if ( !dummyMonitor.errorsReported() )
        {
            numberOfDeletedEntries++;
            monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.model__deleted_n_entries,
                new String[]
                    { "" + numberOfDeletedEntries } ) ); //$NON-NLS-1$
        }
        else if ( dummyMonitor.getException() instanceof ContextNotEmptyException )
        {
            // do not follow referrals or dereference aliases when deleting entries
            AliasDereferencingMethod aliasDereferencingMethod = AliasDereferencingMethod.NEVER;
            ReferralHandlingMethod referralsHandlingMethod = ReferralHandlingMethod.IGNORE;

            // perform one-level search and delete recursively
            int numberInBatch;
            dummyMonitor.reset();
            do
            {
                numberInBatch = 0;

                SearchControls searchControls = new SearchControls();
                searchControls.setCountLimit( 1000 );
                searchControls.setReturningAttributes( new String[0] );
                searchControls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
                NamingEnumeration<SearchResult> result = browserConnection.getConnection().getJNDIConnectionWrapper()
                    .search( dn.getUpName(), ISearch.FILTER_TRUE, searchControls, aliasDereferencingMethod,
                        referralsHandlingMethod, null, dummyMonitor, null );

                try
                {
                    // delete all child entries
                    while ( !dummyMonitor.isCanceled() && !dummyMonitor.errorsReported() && result.hasMore() )
                    {
                        SearchResult sr = result.next();
                        LdapDN childDn = JNDIUtils.getDn( sr );

                        numberOfDeletedEntries = optimisticDeleteEntryRecursive( browserConnection, childDn, false,
                            false, numberOfDeletedEntries, dummyMonitor, monitor );
                        numberInBatch++;
                    }
                }
                catch ( Exception e )
                {
                    int ldapStatusCode = JNDIUtils.getLdapStatusCode( e );
                    if ( ldapStatusCode == 3 || ldapStatusCode == 4 || ldapStatusCode == 11 )
                    {
                        // continue with search
                    }
                    else
                    {
                        dummyMonitor.reportError( e );
                        break;
                    }
                }
            }
            while ( numberInBatch > 0 && !monitor.isCanceled() && !dummyMonitor.errorsReported() );

            // try to delete the entry again 
            if ( !dummyMonitor.errorsReported() )
            {
                deleteEntry( browserConnection, dn, false, false, dummyMonitor );
            }
            if ( !dummyMonitor.errorsReported() )
            {
                numberOfDeletedEntries++;
                monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.model__deleted_n_entries,
                    new String[]
                        { "" + numberOfDeletedEntries } ) ); //$NON-NLS-1$
            }
        }
        else
        {
            Exception exception = dummyMonitor.getException();
            // we have another exception
            // report it to the dummy monitor if we are in the recursion
            dummyMonitor.reportError( exception );
            // also report it to the real monitor
            monitor.reportError( exception );
        }

        return numberOfDeletedEntries;
    }


    protected void runNotification()
    {
        // don't fire an EntryDeletedEvent for each deleted entry
        // that would cause massive UI updates
        // instead we unset children information and fire a BulkModificationEvent
        IBrowserConnection browserConnection = entriesToDelete.iterator().next().getBrowserConnection();
        EventRegistry.fireEntryUpdated( new BulkModificationEvent( browserConnection ), this );

        for ( ISearch search : searchesToUpdateSet )
        {
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search,
                SearchUpdateEvent.EventDetail.SEARCH_PERFORMED ), this );
        }
    }


    protected String getErrorMessage()
    {
        return entriesToDelete.size() == 1 ? BrowserCoreMessages.jobs__delete_entries_error_1
            : BrowserCoreMessages.jobs__delete_entries_error_n;
    }


    static void deleteEntry( IBrowserConnection browserConnection, LdapDN dn, boolean useManageDsaItControl,
        boolean useTreeDeleteControl, StudioProgressMonitor monitor )
    {
        // controls
        List<Control> controlList = new ArrayList<Control>();
        if ( useTreeDeleteControl
            && browserConnection.getRootDSE().isControlSupported( StudioControl.TREEDELETE_CONTROL.getOid() ) )
        {
            Control treeDeleteControl = new BasicControl( StudioControl.TREEDELETE_CONTROL.getOid(),
                StudioControl.TREEDELETE_CONTROL.isCritical(), StudioControl.TREEDELETE_CONTROL.getControlValue() );
            controlList.add( treeDeleteControl );
        }
        if ( useManageDsaItControl
            && browserConnection.getRootDSE().isControlSupported( StudioControl.MANAGEDSAIT_CONTROL.getOid() ) )
        {
            controlList.add( new ManageReferralControl( false ) );
        }
        Control[] controls = controlList.toArray( new Control[controlList.size()] );

        // delete entry
        if ( browserConnection.getConnection() != null )
        {
            browserConnection.getConnection().getJNDIConnectionWrapper().deleteEntry( dn.getUpName(), controls,
                monitor, null );
        }
    }

}
