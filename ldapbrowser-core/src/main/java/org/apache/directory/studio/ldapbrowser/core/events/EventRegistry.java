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


import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.EventRunnable;
import org.apache.directory.studio.connection.core.event.EventRunnableFactory;
import org.apache.directory.studio.connection.core.event.EventRunner;


/**
 * The EventRegistry is a central point to register for Apache Directory Studio specific
 * events and to fire events to registered listeners.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EventRegistry extends ConnectionEventRegistry
{

    static final EventManager<SearchUpdateListener, EventRunner> searchUpdateEventManager = new EventManager<SearchUpdateListener, EventRunner>();


    /**
     * Adds the search update listener.
     *
     * @param listener the listener
     * @param runner the runner
     */
    public static void addSearchUpdateListener( SearchUpdateListener listener, EventRunner runner )
    {
        searchUpdateEventManager.addListener( listener, runner );
    }


    /**
     * Removes the search update listener.
     *
     * @param listener the listener
     */
    public static void removeSearchUpdateListener( SearchUpdateListener listener )
    {
        searchUpdateEventManager.removeListener( listener );
    }


    /**
     * Notifies each {@link SearchUpdateListener} about the the given {@link SearchUpdateEvent}.
     * Uses the {@link EventRunner}s.
     *
     * @param searchUpdateEvent the search update event
     * @param source the source
     */
    public static void fireSearchUpdated( final SearchUpdateEvent searchUpdateEvent, final Object source )
    {
        EventRunnableFactory<SearchUpdateListener> factory = new EventRunnableFactory<SearchUpdateListener>()
        {
            public EventRunnable<SearchUpdateListener> createEventRunnable( final SearchUpdateListener listener )
            {
                return new EventRunnable<SearchUpdateListener>()
                {
                    public void run()
                    {
                        listener.searchUpdated( searchUpdateEvent );
                    }
                };
            }
        };
        searchUpdateEventManager.fire( factory );
    }

    static final EventManager<BookmarkUpdateListener, EventRunner> bookmarkUpdateEventManager = new EventManager<BookmarkUpdateListener, EventRunner>();


    /**
     * Adds the bookmark update listener.
     *
     * @param listener the listener
     * @param runner the runner
     */
    public static void addBookmarkUpdateListener( BookmarkUpdateListener listener, EventRunner runner )
    {
        bookmarkUpdateEventManager.addListener( listener, runner );
    }


    /**
     * Removes the bookmark update listener.
     *
     * @param listener the listener
     */
    public static void removeBookmarkUpdateListener( BookmarkUpdateListener listener )
    {
        bookmarkUpdateEventManager.removeListener( listener );
    }


    /**
     * Notifies each {@link BookmarkUpdateListener} about the the given {@link BookmarkUpdateEvent}.
     * Uses the {@link EventRunner}s.
     *
     * @param bookmarkUpdateEvent the bookmark update event
     * @param source the source
     */
    public static void fireBookmarkUpdated( final BookmarkUpdateEvent bookmarkUpdateEvent, final Object source )
    {
        EventRunnableFactory<BookmarkUpdateListener> factory = new EventRunnableFactory<BookmarkUpdateListener>()
        {
            public EventRunnable<BookmarkUpdateListener> createEventRunnable( final BookmarkUpdateListener listener )
            {
                return new EventRunnable<BookmarkUpdateListener>()
                {
                    public void run()
                    {
                        listener.bookmarkUpdated( bookmarkUpdateEvent );
                    }
                };
            }
        };
        bookmarkUpdateEventManager.fire( factory );
    }

    static final EventManager<BrowserConnectionUpdateListener, EventRunner> browserConnectionUpdateEventManager = new EventManager<BrowserConnectionUpdateListener, EventRunner>();


    /**
     * Adds the browser connection update listener.
     *
     * @param listener the listener
     * @param runner the runner
     */
    public static void addBrowserConnectionUpdateListener( BrowserConnectionUpdateListener listener, EventRunner runner )
    {
        browserConnectionUpdateEventManager.addListener( listener, runner );
    }


    /**
     * Removes the browser connection update listener.
     *
     * @param listener the listener
     */
    public static void removeBrowserConnectionUpdateListener( BrowserConnectionUpdateListener listener )
    {
        browserConnectionUpdateEventManager.removeListener( listener );
    }


    /**
     * Notifies each {@link BrowserConnectionUpdateListener} about the the given {@link BrowserConnectionUpdateEvent}.
     * Uses the {@link EventRunner}s.
     *
     * @param browserConnectionUpdateEvent the browser connection update event
     * @param source the source
     */
    public static void fireBrowserConnectionUpdated( final BrowserConnectionUpdateEvent browserConnectionUpdateEvent,
        final Object source )
    {
        EventRunnableFactory<BrowserConnectionUpdateListener> factory = new EventRunnableFactory<BrowserConnectionUpdateListener>()
        {
            public EventRunnable<BrowserConnectionUpdateListener> createEventRunnable(
                final BrowserConnectionUpdateListener listener )
            {
                return new EventRunnable<BrowserConnectionUpdateListener>()
                {
                    public void run()
                    {
                        listener.browserConnectionUpdated( browserConnectionUpdateEvent );
                    }
                };
            }
        };
        browserConnectionUpdateEventManager.fire( factory );
    }

    static final EventManager<EntryUpdateListener, EventRunner> entryUpdateEventManager = new EventManager<EntryUpdateListener, EventRunner>();


    /**
     * Adds the entry update listener.
     *
     * @param listener the listener
     * @param runner the runner
     */
    public static void addEntryUpdateListener( EntryUpdateListener listener, EventRunner runner )
    {
        entryUpdateEventManager.addListener( listener, runner );
    }


    /**
     * Removes the entry update listener.
     *
     * @param listener the listener
     */
    public static void removeEntryUpdateListener( EntryUpdateListener listener )
    {
        entryUpdateEventManager.removeListener( listener );
    }


    /**
     * Notifies each {@link EntryUpdateListener} about the the given {@link EntryModificationEvent}.
     * Uses the {@link EventRunner}s.
     *
     * @param entryUpdateEvent the entry update event
     * @param source the source
     */
    public static void fireEntryUpdated( final EntryModificationEvent entryUpdateEvent, final Object source )
    {
        EventRunnableFactory<EntryUpdateListener> factory = new EventRunnableFactory<EntryUpdateListener>()
        {
            public EventRunnable<EntryUpdateListener> createEventRunnable( final EntryUpdateListener listener )
            {
                return new EventRunnable<EntryUpdateListener>()
                {
                    public void run()
                    {
                        listener.entryUpdated( entryUpdateEvent );
                    }
                };
            }
        };
        entryUpdateEventManager.fire( factory );
    }

}
