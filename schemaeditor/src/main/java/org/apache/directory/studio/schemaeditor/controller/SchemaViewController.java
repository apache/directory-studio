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
package org.apache.directory.studio.schemaeditor.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.actions.CollapseAllAction;
import org.apache.directory.studio.schemaeditor.controller.actions.DeleteSchemaElementAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ExportSchemasAsOpenLdapAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ExportSchemasAsXmlAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ExportSchemasForADSAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ImportCoreSchemasAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ImportSchemasFromOpenLdapAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ImportSchemasFromXmlAction;
import org.apache.directory.studio.schemaeditor.controller.actions.LinkWithEditorSchemaViewAction;
import org.apache.directory.studio.schemaeditor.controller.actions.NewAttributeTypeAction;
import org.apache.directory.studio.schemaeditor.controller.actions.NewObjectClassAction;
import org.apache.directory.studio.schemaeditor.controller.actions.NewSchemaAction;
import org.apache.directory.studio.schemaeditor.controller.actions.OpenElementAction;
import org.apache.directory.studio.schemaeditor.controller.actions.OpenSchemaViewPreferenceAction;
import org.apache.directory.studio.schemaeditor.controller.actions.OpenSchemaViewSortingDialogAction;
import org.apache.directory.studio.schemaeditor.controller.actions.OpenTypeHierarchyAction;
import org.apache.directory.studio.schemaeditor.controller.actions.SwitchSchemaPresentationToFlatAction;
import org.apache.directory.studio.schemaeditor.controller.actions.SwitchSchemaPresentationToHierarchicalAction;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.apache.directory.studio.schemaeditor.view.views.SchemaView;
import org.apache.directory.studio.schemaeditor.view.views.SchemaViewContentProvider;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;


/**
 * This class implements the Controller for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaViewController
{
    /** The associated view */
    private SchemaView view;

    /** The TreeViewer */
    private TreeViewer viewer;

    /** The authorized Preferences keys*/
    private List<String> authorizedPrefs;

    /** The Context Menu */
    private MenuManager contextMenu;

    /** The SchemaHandlerListener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeAdded(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeAdded( AttributeTypeImpl at )
        {
            SchemaViewContentProvider contentProvider = ( SchemaViewContentProvider ) viewer.getContentProvider();
            contentProvider.attributeTypeAdded( at );
            view.refresh();

            TreeNode wrapper = contentProvider.getWrapper( at );
            if ( wrapper != null )
            {
                viewer.setSelection( new StructuredSelection( wrapper ) );
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeModified(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeModified( AttributeTypeImpl at )
        {
            ( ( SchemaViewContentProvider ) viewer.getContentProvider() ).attributeTypeModified( at );
            view.refresh();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeRemoved(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeRemoved( AttributeTypeImpl at )
        {
            ( ( SchemaViewContentProvider ) viewer.getContentProvider() ).attributeTypeRemoved( at );
            view.refresh();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassAdded(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassAdded( ObjectClassImpl oc )
        {
            SchemaViewContentProvider contentProvider = ( SchemaViewContentProvider ) viewer.getContentProvider();
            contentProvider.objectClassAdded( oc );
            view.refresh();

            TreeNode wrapper = contentProvider.getWrapper( oc );
            if ( wrapper != null )
            {
                viewer.setSelection( new StructuredSelection( wrapper ) );
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassModified(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassModified( ObjectClassImpl oc )
        {
            ( ( SchemaViewContentProvider ) viewer.getContentProvider() ).objectClassModified( oc );
            view.refresh();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassRemoved(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassRemoved( ObjectClassImpl oc )
        {
            ( ( SchemaViewContentProvider ) viewer.getContentProvider() ).objectClassRemoved( oc );
            view.refresh();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaAdded(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaAdded( Schema schema )
        {
            SchemaViewContentProvider contentProvider = ( SchemaViewContentProvider ) viewer.getContentProvider();
            contentProvider.schemaAdded( schema );

            final TreeNode wrapper = contentProvider.getWrapper( schema );

            Display.getDefault().asyncExec( new Runnable()
            {
                public void run()
                {
                    view.refresh();

                    if ( wrapper != null )
                    {
                        viewer.setSelection( new StructuredSelection( wrapper ) );
                    }
                }
            } );
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaRemoved(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaRemoved( Schema schema )
        {
            ( ( SchemaViewContentProvider ) viewer.getContentProvider() ).schemaRemoved( schema );
            view.refresh();
        }
    };

    /** Token used to activate and deactivate shortcuts in the view */
    private IContextActivation contextActivation;

    // The Actions
    private NewSchemaAction newSchema;
    private NewAttributeTypeAction newAttributeType;
    private NewObjectClassAction newObjectClass;
    private OpenElementAction openElement;
    private OpenTypeHierarchyAction openTypeHierarchy;
    private DeleteSchemaElementAction deleteSchemaElement;
    private ImportCoreSchemasAction importCoreSchemas;
    private ImportSchemasFromOpenLdapAction importSchemasFromOpenLdap;
    private ImportSchemasFromXmlAction importSchemasFromXml;
    private ExportSchemasAsOpenLdapAction exportSchemasAsOpenLdap;
    private ExportSchemasAsXmlAction exportSchemasAsXml;
    private ExportSchemasForADSAction exportSchemasForADS;
    private CollapseAllAction collapseAll;
    private OpenSchemaViewSortingDialogAction openSchemaViewSortingDialog;
    private SwitchSchemaPresentationToFlatAction switchSchemaPresentationToFlat;
    private SwitchSchemaPresentationToHierarchicalAction switchSchemaPresentationToHierarchical;
    private OpenSchemaViewPreferenceAction openSchemaViewPreference;
    private LinkWithEditorSchemaViewAction linkWithEditor;


    //    private CommitChangesAction commitChanges;

    /**
     * Creates a new instance of SchemasViewController.
     *
     * @param view
     *      the associated view
     */
    public SchemaViewController( SchemaView view )
    {
        this.view = view;
        viewer = view.getViewer();

        initActions();
        initToolbar();
        initMenu();
        initContextMenu();
        initProjectsHandlerListener();
        initDoubleClickListener();
        initAuthorizedPrefs();
        initPreferencesListener();
        initState();
        initPartListener();
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
        newSchema = new NewSchemaAction();
        newAttributeType = new NewAttributeTypeAction( viewer );
        newObjectClass = new NewObjectClassAction( viewer );
        openElement = new OpenElementAction( viewer );
        openTypeHierarchy = new OpenTypeHierarchyAction( viewer );
        deleteSchemaElement = new DeleteSchemaElementAction( viewer );
        importCoreSchemas = new ImportCoreSchemasAction();
        importSchemasFromOpenLdap = new ImportSchemasFromOpenLdapAction();
        importSchemasFromXml = new ImportSchemasFromXmlAction();
        exportSchemasAsOpenLdap = new ExportSchemasAsOpenLdapAction( viewer );
        exportSchemasAsXml = new ExportSchemasAsXmlAction( viewer );
        exportSchemasForADS = new ExportSchemasForADSAction( viewer );
        collapseAll = new CollapseAllAction( viewer );
        openSchemaViewSortingDialog = new OpenSchemaViewSortingDialogAction();
        switchSchemaPresentationToFlat = new SwitchSchemaPresentationToFlatAction();
        switchSchemaPresentationToHierarchical = new SwitchSchemaPresentationToHierarchicalAction();
        openSchemaViewPreference = new OpenSchemaViewPreferenceAction();
        linkWithEditor = new LinkWithEditorSchemaViewAction( view );
        //        commitChanges = new CommitChangesAction();
    }


    /**
     * Initializes the Toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
        toolbar.add( newSchema );
        toolbar.add( newAttributeType );
        toolbar.add( newObjectClass );
        //        toolbar.add( new Separator() );
        //        toolbar.add( commitChanges );
        toolbar.add( new Separator() );
        toolbar.add( collapseAll );
        toolbar.add( linkWithEditor );
    }


    /**
     * Initializes the Menu.
     */
    private void initMenu()
    {
        IMenuManager menu = view.getViewSite().getActionBars().getMenuManager();
        menu.add( openSchemaViewSortingDialog );
        menu.add( new Separator() );
        IMenuManager schemaPresentationMenu = new MenuManager( Messages
            .getString( "SchemaViewController.SchemaPresentationAction" ) );
        schemaPresentationMenu.add( switchSchemaPresentationToFlat );
        schemaPresentationMenu.add( switchSchemaPresentationToHierarchical );
        menu.add( schemaPresentationMenu );
        menu.add( new Separator() );
        menu.add( linkWithEditor );
        menu.add( new Separator() );
        menu.add( openSchemaViewPreference );
    }


    /**
     * Initializes the ContextMenu.
     */
    private void initContextMenu()
    {
        contextMenu = new MenuManager( "" ); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown( true );
        contextMenu.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                MenuManager newManager = new MenuManager( Messages.getString( "SchemaViewController.NewAction" ) );
                MenuManager importManager = new MenuManager( Messages.getString( "SchemaViewController.ImportAction" ) );
                MenuManager exportManager = new MenuManager( Messages.getString( "SchemaViewController.ExportAction" ) );
                manager.add( newManager );
                newManager.add( newSchema );
                newManager.add( newAttributeType );
                newManager.add( newObjectClass );
                manager.add( new Separator() );
                manager.add( openElement );
                manager.add( openTypeHierarchy );
                manager.add( new Separator() );
                manager.add( deleteSchemaElement );
                manager.add( new Separator() );
                manager.add( importManager );
                importManager.add( importCoreSchemas );
                importManager.add( new Separator() );
                importManager.add( importSchemasFromOpenLdap );
                importManager.add( importSchemasFromXml );
                manager.add( exportManager );
                exportManager.add( exportSchemasAsOpenLdap );
                exportManager.add( exportSchemasAsXml );
                exportManager.add( new Separator() );
                exportManager.add( exportSchemasForADS );

                manager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
            }
        } );

        // set the context menu to the table viewer
        viewer.getControl().setMenu( contextMenu.createContextMenu( viewer.getControl() ) );

        // register the context menu to enable extension actions
        view.getSite().registerContextMenu( contextMenu, viewer );
    }


    /**
     * Initializes the ProjectsHandlerListener.
     */
    private void initProjectsHandlerListener()
    {
        Activator.getDefault().getProjectsHandler().addListener( new ProjectsHandlerAdapter()
        {
            public void openProjectChanged( Project oldProject, Project newProject )
            {
                if ( oldProject != null )
                {
                    removeSchemaHandlerListener( oldProject );
                }

                if ( newProject != null )
                {
                    viewer.getTree().setEnabled( true );
                    newSchema.setEnabled( true );
                    newAttributeType.setEnabled( true );
                    newObjectClass.setEnabled( true );
                    collapseAll.setEnabled( true );
                    linkWithEditor.setEnabled( true );
                    openSchemaViewSortingDialog.setEnabled( true );
                    switchSchemaPresentationToFlat.setEnabled( true );
                    switchSchemaPresentationToHierarchical.setEnabled( true );
                    openSchemaViewPreference.setEnabled( true );
                    //                    commitChanges.setEnabled( true );

                    addSchemaHandlerListener( newProject );
                    view.reloadViewer();
                }
                else
                {
                    viewer.setInput( null );
                    viewer.getTree().setEnabled( false );
                    newSchema.setEnabled( false );
                    newAttributeType.setEnabled( false );
                    newObjectClass.setEnabled( false );
                    collapseAll.setEnabled( false );
                    linkWithEditor.setEnabled( false );
                    openSchemaViewSortingDialog.setEnabled( false );
                    switchSchemaPresentationToFlat.setEnabled( false );
                    switchSchemaPresentationToHierarchical.setEnabled( false );
                    openSchemaViewPreference.setEnabled( false );
                    //                    commitChanges.setEnabled( false );
                }
            }
        } );
    }


    /**
     * Adds the SchemaHandlerListener.
     *
     * @param project
     *      the project
     */
    private void addSchemaHandlerListener( Project project )
    {
        SchemaHandler schemaHandler = project.getSchemaHandler();
        if ( schemaHandler != null )
        {
            schemaHandler.addListener( schemaHandlerListener );
        }
    }


    /**
     * Removes the SchemaHandlerListener.
     *
     * @param project
     *      the project
     */
    private void removeSchemaHandlerListener( Project project )
    {
        SchemaHandler schemaHandler = project.getSchemaHandler();
        if ( schemaHandler != null )
        {
            schemaHandler.removeListener( schemaHandlerListener );
        }
    }


    /**
     * Initializes the DoubleClickListener.
     */
    private void initDoubleClickListener()
    {
        viewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                TreeViewer viewer = view.getViewer();

                // What we get from the viewer is a StructuredSelection
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();

                // Here's the real object (an AttributeTypeWrapper, ObjectClassWrapper or IntermediateNode)
                Object objectSelection = selection.getFirstElement();
                IEditorInput input = null;
                String editorId = null;

                // Selecting the right editor and input
                if ( objectSelection instanceof AttributeTypeWrapper )
                {
                    input = new AttributeTypeEditorInput( ( ( AttributeTypeWrapper ) objectSelection )
                        .getAttributeType() );
                    editorId = AttributeTypeEditor.ID;
                }
                else if ( objectSelection instanceof ObjectClassWrapper )
                {
                    input = new ObjectClassEditorInput( ( ( ObjectClassWrapper ) objectSelection ).getObjectClass() );
                    editorId = ObjectClassEditor.ID;
                }
                else if ( ( objectSelection instanceof Folder ) || ( objectSelection instanceof SchemaWrapper ) )
                {
                    // Here we don't open an editor, we just expand the node.
                    viewer.setExpandedState( objectSelection, !viewer.getExpandedState( objectSelection ) );
                }

                // Let's open the editor
                if ( input != null )
                {
                    try
                    {
                        page.openEditor( input, editorId );
                    }
                    catch ( PartInitException e )
                    {
                        PluginUtils.logError( Messages.getString( "SchemaViewController.ErrorOpeningEditor" ), e );
                        ViewUtils.displayErrorMessageBox( Messages.getString( "SchemaViewController.error" ), Messages
                            .getString( "SchemaViewController.ErrorOpeningEditor" ) );
                    }
                }
            }
        } );
    }


    /**
     * Initializes the Authorized Prefs IDs.
     */
    private void initAuthorizedPrefs()
    {
        authorizedPrefs = new ArrayList<String>();
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_ABBREVIATE_MAX_LENGTH );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_DISPLAY );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SORTING_BY );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SORTING_ORDER );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_LABEL_DISPLAY );
    }


    /**
     * Initializes the listener on the preferences store
     */
    private void initPreferencesListener()
    {
        Activator.getDefault().getPreferenceStore().addPropertyChangeListener( new IPropertyChangeListener()
        {
            /* (non-Javadoc)
             * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
             */
            public void propertyChange( PropertyChangeEvent event )
            {
                if ( authorizedPrefs.contains( event.getProperty() ) )
                {
                    if ( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING == event.getProperty()
                        || PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION == event.getProperty() )
                    {
                        view.reloadViewer();
                    }
                    else
                    {
                        view.refresh();
                    }
                }
            }
        } );
    }


    /**
     * Initializes the state of the View.
     */
    private void initState()
    {
        Project project = Activator.getDefault().getProjectsHandler().getOpenProject();
        if ( project != null )
        {
            viewer.getTree().setEnabled( true );
            newSchema.setEnabled( true );
            newAttributeType.setEnabled( true );
            newObjectClass.setEnabled( true );
            collapseAll.setEnabled( true );
            linkWithEditor.setEnabled( true );
            openSchemaViewSortingDialog.setEnabled( true );
            switchSchemaPresentationToFlat.setEnabled( true );
            switchSchemaPresentationToHierarchical.setEnabled( true );
            openSchemaViewPreference.setEnabled( true );
            //            commitChanges.setEnabled( true );

            addSchemaHandlerListener( project );
            view.reloadViewer();
        }
        else
        {
            viewer.getTree().setEnabled( false );
            newSchema.setEnabled( false );
            newAttributeType.setEnabled( false );
            newObjectClass.setEnabled( false );
            collapseAll.setEnabled( false );
            linkWithEditor.setEnabled( false );
            openSchemaViewSortingDialog.setEnabled( false );
            switchSchemaPresentationToFlat.setEnabled( false );
            switchSchemaPresentationToHierarchical.setEnabled( false );
            openSchemaViewPreference.setEnabled( false );
            //            commitChanges.setEnabled( false );
        }
    }


    /**
     * Initializes the PartListener.
     */
    private void initPartListener()
    {
        view.getSite().getPage().addPartListener( new IPartListener2()
        {
            /**
              * This implementation deactivates the shortcuts when the part is deactivated.
              */
            public void partDeactivated( IWorkbenchPartReference partRef )
            {
                if ( partRef.getPart( false ) == view && contextActivation != null )
                {
                    ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
                        ICommandService.class );
                    if ( commandService != null )
                    {
                        commandService.getCommand( newSchema.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( newAttributeType.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( newObjectClass.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( openElement.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( openTypeHierarchy.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( deleteSchemaElement.getActionDefinitionId() ).setHandler( null );
                    }

                    IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                        IContextService.class );
                    contextService.deactivateContext( contextActivation );
                    contextActivation = null;
                }
            }


            /**
             * This implementation activates the shortcuts when the part is activated.
             */
            public void partActivated( IWorkbenchPartReference partRef )
            {
                if ( partRef.getPart( false ) == view )
                {
                    IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                        IContextService.class );
                    contextActivation = contextService.activateContext( PluginConstants.CONTEXT_SCHEMA_VIEW );

                    ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
                        ICommandService.class );
                    if ( commandService != null )
                    {
                        commandService.getCommand( newSchema.getActionDefinitionId() ).setHandler(
                            new ActionHandler( newSchema ) );
                        commandService.getCommand( newAttributeType.getActionDefinitionId() ).setHandler(
                            new ActionHandler( newAttributeType ) );
                        commandService.getCommand( newObjectClass.getActionDefinitionId() ).setHandler(
                            new ActionHandler( newObjectClass ) );
                        commandService.getCommand( openElement.getActionDefinitionId() ).setHandler(
                            new ActionHandler( openElement ) );
                        commandService.getCommand( openTypeHierarchy.getActionDefinitionId() ).setHandler(
                            new ActionHandler( openTypeHierarchy ) );
                        commandService.getCommand( deleteSchemaElement.getActionDefinitionId() ).setHandler(
                            new ActionHandler( deleteSchemaElement ) );
                    }
                }
            }


            public void partBroughtToTop( IWorkbenchPartReference partRef )
            {
            }


            public void partClosed( IWorkbenchPartReference partRef )
            {
            }


            public void partHidden( IWorkbenchPartReference partRef )
            {
            }


            public void partInputChanged( IWorkbenchPartReference partRef )
            {
            }


            public void partOpened( IWorkbenchPartReference partRef )
            {
            }


            public void partVisible( IWorkbenchPartReference partRef )
            {
            }
        } );
    }
}
