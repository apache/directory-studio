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


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.ldap.handlers.extended.PwdModifyHandler;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ModificationLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests error handling
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") }, extendedOpHandlers =
    { PwdModifyHandler.class })
@ApplyLdifFiles(clazz = ErrorHandlingTest.class, value = "org/apache/directory/studio/test/integration/ui/BrowserTest.ldif")
public class ErrorHandlingTest extends AbstractLdapTestUnit
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
        connectionsViewBot.createTestConnection( "BrowserTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
        modificationLogsViewBot = studioBot.getModificationLogsViewBot();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();
    }


    @Test
    public void testDeleteObjectClassTopSchemaEntryShouldFail() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=schema", "cn=system", "ou=objectClasses", "m-oid=2.5.6.0" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=schema", "cn=system", "ou=objectClasses", "m-oid=2.5.6.0" );
        DeleteDialogBot deleteDialog = browserViewBot.openDeleteDialog();
        ErrorDialogBot errorDialog = deleteDialog.clickOkButtonExpectingErrorDialog();

        // verify message in error dialog
        assertThat( errorDialog.getErrorDetails(), containsString( "[LDAP result code 53 - unwillingToPerform]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 53 - unwillingToPerform]",
            "dn: m-oid=2.5.6.0,ou=objectClasses,cn=system,ou=schema", "changetype: delete" );
    }


    @Test
    public void testDeleteObjectClassAttributeShouldFail() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,ou=system" );
        entryEditorBot.activate();
        ErrorDialogBot errorDialog = entryEditorBot.deleteValueExpectingErrorDialog( "objectclass",
            "inetOrgPerson (structural)" );

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(), containsString( "[LDAP result code 65 - objectClassViolation]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 65 - objectClassViolation]",
            "dn: uid=user.1,ou=users,ou=system", "changetype: modify" );
    }


    @Test
    public void testDeleteRdnAttributeShouldFail() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,ou=system" );
        entryEditorBot.activate();
        ErrorDialogBot errorDialog = entryEditorBot.deleteValueExpectingErrorDialog( "uid", "user.1" );

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(), containsString( "[LDAP result code 67 - notAllowedOnRDN]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 67 - notAllowedOnRDN]",
            "dn: uid=user.1,ou=users,ou=system", "changetype: modify", "delete: uid" );
    }


    @Test
    public void testDeleteMustAttributeShouldFail() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,ou=system" );
        entryEditorBot.activate();
        ErrorDialogBot errorDialog = entryEditorBot.deleteValueExpectingErrorDialog( "sn", "Amar" );

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(), containsString( "[LDAP result code 65 - objectClassViolation]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 65 - objectClassViolation]",
            "dn: uid=user.1,ou=users,ou=system", "changetype: modify", "delete: sn" );
    }


    @Test
    public void testDeleteOperationalAttributeShouldFail() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,ou=system" );
        entryEditorBot.activate();
        entryEditorBot.fetchOperationalAttributes();
        SWTUtils.sleep( 1000 );
        entryEditorBot.activate();
        ErrorDialogBot errorDialog = entryEditorBot.deleteValueExpectingErrorDialog( "nbChildren", "0" );

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(),
            containsString( "[LDAP result code 50 - insufficientAccessRights]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 50 - insufficientAccessRights]",
            "dn: uid=user.1,ou=users,ou=system", "changetype: modify", "delete: nbChildren" );
    }


    @Test
    public void testModifyInvalidSyntaxShouldFail() throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "uid=user.1" );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "uid=user.1,ou=users,ou=system" );
        entryEditorBot.activate();
        entryEditorBot.editValue( "telephonenumber", "976-893-3312" );
        ErrorDialogBot errorDialog = entryEditorBot.typeValueAndFinishAndExpectErrorDialog( "Invalid phone number" );

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(),
            containsString( "[LDAP result code 21 - invalidAttributeSyntax]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 21 - invalidAttributeSyntax]",
            "dn: uid=user.1,ou=users,ou=system", "changetype: modify", "delete: telephonenumber",
            "telephonenumber: 976-893-3312", "-", "add: telephonenumber",
            "telephonenumber: Invalid phone number" );
    }


    @Ignore("Until DIRSERVER-2308 is fixed")
    @Test
    public void testRenameAlreadyExistingEntry() throws Exception
    {
    }


    @Ignore("Until DIRSERVER-2308 is fixed")
    @Test
    public void testMoveAlreadyExistingEntry() throws Exception
    {
    }

}
