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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.ALIAS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.CONTEXT_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.TARGET_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.SUBENTRY_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.dn;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.SelectCopyDepthDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SelectCopyStrategyBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests copy/paste of entries
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CopyEntryTest extends AbstractTestBase
{

    @AfterEach
    public void resetPreferences()
    {
        // DIRSERVER-2133: reset check for children preference
        UIThreadRunnable.syncExec( () -> {
            BrowserCorePlugin.getDefault()
                .getPluginPreferences().setValue( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN, true );
        } );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteSingleEntryWithoutCopyDepthDialog( TestLdapServer server ) throws Exception
    {
        Dn newDn = dn( USER1_DN.getRdn(), TARGET_DN );

        // expand the entry to avoid copy depth dialog
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( USER1_DN ) );
        browserViewBot.expandEntry( path( USER1_DN ) );

        // copy an entry
        browserViewBot.copy();

        // select the parent entry where the copied entry should be pasted to
        browserViewBot.selectEntry( path( TARGET_DN ) );
        assertFalse( browserViewBot.existsEntry( path( newDn ) ) );

        // paste the entry
        browserViewBot.pasteEntry();

        // verify the entry was copied
        assertTrue( browserViewBot.existsEntry( path( newDn ) ) );
        browserViewBot.selectEntry( path( newDn ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk( "dn: " + newDn.getName(), "changetype: add" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteMultipleEntriesWithCopyDepthDialogObjectOnly( TestLdapServer server ) throws Exception
    {
        // DIRSERVER-2133: disable check for children for this test
        UIThreadRunnable.syncExec( () -> {
            BrowserCorePlugin.getDefault()
                .getPluginPreferences().setValue( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN, false );
        } );

        // select and copy multiple entries
        connectionsViewBot.createTestConnection( server );
        browserViewBot.expandEntry( path( USERS_DN ) );
        String[] children =
            { "uid=user.1", "uid=user.2", "uid=user.3", "uid=user.4" };
        browserViewBot.selectChildrenOfEntry( children, path( USERS_DN ) );
        browserViewBot.copy();

        // select the parent entry where the copied entries should be pasted to
        browserViewBot.selectEntry( path( TARGET_DN ) );
        assertFalse( browserViewBot.existsEntry( path( TARGET_DN, "uid=user.1" ) ) );

        // paste the entry
        SelectCopyDepthDialogBot dialog = browserViewBot.pasteEntriesExpectingSelectCopyDepthDialog( 4 );
        dialog.selectObject();
        dialog.clickOkButton();

        // verify the entries were copied
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "uid=user.1" ) ) );
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "uid=user.2" ) ) );
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "uid=user.3" ) ) );
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "uid=user.4" ) ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( "uid=user.1", TARGET_DN ), "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( "uid=user.2", TARGET_DN ), "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( "uid=user.3", TARGET_DN ), "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( "uid=user.4", TARGET_DN ), "changetype: add" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteMultipleEntriesWithCopyDepthDialogSubtree( TestLdapServer server ) throws Exception
    {
        // select and copy multiple entries
        connectionsViewBot.createTestConnection( server );
        browserViewBot.expandEntry( path( CONTEXT_DN ) );
        String[] children =
            { "ou=users", "ou=groups" };
        browserViewBot.selectChildrenOfEntry( children, path( CONTEXT_DN ) );
        browserViewBot.copy();

        // select the parent entry where the copied entries should be pasted to
        browserViewBot.selectEntry( path( TARGET_DN ) );
        assertFalse( browserViewBot.existsEntry( path( TARGET_DN, "ou=users" ) ) );
        assertFalse( browserViewBot.existsEntry( path( TARGET_DN, "ou=groups" ) ) );

        // paste the entry
        SelectCopyDepthDialogBot dialog = browserViewBot.pasteEntriesExpectingSelectCopyDepthDialog( 2 );
        dialog.selectSubTree();
        dialog.clickOkButton();

        // verify the entries were copied
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "ou=users" ) ) );
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "ou=users", "uid=user.1" ) ) );
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "ou=users", "uid=user.8" ) ) );
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "ou=groups" ) ) );
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "ou=groups", "cn=group.1" ) ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( "ou=users", TARGET_DN ), "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( "ou=groups", TARGET_DN ), "changetype: add" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteSingleEntryOverwrite( TestLdapServer server ) throws Exception
    {
        // expand the entry to avoid copy depth dialog
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( USER1_DN ) );
        browserViewBot.expandEntry( path( USER1_DN ) );

        // copy an entry
        browserViewBot.copy();

        // select the parent entry where the copied entry should be pasted to
        browserViewBot.selectEntry( path( USERS_DN ) );

        // paste the entry
        SelectCopyStrategyBot dialog = browserViewBot.pasteEntriesExpectingSelectCopyStrategy();
        dialog.selectOverwriteEntryAndContinue();
        dialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 68 - entryAlreadyExists]",
            "dn: " + USER1_DN.getName(), "changetype: add", "uid: user.1" );
        modificationLogsViewBot.assertContainsOk( "dn: " + USER1_DN.getName(), "changetype: modify", "replace: uid"
            + LdifParserConstants.LINE_SEPARATOR + "uid: user.1" + LdifParserConstants.LINE_SEPARATOR + "-",
            "replace: objectClass" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteSingleEntryRename( TestLdapServer server ) throws Exception
    {
        // expand the entry to avoid copy depth dialog
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( USER1_DN ) );
        browserViewBot.expandEntry( path( USER1_DN ) );

        // copy an entry
        browserViewBot.copy();

        // select the parent entry where the copied entry should be pasted to
        browserViewBot.selectEntry( path( USERS_DN ) );

        // paste the entry
        Dn renamedDn = dn( "uid=user.renamed", USERS_DN );
        SelectCopyStrategyBot dialog = browserViewBot.pasteEntriesExpectingSelectCopyStrategy();
        dialog.selectRenameEntryAndContinue();
        dialog.setRdnValue( 1, "user.renamed" );
        dialog.clickOkButton();

        // verify the entry was copied
        assertTrue( browserViewBot.existsEntry( path( renamedDn ) ) );
        browserViewBot.selectEntry( path( renamedDn ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 68 - entryAlreadyExists]",
            "dn: " + USER1_DN.getName(), "changetype: add", "uid: user.1" );
        modificationLogsViewBot.assertContainsOk( "dn: " + renamedDn.getName(), "changetype: add",
            "uid: user.renamed" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteAliasEntry( TestLdapServer server ) throws Exception
    {
        // disable alias dereferencing
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            AliasDereferencingMethod.NEVER.ordinal() );

        // expand the entry to avoid copy depth dialog
        browserViewBot.expandEntry( path( ALIAS_DN ) );
        browserViewBot.selectEntry( path( ALIAS_DN ) );
        browserViewBot.copy();

        // select the parent entry where the copied entry should be pasted to
        browserViewBot.selectEntry( path( TARGET_DN ) );
        assertFalse( browserViewBot.existsEntry( path( TARGET_DN, ALIAS_DN.getRdn() ) ) );

        // paste the entry
        browserViewBot.pasteEntries( 1 );

        // verify the entyr was copied
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, ALIAS_DN.getRdn() ) ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( ALIAS_DN.getRdn(), TARGET_DN ), "changetype: add" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteReferralEntry( TestLdapServer server ) throws Exception
    {
        // enable ManageDsaIT control
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );

        // expand the entry to avoid copy depth dialog
        browserViewBot.expandEntry( path( REFERRAL_TO_USER1_DN ) );
        browserViewBot.selectEntry( path( REFERRAL_TO_USER1_DN ) );
        browserViewBot.copy();

        // select the parent entry where the copied entry should be pasted to
        browserViewBot.selectEntry( path( TARGET_DN ) );
        assertFalse( browserViewBot.existsEntry( path( TARGET_DN, REFERRAL_TO_USER1_DN.getRdn() ) ) );

        // paste the entry
        browserViewBot.pasteEntries( 1 );

        // verify the entry was copied
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, REFERRAL_TO_USER1_DN.getRdn() ) ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( REFERRAL_TO_USER1_DN.getRdn(), TARGET_DN ),
            "changetype: add" );
    }


    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "ApacheDS specific test")
    public void testCopyPasteSubentry( TestLdapServer server ) throws Exception
    {
        // enable Subentries control
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES, true );

        // expand the entry to avoid copy depth dialog
        browserViewBot.expandEntry( path( SUBENTRY_DN ) );
        browserViewBot.selectEntry( path( SUBENTRY_DN ) );
        browserViewBot.copy();

        // select the parent entry where the copied entry should be pasted to
        browserViewBot.selectEntry( path( TARGET_DN ) );
        assertFalse( browserViewBot.existsEntry( path( TARGET_DN, SUBENTRY_DN.getRdn() ) ) );

        // paste the entry
        browserViewBot.pasteEntries( 1 );

        // verify the entry was copied
        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, SUBENTRY_DN.getRdn() ) ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( SUBENTRY_DN.getRdn(), TARGET_DN ), "changetype: add" );
    }

}
