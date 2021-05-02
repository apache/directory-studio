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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.BotUtils;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.GeneratedPasswordDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.PasswordEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.PasswordModifyExtendedOperationDialogBot;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the extended operations.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExtendedOperationsTest extends AbstractTestBase
{

    @ParameterizedTest
    @LdapServersSource
    public void testPasswordModifyExtendedOperationDialogValidation( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( USER1_DN ) );

        // Open dialog
        PasswordModifyExtendedOperationDialogBot dialogBot = browserViewBot.openPasswordModifyExtendedOperationDialog();
        assertTrue( dialogBot.isVisible() );

        // Verify default UI state
        assertEquals( USER1_DN.getName(), dialogBot.getUserIdentity() );
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


    @ParameterizedTest
    @LdapServersSource(except = LdapServerType.Fedora389ds, reason = "389ds requires secure connection")
    public void testPasswordModifyExtendedOperationDialogSetNewPassword( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String random = RandomStringUtils.random( 20 );
        browserViewBot.selectEntry( path( USER1_DN ) );

        // Open dialog
        PasswordModifyExtendedOperationDialogBot dialogBot = browserViewBot.openPasswordModifyExtendedOperationDialog();
        assertTrue( dialogBot.isVisible() );
        assertEquals( USER1_DN.getName(), dialogBot.getUserIdentity() );

        // Change password
        dialogBot.noOldPassword( true );
        dialogBot.setNewPassword( random );
        dialogBot.clickOkButton();

        // Verify and bind with the correct password
        browserViewBot.refresh();
        BotUtils.sleep( 1000L );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        PasswordEditorDialogBot pwdEditorBot = entryEditorBot.editValueExpectingPasswordEditor( "userPassword", null );
        pwdEditorBot.activateCurrentPasswordTab();
        pwdEditorBot.setVerifyPassword( random );
        assertNull( pwdEditorBot.clickVerifyButton() );
        assertNull( pwdEditorBot.clickBindButton() );

        pwdEditorBot.clickCancelButton();
    }


    @ParameterizedTest
    @LdapServersSource(except = LdapServerType.Fedora389ds, reason = "389ds requires secure connection")
    public void testPasswordModifyExtendedOperationDialogGenerateNewPassword( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( USER1_DN ) );

        // Open dialog
        PasswordModifyExtendedOperationDialogBot dialogBot = browserViewBot.openPasswordModifyExtendedOperationDialog();
        assertTrue( dialogBot.isVisible() );
        assertEquals( USER1_DN.getName(), dialogBot.getUserIdentity() );

        // Generate password
        dialogBot.noOldPassword( true );
        dialogBot.generateNewPassword( true );

        // ApacheDS does not support password generation
        if ( server.getType() == LdapServerType.ApacheDS )
        {
            ErrorDialogBot errorBot = dialogBot.clickOkButtonExpectingErrorDialog();
            assertThat( errorBot.getErrorMessage(), containsString( "null new password" ) );
            errorBot.clickOkButton();
            dialogBot.activate();
            dialogBot.clickCancelButton();
        }
        else
        {
            dialogBot.clickOkButton();
            GeneratedPasswordDialogBot generatedPasswordDialogBot = new GeneratedPasswordDialogBot();
            String generatedPassword = generatedPasswordDialogBot.getGeneratedPassword();
            generatedPasswordDialogBot.clickOkButton();

            // Verify and bind with the correct password
            browserViewBot.refresh();
            BotUtils.sleep( 1000L );
            EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
            entryEditorBot.activate();
            PasswordEditorDialogBot pwdEditorBot = entryEditorBot.editValueExpectingPasswordEditor( "userPassword",
                null );
            pwdEditorBot.activateCurrentPasswordTab();
            pwdEditorBot.setVerifyPassword( generatedPassword );
            assertNull( pwdEditorBot.clickVerifyButton() );
            assertNull( pwdEditorBot.clickBindButton() );
            pwdEditorBot.clickCancelButton();
        }

    }


    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.Fedora389ds, reason = "389ds requires secure connection")
    public void testPasswordModifyExtendedOperationRequiresSecureConnection( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String random = RandomStringUtils.random( 20 );
        browserViewBot.selectEntry( path( USER1_DN ) );

        // Open dialog
        PasswordModifyExtendedOperationDialogBot dialogBot = browserViewBot.openPasswordModifyExtendedOperationDialog();
        assertTrue( dialogBot.isVisible() );
        assertEquals( USER1_DN.getName(), dialogBot.getUserIdentity() );

        // Change password
        dialogBot.noOldPassword( true );
        dialogBot.setNewPassword( random );
        ErrorDialogBot errorBot = dialogBot.clickOkButtonExpectingErrorDialog();
        assertThat( errorBot.getErrorMessage(), containsString( "Operation requires a secure connection" ) );
        errorBot.clickOkButton();

        dialogBot.activate();
        dialogBot.clickCancelButton();
    }


    @ParameterizedTest
    @LdapServersSource
    public void testPasswordModifyExtendedOperationDialogError( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String random = RandomStringUtils.random( 20 );
        browserViewBot.selectEntry( path( USER1_DN ) );
        String dn = USER1_DN.getName();

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
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            assertThat( errorBot.getErrorMessage(), containsString( "unwilling to verify old password" ) );
        }
        else if ( server.getType() == LdapServerType.Fedora389ds )
        {
            assertThat( errorBot.getErrorMessage(), containsString( "Operation requires a secure connection" ) );
        }
        else
        {
            assertThat( errorBot.getErrorMessage(), containsString( "invalid credentials" ) );
        }
        errorBot.clickOkButton();

        // Not existing entry
        dialogBot.activate();
        dialogBot.setUserIdentity( "cn=non-existing-entry" );
        dialogBot.noOldPassword( true );
        dialogBot.setNewPassword( random );
        errorBot = dialogBot.clickOkButtonExpectingErrorDialog();
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            assertThat( errorBot.getErrorMessage(), containsString( "unable to retrieve SASL username" ) );
        }
        else if ( server.getType() == LdapServerType.Fedora389ds )
        {
            assertThat( errorBot.getErrorMessage(), containsString( "Operation requires a secure connection" ) );
        }
        else
        {
            assertThat( errorBot.getErrorMessage(), containsString( "The entry does not exist" ) );
        }
        errorBot.clickOkButton();

        // ApacheDS does not support password generation
        if ( server.getType() == LdapServerType.ApacheDS )
        {
            dialogBot.activate();
            dialogBot.setUserIdentity( dn );
            dialogBot.noOldPassword( true );
            dialogBot.generateNewPassword( true );
            errorBot = dialogBot.clickOkButtonExpectingErrorDialog();
            assertThat( errorBot.getErrorMessage(), containsString( "null new password" ) );
            errorBot.clickOkButton();
        }

        dialogBot.activate();
        dialogBot.clickCancelButton();
    }
}
