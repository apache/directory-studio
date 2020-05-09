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
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests against OpenLDAP.
 * 
 * Expects a running OpenLDAP server with below default connection parameters
 * which can be configured via environment variables.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
public class OpenLdapTest
{
    static String getOrDefault( String key, String defaultValue )
    {
        return System.getenv().getOrDefault( key, defaultValue );
    }

    private static final String OPENLDAP_HOST = getOrDefault( "OPENLDAP_HOST", LOCALHOST );
    private static final int OPENLDAP_PORT = Integer.parseInt( getOrDefault( "OPENLDAP_PORT", "20389" ) );
    private static final String OPENLDAP_ADMIN_DN = getOrDefault( "OPENLDAP_ADMIN_DN", "cn=admin,dc=example,dc=org" );
    private static final String OPENLDAP_ADMIN_PASSWORD = getOrDefault( "OPENLDAP_ADMIN_PASSWORD", "admin" );

    @BeforeClass
    public static void skipOpenLdapTestIfNotRunning() throws Exception
    {

        try ( LdapNetworkConnection connection = new LdapNetworkConnection( OPENLDAP_HOST, OPENLDAP_PORT ) )
        {
            connection.connect();
            connection.bind( OPENLDAP_ADMIN_DN, OPENLDAP_ADMIN_PASSWORD );
        }
        catch ( InvalidConnectionException e )
        {
            Assume.assumeNoException(
                "Skipping tests as connection to OpenLDAP server failed: " + OPENLDAP_HOST + ":" + OPENLDAP_PORT, e );
        }
        catch ( LdapAuthenticationException e )
        {
            Assume.assumeNoException(
                "Skipping tests as bind to OpenLDAP server failed: " + OPENLDAP_HOST + ":" + OPENLDAP_PORT, e );
        }
    }

    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;

    private Connection connection;

    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connection = connectionsViewBot.createTestConnection( "OpenLdapTest", OPENLDAP_HOST, OPENLDAP_PORT,
            OPENLDAP_ADMIN_DN, OPENLDAP_ADMIN_PASSWORD );
        browserViewBot = studioBot.getBrowserView();

        try ( LdapNetworkConnection connection = new LdapNetworkConnection( OPENLDAP_HOST, OPENLDAP_PORT );
            LdifReader ldifReader = new LdifReader( OpenLdapTest.class.getResourceAsStream( "OpenLdapTest.ldif" ) ) )
        {
            connection.bind( OPENLDAP_ADMIN_DN, OPENLDAP_ADMIN_PASSWORD );
            for ( LdifEntry entry : ldifReader )
            {
                connection.add( entry.getEntry() );
            }
        }
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();

        try ( LdapNetworkConnection connection = new LdapNetworkConnection( OPENLDAP_HOST, OPENLDAP_PORT );
            LdifReader ldifReader = new LdifReader( OpenLdapTest.class.getResourceAsStream( "OpenLdapTest.ldif" ) ) )
        {
            connection.bind( OPENLDAP_ADMIN_DN, OPENLDAP_ADMIN_PASSWORD );
            List<LdifEntry> ldifEntries = StreamSupport.stream( ldifReader.spliterator(), false )
                .collect( Collectors.toList() );
            Collections.reverse( ldifEntries );
            for ( LdifEntry entry : ldifEntries )
            {
                connection.delete( entry.getDn() );
            }
        }
    }


    @Test
    public void testBrowseWithPagingWithScrollMode()
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users" );

        // enable Simple Paged Results control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH, true );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE, 3 );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE, true );

        // 1st page
        browserViewBot.expandEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users" );
        assertFalse(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Top Page ---" ) );
        assertTrue(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Next Page ---" ) );

        // next page
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Next Page ---" );
        assertTrue(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Top Page ---" ) );
        assertTrue(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Next Page ---" ) );

        // last page
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Next Page ---" );
        assertTrue(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Top Page ---" ) );
        assertFalse(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Next Page ---" ) );

        // back to top
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Top Page ---" );
        assertFalse(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Top Page ---" ) );
        assertTrue(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Next Page ---" ) );
    }


    @Test
    public void testBrowseWithPagingWithoutScrollMode()
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users" );

        // enable Simple Paged Results control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH, true );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE, 3 );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE, false );

        browserViewBot.expandEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users" );
        assertFalse(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Top Page ---" ) );
        assertFalse(
            browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "--- Next Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users (8)" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "uid=user.1" ) );
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "uid=user.8" ) );
    }


    @Test
    public void testSearchWithPagingWithScrollMode() throws Exception
    {
        String searchName = "Paged search with scroll mode";
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users" );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=*)" );
        dialogBot.setReturningAttributes( "objectClass,ou,cn,uid" );
        dialogBot.setControlPagedSearch( true, 3, true );
        dialogBot.clickSearchButton();

        // 1st page
        browserViewBot.expandEntry( "Searches", searchName );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );

        // next page
        browserViewBot.selectEntry( "Searches", searchName, "--- Next Page ---" );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );

        // last page
        browserViewBot.selectEntry( "Searches", searchName, "--- Next Page ---" );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );

        // back to top
        browserViewBot.selectEntry( "Searches", searchName, "--- Top Page ---" );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );
    }


    @Test
    public void testSearchWithPagingWithoutScrollMode() throws Exception
    {
        String searchName = "Paged search without scroll mode";
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users" );
        SearchDialogBot dialogBot = browserViewBot.openSearchDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.setSearchName( searchName );
        dialogBot.setFilter( "(objectClass=*)" );
        dialogBot.setReturningAttributes( "objectClass,ou,cn,uid" );
        dialogBot.setControlPagedSearch( true, 3, false );
        dialogBot.clickSearchButton();

        browserViewBot.expandEntry( "Searches", searchName );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Top Page ---" ) );
        assertFalse( browserViewBot.existsEntry( "Searches", searchName, "--- Next Page ---" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName + " (9+)" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "uid=user.1" ) );
        assertTrue( browserViewBot.existsEntry( "Searches", searchName, "uid=user.8" ) );
    }


    @Test
    public void testCheckAuthenticationButtonNotOK()
    {
        // enter connection parameter
        NewConnectionWizardBot wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( "OpenLDAP connection test not ok" );
        wizardBot.typeHost( OPENLDAP_HOST );
        wizardBot.typePort( OPENLDAP_PORT );
        wizardBot.clickNextButton();

        // enter incorrect authentication parameter
        wizardBot.typeUser( OPENLDAP_ADMIN_DN );
        wizardBot.typePassword( "wrongpassword" );

        // click "Check Network Parameter" button
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNotNull( "Expected Error", result );
        assertThat( result, containsString( "[LDAP result code 49 - invalidCredentials]" ) );

        wizardBot.clickCancelButton();
    }

}
