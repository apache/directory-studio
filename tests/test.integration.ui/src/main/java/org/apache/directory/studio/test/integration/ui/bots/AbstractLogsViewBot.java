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


import org.apache.commons.lang3.StringUtils;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarPushButton;


public class AbstractLogsViewBot
{

    protected SWTBotView view;

    public AbstractLogsViewBot( String title )
    {
        view = new SWTWorkbenchBot().viewByTitle( title );
    }


    public String getLogsText()
    {
        view.show();
        SWTBotToolbarPushButton refreshButton = view.toolbarPushButton( "Refresh" );
        if ( refreshButton.isEnabled() )
        {
            refreshButton.click();
        }
        return view.bot().styledText().getText();
    }


    public void waitForText( final String text )
    {
        view.show();

        view.bot().waitUntil( new DefaultCondition()
        {
            @Override
            public boolean test() throws Exception
            {
                SWTBotToolbarPushButton refreshButton = view.toolbarPushButton( "Refresh" );
                if ( refreshButton.isEnabled() )
                {
                    refreshButton.click();
                }
                return StringUtils.containsIgnoreCase( view.bot().styledText().getText(), text );
            }


            @Override
            public String getFailureMessage()
            {
                return "Text '" + text + "' not found.";
            }

        } );
    }


    public void clear()
    {
        view.show();
        view.toolbarPushButton( "Clear" ).click();
        new DialogBot( "Delete" )
        {
        }.clickOkButton();
    }


    public boolean isOlderButtonEnabled()
    {
        view.show();
        return view.toolbarPushButton( "Older" ).isEnabled();
    }


    public void clickOlderButton()
    {
        view.show();
        view.toolbarPushButton( "Older" ).click();
    }


    public boolean isNewerButtonEnabled()
    {
        view.show();
        return view.toolbarPushButton( "Newer" ).isEnabled();
    }


    public void clickNewerButton()
    {
        view.show();
        view.toolbarPushButton( "Newer" ).click();
    }

}
