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
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescription;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.jobs.StudioBulkRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionJob;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;


/**
 * Runnable to initialize the attributes of an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class InitializeAttributesRunnable implements StudioBulkRunnableWithProgress
{

    /** The entries. */
    private IEntry[] entries;

    /** The flag if operational attributes should be initialized. */
    private boolean initOperationalAttributes;


    /**
     * Creates a new instance of InitializeAttributesRunnable.
     * 
     * @param entries the entries
     * @param initOperationalAttributes true if operational attributes should be initialized
     */
    public InitializeAttributesRunnable( IEntry[] entries, boolean initOperationalAttributes )
    {
        this.entries = entries;
        this.initOperationalAttributes = initOperationalAttributes;
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
        return BrowserCoreMessages.jobs__init_entries_title_attonly;
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
     * {@inheritDoc}
     */
    public void runNotification()
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
    public static synchronized void initializeAttributes( IEntry entry, boolean initOperationalAttributes,
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

        initializeAttributes( entry, returningAttributes, true, monitor );

        entry.setOperationalAttributesInitialized( initOperationalAttributes );
    }


    /**
     * Initializes the attributes.
     * 
     * @param entry the entry
     * @param attributes the returning attributes
     * @param clearAllAttributes true to clear all old attributes before searching
     * @param monitor the progress monitor
     */
    public static synchronized void initializeAttributes( IEntry entry, String[] attributes,
        boolean clearAllAttributes, StudioProgressMonitor monitor )
    {
        monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_att,
            new String[]
                { entry.getDn().getUpName() } ) );

        if ( entry instanceof IRootDSE )
        {
            // special handling for Root DSE
            InitializeRootDSERunnable runnable = new InitializeRootDSERunnable( ( IRootDSE ) entry );
            StudioConnectionJob job = new StudioConnectionJob( runnable );
            job.execute();
            try
            {
                job.join();
            }
            catch ( InterruptedException e )
            {
            }
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

            if ( clearAllAttributes )
            {
                // Clear all attributes (user and operational)
                // Must be done here because SearchRunnable.searchAndUpdateModel only clears
                // requested attributes. If the user switches the "Show operational attributes"
                // property then the operational attributes are not cleared.
                IAttribute[] oldAttributes = entry.getAttributes();
                if ( oldAttributes != null )
                {
                    for ( IAttribute oldAttribute : oldAttributes )
                    {
                        entry.deleteAttribute( oldAttribute );
                    }
                }
            }

            // search
            ISearch search = new Search( null, entry.getBrowserConnection(), entry.getDn(),
                entry.isSubentry() ? ISearch.FILTER_SUBENTRY : ISearch.FILTER_TRUE, attributes, SearchScope.OBJECT, 0,
                0, aliasesDereferencingMethod, referralsHandlingMethod, false, null );
            SearchRunnable.searchAndUpdateModel( entry.getBrowserConnection(), search, monitor );

            // we requested all attributes, set initialized state
            entry.setAttributesInitialized( true );
        }
    }
}
