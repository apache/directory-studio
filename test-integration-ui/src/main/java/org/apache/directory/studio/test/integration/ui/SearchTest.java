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

import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
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

    private SWTWorkbenchBot bot;


    @Before
    public void setUp() throws Exception
    {
        bot = new SWTWorkbenchBot();
        SWTBotUtils.openLdapPerspective( bot );
        SWTBotUtils.createTestConnection( bot, "SearchTest1", ldapServer.getPort() );
        SWTBotUtils.createTestConnection( bot, "SearchTest2", ldapServer.getPort() );
    }


    @After
    public void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
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

        SWTBotTree connectionsTree = SWTBotUtils.getConnectionsTree( bot );
        SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        // create a search for in connection 1
        connectionsTree.select( "SearchTest1" );
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system" );
        browserTree.contextMenu( "New Search..." ).click();
        bot.shell( "Search" );
        //SWTBotAssert.assertEnabled( bot.textWithLabel( "Connection:" ) );
        //SWTBotAssert.assertText( "SearchTest1", bot.textWithLabel( "Connection:" ) );
        bot.textWithLabel( "Search Name:" ).setText( "Search all persons" );
        bot.comboBoxWithLabel( "Filter:" ).setText( "(objectClass=person)" );
        //bot.radioWithLabelInGroup( "Subtree", "Scope" ).click();
        bot.button( "Search" ).click();
        SWTBotUtils.selectEntry( bot, browserTree, false, "Searches", "Search all persons" );

        // assert browser connection in searches
        assertEquals( 1, browserConnection1.getSearchManager().getSearches().size() );
        assertEquals( browserConnection1, browserConnection1.getSearchManager().getSearches().get( 0 )
            .getBrowserConnection() );
        assertEquals( 0, browserConnection2.getSearchManager().getSearches().size() );

        // copy/paste the created search from connection 1 to connection 2
        browserTree.contextMenu( "Copy Search" ).click();
        connectionsTree.select( "SearchTest2" );
        SWTBotUtils.selectEntry( bot, browserTree, false, "Searches" );
        browserTree.contextMenu( "Paste Search" ).click();
        bot.shell( "Properties for Search all persons" );
        //SWTBotAssert.assertNotEnabled( bot.textWithLabel( "Connection:" ) );
        //SWTBotAssert.assertText( "SearchTest2", bot.textWithLabel( "Connection:" ) );
        bot.button( "Cancel" ).click();

        // assert browser connection in searches
        assertEquals( 1, browserConnection1.getSearchManager().getSearches().size() );
        assertEquals( browserConnection1, browserConnection1.getSearchManager().getSearches().get( 0 )
            .getBrowserConnection() );
        assertEquals( 1, browserConnection2.getSearchManager().getSearches().size() );
        assertEquals( browserConnection2, browserConnection2.getSearchManager().getSearches().get( 0 )
            .getBrowserConnection() );
    }

}
