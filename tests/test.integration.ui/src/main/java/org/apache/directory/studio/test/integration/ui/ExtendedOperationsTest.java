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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.ldap.handlers.extended.PwdModifyHandler;
import org.apache.directory.studio.test.integration.ui.bots.BotUtils;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.PasswordEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.PasswordModifyExtendedOperationDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the extended operations.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") }, extendedOpHandlers =
    { PwdModifyHandler.class })
@ApplyLdifFiles(clazz = ExtendedOperationsTest.class, value = "org/apache/directory/studio/test/integration/ui/BrowserTest.ldif")
public class ExtendedOperationsTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "BrowserTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();
    }


    @Test
    public void testPasswordModifyExtendedOperationDialogValidation()
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );

        // Open dialog
        PasswordModifyExtendedOperationDialogBot dialogBot = browserViewBot.openPasswordModifyExtendedOperationDialog();
        assertTrue( dialogBot.isVisible() );

        // Verify default UI state
        assertEquals( "uid=user.1,ou=users,ou=system", dialogBot.getUserIdentity() );
        assertFalse( dialogBot.useBindUserIdentity() );
        assertEquals( "", dialogBot.getOldPassword() );
        assertFalse( dialogBot.noOldPassword() );
        assertEquals( "", dialogBot.getNewPassword() );
        assertFalse( dialogBot.generateNewPassword() );
        assertFalse( dialogBot.isOkButtonEnabled() );

        // Enter passwords, should enable OK button
        dialogBot.setOldPassword( "old123" );
        dialogBot.setNewPassword( "new456" );
        assertTrue( dialogBot.isOkButtonEnabled() );

        // Check "Use bind user identity"
        dialogBot.useBindUserIdentity( true );
        assertEquals( "", dialogBot.getUserIdentity() );
        assertTrue( dialogBot.isOkButtonEnabled() );
        dialogBot.useBindUserIdentity( false );
        assertEquals( "", dialogBot.getUserIdentity() );
        assertFalse( dialogBot.isOkButtonEnabled() );
        dialogBot.useBindUserIdentity( true );

        // Check "No old password"
        dialogBot.noOldPassword( true );
        assertEquals( "", dialogBot.getOldPassword() );
        assertTrue( dialogBot.isOkButtonEnabled() );
        dialogBot.noOldPassword( false );
        assertEquals( "", dialogBot.getOldPassword() );
        assertFalse( dialogBot.isOkButtonEnabled() );
        dialogBot.noOldPassword( true );

        // Check "Generate new password"
        dialogBot.generateNewPassword( true );
        assertEquals( "", dialogBot.getNewPassword() );
        assertTrue( dialogBot.isOkButtonEnabled() );
        dialogBot.generateNewPassword( false );
        assertEquals( "", dialogBot.getNewPassword() );
        assertFalse( dialogBot.isOkButtonEnabled() );
        dialogBot.generateNewPassword( true );

        // Uncheck all again
        dialogBot.useBindUserIdentity( false );
        dialogBot.noOldPassword( false );
        dialogBot.generateNewPassword( false );
        assertFalse( dialogBot.isOkButtonEnabled() );

        // Fill data
        dialogBot.setNewPassword( "new123" );
        dialogBot.setOldPassword( "old456" );
        dialogBot.setUserIdentity( "foo=bar" );
        assertTrue( dialogBot.isOkButtonEnabled() );

        dialogBot.clickCancelButton();
    }


    @Test
    public void testPasswordModifyExtendedOperationDialogOk()
    {
        String random = RandomStringUtils.random( 20 );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        String dn = "uid=user.1,ou=users,ou=system";

        // Open dialog
        PasswordModifyExtendedOperationDialogBot dialogBot = browserViewBot.openPasswordModifyExtendedOperationDialog();
        assertTrue( dialogBot.isVisible() );
        assertEquals( dn, dialogBot.getUserIdentity() );

        // Change password
        dialogBot.noOldPassword( true );
        dialogBot.setNewPassword( random );
        dialogBot.clickOkButton();

        // Verify and bind with the correct password
        browserViewBot.refresh();
        BotUtils.sleep( 1000L );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( dn );
        entryEditorBot.activate();
        PasswordEditorDialogBot pwdEditorBot = entryEditorBot.editValueExpectingPasswordEditor( "userPassword",
            "Plain text password" );
        pwdEditorBot.activateCurrentPasswordTab();
        pwdEditorBot.setVerifyPassword( random );
        assertNull( pwdEditorBot.clickVerifyButton() );
        assertNull( pwdEditorBot.clickBindButton() );
        pwdEditorBot.clickCancelButton();
    }


    @Test
    public void testPasswordModifyExtendedOperationDialogError()
    {
        String random = RandomStringUtils.random( 20 );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        String dn = "uid=user.1,ou=users,ou=system";

        // Open dialog
        PasswordModifyExtendedOperationDialogBot dialogBot = browserViewBot.openPasswordModifyExtendedOperationDialog();
        assertTrue( dialogBot.isVisible() );
        assertEquals( dn, dialogBot.getUserIdentity() );

        // Wrong old password
        dialogBot.activate();
        dialogBot.setUserIdentity( dn );
        dialogBot.setOldPassword( "wrong password" );
        dialogBot.setNewPassword( random );
        ErrorDialogBot errorBot = dialogBot.clickOkButtonExpectingErrorDialog();
        assertTrue( errorBot.getErrorMessage().contains( "invalid credentials" ) );
        errorBot.clickOkButton();

        // Not existing entry
        dialogBot.activate();
        dialogBot.setUserIdentity( "cn=non-existing-entry" );
        dialogBot.noOldPassword( true );
        dialogBot.setNewPassword( random );
        errorBot = dialogBot.clickOkButtonExpectingErrorDialog();
        assertTrue( errorBot.getErrorMessage().contains( "The entry does not exist" ) );
        errorBot.clickOkButton();

        // ApacheDS does not support password generation
        dialogBot.activate();
        dialogBot.setUserIdentity( dn );
        dialogBot.noOldPassword( true );
        dialogBot.generateNewPassword( true );
        errorBot = dialogBot.clickOkButtonExpectingErrorDialog();
        assertTrue( errorBot.getErrorMessage().contains( "null new password" ) );
        errorBot.clickOkButton();

        dialogBot.activate();
        dialogBot.clickCancelButton();
    }
}
