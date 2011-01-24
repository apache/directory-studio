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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.apache.directory.shared.ldap.name.Dn;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.connection.core.StudioPagedResultsControl;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.io.jndi.StudioSearchResult;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BaseDNEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.ContinuedSearchResultEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Entry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.SearchContinuation;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldapbrowser.core.utils.JNDIUtils;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;


/**
 * Runnable to perform search operations. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchRunnable implements StudioConnectionBulkRunnableWithProgress
{
    /** The searches. */
    protected ISearch[] searches;

    /** The searches to perform. */
    protected ISearch[] searchesToPerform;


    /**
     * Creates a new instance of SearchRunnable.
     * 
     * @param searches the searches
     */
    public SearchRunnable( ISearch[] searches )
    {
        this.searches = searches;
        this.searchesToPerform = searches;
    }


    /**
     * Creates a new instance of SearchRunnable.
     * 
     * @param search the search
     * @param searchToPerform the search to perform
     */
    private SearchRunnable( ISearch search, ISearch searchToPerform )
    {
        this.searches = new ISearch[]
            { search };
        this.searchesToPerform = new ISearch[]
            { searchToPerform };
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        Connection[] connections = new Connection[searches.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = searches[i].getBrowserConnection().getConnection();
        }
        return connections;
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return BrowserCoreMessages.jobs__search_name;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.addAll( Arrays.asList( searches ) );
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return searches.length == 1 ? BrowserCoreMessages.jobs__search_error_1
            : BrowserCoreMessages.jobs__search_error_n;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", searches.length + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int pi = 0; pi < searches.length; pi++ )
        {
            ISearch search = searches[pi];
            ISearch searchToPerform = searchesToPerform[pi];

            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__search_task, new String[]
                { search.getName() } ) );
            monitor.worked( 1 );

            if ( search.getBrowserConnection() != null )
            {
                // reset search results
                search.setSearchResults( new ISearchResult[0] );
                search.getResponseControls().clear();
                search.setNextPageSearchRunnable( null );
                search.setTopPageSearchRunnable( null );
                searchToPerform.setSearchResults( new ISearchResult[0] );
                searchToPerform.setNextPageSearchRunnable( null );
                searchToPerform.setTopPageSearchRunnable( null );
                searchToPerform.getResponseControls().clear();

                do
                {
                    // perform search
                    searchAndUpdateModel( searchToPerform.getBrowserConnection(), searchToPerform, monitor );

                    if ( search != searchToPerform )
                    {
                        // merge search results
                        ISearchResult[] sr1 = search.getSearchResults();
                        ISearchResult[] sr2 = searchToPerform.getSearchResults();
                        ISearchResult[] sr = new ISearchResult[sr1.length + sr2.length];
                        System.arraycopy( sr1, 0, sr, 0, sr1.length );
                        System.arraycopy( sr2, 0, sr, sr1.length, sr2.length );
                        search.setSearchResults( sr );
                    }
                    else
                    {
                        // set search results
                        search.setSearchResults( searchToPerform.getSearchResults() );
                    }

                    // check response controls
                    ISearch clonedSearch = ( ISearch ) searchToPerform.clone();
                    clonedSearch.getResponseControls().clear();
                    StudioPagedResultsControl sprResponseControl = null;
                    StudioPagedResultsControl sprRequestControl = null;
                    for ( StudioControl responseControl : searchToPerform.getResponseControls() )
                    {
                        if ( responseControl instanceof StudioPagedResultsControl )
                        {
                            sprResponseControl = ( StudioPagedResultsControl ) responseControl;
                        }
                    }
                    for ( Iterator<StudioControl> it = clonedSearch.getControls().iterator(); it.hasNext(); )
                    {
                        StudioControl requestControl = it.next();
                        if ( requestControl instanceof StudioPagedResultsControl )
                        {
                            sprRequestControl = ( StudioPagedResultsControl ) requestControl;
                            it.remove();
                        }
                    }
                    searchToPerform = null;

                    // paged search
                    if ( sprResponseControl != null && sprRequestControl != null )
                    {
                        StudioPagedResultsControl nextSpsc = new StudioPagedResultsControl(
                            sprRequestControl.getSize(), sprResponseControl.getCookie(),
                            sprRequestControl.isCritical(), sprRequestControl.isScrollMode() );
                        ISearch nextPageSearch = ( ISearch ) clonedSearch.clone();
                        nextPageSearch.getResponseControls().clear();
                        nextPageSearch.getControls().add( nextSpsc );
                        if ( sprRequestControl.isScrollMode() )
                        {
                            if ( sprRequestControl.getCookie() != null )
                            {
                                // create top page search runnable, same as original search
                                ISearch topPageSearch = ( ISearch ) search.clone();
                                topPageSearch.getResponseControls().clear();
                                SearchRunnable topPageSearchRunnable = new SearchRunnable( search, topPageSearch );
                                search.setTopPageSearchRunnable( topPageSearchRunnable );
                            }
                            if ( sprResponseControl.getCookie() != null )
                            {
                                // create next page search runnable
                                SearchRunnable nextPageSearchRunnable = new SearchRunnable( search, nextPageSearch );
                                search.setNextPageSearchRunnable( nextPageSearchRunnable );
                            }
                        }
                        else
                        {
                            // transparently continue search, till count limit is reached
                            if ( sprResponseControl.getCookie() != null
                                && ( search.getCountLimit() == 0 || search.getSearchResults().length < search
                                    .getCountLimit() ) )
                            {
                                searchToPerform = nextPageSearch;
                            }
                        }
                    }
                }
                while ( searchToPerform != null );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        for ( int pi = 0; pi < searches.length; pi++ )
        {
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( searches[pi],
                SearchUpdateEvent.EventDetail.SEARCH_PERFORMED ), this );
        }
    }


    /**
     * Searches the directory and updates the browser model.
     * 
     * @param browserConnection the browser connection
     * @param search the search
     * @param monitor the progress monitor
     */
    public static void searchAndUpdateModel( IBrowserConnection browserConnection, ISearch search,
        StudioProgressMonitor monitor )
    {
        if ( browserConnection.getConnection() == null )
        {
            return;
        }

        try
        {
            if ( !monitor.isCanceled() )
            {
                // add returning attributes for children and alias detection
                SearchParameter searchParameter = getSearchParameter( search );
                ArrayList<ISearchResult> searchResultList = new ArrayList<ISearchResult>();
                ArrayList<SearchContinuation> searchContinuationList = new ArrayList<SearchContinuation>();

                StudioNamingEnumeration enumeration = null;
                // search
                try
                {
                    enumeration = search( browserConnection, searchParameter, monitor );

                    // iterate through the search result
                    while ( !monitor.isCanceled() && enumeration != null && enumeration.hasMore() )
                    {
                        StudioSearchResult sr = ( StudioSearchResult ) enumeration.next();
                        boolean isContinuedSearchResult = sr.isContinuedSearchResult();
                        LdapURL searchContinuationUrl = sr.getSearchContinuationUrl();

                        if ( searchContinuationUrl == null )
                        {
                            Dn dn = JNDIUtils.getDn( sr );
                            IEntry entry = null;

                            Connection resultConnection = sr.getConnection();
                            IBrowserConnection resultBrowserConnection = BrowserCorePlugin.getDefault()
                                .getConnectionManager().getBrowserConnection( resultConnection );
                            if ( resultBrowserConnection == null )
                            {
                                resultBrowserConnection = browserConnection;
                            }

                            // get entry from cache or create it
                            entry = resultBrowserConnection.getEntryFromCache( dn );
                            if ( entry == null )
                            {
                                entry = createAndCacheEntry( resultBrowserConnection, dn, monitor );
                            }

                            // initialize special flags
                            initFlags( entry, sr, searchParameter );

                            // fill the attributes
                            fillAttributes( entry, sr, search.getSearchParameter() );

                            if ( isContinuedSearchResult )
                            {
                                // the result is from a continued search
                                // we create a special entry that displays the URL of the entry
                                entry = new ContinuedSearchResultEntry( resultBrowserConnection, dn );
                            }

                            searchResultList
                                .add( new org.apache.directory.studio.ldapbrowser.core.model.impl.SearchResult( entry,
                                    search ) );
                        }
                        else
                        {
                            //entry = new ContinuedSearchResultEntry( resultBrowserConnection, dn );
                            SearchContinuation searchContinuation = new SearchContinuation( search,
                                searchContinuationUrl );
                            searchContinuationList.add( searchContinuation );
                        }

                        monitor
                            .reportProgress( searchResultList.size() == 1 ? BrowserCoreMessages.model__retrieved_1_entry
                                : BrowserCoreMessages.bind( BrowserCoreMessages.model__retrieved_n_entries,
                                    new String[]
                                        { Integer.toString( searchResultList.size() ) } ) );
                    }
                }
                catch ( Exception e )
                {
                    int ldapStatusCode = JNDIUtils.getLdapStatusCode( e );
                    if ( ldapStatusCode == 3 || ldapStatusCode == 4 || ldapStatusCode == 11 )
                    {
                        search.setCountLimitExceeded( true );
                    }
                    else
                    {
                        monitor.reportError( e );
                    }
                }

                // check for response controls
                try
                {
                    if ( enumeration != null )
                    {
                        Control[] jndiControls = enumeration.getResponseControls();
                        if ( jndiControls != null )
                        {
                            for ( Control jndiControl : jndiControls )
                            {
                                if ( jndiControl instanceof PagedResultsResponseControl )
                                {
                                    PagedResultsResponseControl prrc = ( PagedResultsResponseControl ) jndiControl;
                                    StudioPagedResultsControl studioControl = new StudioPagedResultsControl(
                                        prrc.getResultSize(), prrc.getCookie(), prrc.isCritical(), false );
                                    search.getResponseControls().add( studioControl );

                                    search.setCountLimitExceeded( prrc.getCookie() != null );
                                }
                                else
                                {
                                    StudioControl studioControl = new StudioControl();
                                    studioControl.setOid( jndiControl.getID() );
                                    studioControl.setCritical( jndiControl.isCritical() );
                                    studioControl.setControlValue( jndiControl.getEncodedValue() );
                                    search.getResponseControls().add( studioControl );
                                }
                            }
                        }
                    }
                }
                catch ( Exception e )
                {
                    monitor.reportError( e );
                }

                monitor.reportProgress( searchResultList.size() == 1 ? BrowserCoreMessages.model__retrieved_1_entry
                    : BrowserCoreMessages.bind( BrowserCoreMessages.model__retrieved_n_entries, new String[]
                        { Integer.toString( searchResultList.size() ) } ) );
                monitor.worked( 1 );

                search.setSearchResults( ( ISearchResult[] ) searchResultList
                    .toArray( new ISearchResult[searchResultList.size()] ) );
                search.setSearchContinuations( ( SearchContinuation[] ) searchContinuationList
                    .toArray( new SearchContinuation[searchContinuationList.size()] ) );
            }
        }
        catch ( Exception e )
        {
            if ( search != null )
            {
                search.setSearchResults( new ISearchResult[0] );
            }
            monitor.reportError( e );
        }
    }


    public static StudioNamingEnumeration search( IBrowserConnection browserConnection, SearchParameter parameter,
        StudioProgressMonitor monitor )
    {
        if ( browserConnection == null )
        {
            return null;
        }

        String searchBase = parameter.getSearchBase().getName();
        SearchControls controls = new SearchControls();
        switch ( parameter.getScope() )
        {
            case OBJECT:
                controls.setSearchScope( SearchControls.OBJECT_SCOPE );
                break;
            case ONELEVEL:
                controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
                break;
            case SUBTREE:
                controls.setSearchScope( SearchControls.SUBTREE_SCOPE );
                break;
            default:
                controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        }
        controls.setReturningAttributes( parameter.getReturningAttributes() );
        controls.setCountLimit( parameter.getCountLimit() );
        int timeLimit = parameter.getTimeLimit() * 1000;
        if ( timeLimit > 1 )
        {
            timeLimit--;
        }
        controls.setTimeLimit( timeLimit );
        String filter = parameter.getFilter();
        AliasDereferencingMethod aliasesDereferencingMethod = parameter.getAliasesDereferencingMethod();
        ReferralHandlingMethod referralsHandlingMethod = parameter.getReferralsHandlingMethod();

        Control[] jndiControls = null;
        if ( parameter.getControls() != null )
        {
            List<StudioControl> ctls = parameter.getControls();
            jndiControls = new Control[ctls.size()];
            for ( int i = 0; i < ctls.size(); i++ )
            {
                StudioControl ctl = ctls.get( i );
                jndiControls[i] = new BasicControl( ctl.getOid(), ctl.isCritical(), ctl.getControlValue() );
            }
        }

        StudioNamingEnumeration result = browserConnection
            .getConnection()
            .getConnectionWrapper()
            .search( searchBase, filter, controls, aliasesDereferencingMethod, referralsHandlingMethod, jndiControls,
                monitor, null );
        return result;
    }


    private static SearchParameter getSearchParameter( ISearch search )
    {
        SearchParameter searchParameter = ( SearchParameter ) search.getSearchParameter().clone();

        // add children detetion attributes
        if ( search.isInitHasChildrenFlag() )
        {
            if ( search.getBrowserConnection().getSchema()
                .hasAttributeTypeDescription( SchemaConstants.HAS_SUBORDINATES_AT )
                && !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
                    SchemaConstants.HAS_SUBORDINATES_AT ) )
            {
                String[] returningAttributes = new String[searchParameter.getReturningAttributes().length + 1];
                System.arraycopy( searchParameter.getReturningAttributes(), 0, returningAttributes, 0,
                    searchParameter.getReturningAttributes().length );
                returningAttributes[returningAttributes.length - 1] = SchemaConstants.HAS_SUBORDINATES_AT;
                searchParameter.setReturningAttributes( returningAttributes );
            }
            else if ( search.getBrowserConnection().getSchema()
                .hasAttributeTypeDescription( SchemaConstants.NUM_SUBORDINATES_AT )
                && !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
                    SchemaConstants.NUM_SUBORDINATES_AT ) )
            {
                String[] returningAttributes = new String[searchParameter.getReturningAttributes().length + 1];
                System.arraycopy( searchParameter.getReturningAttributes(), 0, returningAttributes, 0,
                    searchParameter.getReturningAttributes().length );
                returningAttributes[returningAttributes.length - 1] = SchemaConstants.NUM_SUBORDINATES_AT;
                searchParameter.setReturningAttributes( returningAttributes );
            }
            else if ( search.getBrowserConnection().getSchema()
                .hasAttributeTypeDescription( SchemaConstants.SUBORDINATE_COUNT_AT )
                && !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
                    SchemaConstants.SUBORDINATE_COUNT_AT ) )
            {
                String[] returningAttributes = new String[searchParameter.getReturningAttributes().length + 1];
                System.arraycopy( searchParameter.getReturningAttributes(), 0, returningAttributes, 0,
                    searchParameter.getReturningAttributes().length );
                returningAttributes[returningAttributes.length - 1] = SchemaConstants.SUBORDINATE_COUNT_AT;
                searchParameter.setReturningAttributes( returningAttributes );
            }
        }

        // always add the objectClass attribute, we need it  
        // - to detect alias and referral entries
        // - to determine the entry's icon
        // - to determine must and may attributes
        if ( !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
            SchemaConstants.OBJECT_CLASS_AT )
            && !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
                SchemaConstants.ALL_USER_ATTRIBUTES ) )
        {
            String[] returningAttributes = new String[searchParameter.getReturningAttributes().length + 1];
            System.arraycopy( searchParameter.getReturningAttributes(), 0, returningAttributes, 0,
                searchParameter.getReturningAttributes().length );
            returningAttributes[returningAttributes.length - 1] = SchemaConstants.OBJECT_CLASS_AT;
            searchParameter.setReturningAttributes( returningAttributes );
        }

        // filter controls if not supported by server
        if ( searchParameter.getControls() != null )
        {
            IBrowserConnection connection = search.getBrowserConnection();
            Set<String> supportedConrolSet = new HashSet<String>();
            if ( connection.getRootDSE() != null
                && connection.getRootDSE().getAttribute( SchemaConstants.SUPPORTED_CONTROL_AT ) != null )
            {
                IAttribute scAttribute = connection.getRootDSE().getAttribute( SchemaConstants.SUPPORTED_CONTROL_AT );
                String[] supportedControls = scAttribute.getStringValues();
                for ( int i = 0; i < supportedControls.length; i++ )
                {
                    supportedConrolSet.add( supportedControls[i].toLowerCase() );
                }
            }

            List<StudioControl> controls = searchParameter.getControls();
            for ( Iterator<StudioControl> it = controls.iterator(); it.hasNext(); )
            {
                StudioControl control = it.next();
                if ( !supportedConrolSet.contains( control.getOid().toLowerCase() ) )
                {
                    it.remove();
                }
            }
        }

        return searchParameter;
    }


    /**
     * Creates the entry and puts it into the BrowserConnection's entry cache.
     * 
     * @param browserConnection the browser connection
     * @param dn the Dn of the entry
     * @param monitor 
     * 
     * @return the created entry
     */
    private static IEntry createAndCacheEntry( IBrowserConnection browserConnection, Dn dn,
        StudioProgressMonitor monitor )
    {
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );
        IEntry entry = null;

        // build tree to parent
        LinkedList<Dn> parentDnList = new LinkedList<Dn>();
        Dn parentDn = dn;
        while ( parentDn != null && browserConnection.getEntryFromCache(parentDn) == null )
        {
            parentDnList.addFirst(parentDn);
            parentDn = DnUtils.getParent(parentDn);
        }

        for ( Dn aDn : parentDnList )
        {
            parentDn = DnUtils.getParent(aDn);
            if ( parentDn == null )
            {
                // only the root DSE has a null parent
                entry = browserConnection.getRootDSE();
            }
            else if ( !parentDn.isEmpty() && browserConnection.getEntryFromCache(parentDn) != null )
            {
                // a normal entry has a parent but the parent isn't the rootDSE
                IEntry parentEntry = browserConnection.getEntryFromCache(parentDn);
                entry = new Entry( parentEntry, aDn.getRdn() );
                entry.setDirectoryEntry( true );
                parentEntry.addChild( entry );
                parentEntry.setChildrenInitialized( true );
                parentEntry.setHasMoreChildren( true );
                parentEntry.setHasChildrenHint( true );
                browserConnection.cacheEntry( entry );
            }
            else
            {
                // we have a base Dn, check if the entry really exists in LDAP
                // this is to avoid that a node "dc=com" is created for "dc=example,dc=com" context entry
                SearchParameter searchParameter = new SearchParameter();
                searchParameter.setSearchBase(aDn);
                searchParameter.setFilter( null );
                searchParameter.setReturningAttributes( ISearch.NO_ATTRIBUTES );
                searchParameter.setScope( SearchScope.OBJECT );
                searchParameter.setCountLimit( 1 );
                searchParameter.setTimeLimit( 0 );
                searchParameter.setAliasesDereferencingMethod( browserConnection.getAliasesDereferencingMethod() );
                searchParameter.setReferralsHandlingMethod( browserConnection.getReferralsHandlingMethod() );
                searchParameter.setInitHasChildrenFlag( true );
                dummyMonitor.reset();
                StudioNamingEnumeration enumeration = search( browserConnection, searchParameter, dummyMonitor );
                try
                {
                    if ( enumeration != null && enumeration.hasMore() )
                    {
                        // create base Dn entry
                        entry = new BaseDNEntry(aDn, browserConnection );
                        browserConnection.getRootDSE().addChild( entry );
                        browserConnection.cacheEntry( entry );
                        enumeration.close();
                    }
                }
                catch ( NamingException e )
                {
                }
            }
        }

        return entry;
    }


    /**
     * Initializes the following flags of the entry:
     * <ul>
     * <li>hasChildren</li>
     * <li>isAlias</li>
     * <li>isReferral</li>
     * <li>isSubentry</li>
     * </ul>
     * 
     * @param entry the entry
     * @param sr the the JNDI search result
     * @param searchParameter the search parameters
     */
    private static void initFlags( IEntry entry, SearchResult sr, SearchParameter searchParameter )
        throws NamingException
    {
        NamingEnumeration<? extends Attribute> attributeEnumeration = sr.getAttributes().getAll();
        while ( attributeEnumeration.hasMore() )
        {
            Attribute attribute = attributeEnumeration.next();
            String attributeDescription = attribute.getID();
            NamingEnumeration<?> valueEnumeration = attribute.getAll();
            if ( SchemaConstants.OBJECT_CLASS_AT.equalsIgnoreCase( attributeDescription ) )
            {
                if ( entry.getAttribute( attributeDescription ) != null )
                {
                    entry.deleteAttribute( entry.getAttribute( attributeDescription ) );
                }
                entry.addAttribute( new org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute( entry,
                    attributeDescription ) );
            }
            while ( valueEnumeration.hasMore() )
            {
                Object o = valueEnumeration.next();
                if ( o instanceof String )
                {
                    String value = ( String ) o;

                    if ( searchParameter.isInitHasChildrenFlag() )
                    {
                        // hasChildren flag
                        if ( SchemaConstants.HAS_SUBORDINATES_AT.equalsIgnoreCase( attributeDescription ) )
                        {
                            if ( "FALSE".equalsIgnoreCase( value ) ) { //$NON-NLS-1$
                                entry.setHasChildrenHint( false );
                            }
                        }
                        if ( SchemaConstants.NUM_SUBORDINATES_AT.equalsIgnoreCase( attributeDescription ) )
                        {
                            if ( "0".equalsIgnoreCase( value ) ) { //$NON-NLS-1$
                                entry.setHasChildrenHint( false );
                            }
                        }
                        if ( SchemaConstants.SUBORDINATE_COUNT_AT.equalsIgnoreCase( attributeDescription ) )
                        {
                            if ( "0".equalsIgnoreCase( value ) ) { //$NON-NLS-1$
                                entry.setHasChildrenHint( false );
                            }
                        }
                    }

                    if ( SchemaConstants.OBJECT_CLASS_AT.equalsIgnoreCase( attributeDescription ) )
                    {
                        if ( SchemaConstants.ALIAS_OC.equalsIgnoreCase( value ) )
                        {
                            entry.setAlias( true );
                            entry.setHasChildrenHint( false );
                        }

                        if ( SchemaConstants.REFERRAL_OC.equalsIgnoreCase( value ) )
                        {
                            entry.setReferral( true );
                            entry.setHasChildrenHint( false );
                        }

                        IAttribute ocAttribute = entry.getAttribute( attributeDescription );
                        Value ocValue = new Value( ocAttribute, value );
                        ocAttribute.addValue( ocValue );
                    }
                }
            }
        }

        if ( ( searchParameter.getControls() != null && searchParameter.getControls().contains(
            StudioControl.SUBENTRIES_CONTROL ) )
            || ISearch.FILTER_SUBENTRY.equalsIgnoreCase( searchParameter.getFilter() ) )
        {
            entry.setSubentry( true );
            entry.setHasChildrenHint( false );
        }
    }


    /**
     * Fills the attributes and values of the search result into the entry.
     * Clears existing attributes and values in the entry.
     * 
     * @param entry the entry
     * @param sr the JNDI search result
     * @param searchParameter the search parameters
     */
    private static void fillAttributes( IEntry entry, SearchResult sr, SearchParameter searchParameter )
        throws NamingException
    {
        if ( searchParameter.getReturningAttributes() == null || searchParameter.getReturningAttributes().length > 0 )
        {
            // clear old attributes defined as returning attributes or clear all
            if ( searchParameter.getReturningAttributes() != null )
            {
                String[] ras = searchParameter.getReturningAttributes();

                // special case *
                if ( Arrays.asList( ras ).contains( SchemaConstants.ALL_USER_ATTRIBUTES ) )
                {
                    // clear all user attributes
                    IAttribute[] oldAttributes = entry.getAttributes();
                    for ( int i = 0; oldAttributes != null && i < oldAttributes.length; i++ )
                    {
                        if ( !oldAttributes[i].isOperationalAttribute() )
                        {
                            entry.deleteAttribute( oldAttributes[i] );
                        }
                    }
                }

                // special case +
                if ( Arrays.asList( ras ).contains( SchemaConstants.ALL_OPERATIONAL_ATTRIBUTES ) )
                {
                    // clear all operational attributes
                    IAttribute[] oldAttributes = entry.getAttributes();
                    for ( int i = 0; oldAttributes != null && i < oldAttributes.length; i++ )
                    {
                        if ( oldAttributes[i].isOperationalAttribute() )
                        {
                            entry.deleteAttribute( oldAttributes[i] );
                        }
                    }
                }

                for ( int r = 0; r < ras.length; r++ )
                {
                    // clear attributes requested from server, also include sub-types
                    AttributeHierarchy ah = entry.getAttributeWithSubtypes( ras[r] );
                    if ( ah != null )
                    {
                        for ( Iterator<IAttribute> it = ah.iterator(); it.hasNext(); )
                        {
                            IAttribute attribute = it.next();
                            entry.deleteAttribute( attribute );
                        }
                    }
                }
            }
            else
            {
                // clear all
                IAttribute[] oldAttributes = entry.getAttributes();
                for ( int i = 0; oldAttributes != null && i < oldAttributes.length; i++ )
                {
                    entry.deleteAttribute( oldAttributes[i] );
                }
            }

            // additional clear old attributes if the record contains the attribute
            NamingEnumeration<? extends Attribute> attributeEnumeration = sr.getAttributes().getAll();
            while ( attributeEnumeration.hasMore() )
            {
                Attribute attribute = attributeEnumeration.next();
                String attributeDescription = attribute.getID();
                IAttribute oldAttribute = entry.getAttribute( attributeDescription );
                if ( oldAttribute != null )
                {
                    entry.deleteAttribute( oldAttribute );
                }
            }

            // set new attributes and values
            attributeEnumeration = sr.getAttributes().getAll();
            while ( attributeEnumeration.hasMore() )
            {
                Attribute attribute = attributeEnumeration.next();
                String attributeDescription = attribute.getID();

                if ( attribute.getAll().hasMore() )
                {
                    IAttribute studioAttribute = null;
                    if ( entry.getAttribute( attributeDescription ) == null )
                    {
                        studioAttribute = new org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute( entry,
                            attributeDescription );
                        entry.addAttribute( studioAttribute );
                    }
                    else
                    {
                        studioAttribute = entry.getAttribute( attributeDescription );
                    }

                    NamingEnumeration<?> valueEnumeration = attribute.getAll();
                    while ( valueEnumeration.hasMore() )
                    {
                        Object value = valueEnumeration.next();
                        studioAttribute.addValue( new Value( studioAttribute, value ) );
                    }
                }
            }
        }
    }
}
