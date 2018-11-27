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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.SchemaObject;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This action deletes one or more Schema Elements from the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteSchemaElementAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated viewer */
    private TreeViewer viewer;


    /**
     * Creates a new instance of DeleteSchemaElementAction.
     */
    public DeleteSchemaElementAction( TreeViewer viewer )
    {
        super( Messages.getString( "DeleteSchemaElementAction.DeleteAction" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "DeleteSchemaElementAction.DeleteToolTip" ) ); //$NON-NLS-1$
        setId( PluginConstants.CMD_DELETE_SCHEMA_ELEMENT );
        setActionDefinitionId( PluginConstants.CMD_DELETE_SCHEMA_ELEMENT );
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_DELETE ) );
        setEnabled( true );
        this.viewer = viewer;
        this.viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();

                if ( selection.size() > 0 )
                {
                    boolean enabled = true;

                    for ( Iterator<?> iterator = selection.iterator(); iterator.hasNext(); )
                    {
                        Object selectedItem = iterator.next();

                        if ( !( selectedItem instanceof SchemaWrapper )
                            && !( selectedItem instanceof AttributeTypeWrapper )
                            && !( selectedItem instanceof ObjectClassWrapper ) )
                        {
                            enabled = false;
                            break;
                        }
                    }

                    setEnabled( enabled );
                }
                else
                {
                    setEnabled( false );
                }
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

        if ( !selection.isEmpty() )
        {
            StringBuilder message = new StringBuilder();

            int count = selection.size();

            if ( count == 1 )
            {
                Object firstElement = selection.getFirstElement();
                if ( firstElement instanceof AttributeTypeWrapper )
                {
                    message.append( Messages.getString( "DeleteSchemaElementAction.SureDeleteAttributeType" ) ); //$NON-NLS-1$
                }
                else if ( firstElement instanceof ObjectClassWrapper )
                {
                    message.append( Messages.getString( "DeleteSchemaElementAction.SureDeleteObjectClass" ) ); //$NON-NLS-1$
                }
                else if ( firstElement instanceof SchemaWrapper )
                {
                    message.append( Messages.getString( "DeleteSchemaElementAction.SureDeleteSchema" ) ); //$NON-NLS-1$
                }
                else
                {
                    message.append( Messages.getString( "DeleteSchemaElementAction.SureDeleteItem" ) ); //$NON-NLS-1$
                }
            }
            else
            {
                message.append( NLS.bind(
                    Messages.getString( "DeleteSchemaElementAction.SureDeleteItems" ), new Object[] { count } ) ); //$NON-NLS-1$
            }

            // Showing the confirmation window
            if ( MessageDialog.openConfirm( viewer.getControl().getShell(),
                Messages.getString( "DeleteSchemaElementAction.DeleteTitle" ), message.toString() ) ) //$NON-NLS-1$
            {
                Map<String, Schema> schemasMap = new HashMap<String, Schema>();
                List<SchemaObject> schemaObjectsList = new ArrayList<SchemaObject>();

                for ( Iterator<?> iterator = selection.iterator(); iterator.hasNext(); )
                {
                    Object selectedItem = iterator.next();
                    if ( selectedItem instanceof SchemaWrapper )
                    {
                        Schema schema = ( ( SchemaWrapper ) selectedItem ).getSchema();
                        schemasMap.put( Strings.toLowerCase( schema.getSchemaName() ), schema );
                    }
                    else if ( selectedItem instanceof AttributeTypeWrapper )
                    {
                        AttributeType at = ( ( AttributeTypeWrapper ) selectedItem ).getAttributeType();
                        schemaObjectsList.add( at );
                    }
                    else if ( selectedItem instanceof ObjectClassWrapper )
                    {
                        ObjectClass oc = ( ( ObjectClassWrapper ) selectedItem ).getObjectClass();
                        schemaObjectsList.add( oc );
                    }
                }

                SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
                // Removing schema objects
                for ( SchemaObject schemaObject : schemaObjectsList )
                {
                    if ( !schemasMap.containsKey( Strings.toLowerCase( schemaObject.getSchemaName() ) ) )
                    {
                        // If the schema object is not part of deleted schema, we need to delete it.
                        // But, we don't delete schema objects that are part of a deleted schema, since
                        // deleting the schema will also delete this schema object.
                        if ( schemaObject instanceof AttributeType )
                        {
                            schemaHandler.removeAttributeType( ( AttributeType ) schemaObject );
                        }
                        else if ( schemaObject instanceof ObjectClass )
                        {
                            schemaHandler.removeObjectClass( ( ObjectClass ) schemaObject );
                        }
                    }
                }

                // Removing schemas
                for ( Schema schema : schemasMap.values() )
                {
                    schemaHandler.removeSchema( schema );
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
