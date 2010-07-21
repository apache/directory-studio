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
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.connection.core.StudioPagedResultsControl;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.jobs.StudioBulkRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.ContinuedSearchResultEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.core.model.impl.SearchContinuation;


/**
 * Runnable to initialize the child entries of an entry
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitializeChildrenRunnable implements StudioBulkRunnableWithProgress
{

    /** The entries. */
    private IEntry[] entries;

    /** The purge all caches flag. */
    private boolean purgeAllCaches;

    /** The paged search control, only used internally. */
    private StudioControl pagedSearchControl;


    /**
     * Creates a new instance of InitializeChildrenRunnable.
     * 
     * @param entries the entries
     * @param purgeAllCaches true to purge all caches
     */
    public InitializeChildrenRunnable( boolean purgeAllCaches, IEntry... entries )
    {
        this.entries = entries;
        this.purgeAllCaches = purgeAllCaches;
    }


    /**
     * Creates a new instance of InitializeChildrenRunnable.
     * 
     * @param entry the entry
     * @param pagedSearchControl the paged search control
     */
    private InitializeChildrenRunnable( IEntry entry, StudioControl pagedSearchControl )
    {
        this.entries = new IEntry[]
            { entry };
        this.pagedSearchControl = pagedSearchControl;
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        Connection[] connections = new Connection[entries.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = entries[i].getBrowserConnection().getConnection();
        }
        return connections;
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return BrowserCoreMessages.jobs__init_entries_title_subonly;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.addAll( Arrays.asList( entries ) );
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return entries.length == 1 ? BrowserCoreMessages.jobs__init_entries_error_1
            : BrowserCoreMessages.jobs__init_entries_error_n;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", entries.length + 2 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( IEntry entry : entries )
        {
            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_task, new String[]
                { entry.getDn().getUpName() } ) );
            monitor.worked( 1 );

            IBrowserConnection browserConnection = entry.getBrowserConnection();
            if ( browserConnection != null )
            {
                if ( entry instanceof IRootDSE )
                {
                    // special handling for Root DSE
                    InitializeRootDSERunnable.loadRootDSE( browserConnection, monitor );
                    continue;
                }

                if ( pagedSearchControl == null && browserConnection.isPagedSearch() )
                {
                    pagedSearchControl = new StudioPagedResultsControl( browserConnection.getPagedSearchSize(), null,
                        false, browserConnection.isPagedSearchScrollMode() );
                }

                initializeChildren( entry, monitor, pagedSearchControl );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        for ( IEntry entry : entries )
        {
            if ( entry.getBrowserConnection() != null && entry.isChildrenInitialized() )
            {
                EventRegistry.fireEntryUpdated( new ChildrenInitializedEvent( entry ), this );
            }
        }
    }


    /**
     * Initializes the child entries.
     * 
     * @param parent the parent
     * @param monitor the progress monitor
     * @param pagedSearchControl the paged search control
     */
    private void initializeChildren( IEntry parent, StudioProgressMonitor monitor, StudioControl pagedSearchControl )
    {
        monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_sub,
            new String[]
                { parent.getDn().getUpName() } ) );

        // clear old children
        clearCaches( parent, purgeAllCaches );

        // create search
        ISearch search = createSearch( parent, pagedSearchControl, false, false, false );

        // search
        executeSearch( parent, search, monitor );
        ISearchResult[] srs = search.getSearchResults();
        SearchContinuation[] scs = search.getSearchContinuations();

        // fill children in search result
        if ( ( srs != null && srs.length > 0 ) || ( scs != null && scs.length > 0 ) )
        {
            // clearing old children before filling new children is
            // necessary to handle aliases and referrals.
            clearCaches( parent, false );

            do
            {
                if ( srs != null )
                {
                    for ( ISearchResult searchResult : srs )
                    {
                        parent.addChild( searchResult.getEntry() );
                    }
                    srs = null;
                }

                if ( scs != null )
                {
                    for ( SearchContinuation searchContinuation : scs )
                    {
                        ContinuedSearchResultEntry entry = new ContinuedSearchResultEntry( parent
                            .getBrowserConnection(), searchContinuation.getUrl().getDn() );
                        entry.setUnresolved( searchContinuation.getUrl() );
                        parent.addChild( entry );
                    }
                    scs = null;
                }

                StudioPagedResultsControl sprRequestControl = null;
                StudioPagedResultsControl sprResponseControl = null;
                for ( StudioControl responseControl : search.getResponseControls() )
                {
                    if ( responseControl instanceof StudioPagedResultsControl )
                    {
                        sprResponseControl = ( StudioPagedResultsControl ) responseControl;
                    }
                }
                for ( StudioControl requestControl : search.getControls() )
                {
                    if ( requestControl instanceof StudioPagedResultsControl )
                    {
                        sprRequestControl = ( StudioPagedResultsControl ) requestControl;
                    }
                }

                if ( sprRequestControl != null && sprResponseControl != null )
                {
                    if ( sprRequestControl.isScrollMode() )
                    {
                        if ( sprRequestControl.getCookie() != null )
                        {
                            // create top page search runnable, same as original search
                            InitializeChildrenRunnable topPageChildrenRunnable = new InitializeChildrenRunnable(
                                parent, null );
                            parent.setTopPageChildrenRunnable( topPageChildrenRunnable );
                        }

                        if ( sprResponseControl.getCookie() != null )
                        {
                            StudioPagedResultsControl newSprc = new StudioPagedResultsControl( sprRequestControl
                                .getSize(), sprResponseControl.getCookie(), sprRequestControl.isCritical(),
                                sprRequestControl.isScrollMode() );
                            InitializeChildrenRunnable nextPageChildrenRunnable = new InitializeChildrenRunnable(
                                parent, newSprc );
                            parent.setNextPageChildrenRunnable( nextPageChildrenRunnable );
                        }
                    }
                    else
                    {
                        // transparently continue search, till count limit is reached
                        if ( sprResponseControl.getCookie() != null
                            && ( search.getCountLimit() == 0 || search.getSearchResults().length < search
                                .getCountLimit() ) )
                        {

                            search.setSearchResults( new ISearchResult[0] );
                            search.getResponseControls().clear();
                            sprRequestControl.setCookie( sprResponseControl.getCookie() );

                            executeSearch( parent, search, monitor );
                            srs = search.getSearchResults();
                            scs = search.getSearchContinuations();
                        }
                    }
                }
            }
            while ( srs != null && srs.length > 0 );
        }
        else
        {
            parent.setHasChildrenHint( false );
        }

        // get sub-entries
        ISearch subSearch = createSearch( parent, null, true, false, false );
        if ( parent.getBrowserConnection().isFetchSubentries() || parent.isFetchSubentries() )
        {
            executeSubSearch( parent, subSearch, monitor );
        }

        // get aliases and referrals
        ISearch aliasOrReferralSearch = createSearch( parent, null, false, parent.isFetchAliases(), parent
            .isFetchReferrals() );
        if ( parent.isFetchAliases() || parent.isFetchReferrals() )
        {
            executeSubSearch( parent, aliasOrReferralSearch, monitor );
        }

        // check exceeded limits / canceled
        parent.setHasMoreChildren( search.isCountLimitExceeded() || subSearch.isCountLimitExceeded()
            || aliasOrReferralSearch.isCountLimitExceeded() || monitor.isCanceled() );

        // set initialized state
        parent.setChildrenInitialized( true );
    }


    private void executeSubSearch( IEntry parent, ISearch subSearch, StudioProgressMonitor monitor )
    {
        executeSearch( parent, subSearch, monitor );
        ISearchResult[] subSrs = subSearch.getSearchResults();
        SearchContinuation[] subScs = subSearch.getSearchContinuations();

        // fill children in search result
        if ( subSrs != null && subSrs.length > 0 )
        {
            for ( ISearchResult searchResult : subSrs )
            {
                parent.addChild( searchResult.getEntry() );
            }
            for ( SearchContinuation searchContinuation : subScs )
            {
                ContinuedSearchResultEntry entry = new ContinuedSearchResultEntry( parent.getBrowserConnection(),
                    searchContinuation.getUrl().getDn() );
                entry.setUnresolved( searchContinuation.getUrl() );
                parent.addChild( entry );
            }
        }
    }


    private static void executeSearch( IEntry parent, ISearch search, StudioProgressMonitor monitor )
    {
        SearchRunnable.searchAndUpdateModel( parent.getBrowserConnection(), search, monitor );
        ISearchResult[] srs = search.getSearchResults();
        monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_subcount,
            new String[]
                { srs == null ? Integer.toString( 0 ) : Integer.toString( srs.length ), parent.getDn().getUpName() } ) );
    }


    private static ISearch createSearch( IEntry parent, StudioControl pagedSearchControl, boolean isSubentriesSearch,
        boolean isAliasSearch, boolean isReferralsSearch )
    {
        // scope
        SearchScope scope = SearchScope.ONELEVEL;

        // filter
        String filter = parent.getChildrenFilter();
        if ( isSubentriesSearch )
        {
            filter = ISearch.FILTER_SUBENTRY;
        }
        else if ( isAliasSearch && isReferralsSearch )
        {
            filter = ISearch.FILTER_ALIAS_OR_REFERRAL;
        }
        else if ( isAliasSearch )
        {
            filter = ISearch.FILTER_ALIAS;
        }
        else if ( isReferralsSearch )
        {
            filter = ISearch.FILTER_REFERRAL;
        }

        // alias handling
        AliasDereferencingMethod aliasesDereferencingMethod = parent.getBrowserConnection()
            .getAliasesDereferencingMethod();
        if ( parent.isAlias() || isAliasSearch )
        {
            aliasesDereferencingMethod = AliasDereferencingMethod.NEVER;
        }

        // referral handling
        ReferralHandlingMethod referralsHandlingMethod = parent.getBrowserConnection().getReferralsHandlingMethod();

        // create search
        ISearch search = new Search( null, parent.getBrowserConnection(), parent.getDn(), filter,
            ISearch.NO_ATTRIBUTES, scope, parent.getBrowserConnection().getCountLimit(), parent.getBrowserConnection()
                .getTimeLimit(), aliasesDereferencingMethod, referralsHandlingMethod, BrowserCorePlugin.getDefault()
                .getPluginPreferences().getBoolean( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN ), null );

        // controls
        if ( parent.isReferral() || isReferralsSearch || parent.getBrowserConnection().isManageDsaIT() )
        {
            search.getSearchParameter().getControls().add( StudioControl.MANAGEDSAIT_CONTROL );
        }
        if ( isSubentriesSearch )
        {
            search.getSearchParameter().getControls().add( StudioControl.SUBENTRIES_CONTROL );
        }
        if ( pagedSearchControl != null )
        {
            search.getSearchParameter().getControls().add( pagedSearchControl );
        }

        return search;
    }


    static void clearCaches( IEntry entry, boolean purgeAllCaches )
    {
        // clear the parent-child relationship, recursively
        IEntry[] children = entry.getChildren();
        if ( children != null )
        {
            for ( IEntry child : children )
            {
                if ( child != null )
                {
                    entry.deleteChild( child );
                    clearCaches( child, purgeAllCaches );
                }
            }
        }
        entry.setChildrenInitialized( false );

        // reset paging runnables
        entry.setTopPageChildrenRunnable( null );
        entry.setNextPageChildrenRunnable( null );

        // reset attributes and additional flags
        if ( purgeAllCaches )
        {
            entry.setAttributesInitialized( false );
            entry.setHasChildrenHint( true );
            entry.setHasMoreChildren( false );
        }
    }

}
