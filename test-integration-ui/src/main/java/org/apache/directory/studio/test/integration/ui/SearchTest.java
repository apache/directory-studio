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

import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchPropertiesDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
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
//@RunWith(SWTBotJunit4ClassRunner.class)
@RunWith(SiRunner.class)
@CleanupLevel(Level.SUITE)
public class SearchTest
{
    public static LdapServer ldapServer;

    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "SearchTest1", ldapServer.getPort() );
        connectionsViewBot.createTestConnection( "SearchTest2", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
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
        IBrowserConnection browserConnection1 = browserConnectionManager.getBrowserConnectionByName( "SearchTest1" );
        IBrowserConnection browserConnection2 = browserConnectionManager.getBrowserConnectionByName( "SearchTest2" );
        assertEquals( 0, browserConnection1.getSearchManager().getSearches().size() );
        assertEquals( 0, browserConnection2.getSearchManager().getSearches().size() );

        // create a search for in connection 1
        connectionsViewBot.selectConnection( "SearchTest1" );
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
        connectionsViewBot.selectConnection( "SearchTest2" );
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

}
