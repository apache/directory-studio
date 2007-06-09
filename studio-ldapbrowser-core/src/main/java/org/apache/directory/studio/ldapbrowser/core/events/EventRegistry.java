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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * The EventRegistry is a central point to register for Apache Directory Studio specific
 * events and to fire events to registered listeners.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EventRegistry
{

    /** The list of threads with suspended event fireing. */
    private static Set<Thread> suspendedEventFireringThreads = new HashSet<Thread>();;

    /** The lock used to synchronize event fireings */
    private static Object lock = new Object();


    /**
     * Checks if event fireing is suspended in the current thread.
     *
     * @return true, if event fireing is suspended in the current thread
     */
    public static boolean isEventFireingSuspendedInCurrentThread()
    {
        return suspendedEventFireringThreads.contains( Thread.currentThread() );
    }


    /**
     * Resumes event fireing in the current thread.
     */
    public static void resumeEventFireingInCurrentThread()
    {
        suspendedEventFireringThreads.remove( Thread.currentThread() );
    }


    /**
     * Suspends event fireing in the current thread.
     */
    public static void suspendEventFireingInCurrentThread()
    {
        suspendedEventFireringThreads.add( Thread.currentThread() );
    }



    /** The map with search update listeners and their runners */
    private static Map<SearchUpdateListener, EventRunner> searchUpdateListeners = new HashMap<SearchUpdateListener, EventRunner>();


    /**
     * Adds the search update listener.
     *
     * @param listener the listener
     * @param runner the runner
     */
    public static void addSearchUpdateListener( SearchUpdateListener listener, EventRunner runner )
    {
        assert listener != null;
        assert runner != null;

        if ( !searchUpdateListeners.containsKey( listener ) )
        {
            searchUpdateListeners.put( listener, runner );
        }
    }


    /**
     * Removes the search update listener.
     *
     * @param listener the listener
     */
    public static void removeSearchUpdateListener( SearchUpdateListener listener )
    {
        if ( searchUpdateListeners.containsKey( listener ) )
        {
            searchUpdateListeners.remove( listener );
        }
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
        if( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Iterator<SearchUpdateListener> it = searchUpdateListeners.keySet().iterator();
        while( it.hasNext() )
        {
            final SearchUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.searchUpdated( searchUpdateEvent );
                }
            };

            EventRunner runner = searchUpdateListeners.get( listener );

            synchronized( lock )
            {
                runner.execute( runnable );
            }
        }
    }


    /** The map with bookmark update listeners and their runners */
    private static Map<BookmarkUpdateListener, EventRunner> bookmarkUpdateListeners = new HashMap<BookmarkUpdateListener, EventRunner>();


    /**
     * Adds the bookmark update listener.
     *
     * @param listener the listener
     * @param runner the runner
     */
    public static void addBookmarkUpdateListener( BookmarkUpdateListener listener, EventRunner runner )
    {
        assert listener != null;
        assert runner != null;

        if ( !bookmarkUpdateListeners.containsKey( listener ) )
        {
            bookmarkUpdateListeners.put( listener, runner );
        }
    }


    /**
     * Removes the bookmark update listener.
     *
     * @param listener the listener
     */
    public static void removeBookmarkUpdateListener( BookmarkUpdateListener listener )
    {
        if ( bookmarkUpdateListeners.containsKey( listener ) )
        {
            bookmarkUpdateListeners.remove( listener );
        }
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
        if( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Iterator<BookmarkUpdateListener> it = bookmarkUpdateListeners.keySet().iterator();
        while( it.hasNext() )
        {
            final BookmarkUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.bookmarkUpdated( bookmarkUpdateEvent );
                }
            };

            EventRunner runner = bookmarkUpdateListeners.get( listener );
            synchronized( lock )
            {
                runner.execute( runnable );
            }
        }
    }

    /** The map with connection update listeners and their runners */
    private static Map<ConnectionUpdateListener, EventRunner> connectionUpdateListeners = new HashMap<ConnectionUpdateListener, EventRunner>();


    /**
     * Adds the connection update listener.
     *
     * @param listener the listener
     * @param runner the runner
     */
    public static void addConnectionUpdateListener( ConnectionUpdateListener listener, EventRunner runner )
    {
        assert listener != null;
        assert runner != null;

        if ( !connectionUpdateListeners.containsKey( listener ) )
        {
            connectionUpdateListeners.put( listener, runner );
        }
    }


    /**
     * Removes the connection update listener.
     *
     * @param listener the listener
     */
    public static void removeConnectionUpdateListener( ConnectionUpdateListener listener )
    {
        if ( connectionUpdateListeners.containsKey( listener ) )
        {
            connectionUpdateListeners.remove( listener );
        }
    }


    /**
     * Notifies each {@link ConnectionUpdateListener} about the the given {@link ConnectionUpdateEvent}.
     * Uses the {@link EventRunner}s.
     *
     * @param connectionUpdateEvent the connection update event
     * @param source the source
     */
    public static void fireConnectionUpdated( final ConnectionUpdateEvent connectionUpdateEvent, final Object source )
    {
        if( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Iterator<ConnectionUpdateListener> it = connectionUpdateListeners.keySet().iterator();
        while( it.hasNext() )
        {
            final ConnectionUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.connectionUpdated( connectionUpdateEvent );
                }
            };

            EventRunner runner = connectionUpdateListeners.get( listener );
            synchronized( lock )
            {
                runner.execute( runnable );
            }
        }
    }


    /** The map with entry update listeners and their runners */
    private static Map<EntryUpdateListener, EventRunner> entryUpdateListeners = new HashMap<EntryUpdateListener, EventRunner>();


    /**
     * Adds the entry update listener.
     *
     * @param listener the listener
     * @param runner the runner
     */
    public static void addEntryUpdateListener( EntryUpdateListener listener, EventRunner runner )
    {
        assert listener != null;
        assert runner != null;

        if ( !entryUpdateListeners.containsKey( listener ) )
        {
            entryUpdateListeners.put( listener, runner );
        }
    }


    /**
     * Removes the entry update listener.
     *
     * @param listener the listener
     */
    public static void removeEntryUpdateListener( EntryUpdateListener listener )
    {
        if ( entryUpdateListeners.containsKey( listener ) )
        {
            entryUpdateListeners.remove( listener );
        }
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
        if( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Iterator<EntryUpdateListener> it = entryUpdateListeners.keySet().iterator();
        while( it.hasNext() )
        {
            final EntryUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.entryUpdated( entryUpdateEvent );
                }
            };

            EventRunner runner = entryUpdateListeners.get( listener );
            synchronized( lock )
            {
                runner.execute( runnable );
            }
        }
    }


}
