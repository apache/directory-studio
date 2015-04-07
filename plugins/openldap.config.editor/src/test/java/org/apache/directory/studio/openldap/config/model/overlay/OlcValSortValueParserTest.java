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
package org.apache.directory.studio.openldap.config.model.overlay;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.directory.api.ldap.model.name.Dn;
import org.junit.Test;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcValSortValueParserTest
{
    @Test
    public void testEmpty1() throws Exception
    {
        OlcValSortValue value = OlcValSortValue.parse( "" );

        assertNull( value );
    }


    @Test
    public void testEmpty2() throws Exception
    {
        OlcValSortValue value = OlcValSortValue.parse( " " );

        assertNull( value );
    }


    @Test
    public void testEmpty3() throws Exception
    {
        OlcValSortValue value = OlcValSortValue.parse( "\t" );

        assertNull( value );
    }


    @Test
    public void testEmpty4() throws Exception
    {
        OlcValSortValue value = OlcValSortValue.parse( "\n" );

        assertNull( value );
    }


    @Test
    public void testOk1() throws Exception
    {
        String attribute = "member";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );
        OlcValSortMethodEnum sortMethod = OlcValSortMethodEnum.ALPHA_ASCEND;

        String s = attribute + " " + baseDn + " " + sortMethod;

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertFalse( value.isWeighted() );
        assertEquals( sortMethod, value.getSortMethod() );
        assertEquals( s, value.toString() );
    }


    @Test
    public void testOk2() throws Exception
    {
        String attribute = "member";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );
        OlcValSortMethodEnum sortMethod = OlcValSortMethodEnum.ALPHA_ASCEND;

        String s = attribute + "   " + baseDn + "   " + sortMethod;
        String s2 = attribute + " " + baseDn + " " + sortMethod;

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertFalse( value.isWeighted() );
        assertEquals( sortMethod, value.getSortMethod() );
        assertEquals( s2, value.toString() );
    }


    @Test
    public void testOk3() throws Exception
    {
        String attribute = "member";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );
        OlcValSortMethodEnum sortMethod = OlcValSortMethodEnum.ALPHA_DESCEND;

        String s = attribute + " " + baseDn + " " + sortMethod;

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertFalse( value.isWeighted() );
        assertEquals( sortMethod, value.getSortMethod() );
        assertEquals( s, value.toString() );
    }


    @Test
    public void testOk4() throws Exception
    {
        String attribute = "member";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );
        OlcValSortMethodEnum sortMethod = OlcValSortMethodEnum.NUMERIC_ASCEND;

        String s = attribute + " " + baseDn + " " + sortMethod;

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertFalse( value.isWeighted() );
        assertEquals( sortMethod, value.getSortMethod() );
        assertEquals( s, value.toString() );
    }


    @Test
    public void testOk5() throws Exception
    {
        String attribute = "member";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );
        OlcValSortMethodEnum sortMethod = OlcValSortMethodEnum.NUMERIC_DESCEND;

        String s = attribute + " " + baseDn + " " + sortMethod;

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertFalse( value.isWeighted() );
        assertEquals( sortMethod, value.getSortMethod() );
        assertEquals( s, value.toString() );
    }


    @Test
    public void testOk6() throws Exception
    {
        String attribute = "attr";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );

        String s = attribute + " " + baseDn + " weighted";

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertTrue( value.isWeighted() );
        assertNull( value.getSortMethod() );
        assertEquals( s, value.toString() );
    }


    @Test
    public void testOk7() throws Exception
    {
        String attribute = "member";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );
        OlcValSortMethodEnum sortMethod = OlcValSortMethodEnum.ALPHA_ASCEND;

        String s = attribute + " " + baseDn + " weighted " + sortMethod;

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertTrue( value.isWeighted() );
        assertEquals( sortMethod, value.getSortMethod() );
        assertEquals( s, value.toString() );
    }


    @Test
    public void testOk8() throws Exception
    {
        String attribute = "member";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );
        OlcValSortMethodEnum sortMethod = OlcValSortMethodEnum.ALPHA_DESCEND;

        String s = attribute + " " + baseDn + " weighted " + sortMethod;

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertTrue( value.isWeighted() );
        assertEquals( sortMethod, value.getSortMethod() );
        assertEquals( s, value.toString() );
    }


    @Test
    public void testOk9() throws Exception
    {
        String attribute = "member";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );
        OlcValSortMethodEnum sortMethod = OlcValSortMethodEnum.NUMERIC_ASCEND;

        String s = attribute + " " + baseDn + " weighted " + sortMethod;

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertTrue( value.isWeighted() );
        assertEquals( sortMethod, value.getSortMethod() );
        assertEquals( s, value.toString() );
    }


    @Test
    public void testOk10() throws Exception
    {
        String attribute = "member";
        Dn baseDn = new Dn( "ou=groups,dc=example,dc=com" );
        OlcValSortMethodEnum sortMethod = OlcValSortMethodEnum.NUMERIC_DESCEND;

        String s = attribute + " " + baseDn + " weighted " + sortMethod;

        OlcValSortValue value = OlcValSortValue.parse( s );

        assertNotNull( value );
        assertEquals( attribute, value.getAttribute() );
        assertEquals( baseDn, value.getBaseDn() );
        assertTrue( value.isWeighted() );
        assertEquals( sortMethod, value.getSortMethod() );
        assertEquals( s, value.toString() );
    }
}
