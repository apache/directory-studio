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


public class RenameEntryDialogBot extends DialogBot
{

    public RenameEntryDialogBot()
    {
        super( "Rename Entry" );
    }


    public void clickOkButton()
    {
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__rename_entry_name );
        super.clickOkButton();
        watcher.waitUntilDone();
    }


    public void setRdnValue( int number, String text )
    {
        int index = number - 1;
        bot.text( index ).setText( text );
    }


    public void setRdnType( int number, String text )
    {
        int index = number - 1;
        bot.comboBox( index ).setText( text );
    }

}
