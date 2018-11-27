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
import org.apache.directory.studio.ldapbrowser.common.actions.DeleteAllAction;
import org.apache.directory.studio.ldapbrowser.common.actions.FetchAliasesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.FetchOperationalAttributesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.FetchReferralsAction;
import org.apache.directory.studio.ldapbrowser.common.actions.FetchSubentriesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.RenameAction;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.BrowserActionProxy;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.BrowserViewActionProxy;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserActionGroup;
import org.apache.directory.studio.ldapbrowser.ui.actions.BrowserPasteAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyDnAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyEntryAsCsvAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyEntryAsLdifAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyUrlAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.EntryEditorMenuManager;
import org.apache.directory.studio.ldapbrowser.ui.actions.GotoDnAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.ImportExportAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.LocateEntryInDitAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.MoveAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewBatchOperationAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewBookmarkAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewContextEntryAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewEntryAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewSearchAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.OpenEntryEditorAction;
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
    private static final String locateEntryInDitAction = "locateEntryInDitAction"; //$NON-NLS-1$

    /** The Constant gotoDnAction. */
    private static final String gotoDnAction = "gotoDnAction"; //$NON-NLS-1$

    /** The Constant newEntryAction. */
    private static final String newEntryAction = "newEntryAction"; //$NON-NLS-1$

    /** The Constant newContextEntryAction. */
    private static final String newContextEntryAction = "newContextEntryAction"; //$NON-NLS-1$

    /** The Constant newSearchAction. */
    private static final String newSearchAction = "newSearchAction"; //$NON-NLS-1$

    /** The Constant newBookmarkAction. */
    private static final String newBookmarkAction = "newBookmarkAction"; //$NON-NLS-1$

    /** The Constant newBatchOperationAction. */
    private static final String newBatchOperationAction = "newBatchOperationAction"; //$NON-NLS-1$

    /** The Constant copyAction. */
    private static final String copyAction = "copyAction"; //$NON-NLS-1$

    /** The Constant pasteAction. */
    private static final String pasteAction = "pasteAction"; //$NON-NLS-1$

    /** The Constant deleteAction. */
    private static final String deleteAction = "deleteAction"; //$NON-NLS-1$

    /** The Constant moveAction. */
    private static final String moveAction = "moveAction"; //$NON-NLS-1$

    /** The Constant renameAction. */
    private static final String renameAction = "renameAction"; //$NON-NLS-1$

    /** The Constant copyDnAction. */
    private static final String copyDnAction = "copyDnAction"; //$NON-NLS-1$

    /** The Constant copyUrlAction. */
    private static final String copyUrlAction = "copyUrlAction"; //$NON-NLS-1$

    /** The Constant copyEntryAsLdifDnOnlyAction. */
    private static final String copyEntryAsLdifDnOnlyAction = "copyEntryAsLdifDnOnlyAction"; //$NON-NLS-1$

    /** The Constant copyEntryAsLdifReturningAttributesOnlyAction. */
    private static final String copyEntryAsLdifReturningAttributesOnlyAction = "copyEntryAsLdifReturningAttributesOnlyAction"; //$NON-NLS-1$

    /** The Constant copyEntryAsLdifAction. */
    private static final String copyEntryAsLdifAction = "copyEntryAsLdifAction"; //$NON-NLS-1$

    /** The Constant copyEntryAsLdifOperationalAction. */
    private static final String copyEntryAsLdifOperationalAction = "copyEntryAsLdifOperationalAction"; //$NON-NLS-1$

    /** The Constant copyEntryAsCsvDnOnlyAction. */
    private static final String copyEntryAsCsvDnOnlyAction = "copyEntryAsCsvDnOnlyAction"; //$NON-NLS-1$

    /** The Constant copyEntryAsCsvReturningAttributesOnlyAction. */
    private static final String copyEntryAsCsvReturningAttributesOnlyAction = "copyEntryAsCsvReturningAttributesOnlyAction"; //$NON-NLS-1$

    /** The Constant copyEntryAsCsvAction. */
    private static final String copyEntryAsCsvAction = "copyEntryAsCsvAction"; //$NON-NLS-1$

    /** The Constant copyEntryAsCsvOperationalAction. */
    private static final String copyEntryAsCsvOperationalAction = "copyEntryAsCsvOperationalAction"; //$NON-NLS-1$

    /** The Constant deleteAllAction. */
    private static final String deleteAllAction = "deleteAllAction"; //$NON-NLS-1$

    /** The Constant importDsmlAction. */
    private static final String importDsmlAction = "importDsmlAction"; //$NON-NLS-1$

    /** The Constant importLdifAction. */
    private static final String importLdifAction = "importLdifAction"; //$NON-NLS-1$

    /** The Constant exportLdifAction. */
    private static final String exportLdifAction = "exportLdifAction"; //$NON-NLS-1$

    /** The Constant exportDsmlAction. */
    private static final String exportDsmlAction = "exportDsmlAction"; //$NON-NLS-1$

    /** The Constant exportCsvAction. */
    private static final String exportCsvAction = "exportCsvAction"; //$NON-NLS-1$

    /** The Constant exportExcelAction. */
    private static final String exportExcelAction = "exportExcelAction"; //$NON-NLS-1$

    /** The Constant exportOdfAction. */
    private static final String exportOdfAction = "exportOdfAction"; //$NON-NLS-1$

    /** The Constant fetchOperationalAttributesAction. */
    private static final String fetchOperationalAttributesAction = "fetchOperationalAttributesAction"; //$NON-NLS-1$

    /** The Constant fetchAliasesAction. */
    private static final String fetchAliasesAction = "fetchAliasesAction"; //$NON-NLS-1$

    /** The Constant fetchReferralsAction. */
    private static final String fetchReferralsAction = "fetchReferralsAction"; //$NON-NLS-1$

    /** The Constant fetchSubentriesAction. */
    private static final String fetchSubentriesAction = "fetchSubentriesAction"; //$NON-NLS-1$

    /** The Constant openEntryEditorAction. */
    private static final String openEntryEditorAction = "openEntryEditor"; //$NON-NLS-1$


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
        browserActionMap.put( newContextEntryAction, new BrowserViewActionProxy( viewer, new NewContextEntryAction(
            view.getSite().getWorkbenchWindow() ) ) );
        browserActionMap.put( newSearchAction, new BrowserViewActionProxy( viewer, new NewSearchAction() ) );
        browserActionMap.put( newBookmarkAction, new BrowserViewActionProxy( viewer, new NewBookmarkAction() ) );
        browserActionMap.put( newBatchOperationAction, new BrowserViewActionProxy( viewer,
            new NewBatchOperationAction() ) );

        browserActionMap
            .put( locateEntryInDitAction, new BrowserViewActionProxy( viewer, new LocateEntryInDitAction() ) );
        browserActionMap.put( gotoDnAction, new BrowserViewActionProxy( viewer, new GotoDnAction() ) );

        browserActionMap.put( pasteAction, new BrowserViewActionProxy( viewer, new BrowserPasteAction() ) );
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
        browserActionMap.put( deleteAllAction, new BrowserViewActionProxy( viewer, new DeleteAllAction() ) );

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
        browserActionMap.put( exportOdfAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_ODF ) ) );

        browserActionMap.put( fetchOperationalAttributesAction, new BrowserViewActionProxy( viewer,
            new FetchOperationalAttributesAction() ) );
        browserActionMap.put( fetchAliasesAction, new BrowserViewActionProxy( viewer, new FetchAliasesAction() ) );
        browserActionMap.put( fetchReferralsAction, new BrowserViewActionProxy( viewer, new FetchReferralsAction() ) );
        browserActionMap.put( fetchSubentriesAction, new BrowserViewActionProxy( viewer, new FetchSubentriesAction() ) );

        browserActionMap.put( openEntryEditorAction, new BrowserViewActionProxy( viewer, new OpenEntryEditorAction() ) );
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
        toolBarManager.add( browserActionMap.get( UP_ACTION ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( browserActionMap.get( REFRESH_ACTION ) );
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
        menuManager.add( showQuickSearchAction );
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
        MenuManager newMenuManager = new MenuManager( Messages.getString( "BrowserViewActionGroup.New" ) ); //$NON-NLS-1$
        newMenuManager.add( browserActionMap.get( newEntryAction ) );
        newMenuManager.add( browserActionMap.get( newContextEntryAction ) );
        newMenuManager.add( new Separator() );
        newMenuManager.add( browserActionMap.get( newSearchAction ) );
        newMenuManager.add( browserActionMap.get( newBookmarkAction ) );
        newMenuManager.add( new Separator() );
        newMenuManager.add( browserActionMap.get( newBatchOperationAction ) );
        menuManager.add( newMenuManager );
        menuManager.add( new Separator() );

        // navigation
        menuManager.add( browserActionMap.get( openEntryEditorAction ) );
        MenuManager openWithEntryEditorMenuManager = new EntryEditorMenuManager( mainWidget.getViewer() );
        menuManager.add( openWithEntryEditorMenuManager );
        BrowserViewActionProxy leid = ( BrowserViewActionProxy ) browserActionMap.get( locateEntryInDitAction );
        leid.setImageDescriptor( leid.getAction().getImageDescriptor() );
        menuManager.add( leid );
        menuManager.add( browserActionMap.get( gotoDnAction ) );
        menuManager.add( browserActionMap.get( UP_ACTION ) );
        menuManager.add( new Separator() );

        // copy/paste/...
        menuManager.add( browserActionMap.get( copyAction ) );
        menuManager.add( browserActionMap.get( pasteAction ) );
        menuManager.add( browserActionMap.get( deleteAction ) );
        menuManager.add( browserActionMap.get( moveAction ) );
        menuManager.add( browserActionMap.get( renameAction ) );
        MenuManager advancedMenuManager = new MenuManager( Messages.getString( "BrowserViewActionGroup.Advanced" ) ); //$NON-NLS-1$
        advancedMenuManager.add( browserActionMap.get( copyDnAction ) );
        advancedMenuManager.add( browserActionMap.get( copyUrlAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( browserActionMap.get( copyEntryAsLdifDnOnlyAction ) );
        advancedMenuManager.add( browserActionMap.get( copyEntryAsLdifReturningAttributesOnlyAction ) );
        advancedMenuManager.add( browserActionMap.get( copyEntryAsLdifAction ) );
        advancedMenuManager.add( browserActionMap.get( copyEntryAsLdifOperationalAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( browserActionMap.get( copyEntryAsCsvDnOnlyAction ) );
        advancedMenuManager.add( browserActionMap.get( copyEntryAsCsvReturningAttributesOnlyAction ) );
        advancedMenuManager.add( browserActionMap.get( copyEntryAsCsvAction ) );
        advancedMenuManager.add( browserActionMap.get( copyEntryAsCsvOperationalAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( browserActionMap.get( deleteAllAction ) );
        advancedMenuManager.add( new Separator() );
        menuManager.add( advancedMenuManager );
        menuManager.add( new Separator() );

        // filter, batch
        menuManager.add( browserActionMap.get( FILTER_CHILDREN_ACTION ) );
        if ( ( browserActionMap.get( UNFILTER_CHILDREN_ACTION ) ).isEnabled() )
        {
            menuManager.add( browserActionMap.get( UNFILTER_CHILDREN_ACTION ) );
        }
        menuManager.add( browserActionMap.get( OPEN_QUICK_SEARCH_ACTION ) );
        menuManager.add( new Separator() );

        // import/export
        MenuManager importMenuManager = new MenuManager( Messages.getString( "BrowserViewActionGroup.Import" ) ); //$NON-NLS-1$
        importMenuManager.add( browserActionMap.get( importLdifAction ) );
        importMenuManager.add( browserActionMap.get( importDsmlAction ) );
        importMenuManager.add( new Separator() );
        menuManager.add( importMenuManager );

        MenuManager exportMenuManager = new MenuManager( Messages.getString( "BrowserViewActionGroup.Export" ) ); //$NON-NLS-1$
        exportMenuManager.add( browserActionMap.get( exportLdifAction ) );
        exportMenuManager.add( browserActionMap.get( exportDsmlAction ) );
        exportMenuManager.add( new Separator() );
        exportMenuManager.add( browserActionMap.get( exportCsvAction ) );
        exportMenuManager.add( browserActionMap.get( exportExcelAction ) );
        exportMenuManager.add( browserActionMap.get( exportOdfAction ) );
        menuManager.add( exportMenuManager );
        menuManager.add( new Separator() );

        // refresh
        menuManager.add( browserActionMap.get( REFRESH_ACTION ) );
        MenuManager fetchMenuManager = new MenuManager( Messages.getString( "BrowserViewActionGroup.Fetch" ) ); //$NON-NLS-1$
        if ( browserActionMap.get( fetchOperationalAttributesAction ).isEnabled() )
        {
            fetchMenuManager.add( browserActionMap.get( fetchOperationalAttributesAction ) );
        }
        if ( browserActionMap.get( fetchAliasesAction ).isEnabled() )
        {
            fetchMenuManager.add( browserActionMap.get( fetchAliasesAction ) );
        }
        if ( browserActionMap.get( fetchReferralsAction ).isEnabled() )
        {
            fetchMenuManager.add( browserActionMap.get( fetchReferralsAction ) );
        }
        if ( browserActionMap.get( fetchSubentriesAction ).isEnabled() )
        {
            fetchMenuManager.add( browserActionMap.get( fetchSubentriesAction ) );
        }
        menuManager.add( fetchMenuManager );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( browserActionMap.get( PROPERTY_DIALOG_ACTION ) );
    }


    /**
     * {@inheritDoc}
     */
    public void activateGlobalActionHandlers()
    {
        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), browserActionMap.get( copyAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), browserActionMap.get( pasteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), browserActionMap.get( deleteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.MOVE.getId(), browserActionMap.get( moveAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), browserActionMap.get( renameAction ) );
        }

        super.activateGlobalActionHandlers();

        IAction leid = browserActionMap.get( locateEntryInDitAction );
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

        IAction leid = browserActionMap.get( locateEntryInDitAction );
        ActionUtils.deactivateActionHandler( leid );
    }

}
