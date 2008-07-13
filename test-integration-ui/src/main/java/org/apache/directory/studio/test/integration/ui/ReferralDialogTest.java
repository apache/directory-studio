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


import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import net.sf.swtbot.eclipse.finder.SWTEclipseBot;
import net.sf.swtbot.finder.UIThreadRunnable;
import net.sf.swtbot.widgets.SWTBotTree;
import net.sf.swtbot.widgets.SWTBotTreeItem;

import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * Tests the referral dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReferralDialogTest extends AbstractServerTest
{
    private SWTEclipseBot bot;
    private Connection connection;


    protected void setUp() throws Exception
    {
        super.setUp();
        bot = new SWTEclipseBot();
        SWTBotUtils.openLdapPerspective( bot );
        connection = SWTBotUtils.createTestConnection( bot, "ReferralDialogTest", ldapServer.getIpPort() );
    }


    protected void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
        super.tearDown();
    }


    /**
     * Test for DIRSTUDIO-343.
     * 
     * Follows a continuation reference.
     * 
     * @throws Exception the exception
     */
    public void testBrowseAndFollowContinuationReference() throws Exception
    {
        // ensure that referrals handling method is FOLLOW 
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW.ordinal(), referralsHandlingMethodOrdinal );

        // create the referral entry
        createReferralEntry();

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        // expand ou=system, that reads the referral and opens the referral dialog
        final SWTBotTreeItem systemNode = SWTBotUtils.selectNode( bot, browserTree, "DIT", "Root DSE", "ou=system" );
        UIThreadRunnable.asyncExec( bot.getDisplay(), new UIThreadRunnable.VoidResult()
        {
            public void run()
            {
                systemNode.expand();
            }
        } );
        bot.sleep( 1000 );

        // click OK in the referral dialog
        bot.button( "OK" ).click();
        systemNode.expand();
        bot.sleep( 1000 );

        // ensure that the referral URL and target is visible
        SWTBotTreeItem referralNode = systemNode.getNode( "ldap://localhost:" + ldapServer.getIpPort()
            + "/ou=users,ou=system" );
        assertNotNull( referralNode );
        SWTBotUtils.selectNode( bot, browserTree, "DIT", "Root DSE", "ou=system", "ldap://localhost:"
            + ldapServer.getIpPort() + "/ou=users,ou=system" );

    }


    /**
     * Test for DIRSTUDIO-343.
     * 
     * Does not follow a continuation reference by clicking 
     * the cancel button in the referral dialog.
     * 
     * @throws Exception the exception
     */
    public void testBrowseAndCancelFollowingContinuationReference() throws Exception
    {
        // ensure that referrals handling method is FOLLOW 
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW.ordinal(), referralsHandlingMethodOrdinal );

        // create the referral entry
        createReferralEntry();

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        // expand ou=system, that reads the referral and opens the referral dialog
        final SWTBotTreeItem systemNode = SWTBotUtils.selectNode( bot, browserTree, "DIT", "Root DSE", "ou=system" );
        UIThreadRunnable.asyncExec( bot.getDisplay(), new UIThreadRunnable.VoidResult()
        {
            public void run()
            {
                systemNode.expand();
            }
        } );
        bot.sleep( 1000 );

        // click Cancel in the referral dialog
        bot.button( "Cancel" ).click();
        systemNode.expand();
        bot.sleep( 1000 );

        // ensure that the referral URL and target is not visible
        SWTBotTreeItem referralNode = systemNode.getNode( "ldap://localhost:" + ldapServer.getIpPort()
            + "/ou=users,ou=system" );
        assertNull( referralNode );
    }


    /**
     * Tests ignore referral by setting the connection property to IGNORE.
     * 
     * @throws Exception the exception
     */
    public void testBrowseAndIgnoreReferral() throws Exception
    {
        // ensure that referrals handling method is IGNORE
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.IGNORE.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.IGNORE.ordinal(), referralsHandlingMethodOrdinal );

        // create the referral entry
        createReferralEntry();

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        // expand ou=system, that reads the referral and opens the referral dialog
        final SWTBotTreeItem systemNode = SWTBotUtils.selectNode( bot, browserTree, "DIT", "Root DSE", "ou=system" );
        systemNode.expand();
        systemNode.expand();
        bot.sleep( 1000 );

        // ensure that the referral entry is not visible
        SWTBotTreeItem referralNode = systemNode.getNode( "ldap://localhost:" + ldapServer.getIpPort()
            + "/ou=users,ou=system" );
        assertNull( referralNode );
    }


    /**
     * Tests manage referral entry by setting the connection property to MANAGE.
     * 
     * @throws Exception the exception
     */
    public void testBrowseAndManageReferralEntry() throws Exception
    {
        // ensure that referrals handling method is MANAGE
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.MANAGE.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.MANAGE.ordinal(), referralsHandlingMethodOrdinal );

        // create the referral entry
        createReferralEntry();

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        // expand ou=system, that reads the referral and opens the referral dialog
        final SWTBotTreeItem systemNode = SWTBotUtils.selectNode( bot, browserTree, "DIT", "Root DSE", "ou=system" );
        systemNode.expand();
        systemNode.expand();
        bot.sleep( 1000 );

        // ensure that the referral entry is visible
        SWTBotTreeItem referralNode = systemNode.getNode( "cn=referralDialogTest" );
        assertNotNull( referralNode );
        SWTBotUtils.selectNode( bot, browserTree, "DIT", "Root DSE", "ou=system", "cn=referralDialogTest" );
    }


    private void createReferralEntry() throws NamingException
    {
        Attributes attrs = new BasicAttributes();
        Attribute ocAttr = new BasicAttribute( "objectClass" );
        ocAttr.add( "top" );
        ocAttr.add( "referral" );
        ocAttr.add( "extensibleObject" );
        attrs.put( ocAttr );
        attrs.put( "cn", "referralDialogTest" );
        attrs.put( "ref", "ldap://localhost:" + ldapServer.getIpPort() + "/ou=users,ou=system" );
        rootDSE.createSubcontext( "cn=referralDialogTest,ou=system", attrs );
    }
}
