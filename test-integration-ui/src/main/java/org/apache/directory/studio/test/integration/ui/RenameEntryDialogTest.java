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


import net.sf.swtbot.eclipse.finder.SWTEclipseBot;
import net.sf.swtbot.widgets.SWTBotMenu;
import net.sf.swtbot.widgets.SWTBotTree;

import org.apache.directory.server.unit.AbstractServerTest;


/**
 * Tests the rename entry dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RenameEntryDialogTest extends AbstractServerTest
{
    private SWTEclipseBot bot;


    protected void setUp() throws Exception
    {
        super.setUp();
        super.loadTestLdif( true );
        bot = new SWTEclipseBot();
        SWTBotUtils.openLdapPerspective( bot );
        SWTBotUtils.createTestConnection( bot, "RenameEntryDialogTest", ldapServer.getIpPort() );
    }


    protected void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
        super.tearDown();
    }


    /**
     * Test for DIRSTUDIO-318.
     * 
     * Renames a multi-valued RDN by changing both RDN attributes.
     * 
     * @throws Exception the exception
     */
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

}
