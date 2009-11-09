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


import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;


public class EntryEditorBot
{

    private SWTBotEditor editor;
    private SWTBot bot;
    private EntryEditorWidgetBot editorBot;


    public EntryEditorBot( String title )
    {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        editor = bot.editorByTitle( title );
        this.bot = editor.bot();
        this.editorBot = new EntryEditorWidgetBot( editor.bot() );
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
        return editorBot.getAttributeValues();
    }


    public NewAttributeWizardBot openNewAttributeWizard()
    {
        return editorBot.openNewAttributeWizard();
    }


    public void activate()
    {
        editor.setFocus();
        bot.tree().setFocus();
    }


    public void typeValueAndFinish( String value )
    {
        editorBot.typeValueAndFinish( value );
    }


    public void addValue( String attributeType )
    {
        editorBot.addValue( attributeType );
    }


    public void editValue( String attributeType, String value )
    {
        editorBot.editValue( attributeType, value );
    }


    public DnEditorDialogBot editValueExpectingDnEditor( String attributeType, String value )
    {
        return editorBot.editValueExpectingDnEditor( attributeType, value );
    }


    public void deleteValue( String attributeType, String value )
    {
        editorBot.deleteValue( attributeType, value );
    }

}
