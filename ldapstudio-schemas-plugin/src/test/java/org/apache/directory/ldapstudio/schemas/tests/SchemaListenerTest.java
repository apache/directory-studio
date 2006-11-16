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
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaListener;
import org.apache.directory.server.core.tools.schema.AttributeTypeLiteral;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;


/**
 * Tests schema listener events 
 *
 */
public class SchemaListenerTest extends TestCase implements SchemaListener
{
    boolean schemaChanged;
    Schema schema;


    public void setUp()
    {
        schemaChanged = false;
        schema = new Schema( "test" ); //$NON-NLS-1$
    }


    public void testRegisterListener() throws Exception
    {
        schema.addListener( this );
    }


    public void testRemoveListener() throws Exception
    {
        schema.addListener( this );
        schema.removeListener( this );
    }


    public void testNotification() throws Exception
    {
        schema.addListener( this );

        //Add an attribute type to the schema -> notification
        schema.addAttributeType( new AttributeType( new AttributeTypeLiteral( "1.2.3" ), schema ) ); //$NON-NLS-1$
        assertTrue( schemaChanged );

        schemaChanged = false;

        schema.addObjectClass( new ObjectClass( new ObjectClassLiteral( "4.5.6" ), schema ) ); //$NON-NLS-1$
        assertTrue( schemaChanged );
    }


    public void schemaChanged( Schema originatingSchema, LDAPModelEvent e )
    {
        schemaChanged = true;
    }

}
