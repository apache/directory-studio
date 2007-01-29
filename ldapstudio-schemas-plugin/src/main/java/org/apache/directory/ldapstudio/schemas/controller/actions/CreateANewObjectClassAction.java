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
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.apache.directory.ldapstudio.schemas.view.wizards.CreateANewObjectClassWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for creating a new object class
 */
public class CreateANewObjectClassAction extends Action
{

    /**
     * Default constructor
     * @param window
     * @param label
     */
    public CreateANewObjectClassAction()
    {
        setText( Messages.getString( "CreateANewObjectClassAction.Create_a_new_object_class" ) ); //$NON-NLS-1$
        // The id is used to refer to the action in a menu or toolbar
        setId( ICommandIds.CMD_CREATE_A_NEW_OBJECTCLASS );
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId( ICommandIds.CMD_CREATE_A_NEW_OBJECTCLASS );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            IImageKeys.CREATE_A_NEW_OBJECTCLASS ) );
    }


    /**
     * {@inheritDoc}
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
        else if ( selection instanceof IntermediateNode )
        {
            schemaName = ( ( SchemaWrapper ) ( ( IntermediateNode ) selection ).getParent() ).getName();
        }

        if ( schemaName == null )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setMessage( Messages.getString( "CreateANewObjectClassAction.A_schema_must_be_selected" ) ); //$NON-NLS-1$
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
                    .setMessage( Messages.getString( "CreateANewObjectClassAction.The_schema" ) + schemaName + Messages.getString( "CreateANewObjectClassAction.Is_a_core_schema_It_cant_be_modified" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                messageBox.open();
            }
            else
            {
                // Instantiates and initializes the wizard
                CreateANewObjectClassWizard wizard = new CreateANewObjectClassWizard( schemaName );
                wizard.init( PlatformUI.getWorkbench(), StructuredSelection.EMPTY );
                // Instantiates the wizard container with the wizard and opens it
                WizardDialog dialog = new WizardDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard );
                dialog.create();
                dialog.open();
            }
        }
    }
}
