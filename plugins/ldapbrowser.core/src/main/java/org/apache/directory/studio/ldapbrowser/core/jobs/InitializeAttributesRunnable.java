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

import org.apache.directory.shared.ldap.model.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.message.SearchScope;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;


/**
 * Runnable to initialize the attributes of an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitializeAttributesRunnable implements StudioConnectionBulkRunnableWithProgress
{
    /** The entries. */
    private IEntry[] entries;


    /**
     * Creates a new instance of InitializeAttributesRunnable.
     * 
     * @param entries the entries
     * @param initOperationalAttributes true if operational attributes should be initialized
     */
    public InitializeAttributesRunnable( IEntry... entries )
    {
        this.entries = entries;
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
                { this.entries[pi].getDn().getName() } ) );
            monitor.worked( 1 );
            if ( entries[pi].getBrowserConnection() != null )
            {
                initializeAttributes( entries[pi], monitor );
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
            if ( entry.getBrowserConnection() != null && entry.isAttributesInitialized() )
            {
                // lookup the entry from cache and fire event with real entry
                if ( entry.getBrowserConnection().getEntryFromCache( entry.getDn() ) != null )
                {
                    entry = entry.getBrowserConnection().getEntryFromCache( entry.getDn() );
                }
                EventRegistry.fireEntryUpdated( new AttributesInitializedEvent( entry ), this );
            }
        }
    }


    /**
     * Initializes the attributes.
     * 
     * @param entry the entry
     * @param monitor the progress monitor
     */
    public static synchronized void initializeAttributes( IEntry entry, StudioProgressMonitor monitor )
    {
        // get user attributes or both user and operational attributes
        String[] returningAttributes = null;
        LinkedHashSet<String> raSet = new LinkedHashSet<String>();
        raSet.add( SchemaConstants.ALL_USER_ATTRIBUTES );
        boolean initOperationalAttributes = entry.getBrowserConnection().isFetchOperationalAttributes()
            || entry.isInitOperationalAttributes();
        if ( initOperationalAttributes )
        {
            if ( entry.getBrowserConnection().getRootDSE().isFeatureSupported(
                SchemaConstants.FEATURE_ALL_OPERATIONAL_ATTRIBUTES ) )
            {
                raSet.add( SchemaConstants.ALL_OPERATIONAL_ATTRIBUTES );
            }
            else
            {
                Collection<AttributeType> opAtds = SchemaUtils.getOperationalAttributeDescriptions( entry
                    .getBrowserConnection().getSchema() );
                Collection<String> atdNames = SchemaUtils.getNames( opAtds );
                raSet.addAll( atdNames );
            }
        }
        if ( entry.isReferral() )
        {
            raSet.add( SchemaConstants.REF_AT );
        }
        returningAttributes = ( String[] ) raSet.toArray( new String[raSet.size()] );

        initializeAttributes( entry, returningAttributes, true, monitor );
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
                { entry.getDn().getName() } ) );

        if ( entry instanceof IRootDSE )
        {
            // special handling for Root DSE
            InitializeRootDSERunnable.loadRootDSE( entry.getBrowserConnection(), monitor );
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

            // create search
            ISearch search = new Search( null, entry.getBrowserConnection(), entry.getDn(),
                entry.isSubentry() ? ISearch.FILTER_SUBENTRY : ISearch.FILTER_TRUE, attributes, SearchScope.OBJECT, 0,
                0, aliasesDereferencingMethod, referralsHandlingMethod, false, null );

            // add controls
            if ( entry.isReferral() )
            {
                search.getControls().add( StudioControl.MANAGEDSAIT_CONTROL );
            }

            // search
            SearchRunnable.searchAndUpdateModel( entry.getBrowserConnection(), search, monitor );

            // we requested all attributes, set initialized state
            entry.setAttributesInitialized( true );
        }
    }
}
