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


import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withStyle;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withTooltip;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarPushButton;
import org.hamcrest.Matcher;


public class SearchResultEditorBot
{

    private SWTBotEditor editor;
    private SWTBot bot;


    public SearchResultEditorBot( String title )
    {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        editor = bot.editorByTitle( title );
        this.bot = editor.bot();
    }


    public boolean isEnabled()
    {
        return bot.table().isEnabled();
    }


    public void activate()
    {
        editor.setFocus();
        bot.table().setFocus();
    }


    public String getContent( int row, int column )
    {
        return bot.table().cell( row - 1, column - 1 );
    }


    public void refresh()
    {
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__search_name );
        //bot.toolbarButton( "Search Again (F5)" ).click();
        Matcher<Widget> matcher = allOf( widgetOfType( ToolItem.class ), withTooltip( "Search Again (F5)" ), withStyle(
            SWT.PUSH, "SWT.PUSH" ) );
        SWTBotToolbarPushButton button = new SWTBotToolbarPushButton( ( ToolItem ) bot.widget( matcher, 0 ), matcher );
        button.click();
        watcher.waitUntilDone();
    }

}
