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


import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;

import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldifparser.model.LdifEnumeration;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;


/**
 * Runnable to reload the schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReloadSchemaRunnable implements StudioConnectionBulkRunnableWithProgress
{

    /** The browser connection. */
    private IBrowserConnection browserConnection;


    /**
     * Creates a new instance of ReloadSchemaRunnable.
     * 
     * @param browserConnection the browser connection
     */
    public ReloadSchemaRunnable( IBrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return BrowserCoreMessages.jobs__reload_schemas_name_1;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new IBrowserConnection[]
            { browserConnection };
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", 3 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__reload_schemas_task, new String[]
            { browserConnection.getConnection().getName() } ) );
        monitor.worked( 1 );

        // load schema
        monitor.reportProgress( BrowserCoreMessages.model__loading_schema );
        reloadSchema( true, browserConnection, monitor );
        monitor.worked( 1 );
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        BrowserConnectionUpdateEvent browserConnectionUpdateEvent = new BrowserConnectionUpdateEvent(
            browserConnection, BrowserConnectionUpdateEvent.Detail.SCHEMA_UPDATED );
        EventRegistry.fireBrowserConnectionUpdated( browserConnectionUpdateEvent, this );
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__reload_schemas_error_1;
    }


    /**
     * Reloads the schema.
     * 
     * @param forceReload true to force the reload of the schema, otherwise it would only be reloaded
     *                    if the server-side schema is newer than the cached schema.
     * @param browserConnection the browser connection
     * @param monitor the progress monitor
     */
    public static void reloadSchema( boolean forceReload, IBrowserConnection browserConnection,
        StudioProgressMonitor monitor )
    {
        LdapDN schemaLocation = getSchemaLocation( browserConnection, monitor );
        if ( schemaLocation == null )
        {
            monitor.reportError( BrowserCoreMessages.model__missing_schema_location );
            return;
        }

        Schema schema = browserConnection.getSchema();

        boolean mustReload = forceReload || ( schema == Schema.DEFAULT_SCHEMA )
            || mustReload( schemaLocation, browserConnection, monitor );

        if ( mustReload )
        {
            browserConnection.setSchema( Schema.DEFAULT_SCHEMA );

            try
            {
                SearchParameter sp = new SearchParameter();
                sp.setSearchBase( schemaLocation );
                sp.setFilter( Schema.SCHEMA_FILTER );
                sp.setScope( SearchScope.OBJECT );
                sp.setReturningAttributes( new String[]
                    { SchemaConstants.OBJECT_CLASSES_AT, SchemaConstants.ATTRIBUTE_TYPES_AT,
                        SchemaConstants.LDAP_SYNTAXES_AT, SchemaConstants.MATCHING_RULES_AT,
                        SchemaConstants.MATCHING_RULE_USE_AT, SchemaConstants.CREATE_TIMESTAMP_AT,
                        SchemaConstants.MODIFY_TIMESTAMP_AT } );

                LdifEnumeration le = ExportLdifJob.search( browserConnection, sp, monitor );
                if ( le.hasNext() )
                {
                    LdifContentRecord schemaRecord = ( LdifContentRecord ) le.next();
                    schema = new Schema();
                    schema.loadFromRecord( schemaRecord );
                    browserConnection.setSchema( schema );
                }
                else
                {
                    monitor.reportError( BrowserCoreMessages.model__no_schema_information );
                }
            }
            catch ( Exception e )
            {
                monitor.reportError( BrowserCoreMessages.model__error_loading_schema, e );
                e.printStackTrace();
            }
        }
    }


    /**
     * Checks if the schema must be reloaded
     * 
     * @param browserConnection the browser connection
     * @param monitor the progress monitor
     */
    private static boolean mustReload( LdapDN schemaLocation, IBrowserConnection browserConnection,
        StudioProgressMonitor monitor )
    {
        Schema schema = browserConnection.getSchema();

        try
        {
            SearchParameter sp = new SearchParameter();
            sp.setSearchBase( schemaLocation );
            sp.setFilter( Schema.SCHEMA_FILTER );
            sp.setScope( SearchScope.OBJECT );
            sp.setReturningAttributes( new String[]
                { SchemaConstants.CREATE_TIMESTAMP_AT, SchemaConstants.MODIFY_TIMESTAMP_AT } );
            NamingEnumeration<SearchResult> enumeration = SearchRunnable.search( browserConnection, sp, monitor );
            while ( enumeration != null && enumeration.hasMore() )
            {
                String createTimestamp = null;
                String modifyTimestamp = null;

                SearchResult sr = enumeration.next();
                NamingEnumeration<? extends Attribute> attributes = sr.getAttributes().getAll();
                while ( attributes.hasMore() )
                {
                    Attribute attribute = attributes.next();
                    if ( attribute.getID().equalsIgnoreCase( SchemaConstants.MODIFY_TIMESTAMP_AT ) )
                    {
                        modifyTimestamp = ( String ) attribute.get();
                    }
                    if ( attribute.getID().equalsIgnoreCase( SchemaConstants.CREATE_TIMESTAMP_AT ) )
                    {
                        createTimestamp = ( String ) attribute.get();
                    }
                }

                String schemaTimestamp = modifyTimestamp != null ? modifyTimestamp : createTimestamp;
                String cacheTimestamp = schema.getModifyTimestamp() != null ? schema.getModifyTimestamp() : schema
                    .getCreateTimestamp();
                if ( cacheTimestamp != null && schemaTimestamp != null
                    && schemaTimestamp.compareTo( cacheTimestamp ) > 0 )
                {
                    return true;
                }
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( BrowserCoreMessages.model__error_loading_schema, e );
            e.printStackTrace();
        }

        return false;
    }


    private static LdapDN getSchemaLocation( IBrowserConnection browserConnection, StudioProgressMonitor monitor )
    {
        try
        {
            SearchParameter sp = new SearchParameter();
            sp.setSearchBase( new LdapDN() );
            sp.setScope( SearchScope.OBJECT );
            sp.setReturningAttributes( new String[]
                { SchemaConstants.SUBSCHEMA_SUBENTRY_AT } );
            NamingEnumeration<SearchResult> enumeration = SearchRunnable.search( browserConnection, sp, monitor );
            while ( enumeration != null && enumeration.hasMore() )
            {
                SearchResult sr = enumeration.next();
                NamingEnumeration<? extends Attribute> attributes = sr.getAttributes().getAll();
                while ( attributes.hasMore() )
                {
                    Attribute attribute = attributes.next();
                    if ( attribute.getID().equalsIgnoreCase( SchemaConstants.SUBSCHEMA_SUBENTRY_AT ) )
                    {
                        String value = ( String ) attribute.get();
                        if ( LdapDN.isValid( value ) )
                        {
                            LdapDN dn = new LdapDN( value );
                            return dn;
                        }
                    }
                }
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( BrowserCoreMessages.model__error_loading_schema, e );
            return null;
        }

        return null;
    }

}
