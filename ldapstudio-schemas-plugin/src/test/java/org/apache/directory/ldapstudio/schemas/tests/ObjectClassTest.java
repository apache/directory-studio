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

import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;


public class ObjectClassTest extends TestCase
{

    ObjectClassLiteral literal;
    Schema schema;
    final String name = "toto"; //$NON-NLS-1$
    final String oid = "1.2.3"; //$NON-NLS-1$
    final String desc = "toto description"; //$NON-NLS-1$
    final String[] superiors = new String[]
        { "titi", "tata", "tutu" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final String[] may = new String[]
        { "att1", "att2", "att3" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final String[] must = new String[]
        { "att4", "att5", "att6" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$


    public void setUp()
    {
        literal = new ObjectClassLiteral( oid );
        literal.setNames( new String[]
            { name } );
        literal.setDescription( desc );
        literal.setSuperiors( superiors );
        literal.setMay( may );
        literal.setMust( must );

        schema = new Schema( "test" ); //$NON-NLS-1$
    }


    /**
     * Tries to create a new Object Class
     * @throws Exception
     */
    public void testCreateObjectClassFromLiteral() throws Exception
    {
        ObjectClass oc = new ObjectClass( literal, schema );
    }


    /**
     * Tries to access the underlying litteral issued by the parser from the
     * object class instance
     * @throws Exception
     */
    public void testAccessLiteralFromObjectClass() throws Exception
    {
        ObjectClass oc = new ObjectClass( literal, schema );
        assertEquals( oc.getLiteral(), literal );
    }


    /**
     * Tries to access an object-class' related schema
     * @throws Exception
     */
    public void testAccessSchemaFromObjectClass() throws Exception
    {
        ObjectClass oc = new ObjectClass( literal, schema );
        assertEquals( oc.getOriginatingSchema(), schema );
    }


    /**
     * Tries the various wrapper methods
     * @throws Exception
     */
    public void testWrapperMethods() throws Exception
    {
        ObjectClass oc = new ObjectClass( literal, schema );
        assertEquals( oc.getOid(), oid );
        assertEquals( oc.getDescription(), desc );
        assertEquals( oc.getSuperiors(), superiors );
        assertEquals( oc.getMay(), may );
        assertEquals( oc.getMust(), must );
    }


    /**
     * Tests the toString() method
     * @throws Exception
     */
    public void testToString() throws Exception
    {
        ObjectClass oc = new ObjectClass( literal, schema );
        assertEquals( oc.toString(), name );
    }


    /**
     * Tests the write method
     * @throws Exception
     */
    public void testWrite() throws Exception
    {
        ObjectClass oc1 = new ObjectClass( literal, schema );
        ObjectClass oc2 = new ObjectClass( literal, schema );

        assertEquals( oc1.write(), oc2.write() );
    }

}
