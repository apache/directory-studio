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
package org.apache.directory.studio.connection.core.io;


import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;


/**
 * A ConnectionWrapper is a wrapper for a real directory connection implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ConnectionWrapper
{

    /**
     * Connects to the directory server.
     * 
     * @param monitor the progres monitor
     */
    public void connect( StudioProgressMonitor monitor );


    /**
     * Disconnects from the directory server.
     */
    public void disconnect();


    /**
     * Binds to the directory server.
     * 
     * @param monitor the progress monitor
     */
    public void bind( StudioProgressMonitor monitor );


    /**
     * Unbinds from the directory server.
     */
    public void unbind();


    /**
     * Checks if is connected.
     * 
     * @return true, if is connected
     */
    public boolean isConnected();

}
