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


import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

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
import org.apache.directory.studio.valueeditors.address.AddressValueEditor;
import org.apache.directory.studio.valueeditors.bool.InPlaceBooleanValueEditor;
import org.apache.directory.studio.valueeditors.oid.InPlaceOidValueEditor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


/**
 * Tests the value editors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ValueEditorTest extends AbstractTestBase
{

    private static final String CN = "cn";
    private static final String POSTAL = "postalAddress";
    private static final String USER_PWD = "userPassword";

    private static final String EMPTY_STRING = "";
    private static final String ASCII = "a-zA+Z0.9";
    private static final String UNICODE = "a-z\nA+Z\r0.9\t\u00e4\u00f6\u00fc\u00df \u2000\u3000\u5047";

    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final byte[] ISO88591 = "\u00e4\u00f6\u00fc\u00df".getBytes( ISO_8859_1 );
    private static final byte[] UTF8 = UNICODE.getBytes( UTF_8 );
    private static final byte[] PNG = new byte[]
        { ( byte ) 0x89, 0x50, 0x4E, 0x47 };

    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";

    private static final String NUMERIC_OID = "1.3.6.1.4.1.1466.20037";
    private static final String DESCR_OID = "a-zA-Z0-9";

    private static final String ADDRESS_DISPLAY = "$1,000,000 Sweepstakes, PO Box 1000000, Anytown, CA 12345, USA";
    private static final String ADDRESS_RAW = "\\241,000,000 Sweepstakes$PO Box 1000000$Anytown, CA 12345$USA";

    public static Stream<Arguments> data()
    {
        return Stream.of( new Object[][]
            {
                /*
                 * InPlaceTextValueEditor can handle string values and binary values that can be decoded as UTF-8.
                 */

                {
                    "InPlaceTextValueEditor - empty value",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( CN )
                        .rawValue( IValue.EMPTY_STRING_VALUE ).expectedRawValue( EMPTY_STRING )
                        .expectedDisplayValue( EMPTY_STRING ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( EMPTY_STRING ) },

                {
                    "InPlaceTextValueEditor - empty string",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( CN )
                        .rawValue( EMPTY_STRING ).expectedRawValue( EMPTY_STRING ).expectedDisplayValue( EMPTY_STRING )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( EMPTY_STRING ) },

                {
                    "InPlaceTextValueEditor - ascii",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( CN ).rawValue( ASCII )
                        .expectedRawValue( ASCII ).expectedDisplayValue( ASCII ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( ASCII ) },

                {
                    "InPlaceTextValueEditor - unicode",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( CN ).rawValue( UNICODE )
                        .expectedRawValue( UNICODE ).expectedDisplayValue( UNICODE ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( UNICODE ) },

                {
                    "InPlaceTextValueEditor - bytearray UTF8",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( USER_PWD ).rawValue( UTF8 )
                        .expectedRawValue( UNICODE ).expectedDisplayValue( UNICODE ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( UNICODE ) },

                // text editor always tries to decode byte[] as UTF-8, so it can not handle ISO-8859-1 encoded byte[] 
                {
                    "InPlaceTextValueEditor - bytearray ISO-8859-1",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( USER_PWD )
                        .rawValue( ISO88591 ).expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( null ) },

                // text editor always tries to decode byte[] as UTF-8, so it can not handle arbitrary byte[] 
                {
                    "InPlaceTextValueEditor - bytearray PNG",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( USER_PWD ).rawValue( PNG )
                        .expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( null ) },

                /*
                 * InPlaceBooleanValueEditor can only handle TRUE or FALSE values.
                 */

                {
                    "InPlaceBooleanValueEditor - TRUE",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( CN ).rawValue( TRUE )
                        .expectedRawValue( TRUE ).expectedDisplayValue( TRUE ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( TRUE ) },

                {
                    "InPlaceBooleanValueEditor - FALSE",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( CN ).rawValue( FALSE )
                        .expectedRawValue( FALSE ).expectedDisplayValue( FALSE ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( FALSE ) },

                {
                    "InPlaceBooleanValueEditor - INVALID",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( CN )
                        .rawValue( "invalid" ).expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( null ) },

                {
                    "InPlaceBooleanValueEditor - bytearray TRUE",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( USER_PWD )
                        .rawValue( TRUE.getBytes( UTF_8 ) ).expectedRawValue( TRUE ).expectedDisplayValue( TRUE )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( TRUE ) },

                {
                    "InPlaceBooleanValueEditor - bytearray FALSE",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( USER_PWD )
                        .rawValue( FALSE.getBytes( UTF_8 ) ).expectedRawValue( FALSE ).expectedDisplayValue( FALSE )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( FALSE ) },

                {
                    "InPlaceBooleanValueEditor - bytearray INVALID",
                    Data.data().valueEditorClass( InPlaceBooleanValueEditor.class ).attribute( USER_PWD )
                        .rawValue( "invalid".getBytes( UTF_8 ) ).expectedRawValue( null )
                        .expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( null ) },

                /*
                 * InPlaceOidValueEditor can only handle OIDs
                 */

                {
                    "InPlaceOidValueEditor - numeric OID",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( CN ).rawValue( NUMERIC_OID )
                        .expectedRawValue( NUMERIC_OID ).expectedDisplayValue( NUMERIC_OID + " (Start TLS)" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( NUMERIC_OID ) },

                {
                    "InPlaceOidValueEditor - descr OID",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( CN ).rawValue( DESCR_OID )
                        .expectedRawValue( DESCR_OID ).expectedDisplayValue( DESCR_OID ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( DESCR_OID ) },

                {
                    "InPlaceOidValueEditor - relaxed descr OID",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( CN )
                        .rawValue( "orclDBEnterpriseRole_82" ).expectedRawValue( "orclDBEnterpriseRole_82" )
                        .expectedDisplayValue( "orclDBEnterpriseRole_82" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( "orclDBEnterpriseRole_82" ) },

                {
                    "InPlaceOidValueEditor - INVALID",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( CN ).rawValue( "in valid" )
                        .expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( null ) },

                {
                    "InPlaceOidValueEditor - bytearray numeric OID",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( USER_PWD )
                        .rawValue( NUMERIC_OID.getBytes( UTF_8 ) ).expectedRawValue( NUMERIC_OID )
                        .expectedDisplayValue( NUMERIC_OID + " (Start TLS)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( NUMERIC_OID ) },

                {
                    "InPlaceOidValueEditor - bytearray INVALID",
                    Data.data().valueEditorClass( InPlaceOidValueEditor.class ).attribute( USER_PWD )
                        .rawValue( "in valid".getBytes( UTF_8 ) ).expectedRawValue( null )
                        .expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( null ) },

                /*
                 * TextValueEditor can handle string values and binary values that can be decoded as UTF-8.
                 */

                {
                    "TextValueEditor - empty string value",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( CN )
                        .rawValue( IValue.EMPTY_STRING_VALUE ).expectedRawValue( EMPTY_STRING )
                        .expectedDisplayValue( EMPTY_STRING ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( EMPTY_STRING ) },

                {
                    "TextValueEditor - empty string",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( CN ).rawValue( EMPTY_STRING )
                        .expectedRawValue( EMPTY_STRING ).expectedDisplayValue( EMPTY_STRING ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( EMPTY_STRING ) },

                {
                    "TextValueEditor - ascii",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( CN ).rawValue( ASCII )
                        .expectedRawValue( ASCII ).expectedDisplayValue( ASCII ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( ASCII ) },

                {
                    "TextValueEditor - unicode",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( CN ).rawValue( UNICODE )
                        .expectedRawValue( UNICODE ).expectedDisplayValue( UNICODE ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( UNICODE ) },

                {
                    "TextValueEditor - empty binary value",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( USER_PWD )
                        .rawValue( IValue.EMPTY_BINARY_VALUE ).expectedRawValue( EMPTY_STRING )
                        .expectedDisplayValue( EMPTY_STRING ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( EMPTY_STRING ) },

                {
                    "TextValueEditor - empty bytearray",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( USER_PWD ).rawValue( EMPTY_BYTES )
                        .expectedRawValue( EMPTY_STRING ).expectedDisplayValue( EMPTY_STRING ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( EMPTY_STRING ) },

                {
                    "TextValueEditor - bytearray UTF8",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( USER_PWD ).rawValue( UTF8 )
                        .expectedRawValue( UNICODE ).expectedDisplayValue( UNICODE ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( UNICODE ) },

                // text editor always tries to decode byte[] as UTF-8, so it can not handle ISO-8859-1 encoded byte[] 
                {
                    "TextValueEditor - bytearray ISO-8859-1",
                    Data.data().valueEditorClass( TextValueEditor.class ).attribute( USER_PWD ).rawValue( ISO88591 )
                        .expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( null ) },

                // text editor always tries to decode byte[] as UTF-8, so it can not handle arbitrary byte[]
                {
                    "TextValueEditor - bytearray PNG",
                    Data.data().valueEditorClass( InPlaceTextValueEditor.class ).attribute( USER_PWD ).rawValue( PNG )
                        .expectedRawValue( null ).expectedDisplayValue( IValueEditor.NULL ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( null ) },

                /*
                 * HexValueEditor can handle all string or binary values.
                 */

                {
                    "HexValueEditor - empty string value",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( CN )
                        .rawValue( IValue.EMPTY_STRING_VALUE ).expectedRawValue( EMPTY_BYTES )
                        .expectedDisplayValue( "Binary Data (0 Bytes)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( EMPTY_BYTES ) },

                {
                    "HexValueEditor - empty string",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( CN ).rawValue( EMPTY_STRING )
                        .expectedRawValue( EMPTY_BYTES ).expectedDisplayValue( "Binary Data (0 Bytes)" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( EMPTY_BYTES ) },

                {
                    "HexValueEditor - ascii",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( CN ).rawValue( ASCII )
                        .expectedRawValue( ASCII.getBytes( StandardCharsets.US_ASCII ) )
                        .expectedDisplayValue( "Binary Data (9 Bytes)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( ASCII.getBytes( StandardCharsets.US_ASCII ) ) },

                {
                    "HexValueEditor - empty binary value",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( USER_PWD )
                        .rawValue( IValue.EMPTY_BINARY_VALUE ).expectedRawValue( EMPTY_BYTES )
                        .expectedDisplayValue( "Binary Data (0 Bytes)" ).expectedHasValue( true )
                        .expectedStringOrBinaryValue( EMPTY_BYTES ) },

                {
                    "HexValueEditor - empty bytearray",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( USER_PWD ).rawValue( EMPTY_BYTES )
                        .expectedRawValue( EMPTY_BYTES ).expectedDisplayValue( "Binary Data (0 Bytes)" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( EMPTY_BYTES ) },

                {
                    "HexValueEditor - bytearray UTF8",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( USER_PWD ).rawValue( UTF8 )
                        .expectedRawValue( UTF8 ).expectedDisplayValue( "Binary Data (30 Bytes)" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( UTF8 ) },

                {
                    "HexValueEditor - bytearray ISO-8859-1",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( USER_PWD ).rawValue( ISO88591 )
                        .expectedRawValue( ISO88591 ).expectedDisplayValue( "Binary Data (4 Bytes)" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( ISO88591 ) },

                {
                    "HexValueEditor - bytearray PNG",
                    Data.data().valueEditorClass( HexValueEditor.class ).attribute( USER_PWD ).rawValue( PNG )
                        .expectedRawValue( PNG ).expectedDisplayValue( "Binary Data (4 Bytes)" )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( PNG ) },

                /*
                 * AddressValueEditor can handle a multi-line postal address with escaped characters.
                 */

                {
                    "AddressValueEditor - RFC example",
                    Data.data().valueEditorClass( AddressValueEditor.class ).attribute( POSTAL ).rawValue( ADDRESS_RAW )
                        .expectedRawValue( ADDRESS_RAW ).expectedDisplayValue( ADDRESS_DISPLAY )
                        .expectedHasValue( true ).expectedStringOrBinaryValue( ADDRESS_RAW ) },
            } ).map( d -> Arguments.arguments( ( String ) d[0], ( Data ) d[1] ) );
    }

    private IValue value;

    private IValueEditor editor;

    public void setup( String name, Data data ) throws Exception
    {
        IEntry entry = new DummyEntry( new Dn(), new DummyConnection( Schema.DEFAULT_SCHEMA ) );
        IAttribute attribute = new Attribute( entry, data.attribute );
        value = new Value( attribute, data.rawValue );
        editor = data.valueEditorClass.newInstance();
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testGetRawValue( String name, Data data ) throws Exception
    {
        setup( name, data );
        if ( data.expectedRawValue instanceof byte[] )
        {
            assertArrayEquals( ( byte[] ) data.expectedRawValue, ( byte[] ) editor.getRawValue( value ) );
        }
        else
        {
            assertEquals( data.expectedRawValue, editor.getRawValue( value ) );
        }
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testGetDisplayValue( String name, Data data ) throws Exception
    {
        setup( name, data );
        assertEquals( data.expectedDisplayValue, editor.getDisplayValue( value ) );
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testHasValue( String name, Data data ) throws Exception
    {
        setup( name, data );
        assertEquals( data.expectedHasValue, editor.hasValue( value ) );
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testGetStringOrBinaryValue( String name, Data data ) throws Exception
    {
        setup( name, data );
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
