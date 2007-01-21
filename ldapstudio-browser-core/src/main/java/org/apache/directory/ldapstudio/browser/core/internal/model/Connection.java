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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.BookmarkManager;
import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.SearchManager;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionRenamedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.ldapstudio.browser.core.jobs.ExtendedProgressMonitor;
import org.apache.directory.ldapstudio.browser.core.model.ConnectionParameter;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IAuthHandler;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IConnectionProvider;
import org.apache.directory.ldapstudio.browser.core.model.ICredentials;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IRootDSE;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.core.model.SearchParameter;
import org.apache.directory.ldapstudio.browser.core.model.URL;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifEnumeration;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.eclipse.search.ui.ISearchPageScoreComputer;


public class Connection implements IConnection, Serializable
{

    private static final long serialVersionUID = 2987596234755856270L;

    private ConnectionParameter connectionParameter;

    private IEntry[] baseDNEntries;

    private IEntry[] metadataEntries;

    private IRootDSE rootDSE;

    private Schema schema;

    private SearchManager searchManager;

    private BookmarkManager bookmarkManager;

    private volatile Map dnToEntryCache;

    private volatile Map entryToChildrenFilterMap;

    private volatile Map entryToAttributeInfoMap;

    private volatile Map entryToChildrenInfoMap;

    private static final String DEFAULT_PROVIDER = JNDIConnectionProvider.class.getName();

    transient IConnectionProvider connectionProvider;

    transient ConnectionModifyHandler modifyHandler;

    transient ConnectionSearchHandler searchHandler;


    public Connection()
    {
        this( null, null, 0, 0, true, new DN(), 0, 0, IConnection.DEREFERENCE_ALIASES_NEVER,
            IConnection.HANDLE_REFERRALS_IGNORE, IConnection.AUTH_ANONYMOUS, null, null );
    }


    public Connection( String name, String host, int port, int encryptionMethod, boolean fetchBaseDNs, DN baseDN,
        int countLimit, int timeLimit, int aliasesDereferencingMethod, int referralsHandlingMethod, int authMethod,
        String bindPrincipal, String bindPassword )
    {

        this.connectionParameter = new ConnectionParameter();
        this.connectionParameter.setName( name );
        this.connectionParameter.setHost( host );
        this.connectionParameter.setPort( port );
        this.connectionParameter.setEncryptionMethod( encryptionMethod );
        this.connectionParameter.setCountLimit( countLimit );
        this.connectionParameter.setTimeLimit( timeLimit );
        this.connectionParameter.setAliasesDereferencingMethod( aliasesDereferencingMethod );
        this.connectionParameter.setReferralsHandlingMethod( referralsHandlingMethod );
        this.connectionParameter.setFetchBaseDNs( fetchBaseDNs );
        this.connectionParameter.setBaseDN( baseDN );
        this.connectionParameter.setAuthMethod( authMethod );
        // this.connectionParameter.setBindDN(bindDn);
        this.connectionParameter.setBindPrincipal( bindPrincipal );
        this.connectionParameter.setBindPassword( bindPassword );

        this.baseDNEntries = new IEntry[0];
        this.metadataEntries = new IEntry[0];
        this.rootDSE = null;

        this.schema = Schema.DEFAULT_SCHEMA;
        this.searchManager = new SearchManager( this );
        this.bookmarkManager = new BookmarkManager( this );

        this.entryToChildrenFilterMap = new HashMap();
        this.dnToEntryCache = new HashMap();
        this.entryToAttributeInfoMap = new HashMap();
        this.entryToChildrenInfoMap = new HashMap();

        this.setConnectionProviderClassName( DEFAULT_PROVIDER );
        this.modifyHandler = new ConnectionModifyHandler( this );
        this.searchHandler = new ConnectionSearchHandler( this );
    }


    public URL getUrl()
    {
        return new URL( this );
    }


    public Object clone()
    {
        Connection newConnection = new Connection( this.getName(), this.getHost(), this.getPort(), this
            .getEncryptionMethod(), this.isFetchBaseDNs(), this.getBaseDN(), this.getCountLimit(), this.getTimeLimit(),
            this.getAliasesDereferencingMethod(), this.getReferralsHandlingMethod(), this.getAuthMethod(), this
                .getBindPrincipal(), this.getBindPassword() );

        return newConnection;
    }


    public void reloadSchema( ExtendedProgressMonitor monitor )
    {
        monitor.reportProgress( BrowserCoreMessages.model__loading_schema );
        this.loadSchema( monitor );
    }


    public void connect( ExtendedProgressMonitor monitor )
    {
        if ( this.connectionProvider == null )
        {
            if ( this.getConnectionProviderClassName() == null )
            {
                monitor.reportError( BrowserCoreMessages.model__no_connection_provider );
                return;
            }

            try
            {
                this.connectionProvider = ( IConnectionProvider ) Class.forName( this.getConnectionProviderClassName() )
                    .newInstance();
            }
            catch ( Exception e )
            {
                monitor.reportError( BrowserCoreMessages.model__no_connection_provider );
                return;
            }
        }

        try
        {

            this.entryToChildrenFilterMap = new HashMap();
            this.dnToEntryCache = new HashMap();
            this.entryToAttributeInfoMap = new HashMap();
            this.entryToChildrenInfoMap = new HashMap();

            modifyHandler.connectionOpened();
            searchHandler.connectionOpened();

            monitor.reportProgress( BrowserCoreMessages.model__connecting );
            this.connectionProvider.connect( this.connectionParameter, monitor );
            monitor.worked( 1 );
        }
        catch ( ConnectionException e )
        {
            monitor.reportError( e.getMessage(), e );
            this.connectionProvider = null;
        }
    }


    public void bind( ExtendedProgressMonitor monitor )
    {
        this.connect( monitor );

        if ( this.connectionProvider != null )
        {
            try
            {
                monitor.reportProgress( BrowserCoreMessages.model__binding );

                IAuthHandler authHandler = BrowserCorePlugin.getDefault().getAuthHandler();
                if ( authHandler == null )
                {
                    throw new ConnectionException( BrowserCoreMessages.model__no_auth_handler );
                }

                ICredentials credentials = authHandler.getCredentials( this.connectionParameter );
                if ( credentials == null )
                {
                    throw new ConnectionException( BrowserCoreMessages.model__no_credentials );
                }

                this.connectionProvider.bind( this.connectionParameter, credentials, monitor );
                monitor.worked( 1 );
            }
            catch ( ConnectionException e )
            {
                monitor.reportError( e.getMessage(), e );
                this.connectionProvider = null;
            }
        }
    }


    public void fetchRootDSE( ExtendedProgressMonitor monitor )
    {
        this.bind( monitor );

        if ( this.connectionProvider != null )
        {
            try
            {
                monitor.reportProgress( BrowserCoreMessages.model__loading_rootdse );
                this.loadRootDSE( monitor );
                monitor.worked( 1 );
            }
            catch ( Exception e )
            {
                monitor.reportError( BrowserCoreMessages.model__error_loading_rootdse );
                this.rootDSE = null;
            }
        }
    }


    public void open( ExtendedProgressMonitor monitor )
    {
        this.fetchRootDSE( monitor );

        if ( this.connectionProvider != null && this.rootDSE != null )
        {
            try
            {
                monitor.reportProgress( BrowserCoreMessages.model__setting_base_dn );
                if ( !this.connectionParameter.isFetchBaseDNs() )
                {
                    this.baseDNEntries = new BaseDNEntry[1];
                    this.baseDNEntries[0] = new BaseDNEntry( new DN( this.connectionParameter.getBaseDN() ), this );
                    this.cacheEntry( this.baseDNEntries[0] );
                }
            }
            catch ( ModelModificationException mme )
            {
                monitor.reportError( BrowserCoreMessages.model__error_setting_base_dn, mme );
            }

            try
            {
                this.loadDirectoryMetadataEntries();
            }
            catch ( ModelModificationException mme )
            {
                monitor.reportError( BrowserCoreMessages.model__error_setting_metadata, mme );
            }

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
                        sp.setFilter( ISearch.FILTER_TRUE );
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

            EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
                ConnectionUpdateEvent.CONNECTION_OPENED ), this );
        }
    }


    public boolean isOpened()
    {
        return this.connectionProvider != null;
    }


    public boolean canOpen()
    {
        return !this.isOpened();
    }


    public boolean canClose()
    {
        return this.isOpened();
    }


    public void close()
    {
        if ( this.isOpened() )
        {
            if ( this.connectionProvider != null )
            {
                try
                {
                    this.connectionProvider.close();
                }
                catch ( ConnectionException ce )
                {
                    ce.printStackTrace();
                }
                this.connectionProvider = null;
            }

            if ( this.baseDNEntries != null )
            {
                this.baseDNEntries = new BaseDNEntry[0];
            }
            if ( this.metadataEntries != null )
            {
                this.metadataEntries = new IEntry[0];
            }

            for ( int i = 0; i < this.getSearchManager().getSearchCount(); i++ )
            {
                this.getSearchManager().getSearches()[i].setSearchResults( null );
            }

            this.dnToEntryCache.clear();
            this.entryToAttributeInfoMap.clear();
            this.entryToChildrenInfoMap.clear();
            this.entryToChildrenFilterMap.clear();

            modifyHandler.connectionClosed();
            searchHandler.connectionClosed();

            EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
                ConnectionUpdateEvent.CONNECTION_CLOSED ), this );
            System.gc();
        }
    }


    private void loadRootDSE( ExtendedProgressMonitor monitor ) throws Exception
    {
        this.rootDSE = new RootDSE( this );
        this.cacheEntry( this.rootDSE );

        // First get ALL attributes
        ISearch search = new Search( null, this, new DN(), ISearch.FILTER_TRUE, null, ISearch.SCOPE_OBJECT, 0, 0,
            IConnection.DEREFERENCE_ALIASES_NEVER, IConnection.HANDLE_REFERRALS_IGNORE, false, false, null );
        this.search( search, monitor );

        // Second get well-known root DSE attributes
        search = new Search( null, this, new DN(), ISearch.FILTER_TRUE, ROOT_DSE_ATTRIBUTES, ISearch.SCOPE_OBJECT, 0,
            0, IConnection.DEREFERENCE_ALIASES_NEVER, IConnection.HANDLE_REFERRALS_IGNORE, false, false, null );
        this.search( search, monitor );

        // Set base DNs from root DSE
        if ( this.rootDSE != null )
        {
            try
            {
                List baseDnEntryList = new ArrayList();
                String[] baseDnAttributeNames = new String[]
                    { IRootDSE.ROOTDSE_ATTRIBUTE_NAMINGCONTEXTS };
                for ( int x = 0; x < baseDnAttributeNames.length; x++ )
                {
                    IAttribute attribute = this.rootDSE.getAttribute( baseDnAttributeNames[x] );
                    if ( attribute != null )
                    {
                        String[] values = attribute.getStringValues();
                        for ( int i = 0; i < values.length; i++ )
                        {
                            if ( !"".equals( values[i] ) ) { //$NON-NLS-1$
                                baseDnEntryList.add( values[i] );
                            }
                        }
                    }
                }
                this.baseDNEntries = new BaseDNEntry[baseDnEntryList.size()];
                for ( int i = 0; i < this.baseDNEntries.length; i++ )
                {
                    this.baseDNEntries[i] = new BaseDNEntry( new DN( ( String ) baseDnEntryList.get( i ) ), this );
                    this.cacheEntry( this.baseDNEntries[i] );
                }

                // this.loadDirectoryMetadataEntries();
            }
            catch ( Exception e )
            {
                monitor.reportError( BrowserCoreMessages.model__error_setting_base_dn, e );
            }
        }
    }


    private void loadDirectoryMetadataEntries() throws ModelModificationException
    {

        List metadataEntryList = new ArrayList();

        // special case for schema entry
        DirectoryMetadataEntry[] schemaEntries = getDirectoryMetadataEntries( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY );
        for ( int i = 0; i < schemaEntries.length; i++ )
        {
            schemaEntries[i].setSchemaEntry( true );
        }
        metadataEntryList.addAll( Arrays.asList( schemaEntries ) );

        // other metadata entries
        String[] metadataAttributeNames = new String[]
            { IRootDSE.ROOTDSE_ATTRIBUTE_MONITORCONTEXT, IRootDSE.ROOTDSE_ATTRIBUTE_CONFIGCONTEXT,
                IRootDSE.ROOTDSE_ATTRIBUTE_DSANAME };
        for ( int x = 0; x < metadataAttributeNames.length; x++ )
        {
            DirectoryMetadataEntry[] metadataEntries = getDirectoryMetadataEntries( metadataAttributeNames[x] );
            metadataEntryList.addAll( Arrays.asList( metadataEntries ) );
        }

        this.metadataEntries = new IEntry[metadataEntryList.size()];
        for ( int i = 0; i < metadataEntryList.size(); i++ )
        {
            this.metadataEntries[i] = ( IEntry ) metadataEntryList.get( i );
            this.cacheEntry( this.metadataEntries[i] );
        }

    }


    private DirectoryMetadataEntry[] getDirectoryMetadataEntries( String metadataAttributeName )
        throws ModelModificationException
    {
        List metadataEntryList = new ArrayList();
        IAttribute attribute = this.rootDSE.getAttribute( metadataAttributeName );
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
            metadataEntries[i] = new DirectoryMetadataEntry( ( DN ) metadataEntryList.get( i ), this );
            metadataEntries[i].setDirectoryEntry( true );
        }
        return metadataEntries;
    }


    private void loadSchema( ExtendedProgressMonitor monitor )
    {

        this.schema = Schema.DEFAULT_SCHEMA;

        try
        {

            // System.out.println(this.rootDSE);
            // System.out.println(Arrays.asList(this.rootDSE.getAttributes()));
            // System.out.println(this.rootDSE.getAttribute(IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY));

            if ( this.rootDSE.getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY ) != null )
            {
                SearchParameter sp = new SearchParameter();
                sp.setSearchBase( new DN( this.rootDSE.getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY )
                    .getStringValue() ) );
                sp.setFilter( ISearch.FILTER_TRUE );
                sp.setScope( ISearch.SCOPE_OBJECT );
                sp.setReturningAttributes( new String[]
                    { Schema.SCHEMA_ATTRIBUTE_OBJECTCLASSES, Schema.SCHEMA_ATTRIBUTE_ATTRIBUTETYPES,
                        Schema.SCHEMA_ATTRIBUTE_LDAPSYNTAXES, Schema.SCHEMA_ATTRIBUTE_MATCHINGRULES,
                        Schema.SCHEMA_ATTRIBUTE_MATCHINGRULEUSE, IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP,
                        IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP, } );
                LdifEnumeration le = this.connectionProvider.search( sp, monitor );
                if ( le.hasNext( monitor ) )
                {
                    LdifContentRecord schemaRecord = ( LdifContentRecord ) le.next( monitor );
                    this.schema = new Schema();
                    this.schema.loadFromRecord( schemaRecord );
                    EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
                        ConnectionUpdateEvent.CONNECTION_SCHEMA_LOADED ), this );
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

        if ( this.schema == null )
        {
            this.schema = Schema.DEFAULT_SCHEMA;
        }

    }


    public void search( ISearch searchRequest, ExtendedProgressMonitor monitor )
    {
        searchHandler.search( searchRequest, monitor );
    }


    public boolean existsEntry( DN dn, ExtendedProgressMonitor monitor )
    {
        return searchHandler.existsEntry( dn, monitor );
    }


    public IEntry getEntryFromCache( DN dn )
    {

        if ( this.dnToEntryCache != null && this.dnToEntryCache.containsKey( dn.toOidString( this.schema ) ) )
        {
            return ( IEntry ) dnToEntryCache.get( dn.toOidString( this.schema ) );
        }
        if ( this.rootDSE != null && this.rootDSE.getDn() != null && this.rootDSE.getDn().equals( dn ) )
        {
            return this.rootDSE;
        }
        if ( this.baseDNEntries != null )
        {
            for ( int i = 0; i < this.baseDNEntries.length; i++ )
            {
                if ( this.baseDNEntries[i] != null && this.baseDNEntries[i].getDn() != null
                    && this.baseDNEntries[i].getDn().equals( dn ) )
                {
                    return this.baseDNEntries[i];
                }
            }
        }
        if ( this.metadataEntries != null )
        {
            for ( int i = 0; i < this.metadataEntries.length; i++ )
            {
                if ( this.metadataEntries[i] != null && this.metadataEntries[i].getDn() != null
                    && this.metadataEntries[i].getDn().equals( dn ) )
                {
                    return this.metadataEntries[i];
                }
            }
        }
        return null;
    }


    public IEntry getEntry( DN dn, ExtendedProgressMonitor monitor )
    {
        return searchHandler.getEntry( dn, monitor );
    }


    public void create( IValue[] valuesToCreate, ExtendedProgressMonitor monitor )
    {
        modifyHandler.create( valuesToCreate, monitor );
    }


    public void modify( IValue oldValue, IValue newValue, ExtendedProgressMonitor monitor )
    {
        modifyHandler.modify( oldValue, newValue, monitor );
    }


    public void create( IEntry entryToCreate, ExtendedProgressMonitor monitor )
    {
        modifyHandler.create( entryToCreate, monitor );
    }


    public void rename( IEntry entryToRename, DN newDn, boolean deleteOldRdn, ExtendedProgressMonitor monitor )
    {
        modifyHandler.rename( entryToRename, newDn, deleteOldRdn, monitor );
    }


    public void move( IEntry entryToMove, DN newSuperior, ExtendedProgressMonitor monitor )
    {
        modifyHandler.move( entryToMove, newSuperior, monitor );
    }


    public LdifEnumeration exportLdif( SearchParameter searchParameter, ExtendedProgressMonitor monitor )
        throws ConnectionException
    {
        LdifEnumeration subEnumeration = this.connectionProvider.search( searchParameter, monitor );
        return subEnumeration;
    }


    public final String getName()
    {
        return this.connectionParameter.getName();
    }


    public final void setName( String name )
    {
        String oldName = this.getName();
        this.connectionParameter.setName( name );
        EventRegistry.fireConnectionUpdated( new ConnectionRenamedEvent( this, oldName ), this );
    }


    public boolean isFetchBaseDNs()
    {
        return this.connectionParameter.isFetchBaseDNs();
    }


    public void setFetchBaseDNs( boolean fetchBaseDNs )
    {
        this.connectionParameter.setFetchBaseDNs( fetchBaseDNs );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public DN getBaseDN()
    {
        return this.connectionParameter.getBaseDN();
    }


    public void setBaseDN( DN baseDN )
    {
        this.connectionParameter.setBaseDN( baseDN );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public int getCountLimit()
    {
        return this.connectionParameter.getCountLimit();
    }


    public void setCountLimit( int countLimit )
    {
        this.connectionParameter.setCountLimit( countLimit );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public String getHost()
    {
        return this.connectionParameter.getHost();
    }


    public void setHost( String host )
    {
        this.connectionParameter.setHost( host );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public int getPort()
    {
        return this.connectionParameter.getPort();
    }


    public void setPort( int port )
    {
        this.connectionParameter.setPort( port );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public int getAliasesDereferencingMethod()
    {
        return this.connectionParameter.getAliasesDereferencingMethod();
    }


    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        this.connectionParameter.setAliasesDereferencingMethod( aliasesDereferencingMethod );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public int getReferralsHandlingMethod()
    {
        return this.connectionParameter.getReferralsHandlingMethod();
    }


    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        this.connectionParameter.setReferralsHandlingMethod( referralsHandlingMethod );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public int getEncryptionMethod()
    {
        return this.connectionParameter.getEncryptionMethod();
    }


    public void setEncryptionMethod( int encryptionMethod )
    {
        this.connectionParameter.setEncryptionMethod( encryptionMethod );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public int getTimeLimit()
    {
        return this.connectionParameter.getTimeLimit();
    }


    public void setTimeLimit( int timeLimit )
    {
        this.connectionParameter.setTimeLimit( timeLimit );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public String getBindPrincipal()
    {
        return this.connectionParameter.getBindPrincipal();
    }


    public void setBindPrincipal( String bindPrincipal )
    {
        this.connectionParameter.setBindPrincipal( bindPrincipal );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public String getBindPassword()
    {
        return this.connectionParameter.getBindPassword();
    }


    public void setBindPassword( String bindPassword )
    {
        this.connectionParameter.setBindPassword( bindPassword );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public int getAuthMethod()
    {
        return this.connectionParameter.getAuthMethod();
    }


    public void setAuthMethod( int authMethod )
    {
        this.connectionParameter.setAuthMethod( authMethod );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public String getConnectionProviderClassName()
    {
        return this.connectionParameter.getConnectionProviderClassName();
    }


    public void setConnectionProviderClassName( String connectionProviderClassName )
    {
        this.connectionParameter.setConnectionProviderClassName( connectionProviderClassName );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
            ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED ), this );
    }


    public final IEntry[] getBaseDNEntries()
    {
        return this.baseDNEntries;
    }


    public final IEntry[] getMetadataEntries()
    {
        return this.metadataEntries;
    }


    public final IRootDSE getRootDSE()
    {
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


    public ConnectionParameter getConnectionParameter()
    {
        return connectionParameter;
    }


    public void setConnectionParameter( ConnectionParameter connectionParameter )
    {
        this.connectionParameter = connectionParameter;
    }


    public String toString()
    {
        return this.connectionParameter.getName();
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
        return modifyHandler.getModificationLogger();
    }


    public Object getAdapter( Class adapter )
    {

        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( adapter == IConnection.class )
        {
            return this;
        }

        return null;
    }


    public IConnection getConnection()
    {
        return this;
    }


    protected void cacheEntry( IEntry entry )
    {
        this.dnToEntryCache.put( entry.getDn().toOidString( this.schema ), entry );
    }


    protected void uncacheEntry( IEntry entry )
    {
        this.dnToEntryCache.remove( entry.getDn().toOidString( this.schema ) );
    }


    protected void uncacheEntry( DN dn )
    {
        this.dnToEntryCache.remove( dn.toOidString( this.schema ) );
    }


    protected String getChildrenFilter( IEntry entry )
    {
        return this.entryToChildrenFilterMap == null ? null : ( String ) this.entryToChildrenFilterMap.get( entry );
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
        return this.entryToAttributeInfoMap == null ? null : ( AttributeInfo ) this.entryToAttributeInfoMap.get( entry );
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
        return this.entryToChildrenInfoMap == null ? null : ( ChildrenInfo ) this.entryToChildrenInfoMap.get( entry );
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


    public void delete( IEntry entry, ExtendedProgressMonitor monitor )
    {
        modifyHandler.delete( entry, monitor );
    }


    public void delete( IValue[] valuesToDelete, ExtendedProgressMonitor monitor )
    {
        modifyHandler.delete( valuesToDelete, monitor );
    }


    public void delete( IAttribute[] attriubtesToDelete, ExtendedProgressMonitor monitor )
    {
        modifyHandler.delete( attriubtesToDelete, monitor );
    }


    public void importLdif( LdifEnumeration enumeration, Writer logWriter, boolean continueOnError,
        ExtendedProgressMonitor monitor )
    {
        modifyHandler.importLdif( enumeration, logWriter, continueOnError, monitor );
    }


    public synchronized boolean isSuspended()
    {
        return modifyHandler.isSuspended();
    }


    public synchronized void suspend()
    {
        modifyHandler.suspend();
    }


    public synchronized void resume( ExtendedProgressMonitor monitor )
    {
        modifyHandler.resume( monitor );
    }


    public synchronized void reset()
    {
        modifyHandler.reset();
    }

}
