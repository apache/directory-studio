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
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder.FolderType;
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

    /** The authorized Preferences keys*/
    private List<String> authorizedPrefs;

    /** The Context Menu */
    private MenuManager contextMenu;

    /** The TreeViewer */
    private TreeViewer viewer;

    /** The SchemaHandlerListener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeAdded(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeAdded( AttributeTypeImpl at )
        {
            TreeNode schemaWrapper = findSchemaWrapperInTree( at.getSchema() );

            if ( schemaWrapper != null )
            {
                AttributeTypeWrapper atw = null;
                int group = Activator.getDefault().getPreferenceStore().getInt(
                    PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
                if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
                {
                    List<TreeNode> children = schemaWrapper.getChildren();
                    for ( TreeNode child : children )
                    {
                        Folder folder = ( Folder ) child;
                        if ( folder.getType() == FolderType.ATTRIBUTE_TYPE )
                        {
                            atw = new AttributeTypeWrapper( at, folder );
                            folder.addChild( atw );
                            break;
                        }
                    }
                }
                else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                {
                    atw = new AttributeTypeWrapper( at, schemaWrapper );
                    schemaWrapper.addChild( atw );
                }

                viewer.refresh( schemaWrapper );
                if ( atw != null )
                {
                    viewer.setSelection( new StructuredSelection( atw ) );
                }
            }
            else
            {
                // An error has occurred we need to reload the view.
                view.reloadViewer();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeModified(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeModified( AttributeTypeImpl at )
        {
            AttributeTypeWrapper atw = findAttributeTypeWrapperInTree( at );

            if ( atw != null )
            {
                updateNodeAndParents( atw );
            }
            else
            {
                // An error has occurred we need to reload the view.
                view.reloadViewer();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeRemoved(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeRemoved( AttributeTypeImpl at )
        {
            AttributeTypeWrapper atw = findAttributeTypeWrapperInTree( at );

            if ( atw != null )
            {
                atw.getParent().removeChild( atw );
                viewer.refresh( atw.getParent() );
            }
            else
            {
                // An error has occurred we need to reload the view.
                view.reloadViewer();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassAdded(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassAdded( ObjectClassImpl oc )
        {
            TreeNode schemaWrapper = findSchemaWrapperInTree( oc.getSchema() );

            if ( schemaWrapper != null )
            {
                ObjectClassWrapper ocw = null;
                int group = Activator.getDefault().getPreferenceStore().getInt(
                    PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
                if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
                {
                    List<TreeNode> children = schemaWrapper.getChildren();
                    for ( TreeNode child : children )
                    {
                        Folder folder = ( Folder ) child;
                        if ( folder.getType() == FolderType.OBJECT_CLASS )
                        {
                            ocw = new ObjectClassWrapper( oc, folder );
                            folder.addChild( ocw );
                            break;
                        }
                    }
                }
                else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                {
                    ocw = new ObjectClassWrapper( oc, schemaWrapper );
                    schemaWrapper.addChild( ocw );
                }

                viewer.refresh( schemaWrapper );
                if ( ocw != null )
                {
                    viewer.setSelection( new StructuredSelection( ocw ) );
                }
            }
            else
            {
                // An error has occurred we need to reload the view.
                view.reloadViewer();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassModified(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassModified( ObjectClassImpl oc )
        {
            ObjectClassWrapper ocw = findObjectClassWrapperInTree( oc );

            if ( ocw != null )
            {
                updateNodeAndParents( ocw );
            }
            else
            {
                // An error has occurred we need to reload the view.
                view.reloadViewer();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassRemoved(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassRemoved( ObjectClassImpl oc )
        {
            ObjectClassWrapper ocw = findObjectClassWrapperInTree( oc );

            if ( ocw != null )
            {
                ocw.getParent().removeChild( ocw );
                viewer.refresh( ocw.getParent() );
            }
            else
            {
                // An error has occurred we need to reload the view.
                view.reloadViewer();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaAdded(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaAdded( Schema schema )
        {
            final TreeNode rootNode = ( TreeNode ) viewer.getInput();
            final SchemaWrapper schemaWrapper = new SchemaWrapper( schema, rootNode );
            rootNode.addChild( schemaWrapper );

            int group = Activator.getDefault().getPreferenceStore().getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
            if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
            {
                Folder atFolder = new Folder( FolderType.ATTRIBUTE_TYPE, schemaWrapper );
                schemaWrapper.addChild( atFolder );

                for ( AttributeTypeImpl attributeType : schema.getAttributeTypes() )
                {
                    atFolder.addChild( new AttributeTypeWrapper( attributeType, atFolder ) );
                }

                Folder ocFolder = new Folder( FolderType.OBJECT_CLASS, schemaWrapper );
                schemaWrapper.addChild( ocFolder );

                for ( ObjectClassImpl objectClass : schema.getObjectClasses() )
                {
                    ocFolder.addChild( new ObjectClassWrapper( objectClass, ocFolder ) );
                }
            }
            else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
            {
                for ( AttributeTypeImpl attributeType : schema.getAttributeTypes() )
                {
                    schemaWrapper.addChild( new AttributeTypeWrapper( attributeType, schemaWrapper ) );
                }

                for ( ObjectClassImpl objectClass : schema.getObjectClasses() )
                {
                    schemaWrapper.addChild( new ObjectClassWrapper( objectClass, schemaWrapper ) );
                }
            }

            Display.getDefault().asyncExec( new Runnable()
            {
                public void run()
                {
                    viewer.refresh( rootNode );
                    viewer.setSelection( new StructuredSelection( schemaWrapper ) );
                }
            } );
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaRemoved(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaRemoved( Schema schema )
        {
            TreeNode schemaWrapper = findSchemaWrapperInTree( schema.getName() );
            if ( schemaWrapper != null )
            {
                schemaWrapper.getParent().removeChild( schemaWrapper );
                viewer.refresh( viewer.getInput() );
            }
            else
            {
                // An error has occurred we need to reload the view.
                view.reloadViewer();
            }
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
    private ImportSchemasFromOpenLdapAction importSchemasFromOpenLdap;
    private ImportSchemasFromXmlAction importSchemasFromXml;
    private ExportSchemasAsOpenLdapAction exportSchemasAsOpenLdap;
    private ExportSchemasAsXmlAction exportSchemasAsXml;
    private ExportSchemasForADSAction exportSchemasForADS;
    private CollapseAllAction collapseAll;
    private OpenSchemaViewSortingDialogAction openSchemaViewSortingDialog;
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
        importSchemasFromOpenLdap = new ImportSchemasFromOpenLdapAction();
        importSchemasFromXml = new ImportSchemasFromXmlAction();
        exportSchemasAsOpenLdap = new ExportSchemasAsOpenLdapAction( viewer );
        exportSchemasAsXml = new ExportSchemasAsXmlAction( viewer );
        exportSchemasForADS = new ExportSchemasForADSAction( viewer );
        collapseAll = new CollapseAllAction( viewer );
        openSchemaViewSortingDialog = new OpenSchemaViewSortingDialogAction();
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
                MenuManager newManager = new MenuManager( "Ne&w" );
                MenuManager importManager = new MenuManager( "&Import..." );
                MenuManager exportManager = new MenuManager( "Exp&ort..." );
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
                        PluginUtils.logError( "An error occured when opening the editor.", e );
                        ViewUtils.displayErrorMessageBox( "Error", "An error occured when opening the editor." );
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
                    if ( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING == event.getProperty() )
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
     * Finds the corresponding SchemaWrapper in the Tree.
     *
     * @param name
     *      the name of the SchemaWrapper to search
     * @return
     *      the corresponding SchemaWrapper in the Tree
     */
    private SchemaWrapper findSchemaWrapperInTree( String name )
    {
        List<TreeNode> schemaWrappers = ( ( TreeNode ) viewer.getInput() ).getChildren();
        for ( TreeNode sw : schemaWrappers )
        {
            if ( ( ( SchemaWrapper ) sw ).getSchema().getName().toLowerCase().equals( name.toLowerCase() ) )
            {
                return ( SchemaWrapper ) sw;
            }
        }

        return null;
    }


    /**
     * Finds the corresponding AttributeTypeWrapper in the Tree.
     *
     * @param at
     *      the attribute type
     * @return
     *      the corresponding AttributeTypeWrapper in the Tree
     */
    private AttributeTypeWrapper findAttributeTypeWrapperInTree( AttributeTypeImpl at )
    {
        SchemaWrapper schemaWrapper = findSchemaWrapperInTree( at.getSchema() );
        if ( schemaWrapper == null )
        {
            return null;
        }

        // Finding the correct node
        int group = Activator.getDefault().getPreferenceStore().getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
        List<TreeNode> children = schemaWrapper.getChildren();
        if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
        {
            for ( TreeNode child : children )
            {
                Folder folder = ( Folder ) child;
                if ( folder.getType() == FolderType.ATTRIBUTE_TYPE )
                {
                    for ( TreeNode folderChild : folder.getChildren() )
                    {
                        AttributeTypeWrapper atw = ( AttributeTypeWrapper ) folderChild;
                        if ( atw.getAttributeType().equals( at ) )
                        {
                            return atw;
                        }
                    }
                }
            }
        }
        else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
        {
            for ( Object child : children )
            {
                if ( child instanceof AttributeTypeImpl )
                {
                    AttributeTypeWrapper atw = ( AttributeTypeWrapper ) child;
                    if ( atw.getAttributeType().equals( at ) )
                    {
                        return atw;
                    }
                }
            }
        }

        return null;
    }


    /**
     * Finds the corresponding ObjectClassWrapper in the Tree.
     *
     * @param oc
     *      the attribute type
     * @return
     *      the corresponding ObjectClassWrapper in the Tree
     */
    private ObjectClassWrapper findObjectClassWrapperInTree( ObjectClassImpl oc )
    {
        SchemaWrapper schemaWrapper = findSchemaWrapperInTree( oc.getSchema() );
        if ( schemaWrapper == null )
        {
            return null;
        }

        // Finding the correct node
        int group = Activator.getDefault().getPreferenceStore().getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
        List<TreeNode> children = schemaWrapper.getChildren();
        if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
        {
            for ( TreeNode child : children )
            {
                Folder folder = ( Folder ) child;
                if ( folder.getType() == FolderType.OBJECT_CLASS )
                {
                    for ( TreeNode folderChild : folder.getChildren() )
                    {
                        ObjectClassWrapper ocw = ( ObjectClassWrapper ) folderChild;
                        if ( ocw.getObjectClass().equals( oc ) )
                        {
                            return ocw;
                        }
                    }
                }
            }
        }
        else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
        {
            for ( Object child : children )
            {
                if ( child instanceof ObjectClassWrapper )
                {
                    ObjectClassWrapper ocw = ( ObjectClassWrapper ) child;
                    if ( ocw.getObjectClass().equals( oc ) )
                    {
                        return ocw;
                    }
                }
            }
        }

        return null;
    }


    /**
     * Updates the given node and its parents.
     *
     * @param node
     *      the node
     */
    public void updateNodeAndParents( TreeNode node )
    {
        viewer.update( node, null );
        TreeNode parent = node.getParent();
        while ( parent != null )
        {
            viewer.update( parent, null );
            parent = parent.getParent();
        }
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
