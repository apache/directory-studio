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
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.internal.model.RootDSE;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Search;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;


public class InitializeAttributesJob extends AbstractNotificationJob
{

    private IEntry[] entries;

    private boolean initOperationalAttributes;


    public InitializeAttributesJob( IEntry[] entries, boolean initOperationalAttributes )
    {
        this.entries = entries;
        this.initOperationalAttributes = initOperationalAttributes;
        setName( BrowserCoreMessages.jobs__init_entries_title_attonly );
    }


    protected Connection[] getConnections()
    {
        Connection[] connections = new Connection[entries.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = entries[i].getBrowserConnection().getConnection();
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


    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", entries.length + 2 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int pi = 0; pi < entries.length && !monitor.isCanceled(); pi++ )
        {
            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_task, new String[]
                { this.entries[pi].getDn().toString() } ) );
            monitor.worked( 1 );
            if ( entries[pi].getBrowserConnection() != null && entries[pi].isDirectoryEntry() )
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
            if ( parent.getBrowserConnection() != null && parent.isDirectoryEntry() )
            {
                EventRegistry.fireEntryUpdated( new AttributesInitializedEvent( parent ), this );
            }
        }
    }


    public static void initializeAttributes( IEntry entry, boolean initOperationalAttributes,
        StudioProgressMonitor monitor )
    {

        // get user attributes or both user and operational attributes
        String[] returningAttributes = null;
        LinkedHashSet raSet = new LinkedHashSet();
        raSet.add( ISearch.ALL_USER_ATTRIBUTES );
        if ( initOperationalAttributes )
        {
            AttributeTypeDescription[] opAtds = SchemaUtils.getOperationalAttributeDescriptions( entry.getBrowserConnection()
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


    public static void initializeAttributes( IEntry entry, String[] attributes, StudioProgressMonitor monitor )
    {

        monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_att,
            new String[]
                { entry.getDn().toString() } ) );

        // entry.setAttributesInitialized(false, entry.getConnection());

        if ( entry instanceof IRootDSE )
        {
            IEntry[] oldChildren = entry.getChildren();
            for ( int i = 0; oldChildren != null && i < oldChildren.length; i++ )
            {
                if ( oldChildren[i] != null )
                {
                    entry.deleteChild( oldChildren[i] );
                }
            }
            entry.setChildrenInitialized( false );
            
            // special handling for Root DSE
        	entry.getBrowserConnection().fetchRootDSE( monitor );
        	entry.setAttributesInitialized( true );
        	entry.setChildrenInitialized( true );
        }
        else
        {
	        // search
	        ISearch search = new Search( null, entry.getBrowserConnection(), entry.getDn(), entry.isSubentry()?ISearch.FILTER_SUBENTRY:ISearch.FILTER_TRUE, attributes,
	            ISearch.SCOPE_OBJECT, 0, 0, IBrowserConnection.DEREFERENCE_ALIASES_NEVER, IBrowserConnection.HANDLE_REFERRALS_IGNORE,
	            false, false, null );
	        entry.getBrowserConnection().search( search, monitor );
	
	        // set initialized state
	        entry.setAttributesInitialized( true );
        }
    }

}
