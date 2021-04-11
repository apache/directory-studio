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

package org.apache.directory.studio.test.integration.core;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.junit.jupiter.api.Test;


/**
 * Tests the {@link Schema}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaTest
{

    /**
     * Test that the default schema is properly loaded.
     */
    @Test
    public void testDefaultSchema()
    {
        Schema defaultSchema = Schema.DEFAULT_SCHEMA;
        assertNotNull( defaultSchema );

        Collection<ObjectClass> ocds = defaultSchema.getObjectClassDescriptions();
        assertNotNull( ocds );
        assertFalse( ocds.isEmpty() );

        assertNotNull( defaultSchema.getObjectClassDescription( "top" ) );
        assertNotNull( defaultSchema.getObjectClassDescription( "inetOrgPerson" ) );
        assertNotNull( defaultSchema.getObjectClassDescription( "groupOfNames" ) );

        Collection<AttributeType> atds = defaultSchema.getAttributeTypeDescriptions();
        assertNotNull( atds );
        assertFalse( atds.isEmpty() );

        assertNotNull( defaultSchema.getAttributeTypeDescription( "objectClass" ) );
        assertNotNull( defaultSchema.getAttributeTypeDescription( "cn" ) );
        assertNotNull( defaultSchema.getAttributeTypeDescription( "member" ) );
    }

}
