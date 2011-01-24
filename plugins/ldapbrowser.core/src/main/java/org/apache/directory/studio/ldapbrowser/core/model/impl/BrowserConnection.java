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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.ldapbrowser.core.BookmarkManager;
import org.apache.directory.studio.ldapbrowser.core.SearchManager;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.search.ui.ISearchPageScoreComputer;


/**
 * The default implementation of {@link IBrowserConnection}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserConnection implements IBrowserConnection, Serializable
{

    private static final long serialVersionUID = 2987596234755856270L;

    /** The connection. */
    private Connection connection;

    /** The root DSE. */
    private IRootDSE rootDSE;

    /** The schema. */
    private Schema schema;

    /** The search manager. */
    private SearchManager searchManager;

    /** The bookmark manager. */
    private BookmarkManager bookmarkManager;

    /** The dn to entry cache. */
    private volatile Map<String, IEntry> dnToEntryCache;

    /** The entry to children filter map. */
    private volatile Map<IEntry, String> entryToChildrenFilterMap;

    /** The entry to attribute info map. */
    private volatile Map<IEntry, AttributeInfo> entryToAttributeInfoMap;

    /** The entry to children info map. */
    private volatile Map<IEntry, ChildrenInfo> entryToChildrenInfoMap;


    /**
     * Creates a new instance of BrowserConnection.
     *
     * @param connection the connection
     */
    public BrowserConnection( Connection connection )
    {
        this.connection = connection;

        if ( connection.getConnectionParameter().getExtendedProperty( CONNECTION_PARAMETER_COUNT_LIMIT ) == null )
        {
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_COUNT_LIMIT, 1000 );
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_TIME_LIMIT, 0 );
            connection.getConnectionParameter().setExtendedIntProperty(
                CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD, AliasDereferencingMethod.ALWAYS.getOrdinal() );
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
                ReferralHandlingMethod.FOLLOW_MANUALLY.getOrdinal() );
            connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_BASE_DNS, true );
            connection.getConnectionParameter().setExtendedProperty( CONNECTION_PARAMETER_BASE_DN, "" );
            connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_SUBENTRIES, false );
            connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_PAGED_SEARCH, false );
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_PAGED_SEARCH_SIZE, 100 );
            connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE,
                true );
        }
        if ( connection.getConnectionParameter().getExtendedProperty( CONNECTION_PARAMETER_MODIFY_MODE ) == null )
        {
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_MODIFY_MODE,
                ModifyMode.DEFAULT.getOrdinal() );
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR,
                ModifyMode.DEFAULT.getOrdinal() );
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_MODIFY_ORDER,
                ModifyOrder.DELETE_FIRST.getOrdinal() );
        }

        this.searchManager = new SearchManager( this );
        this.bookmarkManager = new BookmarkManager( this );

        this.entryToChildrenFilterMap = new HashMap<IEntry, String>();
        this.dnToEntryCache = new HashMap<String, IEntry>();
        this.entryToAttributeInfoMap = new HashMap<IEntry, AttributeInfo>();
        this.entryToChildrenInfoMap = new HashMap<IEntry, ChildrenInfo>();

        this.schema = Schema.DEFAULT_SCHEMA;
        this.rootDSE = new RootDSE( this );
        cacheEntry( this.rootDSE );
    }


    /**
     * {@inheritDoc}
     */
    public LdapURL getUrl()
    {
        return Utils.getLdapURL( this );
    }


    /**
     * {@inheritDoc}
     */
    public void clearCaches()
    {
        for ( ISearch search : getSearchManager().getSearches() )
        {
            search.setSearchResults( null );
        }

        dnToEntryCache.clear();
        entryToAttributeInfoMap.clear();
        entryToChildrenInfoMap.clear();
        entryToChildrenFilterMap.clear();
        searchManager.setQuickSearch( null );

        rootDSE = new RootDSE( this );
        cacheEntry( rootDSE );
    }


    /**
     * {@inheritDoc}
     */
    public IEntry getEntryFromCache( Dn dn )
    {
        if ( dn == null )
        {
            return null;
        }

        String oidDn = Utils.getNormalizedOidString( dn, getSchema() );
        if ( dnToEntryCache != null && dnToEntryCache.containsKey( oidDn ) )
        {
            return dnToEntryCache.get( oidDn );
        }
        if ( getRootDSE().getDn().equals( dn ) )
        {
            return getRootDSE();
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isFetchBaseDNs()
    {
        return connection.getConnectionParameter().getExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_BASE_DNS );
    }


    /**
     * {@inheritDoc}
     */
    public void setFetchBaseDNs( boolean fetchBaseDNs )
    {
        connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_BASE_DNS, fetchBaseDNs );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getBaseDN()
    {
        try
        {
            return new Dn( connection.getConnectionParameter().getExtendedProperty( CONNECTION_PARAMETER_BASE_DN ) );
        }
        catch ( LdapInvalidDnException e )
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setBaseDN( Dn baseDn)
    {
        connection.getConnectionParameter().setExtendedProperty( CONNECTION_PARAMETER_BASE_DN, baseDn.toString() );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public int getCountLimit()
    {
        return connection.getConnectionParameter().getExtendedIntProperty( CONNECTION_PARAMETER_COUNT_LIMIT );
    }


    /**
     * {@inheritDoc}
     */
    public void setCountLimit( int countLimit )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_COUNT_LIMIT, countLimit );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public AliasDereferencingMethod getAliasesDereferencingMethod()
    {
        int ordinal = connection.getConnectionParameter().getExtendedIntProperty(
            CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD );
        return AliasDereferencingMethod.getByOrdinal( ordinal );
    }


    /**
     * {@inheritDoc}
     */
    public void setAliasesDereferencingMethod( AliasDereferencingMethod aliasesDereferencingMethod )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            aliasesDereferencingMethod.getOrdinal() );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public ReferralHandlingMethod getReferralsHandlingMethod()
    {
        int ordinal = connection.getConnectionParameter().getExtendedIntProperty(
            CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        return ReferralHandlingMethod.getByOrdinal( ordinal );
    }


    /**
     * {@inheritDoc}
     */
    public void setReferralsHandlingMethod( ReferralHandlingMethod referralsHandlingMethod )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            referralsHandlingMethod.getOrdinal() );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public int getTimeLimit()
    {
        return connection.getConnectionParameter().getExtendedIntProperty( CONNECTION_PARAMETER_TIME_LIMIT );
    }


    /**
     * {@inheritDoc}
     */
    public void setTimeLimit( int timeLimit )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_TIME_LIMIT, timeLimit );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isManageDsaIT()
    {
        return connection.getConnectionParameter().getExtendedBoolProperty( CONNECTION_PARAMETER_MANAGE_DSA_IT );
    }


    /**
     * {@inheritDoc}
     */
    public void setManageDsaIT( boolean manageDsaIT )
    {
        connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_MANAGE_DSA_IT, manageDsaIT );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isFetchSubentries()
    {
        return connection.getConnectionParameter().getExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_SUBENTRIES );
    }


    /**
     * {@inheritDoc}
     */
    public void setFetchSubentries( boolean fetchSubentries )
    {
        connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_SUBENTRIES,
            fetchSubentries );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isFetchOperationalAttributes()
    {
        return connection.getConnectionParameter().getExtendedBoolProperty(
            CONNECTION_PARAMETER_FETCH_OPERATIONAL_ATTRIBUTES );
    }


    /**
     * {@inheritDoc}
     */
    public void setFetchOperationalAttributes( boolean fetchOperationalAttribures )
    {
        connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_OPERATIONAL_ATTRIBUTES,
            fetchOperationalAttribures );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isPagedSearch()
    {
        return connection.getConnectionParameter().getExtendedBoolProperty( CONNECTION_PARAMETER_PAGED_SEARCH );
    }


    /**
     * {@inheritDoc}
     */
    public void setPagedSearch( boolean pagedSearch )
    {
        connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_PAGED_SEARCH, pagedSearch );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public int getPagedSearchSize()
    {
        return connection.getConnectionParameter().getExtendedIntProperty( CONNECTION_PARAMETER_PAGED_SEARCH_SIZE );
    }


    /**
     * {@inheritDoc}
     */
    public void setPagedSearchSize( int pagedSearchSize )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_PAGED_SEARCH_SIZE,
            pagedSearchSize );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isPagedSearchScrollMode()
    {
        return connection.getConnectionParameter().getExtendedBoolProperty(
            CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE );
    }


    /**
     * {@inheritDoc}
     */
    public void setPagedSearchScrollMode( boolean pagedSearchScrollMode )
    {
        connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE,
            pagedSearchScrollMode );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /** 
     * {@inheritDoc}
     */
    public ModifyMode getModifyMode()
    {
        int ordinal = connection.getConnectionParameter().getExtendedIntProperty( CONNECTION_PARAMETER_MODIFY_MODE );
        return ModifyMode.getByOrdinal( ordinal );
    }


    /** 
     * {@inheritDoc}
     */
    public void setModifyMode( ModifyMode mode )
    {
        connection.getConnectionParameter()
            .setExtendedIntProperty( CONNECTION_PARAMETER_MODIFY_MODE, mode.getOrdinal() );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /** 
     * {@inheritDoc}
     */
    public ModifyMode getModifyModeNoEMR()
    {
        int ordinal = connection.getConnectionParameter().getExtendedIntProperty(
            CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR );
        return ModifyMode.getByOrdinal( ordinal );
    }


    /** 
     * {@inheritDoc}
     */
    public void setModifyModeNoEMR( ModifyMode mode )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR,
            mode.getOrdinal() );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /** 
     * {@inheritDoc}
     */
    public ModifyOrder getModifyAddDeleteOrder()
    {
        int ordinal = connection.getConnectionParameter().getExtendedIntProperty( CONNECTION_PARAMETER_MODIFY_ORDER );
        return ModifyOrder.getByOrdinal( ordinal );
    }


    /** 
     * {@inheritDoc}
     */
    public void setModifyAddDeleteOrder( ModifyOrder mode )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_MODIFY_ORDER,
            mode.getOrdinal() );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * {@inheritDoc}
     */
    public final IRootDSE getRootDSE()
    {
        return rootDSE;
    }


    /**
     * {@inheritDoc}
     */
    public Schema getSchema()
    {
        return schema;
    }


    /**
     * {@inheritDoc}
     */
    public void setSchema( Schema schema )
    {
        this.schema = schema;
    }


    /**
     * This implementation returns the connection name
     */
    public String toString()
    {
        return getConnection() != null ? getConnection().getName() : "null";
    }


    /**
     * {@inheritDoc}
     */
    public SearchManager getSearchManager()
    {
        return searchManager;
    }


    /**
     * {@inheritDoc}
     */
    public BookmarkManager getBookmarkManager()
    {
        return bookmarkManager;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter )
    {
        Class<?> clazz = ( Class<?> ) adapter;
        if ( clazz.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( clazz.isAssignableFrom( IBrowserConnection.class ) )
        {
            return this;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public synchronized void cacheEntry( IEntry entry )
    {
        dnToEntryCache.put( Utils.getNormalizedOidString( entry.getDn(), getSchema() ), entry );
    }


    /**
     * Removes the entry from the cache.
     * 
     * @param entry the entry to remove from cache
     */
    protected synchronized void uncacheEntry( IEntry entry )
    {
        dnToEntryCache.remove( Utils.getNormalizedOidString( entry.getDn(), getSchema() ) );
    }


    /**
     * {@inheritDoc}
     */
    public synchronized void uncacheEntryRecursive( IEntry entry )
    {
        IEntry[] children = entry.getChildren();
        if ( entry.getChildren() != null )
        {
            for ( int i = 0; i < children.length; i++ )
            {
                uncacheEntryRecursive( children[i] );
            }
        }
        uncacheEntry( entry );
    }


    /**
     * Removes the entry from the cache.
     * 
     * @param dn the Dn of the entry to remove from cache
     */
    protected synchronized void uncacheEntry( Dn dn )
    {
        dnToEntryCache.remove( Utils.getNormalizedOidString( dn, getSchema() ) );
    }


    /**
     * Gets the children filter of the entry.
     * 
     * @param entry the entry
     * 
     * @return the children filter of the entry, or null if no children filter is set
     */
    protected String getChildrenFilter( IEntry entry )
    {
        return entryToChildrenFilterMap == null ? null : entryToChildrenFilterMap.get( entry );
    }


    /**
     * Sets the children filter.
     * 
     * @param entry the entry
     * @param childrenFilter the children filter, null to remove the children filter
     */
    protected void setChildrenFilter( IEntry entry, String childrenFilter )
    {
        if ( childrenFilter == null || "".equals( childrenFilter ) ) //$NON-NLS-1$
        {
            entryToChildrenFilterMap.remove( entry );
        }
        else
        {
            entryToChildrenFilterMap.put( entry, childrenFilter );
        }
    }


    /**
     * Gets the attribute info.
     * 
     * @param entry the entry
     * 
     * @return the attribute info, null if no attribute info exists
     */
    protected AttributeInfo getAttributeInfo( IEntry entry )
    {
        return entryToAttributeInfoMap == null ? null : entryToAttributeInfoMap.get( entry );
    }


    /**
     * Sets the attribute info.
     * 
     * @param entry the entry
     * @param ai the attribute info, null to remove the attribute info
     */
    protected void setAttributeInfo( IEntry entry, AttributeInfo ai )
    {
        if ( ai == null )
        {
            entryToAttributeInfoMap.remove( entry );
        }
        else
        {
            entryToAttributeInfoMap.put( entry, ai );
        }
    }


    /**
     * Gets the children info.
     * 
     * @param entry the entry
     * 
     * @return the children info, null if no children info exists
     */
    protected ChildrenInfo getChildrenInfo( IEntry entry )
    {
        return entryToChildrenInfoMap == null ? null : entryToChildrenInfoMap.get( entry );
    }


    /**
     * Sets the children info.
     * 
     * @param entry the entry
     * @param ci the children info, null to remove the children info
     */
    protected void setChildrenInfo( IEntry entry, ChildrenInfo ci )
    {
        if ( ci == null )
        {
            entryToChildrenInfoMap.remove( entry );
        }
        else
        {
            entryToChildrenInfoMap.put( entry, ci );
        }
    }


    /**
     * {@inheritDoc}
     */
    public Connection getConnection()
    {
        return connection;
    }

}
