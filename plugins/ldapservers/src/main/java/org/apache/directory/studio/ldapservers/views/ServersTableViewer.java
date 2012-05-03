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
package org.apache.directory.studio.ldapservers.views;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.LdapServersManagerListener;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerEvent;
import org.apache.directory.studio.ldapservers.model.LdapServerEventType;
import org.apache.directory.studio.ldapservers.model.LdapServerListener;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
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
 */
public class ServersTableViewer extends TreeViewer
{
    /** The root element */
    protected static final String ROOT = "root"; //$NON-NLS-1$

    /** The label provider */
    private ServersViewLabelProvider labelProvider;

    /** The server handler listener */
    private LdapServersManagerListener serversHandlerListener;

    /** The server listener */
    private LdapServerListener serverListener;

    /** A flag to stop the animation */
    private boolean stopAnimation;

    /** The list of server needing animation */
    private List<LdapServer> serversNeedingAnimation = new ArrayList<LdapServer>();


    /**
     * Creates a new instance of ServersTableViewer.
     *
     * @param tree
     *      the associated tree
     */
    public ServersTableViewer( Tree tree )
    {
        super( tree );

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
        serversHandlerListener = new LdapServersManagerListener()
        {
            public void serverAdded( LdapServer server )
            {
                addServer( server );
                server.addListener( serverListener );
            }


            public void serverRemoved( LdapServer server )
            {
                refreshServer( server );
            }


            public void serverUpdated( LdapServer server )
            {
                removeServer( server );
                server.removeListener( serverListener );

            }
        };

        // Adding the listener to the servers handler
        LdapServersManager.getDefault().addListener( serversHandlerListener );

        // The server listener
        serverListener = new LdapServerListener()
        {
            public void serverChanged( LdapServerEvent event )
            {
                // Checking if the event is null
                if ( event == null )
                {
                    return;
                }

                // Getting the kind of event and the associated server 
                LdapServerEventType kind = event.getKind();
                LdapServer server = event.getServer();
                switch ( kind )
                {
                    // The server status has changed
                    case STATUS_CHANGED:
                        // First, we refresh the server
                        refreshServer( server );

                        // Then, we get the status of the server to see if we
                        // need to start or stop the animation thread
                        LdapServerStatus state = server.getStatus();

                        // If the state is STARTING or STOPPING, we need to
                        // add the server to the list of servers needing
                        // animation and eventually start the animation thread
                        if ( ( state == LdapServerStatus.STARTING ) || ( state == LdapServerStatus.STOPPING ) )
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
        for ( LdapServer server : LdapServersManager.getDefault().getServersList() )
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
    private void addServer( final LdapServer server )
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
    private void refreshServer( final LdapServer server )
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                try
                {
                    refresh( server );
                    ISelection sel = ServersTableViewer.this.getSelection();
                    ServersTableViewer.this.setSelection( sel );
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
    private void removeServer( final LdapServer server )
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
                        for ( LdapServer server : serversNeedingAnimation.toArray( new LdapServer[0] ) )
                        {
                            if ( server != null && getTree() != null && !getTree().isDisposed() )
                            {
                                updateAnimation( server );
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                        // Trace.trace( Trace.FINEST, "Error in Servers view animation", e ); TODO
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
    private void updateAnimation( LdapServer server )
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
            // Trace.trace( Trace.WARNING, "Error in optimized animation", e ); TODO
        }
    }
}