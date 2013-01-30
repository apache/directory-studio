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


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Bookmark;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ModificationLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ReferralDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the LDAP browser.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles(
    { "org/apache/directory/studio/test/integration/ui/BrowserTest.ldif" })
public class BrowserTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;
    private SearchLogsViewBot searchLogsViewBot;
    private ModificationLogsViewBot modificationLogsViewBot;

    private Connection connection;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        System.out.println( connectionsViewBot );
        System.out.println( ldapServer );
        connection = connectionsViewBot.createTestConnection( "BrowserTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
        searchLogsViewBot = studioBot.getSearchLogsViewBot();
        modificationLogsViewBot = studioBot.getModificationLogsViewBot();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
    }


    /**
     * Test for DIRSTUDIO-463.
     *
     * When expanding an entry in the browser only one search request
     * should be send to the server
     *
     * @throws Exception
     */
    @Test
    public void testOnlyOneSearchRequestWhenExpandingEntry() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );

        // get number of search requests before expanding the entry
        String text = searchLogsViewBot.getSearchLogsText();
        int countMatchesBefore = StringUtils.countMatches( text, "#!SEARCH REQUEST" );

        // expand
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system" );
        browserViewBot.waitForEntry( "DIT", "Root DSE", "ou=system", "ou=users" );

        // get number of search requests after expanding the entry
        text = searchLogsViewBot.getSearchLogsText();
        int countMatchesAfter = StringUtils.countMatches( text, "#!SEARCH REQUEST" );

        assertEquals( "Expected exactly 1 search request", 1, countMatchesAfter - countMatchesBefore );

        assertEquals( "No modification expected", "", modificationLogsViewBot.getModificationLogsText() );
    }


    /**
     * Test for DIRSTUDIO-512.
     *
     * Verify minimum UI updates when deleting multiple entries.
     *
     * @throws Exception
     */
    @Test
    public void testDeleteDontUpdateUI() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );

        long fireCount0 = EventRegistry.getFireCount();

        // delete
        String[] children = new String[]
            { "uid=user.1", "uid=user.2", "uid=user.3", "uid=user.4", "uid=user.5", "uid=user.6", "uid=user.7",
                "uid=user.8" };
        browserViewBot.selectChildrenOfEnty( children, "DIT", "Root DSE", "ou=system", "ou=users" );
        DeleteDialogBot deleteDialog = browserViewBot.openDeleteDialog();
        deleteDialog.clickOkButton();
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );

        long fireCount1 = EventRegistry.getFireCount();

        // verify that only two events were fired during deletion
        long fireCount = fireCount1 - fireCount0;
        assertEquals( "Only 2 event firings expected when deleting multiple entries.", 2, fireCount );
    }


    /**
     * Test for DIRSTUDIO-575.
     *
     * When opening a bookmark the entry editor should be opened and the
     * bookmark entry's attributes should be displayed.
     *
     * @throws Exception
     */
    @Test
    public void testBookmark() throws Exception
    {
        // create a bookmark
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( connection );
        browserConnection.getBookmarkManager().addBookmark(
            new Bookmark( browserConnection, new Dn( "uid=user.1,ou=users,ou=system" ), "Existing Bookmark" ) );

        // select the bookmark
        browserViewBot.selectEntry( "Bookmarks", "Existing Bookmark" );

        // check that entry editor was opened and attributes are visible
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,ou=system" );
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: uid=user.1,ou=users,ou=system", dn );
        List<String> attributeValues = entryEditorBot.getAttributeValues();
        assertEquals( 23, attributeValues.size() );
        assertTrue( attributeValues.contains( "uid: user.1" ) );

        assertEquals( "No modification expected", "", modificationLogsViewBot.getModificationLogsText() );
    }


    /**
     * Test for DIRSTUDIO-481.
     *
     * Check proper operation of refresh action.
     *
     * @throws Exception
     */
    @Test
    public void testRefreshParent() throws Exception
    {
        // check the entry doesn't exist yet
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );

        // add the entry directly in the server
        Entry entry = new DefaultEntry( service.getSchemaManager() );
        entry.setDn( new Dn( "cn=refresh,ou=users,ou=system" ) );
        entry.add( "objectClass", "top", "person" );
        entry.add( "cn", "refresh" );
        entry.add( "sn", "refresh" );
        ldapServer.getDirectoryService().getAdminSession().add( entry );

        // check the entry still isn't visible in the tree
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );

        // refresh parent
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        browserViewBot.refresh();

        // check the entry exists now
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" );

        // delete the entry directly in the server
        ldapServer.getDirectoryService().getAdminSession().delete( new Dn( "cn=refresh,ou=users,ou=system" ) );

        // check the entry still is now visible in the tree
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );

        // refresh parent
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        browserViewBot.refresh();

        // check the entry doesn't exist now
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );
    }


    /**
     * Test for DIRSTUDIO-481.
     *
     * Check proper operation of refresh action.
     *
     * @throws Exception
     */
    @Test
    public void testRefreshContextEntry() throws Exception
    {
        // check the entry doesn't exist yet
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );

        // add the entry directly in the server
        Entry entry = new DefaultEntry( service.getSchemaManager() );
        entry.setDn( new Dn( "cn=refresh,ou=users,ou=system" ) );
        entry.add( "objectClass", "top", "person" );
        entry.add( "cn", "refresh" );
        entry.add( "sn", "refresh" );
        ldapServer.getDirectoryService().getAdminSession().add( entry );

        // check the entry still isn't visible in the tree
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );

        // refresh context entry
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );
        browserViewBot.refresh();

        // check the entry exists now
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" );

        // delete the entry directly in the server
        ldapServer.getDirectoryService().getAdminSession().delete( new Dn( "cn=refresh,ou=users,ou=system" ) );

        // check the entry still is now visible in the tree
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );

        // refresh context entry
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );
        browserViewBot.refresh();

        // check the entry doesn't exist now
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );
    }


    /**
     * Test for DIRSTUDIO-481.
     *
     * Check proper operation of refresh action.
     *
     * @throws Exception
     */
    @Test
    public void testRefreshRootDSE() throws Exception
    {
        // check the entry doesn't exist yet
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );

        // add the entry directly in the server
        Entry entry = new DefaultEntry( service.getSchemaManager() );
        entry.setDn( new Dn( "cn=refresh,ou=users,ou=system" ) );
        entry.add( "objectClass", "top", "person" );
        entry.add( "cn", "refresh" );
        entry.add( "sn", "refresh" );
        ldapServer.getDirectoryService().getAdminSession().add( entry );

        // check the entry still isn't visible in the tree
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );

        // refresh Root DSE
        browserViewBot.selectEntry( "DIT", "Root DSE" );
        browserViewBot.refresh();

        // check the entry exists now
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" );

        // delete the entry directly in the server
        ldapServer.getDirectoryService().getAdminSession().delete( new Dn( "cn=refresh,ou=users,ou=system" ) );

        // check the entry still is now visible in the tree
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );

        // refresh Root DSE
        browserViewBot.selectEntry( "DIT", "Root DSE" );
        browserViewBot.refresh();

        // check the entry doesn't exist now
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=refresh" ) );
    }


    /**
     * Test for DIRSTUDIO-481.
     *
     * Check proper operation of refresh action.
     *
     * @throws Exception
     */
    @Test
    public void testRefreshSearchContinuation() throws Exception
    {
        // preparation: add referral entry and set referral handling
        String url = "ldap://localhost:" + ldapServer.getPort() + "/ou=users,ou=system";
        Entry refEntry = new DefaultEntry( service.getSchemaManager() );
        refEntry.setDn( new Dn( "cn=referral,ou=system" ) );
        refEntry.add( "objectClass", "top", "referral", "extensibleObject" );
        refEntry.add( "cn", "referral" );
        refEntry.add( "ref", url );
        ldapServer.getDirectoryService().getAdminSession().add( refEntry );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.FOLLOW_MANUALLY.ordinal() );
        browserViewBot.selectEntry( "DIT", "Root DSE" );
        browserViewBot.refresh();

        // check the entry doesn't exist yet
        ReferralDialogBot refDialog = browserViewBot.expandEntryExpectingReferralDialog( "DIT", "Root DSE",
            "ou=system", url );
        refDialog.clickOkButton();
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", url, "cn=refresh" ) );

        // add the entry directly in the server
        Entry entry = new DefaultEntry( service.getSchemaManager() );
        entry.setDn( new Dn( "cn=refresh,ou=users,ou=system" ) );
        entry.add( "objectClass", "top", "person" );
        entry.add( "cn", "refresh" );
        entry.add( "sn", "refresh" );
        ldapServer.getDirectoryService().getAdminSession().add( entry );

        // check the entry still isn't visible in the tree
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", url, "cn=refresh" ) );

        // refresh search continuation
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", url );
        browserViewBot.refresh();

        // check the entry exists now
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", url );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", url, "cn=refresh" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", url, "cn=refresh" );

        // delete the entry directly in the server
        ldapServer.getDirectoryService().getAdminSession().delete( new Dn( "cn=refresh,ou=users,ou=system" ) );

        // check the entry still is now visible in the tree
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", url, "cn=refresh" ) );

        // refresh search continuation
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", url );
        browserViewBot.refresh();

        // check the entry doesn't exist now
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", url );
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", url, "cn=refresh" ) );
    }


    /**
     * Test for DIRSTUDIO-591.
     * (Error reading objects with # in DN)
     *
     * @throws Exception
     */
    @Test
    public void testBrowseDnWithSharpAndHexSequence() throws Exception
    {
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=\\#ACL_AD-Projects_Author" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#ACL_AD-Projects_Author" );

        assertEquals( "No modification expected", "", modificationLogsViewBot.getModificationLogsText() );
    }


    /**
     * Test for DIRSTUDIO-597.
     * (Modification sent to the server while browsing through the DIT and refreshing entries)
     *
     * @throws Exception
     */
    @Test
    public void testNoModificationWhileBrowsingAndRefreshing() throws Exception
    {
        boolean errorDialogAutomatedMode = ErrorDialog.AUTOMATED_MODE;
        ErrorDialog.AUTOMATED_MODE = false;

        String text = modificationLogsViewBot.getModificationLogsText();
        assertEquals( "", text );

        try
        {
            assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
                "cn=\\#ACL_AD-Projects_Author" ) );

            for ( int i = 0; i < 5; i++ )
            {
                // select entry and refresh
                browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#ACL_AD-Projects_Author" );
                browserViewBot.refresh();

                // select parent and refresh
                browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
                browserViewBot.refresh();
            }
        }
        finally
        {
            // reset flag
            ErrorDialog.AUTOMATED_MODE = errorDialogAutomatedMode;
        }

        // check that modification logs is still empty
        // to ensure that no modification was sent to the server
        assertEquals( "No modification expected", "", modificationLogsViewBot.getModificationLogsText() );
    }


    /**
     * Test for DIRSTUDIO-603, DIRSHARED-41.
     * (Error browsing/entering rfc2307 compliant host entry.)
     */
    @Test
    public void testBrowseDnWithIpHostNumber() throws Exception
    {
        ApacheDsUtils.enableSchema( ldapServer, "nis" );

        // create entry with multi-valued RDN containing an IP address value
        Entry entry = new DefaultEntry( service.getSchemaManager() );
        entry.setDn( new Dn( "cn=loopback+ipHostNumber=127.0.0.1,ou=users,ou=system" ) );
        entry.add( "objectClass", "top", "device", "ipHost" );
        entry.add( "cn", "loopback" );
        entry.add( "ipHostNumber", "127.0.0.1" );
        ldapServer.getDirectoryService().getAdminSession().add( entry );

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=loopback+ipHostNumber=127.0.0.1" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=loopback+ipHostNumber=127.0.0.1" );
    }


    /**
     * DIRSTUDIO-637: copy/paste of attributes no longer works.
     * Test copy/paste of a value to a bookmark.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testCopyPasteValueToBookmark() throws Exception
    {
        // create a bookmark
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( connection );
        browserConnection.getBookmarkManager().addBookmark(
            new Bookmark( browserConnection, new Dn( "uid=user.2,ou=users,ou=system" ), "My Bookmark" ) );

        // copy a value
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,ou=system" );
        entryEditorBot.activate();
        entryEditorBot.copyValue( "uid", "user.1" );

        // select the bookmark
        browserViewBot.selectEntry( "Bookmarks", "My Bookmark" );
        entryEditorBot = studioBot.getEntryEditorBot( "uid=user.2,ou=users,ou=system" );
        entryEditorBot.activate();
        assertEquals( 23, entryEditorBot.getAttributeValues().size() );

        // paste the value
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__execute_ldif_name );
        browserViewBot.paste();
        watcher.waitUntilDone();

        // assert pasted value visible in editor
        assertEquals( 24, entryEditorBot.getAttributeValues().size() );
        entryEditorBot.getAttributeValues().contains( "uid: user.1" );

        // assert pasted value was written to directory
        Entry entry = ldapServer.getDirectoryService().getAdminSession().lookup(
            new Dn( "uid=user.2,ou=users,ou=system" ) );
        assertTrue( entry.contains( "uid", "user.1" ) );
    }

}
