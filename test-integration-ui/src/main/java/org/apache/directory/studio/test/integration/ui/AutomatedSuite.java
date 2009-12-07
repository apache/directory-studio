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


import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.SetupMode;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.core.integ.annotations.Mode;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Test suite.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
    { BrowserTest.class, EntryEditorTest.class, ImportExportTest.class, NewConnectionWizardTest.class,
        NewEntryWizardTest.class, PreferencesTest.class, ReferralDialogTest.class, RenameEntryDialogTest.class,
        SearchTest.class })
@CleanupLevel(Level.SUITE)
@Mode(SetupMode.ROLLBACK)
public class AutomatedSuite
{
    static
    {
        ErrorDialog.AUTOMATED_MODE = false;
        SWTBotPreferences.PLAYBACK_DELAY = 50;
    }

}
