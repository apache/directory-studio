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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.DetectedConnectionProperties;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BaseDNEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DirectoryMetadataEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;


/**
 * Runnable to initialize the Root DSE.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitializeRootDSERunnable implements StudioConnectionBulkRunnableWithProgress
{
    /** The requested attributes when reading the Root DSE. */
    public static final String[] ROOT_DSE_ATTRIBUTES =
        { 
            SchemaConstants.NAMING_CONTEXTS_AT, 
            SchemaConstants.SUBSCHEMA_SUBENTRY_AT,
            SchemaConstants.SUPPORTED_LDAP_VERSION_AT, 
            SchemaConstants.SUPPORTED_SASL_MECHANISMS_AT,
            SchemaConstants.SUPPORTED_EXTENSION_AT, 
            SchemaConstants.SUPPORTED_CONTROL_AT,
            SchemaConstants.SUPPORTED_FEATURES_AT, 
            SchemaConstants.VENDOR_NAME_AT,
            SchemaConstants.VENDOR_VERSION_AT,
            SchemaConstants.ALL_OPERATIONAL_ATTRIBUTES };

    private IRootDSE rootDSE;


    /**
     * Creates a new instance of InitializeRootDSERunnable.
     * 
     * @param rootDSE the root DSE
     */
    private InitializeRootDSERunnable( IRootDSE rootDSE )
    {
        this.rootDSE = rootDSE;
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        return new Connection[]
            { rootDSE.getBrowserConnection().getConnection() };
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return BrowserCoreMessages.jobs__init_entries_title_attonly;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new IEntry[]
            { rootDSE };
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__init_entries_error_1;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", 3 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_task, new String[]
            { rootDSE.getDn().getName() } ) );
        monitor.worked( 1 );

        monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_att,
            new String[]
                { rootDSE.getDn().getName() } ) );

        loadRootDSE( rootDSE.getBrowserConnection(), monitor );
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        EventRegistry.fireEntryUpdated( new AttributesInitializedEvent( rootDSE ), this );
    }


    /**
     * Loads the Root DSE.
     * 
     * @param browserConnection the browser connection
     * @param monitor the progress monitor
     * 
     * @throws Exception the exception
     */
    public static synchronized void loadRootDSE( IBrowserConnection browserConnection, StudioProgressMonitor monitor )
    {
        // clear old children
        InitializeChildrenRunnable.clearCaches( browserConnection.getRootDSE(), true );

        // delete old attributes
        IAttribute[] oldAttributes = browserConnection.getRootDSE().getAttributes();
        
        if ( oldAttributes != null )
        {
            for ( IAttribute oldAttribute : oldAttributes )
            {
                browserConnection.getRootDSE().deleteAttribute( oldAttribute );
            }
        }

        // load well-known Root DSE attributes and operational attributes
        ISearch search = new Search( null, browserConnection, Dn.EMPTY_DN, ISearch.FILTER_TRUE,
            ROOT_DSE_ATTRIBUTES, SearchScope.OBJECT, 0, 0, Connection.AliasDereferencingMethod.NEVER,
            Connection.ReferralHandlingMethod.IGNORE, false, null );
        SearchRunnable.searchAndUpdateModel( browserConnection, search, monitor );

        // Load all user attributes. This is done because the BEA "LDAP server" (so called) is stupid
        // enough not to accept searches where "+" and "*" are provided on the list of parameters.
        // We have to do two searches...
        search = new Search( null, browserConnection, Dn.EMPTY_DN, ISearch.FILTER_TRUE, new String[]
            { SchemaConstants.ALL_USER_ATTRIBUTES }, SearchScope.OBJECT, 0, 0,
            Connection.AliasDereferencingMethod.NEVER, Connection.ReferralHandlingMethod.IGNORE, false, null );
        SearchRunnable.searchAndUpdateModel( browserConnection, search, monitor );

        // the list of entries under the Root DSE
        Map<Dn, IEntry> rootDseEntries = new HashMap<Dn, IEntry>();

        // 1st: add base DNs, either the specified or from the namingContexts attribute
        if ( !browserConnection.isFetchBaseDNs() && browserConnection.getBaseDN() != null
            && !"".equals( browserConnection.getBaseDN().toString() ) ) //$NON-NLS-1$
        {
            // only add the specified base Dn
            Dn dn = browserConnection.getBaseDN();
            IEntry entry = browserConnection.getEntryFromCache( dn );
            
            if ( entry == null )
            {
                entry = new BaseDNEntry( dn, browserConnection );
                browserConnection.cacheEntry( entry );
            }
            rootDseEntries.put( dn, entry );
        }
        else
        {
            // get base DNs from namingContexts attribute
            Set<String> namingContextSet = new HashSet<String>();
            IAttribute attribute = browserConnection.getRootDSE().getAttribute( SchemaConstants.NAMING_CONTEXTS_AT );
            
            if ( attribute != null )
            {
                String[] values = attribute.getStringValues();
                
                for ( int i = 0; i < values.length; i++ )
                {
                    namingContextSet.add( values[i] );
                }
            }

            if ( !namingContextSet.isEmpty() )
            {
                for ( String namingContext : namingContextSet )
                {
                    if ( namingContext.length() > 0 && namingContext.charAt( namingContext.length() - 1 ) == '\u0000' )
                    {
                        namingContext = namingContext.substring( 0, namingContext.length() - 1 );
                    }

                    if ( !"".equals( namingContext ) ) //$NON-NLS-1$
                    {
                        try
                        {
                            Dn dn = new Dn( namingContext );
                            IEntry entry = browserConnection.getEntryFromCache( dn );
                            
                            if ( entry == null )
                            {
                                entry = new BaseDNEntry( dn, browserConnection );
                                browserConnection.cacheEntry( entry );
                            }
                            
                            rootDseEntries.put( dn, entry );
                        }
                        catch ( LdapInvalidDnException e )
                        {
                            monitor.reportError( BrowserCoreMessages.model__error_setting_base_dn, e );
                        }
                    }
                    else
                    {
                        // special handling of empty namingContext (Novell eDirectory): 
                        // perform a one-level search and add all result DNs to the set
                        searchRootDseEntries( browserConnection, rootDseEntries, monitor );
                    }
                }
            }
            else
            {
                // special handling of non-existing namingContexts attribute (Oracle Internet Directory)
                // perform a one-level search and add all result DNs to the set
                searchRootDseEntries( browserConnection, rootDseEntries, monitor );
            }
        }

        // 2nd: add schema sub-entry
        IEntry[] schemaEntries = getDirectoryMetadataEntries( browserConnection, SchemaConstants.SUBSCHEMA_SUBENTRY_AT );
        
        for ( IEntry entry : schemaEntries )
        {
            if ( entry instanceof DirectoryMetadataEntry )
            {
                ( ( DirectoryMetadataEntry ) entry ).setSchemaEntry( true );
            }
            
            rootDseEntries.put( entry.getDn(), entry );
        }

        // get other meta data entries
        IAttribute[] rootDseAttributes = browserConnection.getRootDSE().getAttributes();
        
        if ( rootDseAttributes != null )
        {
            for ( IAttribute attribute : rootDseAttributes )
            {
                IEntry[] metadataEntries = getDirectoryMetadataEntries( browserConnection, attribute.getDescription() );
                
                for ( IEntry entry : metadataEntries )
                {
                    rootDseEntries.put( entry.getDn(), entry );
                }
            }
        }

        // try to init entries
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );
        
        for ( IEntry entry : rootDseEntries.values() )
        {
            initBaseEntry( entry, dummyMonitor );
        }

        // set flags
        browserConnection.getRootDSE().setHasMoreChildren( false );
        browserConnection.getRootDSE().setAttributesInitialized( true );
        browserConnection.getRootDSE().setInitOperationalAttributes( true );
        browserConnection.getRootDSE().setChildrenInitialized( true );
        browserConnection.getRootDSE().setHasChildrenHint( true );
        browserConnection.getRootDSE().setDirectoryEntry( true );

        // Set detected connection properties
        DetectedConnectionProperties detectedConnectionProperties = browserConnection.getConnection()
            .getDetectedConnectionProperties();
        IAttribute vendorNameAttribute = browserConnection.getRootDSE().getAttribute( "vendorName" ); //$NON-NLS-1$
        
        if ( ( vendorNameAttribute != null ) && ( vendorNameAttribute.getValueSize() > 0 ) )
        {
            detectedConnectionProperties.setVendorName( vendorNameAttribute.getStringValue() );
        }
        
        IAttribute vendorVersionAttribute = browserConnection.getRootDSE().getAttribute( "vendorVersion" ); //$NON-NLS-1$
        
        if ( ( vendorVersionAttribute != null ) && ( vendorVersionAttribute.getValueSize() > 0 ) )
        {
            detectedConnectionProperties.setVendorVersion( vendorVersionAttribute.getStringValue() );
        }
        
        IAttribute supportedControlAttribute = browserConnection.getRootDSE().getAttribute( "supportedControl" ); //$NON-NLS-1$
        
        if ( ( supportedControlAttribute != null ) && ( supportedControlAttribute.getValueSize() > 0 ) )
        {
            detectedConnectionProperties.setSupportedControls( Arrays.asList( supportedControlAttribute
                .getStringValues() ) );
        }
        
        IAttribute supportedExtensionAttribute = browserConnection.getRootDSE().getAttribute( "supportedExtension" ); //$NON-NLS-1$
        
        if ( ( supportedExtensionAttribute != null ) && ( supportedExtensionAttribute.getValueSize() > 0 ) )
        {
            detectedConnectionProperties.setSupportedExtensions( Arrays.asList( supportedExtensionAttribute
                .getStringValues() ) );
        }
        
        IAttribute supportedFeaturesAttribute = browserConnection.getRootDSE().getAttribute( "supportedFeatures" ); //$NON-NLS-1$
        
        if ( ( supportedFeaturesAttribute != null ) && ( supportedFeaturesAttribute.getValueSize() > 0 ) )
        {
            detectedConnectionProperties.setSupportedFeatures( Arrays.asList( supportedFeaturesAttribute
                .getStringValues() ) );
        }
        
        detectedConnectionProperties
            .setServerType( ServerTypeDetector.detectServerType( browserConnection.getRootDSE() ) );

        ConnectionCorePlugin.getDefault().getConnectionManager()
            .connectionUpdated( browserConnection.getConnection() );
    }


    private static void initBaseEntry( IEntry entry, StudioProgressMonitor monitor )
    {
        IBrowserConnection browserConnection = entry.getBrowserConnection();
        Dn dn = entry.getDn();

        // search the entry
        AliasDereferencingMethod derefAliasMethod = browserConnection.getAliasesDereferencingMethod();
        ReferralHandlingMethod handleReferralsMethod = browserConnection.getReferralsHandlingMethod();
        ISearch search = new Search( null, browserConnection, dn, ISearch.FILTER_TRUE, ISearch.NO_ATTRIBUTES,
            SearchScope.OBJECT, 1, 0, derefAliasMethod, handleReferralsMethod, true, null );
        SearchRunnable.searchAndUpdateModel( browserConnection, search, monitor );

        ISearchResult[] results = search.getSearchResults();
        
        if ( results != null && results.length == 1 )
        {
            // add entry to Root DSE
            ISearchResult result = results[0];
            entry = result.getEntry();
            browserConnection.getRootDSE().addChild( entry );
        }
        else
        {
            // Dn exists in the Root DSE, but doesn't exist in directory
            browserConnection.uncacheEntryRecursive( entry );
        }
    }


    private static IEntry[] getDirectoryMetadataEntries( IBrowserConnection browserConnection,
        String metadataAttributeName )
    {
        List<Dn> metadataEntryDnList = new ArrayList<Dn>();
        IAttribute attribute = browserConnection.getRootDSE().getAttribute( metadataAttributeName );
        
        if ( attribute != null )
        {
            String[] values = attribute.getStringValues();
            
            for ( String dn : values )
            {
                if ( dn != null && !"".equals( dn ) ) //$NON-NLS-1$
                {
                    try
                    {
                        metadataEntryDnList.add( new Dn( dn ) );
                    }
                    catch ( LdapInvalidDnException e )
                    {
                    }
                }
            }
        }

        IEntry[] metadataEntries = new IEntry[metadataEntryDnList.size()];
        
        for ( int i = 0; i < metadataEntryDnList.size(); i++ )
        {
            Dn dn = metadataEntryDnList.get( i );
            metadataEntries[i] = browserConnection.getEntryFromCache( dn );
            
            if ( metadataEntries[i] == null )
            {
                metadataEntries[i] = new DirectoryMetadataEntry( dn, browserConnection );
                metadataEntries[i].setDirectoryEntry( true );
                browserConnection.cacheEntry( metadataEntries[i] );
            }
        }
        
        return metadataEntries;
    }


    private static void searchRootDseEntries( IBrowserConnection browserConnection, Map<Dn, IEntry> rootDseEntries,
        StudioProgressMonitor monitor )
    {
        ISearch search = new Search( null, browserConnection, Dn.EMPTY_DN, ISearch.FILTER_TRUE,
            ISearch.NO_ATTRIBUTES, SearchScope.ONELEVEL, 0, 0, Connection.AliasDereferencingMethod.NEVER,
            Connection.ReferralHandlingMethod.IGNORE, false, null );
        SearchRunnable.searchAndUpdateModel( browserConnection, search, monitor );

        ISearchResult[] results = search.getSearchResults();

        if ( results != null )
        {
            for ( ISearchResult searchResult : results )
            {
                IEntry entry = searchResult.getEntry();
                rootDseEntries.put( entry.getDn(), entry );
            }
        }
    }
}
