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

import org.apache.directory.studio.apacheds.model.ServersHandler;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.test.integration.ui.bots.ApacheDSServersViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionFromServerDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewApacheDSServerWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the Apache DS Plugin's UI.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ApacheDSPluginTest
{
    private StudioBot studioBot;
    private ApacheDSServersViewBot serversViewBot;
    private ConnectionsViewBot connectionsViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        serversViewBot = studioBot.getApacheDSServersViewBot();
        connectionsViewBot = studioBot.getConnectionView();
    }


    /**
     * Run the following tests:
     * <ul>
     *      <li>Creates a new server</li>
     *      <li>Runs the server</li>
     *      <li>Stops the server (after waiting for the server to be completely started)</li>
     *      <li>Deletes the server</li>
     * </ul>
     */
    @Test
    public void serverCreationStartStopAndDeletion()
    {
        // Showing view
        serversViewBot.show();

        // Verifying the servers count is 0
        assertEquals( 0, getCoreServersCount() );
        assertEquals( 0, serversViewBot.getServersCount() );

        // Opening wizard
        NewApacheDSServerWizardBot wizardBot = serversViewBot.openNewServerWizard();

        // Verifying the wizard can't be finished yet
        assertFalse( wizardBot.isFinishButtonEnabled() );

        // Filling fields of the wizard
        String serverName = "NewServerWizardTest";
        wizardBot.typeServerName( serverName );

        // Verifying the wizard can now be finished
        assertTrue( wizardBot.isFinishButtonEnabled() );

        // Closing wizard
        wizardBot.clickFinishButton();
        serversViewBot.waitForServer( serverName );

        // Verifying the servers count is now 1
        assertEquals( 1, getCoreServersCount() );
        assertEquals( 1, serversViewBot.getServersCount() );

        // Starting the server
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // Stopping the server
        serversViewBot.stopServer( serverName );
        serversViewBot.waitForServerStop( serverName );

        // Deleting the server
        DeleteDialogBot deleteDialogBot = serversViewBot.openDeleteServerDialog();
        deleteDialogBot.clickOkButton();

        // Verifying the servers count is back to 0
        assertEquals( 0, getCoreServersCount() );
        assertEquals( 0, serversViewBot.getServersCount() );
    }


    /**
     * Verifies that the 'New Server' does not allow the creation of
     * 2 servers with the same name.
     */
    @Test
    public void verifyServerNameCollisionInNewWizard()
    {
        // Showing view
        serversViewBot.show();

        // Verifying the servers count is 0
        assertEquals( 0, getCoreServersCount() );
        assertEquals( 0, serversViewBot.getServersCount() );

        // Opening wizard
        NewApacheDSServerWizardBot wizardBot = serversViewBot.openNewServerWizard();

        // Verifying the wizard can't be finished yet
        assertFalse( wizardBot.isFinishButtonEnabled() );

        // Filling fields of the wizard
        String serverName = "NewServerWizardTest";
        wizardBot.typeServerName( serverName );

        // Verifying the wizard can now be finished
        assertTrue( wizardBot.isFinishButtonEnabled() );

        // Closing wizard
        wizardBot.clickFinishButton();
        serversViewBot.waitForServer( serverName );

        // Verifying the servers count is now 1
        assertEquals( 1, getCoreServersCount() );
        assertEquals( 1, serversViewBot.getServersCount() );

        // Opening wizard
        wizardBot = serversViewBot.openNewServerWizard();

        // Verifying the wizard can't be finished yet
        assertFalse( wizardBot.isFinishButtonEnabled() );

        // Filling fields of the wizard
        wizardBot.typeServerName( serverName );

        // Verifying the wizard can't be finished (because a server with
        // same name already exists)
        assertFalse( wizardBot.isFinishButtonEnabled() );

        // Canceling wizard
        wizardBot.clickCancelButton();

        // Selecting the server row
        serversViewBot.selectServer( serverName );

        // Deleting the server
        DeleteDialogBot deleteDialogBot = serversViewBot.openDeleteServerDialog();
        deleteDialogBot.clickOkButton();

        // Verifying the servers count is back to 0
        assertEquals( 0, getCoreServersCount() );
        assertEquals( 0, serversViewBot.getServersCount() );
    }


    /**
     * Checks the creation of a connection from a server.
     */
    @Test
    public void connectionCreationFromServer()
    {
        // Showing view
        serversViewBot.show();

        // Verifying the servers count is 0
        assertEquals( 0, getCoreServersCount() );
        assertEquals( 0, serversViewBot.getServersCount() );

        // Opening wizard
        NewApacheDSServerWizardBot wizardBot = serversViewBot.openNewServerWizard();

        // Verifying the wizard can't be finished yet
        assertFalse( wizardBot.isFinishButtonEnabled() );

        // Filling fields of the wizard
        String serverName = "NewServerWizardTest";
        wizardBot.typeServerName( serverName );

        // Verifying the wizard can now be finished
        assertTrue( wizardBot.isFinishButtonEnabled() );

        // Closing wizard
        wizardBot.clickFinishButton();
        serversViewBot.waitForServer( serverName );

        // Verifying the servers count is now 1
        assertEquals( 1, getCoreServersCount() );
        assertEquals( 1, serversViewBot.getServersCount() );

        // Starting the server
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // Verifying the connections count is 0
        assertEquals( 0, getBrowserConnectionsCount() );

        // Creating a connection associated with the server
        ConnectionFromServerDialogBot connectionFromServerDialogBot = serversViewBot.createConnectionFromServer();
        connectionFromServerDialogBot.clickOkButton();

        // Verifying the connections count is now 1
        assertEquals( 1, getBrowserConnectionsCount() );

        // Opening the connection
        connectionsViewBot.selectConnection( serverName );
        connectionsViewBot.openSelectedConnection();

        // Getting the associated connection object
        Connection connection = getBrowserConnection();

        // Checking if the connection is open
        waitForConnectionOpened( connection );
        assertTrue( connection.getConnectionWrapper().isConnected() );

        // Closing the connection
        connectionsViewBot.selectConnection( serverName );
        connectionsViewBot.closeSelectedConnections();

        // Checking if the connection is closed
        waitForConnectionClosed( connection );
        assertFalse( connection.getConnectionWrapper().isConnected() );

        // Deleting the connection
        connectionsViewBot.deleteTestConnections();

        // Stopping the server
        serversViewBot.stopServer( serverName );
        serversViewBot.waitForServerStop( serverName );

        // Deleting the server
        DeleteDialogBot deleteDialogBot = serversViewBot.openDeleteServerDialog();
        deleteDialogBot.clickOkButton();

        // Verifying the servers count is back to 0
        assertEquals( 0, getCoreServersCount() );
        assertEquals( 0, serversViewBot.getServersCount() );
    }


    /**
     * Gets the servers count found in the core of the plugin.
     *
     * @return
     *      the servers count found in the core of the plugin
     */
    public int getCoreServersCount()
    {
        ServersHandler serversHandler = ServersHandler.getDefault();
        if ( serversHandler != null )
        {
            return serversHandler.getServersList().size();
        }

        return 0;
    }


    /**
     * Get the connections count found in the LDAP Browser.
     *
     * @return
     *      the connections count found in the LDAP Browser
     */
    public int getBrowserConnectionsCount()
    {
        ConnectionFolderManager connectionFolderManager = ConnectionCorePlugin.getDefault()
            .getConnectionFolderManager();
        if ( connectionFolderManager != null )
        {
            ConnectionFolder rootConnectionFolder = connectionFolderManager.getRootConnectionFolder();
            if ( rootConnectionFolder != null )
            {
                return rootConnectionFolder.getConnectionIds().size();
            }
        }

        return 0;
    }


    /**
     * Get the connections count found in the LDAP Browser.
     *
     * @return
     *      the connections count found in the LDAP Browser
     */
    public Connection getBrowserConnection()
    {
        ConnectionFolderManager connectionFolderManager = ConnectionCorePlugin.getDefault()
            .getConnectionFolderManager();
        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        if ( connectionFolderManager != null )
        {
            ConnectionFolder rootConnectionFolder = connectionFolderManager.getRootConnectionFolder();
            if ( rootConnectionFolder != null )
            {
                return connectionManager.getConnectionById( rootConnectionFolder.getConnectionIds().get( 0 ) );
            }
        }

        return null;
    }


    /**
     * Waits until the given connection is opened.
     *
     * @param connection
     *      the connection
     */
    public void waitForConnectionOpened( final Connection connection )
    {
        new SWTWorkbenchBot().waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return connection.getConnectionWrapper().isConnected();
            }


            public String getFailureMessage()
            {
                return "Connection " + connection.getName() + " not opened in connections view.";
            }
        } );
    }


    /**
     * Waits until the given connection is closed.
     *
     * @param connection
     *      the connection
     */
    public void waitForConnectionClosed( final Connection connection )
    {
        new SWTWorkbenchBot().waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return !connection.getConnectionWrapper().isConnected();
            }


            public String getFailureMessage()
            {
                return "Connection " + connection.getName() + " not closed in connections view.";
            }
        } );
    }
}
