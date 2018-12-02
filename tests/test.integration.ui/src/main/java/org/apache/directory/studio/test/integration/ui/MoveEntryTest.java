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

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.MoveEntriesDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SelectDnDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests entry move (moddn) and the move dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles(clazz = MoveEntryTest.class, value = "org/apache/directory/studio/test/integration/ui/RenameEntryDialogTest.ldif")
public class MoveEntryTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "MoveEntryTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();
    }


    @Test
    public void testMoveUp() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen+uid=bjensen" );

        MoveEntriesDialogBot moveEntryDialog = browserViewBot.openMoveEntryDialog();
        assertTrue( moveEntryDialog.isVisible() );
        moveEntryDialog.setParentText( "ou=system" );
        moveEntryDialog.clickOkButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "cn=Barbara Jensen+uid=bjensen" ) );
        assertFalse(
            browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen+uid=bjensen" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "cn=Barbara Jensen+uid=bjensen" );
    }


    @Test
    public void testMoveDown() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#123456" );

        MoveEntriesDialogBot moveEntryDialog = browserViewBot.openMoveEntryDialog();
        assertTrue( moveEntryDialog.isVisible() );
        SelectDnDialogBot selectDnBot = moveEntryDialog.clickBrowseButtonExpectingSelectDnDialog();
        assertTrue( selectDnBot.isVisible() );
        selectDnBot.selectEntry( "Root DSE", "ou=system", "ou=users", "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );
        selectDnBot.clickOkButton();
        moveEntryDialog.activate();
        assertEquals( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system", moveEntryDialog.getParentText() );
        moveEntryDialog.clickOkButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"", "cn=\\#123456" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"", "cn=\\#123456" );
    }

}
