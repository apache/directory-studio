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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.test.integration.ui.bots.ExportWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.ImportWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.NewWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.PreferencesBot;
import org.apache.directory.studio.test.integration.ui.bots.ShowViewsBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * General tests of the Studio RCP application: layout of perspectives, visible menu items, etc.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
public class RcpAppTest extends AbstractLdapTestUnit
{
    private SWTWorkbenchBot bot;
    private StudioBot studioBot;


    @Before
    public void setUp() throws Exception
    {
        bot = new SWTWorkbenchBot();
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
    }


    @After
    public void tearDown() throws Exception
    {
    }


    /**
     * Verify views in LDAP perspective.
     */
    @Test
    public void testLdapPerspectiveViews() throws Exception
    {
        studioBot.resetLdapPerspective();

        assertNotNull( bot.viewByTitle( "LDAP Browser" ) );
        assertNotNull( bot.viewByTitle( "LDAP Servers" ) );
        assertNotNull( bot.viewByTitle( "Connections" ) );
        assertNotNull( bot.viewByTitle( "Outline" ) );
        assertNotNull( bot.viewByTitle( "Progress" ) );
        assertNotNull( bot.viewByTitle( "Modification Logs" ) );
        assertNotNull( bot.viewByTitle( "Search Logs" ) );
        assertNotNull( bot.viewByTitle( "Error Log" ) );
    }


    /**
     * Verify views in Schema Editor perspective.
     */
    @Test
    public void testSchemaEditorPerspectiveViews() throws Exception
    {
        studioBot.resetSchemaPerspective();

        assertNotNull( bot.viewByTitle( "Schema" ) );
        assertNotNull( bot.viewByTitle( "Hierarchy" ) );
        assertNotNull( bot.viewByTitle( "Projects" ) );
        assertNotNull( bot.viewByTitle( "Problems" ) );
        assertNotNull( bot.viewByTitle( "Search" ) );
    }


    /**
     * Verify visible items in 'Show Views' dialog.
     */
    @Test
    public void testVisibleItemsInOpenViewsDialog() throws Exception
    {
        ShowViewsBot showViews = studioBot.openShowViews();

        assertTrue( showViews.existsView( "General", "Console" ) );
        assertTrue( showViews.existsView( "General", "Console" ) );
        assertTrue( showViews.existsView( "General", "Error Log" ) );
        assertTrue( showViews.existsView( "General", "Outline" ) );
        assertTrue( showViews.existsView( "General", "Progress" ) );
        assertTrue( showViews.existsView( "Help", "Help" ) );
        assertTrue( showViews.existsView( "LDAP Browser", "Connections" ) );
        assertTrue( showViews.existsView( "LDAP Browser", "LDAP Browser" ) );
        assertTrue( showViews.existsView( "LDAP Browser", "Modification Logs" ) );
        assertTrue( showViews.existsView( "LDAP Browser", "Search Logs" ) );
        assertTrue( showViews.existsView( "LDAP Servers", "LDAP Servers" ) );
        assertTrue( showViews.existsView( "Schema Editor", "Hierarchy" ) );
        assertTrue( showViews.existsView( "Schema Editor", "Problems" ) );
        assertTrue( showViews.existsView( "Schema Editor", "Projects" ) );
        assertTrue( showViews.existsView( "Schema Editor", "Schema" ) );
        assertTrue( showViews.existsView( "Schema Editor", "Search" ) );

        showViews.clickCancelButton();
    }


    /**
     * Verify hidden items in 'Show Views' dialog. Many unwanted views are contributed 
     * by org.eclipse.* plugins, we configured to hide them in rcp/plugin.xml. 
     */
    @Test
    public void testHiddenItemsInOpenViewsDialog() throws Exception
    {
        ShowViewsBot showViews = studioBot.openShowViews();

        assertFalse( showViews.existsView( "General", "Bookmarks" ) );
        assertFalse( showViews.existsView( "General", "Problems" ) );
        assertFalse( showViews.existsView( "General", "Navigator" ) );
        assertFalse( showViews.existsView( "General", "Project Explorer" ) );
        assertFalse( showViews.existsView( "General", "Properties" ) );

        showViews.clickCancelButton();
    }


    /**
     * Verify visible items in 'New' wizard.
     */
    @Test
    public void testVisibleItemsInNewWizard() throws Exception
    {
        NewWizardBot newWizard = studioBot.openNewWizard();

        assertTrue( newWizard.existsWizard( "ApacheDS", "ApacheDS 2.0 Configuration File" ) );

        assertTrue( newWizard.existsWizard( "LDAP Browser", "LDAP Connection" ) );
        assertTrue( newWizard.existsWizard( "LDAP Browser", "LDAP Entry" ) );
        assertTrue( newWizard.existsWizard( "LDAP Browser", "LDAP Search" ) );
        assertTrue( newWizard.existsWizard( "LDAP Browser", "LDAP Bookmark" ) );
        assertTrue( newWizard.existsWizard( "LDAP Browser", "LDIF File" ) );

        assertTrue( newWizard.existsWizard( "Schema Editor", "New Schema Project" ) );
        assertTrue( newWizard.existsWizard( "Schema Editor", "New Schema" ) );
        assertTrue( newWizard.existsWizard( "Schema Editor", "New Object Class" ) );
        assertTrue( newWizard.existsWizard( "Schema Editor", "New Attribute Type" ) );

        newWizard.clickCancelButton();
    }


    /**
     * Verify hidden items in 'New' wizard. Many unwanted wizards are contributed 
     * by org.eclipse.* plugins, we configured to hide them in rcp/plugin.xml. 
     */
    @Test
    public void testHiddenItemsInNewWizard() throws Exception
    {
        NewWizardBot newWizard = studioBot.openNewWizard();

        assertFalse( newWizard.existsWizard( "General", "File" ) );
        assertFalse( newWizard.existsWizard( "General", "Folder" ) );
        assertFalse( newWizard.existsWizard( "General", "Project" ) );

        newWizard.clickCancelButton();
    }


    /**
     * Verify visible items in 'Export' wizard.
     */
    @Test
    public void testVisibleItemsInExportWizard() throws Exception
    {
        ExportWizardBot exportWizard = studioBot.openExportWizard();

        assertTrue( exportWizard.existsWizard( "LDAP Browser", "LDAP to CSV" ) );
        assertTrue( exportWizard.existsWizard( "LDAP Browser", "LDAP to DSML" ) );
        assertTrue( exportWizard.existsWizard( "LDAP Browser", "LDAP to Excel" ) );
        assertTrue( exportWizard.existsWizard( "LDAP Browser", "LDAP to LDIF" ) );
        assertTrue( exportWizard.existsWizard( "LDAP Browser", "LDAP to ODF" ) );

        assertTrue( exportWizard.existsWizard( "Schema Editor", "Schema Projects" ) );
        assertTrue( exportWizard.existsWizard( "Schema Editor", "Schemas as OpenLDAP files" ) );
        assertTrue( exportWizard.existsWizard( "Schema Editor", "Schemas as XML file(s)" ) );
        assertTrue( exportWizard.existsWizard( "Schema Editor", "Schemas for ApacheDS" ) );

        exportWizard.clickCancelButton();
    }


    /**
     * Verify hidden items in 'Export' wizard. Many unwanted wizards are contributed 
     * by org.eclipse.* plugins, we configured to hide them in rcp/plugin.xml. 
     */
    @Test
    public void testHiddenItemsInExportWizard() throws Exception
    {
        ExportWizardBot exportWizard = studioBot.openExportWizard();

        assertFalse( exportWizard.existsWizard( "General", "Archive File" ) );
        assertFalse( exportWizard.existsWizard( "General", "Filesystem" ) );

        assertFalse( exportWizard.existsCategory( "Install" ) );
        assertFalse( exportWizard.existsCategory( "Team" ) );
        assertFalse( exportWizard.existsCategory( "Java" ) );

        exportWizard.clickCancelButton();
    }


    /**
     * Verify visible items in 'Import' wizard.
     */
    @Test
    public void testVisibleItemsInImportWizard() throws Exception
    {
        ImportWizardBot importWizard = studioBot.openImportWizard();

        assertTrue( importWizard.existsWizard( "LDAP Browser", "DSML into LDAP" ) );
        assertTrue( importWizard.existsWizard( "LDAP Browser", "LDIF into LDAP" ) );

        assertTrue( importWizard.existsWizard( "Schema Editor", "Core schemas files" ) );
        assertTrue( importWizard.existsWizard( "Schema Editor", "Schema Projects" ) );
        assertTrue( importWizard.existsWizard( "Schema Editor", "Schemas from OpenLDAP files" ) );
        assertTrue( importWizard.existsWizard( "Schema Editor", "Schemas from XML file(s)" ) );

        importWizard.clickCancelButton();
    }


    /**
     * Verify hidden items in 'Import' wizard. Many unwanted wizards are contributed 
     * by org.eclipse.* plugins, we configured to hide them in rcp/plugin.xml. 
     */
    @Test
    public void testHiddenItemsInImportWizard() throws Exception
    {
        ImportWizardBot importWizard = studioBot.openImportWizard();

        assertFalse( importWizard.existsWizard( "General", "Archive File" ) );
        assertFalse( importWizard.existsWizard( "General", "Filesystem" ) );

        assertFalse( importWizard.existsCategory( "Install" ) );
        assertFalse( importWizard.existsCategory( "Team" ) );
        assertFalse( importWizard.existsCategory( "Java" ) );
        assertFalse( importWizard.existsCategory( "Maven" ) );
        assertFalse( importWizard.existsCategory( "Git" ) );
        assertFalse( importWizard.existsCategory( "CSV" ) );

        importWizard.clickCancelButton();
    }


    /**
     * Verify visible preference pages.
     */
    @Test
    public void testVisiblePreferencePages() throws Exception
    {
        PreferencesBot prefs = studioBot.openPreferences();
        bot.sleep( 5000 );

        assertTrue( prefs.pageExists( "Apache Directory Studio" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "Connections" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "Connections", "Certificate Validation" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "Connections", "Passwords Keystore" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Attributes" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Attributes", "Binary Attributes" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Entry Editors" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Entry Editors", "Table Entry Editor" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Search Result Editor" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Text Formats" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Value Editors" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Views" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Views", "Browser View" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Views", "Modification Logs View" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDAP Browser", "Views", "Search Logs View" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDIF Editor" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDIF Editor", "Content Assist" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDIF Editor", "Syntax Coloring" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "LDIF Editor", "Templates" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "Schema Editor" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "Schema Editor", "Hierarchy View" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "Schema Editor", "Schema View" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "Schema Editor", "Search View" ) );
        assertTrue( prefs.pageExists( "Apache Directory Studio", "Shutdown" ) );

        assertTrue( prefs.pageExists( "General", "Network Connections" ) );
        assertTrue( prefs.pageExists( "Help" ) );
        assertTrue( prefs.pageExists( "Install/Update" ) );

        prefs.clickCancelButton();
    }


    /**
     * Verify hidden preference pages. Many unwanted prefernce pages are contributed 
     * by org.eclipse.* plugins, we configured to hide them in rcp/plugin.xml. 
     */
    @Test
    public void testHiddenPreferencePages() throws Exception
    {
        PreferencesBot prefs = studioBot.openPreferences();

        assertFalse( prefs.pageExists( "Team" ) );
        assertFalse( prefs.pageExists( "Maven" ) );
        assertFalse( prefs.pageExists( "Java" ) );

        prefs.clickCancelButton();
    }

}
