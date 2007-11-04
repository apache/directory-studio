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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob;
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
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;


/**
 * Job to open the browser connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenBrowserConnectionsJob extends AbstractNotificationJob
{

    /** The browser connection. */
    private BrowserConnection browserConnection;


    /**
     * Creates a new instance of OpenBrowserConnectionsJob.
     * 
     * @param browserConnection the browser connection
     */
    public OpenBrowserConnectionsJob( BrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;
        setName( BrowserCoreMessages.jobs__open_connections_name_1 );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[0];
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        return new Object[]
            { browserConnection };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__open_connections_error_1;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", 1 * 6 + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__open_connections_task, new String[]
            { this.browserConnection.getConnection().getName() } ) );
        monitor.worked( 1 );

        openBrowserConnection( browserConnection, monitor );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
    }


    /**
     * Opens the browser connection.
     * 
     * @param browserConnection the browser connection
     * @param monitor the progress monitor
     */
    static void openBrowserConnection( IBrowserConnection browserConnection, StudioProgressMonitor monitor )
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
                return;
            }
        }
    }
}
