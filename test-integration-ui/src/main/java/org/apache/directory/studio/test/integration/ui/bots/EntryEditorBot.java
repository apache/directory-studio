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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;


public class EntryEditorBot
{

    private SWTBotEditor editor;
    private SWTBot bot;


    public EntryEditorBot( String title )
    {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        editor = bot.editorByTitle( title );
        this.bot = editor.bot();
    }


    public String getDnText()
    {
        String text = bot.text().getText();
        return text;
    }


    public boolean isEnabled()
    {
        return bot.tree().isEnabled();
    }


    public List<String> getAttributeValues()
    {
        SWTBotTree tree = bot.tree();
        List<String> attributes = new ArrayList<String>();
        int rowCount = tree.rowCount();
        for ( int i = 0; i < rowCount; i++ )
        {
            String attribute = tree.cell( i, 0 );
            String value = tree.cell( i, 1 );
            attributes.add( attribute + ": " + value );
        }
        return attributes;
    }

}
