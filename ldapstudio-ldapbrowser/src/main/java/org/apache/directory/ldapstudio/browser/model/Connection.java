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
import java.util.List;

import org.apache.directory.shared.ldap.name.LdapDN;


/**
 * This class represents a LDAP Connection used in the preferences
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Connection implements Comparable<Connection>
{
    private String name;

    private String host = "localhost";

    private int port = 389;

    private LdapDN baseDN;

    private boolean anonymousBind = true;

    private LdapDN userDN;

    private boolean appendBaseDNtoUserDNWithBaseDN = false;

    private String password;

    /** The Listeners List */
    private List<ConnectionListener> listeners;


    /**
     * Default Constructor
     */
    public Connection()
    {
        listeners = new ArrayList<ConnectionListener>();
    }


    /**
     * Constructor for a Connection
     * 
     * @param name
     *                the Name of the connection
     * @param host
     *                the Host
     * @param port
     *                the Port
     * @param baseDN
     *                the Base DN
     * @param anonymousBind
     *                the value of the Anonymous Bind flag
     * @param userDN
     *                the User DN
     * @param appendBaseDNtoUserDNWithBaseDN
     *                the value of the appendBaseDNtoUserDNWithBaseDN flag
     * @param password
     *                the Password
     */
    public Connection( String name, String host, int port, LdapDN baseDN, boolean anonymousBind, LdapDN userDN,
        boolean appendBaseDNtoUserDNWithBaseDN, String password )
    {
        this.name = name;
        this.host = host;
        this.port = port;
        this.baseDN = baseDN;
        this.anonymousBind = anonymousBind;
        this.userDN = userDN;
        this.appendBaseDNtoUserDNWithBaseDN = appendBaseDNtoUserDNWithBaseDN;
        this.password = password;
    }


    /**
     * Get the Anonymous Bind Flag
     * 
     * @return the anonymousBind
     */
    public boolean isAnonymousBind()
    {
        return anonymousBind;
    }


    /**
     * Set the Anonymous Bind flag
     * 
     * @param anonymousBind
     *                the anonymousBind to set
     */
    public void setAnonymousBind( boolean anonymousBind )
    {
        this.anonymousBind = anonymousBind;
    }


    /**
     * Get the Base DN
     * 
     * @return the BaseDN
     */
    public LdapDN getBaseDN()
    {
        return baseDN;
    }


    /**
     * Set the BaseDN
     * 
     * @param baseDN
     *                the BaseDN to set
     */
    public void setBaseDN( LdapDN baseDN )
    {
        this.baseDN = baseDN;
    }


    /**
     * Get the Host
     * 
     * @return the Host
     */
    public String getHost()
    {
        return host;
    }


    /**
     * Set the Host
     * 
     * @param host
     *                the Host to set
     */
    public void setHost( String host )
    {
        this.host = host;
    }


    /**
     * Get the Name of the connection
     * 
     * @return the Name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Set the Name of the connection
     * 
     * @param name
     *                the Name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Get the Password
     * 
     * @return the Password
     */
    public String getPassword()
    {
        return password;
    }


    /**
     * Set the Password
     * 
     * @param password
     *                the Password to set
     */
    public void setPassword( String password )
    {
        this.password = password;
    }


    /**
     * Get the Port
     * 
     * @return the Port
     */
    public int getPort()
    {
        return port;
    }


    /**
     * Set the Port
     * 
     * @param port
     *                the Port to set
     */
    public void setPort( int port )
    {
        this.port = port;
    }


    /**
     * Get the User DN
     * 
     * @return the User DN
     */
    public LdapDN getUserDN()
    {
        return userDN;
    }


    /**
     * Set the User DN
     * 
     * @param userDN
     *                the User DN to set
     */
    public void setUserDN( LdapDN userDN )
    {
        this.userDN = userDN;
    }


    /**
     * Get the appendBaseDNtoUserDNWithBaseDN Flag
     * 
     * @return the appendBaseDNtoUserDNWithBaseDN Flag
     */
    public boolean isAppendBaseDNtoUserDNWithBaseDN()
    {
        return appendBaseDNtoUserDNWithBaseDN;
    }


    /**
     * Sets appendBaseDNtoUserDNWithBaseDN Flag
     * 
     * @param appendBaseDNtoUserDNWithBaseDN
     *                the appendBaseDNtoUserDNWithBaseDN Flag
     */
    public void setAppendBaseDNtoUserDNWithBaseDN( boolean appendBaseDNtoUserDNWithBaseDN )
    {
        this.appendBaseDNtoUserDNWithBaseDN = appendBaseDNtoUserDNWithBaseDN;
    }


    /**
     * Converts a Connection into XML Format
     * 
     * @return the corresponding XML String
     */
    public String toXml()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "<connection>" );
        sb.append( "<name>" + ( ( "".equals( name ) ) ? "null" : name ) + "</name>" );
        sb.append( "<host>" + ( ( "".equals( host ) ) ? "null" : host ) + "</host>" );
        sb.append( "<port>" + ( ( "".equals( port ) ) ? "null" : port ) + "</port>" );
        sb
            .append( "<baseDN>" + ( ( "".equals( baseDN.getNormName() ) ) ? "null" : baseDN.getNormName() )
                + "</baseDN>" );
        sb.append( "<anonymousBind>" + anonymousBind + "</anonymousBind>" );
        sb
            .append( "<userDN>" + ( ( "".equals( userDN.getNormName() ) ) ? "null" : userDN.getNormName() )
                + "</userDN>" );
        sb.append( "<appendBaseDNtoUserDNWithBaseDN>" + appendBaseDNtoUserDNWithBaseDN
            + "</appendBaseDNtoUserDNWithBaseDN>" );
        sb.append( "<password>" + ( ( "".equals( password ) ) ? "null" : password ) + "</password>" );
        sb.append( "</connection>" );

        return sb.toString();
    }


    /**
     * Checks if the connection is valid to connect
     * 
     * @return true if the connection is valid to connect
     */
    public boolean validToConnect()
    {
        // Host
        if ( ( host == null ) || ( "".equals( host ) ) )
        {
            return false;
        }
        // Port
        if ( ( port <= 0 ) || ( port > 65535 ) )
        {
            return false;
        }
        return true;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "[" );
        sb.append( "name=\"" + name );
        sb.append( "\" | " );
        sb.append( "host=\"" + host );
        sb.append( "\" | " );
        sb.append( "port=\"" + port );
        sb.append( "\" | " );
        sb.append( "baseDN=\"" + baseDN.getNormName() );
        sb.append( "\" | " );
        sb.append( "anonymousBind=\"" + anonymousBind );
        sb.append( "\" | " );
        sb.append( "userDN=\"" + userDN.getNormName() );
        sb.append( "\" | " );
        sb.append( "appendBaseDNtoUserDNWithBaseDN=\"" + appendBaseDNtoUserDNWithBaseDN );
        sb.append( "\" | " );
        sb.append( "password=\"" + password );
        sb.append( "\"]" );

        return sb.toString();
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( Connection o )
    {
        Connection otherWrapper = ( Connection ) o;
        return getName().compareToIgnoreCase( otherWrapper.getName() );
    }


    /**
     * Adds a listener for the Connections modifications
     * 
     * @param listener
     *                the listener to add
     * @return true (as per the general contract of Collection.add).
     */
    public boolean addListener( ConnectionListener listener )
    {
        return listeners.add( listener );
    }


    /**
     * Removes a listener for the Connections modifications
     * 
     * @param listener
     *                the listener to remove
     * @return true if the list contained the specified element.
     */
    public boolean removeListener( ConnectionListener listener )
    {
        return listeners.remove( listener );
    }


    /**
     * Notifies all the listeners that the Connection has changed
     */
    private void notifyChanged()
    {
        for ( ConnectionListener listener : listeners )
        {
            listener.connectionChanged( this );
        }
    }


    /**
     * Notifies all the listeners that the Connection has changed
     */
    public void notifyListeners()
    {
        notifyChanged();
    }
}
