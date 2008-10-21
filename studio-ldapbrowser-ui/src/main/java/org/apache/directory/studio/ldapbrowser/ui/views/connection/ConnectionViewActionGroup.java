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

package org.apache.directory.studio.ldapbrowser.ui.views.connection;


import org.apache.directory.studio.connection.ui.actions.ConnectionViewActionProxy;
import org.apache.directory.studio.connection.ui.dnd.ConnectionTransfer;
import org.apache.directory.studio.connection.ui.widgets.ConnectionActionGroup;
import org.apache.directory.studio.ldapbrowser.common.actions.SelectAllAction;
import org.apache.directory.studio.ldapbrowser.common.dnd.SearchTransfer;
import org.apache.directory.studio.ldapbrowser.ui.actions.ImportExportAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.OpenSchemaBrowserAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;


/**
 * This class manages all the actions of the connection view.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionViewActionGroup extends ConnectionActionGroup
{

    /** The connection view */
    private ConnectionView view;

    /** The link with editor action. */
    private LinkWithEditorAction linkWithEditorAction;

    /** The Constant selectAllAction. */
    private static final String selectAllAction = "selectAllAction";

    /** The Constant importDsmlAction. */
    private static final String importDsmlAction = "importDsmlAction";

    /** The Constant exportDsmlAction. */
    private static final String exportDsmlAction = "exportDsmlAction";

    /** The Constant importLdifAction. */
    private static final String importLdifAction = "importLdifAction";

    /** The Constant exportLdifAction. */
    private static final String exportLdifAction = "exportLdifAction";

    /** The Constant exportCsvAction. */
    private static final String exportCsvAction = "exportCsvAction";

    /** The Constant exportExcelAction. */
    private static final String exportExcelAction = "exportExcelAction";

    /** The Constant openSchemaBrowserAction. */
    private static final String openSchemaBrowserAction = "openSchemaBrowserAction";

    /** The drag connection listener. */
    private DragConnectionListener dragConnectionListener;

    /** The drop connection listener. */
    private DropConnectionListener dropConnectionListener;


    /**
     * Creates a new instance of ConnectionViewActionGroup and creates
     * all actions.
     *
     * @param view the connection view
     */
    public ConnectionViewActionGroup( ConnectionView view )
    {
        super( view.getMainWidget(), view.getConfiguration() );
        this.view = view;
        TableViewer viewer = view.getMainWidget().getViewer();

        linkWithEditorAction = new LinkWithEditorAction( view );
        connectionActionMap.put( selectAllAction, new ConnectionViewActionProxy( viewer, this, new SelectAllAction(
            viewer ) ) );
        connectionActionMap.put( importDsmlAction, new ConnectionViewActionProxy( viewer, this, new ImportExportAction(
            ImportExportAction.TYPE_IMPORT_DSML ) ) );
        connectionActionMap.put( exportDsmlAction, new ConnectionViewActionProxy( viewer, this, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_DSML ) ) );
        connectionActionMap.put( importLdifAction, new ConnectionViewActionProxy( viewer, this, new ImportExportAction(
            ImportExportAction.TYPE_IMPORT_LDIF ) ) );
        connectionActionMap.put( exportLdifAction, new ConnectionViewActionProxy( viewer, this, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_LDIF ) ) );
        connectionActionMap.put( exportCsvAction, new ConnectionViewActionProxy( viewer, this, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_CSV ) ) );
        connectionActionMap.put( exportExcelAction, new ConnectionViewActionProxy( viewer, this,
            new ImportExportAction( ImportExportAction.TYPE_EXPORT_EXCEL ) ) );

        connectionActionMap.put( openSchemaBrowserAction, new ConnectionViewActionProxy( viewer, this,
            new OpenSchemaBrowserAction() ) );

        // DND support
        dropConnectionListener = new DropConnectionListener();
        dragConnectionListener = new DragConnectionListener();
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[]
            { ConnectionTransfer.getInstance(), SearchTransfer.getInstance() };
        viewer.addDragSupport( ops, transfers, dragConnectionListener );
        viewer.addDropSupport( ops, transfers, dropConnectionListener );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( view != null )
        {
            linkWithEditorAction.dispose();
            linkWithEditorAction = null;
            dragConnectionListener = null;
            dropConnectionListener = null;
            view = null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void menuAboutToShow( IMenuManager menuManager )
    {

        // add
        menuManager.add( ( IAction ) connectionActionMap.get( newConnectionAction ) );
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

        menuManager.add( ( IAction ) connectionActionMap.get( openSchemaBrowserAction ) );
        menuManager.add( new Separator() );

        // copy/paste/...
        menuManager.add( ( IAction ) connectionActionMap.get( copyConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( pasteConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( deleteConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( selectAllAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( renameConnectionAction ) );
        menuManager.add( new Separator() );

        // import/export
        MenuManager importMenuManager = new MenuManager( "Import" );
        importMenuManager.add( ( IAction ) connectionActionMap.get( importLdifAction ) );
        importMenuManager.add( ( IAction ) connectionActionMap.get( importDsmlAction ) );
        importMenuManager.add( new Separator() );
        menuManager.add( importMenuManager );
        MenuManager exportMenuManager = new MenuManager( "Export" );
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportLdifAction ) );
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportDsmlAction ) );
        exportMenuManager.add( new Separator() );
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportCsvAction ) );
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportExcelAction ) );
        menuManager.add( exportMenuManager );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );

        // properties
        menuManager.add( ( IAction ) connectionActionMap.get( propertyDialogAction ) );
    }


    /**
     * {@inheritDoc}
     */
    public void activateGlobalActionHandlers()
    {

        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.SELECT_ALL.getId(), ( IAction ) connectionActionMap
                .get( selectAllAction ) );
        }

        super.activateGlobalActionHandlers();

    }


    /**
     * {@inheritDoc}
     */
    public void deactivateGlobalActionHandlers()
    {

        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.SELECT_ALL.getId(), null );
        }

        super.deactivateGlobalActionHandlers();

    }

}
