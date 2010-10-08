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

package org.apache.directory.studio.ldapbrowser.core;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescriptionSchemaParser;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReloadSchemaRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;


/**
 * The {@link BrowserConnectionListener} opens and closes the 
 * {@link IBrowserConnection} if the underlying {@link Connection}
 * is opened and closed.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserConnectionListener implements IConnectionListener
{

    /**
     * This implementation opens the browser connection when the connection was opened.
     */
    public void connectionOpened( Connection connection, StudioProgressMonitor monitor )
    {
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( connection );
        if ( browserConnection != null )
        {
            try
            {
                EventRegistry.suspendEventFiringInCurrentThread();
                openBrowserConnection( browserConnection, monitor );
                setBinaryAttributes( browserConnection, monitor );
            }
            finally
            {
                EventRegistry.resumeEventFiringInCurrentThread();
                BrowserConnectionUpdateEvent browserConnectionUpdateEvent = new BrowserConnectionUpdateEvent(
                    browserConnection, BrowserConnectionUpdateEvent.Detail.BROWSER_CONNECTION_OPENED );
                EventRegistry.fireBrowserConnectionUpdated( browserConnectionUpdateEvent, this );
                BrowserConnectionUpdateEvent schemaUpdateEvent = new BrowserConnectionUpdateEvent( browserConnection,
                    BrowserConnectionUpdateEvent.Detail.SCHEMA_UPDATED );
                EventRegistry.fireBrowserConnectionUpdated( schemaUpdateEvent, this );
            }
        }
    }


    /**
     * This implementation closes the browser connection when the connection was closed.
     */
    public void connectionClosed( Connection connection, StudioProgressMonitor monitor )
    {
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( connection );
        if ( browserConnection != null )
        {
            try
            {
                EventRegistry.suspendEventFiringInCurrentThread();
                browserConnection.clearCaches();
            }
            finally
            {
                EventRegistry.resumeEventFiringInCurrentThread();
                BrowserConnectionUpdateEvent browserConnectionUpdateEvent = new BrowserConnectionUpdateEvent(
                    browserConnection, BrowserConnectionUpdateEvent.Detail.BROWSER_CONNECTION_CLOSED );
                EventRegistry.fireBrowserConnectionUpdated( browserConnectionUpdateEvent, this );
            }
        }
    }


    /**
     * Opens the browser connection.
     * 
     * @param browserConnection the browser connection
     * @param monitor the progress monitor
     */
    private static void openBrowserConnection( IBrowserConnection browserConnection, StudioProgressMonitor monitor )
    {
        ReloadSchemaRunnable.reloadSchema( false, browserConnection, monitor );

        IRootDSE rootDSE = browserConnection.getRootDSE();
        InitializeAttributesRunnable.initializeAttributes( rootDSE, monitor );
    }


    /**
     * Extracts the binary attributes from the schema and 
     * sets the binary attributes to the underlying connection.
     * 
     * @param browserConnection the browser connection
     * @param monitor the progress monitor
     */
    private static void setBinaryAttributes( IBrowserConnection browserConnection, StudioProgressMonitor monitor )
    {
        List<String> binaryAttributeNames = new ArrayList<String>();

        Schema schema = browserConnection.getSchema();
        Collection<AttributeType> attributeTypeDescriptions = schema.getAttributeTypeDescriptions();
        for ( AttributeType atd : attributeTypeDescriptions )
        {
            if ( SchemaUtils.isBinary( atd, schema ) )
            {
                String name = atd.getNames().isEmpty() ? atd.getOid() : atd.getNames().get( 0 );
                binaryAttributeNames.add( name );
            }
        }

        if ( browserConnection.getConnection() != null )
        {
            browserConnection.getConnection().getJNDIConnectionWrapper().setBinaryAttributes( binaryAttributeNames );
        }
    }

}
