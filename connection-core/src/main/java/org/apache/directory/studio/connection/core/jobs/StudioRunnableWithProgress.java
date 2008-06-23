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

package org.apache.directory.studio.connection.core.jobs;

import org.apache.directory.studio.connection.core.Connection;


/**
 * A runnable with a progress monitor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface StudioRunnableWithProgress
{

    /**
     * Runs the runnable.
     * 
     * @param monitor the monitor
     */
    public void run( StudioProgressMonitor monitor );


    /**
     * Gets the locked objects.
     * 
     * @return the locked objects
     */
    public Object[] getLockedObjects();


    /**
     * Gets the error message.
     * 
     * @return the error message
     */
    public String getErrorMessage();


    /**
     * Gets the name that is used when reporting progress.
     * 
     * @return the name
     */
    public String getName();


    /**
     * Gets the connections that must be opened before running this runnable.
     * 
     * @return the connections, null if none
     */
    public Connection[] getConnections();
}
