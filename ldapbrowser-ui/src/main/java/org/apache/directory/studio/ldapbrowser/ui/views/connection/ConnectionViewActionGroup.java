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
import org.apache.directory.studio.connection.ui.widgets.ConnectionActionGroup;
import org.apache.directory.studio.ldapbrowser.ui.actions.ExportConnectionsAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.ImportConnectionsAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.ImportExportAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.OpenSchemaBrowserAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchActionConstants;


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

    /** The Constant importDsmlAction. */
    private static final String importDsmlAction = "importDsmlAction"; //$NON-NLS-1$

    /** The Constant exportDsmlAction. */
    private static final String exportDsmlAction = "exportDsmlAction"; //$NON-NLS-1$

    /** The Constant importLdifAction. */
    private static final String importLdifAction = "importLdifAction"; //$NON-NLS-1$

    /** The Constant exportLdifAction. */
    private static final String exportLdifAction = "exportLdifAction"; //$NON-NLS-1$

    /** The Constant exportCsvAction. */
    private static final String exportCsvAction = "exportCsvAction"; //$NON-NLS-1$

    /** The Constant exportExcelAction. */
    private static final String exportExcelAction = "exportExcelAction"; //$NON-NLS-1$

    /** The Constant exportOdfAction. */
    private static final String exportOdfAction = "exportOdfAction"; //$NON-NLS-1$

    /** The Constant importConnectionsAction. */
    private static final String importConnectionsAction = "importConnectionsAction"; //$NON-NLS-1$

    /** The Constant importConnectionsAction. */
    private static final String exportConnectionsAction = "exportConnectionsAction"; //$NON-NLS-1$

    /** The Constant openSchemaBrowserAction. */
    private static final String openSchemaBrowserAction = "openSchemaBrowserAction"; //$NON-NLS-1$


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
        TreeViewer viewer = view.getMainWidget().getViewer();

        linkWithEditorAction = new LinkWithEditorAction( view );
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
        connectionActionMap.put( exportOdfAction, new ConnectionViewActionProxy( viewer, this, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_ODF ) ) );
        connectionActionMap.put( importConnectionsAction, new ConnectionViewActionProxy( viewer, this,
            new ImportConnectionsAction() ) );
        connectionActionMap.put( exportConnectionsAction, new ConnectionViewActionProxy( viewer, this,
            new ExportConnectionsAction() ) );

        connectionActionMap.put( openSchemaBrowserAction, new ConnectionViewActionProxy( viewer, this,
            new OpenSchemaBrowserAction() ) );
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
            view = null;
        }
        super.dispose();
    }


    /**
     * {@inheritDoc}
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

        menuManager.add( ( IAction ) connectionActionMap.get( openSchemaBrowserAction ) );
        menuManager.add( new Separator() );

        // copy/paste/...
        menuManager.add( ( IAction ) connectionActionMap.get( copyConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( pasteConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( deleteConnectionAction ) );
        menuManager.add( ( IAction ) connectionActionMap.get( renameConnectionAction ) );
        menuManager.add( new Separator() );

        // import/export
        MenuManager importMenuManager = new MenuManager( Messages.getString( "ConnectionViewActionGroup.Import" ) ); //$NON-NLS-1$
        importMenuManager.add( ( IAction ) connectionActionMap.get( importLdifAction ) );
        importMenuManager.add( ( IAction ) connectionActionMap.get( importDsmlAction ) );
        importMenuManager.add( new Separator() );
        importMenuManager.add( ( IAction ) connectionActionMap.get( importConnectionsAction ) );
        importMenuManager.add( new Separator() );
        menuManager.add( importMenuManager );
        MenuManager exportMenuManager = new MenuManager( Messages.getString( "ConnectionViewActionGroup.Export" ) ); //$NON-NLS-1$
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportLdifAction ) );
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportDsmlAction ) );
        exportMenuManager.add( new Separator() );
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportCsvAction ) );
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportExcelAction ) );
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportOdfAction ) );
        exportMenuManager.add( new Separator() );
        exportMenuManager.add( ( IAction ) connectionActionMap.get( exportConnectionsAction ) );
        exportMenuManager.add( new Separator() );
        menuManager.add( exportMenuManager );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( ( IAction ) connectionActionMap.get( propertyDialogAction ) );
    }

}
