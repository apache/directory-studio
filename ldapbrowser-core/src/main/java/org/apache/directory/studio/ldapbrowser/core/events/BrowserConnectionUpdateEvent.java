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


import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * An BrowserConnectionUpdateEvent indicates that an {@link IBrowserConnection} was modified.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserConnectionUpdateEvent
{

    /**
     * Contains constants to specify the event detail.
     */
    public enum Detail
    {
        /** Indicates that the browser connection was opened. */
        BROWSER_CONNECTION_OPENED,

        /** Indicates that the browser connection was closed. */
        BROWSER_CONNECTION_CLOSED,

        /** Indicates that the schema was updated. */
        SCHEMA_UPDATED
    }

    /** The event detail. */
    private Detail detail;

    /** The updated browser connection. */
    private IBrowserConnection browserConnection;


    /**
     * Creates a new instance of BrowserConnectionUpdateEvent.
     *
     * @param browserConnection the updated browser connection
     * @param detail the event detail
     */
    public BrowserConnectionUpdateEvent( IBrowserConnection browserConnection, Detail detail )
    {
        this.browserConnection = browserConnection;
        this.detail = detail;
    }


    /**
     * Gets the updated browser connection.
     *
     * @return the updated browser connection
     */
    public IBrowserConnection getBrowserConnection()
    {
        return browserConnection;
    }


    /**
     * Gets the event detail.
     *
     * @return the event detail
     */
    public Detail getDetail()
    {
        return detail;
    }

}
