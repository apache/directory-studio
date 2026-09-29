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

package org.apache.directory.studio.ldifparser.parser;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeDeleteRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * Characterization tests for parsing LDIF change records.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifChangeRecordParserTest
{
    private static LdifFile parse( String ldif )
    {
        return new LdifParser().parse( ldif );
    }


    private static LdifChangeRecord parseSingleChangeRecord( String ldif )
    {
        LdifFile model = parse( ldif );

        assertTrue( model.isChangeType() );
        assertFalse( model.isContentType() );
        assertEquals( 1, model.getRecords().length );
        assertEquals( ldif, model.toRawString() );

        return ( LdifChangeRecord ) model.getRecords()[0];
    }


    @Test
    public void testChangeTypeAdd()
    {
        LdifChangeRecord record = parseSingleChangeRecord( ""
            + "dn: cn=foo,dc=example,dc=com\n"
            + "changetype: add\n"
            + "objectClass: top\n"
            + "cn: foo\n" );

        assertTrue( record instanceof LdifChangeAddRecord );
        assertTrue( record.isValid() );
        assertEquals( "cn=foo,dc=example,dc=com", record.getDnLine().getValueAsString() );

        LdifChangeTypeLine changeType = record.getChangeTypeLine();
        assertEquals( "add", changeType.getUnfoldedChangeType() );
        assertTrue( changeType.isAdd() );
        assertFalse( changeType.isDelete() );
        assertFalse( changeType.isModify() );
        assertFalse( changeType.isModDn() );

        LdifAttrValLine[] attrVals = ( ( LdifChangeAddRecord ) record ).getAttrVals();
        assertEquals( 2, attrVals.length );
        assertEquals( "objectClass", attrVals[0].getUnfoldedAttributeDescription() );
        assertEquals( "top", attrVals[0].getValueAsString() );
        assertEquals( "cn", attrVals[1].getUnfoldedAttributeDescription() );
        assertEquals( "foo", attrVals[1].getValueAsString() );
    }


    @Test
    public void testChangeTypeAddWithoutAttributesIsInvalid()
    {
        LdifChangeRecord record = parseSingleChangeRecord( "dn: cn=foo\nchangetype: add\n\n" );

        assertTrue( record instanceof LdifChangeAddRecord );
        assertFalse( record.isValid() );
    }


    @Test
    public void testChangeTypeDelete()
    {
        LdifChangeRecord record = parseSingleChangeRecord( "dn: cn=foo,dc=example,dc=com\nchangetype: delete\n\n" );

        assertTrue( record instanceof LdifChangeDeleteRecord );
        assertTrue( record.isValid() );
        assertTrue( record.getChangeTypeLine().isDelete() );
        assertFalse( record.getChangeTypeLine().isAdd() );
        assertEquals( 0, record.getControls().length );
    }


    @Test
    @Disabled("Suspected bug: a delete record at the end of the file without a trailing empty line is invalid "
        + "(LdifChangeDeleteRecord.isValid() requires the separator line, unlike all other record types), "
        + "so ImportLdifRunnable rejects it with 'Record must end with an empty line'")
    public void testChangeTypeDeleteAtEndOfFileWithoutEmptyLineIsValid()
    {
        LdifChangeRecord record = parseSingleChangeRecord( "dn: cn=foo,dc=example,dc=com\nchangetype: delete\n" );

        assertTrue( record instanceof LdifChangeDeleteRecord );
        assertTrue( record.isValid() );
    }


    @Test
    public void testChangeTypeModifyWithAllModSpecTypes()
    {
        LdifChangeRecord record = parseSingleChangeRecord( ""
            + "dn: cn=foo,dc=example,dc=com\n"
            + "changetype: modify\n"
            + "add: mail\n"
            + "mail: a@example.com\n"
            + "mail: b@example.com\n"
            + "-\n"
            + "replace: sn\n"
            + "sn: new\n"
            + "-\n"
            + "delete: description\n"
            + "-\n"
            + "replace: telephoneNumber\n"
            + "-\n"
            + "delete: mail\n"
            + "mail: a@example.com\n"
            + "-\n" );

        assertTrue( record instanceof LdifChangeModifyRecord );
        assertTrue( record.isValid() );
        assertTrue( record.getChangeTypeLine().isModify() );

        LdifModSpec[] modSpecs = ( ( LdifChangeModifyRecord ) record ).getModSpecs();
        assertEquals( 5, modSpecs.length );

        assertTrue( modSpecs[0].isAdd() );
        assertEquals( "mail", modSpecs[0].getModSpecType().getUnfoldedAttributeDescription() );
        assertEquals( 2, modSpecs[0].getAttrVals().length );
        assertEquals( "a@example.com", modSpecs[0].getAttrVals()[0].getValueAsString() );
        assertEquals( "b@example.com", modSpecs[0].getAttrVals()[1].getValueAsString() );

        assertTrue( modSpecs[1].isReplace() );
        assertEquals( "sn", modSpecs[1].getModSpecType().getUnfoldedAttributeDescription() );
        assertEquals( 1, modSpecs[1].getAttrVals().length );
        assertEquals( "new", modSpecs[1].getAttrVals()[0].getValueAsString() );

        // delete without values: delete the whole attribute
        assertTrue( modSpecs[2].isDelete() );
        assertEquals( "description", modSpecs[2].getModSpecType().getUnfoldedAttributeDescription() );
        assertEquals( 0, modSpecs[2].getAttrVals().length );

        // replace without values: delete all values
        assertTrue( modSpecs[3].isReplace() );
        assertEquals( "telephoneNumber", modSpecs[3].getModSpecType().getUnfoldedAttributeDescription() );
        assertEquals( 0, modSpecs[3].getAttrVals().length );

        // delete with a value: delete only that value
        assertTrue( modSpecs[4].isDelete() );
        assertEquals( 1, modSpecs[4].getAttrVals().length );
        assertEquals( "a@example.com", modSpecs[4].getAttrVals()[0].getValueAsString() );

        for ( LdifModSpec modSpec : modSpecs )
        {
            assertTrue( modSpec.isValid() );
            assertTrue( modSpec.getModSpecSep() != null );
        }
    }


    @Test
    public void testChangeTypeModRdn()
    {
        LdifChangeRecord record = parseSingleChangeRecord( ""
            + "dn: cn=foo,dc=example,dc=com\n"
            + "changetype: modrdn\n"
            + "newrdn: cn=bar\n"
            + "deleteoldrdn: 1\n" );

        assertTrue( record instanceof LdifChangeModDnRecord );
        assertTrue( record.isValid() );
        assertEquals( "modrdn", record.getChangeTypeLine().getUnfoldedChangeType() );
        assertTrue( record.getChangeTypeLine().isModDn() );

        LdifChangeModDnRecord modDn = ( LdifChangeModDnRecord ) record;
        assertEquals( "cn=bar", modDn.getNewrdnLine().getUnfoldedNewrdn() );
        assertTrue( modDn.getDeloldrdnLine().isDeleteOldRdn() );
        assertNull( modDn.getNewsuperiorLine() );
    }


    @Test
    public void testChangeTypeModDnWithNewSuperior()
    {
        LdifChangeRecord record = parseSingleChangeRecord( ""
            + "dn: cn=foo,dc=example,dc=com\n"
            + "changetype: moddn\n"
            + "newrdn: cn=bar\n"
            + "deleteoldrdn: 0\n"
            + "newsuperior: ou=people,dc=example,dc=com\n" );

        assertTrue( record instanceof LdifChangeModDnRecord );
        assertTrue( record.isValid() );
        assertEquals( "moddn", record.getChangeTypeLine().getUnfoldedChangeType() );
        assertTrue( record.getChangeTypeLine().isModDn() );

        LdifChangeModDnRecord modDn = ( LdifChangeModDnRecord ) record;
        assertEquals( "cn=bar", modDn.getNewrdnLine().getUnfoldedNewrdn() );
        assertFalse( modDn.getDeloldrdnLine().isDeleteOldRdn() );
        assertEquals( "ou=people,dc=example,dc=com", modDn.getNewsuperiorLine().getUnfoldedNewSuperiorDn() );
    }


    @Test
    public void testControls()
    {
        LdifChangeRecord record = parseSingleChangeRecord( ""
            + "dn: cn=foo,dc=example,dc=com\n"
            + "control: 1.2.840.113556.1.4.805 true\n"
            + "control: 1.2.3.4 false: plain value\n"
            + "control: 1.2.3.5:: AAEC\n"
            + "control: 1.2.3.6\n"
            + "changetype: delete\n"
            + "\n" );

        assertTrue( record instanceof LdifChangeDeleteRecord );
        assertTrue( record.isValid() );

        LdifControlLine[] controls = record.getControls();
        assertEquals( 4, controls.length );

        // OID and criticality, no value
        assertEquals( "1.2.840.113556.1.4.805", controls[0].getUnfoldedOid() );
        assertTrue( controls[0].isCritical() );
        assertFalse( controls[0].isControlValueTypeSafe() );
        assertFalse( controls[0].isControlValueTypeBase64() );
        assertNull( controls[0].getControlValueAsObject() );
        assertArrayEquals( new byte[0], controls[0].getControlValueAsBinary() );
        assertTrue( controls[0].isValid() );

        // OID, criticality false and a plain value
        assertEquals( "1.2.3.4", controls[1].getUnfoldedOid() );
        assertFalse( controls[1].isCritical() );
        assertTrue( controls[1].isControlValueTypeSafe() );
        assertEquals( "plain value", controls[1].getUnfoldedControlValue() );
        assertEquals( "plain value", controls[1].getControlValueAsObject() );

        // OID and a base64 value, no criticality
        assertEquals( "1.2.3.5", controls[2].getUnfoldedOid() );
        assertFalse( controls[2].isCritical() );
        assertTrue( controls[2].isControlValueTypeBase64() );
        assertArrayEquals( new byte[]
            { 0, 1, 2 }, controls[2].getControlValueAsBinary() );

        // OID only
        assertEquals( "1.2.3.6", controls[3].getUnfoldedOid() );
        assertFalse( controls[3].isCritical() );
        assertNull( controls[3].getControlValueAsObject() );
        assertTrue( controls[3].isValid() );
    }


    @Test
    public void testUnknownChangeType()
    {
        LdifFile model = parse( "dn: cn=foo\nchangetype: bogus\n" );

        assertTrue( model.isChangeType() );
        assertEquals( 1, model.getRecords().length );

        LdifChangeRecord record = ( LdifChangeRecord ) model.getRecords()[0];
        assertEquals( LdifChangeRecord.class, record.getClass() );
        assertFalse( record.getChangeTypeLine().isValid() );
        assertFalse( record.getChangeTypeLine().isAdd() );
        assertFalse( record.getChangeTypeLine().isDelete() );
        assertFalse( record.getChangeTypeLine().isModify() );
        assertFalse( record.getChangeTypeLine().isModDn() );
        assertEquals( "dn: cn=foo\nchangetype: bogus\n", model.toRawString() );
    }


    @Test
    public void testMixedContentAndChangeRecords()
    {
        String ldif = ""
            + "version: 1\n"
            + "\n"
            + "# comment\n"
            + "dn: cn=a\n"
            + "cn: a\n"
            + "\n"
            + "dn: cn=b\n"
            + "changetype: modify\n"
            + "replace: cn\n"
            + "cn: b\n"
            + "-\n"
            + "\n"
            + "dn: cn=c\n"
            + "changetype: delete\n"
            + "\n";

        LdifFile model = parse( ldif );

        // a single change record makes the whole file a change file
        assertTrue( model.isChangeType() );
        assertEquals( 6, model.getContainers().size() );
        assertEquals( 3, model.getRecords().length );
        assertTrue( model.getRecords()[0] instanceof LdifContentRecord );
        assertTrue( model.getRecords()[1] instanceof LdifChangeModifyRecord );
        assertTrue( model.getRecords()[2] instanceof LdifChangeDeleteRecord );
        for ( int i = 0; i < 3; i++ )
        {
            assertTrue( model.getRecords()[i].isValid(), "record " + i );
        }

        assertEquals( ldif, model.toRawString() );
    }
}
