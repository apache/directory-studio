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
package org.apache.directory.studio.schemaeditor.model.io;


import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

import org.apache.directory.shared.ldap.message.AttributeImpl;
import org.apache.directory.shared.ldap.message.AttributesImpl;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.io.jndi.JNDIConnectionWrapper;
import org.apache.directory.studio.schemaeditor.model.DependenciesComputer;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * This class is used to import the schema from Apache Directory Server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ApacheDSSchemaExporter
{
    private static final String SCHEMA_BASE_DN = "dc=example,dc=com";

    /** The Dependencies Computer */
    private DependenciesComputer dependenciesComputer;

    /** The JNDI Connection wrapper */
    private JNDIConnectionWrapper wrapper;

    /** The Progress Monitor */
    private StudioProgressMonitor monitor;

    /** The controls used in the request */
    private Control[] controls = new Control[0];


    /**
     * Export the Schema to the given Apache Directory Server JNDIConnectionWrapper.
     * @param dependenciesComputer 
     * 
     * @param  wrapper
     *      the JNDIConnectionWrapper
     * @param monitor 
     * @throws NamingException 
     */
    public void exportSchema( List<Schema> schemas, DependenciesComputer dependenciesComputer,
        JNDIConnectionWrapper wrapper, StudioProgressMonitor monitor ) throws NamingException
    {
        this.dependenciesComputer = dependenciesComputer;
        this.wrapper = wrapper;
        this.monitor = monitor;

        monitor.beginTask( "Committing changes:", schemas.size() );

        for ( Schema schema : schemas )
        {
            monitor.subTask( "Committing schema '" + schema.getName() + "'" );

            createSchemaEntries( schema );

            monitor.worked( 1 );
        }

        monitor.done();
    }


    /**
     * Creates the schema entry WITH all its child nodes (including
     * attribute types, object classes, etc.).
     *
     * @param schema
     *      the schema
     */
    private void createSchemaEntries( Schema schema )
    {
        // Creating the schema entry
        createSchemaEntry( schema );

        // Creating the schema sub-entries
        String schemaEntryDN = "cn=" + schema.getName() + "," + SCHEMA_BASE_DN;
        createOrganizationalUnitEntry( "ou=attributeTypes," + schemaEntryDN, "attributeTypes" );
        createOrganizationalUnitEntry( "ou=comparators," + schemaEntryDN, "comparators" );
        createOrganizationalUnitEntry( "ou=ditContentRules," + schemaEntryDN, "ditContentRules" );
        createOrganizationalUnitEntry( "ou=ditStructureRules," + schemaEntryDN, "ditStructureRules" );
        createOrganizationalUnitEntry( "ou=matchingRules," + schemaEntryDN, "matchingRules" );
        createOrganizationalUnitEntry( "ou=matchingRuleUse," + schemaEntryDN, "matchingRuleUse" );
        createOrganizationalUnitEntry( "ou=nameForms," + schemaEntryDN, "nameForms" );
        createOrganizationalUnitEntry( "ou=normalizers," + schemaEntryDN, "normalizers" );
        createOrganizationalUnitEntry( "ou=objectClasses," + schemaEntryDN, "objectClasses" );
        createOrganizationalUnitEntry( "ou=syntaxCheckers," + schemaEntryDN, "syntaxCheckers" );
        createOrganizationalUnitEntry( "ou=syntaxes," + schemaEntryDN, "syntaxes" );
    }


    /**
     * Creates the schema entry WITHOUT all its child nodes .
     *
     * @param schema
     *      the schema
     */
    private void createSchemaEntry( Schema schema )
    {
        // Attribute 'objectClass'
        Attribute objectClassAttribute = new AttributeImpl( "objectClass" );
        objectClassAttribute.add( "top" );
        objectClassAttribute.add( "metaSchema" );

        // Attribute 'cn'
        Attribute cnAttribute = new AttributeImpl( "cn" );
        cnAttribute.add( schema.getName() );

        // Attribute 'm-dependencies'
        Attribute dependenciesAttribute = null;
        List<Schema> dependencies = dependenciesComputer.getDependencies( schema );
        if ( ( dependencies != null ) && ( dependencies.size() > 0 ) )
        {
            dependenciesAttribute = new AttributeImpl( "m-dependencies" );
            for ( Schema dependency : dependencies )
            {
                dependenciesAttribute.add( dependency.getName() );
            }
        }

        // Building attributes
        Attributes attributes = new AttributesImpl();
        attributes.put( objectClassAttribute );
        attributes.put( cnAttribute );
        if ( dependenciesAttribute != null )
        {
            attributes.put( dependenciesAttribute );
        }

        // Schema entry's DN
        String dn = "cn=" + schema.getName() + "," + SCHEMA_BASE_DN;

        // Creating the entry
        wrapper.createEntry( dn, attributes, controls, monitor );
    }


    /**
     * Creates an OrganizationalUnit entry with the given information.
     *
     * @param dn
     *      the DN
     * @param ouValue
     *      the value of the 'ou' attribute
     */
    private void createOrganizationalUnitEntry( String dn, String ouValue )
    {
        Attribute objectClassAttribute = new AttributeImpl( "objectClass" );
        objectClassAttribute.add( "top" );
        objectClassAttribute.add( "organizationalUnit" );

        Attribute ouAttribute = new AttributeImpl( "ou" );
        ouAttribute.add( ouValue );

        Attributes attributes = new AttributesImpl();
        attributes.put( objectClassAttribute );
        attributes.put( ouAttribute );

        wrapper.createEntry( dn, attributes, controls, monitor );
    }
}
