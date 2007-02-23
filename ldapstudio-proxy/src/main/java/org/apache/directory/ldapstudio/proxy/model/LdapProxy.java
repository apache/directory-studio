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
package org.apache.directory.ldapstudio.proxy.model;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class implements a LDAP Proxy
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapProxy
{
    /** Default timeout in milli seconds */
    public static final int DEFAULT_TIMEOUT = 30000;

    /** The listeners list */
    private List<LdapProxyListener> listeners;

    private LdapProxyThread proxyThread;


    /**
     * Creates a new instance of LdapProxy.
     *
     * @param localPort
     *      the local port
     * @param remoteHost
     *      the host name of the LDAP Server
     * @param remotePort
     *      the port of the LDAP Server
     */
    public LdapProxy( int localPort, String remoteHost, int remotePort )
    {
        listeners = new ArrayList<LdapProxyListener>();

        proxyThread = new LdapProxyThread( localPort, remoteHost, remotePort, DEFAULT_TIMEOUT );
    }


    /**
     * Connects the LDAP Proxy.
     *
     * @throws IOException
     */
    public void connect() throws IOException
    {
        proxyThread.start();
    }


    /**
     * Disconnects the LDAP Proxy.
     */
    public void disconnect()
    {
        proxyThread.interrupt();
    }


    /**
     * Adds a LDAP Proxy Listener.
     *
     * @param listener
     *      the listener to add
     */
    public void addListener( LdapProxyListener listener )
    {
        listeners.add( listener );
    }


    /**
     * Removes the LDAP Proxy Listener.
     *
     * @param listener
     *      the listener to remove
     */
    public void removeListener( LdapProxyListener listener )
    {
        listeners.remove( listener );
    }
}
