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

import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapService;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the entry editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(SiRunner.class)
@CleanupLevel(Level.SUITE)
@ApplyLdifFiles(
    { "EntryEditorTest.ldif" })
public class EntryEditorTest
{
    public static LdapService ldapService;

    private SWTWorkbenchBot bot;


    @Before
    public void setUp() throws Exception
    {
        bot = new SWTWorkbenchBot();
        SWTBotUtils.openLdapPerspective( bot );
        SWTBotUtils.createTestConnection( bot, "EntryEditorTest", ldapService.getPort() );
    }


    @After
    public void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
    }


    /**
     * Test adding, editing and deleting of attributes in the entry editor.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testAddEditDeleteAttribute() throws Exception
    {
        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Barbara Jensen" );

        final SWTBotTree entryEditorTree = SWTBotUtils.getEntryEditorTree( bot );

        // add description attribute
        entryEditorTree.contextMenu( "New Attribute..." ).click();
        bot.comboBoxWithLabel( "Attribute type:" ).setText( "description" );
        SWTBotUtils.asyncClick( bot, bot.button( "Finish" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.text( "" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find empty description attribute";
            }
        } );
        bot.text( "" ).setText( "This is the 1st description." );
        SWTBotUtils.asyncClick( bot, entryEditorTree.getTreeItem( "objectClass" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return entryEditorTree.cell( 6, 0 ).equals( "description" )
                    && entryEditorTree.cell( 6, 1 ).equals( "This is the 1st description." );
            }


            public String getFailureMessage()
            {
                return "Could not find attribute 'description:This is the 1st description.'";
            }
        } );

        // add second value
        entryEditorTree.getTreeItem( "description" ).click();
        SWTBotUtils.asyncClick( bot, entryEditorTree.contextMenu( "New Value" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.text( "" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find empty description attribute";
            }
        } );
        bot.text( "" ).setText( "This is the 2nd description." );
        SWTBotUtils.asyncClick( bot, entryEditorTree.getTreeItem( "objectClass" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return entryEditorTree.cell( 7, 0 ).equals( "description" )
                    && entryEditorTree.cell( 7, 1 ).equals( "This is the 2nd description." );
            }


            public String getFailureMessage()
            {
                return "Could not find attribute 'description:This is the 2nd description.'";
            }
        } );

        // edit second value
        entryEditorTree.select( 7 );
        SWTBotUtils.asyncClick( bot, entryEditorTree.contextMenu( "Edit Value" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.text( "This is the 2nd description." ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find description 'This is the 2nd description.'";
            }
        } );
        bot.text( "This is the 2nd description." ).setText( "This is the 3rd description." );
        SWTBotUtils.asyncClick( bot, entryEditorTree.getTreeItem( "objectClass" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return entryEditorTree.cell( 7, 0 ).equals( "description" )
                    && entryEditorTree.cell( 7, 1 ).equals( "This is the 3rd description." );
            }


            public String getFailureMessage()
            {
                return "Could not find attribute 'description:This is the 3rd description.'";
            }
        } );

        // delete second value
        entryEditorTree.select( 7 );
        entryEditorTree.contextMenu( "Delete Value" ).click();
        SWTBotUtils.asyncClick( bot, bot.button( "OK" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return !entryEditorTree.cell( 7, 0 ).equals( "description" )
                    && entryEditorTree.cell( 6, 0 ).equals( "description" )
                    && entryEditorTree.cell( 6, 1 ).equals( "This is the 1st description." );
            }


            public String getFailureMessage()
            {
                return "Attribute 'description' is still there.";
            }
        } );

        // edit 1st value
        entryEditorTree.select( 6 );
        SWTBotUtils.asyncClick( bot, entryEditorTree.contextMenu( "Edit Value" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.text( "This is the 1st description." ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find description 'This is the 1st description.'";
            }
        } );
        bot.text( "This is the 1st description." ).setText( "This is the final description." );
        SWTBotUtils.asyncClick( bot, entryEditorTree.getTreeItem( "objectClass" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return entryEditorTree.cell( 6, 0 ).equals( "description" )
                    && entryEditorTree.cell( 6, 1 ).equals( "This is the final description." );
            }


            public String getFailureMessage()
            {
                return "Could not find attribute 'description:This is the final description.'";
            }
        } );

        // delete 1st value/attribute
        entryEditorTree.select( 6 );
        entryEditorTree.contextMenu( "Delete Value" ).click();
        SWTBotUtils.asyncClick( bot, bot.button( "OK" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return !entryEditorTree.cell( 6, 0 ).equals( "description" );
            }


            public String getFailureMessage()
            {
                return "Attribute 'description' is still there.";
            }
        } );
    }


    /**
     * DIRSTUDIO-483: DN Editor escapes all non-ascii characters 
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDnValueEditor() throws Exception
    {
        SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=groups", "cn=My Group" );

        SWTBotTree entryEditorTree = SWTBotUtils.getEntryEditorTree( bot );
        entryEditorTree.contextMenu( "New Attribute..." ).click();
        bot.shell( "New Attribute" );
        bot.comboBoxWithLabel( "Attribute type:" ).setText( "member" );
        bot.button( "Finish" ).click();

        // DN Editor automatically opened
        bot.shell( "DN Editor" );
        bot.button( "Browse..." ).click();

        // select value from DN picker
        bot.shell( "Select DN" );
        SWTBotTree tree = bot.tree( 0 );
        SWTBotUtils.selectEntry( bot, tree, false, "Root DSE", "ou=system", "ou=users",
            "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\"" );
        bot.button( "OK" ).click();

        // assert value after selection
        assertEquals( "Unexpected value", "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system", bot.comboBox()
            .getText() );

        // save value
        bot.button( "OK" ).click();
        bot.sleep( 1000 );

        // assert value after saved and reloaded from server
        entryEditorTree.select( 3 );
        entryEditorTree.getTreeItem( "member" ).doubleClick();
        assertEquals( "Unexpected value", "cn=\\#\\\\\\+\\, \\\"\u00F6\u00E9\\\",ou=users,ou=system", bot.comboBox()
            .getText() );
        bot.button( "Cancel" ).click();
    }

}
