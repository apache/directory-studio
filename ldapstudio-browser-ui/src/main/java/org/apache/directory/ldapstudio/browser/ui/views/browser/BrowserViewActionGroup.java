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

package org.apache.directory.ldapstudio.browser.ui.views.browser;


import org.apache.directory.ldapstudio.browser.ui.actions.CopyAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyDnAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyEntryAsCsvAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyEntryAsLdifAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyUrlAction;
import org.apache.directory.ldapstudio.browser.ui.actions.DeleteAction;
import org.apache.directory.ldapstudio.browser.ui.actions.ImportExportAction;
import org.apache.directory.ldapstudio.browser.ui.actions.LocateEntryInDitAction;
import org.apache.directory.ldapstudio.browser.ui.actions.MoveAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewBatchOperationAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewBookmarkAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewEntryAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewSearchAction;
import org.apache.directory.ldapstudio.browser.ui.actions.PasteAction;
import org.apache.directory.ldapstudio.browser.ui.actions.RenameAction;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.BrowserViewActionProxy;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.BrowserActionProxy;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserActionGroup;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.commands.ICommandService;


public class BrowserViewActionGroup extends BrowserActionGroup
{

    private ShowDITAction showDITAction;

    private ShowSearchesAction showSearchesAction;

    private ShowBookmarksAction showBookmarksAction;

    private ShowDirectoryMetadataEntriesAction showDirectoryMetadataEntriesAction;

    private OpenBrowserPreferencePageAction openBrowserPreferencePageAction;

    private static final String locateEntryInDitAction = "locateEntryInDitAction";

    private static final String newEntryAction = "newEntryAction";

    private static final String newSearchAction = "newSearchAction";

    private static final String newBookmarkAction = "newBookmarkAction";

    private static final String newBatchOperationAction = "newBatchOperationAction";

    private static final String copyAction = "copyAction";

    private static final String pasteAction = "pasteAction";

    private static final String deleteAction = "deleteAction";

    private static final String moveAction = "moveAction";

    private static final String renameAction = "renameAction";

    private static final String copyDnAction = "copyDnAction";

    private static final String copyUrlAction = "copyUrlAction";

    private static final String copyEntryAsLdifDnOnlyAction = "copyEntryAsLdifDnOnlyAction";

    private static final String copyEntryAsLdifReturningAttributesOnlyAction = "copyEntryAsLdifReturningAttributesOnlyAction";

    private static final String copyEntryAsLdifAction = "copyEntryAsLdifAction";

    private static final String copyEntryAsLdifOperationalAction = "copyEntryAsLdifOperationalAction";

    private static final String copyEntryAsCsvDnOnlyAction = "copyEntryAsCsvDnOnlyAction";

    private static final String copyEntryAsCsvReturningAttributesOnlyAction = "copyEntryAsCsvReturningAttributesOnlyAction";

    private static final String copyEntryAsCsvAction = "copyEntryAsCsvAction";

    private static final String copyEntryAsCsvOperationalAction = "copyEntryAsCsvOperationalAction";

    private static final String importDsmlAction = "importDsmlAction";

    private static final String importLdifAction = "importLdifAction";

    private static final String exportLdifAction = "exportLdifAction";
    
    private static final String exportDsmlAction = "exportDsmlAction";

    private static final String exportCsvAction = "exportCsvAction";

    private static final String exportExcelAction = "exportExcelAction";


    public BrowserViewActionGroup( BrowserView view )
    {
        super( view.getMainWidget(), view.getConfiguration() );
        TreeViewer viewer = view.getMainWidget().getViewer();

        this.showDITAction = new ShowDITAction();
        this.showSearchesAction = new ShowSearchesAction();
        this.showBookmarksAction = new ShowBookmarksAction();
        this.showDirectoryMetadataEntriesAction = new ShowDirectoryMetadataEntriesAction();
        this.openBrowserPreferencePageAction = new OpenBrowserPreferencePageAction();

        this.browserActionMap.put( newEntryAction, new BrowserViewActionProxy( viewer, new NewEntryAction( view
            .getSite().getWorkbenchWindow() ) ) );
        this.browserActionMap.put( newSearchAction, new BrowserViewActionProxy( viewer, new NewSearchAction() ) );
        this.browserActionMap.put( newBookmarkAction, new BrowserViewActionProxy( viewer, new NewBookmarkAction() ) );
        this.browserActionMap.put( newBatchOperationAction, new BrowserViewActionProxy( viewer,
            new NewBatchOperationAction() ) );

        this.browserActionMap.put( locateEntryInDitAction, new BrowserViewActionProxy( viewer,
            new LocateEntryInDitAction() ) );

        this.browserActionMap.put( pasteAction, new BrowserViewActionProxy( viewer, new PasteAction() ) );
        this.browserActionMap.put( copyAction, new BrowserViewActionProxy( viewer, new CopyAction(
            ( BrowserActionProxy ) this.browserActionMap.get( pasteAction ) ) ) );
        this.browserActionMap.put( deleteAction, new BrowserViewActionProxy( viewer, new DeleteAction() ) );
        this.browserActionMap.put( moveAction, new BrowserViewActionProxy( viewer, new MoveAction() ) );
        this.browserActionMap.put( renameAction, new BrowserViewActionProxy( viewer, new RenameAction() ) );

        this.browserActionMap.put( copyDnAction, new BrowserViewActionProxy( viewer, new CopyDnAction() ) );
        this.browserActionMap.put( copyUrlAction, new BrowserViewActionProxy( viewer, new CopyUrlAction() ) );

        this.browserActionMap.put( copyEntryAsLdifAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsLdifAction( CopyEntryAsLdifAction.MODE_NORMAL ) ) );
        this.browserActionMap.put( copyEntryAsLdifDnOnlyAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsLdifAction( CopyEntryAsLdifAction.MODE_DN_ONLY ) ) );
        this.browserActionMap.put( copyEntryAsLdifReturningAttributesOnlyAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsLdifAction( CopyEntryAsLdifAction.MODE_RETURNING_ATTRIBUTES_ONLY ) ) );
        this.browserActionMap.put( copyEntryAsLdifOperationalAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsLdifAction( CopyEntryAsLdifAction.MODE_INCLUDE_OPERATIONAL_ATTRIBUTES ) ) );
        this.browserActionMap.put( copyEntryAsCsvAction, new BrowserViewActionProxy( viewer, new CopyEntryAsCsvAction(
            CopyEntryAsLdifAction.MODE_NORMAL ) ) );
        this.browserActionMap.put( copyEntryAsCsvDnOnlyAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsCsvAction( CopyEntryAsLdifAction.MODE_DN_ONLY ) ) );
        this.browserActionMap.put( copyEntryAsCsvReturningAttributesOnlyAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsCsvAction( CopyEntryAsLdifAction.MODE_RETURNING_ATTRIBUTES_ONLY ) ) );
        this.browserActionMap.put( copyEntryAsCsvOperationalAction, new BrowserViewActionProxy( viewer,
            new CopyEntryAsCsvAction( CopyEntryAsLdifAction.MODE_INCLUDE_OPERATIONAL_ATTRIBUTES ) ) );

        this.browserActionMap.put( importDsmlAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
                ImportExportAction.TYPE_IMPORT_DSML ) ) );
        this.browserActionMap.put( exportDsmlAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_DSML ) ) );
        this.browserActionMap.put( importLdifAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_IMPORT_LDIF ) ) );
        this.browserActionMap.put( exportLdifAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_LDIF ) ) );
        this.browserActionMap.put( exportCsvAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_CSV ) ) );
        this.browserActionMap.put( exportExcelAction, new BrowserViewActionProxy( viewer, new ImportExportAction(
            ImportExportAction.TYPE_EXPORT_EXCEL ) ) );

    }


    public void dispose()
    {
        if ( this.openBrowserPreferencePageAction != null )
        {

            this.showDITAction.dispose();
            this.showDITAction = null;
            this.showSearchesAction.dispose();
            this.showSearchesAction = null;
            this.showBookmarksAction.dispose();
            this.showBookmarksAction = null;
            this.showDirectoryMetadataEntriesAction.dispose();
            this.showDirectoryMetadataEntriesAction = null;
            this.openBrowserPreferencePageAction.dispose();
            this.openBrowserPreferencePageAction = null;

            this.openBrowserPreferencePageAction = null;
        }

        super.dispose();
    }


    public void fillMenu( IMenuManager menuManager )
    {

        menuManager.add( this.openSortDialogAction );
        menuManager.add( new Separator() );
        menuManager.add( this.showDITAction );
        menuManager.add( this.showSearchesAction );
        menuManager.add( this.showBookmarksAction );
        menuManager.add( this.showDirectoryMetadataEntriesAction );
        menuManager.add( new Separator() );
        menuManager.add( this.openBrowserPreferencePageAction );
        menuManager.update( true );
    }


    public void menuAboutToShow( IMenuManager menuManager )
    {

        // new
        menuManager.add( ( IAction ) this.browserActionMap.get( newEntryAction ) );
        menuManager.add( ( IAction ) this.browserActionMap.get( newSearchAction ) );
        menuManager.add( ( IAction ) this.browserActionMap.get( newBookmarkAction ) );
        menuManager.add( ( IAction ) this.browserActionMap.get( newBatchOperationAction ) );
        menuManager.add( new Separator() );

        // navigation
        BrowserViewActionProxy leid = ( BrowserViewActionProxy ) this.browserActionMap.get( locateEntryInDitAction );
        leid.setImageDescriptor( leid.getAction().getImageDescriptor() );
        menuManager.add( leid );
        menuManager.add( ( IAction ) this.browserActionMap.get( upAction ) );
        menuManager.add( new Separator() );

        // copy/paste/...
        menuManager.add( ( IAction ) this.browserActionMap.get( copyAction ) );
        menuManager.add( ( IAction ) this.browserActionMap.get( pasteAction ) );
        menuManager.add( ( IAction ) this.browserActionMap.get( deleteAction ) );
        menuManager.add( ( IAction ) this.browserActionMap.get( moveAction ) );
        menuManager.add( ( IAction ) this.browserActionMap.get( renameAction ) );
        MenuManager advancedMenuManager = new MenuManager( "Advanced" );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyDnAction ) );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyUrlAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyEntryAsLdifDnOnlyAction ) );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyEntryAsLdifReturningAttributesOnlyAction ) );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyEntryAsLdifAction ) );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyEntryAsLdifOperationalAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyEntryAsCsvDnOnlyAction ) );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyEntryAsCsvReturningAttributesOnlyAction ) );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyEntryAsCsvAction ) );
        advancedMenuManager.add( ( IAction ) this.browserActionMap.get( copyEntryAsCsvOperationalAction ) );
        advancedMenuManager.add( new Separator() );
        menuManager.add( advancedMenuManager );
        menuManager.add( new Separator() );

        // filter, batch
        menuManager.add( ( IAction ) this.browserActionMap.get( filterChildrenAction ) );
        if ( ( ( IAction ) this.browserActionMap.get( unfilterChildrenAction ) ).isEnabled() )
        {
            menuManager.add( ( IAction ) this.browserActionMap.get( unfilterChildrenAction ) );
        }
        menuManager.add( new Separator() );

        // import/export
        MenuManager importMenuManager = new MenuManager( "Import" );
        importMenuManager.add( ( IAction ) this.browserActionMap.get( importLdifAction ) );
        importMenuManager.add( ( IAction ) this.browserActionMap.get( importDsmlAction ) );
        importMenuManager.add( new Separator() );
        menuManager.add( importMenuManager );

        MenuManager exportMenuManager = new MenuManager( "Export" );
        exportMenuManager.add( ( IAction ) this.browserActionMap.get( exportLdifAction ) );
        exportMenuManager.add( ( IAction ) this.browserActionMap.get( exportDsmlAction ) );
        exportMenuManager.add( new Separator() );
        exportMenuManager.add( ( IAction ) this.browserActionMap.get( exportCsvAction ) );
        exportMenuManager.add( ( IAction ) this.browserActionMap.get( exportExcelAction ) );
        menuManager.add( exportMenuManager );
        menuManager.add( new Separator() );

        // refresh
        menuManager.add( ( IAction ) this.browserActionMap.get( refreshAction ) );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );

        // properties
        menuManager.add( ( IAction ) this.browserActionMap.get( propertyDialogAction ) );

    }


    public void activateGlobalActionHandlers()
    {

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), ( IAction ) this.browserActionMap
                .get( copyAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), ( IAction ) this.browserActionMap
                .get( pasteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), ( IAction ) this.browserActionMap
                .get( deleteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.MOVE.getId(), ( IAction ) this.browserActionMap
                .get( moveAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), ( IAction ) this.browserActionMap
                .get( renameAction ) );
        }

        super.activateGlobalActionHandlers();

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction leid = ( IAction ) this.browserActionMap.get( locateEntryInDitAction );
            commandService.getCommand( leid.getActionDefinitionId() ).setHandler( new ActionHandler( leid ) );
        }

    }


    public void deactivateGlobalActionHandlers()
    {

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.RENAME.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.MOVE.getId(), null );
        }

        super.deactivateGlobalActionHandlers();

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction leid = ( IAction ) this.browserActionMap.get( locateEntryInDitAction );
            commandService.getCommand( leid.getActionDefinitionId() ).setHandler( null );
        }

    }

}
