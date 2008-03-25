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

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.schema.syntax.AttributeTypeDescription;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReloadSchemasJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchJob;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;


/**
 * The {@link BrowserConnectionListener} opens and closes the 
 * {@link IBrowserConnection} if the underlying {@link Connection}
 * is opened and closed.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
                EventRegistry.suspendEventFireingInCurrentThread();
                openBrowserConnection( browserConnection, monitor );
                setBinaryAttributes( browserConnection, monitor );
            }
            finally
            {
                EventRegistry.resumeEventFireingInCurrentThread();
                BrowserConnectionUpdateEvent browserConnectionUpdateEvent = new BrowserConnectionUpdateEvent(
                    browserConnection, BrowserConnectionUpdateEvent.Detail.BROWSER_CONNECTION_OPENED );
                EventRegistry.fireBrowserConnectionUpdated( browserConnectionUpdateEvent, this );
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
                EventRegistry.suspendEventFireingInCurrentThread();
                browserConnection.clearCaches();
            }
            finally
            {
                EventRegistry.resumeEventFireingInCurrentThread();
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
        IRootDSE rootDSE = browserConnection.getRootDSE();
        InitializeAttributesJob.initializeAttributes( rootDSE, true, monitor );

        // check schema reload
        if ( rootDSE != null )
        {
            try
            {
                monitor.reportProgress( BrowserCoreMessages.model__loading_schema );

                // check if schema is cached
                Schema schema = browserConnection.getSchema();
                if ( schema == Schema.DEFAULT_SCHEMA )
                {
                    ReloadSchemasJob.reloadSchema( browserConnection, monitor );
                }
                else if ( rootDSE.getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY ) != null )
                {
                    // check if schema is up-to-date
                    SearchParameter sp = new SearchParameter();
                    sp.setSearchBase( new LdapDN( rootDSE.getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY )
                        .getStringValue() ) );
                    sp.setFilter( Schema.SCHEMA_FILTER );
                    sp.setScope( SearchScope.OBJECT );
                    sp.setReturningAttributes( new String[]
                        { IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP,
                            IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP, } );
                    ISearch search = new Search( browserConnection, sp );

                    SearchJob.searchAndUpdateModel( browserConnection, search, monitor );
                    ISearchResult[] results = search.getSearchResults();

                    if ( results != null && results.length == 1 )
                    {
                        String schemaTimestamp = results[0]
                            .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP ) != null ? results[0]
                            .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP ).getStringValue() : null;
                        if ( schemaTimestamp == null )
                        {
                            schemaTimestamp = results[0]
                                .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP ) != null ? results[0]
                                .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP ).getStringValue()
                                : null;
                        }
                        String cacheTimestamp = schema.getModifyTimestamp() != null ? schema.getModifyTimestamp()
                            : schema.getCreateTimestamp();
                        if ( cacheTimestamp == null
                            || ( cacheTimestamp != null && schemaTimestamp != null && schemaTimestamp
                                .compareTo( cacheTimestamp ) > 0 ) )
                        {
                            ReloadSchemasJob.reloadSchema( browserConnection, monitor );
                        }
                    }
                    else
                    {
                        browserConnection.setSchema( Schema.DEFAULT_SCHEMA );
                        monitor.reportError( BrowserCoreMessages.model__no_schema_information );
                    }
                }
                else
                {
                    browserConnection.setSchema( Schema.DEFAULT_SCHEMA );
                    monitor.reportError( BrowserCoreMessages.model__missing_schema_location );
                }
            }
            catch ( Exception e )
            {
                browserConnection.setSchema( Schema.DEFAULT_SCHEMA );
                monitor.reportError( BrowserCoreMessages.model__error_loading_schema, e );
                e.printStackTrace();
            }
        }
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
        Collection<AttributeTypeDescription> attributeTypeDescriptions = schema.getAttributeTypeDescriptions();
        for ( AttributeTypeDescription atd : attributeTypeDescriptions )
        {
            if ( SchemaUtils.isBinary( atd, schema ) )
            {
                String name = atd.getNames().isEmpty() ? atd.getNumericOid() : atd.getNames().get( 0 );
                binaryAttributeNames.add( name );
            }
        }

        browserConnection.getConnection().getJNDIConnectionWrapper().setBinaryAttributes( binaryAttributeNames );
    }

}
