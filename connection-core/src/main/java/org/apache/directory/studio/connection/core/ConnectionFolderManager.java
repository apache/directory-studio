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

package org.apache.directory.studio.connection.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.core.io.ConnectionIO;
import org.apache.directory.studio.connection.core.io.ConnectionIOException;


/**
 * This class is used to manage {@link ConnectionFolder}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionFolderManager implements ConnectionUpdateListener
{

    /** The root connection folder. */
    private ConnectionFolder root;

    /** The list of folders. */
    private Set<ConnectionFolder> folderList;


    /**
     * Creates a new instance of ConnectionFolderManager.
     */
    public ConnectionFolderManager()
    {
        this.root = new ConnectionFolder( "" );
        this.root.setId( "0" ); //$NON-NLS-1$s
        this.folderList = new HashSet<ConnectionFolder>();

        loadConnectionFolders();
        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionCorePlugin.getDefault().getEventRunner() );

        this.folderList.add( this.root );
    }


    /**
     * Gets the filename of the connection folder store.
     *
     * @return
     *      the filename of the connection folder store
     */
    public static final String getConnectionFolderStoreFileName()
    {
        String filename = ConnectionCorePlugin.getDefault().getStateLocation()
            .append( "connectionFolders.xml" ).toOSString(); //$NON-NLS-1$
        return filename;
    }


    /**
     * Adds the ConnectionFolder to the connection folder list. If there is already a
     * connection folder with the same name the new connection folder is renamed.
     * 
     * @param connectionFolder the connection folder
     */
    public void addConnectionFolder( ConnectionFolder connectionFolder )
    {
        if ( getConnectionFolderByName( connectionFolder.getName() ) != null )
        {
            String newConnectionFolderName = Messages.bind( Messages.copy_n_of_s, "", connectionFolder.getName() ); //$NON-NLS-1$
            for ( int i = 2; getConnectionFolderByName( newConnectionFolderName ) != null; i++ )
            {
                newConnectionFolderName = Messages.bind( Messages.copy_n_of_s, i + " ", connectionFolder.getName() ); //$NON-NLS-1$
            }
            connectionFolder.setName( newConnectionFolderName );
        }

        folderList.add( connectionFolder );
        ConnectionEventRegistry.fireConnectonFolderModified( connectionFolder, this );
    }


    /**
     * Removes the given ConnectionFolder from the connection folder list.
     *
     * @param connectionFolder
     *      the connection folder to remove
     */
    public void removeConnectionFolder( ConnectionFolder connectionFolder )
    {
        folderList.remove( connectionFolder );
        ConnectionEventRegistry.fireConnectonFolderModified( connectionFolder, this );
    }


    /**
     * Gets an array of connection folders.
     *
     * @return
     *      an array of connection folders
     */
    public ConnectionFolder[] getConnectionFolders()
    {
        return folderList.toArray( new ConnectionFolder[0] );
    }


    /**
     * Gets a connection folder from its id.
     *
     * @param id
     *      the id of the connection folder
     * @return
     *      the corresponding connection folder
     */
    public ConnectionFolder getConnectionFolderById( String id )
    {
        for ( ConnectionFolder folder : folderList )
        {
            if ( folder.getId().equals( id ) )
            {
                return folder;
            }
        }
        return null;
    }


    /**
     * Gets a connection folder from its name.
     *
     * @param name
     *      the name of the connection folder
     * @return
     *      the corresponding connection folder
     */
    public ConnectionFolder getConnectionFolderByName( String name )
    {
        for ( ConnectionFolder folder : folderList )
        {
            if ( folder.getName().equals( name ) )
            {
                return folder;
            }
        }
        return null;
    }


    /**
     * Gets the parent connection folder of the given connection.
     *
     * @param connection
     *      the connection used to search the parent
     * @return
     *      the parent connection folder of the given connection
     */
    public ConnectionFolder getParentConnectionFolder( Connection connection )
    {
        for ( ConnectionFolder folder : folderList )
        {
            if ( folder.getConnectionIds().contains( connection.getId() ) )
            {
                return folder;
            }
        }
        return getRootConnectionFolder();
    }


    /**
     * Gets the parent connection folder of the given connection folder 
     * or null if the folder doesn't have a parent folder.
     *
     * @param connectionFolder
     *      the connection folder used to search the parent
     * @return
     *      the parent connection folder of the given connection folder
     *      or null if the folder doesn't have a parent folder
     */
    public ConnectionFolder getParentConnectionFolder( ConnectionFolder connectionFolder )
    {
        for ( ConnectionFolder folder : folderList )
        {
            if ( folder.getSubFolderIds().contains( connectionFolder.getId() ) )
            {
                return folder;
            }
        }
        return null;
    }


    /**
     * Gets the all sub-folders of the given folder including the given folder.
     * 
     * @param folder the folder
     * 
     * @return all sub-folders
     */
    public Set<ConnectionFolder> getAllSubFolders( ConnectionFolder folder )
    {
        Set<ConnectionFolder> allSubFolders = new HashSet<ConnectionFolder>();

        List<String> ids = new ArrayList<String>();
        ids.add( folder.getId() );
        while ( !ids.isEmpty() )
        {
            String id = ids.remove( 0 );
            ConnectionFolder subFolder = getConnectionFolderById( id );
            allSubFolders.add( subFolder );

            ids.addAll( subFolder.getSubFolderIds() );
        }

        return allSubFolders;
    }


    /**
     * Gets the all parent folders of the given folder including the given folder.
     * 
     * @param folder the folder
     * 
     * @return all parent folders
     */
    public Set<ConnectionFolder> getAllParentFolders( ConnectionFolder folder )
    {
        Set<ConnectionFolder> allParentFolders = new HashSet<ConnectionFolder>();

        do
        {
            allParentFolders.add( folder );
            folder = getParentConnectionFolder( folder );
        }
        while ( folder != null );

        return allParentFolders;
    }


    /**
     * Gets the root connection folder.
     * 
     * @return the root connection folder
     */
    public ConnectionFolder getRootConnectionFolder()
    {
        return root;
    }


    /**
     * Sets the root connection folder.
     * 
     * @param root the new root connection folder
     */
    public void setRootConnectionFolder( ConnectionFolder root )
    {
        this.root = root;
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionAdded(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionAdded( Connection connection )
    {
        saveConnectionFolders();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionRemoved(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionRemoved( Connection connection )
    {
        saveConnectionFolders();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionUpdated(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionUpdated( Connection connection )
    {
        saveConnectionFolders();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionOpened(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionOpened( Connection connection )
    {
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionClosed(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionClosed( Connection connection )
    {
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderModified(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
        saveConnectionFolders();
    }


    /**
     * Saves the Connection Folders
     */
    private synchronized void saveConnectionFolders()
    {
        // To avoid a corrupt file, save object to a temp file first 
        try
        {
            ConnectionIO.saveConnectionFolders( folderList, new FileOutputStream( getConnectionFolderStoreFileName()
                + "-temp" ) );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // move temp file to good file
        File file = new File( getConnectionFolderStoreFileName() );
        File tempFile = new File( getConnectionFolderStoreFileName() + "-temp" );
        if ( file.exists() )
        {
            file.delete();
        }

        try
        {
            String content = FileUtils.readFileToString( tempFile, "UTF-8" );
            FileUtils.writeStringToFile( file, content, "UTF-8" );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Loads the Connection Folders
     */
    private synchronized void loadConnectionFolders()
    {
        try
        {
            folderList = ConnectionIO.loadConnectionFolders( new FileInputStream( getConnectionFolderStoreFileName() ) );
        }
        catch ( Exception e )
        {
            // If loading failed, try with temp file
            try
            {
                folderList = ConnectionIO.loadConnectionFolders( new FileInputStream(
                    getConnectionFolderStoreFileName() + "-temp" ) );
            }
            catch ( FileNotFoundException e1 )
            {
                // TODO Auto-generated catch block
            }
            catch ( ConnectionIOException e1 )
            {
                // TODO Auto-generated catch block
            }
        }

        if ( !folderList.isEmpty() )
        {
            for ( ConnectionFolder folder : folderList )
            {
                if ( "0".equals( folder.getId() ) )
                {
                    root = folder;
                }
            }
        }
        else
        {
            Connection[] connections = ConnectionCorePlugin.getDefault().getConnectionManager().getConnections();
            for ( Connection connection : connections )
            {
                root.addConnectionId( connection.getId() );
            }
        }

    }

}
