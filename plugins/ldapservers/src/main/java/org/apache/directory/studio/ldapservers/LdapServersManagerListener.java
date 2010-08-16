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
package org.apache.directory.studio.ldapservers;


import org.apache.directory.studio.ldapservers.model.LdapServer;


/**
 * This interface represents a listener for the LDAP Servers Manager.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface LdapServersManagerListener
{
    /**
     * This method is called when a server is added.
     *
     * @param server
     *      the added server
     */
    void serverAdded( LdapServer server );


    /**
     * This method is called when a server is removed.
     *
     * @param server
     *      the removed server
     */
    void serverRemoved( LdapServer server );


    /**
     * This method is called when a server is updated.
     *
     * @param server
     *      the updated server
     */
    void serverUpdated( LdapServer server );
}
