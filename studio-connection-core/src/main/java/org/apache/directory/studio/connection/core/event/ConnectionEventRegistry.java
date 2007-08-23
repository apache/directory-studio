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

package org.apache.directory.studio.connection.core.event;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.directory.studio.connection.core.Connection;


/**
 * The ConnectionEventRegistry is a central point to register for connection specific
 * events and to fire events to registered listeners.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionEventRegistry
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
     * Notifies each {@link ConnectionUpdateListener} about the opened connection.
     * Uses the {@link EventRunner}s.
     *
     * @param connection the opened connection
     * @param source the source
     */
    public static void fireConnectionOpened( final Connection connection, final Object source )
    {
        if ( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Map<ConnectionUpdateListener, EventRunner> listeners = new HashMap<ConnectionUpdateListener, EventRunner>(
            connectionUpdateListeners );
        Iterator<ConnectionUpdateListener> it = listeners.keySet().iterator();
        while ( it.hasNext() )
        {
            final ConnectionUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.connectionOpened( connection );
                }
            };

            EventRunner runner = listeners.get( listener );
            synchronized ( lock )
            {
                runner.execute( runnable );
            }
        }
    }


    /**
     * Notifies each {@link ConnectionUpdateListener} about the closed connection.
     * Uses the {@link EventRunner}s.
     *
     * @param connection the closed connection
     * @param source the source
     */
    public static void fireConnectionClosed( final Connection connection, final Object source )
    {
        if ( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Map<ConnectionUpdateListener, EventRunner> listeners = new HashMap<ConnectionUpdateListener, EventRunner>(
            connectionUpdateListeners );
        Iterator<ConnectionUpdateListener> it = listeners.keySet().iterator();
        while ( it.hasNext() )
        {
            final ConnectionUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.connectionClosed( connection );
                }
            };

            EventRunner runner = listeners.get( listener );
            synchronized ( lock )
            {
                runner.execute( runnable );
            }
        }
    }


    /**
     * Notifies each {@link ConnectionUpdateListener} about the updated connection.
     * Uses the {@link EventRunner}s.
     *
     * @param connection the updated connection
     * @param source the source
     */
    public static void fireConnectionUpdated( final Connection connection, final Object source )
    {
        if ( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Map<ConnectionUpdateListener, EventRunner> listeners = new HashMap<ConnectionUpdateListener, EventRunner>(
            connectionUpdateListeners );
        Iterator<ConnectionUpdateListener> it = listeners.keySet().iterator();
        while ( it.hasNext() )
        {
            final ConnectionUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.connectionUpdated( connection );
                }
            };

            EventRunner runner = listeners.get( listener );
            synchronized ( lock )
            {
                runner.execute( runnable );
            }
        }
    }


    /**
     * Notifies each {@link ConnectionUpdateListener} about the renamed connection.
     * Uses the {@link EventRunner}s.
     *
     * @param connection the renamed connection
     * @param oldName the old name
     * @param source the source
     */
    public static void fireConnectionRenamed( final Connection connection, final String oldName, final Object source )
    {
        if ( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Map<ConnectionUpdateListener, EventRunner> listeners = new HashMap<ConnectionUpdateListener, EventRunner>(
            connectionUpdateListeners );
        Iterator<ConnectionUpdateListener> it = listeners.keySet().iterator();
        while ( it.hasNext() )
        {
            final ConnectionUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.connectionRenamed( connection, oldName );
                }
            };

            EventRunner runner = listeners.get( listener );
            synchronized ( lock )
            {
                runner.execute( runnable );
            }
        }
    }


    /**
     * Notifies each {@link ConnectionUpdateListener} about the added connection.
     * Uses the {@link EventRunner}s.
     *
     * @param connection the added connection
     * @param source the source
     */
    public static void fireConnectionAdded( final Connection connection, final Object source )
    {
        if ( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Map<ConnectionUpdateListener, EventRunner> listeners = new HashMap<ConnectionUpdateListener, EventRunner>(
            connectionUpdateListeners );
        Iterator<ConnectionUpdateListener> it = listeners.keySet().iterator();
        while ( it.hasNext() )
        {
            final ConnectionUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.connectionAdded( connection );
                }
            };

            EventRunner runner = listeners.get( listener );
            synchronized ( lock )
            {
                runner.execute( runnable );
            }
        }
    }


    /**
     * Notifies each {@link ConnectionUpdateListener} about the removed connection.
     * Uses the {@link EventRunner}s.
     *
     * @param connection the removed connection
     * @param source the source
     */
    public static void fireConnectionRemoved( final Connection connection, final Object source )
    {
        if ( isEventFireingSuspendedInCurrentThread() )
        {
            return;
        }

        Map<ConnectionUpdateListener, EventRunner> listeners = new HashMap<ConnectionUpdateListener, EventRunner>(
            connectionUpdateListeners );
        Iterator<ConnectionUpdateListener> it = listeners.keySet().iterator();
        while ( it.hasNext() )
        {
            final ConnectionUpdateListener listener = it.next();
            EventRunnable runnable = new EventRunnable()
            {
                public void run()
                {
                    listener.connectionRemoved( connection );
                }
            };

            EventRunner runner = listeners.get( listener );
            synchronized ( lock )
            {
                runner.execute( runnable );
            }
        }
    }

}
