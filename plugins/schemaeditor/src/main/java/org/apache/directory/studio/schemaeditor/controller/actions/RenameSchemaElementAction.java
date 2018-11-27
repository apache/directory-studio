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
package org.apache.directory.studio.schemaeditor.controller.actions;


import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.view.dialogs.RenameAttributeTypeDialog;
import org.apache.directory.studio.schemaeditor.view.dialogs.RenameObjectClassDialog;
import org.apache.directory.studio.schemaeditor.view.dialogs.RenameSchemaDialog;
import org.apache.directory.studio.schemaeditor.view.editors.EditorsUtils;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This action launches a rename dialog for schema elements (schema, attribute type and object class).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameSchemaElementAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated viewer */
    private TreeViewer viewer;


    /**
     * Creates a new instance of RenameProjectAction.
     *
     * @param viewer
     *      the associated viewer
     */
    public RenameSchemaElementAction( TreeViewer viewer )
    {
        super( Messages.getString( "RenameSchemaElementAction.RenameSchemaElementAction" ) ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_RENAME_SCHEMA_ELEMENT );
        setActionDefinitionId( PluginConstants.CMD_RENAME_SCHEMA_ELEMENT );
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_RENAME ) );
        setEnabled( false );
        this.viewer = viewer;
        this.viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();
                setEnabled( ( selection.size() == 1 )
                    && ( ( selection.getFirstElement() instanceof SchemaWrapper )
                        || ( selection.getFirstElement() instanceof AttributeTypeWrapper )
                        || ( selection.getFirstElement() instanceof ObjectClassWrapper ) ) );
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
        if ( ( !selection.isEmpty() ) && ( selection.size() == 1 ) )
        {
            Object selectedElement = selection.getFirstElement();

            // Saving all dirty editors before processing the renaming
            if ( EditorsUtils.saveAllDirtyEditors() )
            {
                // SCHEMA
                if ( selectedElement instanceof SchemaWrapper )
                {
                    Schema schema = ( ( SchemaWrapper ) selectedElement ).getSchema();

                    RenameSchemaDialog dialog = new RenameSchemaDialog( schema.getSchemaName() );
                    if ( dialog.open() == RenameSchemaDialog.OK )
                    {
                        Activator.getDefault().getSchemaHandler().renameSchema( schema, dialog.getNewName() );
                    }
                }
                // ATTRIBUTE TYPE
                else if ( selectedElement instanceof AttributeTypeWrapper )
                {
                    MutableAttributeType attributeType = ( MutableAttributeType ) ( ( AttributeTypeWrapper ) selectedElement )
                        .getAttributeType();

                    RenameAttributeTypeDialog dialog = new RenameAttributeTypeDialog( attributeType.getNames() );
                    if ( dialog.open() == RenameAttributeTypeDialog.OK )
                    {
                        MutableAttributeType modifiedAttributeType = PluginUtils.getClone( attributeType );
                        modifiedAttributeType.setNames( dialog.getAliases() );
                        Activator.getDefault().getSchemaHandler()
                            .modifyAttributeType( attributeType, modifiedAttributeType );
                    }
                }
                // OBJECT CLASS
                else if ( selectedElement instanceof ObjectClassWrapper )
                {
                    MutableObjectClass objectClass = ( MutableObjectClass ) ( ( ObjectClassWrapper ) selectedElement )
                        .getObjectClass();

                    RenameObjectClassDialog dialog = new RenameObjectClassDialog( objectClass.getNames() );
                    if ( dialog.open() == RenameObjectClassDialog.OK )
                    {
                        ObjectClass modifiedObjectClass = PluginUtils.getClone( objectClass );
                        modifiedObjectClass.setNames( dialog.getAliases() );
                        Activator.getDefault().getSchemaHandler()
                            .modifyObjectClass( objectClass, modifiedObjectClass );
                    }
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
