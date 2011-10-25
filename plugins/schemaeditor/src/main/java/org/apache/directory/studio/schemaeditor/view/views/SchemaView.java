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
package org.apache.directory.studio.schemaeditor.view.views;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaViewController;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaViewRoot;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This class represents the SchemaView. 
 * It is used to display the Schema and its elements (Schemas, AttributeTypes 
 * and ObjectClasses).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaView extends ViewPart
{
    /** The ID of the View */
    public static final String ID = PluginConstants.VIEW_SCHEMA_VIEW_ID;

    /** The viewer */
    private TreeViewer treeViewer;


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {
        initViewer( parent );

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( treeViewer );

        // Adding the controller
        new SchemaViewController( this );

        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent, PluginConstants.PLUGIN_ID + "." + "schema_view" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Initializes the Viewer
     */
    private void initViewer( Composite parent )
    {
        treeViewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        treeViewer.setContentProvider( new SchemaViewContentProvider() );
        treeViewer.setLabelProvider( new DecoratingLabelProvider( new SchemaViewLabelProvider(), Activator.getDefault()
            .getWorkbench().getDecoratorManager().getLabelDecorator() ) );
        treeViewer.getTree().setEnabled( false );
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        treeViewer.getTree().setFocus();
    }


    /**
     * Gets the TreeViewer.
     *
     * @return
     *      the TreeViewer
     */
    public TreeViewer getViewer()
    {
        return treeViewer;
    }


    /**
     * Reloads the Viewer
     */
    public void reloadViewer()
    {
        treeViewer.setInput( new SchemaViewRoot() );
    }


    /**
     * Refreshes the viewer
     */
    public void refresh()
    {
        treeViewer.refresh();
    }
}
