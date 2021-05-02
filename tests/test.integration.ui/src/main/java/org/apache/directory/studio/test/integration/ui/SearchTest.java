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
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.GROUP1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.GROUPS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MISC_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.SUBENTRY_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER8_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USERS_DN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.message.ModifyRequest;
import org.apache.directory.api.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.FilterEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchPropertiesDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchResultEditorBot;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the Search dialog and Search category in the LDAP Browser view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchTest extends AbstractTestBase
{

    /**
     * Test for DIRSTUDIO-490.
     *
     * Copy/Paste a search between connections and verify that the associated browser connection is correct.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteSearchBetweenConnections( TestLdapServer server ) throws Exception
    {
        Connection connection1 = connectionsViewBot.createTestConnection( server );
        Connection connection2 = connectionsViewBot.createTestConnection( server );

        BrowserConnectionManager browserConnectionManager = BrowserCorePlugin.getDefault().getConnectionManager();
        IBrowserConnection browserConnection1 = browserConnectionManager.getBrowserConnectionByName( connection1
            .getName() );
        IBrowserConnection browserConnection2 = browserConnectionManager.getBrowserConnectionByName( connection2
            .getName() );
        assertEquals( 0, browserConnection1.getSearchManager().getSearches().size() );
        assertEquals( 0, browserConnection2.getSearchManager().getSearches().size() );

        // create a search for in connection 1
        connectionsViewBot.select( connection1.getName() );
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
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
        connectionsViewBot.select( connection2.getName() );
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
     */
    @ParameterizedTest
    @LdapServersSource
    public void testOnlyOneUiUpdateOnQuickSearch( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
        browserViewBot.expandEntry( path( CONTEXT_DN ) );

        browserViewBot.typeQuickSearchAttributeType( "ou" );
        browserViewBot.typeQuickSearchValue( "*" );

        long fireCount0 = EventRegistry.getFireCount();
        browserViewBot.clickRunQuickSearchButton();
        browserViewBot.waitForEntry( path( CONTEXT_DN, "Quick Search" ) );
        long fireCount1 = EventRegistry.getFireCount();

        browserViewBot.selectEntry( path( CONTEXT_DN, "Quick Search" ) );

        // verify that only one event was fired
        long fireCount = fireCount1 - fireCount0;
        assertEquals( 1, fireCount, "Only 1 event firings expected when running quick search." );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testQuickSearch( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        // quick search on context entry
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
        browserViewBot.expandEntry( path( CONTEXT_DN ) );

        browserViewBot.typeQuickSearchAttributeType( "ou" );
        browserViewBot.typeQuickSearchValue( "*" );
        browserViewBot.clickRunQuickSearchButton();

        browserViewBot.waitForEntry( path( CONTEXT_DN, "Quick Search (5)" ) );
        browserViewBot.selectEntry( path( CONTEXT_DN, "Quick Search (5)" ) );
        browserViewBot.expandEntry( path( CONTEXT_DN, "Quick Search (5)" ) );
        browserViewBot.selectEntry( path( CONTEXT_DN, "Quick Search (5)", USERS_DN.getName() ) );

        // quick search on non-leaf entry
        browserViewBot.selectEntry( path( USERS_DN ) );
        browserViewBot.expandEntry( path( USERS_DN ) );

        browserViewBot.typeQuickSearchAttributeType( "uid" );
        browserViewBot.typeQuickSearchValue( "user.1" );
        browserViewBot.clickRunQuickSearchButton();

        browserViewBot.waitForEntry( path( USERS_DN, "Quick Search (1)" ) );
        browserViewBot.selectEntry( path( USERS_DN, "Quick Search (1)" ) );
        browserViewBot.expandEntry( path( USERS_DN, "Quick Search (1)" ) );
        browserViewBot.selectEntry( path( USERS_DN, "Quick Search (1)", USER1_DN.getName() ) );

        // quick search on leaf entry
        browserViewBot.selectEntry( path( USER1_DN ) );
        browserViewBot.expandEntry( path( USER1_DN ) );

        browserViewBot.typeQuickSearchAttributeType( "uid" );
        browserViewBot.typeQuickSearchValue( "user.1" );
        browserViewBot.clickRunQuickSearchButton();

        browserViewBot.waitForEntry( path( USER1_DN, "Quick Search (0)" ) );
        browserViewBot.selectEntry( path( USER1_DN, "Quick Search (0)" ) );
        browserViewBot.expandEntry( path( USER1_DN, "Quick Search (0)" ) );
        browserViewBot.selectEntry( path( USER1_DN, "Quick Search (0)", "No Results" ) );
    }


    /**
     * Test for DIRSTUDIO-601.
     * (The 'Perform Search/Search Again' button in the Search Result Editor does not work correctly)
     */
    @ParameterizedTest
    @LdapServersSource
    public void testRefresh( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( GROUPS_DN ) );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( "Search Group" );
        dialogBot.setFilter( "(" + GROUP1_DN.getRdn().getName() + ")" );
        dialogBot.setReturningAttributes( "objectClass, cn, description" );
        dialogBot.clickSearchButton();
        browserViewBot.selectEntry( "Searches", "Search Group" );

        SearchResultEditorBot srEditorBot = studioBot.getSearchResultEditorBot( "Search Group" );
        srEditorBot.activate();
        assertTrue( srEditorBot.isEnabled() );

        // assert that description attribute is empty
        assertEquals( GROUP1_DN.getName(), srEditorBot.getContent( 1, 1 ) );
        assertEquals( "", srEditorBot.getContent( 1, 4 ) );

        // add description
        ModifyRequest request = new ModifyRequestImpl();
        request.setName( GROUP1_DN );
        request.replace( "description", "The 1st description." );
        server.withAdminConnection( conn -> {
            conn.modify( request );
        } );

        // refresh the search, using the toolbar icon
        srEditorBot.refresh();
        SWTUtils.sleep( 1000 );

        // assert the description attribute value is displayed now
        assertEquals( GROUP1_DN.getName(), srEditorBot.getContent( 1, 1 ) );
        assertEquals( "The 1st description.", srEditorBot.getContent( 1, 4 ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchAlias( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String searchName = "Search Alias";
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=alias)" );
        dialogBot.setReturningAttributes( "cn,aliasedObjectName" );
        dialogBot.setAliasDereferencingMode( AliasDereferencingMethod.NEVER );
        dialogBot.clickSearchButton();

        // assert search result exists in tree
        browserViewBot.expandEntry( "Searches", "Search Alias" );
        assertTrue( browserViewBot.existsEntry( "Searches", "Search Alias", ALIAS_DN.getName() ) );

        // assert attributes in search result editor
        browserViewBot.selectEntry( "Searches", "Search Alias" );
        SearchResultEditorBot srEditorBot = studioBot.getSearchResultEditorBot( "Search Alias" );
        srEditorBot.activate();
        assertTrue( srEditorBot.isEnabled() );
        assertEquals( ALIAS_DN.getName(), srEditorBot.getContent( 1, 1 ) );
        assertEquals( "alias", srEditorBot.getContent( 1, 2 ) );
        assertEquals( MISC_DN, srEditorBot.getContent( 1, 3 ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchReferral( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String searchName = "Search Referral";
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(&(objectClass=referral)(" + REFERRAL_TO_USER1_DN.getRdn().getName() + "))" );
        dialogBot.setReturningAttributes( "cn,ref" );
        dialogBot.setControlManageDsaIT( true );
        dialogBot.clickSearchButton();

        // assert search result exists in tree
        browserViewBot.expandEntry( "Searches", searchName );
        assertTrue(
            browserViewBot.existsEntry( "Searches", searchName, Utils.shorten( REFERRAL_TO_USER1_DN.getName(), 50 ) ) );

        // assert attributes in search result editor
        browserViewBot.selectEntry( "Searches", searchName );
        SearchResultEditorBot srEditorBot = studioBot.getSearchResultEditorBot( searchName );
        srEditorBot.activate();
        assertTrue( srEditorBot.isEnabled() );
        assertEquals( REFERRAL_TO_USER1_DN.getName(), srEditorBot.getContent( 1, 1 ) );
        assertEquals( REFERRAL_TO_USER1_DN.getRdn().getValue(), srEditorBot.getContent( 1, 2 ) );
        assertEquals( StringUtils.abbreviate( server.getLdapUrl() + "/" + USER1_DN.getName(), 50 ),
            srEditorBot.getContent( 1, 3 ) );
    }


    @ParameterizedTest
    @LdapServersSource(types = LdapServerType.ApacheDS)
    public void testSearchSubentry( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String searchName = "Search Subentry";
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=subentry)" );
        dialogBot.setReturningAttributes( "cn,subtreeSpecification" );
        dialogBot.setControlSubentries( true );
        dialogBot.clickSearchButton();

        // assert search result exists in tree
        browserViewBot.expandEntry( "Searches", searchName );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, SUBENTRY_DN.getName() ) );

        // assert attributes in search result editor
        browserViewBot.selectEntry( "Searches", searchName );
        SearchResultEditorBot srEditorBot = studioBot.getSearchResultEditorBot( searchName );
        srEditorBot.activate();
        assertTrue( srEditorBot.isEnabled() );
        assertEquals( SUBENTRY_DN.getName(), srEditorBot.getContent( 1, 1 ) );
        assertEquals( SUBENTRY_DN.getRdn().getValue(), srEditorBot.getContent( 1, 2 ) );
        assertEquals( "{}", srEditorBot.getContent( 1, 3 ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchWithPagingWithScrollMode( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String searchName = "Paged search with scroll mode";
        browserViewBot.selectEntry( path( USERS_DN ) );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=*)" );
        dialogBot.setReturningAttributes( "objectClass,ou,cn,uid" );
        dialogBot.setControlPagedSearch( true, 3, true );
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


    @ParameterizedTest
    @LdapServersSource
    public void testSearchWithPagingWithoutScrollMode( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String searchName = "Paged search without scroll mode";
        browserViewBot.selectEntry( path( USERS_DN ) );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=*)" );
        dialogBot.setReturningAttributes( "objectClass,ou,cn,uid" );
        dialogBot.setControlPagedSearch( true, 3, false );
        dialogBot.clickSearchButton();

        browserViewBot.expandEntry( "Searches", searchName );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName + " (9+)" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, USER1_DN.getName() ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, USER8_DN.getName() ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testFilterEditor( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String searchName = "Test filter editor";
        browserViewBot.selectEntry( path( USERS_DN ) );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setReturningAttributes( "objectClass,ou,cn,uid" );

        FilterEditorDialogBot filterBot = dialogBot.openFilterEditor();
        filterBot.setFilter( "(&(objectClass=*)(uid=user.1))" );
        filterBot.clickFormatButton();
        String formattetFilter = filterBot.getFilter();
        filterBot.clickOkButton();
        dialogBot.activate();
        String filter = dialogBot.getFilter();
        dialogBot.clickSearchButton();

        browserViewBot.expandEntry( "Searches", searchName );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, USER1_DN.getName() ) );
        assertEquals( "(&(objectClass=*)(uid=user.1))", filter );
        assertEquals( "(&\n    (objectClass=*)\n    (uid=user.1)\n)", formattetFilter );
    }


    /**
     * Test for DIRSTUDIO-1078/DIRAPI-365: unable to use # pound hash sign in LDAP filters
     */
    @ParameterizedTest
    @LdapServersSource
    public void testFilterForDnWithLeadingHash( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String searchName = "Test filter for DN with leading hash character";
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setReturningAttributes( "objectClass,ou,cn,uid" );

        String filterValue = DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED.getName().replace( "\\#", "\\5c#" );
        FilterEditorDialogBot filterBot = dialogBot.openFilterEditor();
        filterBot.setFilter( "member=" + filterValue );
        filterBot.clickFormatButton();
        filterBot.clickOkButton();
        dialogBot.activate();
        String filter = dialogBot.getFilter();
        dialogBot.clickSearchButton();

        browserViewBot.expandEntry( "Searches", searchName );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, GROUP1_DN.getName() ) );
        assertEquals( "(member=" + filterValue + ")", filter );
    }


    /**
     * Test for DIRSTUDIO-1078/DIRAPI-365: unable to use # pound hash sign in LDAP filters
     */
    @ParameterizedTest
    @LdapServersSource
    public void testFilterForDnWithLeadingHashHex( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String searchName = "Test filter for DN with leading hash character";
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setReturningAttributes( "objectClass,ou,cn,uid" );

        String filterValue = DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED.getName().replace( "\\23", "\\5C23" );
        FilterEditorDialogBot filterBot = dialogBot.openFilterEditor();
        filterBot.setFilter( "member=" + filterValue );
        filterBot.clickFormatButton();
        filterBot.clickOkButton();
        dialogBot.activate();
        String filter = dialogBot.getFilter();
        dialogBot.clickSearchButton();

        browserViewBot.expandEntry( "Searches", searchName );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, GROUP1_DN.getName() ) );
        assertEquals( "(member=" + filterValue + ")", filter );
    }

}
