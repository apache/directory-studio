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
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.ldapstudio.schemas.view.viewers.PoolManager;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for removing a schema from the pool
 */
public class RemoveSchemaAction extends Action implements IWorkbenchWindowActionDelegate, IViewActionDelegate
{

    /**
     * Default constructor
     * @param window
     * @param label
     */
    public RemoveSchemaAction()
    {
        setText( Messages.getString( "RemoveSchemaAction.Remove_the_selected_schema" ) ); //$NON-NLS-1$
        // The id is used to refer to the action in a menu or toolbar
        setId( ICommandIds.CMD_REMOVE_SCHEMA );
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId( ICommandIds.CMD_REMOVE_SCHEMA );
        setImageDescriptor( AbstractUIPlugin
            .imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.REMOVE_SCHEMA ) );
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

        // We have to check on which node we are to get the schema name
        if ( selection instanceof SchemaWrapper )
        {
            schemaName = ( ( SchemaWrapper ) selection ).getName();
        }
        else if ( selection instanceof AttributeTypeWrapper )
        {
            // We have to get the parent of the parent ( AttributeTypeWrapper => IntermediateNode => SchemaWrapper )
            schemaName = ( ( SchemaWrapper ) ( ( AttributeTypeWrapper ) selection ).getParent().getParent() ).getName();
        }
        else if ( selection instanceof ObjectClassWrapper )
        {
            // We have to get the parent of the parent ( ObjectClassWrapper => IntermediateNode => SchemaWrapper )
            schemaName = ( ( SchemaWrapper ) ( ( ObjectClassWrapper ) selection ).getParent().getParent() ).getName();
        }

        if ( schemaName != null )
        {
            // Getting the SchemaPool
            SchemaPool pool = SchemaPool.getInstance();
            // Getting the right schema
            Schema schema = pool.getSchema( schemaName );

            if ( schema.type == Schema.SchemaType.coreSchema )
            {
                MessageBox messageBox = new MessageBox(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK | SWT.ICON_ERROR );
                messageBox
                    .setMessage( Messages.getString( "RemoveSchemaAction.The_schema" ) + schemaName + Messages.getString( "RemoveSchemaAction.Is_a_core_schema_It_cant_be_removed_from_the_pool" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                messageBox.open();
                return;
            }
            if ( schema.hasBeenModified() || schema.hasPendingModification() )
            {
                MessageBox messageBox = new MessageBox(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK | SWT.CANCEL
                        | SWT.ICON_QUESTION );
                messageBox.setMessage( Messages
                    .getString( "RemoveSchemaAction.This_schema_has_been_modified_or_has_pending_modifications" ) ); //$NON-NLS-1$
                if ( messageBox.open() == SWT.OK )
                {
                    pool.removeSchema( schema );
                }
            }
            else
                pool.removeSchema( schema );
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
