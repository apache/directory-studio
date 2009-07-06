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


import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the rename entry dialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(SiRunner.class)
@CleanupLevel(Level.SUITE)
@ApplyLdifFiles("RenameEntryDialogTest.ldif")
public class RenameEntryDialogTest
{
    public static LdapServer ldapServer;

    private SWTWorkbenchBot bot;


    @Before
    public void setUp() throws Exception
    {
        bot = new SWTWorkbenchBot();
        SWTBotUtils.openLdapPerspective( bot );
        SWTBotUtils.createTestConnection( bot, "RenameEntryDialogTest", ldapServer.getPort() );
    }


    @After
    public void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
    }


    /**
     * Test for DIRSTUDIO-318.
     * 
     * Renames a multi-valued RDN by changing both RDN attributes.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testRenameMultiValuedRdn() throws Exception
    {
        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Barbara Jensen+uid=bjensen" );

        bot.sleep( 2000 );
        SWTBotMenu contextMenu = browserTree.contextMenu( "Rename Entry..." );
        contextMenu.click();

        bot.text( "Barbara Jensen" ).setText( "Babs Jensen" );
        bot.text( "bjensen" ).setText( "babsjens" );
        bot.button( "OK" ).click();

        // ensure that the entry with the new name exists
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Babs Jensen+uid=babsjens" );
    }


    /**
     * Test for DIRSTUDIO-484.
     * 
     * Renames a RDN with escaped characters.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testRenameRdnWithEscapedCharacters() throws Exception
    {
        SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );

        SWTBotMenu contextMenu = browserTree.contextMenu( "Rename Entry..." );
        contextMenu.click();

        // ensure that the unescaped value is in the text field
        bot.text( "#\\+, \"\u00F6\u00E9\"" ).setText( "#\\+, \"\u00F6\u00E9\"2" );
        bot.button( "OK" ).click();

        // ensure that the entry with the new name exists
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"2" );
    }

}
