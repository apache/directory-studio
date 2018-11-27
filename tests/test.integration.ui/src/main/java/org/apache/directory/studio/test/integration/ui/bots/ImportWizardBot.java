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


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;


public class ImportWizardBot extends WizardBot
{
    public static final String IMPORT_LDIF_TITLE = "LDIF Import";
    public static final String IMPORT_DSML_TITLE = "DSML Import";
    private String title;


    public ImportWizardBot()
    {
        this( "Import" );
    }


    public ImportWizardBot( String title )
    {
        super( title );
        this.title = title;
    }


    public void typeFile( String file )
    {
        bot.comboBox().setText( file );
    }


    @Override
    public void clickFinishButton()
    {
        JobWatcher watcher = null;
        if ( IMPORT_LDIF_TITLE.equals( title ) )
        {
            watcher = new JobWatcher( BrowserCoreMessages.jobs__import_ldif_name );
        }
        else if ( IMPORT_DSML_TITLE.equals( title ) )
        {
            watcher = new JobWatcher( BrowserCoreMessages.jobs__import_dsml_name );
        }

        super.clickFinishButton();

        if ( watcher != null )
        {
            watcher.waitUntilDone();
        }
    }

}
