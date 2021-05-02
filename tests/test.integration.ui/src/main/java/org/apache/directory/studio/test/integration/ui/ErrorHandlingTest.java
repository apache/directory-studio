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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests error handling
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ErrorHandlingTest extends AbstractTestBase
{

    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "ApacheDS specific test")
    public void testDeleteObjectClassTopSchemaEntryShouldFail( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
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


    @ParameterizedTest
    @LdapServersSource
    public void testDeleteObjectClassAttributeShouldFail( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( ( USER1_DN ) ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        ErrorDialogBot errorDialog = entryEditorBot.deleteValueExpectingErrorDialog( "objectClass",
            "inetOrgPerson (structural)" );

        String expectedError = "65 - objectClassViolation";
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            expectedError = "69 - objectClassModsProhibited";
        }

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(), containsString( "[LDAP result code " + expectedError + "]" ) );
        errorDialog.clickOkButton();

        modificationLogsViewBot.assertContainsError( "[LDAP result code " + expectedError + "]",
            "dn: " + USER1_DN.getName(), "changetype: modify" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testDeleteRdnAttributeShouldFail( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( ( USER1_DN ) ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        ErrorDialogBot errorDialog = entryEditorBot.deleteValueExpectingErrorDialog( "uid", "user.1" );

        String expectedError = "67 - notAllowedOnRDN";
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            expectedError = "64 - namingViolation";
        }

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(), containsString( "[LDAP result code " + expectedError + "]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code " + expectedError + "]",
            "dn: " + USER1_DN.getName(), "changetype: modify", "delete: uid" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testDeleteMustAttributeShouldFail( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( ( USER1_DN ) ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        ErrorDialogBot errorDialog = entryEditorBot.deleteValueExpectingErrorDialog( "sn", "Amar" );

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(), containsString( "[LDAP result code 65 - objectClassViolation]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 65 - objectClassViolation]",
            "dn: " + USER1_DN.getName(), "changetype: modify", "delete: sn" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testDeleteOperationalAttributeShouldFail( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( ( USER1_DN ) ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.fetchOperationalAttributes();
        SWTUtils.sleep( 1000 );
        entryEditorBot.activate();
        ErrorDialogBot errorDialog = entryEditorBot.deleteValueExpectingErrorDialog( "creatorsName", null );

        String expectedError = "50 - insufficientAccessRights";
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            expectedError = "19 - constraintViolation";
        }
        if ( server.getType() == LdapServerType.Fedora389ds )
        {
            expectedError = "53 - unwillingToPerform";
        }

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(),
            containsString( "[LDAP result code " + expectedError + "]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code " + expectedError + "]",
            "dn: " + USER1_DN.getName(), "changetype: modify", "delete: creatorsName" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testModifyInvalidSyntaxShouldFail( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( ( USER1_DN ) ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.editValue( "mail", null );
        ErrorDialogBot errorDialog = entryEditorBot.typeValueAndFinishAndExpectErrorDialog( "äöüß" );

        // verify message in error dialog
        assertThat( errorDialog.getErrorMessage(),
            containsString( "[LDAP result code 21 - invalidAttributeSyntax]" ) );
        errorDialog.clickOkButton();

        // verify in modification logs
        modificationLogsViewBot.assertContainsError( "[LDAP result code 21 - invalidAttributeSyntax]",
            "dn: " + USER1_DN.getName(), "changetype: modify", "delete: mail" );
    }


    @Disabled("Until DIRSERVER-2308 is fixed")
    @ParameterizedTest
    @LdapServersSource
    public void testRenameAlreadyExistingEntry( TestLdapServer server ) throws Exception
    {
    }


    @Disabled("Until DIRSERVER-2308 is fixed")
    @ParameterizedTest
    @LdapServersSource
    public void testMoveAlreadyExistingEntry( TestLdapServer server ) throws Exception
    {
    }

}
