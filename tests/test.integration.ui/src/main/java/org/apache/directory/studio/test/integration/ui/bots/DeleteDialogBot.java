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


public class DeleteDialogBot extends DialogBot
{
    public static final String DELETE_CONNECTION = "Delete Connection";
    public static final String DELETE_CONNECTION_FOLDER = "Delete Connection Folder";
    public static final String DELETE_ENTRY_TITLE = "Delete Entry";
    public static final String DELETE_ENTRIES_TITLE = "Delete Entries";
    public static final String DELETE_VALUE_TITLE = "Delete Value";
    public static final String DELETE_SERVER = "Delete Server";
    public static final String DELETE_PROJECT = "Delete Project";


    public DeleteDialogBot( String title )
    {
        super( title );
    }


    public void clickOkButton()
    {
        String jobWatcherTitle = getJobWatcherTitle( title );
        if ( jobWatcherTitle != null )
        {
            JobWatcher watcher = new JobWatcher( jobWatcherTitle );
            super.clickOkButton();
            watcher.waitUntilDone();
        }
        else
        {
            super.clickOkButton();
        }
    }


    private String getJobWatcherTitle( String dialogTitle )
    {
        switch ( dialogTitle )
        {
            case DELETE_VALUE_TITLE:
                return BrowserCoreMessages.jobs__execute_ldif_name;
            case DELETE_CONNECTION:
            case DELETE_CONNECTION_FOLDER:
            case DELETE_PROJECT:
                return null;
            default:
                return dialogTitle;
        }
    }


    public void clickCancelButton()
    {
        super.clickButton( "Cancel" );
    }
}
