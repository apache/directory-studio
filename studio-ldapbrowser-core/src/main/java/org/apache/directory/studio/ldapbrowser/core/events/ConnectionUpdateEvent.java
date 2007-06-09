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

package org.apache.directory.studio.ldapbrowser.core.events;


/**
 * An ConnectionUpdateEvent indicates that an {@link IConnection} was modified.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;


/**
 * An ConnectionUpdateEvent indicates that an {@link IConnection} was modified.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionUpdateEvent
{

    /**
     * Contains constants to specify the event detail.
     */
    public enum EventDetail
    {
        /** Indicates that the connection to the directory was opened. */
        CONNECTION_OPENED,

        /** Indicates that the connection to the directory was closed. */
        CONNECTION_CLOSED,

        /** Indicates that the schema was loaded from directory. */
        SCHEMA_LOADED,

        /** Indicates that the connection was added to the connection pool. */
        CONNECTION_ADDED,

        /** Indicates that the connection was removed from the connection pool. */
        CONNECTION_REMOVED,

        /**
         * Indicates that the connection parameters were updated.
         * Note: This event detail doesn't include the renaming of a connection!
         * */
        CONNECTION_PARAMETER_UPDATED,

        /** Indicates that the connection was renamed. */
        CONNECTION_RENAMED
    }

    /** The event detail. */
    private EventDetail detail;

    /** The updated connection. */
    private IConnection connection;


    /**
     * Creates a new instance of ConnectionUpdateEvent.
     *
     * @param detail the event detail
     * @param connection the updated connection
     */
    public ConnectionUpdateEvent( IConnection connection, EventDetail detail )
    {
        this.connection = connection;
        this.detail = detail;
    }


    /**
     * Gets the updated connection.
     *
     * @return the updated connection
     */
    public IConnection getConnection()
    {
        return connection;
    }


    /**
     * Gets the event detail.
     *
     * @return the event detail
     */
    public EventDetail getDetail()
    {
        return detail;
    }

}
