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

package org.apache.directory.studio.connection.core.event;


import java.util.EventListener;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;


/**
 * A listener for connection updates
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ConnectionUpdateListener extends EventListener
{

    /**
     * Called when an {@link Connection} was opened.
     *
     * @param connection the opened connection 
     */
    public void connectionOpened( Connection connection );


    /**
     * Called when an {@link Connection} was closed.
     *
     * @param connection the closed connection 
     */
    public void connectionClosed( Connection connection );


    /**
     * Called when an {@link Connection} was added.
     *
     * @param connection the added connection 
     */
    public void connectionAdded( Connection connection );


    /**
     * Called when an {@link Connection} was removed.
     *
     * @param connection the removed connection 
     */
    public void connectionRemoved( Connection connection );


    /**
     * Called when {@link Connection} parameters were updated.
     *
     * @param connection the updated connection 
     */
    public void connectionUpdated( Connection connection );


    /**
     * Called when an {@link ConnectionFolder} was modified.
     *
     * @param connectionFolder the modified connection folder 
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder );
    
    
    /**
     * Called when an {@link ConnectionFolder} was added.
     *
     * @param connectionFolder the added connection folder 
     */
    public void connectionFolderAdded( ConnectionFolder connectionFolder );
    
    
    /**
     * Called when an {@link ConnectionFolder} was removed.
     *
     * @param connectionFolder the removed connection folder 
     */
    public void connectionFolderRemoved( ConnectionFolder connectionFolder );

}
