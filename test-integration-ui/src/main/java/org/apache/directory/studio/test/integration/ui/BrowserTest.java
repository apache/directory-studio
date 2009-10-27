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
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Bookmark;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
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
@RunWith(SiRunner.class)
@CleanupLevel(Level.SUITE)
@ApplyLdifFiles(
    { "BrowserTest.ldif" })
public class BrowserTest
{
    public static LdapServer ldapServer;

    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;

    private Connection connection;

    private SWTWorkbenchBot eBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connection = connectionsViewBot.createTestConnection( "BrowserTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();

        eBot = new SWTWorkbenchBot();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        eBot = null;
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
        SWTBotStyledText searchLogsText = SWTBotUtils.getSearchLogsText( eBot );
        String text = searchLogsText.getText();
        int countMatchesBefore = StringUtils.countMatches( text, "#!SEARCH REQUEST" );

        // expand
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system" );
        browserViewBot.waitForEntry( "DIT", "Root DSE", "ou=system", "ou=users" );

        // get number of search requests after expanding the entry
        searchLogsText = SWTBotUtils.getSearchLogsText( eBot );
        text = searchLogsText.getText();
        int countMatchesAfter = StringUtils.countMatches( text, "#!SEARCH REQUEST" );

        assertEquals( "Expected exactly 1 search request", 1, countMatchesAfter - countMatchesBefore );
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
            new Bookmark( browserConnection, new LdapDN( "uid=user.1,ou=users,ou=system" ), "Existing Bookmark" ) );

        // select the bookmark
        browserViewBot.selectEntry( "Bookmarks", "Existing Bookmark" );

        // check that entry editor was opened and attributes are visible
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,ou=system" );
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: uid=user.1,ou=users,ou=system", dn );
        List<String> attributeValues = entryEditorBot.getAttributeValues();
        assertEquals( 23, attributeValues.size() );
        assertTrue( attributeValues.contains( "uid: user.1" ) );
    }

}
