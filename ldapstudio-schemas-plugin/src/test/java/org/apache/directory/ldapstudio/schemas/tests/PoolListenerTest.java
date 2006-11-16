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


import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.server.core.tools.schema.AttributeTypeLiteral;

import junit.framework.TestCase;


/**
 * Tests pool listener events
 *
 */
public class PoolListenerTest extends TestCase implements PoolListener
{
    SchemaPool pool;
    boolean poolChanged;


    public void setUp()
    {
        pool = new SchemaPool();
        poolChanged = false;
    }


    public void testRegisterListener() throws Exception
    {
        pool.addListener( this );
    }


    public void testRemoveListener() throws Exception
    {
        pool.addListener( this );
        pool.removeListener( this );
    }


    public void testNotification() throws Exception
    {
        //Add a schema to the pool -> notification
        pool.addListener( this );
        Schema schema = new Schema( "toto" ); //$NON-NLS-1$
        pool.addSchema( schema );
        assertTrue( poolChanged );

        poolChanged = false;

        //Add an attribute type to a schema inside the pool -> notification
        schema.addAttributeType( new AttributeType( new AttributeTypeLiteral( "1.2.3" ), schema ) ); //$NON-NLS-1$
        assertTrue( poolChanged );

        poolChanged = false;

        //Remove a schema from the pool -> notification
        pool.removeSchema( schema );
        assertTrue( poolChanged );
    }


    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        poolChanged = true;
    }
}
