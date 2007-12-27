/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.studio.ldapbrowser.core.model;


import junit.framework.TestCase;

import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken;


/**
 * Tests the filter parser for error tolerance. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapFilterParserErrorTolerantTest extends TestCase
{
    private LdapFilterParser parser = new LdapFilterParser();


    public void testLpar()
    {
        parser.parse( "(" );

        LdapFilter model = parser.getModel();

        assertNotNull( model.getStartToken() );
        assertEquals( 0, model.getStartToken().getOffset() );
        assertEquals( 1, model.getStartToken().getLength() );
        assertEquals( LdapFilterToken.LPAR, model.getStartToken().getType() );
        assertEquals( "(", model.getStartToken().getValue() );

        assertNull( model.getFilterComponent() );

        assertNull( model.getStopToken() );

        assertEquals( "(", model.toString() );
        assertFalse( parser.getModel().isValid() );
    }


    public void testLparAttr()
    {
        parser.parse( "(objectClass" );

        LdapFilter model = parser.getModel();

        assertNotNull( model.getStartToken() );
        assertEquals( 0, model.getStartToken().getOffset() );
        assertEquals( 1, model.getStartToken().getLength() );
        assertEquals( LdapFilterToken.LPAR, model.getStartToken().getType() );
        assertEquals( "(", model.getStartToken().getValue() );

        LdapFilterComponent filterComponent = model.getFilterComponent();
        assertNotNull( filterComponent );
        assertTrue( filterComponent instanceof LdapFilterItemComponent );
        LdapFilterItemComponent filterItemComponent = ( LdapFilterItemComponent ) filterComponent;
        assertNotNull( filterItemComponent.getAttributeToken() );
        assertEquals( 1, filterItemComponent.getAttributeToken().getOffset() );
        assertEquals( 11, filterItemComponent.getAttributeToken().getLength() );
        assertEquals( LdapFilterToken.ATTRIBUTE, filterItemComponent.getAttributeToken().getType() );
        assertEquals( "objectClass", filterItemComponent.getAttributeToken().getValue() );
        assertNull( filterItemComponent.getFilterToken() );
        assertNull( filterItemComponent.getValueToken() );

        assertNull( model.getStopToken() );

        assertEquals( "(objectClass", model.toString() );
        assertFalse( parser.getModel().isValid() );
    }


    public void testLparAttrEquals()
    {
        parser.parse( "(objectClass=" );

        LdapFilter model = parser.getModel();

        assertNotNull( model.getStartToken() );
        assertEquals( 0, model.getStartToken().getOffset() );
        assertEquals( 1, model.getStartToken().getLength() );
        assertEquals( LdapFilterToken.LPAR, model.getStartToken().getType() );
        assertEquals( "(", model.getStartToken().getValue() );

        LdapFilterComponent filterComponent = model.getFilterComponent();
        assertNotNull( filterComponent );
        assertTrue( filterComponent instanceof LdapFilterItemComponent );
        LdapFilterItemComponent filterItemComponent = ( LdapFilterItemComponent ) filterComponent;
        assertNotNull( filterItemComponent.getAttributeToken() );
        assertEquals( 1, filterItemComponent.getAttributeToken().getOffset() );
        assertEquals( 11, filterItemComponent.getAttributeToken().getLength() );
        assertEquals( LdapFilterToken.ATTRIBUTE, filterItemComponent.getAttributeToken().getType() );
        assertEquals( "objectClass", filterItemComponent.getAttributeToken().getValue() );

        assertNotNull( filterItemComponent.getFilterToken() );
        assertEquals( 12, filterItemComponent.getFilterToken().getOffset() );
        assertEquals( 1, filterItemComponent.getFilterToken().getLength() );
        assertEquals( LdapFilterToken.EQUAL, filterItemComponent.getFilterToken().getType() );
        assertEquals( "=", filterItemComponent.getFilterToken().getValue() );

        assertNull( filterItemComponent.getValueToken() );

        assertNull( model.getStopToken() );

        assertEquals( "(objectClass=", model.toString() );
        assertFalse( parser.getModel().isValid() );
    }


    public void testLparAttrEqualsRpar()
    {
        parser.parse( "(objectClass=)" );

        LdapFilter model = parser.getModel();

        assertNotNull( model.getStartToken() );
        assertEquals( 0, model.getStartToken().getOffset() );
        assertEquals( 1, model.getStartToken().getLength() );
        assertEquals( LdapFilterToken.LPAR, model.getStartToken().getType() );
        assertEquals( "(", model.getStartToken().getValue() );

        LdapFilterComponent filterComponent = model.getFilterComponent();
        assertNotNull( filterComponent );
        assertTrue( filterComponent instanceof LdapFilterItemComponent );
        LdapFilterItemComponent filterItemComponent = ( LdapFilterItemComponent ) filterComponent;
        assertNotNull( filterItemComponent.getAttributeToken() );
        assertEquals( 1, filterItemComponent.getAttributeToken().getOffset() );
        assertEquals( 11, filterItemComponent.getAttributeToken().getLength() );
        assertEquals( LdapFilterToken.ATTRIBUTE, filterItemComponent.getAttributeToken().getType() );
        assertEquals( "objectClass", filterItemComponent.getAttributeToken().getValue() );

        assertNotNull( filterItemComponent.getFilterToken() );
        assertEquals( 12, filterItemComponent.getFilterToken().getOffset() );
        assertEquals( 1, filterItemComponent.getFilterToken().getLength() );
        assertEquals( LdapFilterToken.EQUAL, filterItemComponent.getFilterToken().getType() );
        assertEquals( "=", filterItemComponent.getFilterToken().getValue() );

        assertNotNull( filterItemComponent.getValueToken() );

        assertNotNull( model.getStopToken() );

        assertEquals( "(objectClass=)", model.toString() );
        assertTrue( parser.getModel().isValid() );
    }

}
