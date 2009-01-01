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


import java.io.File;
import java.net.URL;

import net.sf.swtbot.eclipse.finder.SWTEclipseBot;
import net.sf.swtbot.wait.DefaultCondition;
import net.sf.swtbot.widgets.SWTBotTree;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.unit.AbstractServerTest;
import org.eclipse.core.runtime.Platform;


/**
 * Tests the import and export (LDIF, DSML).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportExportTest extends AbstractServerTest
{
    private SWTEclipseBot bot;


    protected void setUp() throws Exception
    {
        super.setUp();
        super.loadTestLdif( false );
        bot = new SWTEclipseBot();
        SWTBotUtils.openLdapPerspective( bot );
        SWTBotUtils.createTestConnection( bot, "ImportExportTest", ldapService.getPort() );
    }


    protected void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
        super.tearDown();
    }


    /**
     * Test for DIRSTUDIO-395.
     * 
     * <li>export an entry with German umlaut in DN to LDIF</li>
     * <li>verify that exported LDIF starts with the Base64 encoded DN</li>
     * <li>delete the entry</li>
     * <li>import the exported LDIF</li>
     * <li>verify that entry with umlaut exists</li>
     * 
     * @throws Exception the exception
     */
    public void testExportImportLdifWithGermanUmlautInDN() throws Exception
    {
        URL url = Platform.getInstanceLocation().getURL();
        String file = url.getFile() + "ImportExportTest.ldif";

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Wolfgang K\u00f6lbel" );

        // export LDIF
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "LDIF Export..." ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "LDIF Export" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'LDIF Export'";
            }
        } );
        bot.button( "Next >" ).click();
        bot.comboBoxWithLabel( "LDIF File:" ).setText( file );
        bot.button( "Finish" ).click();

        // verify that exported LDIF starts with the Base64 encoded DN 
        String content = FileUtils.readFileToString( new File( file ) );
        assertTrue( "LDIF must start with Base64 encoded DN.", content
            .startsWith( "dn:: Y249V29sZmdhbmcgS8O2bGJlbCxvdT11c2VycyxvdT1zeXN0ZW0=" ) );

        // delete entry
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "Delete Entry" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "Delete Entry" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'New Entry'";
            }
        } );
        SWTBotUtils.asyncClick( bot, bot.button( "OK" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 ).startsWith( "ou=users" );
            }


            public String getFailureMessage()
            {
                return "Could not select 'ou=system'";
            }
        } );

        // import LDIF
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "LDIF Import..." ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "LDIF Import" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'LDIF Import'";
            }
        } );
        bot.comboBoxWithLabel( "LDIF File:" ).setText( file );
        bot.button( "Finish" ).click();

        // verify that entry with umlaut exists
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Wolfgang K\u00f6lbel" );
    }


    /**
     * Test for DIRSTUDIO-395.
     * 
     * <li>export an entry with German umlaut in DN to DSML</li>
     * <li>verify that exported DSML starts with the Base64 encoded DN</li>
     * <li>delete the entry</li>
     * <li>import the exported DSML</li>
     * <li>verify that entry with umlaut exists</li>
     * 
     * @throws Exception the exception
     */
    public void testExportImportDsmlWithGermanUmlautInDN() throws Exception
    {
        URL url = Platform.getInstanceLocation().getURL();
        String file = url.getFile() + "ImportExportTest.dsml";

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Wolfgang K\u00f6lbel" );
        bot.sleep( 2000 );

        // export DSML
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "DSML Export..." ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "DSML Export" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'DSML Export'";
            }
        } );
        bot.button( "Next >" ).click();
        bot.comboBoxWithLabel( "DSML File:" ).setText( file );
        bot.radio( "DSML Request" ).click();
        bot.button( "Finish" ).click();

        // verify that exported DSML contains the Base64 encoded DN 
        String content = FileUtils.readFileToString( new File( file ) );
        assertTrue( "DSML must contain DN with umlaut.", content
            .contains( "dn=\"cn=Wolfgang Kölbel,ou=users,ou=system\"" ) );

        // delete entry
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "Delete Entry" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "Delete Entry" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'New Entry'";
            }
        } );
        SWTBotUtils.asyncClick( bot, bot.button( "OK" ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return browserTree.selection().get( 0 ).get( 0 ).startsWith( "ou=users" );
            }


            public String getFailureMessage()
            {
                return "Could not select 'ou=system'";
            }
        } );

        // import DSML
        SWTBotUtils.asyncClick( bot, browserTree.contextMenu( "DSML Import..." ), new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return bot.shell( "DSML Import" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find dialog 'LDIF Import'";
            }
        } );
        bot.comboBoxWithLabel( "DSML File:" ).setText( file );
        bot.button( "Finish" ).click();

        // verify that entry with umlaut exists
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ou=users",
            "cn=Wolfgang K\u00f6lbel" );
    }

}
