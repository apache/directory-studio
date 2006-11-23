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

package org.apache.directory.ldapstudio.browser.view.views.wrappers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.directory.ldapstudio.browser.model.Connection;
import org.apache.directory.ldapstudio.browser.model.Connections;
import org.apache.directory.ldapstudio.browser.model.ConnectionsEvent;
import org.apache.directory.ldapstudio.browser.model.ConnectionsEvent.ConnectionsEventType;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;


/**
 * This class represents the Root Node of the Browser View Table
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TreeViewerRootNode implements DisplayableTreeViewerElement
{
    private List<ConnectionWrapper> children;

    private static TreeViewerRootNode instance;

    private Connections connections;

    // Static thread-safe singleton initializer
    static
    {
        try
        {
            instance = new TreeViewerRootNode();
        }
        catch ( Throwable e )
        {
            throw new RuntimeException( e.getMessage() );
        }
    }


    /**
     * Use this method to get the singleton instance of the Root Node
     * @return
     */
    public static TreeViewerRootNode getInstance()
    {
        return instance;
    }


    private TreeViewerRootNode()
    {
        connections = Connections.getInstance();
    }


    public Object[] getChildren()
    {
        if ( children == null )
        {
            children = new ArrayList<ConnectionWrapper>();

            // Sorting the connections
            connections.sort();

            // Adding each Connection
            for ( int i = 0; i < connections.size(); i++ )
            {
                ConnectionWrapper connectionWrapper = new ConnectionWrapper( connections.getConnection( i ) );

                connectionWrapper.setParent( this );

                children.add( connectionWrapper );
            }
        }

        return children.toArray( new Object[0] );
    }


    public void updateChildren( ConnectionsEvent event )
    {
        // Getting the Browser View
        BrowserView browserView = ( BrowserView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( BrowserView.ID );

        // A CONNECTION HAS BEEN ADDED
        if ( event.getType().equals( ConnectionsEventType.ADD ) )
        {
            // Adding the new connection
            children.add( new ConnectionWrapper( event.getConnection() ) );

            // Sorting
            Collections.sort( children );
        }
        // A CONNECTION HAS BEEN UPDATED
        else if ( event.getType().equals( ConnectionsEventType.UPDATE ) )
        {
            // Searching for the correct ConnectionWrapper Node.
            for ( ConnectionWrapper connectionWrapper : children )
            {
                if ( event.getConnection().equals( connectionWrapper.getConnection() ) )
                {
                    // Updating the node
                    browserView.getViewer().update( connectionWrapper, null );
                }
            }

            // Sorting
            Collections.sort( children );
        }
        // A CONNECTION HAS BEEN REMOVED
        else if ( event.getType().equals( ConnectionsEventType.REMOVE ) )
        {
            // Searching for the correct ConnectionWrapper Node.
            for ( ConnectionWrapper connectionWrapper : children )
            {
                if ( event.getConnection().equals( connectionWrapper.getConnection() ) )
                {
                    children.remove( connectionWrapper );
                    break;
                }
            }
        }
    }


    public Connection getConnection()
    {
        // The root element is not linked to any connection
        return null;
    }


    public Image getDisplayImage()
    {
        // No image to display
        return null;
    }


    public String getDisplayName()
    {
        // No name to display
        return null;
    }


    public Object getParent()
    {
        // This is root element, so it has no parent
        return null;
    }


    public void setParent( Object parent )
    {
        // Nothing to do, since the root element has no parent
    }

}
