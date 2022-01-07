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


public class SelectCopyDepthDialogBot extends DialogBot
{
    private String objectText;
    private String oneLevelText;
    private String subTreeText;

    public SelectCopyDepthDialogBot( int numEntries )
    {
        super( "Select Copy Depth" );
        if ( numEntries > 1 )
        {
            objectText = "Object (Only the copied entries)";
            oneLevelText = "One Level (Only copied entries and their direct children)";
            subTreeText = "Subtree (The whole subtrees)";
            super.setWaitAfterClickOkButton( true, BrowserCoreMessages.jobs__copy_entries_name_n );
        }
        else
        {
            objectText = "Object (Only the copied entry)";
            oneLevelText = "One Level (Only copied entry and its direct children)";
            subTreeText = "Subtree (The whole subtree)";
            super.setWaitAfterClickOkButton( true, BrowserCoreMessages.jobs__copy_entries_name_1 );
        }
        activate();
    }


    public void selectObject()
    {
        bot.radio( objectText ).click();
    }


    public void selectOneLevel()
    {
        bot.radio( oneLevelText ).click();
    }


    public void selectSubTree()
    {
        bot.radio( subTreeText ).click();
    }

}
