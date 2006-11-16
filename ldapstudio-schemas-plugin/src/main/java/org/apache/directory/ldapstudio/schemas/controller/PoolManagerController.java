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


import org.apache.directory.ldapstudio.schemas.controller.actions.CreateANewAttributeTypeAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.CreateANewObjectClassAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.CreateANewSchemaAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.DeleteAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.OpenSchemaSourceCode;
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
import org.apache.directory.ldapstudio.schemas.view.viewers.PoolManager;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class PoolManagerController implements IMenuListener, IDoubleClickListener, DropTargetListener
{
    private static Logger logger = Logger.getLogger( PoolManagerController.class );
    private static final PoolManagerController instance_;
    private final static FileTransfer fileTransfer = FileTransfer.getInstance();

    //Static thread-safe singleton initializer
    static
    {
        try
        {
            instance_ = new PoolManagerController();
        }
        catch ( Throwable e )
        {
            throw new RuntimeException( e.getMessage() );
        }
    }


    /**
     * Use this method to get the singleton instance of the controller
     * @return
     */
    public static PoolManagerController getInstance()
    {
        return instance_;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    public void menuAboutToShow( IMenuManager manager )
    {
        CreateANewSchemaAction createANewSchemaAction = new CreateANewSchemaAction();
        CreateANewObjectClassAction createANewObjectClassAction = new CreateANewObjectClassAction();
        CreateANewAttributeTypeAction createANewAttributeTypeAction = new CreateANewAttributeTypeAction();
        DeleteAction deleteAction = new DeleteAction();
        OpenSchemaSourceCode openSchemaSourceCode = new OpenSchemaSourceCode( PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow(), "View source code" ); //$NON-NLS-1$
        SaveAction saveAction = new SaveAction();
        SaveAsAction saveAsAction = new SaveAsAction();
        RemoveSchemaAction removeSchemaAction = new RemoveSchemaAction();

        PoolManager view = ( PoolManager ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( Application.PLUGIN_ID + ".view.PoolManager" ); //$NON-NLS-1$

        Object selection = ( ( TreeSelection ) view.getViewer().getSelection() ).getFirstElement();

        if ( selection instanceof SchemaWrapper )
        {
            Schema schema = ( ( SchemaWrapper ) selection ).getMySchema();
            if ( schema.type == SchemaType.coreSchema )
            {
                manager.add( saveAsAction );
                manager.add( new Separator() );
                manager.add( openSchemaSourceCode );
            }
            else if ( schema.type == SchemaType.userSchema )
            {
                manager.add( createANewObjectClassAction );
                manager.add( createANewAttributeTypeAction );
                manager.add( new Separator() );
                manager.add( saveAction );
                manager.add( saveAsAction );
                manager.add( removeSchemaAction );
                manager.add( new Separator() );
                manager.add( openSchemaSourceCode );
            }
        }
        else if ( selection instanceof IntermediateNode )
        {
            if ( ( ( IntermediateNode ) selection ).getDisplayName().equals( "Attribute Types" ) ) { //$NON-NLS-1$
                manager.add( createANewAttributeTypeAction );
            }
            else if ( ( ( IntermediateNode ) selection ).getDisplayName().equals( "Object Classes" ) ) { //$NON-NLS-1$
                manager.add( createANewObjectClassAction );
            }
        }
        else if ( ( selection instanceof AttributeTypeWrapper ) )
        {
            manager.add( deleteAction );
            manager.add( new Separator() );
            manager.add( createANewAttributeTypeAction );
        }
        else if ( ( selection instanceof ObjectClassWrapper ) )
        {
            manager.add( deleteAction );
            manager.add( new Separator() );
            manager.add( createANewObjectClassAction );
        }
        else
        {
            // Nothing is selected
            if ( selection == null )
            {
                manager.add( createANewSchemaAction );
            }
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     */
    public void doubleClick( DoubleClickEvent event )
    {
        // TODO : /!\ Essayer de factoriser le code commun � la fenetre de vue hierarchique dans une classe abstraite

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        PoolManager view = ( PoolManager ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( PoolManager.ID );
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
            input = new AttributeTypeFormEditorInput( ( ( AttributeTypeWrapper ) objectSelection ).getMyAttributeType() );
            editorId = AttributeTypeFormEditor.ID;
        }
        else if ( objectSelection instanceof ObjectClassWrapper )
        {
            input = new ObjectClassFormEditorInput( ( ( ObjectClassWrapper ) objectSelection ).getMyObjectClass() );
            editorId = ObjectClassFormEditor.ID;
        }
        else if ( ( objectSelection instanceof IntermediateNode ) || ( objectSelection instanceof SchemaWrapper ) )
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


    /******************************************
     *         DropTargetListener Impl        *
     ******************************************/

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


    public void dragOver( DropTargetEvent event )
    {
    }


    public void dragOperationChanged( DropTargetEvent event )
    {
    }


    public void dragLeave( DropTargetEvent event )
    {
    }


    public void dropAccept( DropTargetEvent event )
    {
    }


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
}
