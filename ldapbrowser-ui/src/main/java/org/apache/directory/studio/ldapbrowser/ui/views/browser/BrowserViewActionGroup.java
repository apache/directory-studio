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

package org.apache.directory.studio.ldapbrowser.ui.views.browser;


import org.apache.directory.studio.ldapbrowser.common.actions.CopyAction;
import org.apache.directory.studio.ldapbrowser.common.actions.DeleteAction;
import org.apache.directory.studio.ldapbrowser.common.actions.PasteAction;
import org.apache.directory.studio.ldapbrowser.common.actions.RenameAction;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.BrowserActionProxy;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.BrowserViewActionProxy;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserActionGroup;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyDnAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyEntryAsCsvAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyEntryAsLdifAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyUrlAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.ImportExportAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.LocateEntryInDitAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.MoveAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewBatchOperationAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewBookmarkAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewEntryAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewSearchAction;
import org.apache.directory.studio.utils.ActionUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;


/**
 * This class manages all the actions of the browser view.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserViewActionGroup extends BrowserActionGroup
{

    /** The action to show/hide the DIT. */
    private ShowDITAction showDITAction;

    /** The action to show/hide searches. */
    private ShowSearchesAction showSearchesAction;

    /** The action to show/hide bookmarks. */
    private ShowBookmarksAction showBookmarksAction;

    /** The action to show/hide metadata. */
    private ShowDirectoryMetadataEntriesAction showDirectoryMetadataEntriesAction;

    /** The action to open the browser's preference page. */
    private OpenBrowserPreferencePageAction openBrowserPreferencePageAction;

    /** The action to link the current editor with the browser view. */
    private LinkWithEditorAction linkWithEditorAction;

    /** The Constant locateEntryInDitAction. */
    private static final String locateEntryInDitAction = "locateEntryInDitAction";

    /** The Constant newEntryAction. */
    private static final String newEntryAction = "newEntryAction";

    /** The Constant newSearchAction. */
    private static final String newSearchAction = "newSearchAction";

    /** The Constant newBookmarkAction. */
    private static final String newBookmarkAction = "newBookmarkAction";

    /** The Constant newBatchOperationAction. */
    private static final String newBatchOperationAction = "newBatchOperationAction";

    /** The Constant copyAction. */
    private static final String copyAction = "copyAction";

    /** The Constant pasteAction. */
    private static final String pasteAction = "pasteAction";

    /** The Constant deleteAction. */
    private static final String deleteAction = "deleteAction";

    /** The Constant moveAction. */
    private static final String moveAction = "moveAction";

    /** The Constant renameAction. */
    private static final String renameAction = "renameAction";

    /** The Constant copyDnAction. */
    private static final String copyDnAction = "copyDnAction";

    /** The Constant copyUrlAction. */
    private static final String copyUrlAction = "copyUrlAction";

    /** The Constant copyEntryAsLdifDnOnlyAction. */
    private static final String copyEntryAsLdifDnOnlyAction = "copyEntryAsLdifDnOnlyAction";

    /** The Constant copyEntryAsLdifReturningAttributesOnlyAction. */
    private static final String copyEntryAsLdifReturningAttributesOnlyAction = "copyEntryAsLdifReturningAttributesOnlyAction";

    /** The Constant copyEntryAsLdifAction. */
    private static final String copyEntryAsLdifAction = "copyEntryAsLdifAction";

    /** The Constant copyEntryAsLdifOperationalAction. */
    private static final String copyEntryAsLdifOperationalAction = "copyEntryAsLdifOperationalAction";

    /** The Constant copyEntryAsCsvDnOnlyAction. */
    private static final String copyEntryAsCsvDnOnlyAction = "copyEntryAsCsvDnOnlyAction";

    /** The Constant copyEntryAsCsvReturningAttributesOnlyAction. */
    private static final String copyEntryAsCsvReturningAttributesOnlyAction = "copyEntryAsCsvReturningAttributesOnlyAction";

    /** The Constant copyEntryAsCsvAction. */
    private static final String copyEntryAsCsvAction = "copyEntryAsCsvAction";

    /** The Constant copyEntryAsCsvOperationalAction. */
    private static final String copyEntryAsCsvOperationalAction = "copyEntryAsCsvOperationalAction";

    /** The Constant importDsmlAction. */
    private static final String importDsmlAction = "importDsmlAction";

    /** The Constant importLdifAction. */
    private static final String importLdifAction = "importLdifAction";

    /** The Constant exportLdifAction. */
    private static final String exportLdifAction = "exportLdifAction";

    /** The Constant exportDsmlAction. */
    private static final String exportDsmlAction = "exportDsmlAction";

    /** The Constant exportCsvAction. */
    private static final String exportCsvAction = "exportCsvAction";

    /** The Constant exportExcelAction. */
    private static final String exportExcelAction = "exportExcelAction";


    /**
     * Creates a new instance of BrowserViewActionGroup and 
     * creates all the actions.
     * 
     * @param view the browser view
     */
    public BrowserViewActionGroup( BrowserView view )
    {
        super( view.getMainWidget(), view.getConfiguration() );
        TreeViewer viewer = view.getMainWidget().getViewer();

        linkWithEditorAction = new LinkWithEditorAction( view );
        showDITAction = new ShowDITAction();
        showSearchesAction = new ShowSearchesAction();
        showBookmarksAction = new ShowBookmarksAction();
        showDirectoryMetadataEntriesAction = new ShowDirectoryMetadataEntriesAction();
        openBrowserPreferencePageAction = new OpenBrowserPreferencePageAction();

        browserActionMap.put( newEntryAction, new BrowserViewActionProxy( viewer, new NewEntryAction( view.getSite()
            .getWorkbenchWindow() ) ) );
        browserActionMap.put( newSearchAction, new BrowserViewActionProxy( viewer, new NewSearchAction() ) );
        browserActionMap.put( newBookmarkAction, new BrowserViewActionProxy( viewer, new NewBookmarkAction() ) );
        browserActionMap.put( newBatchOperationAction, new BrowserViewActionProxy( viewer,
            new NewBatchOperationAction() ) );

        browserActionMap
            .put( locateEntryInDitAction, new BrowserViewActionProxy( viewer, new LocateEntryInDitAction() ) );

        browserActionMap.put( pasteAction, new BrowserViewActionProxy( viewer, new PasteAction() ) );
        browserActionMap.put( copyAction, new BrowserViewActionProxy( viewer, new CopyAction(
            ( BrowserActionProxy ) browserActionMap.get( pasteAction ) ) ) );
        browserActionMap.put( deleteAction, new BrowserViewActionProxy( viewer, new DeleteAction() ) );
        browserActionMap.put( moveAction, new BrowserViewActionProxy( viewer, new MoveAction() ) );
        browserActionMap.put( renameAction, new BrowserViewActionProxy( viewer, new RenameAction() ) );

        browserActionMap.put( copyDnAction, new BrowserViewActionProxy( viewer, new CopyDnAction() ) );
        browserActionMap.put( copyUrlAction, new BrowserViewActionProxy( viewer, new CopyUrlAction() ) );

        browserActionMap.put( copyEntryAsLdifAction, new BrowserViewActionProxy( viewer, new CopyEntryAsLdifAction(
            CopyEntryAsLdifAction.MODE_NORMAL ) ) );
        browserActionMap.put( copyEntryAsLdifDnOnlyAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsLdifAction( CopyEntryAsLdifAction.MODE_DN_ONLY ) ) );
        browserActionMap.put( copyEntryAsLdifReturningAttributesOnlyAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsLdifAction( CopyEntryAsLdifAction.MODE_RETURNING_ATTRIBUTES_ONLY ) ) );
        browserActionMap.put( copyEntryAsLdifOperationalAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsLdifAction( CopyEntryAsLdifAction.MODE_INCLUDE_OPERATIONAL_ATTRIBUTES ) ) );
        browserActionMap.put( copyEntryAsCsvAction, new BrowserViewActionProxy( viewer, new CopyEntryAsCsvAction(
            CopyEntryAsLdifAction.MODE_NORMAL ) ) );
        browserActionMap.put( copyEntryAsCsvDnOnlyAction, new BrowserViewActionProxy( viewer, new CopyEntryAsCsvAction(
            CopyEntryAsLdifAction.MODE_DN_ONLY ) ) );
        browserActionMap.put( copyEntryAsCsvReturningAttributesOnlyAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsCsvAction( CopyEntryAsLdifAction.MODE_RETURNING_ATTRIBUTES_ONLY ) ) );
        browserActionMap.put( copyEntryAsCsvOperationalAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsCsvAction( CopyEntryAsLdifAction.MODE_INCLUDE_OPERATIONAL_ATTRIBUTES ) ) );

        browserActionMap.put( importDsmlAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_IMPORT_DSML ) ) );
        browserActionMap.put( exportDsmlAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_DSML ) ) );
        browserActionMap.put( importLdifAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_IMPORT_LDIF ) ) );
        browserActionMap.put( exportLdifAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_LDIF ) ) );
        browserActionMap.put( exportCsvAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_CSV ) ) );
        browserActionMap.put( exportExcelAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_EXCEL ) ) );

    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( openBrowserPreferencePageAction != null )
        {
            linkWithEditorAction.dispose();
            linkWithEditorAction = null;

            showDITAction = null;
            showSearchesAction = null;
            showBookmarksAction = null;
            showDirectoryMetadataEntriesAction = null;
            openBrowserPreferencePageAction = null;
        }

        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void fillToolBar( IToolBarManager toolBarManager )
    {

        toolBarManager.add( ( IAction ) browserActionMap.get( upAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) browserActionMap.get( refreshAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( collapseAllAction );
        toolBarManager.add( linkWithEditorAction );
        toolBarManager.update( true );

    }


    /**
     * {@inheritDoc}
     */
    public void fillMenu( IMenuManager menuManager )
    {

        menuManager.add( openSortDialogAction );
        menuManager.add( new Separator() );
        menuManager.add( showDITAction );
        menuManager.add( showSearchesAction );
        menuManager.add( showBookmarksAction );
        menuManager.add( showDirectoryMetadataEntriesAction );
        menuManager.add( new Separator() );
        menuManager.add( openBrowserPreferencePageAction );
        menuManager.update( true );
    }


    /**
     * {@inheritDoc}
     */
    public void menuAboutToShow( IMenuManager menuManager )
    {
        // new
        menuManager.add( ( IAction ) browserActionMap.get( newEntryAction ) );
        menuManager.add( ( IAction ) browserActionMap.get( newSearchAction ) );
        menuManager.add( ( IAction ) browserActionMap.get( newBookmarkAction ) );
        menuManager.add( ( IAction ) browserActionMap.get( newBatchOperationAction ) );
        menuManager.add( new Separator() );

        // navigation
        BrowserViewActionProxy leid = ( BrowserViewActionProxy ) browserActionMap.get( locateEntryInDitAction );
        leid.setImageDescriptor( leid.getAction().getImageDescriptor() );
        menuManager.add( leid );
        menuManager.add( ( IAction ) browserActionMap.get( upAction ) );
        menuManager.add( new Separator() );

        // copy/paste/...
        menuManager.add( ( IAction ) browserActionMap.get( copyAction ) );
        menuManager.add( ( IAction ) browserActionMap.get( pasteAction ) );
        menuManager.add( ( IAction ) browserActionMap.get( deleteAction ) );
        menuManager.add( ( IAction ) browserActionMap.get( moveAction ) );
        menuManager.add( ( IAction ) browserActionMap.get( renameAction ) );
        MenuManager advancedMenuManager = new MenuManager( "Advanced" );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyDnAction ) );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyUrlAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyEntryAsLdifDnOnlyAction ) );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyEntryAsLdifReturningAttributesOnlyAction ) );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyEntryAsLdifAction ) );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyEntryAsLdifOperationalAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyEntryAsCsvDnOnlyAction ) );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyEntryAsCsvReturningAttributesOnlyAction ) );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyEntryAsCsvAction ) );
        advancedMenuManager.add( ( IAction ) browserActionMap.get( copyEntryAsCsvOperationalAction ) );
        advancedMenuManager.add( new Separator() );
        menuManager.add( advancedMenuManager );
        menuManager.add( new Separator() );

        // filter, batch
        menuManager.add( ( IAction ) browserActionMap.get( filterChildrenAction ) );
        if ( ( ( IAction ) browserActionMap.get( unfilterChildrenAction ) ).isEnabled() )
        {
            menuManager.add( ( IAction ) browserActionMap.get( unfilterChildrenAction ) );
        }
        menuManager.add( new Separator() );

        // import/export
        MenuManager importMenuManager = new MenuManager( "Import" );
        importMenuManager.add( ( IAction ) browserActionMap.get( importLdifAction ) );
        importMenuManager.add( ( IAction ) browserActionMap.get( importDsmlAction ) );
        importMenuManager.add( new Separator() );
        menuManager.add( importMenuManager );

        MenuManager exportMenuManager = new MenuManager( "Export" );
        exportMenuManager.add( ( IAction ) browserActionMap.get( exportLdifAction ) );
        exportMenuManager.add( ( IAction ) browserActionMap.get( exportDsmlAction ) );
        exportMenuManager.add( new Separator() );
        exportMenuManager.add( ( IAction ) browserActionMap.get( exportCsvAction ) );
        exportMenuManager.add( ( IAction ) browserActionMap.get( exportExcelAction ) );
        menuManager.add( exportMenuManager );
        menuManager.add( new Separator() );

        // refresh
        menuManager.add( ( IAction ) browserActionMap.get( refreshAction ) );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( ( IAction ) browserActionMap.get( propertyDialogAction ) );

    }


    /**
     * {@inheritDoc}
     */
    public void activateGlobalActionHandlers()
    {
        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), ( IAction ) browserActionMap
                .get( copyAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), ( IAction ) browserActionMap
                .get( pasteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), ( IAction ) browserActionMap
                .get( deleteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.MOVE.getId(), ( IAction ) browserActionMap
                .get( moveAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), ( IAction ) browserActionMap
                .get( renameAction ) );
        }

        super.activateGlobalActionHandlers();

        IAction leid = ( IAction ) browserActionMap.get( locateEntryInDitAction );
        ActionUtils.activateActionHandler( leid );
    }


    /**
     * {@inheritDoc}
     */
    public void deactivateGlobalActionHandlers()
    {
        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.MOVE.getId(), null );
        }

        super.deactivateGlobalActionHandlers();

        IAction leid = ( IAction ) browserActionMap.get( locateEntryInDitAction );
        ActionUtils.deactivateActionHandler( leid );
    }

}
