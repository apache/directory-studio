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


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.api.ldap.model.schema.UsageEnum;
import org.junit.Before;
import org.junit.Test;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapSchemaFileExporterTest
{

    private ObjectClass objectClassSimple;
    private ObjectClass objectClassComplex;

    private AttributeType attributeTypeSimple;
    private AttributeType attributeTypeComplex;

    @Before
    public void setUp()
    {
        objectClassSimple = new ObjectClass( "1.2.3.4" );
        objectClassSimple.setNames( "name0" );
        objectClassSimple.setMustAttributeTypeOids( Arrays.asList( "att0" ) );
        objectClassSimple.setSchemaName( "dummy" );

        objectClassComplex = new ObjectClass( "1.2.3.4" );
        objectClassComplex.setNames( "name1", "name2" );
        objectClassComplex.setDescription( "description with 'quotes'" );
        objectClassComplex.setObsolete( true );
        objectClassComplex.setSuperiorOids( Collections.singletonList( "1.3.5.7" ) );
        objectClassComplex.setType( ObjectClassTypeEnum.AUXILIARY );
        objectClassComplex.setMustAttributeTypeOids( Arrays.asList( "att1", "att2" ) );
        objectClassComplex.setMayAttributeTypeOids( Arrays.asList( "att3", "att4" ) );
        objectClassComplex.setSchemaName( "dummy" );
        
        attributeTypeSimple = new AttributeType( "1.2.3.4" );
        attributeTypeSimple.setNames( "name0" );
        attributeTypeSimple.setEqualityOid( "matchingRule0" );
        attributeTypeSimple.setSyntaxOid( "2.3.4.5" );
        attributeTypeSimple.setSyntaxLength( 512 );
        attributeTypeSimple.setCollective( true );
        attributeTypeSimple.setSchemaName( "dummy" );

        attributeTypeComplex = new AttributeType( "1.2.3.4" );
        attributeTypeComplex.setNames( "name1", "name2" );
        attributeTypeComplex.setDescription( "description with 'quotes'" );
        attributeTypeComplex.setObsolete( true );
        attributeTypeComplex.setSuperiorOid( "superAttr" );
        attributeTypeComplex.setEqualityOid( "matchingRule1" );
        attributeTypeComplex.setOrderingOid( "matchingRule2" );
        attributeTypeComplex.setSubstringOid( "matchingRule3" );
        attributeTypeComplex.setSingleValued( true );
        attributeTypeComplex.setUserModifiable( false );
        attributeTypeComplex.setUsage( UsageEnum.DIRECTORY_OPERATION );
        attributeTypeComplex.setSchemaName( "dummy" );
    }


    @Test
    public void testOpenLdapSchemaRendererObjectClassSimple()
    {
        String actual = OpenLdapSchemaFileExporter.toSourceCode( objectClassSimple );
        String expected = "objectclass ( 1.2.3.4 NAME 'name0'\n\tSTRUCTURAL\n\tMUST att0 )";
        assertEquals( expected, actual );
    }


    @Test
    public void testOpenLdapSchemaRendererObjectClassComplex()
    {
        String actual = OpenLdapSchemaFileExporter.toSourceCode( objectClassComplex );
        String expected = "objectclass ( 1.2.3.4 NAME ( 'name1' 'name2' )\n\tDESC 'description with \\27quotes\\27'\n\tOBSOLETE\n\tSUP 1.3.5.7\n\tAUXILIARY\n\tMUST ( att1 $ att2 )\n\tMAY ( att3 $ att4 ) )";
        assertEquals( expected, actual );
    }
    
    @Test
    public void testOpenLdapSchemaRendererAttributeTypeSimple()
    {
        String actual = OpenLdapSchemaFileExporter.toSourceCode( attributeTypeSimple );
        String expected = "attributetype ( 1.2.3.4 NAME 'name0'\n\tEQUALITY matchingRule0\n\tSYNTAX 2.3.4.5{512}\n\tCOLLECTIVE\n\tUSAGE userApplications )";
        assertEquals( expected, actual );
    }


    @Test
    public void testOpenLdapSchemaRendererAttributeTypeComplex()
    {
        String actual = OpenLdapSchemaFileExporter.toSourceCode( attributeTypeComplex );
        String expected = "attributetype ( 1.2.3.4 NAME ( 'name1' 'name2' )\n\tDESC 'description with \\27quotes\\27'\n\tOBSOLETE\n\tSUP superAttr\n\tEQUALITY matchingRule1\n\tORDERING matchingRule2\n\tSUBSTR matchingRule3\n\tSINGLE-VALUE\n\tNO-USER-MODIFICATION\n\tUSAGE directoryOperation )";
        assertEquals( expected, actual );
    }

}
