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

package org.apache.directory.ldapstudio.schemas.controller;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.controller.actions.CollapseAllAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.CreateANewAttributeTypeAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.CreateANewObjectClassAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.CreateANewSchemaAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.DeleteAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.LinkWithEditorSchemasView;
import org.apache.directory.ldapstudio.schemas.controller.actions.OpenLocalFileAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.OpenSchemaSourceCode;
import org.apache.directory.ldapstudio.schemas.controller.actions.OpenSchemasViewPreferencesAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.OpenSchemasViewSortDialogAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.RemoveSchemaAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.SaveAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.SaveAsAction;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaCreationException;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.model.Schema.SchemaType;
import org.apache.directory.ldapstudio.schemas.view.editors.AttributeTypeFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.AttributeTypeFormEditorInput;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditorInput;
import org.apache.directory.ldapstudio.schemas.view.viewers.SchemasView;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode.IntermediateNodeType;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Controller for the Schemas View
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemasViewController
{
    /** The logger */
    private static Logger logger = Logger.getLogger( SchemasViewController.class );

    /** The associated view */
    private SchemasView view;

    /** The Drag'n'Drop FileTransfer Object */
    private final static FileTransfer fileTransfer = FileTransfer.getInstance();

    /** The Context Menu */
    private MenuManager contextMenu;

    // The Actions
    private Action openLocalFile;
    private Action createANewSchema;
    private Action removeSchema;
    private Action createANewObjectClass;
    private Action createANewAttributeType;
    private Action deleteAction;
    private Action collapseAll;
    private Action linkWithEditor;
    private Action openSchemaSourceCode;
    private Action save;
    private Action saveAs;
    private Action openSortDialog;
    private Action openPreferencePage;


    /**
     * Creates a new instance of SchemasViewController.
     *
     * @param view
     *      the associated view
     */
    public SchemasViewController( SchemasView view )
    {
        this.view = view;

        initActions();
        initToolbar();
        initMenu();
        initContextMenu();
        initDragAndDrop();
        initDoubleClickListener();
        registerUpdateActions();
        initPreferencesListener();
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
        openLocalFile = new OpenLocalFileAction();
        createANewSchema = new CreateANewSchemaAction();
        removeSchema = new RemoveSchemaAction();
        createANewObjectClass = new CreateANewObjectClassAction();
        createANewAttributeType = new CreateANewAttributeTypeAction();
        deleteAction = new DeleteAction();
        collapseAll = new CollapseAllAction( view.getViewer() );
        linkWithEditor = new LinkWithEditorSchemasView( view );
        openSchemaSourceCode = new OpenSchemaSourceCode( PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
            "View source code" );
        save = new SaveAction();
        saveAs = new SaveAsAction();
        openSortDialog = new OpenSchemasViewSortDialogAction();
        openPreferencePage = new OpenSchemasViewPreferencesAction();
    }


    /**
     * Initializes the Toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
        toolbar.add( openLocalFile );
        toolbar.add( createANewSchema );
        toolbar.add( removeSchema );
        toolbar.add( new Separator() );
        toolbar.add( createANewObjectClass );
        toolbar.add( createANewAttributeType );
        toolbar.add( ( IAction ) deleteAction );
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
        menu.add( openSortDialog );
        menu.add( new Separator() );
        menu.add( linkWithEditor );
        menu.add( new Separator() );
        menu.add( openPreferencePage );
    }


    /**
     * Initializes the ContextMenu.
     */
    private void initContextMenu()
    {
        TreeViewer viewer = view.getViewer();

        contextMenu = new MenuManager( "" ); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown( true );

        contextMenu.addMenuListener( new IMenuListener()
        {
            /**
             * {@inheritDoc}
             */
            public void menuAboutToShow( IMenuManager manager )
            {
                Object selection = ( ( TreeSelection ) view.getViewer().getSelection() ).getFirstElement();

                if ( selection instanceof SchemaWrapper )
                {
                    Schema schema = ( ( SchemaWrapper ) selection ).getMySchema();
                    if ( schema.type == SchemaType.coreSchema )
                    {
                        manager.add( saveAs );
                        manager.add( new Separator() );
                        manager.add( openSchemaSourceCode );
                    }
                    else if ( schema.type == SchemaType.userSchema )
                    {
                        manager.add( createANewObjectClass );
                        manager.add( createANewAttributeType );
                        manager.add( new Separator() );
                        manager.add( save );
                        manager.add( saveAs );
                        manager.add( removeSchema );
                        manager.add( new Separator() );
                        manager.add( openSchemaSourceCode );
                    }
                }
                else if ( selection instanceof IntermediateNode )
                {
                    if ( ( ( IntermediateNode ) selection ).getType() == IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER )
                    {
                        manager.add( createANewAttributeType );
                    }
                    else if ( ( ( IntermediateNode ) selection ).getType() == IntermediateNodeType.OBJECT_CLASS_FOLDER )
                    {
                        manager.add( createANewObjectClass );
                    }
                }
                else if ( ( selection instanceof AttributeTypeWrapper ) )
                {
                    manager.add( deleteAction );
                    manager.add( new Separator() );
                    manager.add( createANewAttributeType );
                }
                else if ( ( selection instanceof ObjectClassWrapper ) )
                {
                    manager.add( deleteAction );
                    manager.add( new Separator() );
                    manager.add( createANewObjectClass );
                }
                else
                {
                    // Nothing is selected
                    if ( selection == null )
                    {
                        manager.add( createANewSchema );
                    }
                }
            }
        } );

        // set the context menu to the table viewer
        viewer.getControl().setMenu( contextMenu.createContextMenu( viewer.getControl() ) );
        // register the context menu to enable extension actions
        view.getSite().registerContextMenu( contextMenu, viewer );
    }


    /**
     * Initializes the DragNDrop support
     */
    private void initDragAndDrop()
    {
        DropTarget target = new DropTarget( view.getViewer().getControl(), DND.DROP_COPY );
        //we only support file dropping on the viewer
        Transfer[] types = new Transfer[]
            { FileTransfer.getInstance() };
        target.setTransfer( types );
        target.addDropListener( new DropTargetListener()
        {
            /**
             * {@inheritDoc}
             */
            public void dragEnter( DropTargetEvent event )
            {
                if ( ( event.operations & DND.DROP_COPY ) != 0 )
                {
                    event.detail = DND.DROP_COPY;
                }
                else
                {
                    event.detail = DND.DROP_NONE;
                }

                //we only want files
                for ( int i = 0; i < event.dataTypes.length; i++ )
                {
                    if ( fileTransfer.isSupportedType( event.dataTypes[i] ) )
                    {
                        event.currentDataType = event.dataTypes[i];
                        break;
                    }
                }
            }


            /**
             * {@inheritDoc}
             */
            public void dragOver( DropTargetEvent event )
            {
            }


            /**
             * {@inheritDoc}
             */
            public void dragOperationChanged( DropTargetEvent event )
            {
            }


            /**
             * {@inheritDoc}
             */
            public void dragLeave( DropTargetEvent event )
            {
            }


            /**
             * {@inheritDoc}
             */
            public void dropAccept( DropTargetEvent event )
            {
            }


            /**
             * {@inheritDoc}
             */
            public void drop( DropTargetEvent event )
            {
                if ( fileTransfer.isSupportedType( event.currentDataType ) )
                {
                    SchemaPool pool = SchemaPool.getInstance();
                    String[] files = ( String[] ) event.data;
                    for ( int i = 0; i < files.length; i++ )
                    {
                        try
                        {
                            pool.addAlreadyExistingSchema( files[i], SchemaType.userSchema );
                        }
                        catch ( SchemaCreationException e )
                        {
                            logger.debug( "error when initializing new schema after drag&drop: " + files[i] ); //$NON-NLS-1$
                        }
                    }
                }
            }
        } );

    }


    /**
     * Initializes the DoubleClickListener
     */
    private void initDoubleClickListener()
    {
        view.getViewer().addDoubleClickListener( new IDoubleClickListener()
        {
            /**
             * {@inheritDoc}
             */
            public void doubleClick( DoubleClickEvent event )
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                SchemasView view = ( SchemasView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .findView( SchemasView.ID );
                TreeViewer viewer = view.getViewer();

                // What we get from the treeViewer is a StructuredSelection
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();

                // Here's the real object (an AttributeTypeWrapper, ObjectClassWrapper or IntermediateNode)
                Object objectSelection = selection.getFirstElement();
                IEditorInput input = null;
                String editorId = null;

                // Selecting the right editor and input
                if ( objectSelection instanceof AttributeTypeWrapper )
                {
                    input = new AttributeTypeFormEditorInput( ( ( AttributeTypeWrapper ) objectSelection )
                        .getMyAttributeType() );
                    editorId = AttributeTypeFormEditor.ID;
                }
                else if ( objectSelection instanceof ObjectClassWrapper )
                {
                    input = new ObjectClassFormEditorInput( ( ( ObjectClassWrapper ) objectSelection )
                        .getMyObjectClass() );
                    editorId = ObjectClassFormEditor.ID;
                }
                else if ( ( objectSelection instanceof IntermediateNode )
                    || ( objectSelection instanceof SchemaWrapper ) )
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
                        logger.debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        } );

    }


    /**
     * Registers a Listener on the Schemas View and enable/disable the
     * Actions according to the selection
     */
    private void registerUpdateActions()
    {
        // Handling selection of the Browser View to enable/disable the Actions
        view.getSite().getPage().addSelectionListener( SchemasView.ID, new ISelectionListener()
        {
            /**
             * {@inheritDoc}
             */
            public void selectionChanged( IWorkbenchPart part, ISelection selection )
            {
                TreeSelection treeSelection = ( TreeSelection ) selection;

                Object selectedObject = ( ( TreeSelection ) selection ).getFirstElement();

                if ( treeSelection.size() != 1 || selectedObject == null )
                {
                    removeSchema.setEnabled( false );
                    createANewObjectClass.setEnabled( false );
                    createANewAttributeType.setEnabled( false );
                    deleteAction.setEnabled( false );
                }
                else if ( selectedObject instanceof SchemaWrapper )
                {
                    SchemaWrapper schemaWrapper = ( SchemaWrapper ) selectedObject;

                    if ( schemaWrapper.getMySchema().type == SchemaType.coreSchema )
                    {
                        removeSchema.setEnabled( false );
                        createANewObjectClass.setEnabled( false );
                        createANewAttributeType.setEnabled( false );
                        deleteAction.setEnabled( false );
                    }
                    else
                    {
                        removeSchema.setEnabled( true );
                        createANewObjectClass.setEnabled( true );
                        createANewAttributeType.setEnabled( true );
                        deleteAction.setEnabled( false );
                    }
                }
                else if ( selectedObject instanceof AttributeTypeWrapper )
                {
                    AttributeTypeWrapper attributeTypeWrapper = ( AttributeTypeWrapper ) selectedObject;
                    deleteAction.setText( "Delete Attribute Type" + " '"
                        + attributeTypeWrapper.getMyAttributeType().getNames()[0] + "'" );

                    if ( attributeTypeWrapper.getMyAttributeType().getOriginatingSchema().type == SchemaType.coreSchema )
                    {
                        createANewObjectClass.setEnabled( false );
                        createANewAttributeType.setEnabled( false );
                        deleteAction.setEnabled( false );
                    }
                    else
                    {
                        createANewObjectClass.setEnabled( false );
                        createANewAttributeType.setEnabled( true );
                        deleteAction.setEnabled( true );
                    }
                }
                else if ( selectedObject instanceof ObjectClassWrapper )
                {
                    ObjectClassWrapper objectClassWrapper = ( ObjectClassWrapper ) selectedObject;
                    deleteAction.setText( "Delete Object Class" + " '"
                        + objectClassWrapper.getMyObjectClass().getNames()[0] + "'" );

                    if ( objectClassWrapper.getMyObjectClass().getOriginatingSchema().type == SchemaType.coreSchema )
                    {
                        createANewObjectClass.setEnabled( false );
                        createANewAttributeType.setEnabled( false );
                        deleteAction.setEnabled( false );
                    }
                    else
                    {
                        createANewObjectClass.setEnabled( true );
                        createANewAttributeType.setEnabled( false );
                        deleteAction.setEnabled( true );
                    }
                }
                else if ( selectedObject instanceof IntermediateNode )
                {
                    IntermediateNode intermediateNode = ( IntermediateNode ) selectedObject;
                    SchemaWrapper schemaWrapper = ( SchemaWrapper ) intermediateNode.getParent();

                    if ( schemaWrapper.getMySchema().type == SchemaType.coreSchema )
                    {
                        removeSchema.setEnabled( false );
                        createANewObjectClass.setEnabled( false );
                        createANewAttributeType.setEnabled( false );
                        deleteAction.setEnabled( false );
                    }
                    else
                    {
                        if ( intermediateNode.getType() == IntermediateNodeType.OBJECT_CLASS_FOLDER )
                        {
                            removeSchema.setEnabled( true );
                            createANewObjectClass.setEnabled( true );
                            createANewAttributeType.setEnabled( false );
                            deleteAction.setEnabled( false );
                        }
                        else if ( intermediateNode.getType() == IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER )
                        {
                            removeSchema.setEnabled( true );
                            createANewObjectClass.setEnabled( false );
                            createANewAttributeType.setEnabled( true );
                            deleteAction.setEnabled( false );
                        }
                        else
                        {
                            removeSchema.setEnabled( true );
                            createANewObjectClass.setEnabled( false );
                            createANewAttributeType.setEnabled( false );
                            deleteAction.setEnabled( false );
                        }
                    }
                }
            }
        } );
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
                view.getViewer().refresh();
            }
        } );
    }
}
