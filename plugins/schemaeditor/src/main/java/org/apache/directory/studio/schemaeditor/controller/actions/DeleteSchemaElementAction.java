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

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;
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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


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
                    boolean enabled = false;

                    for ( Iterator<?> iterator = selection.iterator(); iterator.hasNext(); )
                    {
                        Object selectedItem = iterator.next();
                        if ( selectedItem instanceof SchemaWrapper )
                        {
                            enabled = true;
                        }
                        else if ( selectedItem instanceof AttributeTypeWrapper )
                        {
                            enabled = true;
                        }
                        else if ( selectedItem instanceof ObjectClassWrapper )
                        {
                            enabled = true;
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


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

        if ( !selection.isEmpty() )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.YES | SWT.NO | SWT.ICON_QUESTION );
            int count = selection.size();
            if ( count == 1 )
            {
                Object firstElement = selection.getFirstElement();
                if ( firstElement instanceof AttributeTypeWrapper )
                {
                    messageBox.setMessage( Messages.getString( "DeleteSchemaElementAction.SureToDeleteAttributeType" ) ); //$NON-NLS-1$
                }
                else if ( firstElement instanceof ObjectClassWrapper )
                {
                    messageBox.setMessage( Messages.getString( "DeleteSchemaElementAction.SureToDeleteObjectClass" ) ); //$NON-NLS-1$
                }
                else if ( firstElement instanceof SchemaWrapper )
                {
                    messageBox.setMessage( Messages.getString( "DeleteSchemaElementAction.SureToDeleteSchema" ) ); //$NON-NLS-1$
                }
                else
                {
                    messageBox.setMessage( Messages.getString( "DeleteSchemaElementAction.SureToDeleteItem" ) ); //$NON-NLS-1$
                }

            }
            else
            {
                messageBox.setMessage( NLS.bind(
                    Messages.getString( "DeleteSchemaElementAction.SureToDeleteItems" ), new Object[] { count } ) ); //$NON-NLS-1$
            }
            if ( messageBox.open() == SWT.YES )
            {

                Map<String, Schema> schemasMap = new HashMap<String, Schema>();
                List<SchemaObject> schemaObjectsList = new ArrayList<SchemaObject>();

                for ( Iterator<?> iterator = selection.iterator(); iterator.hasNext(); )
                {
                    Object selectedItem = iterator.next();
                    if ( selectedItem instanceof SchemaWrapper )
                    {
                        Schema schema = ( ( SchemaWrapper ) selectedItem ).getSchema();
                        schemasMap.put( schema.getName().toLowerCase(), schema );
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
                    if ( !schemasMap.containsKey( schemaObject.getSchemaName().toLowerCase() ) )
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        run();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
