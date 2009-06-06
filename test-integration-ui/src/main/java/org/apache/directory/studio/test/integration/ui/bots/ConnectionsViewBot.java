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


import org.apache.directory.studio.test.integration.ui.ContextMenuHelper;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


public class ConnectionsViewBot
{
    private SWTWorkbenchBot bot = new SWTWorkbenchBot();


    public NewConnectionWizardBot openNewConnectionWizard()
    {
        ContextMenuHelper.clickContextMenu( getConnectionsTree(), "New Connection..." );
        return new NewConnectionWizardBot();
    }


    public void closeSelectedConnections()
    {
        getConnectionsTree().contextMenu( "Close Connection" ).click();
    }


    public int getConnectionCount()
    {
        return getConnectionsTree().rowCount();
    }


    private SWTBotTree getConnectionsTree()
    {
        SWTBotView view = bot.viewByTitle( "Connections" );
        view.show();
        SWTBotTree tree = view.bot().tree();
        return tree;
    }


    public void waitForConnection( final String connectionName )
    {
        bot.waitUntil( new DefaultCondition()
        {

            public boolean test() throws Exception
            {
                for ( SWTBotTreeItem item : getConnectionsTree().getAllItems() )
                {
                    String text = item.getText();
                    if ( text.startsWith( connectionName ) )
                    {
                        return true;
                    }
                }
                return false;
            }


            public String getFailureMessage()
            {
                return "Connection " + connectionName + " not visible in connectoins view.";
            }
        } );

    }

}
