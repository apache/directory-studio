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

package org.apache.directory.studio.connection.ui.actions;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;


/**
 * Proxy class for actions. The proxy class registers for modification events and 
 * updates the real actions on every modificaton. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class StudioActionProxy extends Action implements ISelectionChangedListener, ConnectionUpdateListener
{

    /** The action handler manager, used to deactivate and activate the action handlers and key bindings. */
    private ActionHandlerManager actionHandlerManager;

    /** The real action. */
    protected StudioAction action;

    /** The selection provider. */
    protected ISelectionProvider selectionProvider;


    /**
     * Creates a new instance of StudioActionProxy.
     * 
     * @param selectionProvider the selection provider
     * @param actionHandlerManager the action handler manager
     * @param action the action
     * @param style the style
     */
    protected StudioActionProxy( ISelectionProvider selectionProvider, ActionHandlerManager actionHandlerManager,
        StudioAction action, int style )
    {
        super( action.getText(), style );
        this.selectionProvider = selectionProvider;
        this.actionHandlerManager = actionHandlerManager;
        this.action = action;

        super.setImageDescriptor( action.getImageDescriptor() );
        super.setActionDefinitionId( action.getCommandId() );

        selectionProvider.addSelectionChangedListener( this );

        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionUIPlugin.getDefault().getEventRunner() );

        updateAction();
    }


    /**
     * Creates a new instance of StudioActionProxy.
     * 
     * @param selectionProvider the selection provider
     * @param actionHandlerManager the action handler manager
     * @param action the action
     */
    protected StudioActionProxy( ISelectionProvider selectionProvider, ActionHandlerManager actionHandlerManager,
        StudioAction action )
    {
        this( selectionProvider, actionHandlerManager, action, Action.AS_PUSH_BUTTON );
    }


    /**
     * Disposes this action proxy.
     */
    public void dispose()
    {
        ConnectionEventRegistry.removeConnectionUpdateListener( this );
        selectionProvider.removeSelectionChangedListener( this );

        action.dispose();
        action = null;
    }


    /**
     * Checks if is disposed.
     * 
     * @return true, if is disposed
     */
    public boolean isDisposed()
    {
        return action == null;
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionUpdated(org.apache.directory.studio.connection.core.Connection)
     */
    public final void connectionUpdated( Connection connection )
    {
        if ( !isDisposed() )
        {
            updateAction();
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionAdded(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionAdded( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionRemoved(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionRemoved( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionOpened(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionOpened( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionClosed(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionClosed( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderModified(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
        connectionUpdated( null );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderAdded(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderAdded( ConnectionFolder connectionFolder )
    {
        connectionUpdated( null );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderRemoved(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderRemoved( ConnectionFolder connectionFolder )
    {
        connectionUpdated( null );
    }


    /**
     * Input changed.
     * 
     * @param input the input
     */
    public void inputChanged( Object input )
    {
        if ( !isDisposed() )
        {
            action.setInput( input );
            selectionChanged( new SelectionChangedEvent( this.selectionProvider, new StructuredSelection() ) );
            // updateAction();
        }
    }


    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged( SelectionChangedEvent event )
    {
        if ( !isDisposed() )
        {
            ISelection selection = event.getSelection();
            action.setSelectedConnections( SelectionUtils.getConnections( selection ) );
            action.setSelectedConnectionFolders( SelectionUtils.getConnectionFolders( selection ) );
            updateAction();
        }
    }


    /**
     * Updates the action.
     */
    public void updateAction()
    {
        if ( !isDisposed() )
        {
            setText( action.getText() );
            setToolTipText( action.getText() );
            setEnabled( action.isEnabled() );
            setImageDescriptor( action.getImageDescriptor() );
            setChecked( action.isChecked() );
        }
    }


    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        if ( !isDisposed() )
        {
            // deactivate global actions
            if ( actionHandlerManager != null )
            {
                actionHandlerManager.deactivateGlobalActionHandlers();
            }

            action.run();

            // activate global actions
            if ( actionHandlerManager != null )
            {
                actionHandlerManager.activateGlobalActionHandlers();
            }
        }
    }


    /**
     * Gets the real action.
     * 
     * @return the real action
     */
    public StudioAction getAction()
    {
        return action;
    }

}
