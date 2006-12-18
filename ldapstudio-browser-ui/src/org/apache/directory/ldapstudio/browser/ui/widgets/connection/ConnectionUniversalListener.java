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

package org.apache.directory.ldapstudio.browser.ui.widgets.connection;


import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateListener;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;


public class ConnectionUniversalListener implements ConnectionUpdateListener, EntryUpdateListener,
    SearchUpdateListener, BookmarkUpdateListener
{

    protected TableViewer viewer;


    public ConnectionUniversalListener( TableViewer viewer )
    {
        this.viewer = viewer;

        EventRegistry.addConnectionUpdateListener( this );
        EventRegistry.addEntryUpdateListener( this );
        EventRegistry.addSearchUpdateListener( this );
        EventRegistry.addBookmarkUpdateListener( this );
    }


    public void dispose()
    {
        if ( this.viewer != null )
        {
            EventRegistry.removeConnectionUpdateListener( this );
            EventRegistry.removeEntryUpdateListener( this );
            EventRegistry.removeSearchUpdateListener( this );
            EventRegistry.removeBookmarkUpdateListener( this );
            this.viewer = null;
        }
    }


    public void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
        if ( this.viewer != null )
        {
            this.viewer.refresh();
            if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_ADDED )
            {
                this.viewer.setSelection( new StructuredSelection( connectionUpdateEvent.getConnection() ) );
            }
        }
    }


    public void entryUpdated( EntryModificationEvent event )
    {
        if ( this.viewer != null )
        {
            this.viewer.refresh();
        }
    }


    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( this.viewer != null )
        {
            this.viewer.refresh();
        }
    }


    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
        if ( this.viewer != null )
        {
            this.viewer.refresh();
        }
    }

}
