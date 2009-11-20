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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.hamcrest.Matcher;


public class ModificationLogsViewBot
{

    private SWTBotView view;


    public ModificationLogsViewBot()
    {
        view = new SWTWorkbenchBot().viewByTitle( "Modification Logs" );
    }


    public String getModificationLogsText()
    {
        view.show();

        //view.toolbarButton( "Refresh" ).click();
        // just a workaround till view.toolbarButton() is fixed
        Matcher matcher = allOf( widgetOfType( ToolItem.class ), withTooltip( "Refresh" ), withStyle( SWT.PUSH,
            "SWT.PUSH" ) );
        SWTBotToolbarButton button = new SWTBotToolbarButton( ( ToolItem ) new SWTWorkbenchBot().widget( matcher, 0 ),
            matcher );
        button.click();

        return view.bot().styledText().getText();
    }

}
