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
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarToggleButton;


public class SchemaSearchViewBot
{
    private SWTBotView view;


    public SchemaSearchViewBot()
    {
        view = new SWTWorkbenchBot().viewByTitle( "Search" );
    }


    public void search( String text )
    {
        view.show();
        SWTBotToolbarToggleButton button = view.toolbarToggleButton( "Show Search Field" );
        button.select();
        view.bot().text().setText( text );
        view.bot().text().pressShortcut( Keystrokes.LF );
    }


    public List<String> getResults()
    {
        List<String> results = new ArrayList<String>();
        SWTBotTable table = view.bot().table();
        for ( int i = 0; i < table.rowCount(); i++ )
        {
            String text = table.getTableItem( i ).getText();
            results.add( text );
        }
        return results;
    }

}
