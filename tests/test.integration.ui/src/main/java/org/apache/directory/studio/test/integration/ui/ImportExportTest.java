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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.ALIAS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.GERMAN_UMLAUT_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MISC111_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MISC_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.SUBENTRY_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER2_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.dn;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.FileUtils;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.test.integration.junit5.ApacheDirectoryServer;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.BotUtils;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ExportWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.ImportWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Characters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the import and export (LDIF, DSML).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportExportTest extends AbstractTestBase
{

    /**
     * Test for DIRSTUDIO-395.
     *
     * <li>export an entry with German umlaut in DN to LDIF</li> <li>verify that
     * exported LDIF starts with the Base64 encoded DN</li> <li>delete the entry
     * </li> <li>import the exported LDIF</li> <li>verify that entry with umlaut
     * exists</li>
     *
     * @throws Exception
     *             the exception
     */
    @ParameterizedTest
    @LdapServersSource
    public void testExportImportLdifWithGermanUmlautInDN( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportWithGermanUmlautInDnTest" + server.getType().name() + ".ldif";

        browserViewBot.selectEntry( path( GERMAN_UMLAUT_DN ) );

        // export LDIF
        ExportWizardBot wizardBot = browserViewBot.openExportLdifWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 200 ); // is actually 217 bytes

        List<String> lines = FileUtils.readLines( new File( file ), StandardCharsets.UTF_8 );
        // verify that the first line of exported LDIF is "version: 1"
        assertEquals( "LDIF must start with version: 1", lines.get( 0 ), "version: 1" );
        // verify that the third line of exported LDIF is the Base64 encoded DN
        assertEquals( "Expected Base64 encoded DN", lines.get( 2 ),
            "dn:: Y249V29sZmdhbmcgS8O2bGJlbCxvdT1taXNjLGRjPWV4YW1wbGUsZGM9b3Jn" );

        // delete entry
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        assertFalse( browserViewBot.existsEntry( path( GERMAN_UMLAUT_DN ) ) );

        // import LDIF
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry with umlaut exists
        assertTrue( browserViewBot.existsEntry( path( GERMAN_UMLAUT_DN ) ) );
        browserViewBot.selectEntry( path( GERMAN_UMLAUT_DN ) );
    }


    /**
     * Test for DIRSTUDIO-395.
     *
     * <li>export an entry with German umlaut in DN to DSML</li> <li>verify that
     * exported DSML starts with the Base64 encoded DN</li> <li>delete the entry
     * </li> <li>import the exported DSML</li> <li>verify that entry with umlaut
     * exists</li>
     *
     * @throws Exception
     *             the exception
     */
    @ParameterizedTest
    @LdapServersSource
    public void testExportImportDsmlWithGermanUmlautInDN( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportWithGermanUmlautInDnTest" + server.getType().name() + ".dsml";

        browserViewBot.selectEntry( path( GERMAN_UMLAUT_DN ) );

        // export DSML
        ExportWizardBot wizardBot = browserViewBot.openExportDsmlWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.selectDsmlRequest();
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 500 ); // is actually 542 bytes

        // verify that exported DSML contains the Base64 encoded DN
        String content = FileUtils.readFileToString( new File( file ), StandardCharsets.UTF_8 );
        assertTrue( "DSML must contain DN with umlaut.",
            content.contains( "dn=\"" + GERMAN_UMLAUT_DN.getName() + "\"" ) );

        // delete entry
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        assertFalse( browserViewBot.existsEntry( path( GERMAN_UMLAUT_DN ) ) );

        // import DSML
        ImportWizardBot importWizardBot = browserViewBot.openImportDsmlWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry with umlaut exists
        assertTrue( browserViewBot.existsEntry( path( GERMAN_UMLAUT_DN ) ) );
        browserViewBot.selectEntry( path( GERMAN_UMLAUT_DN ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testExportImportLdifAlias( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        // disable alias dereferencing
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            AliasDereferencingMethod.NEVER.ordinal() );

        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportAlias" + server.getType().name() + ".ldif";

        browserViewBot.selectEntry( path( ALIAS_DN.getParent() ) );

        // export to LDIF
        ExportWizardBot wizardBot = browserViewBot.openExportLdifWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.setFilter( "(objectClass=alias)" );
        wizardBot.setScope( SearchScope.ONELEVEL );
        wizardBot.setAliasDereferencingMode( AliasDereferencingMethod.NEVER );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 50 );

        List<String> lines = FileUtils.readLines( new File( file ), StandardCharsets.UTF_8 );
        assertEquals( "LDIF must start with version: 1", lines.get( 0 ), "version: 1" );
        assertTrue( lines.contains( "dn: " + ALIAS_DN.getName() ) );

        // delete entry
        browserViewBot.selectEntry( path( ALIAS_DN ) );
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        waitAndAssert( false,
            () -> browserViewBot.existsEntry( path( ALIAS_DN ) ) );

        // import LDIF
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry exist
        assertTrue( browserViewBot.existsEntry( path( ALIAS_DN ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testExportImportLdifReferral( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        // enable ManageDsaIT control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );

        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportReferral" + server.getType().name() + ".ldif";

        browserViewBot.selectEntry( path( REFERRAL_TO_USER1_DN.getParent() ) );

        // export to LDIF
        ExportWizardBot wizardBot = browserViewBot.openExportLdifWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.setFilter( "(" + REFERRAL_TO_USER1_DN.getRdn().getName() + ")" );
        wizardBot.setReturningAttributes( "ref" );
        wizardBot.setScope( SearchScope.ONELEVEL );
        wizardBot.setControlManageDsaIT( true );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 20 );

        List<String> lines = FileUtils.readLines( new File( file ), StandardCharsets.UTF_8 );
        assertEquals( "LDIF must start with version: 1", lines.get( 0 ), "version: 1" );
        assertTrue( lines.contains( "dn: " + REFERRAL_TO_USER1_DN.getName() ) );
        assertTrue( lines.contains( "ref: " + server.getLdapUrl() + "/" + USER1_DN.getName() ) );

        // delete entry
        browserViewBot.selectEntry( path( REFERRAL_TO_USER1_DN ) );
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        waitAndAssert( false,
            () -> browserViewBot.existsEntry( path( REFERRAL_TO_USER1_DN ) ) );

        // import LDIF
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry exist
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_TO_USER1_DN ) ) );
    }


    @ParameterizedTest
    @LdapServersSource(types = LdapServerType.ApacheDS)
    public void testExportImportLdifSubentry( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        // enable Subentries control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES, true );

        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportSubentry" + server.getType().name() + ".ldif";

        browserViewBot.selectEntry( path( SUBENTRY_DN.getParent() ) );

        // export to LDIF
        ExportWizardBot wizardBot = browserViewBot.openExportLdifWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.setFilter( "(objectClass=subentry)" );
        wizardBot.setReturningAttributes( "subtreeSpecification" );
        wizardBot.setScope( SearchScope.ONELEVEL );
        wizardBot.setControlSubentries( true );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 20 );

        List<String> lines = FileUtils.readLines( new File( file ), StandardCharsets.UTF_8 );
        assertEquals( "LDIF must start with version: 1", lines.get( 0 ), "version: 1" );
        assertTrue( lines.contains( "dn: " + SUBENTRY_DN.getName() ) );
        assertTrue( lines.contains( "subtreeSpecification: {}" ) );

        // delete entry
        browserViewBot.selectEntry( path( SUBENTRY_DN ) );
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        waitAndAssert( false,
            () -> browserViewBot.existsEntry( path( SUBENTRY_DN ) ) );

        // import LDIF
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry exist
        assertTrue( browserViewBot.existsEntry( path( SUBENTRY_DN ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testExportImportDsmlAlias( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        // disable alias dereferencing
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            AliasDereferencingMethod.NEVER.ordinal() );

        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportAlias" + server.getType().name() + ".dsml";

        browserViewBot.selectEntry( path( ALIAS_DN.getParent() ) );

        // export to DSML
        ExportWizardBot wizardBot = browserViewBot.openExportDsmlWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.setFilter( "(objectClass=alias)" );
        wizardBot.setScope( SearchScope.ONELEVEL );
        wizardBot.setAliasDereferencingMode( AliasDereferencingMethod.NEVER );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.selectDsmlRequest();
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 50 );

        // verify that exported DSML contains the entry
        String content = FileUtils.readFileToString( new File( file ), StandardCharsets.UTF_8 );
        assertTrue( content.contains( "dn=\"" + ALIAS_DN.getName() + "\"" ) );

        // delete entry
        browserViewBot.selectEntry( path( ALIAS_DN ) );
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        waitAndAssert( false,
            () -> browserViewBot.existsEntry( path( ALIAS_DN ) ) );

        // import DSML
        ImportWizardBot importWizardBot = browserViewBot.openImportDsmlWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry exist
        assertTrue( browserViewBot.existsEntry( path( ALIAS_DN ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testExportImportDsmlReferral( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        // enable ManageDsaIT control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );

        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportReferral" + server.getType().name() + ".dsml";

        browserViewBot.selectEntry( path( REFERRAL_TO_USER1_DN.getParent() ) );

        // export to DSML
        ExportWizardBot wizardBot = browserViewBot.openExportDsmlWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.setFilter( "(" + REFERRAL_TO_USER1_DN.getRdn().getName() + ")" );
        wizardBot.setReturningAttributes( "ref" );
        wizardBot.setScope( SearchScope.ONELEVEL );
        wizardBot.setControlManageDsaIT( true );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.selectDsmlRequest();
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 50 );

        // verify that exported DSML contains the entry
        String content = FileUtils.readFileToString( new File( file ), StandardCharsets.UTF_8 );
        assertTrue( content.contains( "dn=\"" + REFERRAL_TO_USER1_DN.getName() + "\"" ) );
        assertTrue( content.contains( "<attr name=\"ref\">" ) );
        assertTrue( content.contains( "<value>" + server.getLdapUrl() + "/" + USER1_DN.getName() + "</value>" ) );

        // delete entry
        browserViewBot.selectEntry( path( REFERRAL_TO_USER1_DN ) );
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        waitAndAssert( false,
            () -> browserViewBot.existsEntry( path( REFERRAL_TO_USER1_DN ) ) );

        // import DSML
        ImportWizardBot importWizardBot = browserViewBot.openImportDsmlWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry exist
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_TO_USER1_DN ) ) );
    }


    @ParameterizedTest
    @LdapServersSource(types = LdapServerType.ApacheDS)
    public void testExportImportDsmlSubentry( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        // enable Subentries control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES, true );

        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportSubentry" + server.getType().name() + ".dsml";

        browserViewBot.selectEntry( path( SUBENTRY_DN.getParent() ) );

        // export to DSML
        ExportWizardBot wizardBot = browserViewBot.openExportDsmlWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.setFilter( "(objectClass=subentry)" );
        wizardBot.setReturningAttributes( "subtreeSpecification" );
        wizardBot.setScope( SearchScope.ONELEVEL );
        wizardBot.setControlSubentries( true );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.selectDsmlRequest();
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 50 );

        // verify that exported DSML
        String content = FileUtils.readFileToString( new File( file ), StandardCharsets.UTF_8 );
        System.out.println( content );
        assertTrue( content.contains( "dn=\"" + SUBENTRY_DN.getName() + "\"" ) );
        assertTrue( content.contains( "<attr name=\"subtreespecification\">" ) );
        assertTrue( content.contains( "<value>{}</value>" ) );

        // delete entry
        browserViewBot.selectEntry( path( SUBENTRY_DN ) );
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        waitAndAssert( false,
            () -> browserViewBot.existsEntry( path( SUBENTRY_DN ) ) );

        // import DSML
        ImportWizardBot importWizardBot = browserViewBot.openImportDsmlWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry exist
        assertTrue( browserViewBot.existsEntry( path( SUBENTRY_DN ) ) );
    }


    private void waitAndAssert( boolean expectedResult, Supplier<Boolean> fn )
    {
        for ( int i = 0; i < 30; i++ )
        {
            if ( fn.get() == expectedResult )
            {
                break;
            }
            BotUtils.sleep( 1000L );
        }
        if ( expectedResult == true )
        {
            assertTrue( fn.get() );
        }
        else
        {
            assertFalse( fn.get() );
        }
    }


    @ParameterizedTest
    @LdapServersSource(types = LdapServerType.ApacheDS)
    public void testExportWithPagedResultControl( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ExportWithPagedResultControl.ldif";

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=schema" );

        // export LDIF
        ExportWizardBot wizardBot = browserViewBot.openExportLdifWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.setScope( SearchScope.ONELEVEL );
        wizardBot.setControlPagedSearch( true, 17, true );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 2500 );

        List<String> lines = FileUtils.readLines( new File( file ), StandardCharsets.UTF_8 );
        assertEquals( "LDIF must start with version: 1", lines.get( 0 ), "version: 1" );
        assertTrue( lines.contains( "dn: cn=adsconfig,ou=schema" ) );
        assertTrue( lines.contains( "dn: cn=apachemeta,ou=schema" ) );
        assertTrue( lines.contains( "dn: cn=core,ou=schema" ) );
        assertTrue( lines.contains( "dn: cn=rfc2307bis,ou=schema" ) );
        assertTrue( lines.contains( "dn: cn=system,ou=schema" ) );
        searchLogsViewBot.getSearchLogsText().contains( "# numEntries : 17" );
        searchLogsViewBot.getSearchLogsText().contains( "# control      : 1.2.840.113556.1.4.319" );
    }


    /**
     * Test for DIRSTUDIO-465.
     *
     * Import a new context entry must refresh the root DSE and
     * show the new context entry in the LDAP Browser view.
     */
    @ParameterizedTest
    @LdapServersSource(types = LdapServerType.ApacheDS)
    public void testImportContextEntryRefreshesRootDSE( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );

        // add a new partition
        ApacheDirectoryServer apacheds = ( ApacheDirectoryServer ) server;
        DirectoryService service = apacheds.getService();
        Partition partition = new AvlPartition( service.getSchemaManager(), service.getDnFactory() );
        partition.setId( "example" );
        partition.setSuffixDn( new Dn( "dc=example,dc=com" ) );
        service.addPartition( partition );

        // refresh root DSE and ensure that the partition is in root DSE
        browserViewBot.selectEntry( "DIT", "Root DSE" );
        browserViewBot.refresh();

        // ensure context entry is not there
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=com" ) );

        // import
        URL url = Platform.getInstanceLocation().getURL();
        String file = url.getFile() + "ImportContextEntry.ldif";
        String data = "dn:dc=example,dc=com\nobjectClass:top\nobjectClass:domain\ndc:example\n\n";
        FileUtils.writeStringToFile( new File( file ), data, StandardCharsets.UTF_8, false );
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // ensure context entry is there now, without a manual refresh
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "dc=example,dc=com" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "dc=example,dc=com" );
    }


    /**
     * Test for DIRSTUDIO-489.
     *
     * Verify that there are no UI updates when importing an LDIF.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testImportDoesNotUpdateUI( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( MISC111_DN ) );
        browserViewBot.expandEntry( path( MISC111_DN ) );

        long fireCount0 = EventRegistry.getFireCount();

        // import the LDIF
        String file = prepareInputFile( "ImportExportTest_User1to8.ldif" );
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();
        browserViewBot.waitForEntry( path( MISC111_DN, "uid=User.1" ) );
        browserViewBot.selectEntry( path( MISC111_DN, "uid=User.1" ) );

        long fireCount1 = EventRegistry.getFireCount();

        // verify that only three two events were fired between Import
        long fireCount = fireCount1 - fireCount0;
        assertEquals( "Only 2 event firings expected when importing LDIF.", 2, fireCount );
    }


    /**
     * Export to CSV and checks that spreadsheet formulas are prefixed with an apostrophe.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testExportCsvShouldPrefixFormulaWithApostrophe( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        // set CSV encoding explicit to UTF-8, otherwise platform default encoding would be used
        Preferences store = BrowserCorePlugin.getDefault().getPluginPreferences();
        store.setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ENCODING, "UTF-8" );

        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportTest" + server.getType().name() + ".csv";

        browserViewBot.selectEntry( path( GERMAN_UMLAUT_DN ) );

        // export CSV
        ExportWizardBot wizardBot = browserViewBot.openExportCsvWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.setReturningAttributes( "cn, description" );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 80 ); // is actually 86 bytes

        List<String> lines = FileUtils.readLines( new File( file ), StandardCharsets.UTF_8 );
        // verify that the first line is header
        assertEquals( "dn,cn,description", lines.get( 0 ) );
        // verify that the second line is actual content and the formula is prefixed with an apostrophe
        assertEquals( "\"" + GERMAN_UMLAUT_DN.getName() + "\",\"Wolfgang K\u00f6lbel\",\"'=1+1\"", lines.get( 1 ) );
    }


    /**
     * Test for DIRSTUDIO-1160.
     *
     * Attributes silently dropped and not imported when import LDIF and provider is Apache Directory LDAP API.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testDIRSTUDIO_1160( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        Dn dn = dn( "cn=U0034692", MISC_DN );

        // import the LDIF
        String file = prepareInputFile( "DIRSTUDIO-1160.ldif" );
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        browserViewBot.waitForEntry( path( dn ) );
        browserViewBot.selectEntry( path( dn ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( dn.getName() );
        entryEditorBot.activate();
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: Initial import" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: Good#Stuff" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: account#oldUserID#ABC123" ) );
        assertTrue( entryEditorBot.getAttributeValues()
            .contains( "description: person#homeEmailAddress#jhon.doe@apache.com" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: Th\u00f6is \u00e5s a t\u00e4st yes" ) );
    }


    /**
     * Test LDIF with several modifications.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testLdifModification( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        // import the LDIF
        String file = prepareInputFile( "ImportExportTest_Modifications.ldif" );
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        browserViewBot.waitForEntry( path( USER1_DN ) );
        browserViewBot.selectEntry( path( USER1_DN ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: " + Characters.ALL ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "roomNumber: 1388" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "roomNumber: 1234" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "roomNumber: 2345" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "roomNumber: 3456" ) );
        if ( server.getType() == LdapServerType.ApacheDS )
        {
            assertTrue( entryEditorBot.getAttributeValues()
                .contains( "userCertificate: X.509v3: CN=End Entity,DC=example,DC=com" ) );
            assertTrue( entryEditorBot.getAttributeValues().contains( "description: Deutsch" ) );
            assertTrue( entryEditorBot.getAttributeValues().contains( "description: English" ) );
        }
        else
        {
            assertTrue( entryEditorBot.getAttributeValues()
                .contains( "userCertificate;binary: X.509v3: CN=End Entity,DC=example,DC=com" ) );
            assertTrue( entryEditorBot.getAttributeValues().contains( "description;lang-de: Deutsch" ) );
            assertTrue( entryEditorBot.getAttributeValues().contains( "description;lang-en: English" ) );
        }
        modificationLogsViewBot.waitForText( "add: userCertificate;binary\nuserCertificate;binary:: " );
        modificationLogsViewBot.waitForText( "add: description;lang-en\ndescription;lang-en: " );
        modificationLogsViewBot.waitForText( "add: description;lang-de\ndescription;lang-de: " );

        assertFalse( browserViewBot.existsEntry( path( USER2_DN ) ) );

        browserViewBot.waitForEntry( path( USERS_DN, "uid=user.33" ) );
        browserViewBot.selectEntry( path( USERS_DN, "uid=user.33" ) );
        entryEditorBot.activate();
        assertTrue( entryEditorBot.getAttributeValues().contains( "uid: user.33" ) );
        assertFalse( entryEditorBot.getAttributeValues().contains( "uid: user.3" ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testImportUpdateExistingEntriesFalse( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        Dn dn = dn( "uid=User.1", MISC111_DN );
        server.withAdminConnection( conn -> {
            conn.add( new DefaultEntry( conn.getSchemaManager(), dn,
                "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: User.1" ) );
        } );

        // import the LDIF
        String file = prepareInputFile( "ImportExportTest_User1to8.ldif" );
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.setUpdateExistingEntries( false );
        importWizardBot.setContinueOnError( false );
        importWizardBot.clickFinishButton();

        // check entry was not updated
        browserViewBot.waitForEntry( path( dn ) );
        browserViewBot.selectEntry( path( dn ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( dn.getName() );
        entryEditorBot.activate();
        assertTrue( entryEditorBot.getAttributeValues().contains( "sn: X" ) );
        assertFalse( entryEditorBot.getAttributeValues().contains( "sn: Amar" ) );
        assertFalse( entryEditorBot.getAttributeValues().contains( "roomNumber: 1388" ) );

        // check error in modifications logs view
        modificationLogsViewBot.assertContainsError( "[LDAP result code 68 - entryAlreadyExists]",
            "dn: " + dn.getName(), "changetype: add" );
        // check error in LDIF log file
        String logContent = FileUtils.readFileToString( new File( file + ".log" ), StandardCharsets.UTF_8 );
        assertThat( logContent, containsString( "[LDAP result code 68 - entryAlreadyExists]" ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testImportUpdateExistingEntriesTrue( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        Dn dn = dn( "uid=User.1", MISC111_DN );
        server.withAdminConnection( conn -> {
            conn.add( new DefaultEntry( conn.getSchemaManager(), dn,
                "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: User.1" ) );
        } );

        // import the LDIF
        String file = prepareInputFile( "ImportExportTest_User1to8.ldif" );
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.setUpdateExistingEntries( true );
        importWizardBot.setContinueOnError( false );
        importWizardBot.clickFinishButton();

        // check entry was updated
        browserViewBot.waitForEntry( path( dn ) );
        browserViewBot.selectEntry( path( dn ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( dn.getName() );
        entryEditorBot.activate();
        assertFalse( entryEditorBot.getAttributeValues().contains( "sn: X" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "sn: Amar" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "roomNumber: 1388" ) );

        // check error and update in modifications logs view
        modificationLogsViewBot.assertContainsError( "[LDAP result code 68 - entryAlreadyExists]",
            "dn: " + dn.getName(), "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: " + dn.getName(), "changetype: modify" );
        // check no error but update in LDIF log file
        String logContent = FileUtils.readFileToString( new File( file + ".log" ), StandardCharsets.UTF_8 );
        assertThat( logContent, not( containsString( "[LDAP result code 68 - entryAlreadyExists]" ) ) );
    }


    private String prepareInputFile( String inputFileName ) throws IOException
    {
        return ResourceUtils.prepareInputFile( inputFileName );
    }

}
