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


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.view.views.SchemasView;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.SchemaWrapper;
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
        super( Messages.getString( "CreateANewObjectClassAction.Create_a_new_object_class" ) ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_CREATE_A_NEW_OBJECTCLASS );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_CREATE_A_NEW_OBJECTCLASS ) );
        setEnabled( true );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        SchemasView view = ( SchemasView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( SchemasView.ID ); //$NON-NLS-1$
        Object selection = ( ( TreeSelection ) view.getViewer().getSelection() ).getFirstElement();

        Schema schema = null;

        // We have to check on which node we are to get the schema name
        if ( selection instanceof SchemaWrapper )
        {
            schema = ( ( SchemaWrapper ) selection ).getMySchema();
        }
        else if ( selection instanceof AttributeTypeWrapper )
        {
            schema = ( ( AttributeTypeWrapper ) selection ).getMyAttributeType().getOriginatingSchema();
        }
        else if ( selection instanceof ObjectClassWrapper )
        {
            schema = ( ( ObjectClassWrapper ) selection ).getMyObjectClass().getOriginatingSchema();
        }
        else if ( selection instanceof IntermediateNode )
        {
            schema = ( ( SchemaWrapper ) ( ( IntermediateNode ) selection ).getParent() ).getMySchema();
        }
        else
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setMessage( Messages.getString( "CreateANewObjectClassAction.A_schema_must_be_selected" ) ); //$NON-NLS-1$
            messageBox.open();
            return;
        }

        // Check if the schema isn't a core schema (core schema can't be modified
        if ( schema.type == Schema.SchemaType.coreSchema )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox
                .setMessage( Messages.getString( "CreateANewObjectClassAction.The_schema" ) + schema.getName() + Messages.getString( "CreateANewObjectClassAction.Is_a_core_schema_It_cant_be_modified" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            messageBox.open();
        }
        else
        {
            // Instantiates and initializes the wizard
            CreateANewObjectClassWizard wizard = new CreateANewObjectClassWizard( schema.getName() );
            wizard.init( PlatformUI.getWorkbench(), StructuredSelection.EMPTY );
            // Instantiates the wizard container with the wizard and opens it
            WizardDialog dialog = new WizardDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                wizard );
            dialog.create();
            dialog.open();
        }
    }
}
