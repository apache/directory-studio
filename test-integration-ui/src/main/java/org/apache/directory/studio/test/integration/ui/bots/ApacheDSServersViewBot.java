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


import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.model.ServerStateEnum;
import org.apache.directory.studio.apacheds.model.ServersHandler;
import org.apache.directory.studio.test.integration.ui.ContextMenuHelper;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


public class ApacheDSServersViewBot
{
    private SWTWorkbenchBot bot = new SWTWorkbenchBot();

    private SWTBotView view;


    public ApacheDSServersViewBot()
    {
        view = new SWTWorkbenchBot().viewByTitle( "Servers" );
    }


    public void show()
    {
        view.show();
    }


    public NewApacheDSServerWizardBot openNewServerWizard()
    {
        ContextMenuHelper.clickContextMenu( getServersTree(), "New", "New &Server" );
        return new NewApacheDSServerWizardBot();
    }


    private SWTBotTree getServersTree()
    {
        view.show();
        SWTBotTree tree = view.bot().tree();
        return tree;
    }


    private SWTBotTable getServersTable()
    {
        view.show();
        SWTBotTable table = view.bot().table();
        return table;
    }


    public void selectServer( String serverName )
    {
        getServersTree().select( serverName );
    }


    public void runServer( String serverName )
    {
        getServersTree().select( serverName );
        ContextMenuHelper.clickContextMenu( getServersTree(), "&Run" );
    }
    
    public void stopServer( String serverName )
    {
        getServersTree().select( serverName );
        ContextMenuHelper.clickContextMenu( getServersTree(), "S&top" );
    }


    public void waitForServer( final String serverName )
    {
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                for ( SWTBotTreeItem item : getServersTree().getAllItems() )
                {
                    String text = item.getText();
                    if ( text.startsWith( serverName ) )
                    {
                        return true;
                    }
                }
                return false;
            }


            public String getFailureMessage()
            {
                return "Server " + serverName + " not visible in servers view.";
            }
        } );
    }


    public void waitForServerStart( final String serverName )
    {
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                Server server = getServer( serverName );
                if ( server != null )
                {
                    return ( ServerStateEnum.STARTED == server.getState() );
                }

                return false;
            }


            public String getFailureMessage()
            {
                return "Server " + serverName + " not started in servers view.";
            }
        }, 20000 );
    }


    public void waitForServerStop( final String serverName )
    {
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                Server server = getServer( serverName );
                if ( server != null )
                {
                    return ( ServerStateEnum.STOPPED == server.getState() );
                }

                return false;
            }


            public String getFailureMessage()
            {
                return "Server " + serverName + " not stopped in servers view.";
            }
        }, 10000 );
    }


    private Server getServer( String serverName )
    {
        for ( Server server : ServersHandler.getDefault().getServersList() )
        {
            if ( serverName.equals( server.getName() ) )
            {
                return server;
            }
        }

        return null;
    }
}
