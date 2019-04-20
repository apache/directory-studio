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


import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.password.PasswordUtil;
import org.apache.directory.api.util.FileUtils;
import org.apache.directory.api.util.IOUtils;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.security.CertificateUtil;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.test.integration.ui.bots.BotUtils;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DnEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ImageEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ModificationLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewAttributeWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.PasswordEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SelectDnDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.TextEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.Characters;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import sun.security.x509.X500Name;


/**
 * Tests the entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles(clazz = EntryEditorTest.class, value =
    { "org/apache/directory/studio/test/integration/ui/EntryEditorTest.ldif" })
public class EntryEditorTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;
    private ModificationLogsViewBot modificationLogsViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "EntryEditorTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
        modificationLogsViewBot = studioBot.getModificationLogsViewBot();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();
    }


    /**
     * Test adding, editing and deleting of attributes in the entry editor.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testAddEditDeleteAttribute() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=Barbara Jensen,ou=users,ou=system" );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: cn=Barbara Jensen,ou=users,ou=system", dn );
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
        modificationLogsViewBot.waitForText( "replace: description\ndescription: This is the final description." );

        // delete 1st value/attribute
        entryEditorBot.deleteValue( "description", "This is the final description." );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: This is the final description." ) );
        modificationLogsViewBot.waitForText( "delete: description\n-" );

        assertEquals( "Expected 6 modifications.", 6,
            StringUtils.countMatches( modificationLogsViewBot.getModificationLogsText(), "#!RESULT OK" ) );
    }


    /**
     * DIRSTUDIO-483: DN Editor escapes all non-ascii characters
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDnValueEditor() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=groups", "cn=My Group" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=My Group,ou=groups,ou=system" );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: cn=My Group,ou=groups,ou=system", dn );
        assertEquals( 4, entryEditorBot.getAttributeValues().size() );

        // add member attribute
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "member" );
        DnEditorDialogBot dnEditorBot = wizardBot.clickFinishButtonExpectingDnEditor();
        assertTrue( dnEditorBot.isVisible() );
        SelectDnDialogBot selectDnBot = dnEditorBot.clickBrowseButtonExpectingSelectDnDialog();
        assertTrue( selectDnBot.isVisible() );
        selectDnBot.selectEntry( "Root DSE", "ou=system", "ou=users", "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );
        selectDnBot.clickOkButton();
        dnEditorBot.activate();
        assertEquals( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system", dnEditorBot.getDnText() );
        dnEditorBot.clickOkButton();

        // assert value after saved and reloaded from server
        SWTUtils.sleep( 1000 );
        assertEquals( 5, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains(
            "member: cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" ) );
        dnEditorBot = entryEditorBot.editValueExpectingDnEditor( "member",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" );
        assertEquals( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system", dnEditorBot.getDnText() );
        dnEditorBot.clickCancelButton();

        modificationLogsViewBot.waitForText( "#!RESULT OK" );
        assertEquals( "Expected 1 modification.", 1,
            StringUtils.countMatches( modificationLogsViewBot.getModificationLogsText(), "#!RESULT OK" ) );
    }


    /**
     * DIRSTUDIO-637: copy/paste of attributes no longer works.
     * Test copy/paste within entry editor.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testCopyPasteStringValue() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen" );

        // copy a value
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=Barbara Jensen,ou=users,ou=system" );
        entryEditorBot.activate();
        entryEditorBot.copyValue( "uid", "bjensen" );

        // go to another entry
        browserViewBot
            .selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );
        entryEditorBot = studioBot.getEntryEditorBot( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" );
        entryEditorBot.activate();
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );

        // paste value, wait till job is done
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__execute_ldif_name );
        entryEditorBot.pasteValue();
        watcher.waitUntilDone();

        // assert pasted value visible in editor
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertTrue( "Should contain uid=bjensen: " + entryEditorBot.getAttributeValues(),
            entryEditorBot.getAttributeValues().contains( "uid: bjensen" ) );

        // assert pasted value was written to directory
        Entry entry = service.getAdminSession()
            .lookup( new Dn( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" ) );
        assertTrue( "Should contain uid=bjensen: " + entry, entry.contains( "uid", "bjensen" ) );
    }


    @Test
    public void testCopyPasteMultipleStringAndBinaryValues() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=hnelson" );

        // copy the values
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=hnelson,ou=users,ou=system" );
        entryEditorBot.activate();
        entryEditorBot.copyValues( "userpassword", "uid", "description", "jpegphoto" );

        // go to another entry
        browserViewBot
            .selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );
        entryEditorBot = studioBot.getEntryEditorBot( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" );
        entryEditorBot.activate();
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );

        // paste values, wait till job is done
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__execute_ldif_name );
        entryEditorBot.pasteValues();
        watcher.waitUntilDone();

        // assert pasted values are visible in editor
        SWTUtils.sleep( 1000 );
        assertEquals( 12, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "uid: hnelson" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: " + Characters.ALL ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "jpegPhoto: JPEG-Image (1x1 Pixel, 631 Bytes)" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "userPassword: SSHA-512 hashed password" ) );

        // assert pasted values were written to directory
        Entry entry = service.getAdminSession()
            .lookup( new Dn( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" ) );
        assertTrue( "Should contain uid=hnelson: " + entry, entry.contains( "uid", "hnelson" ) );
        assertTrue( "Should contain description: " + entry, entry.contains( "description", Characters.ALL ) );
        assertTrue( "Should contain userPassword: " + entry, entry.containsAttribute( "userPassword" ) );
        assertTrue( "Should contain jpegPhoto: " + entry, entry.containsAttribute( "jpegPhoto" ) );
    }


    /**
     * DIRSTUDIO-738: Add support for modular crypt format password
     */
    @Test
    public void testPasswordValueEditor() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=Barbara Jensen,ou=users,ou=system" );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: cn=Barbara Jensen,ou=users,ou=system", dn );
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
        SWTUtils.sleep( 1000 );
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
        SWTUtils.sleep( 1000 );
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
    @Test
    public void testTextValueEditor() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=Barbara Jensen,ou=users,ou=system" );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: cn=Barbara Jensen,ou=users,ou=system", dn );
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
            .toFormattedString( LdifFormatParameters.DEFAULT );
        modificationLogsViewBot.waitForText( "replace: description\n" + description2Ldif );
    }


    /**
     * DIRSTUDIO-1199, DIRSTUDIO-1204: Binary attributes
     */
    @Test
    public void testCertificateValueEditor() throws Exception
    {
        X500Name issuer = new X500Name( "Foo", "Bar", "Baz", "US" );
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance( "EC" );
        keyPairGenerator.initialize( 256 );
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        X509Certificate certificate = CertificateUtil.generateSelfSignedCertificate( issuer, keyPair, 365,
            "SHA256WithECDSA" );
        getService().getAdminSession().modify( new Dn( "cn=Barbara Jensen,ou=users,ou=system" ),
            new DefaultModification( ModificationOperation.ADD_ATTRIBUTE, "userCertificate",
                certificate.getEncoded() ) );

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=Barbara Jensen,ou=users,ou=system" );
        entryEditorBot.activate();

        BotUtils.sleep( 10000 );

        assertTrue( entryEditorBot.getAttributeValues()
            .contains( "userCertificate: X.509v3: CN=Foo,OU=Bar,O=Baz,C=US" ) );
    }


    /**
     * DIRSTUDIO-1199, DIRSTUDIO-1204: Binary attributes
     */
    @Test
    public void testImageValueEditor() throws Exception
    {
        URL url = Platform.getInstanceLocation().getURL();
        String destFile = url.getFile() + "studio_64x64.jpg";
        InputStream is = getClass().getResourceAsStream( "studio_64x64.jpg" );
        byte[] data = IOUtils.toByteArray( is, 2014 );
        FileUtils.writeByteArrayToFile( new File( destFile ), data );

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=Barbara Jensen,ou=users,ou=system" );
        entryEditorBot.activate();

        // add image
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "jpegPhoto" );
        ImageEditorDialogBot imageEditorBot = wizardBot.clickFinishButtonExpectingImageEditor();
        assertTrue( imageEditorBot.isVisible() );
        imageEditorBot.typeFile( destFile );
        imageEditorBot.clickOkButton();

        // assert value after saved and reloaded from server
        SWTUtils.sleep( 1000 );
        assertTrue( entryEditorBot.getAttributeValues().contains( "jpegPhoto: JPEG-Image (64x64 Pixel, 2014 Bytes)" ) );
    }

}
