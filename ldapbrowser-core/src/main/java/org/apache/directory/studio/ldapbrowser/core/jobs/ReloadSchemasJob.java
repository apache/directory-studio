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


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldifparser.model.LdifEnumeration;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;


/**
 * Job to reload the schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReloadSchemasJob extends AbstractNotificationJob
{

    /** The browser connection. */
    private IBrowserConnection browserConnection;


    /**
     * Creates a new instance of ReloadSchemasJob.
     * 
     * @param browserConnection the browser connection
     */
    public ReloadSchemasJob( IBrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;
        setName( BrowserCoreMessages.jobs__reload_schemas_name_1 );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        return new IBrowserConnection[]
            { browserConnection };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", 3 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__reload_schemas_task, new String[]
            { browserConnection.getConnection().getName() } ) );
        monitor.worked( 1 );

        // load root DSE
        monitor.reportProgress( BrowserCoreMessages.model__loading_rootdse );
        InitializeAttributesJob.initializeAttributes( browserConnection.getRootDSE(), true, monitor );
        monitor.worked( 1 );
        
        // load schema
        monitor.reportProgress( BrowserCoreMessages.model__loading_schema );
        reloadSchema( browserConnection, monitor );
        monitor.worked( 1 );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        BrowserConnectionUpdateEvent browserConnectionUpdateEvent = new BrowserConnectionUpdateEvent(
            browserConnection, BrowserConnectionUpdateEvent.Detail.SCHEMA_UPDATED );
        EventRegistry.fireBrowserConnectionUpdated( browserConnectionUpdateEvent, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__reload_schemas_error_1;
    }
    

    /**
     * Reloads the schema.
     * 
     * @param browserConnection the browser connection
     * @param monitor the progress monitor
     */
    public static void reloadSchema( IBrowserConnection browserConnection, StudioProgressMonitor monitor )
    {
        browserConnection.setSchema( Schema.DEFAULT_SCHEMA );

        try
        {
            if ( browserConnection.getRootDSE().getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY ) != null )
            {
                SearchParameter sp = new SearchParameter();
                sp.setSearchBase( new LdapDN( browserConnection.getRootDSE().getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY )
                    .getStringValue() ) );
                sp.setFilter( Schema.SCHEMA_FILTER );
                sp.setScope( SearchScope.OBJECT );
                sp.setReturningAttributes( new String[]
                    { Schema.SCHEMA_ATTRIBUTE_OBJECTCLASSES, Schema.SCHEMA_ATTRIBUTE_ATTRIBUTETYPES,
                        Schema.SCHEMA_ATTRIBUTE_LDAPSYNTAXES, Schema.SCHEMA_ATTRIBUTE_MATCHINGRULES,
                        Schema.SCHEMA_ATTRIBUTE_MATCHINGRULEUSE, IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP,
                        IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP, } );
                
                LdifEnumeration le = ExportLdifJob.search( browserConnection, sp, monitor );
                if ( le.hasNext() )
                {
                    LdifContentRecord schemaRecord = ( LdifContentRecord ) le.next();
                    Schema schema = new Schema();
                    schema.loadFromRecord( schemaRecord );
                    browserConnection.setSchema( schema );
                    // TODO: Schema update event
//                    EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( this,
//                        ConnectionUpdateEvent.EventDetail.SCHEMA_LOADED ), this );
                }
                else
                {
                    monitor.reportError( BrowserCoreMessages.model__no_schema_information );
                }
            }
            else
            {
                monitor.reportError( BrowserCoreMessages.model__missing_schema_location );
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( BrowserCoreMessages.model__error_loading_schema, e );
            e.printStackTrace();
        }
    }
}
