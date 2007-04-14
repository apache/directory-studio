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

package org.apache.directory.ldapstudio.browser.core.events;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;


/**
 * An ConnectionRenamedEvent indicates that an {@link IConnection} was renamed.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionRenamedEvent extends ConnectionUpdateEvent
{

    /** The old name. */
    private String oldName;


    /**
     * Creates a new instance of ConnectionRenamedEvent.
     * 
     * @param connection the connection
     * @param oldName the old name
     */
    public ConnectionRenamedEvent( IConnection connection, String oldName )
    {
        super( connection, EventDetail.CONNECTION_RENAMED );
        this.oldName = oldName;
    }


    /**
     * Gets the old name.
     * 
     * @return the old name
     */
    public String getOldName()
    {
        return oldName;
    }

}
