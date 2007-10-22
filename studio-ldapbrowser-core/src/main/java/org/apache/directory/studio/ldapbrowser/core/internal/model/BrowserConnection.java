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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.BookmarkManager;
import org.apache.directory.studio.ldapbrowser.core.SearchManager;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.NameException;
import org.apache.directory.studio.ldapbrowser.core.model.URL;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.eclipse.search.ui.ISearchPageScoreComputer;


public class BrowserConnection implements ConnectionUpdateListener, IBrowserConnection, Serializable
{

    private static final long serialVersionUID = 2987596234755856270L;

    private org.apache.directory.studio.connection.core.Connection connection;
    
//    private BrowserConnectionParameter browserConnectionParameter;

    private IRootDSE rootDSE;

    private Schema schema;

    private SearchManager searchManager;

    private BookmarkManager bookmarkManager;

    private volatile Map<String, IEntry> dnToEntryCache;

    private volatile Map<IEntry, String> entryToChildrenFilterMap;

    private volatile Map<IEntry, AttributeInfo> entryToAttributeInfoMap;

    private volatile Map<IEntry, ChildrenInfo> entryToChildrenInfoMap;

    transient ModificationLogger modificationLogger;


    /**
     * Creates a new instance of BrowserConnection.
     *
     * @param connection the connection
     */
    public BrowserConnection( org.apache.directory.studio.connection.core.Connection connection )
    {
        this.connection = connection;
        
        if( connection.getConnectionParameter().getExtendedProperty( CONNECTION_PARAMETER_COUNT_LIMIT ) == null )
        {
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_COUNT_LIMIT, 0 );
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_TIME_LIMIT, 0 );
            connection.getConnectionParameter().setExtendedIntProperty(
                CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD, AliasDereferencingMethod.NEVER.getOrdinal() );
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
                ReferralHandlingMethod.IGNORE.getOrdinal() );
            connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_BASE_DNS, true );
            connection.getConnectionParameter().setExtendedProperty( CONNECTION_PARAMETER_BASE_DN, "" );
        }

        this.searchManager = new SearchManager( this );
        this.bookmarkManager = new BookmarkManager( this );
        this.modificationLogger = new ModificationLogger( this );

        this.entryToChildrenFilterMap = new HashMap<IEntry, String>();
        this.dnToEntryCache = new HashMap<String, IEntry>();
        this.entryToAttributeInfoMap = new HashMap<IEntry, AttributeInfo>();
        this.entryToChildrenInfoMap = new HashMap<IEntry, ChildrenInfo>();
        
        this.schema = Schema.DEFAULT_SCHEMA;
        this.rootDSE = new RootDSE( this );
        cacheEntry( this.rootDSE );

        this.connection.getJNDIConnectionWrapper().setModificationLogger( this.modificationLogger );

        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionCorePlugin.getDefault().getEventRunner() );
    }
    
    
    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#getUrl()
     */
    public URL getUrl()
    {
        return new URL( this );
    }


    /**
     * Closes the connections, clears all caches
     */
    private void clearCaches()
    {
        for ( int i = 0; i < getSearchManager().getSearchCount(); i++ )
        {
            this.getSearchManager().getSearches()[i].setSearchResults( null );
        }

        dnToEntryCache.clear();
        entryToAttributeInfoMap.clear();
        entryToChildrenInfoMap.clear();
        entryToChildrenFilterMap.clear();

        schema = Schema.DEFAULT_SCHEMA;
        rootDSE = new RootDSE( this );
        cacheEntry( rootDSE );

        System.gc();
    }





    public IEntry getEntryFromCache( DN dn )
    {

        if ( this.dnToEntryCache != null && this.dnToEntryCache.containsKey( dn.toOidString( this.schema ) ) )
        {
            return dnToEntryCache.get( dn.toOidString( this.schema ) );
        }
        if ( getRootDSE().getDn().equals( dn ) )
        {
            return getRootDSE();
        }
        return null;
    }

    
    
    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#isFetchBaseDNs()
     */
    public boolean isFetchBaseDNs()
    {
        return connection.getConnectionParameter().getExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_BASE_DNS );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#setFetchBaseDNs(boolean)
     */
    public void setFetchBaseDNs( boolean fetchBaseDNs )
    {
        connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_BASE_DNS, fetchBaseDNs );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#getBaseDN()
     */
    public DN getBaseDN()
    {
        try
        {
            return new DN( connection.getConnectionParameter().getExtendedProperty( CONNECTION_PARAMETER_BASE_DN ) );
        }
        catch ( NameException e )
        {
            return null;
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#setBaseDN(org.apache.directory.studio.ldapbrowser.core.model.DN)
     */
    public void setBaseDN( DN baseDN )
    {
        connection.getConnectionParameter().setExtendedProperty( CONNECTION_PARAMETER_BASE_DN, baseDN.toString() );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#getCountLimit()
     */
    public int getCountLimit()
    {
        return connection.getConnectionParameter().getExtendedIntProperty( CONNECTION_PARAMETER_COUNT_LIMIT );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#setCountLimit(int)
     */
    public void setCountLimit( int countLimit )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_COUNT_LIMIT, countLimit );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#getAliasesDereferencingMethod()
     */
    public AliasDereferencingMethod getAliasesDereferencingMethod()
    {
        int ordinal = connection.getConnectionParameter().getExtendedIntProperty(
            CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD );
        return AliasDereferencingMethod.getByOrdinal( ordinal );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#setAliasesDereferencingMethod(AliasDereferencingMethod)
     */
    public void setAliasesDereferencingMethod( AliasDereferencingMethod aliasesDereferencingMethod )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            aliasesDereferencingMethod.getOrdinal() );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#getReferralsHandlingMethod()
     */
    public ReferralHandlingMethod getReferralsHandlingMethod()
    {
        int ordinal = connection.getConnectionParameter().getExtendedIntProperty(
            CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        return ReferralHandlingMethod.getByOrdinal( ordinal );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#setReferralsHandlingMethod(ReferralHandlingMethod)
     */
    public void setReferralsHandlingMethod( ReferralHandlingMethod referralsHandlingMethod )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            referralsHandlingMethod.getOrdinal() );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#getTimeLimit()
     */
    public int getTimeLimit()
    {
        return connection.getConnectionParameter().getExtendedIntProperty( CONNECTION_PARAMETER_TIME_LIMIT );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#setTimeLimit(int)
     */
    public void setTimeLimit( int timeLimit )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_TIME_LIMIT, timeLimit );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#getRootDSE()
     */
    public final IRootDSE getRootDSE()
    {
        return rootDSE;
    }


    public Schema getSchema()
    {
        return schema;
    }


    public void setSchema( Schema schema )
    {
        this.schema = schema;
    }


//    public BrowserConnectionParameter getConnectionParameter()
//    {
//        return browserConnectionParameter;
//    }
//
//
//    public void setConnectionParameter( BrowserConnectionParameter connectionParameter )
//    {
//        this.browserConnectionParameter = connectionParameter;
//    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getConnection().getName();
    }


    public SearchManager getSearchManager()
    {
        return searchManager;
    }


    public BookmarkManager getBookmarkManager()
    {
        return bookmarkManager;
    }


    public ModificationLogger getModificationLogger()
    {
        return modificationLogger;
    }


    public Object getAdapter( Class adapter )
    {

        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( adapter == IBrowserConnection.class )
        {
            return this;
        }

        return null;
    }


    public void cacheEntry( IEntry entry )
    {
        this.dnToEntryCache.put( entry.getDn().toOidString( this.schema ), entry );
    }


    protected void uncacheEntry( IEntry entry )
    {
        this.dnToEntryCache.remove( entry.getDn().toOidString( this.schema ) );
    }
    

    public void uncacheEntryRecursive( IEntry entry )
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


    protected void uncacheEntry( DN dn )
    {
        this.dnToEntryCache.remove( dn.toOidString( this.schema ) );
    }


    protected String getChildrenFilter( IEntry entry )
    {
        return this.entryToChildrenFilterMap == null ? null : this.entryToChildrenFilterMap.get( entry );
    }


    protected void setChildrenFilter( IEntry entry, String childrenFilter )
    {
        if ( childrenFilter == null || "".equals( childrenFilter ) ) { //$NON-NLS-1$
            this.entryToChildrenFilterMap.remove( entry );
        }
        else
        {
            this.entryToChildrenFilterMap.put( entry, childrenFilter );
        }
    }


    protected AttributeInfo getAttributeInfo( IEntry entry )
    {
        return this.entryToAttributeInfoMap == null ? null : this.entryToAttributeInfoMap.get( entry );
    }


    protected void setAttributeInfo( IEntry entry, AttributeInfo ai )
    {
        if ( ai == null )
        {
            this.entryToAttributeInfoMap.remove( entry );
        }
        else
        {
            this.entryToAttributeInfoMap.put( entry, ai );
        }
    }


    protected ChildrenInfo getChildrenInfo( IEntry entry )
    {
        return this.entryToChildrenInfoMap == null ? null : this.entryToChildrenInfoMap.get( entry );
    }


    protected void setChildrenInfo( IEntry entry, ChildrenInfo si )
    {
        if ( si == null )
        {
            this.entryToChildrenInfoMap.remove( entry );
        }
        else
        {
            this.entryToChildrenInfoMap.put( entry, si );
        }
    }


    public org.apache.directory.studio.connection.core.Connection getConnection()
    {
        return connection;
    }
    
    public void connectionAdded( org.apache.directory.studio.connection.core.Connection connection )
    {
    }
    public void connectionRemoved( org.apache.directory.studio.connection.core.Connection connection )
    {
    }
    public void connectionUpdated( org.apache.directory.studio.connection.core.Connection connection )
    {
    }
    public void connectionOpened( org.apache.directory.studio.connection.core.Connection connection )
    {
        if(this.connection == connection)
        {
            new OpenBrowserConnectionsJob( this ).execute();
            BrowserConnectionUpdateEvent browserConnectionUpdateEvent = new BrowserConnectionUpdateEvent( this,
                BrowserConnectionUpdateEvent.Detail.BROWSER_CONNECTION_OPENED );
            EventRegistry.fireBrowserConnectionUpdated( browserConnectionUpdateEvent , this );
        }
    }
    public void connectionClosed( org.apache.directory.studio.connection.core.Connection connection )
    {
        if(this.connection == connection)
        {
            clearCaches();
            BrowserConnectionUpdateEvent browserConnectionUpdateEvent = new BrowserConnectionUpdateEvent( this,
                BrowserConnectionUpdateEvent.Detail.BROWSER_CONNECTION_CLOSED );
            EventRegistry.fireBrowserConnectionUpdated( browserConnectionUpdateEvent , this );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderModified(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
    }
}
