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

import javax.naming.ldap.Control;
import javax.naming.ldap.ManageReferralControl;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.AliasDereferencingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;


/*
 * Search+Delete vs. Delete+SearchOnError
 * 
 * Test for:
 * - delete leaf entry 100.000 times
 * - tree with 100.000 childs
 * - tree with 1000 childs each with 1000 childs 
 * 
 */

public class DeleteEntriesJob extends AbstractNotificationJob
{

    private IEntry[] entriesToDelete;

    private Set deletedEntriesSet = new HashSet();

    private Set entriesToUpdateSet = new HashSet();

    private Set searchesToUpdateSet = new HashSet();


    public DeleteEntriesJob( final IEntry[] entriesToDelete )
    {
        this.entriesToDelete = entriesToDelete;
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
        List l = new ArrayList();
        l.addAll( Arrays.asList( entriesToDelete ) );
        return l.toArray();
    }


    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {

        monitor.beginTask( entriesToDelete.length == 1 ? BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__delete_entries_task_1, new String[]
                { entriesToDelete[0].getDn().toString() } ) : BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__delete_entries_task_n, new String[]
                { Integer.toString( entriesToDelete.length ) } ), 2 + entriesToDelete.length );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        int num = 0;
        for ( int i = 0; !monitor.isCanceled() && !monitor.errorsReported() && i < entriesToDelete.length; i++ )
        {

            IEntry entryToDelete = entriesToDelete[i];
            IBrowserConnection connection = entryToDelete.getBrowserConnection();

            // delete from directory
            // TODO: use TreeDelete Control, if available
            int errorStatusSize1 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$
            num = deleteEntryRecursive( entryToDelete, false, num, monitor );
            int errorStatusSize2 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$
            deletedEntriesSet.add( entryToDelete );

            if ( errorStatusSize1 == errorStatusSize2 )
            {
                // delete from parent
                entryToDelete.getParententry().deleteChild( entryToDelete );
                entriesToUpdateSet.add( entryToDelete.getParententry() );

                // delete from searches
                ISearch[] searches = connection.getSearchManager().getSearches();
                for ( int j = 0; j < searches.length; j++ )
                {
                    ISearch search = searches[j];
                    if ( search.getSearchResults() != null )
                    {
                        ISearchResult[] searchResults = search.getSearchResults();
                        for ( int k = 0; k < searchResults.length; k++ )
                        {
                            ISearchResult result = searchResults[k];
                            if ( entryToDelete.equals( result.getEntry() ) )
                            {
                                ISearchResult[] newsrs = new ISearchResult[searchResults.length - 1];
                                System.arraycopy( searchResults, 0, newsrs, 0, k );
                                System.arraycopy( searchResults, k + 1, newsrs, k, searchResults.length - k - 1 );
                                search.setSearchResults( newsrs );
                                searchResults = newsrs;
                                k--;
                                searchesToUpdateSet.add( search );
                            }
                        }
                    }
                }
            }

            monitor.worked( 1 );
        }
    }


    
    private int deleteEntryRecursive( IBrowserConnection browserConnection, String dn, int numberOfDeletedEntries, StudioProgressMonitor monitor )
    {
//        int numberInBatch;
//        
//        JNDIConnectionWrapper connectionWrapper = browserConnection.getConnection().getJNDIConnectionWrapper();
//        
//        SearchControls searchControls = new SearchControls();
//        searchControls.setCountLimit( 1000 );
//        searchControls.setReturningAttributes( new String[]
//                                                          { IAttribute.OBJECTCLASS_ATTRIBUTE, IAttribute.REFERRAL_ATTRIBUTE } );
//        searchControls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
//        
//        connectionWrapper.search( dn, ISearch.FILTER_TRUE, searchControls, derefAliasMethod, handleReferralsMethod, controls, monitor )
        
        return 0;
    }
    
    
    private int deleteEntryRecursive( IEntry entry, boolean refInitialized, int numberOfDeletedEntries,
        StudioProgressMonitor monitor )
    {
        try
        {
            int numberInBatch;
            do
            {
                numberInBatch = 0;
                
                // TODO: use JNDI here!!!
                
                SearchParameter subParam = new SearchParameter();
                subParam.setSearchBase( entry.getDn() );
                subParam.setFilter( ISearch.FILTER_TRUE );
                subParam.setScope( SearchScope.ONELEVEL );
                subParam.setAliasesDereferencingMethod( AliasDereferencingMethod.NEVER );
                subParam.setReferralsHandlingMethod( ReferralHandlingMethod.IGNORE );
                subParam.setReturningAttributes( new String[]
                    { IAttribute.OBJECTCLASS_ATTRIBUTE, IAttribute.REFERRAL_ATTRIBUTE } );
                subParam.setCountLimit( 1000 );
                ISearch search = new Search( entry.getBrowserConnection(), subParam );
                SearchJob.searchAndUpdateModel( entry.getBrowserConnection(), search, monitor );

                ISearchResult[] srs = search.getSearchResults();
                for ( int i = 0; !monitor.isCanceled() && srs != null && i < srs.length; i++ )
                {
                    IEntry childEntry = srs[i].getEntry();
                    numberOfDeletedEntries = this.deleteEntryRecursive( childEntry, true, numberOfDeletedEntries,
                        monitor );
                    numberInBatch++;
                }
            }
            while ( numberInBatch > 0 && !monitor.isCanceled() && !monitor.errorsReported() );

            if ( !monitor.isCanceled() && !monitor.errorsReported() )
            {
                // check for referrals
                if ( !refInitialized )
                {
                    // TODO: use JNDI here!!!
                    SearchParameter param = new SearchParameter();
                    param.setSearchBase( entry.getDn() );
                    param.setFilter( ISearch.FILTER_TRUE );
                    param.setScope( SearchScope.OBJECT );
                    param.setAliasesDereferencingMethod( AliasDereferencingMethod.NEVER );
                    param.setReferralsHandlingMethod( ReferralHandlingMethod.IGNORE );
                    param.setReturningAttributes( new String[]
                        { IAttribute.OBJECTCLASS_ATTRIBUTE, IAttribute.REFERRAL_ATTRIBUTE } );
                    ISearch search = new Search( entry.getBrowserConnection(), param );
                    SearchJob.searchAndUpdateModel( entry.getBrowserConnection(), search, monitor );

                    ISearchResult[] srs = search.getSearchResults();
                    if ( !monitor.isCanceled() && srs != null && srs.length == 1 )
                    {
                        entry = srs[0].getEntry();
                    }
                }

                int errorStatusSize1 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$
                deleteEntry( entry.getBrowserConnection(), entry, monitor );
                int errorStatusSize2 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$

                if ( errorStatusSize1 == errorStatusSize2 )
                {
                    numberOfDeletedEntries++;
                    monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.model__deleted_n_entries,
                        new String[]
                            { "" + numberOfDeletedEntries } ) ); //$NON-NLS-1$
                }
            }

        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }

        return numberOfDeletedEntries;
    }


    protected void runNotification()
    {
        for ( Iterator it = deletedEntriesSet.iterator(); it.hasNext(); )
        {
            IEntry entry = ( IEntry ) it.next();
            EventRegistry.fireEntryUpdated( new EntryDeletedEvent( entry.getBrowserConnection(), entry ), this );
        }
        for ( Iterator it = entriesToUpdateSet.iterator(); it.hasNext(); )
        {
            IEntry parent = ( IEntry ) it.next();
            EventRegistry.fireEntryUpdated( new ChildrenInitializedEvent( parent ), this );
        }
        for ( Iterator it = searchesToUpdateSet.iterator(); it.hasNext(); )
        {
            ISearch search = ( ISearch ) it.next();
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search, SearchUpdateEvent.EventDetail.SEARCH_PERFORMED ), this );
        }
    }


    protected String getErrorMessage()
    {
        return entriesToDelete.length == 1 ? BrowserCoreMessages.jobs__delete_entries_error_1
            : BrowserCoreMessages.jobs__delete_entries_error_n;
    }

    static void deleteEntry( IBrowserConnection browserConnection, IEntry entry, StudioProgressMonitor monitor )
    {
        // dn
        String dn = entry.getDn().toString();

        // controls
        Control[] controls = null;
        if ( entry.isReferral() )
        {
            controls = new Control[]
                { new ManageReferralControl() };
        }

        browserConnection.getConnection().getJNDIConnectionWrapper()
            .deleteEntry( dn, controls, monitor );
    }
}
