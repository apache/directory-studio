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


import net.sf.swtbot.eclipse.finder.SWTEclipseBot;
import net.sf.swtbot.wait.DefaultCondition;
import net.sf.swtbot.widgets.SWTBotCombo;
import net.sf.swtbot.widgets.SWTBotMenu;
import net.sf.swtbot.widgets.SWTBotText;
import net.sf.swtbot.widgets.SWTBotTree;

import org.apache.directory.server.unit.AbstractServerTest;


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
        SWTBotUtils.selectNode( bot, browserTree, "DIT", "Root DSE", "ou=system" );

        // open "New Entry" wizard
        SWTBotMenu contextMenu = browserTree.contextMenu( "New Entry..." );
        contextMenu.click();

        // select entry creation method
        bot.radio( "Create entry from scratch" ).click();
        bot.button( "Next >" ).click();

        // select object classes
        bot.table( 0 ).select( "organization" );
        bot.button( "Add" ).click();
        bot.button( "Next >" ).click();

        // specify DN
        SWTBotCombo typeCombo = bot.comboBox( "" );
        typeCombo.setText( "o" );
        SWTBotText valueText = bot.text( "" );
        valueText.setText( "testCreateOrganizationEntry" );
        bot.button( "Next >" ).click();

        // wait for check that entry doesn't exist yet
        bot.waitUntil( new DefaultCondition()
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
        bot.button( "Finish" ).click();

        // wait till entry is created and selected in the tree
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 ).equals( "o=testCreateOrganizationEntry" );
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
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
        SWTBotUtils.selectNode( bot, browserTree, "DIT", "Root DSE", "ou=system" );

        // open "New Entry" wizard
        SWTBotMenu contextMenu = browserTree.contextMenu( "New Entry..." );
        contextMenu.click();

        // select entry creation method
        bot.radio( "Create entry from scratch" ).click();
        bot.button( "Next >" ).click();

        // select object classes
        bot.table( 0 ).select( "inetOrgPerson" );
        bot.button( "Add" ).click();
        bot.button( "Next >" ).click();

        // specify DN
        SWTBotCombo typeCombo = bot.comboBox( "" );
        typeCombo.setText( "cn" );
        SWTBotText valueText = bot.text( "" );
        valueText.setText( "testCreatePersonEntry" );
        bot.button( "Next >" ).click();

        // wait for check that entry doesn't exist yet
        bot.waitUntil( new DefaultCondition()
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

        // enter values
        SWTBotTree tree = bot.tree( 0 );
        tree.select( "sn" );
        bot.text( "" ).setText( "test" );
        tree.select( "cn" );
        
        // TODO: with SWTBot 1.2 we could use the tree.click() method! 
        // workaround to apply the new value 
        bot.button( "< Back" ).click();
        bot.button( "Next >" ).click();
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.button( "Finish" ).isEnabled();
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );

        bot.button( "Finish" ).click();

        // wait till entry is created and selected in the tree
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 ).equals( "cn=testCreatePersonEntry" );
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );
    }


    /**
     * Test to create multiple entries.
     * 
     * @throws Exception the exception
     */
    public void testCreateMultipleEntries() throws Exception
    {
        SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        for ( int i = 0; i < 25; i++ )
        {
            createEntry( browserTree, "testCreateMultipleEntries" + i );
        }
    }


    private void createEntry( final SWTBotTree browserTree, final String name ) throws Exception
    {
        SWTBotUtils.selectNode( bot, browserTree, "DIT", "Root DSE", "ou=system" );

        SWTBotMenu contextMenu = browserTree.contextMenu( "New Entry..." );
        contextMenu.click();

        bot.radio( "Create entry from scratch" ).click();
        bot.button( "Next >" ).click();

        bot.table( 0 ).select( "organization" );
        bot.button( "Add" ).click();
        bot.button( "Next >" ).click();

        SWTBotCombo typeCombo = bot.comboBox( "" );
        typeCombo.setText( "o" );
        SWTBotText valueText = bot.text( "" );
        valueText.setText( name );
        bot.button( "Next >" ).click();

        // wait for check that entry doesn't exist yet
        bot.waitUntil( new DefaultCondition()
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
        bot.button( "Finish" ).click();

        // wait till entry is created and selected in the tree
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 ).equals( "o=" + name );
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );
    }

}
