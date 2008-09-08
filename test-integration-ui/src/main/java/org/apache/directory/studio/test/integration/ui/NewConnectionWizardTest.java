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
import net.sf.swtbot.wait.DefaultCondition;
import net.sf.swtbot.widgets.SWTBotButton;
import net.sf.swtbot.widgets.SWTBotCombo;
import net.sf.swtbot.widgets.SWTBotMenu;
import net.sf.swtbot.widgets.SWTBotText;
import net.sf.swtbot.widgets.SWTBotTree;

import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.eclipse.jface.dialogs.ErrorDialog;


/**
 * Tests the new connection wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewConnectionWizardTest extends AbstractServerTest
{
    private SWTEclipseBot bot;


    protected void setUp() throws Exception
    {
        super.setUp();
        bot = new SWTEclipseBot();
        SWTBotUtils.openLdapPerspective( bot );
    }


    protected void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
        super.tearDown();
    }


    /**
     * Creates a new connection using the new connection wizard.
     * 
     * @throws Exception the exception
     */
    public void testCreateConnection() throws Exception
    {
        // Select "Connections" view, ensure no connections exists yet
        SWTBotTree connectionsTree = SWTBotUtils.getConnectionsTree( bot );
        assertEquals( 0, connectionsTree.rowCount() );

        // open "New Connection" wizard
        SWTBotMenu newConnectionMenu = connectionsTree.contextMenu( "New Connection..." );
        newConnectionMenu.click();

        // get buttons
        SWTBotButton backButton = bot.button( "< Back" );
        SWTBotButton nextButton = bot.button( "Next >" );
        SWTBotButton finishButton = bot.button( "Finish" );

        // ensure "Next >" and "Finish" buttons are disabled
        assertFalse( backButton.isEnabled() );
        assertFalse( nextButton.isEnabled() );
        assertFalse( finishButton.isEnabled() );

        // enter connection parameter
        SWTBotText connText = bot.textWithLabel( "Connection name:" );
        connText.setText( "NewConnectionWizardTest" );
        SWTBotCombo hostnameCombo = bot.comboBoxWithLabel( "Hostname:" );
        hostnameCombo.setText( "localhost" );
        SWTBotCombo portCombo = bot.comboBoxWithLabel( "Port:" );
        portCombo.setText( Integer.toString( ldapService.getIpPort() ) );

        // ensure "Next >" button is enabled, "Finish" button is disabled
        assertFalse( backButton.isEnabled() );
        assertTrue( nextButton.isEnabled() );
        assertFalse( finishButton.isEnabled() );

        // jump to auth page
        nextButton.click();

        // ensure "< Back" is enabled, "Next >" button is disabled, "Finish" button is disabled
        assertTrue( backButton.isEnabled() );
        assertFalse( nextButton.isEnabled() );
        assertFalse( finishButton.isEnabled() );

        // ensure "Simple Authentication" is the default
        SWTBotCombo authMethodCombo = bot.comboBoxWithLabel( "Authentication Method" );
        assertEquals( "Simple Authentication", authMethodCombo.selection() );

        // enter authentication parameters
        SWTBotCombo dnCombo = bot.comboBoxWithLabel( "Bind DN or user:" );
        dnCombo.setText( "uid=admin,ou=system" );
        SWTBotText passwordText = bot.textWithLabel( "Bind password:" );
        passwordText.setText( "secret" );

        // ensure "< Back" is enabled, "Next >" button is enabled, "Finish" button is enabled
        assertTrue( backButton.isEnabled() );
        assertTrue( nextButton.isEnabled() );
        assertTrue( finishButton.isEnabled() );

        // finish dialog
        finishButton.click();
        bot.sleep( 2000 );

        // ensure connection was created
        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        assertNotNull( connectionManager.getConnections() );
        assertEquals( 1, connectionManager.getConnections().length );
        Connection connection = connectionManager.getConnections()[0];
        assertEquals( "NewConnectionWizardTest", connection.getName() );
        assertEquals( "localhost", connection.getHost() );
        assertEquals( ldapService.getIpPort(), connection.getPort() );
        assertEquals( AuthenticationMethod.SIMPLE, connection.getAuthMethod() );
        assertEquals( "uid=admin,ou=system", connection.getBindPrincipal() );
        assertEquals( "secret", connection.getBindPassword() );

        // ensure connection is visible in Connections view
        assertEquals( 1, connectionsTree.rowCount() );

        // close connection
        connectionsTree.select( "NewConnectionWizardTest" );
        SWTBotMenu contextMenu = connectionsTree.contextMenu( "Close Connection" );
        contextMenu.click();
    }


    /**
     * Tests the "Check Network Parameter" button.
     * 
     * @throws Exception the exception
     */
    public void testCheckNetworkParameterButtonOK() throws Exception
    {
        // Select "Connections" view, ensure no connections exists yet
        SWTBotTree connectionsTree = SWTBotUtils.getConnectionsTree( bot );
        assertEquals( 0, connectionsTree.rowCount() );

        // open "New Connection" wizard
        SWTBotMenu newConnectionMenu = connectionsTree.contextMenu( "New Connection..." );
        newConnectionMenu.click();

        // enter connection parameter
        SWTBotText connText = bot.textWithLabel( "Connection name:" );
        connText.setText( "NewConnectionWizardTest" );
        SWTBotCombo hostnameCombo = bot.comboBoxWithLabel( "Hostname:" );
        hostnameCombo.setText( "localhost" );
        SWTBotCombo portCombo = bot.comboBoxWithLabel( "Port:" );
        portCombo.setText( Integer.toString( ldapService.getIpPort() ) );

        // click "Check Network Parameter" button
        SWTBotButton checkButton = bot.button( "Check Network Parameter" );
        checkButton.click();
        bot.sleep( 1000 );
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.activeShell().getText().equals( "Check Network Parameter" ) && bot.button( "OK" ) != null;
            }


            public String getFailureMessage()
            {
                return "Expected an dialog box 'Check Network Parameter' with an 'OK' button.";
            }
        } );

        bot.button( "OK" ).click();
        bot.button( "Cancel" ).click();
    }


    /**
     * Tests the "Check Network Parameter" button.
     * 
     * @throws Exception the exception
     */
    public void testCheckNetworkParameterButtonNOK() throws Exception
    {
        // we expect the error dialog here, so set flag to false
        boolean errorDialogAutomatedMode = ErrorDialog.AUTOMATED_MODE;
        ErrorDialog.AUTOMATED_MODE = false;
        
        // Select "Connections" view, ensure no connections exists yet
        SWTBotTree connectionsTree = SWTBotUtils.getConnectionsTree( bot );
        assertEquals( 0, connectionsTree.rowCount() );

        // open "New Connection" wizard
        SWTBotMenu newConnectionMenu = connectionsTree.contextMenu( "New Connection..." );
        newConnectionMenu.click();

        // enter connection parameter
        SWTBotText connText = bot.textWithLabel( "Connection name:" );
        connText.setText( "NewConnectionWizardTest" );
        SWTBotCombo hostnameCombo = bot.comboBoxWithLabel( "Hostname:" );
        hostnameCombo.setText( "localhost" );
        SWTBotCombo portCombo = bot.comboBoxWithLabel( "Port:" );
        portCombo.setText( Integer.toString( ldapService.getIpPort() + 1 ) );

        // click "Check Network Parameter" button
        SWTBotButton checkButton = bot.button( "Check Network Parameter" );
        checkButton.click();
        bot.sleep( 1000 );
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.activeShell().getText().equals( "Error" ) && bot.button( "OK" ) != null;
            }


            public String getFailureMessage()
            {
                return "Expected an dialog box 'Error' with an 'OK' button.";
            }
        } );
        
        bot.button( "OK" ).click();
        bot.button( "Cancel" ).click();
        
        // reset flag
        ErrorDialog.AUTOMATED_MODE = errorDialogAutomatedMode;
    }
}
