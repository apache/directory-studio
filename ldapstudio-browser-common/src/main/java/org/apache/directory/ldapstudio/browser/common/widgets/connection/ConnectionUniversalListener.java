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

package org.apache.directory.ldapstudio.browser.common.widgets.connection;


import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateListener;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;


/**
 * The ConnectionUniversalListener manages all events for the connection widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionUniversalListener implements ConnectionUpdateListener, EntryUpdateListener,
    SearchUpdateListener, BookmarkUpdateListener
{

    /** The table viewer */
    protected TableViewer viewer;


    /**
     * Creates a new instance of ConnectionUniversalListener.
     *
     * @param viewer the table viewer
     */
    public ConnectionUniversalListener( TableViewer viewer )
    {
        this.viewer = viewer;

        EventRegistry.addConnectionUpdateListener( this );
        EventRegistry.addEntryUpdateListener( this );
        EventRegistry.addSearchUpdateListener( this );
        EventRegistry.addBookmarkUpdateListener( this );
    }


    /**
     * Disposes this universal listener.
     */
    public void dispose()
    {
        if ( viewer != null )
        {
            EventRegistry.removeConnectionUpdateListener( this );
            EventRegistry.removeEntryUpdateListener( this );
            EventRegistry.removeSearchUpdateListener( this );
            EventRegistry.removeBookmarkUpdateListener( this );
            viewer = null;
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation refreshes the viewer. If a new connection was added
     * this connection is selected.
     */
    public void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
        if ( viewer != null )
        {
            viewer.refresh();
            if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.CONNECTION_ADDED )
            {
                viewer.setSelection( new StructuredSelection( connectionUpdateEvent.getConnection() ) );
            }
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation refreshes the viewer.
     */
    public void entryUpdated( EntryModificationEvent event )
    {
        if ( viewer != null )
        {
            viewer.refresh();
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation refreshes the viewer.
     */
    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( viewer != null )
        {
            viewer.refresh();

            // select the right connection
            ISearch search = searchUpdateEvent.getSearch();
            viewer.setSelection( new StructuredSelection( search.getConnection() ), true );
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation refreshes the viewer.
     */
    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
        if ( viewer != null )
        {
            viewer.refresh();
        }
    }

}
