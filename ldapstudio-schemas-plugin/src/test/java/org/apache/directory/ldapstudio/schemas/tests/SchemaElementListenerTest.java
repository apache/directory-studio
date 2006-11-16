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
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaElement;
import org.apache.directory.ldapstudio.schemas.model.SchemaElementListener;
import org.apache.directory.server.core.tools.schema.AttributeTypeLiteral;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;

import junit.framework.TestCase;


/**
 * Tests object-class and attribute-type listener events
 *
 */
public class SchemaElementListenerTest extends TestCase implements SchemaElementListener
{

    boolean elementChanged;
    Schema schema;
    ObjectClass oc;
    AttributeType at;


    public void setUp()
    {
        elementChanged = false;
        schema = new Schema( "test" ); //$NON-NLS-1$
        at = new AttributeType( new AttributeTypeLiteral( "1.2.3" ), schema ); //$NON-NLS-1$
        oc = new ObjectClass( new ObjectClassLiteral( "4.5.6" ), schema ); //$NON-NLS-1$
        schema.addAttributeType( at );
        schema.addObjectClass( oc );
    }


    public void testObjectClassNotification() throws Exception
    {
        oc.addListener( this );
        oc.setNames( new String[]
            { "toto" } ); //$NON-NLS-1$

        assertTrue( elementChanged );
        elementChanged = false;

        oc.setOid( "8.9.10" ); //$NON-NLS-1$

        assertTrue( elementChanged );
        elementChanged = false;

        oc.setDescription( "toto's description" ); //$NON-NLS-1$

        assertTrue( elementChanged );
        elementChanged = false;

        oc.setMay( new String[]
            { "at1", "at2" } ); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue( elementChanged );
        elementChanged = false;

        oc.setMust( new String[]
            { "at3", "at4" } ); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue( elementChanged );
    }


    public void testAttributeTypeNotification() throws Exception
    {
        at.addListener( this );

        at.setNames( new String[]
            { "toto" } ); //$NON-NLS-1$

        assertTrue( elementChanged );
        elementChanged = false;

        at.setDescription( "toto's description" ); //$NON-NLS-1$

        assertTrue( elementChanged );
        elementChanged = false;
    }


    public void schemaElementChanged( SchemaElement originatingSchemaElement, LDAPModelEvent e )
    {
        elementChanged = true;
    }

}
