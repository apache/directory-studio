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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.BJENSEN_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.GROUP1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.HNELSON_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MULTI_VALUED_RDN_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.constants.AuthenticationLevel;
import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.password.PasswordUtil;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ModifyMode;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource.Mode;
import org.apache.directory.studio.test.integration.ui.bots.AciItemEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.AddressEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.CertificateEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.DnEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EditAttributeWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.HexEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ImageEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewAttributeWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.PasswordEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SelectDnDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SubtreeSpecificationEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.TextEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.utils.Characters;
import org.apache.directory.studio.test.integration.ui.utils.JobWatcher;
import org.apache.directory.studio.test.integration.ui.utils.ResourceUtils;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorTest extends AbstractTestBase
{

    /**
     * Test adding, editing and deleting of attributes in the entry editor.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testAddEditDeleteAttribute( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( BJENSEN_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( BJENSEN_DN.getName() );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: " + BJENSEN_DN.getName(), dn );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertEquals( "", modificationLogsViewBot.getModificationLogsText() );

        // add description attribute
        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "description" );
        wizardBot.clickFinishButton();
        entryEditorBot.typeValueAndFinish( "This is the 1st description." );
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        modificationLogsViewBot.waitForText( "add: description\ndescription: This is the 1st description." );

        // add second value
        entryEditorBot.activate();
        entryEditorBot.addValue( "description" );
        entryEditorBot.typeValueAndFinish( "This is the 2nd description." );
        assertEquals( 10, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 2nd description." ) );
        modificationLogsViewBot.waitForText( "add: description\ndescription: This is the 2nd description." );

        // edit second value
        entryEditorBot.editValue( "description", "This is the 2nd description." );
        entryEditorBot.typeValueAndFinish( "This is the 3rd description." );
        assertEquals( 10, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: This is the 2nd description." ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 3rd description." ) );
        modificationLogsViewBot.waitForText( "delete: description\ndescription: This is the 2nd description." );
        modificationLogsViewBot.waitForText( "add: description\ndescription: This is the 3rd description." );

        // delete second value
        entryEditorBot.deleteValue( "description", "This is the 3rd description." );
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: This is the 3rd description." ) );
        modificationLogsViewBot.waitForText( "delete: description\ndescription: This is the 3rd description." );

        // edit 1st value
        entryEditorBot.editValue( "description", "This is the 1st description." );
        entryEditorBot.typeValueAndFinish( "This is the final description." );
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the final description." ) );
        modificationLogsViewBot.waitForText( "delete: description\ndescription: This is the 1st description." );
        modificationLogsViewBot.waitForText( "add: description\ndescription: This is the final description." );

        // delete 1st value/attribute
        entryEditorBot.deleteValue( "description", "This is the final description." );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: This is the final description." ) );
        modificationLogsViewBot.waitForText( "delete: description\ndescription: This is the final description.\n-" );

        assertEquals( 6, StringUtils.countMatches( modificationLogsViewBot.getModificationLogsText(), "#!RESULT OK" ),
            "Expected 6 modifications." );
    }


    /**
     * Test adding, editing and deleting of attributes without equality matching rule in the entry editor.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testAddEditDeleteAttributeWithoutEqualityMatchingRule( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );

        if ( server.getType() == LdapServerType.OpenLdap )
        {
            IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnection( connection );
            browserConnection.setModifyModeNoEMR( ModifyMode.REPLACE );
        }

        browserViewBot.selectEntry( path( BJENSEN_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( BJENSEN_DN.getName() );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: " + BJENSEN_DN.getName(), dn );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertEquals( "", modificationLogsViewBot.getModificationLogsText() );

        // add facsimileTelephoneNumber attribute
        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "facsimileTelephoneNumber" );
        wizardBot.clickFinishButton();
        entryEditorBot.typeValueAndFinish( "+1 234 567 890" );
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "facsimileTelephoneNumber: +1 234 567 890" ) );
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            modificationLogsViewBot
                .waitForText( "replace: facsimileTelephoneNumber\nfacsimileTelephoneNumber: +1 234 567 890" );

        }
        else
        {
            modificationLogsViewBot
                .waitForText( "add: facsimileTelephoneNumber\nfacsimileTelephoneNumber: +1 234 567 890" );

        }

        // edit value
        entryEditorBot.editValue( "facsimileTelephoneNumber", "+1 234 567 890" );
        entryEditorBot.typeValueAndFinish( "000000000000" );
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "facsimileTelephoneNumber: +1 234 567 890" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "facsimileTelephoneNumber: 000000000000" ) );
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            modificationLogsViewBot
                .waitForText( "replace: facsimileTelephoneNumber\nfacsimileTelephoneNumber: 000000000000" );

        }
        else
        {
            modificationLogsViewBot
                .waitForText( "delete: facsimileTelephoneNumber\nfacsimileTelephoneNumber: +1 234 567 890" );
            modificationLogsViewBot
                .waitForText( "add: facsimileTelephoneNumber\nfacsimileTelephoneNumber: 000000000000" );
        }

        // delete 1st value/attribute
        entryEditorBot.deleteValue( "facsimileTelephoneNumber", "000000000000" );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "facsimileTelephoneNumber: 000000000000" ) );
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            modificationLogsViewBot.waitForText( "replace: facsimileTelephoneNumber\n-" );

        }
        else
        {
            modificationLogsViewBot
                .waitForText( "delete: facsimileTelephoneNumber\nfacsimileTelephoneNumber: 000000000000\n-" );
        }

        assertEquals( 3, StringUtils.countMatches( modificationLogsViewBot.getModificationLogsText(), "#!RESULT OK" ),
            "Expected 3 modifications." );
    }


    /**
     * DIRSTUDIO-1267:Test adding, editing and deleting of attributes with language tag in the entry editor.
     */
    @ParameterizedTest
    @LdapServersSource(except = LdapServerType.ApacheDS, reason = "Language tags not yet supported by ApacheDS")
    public void testAddEditDeleteAttributeWithLanguageTag( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( USER1_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();

        // add attribute description;lang-en
        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "description" );
        wizardBot.clickNextButton();
        wizardBot.setLanguageTag( "en", "" );
        wizardBot.clickFinishButton();
        entryEditorBot.typeValueAndFinish( "English" );
        modificationLogsViewBot.waitForText( "add: description;lang-en\ndescription;lang-en: English\n-" );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description;lang-en: English" ) );

        // edit the attribute to description;lang-en
        EditAttributeWizardBot editWizardBot = entryEditorBot.editAttribute( "description;lang-en", "English" );
        editWizardBot.clickNextButton();
        editWizardBot.setLanguageTag( "en", "us" );
        editWizardBot.clickFinishButton();
        modificationLogsViewBot.waitForText( "delete: description;lang-en\ndescription;lang-en: English\n-" );
        modificationLogsViewBot.waitForText( "add: description;lang-en-us\ndescription;lang-en-us: English\n-" );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description;lang-en: English" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description;lang-en-us: English" ) );

        // edit the value
        entryEditorBot.editValue( "description;lang-en-us", "English" );
        entryEditorBot.typeValueAndFinish( "English US" );
        modificationLogsViewBot.waitForText( "delete: description;lang-en-us\ndescription;lang-en-us: English\n-" );
        modificationLogsViewBot.waitForText( "add: description;lang-en-us\ndescription;lang-en-us: English US\n-" );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description;lang-en-us: English" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description;lang-en-us: English US" ) );

        // delete the attribute
        entryEditorBot.deleteValue( "description;lang-en-us", "English US" );
        modificationLogsViewBot.waitForText( "delete: description;lang-en-us\ndescription;lang-en-us: English US\n-" );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description;lang-en-us: English US" ) );
    }


    /**
     * DIRSTUDIO-483: DN Editor escapes all non-ascii characters
     */
    @ParameterizedTest
    @LdapServersSource
    public void testDnValueEditor( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( GROUP1_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( GROUP1_DN.getName() );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: " + GROUP1_DN.getName(), dn );
        assertEquals( 12, entryEditorBot.getAttributeValues().size() );

        // add member attribute
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "member" );
        DnEditorDialogBot dnEditorBot = wizardBot.clickFinishButtonExpectingDnEditor();
        assertTrue( dnEditorBot.isVisible() );
        SelectDnDialogBot selectDnBot = dnEditorBot.clickBrowseButtonExpectingSelectDnDialog();
        assertTrue( selectDnBot.isVisible() );
        selectDnBot.selectEntry( ArrayUtils.remove( path( MULTI_VALUED_RDN_DN ), 0 ) );
        selectDnBot.clickOkButton();
        assertEquals( MULTI_VALUED_RDN_DN.getName(), dnEditorBot.getDnText() );
        dnEditorBot.clickOkButton();

        // assert value after saved and reloaded from server
        assertEquals( 13, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "member: " + MULTI_VALUED_RDN_DN.getName() ) );
        dnEditorBot = entryEditorBot.editValueExpectingDnEditor( "member", MULTI_VALUED_RDN_DN.getName() );
        assertEquals( MULTI_VALUED_RDN_DN.getName(), dnEditorBot.getDnText() );
        dnEditorBot.clickCancelButton();

        modificationLogsViewBot.waitForText( "#!RESULT OK" );
        assertEquals( 1, StringUtils.countMatches( modificationLogsViewBot.getModificationLogsText(), "#!RESULT OK" ),
            "Expected 1 modification." );
    }


    /**
     * DIRSTUDIO-637: copy/paste of attributes no longer works.
     * Test copy/paste within entry editor.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteStringValue( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( BJENSEN_DN ) );

        // copy a value
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( BJENSEN_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.copyValue( "uid", "bjensen" );

        // go to another entry
        browserViewBot.selectEntry( path( USER1_DN ) );
        entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        assertEquals( 23, entryEditorBot.getAttributeValues().size() );

        // paste value, wait till job is done
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__execute_ldif_name );
        entryEditorBot.pasteValue();
        watcher.waitUntilDone();

        // assert pasted value visible in editor
        assertEquals( 24, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "uid: bjensen" ),
            "Should contain uid=bjensen: " + entryEditorBot.getAttributeValues() );

        // assert pasted value was written to directory
        server.withAdminConnection( conn -> {
            Entry entry = conn.lookup( USER1_DN );
            assertTrue( entry.contains( "uid", "bjensen" ), "Should contain uid=bjensen: " + entry );

        } );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteMultipleStringAndBinaryValues( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( HNELSON_DN ) );

        // copy the values
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( HNELSON_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.copyValues( "userPassword", "uid", "description", "jpegPhoto" );

        // go to another entry
        browserViewBot.selectEntry( path( USER1_DN ) );
        entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        assertEquals( 23, entryEditorBot.getAttributeValues().size() );

        // paste values, wait till job is done
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__execute_ldif_name );
        entryEditorBot.pasteValues();
        watcher.waitUntilDone();

        // assert pasted values are visible in editor
        assertEquals( 27, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "uid: hnelson" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: " + Characters.ALL ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "jpegPhoto: JPEG-Image (1x1 Pixel, 631 Bytes)" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "userPassword: SSHA-512 hashed password" ) );

        // assert pasted values were written to directory
        server.withAdminConnection( conn -> {
            Entry entry = conn.lookup( USER1_DN );
            assertTrue( entry.contains( "uid", "hnelson" ), "Should contain uid=hnelson: " + entry );
            assertTrue( entry.contains( "description", Characters.ALL ), "Should contain description: " + entry );
            assertTrue( entry.containsAttribute( "userPassword" ), "Should contain userPassword: " + entry );
            assertTrue( entry.containsAttribute( "jpegPhoto" ), "Should contain jpegPhoto: " + entry );
        } );
    }


    /**
     * DIRSTUDIO-738: Add support for modular crypt format password
     */
    @ParameterizedTest
    @LdapServersSource(mode=Mode.All)
    public void testPasswordValueEditor( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( BJENSEN_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( BJENSEN_DN.getName() );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: " + BJENSEN_DN.getName(), dn );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertEquals( "", modificationLogsViewBot.getModificationLogsText() );

        // add userPassword attribute
        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "userPassword" );
        PasswordEditorDialogBot pwdEditorBot = wizardBot.clickFinishButtonExpectingPasswordEditor();
        assertTrue( pwdEditorBot.isVisible() );

        String random = RandomStringUtils.random( 20 );
        pwdEditorBot.setNewPassword1( random );
        pwdEditorBot.setNewPassword2( random );
        pwdEditorBot.setShowNewPasswordDetails( true );

        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_MD5, PasswordUtil.MD5_LENGTH, 0 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SMD5, PasswordUtil.MD5_LENGTH, 8 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SHA, PasswordUtil.SHA1_LENGTH, 0 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SSHA, PasswordUtil.SHA1_LENGTH, 8 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SHA256, PasswordUtil.SHA256_LENGTH, 0 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SSHA256, PasswordUtil.SHA256_LENGTH, 8 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SHA384, PasswordUtil.SHA384_LENGTH, 0 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SSHA384, PasswordUtil.SHA384_LENGTH, 8 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SHA512, PasswordUtil.SHA512_LENGTH, 0 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SSHA512, PasswordUtil.SHA512_LENGTH, 8 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_PKCS5S2, PasswordUtil.PKCS5S2_LENGTH, 16 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_CRYPT, PasswordUtil.CRYPT_LENGTH, 2 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_CRYPT_MD5, PasswordUtil.CRYPT_MD5_LENGTH, 8 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_CRYPT_SHA256,
            PasswordUtil.CRYPT_SHA256_LENGTH, 8 );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_CRYPT_SHA512,
            PasswordUtil.CRYPT_SHA512_LENGTH, 8 );

        pwdEditorBot.clickOkButton();

        // assert value after saved and reloaded from server
        assertTrue( entryEditorBot.getAttributeValues().contains( "userPassword: CRYPT-SHA-512 hashed password" ) );

        // verify and bind with the correct password
        pwdEditorBot = entryEditorBot.editValueExpectingPasswordEditor( "userPassword",
            "CRYPT-SHA-512 hashed password" );
        pwdEditorBot.activateCurrentPasswordTab();
        pwdEditorBot.setVerifyPassword( random );
        assertNull( pwdEditorBot.clickVerifyButton() );
        assertNull( pwdEditorBot.clickBindButton() );

        // verify and bind with the wrong password
        pwdEditorBot.activateCurrentPasswordTab();
        pwdEditorBot.setVerifyPassword( "Wrong Password" );
        assertEquals( "Password verification failed", pwdEditorBot.clickVerifyButton() );
        assertThat( pwdEditorBot.clickBindButton(), startsWith( "The authentication failed" ) );

        pwdEditorBot.clickCancelButton();

        // set a new password
        pwdEditorBot = entryEditorBot.editValueExpectingPasswordEditor( "userPassword",
            "CRYPT-SHA-512 hashed password" );
        pwdEditorBot.activateNewPasswordTab();
        random = RandomStringUtils.random( 20 );
        pwdEditorBot.setNewPassword1( random );
        pwdEditorBot.setNewPassword2( random );
        pwdEditorBot.setShowNewPasswordDetails( true );
        assertHashMethod( pwdEditorBot, LdapSecurityConstants.HASH_METHOD_SSHA256, PasswordUtil.SHA256_LENGTH, 8 );
        pwdEditorBot.clickOkButton();
        assertTrue( entryEditorBot.getAttributeValues().contains( "userPassword: SSHA-256 hashed password" ) );
    }
    private void assertHashMethod( PasswordEditorDialogBot passwordEditorBot, LdapSecurityConstants hashMethod,
        int passwordLength, int saltLength ) throws Exception
    {
        passwordEditorBot.selectHashMethod( hashMethod );

        String preview = passwordEditorBot.getPasswordPreview();
        assertThat( preview, startsWith( "{" + Strings.upperCase( hashMethod.getPrefix() ) + "}" ) );

        String passwordHex = passwordEditorBot.getPasswordHex();
        assertEquals( passwordLength * 2, passwordHex.length() );
        assertTrue( passwordHex.matches( "[0-9a-f]{" + ( passwordLength * 2 ) + "}" ) );

        String saltHex = passwordEditorBot.getSaltHex();
        if ( saltLength > 0 )
        {
            assertEquals( saltLength * 2, saltHex.length() );
            assertTrue( saltHex.matches( "[0-9a-f]{" + ( saltLength * 2 ) + "}" ) );
        }
        else
        {
            assertEquals( "-", saltHex );
        }
    }


    /**
     * DIRSTUDIO-1157: Values cannot be modified by text editor
     */
    @ParameterizedTest
    @LdapServersSource
    public void testTextValueEditor( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( BJENSEN_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( BJENSEN_DN.getName() );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: " + BJENSEN_DN.getName(), dn );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertEquals( "", modificationLogsViewBot.getModificationLogsText() );

        // add description attribute
        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "description" );
        wizardBot.clickFinishButton();
        entryEditorBot.typeValueAndFinish( "testTextValueEditor 1" );
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: testTextValueEditor 1" ) );
        modificationLogsViewBot.waitForText( "add: description\ndescription: testTextValueEditor 1" );

        // edit value with the text editor
        TextEditorDialogBot textEditorBot = entryEditorBot.editValueWithTextEditor( "description",
            "testTextValueEditor 1" );
        assertTrue( textEditorBot.isVisible() );
        String newValue = "testTextValueEditor 2 " + Characters.ALL;
        textEditorBot.setText( newValue );
        textEditorBot.clickOkButton();
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: testTextValueEditor 1" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: " + newValue ) );
        String description2Ldif = LdifAttrValLine.create( "description", newValue )
            .toFormattedString( LdifFormatParameters.DEFAULT ).replace( LdifParserConstants.LINE_SEPARATOR, "\n" );
        modificationLogsViewBot.waitForText( "delete: description\ndescription: testTextValueEditor 1" );
        modificationLogsViewBot.waitForText( "add: description\n" + description2Ldif );
    }


    /**
     * DIRSTUDIO-1298: The RFC 4517 Postal Address value editor en-/decoding is incomplete
     */
    @ParameterizedTest
    @LdapServersSource
    public void testAddressValueEditor( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( BJENSEN_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( BJENSEN_DN.getName() );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: " + BJENSEN_DN.getName(), dn );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertEquals( "", modificationLogsViewBot.getModificationLogsText() );

        // add postalAddress attribute and verify value is correctly encoded
        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "postalAddress" );
        AddressEditorDialogBot addressEditorDialogBot = wizardBot.clickFinishButtonExpectingAddressEditor();
        assertTrue( addressEditorDialogBot.isVisible() );
        addressEditorDialogBot.setText( "1234 Main St.\nAnytown, CA 12345\nUSA" );
        addressEditorDialogBot.clickOkButton();
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertTrue(
            entryEditorBot.getAttributeValues().contains( "postalAddress: 1234 Main St., Anytown, CA 12345, USA" ) );
        modificationLogsViewBot.waitForText( "add: postalAddress\npostalAddress: 1234 Main St.$Anytown, CA 12345$USA" );

        // verify value is correctly decoded
        addressEditorDialogBot = entryEditorBot.editValueExpectingAddressEditor( "postalAddress",
            "1234 Main St., Anytown, CA 12345, USA" );
        assertTrue( addressEditorDialogBot.isVisible() );
        assertEquals( "1234 Main St.\nAnytown, CA 12345\nUSA", addressEditorDialogBot.getText() );
        addressEditorDialogBot.clickCancelButton();

        // edit value with a complex address and verify value is correctly encoded
        addressEditorDialogBot = entryEditorBot.editValueExpectingAddressEditor( "postalAddress",
            "1234 Main St., Anytown, CA 12345, USA" );
        assertTrue( addressEditorDialogBot.isVisible() );
        assertEquals( "1234 Main St.\nAnytown, CA 12345\nUSA", addressEditorDialogBot.getText() );
        // TODO: $1,000,000 Sweepstakes
        addressEditorDialogBot.setText( "1,000,000 Sweepstakes\nPO Box 1000000\nAnytown, CA 12345\nUSA" );
        addressEditorDialogBot.clickOkButton();
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues()
            .contains( "postalAddress: 1234 Main St., Anytown, CA 12345, USA" ) );
        assertTrue( entryEditorBot.getAttributeValues()
            // TODO: $1,000,000 Sweepstakes
            .contains( "postalAddress: 1,000,000 Sweepstakes, PO Box 1000000, Anytown, CA 12345, USA" ) );
        modificationLogsViewBot
            .waitForText( "delete: postalAddress\npostalAddress: 1234 Main St.$Anytown, CA 12345$USA" );
        modificationLogsViewBot.waitForText(
            // TODO: $1,000,000 Sweepstakes
            "add: postalAddress\npostalAddress: 1,000,000 Sweepstakes$PO Box 1000000$Anytown, CA 12345$USA" );

        // verify value is correctly decoded
        // TODO: $1,000,000 Sweepstakes
        addressEditorDialogBot = entryEditorBot.editValueExpectingAddressEditor( "postalAddress",
            "1,000,000 Sweepstakes, PO Box 1000000, Anytown, CA 12345, USA" );
        assertTrue( addressEditorDialogBot.isVisible() );
        // TODO: $1,000,000 Sweepstakes
        assertEquals( "1,000,000 Sweepstakes\nPO Box 1000000\nAnytown, CA 12345\nUSA",
            addressEditorDialogBot.getText() );
        addressEditorDialogBot.clickCancelButton();
    }


    /**
     * DIRSTUDIO-1199, DIRSTUDIO-1204, DIRSTUDIO-1267: Binary attributes.
     * Test adding, editing and deleting of attributes with binary option in the entry editor.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCertificateValueEditor( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String certFile = ResourceUtils.prepareInputFile( "rfc5280_cert2.cer" );
        String cert3File = ResourceUtils.prepareInputFile( "rfc5280_cert3.cer" );

        browserViewBot.selectEntry( path( BJENSEN_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( BJENSEN_DN.getName() );
        entryEditorBot.activate();

        // add userCertificate;binary
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "userCertificate" );
        wizardBot.clickNextButton();
        wizardBot.selectBinaryOption();
        CertificateEditorDialogBot certEditorBot = wizardBot.clickFinishButtonExpectingCertificateEditor();
        assertTrue( certEditorBot.isVisible() );
        certEditorBot.typeFile( certFile );
        certEditorBot.clickOkButton();
        modificationLogsViewBot.waitForText( "add: userCertificate;binary\nuserCertificate;binary:: " );
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            assertTrue( entryEditorBot.getAttributeValues()
                .contains( "userCertificate;binary: X.509v3: CN=End Entity,DC=example,DC=com" ) );
        }
        else
        {
            assertTrue( entryEditorBot.getAttributeValues()
                .contains( "userCertificate: X.509v3: CN=End Entity,DC=example,DC=com" ) );
        }

        // edit userCertificate;binary
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            certEditorBot = entryEditorBot.editValueExpectingCertificateEditor( "userCertificate;binary",
                "X.509v3: CN=End Entity,DC=example,DC=com" );
        }
        else
        {
            certEditorBot = entryEditorBot.editValueExpectingCertificateEditor( "userCertificate",
                "X.509v3: CN=End Entity,DC=example,DC=com" );
        }
        assertTrue( certEditorBot.isVisible() );
        certEditorBot.typeFile( cert3File );
        certEditorBot.clickOkButton();
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            modificationLogsViewBot
                .waitForText( "delete: userCertificate;binary\nuserCertificate;binary:: MIICcTCCAdqg" );
            modificationLogsViewBot.waitForText( "add: userCertificate;binary\nuserCertificate;binary:: MIIDjjCCA06g" );
            assertFalse( entryEditorBot.getAttributeValues()
                .contains( "userCertificate;binary: X.509v3: CN=End Entity,DC=example,DC=com" ) );
            assertTrue( entryEditorBot.getAttributeValues()
                .contains( "userCertificate;binary: X.509v3: CN=DSA End Entity,DC=example,DC=com" ) );
        }
        else
        {
            modificationLogsViewBot.waitForText( "delete: userCertificate\nuserCertificate:: MIICcTCCAdqg" );
            modificationLogsViewBot.waitForText( "add: userCertificate\nuserCertificate:: MIIDjjCCA06g" );
            assertFalse( entryEditorBot.getAttributeValues()
                .contains( "userCertificate: X.509v3: CN=End Entity,DC=example,DC=com" ) );
            assertTrue( entryEditorBot.getAttributeValues()
                .contains( "userCertificate: X.509v3: CN=DSA End Entity,DC=example,DC=com" ) );
        }

        // delete userCertificate;binary
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            entryEditorBot.deleteValue( "userCertificate;binary", "X.509v3: CN=DSA End Entity,DC=example,DC=com" );
            modificationLogsViewBot
                .waitForText( "delete: userCertificate;binary\nuserCertificate;binary:: MIIDjjCCA06g" );
            assertFalse( entryEditorBot.getAttributeValues()
                .contains( "userCertificate;binary: X.509v3: CN=DSA End Entity,DC=example,DC=com" ) );
        }
        else
        {
            entryEditorBot.deleteValue( "userCertificate", "X.509v3: CN=DSA End Entity,DC=example,DC=com" );
            modificationLogsViewBot.waitForText( "delete: userCertificate\nuserCertificate:: MIIDjjCCA06g" );
            assertFalse( entryEditorBot.getAttributeValues()
                .contains( "userCertificate: X.509v3: CN=DSA End Entity,DC=example,DC=com" ) );
        }
    }


    /**
     * DIRSTUDIO-1199, DIRSTUDIO-1204: Binary attributes
     */
    @ParameterizedTest
    @LdapServersSource
    public void testImageValueEditor( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String imgFile = ResourceUtils.prepareInputFile( "studio_64x64.jpg" );

        browserViewBot.selectEntry( path( BJENSEN_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( BJENSEN_DN.getName() );
        entryEditorBot.activate();

        // add image
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "jpegPhoto" );
        ImageEditorDialogBot imageEditorBot = wizardBot.clickFinishButtonExpectingImageEditor();
        assertTrue( imageEditorBot.isVisible() );
        imageEditorBot.typeFile( imgFile );
        imageEditorBot.clickOkButton();

        // assert value after saved and reloaded from server
        modificationLogsViewBot.waitForText( "add: jpegPhoto\njpegPhoto:: " );
        assertTrue( entryEditorBot.getAttributeValues().contains( "jpegPhoto: JPEG-Image (64x64 Pixel, 2014 Bytes)" ) );
    }


    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "ApacheDS specific test")
    public void testAciItemEditorAllOptions( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( HNELSON_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( HNELSON_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.fetchOperationalAttributes();
        SWTUtils.sleep( 1000 );

        entryEditorBot.activate();
        AciItemEditorDialogBot aciItemEditor = entryEditorBot.editValueExpectingAciItemEditor( "entryACI", null );

        aciItemEditor.activateSourceTab();
        aciItemEditor.activateVisualEditorTab();

        aciItemEditor.setIdentificationTag( "Test 1234" );

        aciItemEditor.setPrecedence( 1 );
        aciItemEditor.setPrecedence( 10 );
        aciItemEditor.setPrecedence( 100 );

        aciItemEditor.setAuthenticationLevel( AuthenticationLevel.NONE );
        aciItemEditor.setAuthenticationLevel( AuthenticationLevel.SIMPLE );
        aciItemEditor.setAuthenticationLevel( AuthenticationLevel.STRONG );

        aciItemEditor.setUserFirst();

        aciItemEditor.enableUserClassAllUsers();
        aciItemEditor.disableUserClassAllUsers();
        aciItemEditor.enableUserClassThisEntry();
        aciItemEditor.disableUserClassThisEntry();
        aciItemEditor.enableUserClassParentOfEntry();
        aciItemEditor.disableUserClassParentOfEntry();
        aciItemEditor.enableUserClassName();
        aciItemEditor.disableUserClassName();
        aciItemEditor.enableUserClassUserGroup();
        aciItemEditor.disableUserClassUserGroup();
        aciItemEditor.enableUserClassSubtree();
        aciItemEditor.disableUserClassSubtree();

        aciItemEditor.setItemFirst();

        aciItemEditor.enableProtectedItemEntry();
        aciItemEditor.disableProtectedItemEntry();
        aciItemEditor.enableProtectedItemAllUserAttributeTypes();
        aciItemEditor.disableProtectedItemAllUserAttributeTypes();
        aciItemEditor.enableProtectedItemAttributeType();
        aciItemEditor.disableProtectedItemAttributeType();
        aciItemEditor.enableProtectedItemAllAttributeValues();
        aciItemEditor.disableProtectedItemAllAttributeValues();
        aciItemEditor.enableProtectedItemAllUserAttributeTypesAndValues();
        aciItemEditor.disableProtectedItemAllUserAttributeTypesAndValues();
        aciItemEditor.enableProtectedItemAttributeValues();
        aciItemEditor.disableProtectedItemAttributeValues();
        aciItemEditor.enableProtectedItemSelfValue();
        aciItemEditor.disableProtectedItemSelfValue();
        aciItemEditor.enableProtectedItemRangeOfValues();
        aciItemEditor.disableProtectedItemRangeOfValues();
        aciItemEditor.enableProtectedItemMaxValueCount();
        aciItemEditor.disableProtectedItemMaxValueCount();
        aciItemEditor.enableProtectedItemMaxNumberOfImmediateSubordinates();
        aciItemEditor.disableProtectedItemMaxNumberOfImmediateSubordinates();
        aciItemEditor.enableProtectedItemRestrictedBy();
        aciItemEditor.disableProtectedItemRestrictedBy();
        aciItemEditor.enableProtectedItemClasses();
        aciItemEditor.disableProtectedItemClasses();

        aciItemEditor.clickCancelButton();
    }


    /**
     * Test for DIRSTUDIO-1135
     */
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "ApacheDS specific test")
    public void testAciItemEditorAllAttributesValuesParser( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( HNELSON_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( HNELSON_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.fetchOperationalAttributes();
        SWTUtils.sleep( 1000 );

        entryEditorBot.activate();
        AciItemEditorDialogBot aciItemEditor = entryEditorBot.editValueExpectingAciItemEditor( "entryACI", null );

        aciItemEditor.activateSourceTab();
        aciItemEditor.setSource(
            "{ identificationTag \"Test\", precedence 0, authenticationLevel none, itemOrUserFirst itemFirst: { protectedItems { allAttributeValues { cn } }, itemPermissions { } } }" );
        aciItemEditor.clickCheckSyntaxButtonOk();

        aciItemEditor.activateVisualEditorTab();
        aciItemEditor.clickCheckSyntaxButtonOk();

        aciItemEditor.activateSourceTab();
        aciItemEditor.clickCheckSyntaxButtonOk();

        aciItemEditor.activateVisualEditorTab();
        aciItemEditor.clickCheckSyntaxButtonOk();

        aciItemEditor.clickOkButton();

        modificationLogsViewBot.waitForText( "delete: entryACI\n" );
        modificationLogsViewBot.waitForText( "add: entryACI\n" );
    }


    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "ApacheDS specific test")
    public void testAciItemEditorEntryAci( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( HNELSON_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( HNELSON_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.fetchOperationalAttributes();
        SWTUtils.sleep( 1000 );

        entryEditorBot.activate();
        AciItemEditorDialogBot aciItemEditor = entryEditorBot.editValueExpectingAciItemEditor( "entryACI", null );

        aciItemEditor.activateSourceTab();
        aciItemEditor.clickCheckSyntaxButtonOk();

        aciItemEditor.activateVisualEditorTab();
        aciItemEditor.clickCheckSyntaxButtonOk();

        aciItemEditor.activateSourceTab();
        aciItemEditor.clickFormatButton();
        aciItemEditor.clickCheckSyntaxButtonOk();

        String source = aciItemEditor.getSource();
        source = source.replace( "grantFilterMatch,", "" );

        aciItemEditor.setSource( "invalid" );
        aciItemEditor.clickCheckSyntaxButtonError();

        aciItemEditor.setSource( source );
        aciItemEditor.clickCheckSyntaxButtonOk();

        aciItemEditor.clickOkButton();

        modificationLogsViewBot.waitForText( "delete: entryACI\n" );
        modificationLogsViewBot.waitForText( "add: entryACI\n" );
    }


    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "ApacheDS specific test")
    public void testAciItemEditorPrescriptiveAci( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( HNELSON_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( HNELSON_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.fetchOperationalAttributes();
        SWTUtils.sleep( 1000 );

        entryEditorBot.activate();
        AciItemEditorDialogBot aciItemEditor = entryEditorBot.editValueExpectingAciItemEditor( "prescriptiveACI",
            null );

        aciItemEditor.activateSourceTab();
        aciItemEditor.clickFormatButton();
        aciItemEditor.clickCheckSyntaxButtonOk();

        aciItemEditor.activateVisualEditorTab();
        aciItemEditor.clickCheckSyntaxButtonOk();

        aciItemEditor.clickOkButton();

        modificationLogsViewBot.waitForText( "delete: prescriptiveACI\n" );
        modificationLogsViewBot.waitForText( "add: prescriptiveACI\n" );
    }


    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "ApacheDS specific test")
    public void testSubtreeSpecificationEditor( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( HNELSON_DN ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( HNELSON_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.fetchOperationalAttributes();
        SWTUtils.sleep( 1000 );

        entryEditorBot.activate();
        SubtreeSpecificationEditorDialogBot subtreeEditorBot = entryEditorBot
            .editValueExpectingSubtreeSpecificationEditor( "subtreeSpecification",
                null );

        subtreeEditorBot.clickOkButton();
    }


    /**
     * Test for DIRSTUDIO-1249, DIRSTUDIO-1267: userSMIMECertificate is a binary attribute.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testHexEditor( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnection( connection );
            browserConnection.setModifyModeNoEMR( ModifyMode.REPLACE );
        }

        browserViewBot.selectEntry( path( HNELSON_DN ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( HNELSON_DN.getName() );
        entryEditorBot.activate();
        assertTrue( entryEditorBot.getAttributeValues().contains( "userSMIMECertificate: Binary Data (255 Bytes)" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "userSMIMECertificate: Binary Data (256 Bytes)" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "userSMIMECertificate: Binary Data (257 Bytes)" ) );

        HexEditorDialogBot hexEditorDialogBot = entryEditorBot.editValueExpectingHexEditor( "userSMIMECertificate",
            "Binary Data (256 Bytes)" );
        String hexText = hexEditorDialogBot.getHexText();
        assertTrue( hexText.contains( "00 01 02 03 04 05 06 07  08 09 0a 0b 0c 0d 0e 0f     ........ ........" ) );
        assertTrue( hexText.contains( "70 71 72 73 74 75 76 77  78 79 7a 7b 7c 7d 7e 7f     pqrstuvw xyz{|}~." ) );
        assertTrue( hexText.contains( "80 81 82 83 84 85 86 87  88 89 8a 8b 8c 8d 8e 8f     ........ ........" ) );
        assertTrue( hexText.contains( "f0 f1 f2 f3 f4 f5 f6 f7  f8 f9 fa fb fc fd fe ff     ........ ........" ) );
        hexEditorDialogBot.clickCancelButton();

        hexEditorDialogBot = entryEditorBot.editValueExpectingHexEditor( "userSMIMECertificate",
            "Binary Data (255 Bytes)" );
        hexText = hexEditorDialogBot.getHexText();
        assertTrue( hexText.contains( "f0 f1 f2 f3 f4 f5 f6 f7  f8 f9 fa fb fc fd fe        ........ ......." ) );
        hexEditorDialogBot.clickCancelButton();

        hexEditorDialogBot = entryEditorBot.editValueExpectingHexEditor( "userSMIMECertificate",
            "Binary Data (257 Bytes)" );
        hexText = hexEditorDialogBot.getHexText();
        assertTrue( hexText.contains( "f0 f1 f2 f3 f4 f5 f6 f7  f8 f9 fa fb fc fd fe ff     ........ ........" ) );
        assertTrue( hexText.contains( "00                                                   ." ) );
        hexEditorDialogBot.clickCancelButton();

        String crtFile = ResourceUtils.prepareInputFile( "rfc5280_cert2.cer" );

        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "userSMIMECertificate" );
        wizardBot.clickNextButton();
        HexEditorDialogBot hexEditorBot = wizardBot.clickFinishButtonExpectingHexEditor();

        assertTrue( hexEditorBot.isVisible() );
        hexEditorBot.typeFile( crtFile );

        hexEditorBot.clickOkButton();

        if ( server.getType() == LdapServerType.OpenLdap )
        {
            modificationLogsViewBot.waitForText( "replace: userSMIMECertificate\nuserSMIMECertificate:: " );
            assertTrue( entryEditorBot.getAttributeValues()
                .contains( "userSMIMECertificate: Binary Data (629 Bytes)" ) );
        }
        else
        {
            modificationLogsViewBot.waitForText( "add: userSMIMECertificate\nuserSMIMECertificate:: " );
            assertTrue( entryEditorBot.getAttributeValues()
                .contains( "userSMIMECertificate: Binary Data (629 Bytes)" ) );
        }
    }

}
