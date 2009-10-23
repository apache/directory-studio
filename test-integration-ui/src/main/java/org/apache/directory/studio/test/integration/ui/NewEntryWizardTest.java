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

import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewEntryWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
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

    private SWTWorkbenchBot bot;


    @Before
    public void setUp() throws Exception
    {
        // check if krb5kdc is disabled
        DirContext schemaRoot = ( DirContext ) getWiredContext( ldapServer ).lookup( "ou=schema" );
        Attributes krb5kdcAttrs = schemaRoot.getAttributes( "cn=Krb5kdc" );
        boolean isKrb5KdcDisabled = false;
        if ( krb5kdcAttrs.get( "m-disabled" ) != null )
        {
            isKrb5KdcDisabled = ( ( String ) krb5kdcAttrs.get( "m-disabled" ).get() ).equalsIgnoreCase( "TRUE" );
        }
        // if krb5kdc is disabled then enable it
        if ( isKrb5KdcDisabled )
        {
            Attribute disabled = new BasicAttribute( "m-disabled" );
            ModificationItem[] mods = new ModificationItem[]
                { new ModificationItem( DirContext.REMOVE_ATTRIBUTE, disabled ) };
            schemaRoot.modifyAttributes( "cn=Krb5kdc", mods );
        }

        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "NewEntryWizardTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();

        bot = new SWTWorkbenchBot();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        bot = null;
    }


    /**
     * Test to create a single organization entry.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testCreateOrganizationEntry() throws Exception
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

        browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "o=testCreateOrganizationEntry" );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "o=testCreateOrganizationEntry" );
    }


    /**
     * Test to create a single person entry.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testCreatePersonEntry() throws Exception
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

        wizardBot.setAttributeValue( "sn", 1, "test" );
        wizardBot.clickFinishButton();

        browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "cn=testCreatePersonEntry" );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "cn=testCreatePersonEntry" );
    }


    /**
     * Test for DIRSTUDIO-350.
     * 
     * Create entries with upper case attribute types and ensures that the
     * retrieved entries still are in upper case.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testCreateUpperCaseOrganizationEntries() throws Exception
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

        browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "O=testCreateOrganizationEntry" );
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

        browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "O=testCreateOrganizationEntry",
            "O=testCreateOrganizationEntry2" );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "O=testCreateOrganizationEntry",
            "O=testCreateOrganizationEntry2" );
    }


    /**
     * Test for DIRSTUDIO-360.
     * 
     * Create entries with a slash '/' in the RDN value.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testCreateEntryWithSlash() throws Exception
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

        wizardBot.setAttributeValue( "cn", 1, "test" );
        wizardBot.setAttributeValue( "sn", 1, "test" );
        wizardBot.clickFinishButton();

        browserViewBot.existsEntry( "DIT", "Root DSE", "ou=system", "krb5PrincipalName=kadmin/changepw@DOMAIN" );
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "krb5PrincipalName=kadmin/changepw@DOMAIN" );
    }

}
