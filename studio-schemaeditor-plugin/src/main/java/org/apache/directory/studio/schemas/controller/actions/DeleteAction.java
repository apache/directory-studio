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

package org.apache.directory.studio.schemas.controller.actions;


import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.Messages;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.model.AttributeType;
import org.apache.directory.studio.schemas.model.ObjectClass;
import org.apache.directory.studio.schemas.model.Schema;
import org.apache.directory.studio.schemas.view.views.SchemasView;
import org.apache.directory.studio.schemas.view.views.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemas.view.views.wrappers.ObjectClassWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for deleting an element (object class or attribute type)
 */
public class DeleteAction extends Action
{
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
        super( Messages.getString( "DeleteAction.Delete_the_selected_item" ) ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_DELETE );
        setImageDescriptor( AbstractUIPlugin
            .imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_DELETE ) );
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
        ItemType item = null;

        if ( selection instanceof AttributeTypeWrapper )
        {
            // We have to get the parent of the parent ( AttributeTypeWrapper => IntermediateNode => SchemaWrapper )
            schema = ( ( AttributeTypeWrapper ) selection ).getMyAttributeType().getOriginatingSchema();
            item = DeleteAction.ItemType.attributeType;
        }
        else if ( selection instanceof ObjectClassWrapper )
        {
            // We have to get the parent of the parent ( ObjectClassWrapper => IntermediateNode => SchemaWrapper )
            schema = ( ( ObjectClassWrapper ) selection ).getMyObjectClass().getOriginatingSchema();
            item = DeleteAction.ItemType.objectClass;
        }
        else
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setMessage( Messages.getString( "DeleteAction.This_item_cant_be_deleted" ) ); //$NON-NLS-1$
            messageBox.open();
            return;
        }

        // Check if the schema isn't a core schema (core schema can't be modified
        if ( schema.type == Schema.SchemaType.coreSchema )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox
                .setMessage( Messages.getString( "DeleteAction.The_schema" ) + schema.getName() + Messages.getString( "DeleteAction.Is_a_core_schema_It_cant_be_modified" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            messageBox.open();
        }
        else
        {
            if ( item == DeleteAction.ItemType.attributeType )
            {
                AttributeType attributeType = ( ( AttributeTypeWrapper ) selection ).getMyAttributeType();
                MessageBox messageBox = new MessageBox(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK | SWT.CANCEL
                        | SWT.ICON_QUESTION );
                messageBox
                    .setMessage( Messages.getString( "DeleteAction.Are_you_sure_you_want_to_delete_the_attribute_type" ) + attributeType.getNames()[0] + Messages.getString( "DeleteAction.Interrogation" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                if ( messageBox.open() == SWT.OK )
                {
                    schema.removeAttributeType( attributeType );
                }
            }
            else if ( item == DeleteAction.ItemType.objectClass )
            {
                ObjectClass objectClass = ( ( ObjectClassWrapper ) selection ).getMyObjectClass();
                MessageBox messageBox = new MessageBox(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK | SWT.CANCEL
                        | SWT.ICON_QUESTION );
                messageBox
                    .setMessage( Messages.getString( "DeleteAction.Are_you_sure_you_want_to_delete_the_object_class" ) + objectClass.getNames()[0] + Messages.getString( "DeleteAction.Interrogation" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                if ( messageBox.open() == SWT.OK )
                {
                    schema.removeObjectClass( objectClass );
                }
            }
        }
    }
}
