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

package org.apache.directory.ldapstudio.browser.core.jobs;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.AttributesInitializedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.RootDSE;
import org.apache.directory.ldapstudio.browser.core.internal.model.Search;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.SchemaUtils;


public class InitializeAttributesJob extends AbstractAsyncBulkJob
{

    private IEntry[] entries;

    private boolean initOperationalAttributes;


    public InitializeAttributesJob( IEntry[] entries, boolean initOperationalAttributes )
    {
        this.entries = entries;
        this.initOperationalAttributes = initOperationalAttributes;
        setName( BrowserCoreMessages.jobs__init_entries_title_attonly );
    }


    protected IConnection[] getConnections()
    {
        IConnection[] connections = new IConnection[entries.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = entries[i].getConnection();
        }
        return connections;
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.addAll( Arrays.asList( entries ) );
        return l.toArray();
    }


    protected String getErrorMessage()
    {
        return entries.length == 1 ? BrowserCoreMessages.jobs__init_entries_error_1
            : BrowserCoreMessages.jobs__init_entries_error_n;
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {
        monitor.beginTask( " ", entries.length + 2 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int pi = 0; pi < entries.length && !monitor.isCanceled(); pi++ )
        {
            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_task, new String[]
                { this.entries[pi].getDn().toString() } ) );
            monitor.worked( 1 );
            if ( entries[pi].getConnection() != null && entries[pi].getConnection().isOpened()
                && entries[pi].isDirectoryEntry() )
            {
                initializeAttributes( entries[pi], initOperationalAttributes, monitor );
            }
        }
    }


    protected void runNotification()
    {
        for ( int pi = 0; pi < entries.length; pi++ )
        {
            IEntry parent = entries[pi];
            if ( parent.getConnection() != null && entries[pi].getConnection().isOpened() && parent.isDirectoryEntry() )
            {
                EventRegistry.fireEntryUpdated( new AttributesInitializedEvent( parent ), this );
            }
        }
    }


    public static void initializeAttributes( IEntry entry, boolean initOperationalAttributes,
        ExtendedProgressMonitor monitor )
    {

        // get user attributes or both user and operational attributes
        String[] returningAttributes = null;
        LinkedHashSet raSet = new LinkedHashSet();
        raSet.add( ISearch.ALL_USER_ATTRIBUTES );
        if ( initOperationalAttributes )
        {
            AttributeTypeDescription[] opAtds = SchemaUtils.getOperationalAttributeDescriptions( entry.getConnection()
                .getSchema() );
            String[] attributeTypeDescriptionNames = SchemaUtils.getAttributeTypeDescriptionNames( opAtds );
            raSet.addAll( Arrays.asList( attributeTypeDescriptionNames ) );
            raSet.add( ISearch.ALL_OPERATIONAL_ATTRIBUTES );
        }
        if ( entry instanceof RootDSE )
        {
            raSet.add( ISearch.ALL_USER_ATTRIBUTES );
            raSet.add( ISearch.ALL_OPERATIONAL_ATTRIBUTES );
        }
        if ( entry.isReferral() )
        {
            raSet.add( IAttribute.REFERRAL_ATTRIBUTE );
        }
        returningAttributes = ( String[] ) raSet.toArray( new String[raSet.size()] );

        initializeAttributes( entry, returningAttributes, monitor );
    }


    public static void initializeAttributes( IEntry entry, String[] attributes, ExtendedProgressMonitor monitor )
    {

        monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_att,
            new String[]
                { entry.getDn().toString() } ) );

        // entry.setAttributesInitialized(false, entry.getConnection());

        // search
        ISearch search = new Search( null, entry.getConnection(), entry.getDn(), ISearch.FILTER_TRUE, attributes,
            ISearch.SCOPE_OBJECT, 0, 0, IConnection.DEREFERENCE_ALIASES_NEVER, IConnection.HANDLE_REFERRALS_IGNORE,
            false, false, null );
        entry.getConnection().search( search, monitor );

        // set initialized state
        entry.setAttributesInitialized( true );
    }

}
