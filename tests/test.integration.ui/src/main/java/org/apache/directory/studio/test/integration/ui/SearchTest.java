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
import static org.junit.Assert.assertTrue;

import org.apache.directory.api.ldap.model.message.ModifyRequest;
import org.apache.directory.api.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchPropertiesDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchResultEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the Search dialog and Search category in the LDAP Browser view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles(clazz = BrowserTest.class, value = "org/apache/directory/studio/test/integration/ui/BrowserTest.ldif")
public class SearchTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;

    private Connection connection1;
    private Connection connection2;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connection1 = connectionsViewBot.createTestConnection( "SearchTest1", ldapServer.getPort() );
        connection2 = connectionsViewBot.createTestConnection( "SearchTest2", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();
    }


    /**
     * Test for DIRSTUDIO-490.
     *
     * Copy/Paste a search between connections and verify that the associated browser connection is correct.
     *
     * @throws Exception
     */
    @Test
    public void testCopyPasteSearchBetweenConnections() throws Exception
    {
        BrowserConnectionManager browserConnectionManager = BrowserCorePlugin.getDefault().getConnectionManager();
        IBrowserConnection browserConnection1 = browserConnectionManager.getBrowserConnectionByName( connection1
            .getName() );
        IBrowserConnection browserConnection2 = browserConnectionManager.getBrowserConnectionByName( connection2
            .getName() );
        assertEquals( 0, browserConnection1.getSearchManager().getSearches().size() );
        assertEquals( 0, browserConnection2.getSearchManager().getSearches().size() );

        // create a search for in connection 1
        connectionsViewBot.selectConnection( connection1.getName() );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( "Search all persons" );
        dialogBot.setFilter( "(objectClass=person)" );
        dialogBot.clickSearchButton();
        browserViewBot.selectEntry( "Searches", "Search all persons" );

        // assert browser connection in searches
        assertEquals( 1, browserConnection1.getSearchManager().getSearches().size() );
        assertEquals( browserConnection1, browserConnection1.getSearchManager().getSearches().get( 0 )
            .getBrowserConnection() );
        assertEquals( 0, browserConnection2.getSearchManager().getSearches().size() );

        // copy/paste the created search from connection 1 to connection 2
        browserViewBot.copy();
        connectionsViewBot.selectConnection( connection2.getName() );
        browserViewBot.selectEntry( "Searches" );
        SearchPropertiesDialogBot searchPropertiesDialogBot = browserViewBot.pasteSearch();
        assertTrue( searchPropertiesDialogBot.isVisible() );
        searchPropertiesDialogBot.clickCancelButton();

        // assert browser connection in searches
        assertEquals( 1, browserConnection1.getSearchManager().getSearches().size() );
        assertEquals( browserConnection1, browserConnection1.getSearchManager().getSearches().get( 0 )
            .getBrowserConnection() );
        assertEquals( 1, browserConnection2.getSearchManager().getSearches().size() );
        assertEquals( browserConnection2, browserConnection2.getSearchManager().getSearches().get( 0 )
            .getBrowserConnection() );
    }


    /**
     * Test for DIRSTUDIO-587 (UI flickers on quick search).
     *
     * When performing a quick search only one UI update should be fired.
     *
     * @throws Exception
     */
    @Test
    public void testOnlyOneUiUpdateOnQuickSearch() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system" );

        browserViewBot.typeQuickSearchAttributeType( "ou" );
        browserViewBot.typeQuickSearchValue( "*" );

        long fireCount0 = EventRegistry.getFireCount();
        browserViewBot.clickRunQuickSearchButton();
        browserViewBot.waitForEntry( "DIT", "Root DSE", "ou=system", "Quick Search" );
        long fireCount1 = EventRegistry.getFireCount();

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "Quick Search" );

        // verify that only one event was fired
        long fireCount = fireCount1 - fireCount0;
        assertEquals( "Only 1 event firings expected when running quick search.", 1, fireCount );
    }


    /**
     * Test for DIRSTUDIO-601.
     * (The 'Perform Search/Search Again' button in the Search Result Editor does not work correctly)
     *
     * @throws Exception
     */
    @Test
    public void testRefresh() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( "Search Admin" );
        dialogBot.setFilter( "(uid=admin)" );
        dialogBot.setReturningAttributes( "objectClass, uid, description" );
        dialogBot.clickSearchButton();
        browserViewBot.selectEntry( "Searches", "Search Admin" );

        SearchResultEditorBot srEditorBot = studioBot.getSearchResultEditorBot( "Search Admin" );
        srEditorBot.activate();
        assertTrue( srEditorBot.isEnabled() );

        // assert that description attribute is empty
        assertEquals( "uid=admin,ou=system", srEditorBot.getContent( 1, 1 ) );
        assertEquals( "", srEditorBot.getContent( 1, 4 ) );

        // add description
        ModifyRequest request = new ModifyRequestImpl();
        request.setName( new Dn( "uid=admin,ou=system" ) );
        request.replace( "description", "The 1st description." );
        service.getAdminSession().modify( request );

        // refresh the search, using the toolbar icon
        srEditorBot.refresh();
        SWTUtils.sleep( 1000 );

        // assert the description attribute value is displayed now
        assertEquals( "uid=admin,ou=system", srEditorBot.getContent( 1, 1 ) );
        assertEquals( "The 1st description.", srEditorBot.getContent( 1, 4 ) );
    }


    @Test
    public void testSearchAlias() throws Exception
    {
        String searchName = "Search Alias";
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=alias)" );
        dialogBot.setReturningAttributes( "cn,aliasedObjectName" );
        dialogBot.setAliasDereferencingMode( AliasDereferencingMethod.NEVER );
        dialogBot.clickSearchButton();

        // assert search result exists in tree
        browserViewBot.expandEntry( "Searches", "Search Alias" );
        assertTrue( browserViewBot.existsEntry( "Searches", "Search Alias", "cn=alias,ou=special,ou=system" ) );

        // assert attributes in search result editor
        browserViewBot.selectEntry( "Searches", "Search Alias" );
        SearchResultEditorBot srEditorBot = studioBot.getSearchResultEditorBot( "Search Alias" );
        srEditorBot.activate();
        assertTrue( srEditorBot.isEnabled() );
        assertEquals( "cn=alias,ou=special,ou=system", srEditorBot.getContent( 1, 1 ) );
        assertEquals( "alias", srEditorBot.getContent( 1, 2 ) );
        assertEquals( "ou=special,ou=system", srEditorBot.getContent( 1, 3 ) );
    }


    @Test
    public void testSearchReferral() throws Exception
    {
        String searchName = "Search Referral";
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=referral)" );
        dialogBot.setReturningAttributes( "cn,ref" );
        dialogBot.setControlManageDsaIT( true );
        dialogBot.clickSearchButton();

        // assert search result exists in tree
        browserViewBot.expandEntry( "Searches", searchName );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "cn=referral,ou=special,ou=system" ) );

        // assert attributes in search result editor
        browserViewBot.selectEntry( "Searches", searchName );
        SearchResultEditorBot srEditorBot = studioBot.getSearchResultEditorBot( searchName );
        srEditorBot.activate();
        assertTrue( srEditorBot.isEnabled() );
        assertEquals( "cn=referral,ou=special,ou=system", srEditorBot.getContent( 1, 1 ) );
        assertEquals( "referral", srEditorBot.getContent( 1, 2 ) );
        assertEquals( "ldap://foo.example.com/ou=system", srEditorBot.getContent( 1, 3 ) );
    }


    @Test
    public void testSearchSubentry() throws Exception
    {
        String searchName = "Search Subentry";
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=subentry)" );
        dialogBot.setReturningAttributes( "cn,subtreeSpecification" );
        dialogBot.setControlSubentries( true );
        dialogBot.clickSearchButton();

        // assert search result exists in tree
        browserViewBot.expandEntry( "Searches", searchName );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "cn=subentry,ou=special,ou=system" ) );

        // assert attributes in search result editor
        browserViewBot.selectEntry( "Searches", searchName );
        SearchResultEditorBot srEditorBot = studioBot.getSearchResultEditorBot( searchName );
        srEditorBot.activate();
        assertTrue( srEditorBot.isEnabled() );
        assertEquals( "cn=subentry,ou=special,ou=system", srEditorBot.getContent( 1, 1 ) );
        assertEquals( "subentry", srEditorBot.getContent( 1, 2 ) );
        assertEquals( "{ }", srEditorBot.getContent( 1, 3 ) );
    }


    @Test
    public void testSearchWithPagingWithScrollMode() throws Exception
    {
        String searchName = "Paged search with scroll mode";
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=*)" );
        dialogBot.setReturningAttributes( "objectClass,ou,cn,uid" );
        dialogBot.setControlPagedSearch( true, 5, true );
        dialogBot.clickSearchButton();

        // 1st page
        browserViewBot.expandEntry( "Searches", searchName );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );

        // next page
        browserViewBot.selectEntry( "Searches", searchName, "--- Next Page ---" );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );

        // last page
        browserViewBot.selectEntry( "Searches", searchName, "--- Next Page ---" );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );

        // back to top
        browserViewBot.selectEntry( "Searches", searchName, "--- Top Page ---" );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );
    }


    @Test
    public void testSearchWithPagingWithoutScrollMode() throws Exception
    {
        String searchName = "Paged search without scroll mode";
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=*)" );
        dialogBot.setReturningAttributes( "objectClass,ou,cn,uid" );
        dialogBot.setControlPagedSearch( true, 5, false );
        dialogBot.clickSearchButton();

        browserViewBot.expandEntry( "Searches", searchName );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName + " (15+)" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "uid=user.1" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "uid=user.8" ) );
    }

}
