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


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;


/**
 * A ConnectionFolder helps to organize connections in folders.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionFolder
{
    private String id;

    private String name;

    private List<String> subFolderIds;

    private List<String> connectionIds;


    /**
     * Creates a new instance of ConnectionFolder.
     */
    public ConnectionFolder()
    {
        this.subFolderIds = new ArrayList<String>();
        this.connectionIds = new ArrayList<String>();
    }


    /**
     * Creates a new instance of ConnectionFolder.
     * 
     * @param name the folder name
     */
    public ConnectionFolder( String name )
    {
        this();
        this.id = createId();
        this.name = name;
    }


    /**
     * Adds the connection id to the end of the connection list.
     * 
     * @param connectionId the connection id
     */
    public void addConnectionId( String connectionId )
    {
        addConnectionId( connectionIds.size(), connectionId );
    }


    /**
     * Adds the connection id at the specified position of the connection list.
     * 
     * @param index the index
     * @param connectionId the connection id
     */
    public void addConnectionId( int index, String connectionId )
    {
        connectionIds.add( index, connectionId );
        ConnectionEventRegistry.fireConnectonFolderModified( this, this );
    }


    /**
     * Adds the folder id to the end of the sub-folder list.
     * 
     * @param folderId the folder id
     */
    public void addSubFolderId( String folderId )
    {
        addSubFolderId( subFolderIds.size(), folderId );
    }


    /**
     * Adds the folder id to the end of the sub-folder list.
     * 
     * @param index the index
     * @param folderId the folder id
     */
    public void addSubFolderId( int index, String folderId )
    {
        subFolderIds.add( index, folderId );
        ConnectionEventRegistry.fireConnectonFolderModified( this, this );
    }


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return id;
    }


    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName( String name )
    {
        this.name = name;
        ConnectionEventRegistry.fireConnectonFolderModified( this, this );
    }


    /**
     * Gets the sub-folder ids.
     * 
     * @return the sub-folder ids
     */
    public List<String> getSubFolderIds()
    {
        List<String> ids = new ArrayList<String>();
        for ( String id : subFolderIds )
        {
            if ( ConnectionCorePlugin.getDefault().getConnectionFolderManager().getConnectionFolderById( id ) != null )
            {
                ids.add( id );
            }
        }
        return ids;
    }


    /**
     * Sets the sub-folder ids.
     * 
     * @param subFolderIds the new sub-folder ids
     */
    public void setSubFolderIds( List<String> subFolderIds )
    {
        this.subFolderIds = subFolderIds;
        ConnectionEventRegistry.fireConnectonFolderModified( this, this );
    }


    /**
     * Gets the connection ids.
     * 
     * @return the connection ids
     */
    public List<String> getConnectionIds()
    {
        List<String> ids = new ArrayList<String>();
        for ( String id : connectionIds )
        {
            if ( ConnectionCorePlugin.getDefault().getConnectionManager().getConnectionById( id ) != null )
            {
                ids.add( id );
            }
        }
        return ids;
    }


    /**
     * Sets the connection ids.
     * 
     * @param connectionIds the new connection ids
     */
    public void setConnectionIds( List<String> connectionIds )
    {
        this.connectionIds = connectionIds;
        ConnectionEventRegistry.fireConnectonFolderModified( this, this );
    }


    /**
     * Creates a unique id.
     * 
     * @return the created id
     */
    private String createId()
    {
        long id = new Random( System.currentTimeMillis() ).nextLong();
        return Long.valueOf( id ).toString();
    }

}
