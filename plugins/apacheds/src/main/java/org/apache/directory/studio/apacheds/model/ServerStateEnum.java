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
 * This enum defines the different states that a server can take.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum ServerStateEnum
{
    STARTING(Messages.getString( "ServerStateEnum.Starting" )), STARTED(Messages.getString( "ServerStateEnum.Started" )), STOPPING(Messages.getString( "ServerStateEnum.Stopping" )), STOPPED(Messages.getString( "ServerStateEnum.Stopped" )), UNKNONW(Messages.getString( "ServerStateEnum.Unknown" )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    /** The dispayable name of the state */
    private String displayName;


    /**
     * Creates a new instance of ServerStateEnum.
     *
     * @param displayName
     *      the display name
     */
    private ServerStateEnum( String displayName )
    {
        this.displayName = displayName;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return displayName;
    }
}
