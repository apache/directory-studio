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
package org.apache.directory.studio.apacheds.schemaeditor.controller.actions;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This action deletes one or more Schema Elements from the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
        super( "Delete" );
        setToolTipText( getText() );
        setId( PluginConstants.CMD_DELETE_SCHEMA_ELEMENT );
        setImageDescriptor( AbstractUIPlugin
            .imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_DELETE ) );
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
                    AttributeTypeImpl at = ( ( AttributeTypeWrapper ) selectedItem ).getAttributeType();
                    schemaObjectsList.add( at );
                }
                else if ( selectedItem instanceof ObjectClassWrapper )
                {
                    ObjectClassImpl oc = ( ( ObjectClassWrapper ) selectedItem ).getObjectClass();
                    schemaObjectsList.add( oc );
                }
            }

            SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
            // Removing schema objects
            for ( SchemaObject schemaObject : schemaObjectsList )
            {
                if ( !schemasMap.containsKey( schemaObject.getSchema().toLowerCase() ) )
                {
                    // If the schema object is not part of deleted schema, we need to delete it.
                    // But, we don't delete schema objects that are part of a deleted schema, since
                    // deleting the schema will also delete this schema object.
                    if ( schemaObject instanceof AttributeTypeImpl )
                    {
                        schemaHandler.removeAttributeType( ( AttributeTypeImpl ) schemaObject );
                    }
                    else if ( schemaObject instanceof ObjectClassImpl )
                    {
                        schemaHandler.removeObjectClass( ( ObjectClassImpl ) schemaObject );
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
