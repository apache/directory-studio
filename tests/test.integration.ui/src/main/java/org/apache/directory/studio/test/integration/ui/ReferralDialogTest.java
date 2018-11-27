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


import static org.apache.directory.studio.test.integration.ui.Constants.LOCALHOST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ReferralDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the referral dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
public class ReferralDialogTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;

    private Connection connection;

    private String[] parentPath;
    private String[] referralPath;
    private String[] targetPath;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connection = connectionsViewBot.createTestConnection( "ReferralDialogTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();

        // create referral entry
        Entry entry = new DefaultEntry( service.getSchemaManager() );
        entry.setDn( new Dn( "cn=referralDialogTest,ou=system" ) );
        entry.add( "objectClass", "top", "referral", "extensibleObject" );
        entry.add( "cn", "referralDialogTest" );
        entry.add( "ref", "ldap://" + LOCALHOST + ":" + ldapServer.getPort() + "/ou=users,ou=system" );
        service.getAdminSession().add( entry );

        // get paths
        parentPath = new String[]
            { "DIT", "Root DSE", "ou=system" };
        referralPath = new String[]
            { "DIT", "Root DSE", "ou=system", "cn=referralDialogTest" };
        targetPath = new String[]
            { "DIT", "Root DSE", "ou=system",
                "ldap://" + LOCALHOST + ":" + ldapServer.getPort() + "/ou=users,ou=system" };
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.closeSelectedConnections();
        connectionsViewBot.deleteTestConnections();
    }


    /**
     * Test for DIRSTUDIO-343.
     *
     * Follows a continuation reference.
     */
    @Test
    public void testBrowseAndFollowContinuationReference()
    {
        // ensure that referrals handling method is FOLLOW
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.FOLLOW.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW.ordinal(), referralsHandlingMethodOrdinal );

        // expand ou=system, that reads the referral and opens the referral dialog
        ReferralDialogBot referralDialogBot = browserViewBot.expandEntryExpectingReferralDialog( parentPath );
        assertTrue( referralDialogBot.isVisible() );
        assertEquals( connection.getName(), referralDialogBot.getSelectedConnection() );
        referralDialogBot.clickOkButton();

        // ensure that the target exists, the referral is not visible
        assertFalse( browserViewBot.existsEntry( referralPath ) );
        assertTrue( browserViewBot.existsEntry( targetPath ) );
        browserViewBot.selectEntry( targetPath );
    }


    /**
     * Test for DIRSTUDIO-343.
     *
     * Does not follow a continuation reference by clicking the cancel button in
     * the referral dialog.
     */
    @Test
    public void testBrowseAndCancelFollowingContinuationReference()
    {
        // ensure that referrals handling method is FOLLOW
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.FOLLOW.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW.ordinal(), referralsHandlingMethodOrdinal );

        // expand ou=system, that reads the referral and opens the referral dialog
        ReferralDialogBot referralDialogBot = browserViewBot.expandEntryExpectingReferralDialog( parentPath );
        assertTrue( referralDialogBot.isVisible() );
        assertEquals( connection.getName(), referralDialogBot.getSelectedConnection() );
        referralDialogBot.clickCancelButton();

        // ensure that neither the target, nor the referral exist
        assertFalse( browserViewBot.existsEntry( referralPath ) );
        assertFalse( browserViewBot.existsEntry( targetPath ) );
    }


    /**
     * Tests ignore referral by setting the connection property to IGNORE.
     */
    @Test
    public void testBrowseAndIgnoreReferral()
    {
        // ensure that referrals handling method is IGNORE
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.IGNORE.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.IGNORE.ordinal(), referralsHandlingMethodOrdinal );

        // expand ou=system, no referral dialog expected
        browserViewBot.expandEntry( parentPath );

        // ensure that neither the target, nor the referral exist
        assertFalse( browserViewBot.existsEntry( referralPath ) );
        assertFalse( browserViewBot.existsEntry( targetPath ) );
    }


    /**
     * Tests manage referral entry by setting the ManageDsaIT control.
     */
    @Test
    public void testBrowseAndManageReferralEntry()
    {
        // ensure that ManageDsaIT is set
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.IGNORE.ordinal() );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        boolean manageDsaIT = connection.getConnectionParameter().getExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT );
        assertEquals( ReferralHandlingMethod.IGNORE.ordinal(), referralsHandlingMethodOrdinal );
        assertTrue( manageDsaIT );

        // expand ou=system, that reads the referral object
        browserViewBot.expandEntry( parentPath );

        // ensure that the referral entry exists
        assertTrue( browserViewBot.existsEntry( referralPath ) );
        assertFalse( browserViewBot.existsEntry( targetPath ) );
        browserViewBot.selectEntry( referralPath );
    }


    /**
     * Tests manual referral following.
     */
    @Test
    public void testBrowseAndFollowManuallyContinuationReference()
    {
        // ensure that referrals handling method is FOLLOW_MANUALLY
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.FOLLOW_MANUALLY.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW_MANUALLY.ordinal(), referralsHandlingMethodOrdinal );

        // expand ou=system, no referral dialog expected yet
        browserViewBot.expandEntry( parentPath );

        // ensure that the target exists, the referral is not visible
        assertFalse( browserViewBot.existsEntry( referralPath ) );
        assertTrue( browserViewBot.existsEntry( targetPath ) );

        // select the target, that should popup the referral dialog
        ReferralDialogBot referralDialogBot = browserViewBot.selectEntryExpectingReferralDialog( targetPath );
        assertTrue( referralDialogBot.isVisible() );
        assertEquals( connection.getName(), referralDialogBot.getSelectedConnection() );
        referralDialogBot.clickOkButton();

        // ensure that the target exists, the referral is not visible
        assertFalse( browserViewBot.existsEntry( referralPath ) );
        assertTrue( browserViewBot.existsEntry( targetPath ) );
        browserViewBot.selectEntry( targetPath );
    }
}