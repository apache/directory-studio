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

package org.apache.directory.studio.test.integration.ui;


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.valueeditors.HexValueEditor;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.InPlaceTextValueEditor;
import org.apache.directory.studio.valueeditors.TextValueEditor;
import org.apache.directory.studio.valueeditors.bool.InPlaceBooleanValueEditor;
import org.apache.directory.studio.valueeditors.oid.InPlaceOidValueEditor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


/**
 * Tests the value editors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(Parameterized.class)
public class ValueEditorTest
{

    @Parameters(name = "{0}")
    public static Object[] data()
    {
        return new Object[][]
            {
                // InPlaceTextValueEditor 

                { "InPlaceTextValueEditor - empty value",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( "cn" )
                        .rawValue( IValue.EMPTY_STRING_VALUE ).expectedRawValue( "" ).expectedDisplayValue( "" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( "" ) },

                { "InPlaceTextValueEditor - empty string",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( "cn" ).rawValue( "" )
                        .expectedRawValue( "" ).expectedDisplayValue( "" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "" ) },

                { "InPlaceTextValueEditor - ascii",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( "cn" )
                        .rawValue( "a-zA+Z0.9" ).expectedRawValue( "a-zA+Z0.9" ).expectedDisplayValue( "a-zA+Z0.9" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( "a-zA+Z0.9" ) },

                { "InPlaceTextValueEditor - ascii with newline",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( "cn" ).rawValue( "a\nb\rc" )
                        .expectedRawValue( "a\nb\rc" ).expectedDisplayValue( "a\nb\rc" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "a\nb\rc" ) },

                { "InPlaceTextValueEditor - unicode",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( "cn" )
                        .rawValue( "\u00e4\u2000\n\u5047" ).expectedRawValue( "\u00e4\u2000\n\u5047" )
                        .expectedDisplayValue( "\u00e4\u2000\n\u5047" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "\u00e4\u2000\n\u5047" ) },

                { "InPlaceTextValueEditor - bytearray UTF8",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( "userPassword" )
                        .rawValue( "a\nb\r\u00e4\t\u5047".getBytes( UTF_8 ) ).expectedRawValue( "a\nb\r\u00e4\t\u5047" )
                        .expectedDisplayValue( "a\nb\r\u00e4\t\u5047" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "a\nb\r\u00e4\t\u5047" ) },

                { "InPlaceTextValueEditor - bytearray binary", Data.data()
                    .valueEditorClass( InPlaceTextValueEditor.class ).attribute( "userPassword" ).rawValue( new byte[]
                        { ( byte ) 0x89, 0x50, 0x4E, 0x47 } )
                    .expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                    .expectedStringOrBinaryValue( null ) },

                // InPlaceBooleanValueEditor 

                { "InPlaceBooleanValueEditor - TRUE",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( "cn" ).rawValue( "TRUE" )
                        .expectedRawValue( "TRUE" ).expectedDisplayValue( "TRUE" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "TRUE" ) },

                { "InPlaceBooleanValueEditor - FALSE",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( "cn" )
                        .rawValue( "FALSE" ).expectedRawValue( "FALSE" ).expectedDisplayValue( "FALSE" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( "FALSE" ) },

                { "InPlaceBooleanValueEditor - INVALID",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( "cn" )
                        .rawValue( "invalid" ).expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( null ) },

                { "InPlaceBooleanValueEditor - bytearray TRUE",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( "userPassword" )
                        .rawValue( "TRUE".getBytes( UTF_8 ) ).expectedRawValue( "TRUE" ).expectedDisplayValue( "TRUE" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( "TRUE" ) },

                { "InPlaceBooleanValueEditor - bytearray FALSE",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( "userPassword" )
                        .rawValue( "FALSE".getBytes( UTF_8 ) ).expectedRawValue( "FALSE" )
                        .expectedDisplayValue( "FALSE" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "FALSE" ) },

                { "InPlaceBooleanValueEditor - bytearray INVALID",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( "userPassword" )
                        .rawValue( "invalid".getBytes( UTF_8 ) ).expectedRawValue( null )
                        .expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( null ) },

                // InPlaceOidValueEditor 

                { "InPlaceOidValueEditor - 1.3.6.1.4.1.1466.20037",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( "cn" )
                        .rawValue( "1.3.6.1.4.1.1466.20037" ).expectedRawValue( "1.3.6.1.4.1.1466.20037" )
                        .expectedDisplayValue( "1.3.6.1.4.1.1466.20037 (Start TLS)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "1.3.6.1.4.1.1466.20037" ) },

                { "InPlaceOidValueEditor - INVALID",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( "cn" ).rawValue( "invalid" )
                        .expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( null ) },

                { "InPlaceOidValueEditor - bytearray 1.3.6.1.4.1.1466.20037",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( "userPassword" )
                        .rawValue( "1.3.6.1.4.1.1466.20037".getBytes( UTF_8 ) )
                        .expectedRawValue( "1.3.6.1.4.1.1466.20037" )
                        .expectedDisplayValue( "1.3.6.1.4.1.1466.20037 (Start TLS)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "1.3.6.1.4.1.1466.20037" ) },

                { "InPlaceOidValueEditor - bytearray INVALID",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( "userPassword" )
                        .rawValue( "invalid".getBytes( UTF_8 ) ).expectedRawValue( null )
                        .expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( null ) },

                // TextValueEditor

                { "TextValueEditor - empty string value",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( "cn" )
                        .rawValue( IValue.EMPTY_STRING_VALUE ).expectedRawValue( "" ).expectedDisplayValue( "" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( "" ) },

                { "TextValueEditor - empty string",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( "cn" ).rawValue( "" )
                        .expectedRawValue( "" ).expectedDisplayValue( "" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "" ) },

                { "TextValueEditor - ascii",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( "cn" ).rawValue( "a-zA+Z0.9" )
                        .expectedRawValue( "a-zA+Z0.9" ).expectedDisplayValue( "a-zA+Z0.9" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "a-zA+Z0.9" ) },

                { "TextValueEditor - ascii with newline",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( "cn" ).rawValue( "a\nb\rc" )
                        .expectedRawValue( "a\nb\rc" ).expectedDisplayValue( "a\nb\rc" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "a\nb\rc" ) },

                { "TextValueEditor - unicode",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( "cn" )
                        .rawValue( "\u00e4\u2000\n\u5047" ).expectedRawValue( "\u00e4\u2000\n\u5047" )
                        .expectedDisplayValue( "\u00e4\u2000\n\u5047" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "\u00e4\u2000\n\u5047" ) },

                { "TextValueEditor - empty binary value",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( "userPassword" )
                        .rawValue( IValue.EMPTY_BINARY_VALUE ).expectedRawValue( "" ).expectedDisplayValue( "" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( "" ) },

                { "TextValueEditor - empty bytearray",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( "userPassword" )
                        .rawValue( new byte[0] ).expectedRawValue( "" ).expectedDisplayValue( "" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( "" ) },

                { "TextValueEditor - bytearray UTF8",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( "userPassword" )
                        .rawValue( "a\nb\r\u00e4\t\u5047".getBytes( UTF_8 ) ).expectedRawValue( "a\nb\r\u00e4\t\u5047" )
                        .expectedDisplayValue( "a\nb\r\u00e4\t\u5047" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "a\nb\r\u00e4\t\u5047" ) },

                { "TextValueEditor - bytearray binary", Data.data().valueEditorClass( InPlaceTextValueEditor.class )
                    .attribute( "userPassword" ).rawValue( new byte[]
                        { ( byte ) 0x89, 0x50, 0x4E, 0x47 } )
                    .expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                    .expectedStringOrBinaryValue( null ) },

                // HexValueEditor

                { "HexValueEditor - empty string value",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( "cn" )
                        .rawValue( IValue.EMPTY_STRING_VALUE ).expectedRawValue( new byte[0] )
                        .expectedDisplayValue( "Binary Data (0 Bytes)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( new byte[0] ) },

                { "HexValueEditor - empty string",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( "cn" ).rawValue( "" )
                        .expectedRawValue( new byte[0] ).expectedDisplayValue( "Binary Data (0 Bytes)" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( new byte[0] ) },

                { "HexValueEditor - ascii",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( "cn" ).rawValue( "a-zA+Z0.9" )
                        .expectedRawValue( "a-zA+Z0.9".getBytes( StandardCharsets.US_ASCII ) )
                        .expectedDisplayValue( "Binary Data (9 Bytes)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "a-zA+Z0.9".getBytes( StandardCharsets.US_ASCII ) ) },

                { "HexValueEditor - empty binary value",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( "userPassword" )
                        .rawValue( IValue.EMPTY_BINARY_VALUE ).expectedRawValue( new byte[0] )
                        .expectedDisplayValue( "Binary Data (0 Bytes)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( new byte[0] ) },

                { "HexValueEditor - empty bytearray",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( "userPassword" )
                        .rawValue( new byte[0] ).expectedRawValue( new byte[0] )
                        .expectedDisplayValue( "Binary Data (0 Bytes)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( new byte[0] ) },

                { "HexValueEditor - bytearray UTF8",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( "userPassword" )
                        .rawValue( "a\nb\r\u00e4\t\u5047".getBytes( UTF_8 ) )
                        .expectedRawValue( "a\nb\r\u00e4\t\u5047".getBytes( UTF_8 ) )
                        .expectedDisplayValue( "Binary Data (10 Bytes)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "a\nb\r\u00e4\t\u5047".getBytes( UTF_8 ) ) },

                { "HexValueEditor - bytearray binary", Data.data().valueEditorClass( HexValueEditor.class )
                    .attribute( "userPassword" ).rawValue( new byte[]
                        { ( byte ) 0x89, 0x50, 0x4E, 0x47 } )
                    .expectedRawValue( new byte[]
                        { ( byte ) 0x89, 0x50, 0x4E, 0x47 } )
                    .expectedDisplayValue( "Binary Data (4 Bytes)" ).expectedHasValue( true )
                    .expectedStringOrBinaryValue( new byte[]
                        { ( byte ) 0x89, 0x50, 0x4E, 0x47 } ) },

            };
    }

    @Parameter(value = 0)
    public String name;

    @Parameter(value = 1)
    public Data data;

    private IValue value;

    private IValueEditor editor;


    @Before
    public void setup() throws Exception
    {
        IEntry entry = new DummyEntry( new Dn(), new DummyConnection( Schema.DEFAULT_SCHEMA ) );
        IAttribute attribute = new Attribute( entry, data.attribute );
        value = new Value( attribute, data.rawValue );
        editor = data.valueEditorClass.newInstance();
    }


    @Test
    public void testGetRawValue()
    {
        if ( data.expectedRawValue instanceof byte[] )
        {
            assertArrayEquals( ( byte[] ) data.expectedRawValue, ( byte[] ) editor.getRawValue( value ) );
        }
        else
        {
            assertEquals( data.expectedRawValue, editor.getRawValue( value ) );
        }
    }


    @Test
    public void testGetDisplayValue()
    {
        assertEquals( data.expectedDisplayValue, editor.getDisplayValue( value ) );
    }


    @Test
    public void testHasValue()
    {
        assertEquals( data.expectedHasValue, editor.hasValue( value ) );
    }


    @Test
    public void testGetStringOrBinaryValue()
    {
        if ( data.expectedStringOrBinaryValue instanceof byte[] )
        {
            assertArrayEquals( ( byte[] ) data.expectedStringOrBinaryValue,
                ( byte[] ) editor.getStringOrBinaryValue( editor.getRawValue( value ) ) );
        }
        else
        {
            assertEquals( data.expectedStringOrBinaryValue,
                editor.getStringOrBinaryValue( editor.getRawValue( value ) ) );
        }
    }

    static class Data
    {
        public Class<? extends IValueEditor> valueEditorClass;

        public String attribute;

        public Object rawValue;

        public Object expectedRawValue;

        public String expectedDisplayValue;

        public boolean expectedHasValue;

        public Object expectedStringOrBinaryValue;


        public static Data data()
        {
            return new Data();
        }


        public Data valueEditorClass( Class<? extends IValueEditor> valueEditorClass )
        {
            this.valueEditorClass = valueEditorClass;
            return this;
        }


        public Data attribute( String attribute )
        {
            this.attribute = attribute;
            return this;
        }


        public Data rawValue( Object rawValue )
        {
            this.rawValue = rawValue;
            return this;
        }


        public Data expectedRawValue( Object expectedRawValue )
        {
            this.expectedRawValue = expectedRawValue;
            return this;
        }


        public Data expectedDisplayValue( String expectedDisplayValue )
        {
            this.expectedDisplayValue = expectedDisplayValue;
            return this;
        }


        public Data expectedHasValue( boolean expectedHasValue )
        {
            this.expectedHasValue = expectedHasValue;
            return this;
        }


        public Data expectedStringOrBinaryValue( Object expectedStringOrBinaryValue )
        {
            this.expectedStringOrBinaryValue = expectedStringOrBinaryValue;
            return this;
        }

    }
}
