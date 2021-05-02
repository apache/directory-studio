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


import static org.apache.directory.studio.test.integration.ui.utils.Constants.LOCALHOST;

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
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.jobs.OpenConnectionsRunnable;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionJob;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.utils.ContextMenuHelper;
import org.apache.directory.studio.test.integration.ui.utils.JobWatcher;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.TableCollection;
import org.eclipse.swtbot.swt.finder.utils.TableRow;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


public class ConnectionsViewBot
{
    private SWTWorkbenchBot bot = new SWTWorkbenchBot();

    public NewConnectionWizardBot openNewConnectionWizard()
    {
        ContextMenuHelper.clickContextMenu( getConnectionsTree(), "New Connection..." );
        NewConnectionWizardBot newConnectionWizardBot = new NewConnectionWizardBot();
        return newConnectionWizardBot;
    }


    public NewConnectionFolderDialogBot openNewConnectionFolderDialog()
    {
        ContextMenuHelper.clickContextMenu( getConnectionsTree(), "New Connection Folder..." );
        return new NewConnectionFolderDialogBot();
    }


    public void openSelectedConnection()
    {
        JobWatcher watcher = new JobWatcher( Messages.jobs__open_connections_name_1 );
        getConnectionsTree().contextMenu( "Open Connection" ).click();
        watcher.waitUntilDone();
    }


    public void openSelectedConnectionExpectingVerifyMasterPasswordDialog( String masterPassword )
    {
        JobWatcher watcher = new JobWatcher( Messages.jobs__open_connections_name_1 );
        getConnectionsTree().contextMenu( "Open Connection" ).click();
        VerifyMasterPasswordDialogBot verifyMasterPasswordDialogBot = new VerifyMasterPasswordDialogBot();
        verifyMasterPasswordDialogBot.enterMasterPassword( masterPassword );
        verifyMasterPasswordDialogBot.clickOkButton();
        watcher.waitUntilDone();
    }


    public ErrorDialogBot openSelectedConnectionExpectingNoSchemaProvidedErrorDialog()
    {
        String shellText = BotUtils.shell( () -> {
            JobWatcher watcher = new JobWatcher( Messages.jobs__open_connections_name_1 );
            getConnectionsTree().contextMenu( "Open Connection" ).click();
            watcher.waitUntilDone();
        }, "Problem Occurred" ).getText();
        return new ErrorDialogBot( shellText );
    }


    public void closeSelectedConnections()
    {
        JobWatcher watcher = new JobWatcher( Messages.jobs__close_connections_name_1 );
        getConnectionsTree().contextMenu( "Close Connection" ).click();
        watcher.waitUntilDone();
    }


    public SchemaBrowserBot openSchemaBrowser()
    {
        ContextMenuHelper.clickContextMenu( getConnectionsTree(), "Open Schema Browser" );
        return new SchemaBrowserBot();
    }


    public DeleteDialogBot openDeleteConnectionDialog()
    {
        getConnectionsTree().contextMenu( "Delete Connection" ).click();
        return new DeleteDialogBot( DeleteDialogBot.DELETE_CONNECTION );
    }


    public DeleteDialogBot openDeleteConnectionFolderDialog()
    {
        getConnectionsTree().contextMenu( DeleteDialogBot.DELETE_CONNECTION_FOLDER ).click();
        return new DeleteDialogBot( DeleteDialogBot.DELETE_CONNECTION_FOLDER );
    }


    public ExportConnectionsWizardBot openExportConnectionsWizard()
    {
        getConnectionsTree().contextMenu( "Export" ).contextMenu( "Export Connections..." ).click();
        return new ExportConnectionsWizardBot();
    }


    public ImportConnectionsWizardBot openImportConnectionsWizard()
    {
        getConnectionsTree().contextMenu( "Import" ).contextMenu( "Import Connections..." ).click();
        return new ImportConnectionsWizardBot();
    }


    public ApacheDSConfigurationEditorBot openApacheDSConfiguration()
    {
        getConnectionsTree().contextMenu( "Open Configuration" ).click();
        String title = getSelection() + " - Configuration";
        return new ApacheDSConfigurationEditorBot( title );
    }


    public void select( String... path )
    {
        List<String> pathList = new ArrayList<String>( Arrays.asList( path ) );
        SWTBotTreeItem item = getConnectionsTree().getTreeItem( pathList.remove( 0 ) );
        while ( !pathList.isEmpty() )
        {
            item = item.getNode( pathList.remove( 0 ) );
        }
        item.select();
    }


    public String getSelection()
    {
        TableCollection selection = getConnectionsTree().selection();
        if ( selection != null && selection.rowCount() == 1 )
        {
            TableRow row = selection.get( 0 );
            return row.get( 0 );
        }
        return null;
    }


    public int getCount()
    {
        return getConnectionsTree().visibleRowCount();
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
                return "Connection " + connectionName + " not visible in connections view.";
            }
        } );
    }


    public Connection createTestConnection( TestLdapServer server ) throws Exception
    {
        return createTestConnection( server.getType().name(), server.getHost(),
            server.getPort(), server.getAdminDn(),
            server.getAdminPassword() );
    }


    /**
     * Creates the test connection.
     *
     * @param name
     *            the name of the connection
     * @param port
     *            the port to use
     *
     * @return the connection
     *
     */
    public Connection createTestConnection( String name, int port ) throws Exception
    {
        return createTestConnection( name, LOCALHOST, port, "uid=admin,ou=system", "secret" );
    }


    public Connection createTestConnection( String name, String host, int port, String bindDn, String bindPassword )
        throws Exception
    {
        name = name + "_" + System.currentTimeMillis();

        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        ConnectionParameter connectionParameter = new ConnectionParameter();
        connectionParameter.setName( name );
        connectionParameter.setHost( host );
        connectionParameter.setPort( port );
        connectionParameter.setEncryptionMethod( EncryptionMethod.NONE );
        connectionParameter.setAuthMethod( AuthenticationMethod.SIMPLE );
        connectionParameter.setBindPrincipal( bindDn );
        connectionParameter.setBindPassword( bindPassword );
        Connection connection = new Connection( connectionParameter );
        connectionManager.addConnection( connection );

        ConnectionFolderManager connectionFolderManager = ConnectionCorePlugin.getDefault()
            .getConnectionFolderManager();
        ConnectionFolder rootConnectionFolder = connectionFolderManager.getRootConnectionFolder();
        rootConnectionFolder.addConnectionId( connection.getId() );

        select( name );
        StudioConnectionJob job = new StudioConnectionJob( new OpenConnectionsRunnable( connection ) );
        job.execute();
        job.join();

        return connection;
    }


    /**
     * Deletes the test connection.
     */
    public void deleteTestConnections()
    {
        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        for ( Connection connection : connectionManager.getConnections() )
        {
            connectionManager.removeConnection( connection );
        }

        ConnectionFolderManager connectionFolderManager = ConnectionCorePlugin.getDefault()
            .getConnectionFolderManager();
        for ( ConnectionFolder connectionFolder : connectionFolderManager.getConnectionFolders() )
        {
            connectionFolderManager.removeConnectionFolder( connectionFolder );
        }
    }

}
