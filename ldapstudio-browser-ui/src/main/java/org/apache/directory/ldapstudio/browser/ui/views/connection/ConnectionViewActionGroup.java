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

package org.apache.directory.ldapstudio.browser.ui.views.connection;


import org.apache.directory.ldapstudio.browser.ui.actions.ImportExportAction;
import org.apache.directory.ldapstudio.browser.ui.actions.OpenSchemaBrowserAction;
import org.apache.directory.ldapstudio.browser.ui.actions.SelectAllAction;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.ConnectionViewActionProxy;
import org.apache.directory.ldapstudio.browser.ui.dnd.ConnectionTransfer;
import org.apache.directory.ldapstudio.browser.ui.dnd.SearchTransfer;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionActionGroup;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;


public class ConnectionViewActionGroup extends ConnectionActionGroup
{
    private ConnectionView view;

    private static final String selectAllAction = "selectAllAction";

    private static final String importDsmlAction = "importDsmlAction";
    
    private static final String exportDsmlAction = "exportDsmlAction";

    private static final String importLdifAction = "importLdifAction";

    private static final String exportLdifAction = "exportLdifAction";

    private static final String exportCsvAction = "exportCsvAction";

    private static final String exportExcelAction = "exportExcelAction";

    private static final String openSchemaBrowserAction = "openSchemaBrowserAction";

    private DragConnectionListener dragConnectionListener;

    private DropConnectionListener dropConnectionListener;


    public ConnectionViewActionGroup( ConnectionView view )
    {
        super( view.getMainWidget(), view.getConfiguration() );
        this.view = view;
        TableViewer viewer = view.getMainWidget().getViewer();

        this.connectionActionMap.put( selectAllAction, new ConnectionViewActionProxy( viewer, new SelectAllAction(
            viewer ) ) );
        this.connectionActionMap.put( importDsmlAction, new ConnectionViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_IMPORT_DSML ) ) );
        this.connectionActionMap.put( exportDsmlAction, new ConnectionViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_DSML ) ) );
        this.connectionActionMap.put( importLdifAction, new ConnectionViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_IMPORT_LDIF ) ) );
        this.connectionActionMap.put( exportLdifAction, new ConnectionViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_LDIF ) ) );
        this.connectionActionMap.put( exportCsvAction, new ConnectionViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_CSV ) ) );
        this.connectionActionMap.put( exportExcelAction, new ConnectionViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_EXCEL ) ) );

        this.connectionActionMap.put( openSchemaBrowserAction, new ConnectionViewActionProxy( viewer,
            new OpenSchemaBrowserAction() ) );

        // DND support
        this.dropConnectionListener = new DropConnectionListener();
        this.dragConnectionListener = new DragConnectionListener();
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[]
            { ConnectionTransfer.getInstance(), SearchTransfer.getInstance() };
        viewer.addDragSupport( ops, transfers, this.dragConnectionListener );
        viewer.addDropSupport( ops, transfers, this.dropConnectionListener );
    }


    public void dispose()
    {
        if ( this.view != null )
        {

            this.dragConnectionListener.dispose();
            this.dragConnectionListener = null;
            this.dropConnectionListener.dispose();
            this.dropConnectionListener = null;

            this.view = null;
        }
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

        menuManager.add( ( IAction ) this.connectionActionMap.get( openSchemaBrowserAction ) );
        menuManager.add( new Separator() );

        // copy/paste/...
        menuManager.add( ( IAction ) this.connectionActionMap.get( copyConnectionAction ) );
        menuManager.add( ( IAction ) this.connectionActionMap.get( pasteConnectionAction ) );
        menuManager.add( ( IAction ) this.connectionActionMap.get( deleteConnectionAction ) );
        menuManager.add( ( IAction ) this.connectionActionMap.get( selectAllAction ) );
        menuManager.add( ( IAction ) this.connectionActionMap.get( renameConnectionAction ) );
        menuManager.add( new Separator() );

        // import/export
        MenuManager importMenuManager = new MenuManager( "Import" );
        importMenuManager.add( ( IAction ) this.connectionActionMap.get( importLdifAction ) );
        importMenuManager.add( ( IAction ) this.connectionActionMap.get( importDsmlAction ) );
        importMenuManager.add( new Separator() );
        menuManager.add( importMenuManager );
        MenuManager exportMenuManager = new MenuManager( "Export" );
        exportMenuManager.add( ( IAction ) this.connectionActionMap.get( exportLdifAction ) );
        exportMenuManager.add( ( IAction ) this.connectionActionMap.get( exportDsmlAction ) );
        exportMenuManager.add( new Separator() );
        exportMenuManager.add( ( IAction ) this.connectionActionMap.get( exportCsvAction ) );
        exportMenuManager.add( ( IAction ) this.connectionActionMap.get( exportExcelAction ) );
        menuManager.add( exportMenuManager );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );

        // properties
        menuManager.add( ( IAction ) this.connectionActionMap.get( propertyDialogAction ) );
    }


    public void activateGlobalActionHandlers()
    {

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.SELECT_ALL.getId(), ( IAction ) this.connectionActionMap
                .get( selectAllAction ) );
        }

        super.activateGlobalActionHandlers();

    }


    public void deactivateGlobalActionHandlers()
    {

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.SELECT_ALL.getId(), null );
        }

        super.deactivateGlobalActionHandlers();

    }

}
