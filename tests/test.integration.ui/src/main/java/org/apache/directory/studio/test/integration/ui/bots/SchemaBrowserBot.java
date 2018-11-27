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

import org.eclipse.swt.widgets.Text;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotMultiPageEditor;
import org.eclipse.swtbot.swt.finder.matchers.WithRegex;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.hamcrest.Matcher;


public class SchemaBrowserBot
{
    private SWTBotMultiPageEditor editor;


    public SchemaBrowserBot()
    {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        editor = bot.multipageEditorByTitle( "Schema Browser" );
        bot.waitUntil( new DefaultCondition()
        {
            @Override
            public boolean test() throws Exception
            {
                return editor.getPageCount() >= 5;
            }


            @Override
            public String getFailureMessage()
            {
                return "ApacheDS configuration editor not ready";
            }
        } );
    }


    public void activateObjectClassesTab()
    {
        editor.activatePage( "Object Classes" );
    }


    public void selectObjectClass( String oc )
    {
        activateObjectClassesTab();
        editor.bot().table().select( oc );
    }


    public String getRawSchemaDefinition()
    {
        Matcher matcher = allOf( widgetOfType( Text.class ), WithRegex.withRegex( ".*NAME.*" ) );
        SWTBotText text = new SWTBotText( ( Text ) editor.bot().widget( matcher, 0 ), matcher );
        return text.getText();
    }

}
