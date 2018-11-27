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


import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;


public class LdifEditorBot
{
    private SWTBotEditor editor;


    public LdifEditorBot( String title )
    {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        editor = bot.editorByTitle( title );
    }


    public void typeText( String text )
    {
        SWTBotPreferences.KEYBOARD_LAYOUT = "org.eclipse.swtbot.swt.finder.keyboard.EN_US";
        SWTBotEclipseEditor textEditor = editor.toTextEditor();
        textEditor.typeText( text );
    }


    public void activate()
    {
        editor.setFocus();
    }


    public void close()
    {
        editor.close();
    }


    public boolean isDirty()
    {
        return editor.isDirty();
    }

}
