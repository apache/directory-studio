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


import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import net.sf.swtbot.eclipse.finder.SWTEclipseBot;
import net.sf.swtbot.wait.DefaultCondition;
import net.sf.swtbot.widgets.SWTBotCombo;
import net.sf.swtbot.widgets.SWTBotText;
import net.sf.swtbot.widgets.SWTBotTree;
import net.sf.swtbot.widgets.SWTBotTreeItem;

import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.shared.ldap.message.AttributeImpl;
import org.apache.directory.shared.ldap.message.ModificationItemImpl;


/**
 * Tests the new entry wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewEntryWizardTest extends AbstractServerTest
{
    private SWTEclipseBot bot;


    protected void setUp() throws Exception
    {
        super.setUp();

        // check if krb5kdc is disabled
        Attributes krb5kdcAttrs = schemaRoot.getAttributes( "cn=Krb5kdc" );
        boolean isKrb5KdcDisabled = false;
        if ( krb5kdcAttrs.get( "m-disabled" ) != null )
        {
            isKrb5KdcDisabled = ( ( String ) krb5kdcAttrs.get( "m-disabled" ).get() ).equalsIgnoreCase( "TRUE" );
        }
        // if krb5kdc is disabled then enable it
        if ( isKrb5KdcDisabled )
        {
            Attribute disabled = new AttributeImpl( "m-disabled" );
            ModificationItemImpl[] mods = new ModificationItemImpl[]
                { new ModificationItemImpl( DirContext.REMOVE_ATTRIBUTE, disabled ) };
            schemaRoot.modifyAttributes( "cn=Krb5kdc", mods );
        }

        bot = new SWTEclipseBot();
        SWTBotUtils.openLdapPerspective( bot );
        SWTBotUtils.createTestConnection( bot, "NewEntryWizardTest", ldapServer.getIpPort() );
    }


    protected void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
        super.tearDown();
    }


    /**
     * Test to create a single organization entry.
     * 
     * @throws Exception the exception
     */
    public void testCreateOrganizationEntry() throws Exception
    {
        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotUtils.selectEntry( bot, browserTree, true, "DIT", "Root DSE", "ou=system" );

        // open "New Entry" wizard
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "New Entry..." ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "New Entry" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'New Entry'";
            }
        } );

        // select entry creation method
        bot.radio( "Create entry from scratch" ).click();
        bot.button( "Next >" ).click();

        // select object classes
        bot.table( 0 ).select( "organization" );
        bot.button( "Add" ).click();
        bot.button( "Next >" ).click();

        // specify DN
        SWTBotCombo typeCombo = bot.comboBoxWithLabel( "RDN:" );
        typeCombo.setText( "o" );
        SWTBotText valueText = bot.text( "" );
        valueText.setText( "testCreateOrganizationEntry" );
        SWTBotUtils.asyncClick( bot, bot.button( "Next >" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.tree( 0 ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );

        // click finish to create the entry
        SWTBotUtils.asyncClick( bot, bot.button( "Finish" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 ).startsWith( "o=testCreateOrganizationEntry" );
            }


            public String getFailureMessage()
            {
                return "Could not find 'o=testCreateOrganizationEntry'";
            }
        } );
    }


    /**
     * Test to create a single person entry.
     * 
     * @throws Exception the exception
     */
    public void testCreatePersonEntry() throws Exception
    {
        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotUtils.selectEntry( bot, browserTree, true, "DIT", "Root DSE", "ou=system" );

        // open "New Entry" wizard
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "New Entry..." ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "New Entry" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'New Entry'";
            }
        } );

        // select entry creation method
        bot.radio( "Create entry from scratch" ).click();
        bot.button( "Next >" ).click();

        // select object classes
        bot.table( 0 ).select( "inetOrgPerson" );
        bot.button( "Add" ).click();
        bot.button( "Next >" ).click();

        // specify DN
        SWTBotCombo typeCombo = bot.comboBoxWithLabel( "RDN:" );
        typeCombo.setText( "cn" );
        SWTBotText valueText = bot.text( "" );
        valueText.setText( "testCreatePersonEntry" );
        SWTBotUtils.asyncClick( bot, bot.button( "Next >" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.tree( 0 ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );

        // enter sn value
        SWTBotTree tree = bot.tree( 0 );
        tree.select( "sn" );
        bot.text( "" ).setText( "test" );
        // click to finish editing of sn
        SWTBotTreeItem snNode = tree.getTreeItem( "sn" );
        snNode.click();

        // click finish to create the entry
        SWTBotUtils.asyncClick( bot, bot.button( "Finish" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 ).startsWith( "cn=testCreatePersonEntry" );
            }


            public String getFailureMessage()
            {
                return "Could not find 'cn=testCreatePersonEntry'";
            }
        } );
    }


    /**
     * Test for DIRSTUDIO-350.
     * 
     * Create entries with upper case attribute types and ensures that
     * the retrieved entries still are in upper case.
     * 
     * @throws Exception the exception
     */
    public void testCreateUpperCaseOrganizationEntries() throws Exception
    {
        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotUtils.selectEntry( bot, browserTree, true, "DIT", "Root DSE", "ou=system" );

        // open "New Entry" wizard
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "New Entry..." ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "New Entry" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'New Entry'";
            }
        } );

        // select entry creation method
        bot.radio( "Create entry from scratch" ).click();
        bot.button( "Next >" ).click();

        // select object classes
        bot.table( 0 ).select( "organization" );
        bot.button( "Add" ).click();
        bot.button( "Next >" ).click();

        // specify DN
        SWTBotCombo typeCombo = bot.comboBoxWithLabel( "RDN:" );
        typeCombo.setText( "O" );
        SWTBotText valueText = bot.text( "" );
        valueText.setText( "testCreateOrganizationEntry" );
        SWTBotUtils.asyncClick( bot, bot.button( "Next >" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.tree( 0 ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );

        // click finish to create the entry
        SWTBotUtils.asyncClick( bot, bot.button( "Finish" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 ).startsWith( "O=testCreateOrganizationEntry" );
            }


            public String getFailureMessage()
            {
                return "Could not find 'O=testCreateOrganizationEntry'";
            }
        } );

        // Now create a second entry under the previously created entry 
        // to ensure that the selected parent is also upper case.

        // open "New Entry" wizard
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "New Entry..." ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "New Entry" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'New Entry'";
            }
        } );

        // select entry creation method
        bot.radio( "Create entry from scratch" ).click();
        bot.button( "Next >" ).click();

        // select object classes
        bot.table( 0 ).select( "organization" );
        bot.button( "Add" ).click();
        bot.button( "Next >" ).click();

        // specify DN
        typeCombo = bot.comboBoxWithLabel( "RDN:" );
        typeCombo.setText( "O" );
        valueText = bot.text( "" );
        valueText.setText( "testCreateOrganizationEntry2" );

        // check preview text 
        SWTBotText previewText = bot.text( "O=testCreateOrganizationEntry2,O=testCreateOrganizationEntry,ou=system" );
        assertEquals( "O=testCreateOrganizationEntry2,O=testCreateOrganizationEntry,ou=system", previewText.getText() );

        SWTBotUtils.asyncClick( bot, bot.button( "Next >" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.tree( 0 ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );

        // click finish to create the entry
        SWTBotUtils.asyncClick( bot, bot.button( "Finish" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 ).startsWith( "O=testCreateOrganizationEntry2" );
            }


            public String getFailureMessage()
            {
                return "Could not find 'O=testCreateOrganizationEntry2'";
            }
        } );
    }


    /**
     * Test for DIRSTUDIO-360.
     * 
     * Create entries with a slash '/' in the RDN value.
     * 
     * @throws Exception the exception
     */
    public void testCreateEntryWithSlash() throws Exception
    {
        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotUtils.selectEntry( bot, browserTree, true, "DIT", "Root DSE", "ou=system" );

        // open "New Entry" wizard
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "New Entry..." ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "New Entry" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'New Entry'";
            }
        } );

        // select entry creation method
        bot.radio( "Create entry from scratch" ).click();
        bot.button( "Next >" ).click();

        // select object classes
        bot.table( 0 ).select( "krb5Principal" );
        bot.button( "Add" ).click();
        bot.table( 0 ).select( "person" );
        bot.button( "Add" ).click();
        bot.button( "Next >" ).click();

        // specify DN
        SWTBotCombo typeCombo = bot.comboBoxWithLabel( "RDN:" );
        typeCombo.setText( "krb5PrincipalName" );
        SWTBotText valueText = bot.text( "" );
        valueText.setText( "kadmin/changepw@DOMAIN" );
        SWTBotUtils.asyncClick( bot, bot.button( "Next >" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.tree( 0 ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find entry editor";
            }
        } );

        SWTBotTree tree = bot.tree( 0 );
        SWTBotTreeItem krbNode = tree.getTreeItem( "krb5PrincipalName" );

        // enter cn value
        tree.select( "cn" );
        bot.text( "" ).setText( "test" );
        // click to finish editing of cn
        krbNode.click();

        // enter sn value
        tree.select( "sn" );
        bot.text( "" ).setText( "test" );
        // click to finish editing of sn
        krbNode.click();

        // click finish to create the entry
        SWTBotUtils.asyncClick( bot, bot.button( "Finish" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 )
                    .startsWith( "krb5PrincipalName=kadmin/changepw@DOMAIN" );
            }


            public String getFailureMessage()
            {
                return "Could not find entry 'krb5Principal=kadmin/changepw@DOMAIN'";
            }
        } );
    }

}
