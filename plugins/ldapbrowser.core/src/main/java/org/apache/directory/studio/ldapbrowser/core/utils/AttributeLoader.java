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
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;

/**
 * A class exposing some common methods on Attributes.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeLoader
{
    /** The browser connection */
    private IBrowserConnection browserConnection;

    /** The array of attributes names and OIDs */
    private String[] attributeNamesAndOids;

    /**
     * Gets the array containing the attribute names and OIDs.
     *
     * @return the array containing the attribute names and OIDs
     */
    public String[] getAttributeNamesAndOids()
    {
        // Checking if the array has already be generated
        if ( ( attributeNamesAndOids == null ) || ( attributeNamesAndOids.length == 0 ) )
        {
            List<String> attributeNamesList = new ArrayList<String>();
            List<String> oidsList = new ArrayList<String>();

            if ( browserConnection == null )
            {
                // Getting all connections in the case where no connection is found
                IBrowserConnection[] connections = BrowserCorePlugin.getDefault().getConnectionManager()
                    .getBrowserConnections();
                
                for ( IBrowserConnection connection : connections )
                {
                    addAttributeNamesAndOids( connection.getSchema(), attributeNamesList, oidsList );
                }
            }
            else
            {
                // Only adding attribute names and OIDs from the associated connection
                addAttributeNamesAndOids( browserConnection.getSchema(), attributeNamesList, oidsList );
            }

            // Also adding attribute names and OIDs from the default schema
            addAttributeNamesAndOids( Schema.DEFAULT_SCHEMA, attributeNamesList, oidsList );

            // Sorting the set
            Collections.sort( attributeNamesList );
            Collections.sort( oidsList );

            attributeNamesAndOids = new String[attributeNamesList.size() + oidsList.size()];
            System.arraycopy( attributeNamesList.toArray(), 0, attributeNamesAndOids, 0, attributeNamesList
                .size() );
            System.arraycopy( oidsList.toArray(), 0, attributeNamesAndOids, attributeNamesList
                .size(), oidsList.size() );
        }

        return attributeNamesAndOids;
    }


    /**
     * Adds the attribute names and OIDs to the given set.
     *
     * @param schema the schema
     * @param attributeNamesList the attribute names list
     * @param oidsList the OIDs name list
     */
    private void addAttributeNamesAndOids( Schema schema, List<String> attributeNamesList, List<String> oidsList )
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
}
