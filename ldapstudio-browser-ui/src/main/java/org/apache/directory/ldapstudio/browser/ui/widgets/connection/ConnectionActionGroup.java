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

package org.apache.directory.ldapstudio.browser.ui.widgets.connection;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.ui.actions.CloseConnectionAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyAction;
import org.apache.directory.ldapstudio.browser.ui.actions.DeleteAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewConnectionAction;
import org.apache.directory.ldapstudio.browser.ui.actions.OpenConnectionAction;
import org.apache.directory.ldapstudio.browser.ui.actions.PasteAction;
import org.apache.directory.ldapstudio.browser.ui.actions.PropertiesAction;
import org.apache.directory.ldapstudio.browser.ui.actions.RenameAction;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.ConnectionViewActionProxy;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.BrowserActionProxy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.commands.ICommandService;


public class ConnectionActionGroup implements IMenuListener
{

    protected static final String newConnectionAction = "newConnectionAction";

    protected static final String openConnectionAction = "openConnectionAction";

    protected static final String closeConnectionAction = "closeConnectionAction";

    protected static final String copyConnectionAction = "copyConnectionAction";

    protected static final String pasteConnectionAction = "pasteConnectionAction";

    protected static final String deleteConnectionAction = "deleteConnectionAction";

    protected static final String renameConnectionAction = "renameConnectionAction";

    protected static final String propertyDialogAction = "propertyDialogAction";

    protected Map connectionActionMap;

    protected IActionBars actionBars;

    protected ConnectionWidget mainWidget;


    public ConnectionActionGroup( ConnectionWidget mainWidget, ConnectionConfiguration configuration )
    {
        this.mainWidget = mainWidget;
        this.connectionActionMap = new HashMap();
        TableViewer viewer = mainWidget.getViewer();

        this.connectionActionMap.put( newConnectionAction, new ConnectionViewActionProxy( viewer,
            new NewConnectionAction() ) );
        this.connectionActionMap.put( openConnectionAction, new ConnectionViewActionProxy( viewer,
            new OpenConnectionAction() ) );
        this.connectionActionMap.put( closeConnectionAction, new ConnectionViewActionProxy( viewer,
            new CloseConnectionAction() ) );
        this.connectionActionMap
            .put( pasteConnectionAction, new ConnectionViewActionProxy( viewer, new PasteAction() ) );
        this.connectionActionMap.put( copyConnectionAction, new ConnectionViewActionProxy( viewer, new CopyAction(
            ( BrowserActionProxy ) this.connectionActionMap.get( pasteConnectionAction ) ) ) );
        this.connectionActionMap.put( deleteConnectionAction,
            new ConnectionViewActionProxy( viewer, new DeleteAction() ) );
        this.connectionActionMap.put( renameConnectionAction,
            new ConnectionViewActionProxy( viewer, new RenameAction() ) );
        this.connectionActionMap.put( propertyDialogAction, new ConnectionViewActionProxy( viewer,
            new PropertiesAction() ) );

    }


    public void dispose()
    {
        if ( this.mainWidget != null )
        {

            for ( Iterator it = this.connectionActionMap.keySet().iterator(); it.hasNext(); )
            {
                String key = ( String ) it.next();
                ConnectionViewActionProxy action = ( ConnectionViewActionProxy ) this.connectionActionMap.get( key );
                action.dispose();
                action = null;
                it.remove();
            }
            this.connectionActionMap.clear();
            this.connectionActionMap = null;

            this.actionBars = null;
            this.mainWidget = null;
        }
    }


    public void enableGlobalActionHandlers( IActionBars actionBars )
    {
        this.actionBars = actionBars;
        this.activateGlobalActionHandlers();
    }


    public void fillToolBar( IToolBarManager toolBarManager )
    {

        toolBarManager.add( ( IAction ) this.connectionActionMap.get( newConnectionAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.connectionActionMap.get( openConnectionAction ) );
        toolBarManager.add( ( IAction ) this.connectionActionMap.get( closeConnectionAction ) );

        toolBarManager.update( true );

    }


    public void fillMenu( IMenuManager menuManager )
    {
        // menuManager.add(this.openSortDialogAction);
        // menuManager.add(new Separator());
        // menuManager.update(true);
    }


    public void fillContextMenu( IMenuManager menuManager )
    {
        menuManager.setRemoveAllWhenShown( true );
        menuManager.addMenuListener( this );
    }


    public void menuAboutToShow( IMenuManager menuManager )
    {

        // add
        menuManager.add( ( IAction ) this.connectionActionMap.get( newConnectionAction ) );
        menuManager.add( new Separator() );

        // open/close
        if ( ( ( IAction ) this.connectionActionMap.get( closeConnectionAction ) ).isEnabled() )
            menuManager.add( ( IAction ) this.connectionActionMap.get( closeConnectionAction ) );
        else if ( ( ( IAction ) this.connectionActionMap.get( openConnectionAction ) ).isEnabled() )
            menuManager.add( ( IAction ) this.connectionActionMap.get( openConnectionAction ) );
        menuManager.add( new Separator() );

        // copy/paste/...
        menuManager.add( ( IAction ) this.connectionActionMap.get( copyConnectionAction ) );
        menuManager.add( ( IAction ) this.connectionActionMap.get( pasteConnectionAction ) );
        menuManager.add( ( IAction ) this.connectionActionMap.get( deleteConnectionAction ) );
        menuManager.add( ( IAction ) this.connectionActionMap.get( renameConnectionAction ) );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );

        // properties
        menuManager.add( ( IAction ) this.connectionActionMap.get( propertyDialogAction ) );
    }


    public void activateGlobalActionHandlers()
    {

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), ( IAction ) this.connectionActionMap
                .get( copyConnectionAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), ( IAction ) this.connectionActionMap
                .get( pasteConnectionAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), ( IAction ) this.connectionActionMap
                .get( deleteConnectionAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), ( IAction ) this.connectionActionMap
                .get( renameConnectionAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), ( IAction ) this.connectionActionMap
                .get( propertyDialogAction ) );
            actionBars.updateActionBars();
        }
        else
        {
            if ( commandService != null )
            {
                IAction ca = ( IAction ) this.connectionActionMap.get( copyConnectionAction );
                ca.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.copy" );
                commandService.getCommand( ca.getActionDefinitionId() ).setHandler( new ActionHandler( ca ) );

                IAction pa = ( IAction ) this.connectionActionMap.get( pasteConnectionAction );
                pa.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.paste" );
                commandService.getCommand( pa.getActionDefinitionId() ).setHandler( new ActionHandler( pa ) );

                IAction da = ( IAction ) this.connectionActionMap.get( deleteConnectionAction );
                da.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.delete" );
                commandService.getCommand( da.getActionDefinitionId() ).setHandler( new ActionHandler( da ) );

                IAction pda = ( IAction ) this.connectionActionMap.get( propertyDialogAction );
                pda.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.properties" );
                commandService.getCommand( pda.getActionDefinitionId() ).setHandler( new ActionHandler( pda ) );

            }
        }

    }


    public void deactivateGlobalActionHandlers()
    {

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );

        if ( this.actionBars != null )
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
            if ( commandService != null )
            {
                IAction ca = ( IAction ) this.connectionActionMap.get( copyConnectionAction );
                commandService.getCommand( ca.getActionDefinitionId() ).setHandler( null );

                IAction pa = ( IAction ) this.connectionActionMap.get( pasteConnectionAction );
                commandService.getCommand( pa.getActionDefinitionId() ).setHandler( null );

                IAction da = ( IAction ) this.connectionActionMap.get( deleteConnectionAction );
                commandService.getCommand( da.getActionDefinitionId() ).setHandler( null );

                IAction pda = ( IAction ) this.connectionActionMap.get( propertyDialogAction );
                commandService.getCommand( pda.getActionDefinitionId() ).setHandler( null );

            }
        }
    }

}
