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

package org.apache.directory.studio.connection.ui.widgets;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.actions.ActionHandlerManager;
import org.apache.directory.studio.connection.ui.actions.CloseConnectionAction;
import org.apache.directory.studio.connection.ui.actions.CollapseAllAction;
import org.apache.directory.studio.connection.ui.actions.ConnectionViewActionProxy;
import org.apache.directory.studio.connection.ui.actions.CopyAction;
import org.apache.directory.studio.connection.ui.actions.DeleteAction;
import org.apache.directory.studio.connection.ui.actions.ExpandAllAction;
import org.apache.directory.studio.connection.ui.actions.NewConnectionAction;
import org.apache.directory.studio.connection.ui.actions.NewConnectionFolderAction;
import org.apache.directory.studio.connection.ui.actions.OpenConnectionAction;
import org.apache.directory.studio.connection.ui.actions.PasteAction;
import org.apache.directory.studio.connection.ui.actions.PropertiesAction;
import org.apache.directory.studio.connection.ui.actions.RenameAction;
import org.apache.directory.studio.connection.ui.actions.StudioActionProxy;
import org.apache.directory.studio.connection.ui.dnd.ConnectionTransfer;
import org.apache.directory.studio.connection.ui.dnd.DragConnectionListener;
import org.apache.directory.studio.connection.ui.dnd.DropConnectionListener;
import org.apache.directory.studio.utils.ActionUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;


/**
 * This class manages all the actions of the connection widget.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionActionGroup implements ActionHandlerManager, IMenuListener
{
    /** The collapse all action. */
    private CollapseAllAction collapseAllAction;

    /** The expand all action. */
    private ExpandAllAction expandAllAction;

    /** The Constant newConnectionAction. */
    protected static final String NEW_CONNECTION_ACTION = "newConnectionAction"; //$NON-NLS-1$

    /** The Constant newConnectionFolderAction. */
    protected static final String NEW_CONNECTION_FOLDER_ACTION = "newConnectionFolderAction"; //$NON-NLS-1$

    /** The Constant openConnectionAction. */
    protected static final String OPEN_CONNECTION_ACTION = "openConnectionAction"; //$NON-NLS-1$

    /** The Constant closeConnectionAction. */
    protected static final String CLOSE_CONNECTION_ACTION = "closeConnectionAction"; //$NON-NLS-1$

    /** The Constant copyConnectionAction. */
    protected static final String COPY_CONNECTION_ACTION = "copyConnectionAction"; //$NON-NLS-1$

    /** The Constant pasteConnectionAction. */
    protected static final String PASTE_CONNECTION_ACTION = "pasteConnectionAction"; //$NON-NLS-1$

    /** The Constant deleteConnectionAction. */
    protected static final String DELETE_CONNECTION_ACTION = "deleteConnectionAction"; //$NON-NLS-1$

    /** The Constant renameConnectionAction. */
    protected static final String RENAME_CONNECTION_ACTION = "renameConnectionAction"; //$NON-NLS-1$

    /** The Constant propertyDialogAction. */
    protected static final String PROPERTY_DIALOG_ACTION = "propertyDialogAction"; //$NON-NLS-1$

    /** The drag connection listener. */
    private DragConnectionListener dragConnectionListener;

    /** The drop connection listener. */
    private DropConnectionListener dropConnectionListener;

    /** The action map. */
    protected Map<String, ConnectionViewActionProxy> connectionActionMap;

    /** The action bars. */
    protected IActionBars actionBars;

    /** The connection main widget. */
    protected ConnectionWidget mainWidget;


    /**
     * Creates a new instance of ConnectionActionGroup.
     *
     * @param mainWidget the connection main widget
     * @param configuration the connection widget configuration
     */
    public ConnectionActionGroup( ConnectionWidget mainWidget, ConnectionConfiguration configuration )
    {
        this.mainWidget = mainWidget;
        TreeViewer viewer = mainWidget.getViewer();

        collapseAllAction = new CollapseAllAction( viewer );
        expandAllAction = new ExpandAllAction( viewer );

        connectionActionMap = new HashMap<String, ConnectionViewActionProxy>();

        connectionActionMap.put( NEW_CONNECTION_ACTION, new ConnectionViewActionProxy( viewer, this,
            new NewConnectionAction() ) );
        connectionActionMap.put( NEW_CONNECTION_FOLDER_ACTION, new ConnectionViewActionProxy( viewer, this,
            new NewConnectionFolderAction() ) );
        connectionActionMap.put( OPEN_CONNECTION_ACTION, new ConnectionViewActionProxy( viewer, this,
            new OpenConnectionAction() ) );
        connectionActionMap.put( CLOSE_CONNECTION_ACTION, new ConnectionViewActionProxy( viewer, this,
            new CloseConnectionAction() ) );
        connectionActionMap
            .put( PASTE_CONNECTION_ACTION, new ConnectionViewActionProxy( viewer, this, new PasteAction() ) );
        connectionActionMap.put( COPY_CONNECTION_ACTION, new ConnectionViewActionProxy( viewer, this, new CopyAction(
            ( StudioActionProxy ) connectionActionMap.get( PASTE_CONNECTION_ACTION ) ) ) );
        connectionActionMap.put( DELETE_CONNECTION_ACTION, new ConnectionViewActionProxy( viewer, this,
            new DeleteAction() ) );
        connectionActionMap.put( RENAME_CONNECTION_ACTION, new ConnectionViewActionProxy( viewer, this,
            new RenameAction() ) );
        connectionActionMap.put( PROPERTY_DIALOG_ACTION, new ConnectionViewActionProxy( viewer, this,
            new PropertiesAction() ) );

        // DND support
        dropConnectionListener = new DropConnectionListener();
        dragConnectionListener = new DragConnectionListener( viewer );
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[]
            { ConnectionTransfer.getInstance() };
        viewer.addDragSupport( ops, transfers, dragConnectionListener );
        viewer.addDropSupport( ops, transfers, dropConnectionListener );
    }


    /**
     * Disposes this action group.
     */
    public void dispose()
    {
        if ( mainWidget != null )
        {
            for ( Iterator<String> it = connectionActionMap.keySet().iterator(); it.hasNext(); )
            {
                String key = it.next();
                ConnectionViewActionProxy action = ( ConnectionViewActionProxy ) this.connectionActionMap.get( key );
                action.dispose();
                action = null;
                it.remove();
            }

            collapseAllAction.dispose();
            collapseAllAction = null;

            expandAllAction.dispose();
            expandAllAction = null;

            connectionActionMap.clear();
            connectionActionMap = null;

            actionBars = null;
            mainWidget = null;

            dragConnectionListener = null;
            dropConnectionListener = null;
        }
    }


    /**
     * Enables the action handlers.
     *
     * @param actionBars the action bars
     */
    public void enableGlobalActionHandlers( IActionBars actionBars )
    {
        this.actionBars = actionBars;
        activateGlobalActionHandlers();
    }


    /**
     * Fills the tool bar.
     *
     * @param toolBarManager the tool bar manager
     */
    public void fillToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( ( IAction ) this.connectionActionMap.get( NEW_CONNECTION_ACTION ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.connectionActionMap.get( OPEN_CONNECTION_ACTION ) );
        toolBarManager.add( ( IAction ) this.connectionActionMap.get( CLOSE_CONNECTION_ACTION ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( expandAllAction );
        toolBarManager.add( collapseAllAction );

        toolBarManager.update( true );
    }


    /**
     * Fills the local menu.
     *
     * @param menuManager the local menu manager
     */
    public void fillMenu( IMenuManager menuManager )
    {
    }


    /**
     * Fills the context menu.
     *
     * @param menuManager the context menu manager
     */
    public void fillContextMenu( IMenuManager menuManager )
    {
        menuManager.setRemoveAllWhenShown( true );
        menuManager.addMenuListener( this );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation fills the context menu.
     */
    public void menuAboutToShow( IMenuManager menuManager )
    {
        // add
        menuManager.add( ( IAction ) connectionActionMap.get( NEW_CONNECTION_ACTION ) );
        menuManager.add( ( IAction ) connectionActionMap.get( NEW_CONNECTION_FOLDER_ACTION ) );
        menuManager.add( new Separator() );

        // open/close
        if ( ( ( IAction ) connectionActionMap.get( CLOSE_CONNECTION_ACTION ) ).isEnabled() )
        {
            menuManager.add( ( IAction ) connectionActionMap.get( CLOSE_CONNECTION_ACTION ) );
        }
        else if ( ( ( IAction ) connectionActionMap.get( OPEN_CONNECTION_ACTION ) ).isEnabled() )
        {
            menuManager.add( ( IAction ) connectionActionMap.get( OPEN_CONNECTION_ACTION ) );
        }
        menuManager.add( new Separator() );

        // copy/paste/...
        menuManager.add( ( IAction ) connectionActionMap.get( COPY_CONNECTION_ACTION ) );
        menuManager.add( ( IAction ) connectionActionMap.get( PASTE_CONNECTION_ACTION ) );
        menuManager.add( ( IAction ) connectionActionMap.get( DELETE_CONNECTION_ACTION ) );
        menuManager.add( ( IAction ) connectionActionMap.get( RENAME_CONNECTION_ACTION ) );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( ( IAction ) connectionActionMap.get( PROPERTY_DIALOG_ACTION ) );
    }


    /**
     * Activates the action handlers.
     */
    public void activateGlobalActionHandlers()
    {
        if ( actionBars == null )
        {
            IAction copyConnectionAction = ( IAction ) connectionActionMap.get( COPY_CONNECTION_ACTION );
            copyConnectionAction.setActionDefinitionId( ConnectionUIConstants.CMD_COPY );
            ActionUtils.activateActionHandler( copyConnectionAction );

            IAction pasteConnectionAction = ( IAction ) connectionActionMap.get( PASTE_CONNECTION_ACTION );
            pasteConnectionAction.setActionDefinitionId( ConnectionUIConstants.CMD_PASTE );
            ActionUtils.activateActionHandler( pasteConnectionAction );

            IAction deleteConnectionAction = ( IAction ) connectionActionMap.get( DELETE_CONNECTION_ACTION );
            deleteConnectionAction.setActionDefinitionId( ConnectionUIConstants.CMD_DELETE );
            ActionUtils.activateActionHandler( deleteConnectionAction );

            IAction propertyDialogAction = ( IAction ) connectionActionMap.get( PROPERTY_DIALOG_ACTION );
            propertyDialogAction.setActionDefinitionId( ConnectionUIConstants.CMD_PROPERTIES );
            ActionUtils.activateActionHandler( propertyDialogAction );
        }
        else
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), ( IAction ) connectionActionMap
                .get( COPY_CONNECTION_ACTION ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), ( IAction ) connectionActionMap
                .get( PASTE_CONNECTION_ACTION ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), ( IAction ) connectionActionMap
                .get( DELETE_CONNECTION_ACTION ) );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), ( IAction ) connectionActionMap
                .get( RENAME_CONNECTION_ACTION ) );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), ( IAction ) connectionActionMap
                .get( PROPERTY_DIALOG_ACTION ) );
            actionBars.updateActionBars();
        }
    }


    /**
     * Deactivates the action handlers.
     */
    public void deactivateGlobalActionHandlers()
    {
        if ( actionBars == null )
        {
            IAction copyConnectionAction = ( IAction ) connectionActionMap.get( COPY_CONNECTION_ACTION );
            ActionUtils.deactivateActionHandler( copyConnectionAction );
            IAction pasteConnectionAction = ( IAction ) connectionActionMap.get( PASTE_CONNECTION_ACTION );
            ActionUtils.deactivateActionHandler( pasteConnectionAction );
            IAction deleteConnectionAction = ( IAction ) connectionActionMap.get( DELETE_CONNECTION_ACTION );
            ActionUtils.deactivateActionHandler( deleteConnectionAction );
            IAction propertyDialogAction = ( IAction ) connectionActionMap.get( PROPERTY_DIALOG_ACTION );
            ActionUtils.deactivateActionHandler( propertyDialogAction );
        }
        else
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), null );
            actionBars.updateActionBars();
        }
    }
}
