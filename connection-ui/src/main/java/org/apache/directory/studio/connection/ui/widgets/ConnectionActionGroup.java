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

import org.apache.directory.studio.connection.ui.actions.ActionHandlerManager;
import org.apache.directory.studio.connection.ui.actions.CloseConnectionAction;
import org.apache.directory.studio.connection.ui.actions.ConnectionViewActionProxy;
import org.apache.directory.studio.connection.ui.actions.CopyAction;
import org.apache.directory.studio.connection.ui.actions.DeleteAction;
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
 * @version $Rev$, $Date$
 */
public class ConnectionActionGroup implements ActionHandlerManager, IMenuListener
{

    /** The Constant newConnectionAction. */
    protected static final String newConnectionAction = "newConnectionAction";

    /** The Constant newConnectionFolderAction. */
    protected static final String newConnectionFolderAction = "newConnectionFolderAction";

    /** The Constant openConnectionAction. */
    protected static final String openConnectionAction = "openConnectionAction";

    /** The Constant closeConnectionAction. */
    protected static final String closeConnectionAction = "closeConnectionAction";

    /** The Constant copyConnectionAction. */
    protected static final String copyConnectionAction = "copyConnectionAction";

    /** The Constant pasteConnectionAction. */
    protected static final String pasteConnectionAction = "pasteConnectionAction";

    /** The Constant deleteConnectionAction. */
    protected static final String deleteConnectionAction = "deleteConnectionAction";

    /** The Constant renameConnectionAction. */
    protected static final String renameConnectionAction = "renameConnectionAction";

    /** The Constant propertyDialogAction. */
    protected static final String propertyDialogAction = "propertyDialogAction";

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
        this.connectionActionMap = new HashMap<String, ConnectionViewActionProxy>();

        TreeViewer viewer = mainWidget.getViewer();
        connectionActionMap.put( newConnectionAction, new ConnectionViewActionProxy( viewer, this,
            new NewConnectionAction() ) );
        connectionActionMap.put( newConnectionFolderAction, new ConnectionViewActionProxy( viewer, this,
            new NewConnectionFolderAction() ) );
        connectionActionMap.put( openConnectionAction, new ConnectionViewActionProxy( viewer, this,
            new OpenConnectionAction() ) );
        connectionActionMap.put( closeConnectionAction, new ConnectionViewActionProxy( viewer, this,
            new CloseConnectionAction() ) );
        connectionActionMap
            .put( pasteConnectionAction, new ConnectionViewActionProxy( viewer, this, new PasteAction() ) );
        connectionActionMap.put( copyConnectionAction, new ConnectionViewActionProxy( viewer, this, new CopyAction(
            ( StudioActionProxy ) connectionActionMap.get( pasteConnectionAction ) ) ) );
        connectionActionMap.put( deleteConnectionAction, new ConnectionViewActionProxy( viewer, this,
            new DeleteAction() ) );
        connectionActionMap.put( renameConnectionAction, new ConnectionViewActionProxy( viewer, this,
            new RenameAction() ) );
        connectionActionMap.put( propertyDialogAction, new ConnectionViewActionProxy( viewer, this,
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
        toolBarManager.add( ( IAction ) this.connectionActionMap.get( newConnectionAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.connectionActionMap.get( openConnectionAction ) );
        toolBarManager.add( ( IAction ) this.connectionActionMap.get( closeConnectionAction ) );

        toolBarManager.update( true );
    }


    /**
     * Fills the local menu.
     *
     * @param menuManager the local menu manager
     */
    public void fillMenu( IMenuManager menuManager )
    {
        // menuManager.add(this.openSortDialogAction);
        // menuManager.add(new Separator());
        // menuManager.update(true);
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
        menuManager.add( ( IAction ) connectionActionMap.get( newConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( newConnectionFolderAction ) );
        menuManager.add( new Separator() );

        // open/close
        if ( ( ( IAction ) connectionActionMap.get( closeConnectionAction ) ).isEnabled() )
        {
            menuManager.add( ( IAction ) connectionActionMap.get( closeConnectionAction ) );
        }
        else if ( ( ( IAction ) connectionActionMap.get( openConnectionAction ) ).isEnabled() )
        {
            menuManager.add( ( IAction ) connectionActionMap.get( openConnectionAction ) );
        }
        menuManager.add( new Separator() );

        // copy/paste/...
        menuManager.add( ( IAction ) connectionActionMap.get( copyConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( pasteConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( deleteConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( renameConnectionAction ) );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );

        // properties
        menuManager.add( ( IAction ) connectionActionMap.get( propertyDialogAction ) );
    }


    /**
     * Activates the action handlers.
     */
    public void activateGlobalActionHandlers()
    {
        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), ( IAction ) connectionActionMap
                .get( copyConnectionAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), ( IAction ) connectionActionMap
                .get( pasteConnectionAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), ( IAction ) connectionActionMap
                .get( deleteConnectionAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), ( IAction ) connectionActionMap
                .get( renameConnectionAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), ( IAction ) connectionActionMap
                .get( propertyDialogAction ) );
            actionBars.updateActionBars();
        }
        else
        {
            IAction ca = ( IAction ) connectionActionMap.get( copyConnectionAction );
            ca.setActionDefinitionId( "org.apache.directory.studio.ldapbrowser.action.copy" );
            ActionUtils.activateActionHandler( ca );

            IAction pa = ( IAction ) connectionActionMap.get( pasteConnectionAction );
            pa.setActionDefinitionId( "org.apache.directory.studio.ldapbrowser.action.paste" );
            ActionUtils.activateActionHandler( pa );

            IAction da = ( IAction ) connectionActionMap.get( deleteConnectionAction );
            da.setActionDefinitionId( "org.apache.directory.studio.ldapbrowser.action.delete" );
            ActionUtils.activateActionHandler( da );

            IAction pda = ( IAction ) connectionActionMap.get( propertyDialogAction );
            pda.setActionDefinitionId( "org.apache.directory.studio.ldapbrowser.action.properties" );
            ActionUtils.activateActionHandler( pda );
        }
    }


    /**
     * Deactivates the action handlers.
     */
    public void deactivateGlobalActionHandlers()
    {
        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), null );
            actionBars.updateActionBars();
        }
        else
        {
            IAction ca = ( IAction ) connectionActionMap.get( copyConnectionAction );
            ActionUtils.deactivateActionHandler( ca );
            IAction pa = ( IAction ) connectionActionMap.get( pasteConnectionAction );
            ActionUtils.deactivateActionHandler( pa );
            IAction da = ( IAction ) connectionActionMap.get( deleteConnectionAction );
            ActionUtils.deactivateActionHandler( da );
            IAction pda = ( IAction ) connectionActionMap.get( propertyDialogAction );
            ActionUtils.deactivateActionHandler( pda );
        }
    }

}
