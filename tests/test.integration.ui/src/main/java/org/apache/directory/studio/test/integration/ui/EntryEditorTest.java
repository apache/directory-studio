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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.eclipse.swtbot.swt.finder.SWTBotTestCase.assertContains;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DnEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ModificationLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewAttributeWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.SelectDnDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles(
    { "org/apache/directory/studio/test/integration/ui/EntryEditorTest.ldif" })
public class EntryEditorTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;
    private ModificationLogsViewBot modificationLogsViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "EntryEditorTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
        modificationLogsViewBot = studioBot.getModificationLogsViewBot();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
    }


    /**
     * Test adding, editing and deleting of attributes in the entry editor.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testAddEditDeleteAttribute() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=Barbara Jensen,ou=users,ou=system" );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: cn=Barbara Jensen,ou=users,ou=system", dn );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertEquals( "", modificationLogsViewBot.getModificationLogsText() );

        // add description attribute
        entryEditorBot.activate();
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "description" );
        wizardBot.clickFinishButton();
        entryEditorBot.typeValueAndFinish( "This is the 1st description." );
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        assertContains( "add: description\ndescription: This is the 1st description.",
            modificationLogsViewBot.getModificationLogsText() );

        // add second value
        entryEditorBot.activate();
        entryEditorBot.addValue( "description" );
        entryEditorBot.typeValueAndFinish( "This is the 2nd description." );
        assertEquals( 10, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 2nd description." ) );
        assertContains( "add: description\ndescription: This is the 2nd description.",
            modificationLogsViewBot.getModificationLogsText() );

        // edit second value
        entryEditorBot.editValue( "description", "This is the 2nd description." );
        entryEditorBot.typeValueAndFinish( "This is the 3rd description." );
        assertEquals( 10, entryEditorBot.getAttributeValues().size() );
        assertEquals( 10, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: This is the 2nd description." ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 3rd description." ) );
        assertContains( "delete: description\ndescription: This is the 2nd description.",
            modificationLogsViewBot.getModificationLogsText() );
        assertContains( "add: description\ndescription: This is the 3rd description.",
            modificationLogsViewBot.getModificationLogsText() );

        // delete second value
        entryEditorBot.deleteValue( "description", "This is the 3rd description." );
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: This is the 3rd description." ) );
        assertContains( "delete: description\ndescription: This is the 3rd description.",
            modificationLogsViewBot.getModificationLogsText() );

        // edit 1st value
        entryEditorBot.editValue( "description", "This is the 1st description." );
        entryEditorBot.typeValueAndFinish( "This is the final description." );
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: This is the 1st description." ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "description: This is the final description." ) );
        assertContains( "replace: description\ndescription: This is the final description.",
            modificationLogsViewBot.getModificationLogsText() );

        // delete 1st value/attribute
        entryEditorBot.deleteValue( "description", "This is the final description." );
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );
        assertFalse( entryEditorBot.getAttributeValues().contains( "description: This is the final description." ) );
        assertContains( "delete: description\n-", modificationLogsViewBot.getModificationLogsText() );

        assertEquals( "Expected 6 modifications.", 6,
            StringUtils.countMatches( modificationLogsViewBot.getModificationLogsText(), "#!RESULT OK" ) );
    }


    /**
     * DIRSTUDIO-483: DN Editor escapes all non-ascii characters
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDnValueEditor() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=groups", "cn=My Group" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=My Group,ou=groups,ou=system" );
        entryEditorBot.activate();
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: cn=My Group,ou=groups,ou=system", dn );
        assertEquals( 4, entryEditorBot.getAttributeValues().size() );

        // add member attribute
        NewAttributeWizardBot wizardBot = entryEditorBot.openNewAttributeWizard();
        assertTrue( wizardBot.isVisible() );
        wizardBot.typeAttributeType( "member" );
        DnEditorDialogBot dnEditorBot = wizardBot.clickFinishButtonExpectingDnEditor();
        assertTrue( dnEditorBot.isVisible() );
        SelectDnDialogBot selectDnBot = dnEditorBot.clickBrowseButtonExpectingSelectDnDialog();
        assertTrue( selectDnBot.isVisible() );
        selectDnBot.selectEntry( "Root DSE", "ou=system", "ou=users", "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );
        selectDnBot.clickOkButton();
        assertEquals( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system", dnEditorBot.getDnText() );
        dnEditorBot.clickOkButton();

        // assert value after saved and reloaded from server
        SWTUtils.sleep( 1000 );
        assertEquals( 5, entryEditorBot.getAttributeValues().size() );
        assertTrue( entryEditorBot.getAttributeValues().contains(
            "member: cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" ) );
        dnEditorBot = entryEditorBot.editValueExpectingDnEditor( "member",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" );
        assertEquals( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system", dnEditorBot.getDnText() );
        dnEditorBot.clickCancelButton();

        assertEquals( "Expected 1 modification.", 1,
            StringUtils.countMatches( modificationLogsViewBot.getModificationLogsText(), "#!RESULT OK" ) );
    }


    /**
     * DIRSTUDIO-637: copy/paste of attributes no longer works.
     * Test copy/paste within entry editor.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testCopyPasteIn() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=Barbara Jensen" );

        // copy a value
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=Barbara Jensen,ou=users,ou=system" );
        entryEditorBot.activate();
        entryEditorBot.copyValue( "uid", "bjensen" );

        // go to another entry
        browserViewBot
            .selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );
        entryEditorBot = studioBot.getEntryEditorBot( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" );
        entryEditorBot.activate();
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );

        // paste value, wait till job is done
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__execute_ldif_name );
        entryEditorBot.pasteValue();
        watcher.waitUntilDone();

        // assert pasted value visible in editor
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        entryEditorBot.getAttributeValues().contains( "uid: bjensen" );

        // assert pasted value was written to directory
        Entry entry = ldapServer.getDirectoryService().getAdminSession()
            .lookup( new Dn( "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system" ) );
        assertTrue( entry.contains( "uid", "bjensen" ) );
    }

}
