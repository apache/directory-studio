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

package org.apache.directory.ldapstudio.browser.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.model.ConnectionsEvent.ConnectionsEventType;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.xmlpull.v1.XmlPullParserException;


/**
 * This class represent the Connections class used to store all the connections
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Connections implements ConnectionListener
{
    /**
     * The Preferences identifier for storing the Connections
     */
    private static final String CONNECTIONS_PREFS = "connections_prefs";

    /**
     * The Connections List
     */
    private List<Connection> connections;

    /**
     * The Listeners List
     */
    private List<ConnectionsListener> listeners;

    /**
     * The instance (used to access Connections as a Singleton)
     */
    private static Connections instance;

    // Static thread-safe singleton initializer
    static
    {
        instance = new Connections();
    }


    /**
     * Private constructor
     */
    private Connections()
    {
        connections = new ArrayList<Connection>();
        listeners = new ArrayList<ConnectionsListener>();

        loadConnections();
    }


    /**
     * Loads the Connections
     */
    private void loadConnections()
    {
        Preferences store = Activator.getDefault().getPluginPreferences();

        String connectionsAsXml = store.getString( CONNECTIONS_PREFS );

        try
        {
            ConnectionParser parser = new ConnectionParser();

            parser.parse( connectionsAsXml );

            connections = parser.getConnections();

            // Registering this class as a listener for modification on any Connection.
            for ( Connection connection : connections )
            {
                connection.addListener( this );
            }
        }
        catch ( XmlPullParserException e )
        {
            MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
                "An error ocurred while recovering the connections." );
        }
    }


    /**
     * Stores the Connections
     */
    private void storeConnections()
    {
        Preferences store = Activator.getDefault().getPluginPreferences();

        StringBuffer sb = new StringBuffer();

        // Constructing the XML String representing all the Connections
        sb.append( "<connections>" );
        for ( int i = 0; i < connections.size(); i++ )
        {
            sb.append( connections.get( i ).toXml() );
        }
        sb.append( "</connections>" );

        store.setValue( CONNECTIONS_PREFS, sb.toString() );
    }


    /**
     * Use this method to get the singleton instance of the controller
     * @return
     */
    public static Connections getInstance()
    {
        return instance;
    }


    /**
     * Adds a Connection
     * @param connection the Connection to add
     * @return true (as per the general contract of Collection.add).
     */
    public boolean addConnection( Connection connection )
    {
        if ( connection != null )
        {
            boolean bool = connections.add( connection );

            connection.addListener( this );

            // Notifying the listeners
            notifyChanged( new ConnectionsEvent( ConnectionsEventType.ADD, connection ) );

            // Saving the Connections
            storeConnections();

            return bool;
        }
        return false;
    }


    /**
     * Removes a Connection
     * @param connection the Connection to remove
     * @return true if the list contained the specified element.
     */
    public boolean removeConnection( Connection connection )
    {
        if ( connection != null )
        {
            boolean bool = connections.remove( connection );

            connection.removeListener( this );

            // Notifying the listeners
            notifyChanged( new ConnectionsEvent( ConnectionsEventType.REMOVE, connection ) );

            // Saving the Connections
            storeConnections();

            return bool;
        }
        return false;
    }


    /**
     * Notifies all the listeners that the Connections have changed
     * @param event the associated event
     */
    private void notifyChanged( ConnectionsEvent event )
    {
        for ( ConnectionsListener listener : listeners )
        {
            listener.connectionsChanged( this, event );
        }
    }


    /**
     * Return the Connection at the specified position in the list
     * @param index index of element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range 
     * (index < 0 || index >= size())
     */
    public Connection getConnection( int index ) throws IndexOutOfBoundsException
    {
        return connections.get( index );
    }


    /**
     * Returns the number of elements in this list. If this list contains 
     * more than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE
     * @return the number of elements in this list
     */
    public int size()
    {
        return connections.size();
    }


    /**
     * Adds a listener for the Connections modifications
     * @param listener the listener to add
     * @return true (as per the general contract of Collection.add).
     */
    public boolean addListener( ConnectionsListener listener )
    {
        return listeners.add( listener );
    }


    /**
     * Removes a listener for the Connections modifications
     * @param listener the listener to remove
     * @return true if the list contained the specified element.
     */
    public boolean removeListener( ConnectionsListener listener )
    {
        return listeners.remove( listener );
    }


    /**
     * Sorts the connections into ascending order, according to the 
     * natural ordering.
     */
    public void sort()
    {
        Collections.sort( connections );
    }


    /**
     * Verifies if the name is already used by a connection
     * @param name the name
     * @param excludeName a name to exclude from the verification 
     * @return true if the name is available, false if a connection
     * 				already has a same name
     */
    public boolean isConnectionNameAvailable( String name, String excludeName )
    {
        for ( int i = 0; i < size(); i++ )
        {
            if ( name.equals( getConnection( i ).getName() ) )
            {
                if ( !name.equals( excludeName ) )
                {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Verifies if the name is already used by a connection
     * @param name the name
     * @return true if the name is available, false if a connection
     * 				already has a same name
     */
    public boolean isConnectionNameAvailable( String name )
    {
        return isConnectionNameAvailable( name, null );
    }


    /**
     * This method is called if the Connection have been modified
     * @param connection the Connection
     */
    public void connectionChanged( Connection connection )
    {
        // Notifying the listeners
        notifyChanged( new ConnectionsEvent( ConnectionsEventType.UPDATE, connection ) );

        // Saving the Connections
        storeConnections();
    }
}
