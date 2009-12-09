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
import org.apache.directory.studio.test.integration.ui.bots.ApacheDSServersViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewApacheDSServerWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
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


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        serversViewBot = studioBot.getApacheDSServersViewBot();
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
    public void serverCreationAndDeletion()
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
}
