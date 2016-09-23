
package org.apache.directory.studio.valueeditors;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.Test;


public class ValueEditorUtilsTest
{

    @Test
    public void testEmptyStringIsEditable()
    {
        assertTrue( StringValueEditorUtils.isEditable( "".getBytes() ) );
    }


    @Test
    public void testAsciiIsEditable()
    {
        assertTrue( StringValueEditorUtils.isEditable( "abc\n123".getBytes( StandardCharsets.US_ASCII ) ) );
    }


    @Test
    public void testUft8IsEditable()
    {
        assertTrue( StringValueEditorUtils.isEditable( "a\nb\r\u00e4\t\u5047".getBytes( StandardCharsets.UTF_8 ) ) );
    }


    @Test
    public void testIso88591IsNotEditable()
    {
        assertFalse(
            StringValueEditorUtils.isEditable( "\u00e4\u00f6\u00fc".getBytes( StandardCharsets.ISO_8859_1 ) ) );
    }


    @Test
    public void testPngIsNotEditable()
    {
        assertFalse( StringValueEditorUtils.isEditable( new byte[]
            { ( byte ) 0x89, 0x50, 0x4E, 0x47 } ) );
    }

}
