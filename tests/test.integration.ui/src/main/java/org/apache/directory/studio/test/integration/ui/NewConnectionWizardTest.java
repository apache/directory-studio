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
import static org.apache.directory.studio.test.integration.ui.Constants.LOCALHOST_ADDRESS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;


/**
 * Tests the new connection wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
public class NewConnectionWizardTest extends AbstractLdapTestUnit
{
    @Rule
    public TestName name = new TestName();

    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private NewConnectionWizardBot wizardBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();

        // open the new connection wizard
        wizardBot = connectionsViewBot.openNewConnectionWizard();

        //ErrorDialog.AUTOMATED_MODE = false;
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        studioBot = null;
        connectionsViewBot = null;
        wizardBot = null;
        Assertions.genericTearDownAssertions();
    }


    private String getConnectionName()
    {
        return "NewConnectionWizardTest." + name.getMethodName();
    }


    /**
     * Tests enabled and disabled widgets, depending on the provided input.
     */
    @Test
    public void testEnabledDisabledWidgets()
    {
        assertTrue( wizardBot.isVisible() );

        // ensure "Next >" and "Finish" buttons are disabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter connection parameter
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapServer.getPort() );
        // ensure "Next >" button is enabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // clear host
        wizardBot.typeHost( "" );
        // ensure "Next >" is disabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter host again
        wizardBot.typeHost( LOCALHOST );
        // ensure "Next >" button is enabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        wizardBot.clickNextButton();

        // check default settings
        assertTrue( wizardBot.isSimpleAuthenticationSelected() );
        assertTrue( wizardBot.isUserEnabled() );
        assertTrue( wizardBot.isPasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );
        assertTrue( wizardBot.isSavePasswordSelected() );
        // ensure "<Back" is enabled, "Next >" and "Finish" is disabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter authentication parameters
        wizardBot.selectSimpleAuthentication();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );
        // ensure "<Back" is enabled, "Next >" and "Finish" is enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // clear user
        wizardBot.typeUser( "" );
        // ensure "<Back" is enabled, "Next >" and "Finish" is disabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter user again
        wizardBot.typeUser( "uid=admin,ou=system" );
        // ensure "<Back" is enabled, "Next >" and "Finish" is enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // deselect password save
        wizardBot.deselectSavePassword();
        // ensure password field is disabled
        assertFalse( wizardBot.isPasswordEnabled() );
        // ensure "<Back" is enabled, "Next >" and "Finish" is enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // select password save
        wizardBot.selectSavePassword();
        // ensure password field is enabled but empty
        assertTrue( wizardBot.isPasswordEnabled() );
        // ensure "<Back" is enabled, "Next >" and "Finish" is disabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter authentication parameters again
        wizardBot.selectSimpleAuthentication();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );
        // ensure "<Back" is enabled, "Next >" and "Finish" is enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // select no authentication
        wizardBot.selectNoAuthentication();
        // ensure authentication parameter input fields are disabled
        assertTrue( wizardBot.isNoAuthenticationSelected() );
        assertFalse( wizardBot.isUserEnabled() );
        assertFalse( wizardBot.isPasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );
        assertFalse( wizardBot.isSavePasswordEnabled() );

        // select DIGEST-MD5
        wizardBot.selectDigestMD5Authentication();
        // ensure authentication parameter input fields are enabled, including SASL Realm field
        assertTrue( wizardBot.isDigestMD5AuthenticationSelected() );
        assertTrue( wizardBot.isUserEnabled() );
        assertTrue( wizardBot.isPasswordEnabled() );
        assertTrue( wizardBot.isSavePasswordEnabled() );
        assertTrue( wizardBot.isRealmEnabled() );

        // select CRAM-MD5
        wizardBot.selectCramMD5Authentication();
        // ensure authentication parameter input fields are enabled, excluding SASL Realm field
        assertTrue( wizardBot.isCramMD5AuthenticationSelected() );
        assertTrue( wizardBot.isUserEnabled() );
        assertTrue( wizardBot.isPasswordEnabled() );
        assertTrue( wizardBot.isSavePasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );

        // select GSSAPI (Kerberos)
        wizardBot.selectGssApiAuthentication();
        // ensure authentication parameter input fields are disabled by default
        assertTrue( wizardBot.isGssApiAuthenticationSelected() );
        assertFalse( wizardBot.isUserEnabled() );
        assertFalse( wizardBot.isPasswordEnabled() );
        assertFalse( wizardBot.isSavePasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );
        // by default "Use native TGT" is selected
        assertTrue( wizardBot.isUseNativeTgtSelected() );
        assertFalse( wizardBot.isObtainTgtFromKdcSelected() );
        assertTrue( wizardBot.isUseNativeSystemConfigurationSelected() );
        assertFalse( wizardBot.isUseConfigurationFileSelected() );
        assertFalse( wizardBot.isUseManualConfigurationSelected() );
        assertFalse( wizardBot.isKerberosRealmEnabled() );
        assertFalse( wizardBot.isKdcHostEnabled() );
        assertFalse( wizardBot.isKdcPortEnabled() );

        // select GSSAPI (Kerberos) and "Obtain TGT from KDC"
        wizardBot.selectObtainTgtFromKdc();
        // ensure authentication parameter input fields are enabled
        assertTrue( wizardBot.isGssApiAuthenticationSelected() );
        assertTrue( wizardBot.isUserEnabled() );
        assertTrue( wizardBot.isPasswordEnabled() );
        assertTrue( wizardBot.isSavePasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );
        assertFalse( wizardBot.isUseNativeTgtSelected() );

        // select GSSAPI (Kerberos) and "Use configuration file"
        wizardBot.selectUseConfigurationFile();
        assertFalse( wizardBot.isUseNativeSystemConfigurationSelected() );
        assertTrue( wizardBot.isUseConfigurationFileSelected() );
        assertFalse( wizardBot.isUseManualConfigurationSelected() );
        assertFalse( wizardBot.isKerberosRealmEnabled() );
        assertFalse( wizardBot.isKdcHostEnabled() );
        assertFalse( wizardBot.isKdcPortEnabled() );

        // select GSSAPI (Kerberos) and "Use manual configuration"
        wizardBot.selectUseManualConfiguration();
        assertFalse( wizardBot.isUseNativeSystemConfigurationSelected() );
        assertFalse( wizardBot.isUseConfigurationFileSelected() );
        assertTrue( wizardBot.isUseManualConfigurationSelected() );
        assertTrue( wizardBot.isKerberosRealmEnabled() );
        assertTrue( wizardBot.isKdcHostEnabled() );
        assertTrue( wizardBot.isKdcPortEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );

        // select GSSAPI (Kerberos) and "Use native system configuration" again
        wizardBot.selectUseNativeSystemConfiguration();
        assertTrue( wizardBot.isUseNativeSystemConfigurationSelected() );
        assertFalse( wizardBot.isUseConfigurationFileSelected() );
        assertFalse( wizardBot.isUseManualConfigurationSelected() );
        assertFalse( wizardBot.isKerberosRealmEnabled() );
        assertFalse( wizardBot.isKdcHostEnabled() );
        assertFalse( wizardBot.isKdcPortEnabled() );

        wizardBot.clickNextButton();

        // check default settings
        assertTrue( wizardBot.isGetBaseDnsFromRootDseSelected() );
        assertFalse( wizardBot.isBaseDnEnabled() );
        // ensure "<Back" and "Finish" is enabled, "Next >" enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // deselect get base DNs from Root DSE
        wizardBot.deselectGetBaseDnsFromRootDse();
        assertFalse( wizardBot.isGetBaseDnsFromRootDseSelected() );
        assertTrue( wizardBot.isBaseDnEnabled() );

        // select get base DNs from Root DSE
        wizardBot.selectGetBaseDnsFromRootDse();
        assertTrue( wizardBot.isGetBaseDnsFromRootDseSelected() );
        assertFalse( wizardBot.isBaseDnEnabled() );

        wizardBot.clickCancelButton();
    }


    /**
     * Creates a new connection using the new connection wizard.
     */
    @Test
    public void testCreateConnection()
    {
        // enter connection parameter
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapServer.getPort() );

        // jump to auth page
        wizardBot.clickNextButton();

        // enter authentication parameters
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // finish dialog
        wizardBot.clickFinishButton( true );
        connectionsViewBot.waitForConnection( getConnectionName() );

        // ensure connection was created
        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        assertNotNull( connectionManager.getConnections() );
        assertEquals( 1, connectionManager.getConnections().length );
        Connection connection = connectionManager.getConnections()[0];
        assertEquals( getConnectionName(), connection.getName() );
        assertEquals( LOCALHOST, connection.getHost() );
        assertEquals( ldapServer.getPort(), connection.getPort() );
        assertEquals( AuthenticationMethod.SIMPLE, connection.getAuthMethod() );
        assertEquals( "uid=admin,ou=system", connection.getBindPrincipal() );
        assertEquals( "secret", connection.getBindPassword() );

        // ensure connection is visible in Connections view
        assertEquals( 1, connectionsViewBot.getConnectionCount() );

        // close connection
        connectionsViewBot.closeSelectedConnections();
    }


    /**
     * Tests the "Check Network Parameter" button.
     */
    @Test
    public void testCheckNetworkParameterButtonOK() throws UnknownHostException
    {
        // enter connection parameter with host name
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapServer.getPort() );

        // click "Check Network Parameter" button
        String result1 = wizardBot.clickCheckNetworkParameterButton();
        assertNull( "Expected OK", result1 );

        // enter connection parameter with IPv4 address
        wizardBot.typeHost( LOCALHOST_ADDRESS );
        wizardBot.typePort( ldapServer.getPort() );

        // click "Check Network Parameter" button
        String result2 = wizardBot.clickCheckNetworkParameterButton();
        assertNull( "Expected OK", result2 );

        //
        // Don't know why this doesn't work with SWTBot.
        // When testing manually it works.
        //
        // // enter connection parameter with IPv6 address
        // wizardBot.typeHost( "[::1]" );
        // wizardBot.typePort( ldapService.getPort() );
        //
        // // click "Check Network Parameter" button
        // String result3 = wizardBot.clickCheckNetworkParameterButton();
        // assertNull( "Expected OK", result3 );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests the "Check Network Parameter" button.
     */
    @Test
    public void testCheckNetworkParameterButtonNotOK()
    {
        // enter connection parameter with invalid port
        wizardBot.typeConnectionName( getConnectionName() );
        int port = ldapServer.getPort() + 1;
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( port );

        // click "Check Network Parameter" button and get the result
        String result1 = wizardBot.clickCheckNetworkParameterButton();
        assertNotNull( "Expected Error", result1 );
        // LDAP API: Connection refused
        // JNDI: The connection failed
        assertThat( result1,
            anyOf( containsString( "Connection refused" ), containsString( "The connection failed" ) ) );

        // enter connection parameter with invalid host name
        String hostname = "qwertzuiop.asdfghjkl.yxcvbnm";
        wizardBot.typeHost( hostname );
        wizardBot.typePort( ldapServer.getPort() );

        // click "Check Network Parameter" button and get the result
        String result2 = wizardBot.clickCheckNetworkParameterButton();
        assertNotNull( "Expected Error", result2 );
        // LDAP API: could not be resolved
        // JNDI: The connection failed
        assertThat( result2,
            anyOf( containsString( "could not be resolved" ), containsString( "The connection failed" ) ) );
        assertThat( "Unknown host name must occur in error message", result2, containsString( hostname ) );

        // disabled this test because it does not work properly
        // as it depends from the network connection settings.
        //        // enter connection parameter with non-routed IP address
        //        String ipAddress = "10.11.12.13";
        //        wizardBot.typeHost( ipAddress );
        //        wizardBot.typePort( ldapServer.getPort() );
        //
        //        // click "Check Network Parameter" button and get the result
        //        String result3 = wizardBot.clickCheckNetworkParameterButton();
        //        assertNotNull( "Expected Error", result3 );
        //        assertTrue( "'No route to host' or 'Network is unreachable' message must occur in error message", //
        //            result3.contains( "No route to host" ) || result3.contains( "Network is unreachable" ) );
        //        assertTrue( "IP address must occur in error message", result3.contains( ipAddress ) );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests the "Check Network Parameter" button.
     */
    @Test
    public void testCheckAuthenticationButtonOK()
    {
        // enter connection parameter
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.clickNextButton();

        // enter correct authentication parameter
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // click "Check Network Parameter" button
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK", result );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests the "Check Authentication" button.
     */
    @Test
    public void testCheckAuthenticationButtonNotOK()
    {
        // enter connection parameter
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.clickNextButton();

        // enter incorrect authentication parameter
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret45" );

        // click "Check Network Parameter" button
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNotNull( "Expected Error", result );
        assertThat( result, containsString( "[LDAP result code 49 - invalidCredentials]" ) );

        wizardBot.clickCancelButton();
    }

}
