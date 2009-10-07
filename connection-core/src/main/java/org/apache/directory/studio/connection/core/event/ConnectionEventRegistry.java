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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * The ConnectionEventRegistry is a central point to register for connection specific
 * events and to fire events to registered listeners.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionEventRegistry
{

    /** The list of threads with suspended event firing. */
    private static List<Long> suspendedEventFiringThreads = new ArrayList<Long>();

    /** The lock used to synchronize event firings */
    protected static Object lock = new Object();

    /** The list with time stamps of recent event firings */
    private static List<Long> fireTimeStamps = new ArrayList<Long>();

    /** A counter for fired events */
    private static long fireCount = 0L;


    /**
     * Checks if event firing is suspended in the current thread.
     *
     * @return true, if event firing is suspended in the current thread
     */
    protected static boolean isEventFiringSuspendedInCurrentThread()
    {
        boolean suspended = suspendedEventFiringThreads.contains( Thread.currentThread().getId() );

        // count the number of fired event in the last second
        // if more then five per second: print a warning
        if ( !suspended )
        {
            fireCount++;

            synchronized ( fireTimeStamps )
            {
                long now = System.currentTimeMillis();

                // remove all time stamps older than one second
                for ( Iterator<Long> it = fireTimeStamps.iterator(); it.hasNext(); )
                {
                    Long ts = it.next();
                    if ( ts + 1000 < now )
                    {
                        it.remove();
                    }
                    else
                    {
                        break;
                    }
                }

                fireTimeStamps.add( now );

                if ( fireTimeStamps.size() > 5 )
                {
                    String message = "Warning: More then " + fireTimeStamps.size() + " events were fired per second!";
                    ConnectionCorePlugin.getDefault().getLog().log(
                        new Status( IStatus.WARNING, ConnectionCoreConstants.PLUGIN_ID, message,
                            new Exception( message ) ) );
                }
            }
        }

        return suspended;
    }


    /**
     * Gets the number of fired events.
     * 
     * @return the number of fired events
     */
    public static long getFireCount()
    {
        return fireCount;
    }


    /**
     * Resumes event firing in the current thread.
     */
    public static void resumeEventFiringInCurrentThread()
    {
        synchronized ( suspendedEventFiringThreads )
        {
            suspendedEventFiringThreads.remove( Thread.currentThread().getId() );
        }
    }


    /**
     * Suspends event firing in the current thread.
     */
    public static void suspendEventFiringInCurrentThread()
    {
        synchronized ( suspendedEventFiringThreads )
        {
            suspendedEventFiringThreads.add( Thread.currentThread().getId() );
        }
    }

    private static final EventManager<ConnectionUpdateListener, EventRunner> connectionUpdateEventManager = new EventManager<ConnectionUpdateListener, EventRunner>();


    /**
     * Adds the connection update listener.
     *
     * @param listener the listener
     * @param runner the runner
     */
    public static void addConnectionUpdateListener( ConnectionUpdateListener listener, EventRunner runner )
    {
        connectionUpdateEventManager.addListener( listener, runner );
    }


    /**
     * Removes the connection update listener.
     *
     * @param listener the listener
     */
    public static void removeConnectionUpdateListener( ConnectionUpdateListener listener )
    {
        connectionUpdateEventManager.removeListener( listener );
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
        EventRunnableFactory<ConnectionUpdateListener> factory = new EventRunnableFactory<ConnectionUpdateListener>()
        {
            public EventRunnable createEventRunnable( final ConnectionUpdateListener listener )
            {
                return new EventRunnable()
                {
                    public void run()
                    {
                        listener.connectionOpened( connection );
                    }
                };
            }
        };
        connectionUpdateEventManager.fire( factory );
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
        EventRunnableFactory<ConnectionUpdateListener> factory = new EventRunnableFactory<ConnectionUpdateListener>()
        {
            public EventRunnable createEventRunnable( final ConnectionUpdateListener listener )
            {
                return new EventRunnable()
                {
                    public void run()
                    {
                        listener.connectionClosed( connection );
                    }
                };
            }
        };
        connectionUpdateEventManager.fire( factory );
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
        EventRunnableFactory<ConnectionUpdateListener> factory = new EventRunnableFactory<ConnectionUpdateListener>()
        {
            public EventRunnable createEventRunnable( final ConnectionUpdateListener listener )
            {
                return new EventRunnable()
                {
                    public void run()
                    {
                        listener.connectionUpdated( connection );
                    }
                };
            }
        };
        connectionUpdateEventManager.fire( factory );
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
        EventRunnableFactory<ConnectionUpdateListener> factory = new EventRunnableFactory<ConnectionUpdateListener>()
        {
            public EventRunnable createEventRunnable( final ConnectionUpdateListener listener )
            {
                return new EventRunnable()
                {
                    public void run()
                    {
                        listener.connectionAdded( connection );
                    }
                };
            }
        };
        connectionUpdateEventManager.fire( factory );
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
        EventRunnableFactory<ConnectionUpdateListener> factory = new EventRunnableFactory<ConnectionUpdateListener>()
        {
            public EventRunnable createEventRunnable( final ConnectionUpdateListener listener )
            {
                return new EventRunnable()
                {
                    public void run()
                    {
                        listener.connectionRemoved( connection );
                    }
                };
            }
        };
        connectionUpdateEventManager.fire( factory );
    }


    /**
     * Notifies each {@link ConnectionUpdateListener} about the modified connection folder.
     * Uses the {@link EventRunner}s.
     *
     * @param connectionFolder the modified connection folder
     * @param source the source
     */
    public static void fireConnectonFolderModified( final ConnectionFolder connectionFolder, final Object source )
    {
        EventRunnableFactory<ConnectionUpdateListener> factory = new EventRunnableFactory<ConnectionUpdateListener>()
        {
            public EventRunnable createEventRunnable( final ConnectionUpdateListener listener )
            {
                return new EventRunnable()
                {
                    public void run()
                    {
                        listener.connectionFolderModified( connectionFolder );
                    }
                };
            }
        };
        connectionUpdateEventManager.fire( factory );
    }


    /**
     * Notifies each {@link ConnectionUpdateListener} about the added connection folder.
     * Uses the {@link EventRunner}s.
     *
     * @param connectionFolder the added connection folder
     * @param source the source
     */
    public static void fireConnectonFolderAdded( final ConnectionFolder connectionFolder, final Object source )
    {
        EventRunnableFactory<ConnectionUpdateListener> factory = new EventRunnableFactory<ConnectionUpdateListener>()
        {
            public EventRunnable createEventRunnable( final ConnectionUpdateListener listener )
            {
                return new EventRunnable()
                {
                    public void run()
                    {
                        listener.connectionFolderAdded( connectionFolder );
                    }
                };
            }
        };
        connectionUpdateEventManager.fire( factory );
    }


    /**
     * Notifies each {@link ConnectionUpdateListener} about the removed connection folder.
     * Uses the {@link EventRunner}s.
     *
     * @param connectionFolder the removed connection folder
     * @param source the source
     */
    public static void fireConnectonFolderRemoved( final ConnectionFolder connectionFolder, final Object source )
    {
        EventRunnableFactory<ConnectionUpdateListener> factory = new EventRunnableFactory<ConnectionUpdateListener>()
        {
            public EventRunnable createEventRunnable( final ConnectionUpdateListener listener )
            {
                return new EventRunnable()
                {
                    public void run()
                    {
                        listener.connectionFolderRemoved( connectionFolder );
                    }
                };
            }
        };
        connectionUpdateEventManager.fire( factory );
    }

    public static class EventManager<L, R extends EventRunner>
    {
        private Map<L, EventRunner> listeners = new HashMap<L, EventRunner>();


        /**
         * Adds the listener.
         *
         * @param listener the listener
         * @param runner the runner
         */
        public void addListener( L listener, R runner )
        {
            assert listener != null;
            assert runner != null;

            synchronized ( listeners )
            {
                if ( !listeners.containsKey( listener ) )
                {
                    listeners.put( listener, runner );
                }
            }
        }


        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void removeListener( L listener )
        {
            synchronized ( listeners )
            {
                if ( listeners.containsKey( listener ) )
                {
                    listeners.remove( listener );
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
        public void fire( EventRunnableFactory<L> factory )
        {
            if ( isEventFiringSuspendedInCurrentThread() )
            {
                return;
            }

            System.out.println( System.currentTimeMillis() +  " Fire: " + factory);
            
            Map<L, EventRunner> clone = new HashMap<L, EventRunner>( listeners );
            for ( final L listener : clone.keySet() )
            {
                EventRunner runner = clone.get( listener );
                synchronized ( lock )
                {
                    EventRunnable runnable = factory.createEventRunnable( listener );
                    runner.execute( runnable );
                }
            }
        }
    }
}
