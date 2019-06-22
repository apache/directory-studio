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


import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.SystemUtils;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.test.integration.ui.bots.LdifEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.NewWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the LDIF editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
public class LdifEditorTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();

        // activate search and modifications logs, they also include an LDIF editor and increment the counter
        studioBot.getSearchLogsViewBot().getSearchLogsText();
        studioBot.getModificationLogsViewBot().getModificationLogsText();
    }


    @After
    public void tearDown() throws Exception
    {
        Assertions.genericTearDownAssertions();
    }


    /**
     * Test for DIRSTUDIO-1043 (First open of LDIF editor fails)
     */
    @Test
    public void testNewLdifEditor() throws Exception
    {
        /*
         * This test fails on Jenkins Windows Server 2012, to be investigated...
         */
        Assume.assumeFalse( SystemUtils.IS_OS_WINDOWS_2012 );

        NewWizardBot newWizard = studioBot.openNewWizard();
        newWizard.selectLdifFile();
        assertTrue( newWizard.isFinishButtonEnabled() );
        newWizard.clickFinishButton();

        // TODO: use matcher instead of hard code editor number
        LdifEditorBot ldifEditorBot = new LdifEditorBot( "LDIF 3" );
        ldifEditorBot.activate();
        ldifEditorBot.typeText( "dn: dc=test\nobjectClass: domain\n\n" );
        assertTrue( ldifEditorBot.isDirty() );
        ldifEditorBot.close();
    }

}
