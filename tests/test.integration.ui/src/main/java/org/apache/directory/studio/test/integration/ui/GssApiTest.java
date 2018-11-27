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
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.directory.api.util.FileUtils;
import org.apache.directory.api.util.IOUtils;
import org.apache.directory.studio.test.integration.ui.bots.ApacheDSConfigurationEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ApacheDSServersViewBot;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ImportWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.NewApacheDSServerWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;


/**
 * Tests secure connection handling.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
public class GssApiTest
{
    private static final String serverName = "GssApiTest";

    @Rule
    public TestName name = new TestName();

    private static int ldapPort;
    private static int kdcPort;

    private static StudioBot studioBot;
    private static ApacheDSServersViewBot serversViewBot;
    private static ConnectionsViewBot connectionsViewBot;


    @BeforeClass
    public static void setUpClass() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        serversViewBot = studioBot.getApacheDSServersViewBot();
        connectionsViewBot = studioBot.getConnectionView();

        // ErrorDialog.AUTOMATED_MODE = false;
    }


    @Before
    public void setUp() throws Exception
    {
        studioBot.resetLdapPerspective();
    }


    @BeforeClass
    public static void skipGssApiTestIfNoDefaultRealmIsConfigured()
    {
        try
        {
            /*
             * When creating a KerberosPrincipial without realm the default realm is looked up.
             * If no default realm is defined (e.g. as not /etc/krb5.conf exists) an exception is throws.
             * The test is skipped in that case as it won't succeed anyway. 
             */
            new KerberosPrincipal( "hnelson" );
        }
        catch ( IllegalArgumentException e )
        {
            Assume.assumeNoException( "Skipping tests as no default realm (/etc/krb5.conf) is configured", e );
        }
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();

        // stop ApacheDS
        serversViewBot.stopServer( serverName );
        serversViewBot.waitForServerStop( serverName );

        // delete ApacheDS
        DeleteDialogBot deleteDialogBot = serversViewBot.openDeleteServerDialog();
        deleteDialogBot.clickOkButton();
    }


    private String getConnectionName()
    {
        return "GssApiTest." + name.getMethodName();
    }


    @Test
    public void testGssApiObtainTgtAndUseManualConfigurationAndObtainServiceTicket() throws Exception
    {
        // create the server
        createServer( serverName );

        // configure ApacheDS and KDC server
        configureApacheDS( serverName );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // import KDC data
        connectionsViewBot.createTestConnection( "GssApiTest", ldapPort );
        importData();

        // connect with GSSAPI authentication
        NewConnectionWizardBot wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
        wizardBot.clickNextButton();
        wizardBot.selectGssApiAuthentication();
        wizardBot.selectObtainTgtFromKdc();
        wizardBot.typeUser( "hnelson" );
        wizardBot.typePassword( "secret" );
        wizardBot.selectUseManualConfiguration();
        wizardBot.typeKerberosRealm( "EXAMPLE.COM" );
        wizardBot.typeKdcHost( LOCALHOST );
        wizardBot.typeKdcPort( kdcPort );

        // check the connection
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK", result );

        wizardBot.clickCancelButton();
    }


    @Test
    public void testGssApiUseNativeTgtAndNativeConfigurationAndObtainServiceTicket() throws Exception
    {
        // create the server
        createServer( serverName );

        // configure ApacheDS and KDC server
        configureApacheDS( serverName );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // import KDC data
        connectionsViewBot.createTestConnection( "GssApiTest", ldapPort );
        importData();

        // obtain native TGT
        String[] cmd =
            { "/bin/sh", "-c", "echo secret | /usr/bin/kinit hnelson" };
        Process process = Runtime.getRuntime().exec( cmd );
        int exitCode = process.waitFor();
        assertEquals( 0, exitCode );

        // connect with GSSAPI authentication
        NewConnectionWizardBot wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
        wizardBot.clickNextButton();
        wizardBot.selectGssApiAuthentication();
        wizardBot.selectUseNativeTgt();
        wizardBot.selectUseNativeSystemConfiguration();

        // check the connection
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK", result );

        wizardBot.clickCancelButton();
    }


    private static void createServer( String serverName )
    {
        // Showing view
        serversViewBot.show();

        // Opening wizard
        NewApacheDSServerWizardBot wizardBot = serversViewBot.openNewServerWizard();

        // Filling fields of the wizard
        wizardBot.selectApacheDS200();
        wizardBot.typeServerName( serverName );

        // Closing wizard
        wizardBot.clickFinishButton();
        serversViewBot.waitForServer( serverName );
    }


    private static void configureApacheDS( String serverName ) throws Exception
    {
        ApacheDSConfigurationEditorBot editorBot = serversViewBot.openConfigurationEditor( serverName );

        editorBot.enableKerberosServer();

        editorBot.setAvailablePorts();
        editorBot.setKerberosPort( 60088 );
        ldapPort = editorBot.getLdapPort();
        kdcPort = editorBot.getKerberosPort();

        editorBot.setKdcRealm( "EXAMPLE.COM" );
        editorBot.setKdcSearchBase( "dc=security,dc=example,dc=com" );

        editorBot.setSaslHost( Constants.LOCALHOST );
        editorBot.setSaslPrincipal( "ldap/" + Constants.LOCALHOST + "@EXAMPLE.COM" );
        editorBot.setSaslSearchBase( "dc=security,dc=example,dc=com" );

        editorBot.save();
        editorBot.close();
    }


    private void importData() throws IOException
    {
        URL url = Platform.getInstanceLocation().getURL();
        String destFile = url.getFile() + "GssApiTest_" + System.currentTimeMillis() + ".ldif";
        InputStream is = getClass().getResourceAsStream( "GssApiTest.ldif" );
        String ldifContent = IOUtils.toString( is, StandardCharsets.UTF_8 );
        ldifContent = ldifContent.replace( "HOSTNAME", Constants.LOCALHOST );
        FileUtils.writeStringToFile( new File( destFile ), ldifContent, StandardCharsets.UTF_8, false );

        BrowserViewBot browserViewBot = studioBot.getBrowserView();
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=com" );
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( destFile );
        importWizardBot.clickFinishButton();
        browserViewBot.waitForEntry( "DIT", "Root DSE", "dc=example,dc=com", "dc=security" );
    }

}
