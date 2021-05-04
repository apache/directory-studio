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
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRALS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_LOOP_1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_LOOP_2_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_MISC_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_REFERRALS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_REFERRAL_TO_USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USERS_DN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.ReferralDialogBot;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the referral dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReferralDialogTest extends AbstractTestBase
{

    /**
     * Test for DIRSTUDIO-343.
     *
     * Follows a continuation reference.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBrowseAndFollowContinuationReference( TestLdapServer server ) throws Exception
    {
        // ensure that referrals handling method is FOLLOW
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.FOLLOW.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW.ordinal(), referralsHandlingMethodOrdinal );

        // expand ou=referrals, that reads the referrals and opens the referral dialog
        ReferralDialogBot referralDialogBot = browserViewBot.expandEntryExpectingReferralDialog( path( REFERRALS_DN ) );
        assertTrue( referralDialogBot.isVisible() );
        assertEquals( connection.getName(), referralDialogBot.getSelectedConnection() );
        referralDialogBot.clickOkButton();

        // ensure that the continuation URLs are visible and can be expanded, but not the referrals entries
        assertReferralEntriesAreNotVisible();
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, USER1_DN ) ) );
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, USERS_DN ) ) );
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, USERS_DN ) );
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, REFERRALS_DN ) ) );
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, REFERRALS_DN ) );
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, MISC_DN ) ) );
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, MISC_DN ) );
    }


    /**
     * Test for DIRSTUDIO-343.
     *
     * Does not follow a continuation reference by clicking the cancel button in
     * the referral dialog.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBrowseAndCancelFollowingContinuationReference( TestLdapServer server ) throws Exception
    {
        // ensure that referrals handling method is FOLLOW
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.FOLLOW.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW.ordinal(), referralsHandlingMethodOrdinal );

        // expand ou=referrals, that reads the referral and opens the referral dialog
        ReferralDialogBot referralDialogBot = browserViewBot.expandEntryExpectingReferralDialog( path( REFERRALS_DN ) );
        assertTrue( referralDialogBot.isVisible() );
        assertEquals( connection.getName(), referralDialogBot.getSelectedConnection() );
        referralDialogBot.clickCancelButton();

        // ensure that neither the continuation URLs, nor the referral entries are visible
        assertReferralEntriesAreNotVisible();
        assertRefLdapUrlsAreNotVisible( server );
    }


    /**
     * Tests ignore referral by setting the connection property to IGNORE.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBrowseAndIgnoreReferral( TestLdapServer server ) throws Exception
    {
        // ensure that referrals handling method is IGNORE
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.IGNORE.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.IGNORE.ordinal(), referralsHandlingMethodOrdinal );

        // expand ou=referrals, no referral dialog expected
        browserViewBot.expandEntry( path( REFERRALS_DN ) );

        // ensure that neither the continuation URLs, nor the referral entries are visible
        assertReferralEntriesAreNotVisible();
        assertRefLdapUrlsAreNotVisible( server );
    }


    /**
     * Tests manage referral entry by setting the ManageDsaIT control.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBrowseAndManageReferralEntry( TestLdapServer server ) throws Exception
    {
        // ensure that ManageDsaIT is set
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.IGNORE.ordinal() );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        boolean manageDsaIT = connection.getConnectionParameter().getExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT );
        assertEquals( ReferralHandlingMethod.IGNORE.ordinal(), referralsHandlingMethodOrdinal );
        assertTrue( manageDsaIT );

        // expand ou=referrals, that reads the referral object
        browserViewBot.expandEntry( path( REFERRALS_DN ) );

        // ensure that the referral entries are visible, but not the continuation URLs
        assertRefLdapUrlsAreNotVisible( server );
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_TO_USER1_DN ) ) );
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_TO_USERS_DN ) ) );
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_TO_REFERRAL_TO_USERS_DN ) ) );
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_TO_REFERRALS_DN ) ) );
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_LOOP_1_DN ) ) );
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_LOOP_2_DN ) ) );
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_TO_MISC_DN ) ) );
    }


    /**
     * Tests manual referral following.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBrowseAndFollowManuallyContinuationReference( TestLdapServer server ) throws Exception
    {
        // ensure that referrals handling method is FOLLOW_MANUALLY
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.FOLLOW_MANUALLY.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW_MANUALLY.ordinal(), referralsHandlingMethodOrdinal );

        // expand ou=referrals, no referral dialog expected yet
        browserViewBot.expandEntry( path( REFERRALS_DN ) );

        // ensure that only the referral targets are visible, not the referrals
        assertReferralEntriesAreNotVisible();
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, USER1_DN ) ) );
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, USERS_DN ) ) );
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, REFERRAL_TO_USERS_DN ) ) );
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, REFERRALS_DN ) ) );
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, REFERRAL_LOOP_1_DN ) ) );
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, REFERRAL_LOOP_2_DN ) ) );
        assertTrue( browserViewBot.existsEntry( pathWithRefLdapUrl( server, MISC_DN ) ) );

        // select one target, that should popup the referral dialog
        ReferralDialogBot referralDialogBot = browserViewBot
            .selectEntryExpectingReferralDialog( pathWithRefLdapUrl( server, USER1_DN ) );
        assertTrue( referralDialogBot.isVisible() );
        assertEquals( connection.getName(), referralDialogBot.getSelectedConnection() );
        referralDialogBot.clickOkButton();

        // now all ref URLs can be expanded, no additional referral dialog is expected
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, USER1_DN ) );
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, USERS_DN ) );
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, REFERRAL_TO_USERS_DN ) );
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, REFERRALS_DN ) );
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, REFERRAL_LOOP_1_DN ) );
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, REFERRAL_LOOP_2_DN ) );
        browserViewBot.expandEntry( pathWithRefLdapUrl( server, MISC_DN ) );
    }


    private void assertRefLdapUrlsAreNotVisible( TestLdapServer server )
    {
        assertFalse( browserViewBot.existsEntry( pathWithRefLdapUrl( server, USER1_DN ) ) );
        assertFalse( browserViewBot.existsEntry( pathWithRefLdapUrl( server, USERS_DN ) ) );
        assertFalse( browserViewBot.existsEntry( pathWithRefLdapUrl( server, REFERRAL_TO_USERS_DN ) ) );
        assertFalse( browserViewBot.existsEntry( pathWithRefLdapUrl( server, REFERRALS_DN ) ) );
        assertFalse( browserViewBot.existsEntry( pathWithRefLdapUrl( server, REFERRAL_LOOP_1_DN ) ) );
        assertFalse( browserViewBot.existsEntry( pathWithRefLdapUrl( server, REFERRAL_LOOP_2_DN ) ) );
        assertFalse( browserViewBot.existsEntry( pathWithRefLdapUrl( server, MISC_DN ) ) );
    }


    private void assertReferralEntriesAreNotVisible()
    {
        assertFalse( browserViewBot.existsEntry( path( REFERRAL_TO_USER1_DN ) ) );
        assertFalse( browserViewBot.existsEntry( path( REFERRAL_TO_USERS_DN ) ) );
        assertFalse( browserViewBot.existsEntry( path( REFERRAL_TO_REFERRAL_TO_USERS_DN ) ) );
        assertFalse( browserViewBot.existsEntry( path( REFERRAL_TO_REFERRALS_DN ) ) );
        assertFalse( browserViewBot.existsEntry( path( REFERRAL_LOOP_1_DN ) ) );
        assertFalse( browserViewBot.existsEntry( path( REFERRAL_LOOP_2_DN ) ) );
        assertFalse( browserViewBot.existsEntry( path( REFERRAL_TO_MISC_DN ) ) );
    }

}