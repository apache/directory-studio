package org.apache.directory.studio.schemaeditor.model.io;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.api.ldap.model.schema.SchemaObjectRenderer;
import org.apache.directory.api.ldap.model.schema.UsageEnum;
import org.junit.Before;
import org.junit.Test;


public class OpenLdapSchemaFileExporterTest
{

    private MutableObjectClass objectClassSimple;
    private MutableObjectClass objectClassComplex;

    private MutableAttributeType attributeTypeSimple;
    private MutableAttributeType attributeTypeComplex;

    @Before
    public void setUp()
    {
        objectClassSimple = new MutableObjectClass( "1.2.3.4" );
        objectClassSimple.setNames( "name0" );
        objectClassSimple.setMustAttributeTypeOids( Arrays.asList( "att0" ) );
        objectClassSimple.setSchemaName( "dummy" );

        objectClassComplex = new MutableObjectClass( "1.2.3.4" );
        objectClassComplex.setNames( "name1", "name2" );
        objectClassComplex.setDescription( "description with 'quotes'" );
        objectClassComplex.setObsolete( true );
        objectClassComplex.setSuperiorOids( Collections.singletonList( "1.3.5.7" ) );
        objectClassComplex.setType( ObjectClassTypeEnum.AUXILIARY );
        objectClassComplex.setMustAttributeTypeOids( Arrays.asList( "att1", "att2" ) );
        objectClassComplex.setMayAttributeTypeOids( Arrays.asList( "att3", "att4" ) );
        objectClassComplex.setSchemaName( "dummy" );
        
        attributeTypeSimple = new MutableAttributeType( "1.2.3.4" );
        attributeTypeSimple.setNames( "name0" );
        attributeTypeSimple.setEqualityOid( "matchingRule0" );
        attributeTypeSimple.setSyntaxOid( "2.3.4.5" );
        attributeTypeSimple.setSyntaxLength( 512 );
        attributeTypeSimple.setCollective( true );
        attributeTypeSimple.setSchemaName( "dummy" );

        attributeTypeComplex = new MutableAttributeType( "1.2.3.4" );
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
        String expected = "objectclass ( 1.2.3.4 NAME 'name0'\n\tSTRUCTURAL\n\tMUST att0\n)";
        assertEquals( expected, actual );
    }


    @Test
    public void testOpenLdapSchemaRendererObjectClassComplex()
    {
        String actual = OpenLdapSchemaFileExporter.toSourceCode( objectClassComplex );
        String expected = "objectclass ( 1.2.3.4 NAME ( 'name1' 'name2' )\n\tDESC 'description with \\27quotes\\27'\n\tOBSOLETE\n\tSUP 1.3.5.7\n\tAUXILIARY\n\tMUST ( att1 $ att2 )\n\tMAY ( att3 $ att4 )\n)";
        assertEquals( expected, actual );
    }
    
    @Test
    public void testOpenLdapSchemaRendererAttributeTypeSimple()
    {
        String actual = OpenLdapSchemaFileExporter.toSourceCode( attributeTypeSimple );
        String expected = "attributetype ( 1.2.3.4 NAME 'name0'\n\tEQUALITY matchingRule0\n\tSYNTAX 2.3.4.5{512}\n\tCOLLECTIVE\n\tUSAGE userApplications\n)";
        assertEquals( expected, actual );
    }


    @Test
    public void testOpenLdapSchemaRendererAttributeTypeComplex()
    {
        String actual = OpenLdapSchemaFileExporter.toSourceCode( attributeTypeComplex );
        String expected = "attributetype ( 1.2.3.4 NAME ( 'name1' 'name2' )\n\tDESC 'description with \\27quotes\\27'\n\tOBSOLETE\n\tSUP superAttr\n\tEQUALITY matchingRule1\n\tORDERING matchingRule2\n\tSUBSTR matchingRule3\n\tSINGLE-VALUE\n\tNO-USER-MODIFICATION\n\tUSAGE directoryOperation\n)";
        assertEquals( expected, actual );
    }

}
