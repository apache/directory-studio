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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.MISC_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_MISC_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.TARGET_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.dn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.CertificateEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.DnEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EditAttributeWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.HexEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewAttributeWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.NewEntryWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.ReferralDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SubtreeSpecificationEditorDialogBot;
import org.apache.directory.studio.test.integration.ui.utils.ResourceUtils;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the new entry wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewEntryWizardTest extends AbstractTestBase
{

    /**
     * Test to create a single organization entry.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCreateOrganizationEntry( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( TARGET_DN ) );

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

        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "o=testCreateOrganizationEntry" ) ) );
        browserViewBot.selectEntry( path( TARGET_DN, "o=testCreateOrganizationEntry" ) );
    }


    /**
     * Test to create a single person entry.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCreatePersonEntry( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( TARGET_DN ) );

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

        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "cn=testCreatePersonEntry" ) ) );
        browserViewBot.selectEntry( path( TARGET_DN, "cn=testCreatePersonEntry" ) );
    }


    /**
     * Test for DIRSTUDIO-350.
     *
     * Create entries with upper case attribute types and ensures that the
     * retrieved entries still are in upper case.
     */
    @ParameterizedTest
    @LdapServersSource(types =
        { LdapServerType.ApacheDS, LdapServerType.Fedora389ds })
    public void testCreateUpperCaseOrganizationEntries( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( TARGET_DN ) );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "organization" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "O" );
        wizardBot.setRdnValue( 1, "testCreateOrganizationEntry" );
        wizardBot.clickNextButton();

        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "O=testCreateOrganizationEntry" ) ) );
        browserViewBot.selectEntry( path( TARGET_DN, "O=testCreateOrganizationEntry" ) );

        // Now create a second entry under the previously created entry
        // to ensure that the selected parent is also upper case.

        wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "organization" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "O" );
        wizardBot.setRdnValue( 1, "testCreateOrganizationEntry2" );
        assertEquals( "O=testCreateOrganizationEntry2,O=testCreateOrganizationEntry," + TARGET_DN.getName(),
            wizardBot.getDnPreview() );
        wizardBot.clickNextButton();

        wizardBot.clickFinishButton();

        assertTrue( browserViewBot
            .existsEntry( path( TARGET_DN, "O=testCreateOrganizationEntry", "O=testCreateOrganizationEntry2" ) ) );
        browserViewBot
            .selectEntry( path( TARGET_DN, "O=testCreateOrganizationEntry", "O=testCreateOrganizationEntry2" ) );
    }


    /**
     * Test for DIRSTUDIO-360.
     *
     * Create entries with a slash '/' in the RDN value.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCreateEntryWithSlash( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( TARGET_DN ) );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "person" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "cn" );
        wizardBot.setRdnValue( 1, "kadmin/changepw@DOMAIN" );
        wizardBot.clickNextButton();

        wizardBot.editValue( "sn", "" );
        wizardBot.typeValueAndFinish( "test" );
        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "cn=kadmin/changepw@DOMAIN" ) ) );
        browserViewBot.selectEntry( path( TARGET_DN, "cn=kadmin/changepw@DOMAIN" ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCreateAliasEntry( TestLdapServer server ) throws Exception
    {
        // disable alias dereferencing
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            AliasDereferencingMethod.NEVER.ordinal() );

        browserViewBot.selectEntry( path( TARGET_DN ) );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "alias", "extensibleObject" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "cn" );
        wizardBot.setRdnValue( 1, "alias" );
        wizardBot.clickNextButton();

        DnEditorDialogBot dnEditorBot = new DnEditorDialogBot();
        dnEditorBot.setDnText( USER1_DN.getName() );
        dnEditorBot.clickOkButton();
        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "cn=alias" ) ) );
        browserViewBot.selectEntry( path( TARGET_DN, "cn=alias" ) );
    }


    @ParameterizedTest
    @LdapServersSource(types = LdapServerType.ApacheDS)
    public void testCreateSubEntry( TestLdapServer server ) throws Exception
    {
        // set Subentries control
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES, true );

        browserViewBot.selectEntry( path( TARGET_DN ) );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "subentry" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "cn" );
        wizardBot.setRdnValue( 1, "subentry" );
        wizardBot.clickNextButton();

        SubtreeSpecificationEditorDialogBot subtreeEditorBot = new SubtreeSpecificationEditorDialogBot();
        subtreeEditorBot.clickOkButton();
        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "cn=subentry" ) ) );
        browserViewBot.selectEntry( path( TARGET_DN, "cn=subentry" ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCreateReferralEntry( TestLdapServer server ) throws Exception
    {
        // set ManageDsaIT control
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.IGNORE.ordinal() );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );

        browserViewBot.selectEntry( path( TARGET_DN ) );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "referral", "extensibleObject" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "cn" );
        wizardBot.setRdnValue( 1, "referral" );
        wizardBot.clickNextButton();

        // 389ds doesn't define ref as mandatory attribute
        if ( server.getType() == LdapServerType.Fedora389ds )
        {
            NewAttributeWizardBot newAttributeWizardBot = wizardBot.openNewAttributeWizard();
            assertTrue( newAttributeWizardBot.isVisible() );
            newAttributeWizardBot.typeAttributeType( "ref" );
            newAttributeWizardBot.clickFinishButton();
            wizardBot.cancelEditValue();
            wizardBot.activate();
        }
        wizardBot.editValue( "ref", "" );

        wizardBot.typeValueAndFinish( server.getLdapUrl() + "/" + USER1_DN.getName() );
        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "cn=referral" ) ) );
        browserViewBot.selectEntry( path( TARGET_DN, "cn=referral" ) );
    }


    /**
     * Test for DIRSTUDIO-409.
     *
     * Try to create an entry below an referral object.
     * The connection selection dialog pops up and we cancel the selection.
     * No error should occur and the wizard is not closed.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCreateEntryBelowReferralObjectCancel( TestLdapServer server ) throws Exception
    {
        // set ManageDsaIT control
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.IGNORE.ordinal() );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );

        browserViewBot.selectEntry( path( REFERRAL_TO_MISC_DN ) );

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
        // timing issues, use ugly sleep for now, should use some condition but have no idea.
        SWTUtils.sleep( 1000 );
        assertTrue( wizardBot.isVisible() );
        assertTrue( wizardBot.isFinishButtonEnabled() );

        wizardBot.clickCancelButton();

        assertFalse( browserViewBot.existsEntry( path( MISC_DN, "o=orgBelowReferral" ) ) );
    }


    /**
     * Test for DIRSTUDIO-409.
     *
     * Try to create an entry below an referral object.
     * The connection selection dialog pops up and we select a connection.
     * The entry is created under the target entry.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCreateEntryBelowReferralObjectFollow( TestLdapServer server ) throws Exception
    {
        // set ManageDsaIT control
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.IGNORE.ordinal() );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );

        browserViewBot.selectEntry( path( REFERRAL_TO_MISC_DN ) );

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
        referralDialogBot.selectConnection( connection.getName() );
        referralDialogBot.clickOkButton();

        // check entry was created under referral target entry
        assertTrue( browserViewBot.existsEntry( path( MISC_DN, "o=orgBelowReferral" ) ) );
        browserViewBot.selectEntry( path( MISC_DN, "o=orgBelowReferral" ) );
    }


    /**
     * Test for DIRSTUDIO-589, DIRSTUDIO-591, DIRSHARED-38.
     *
     * Create an entry with sharp in DN: cn=\#123456.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCreateEntryWithSharp( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( TARGET_DN ) );

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

        if ( server.getType() == LdapServerType.ApacheDS )
        {
            assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "cn=\\#123456" ) ) );
            browserViewBot.selectEntry( path( TARGET_DN, "cn=\\#123456" ) );
        }
        else
        {
            assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "cn=\\23123456" ) ) );
            browserViewBot.selectEntry( path( TARGET_DN, "cn=\\23123456" ) );
        }
    }


    /**
     * Test for DIRSTUDIO-603, DIRSHARED-41.
     *
     * Create an entry with multi-valued RDN and numeric OID (IP address) in RDN value.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCreateMvRdnWithNumericOid( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( TARGET_DN ) );

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

        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "cn=loopback+ipHostNumber=127.0.0.1" ) ) );
        browserViewBot.selectEntry( path( TARGET_DN, "cn=loopback+ipHostNumber=127.0.0.1" ) );
    }


    /**
     * Test for DIRSTUDIO-987, DIRSTUDIO-271.
     *
     * Create and browse entry with multi-valued RDN with same attribute type.
     */
    @ParameterizedTest
    @LdapServersSource(types =
        { LdapServerType.ApacheDS, LdapServerType.Fedora389ds })
    // Multi-valued RDN with same attribute is not supported by OpenLDAP
    public void testCreateMvRdnWithSameAttribute( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( TARGET_DN ) );

        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "locality" );
        wizardBot.clickNextButton();

        wizardBot.clickAddRdnButton( 1 );
        wizardBot.clickAddRdnButton( 2 );
        wizardBot.setRdnType( 1, "l" );
        wizardBot.setRdnValue( 1, "eu" );
        wizardBot.setRdnType( 2, "l" );
        wizardBot.setRdnValue( 2, "de" );
        wizardBot.setRdnType( 3, "l" );
        wizardBot.setRdnValue( 3, "Berlin" );
        wizardBot.clickNextButton();

        wizardBot.clickFinishButton();

        if ( server.getType() == LdapServerType.Fedora389ds )
        {
            // 389ds sorts the RDN
            assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "l=Berlin+l=de+l=eu" ) ) );
            browserViewBot.selectEntry( path( TARGET_DN, "l=Berlin+l=de+l=eu" ) );
        }
        else
        {
            assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "l=eu+l=de+l=Berlin" ) ) );
            browserViewBot.selectEntry( path( TARGET_DN, "l=eu+l=de+l=Berlin" ) );
        }
    }


    /**
     * DIRSTUDIO-1267: Test creation of new entry with binary option and language tags.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCreateEntryWithBinaryOptionAndLanguageTags( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        String certFile = ResourceUtils.prepareInputFile( "rfc5280_cert1.cer" );
        String crlFile = ResourceUtils.prepareInputFile( "rfc5280_crl.crl" );

        browserViewBot.selectEntry( path( TARGET_DN ) );

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

        assertTrue( browserViewBot.existsEntry( path( TARGET_DN, "cn=asdf" ) ) );
        browserViewBot.selectEntry( path( TARGET_DN, "cn=asdf" ) );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( dn( "cn=asdf", TARGET_DN ).getName() );
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
    }
}
