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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ModifyMode;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ComputeDiffTest
{
    private IBrowserConnection connection;

    private IEntry oldEntry;
    private IEntry newEntry;

    static class TestConnection extends DummyConnection
    {
        private static final long serialVersionUID = 1L;

        private ModifyMode modifyMode = ModifyMode.DEFAULT;
        private ModifyMode modifyModeNoEMR = ModifyMode.DEFAULT;

        public TestConnection()
        {
            super( Schema.DEFAULT_SCHEMA );
        }


        public ModifyMode getModifyMode()
        {
            return modifyMode;
        }


        public void setModifyMode( ModifyMode mode )
        {
            this.modifyMode = mode;
        }


        public ModifyMode getModifyModeNoEMR()
        {
            return modifyModeNoEMR;
        }


        public void setModifyModeNoEMR( ModifyMode mode )
        {
            this.modifyModeNoEMR = mode;
        }

    }

    @BeforeEach
    public void setup() throws Exception
    {
        ConnectionEventRegistry.suspendEventFiringInCurrentThread();
        connection = new TestConnection();
        oldEntry = new DummyEntry( new Dn( "cn=foo" ), connection );
        newEntry = new DummyEntry( new Dn( "cn=foo" ), connection );
    }


    @Test
    public void shouldReturnNullForEqualEntries()
    {
        // entries without attribute
        assertThat( Utils.computeDiff( oldEntry, newEntry ), nullValue() );
        assertThat( Utils.computeDiff( oldEntry, oldEntry ), nullValue() );
        assertThat( Utils.computeDiff( newEntry, newEntry ), nullValue() );

        // entries with attributes
        addAttribute( oldEntry, "cn", "1" );
        addAttribute( oldEntry, "member", "cn=1", "cn=2", "cn=3" );
        addAttribute( newEntry, "cn", "1" );
        addAttribute( newEntry, "member", "cn=1", "cn=2", "cn=3" );
        assertThat( Utils.computeDiff( oldEntry, newEntry ), nullValue() );
        assertThat( Utils.computeDiff( oldEntry, oldEntry ), nullValue() );
        assertThat( Utils.computeDiff( newEntry, newEntry ), nullValue() );
    }


    @Test
    public void shouldAddOneAttributeWithOneValue()
    {
        addAttribute( newEntry, "cn", "1" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "add:cn", "cn:1" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "replace:cn", "cn:1" );
    }


    @Test
    public void shouldAddMultipleAttributeWithMultipleValues()
    {
        addAttribute( newEntry, "cn", "1", "2" );
        addAttribute( newEntry, "member", "cn=1", "cn=2", "cn=3" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ),
            "add:cn", "cn:1", "cn:2", "-", "add:member", "member:cn=1", "member:cn=2", "member:cn=3" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ),
            "replace:cn", "cn:1", "cn:2", "-", "replace:member", "member:cn=1", "member:cn=2", "member:cn=3" );
    }


    @Test
    public void shouldAddOneValueToOneExistingAttribute()
    {
        addAttribute( oldEntry, "cn", "1" );
        addAttribute( newEntry, "cn", "1", "2" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "add:cn", "cn:2" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "replace:cn", "cn:1", "cn:2" );
    }


    @Test
    public void shouldAddMultipleValuesToMultipleExistingAttributes()
    {
        addAttribute( oldEntry, "cn", "1" );
        addAttribute( newEntry, "cn", "1", "2", "3" );
        addAttribute( oldEntry, "member", "cn=1", "cn=2", "cn=3" );
        addAttribute( newEntry, "member", "cn=1", "cn=2", "cn=3", "cn=4", "cn=5" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ),
            "add:cn", "cn:2", "cn:3", "-", "add:member", "member:cn=4", "member:cn=5" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ),
            "replace:cn", "cn:1", "cn:2", "cn:3", "-",
            "replace:member", "member:cn=1", "member:cn=2", "member:cn=3", "member:cn=4", "member:cn=5" );
    }


    @Test
    public void shouldDeleteAllOneValue()
    {
        addAttribute( oldEntry, "cn", "1" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "delete:cn", "cn:1" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "replace:cn" );
    }


    @Test
    public void shouldDeleteAllMultipleValues()
    {
        addAttribute( oldEntry, "cn", "1", "2" );
        addAttribute( oldEntry, "member", "cn=1", "cn=2", "cn=3" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ),
            "delete:cn", "cn:1", "cn:2", "-",
            "delete:member", "member:cn=1", "member:cn=2", "member:cn=3" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "replace:cn", "-", "replace:member" );
    }


    @Test
    public void shouldDeleteOneValue()
    {
        addAttribute( oldEntry, "cn", "1", "2", "3" );
        addAttribute( newEntry, "cn", "1", "2" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "delete:cn", "cn:3" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "replace:cn", "cn:1", "cn:2" );
    }


    @Test
    public void shouldDeleteMultipleValues()
    {
        addAttribute( oldEntry, "cn", "1", "2", "3" );
        addAttribute( newEntry, "cn", "1" );
        addAttribute( oldEntry, "member", "cn=1", "cn=2", "cn=3", "cn=4", "cn=5" );
        addAttribute( newEntry, "member", "cn=1" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ),
            "delete:cn", "cn:2", "cn:3", "-",
            "delete:member", "member:cn=2", "member:cn=3", "member:cn=4", "member:cn=5" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ),
            "replace:cn", "cn:1", "-", "replace:member", "member:cn=1" );
    }


    @Test
    public void shouldReplaceOneValue()
    {
        addAttribute( oldEntry, "cn", "1" );
        addAttribute( newEntry, "cn", "2" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "delete:cn", "cn:1", "-", "add:cn", "cn:2" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ), "replace:cn", "cn:2" );
    }


    @Test
    public void shouldReplaceMultipleValues()
    {
        addAttribute( oldEntry, "cn", "1", "2", "3" );
        addAttribute( newEntry, "cn", "4" );
        addAttribute( oldEntry, "member", "cn=1", "cn=2", "cn=3" );
        addAttribute( newEntry, "member", "cn=1", "cn=4", "cn=5" );

        connection.setModifyMode( ModifyMode.DEFAULT );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ),
            "delete:cn", "cn:1", "cn:2", "cn:3", "-", "add:cn", "cn:4", "-",
            "delete:member", "member:cn=2", "member:cn=3", "-", "add:member", "member:cn=4", "member:cn=5" );

        connection.setModifyMode( ModifyMode.REPLACE );
        assertChangeModify( Utils.computeDiff( oldEntry, newEntry ),
            "replace:cn", "cn:4", "-",
            "replace:member", "member:cn=1", "member:cn=4", "member:cn=5" );
    }


    private static void addAttribute( IEntry entry, String attributeName, Object... rawValues )
    {
        Attribute attribute = new Attribute( entry, attributeName );
        entry.addAttribute( attribute );
        for ( Object rawValue : rawValues )
        {
            Value value = new Value( attribute, rawValue );
            attribute.addValue( value );
        }
    }


    private void assertChangeModify( LdifFile diff, String... lines )
    {
        assertThat( diff.isChangeType(), equalTo( true ) );
        assertThat( diff.getContainers(), hasSize( 1 ) );
        assertThat( diff.getLastContainer(), instanceOf( LdifChangeModifyRecord.class ) );

        String s = "changetype:modify" + LdifParserConstants.LINE_SEPARATOR;
        for ( String line : lines )
        {
            assertThat( diff.toRawString(), containsString( line ) );
            s += line + LdifParserConstants.LINE_SEPARATOR;
        }
        s += "-" + LdifParserConstants.LINE_SEPARATOR;
        assertThat( diff.toRawString(), containsString( s ) );
    }

}
