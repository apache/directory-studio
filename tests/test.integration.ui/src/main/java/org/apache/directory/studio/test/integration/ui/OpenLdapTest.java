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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ModifyMode;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.CertificateEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EditAttributeWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.HexEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ModificationLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewAttributeWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.NewEntryWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.SchemaBrowserBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
@Ignore
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
    private static final String OPENLDAP_CONFIG_DN = getOrDefault( "OPENLDAP_CONFIG_DN", "cn=admin,cn=config" );
    private static final String OPENLDAP_CONFIG_PASSWORD = getOrDefault( "OPENLDAP_CONFIG_PASSWORD", "config" );

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
    private ModificationLogsViewBot modificationLogsViewBot;

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
        modificationLogsViewBot = studioBot.getModificationLogsViewBot();

        try ( LdapNetworkConnection connection = new LdapNetworkConnection( OPENLDAP_HOST, OPENLDAP_PORT );
            LdifReader ldifReader = new LdifReader( OpenLdapTest.class.getResourceAsStream( "OpenLdapTest.ldif" ) ) )
        {
            connection.bind( OPENLDAP_ADMIN_DN, OPENLDAP_ADMIN_PASSWORD );
            for ( LdifEntry entry : ldifReader )
            {
                connection.add( entry.getEntry() );
            }
        }

        try ( LdapNetworkConnection connection = new LdapNetworkConnection( OPENLDAP_HOST, OPENLDAP_PORT );
            LdifReader ldifReader = new LdifReader( OpenLdapTest.class.getResourceAsStream( "OpenLdapConfig.ldif" ) ) )
        {
            connection.bind( OPENLDAP_CONFIG_DN, OPENLDAP_CONFIG_PASSWORD );
            for ( LdifEntry entry : ldifReader )
            {
                for ( Modification modification : entry.getModifications() )
                {
                    connection.modify( entry.getDn(), modification );
                }
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




    /**
     * Test adding, editing and deleting of attributes without equality matching rule in the entry editor.
     */
    @Test
    public void testAddEditDeleteAttributeWithoutEqualityMatchingRule() throws Exception
    {
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( connection );
        browserConnection.setModifyModeNoEMR( ModifyMode.REPLACE );

        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "uid=user.1" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,dc=example,dc=org" );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: uid=user.1,ou=users,dc=example,dc=org", dn );
        assertEquals( 22, entryEditorBot.getAttributeValues().size() );
        assertEquals( "", modificationLogsViewBot.getModificationLogsText() );

        // add facsimileTelephoneNumber attribute
        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "facsimileTelephoneNumber" );
        wizardBot.clickFinishButton();
        entryEditorBot.typeValueAndFinish( "+1 234 567 890" );
        assertEquals( 23, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "facsimileTelephoneNumber: +1 234 567 890" ) );
        modificationLogsViewBot
            .waitForText( "replace: facsimileTelephoneNumber\nfacsimileTelephoneNumber: +1 234 567 890" );

        // edit value
        entryEditorBot.editValue( "facsimileTelephoneNumber", "+1 234 567 890" );
        entryEditorBot.typeValueAndFinish( "000000000000" );
        assertEquals( 23, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "facsimileTelephoneNumber: +1 234 567 890" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "facsimileTelephoneNumber: 000000000000" ) );
        modificationLogsViewBot
            .waitForText( "replace: facsimileTelephoneNumber\nfacsimileTelephoneNumber: 000000000000" );

        // delete 1st value/attribute
        entryEditorBot.deleteValue( "facsimileTelephoneNumber", "000000000000" );
        assertEquals( 22, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "facsimileTelephoneNumber: 000000000000" ) );
        modificationLogsViewBot
            .waitForText( "replace: facsimileTelephoneNumber\n-" );

        assertEquals( "Expected 3 modifications.", 3,
            StringUtils.countMatches( modificationLogsViewBot.getModificationLogsText(), "#!RESULT OK" ) );
    }


    @Test
    public void testNoPermissionToReadSchema() throws Exception
    {
        // Close connection and reset cached schema
        connectionsViewBot.closeSelectedConnections();
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( connection );
        browserConnection.setSchema( Schema.DEFAULT_SCHEMA );

        // Open connection as uid=user.1 which is not allowed to read cn=subschema
        connection.setBindPrincipal( "uid=user.1,ou=users,dc=example,dc=org" );
        connection.setBindPassword( "password" );
        ErrorDialogBot errorDialog = connectionsViewBot.openSelectedConnectionExpectingNoSchemaProvidedErrorDialog();
        assertThat( errorDialog.getErrorDetails(),
            containsString( "No schema information returned by server, using default schema." ) );
        errorDialog.clickOkButton();

        // Verify default schema is used
        SchemaBrowserBot schemaBrowser = connectionsViewBot.openSchemaBrowser();
        schemaBrowser.selectObjectClass( "DEFAULTSCHEMA" );
        String rawSchemaDefinition = schemaBrowser.getRawSchemaDefinition();
        assertNotNull( rawSchemaDefinition );
        assertTrue( rawSchemaDefinition.contains( "This is the Default Schema" ) );

        // Verify browser
        browserViewBot.selectEntry( "DIT", "Root DSE" );
    }


    /**
     * DIRSTUDIO-1267: Test creation of new entry with binary option and language tags.
     */
    @Test
    public void testCreateEntryWithBinaryOptionAndLanguageTags() throws Exception
    {
        String certFile = ResourceUtils.prepareInputFile( "rfc5280_cert1.cer" );
        String crlFile = ResourceUtils.prepareInputFile( "rfc5280_crl.crl" );

        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users" );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();
        wizardBot.addObjectClasses( "organizationalRole" );
        wizardBot.addObjectClasses( "certificationAuthority" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "cn" );
        wizardBot.setRdnValue( 1, "asdf" );
        wizardBot.clickNextButton();

        // by default the hex or certificate editor is opened, close it
        try
        {
            new HexEditorDialogBot().clickCancelButton();
        }
        catch ( WidgetNotFoundException e )
        {
        }
        try
        {
            new CertificateEditorDialogBot().clickCancelButton();
        }
        catch ( WidgetNotFoundException e )
        {
        }

        wizardBot.activate();
        NewAttributeWizardBot newAttributeWizardBot = wizardBot.openNewAttributeWizard();
        assertTrue( newAttributeWizardBot.isVisible() );
        newAttributeWizardBot.typeAttributeType( "description" );
        newAttributeWizardBot.clickNextButton();
        newAttributeWizardBot.setLanguageTag( "en", "us" );
        newAttributeWizardBot.clickFinishButton();
        wizardBot.cancelEditValue();
        wizardBot.activate();
        wizardBot.editValue( "description;lang-en-us", null );
        SWTUtils.sleep( 1000 );
        wizardBot.typeValueAndFinish( "English description" );

        wizardBot.activate();
        EditAttributeWizardBot editAttributeBot = wizardBot.editAttribute( "cACertificate", null );
        editAttributeBot.clickNextButton();
        editAttributeBot.selectBinaryOption();
        editAttributeBot.clickFinishButton();

        wizardBot.activate();
        wizardBot.editValue( "cACertificate;binary", null );
        CertificateEditorDialogBot certEditorBot = new CertificateEditorDialogBot();
        assertTrue( certEditorBot.isVisible() );
        certEditorBot.typeFile( certFile );
        certEditorBot.clickOkButton();

        wizardBot.activate();
        editAttributeBot = wizardBot.editAttribute( "certificateRevocationList", null );
        editAttributeBot.clickNextButton();
        editAttributeBot.selectBinaryOption();
        editAttributeBot.clickFinishButton();

        wizardBot.activate();
        wizardBot.editValue( "certificateRevocationList;binary", null );
        HexEditorDialogBot hexEditorBot = new HexEditorDialogBot();
        assertTrue( hexEditorBot.isVisible() );
        hexEditorBot.typeFile( crlFile );
        hexEditorBot.clickOkButton();

        wizardBot.activate();
        editAttributeBot = wizardBot.editAttribute( "authorityRevocationList", null );
        editAttributeBot.clickNextButton();
        editAttributeBot.selectBinaryOption();
        editAttributeBot.clickFinishButton();

        wizardBot.activate();
        wizardBot.editValue( "authorityRevocationList;binary", null );
        assertTrue( hexEditorBot.isVisible() );
        hexEditorBot.typeFile( crlFile );
        hexEditorBot.clickOkButton();

        wizardBot.activate();
        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "cn=asdf" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "cn=asdf" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=asdf,ou=users,dc=example,dc=org" );
        entryEditorBot.activate();

        modificationLogsViewBot.waitForText( "cACertificate;binary:: MIICPjCCAaeg" );
        assertTrue( entryEditorBot.getAttributeValues()
            .contains( "cACertificate;binary: X.509v3: CN=Example CA,DC=example,DC=com" ) );

        modificationLogsViewBot.waitForText( "certificateRevocationList;binary:: MIIBYDCBygIB" );
        assertTrue( entryEditorBot.getAttributeValues()
            .contains( "certificateRevocationList;binary: Binary Data (356 Bytes)" ) );

        modificationLogsViewBot.waitForText( "authorityRevocationList;binary:: MIIBYDCBygIB" );
        assertTrue( entryEditorBot.getAttributeValues()
            .contains( "authorityRevocationList;binary: Binary Data (356 Bytes)" ) );

        modificationLogsViewBot.waitForText( "description;lang-en-us: English description" );
        assertTrue( entryEditorBot.getAttributeValues()
            .contains( "description;lang-en-us: English description" ) );

        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "cn=asdf" );
        DeleteDialogBot deleteDialogBot = browserViewBot.openDeleteDialog();
        deleteDialogBot.clickOkButton();
    }


    /**
     * DIRSTUDIO-1267: Test adding, editing and deleting of attributes with binary option in the entry editor. 
     */
    @Test
    public void testAddEditDeleteAttributeWithBinaryOption() throws Exception
    {
        String cert2File = ResourceUtils.prepareInputFile( "rfc5280_cert2.cer" );
        String cert3File = ResourceUtils.prepareInputFile( "rfc5280_cert3.cer" );

        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "uid=user.1" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,dc=example,dc=org" );
        entryEditorBot.activate();

        // add userCertificate;binary
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "userCertificate" );
        wizardBot.clickNextButton();
        wizardBot.selectBinaryOption();
        CertificateEditorDialogBot certEditorBot = wizardBot.clickFinishButtonExpectingCertificateEditor();
        assertTrue( certEditorBot.isVisible() );
        certEditorBot.typeFile( cert2File );
        certEditorBot.clickOkButton();
        modificationLogsViewBot.waitForText( "add: userCertificate;binary\nuserCertificate;binary:: MIICcTCCAdqg" );
        assertTrue( entryEditorBot.getAttributeValues()
            .contains( "userCertificate;binary: X.509v3: CN=End Entity,DC=example,DC=com" ) );

        // edit userCertificate;binary
        certEditorBot = entryEditorBot.editValueExpectingCertificateEditor( "userCertificate;binary",
            "X.509v3: CN=End Entity,DC=example,DC=com" );
        assertTrue( certEditorBot.isVisible() );
        certEditorBot.typeFile( cert3File );
        certEditorBot.clickOkButton();
        modificationLogsViewBot.waitForText( "delete: userCertificate;binary\nuserCertificate;binary:: MIICcTCCAdqg" );
        modificationLogsViewBot.waitForText( "add: userCertificate;binary\nuserCertificate;binary:: MIIDjjCCA06g" );
        assertFalse( entryEditorBot.getAttributeValues()
            .contains( "userCertificate;binary: X.509v3: CN=End Entity,DC=example,DC=com" ) );
        assertTrue( entryEditorBot.getAttributeValues()
            .contains( "userCertificate;binary: X.509v3: CN=DSA End Entity,DC=example,DC=com" ) );

        // delete userCertificate;binary
        entryEditorBot.deleteValue( "userCertificate;binary", "X.509v3: CN=DSA End Entity,DC=example,DC=com" );
        modificationLogsViewBot.waitForText( "delete: userCertificate;binary\nuserCertificate;binary:: MIIDjjCCA06g" );
        assertFalse( entryEditorBot.getAttributeValues()
            .contains( "userCertificate;binary: X.509v3: CN=DSA End Entity,DC=example,DC=com" ) );

    }


    /**
     * DIRSTUDIO-1267:Test adding, editing and deleting of attributes with language tag in the entry editor.
     */
    @Test
    public void testAddEditDeleteAttributeWithLanguageTag() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=org", "ou=users", "uid=user.1" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,dc=example,dc=org" );
        entryEditorBot.activate();

        // add attribute description;lang-en
        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "description" );
        wizardBot.clickNextButton();
        wizardBot.setLanguageTag( "en", "" );
        wizardBot.clickFinishButton();
        entryEditorBot.typeValueAndFinish( "English" );
        modificationLogsViewBot.waitForText( "add: description;lang-en\ndescription;lang-en: English\n-" );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description;lang-en: English" ) );

        // edit the attribute to description;lang-en
        EditAttributeWizardBot editWizardBot = entryEditorBot.editAttribute( "description;lang-en", "English" );
        editWizardBot.clickNextButton();
        editWizardBot.setLanguageTag( "en", "us" );
        editWizardBot.clickFinishButton();
        modificationLogsViewBot.waitForText( "delete: description;lang-en\ndescription;lang-en: English\n-" );
        modificationLogsViewBot.waitForText( "add: description;lang-en-us\ndescription;lang-en-us: English\n-" );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description;lang-en: English" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description;lang-en-us: English" ) );

        // edit the value
        entryEditorBot.editValue( "description;lang-en-us", "English" );
        entryEditorBot.typeValueAndFinish( "English US" );
        modificationLogsViewBot.waitForText( "delete: description;lang-en-us\ndescription;lang-en-us: English\n-" );
        modificationLogsViewBot.waitForText( "add: description;lang-en-us\ndescription;lang-en-us: English US\n-" );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description;lang-en-us: English" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description;lang-en-us: English US" ) );

        // delete the attribute
        entryEditorBot.deleteValue( "description;lang-en-us", "English US" );
        modificationLogsViewBot.waitForText( "delete: description;lang-en-us\ndescription;lang-en-us: English US\n-" );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description;lang-en-us: English US" ) );
    }

}
