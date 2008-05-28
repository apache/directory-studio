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
package org.apache.directory.studio.apacheds.views;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.model.ServerEvent;
import org.apache.directory.studio.apacheds.model.ServerEventEnum;
import org.apache.directory.studio.apacheds.model.ServerInstance;
import org.apache.directory.studio.apacheds.model.ServerListener;
import org.apache.directory.studio.apacheds.model.ServerStateEnum;
import org.apache.directory.studio.apacheds.model.ServersHandler;
import org.apache.directory.studio.apacheds.model.ServersHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;


/**
 * This class implements a {@link TreeViewer} that displays the servers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerTableViewer extends TreeViewer
{
    protected static final String ROOT = "root";

    private ServersView view;

    private boolean stopAnimation;

    private ServersViewLabelProvider labelProvider;

    private List<ServerInstance> serversNeedingAnimation = new ArrayList<ServerInstance>();

    /** The server handler listener */
    private ServersHandlerListener serversHandlerListener;

    /** The server listener */
    private ServerListener serverListener;


    public ServerTableViewer( ServersView view, Tree tree )
    {
        super( tree );
        this.view = view;

        labelProvider = new ServersViewLabelProvider();
        setLabelProvider( labelProvider );
        setContentProvider( new ServersViewContentProvider() );

        setInput( ROOT );

        addListeners();
    }


    /**
     * Adds the listener
     */
    private void addListeners()
    {
        // The server handler listener
        serversHandlerListener = new ServersHandlerListener()
        {
            public void serverInstanceAdded( ServerInstance serverInstance )
            {
                addServer( serverInstance );
                serverInstance.addListener( serverListener );
            }


            public void serverInstanceRemoved( ServerInstance serverInstance )
            {
                refreshServer( serverInstance );
            }


            public void serverInstanceUpdated( ServerInstance serverInstance )
            {
                removeServer( serverInstance );
                serverInstance.removeListener( serverListener );

            }
        };

        // Adding the listener to the servers handler
        ServersHandler.getDefault().addListener( serversHandlerListener );

        // The server listener
        serverListener = new ServerListener()
        {
            public void serverChanged( ServerEvent event )
            {
                // Checking if the event is null
                if ( event == null )
                {
                    return;
                }

                // Getting the kind of event and the associated server 
                ServerEventEnum kind = event.getKind();
                ServerInstance server = event.getServer();
                switch ( kind )
                {
                    // The server state has changed
                    case STATE_CHANGED:
                        // First, we refresh the server
                        refreshServer( server );

                        // Then, we get the state of the server to see if we
                        // need to start or stop the animation thread
                        ServerStateEnum state = server.getState();

                        // If the state is STARTING or STOPPING, we need to
                        // add the server to the list of servers needing
                        // animation and eventually start the animation thread
                        if ( ( state == ServerStateEnum.STARTING ) || ( state == ServerStateEnum.STOPPING ) )
                        {
                            boolean startAnimationThread = false;

                            synchronized ( serversNeedingAnimation )
                            {
                                if ( !serversNeedingAnimation.contains( server ) )
                                {
                                    if ( serversNeedingAnimation.isEmpty() )
                                        startAnimationThread = true;
                                    serversNeedingAnimation.add( server );
                                }
                            }

                            if ( startAnimationThread )
                            {
                                startAnimationThread();
                            }
                        }

                        // If the state is *not* STARTING or STOPPING, we need
                        // to remove the server from the list of servers
                        // needing animation and eventually stop the animation
                        // if this list is empty
                        else
                        {
                            boolean stopAnimationThread = false;

                            synchronized ( serversNeedingAnimation )
                            {
                                if ( serversNeedingAnimation.contains( server ) )
                                {
                                    serversNeedingAnimation.remove( server );
                                    if ( serversNeedingAnimation.isEmpty() )
                                        stopAnimationThread = true;
                                }
                            }

                            if ( stopAnimationThread )
                            {
                                stopAnimationThread();
                            }
                        }
                        break;
                    // The server has been renamed
                    case RENAMED:
                        // We simply refresh the server
                        refreshServer( server );
                        break;
                }

            }
        };

        // Adding the listener to the servers
        for ( ServerInstance server : ServersHandler.getDefault().getServerInstancesList() )
        {
            server.addListener( serverListener );
        }
    }


    /**
     * Adds a server.
     * 
     * @param server
     *      the server
     */
    private void addServer( final ServerInstance server )
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                add( ROOT, server );
            }
        } );
    }


    /**
     * Refreshes a server.
     * 
     * @param server
     *      the server
     */
    private void refreshServer( final ServerInstance server )
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                try
                {
                    refresh( server );
                    ISelection sel = ServerTableViewer.this.getSelection();
                    ServerTableViewer.this.setSelection( sel );
                }
                catch ( Exception e )
                {
                    // ignore
                }
            }
        } );
    }


    /**
     * Removes a server.
     * 
     * @param server
     *      the server
     */
    private void removeServer( final ServerInstance server )
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                remove( server );
            }
        } );
    }


    /**
     * Starts the animation thread.
     */
    private void startAnimationThread()
    {
        stopAnimation = false;

        final Display display = getTree().getDisplay();
        final int SLEEP = 200;
        final Runnable[] animatorThread = new Runnable[1];
        animatorThread[0] = new Runnable()
        {
            public void run()
            {
                // Checking if we need to stop the animation
                if ( !stopAnimation )
                {
                    try
                    {
                        // Changing the animation state on the label provider
                        labelProvider.animate();

                        // Looping on the currently starting servers
                        for ( ServerInstance server : serversNeedingAnimation.toArray( new ServerInstance[0] ) )
                        {
                            if ( server != null && getTree() != null && !getTree().isDisposed() )
                            {
                                updateAnimation( server );
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                        //                        Trace.trace( Trace.FINEST, "Error in Servers view animation", e ); TODO
                    }

                    // Re-launching the animation
                    display.timerExec( SLEEP, animatorThread[0] );
                }
            }
        };

        // Launching the animation asynchronously
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                display.timerExec( SLEEP, animatorThread[0] );
            }
        } );
    }


    /**
     * Stops the animation thread.
     */
    private void stopAnimationThread()
    {
        stopAnimation = true;
    }


    /**
     * Updates the animation for the given server
     *
     * @param server
     *      the server
     */
    private void updateAnimation( ServerInstance server )
    {
        try
        {
            Widget widget = doFindItem( server );
            TreeItem item = ( TreeItem ) widget;
            item.setText( 1, labelProvider.getColumnText( server, 1 ) );
            item.setImage( 1, labelProvider.getColumnImage( server, 1 ) );
        }
        catch ( Exception e )
        {
            //            Trace.trace( Trace.WARNING, "Error in optimized animation", e );
            //TODO
        }
    }
}