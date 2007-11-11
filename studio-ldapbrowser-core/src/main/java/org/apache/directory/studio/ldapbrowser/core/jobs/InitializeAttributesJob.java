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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.AliasDereferencingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BaseDNEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DirectoryMetadataEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeTypeDescription;
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
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDSASLMECHANISM, ISearch.ALL_OPERATIONAL_ATTRIBUTES,
            ISearch.ALL_USER_ATTRIBUTES };


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
            AttributeTypeDescription[] opAtds = SchemaUtils.getOperationalAttributeDescriptions( entry
                .getBrowserConnection().getSchema() );
            String[] attributeTypeDescriptionNames = SchemaUtils.getAttributeTypeDescriptionNames( opAtds );
            raSet.addAll( Arrays.asList( attributeTypeDescriptionNames ) );
            raSet.add( ISearch.ALL_OPERATIONAL_ATTRIBUTES );
        }
//        if ( entry instanceof RootDSE )
//        {
//            raSet.add( ISearch.ALL_USER_ATTRIBUTES );
//            raSet.add( ISearch.ALL_OPERATIONAL_ATTRIBUTES );
//        }
        if ( entry.isReferral() )
        {
            raSet.add( IAttribute.REFERRAL_ATTRIBUTE );
        }
        returningAttributes = ( String[] ) raSet.toArray( new String[raSet.size()] );

        initializeAttributes( entry, returningAttributes, monitor );
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
	        // search
	        ISearch search = new Search( null, entry.getBrowserConnection(), entry.getDn(), entry.isSubentry()?ISearch.FILTER_SUBENTRY:ISearch.FILTER_TRUE, attributes,
	            SearchScope.OBJECT, 0, 0, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE,
	            false, false, null );
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
        for ( int i = 0; oldChildren != null && i < oldChildren.length; i++ )
        {
            if ( oldChildren[i] != null )
            {
                browserConnection.getRootDSE().deleteChild( oldChildren[i] );
            }
        }
        browserConnection.getRootDSE().setChildrenInitialized( false );

        // get well-known root DSE attributes, includes + and *
        ISearch search = new Search( null, browserConnection, LdapDN.EMPTY_LDAPDN, ISearch.FILTER_TRUE,
            InitializeAttributesJob.ROOT_DSE_ATTRIBUTES, SearchScope.OBJECT, 0, 0,
            AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, false, false,
            null );
        SearchJob.searchAndUpdateModel( browserConnection, search, monitor );

        // get base DNs
        if( !browserConnection.isFetchBaseDNs() && browserConnection.getBaseDN() != null && !"".equals( browserConnection.getBaseDN().toString() ))
        {
            try
            {
                // only add the specified base DN
                LdapDN dn = browserConnection.getBaseDN();
                IEntry entry = new BaseDNEntry( new LdapDN( dn ), browserConnection );
                browserConnection.cacheEntry( entry );
                browserConnection.getRootDSE().addChild( entry );
                
                // check if entry exists
                // TODO: use browserConnection.getEntry( dn, monitor ) ??
                search = new Search( null, browserConnection, dn, ISearch.FILTER_TRUE, ISearch.NO_ATTRIBUTES, SearchScope.OBJECT, 1, 0,
                    AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, true, true, null );
                SearchJob.searchAndUpdateModel( browserConnection, search, monitor );
            }
            catch ( InvalidNameException e )
            {
                monitor.reportError( BrowserCoreMessages.model__error_setting_base_dn, e );
            }
        }
        else
        {
            // get naming contexts 
            Set<String> namingContextSet = new HashSet<String>();
            IAttribute attribute = browserConnection.getRootDSE().getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_NAMINGCONTEXTS );
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
                        IEntry entry = new BaseDNEntry( new LdapDN( namingContext ), browserConnection );
                        browserConnection.getRootDSE().addChild( entry );
                        browserConnection.cacheEntry( entry );
                    }
                    catch ( InvalidNameException e )
                    {
                        monitor.reportError( BrowserCoreMessages.model__error_setting_base_dn, e );
                    }
                }
                else
                {
                    // special handling of empty namingContext: perform a one-level search and add all result DNs to the set
                    search = new Search( null, browserConnection, LdapDN.EMPTY_LDAPDN, ISearch.FILTER_TRUE, ISearch.NO_ATTRIBUTES, SearchScope.ONELEVEL, 0,
                        0, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, false, false, null );
                    SearchJob.searchAndUpdateModel( browserConnection, search, monitor );
                    ISearchResult[] results = search.getSearchResults();
                    for ( int k = 0; results != null && k < results.length; k++ )
                    {
                        ISearchResult result = results[k];
                        IEntry entry = result.getEntry();
                        browserConnection.getRootDSE().addChild( entry );
                    }
                }
            }
        }

        // get schema sub-entry
        DirectoryMetadataEntry[] schemaEntries = getDirectoryMetadataEntries( browserConnection, IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY );
        for ( int i = 0; i < schemaEntries.length; i++ )
        {
            schemaEntries[i].setSchemaEntry( true );
            browserConnection.getRootDSE().addChild( ( IEntry ) schemaEntries[i] );
        }
        
        // get other metadata entries
        // TODO: check all attributes if they are valid DNs
        String[] metadataAttributeNames = new String[]
            { IRootDSE.ROOTDSE_ATTRIBUTE_MONITORCONTEXT, IRootDSE.ROOTDSE_ATTRIBUTE_CONFIGCONTEXT,
                IRootDSE.ROOTDSE_ATTRIBUTE_DSANAME };
        for ( int x = 0; x < metadataAttributeNames.length; x++ )
        {
            DirectoryMetadataEntry[] metadataEntries = getDirectoryMetadataEntries( browserConnection, metadataAttributeNames[x] );
            for ( int i = 0; i < metadataEntries.length; i++ )
            {
                browserConnection.getRootDSE().addChild( ( IEntry ) metadataEntries[i] );
            }
        }
        
        // set flags
        browserConnection.getRootDSE().setHasMoreChildren( false );
        browserConnection.getRootDSE().setAttributesInitialized( true );
        browserConnection.getRootDSE().setChildrenInitialized( true );
        browserConnection.getRootDSE().setHasChildrenHint( true );
        browserConnection.getRootDSE().setDirectoryEntry( true );
    }


    private static DirectoryMetadataEntry[] getDirectoryMetadataEntries( IBrowserConnection browserConnection, String metadataAttributeName )
    {
        List<LdapDN> metadataEntryList = new ArrayList<LdapDN>();
        IAttribute attribute = browserConnection.getRootDSE().getAttribute( metadataAttributeName );
        if ( attribute != null )
        {
            String[] values = attribute.getStringValues();
            for ( int i = 0; i < values.length; i++ )
            {
                try
                {
                    metadataEntryList.add( new LdapDN( values[i] ) );
                }
                catch ( InvalidNameException e )
                {
                }
            }
        }

        DirectoryMetadataEntry[] metadataEntries = new DirectoryMetadataEntry[metadataEntryList.size()];
        for ( int i = 0; i < metadataEntryList.size(); i++ )
        {
            metadataEntries[i] = new DirectoryMetadataEntry( metadataEntryList.get( i ), browserConnection );
            metadataEntries[i].setDirectoryEntry( true );
            browserConnection.cacheEntry( metadataEntries[i] );
        }
        return metadataEntries;
    }
}
