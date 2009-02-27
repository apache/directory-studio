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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.unit.AbstractServerTest;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.SWTEclipseBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * Tests the import and export (LDIF, DSML).
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$, $Date$
 */
public class ImportExportTest extends AbstractServerTest {
	private SWTEclipseBot eBot;

	protected void setUp() throws Exception {
		super.setUp();
		super.loadTestLdif(false);
		eBot = new SWTEclipseBot();
		SWTBotUtils.openLdapPerspective(eBot);
		SWTBotUtils.createTestConnection(eBot, "ImportExportTest", ldapService
				.getPort());
	}

	protected void tearDown() throws Exception {
		SWTBotUtils.deleteTestConnections();
		eBot = null;
		super.tearDown();
	}

	/**
	 * Test for DIRSTUDIO-395.
	 * 
	 * <li>export an entry with German umlaut in DN to LDIF</li> <li>verify that
	 * exported LDIF starts with the Base64 encoded DN</li> <li>delete the entry
	 * </li> <li>import the exported LDIF</li> <li>verify that entry with umlaut
	 * exists</li>
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void testExportImportLdifWithGermanUmlautInDN() throws Exception {
		URL url = Platform.getInstanceLocation().getURL();
		String file = url.getFile() + "ImportExportTest.ldif";

		final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree(eBot);

		SWTBotUtils.selectEntry(eBot, browserTree, false, "DIT", "Root DSE",
				"ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel");

		// export LDIF
		SWTBotUtils.asyncClick(eBot, browserTree.contextMenu("LDIF Export..."),
				new DefaultCondition() {
					public boolean test() throws Exception {
						return bot.shell("LDIF Export") != null;
					}

					public String getFailureMessage() {
						return "Could not find dialog 'LDIF Export'";
					}
				});
		eBot.button("Next >").click();
		eBot.comboBoxWithLabel("LDIF File:").setText(file);
		eBot.button("Finish").click();

		// verify that exported LDIF starts with the Base64 encoded DN
		eBot.sleep(2000);
		String content = FileUtils.readFileToString(new File(file));
		assertTrue(
				"LDIF must start with Base64 encoded DN.",
				content
						.startsWith("dn:: Y249V29sZmdhbmcgS8O2bGJlbCxvdT11c2VycyxvdT1zeXN0ZW0="));

		// delete entry
		SWTBotUtils.asyncClick(eBot, browserTree.contextMenu("Delete Entry"),
				new DefaultCondition() {
					public boolean test() throws Exception {
						return bot.shell("Delete Entry") != null;
					}

					public String getFailureMessage() {
						return "Could not find dialog 'New Entry'";
					}
				});
		SWTBotUtils.asyncClick(eBot, eBot.button("OK"), new DefaultCondition() {
			public boolean test() throws Exception {
				return browserTree.selection().get(0).get(0).startsWith(
						"ou=users");
			}

			public String getFailureMessage() {
				return "Could not select 'ou=system'";
			}
		});

		// import LDIF
		SWTBotUtils.asyncClick(eBot, browserTree.contextMenu("LDIF Import..."),
				new DefaultCondition() {
					public boolean test() throws Exception {
						return bot.shell("LDIF Import") != null;
					}

					public String getFailureMessage() {
						return "Could not find dialog 'LDIF Import'";
					}
				});
		eBot.comboBoxWithLabel("LDIF File:").setText(file);
		eBot.button("Finish").click();

		// verify that entry with umlaut exists
		SWTBotUtils.selectEntry(eBot, browserTree, false, "DIT", "Root DSE",
				"ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel");
	}

	/**
	 * Test for DIRSTUDIO-395.
	 * 
	 * <li>export an entry with German umlaut in DN to DSML</li> <li>verify that
	 * exported DSML starts with the Base64 encoded DN</li> <li>delete the entry
	 * </li> <li>import the exported DSML</li> <li>verify that entry with umlaut
	 * exists</li>
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void testExportImportDsmlWithGermanUmlautInDN() throws Exception {
		URL url = Platform.getInstanceLocation().getURL();
		String file = url.getFile() + "ImportExportTest.dsml";

		final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree(eBot);

		SWTBotUtils.selectEntry(eBot, browserTree, false, "DIT", "Root DSE",
				"ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel");
		eBot.sleep(2000);

		// export DSML
		SWTBotUtils.asyncClick(eBot, browserTree.contextMenu("DSML Export..."),
				new DefaultCondition() {
					public boolean test() throws Exception {
						return bot.shell("DSML Export") != null;
					}

					public String getFailureMessage() {
						return "Could not find dialog 'DSML Export'";
					}
				});
		eBot.button("Next >").click();
		eBot.comboBoxWithLabel("DSML File:").setText(file);
		eBot.radio("DSML Request").click();
		eBot.button("Finish").click();

		// verify that exported DSML contains the Base64 encoded DN
		eBot.sleep(2000);
		String content = FileUtils.readFileToString(new File(file), "UTF-8");
		assertTrue("DSML must contain DN with umlaut.", content
				.contains("dn=\"cn=Wolfgang K\u00f6lbel,ou=users,ou=system\""));

		// delete entry
		SWTBotUtils.asyncClick(eBot, browserTree.contextMenu("Delete Entry"),
				new DefaultCondition() {
					public boolean test() throws Exception {
						return bot.shell("Delete Entry") != null;
					}

					public String getFailureMessage() {
						return "Could not find dialog 'New Entry'";
					}
				});
		SWTBotUtils.asyncClick(eBot, eBot.button("OK"), new DefaultCondition() {
			public boolean test() throws Exception {
				return browserTree.selection().get(0).get(0).startsWith(
						"ou=users");
			}

			public String getFailureMessage() {
				return "Could not select 'ou=system'";
			}
		});

		// import DSML
		SWTBotUtils.asyncClick(eBot, browserTree.contextMenu("DSML Import..."),
				new DefaultCondition() {
					public boolean test() throws Exception {
						return bot.shell("DSML Import") != null;
					}

					public String getFailureMessage() {
						return "Could not find dialog 'LDIF Import'";
					}
				});
		eBot.comboBoxWithLabel("DSML File:").setText(file);
		eBot.button("Finish").click();

		// verify that entry with umlaut exists
		SWTBotUtils.selectEntry(eBot, browserTree, false, "DIT", "Root DSE",
				"ou=system", "ou=users", "cn=Wolfgang K\u00f6lbel");
	}

	/**
     * Test for DIRSTUDIO-465.
     * 
     * Import a new context entry must refresh the root DSE and 
     * show the new context entry in the LDAP Browser view.
     * 
     * @throws Exception
     *             the exception
     */
    public void testImportContextEntryRefreshesRootDSE() throws Exception
    {
        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( eBot );

        // add a new partition
        Partition partition = new JdbmPartition();
        partition.setId( "example" );
        partition.setSuffix( "dc=example,dc=com" );
        directoryService.addPartition( partition );

        // refresh root DSE and ensure that the partition is in root DSE
        SWTBotTreeItem rootDSE = SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE" );
        SWTBotUtils.asyncClick( eBot, browserTree.contextMenu( "Reload Attributes and Children" ),
            new DefaultCondition()
            {
                public boolean test() throws Exception
                {
                    SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE" );
                    final SWTBotTree entryEditorTree = SWTBotUtils.getEntryEditorTree( eBot );
                    String text = entryEditorTree.cell( 2, 1 );
                    return "dc=example,dc=com".equals( text );
                }


                public String getFailureMessage()
                {
                    return "New partition 'dc=example,dc=com' not found in Root DSE entry";
                }
            } );

        // ensure context entry is not there
        rootDSE = SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE" );
        List<String> nodes = rootDSE.getNodes();
        for ( String node : nodes )
        {
            if ( node.startsWith( "dc=example,dc=com" ) )
            {
                fail( "dc=example,dc=com should not exist yet" );
            }
        }

        // import
        URL url = Platform.getInstanceLocation().getURL();
        String file = url.getFile() + "ImportContextEntry.ldif";
        String data = "dn:dc=example,dc=com\nobjectClass:top\nobjectClass:domain\ndc:example\n\n";
        FileUtils.writeStringToFile( new File( file ), data );
        SWTBotUtils.asyncClick( eBot, browserTree.contextMenu( "LDIF Import..." ), new DefaultCondition()
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
        eBot.comboBoxWithLabel( "LDIF File:" ).setText( file );
        eBot.button( "Finish" ).click();

        // ensure context entry is there now, without a manual refresh
        SWTBotUtils.selectEntry( eBot, browserTree, false, "DIT", "Root DSE", "dc=example,dc=com" );
    }
	
}
