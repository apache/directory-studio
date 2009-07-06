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


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
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
@RunWith(SiRunner.class)
@CleanupLevel(Level.SUITE)
@ApplyLdifFiles(
    { "ImportExportTest.ldif" })
public class ImportExportTest
{
    public static LdapServer ldapServer;

    private SWTWorkbenchBot eBot;


    @Before
    public void setUp() throws Exception
    {
        eBot = new SWTWorkbenchBot();
        SWTBotUtils.openLdapPerspective( eBot );
        SWTBotUtils.createTestConnection( eBot, "ImportExportTest", ldapServer.getPort() );
    }


    @After
    public void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        eBot = null;
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

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( eBot );

        SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Wolfgang K\u00f6lbel" );

        // export LDIF
        ContextMenuHelper.clickContextMenu( browserTree, "Export", "LDIF Export..." );
        eBot.shell( "LDIF Export" );
        eBot.button( "Next >" ).click();
        eBot.comboBoxWithLabel( "LDIF File:" ).setText( file );
        eBot.button( "Finish" ).click();

        // wait till LDIF file exists
        eBot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                File f = new File( file );
                return f.exists() && f.length() > 200; // is actually 217 bytes
            }


            public String getFailureMessage()
            {
                return "LDIF File " + file + " not found.";
            }
        } );

        List<String> lines = FileUtils.readLines( new File( file ) );
        // verify that the first line of exported LDIF is "version: 1"
        assertEquals( "LDIF must start with version: 1", lines.get( 0 ), "version: 1" );
        // verify that the third line of exported LDIF is the Base64 encoded DN
        assertEquals( "Expected Base64 encoded DN", lines.get( 2 ),
            "dn:: Y249V29sZmdhbmcgS8O2bGJlbCxvdT11c2VycyxvdT1zeXN0ZW0=" );

        // delete entry
        ContextMenuHelper.clickContextMenu( browserTree, "Delete Entry" );
        eBot.button( "OK" ).click();

        // import LDIF
        ContextMenuHelper.clickContextMenu( browserTree, "Import", "LDIF Import..." );
        eBot.shell( "LDIF Import" );
        eBot.comboBoxWithLabel( "LDIF File:" ).setText( file );
        eBot.button( "Finish" ).click();

        // verify that entry with umlaut exists
        SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Wolfgang K\u00f6lbel" );
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

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( eBot );

        SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Wolfgang K\u00f6lbel" );

        // export DSML
        ContextMenuHelper.clickContextMenu( browserTree, "Export", "DSML Export..." );
        eBot.shell( "DSML Export" );
        eBot.button( "Next >" ).click();
        eBot.comboBoxWithLabel( "DSML File:" ).setText( file );
        eBot.radio( "DSML Request" ).click();
        eBot.button( "Finish" ).click();

        // wait till DSML file exists
        eBot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                File f = new File( file );
                return f.exists() && f.length() > 600; // is actually 651 bytes
            }


            public String getFailureMessage()
            {
                return "DSML File " + file + " not found.";
            }
        } );

        // verify that exported DSML contains the Base64 encoded DN
        String content = FileUtils.readFileToString( new File( file ), "UTF-8" );
        assertTrue( "DSML must contain DN with umlaut.", content
            .contains( "dn=\"cn=Wolfgang K\u00f6lbel,ou=users,ou=system\"" ) );

        // delete entry
        ContextMenuHelper.clickContextMenu( browserTree, "Delete Entry" );
        eBot.button( "OK" ).click();

        // import DSML
        ContextMenuHelper.clickContextMenu( browserTree, "Import", "DSML Import..." );
        eBot.shell( "DSML Import" );
        eBot.comboBoxWithLabel( "DSML File:" ).setText( file );
        eBot.button( "Finish" ).click();

        // verify that entry with umlaut exists
        SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Wolfgang K\u00f6lbel" );
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
        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( eBot );

        // add a new partition
        Partition partition = new JdbmPartition();
        partition.setId( "example" );
        partition.setSuffix( "dc=example,dc=com" );
        ldapServer.getDirectoryService().addPartition( partition );

        // refresh root DSE and ensure that the partition is in root DSE
        SWTBotTreeItem rootDSE = SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE" );
        ContextMenuHelper.clickContextMenu( browserTree, "Reload Entry" );

        // ensure context entry is not there
        rootDSE = SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE" );
        List<String> nodes = rootDSE.getNodes();
        for ( String node : nodes )
        {
            if ( node.startsWith( "dc=example,dc=com" ) )
            {
                fail( "dc=example,dc=com should not exist yet" );
            }
        }

        // import
        URL url = Platform.getInstanceLocation().getURL();
        String file = url.getFile() + "ImportContextEntry.ldif";
        String data = "dn:dc=example,dc=com\nobjectClass:top\nobjectClass:domain\ndc:example\n\n";
        FileUtils.writeStringToFile( new File( file ), data );
        ContextMenuHelper.clickContextMenu( browserTree, "Import", "LDIF Import..." );
        eBot.shell( "LDIF Import" );
        eBot.comboBoxWithLabel( "LDIF File:" ).setText( file );
        eBot.button( "Finish" ).click();

        // ensure context entry is there now, without a manual refresh
        SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE", "dc=example,dc=com" );
    }


    /**
     * Test for DIRSTUDIO-489.
     * 
     * Verify that there are no UI updates when importing an LDIF.
     *
     * @throws Exception
     */
    @Test
    public void testImportDontUptateUI() throws Exception
    {
        URL url = Platform.getInstanceLocation().getURL();
        String destFile = url.getFile() + "ImportDontUpdateUiTest.ldif";
        InputStream is = getClass().getResourceAsStream( "ImportExportTest_ImportDontUpdateUI.ldif" );
        String ldifContent = IOUtils.toString( is );
        FileUtils.writeStringToFile( new File( destFile ), ldifContent );

        SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( eBot );
        SWTBotUtils.selectEntry( eBot, browserTree, true, "DIT", "Root DSE", "ou=system", "ou=users" );

        long fireCount0 = EventRegistry.getFireCount();

        // import the LDIF
        ContextMenuHelper.clickContextMenu( browserTree, "Import", "LDIF Import..." );
        eBot.shell( "LDIF Import" );
        eBot.comboBoxWithLabel( "LDIF File:" ).setText( destFile );
        eBot.button( "Finish" ).click();

        long fireCount1 = EventRegistry.getFireCount();

        SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users", "uid=User.1" );

        // verify that only three two events were fired between Import 
        long fireCount = fireCount1 - fireCount0;
        assertEquals( "Only 2 event firings expected when importing LDIF.", 2, fireCount );
    }

}
