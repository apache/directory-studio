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


import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;


/**
 * A Connection Listener is informed when a connection is opened or closed. 
 * To register for those events the implementing plug-in must also implement
 * the org.apache.directory.studio.connection.core.connectionListener 
 * extension point.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IConnectionListener
{

    /**
     * Called when an {@link Connection} was opened.
     *
     * @param connection the opened connection 
     * @param monitor the progress monitor
     */
    public void connectionOpened( Connection connection, StudioProgressMonitor monitor );


    /**
     * Called when an {@link Connection} was closed.
     *
     * @param connection the closed connection 
     * @param monitor the progress monitor
     */
    public void connectionClosed( Connection connection, StudioProgressMonitor monitor );
}
