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


public class EventPerformanceMeter implements ConnectionUpdateListener, EntryUpdateListener, SearchUpdateListener,
    BookmarkUpdateListener
{

    public EventPerformanceMeter()
    {

    }


    public void start()
    {
        EventRegistry.addConnectionUpdateListener( this );
        EventRegistry.addEntryUpdateListener( this );
        EventRegistry.addSearchUpdateListener( this );
        EventRegistry.addBookmarkUpdateListener( this );
    }


    public void stop()
    {
        EventRegistry.removeConnectionUpdateListener( this );
        EventRegistry.removeEntryUpdateListener( this );
        EventRegistry.removeSearchUpdateListener( this );
        EventRegistry.removeBookmarkUpdateListener( this );
    }


    public void connectionUpdated( ConnectionUpdateEvent event )
    {
        System.out.println( event.getClass().getName() + ": " + event );
    }


    public void entryUpdated( EntryModificationEvent event )
    {
        System.out.println( event.getClass().getName() + ": " + event );
    }


    public void searchUpdated( SearchUpdateEvent event )
    {
        System.out.println( event.getClass().getName() + ": " + event );
    }


    public void bookmarkUpdated( BookmarkUpdateEvent event )
    {
        System.out.println( event.getClass().getName() + ": " + event );
    }

}
