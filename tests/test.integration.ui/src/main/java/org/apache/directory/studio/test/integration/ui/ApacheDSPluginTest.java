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


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.test.integration.ui.bots.ApacheDSConfigurationEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ApacheDSServersViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionFromServerDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConsoleViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ModificationLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewApacheDSServerWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.apache.mina.util.AvailablePortFinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the Apache DS Plugin's UI.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
public class ApacheDSPluginTest
{
    private StudioBot studioBot;
    private ApacheDSServersViewBot serversViewBot;
    private ConnectionsViewBot connectionsViewBot;
    private ConsoleViewBot consoleViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        serversViewBot = studioBot.getApacheDSServersViewBot();
        connectionsViewBot = studioBot.getConnectionView();
        consoleViewBot = studioBot.getConsoleView();
    }


    /**
     * Run the following tests:
     * <ul>
     *      <li>Creates a new server</li>
     *      <li>Starts the server</li>
     *      <li>Creates and uses a connection</li>
     *      <li>Stops the server</li>
     *      <li>Deletes the server</li>
     * </ul>
     */
    @Test
    public void serverCreationStartCreateConnectionStopAndDeletion()
    {
        String serverName = "ServerCreationStartCreateConnectionStopAndDeletion";
        createServer( serverName );
        setAvailablePorts( serverName );

        // Starting the server
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // DIRSTUDIO-1077: Check for log output in console to verify logging configuration still works
        String consoleText = consoleViewBot.getConsoleText();
        assertThat( consoleText, containsString( "You didn't change the admin password" ) );

        // Verifying the connections count is 0
        assertEquals( 0, getBrowserConnectionsCount() );

        // Creating a connection associated with the server
        ConnectionFromServerDialogBot connectionFromServerDialogBot = serversViewBot.createConnectionFromServer();
        assertTrue( connectionFromServerDialogBot.isVisible() );
        connectionFromServerDialogBot.clickOkButton();

        // Verifying the connections count is now 1
        assertEquals( 1, getBrowserConnectionsCount() );

        // Opening the connection
        connectionsViewBot.selectConnection( serverName );
        connectionsViewBot.openSelectedConnection();

        // Getting the associated connection object
        Connection connection = getBrowserConnection();

        // Checking if the connection is open
        assertTrue( connection.getConnectionWrapper().isConnected() );

        // Closing the connection
        connectionsViewBot.selectConnection( serverName );
        connectionsViewBot.closeSelectedConnections();

        // Checking if the connection is closed
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


    private void createServer( String serverName )
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
        wizardBot.selectApacheDS200();
        wizardBot.typeServerName( serverName );

        // Verifying the wizard can now be finished
        assertTrue( wizardBot.isFinishButtonEnabled() );

        // Closing wizard
        wizardBot.clickFinishButton();
        serversViewBot.waitForServer( serverName );

        // Verifying the servers count is now 1
        assertEquals( 1, getCoreServersCount() );
        assertEquals( 1, serversViewBot.getServersCount() );
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
        wizardBot.selectApacheDS200();
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
        wizardBot.selectApacheDS200();
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


    private void setAvailablePorts( String serverName )
    {
        ApacheDSConfigurationEditorBot editorBot = serversViewBot.openConfigurationEditor( serverName );

        int ldapPort = AvailablePortFinder.getNextAvailable( 1024 );
        editorBot.setLdapPort( ldapPort );

        int ldapsPort = AvailablePortFinder.getNextAvailable( ldapPort + 1 );
        editorBot.setLdapsPort( ldapsPort );

        editorBot.save();
        editorBot.close();
    }


    /**
     * Gets the servers count found in the core of the plugin.
     *
     * @return
     *      the servers count found in the core of the plugin
     */
    private int getCoreServersCount()
    {
        LdapServersManager serversHandler = LdapServersManager.getDefault();
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
    private int getBrowserConnectionsCount()
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
    private Connection getBrowserConnection()
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
     * Test for DIRSTUDIO-1080: edit the server configuration via remote connection.
     */
    @Test
    public void editRemoteConfig()
    {
        String serverName = "EditRemoteConfig";
        createServer( serverName );
        setAvailablePorts( serverName );

        // Start the server
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // Create a connection associated with the server
        ConnectionFromServerDialogBot connectionFromServerDialogBot = serversViewBot.createConnectionFromServer();
        connectionFromServerDialogBot.clickOkButton();

        // Open the connection
        connectionsViewBot.selectConnection( serverName );
        connectionsViewBot.openSelectedConnection();

        // Open the config editor and load remote config
        ApacheDSConfigurationEditorBot remoteEditorBot = connectionsViewBot.openApacheDSConfiguration();

        // Remember old ports
        int oldLdapPort = remoteEditorBot.getLdapPort();
        int oldLdapsPort = remoteEditorBot.getLdapsPort();

        // Set new ports
        int newLdapPort = AvailablePortFinder.getNextAvailable( 1024 );
        remoteEditorBot.setLdapPort( newLdapPort );
        int newLdapsPort = AvailablePortFinder.getNextAvailable( newLdapPort + 1 );
        remoteEditorBot.setLdapsPort( newLdapsPort );

        // Save the config editor
        remoteEditorBot.save();
        remoteEditorBot.close();

        // Verify new port settings went over the network
        ModificationLogsViewBot modificationLogsViewBot = studioBot.getModificationLogsViewBot();
        modificationLogsViewBot.waitForText( "add: ads-systemPort" );
        String modificationLogsText = modificationLogsViewBot.getModificationLogsText();
        assertThat( modificationLogsText,
            containsString( "delete: ads-systemPort\nads-systemPort: " + oldLdapPort + "\n" ) );
        assertThat( modificationLogsText,
            containsString( "add: ads-systemPort\nads-systemPort: " + newLdapPort + "\n" ) );
        assertThat( modificationLogsText,
            containsString( "delete: ads-systemPort\nads-systemPort: " + oldLdapsPort + "\n" ) );
        assertThat( modificationLogsText,
            containsString( "add: ads-systemPort\nads-systemPort: " + newLdapsPort + "\n" ) );

        // Verify new port settings are visible in local config editor
        ApacheDSConfigurationEditorBot localEditorBot = serversViewBot.openConfigurationEditor( serverName );
        assertEquals( newLdapPort, localEditorBot.getLdapPort() );
        assertEquals( newLdapsPort, localEditorBot.getLdapsPort() );
        localEditorBot.close();

        // Close the connection
        connectionsViewBot.selectConnection( serverName );
        connectionsViewBot.closeSelectedConnections();

        // Delete the connection
        connectionsViewBot.deleteTestConnections();

        // Stopping the server
        serversViewBot.stopServer( serverName );
        serversViewBot.waitForServerStop( serverName );

        // Deleting the server
        DeleteDialogBot deleteDialogBot = serversViewBot.openDeleteServerDialog();
        deleteDialogBot.clickOkButton();
    }

}
