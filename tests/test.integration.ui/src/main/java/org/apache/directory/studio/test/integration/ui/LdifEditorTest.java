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

import org.apache.directory.studio.test.integration.ui.bots.LdifEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.NewWizardBot;
import org.junit.jupiter.api.Test;


/**
 * Tests the LDIF editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifEditorTest extends AbstractTestBase
{

    /**
     * Test for DIRSTUDIO-1043 (First open of LDIF editor fails)
     */
    @Test
    public void testNewLdifEditor() throws Exception
    {
        // Open first LDIF editor
        NewWizardBot newWizard = studioBot.openNewWizard();
        newWizard.selectLdifFile();
        assertTrue( newWizard.isFinishButtonEnabled() );
        newWizard.clickFinishButton();

        LdifEditorBot ldifEditorBot = new LdifEditorBot( "LDIF" );
        ldifEditorBot.activate();
        ldifEditorBot.typeText( "dn: dc=test\nobjectClass: domain\n\n" );
        assertTrue( ldifEditorBot.isDirty() );
        ldifEditorBot.close();

        // Open second LDIF editor
        NewWizardBot newWizard2 = studioBot.openNewWizard();
        newWizard2.selectLdifFile();
        assertTrue( newWizard2.isFinishButtonEnabled() );
        newWizard2.clickFinishButton();

        LdifEditorBot ldifEditorBot2 = new LdifEditorBot( "LDIF" );
        ldifEditorBot2.activate();
        ldifEditorBot2.typeText( "dn: dc=test\nobjectClass: domain\n\n" );
        assertTrue( ldifEditorBot2.isDirty() );
        ldifEditorBot2.close();
    }

}
