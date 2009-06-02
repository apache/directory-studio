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


import static org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.syncExec;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.hamcrest.Matchers.anything;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotWorkbenchPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


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
     * @param bot
     *            the bot
     * 
     * @throws Exception
     *             the exception
     */
    public static void openLdapPerspective( final SWTWorkbenchBot eBot ) throws Exception
    {
        // optimization: only open LDAP perspective if not yet opened
        try
        {
            if ( eBot.viewByTitle( "Connections" ) != null )
            {
                // LDAP perspective already opened
                return;
            }
        }
        catch ( Exception e )
        {
            // no 'Connections' view, continue to open the LDAP perspective
        }

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
                return eBot.viewByTitle( "Connections" ) != null;
            }


            public String getFailureMessage()
            {
                return "Could not find widget";
            }
        } );

        // close welcome view, if it is there
        try
        {
            eBot.viewByTitle( "Welcome" ).close();
        }
        catch ( Exception e )
        {
        }
    }


    /**
     * Creates the test connection.
     * 
     * @param bot
     *            the bot
     * @param name
     *            the name of the connection
     * @param port
     *            the port to use
     * 
     * @return the connection
     * 
     * @throws Exception
     *             the exception
     */
    public static Connection createTestConnection( SWTWorkbenchBot bot, String name, int port ) throws Exception
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
        // new OpenConnectionsJob( connection ).execute();

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
     * @param bot
     *            the bot
     * 
     * @return the connections tree
     * 
     * @throws Exception
     *             the exception
     */
    public static SWTBotTree getConnectionsTree( SWTWorkbenchBot bot ) throws Exception
    {
        SWTBotView view = bot.viewByTitle( "Connections" );
        view.show();

        Tree tree = ( Tree ) bot.widget( widgetOfType( Tree.class ), view.getWidget() );
        return new SWTBotTree( tree );
    }


    /**
     * Gets the ldap browser tree.
     * 
     * @param bot
     *            the bot
     * 
     * @return the ldap browser tree
     * 
     * @throws Exception
     *             the exception
     */
    public static SWTBotTree getLdapBrowserTree( SWTWorkbenchBot bot ) throws Exception
    {
        SWTBotView view = bot.viewByTitle( "LDAP Browser" );
        view.show();
        Tree tree = ( Tree ) bot.widget( widgetOfType( Tree.class ), view.getWidget() );
        return new SWTBotTree( tree );
    }


    /**
     * Gets the search logs text.
     * 
     * @param bot
     *            the bot
     * 
     * @return the search logs text
     * 
     * @throws Exception
     *             the exception
     */
    public static SWTBotStyledText getSearchLogsText( SWTWorkbenchBot bot ) throws Exception
    {
        SWTBotView view = bot.viewByTitle( "Search Logs" );
        view.show();
        view.toolbarButton( "Refresh" ).click();
        StyledText styledText = ( StyledText ) bot.widget( widgetOfType( StyledText.class ), view.getWidget() );
        return new SWTBotStyledText( styledText );
    }


    /**
     * Gets the modification logs text.
     * 
     * @param bot
     *            the bot
     * 
     * @return the modification logs text
     * 
     * @throws Exception
     *             the exception
     */
    public static SWTBotStyledText getModificationLogsText( SWTWorkbenchBot bot ) throws Exception
    {
        SWTBotView view = bot.viewByTitle( "Modification Logs" );
        view.show();
        view.toolbarButton( "Refresh" ).click();
        StyledText styledText = ( StyledText ) bot.widget( widgetOfType( StyledText.class ), view.getWidget() );
        return new SWTBotStyledText( styledText );
    }


    /**
     * Gets the entry editor tree.
     * 
     * @param bot
     *            the bot
     * 
     * @return the entry editor tree
     * 
     * @throws Exception
     *             the exception
     */
    public static SWTBotTree getEntryEditorTree( final SWTWorkbenchBot bot ) throws Exception
    {
        Tree tree = UIThreadRunnable.syncExec( new WidgetResult<Tree>()
        {
            public Tree run()
            {
                IWorkbench workbench = PlatformUI.getWorkbench();
                IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
                IWorkbenchPage[] pages = activeWorkbenchWindow.getPages();
                for ( int i = 0; i < pages.length; i++ )
                {
                    IWorkbenchPage page = pages[i];
                    IEditorReference[] editorReferences = page.getEditorReferences();
                    for ( int j = 0; j < editorReferences.length; j++ )
                    {
                        IEditorReference editorReference = editorReferences[j];
                        if ( editorReference.getName().equals( "Entry Editor" ) )
                        {
                            DummyEditor editor = new DummyEditor( editorReference, bot );
                            Tree tree = ( Tree ) bot.widget( widgetOfType( Tree.class ), editor.widget );
                            return tree;

                        }
                    }
                }
                throw new WidgetNotFoundException( "Could not find Entry Editor tree" );
            }
        } );
        return new SWTBotTree( tree );
    }

    static class DummyEditor extends SWTBotWorkbenchPart<IEditorReference>
    {
        public Widget widget;


        public DummyEditor( IEditorReference editorReference, SWTWorkbenchBot bot )
        {
            super( editorReference, bot );
            widget = findWidget( anything() );
        }


        public void setFocus()
        {
            syncExec( new VoidResult()
            {
                public void run()
                {
                    ( ( Control ) widget ).setFocus();
                }
            } );
        }


        public boolean isActive()
        {
            return true;
        }
    }


    /**
     * Clicks a button asynchronously and waits till the given condition is
     * fulfilled.
     * 
     * @param bot
     *            the SWT bot
     * @param button
     *            the button to click
     * @param waitCondition
     *            the condition to wait for, may be null
     * 
     * @throws TimeoutException
     */
    public static void asyncClick( final SWTWorkbenchBot bot, final SWTBotButton button, final ICondition waitCondition )
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

        UIThreadRunnable.asyncExec( bot.getDisplay(), new VoidResult()
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
     * Clicks a menu item asynchronously and waits till the given condition is
     * fulfilled.
     * 
     * @param bot
     *            the SWT bot
     * @param button
     *            the button to click
     * @param waitCondition
     *            the condition to wait for, may be null
     * 
     * @throws TimeoutException
     */
    public static void asyncClick( final SWTWorkbenchBot bot, final SWTBotMenu menu, final ICondition waitCondition )
        throws TimeoutException
    {
        UIThreadRunnable.asyncExec( bot.getDisplay(), new VoidResult()
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
     * Clicks a tree item asynchronously and waits till the given condition is
     * fulfilled.
     * 
     * @param bot
     *            the SWT bot
     * @param item
     *            the tree item to click
     * @param waitCondition
     *            the condition to wait for, may be null
     * 
     * @throws TimeoutException
     *             the timeout exception
     */
    public static void asyncClick( final SWTWorkbenchBot bot, final SWTBotTreeItem item, final ICondition waitCondition )
        throws TimeoutException
    {
        UIThreadRunnable.asyncExec( bot.getDisplay(), new VoidResult()
        {
            public void run()
            {
                item.click();
            }
        } );

        if ( waitCondition != null )
        {
            bot.waitUntil( waitCondition );
        }
    }


    /**
     * Selects an entry in the browser tree and optionally expands the selected
     * entry. Takes care that all attributes and child entries are initialized
     * so that there are no pending background actions and event notifications.
     * This is necessary to avoid race conditions.
     * 
     * @param bot
     *            the SWT bot
     * @param tree
     *            the browser tree
     * @param expandChild
     *            true to expand the child entry
     * @param path
     *            the path to the entry
     * 
     * @return the selected entry as SWTBotTreeItem
     * 
     * @throws Exception
     *             the exception
     */
    public static SWTBotTreeItem selectEntry( final SWTWorkbenchBot bot, final SWTBotTree tree,
        final boolean expandChild, final String... path ) throws Exception
    {
        List<String> pathList = new ArrayList<String>( Arrays.asList( path ) );
        SWTBotTreeItem entry = null;

        while ( !pathList.isEmpty() )
        {
            String currentPath = pathList.remove( 0 );

            if ( entry == null )
            {
                currentPath = adjustNodeName( tree, currentPath );
                entry = tree.getTreeItem( currentPath );
            }
            else
            {
                // adjust current path, because the label is decorated with the
                // number of children
                currentPath = adjustNodeName( entry, currentPath );
                entry = entry.getNode( currentPath );
            }

            final SWTBotTreeItem tempEntry = entry;
            UIThreadRunnable.asyncExec( bot.getDisplay(), new VoidResult()
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
     * Expands the entry. Takes care that all attributes and child entries are
     * initialized so that there are no pending background actions and event
     * notifications. This is necessary to avoid race conditions.
     * 
     * @param bot
     *            the bot
     * @param entry
     *            the entry to expand
     * @param nextName
     *            the name of the entry that must become visible, may be null
     * 
     * @throws Exception
     *             the exception
     */
    public static void expandEntry( final SWTWorkbenchBot bot, final SWTBotTreeItem entry, final String nextName )
        throws Exception
    {
        UIThreadRunnable.asyncExec( bot.getDisplay(), new VoidResult()
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


    private static String adjustNodeName( SWTBotTree tree, String nodeName )
    {
        SWTBotTreeItem[] allItems = tree.getAllItems();
        for ( SWTBotTreeItem item : allItems )
        {
            String node = item.getText();
            if ( node.toUpperCase().startsWith( nodeName.toUpperCase() ) )
            {
                return node;
            }
        }
        return null;
    }

}
