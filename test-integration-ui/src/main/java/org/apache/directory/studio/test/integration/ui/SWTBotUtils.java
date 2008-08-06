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
import net.sf.swtbot.finder.ControlFinder;
import net.sf.swtbot.finder.UIThreadRunnable;
import net.sf.swtbot.matcher.ClassMatcher;
import net.sf.swtbot.wait.DefaultCondition;
import net.sf.swtbot.wait.ICondition;
import net.sf.swtbot.widgets.SWTBotButton;
import net.sf.swtbot.widgets.SWTBotMenu;
import net.sf.swtbot.widgets.SWTBotTable;
import net.sf.swtbot.widgets.SWTBotTree;
import net.sf.swtbot.widgets.SWTBotTreeItem;
import net.sf.swtbot.widgets.TimeoutException;
import net.sf.swtbot.widgets.WidgetNotFoundException;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.eclipse.swt.widgets.Tree;


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
    public static void openLdapPerspective( final SWTEclipseBot eBot ) throws Exception
    {
        // open "Open Perspective" dialog
        SWTBotMenu windowMenu = eBot.menu( "Window" );
        windowMenu.click();
        SWTBotMenu perspectiveMenu = windowMenu.menu( "Open Perspective" );
        perspectiveMenu.click();
        SWTBotMenu otherMenu = windowMenu.menu( "Other..." );
        otherMenu.click();

        // select "LDAP" perspective
        SWTBotTable table = eBot.table();
        table.select( "LDAP" );

        // press "OK"
        SWTBotButton okButton = eBot.button( "OK" );
        okButton.click();

        // wait till Connections view become visible
        eBot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return eBot.view( "Connections" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );
    }


    /**
     * Creates the test connection.
     * 
     * @param bot the bot
     * @param name the name of the connection
     * @param port the port to use
     * 
     * @return the connection
     * 
     * @throws Exception the exception
     */
    public static Connection createTestConnection( SWTEclipseBot bot, String name, int port ) throws Exception
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
        return connection;
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


    /**
     * Gets the connections tree.
     * 
     * @param bot the bot
     * 
     * @return the connections tree
     * 
     * @throws Exception the exception
     */
    public static SWTBotTree getConnectionsTree( SWTEclipseBot bot ) throws Exception
    {
        SWTBotView view = bot.view( "Connections" );
        view.show();

        List<Tree> findControls = new ControlFinder().findControls( view.widget, new ClassMatcher( Tree.class ), true );
        if ( findControls.isEmpty() )
        {
            throw new WidgetNotFoundException( "Could not find Connections tree" );
        }
        return new SWTBotTree( findControls.get( 0 ) );
    }


    /**
     * Gets the ldap browser tree.
     * 
     * @param bot the bot
     * 
     * @return the ldap browser tree
     * 
     * @throws Exception the exception
     */
    public static SWTBotTree getLdapBrowserTree( SWTEclipseBot bot ) throws Exception
    {
        SWTBotView view = bot.view( "LDAP Browser" );
        view.show();

        List<Tree> findControls = new ControlFinder().findControls( view.widget, new ClassMatcher( Tree.class ), true );
        if ( findControls.isEmpty() )
        {
            throw new WidgetNotFoundException( "Could not find LDAP Browser tree" );
        }
        return new SWTBotTree( findControls.get( 0 ) );
    }


    /**
     * Clicks a button asynchronously and waits till the given condition
     * is fulfilled.
     * 
     * @param bot the SWT bot
     * @param button the button to click
     * @param waitCondition the condition to wait for, may be null
     * 
     * @throws TimeoutException 
     */
    public static void asyncClick( final SWTEclipseBot bot, final SWTBotButton button, final ICondition waitCondition )
        throws TimeoutException
    {
        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                return button.isEnabled();
            }


            public String getFailureMessage()
            {
                return "Button isn't enabled.";
            }
        } );

        UIThreadRunnable.asyncExec( bot.getDisplay(), new UIThreadRunnable.VoidResult()
        {
            public void run()
            {
                button.click();
            }
        } );

        if ( waitCondition != null )
        {
            bot.waitUntil( waitCondition );
        }
    }


    /**
     * Clicks a menu item asynchronously and waits till the given condition
     * is fulfilled.
     * 
     * @param bot the SWT bot
     * @param button the button to click
     * @param waitCondition the condition to wait for, may be null
     * 
     * @throws TimeoutException 
     */
    public static void asyncClick( final SWTEclipseBot bot, final SWTBotMenu menu, final ICondition waitCondition )
        throws TimeoutException
    {
        UIThreadRunnable.asyncExec( bot.getDisplay(), new UIThreadRunnable.VoidResult()
        {
            public void run()
            {
                menu.click();
            }
        } );

        if ( waitCondition != null )
        {
            bot.waitUntil( waitCondition );
        }
    }


    /**
     * Selects an entry in the browser tree and optionally expands the selected entry.
     * Takes care that all attributes and child entries are initialized so 
     * that there are no pending background actions and event notifications. 
     * This is necessary to avoid race conditions.
     * 
     * @param bot the SWT bot
     * @param tree the browser tree
     * @param expandChild true to expand the child entry
     * @param path the path to the entry
     * 
     * @return the selected entry as SWTBotTreeItem
     * 
     * @throws Exception the exception
     */
    public static SWTBotTreeItem selectEntry( final SWTEclipseBot bot, final SWTBotTree tree,
        final boolean expandChild, final String... path ) throws Exception
    {
        List<String> pathList = new ArrayList<String>( Arrays.asList( path ) );
        SWTBotTreeItem entry = null;

        while ( !pathList.isEmpty() )
        {
            String currentPath = pathList.remove( 0 );

            if ( entry == null )
            {
                entry = tree.getTreeItem( currentPath );
            }
            else
            {
                // adjust current path, because the label is decorated with the number of children
                currentPath = adjustNodeName( entry, currentPath );
                entry = entry.getNode( currentPath );
            }

            final SWTBotTreeItem tempEntry = entry;
            UIThreadRunnable.asyncExec( bot.getDisplay(), new UIThreadRunnable.VoidResult()
            {
                public void run()
                {
                    tempEntry.select();
                }
            } );

            if ( !pathList.isEmpty() || expandChild )
            {
                // expand entry and wait till 
                // - children are displayed
                // - next child is visible
                final String nextName = !pathList.isEmpty() ? pathList.get( 0 ) : null;
                expandEntry( bot, entry, nextName );
            }

        }
        return entry;
    }


    /**
     * Expands the entry.
     * Takes care that all attributes and child entries are initialized so 
     * that there are no pending background actions and event notifications. 
     * This is necessary to avoid race conditions.
     * 
     * @param bot the bot
     * @param entry the entry to expand
     * @param nextName the name of the entry that must become visible, may be null
     * 
     * @throws Exception the exception
     */
    public static void expandEntry( final SWTEclipseBot bot, final SWTBotTreeItem entry, final String nextName )
        throws Exception
    {
        UIThreadRunnable.asyncExec( bot.getDisplay(), new UIThreadRunnable.VoidResult()
        {
            public void run()
            {
                entry.expand();
            }
        } );

        bot.waitUntil( new DefaultCondition()
        {
            public boolean test() throws Exception
            {
                if ( nextName != null )
                {
                    String adjustedNodeName = nextName != null ? adjustNodeName( entry, nextName ) : null;
                    SWTBotTreeItem node = entry.getNode( adjustedNodeName );
                    if ( node == null )
                    {
                        return false;
                    }
                }
                return !entry.getNodes().contains( "Fetching Entries..." );
            }


            public String getFailureMessage()
            {
                return "Could not find entry " + entry.getText() + " -> " + nextName;
            }
        } );
    }


    private static String adjustNodeName( SWTBotTreeItem child, String nodeName )
    {
        List<String> nodes = child.getNodes();
        for ( String node : nodes )
        {
            if ( node.toUpperCase().startsWith( nodeName.toUpperCase() ) )
            {
                return node;
            }
        }
        return null;
    }

}
