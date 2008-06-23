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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.ContextNotEmptyException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.ConnectionException;
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
 * @version $Rev$, $Date$
 */
public class DeleteEntriesJob extends AbstractNotificationJob
{

    /** The entries to delete. */
    private IEntry[] entriesToDelete;
    
    /** The deleted entries. */
    private Set<IEntry> deletedEntriesSet;

    /** The entries to update. */
    private Set<IEntry> entriesToUpdateSet;

    /** The searches to update. */
    private Set<ISearch> searchesToUpdateSet;


    /**
     * Creates a new instance of DeleteEntriesJob. 
     * 
     * @param entriesToDelete the entries to delete
     */
    public DeleteEntriesJob( final IEntry[] entriesToDelete )
    {
        this.entriesToDelete = entriesToDelete;

        this.deletedEntriesSet = new HashSet<IEntry>();
        this.entriesToUpdateSet = new HashSet<IEntry>();
        this.searchesToUpdateSet = new HashSet<ISearch>();

        setName( entriesToDelete.length == 1 ? BrowserCoreMessages.jobs__delete_entries_name_1
            : BrowserCoreMessages.jobs__delete_entries_name_n );
    }


    protected Connection[] getConnections()
    {
        Connection[] connections = new Connection[entriesToDelete.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = entriesToDelete[i].getBrowserConnection().getConnection();
        }
        return connections;
    }


    protected Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.addAll( Arrays.asList( entriesToDelete ) );
        return l.toArray();
    }


    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( entriesToDelete.length == 1 ? BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__delete_entries_task_1, new String[]
                { entriesToDelete[0].getDn().getUpName() } ) : BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__delete_entries_task_n, new String[]
                { Integer.toString( entriesToDelete.length ) } ), 2 + entriesToDelete.length );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        int num = 0;
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );
        for ( int i = 0; !monitor.isCanceled() && !monitor.errorsReported() && i < entriesToDelete.length; i++ )
        {
            IEntry entryToDelete = entriesToDelete[i];
            IBrowserConnection browserConnection = entryToDelete.getBrowserConnection();

            // delete from directory
            int errorStatusSize1 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$
            num = optimisticDeleteEntryRecursive( browserConnection, entryToDelete.getDn(), num, dummyMonitor, monitor );
            int errorStatusSize2 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$

            if ( !monitor.isCanceled() )
            {
                if ( errorStatusSize1 == errorStatusSize2 )
                {
                    // delete
                    deletedEntriesSet.add( entryToDelete );
                    //entryToDelete.setChildrenInitialized( false );

                    // delete from parent entry
                    entriesToUpdateSet.add( entryToDelete.getParententry() );
                    entryToDelete.getParententry().setChildrenInitialized( false );
                    entryToDelete.getParententry().deleteChild( entryToDelete );

                    // delete from searches
                    ISearch[] searches = browserConnection.getSearchManager().getSearches();
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
                }

                // delete from cache
                browserConnection.uncacheEntryRecursive( entryToDelete );
            }
            else
            {
                entriesToUpdateSet.add( entryToDelete );
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
     * @param numberOfDeletedEntries the number of deleted entries
     * @param dummyMonitor the dummy monitor
     * @param monitor the progress monitor
     * 
     * @return the cumulative number of deleted entries
     */
    static int optimisticDeleteEntryRecursive( IBrowserConnection browserConnection, LdapDN dn,
        int numberOfDeletedEntries, StudioProgressMonitor dummyMonitor, StudioProgressMonitor monitor )
    {
        // try to delete entry
        dummyMonitor.reset();
        deleteEntry( browserConnection, dn, dummyMonitor );

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
            ReferralHandlingMethod referralsHandlingMethod = browserConnection.getRootDSE().isControlSupported(
                org.apache.directory.studio.ldapbrowser.core.model.Control.MANAGEDSAIT_CONTROL.getOid() ) ? ReferralHandlingMethod.MANAGE
                : ReferralHandlingMethod.IGNORE;

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
                    while ( !dummyMonitor.isCanceled() && result.hasMore() )
                    {
                        if ( dummyMonitor.errorsReported() )
                        {
                            throw dummyMonitor.getException();
                        }

                        SearchResult sr = result.next();
                        LdapDN childDn = JNDIUtils.getDn( sr );

                        numberOfDeletedEntries = optimisticDeleteEntryRecursive( browserConnection, childDn,
                            numberOfDeletedEntries, dummyMonitor, monitor );
                        numberInBatch++;
                    }
                }
                catch ( Throwable e )
                {
                    ConnectionException ce = JNDIUtils.createConnectionException( null, e );

                    if ( ce.getLdapStatusCode() == 3 || ce.getLdapStatusCode() == 4 || ce.getLdapStatusCode() == 11 )
                    {
                        // continue with search
                    }
                    else
                    {
                        dummyMonitor.reportError( ce );
                        break;
                    }
                }
            }
            while ( numberInBatch > 0 && !monitor.isCanceled() && !dummyMonitor.errorsReported() );

            // try to delete the entry again 
            if ( !dummyMonitor.errorsReported() )
            {
                deleteEntry( browserConnection, dn, dummyMonitor );
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
            Throwable exception = dummyMonitor.getException();
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
        for ( IEntry entry : deletedEntriesSet )
        {
            EventRegistry.fireEntryUpdated( new EntryDeletedEvent( entry.getBrowserConnection(), entry ), this );
        }
        for ( IEntry parent : entriesToUpdateSet )
        {
            EventRegistry.fireEntryUpdated( new ChildrenInitializedEvent( parent ), this );
        }
        for ( ISearch search : searchesToUpdateSet )
        {
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search,
                SearchUpdateEvent.EventDetail.SEARCH_PERFORMED ), this );
        }
    }


    protected String getErrorMessage()
    {
        return entriesToDelete.length == 1 ? BrowserCoreMessages.jobs__delete_entries_error_1
            : BrowserCoreMessages.jobs__delete_entries_error_n;
    }


    static void deleteEntry( IBrowserConnection browserConnection, LdapDN dn, StudioProgressMonitor monitor )
    {
        // controls
        List<Control> controlList = new ArrayList<Control>();
        if ( browserConnection.getRootDSE().isControlSupported(
            org.apache.directory.studio.ldapbrowser.core.model.Control.TREEDELETE_CONTROL.getOid() ) )
        {
            Control treeDeleteControl = new BasicControl(
                org.apache.directory.studio.ldapbrowser.core.model.Control.TREEDELETE_CONTROL.getOid(),
                org.apache.directory.studio.ldapbrowser.core.model.Control.TREEDELETE_CONTROL.isCritical(),
                org.apache.directory.studio.ldapbrowser.core.model.Control.TREEDELETE_CONTROL.getControlValue() );
            controlList.add( treeDeleteControl );
        }
        Control[] controls = controlList.toArray( new Control[controlList.size()] );

        // do not follow referrals
        ReferralHandlingMethod referralsHandlingMethod = browserConnection.getRootDSE().isControlSupported(
            org.apache.directory.studio.ldapbrowser.core.model.Control.MANAGEDSAIT_CONTROL.getOid() ) ? ReferralHandlingMethod.MANAGE
            : ReferralHandlingMethod.IGNORE;

        // delete entry
        browserConnection.getConnection().getJNDIConnectionWrapper().deleteEntry( dn.getUpName(),
            referralsHandlingMethod, controls, monitor, null );
    }

}
