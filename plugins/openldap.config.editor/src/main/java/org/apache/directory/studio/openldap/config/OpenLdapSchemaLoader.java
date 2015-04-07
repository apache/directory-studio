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
package org.apache.directory.studio.openldap.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.schema.registries.Schema;
import org.apache.directory.api.ldap.model.schema.registries.DefaultSchema;
import org.apache.directory.api.ldap.schema.loader.JarLdifSchemaLoader;

/**
 * This class implements a {@link SchemaLoader} based on the OpenLDAP schema bundled
 * with the class, as well as a set of low level base schemas from ApacheDS.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapSchemaLoader extends JarLdifSchemaLoader
{
    /** The name of the OpenLDAP Config schema */
    public static final String OPENLDAPCONFIG_SCHEMA_NAME = "openldapconfig";

    /** The name of the LDIF file for the OpenLDAP Config schema */
    private static final String OPENLDAPCONFIG_SHEMA_LDIF = "openldapconfig.ldif";

    /** The attribute type pattern */
    private static final Pattern ATTRIBUTE_TYPE_PATTERN = Pattern.compile( "m-oid\\s*=\\s*[0-9\\.]*\\s*"
        + ",\\s*ou\\s*=\\s*attributetypes\\s*,\\s*cn\\s*=\\s*" + OPENLDAPCONFIG_SCHEMA_NAME + "\\s*,\\s*ou=schema\\s*",
        Pattern.CASE_INSENSITIVE );

    /** The object class pattern */
    private static final Pattern OBJECT_CLASS_PATTERN = Pattern.compile( "m-oid\\s*=\\s*[0-9\\.]*\\s*"
        + ",\\s*ou\\s*=\\s*objectclasses\\s*,\\s*cn\\s*=\\s*" + OPENLDAPCONFIG_SCHEMA_NAME + "\\s*,\\s*ou=schema\\s*",
        Pattern.CASE_INSENSITIVE );

    /** The attribute types entries */
    private List<Entry> attributeTypesEntries = new ArrayList<Entry>();

    /** The object classes entries */
    private List<Entry> objectClassesEntries = new ArrayList<Entry>();


    /**
     * Creates a new instance of ConnectionSchemaLoader.
     *
     * @throws Exception
     */
    public OpenLdapSchemaLoader() throws Exception
    {
        super();
        initializeSchema();
        initializeSchemaObjects();
    }


    /**
     * Initializes the schema. We will load 'system', 'core' and 'apache'.
     */
    private void initializeSchema()
    {
        Schema schema = new DefaultSchema( OPENLDAPCONFIG_SCHEMA_NAME );
        schema.addDependencies( new String[]
            { "system", "core", "apache" } );
        schemaMap.put( schema.getSchemaName(), schema );
    }


    /**
     * Initializes the schema objects.
     *
     * @throws Exception
     */
    private void initializeSchemaObjects() throws Exception
    {
        LdifReader ldifReader = null;

        try
        {
            // Reading the schema file
            ldifReader = new LdifReader( OpenLdapSchemaLoader.class.getResourceAsStream( OPENLDAPCONFIG_SHEMA_LDIF ) );

            // Looping on all entries
            while ( ldifReader.hasNext() )
            {
                // Getting the LDIF entry and DN
                Entry entry = ldifReader.next().getEntry();
                String dn = entry.getDn().getName();

                // Checking if the entry is an attribute type
                if ( ATTRIBUTE_TYPE_PATTERN.matcher( dn ).matches() )
                {
                    attributeTypesEntries.add( entry );
                }
                // Checking if the entry is an object class
                else if ( OBJECT_CLASS_PATTERN.matcher( dn ).matches() )
                {
                    objectClassesEntries.add( entry );
                }
            }
        }
        finally
        {
            // Closing the LDIF reader
            if ( ldifReader != null )
            {
                ldifReader.close();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadAttributeTypes( Schema... schemas ) throws LdapException, IOException
    {
        // Getting the attribute types from the supertype implementation
        List<Entry> attributeTypes = super.loadAttributeTypes( schemas );

        for ( Schema schema : schemas )
        {
            // Checking if this is the OpenLDAP schema
            if ( OPENLDAPCONFIG_SCHEMA_NAME.equals( schema.getSchemaName() ) )
            {
                // Add all attribute types
                attributeTypes.addAll( attributeTypesEntries );
            }
        }

        return attributeTypes;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadObjectClasses( Schema... schemas ) throws LdapException, IOException
    {
        // Getting the object classes from the supertype implementation
        List<Entry> objectClasses = super.loadObjectClasses( schemas );

        for ( Schema schema : schemas )
        {
            // Checking if this is the OpenLDAP schema
            if ( OPENLDAPCONFIG_SCHEMA_NAME.equals( schema.getSchemaName() ) )
            {
                // Add all object classes
                objectClasses.addAll( objectClassesEntries );
            }
        }

        return objectClasses;
    }
}
