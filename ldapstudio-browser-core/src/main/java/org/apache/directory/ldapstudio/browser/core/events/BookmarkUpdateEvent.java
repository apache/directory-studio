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

package org.apache.directory.ldapstudio.browser.core.events;


import org.apache.directory.ldapstudio.browser.core.model.IBookmark;


public class BookmarkUpdateEvent
{

    public static final int BOOKMARK_ADDED = 1;

    public static final int BOOKMARK_REMOVED = 2;

    public static final int BOOKMARK_UPDATED = 3;

    private int detail;

    private IBookmark bookmark;


    public BookmarkUpdateEvent( IBookmark bookmark, int detail )
    {
        this.bookmark = bookmark;
        this.detail = detail;
    }


    public IBookmark getBookmark()
    {
        return bookmark;
    }


    public int getDetail()
    {
        return detail;
    }

}
