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


import junit.framework.TestCase;

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.server.core.tools.schema.AttributeTypeLiteral;


/**
 * Attribute Type tests
 *
 */
public class AttributeTypeTest extends TestCase
{

    AttributeTypeLiteral literal;
    Schema schema;
    final String name = "toto"; //$NON-NLS-1$
    final String oid = "1.2.3"; //$NON-NLS-1$
    final String desc = "toto description"; //$NON-NLS-1$
    final String equality = "equ1"; //$NON-NLS-1$


    public void setUp()
    {
        literal = new AttributeTypeLiteral( oid );
        literal.setNames( new String[]
            { name } );
        literal.setDescription( desc );
        literal.setCollective( true );
        literal.setEquality( equality );
        schema = new Schema( "test" ); //$NON-NLS-1$
    }


    /**
     * Tries to create a new attribute type
     * @throws Exception
     */
    public void testCreateAttributeTypeFromLiteral() throws Exception
    {
        new AttributeType( literal, schema );
    }


    /**
     * Tries to access the underlying litteral issued by the parser from the
     * attribute type instance
     * @throws Exception
     */
    public void testAccessLiteralFromAttributeType() throws Exception
    {
        AttributeType at = new AttributeType( literal, schema );
        assertEquals( at.getLiteral(), literal );
    }


    /**
     * Tries to access an attribute type's related schema
     * @throws Exception
     */
    public void testAccessSchemaFromAttributeType() throws Exception
    {
        AttributeType at = new AttributeType( literal, schema );
        assertEquals( at.getOriginatingSchema(), schema );
    }


    /**
     * Tries the various wrapper methods
     * @throws Exception
     */
    public void testWrapperMethods() throws Exception
    {
        AttributeType at = new AttributeType( literal, schema );
        assertEquals( at.getOid(), oid );
        assertEquals( at.getDescription(), desc );
        assertEquals( at.getEquality(), equality );
    }


    /**
     * Tests the toString() method
     * @throws Exception
     */
    public void testToString() throws Exception
    {
        AttributeType at = new AttributeType( literal, schema );
        assertEquals( at.toString(), name );
    }


    /**
     * Tests the write method
     * @throws Exception
     */
    public void testWrite() throws Exception
    {
        AttributeType at1 = new AttributeType( literal, schema );
        AttributeType at2 = new AttributeType( literal, schema );

        assertEquals( at1.write(), at2.write() );
    }
}
