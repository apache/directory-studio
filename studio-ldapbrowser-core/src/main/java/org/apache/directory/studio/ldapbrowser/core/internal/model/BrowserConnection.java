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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.BookmarkManager;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.SearchManager;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesJob;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;
import org.apache.directory.studio.ldapbrowser.core.model.NameException;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.URL;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContentRecord;
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

    transient JNDIConnectionProvider connectionProvider;
    
    transient ModificationLogger modificationLogger;

    transient ConnectionSearchHandler searchHandler;


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
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD, IBrowserConnection.DEREFERENCE_ALIASES_NEVER );
            connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, IBrowserConnection.HANDLE_REFERRALS_IGNORE );
            connection.getConnectionParameter().setExtendedBoolProperty( CONNECTION_PARAMETER_FETCH_BASE_DNS, true );
            connection.getConnectionParameter().setExtendedProperty( CONNECTION_PARAMETER_BASE_DN, "" );
        }
        
//        this.browserConnectionParameter = new BrowserConnectionParameter();
//        this.browserConnectionParameter.setCountLimit( 0 );
//        this.browserConnectionParameter.setTimeLimit( 0 );
//        this.browserConnectionParameter.setAliasesDereferencingMethod( IConnection.DEREFERENCE_ALIASES_NEVER );
//        this.browserConnectionParameter.setReferralsHandlingMethod( IConnection.HANDLE_REFERRALS_IGNORE );
//        this.browserConnectionParameter.setFetchBaseDNs( true );
//        this.browserConnectionParameter.setBaseDN( new DN() );
        
        this.rootDSE = null;

        this.schema = Schema.DEFAULT_SCHEMA;
        this.searchManager = new SearchManager( this );
        this.bookmarkManager = new BookmarkManager( this );

        this.entryToChildrenFilterMap = new HashMap<IEntry, String>();
        this.dnToEntryCache = new HashMap<String, IEntry>();
        this.entryToAttributeInfoMap = new HashMap<IEntry, AttributeInfo>();
        this.entryToChildrenInfoMap = new HashMap<IEntry, ChildrenInfo>();

        this.connectionProvider = new JNDIConnectionProvider( connection );
        this.modificationLogger = new ModificationLogger( this );
        this.searchHandler = new ConnectionSearchHandler( this );
        
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
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#reloadSchema(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    public void reloadSchema( StudioProgressMonitor monitor )
    {
        InitializeAttributesJob.initializeAttributes( getRootDSE(), true, monitor );
        
        monitor.reportProgress( BrowserCoreMessages.model__loading_schema );
        loadSchema( monitor );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#fetchRootDSE(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    public void fetchRootDSE( StudioProgressMonitor monitor )
    {
        if ( !monitor.errorsReported() )
        {
            try
            {
                monitor.reportProgress( BrowserCoreMessages.model__loading_rootdse );
                loadRootDSE( monitor );
                monitor.worked( 1 );
            }
            catch ( Exception e )
            {
                monitor.reportError( BrowserCoreMessages.model__error_loading_rootdse );
                rootDSE = null;
            }

            if ( monitor.errorsReported() )
            {
                close();
            }
        }
    }


    /**
     * Open.
     * 
     * @param monitor the monitor
     */
    public void open( StudioProgressMonitor monitor )
    {
        this.fetchRootDSE( monitor );

        if ( this.connectionProvider != null && this.rootDSE != null )
        {
            try
            {
                monitor.reportProgress( BrowserCoreMessages.model__loading_schema );

                // check if schema is cached
                if ( this.schema == Schema.DEFAULT_SCHEMA )
                {
                    this.loadSchema( monitor );
                }
                else
                {
                    if ( this.rootDSE.getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY ) != null )
                    {
                        // check if schema is up-to-date
                        SearchParameter sp = new SearchParameter();
                        sp.setSearchBase( new DN( this.rootDSE.getAttribute(
                            IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY ).getStringValue() ) );
                        sp.setFilter( Schema.SCHEMA_FILTER );
                        sp.setScope( ISearch.SCOPE_OBJECT );
                        sp.setReturningAttributes( new String[]
                            { IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP,
                                IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP, } );
                        ISearch search = new Search( this, sp );
                        // ISearch search = new Search(null, this, new
                        // DN(this.rootDSE.getAttribute("subschemaSubentry").getStringValue()),
                        // ISearch.FILTER_TRUE,
                        // new String[] {
                        // IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP,
                        // IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP },
                        // ISearch.SCOPE_OBJECT, 0, 0);
                        this.search( search, monitor );
                        ISearchResult[] results = search.getSearchResults();

                        if ( results != null && results.length == 1 )
                        {
                            String schemaTimestamp = results[0]
                                .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP ) != null ? results[0]
                                .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP ).getStringValue()
                                : null;
                            if ( schemaTimestamp == null )
                            {
                                schemaTimestamp = results[0]
                                    .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP ) != null ? results[0]
                                    .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP ).getStringValue()
                                    : null;
                            }
                            String cacheTimestamp = this.schema.getModifyTimestamp() != null ? this.schema
                                .getModifyTimestamp() : this.schema.getCreateTimestamp();
                            if ( cacheTimestamp == null
                                || ( cacheTimestamp != null && schemaTimestamp != null && schemaTimestamp
                                    .compareTo( cacheTimestamp ) > 0 ) )
                            {
                                this.loadSchema( monitor );
                            }
                        }
                        else
                        {
                            this.schema = Schema.DEFAULT_SCHEMA;
                            monitor.reportError( BrowserCoreMessages.model__no_schema_information );
                        }
                    }
                    else
                    {
                        this.schema = Schema.DEFAULT_SCHEMA;
                        monitor.reportError( BrowserCoreMessages.model__missing_schema_location );
                    }
                }

            }
            catch ( Exception e )
            {
                this.schema = Schema.DEFAULT_SCHEMA;
                monitor.reportError( BrowserCoreMessages.model__error_loading_schema, e );
                e.printStackTrace();
                return;
            }
        }
    }


    /**
     * Closes the connections, clears all caches
     * 
     * TODO: call when connection is closed
     */
    private void close()
    {
        for ( int i = 0; i < getSearchManager().getSearchCount(); i++ )
        {
            this.getSearchManager().getSearches()[i].setSearchResults( null );
        }

        dnToEntryCache.clear();
        entryToAttributeInfoMap.clear();
        entryToChildrenInfoMap.clear();
        entryToChildrenFilterMap.clear();

        searchHandler.connectionClosed();

        rootDSE = null;
        schema = Schema.DEFAULT_SCHEMA;

        System.gc();
    }


    /**
     * Loads the Root DSE.
     * 
     * @param monitor the progress monitor
     * 
     * @throws Exception the exception
     */
    private void loadRootDSE( StudioProgressMonitor monitor ) throws Exception
    {
        if(rootDSE == null)
        {
            rootDSE = new RootDSE( this );
            cacheEntry( rootDSE );
        }

        // get well-known root DSE attributes, includes + and *
        ISearch search = new Search( null, this, new DN(), ISearch.FILTER_TRUE, ROOT_DSE_ATTRIBUTES, ISearch.SCOPE_OBJECT, 0,
            0, IBrowserConnection.DEREFERENCE_ALIASES_NEVER, IBrowserConnection.HANDLE_REFERRALS_IGNORE, false, false, null );
        search( search, monitor );

        // get base DNs
        if( !isFetchBaseDNs() && getBaseDN() != null && !"".equals( getBaseDN().toString() ))
        {
            // only add the specified base DN
            DN dn = getBaseDN();
            IEntry entry = new BaseDNEntry( new DN( dn ), this );
            cacheEntry( entry );
            rootDSE.addChild( entry );
            
            // check if entry exists
            search = new Search( null, this, dn, ISearch.FILTER_TRUE, ISearch.NO_ATTRIBUTES, ISearch.SCOPE_OBJECT, 1, 0,
                IBrowserConnection.DEREFERENCE_ALIASES_NEVER, IBrowserConnection.HANDLE_REFERRALS_IGNORE, true, true, null );
            search( search, monitor );
        }
        else
        {
            // get naming contexts 
            Set<String> namingContextSet = new HashSet<String>();
            IAttribute attribute = rootDSE.getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_NAMINGCONTEXTS );
            if ( attribute != null )
            {
                String[] values = attribute.getStringValues();
                for ( int i = 0; i < values.length; i++ )
                {
                    namingContextSet.add( values[i] );
                }
            }
            for ( String namingContext : namingContextSet )
            {
                if ( !"".equals( namingContext ) ) { //$NON-NLS-1$
                    try
                    {
                        IEntry entry = new BaseDNEntry( new DN( namingContext ), this );
                        rootDSE.addChild( entry );
                        cacheEntry( entry );
                    }
                    catch ( Exception e )
                    {
                        monitor.reportError( BrowserCoreMessages.model__error_setting_base_dn, e );
                    }
                }
                else
                {
                    // special handling of empty namingContext: perform a one-level search and add all result DNs to the set
                    search = new Search( null, this, new DN(), ISearch.FILTER_TRUE, ISearch.NO_ATTRIBUTES, ISearch.SCOPE_ONELEVEL, 0,
                        0, IBrowserConnection.DEREFERENCE_ALIASES_NEVER, IBrowserConnection.HANDLE_REFERRALS_IGNORE, false, false, null );
                    search( search, monitor );
                    ISearchResult[] results = search.getSearchResults();
                    for ( int k = 0; results != null && k < results.length; k++ )
                    {
                        ISearchResult result = results[k];
                        IEntry entry = result.getEntry();
                        rootDSE.addChild( entry );
                    }
                }
            }
        }

        // get schema entry
        DirectoryMetadataEntry[] schemaEntries = getDirectoryMetadataEntries( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY );
        for ( int i = 0; i < schemaEntries.length; i++ )
        {
            schemaEntries[i].setSchemaEntry( true );
            rootDSE.addChild( ( IEntry ) schemaEntries[i] );
        }
        
        // get other metadata entries
        String[] metadataAttributeNames = new String[]
            { IRootDSE.ROOTDSE_ATTRIBUTE_MONITORCONTEXT, IRootDSE.ROOTDSE_ATTRIBUTE_CONFIGCONTEXT,
                IRootDSE.ROOTDSE_ATTRIBUTE_DSANAME };
        for ( int x = 0; x < metadataAttributeNames.length; x++ )
        {
            DirectoryMetadataEntry[] metadataEntries = getDirectoryMetadataEntries( metadataAttributeNames[x] );
            for ( int i = 0; i < metadataEntries.length; i++ )
            {
                rootDSE.addChild( ( IEntry ) metadataEntries[i] );
            }
        }
        
        // set flags
        rootDSE.setHasMoreChildren( false );
        rootDSE.setAttributesInitialized( true );
        rootDSE.setChildrenInitialized( true );
        rootDSE.setHasChildrenHint( true );
        rootDSE.setDirectoryEntry( true );
    }


    /**
     * Gets the directory metadata entries.
     * 
     * @param metadataAttributeName the metadata attribute name
     * 
     * @return the directory metadata entries
     * 
     * @throws ModelModificationException the model modification exception
     */
    private DirectoryMetadataEntry[] getDirectoryMetadataEntries( String metadataAttributeName )
        throws ModelModificationException
    {
        List<DN> metadataEntryList = new ArrayList<DN>();
        IAttribute attribute = getRootDSE().getAttribute( metadataAttributeName );
        if ( attribute != null )
        {
            String[] values = attribute.getStringValues();
            for ( int i = 0; i < values.length; i++ )
            {
                try
                {
                    metadataEntryList.add( new DN( values[i] ) );
                }
                catch ( NameException e )
                {
                }
            }
        }

        DirectoryMetadataEntry[] metadataEntries = new DirectoryMetadataEntry[metadataEntryList.size()];
        for ( int i = 0; i < metadataEntryList.size(); i++ )
        {
            metadataEntries[i] = new DirectoryMetadataEntry( metadataEntryList.get( i ), this );
            metadataEntries[i].setDirectoryEntry( true );
            cacheEntry( metadataEntries[i] );
        }
        return metadataEntries;
    }


    /**
     * Loads the schema.
     * 
     * @param monitor the progress monitor
     */
    private void loadSchema( StudioProgressMonitor monitor )
    {
        schema = Schema.DEFAULT_SCHEMA;

        try
        {
            if ( getRootDSE().getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY ) != null )
            {
                SearchParameter sp = new SearchParameter();
                sp.setSearchBase( new DN( getRootDSE().getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY )
                    .getStringValue() ) );
                sp.setFilter( Schema.SCHEMA_FILTER );
                sp.setScope( ISearch.SCOPE_OBJECT );
                sp.setReturningAttributes( new String[]
                    { Schema.SCHEMA_ATTRIBUTE_OBJECTCLASSES, Schema.SCHEMA_ATTRIBUTE_ATTRIBUTETYPES,
                        Schema.SCHEMA_ATTRIBUTE_LDAPSYNTAXES, Schema.SCHEMA_ATTRIBUTE_MATCHINGRULES,
                        Schema.SCHEMA_ATTRIBUTE_MATCHINGRULEUSE, IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP,
                        IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP, } );
                LdifEnumeration le = connectionProvider.search( sp, monitor );
                if ( le.hasNext( monitor ) )
                {
                    LdifContentRecord schemaRecord = ( LdifContentRecord ) le.next( monitor );
                    schema = new Schema();
                    schema.loadFromRecord( schemaRecord );
                    // TODO: Schema update event
//                    EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
//                        ConnectionUpdateEvent.EventDetail.SCHEMA_LOADED ), this );
                }
                else
                {
                    monitor.reportError( BrowserCoreMessages.model__no_schema_information );
                }
            }
            else
            {
                monitor.reportError( BrowserCoreMessages.model__missing_schema_location );
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( BrowserCoreMessages.model__error_loading_schema, e );
            e.printStackTrace();
        }

        if ( schema == null )
        {
            schema = Schema.DEFAULT_SCHEMA;
        }
    }


    public void search( ISearch searchRequest, StudioProgressMonitor monitor )
    {
        searchHandler.search( searchRequest, monitor );
    }


    public boolean existsEntry( DN dn, StudioProgressMonitor monitor )
    {
        return searchHandler.existsEntry( dn, monitor );
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


    public IEntry getEntry( DN dn, StudioProgressMonitor monitor )
    {
        return searchHandler.getEntry( dn, monitor );
    }


    public LdifEnumeration exportLdif( SearchParameter searchParameter, StudioProgressMonitor monitor )
        throws ConnectionException
    {
        LdifEnumeration subEnumeration = this.connectionProvider.search( searchParameter, monitor );
        return subEnumeration;
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
    public int getAliasesDereferencingMethod()
    {
        return connection.getConnectionParameter().getExtendedIntProperty( CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#setAliasesDereferencingMethod(int)
     */
    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD, aliasesDereferencingMethod );
        ConnectionEventRegistry.fireConnectionUpdated( connection, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#getReferralsHandlingMethod()
     */
    public int getReferralsHandlingMethod()
    {
        return connection.getConnectionParameter().getExtendedIntProperty( CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection#setReferralsHandlingMethod(int)
     */
    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        connection.getConnectionParameter().setExtendedIntProperty( CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, referralsHandlingMethod );
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
        if( rootDSE == null )
        {
            try
            {
                rootDSE = new RootDSE( this );
                cacheEntry( rootDSE );
            }
            catch ( ModelModificationException e )
            {
            }
        }
        return this.rootDSE;
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


    protected void cacheEntry( IEntry entry )
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
            close();
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
