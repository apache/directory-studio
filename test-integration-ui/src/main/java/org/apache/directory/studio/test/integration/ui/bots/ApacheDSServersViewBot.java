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


    /**
     * Shows the view.
     */
    public void show()
    {
        view.show();
    }


    /**
     * Opens the 'New Server' wizard.
     *
     * @return
     *      a bot associated with the 'New Server' wizard
     */
    public NewApacheDSServerWizardBot openNewServerWizard()
    {
        ContextMenuHelper.clickContextMenu( getServersTree(), "New", "New &Server" );
        return new NewApacheDSServerWizardBot();
    }


    /**
     * Opens the 'LDAP Browser' > 'Create a Connection' action of the context menu.
     *
     * @return
     *      a bot associated with the dialog
     */
    public ConnectionFromServerDialogBot createConnectionFromServer()
    {
        ContextMenuHelper.clickContextMenu( getServersTree(), "LDAP Browser", "Create a Connection" );
        return new ConnectionFromServerDialogBot();
    }


    /**
     * Opens the 'Delete' dialog.
     *
     * @return
     *      a bot associated with the 'Delete' dialog
     */
    public DeleteDialogBot openDeleteServerDialog()
    {
        ContextMenuHelper.clickContextMenu( getServersTree(), "Delete" );
        return new DeleteDialogBot( DeleteDialogBot.DELETE_SERVER );
    }


    /**
     * Gets the tree associated with the 'Servers' view.
     *
     * @return
     *      the tree associated with the 'Servers' view
     */
    private SWTBotTree getServersTree()
    {
        view.show();
        SWTBotTree tree = view.bot().tree();
        return tree;
    }


    /**
     * Selects the server associated with the given name.
     *
     * @param serverName
     *      the name of the server
     */
    public void selectServer( String serverName )
    {
        getServersTree().select( serverName );
    }


    /**
     * Starts the server associated with the given name.
     *
     * @param serverName
     *      the name of the server
     */
    public void runServer( String serverName )
    {
        getServersTree().select( serverName );
        ContextMenuHelper.clickContextMenu( getServersTree(), "&Run" );
    }


    /**
     * Stops the server associated with the given name.
     *
     * @param serverName
     *      the name of the server
     */
    public void stopServer( String serverName )
    {
        getServersTree().select( serverName );
        ContextMenuHelper.clickContextMenu( getServersTree(), "S&top" );
    }


    /**
     * Waits until the server associated with the given name appears in 
     * the 'servers' view.
     *
     * @param serverName
     *      the name of the server
     */
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


    /**
     * Waits until the server associated with the given name is started.
     *
     * @param serverName
     *      the server name
     */
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


    /**
     * Waits until the server associated with the given name is stopped.
     *
     * @param serverName
     *      the name of the server
     */
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


    /**
     * Gets the server associated with the given name.
     *
     * @param serverName
     *      the name of the server
     * @return
     *      the server associated with the given name,
     *      or <code>null</code> if none was found.
     */
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


    /**
     * Gets the servers count found in the 'Servers' view.
     *
     * @return
     *      the servers count found in the 'Servers' view
     */
    public int getServersCount()
    {
        SWTBotTree tree = getServersTree();
        if ( tree != null )
        {
            return tree.rowCount();
        }

        return 0;
    }
}
