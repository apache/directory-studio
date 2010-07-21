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


import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewEntryWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.IntResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests allocation of SWT Resources.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(SiRunner.class)
@CleanupLevel(Level.SUITE)
public class SwtResourcesTest
{
    public static LdapServer ldapServer;

    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "SwtResourcesTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
    }


    /**
     * Test for DIRSTUDIO-319.
     * 
     * Creates multiple entries using the New Entry wizard. Checks that we don't
     * allocate too much SWT resources during the run.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testSwtResourcesDelta() throws Exception
    {
        // run the new entry wizard once to ensure all SWT resources are created
        createAndDeleteEntry( "testSwtResourcesDelta" + 0 );

        // remember the SWT objects before the run
        int beforeObjectCount = getSwtObjectCount();

        // now lets run the new entry wizard it several times
        for ( int i = 1; i < 25; i++ )
        {
            createAndDeleteEntry( "testSwtResourcesDelta" + i );
        }

        // get the SWT objects after the run
        int afterObjectCount = getSwtObjectCount();

        // we expect not more than 10 additional SWT objects
        assertTrue( "Too many SWT resources were allocated in testSwtResourcesDelta: before=" + beforeObjectCount
            + ", after=" + afterObjectCount, afterObjectCount - beforeObjectCount < 10 );
    }


    /**
     * Ensure that we have not allocated more the 1000 SWT resources during the
     * complete test suite.
     * 
     * 1000 is not a fix number but it is a good starting point.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testSwtResourcesCount() throws Exception
    {
        int swtObjectCount = getSwtObjectCount();
        assertTrue( "Too many SWT resources were allocated: " + swtObjectCount, swtObjectCount < 1000 );
    }


    private int getSwtObjectCount()
    {
        final SWTBot bot = new SWTBot();
        return UIThreadRunnable.syncExec( bot.getDisplay(), new IntResult()
        {
            public Integer run()
            {
                DeviceData info = bot.getDisplay().getDeviceData();
                if ( !info.tracking )
                {
                    fail( "To run this test options 'org.eclipse.ui/debug' and 'org.eclipse.ui/trace/graphics' must be true." );
                }
                return info.objects.length;
            }
        } );
    }


    private void createAndDeleteEntry( final String name ) throws Exception
    {
        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system" );
        browserViewBot.expandEntry( "DIT", "Root DSE", "ou=system" );
        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "organization" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "o" );
        wizardBot.setRdnValue( 1, name );
        wizardBot.clickNextButton();

        wizardBot.clickFinishButton();

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "o=" + name );
        DeleteDialogBot dialog = browserViewBot.openDeleteDialog();
        dialog.clickOkButton();
    }

}
