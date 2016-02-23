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
package org.apache.directory.studio.test.integration.ui.bots;


import java.io.File;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;


public class ExportWizardBot extends WizardBot
{
    public static final String EXPORT_LDIF_TITLE = "LDIF Export";
    public static final String EXPORT_DSML_TITLE = "DSML Export";
    public static final String EXPORT_CSV_TITLE = "CSV Export";
    private String title;


    public ExportWizardBot()
    {
        this( "Export" );
    }


    public ExportWizardBot( String title )
    {
        super( title );
        this.title = title;
    }


    public void typeReturningAttributes( String returningAttributes )
    {
        bot.comboBoxWithLabel( "Returning Attributes:" ).setText( returningAttributes );
    }


    public void typeFile( String file )
    {
        bot.comboBox().setText( file );
    }


    @Override
    public void clickFinishButton()
    {
        JobWatcher watcher = null;
        if ( EXPORT_LDIF_TITLE.equals( title ) )
        {
            watcher = new JobWatcher( BrowserCoreMessages.jobs__export_ldif_name );
        }
        else if ( EXPORT_DSML_TITLE.equals( title ) )
        {
            watcher = new JobWatcher( BrowserCoreMessages.jobs__export_dsml_name );
        }

        super.clickFinishButton();

        if ( watcher != null )
        {
            watcher.waitUntilDone();
        }
    }


    public void waitTillExportFinished( final String file, final int expectedFileSize )
    {
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                File f = new File( file );
                return f.exists() && f.length() > expectedFileSize;
            }


            public String getFailureMessage()
            {
                return "LDIF File " + file + " not found.";
            }
        }, SWTBotPreferences.TIMEOUT * 2 );
    }


    public void selectDsmlRequest()
    {
        bot.radio( "DSML Request" ).click();
    }

}
