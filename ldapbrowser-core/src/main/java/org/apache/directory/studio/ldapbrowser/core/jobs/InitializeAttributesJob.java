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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.schema.syntax.AttributeTypeDescription;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BaseDNEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DirectoryMetadataEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;


/**
 * Job to initialize the attributes of an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class InitializeAttributesJob extends AbstractNotificationJob
{

    /** The entries. */
    private IEntry[] entries;

    /** The flag if operational attributes should be initialized. */
    private boolean initOperationalAttributes;

    /** The requested attributes when reading the Root DSE. */
    public static final String[] ROOT_DSE_ATTRIBUTES =
        { IRootDSE.ROOTDSE_ATTRIBUTE_MONITORCONTEXT, IRootDSE.ROOTDSE_ATTRIBUTE_NAMINGCONTEXTS,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDLDAPVERSION, IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY,
            IRootDSE.ROOTDSE_ATTRIBUTE_ALTSERVER, IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDEXTENSION,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL, IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDFEATURES,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDSASLMECHANISM, ISearch.ALL_OPERATIONAL_ATTRIBUTES };


    /**
     * Creates a new instance of InitializeAttributesJob.
     * 
     * @param entries the entries
     * @param initOperationalAttributes true if operational attributes should be initialized
     */
    public InitializeAttributesJob( IEntry[] entries, boolean initOperationalAttributes )
    {
        this.entries = entries;
        this.initOperationalAttributes = initOperationalAttributes;
        setName( BrowserCoreMessages.jobs__init_entries_title_attonly );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        Connection[] connections = new Connection[entries.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = entries[i].getBrowserConnection().getConnection();
        }
        return connections;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.addAll( Arrays.asList( entries ) );
        return l.toArray();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return entries.length == 1 ? BrowserCoreMessages.jobs__init_entries_error_1
            : BrowserCoreMessages.jobs__init_entries_error_n;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", entries.length + 2 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int pi = 0; pi < entries.length && !monitor.isCanceled(); pi++ )
        {
            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_task, new String[]
                { this.entries[pi].getDn().getUpName() } ) );
            monitor.worked( 1 );
            if ( entries[pi].getBrowserConnection() != null && entries[pi].isDirectoryEntry() )
            {
                initializeAttributes( entries[pi], initOperationalAttributes, monitor );
            }
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        for ( IEntry entry : entries )
        {
            if ( entry.getBrowserConnection() != null && entry.isDirectoryEntry() )
            {
                EventRegistry.fireEntryUpdated( new AttributesInitializedEvent( entry ), this );
            }
        }
    }


    /**
     * Initializes the attributes.
     * 
     * @param entry the entry
     * @param initOperationalAttributes true if operational attributes should be initialized
     * @param monitor the progress monitor
     */
    public static void initializeAttributes( IEntry entry, boolean initOperationalAttributes,
        StudioProgressMonitor monitor )
    {
        // get user attributes or both user and operational attributes
        String[] returningAttributes = null;
        LinkedHashSet<String> raSet = new LinkedHashSet<String>();
        raSet.add( ISearch.ALL_USER_ATTRIBUTES );
        if ( initOperationalAttributes )
        {
            Collection<AttributeTypeDescription> opAtds = SchemaUtils.getOperationalAttributeDescriptions( entry
                .getBrowserConnection().getSchema() );
            Collection<String> atdNames = SchemaUtils.getNames( opAtds );
            raSet.addAll( atdNames );
            if ( entry.getBrowserConnection().getRootDSE().isFeatureSupported(
                IRootDSE.FEATURE_ALL_OPERATIONAL_ATTRIBUTES_OID ) )
            {
                raSet.add( ISearch.ALL_OPERATIONAL_ATTRIBUTES );
            }
        }
        if ( entry.isReferral() )
        {
            raSet.add( IAttribute.REFERRAL_ATTRIBUTE );
        }
        returningAttributes = ( String[] ) raSet.toArray( new String[raSet.size()] );

        initializeAttributes( entry, returningAttributes, monitor );

        entry.setOperationalAttributesInitialized( initOperationalAttributes );
    }


    /**
     * Initializes the attributes.
     * 
     * @param entry the entry
     * @param attributes the returning attributes
     * @param monitor the progress monitor
     */
    public static void initializeAttributes( IEntry entry, String[] attributes, StudioProgressMonitor monitor )
    {
        monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_att,
            new String[]
                { entry.getDn().getUpName() } ) );

        if ( entry instanceof IRootDSE )
        {
            // special handling for Root DSE
            loadRootDSE( entry.getBrowserConnection(), monitor );

            entry.setAttributesInitialized( true );
            entry.setChildrenInitialized( true );
        }
        else
        {
            AliasDereferencingMethod aliasesDereferencingMethod = entry.getBrowserConnection()
                .getAliasesDereferencingMethod();
            if ( entry.isAlias() )
            {
                aliasesDereferencingMethod = AliasDereferencingMethod.NEVER;
            }
            ReferralHandlingMethod referralsHandlingMethod = entry.getBrowserConnection().getReferralsHandlingMethod();
            if ( entry.isReferral() )
            {
                referralsHandlingMethod = ReferralHandlingMethod.MANAGE;
            }
            
            // search
            ISearch search = new Search( null, entry.getBrowserConnection(), entry.getDn(),
                entry.isSubentry() ? ISearch.FILTER_SUBENTRY : ISearch.FILTER_TRUE, attributes, SearchScope.OBJECT, 0,
                0, aliasesDereferencingMethod, referralsHandlingMethod, false, null );
            SearchJob.searchAndUpdateModel( entry.getBrowserConnection(), search, monitor );

            // set initialized state
            entry.setAttributesInitialized( true );
        }
    }


    /**
     * Loads the Root DSE.
     * 
     * @param browserConnection the browser connection
     * @param monitor the progress monitor
     * 
     * @throws Exception the exception
     */
    static void loadRootDSE( IBrowserConnection browserConnection, StudioProgressMonitor monitor )
    {
        // delete old children
        IEntry[] oldChildren = browserConnection.getRootDSE().getChildren();
        if ( oldChildren != null )
        {
            for ( IEntry entry : oldChildren )
            {
                if ( entry != null )
                {
                    browserConnection.getRootDSE().deleteChild( entry );
                }
            }
        }
        browserConnection.getRootDSE().setChildrenInitialized( false );

        // load all user attributes
        ISearch search = new Search( null, browserConnection, LdapDN.EMPTY_LDAPDN, ISearch.FILTER_TRUE, new String[]
            { ISearch.ALL_USER_ATTRIBUTES }, SearchScope.OBJECT, 0, 0, Connection.AliasDereferencingMethod.NEVER,
            Connection.ReferralHandlingMethod.IGNORE, false, null );
        SearchJob.searchAndUpdateModel( browserConnection, search, monitor );

        // load well-known Root DSE attributes and operational attributes
        search = new Search( null, browserConnection, LdapDN.EMPTY_LDAPDN, ISearch.FILTER_TRUE, ROOT_DSE_ATTRIBUTES,
            SearchScope.OBJECT, 0, 0, Connection.AliasDereferencingMethod.NEVER,
            Connection.ReferralHandlingMethod.IGNORE, false, null );
        SearchJob.searchAndUpdateModel( browserConnection, search, monitor );

        // the list of entries under the Root DSE
        Map<LdapDN, IEntry> rootDseEntries = new HashMap<LdapDN, IEntry>();

        // 1st: add base DNs, either the specified or from the namingContexts attribute
        if ( !browserConnection.isFetchBaseDNs() && browserConnection.getBaseDN() != null
            && !"".equals( browserConnection.getBaseDN().toString() ) )
        {
            // only add the specified base DN
            try
            {
                LdapDN dn = browserConnection.getBaseDN();
                IEntry entry = browserConnection.getEntryFromCache( dn );
                if ( entry == null )
                {
                    entry = new BaseDNEntry( new LdapDN( dn ), browserConnection );
                    browserConnection.cacheEntry( entry );
                }
                rootDseEntries.put( dn, entry );
            }
            catch ( InvalidNameException e )
            {
                monitor.reportError( BrowserCoreMessages.model__error_setting_base_dn, e );
            }
        }
        else
        {
            // get base DNs from namingContexts attribute
            Set<String> namingContextSet = new HashSet<String>();
            IAttribute attribute = browserConnection.getRootDSE().getAttribute(
                IRootDSE.ROOTDSE_ATTRIBUTE_NAMINGCONTEXTS );
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
                        LdapDN dn = new LdapDN( namingContext );
                        IEntry entry = browserConnection.getEntryFromCache( dn );
                        if ( entry == null )
                        {
                            entry = new BaseDNEntry( new LdapDN( dn ), browserConnection );
                            browserConnection.cacheEntry( entry );
                        }
                        rootDseEntries.put( dn, entry );
                    }
                    catch ( InvalidNameException e )
                    {
                        monitor.reportError( BrowserCoreMessages.model__error_setting_base_dn, e );
                    }
                }
                else
                {
                    // special handling of empty namingContext (Novell eDirectory): 
                    // perform a one-level search and add all result DNs to the set
                    search = new Search( null, browserConnection, LdapDN.EMPTY_LDAPDN, ISearch.FILTER_TRUE,
                        ISearch.NO_ATTRIBUTES, SearchScope.ONELEVEL, 0, 0, Connection.AliasDereferencingMethod.NEVER,
                        Connection.ReferralHandlingMethod.IGNORE, false, null );
                    SearchJob.searchAndUpdateModel( browserConnection, search, monitor );
                    ISearchResult[] results = search.getSearchResults();
                    for ( ISearchResult searchResult : results )
                    {
                        IEntry entry = searchResult.getEntry();
                        rootDseEntries.put( entry.getDn(), entry );
                    }
                }
            }
        }

        // 2nd: add schema sub-entry
        IEntry[] schemaEntries = getDirectoryMetadataEntries( browserConnection,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY );
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
            initBaseEntry( entry.getBrowserConnection(), entry.getDn(), dummyMonitor );
            // TODO: log if a base entry doesn't exist
        }

        // set flags
        browserConnection.getRootDSE().setHasMoreChildren( false );
        browserConnection.getRootDSE().setAttributesInitialized( true );
        browserConnection.getRootDSE().setChildrenInitialized( true );
        browserConnection.getRootDSE().setHasChildrenHint( true );
        browserConnection.getRootDSE().setDirectoryEntry( true );
    }


    private static void initBaseEntry( IBrowserConnection browserConnection, LdapDN dn, StudioProgressMonitor monitor )
    {
        ISearch search;
        IEntry entry;
        // search the entry
        AliasDereferencingMethod derefAliasMethod = browserConnection.getAliasesDereferencingMethod();
        ReferralHandlingMethod handleReferralsMethod = browserConnection.getReferralsHandlingMethod();
        search = new Search( null, browserConnection, dn, ISearch.FILTER_TRUE, ISearch.NO_ATTRIBUTES,
            SearchScope.OBJECT, 1, 0, derefAliasMethod, handleReferralsMethod, true, null );
        SearchJob.searchAndUpdateModel( browserConnection, search, monitor );

        // add entry to Root DSE
        ISearchResult[] results = search.getSearchResults();
        if ( results != null && results.length == 1 )
        {
            ISearchResult result = results[0];
            entry = result.getEntry();
            browserConnection.getRootDSE().addChild( entry );
        }
    }


    private static IEntry[] getDirectoryMetadataEntries( IBrowserConnection browserConnection,
        String metadataAttributeName )
    {
        List<LdapDN> metadataEntryDnList = new ArrayList<LdapDN>();
        IAttribute attribute = browserConnection.getRootDSE().getAttribute( metadataAttributeName );
        if ( attribute != null )
        {
            String[] values = attribute.getStringValues();
            for ( String dn : values )
            {
                if ( dn != null && !"".equals( dn ) )
                {
                    try
                    {
                        metadataEntryDnList.add( new LdapDN( dn ) );
                    }
                    catch ( InvalidNameException e )
                    {
                    }
                }
            }
        }

        IEntry[] metadataEntries = new IEntry[metadataEntryDnList.size()];
        for ( int i = 0; i < metadataEntryDnList.size(); i++ )
        {
            LdapDN dn = metadataEntryDnList.get( i );
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

}
