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


import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;


/**
 * An BookmarkUpdateEvent indicates that an {@link IBookmark} was modified.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BookmarkUpdateEvent
{

    /**
     * Contains constants to specify the event detail.
     */
    public enum Detail
    {
        /** Indicates that the bookmark was added. */
        BOOKMARK_ADDED,

        /** Indicates that the bookmark was updated. */
        BOOKMARK_UPDATED,

        /** Indicates that the bookmark was removed. */
        BOOKMARK_REMOVED
    }

    /** The event detail. */
    private Detail detail;

    /** The updated bookmark. */
    private IBookmark bookmark;


    /**
     * Creates a new instance of BookmarkUpdateEvent.
     *
     * @param bookmark the updated bookmark
     * @param detail the event detail
     */
    public BookmarkUpdateEvent( IBookmark bookmark, Detail detail )
    {
        this.bookmark = bookmark;
        this.detail = detail;
    }


    /**
     * Gets the updated bookmark.
     *
     * @return the updated bookmark
     */
    public IBookmark getBookmark()
    {
        return bookmark;
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
