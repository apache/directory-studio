package org.apache.directory.studio.ldapbrowser.core.utils;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.junit.Before;
import org.junit.Test;


public class AttributeComparatorTest
{
    private IBrowserConnection connection;
    private IEntry entry;

    private Attribute objectClass;
    private Value objectClassTop;
    private Value objectClassPerson;

    private Attribute cn;
    private Value cn_foo_1;
    private Value cn_foo_2;
    private Value cn_bar;
    private Value cn_empty_1;
    private Value cn_empty_2;

    private Attribute sn;
    private Value sn_foo;

    private AttributeComparator comparator;


    @Before
    public void setup() throws Exception
    {
        ConnectionEventRegistry.suspendEventFiringInCurrentThread();
        connection = new DummyConnection( Schema.DEFAULT_SCHEMA );
        entry = new DummyEntry( new Dn( "cn=foo" ), connection );

        objectClass = new Attribute( entry, "objectClass" );
        entry.addAttribute( objectClass );
        objectClassTop = new Value( objectClass, "top" );
        objectClass.addValue( objectClassTop );
        objectClassPerson = new Value( objectClass, "person" );
        objectClass.addValue( objectClassPerson );

        cn = new Attribute( entry, "cn" );
        entry.addAttribute( cn );
        cn_foo_1 = new Value( cn, "foo" );
        cn.addValue( cn_foo_1 );
        cn_foo_2 = new Value( cn, "foo" );
        cn.addValue( cn_foo_2 );
        cn_bar = new Value( cn, "bar" );
        cn.addValue( cn_bar );
        cn_empty_1 = new Value( cn, "" );
        cn.addValue( cn_empty_1 );
        cn_empty_2 = new Value( cn, "" );
        cn.addValue( cn_empty_2 );

        sn = new Attribute( entry, "sn" );
        entry.addAttribute( sn );
        sn_foo = new Value( sn, "foo" );
        sn.addValue( sn_foo );

        comparator = new AttributeComparator();
    }


    @Test
    public void testAttributesEqual()
    {
        assertEquals( 0, comparator.compare( objectClass, objectClass ) );
        assertEquals( 0, comparator.compare( cn, cn ) );
        assertEquals( 0, comparator.compare( cn, new Attribute( entry, "cn" ) ) );
    }


    @Test
    public void testValuesEqual()
    {
        assertEquals( 0, comparator.compare( cn_foo_1, cn_foo_1 ) );
        assertEquals( 0, comparator.compare( cn_foo_1, cn_foo_2 ) );
        assertEquals( 0, comparator.compare( cn_foo_2, cn_foo_1 ) );
    }


    @Test
    public void testMustAttributresDiffer()
    {
        int less = comparator.compare( cn, sn );
        assertTrue( less < 0 );
        int greater = comparator.compare( sn, cn );
        assertTrue( greater > 0 );
        assertEquals( 0, less + greater );
    }


    @Test
    public void testObjectClassMustAttributesDiffer()
    {
        int less = comparator.compare( objectClass, cn );
        assertTrue( less < 0 );
        int greater = comparator.compare( cn, objectClass );
        assertTrue( greater > 0 );
        assertEquals( 0, less + greater );
    }


    // TODO: objectclass, may, operational
    // TODO: objectclass < must < may < operational

    @Test
    public void testValuesDiffer()
    {
        int less = comparator.compare( cn_bar, cn_foo_1 );
        assertTrue( less < 0 );
        int greater = comparator.compare( cn_foo_1, cn_bar );
        assertTrue( greater > 0 );
        assertEquals( 0, less + greater );
    }


    @Test
    public void testEmptyValuesEqual()
    {
        assertEquals( 0, comparator.compare( cn_empty_1, cn_empty_1 ) );
        assertEquals( 0, comparator.compare( cn_empty_1, cn_empty_2 ) );
        assertEquals( 0, comparator.compare( cn_empty_2, cn_empty_1 ) );
    }


    @Test
    public void testEmptyValuesDiffer()
    {
        int less = comparator.compare( cn_empty_1, cn_bar );
        assertTrue( less < 0 );
        int greater = comparator.compare( cn_bar, cn_empty_1 );
        assertTrue( greater > 0 );
        assertEquals( 0, less + greater );
    }


    @Test
    public void test_DIRSTUDIO_1200() throws Exception
    {
        Schema schema = Schema.DEFAULT_SCHEMA;
        DummyConnection connection = new DummyConnection( schema );

        LdifContentRecord record = new LdifContentRecord( LdifDnLine.create( "cn=foo" ) );
        record.addAttrVal( LdifAttrValLine.create( "objectClass", "inetOrgPerson" ) );
        for ( int i = 0; i < 28; i++ )
        {
            record.addAttrVal( LdifAttrValLine.create( "uid", "" + i ) );
        }
        record.addAttrVal( LdifAttrValLine.create( "objectClass", "top" ) );
        record.addAttrVal( LdifAttrValLine.create( "cn", "foo" ) );
        record.addAttrVal( LdifAttrValLine.create( "cn", "bar" ) );

        DummyEntry entry = ModelConverter.ldifContentRecordToEntry( record, connection );
        List<IValue> sortedValues = AttributeComparator.toSortedValues( entry );
        assertEquals( 32, sortedValues.size() );
        assertEquals( "objectClass", sortedValues.get( 0 ).getAttribute().getDescription() );
        assertEquals( "inetOrgPerson", sortedValues.get( 0 ).getStringValue() );
        assertEquals( "uid", sortedValues.get( 31 ).getAttribute().getDescription() );
        assertEquals( "9", sortedValues.get( 31 ).getStringValue() );
    }

}
