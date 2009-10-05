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


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;


/**
 * This class implements a simple {@link ConnectionUpdateListener} which does nothing.
 * <p>
 * All methods are "empty".
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionUpdateAdapter implements ConnectionUpdateListener
{
    /**
     * {@inheritDoc}
     */
    public void connectionOpened( Connection connection )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void connectionClosed( Connection connection )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void connectionAdded( Connection connection )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void connectionRemoved( Connection connection )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void connectionUpdated( Connection connection )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void connectionFolderAdded( ConnectionFolder connectionFolder )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void connectionFolderRemoved( ConnectionFolder connectionFolder )
    {
    }
}
