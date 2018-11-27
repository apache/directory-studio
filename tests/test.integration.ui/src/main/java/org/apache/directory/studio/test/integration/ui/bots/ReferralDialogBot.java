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
import org.eclipse.swtbot.swt.finder.utils.TableCollection;
import org.eclipse.swtbot.swt.finder.utils.TableRow;


public class ReferralDialogBot extends DialogBot
{

    public ReferralDialogBot()
    {
        super( "Select Referral Connection" );
    }


    public void clickOkButton()
    {
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__init_entries_title_subonly );
        super.clickOkButton();
        watcher.waitUntilDone();
    }


    public void selectConnection( String connectionName )
    {
        activate();
        bot.tree().select( connectionName );
    }


    public String getSelectedConnection()
    {
        activate();
        TableCollection selection = bot.tree().selection();
        if ( selection != null && selection.rowCount() == 1 )
        {
            TableRow row = selection.get( 0 );
            return row.get( 0 );
        }
        return null;
    }

}
