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
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapService;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
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
public class BrowserTest
{
    public static LdapService ldapService;

    private SWTWorkbenchBot bot;


    @Before
    public void setUp() throws Exception
    {
        bot = new SWTWorkbenchBot();
        SWTBotUtils.openLdapPerspective( bot );
        SWTBotUtils.createTestConnection( bot, "BrowserTest", ldapService.getPort() );
    }


    @After
    public void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
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
        SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system" );

        // get number of search requests before expanding the entry
        SWTBotStyledText searchLogsText = SWTBotUtils.getSearchLogsText( bot );
        String text = searchLogsText.getText();
        int countMatchesBefore = StringUtils.countMatches( text, "#!SEARCH REQUEST" );

        // expand
        browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotUtils.selectEntry( bot, browserTree, true, "DIT", "Root DSE", "ou=system" );

        // get number of search requests after expanding the entry
        searchLogsText = SWTBotUtils.getSearchLogsText( bot );
        text = searchLogsText.getText();
        int countMatchesAfter = StringUtils.countMatches( text, "#!SEARCH REQUEST" );

        assertEquals( "Expected exactly 1 search request", 1, countMatchesAfter - countMatchesBefore );
    }
}
