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
import static org.apache.directory.server.integ.ServerIntegrationUtils.getWiredContext;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.directory.server.core.entry.DefaultServerEntry;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewEntryWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.ReferralDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the new entry wizard.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(SiRunner.class)
@CleanupLevel(Level.SUITE)
public class NewEntryWizardTest
{
    public static LdapServer ldapServer;

    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;

    private Connection connection;


    @Before
    public void setUp() throws Exception
    {
        ErrorDialog.AUTOMATED_MODE = false;

        // enable krb5kdc and nis schemas
        ApacheDsUtils.enableSchema( ldapServer, "krb5kdc" );
        ApacheDsUtils.enableSchema( ldapServer, "nis" );

        // create referral entry
        ServerEntry entry = new DefaultServerEntry( ldapServer.getDirectoryService().getRegistries() );
        entry.setDn( new LdapDN( "cn=referral,ou=system" ) );
        entry.add( "objectClass", "top", "referral", "extensibleObject" );
        entry.add( "cn", "referralDialogTest" );
        entry.add( "ref", "ldap://localhost:" + ldapServer.getPort() + "/ou=users,ou=system" );
        ldapServer.getDirectoryService().getAdminSession().add( entry );

        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connection = connectionsViewBot.createTestConnection( "NewEntryWizardTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
    }


    /**
     * Test to create a single organization entry.
     */
    @Test
    public void testCreateOrganizationEntry()
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();
        assertTrue( wizardBot.isVisible() );

        wizardBot.selectCreateEntryFromScratch();

        assertFalse( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        wizardBot.clickNextButton();

        assertTrue( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        wizardBot.addObjectClasses( "organization" );
        assertTrue( wizardBot.isObjectClassSelected( "top" ) );
        assertTrue( wizardBot.isObjectClassSelected( "organization" ) );

        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        wizardBot.clickNextButton();

        assertTrue( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        wizardBot.setRdnType( 1, "o" );
        wizardBot.setRdnValue( 1, "testCreateOrganizationEntry" );

        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        wizardBot.clickNextButton();

        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "o=testCreateOrganizationEntry" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "o=testCreateOrganizationEntry" );
    }


    /**
     * Test to create a single person entry.
     */
    @Test
    public void testCreatePersonEntry()
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "inetOrgPerson" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "cn" );
        wizardBot.setRdnValue( 1, "testCreatePersonEntry" );
        wizardBot.clickNextButton();

        wizardBot.typeValueAndFinish( "test" );
        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "cn=testCreatePersonEntry" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "cn=testCreatePersonEntry" );
    }


    /**
     * Test for DIRSTUDIO-350.
     * 
     * Create entries with upper case attribute types and ensures that the
     * retrieved entries still are in upper case.
     */
    @Test
    public void testCreateUpperCaseOrganizationEntries()
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "organization" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "O" );
        wizardBot.setRdnValue( 1, "testCreateOrganizationEntry" );
        wizardBot.clickNextButton();

        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "O=testCreateOrganizationEntry" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "O=testCreateOrganizationEntry" );

        // Now create a second entry under the previously created entry
        // to ensure that the selected parent is also upper case.

        wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "organization" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "O" );
        wizardBot.setRdnValue( 1, "testCreateOrganizationEntry2" );
        assertEquals( "O=testCreateOrganizationEntry2,O=testCreateOrganizationEntry,ou=system", wizardBot
            .getDnPreview() );
        wizardBot.clickNextButton();

        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "O=testCreateOrganizationEntry",
            "O=testCreateOrganizationEntry2" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "O=testCreateOrganizationEntry",
            "O=testCreateOrganizationEntry2" );
    }


    /**
     * Test for DIRSTUDIO-360.
     * 
     * Create entries with a slash '/' in the RDN value.
     */
    @Test
    public void testCreateEntryWithSlash()
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "krb5Principal", "person" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "krb5PrincipalName" );
        wizardBot.setRdnValue( 1, "kadmin/changepw@DOMAIN" );
        wizardBot.clickNextButton();

        wizardBot.editValue( "cn", "" );
        wizardBot.typeValueAndFinish( "test" );
        wizardBot.editValue( "sn", "" );
        wizardBot.typeValueAndFinish( "test" );
        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system",
            "krb5PrincipalName=kadmin/changepw@DOMAIN" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "krb5PrincipalName=kadmin/changepw@DOMAIN" );
    }


    /**
     * Test for DIRSTUDIO-409.
     *
     * Try to create an entry below an referral object.
     * The connection selection dialog pops up and we cancel the selection.
     * No error should occur and the wizard is not closed.
     */
    @Test
    public void testCreateEntryBelowReferralObjectCancel()
    {
        // set ManageDsaIT control
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.IGNORE.ordinal() );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "cn=referral" );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "organization" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "o" );
        wizardBot.setRdnValue( 1, "orgBelowReferral" );
        wizardBot.clickNextButton();

        ReferralDialogBot referralDialogBot = wizardBot.clickFinishButtonExpectingReferralDialog();
        assertTrue( referralDialogBot.isVisible() );

        // click cancel button, check the wizard is not closed
        referralDialogBot.clickCancelButton();
        assertTrue( wizardBot.isVisible() );
        assertTrue( wizardBot.isFinishButtonEnabled() );

        wizardBot.clickCancelButton();

        assertFalse( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "o=orgBelowReferral" ) );
    }


    /**
     * Test for DIRSTUDIO-409.
     *
     * Try to create an entry below an referral object.
     * The connection selection dialog pops up and we select a connection.
     * The entry is created under the target entry.
     */
    @Test
    public void testCreateEntryBelowReferralObjectFollow()
    {
        // set ManageDsaIT control
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.IGNORE.ordinal() );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "cn=referral" );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "organization" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "o" );
        wizardBot.setRdnValue( 1, "orgBelowReferral" );
        wizardBot.clickNextButton();

        ReferralDialogBot referralDialogBot = wizardBot.clickFinishButtonExpectingReferralDialog();
        assertTrue( referralDialogBot.isVisible() );

        // follow referral, click ok button
        referralDialogBot.selectConnection( "NewEntryWizardTest" );
        referralDialogBot.clickOkButton();

        // check entry was created under referral target entry
        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "ou=users", "o=orgBelowReferral" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "ou=users", "o=orgBelowReferral" );
    }


    /**
     * Test for DIRSTUDIO-589, DIRSTUDIO-591, DIRSHARED-38.
     * 
     * Create an entry with sharp in DN: cn=\#123456.
     */
    @Test
    public void testCreateEntryWithSharp()
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "inetOrgPerson" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "cn" );
        wizardBot.setRdnValue( 1, "#123456" );
        wizardBot.clickNextButton();

        wizardBot.typeValueAndFinish( "#123456" );
        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "cn=\\#123456" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "cn=\\#123456" );
    }


    /**
     * Test for DIRSTUDIO-603, DIRSHARED-41.
     * 
     * Create an entry with multi-valued RDN and numeric OID (IP address) in RDN value.
     */
    @Test
    public void testCreateMvRdnWithNumericOid()
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "device" );
        wizardBot.addObjectClasses( "ipHost" );
        wizardBot.clickNextButton();

        wizardBot.clickAddRdnButton( 1 );
        wizardBot.setRdnType( 1, "cn" );
        wizardBot.setRdnValue( 1, "loopback" );
        wizardBot.setRdnType( 2, "ipHostNumber" );
        wizardBot.setRdnValue( 2, "127.0.0.1" );
        wizardBot.clickNextButton();

        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "cn=loopback+ipHostNumber=127.0.0.1" ) );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "cn=loopback+ipHostNumber=127.0.0.1" );
    }
}
