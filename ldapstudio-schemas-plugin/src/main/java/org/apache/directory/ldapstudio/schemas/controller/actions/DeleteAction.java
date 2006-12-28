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

package org.apache.directory.ldapstudio.schemas.controller.actions;


import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.controller.ICommandIds;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditorInput;
import org.apache.directory.ldapstudio.schemas.view.viewers.PoolManager;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for deleting an element (object class or attribute type)
 */
public class DeleteAction extends Action implements IWorkbenchWindowActionDelegate, IViewActionDelegate
{
    private static Logger logger = Logger.getLogger( DeleteAction.class );

    private enum ItemType
    {
        attributeType, objectClass
    }


    /**
     * Default constructor
     * @param window
     * @param label
     */
    public DeleteAction()
    {
        setText( Messages.getString( "DeleteAction.Delete_the_selected_item" ) ); //$NON-NLS-1$

        // The id is used to refer to the action in a menu or toolbar
        setId( ICommandIds.CMD_DELETE );
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId( ICommandIds.CMD_DELETE );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.DELETE ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        PoolManager view = ( PoolManager ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( Application.PLUGIN_ID + ".view.PoolManager" ); //$NON-NLS-1$
        Object selection = ( ( TreeSelection ) view.getViewer().getSelection() ).getFirstElement();

        String schemaName = null;
        ItemType item = null;

        if ( selection instanceof AttributeTypeWrapper )
        {
            // We have to get the parent of the parent ( AttributeTypeWrapper => IntermediateNode => SchemaWrapper )
            schemaName = ( ( SchemaWrapper ) ( ( AttributeTypeWrapper ) selection ).getParent().getParent() ).getName();
            item = DeleteAction.ItemType.attributeType;
        }
        else if ( selection instanceof ObjectClassWrapper )
        {
            // We have to get the parent of the parent ( ObjectClassWrapper => IntermediateNode => SchemaWrapper )
            schemaName = ( ( SchemaWrapper ) ( ( ObjectClassWrapper ) selection ).getParent().getParent() ).getName();
            item = DeleteAction.ItemType.objectClass;
        }

        if ( schemaName == null )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setMessage( Messages.getString( "DeleteAction.This_item_cant_be_deleted" ) ); //$NON-NLS-1$
            messageBox.open();
        }
        else
        {
            // Getting the SchemaPool
            SchemaPool pool = SchemaPool.getInstance();
            // Getting the right schema
            Schema schema = pool.getSchema( schemaName );

            // Check if the schema isn't a core schema (core schema can't be modified
            if ( schema.type == Schema.SchemaType.coreSchema )
            {
                MessageBox messageBox = new MessageBox(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK | SWT.ICON_ERROR );
                messageBox
                    .setMessage( Messages.getString( "DeleteAction.The_schema" ) + schemaName + Messages.getString( "DeleteAction.Is_a_core_schema_It_cant_be_modified" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                messageBox.open();
            }
            else
            {
                if ( item == DeleteAction.ItemType.attributeType )
                {
                    AttributeType attributeType = ( ( AttributeTypeWrapper ) selection ).getMyAttributeType();
                    MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION );
                    messageBox
                        .setMessage( Messages
                            .getString( "DeleteAction.Are_you_sure_you_want_to_delete_the_attribute_type" ) + attributeType.getNames()[0] + Messages.getString( "DeleteAction.Interrogation" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    if ( messageBox.open() == SWT.OK )
                    {
                        schema.removeAttributeType( attributeType );
                    }
                }
                else if ( item == DeleteAction.ItemType.objectClass )
                {
                    ObjectClass objectClass = ( ( ObjectClassWrapper ) selection ).getMyObjectClass();
                    MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION );
                    messageBox
                        .setMessage( Messages
                            .getString( "DeleteAction.Are_you_sure_you_want_to_delete_the_object_class" ) + objectClass.getNames()[0] + Messages.getString( "DeleteAction.Interrogation" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    if ( messageBox.open() == SWT.OK )
                    {

                        //try to close the associated editors before deleting the objectClass
                        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        IEditorReference[] editorReferences = page.getEditorReferences();
                        for ( IEditorReference reference : editorReferences )
                        {
                            try
                            {
                                if ( reference.getEditorInput() instanceof ObjectClassFormEditorInput )
                                {
                                    ObjectClassFormEditorInput input = ( ObjectClassFormEditorInput ) reference
                                        .getEditorInput();
                                    if ( input.getObjectClass().equals( objectClass ) )
                                    {
                                        page.closeEditor( reference.getEditor( false ), false );
                                    }
                                }
                            }
                            catch ( PartInitException e )
                            {
                                logger.debug( "error when closing associated editors" ); //$NON-NLS-1$
                            }
                        }
                        //delete the objectClass
                        schema.removeObjectClass( objectClass );
                    }
                }
            }
        }
    }


    public void dispose()
    {
    }


    public void init( IWorkbenchWindow window )
    {
    }


    public void run( IAction action )
    {
        this.run();
    }


    public void selectionChanged( IAction action, ISelection selection )
    {
    }


    public void init( IViewPart view )
    {
    }
}
