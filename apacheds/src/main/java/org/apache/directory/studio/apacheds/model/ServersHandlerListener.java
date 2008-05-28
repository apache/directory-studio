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
package org.apache.directory.studio.apacheds.model;


/**
 * This interface represents a listener for the servers handler
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ServersHandlerListener
{
    /**
     * This method is called when a server instance is added.
     *
     * @param serverInstance
     *      the added server instance
     */
    void serverInstanceAdded( ServerInstance serverInstance );


    /**
     * This method is called when a server instance is removed.
     *
     * @param serverInstance
     *      the removed server instance
     */
    void serverInstanceRemoved( ServerInstance serverInstance );


    /**
     * This method is called when a server instance is updated.
     *
     * @param serverInstance
     *      the updated server instance
     */
    void serverInstanceUpdated( ServerInstance serverInstance );
}
