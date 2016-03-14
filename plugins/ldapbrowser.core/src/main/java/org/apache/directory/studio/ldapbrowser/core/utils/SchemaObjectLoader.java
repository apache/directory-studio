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
package org.apache.directory.studio.ldapbrowser.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;

/**
 * A class exposing some common methods on the schema.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaObjectLoader
{
    /** The browser connection */
    private IBrowserConnection browserConnection;

    /** The array of attributes names and OIDs */
    private String[] attributeNamesAndOids;

    /** The array of ObjectClasses and OIDs */
    private String[] objectClassesAndOids;

    /**
     * An interface to allow the getSchemaObjectNamesAndOid() to be called for any schema object
     */
    private interface SchemaAdder 
    {
        /**
         * Adds the schema object names and OIDs to the given set.
         *
         * @param schema the schema
         * @param schemaObjectNamesList the schema object names list
         * @param oidsList the OIDs name list
         */
        void add( Schema schema, List<String> schemaObjectNamesList, List<String> oidsList );
    }
    
    
    /**
     * Gets the array containing the objectClass names and OIDs.
     *
     * @return the array containing the objectClass names and OIDs
     */
    public String[] getObjectClassNamesAndOids()
    {
        objectClassesAndOids = getSchemaObjectsAnddOids( objectClassesAndOids, new SchemaAdder()
        {
            @Override
            public void add( Schema schema, List<String> objectClassNamesList, List<String> oidsList )
            {
                if ( schema != null )
                {
                    for ( ObjectClass ocd : schema.getObjectClassDescriptions() )
                    {
                        // OID
                        if ( !oidsList.contains( ocd.getOid() ) )
                        {
                            oidsList.add( ocd.getOid() );
                        }

                        // Names
                        for ( String name : ocd.getNames() )
                        {
                            if ( !objectClassNamesList.contains( name ) )
                            {
                                objectClassNamesList.add( name );
                            }
                        }
                    }
                }
            }
        });
        
        return objectClassesAndOids;
    }

    /**
     * Gets the array containing the attribute names and OIDs.
     *
     * @return the array containing the attribute names and OIDs
     */
    public String[] getAttributeNamesAndOids()
    {
        attributeNamesAndOids = getSchemaObjectsAnddOids( attributeNamesAndOids, new SchemaAdder()
        {
            @Override
            public void add( Schema schema, List<String> attributeNamesList, List<String> oidsList )
            {
                if ( schema != null )
                {
                    for ( AttributeType atd : schema.getAttributeTypeDescriptions() )
                    {
                        // OID
                        if ( !oidsList.contains( atd.getOid() ) )
                        {
                            oidsList.add( atd.getOid() );
                        }

                        // Names
                        for ( String name : atd.getNames() )
                        {
                            if ( !attributeNamesList.contains( name ) )
                            {
                                attributeNamesList.add( name );
                            }
                        }
                    }
                }
            }
        });
        
        return attributeNamesAndOids;
    }

    
    /**
     * Gets the array containing the schemaObjects and OIDs.
     *
     * @return the array containing the Schema objects and OIDs
     */
    private String[] getSchemaObjectsAnddOids( String[] schemaObjects, SchemaAdder schemaAdder )
    {
        // Checking if the array has already be generated
        if ( ( schemaObjects == null ) || ( schemaObjects.length == 0 ) )
        {
            List<String> schemaObjectNamesList = new ArrayList<String>();
            List<String> oidsList = new ArrayList<String>();

            if ( browserConnection == null )
            {
                // Getting all connections in the case where no connection is found
                IBrowserConnection[] connections = BrowserCorePlugin.getDefault().getConnectionManager()
                    .getBrowserConnections();
                
                for ( IBrowserConnection connection : connections )
                {
                    schemaAdder.add( connection.getSchema(), schemaObjectNamesList, oidsList );
                }
            }
            else
            {
                // Only adding schema object names and OIDs from the associated connection
                schemaAdder.add( browserConnection.getSchema(), schemaObjectNamesList, oidsList );
            }

            // Also adding schemaObject names and OIDs from the default schema
            schemaAdder.add( Schema.DEFAULT_SCHEMA, schemaObjectNamesList, oidsList );

            // Sorting the set
            Collections.sort( schemaObjectNamesList );
            Collections.sort( oidsList );

            schemaObjects = new String[schemaObjectNamesList.size() + oidsList.size()];
            System.arraycopy( schemaObjectNamesList.toArray(), 0, schemaObjects, 0, schemaObjectNamesList
                .size() );
            System.arraycopy( oidsList.toArray(), 0, schemaObjects, schemaObjectNamesList
                .size(), oidsList.size() );
        }

        return schemaObjects;
    }
}
