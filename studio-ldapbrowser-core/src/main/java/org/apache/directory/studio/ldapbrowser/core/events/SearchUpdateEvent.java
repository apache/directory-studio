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


import org.apache.directory.studio.ldapbrowser.core.model.ISearch;


/**
 * An SearchUpdateEvent indicates that an {@link ISearch} was updated.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchUpdateEvent
{

    /**
     * Contains constants to specify the event detail.
     */
    public enum EventDetail
    {

        /** Indicates that the search was added. */
        SEARCH_ADDED,

        /** Indicates that the search was removed. */
        SEARCH_REMOVED,

        /** Indicates that the search was performed. */
        SEARCH_PERFORMED,

        /**
         * Indicates that the search parameters were updated.
         * Note: This event detail doesn't include the renaming of a search!
         */
        SEARCH_PARAMETER_UPDATED,

        /** Indicates that the search was renamed. */
        SEARCH_RENAMED
    }

    /** The event detail. */
    private EventDetail detail;

    /** The updated search. */
    private ISearch search;


    /**
     * Creates a new instance of SearchUpdateEvent.
     *
     * @param search the updated search
     * @param detail the event detail
     */
    public SearchUpdateEvent( ISearch search, EventDetail detail )
    {
        this.search = search;
        this.detail = detail;
    }


    /**
     * Gets the updated search.
     *
     * @return the updated search
     */
    public ISearch getSearch()
    {
        return search;
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
