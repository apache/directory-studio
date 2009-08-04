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

import org.apache.commons.lang.StringUtils;
import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
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

    private SWTWorkbenchBot eBot;


    @Before
    public void setUp() throws Exception
    {
        eBot = new SWTWorkbenchBot();
        SWTBotUtils.openLdapPerspective( eBot );
        SWTBotUtils.createTestConnection( eBot, "BrowserTest", ldapServer.getPort() );
    }


    @After
    public void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
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
        SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( eBot );
        SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE", "ou=system" );

        // get number of search requests before expanding the entry
        SWTBotStyledText searchLogsText = SWTBotUtils.getSearchLogsText( eBot );
        String text = searchLogsText.getText();
        int countMatchesBefore = StringUtils.countMatches( text, "#!SEARCH REQUEST" );

        // expand
        browserTree = SWTBotUtils.getLdapBrowserTree( eBot );
        SWTBotUtils.selectEntry( eBot, browserTree, true, "DIT", "Root DSE", "ou=system" );

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
        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( eBot );
        SWTBotTreeItem ou = SWTBotUtils.selectEntry( eBot, browserTree, true, "DIT", "Root DSE", "ou=system",
            "ou=users" );

        long fireCount0 = EventRegistry.getFireCount();

        // delete
        ou.select( "uid=user.1", "uid=user.2", "uid=user.3", "uid=user.4", "uid=user.5", "uid=user.6", "uid=user.7",
            "uid=user.8" );
        browserTree.contextMenu( "Delete Entries" ).click();
        eBot.shell( "Delete Entries" );
        eBot.button( "OK" ).click();

        // wait until tree is refreshed, that is if ou=users doesn't has any children
        eBot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                SWTBotTreeItem ou = SWTBotUtils.selectEntry( eBot, browserTree, true, "DIT", "Root DSE", "ou=system",
                    "ou=users" );
                return "ou=users".equals( ou.getText() ) && ou.getNodes().isEmpty();
            }


            public String getFailureMessage()
            {
                return "'ou=users' should be selected and should not contain any children";
            }
        } );

        long fireCount1 = EventRegistry.getFireCount();

        // verify that only two events were fired during deletion
        long fireCount = fireCount1 - fireCount0;
        assertEquals( "Only 2 event firings expected when deleting multiple entries.", 2, fireCount );
    }

}
