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

import org.apache.commons.lang.StringUtils;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.password.PasswordUtil;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DnEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ModificationLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewAttributeWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.PasswordEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SelectDnDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles( clazz=EntryEditorTest.class,
    value = { "org/apache/directory/studio/test/integration/ui/EntryEditorTest.ldif" })
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
    public void testCopyPaste() throws Exception
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
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );

        // paste value, wait till job is done
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__execute_ldif_name );
        entryEditorBot.pasteValue();
        watcher.waitUntilDone();

        // assert pasted value visible in editor
        assertEquals( 10, entryEditorBot.getAttributeValues().size() );
        entryEditorBot.getAttributeValues().contains( "uid: bjensen" );

        // assert pasted value was written to directory
        Entry entry = service.getAdminSession()
            .lookup( new Dn( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" ) );
        assertTrue( entry.contains( "uid", "bjensen" ) );
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

        pwdEditorBot.setNewPassword1( "secret" );
        pwdEditorBot.setNewPassword2( "secret" );
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
        pwdEditorBot.setVerifyPassword( "secret" );
        assertNull( pwdEditorBot.clickVerifyButton() );
        assertNull( pwdEditorBot.clickBindButton() );

        // verify and bind with the wrong password
        pwdEditorBot.activateCurrentPasswordTab();
        pwdEditorBot.setVerifyPassword( "Wrong Password" );
        assertEquals( "Password verification failed", pwdEditorBot.clickVerifyButton() );
        assertThat( pwdEditorBot.clickBindButton(), startsWith( "The authentication failed" ) );

        pwdEditorBot.clickCancelButton();
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

}
