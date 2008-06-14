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

package org.apache.directory.studio.test.integration.ui;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.swtbot.eclipse.finder.SWTEclipseBot;
import net.sf.swtbot.eclipse.finder.widgets.SWTBotView;
import net.sf.swtbot.wait.DefaultCondition;
import net.sf.swtbot.widgets.SWTBotButton;
import net.sf.swtbot.widgets.SWTBotMenu;
import net.sf.swtbot.widgets.SWTBotTable;
import net.sf.swtbot.widgets.SWTBotTree;
import net.sf.swtbot.widgets.SWTBotTreeItem;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;


/**
 * Helpers for using SWTBot.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SWTBotUtils
{

    /**
     * Opens the LDAP perspective.
     * 
     * @param bot the bot
     * 
     * @throws Exception the exception
     */
    public static void openLdapPerspective( SWTEclipseBot bot ) throws Exception
    {
        // open "Open Perspective" dialog
        SWTBotMenu windowMenu = bot.menu( "Window" );
        windowMenu.click();
        SWTBotMenu perspectiveMenu = windowMenu.menu( "Open Perspective" );
        perspectiveMenu.click();
        SWTBotMenu otherMenu = windowMenu.menu( "Other..." );
        otherMenu.click();

        // select "LDAP" perspective
        SWTBotTable table = bot.table();
        table.select( "LDAP" );

        // press "OK"
        SWTBotButton okButton = bot.button( "OK" );
        okButton.click();
    }


    /**
     * Creates the test connection.
     * 
     * @throws Exception the exception
     */
    public static void createTestConnection( SWTEclipseBot bot, String name, int port ) throws Exception
    {
        SWTBotTree connectionsTree = getConnectionsTree( bot );

        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        ConnectionParameter connectionParameter = new ConnectionParameter();
        connectionParameter.setName( name );
        connectionParameter.setHost( "localhost" );
        connectionParameter.setPort( port );
        connectionParameter.setEncryptionMethod( EncryptionMethod.NONE );
        connectionParameter.setAuthMethod( AuthenticationMethod.SIMPLE );
        connectionParameter.setBindPrincipal( "uid=admin,ou=system" );
        connectionParameter.setBindPassword( "secret" );
        Connection connection = new Connection( connectionParameter );
        connectionManager.addConnection( connection );

        ConnectionFolderManager connectionFolderManager = ConnectionCorePlugin.getDefault()
            .getConnectionFolderManager();
        ConnectionFolder rootConnectionFolder = connectionFolderManager.getRootConnectionFolder();
        rootConnectionFolder.addConnectionId( connection.getId() );

        connectionsTree.select( name );
        //new OpenConnectionsJob( connection ).execute();

        Thread.sleep( 1000 );
    }


    /**
     * Deletes the test connection.
     */
    public static void deleteTestConnections()
    {
        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        for ( Connection connection : connectionManager.getConnections() )
        {
            connectionManager.removeConnection( connection );
        }
    }


    public static SWTBotTree getConnectionsTree( SWTEclipseBot bot ) throws Exception
    {
        SWTBotView view = bot.view( "Connections" );
        view.show();
        SWTBotTree connectionsTree = bot.tree( 0 );
        return connectionsTree;
    }


    public static SWTBotTree getLdapBrowserTree( SWTEclipseBot bot ) throws Exception
    {
        SWTBotView view = bot.view( "LDAP Browser" );
        view.show();
        SWTBotTree browserTree = bot.tree( 1 );
        return browserTree;
    }


    public static SWTBotTreeItem selectNode( SWTEclipseBot bot, SWTBotTree browserTree, String... path )
        throws Exception
    {
        List<String> pathList = new ArrayList<String>( Arrays.asList( path ) );
        String currentPath = pathList.remove( 0 );

        SWTBotTreeItem child = browserTree.getTreeItem( currentPath );
        child.select();
        child.expand();

        if ( !pathList.isEmpty() )
        {
            return selectNode( bot, child, pathList );
        }
        else
        {
            return child;
        }
    }


    public static SWTBotTreeItem selectNode( SWTEclipseBot bot, final SWTBotTreeItem item, List<String> pathList )
        throws Exception
    {
        final String[] currentPath = new String[]
            { pathList.remove( 0 ) };

        // wait till node becomes visible
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                List<String> nodes = item.getNodes();
                for ( String node : nodes )
                {
                    if ( node.toUpperCase().startsWith( currentPath[0].toUpperCase() ) )
                    {
                        currentPath[0] = node;
                        return true;
                    }
                }
                return false;
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );

        SWTBotTreeItem child = item.expandNode( currentPath[0] );
        child.select();
        child.expand();

        if ( !pathList.isEmpty() )
        {
            return selectNode( bot, child, pathList );
        }
        else
        {
            return child;
        }
    }

}
