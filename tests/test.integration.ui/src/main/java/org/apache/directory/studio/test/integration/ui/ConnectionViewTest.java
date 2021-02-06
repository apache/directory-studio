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


import static org.apache.directory.studio.test.integration.ui.Constants.LOCALHOST;
import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ExportConnectionsWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.ImportConnectionsWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionFolderDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.eclipse.core.runtime.Platform;
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
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
public class ConnectionViewTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;

    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();
    }


    @Test
    public void testCreateAndDeleteConnections()
    {
        String connectionName = "Test connection 1";
        createConnection( connectionName );

        // ensure connection is visible in Connections view
        assertEquals( 1, connectionsViewBot.getCount() );

        // delete connection
        connectionsViewBot.select( connectionName );
        connectionsViewBot.openDeleteConnectionDialog().clickOkButton();

        // ensure connection is no longer visible in Connections view
        assertEquals( 0, connectionsViewBot.getCount() );
    }


    @Test
    public void testCreateAndDeleteConnectionFolders()
    {
        String folderName = "Connection folder 1";
        String subFolder1Name = "Connection folder 2";
        String subFolder2Name = "Connection folder 3";

        // create one folder
        createConnectionFolder( folderName );

        // ensure folder is visible
        assertEquals( 1, connectionsViewBot.getCount() );

        // create two sub folders
        connectionsViewBot.select( folderName );
        createConnectionFolder( subFolder1Name );
        connectionsViewBot.select( folderName );
        createConnectionFolder( subFolder2Name );

        // ensure connection folders are visible
        assertEquals( 3, connectionsViewBot.getCount() );

        // delete one sub folder
        connectionsViewBot.select( folderName, subFolder1Name );
        connectionsViewBot.openDeleteConnectionFolderDialog().clickOkButton();

        // ensure connection folders are visible
        assertEquals( 2, connectionsViewBot.getCount() );

        // delete connection folder
        connectionsViewBot.select( folderName );
        connectionsViewBot.openDeleteConnectionFolderDialog().clickOkButton();

        // ensure folders no longer exist
        assertEquals( 0, connectionsViewBot.getCount() );
    }


    @Test
    public void testExportImportConnections() throws Exception
    {
        String connection1Name = "Test connection 1";
        String connection2Name = "Test connection 2";
        String connection3Name = "Test connection 3";
        String folderName = "Connection folder 1";
        String subFolder1Name = "Connection folder 2";
        String subFolder2Name = "Connection folder 3";

        // create connections and folders
        createConnection( connection1Name );
        createConnectionFolder( folderName );
        connectionsViewBot.select( folderName );
        createConnection( connection2Name );
        connectionsViewBot.select( folderName );
        createConnectionFolder( subFolder1Name );
        connectionsViewBot.select( folderName, subFolder1Name );
        createConnection( connection3Name );
        connectionsViewBot.select( folderName );
        createConnectionFolder( subFolder2Name );

        // verify connections and folders exist
        assertEquals( 6, connectionsViewBot.getCount() );
        connectionsViewBot.select( folderName );
        connectionsViewBot.select( folderName, subFolder1Name );
        connectionsViewBot.select( folderName, subFolder2Name );
        connectionsViewBot.select( connection1Name );
        connectionsViewBot.select( folderName, connection2Name );
        connectionsViewBot.select( folderName, subFolder1Name, connection3Name );

        // export connections and folders
        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportConnections.zip";
        ExportConnectionsWizardBot exportConnectionsWizardBot = connectionsViewBot.openExportConnectionsWizard();
        exportConnectionsWizardBot.typeFile( file );
        exportConnectionsWizardBot.clickFinishButton();

        // delete connections and folders
        connectionsViewBot.deleteTestConnections();
        assertEquals( 0, connectionsViewBot.getCount() );

        // import connections and folders
        ImportConnectionsWizardBot importConnectionsWizardBot = connectionsViewBot.openImportConnectionsWizard();
        importConnectionsWizardBot.typeFile( file );
        importConnectionsWizardBot.clickFinishButton();

        // verify connections and folders exist
        assertEquals( 6, connectionsViewBot.getCount() );
        connectionsViewBot.select( folderName );
        connectionsViewBot.select( folderName, subFolder1Name );
        connectionsViewBot.select( folderName, subFolder2Name );
        connectionsViewBot.select( connection1Name );
        connectionsViewBot.select( folderName, connection2Name );
        connectionsViewBot.select( folderName, subFolder1Name, connection3Name );
    }


    private void createConnection( String connectionName )
    {
        NewConnectionWizardBot wizardBot = connectionsViewBot.openNewConnectionWizard();

        // enter connection parameter
        wizardBot.typeConnectionName( connectionName );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapServer.getPort() );

        // jump to auth page
        wizardBot.clickNextButton();

        // enter authentication parameters
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // finish dialog
        wizardBot.clickFinishButton( true );
        // connectionsViewBot.waitForConnection( connectionName );
    }


    private void createConnectionFolder( String folderName )
    {
        NewConnectionFolderDialogBot newConnectionFolderDialog = connectionsViewBot.openNewConnectionFolderDialog();
        newConnectionFolderDialog.setConnectionFoldername( folderName );
        newConnectionFolderDialog.clickOkButton();
    }

}
