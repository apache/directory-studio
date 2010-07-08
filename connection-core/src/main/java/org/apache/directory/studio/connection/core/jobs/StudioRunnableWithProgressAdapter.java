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
 * An adapter class for StudioRunnableWithProgress.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class StudioRunnableWithProgressAdapter implements StudioRunnableWithProgress
{

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Connection[] EMPTY_CONNECTION_ARRAY = new Connection[0];


    /**
     * @return an empty array
     */
    public Object[] getLockedObjects()
    {
        return EMPTY_OBJECT_ARRAY;
    }


    /**
     * @return empty string
     */
    public String getErrorMessage()
    {
        return "";
    }


    /**
     * @return an empty array
     */
    public Connection[] getConnections()
    {
        return EMPTY_CONNECTION_ARRAY;
    }
}
