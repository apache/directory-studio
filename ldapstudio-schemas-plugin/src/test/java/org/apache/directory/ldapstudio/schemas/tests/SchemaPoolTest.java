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

package org.apache.directory.ldapstudio.schemas.tests;


import java.util.Hashtable;

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;

import junit.framework.TestCase;


/**
 * Tests the schema pool
 *
 */
public class SchemaPoolTest extends TestCase
{

    private SchemaPool pool;
    private Schema[] schemas;


    public void setUp()
    {
        pool = new SchemaPool();
        schemas = new Schema[100];
    }


    public void testAddSchemasToPool() throws Exception
    {

        for ( int i = 0; i < 100; i++ )
        {
            Schema sc = new Schema( "schema_" + i ); //$NON-NLS-1$
            schemas[i] = sc;
            pool.addSchema( sc );
        }

        assertEquals( pool.count(), 100 );

        for ( int i = 0; i < 100; i++ )
        {
            assertTrue( pool.containsSchema( schemas[i] ) );
            assertTrue( pool.containsSchema( schemas[i].getName() ) );
        }
        System.out.println();
    }


    public void testRemoveSchemasFromPool() throws Exception
    {
        testAddSchemasToPool();

        for ( int i = 0; i < 100; i++ )
        {
            pool.removeSchema( schemas[i] );
            assertFalse( pool.containsSchema( schemas[i] ) );
        }

        assertEquals( pool.count(), 0 );
    }


    public void testAddExistingSchemasToPool() throws Exception
    {
        Schema sc1 = new Schema( "ressources/schemas/apache.schema", Schema.SchemaType.coreSchema ); //$NON-NLS-1$
        Schema sc2 = new Schema( "ressources/schemas/core.schema", Schema.SchemaType.coreSchema ); //$NON-NLS-1$
        Schema sc3 = new Schema( "ressources/schemas/cosine.schema", Schema.SchemaType.coreSchema ); //$NON-NLS-1$
        Schema sc4 = new Schema( "ressources/schemas/misc.schema", Schema.SchemaType.coreSchema ); //$NON-NLS-1$

        pool.addSchemas( new Schema[]
            { sc1, sc2, sc3, sc4 } );

        assertTrue( pool.containsSchema( sc1 ) );
        assertTrue( pool.containsSchema( sc2 ) );
        assertTrue( pool.containsSchema( sc3 ) );
        assertTrue( pool.containsSchema( sc4 ) );

        Hashtable<String, ObjectClass> objectClasses = pool.getObjectClassesAsHashTableByName();
        Hashtable<String, AttributeType> attributeTypes = pool.getAttributeTypesAsHashTableByName();

        AttributeType[] sc1AT = sc1.getAttributeTypesAsArray();
        for ( AttributeType type : sc1AT )
        {
            assertTrue( attributeTypes.containsValue( type ) );
        }

        AttributeType[] sc2AT = sc2.getAttributeTypesAsArray();
        for ( AttributeType type : sc2AT )
        {
            assertTrue( attributeTypes.containsValue( type ) );
        }

        AttributeType[] sc3AT = sc3.getAttributeTypesAsArray();
        for ( AttributeType type : sc3AT )
        {
            assertTrue( attributeTypes.containsValue( type ) );
        }

        AttributeType[] sc4AT = sc4.getAttributeTypesAsArray();
        for ( AttributeType type : sc4AT )
        {
            assertTrue( attributeTypes.containsValue( type ) );
        }

        ObjectClass[] sc1OC = sc1.getObjectClassesAsArray();
        for ( ObjectClass class1 : sc1OC )
        {
            assertTrue( objectClasses.contains( class1 ) );
        }

        ObjectClass[] sc2OC = sc1.getObjectClassesAsArray();
        for ( ObjectClass class1 : sc2OC )
        {
            assertTrue( objectClasses.contains( class1 ) );
        }

        ObjectClass[] sc3OC = sc1.getObjectClassesAsArray();
        for ( ObjectClass class1 : sc3OC )
        {
            assertTrue( objectClasses.contains( class1 ) );
        }

        ObjectClass[] sc4OC = sc1.getObjectClassesAsArray();
        for ( ObjectClass class1 : sc4OC )
        {
            assertTrue( objectClasses.contains( class1 ) );
        }

        pool.removeSchemas( new Schema[]
            { sc1, sc2, sc3, sc4 } );
        assertFalse( pool.containsSchema( sc1 ) );
        assertFalse( pool.containsSchema( sc2 ) );
        assertFalse( pool.containsSchema( sc3 ) );
        assertFalse( pool.containsSchema( sc4 ) );
        assertEquals( pool.count(), 0 );

        objectClasses = pool.getObjectClassesAsHashTableByName();
        attributeTypes = pool.getAttributeTypesAsHashTableByName();

        assertEquals( objectClasses.size(), 0 );
        assertEquals( attributeTypes.size(), 0 );

    }
}
