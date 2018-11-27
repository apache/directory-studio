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


import static org.apache.directory.studio.test.integration.ui.Constants.LOCALHOST;

import java.util.Random;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.jobs.OpenConnectionsRunnable;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionJob;
import org.apache.directory.studio.test.integration.ui.ContextMenuHelper;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
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
        
        // choose random network provider to test both, LDAP API and JNDI
        if ( new Random().nextBoolean() )
        {
            newConnectionWizardBot.selectLdapApiProvider();
        }
        else
        {
            newConnectionWizardBot.selectJndiProvider();
        }

        return newConnectionWizardBot;
    }


    public SchemaBrowserBot openSchemaBrowser()
    {
        ContextMenuHelper.clickContextMenu( getConnectionsTree(), "Open Schema Browser" );
        return new SchemaBrowserBot();
    }


    public void openSelectedConnection()
    {
        JobWatcher watcher = new JobWatcher( Messages.jobs__open_connections_name_1 );
        getConnectionsTree().contextMenu( "Open Connection" ).click();
        watcher.waitUntilDone();
    }


    public void closeSelectedConnections()
    {
        JobWatcher watcher = new JobWatcher( Messages.jobs__close_connections_name_1 );
        getConnectionsTree().contextMenu( "Close Connection" ).click();
        watcher.waitUntilDone();
    }


    public void selectConnection( String connectionName )
    {
        getConnectionsTree().select( connectionName );
    }


    public String getSelectedConnection()
    {
        TableCollection selection = getConnectionsTree().selection();
        if ( selection != null && selection.rowCount() == 1 )
        {
            TableRow row = selection.get( 0 );
            return row.get( 0 );
        }
        return null;
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
                return "Connection " + connectionName + " not visible in connections view.";
            }
        } );
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
        name = name + "_" + System.currentTimeMillis();

        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        ConnectionParameter connectionParameter = new ConnectionParameter();
        // choose random network provider to test both, LDAP API and JNDI
        connectionParameter
            .setNetworkProvider( NetworkProvider.values()[new Random().nextInt( NetworkProvider.values().length )] );
        connectionParameter.setName( name );
        connectionParameter.setHost( LOCALHOST );
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

        selectConnection( name );
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
    }


    public ApacheDSConfigurationEditorBot openApacheDSConfiguration()
    {
        getConnectionsTree().contextMenu( "Open Configuration" ).click();
        String title = getSelectedConnection() + " - Configuration";
        return new ApacheDSConfigurationEditorBot( title );
    }

}
