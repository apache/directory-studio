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

package org.apache.directory.studio.ldapservers.model;


import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;


/**
 * The {@link LdapServerAdapter} interface defines the required methods
 * to implement an LDAP Server adapter.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface LdapServerAdapter
{
    /**
     * This method is called when a server is added.
     *
     * @param server
     *      the server
     * @param monitor
     *      the progress monitor
     * @throws Exception
     */
    public void add( LdapServer server, StudioProgressMonitor monitor ) throws Exception;


    /**
     * This method is called when a server is deleted.
     *
     * @param server
     *      the server
     * @param monitor
     *      the progress monitor
     * @throws Exception
     */
    public void delete( LdapServer server, StudioProgressMonitor monitor ) throws Exception;


    /**
     * This method is called when a server is double-clicked in the 'LDAP Servers' view.
     *
     * @param server
     *      the server
     * @param monitor
     *      the progress monitor
     * @throws Exception
     */
    public void openConfiguration( LdapServer server, StudioProgressMonitor monitor ) throws Exception;


    /**
     * This method is called when a server needs to be started.
     *
     * @param server
     *      the server
     * @param monitor
     *      the progress monitor
     * @throws Exception
     *      if an error occurs when restarting the server
     */
    public void start( LdapServer server, StudioProgressMonitor monitor ) throws Exception;


    /**
     * This method is called when a server needs to be stopped.
     *
     * @param server
     *      the server
     * @param monitor
     *      the progress monitor
     * @throws Exception
     *      if an error occurs when restarting the server
     */
    public void stop( LdapServer server, StudioProgressMonitor monitor ) throws Exception;
}
