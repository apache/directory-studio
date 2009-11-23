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


import static junit.framework.Assert.assertTrue;

import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.RenameEntryDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
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

    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "RenameEntryDialogTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
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
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen+uid=bjensen" );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "Babs Jensen" );
        renameDialogBot.setRdnValue( 2, "babsjens" );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Babs Jensen+uid=babsjens" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Babs Jensen+uid=babsjens" );
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
        browserViewBot
            .selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "#\\+, \"\u00F6\u00E9\"2" );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"2" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"2" );
    }


    /**
     * Test for DIRSTUDIO-589, DIRSTUDIO-591, DIRSHARED-38.
     * 
     * Rename an entry with sharp in DN: cn=\#123456.
     */
    @Test
    public void testRenameRdnWithSharp() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#123456" );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "#ABCDEF" );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#ABCDEF" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#ABCDEF" );
    }


    /**
     * Test for DIRSHARED-39.
     * 
     * Rename an entry with trailing space in RDN.
     */
    @Test
    public void testRenameRdnWithTrailingSpace() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#123456" );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "#ABCDEF " );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#ABCDEF\\ " ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#ABCDEF\\ " );

        renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "A " );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=A\\ " ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=A\\ " );
    }

}
