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


import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.swtbot.eclipse.finder.SWTEclipseBot;


/**
 * Test suite to run all tests.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache DirectoryProject</a>
 * @version $Rev$, $Date$
 */
public class AutomatedSuite extends TestSuite
{

    public static Test suite()
    {
        return new AutomatedSuite();
    }


    public AutomatedSuite()
    {
        // Test Preparation: close welcome view
        try
        {
            SWTEclipseBot bot = new SWTEclipseBot();
            bot.view( "Welcome" ).close();
        }
        catch ( Exception e )
        {
        }

        // Test Connections view
        addTest( new TestSuite( NewConnectionWizardTest.class ) );

        // Test Import/Export
        addTest( new TestSuite( ImportExportTest.class ) );

        // Test Browser view
        addTest( new TestSuite( NewEntryWizardTest.class ) );
        addTest( new TestSuite( RenameEntryDialogTest.class ) );
        addTest( new TestSuite( ReferralDialogTest.class ) );

        // Test Entry editor
        addTest( new TestSuite( EntryEditorTest.class ) );

        // Test allocated resources
        // addTest( new TestSuite( SwtResourcesTest.class ) );
    }

}