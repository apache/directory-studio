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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ModificationLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.SelectCopyDepthDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SelectCopyStrategyBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests copy/paste of entries
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles(clazz = CopyEntryTest.class, value = "org/apache/directory/studio/test/integration/ui/BrowserTest.ldif")
public class CopyEntryTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;
    private ModificationLogsViewBot modificationLogsViewBot;
    private Connection connection;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connection = connectionsViewBot.createTestConnection( "CopyEntryTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
        modificationLogsViewBot = studioBot.getModificationLogsViewBot();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();
        // DIRSERVER-2133: reset check for children preference
        BrowserCorePlugin.getDefault()
            .getPluginPreferences().setValue( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN, true );
    }


    @Test
    public void testCopyPasteSingleEntryWithoutCopyDepthDialog() throws Exception
    {
        // expand the entry to avoid copy depth dialog
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );

        // copy an entry
        browserViewBot.copy();

        // select the parent entry where the copied entry should be pasted to
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor", "uid=user.1" ) );

        // paste the entry
        browserViewBot.pasteEntry();

        // verify the entry was copied
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor", "uid=user.1" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor", "uid=user.1" );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk(
            "dn: uid=user.1,l=eu + l=de + l=Berlin + l=Brandenburger Tor,ou=users,ou=system", "changetype: add" );
    }


    @Test
    public void testCopyPasteMultipleEntriesWithCopyDepthDialogObjectOnly() throws Exception
    {
        // DIRSERVER-2133: disable check for children for this test
        BrowserCorePlugin.getDefault()
            .getPluginPreferences().setValue( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN, false );

        // select and copy multiple entries
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        String[] children =
            { "uid=user.1", "uid=user.2", "uid=user.3", "uid=user.4" };
        browserViewBot.selectChildrenOfEntry( children, "DIT", "Root DSE", "ou=system", "ou=users" );
        browserViewBot.copy();

        // select the parent entry where the copied entries should be pasted to
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor", "uid=user.1" ) );

        // paste the entry
        SelectCopyDepthDialogBot dialog = browserViewBot.pasteEntriesExpectingSelectCopyDepthDialog( 4 );
        dialog.selectObject();
        dialog.clickOkButton();

        // verify the entries were copied
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor", "uid=user.1" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor", "uid=user.2" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor", "uid=user.3" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor", "uid=user.4" ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk(
            "dn: uid=user.1,l=eu + l=de + l=Berlin + l=Brandenburger Tor,ou=users,ou=system", "changetype: add" );
        modificationLogsViewBot.assertContainsOk(
            "dn: uid=user.2,l=eu + l=de + l=Berlin + l=Brandenburger Tor,ou=users,ou=system", "changetype: add" );
        modificationLogsViewBot.assertContainsOk(
            "dn: uid=user.3,l=eu + l=de + l=Berlin + l=Brandenburger Tor,ou=users,ou=system", "changetype: add" );
        modificationLogsViewBot.assertContainsOk(
            "dn: uid=user.4,l=eu + l=de + l=Berlin + l=Brandenburger Tor,ou=users,ou=system", "changetype: add" );
    }


    @Test
    public void testCopyPasteMultipleEntriesWithCopyDepthDialogSubtree() throws Exception
    {
        // select and copy multiple entries
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system" );
        String[] children =
            { "ou=users", "ou=groups" };
        browserViewBot.selectChildrenOfEntry( children, "DIT", "Root DSE", "ou=system" );
        browserViewBot.copy();

        // select the parent entry where the copied entries should be pasted to
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=target" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "ou=users" ) );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "ou=groups" ) );

        // paste the entry
        SelectCopyDepthDialogBot dialog = browserViewBot.pasteEntriesExpectingSelectCopyDepthDialog( 2 );
        dialog.selectSubTree();
        dialog.clickOkButton();

        // verify the entries were copied
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "ou=users" ) );
        assertTrue(
            browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "ou=users", "uid=user.1" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "ou=users",
            "cn=\\#ACL_AD-Projects_Author" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "ou=users",
            "l=eu + l=de + l=Berlin + l=Brandenburger Tor", "cn=A" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "ou=groups" ) );
        assertTrue(
            browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "ou=groups",
                "cn=Administrators" ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk( "dn: ou=users,ou=target,ou=system", "changetype: add" );
        modificationLogsViewBot.assertContainsOk(
            "dn: cn=A,l=eu + l=de + l=Berlin + l=Brandenburger Tor,ou=users,ou=target,ou=system", "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: ou=groups,ou=target,ou=system", "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: cn=Administrators,ou=groups,ou=target,ou=system",
            "changetype: add" );
    }


    @Test
    public void testCopyPasteSingleEntryOverwrite() throws Exception
    {
        // expand the entry to avoid copy depth dialog
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );

        // copy an entry
        browserViewBot.copy();

        // select the parent entry where the copied entry should be pasted to
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );

        // paste the entry
        SelectCopyStrategyBot dialog = browserViewBot.pasteEntriesExpectingSelectCopyStrategy();
        dialog.selectOverwriteEntryAndContinue();
        dialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "ENTRY_ALREADY_EXISTS", "dn: uid=user.1,ou=users,ou=system",
            "changetype: add", "uid: user.1" );
        modificationLogsViewBot.assertContainsOk( "dn: uid=user.1,ou=users,ou=system", "changetype: modify",
            "replace: uid" + LdifParserConstants.LINE_SEPARATOR + "uid: user.1" + LdifParserConstants.LINE_SEPARATOR
                + "-",
            "replace: objectclass" );
    }


    @Test
    public void testCopyPasteSingleEntryRename() throws Exception
    {
        // expand the entry to avoid copy depth dialog
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );

        // copy an entry
        browserViewBot.copy();

        // select the parent entry where the copied entry should be pasted to
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );

        // paste the entry
        SelectCopyStrategyBot dialog = browserViewBot.pasteEntriesExpectingSelectCopyStrategy();
        dialog.selectRenameEntryAndContinue();
        dialog.setRdnValue( 1, "user.renamed" );
        dialog.clickOkButton();

        // verify the entry was copied
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.renamed" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.renamed" );

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "ENTRY_ALREADY_EXISTS", "dn: uid=user.1,ou=users,ou=system",
            "changetype: add", "uid: user.1" );
        modificationLogsViewBot.assertContainsOk( "dn: uid=user.renamed,ou=users,ou=system", "changetype: add",
            "uid: user.renamed" );
    }


    @Test
    public void testCopyPasteSpecialEntries() throws Exception
    {
        // disable alias dereferencing
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            AliasDereferencingMethod.NEVER.ordinal() );
        // enable ManageDsaIT control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );
        // enable Subentries control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES, true );

        // expand the entries to avoid copy depth dialog
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=special", "cn=alias" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=special", "cn=referral" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=special", "cn=subentry" );

        // select and copy the entries
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=special" );
        String[] children =
            { "cn=alias", "cn=referral", "cn=subentry" };
        browserViewBot.selectChildrenOfEntry( children, "DIT", "Root DSE", "ou=system", "ou=special" );
        browserViewBot.copy();

        // select the parent entry where the copied entries should be pasted to
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=target" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "cn=alias" ) );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "cn=referral" ) );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "cn=subentry" ) );

        // paste the entries
        browserViewBot.pasteEntries( 3 );

        // verify the entries was copied
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "cn=alias" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "cn=referral" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=target", "cn=subentry" ) );

        // verify in modification logs
        modificationLogsViewBot.assertContainsOk( "dn: cn=alias,ou=target,ou=system", "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: cn=referral,ou=target,ou=system",
            "control: 2.16.840.1.113730.3.4.2 false", "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: cn=subentry,ou=target,ou=system", "changetype: add" );
    }

}
