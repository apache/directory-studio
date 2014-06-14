package org.apache.directory.studio.ldapbrowser.common.widgets.search;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class ReturningAttributesWidgetTest
{

    @Test
    public void testStringToArrayNull()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( null );
        assertNull( attributes );
    }


    @Test
    public void testStringToArrayEmpty()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "" );
        assertNotNull( attributes );
        assertArrayEquals( new String[0], attributes );
    }


    @Test
    public void testStringToArrayNoAttrs()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "1.1" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "1.1" }, attributes );
    }


    @Test
    public void testStringToArraySingleAttribute()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn" }, attributes );
    }


    @Test
    public void testStringToArrayStingleAttributeWithTrailingWhitespace()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( " cn\t " );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn" }, attributes );
    }


    @Test
    public void testStringToArrayStingleAttributeWithTrailingCommas()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( " , ,cn,," );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributes()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, sn uid" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "sn", "uid" }, attributes );
    }


    @Test
    public void testStringToArrayMultiplwWithAllUserAndOperationalAttributes()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, sn uid, * +" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "sn", "uid", "*", "+" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributesWithOptions()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, sn;lang-de;lang-en uid" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "sn;lang-de;lang-en", "uid" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributesAsOid()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "2.5.4.3, 2.5.4.4 0.9.2342.19200300.100.1.1" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "2.5.4.3", "2.5.4.4", "0.9.2342.19200300.100.1.1" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributesWithUnderscore()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, s_n u_i_d" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "s_n", "u_i_d" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributesWithRangeOption()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, member;Range=0-* objectClass" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "member;Range=0-*", "objectClass" }, attributes );
    }

}
