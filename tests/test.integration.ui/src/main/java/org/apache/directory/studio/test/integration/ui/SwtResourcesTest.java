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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewEntryWizardBot;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.IntResult;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests allocation of SWT Resources.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SwtResourcesTest extends AbstractTestBase
{

    /**
     * Test for DIRSTUDIO-319.
     *
     * Creates multiple entries using the New Entry wizard. Checks that we don't
     * allocate too much SWT resources during the run.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testSwtResourcesDelta( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );

        // run the new entry wizard once to ensure all SWT resources are created
        createAndDeleteEntry( "testSwtResourcesDelta" + 0 );

        // remember the SWT objects before the run
        int beforeObjectCount = getSwtObjectCount();

        // now lets run the new entry wizard it several times
        for ( int i = 1; i <= 8; i++ )
        {
            createAndDeleteEntry( "testSwtResourcesDelta" + i );
        }

        // get the SWT objects after the run
        int afterObjectCount = getSwtObjectCount();

        // we expect none or only very few additional SWT objects
        assertTrue( afterObjectCount - beforeObjectCount < 5,
            "Too many SWT resources were allocated in testSwtResourcesDelta: before=" + beforeObjectCount
                + ", after=" + afterObjectCount );
    }


    /**
     * Ensure that we have not allocated too many SWT resources during the
     * complete test suite.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testSwtResourcesCount( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        int swtObjectCount = getSwtObjectCount();
        System.out.println( "### SWT resouces count: " + swtObjectCount );
        assertTrue( swtObjectCount < 1500, "Too many SWT resources were allocated: " + swtObjectCount );
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
                    fail(
                        "To run this test options 'org.eclipse.ui/debug' and 'org.eclipse.ui/trace/graphics' must be true." );
                }
                return info.objects.length;
            }
        } );
    }


    private void createAndDeleteEntry( final String name ) throws Exception
    {
        browserViewBot.selectAndExpandEntry( path( MISC_DN ) );
        NewEntryWizardBot wizardBot = browserViewBot.openNewEntryWizard();

        wizardBot.selectCreateEntryFromScratch();
        wizardBot.clickNextButton();

        wizardBot.addObjectClasses( "organization" );
        wizardBot.clickNextButton();

        wizardBot.setRdnType( 1, "o" );
        wizardBot.setRdnValue( 1, name );
        wizardBot.clickNextButton();

        wizardBot.clickFinishButton();

        assertTrue( browserViewBot.existsEntry( path( MISC_DN, "o=" + name ) ) );
        browserViewBot.selectEntry( path( MISC_DN, "o=" + name ) );
        DeleteDialogBot dialog = browserViewBot.openDeleteDialog();
        dialog.clickOkButton();
    }

}
