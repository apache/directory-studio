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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.directory.api.util.FileUtils;
import org.apache.directory.api.util.IOUtils;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ExportWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.ImportWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the import and export (LDIF, DSML).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles( clazz = ImportExportTest.class,
    value = "org/apache/directory/studio/test/integration/ui/ImportExportTest.ldif" )
public class ImportExportTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "ImportExportTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
    }


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
    @Test
    public void testExportImportLdifWithGermanUmlautInDN() throws Exception
    {
        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportTest.ldif";

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel" );

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
            "dn:: Y249V29sZmdhbmcgS8O2bGJlbCxvdT11c2VycyxvdT1zeXN0ZW0=" );

        // delete entry
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel" ) );

        // import LDIF
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry with umlaut exists
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel" );
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
    @Test
    public void testExportImportDsmlWithGermanUmlautInDN() throws Exception
    {
        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportTest.dsml";

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel" );

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
            content.contains( "dn=\"cn=Wolfgang K\u00f6lbel,ou=users,ou=system\"" ) );

        // delete entry
        DeleteDialogBot dialogBot = browserViewBot.openDeleteDialog();
        assertTrue( dialogBot.isVisible() );
        dialogBot.clickOkButton();
        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel" ) );

        // import DSML
        ImportWizardBot importWizardBot = browserViewBot.openImportDsmlWizard();
        importWizardBot.typeFile( file );
        importWizardBot.clickFinishButton();

        // verify that entry with umlaut exists
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel" );
    }


    /**
     * Test for DIRSTUDIO-465.
     *
     * Import a new context entry must refresh the root DSE and
     * show the new context entry in the LDAP Browser view.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testImportContextEntryRefreshesRootDSE() throws Exception
    {
        // add a new partition
        Partition partition = new AvlPartition(service.getSchemaManager(), service.getDnFactory());
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
     *
     * @throws Exception
     */
    @Test
    public void testImportDoesNotUpdateUI() throws Exception
    {
        URL url = Platform.getInstanceLocation().getURL();
        String destFile = url.getFile() + "ImportDontUpdateUiTest.ldif";
        InputStream is = getClass().getResourceAsStream( "ImportExportTest_ImportDontUpdateUI.ldif" );
        String ldifContent = IOUtils.toString( is, StandardCharsets.UTF_8 );
        FileUtils.writeStringToFile( new File( destFile ), ldifContent, StandardCharsets.UTF_8, false );

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system", "ou=users" );

        long fireCount0 = EventRegistry.getFireCount();

        // import the LDIF
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( destFile );
        importWizardBot.clickFinishButton();
        browserViewBot.waitForEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=User.1" );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=User.1" );

        long fireCount1 = EventRegistry.getFireCount();

        // verify that only three two events were fired between Import
        long fireCount = fireCount1 - fireCount0;
        assertEquals( "Only 2 event firings expected when importing LDIF.", 2, fireCount );
    }


    /**
     * Export to CSV and checks that spreadsheet formulas are prefixed with an apostrophe.
     */
    @Test
    public void testExportCsvShouldPrefixFormulaWithApostrophe() throws Exception
    {
        // set CSV encoding explicit to UTF-8, otherwise platform default encoding would be used
        Preferences store = BrowserCorePlugin.getDefault().getPluginPreferences();
        store.setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ENCODING, "UTF-8" );

        URL url = Platform.getInstanceLocation().getURL();
        final String file = url.getFile() + "ImportExportTest.csv";

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel" );

        // export CSV
        ExportWizardBot wizardBot = browserViewBot.openExportCsvWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeReturningAttributes( "cn, description" );
        wizardBot.clickNextButton();
        wizardBot.typeFile( file );
        wizardBot.clickFinishButton();
        wizardBot.waitTillExportFinished( file, 80 ); // is actually 86 bytes

        List<String> lines = FileUtils.readLines( new File( file ), StandardCharsets.UTF_8 );
        // verify that the first line is header
        assertEquals( "dn,cn,description", lines.get( 0 ) );
        // verify that the second line is actual content and the formula is prefixed with an apostrophe
        assertEquals( "\"cn=Wolfgang K\u00f6lbel,ou=users,ou=system\",\"Wolfgang K\u00f6lbel\",\"'=1+1\"",
            lines.get( 1 ) );
    }


    /**
     * Test for DIRSTUDIO-1160.
     *
     * Attributes silently dropped and not imported when import LDIF and provider is Apache Directory LDAP API.
     *
     * @throws Exception
     */
    @Test
    public void testDIRSTUDIO_1160() throws Exception
    {
        URL url = Platform.getInstanceLocation().getURL();
        String destFile = url.getFile() + "DIRSTUDIO-1160.ldif";
        InputStream is = getClass().getResourceAsStream( "DIRSTUDIO-1160.ldif" );
        String ldifContent = IOUtils.toString( is, StandardCharsets.UTF_8 );
        FileUtils.writeStringToFile( new File( destFile ), ldifContent, StandardCharsets.UTF_8, false );

        // import the LDIF
        ImportWizardBot importWizardBot = browserViewBot.openImportLdifWizard();
        importWizardBot.typeFile( destFile );
        importWizardBot.clickFinishButton();

        Thread.sleep( 10000 );
        browserViewBot.waitForEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=U0034692" );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=U0034692" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=U0034692,ou=users,ou=system" );
        entryEditorBot.activate();
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: Initial import" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: Good#Stuff" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: account#oldUserID#ABC123" ) );
        assertTrue( entryEditorBot.getAttributeValues()
            .contains( "description: person#homeEmailAddress#jhon.doe@apache.com" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: Thöis ås a täst yes" ) );
    }
}
