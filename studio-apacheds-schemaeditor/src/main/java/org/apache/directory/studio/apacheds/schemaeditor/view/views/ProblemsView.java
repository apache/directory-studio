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
package org.apache.directory.studio.apacheds.schemaeditor.view.views;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.controller.ProblemsViewController;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ProblemsViewRoot;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;


/**
 * This class represents the SchemaView. 
 * It is used to display the Schema and its elements (Schemas, AttributeTypes 
 * and ObjectClasses).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProblemsView extends ViewPart
{
    /** The ID of the View */
    public static final String ID = Activator.PLUGIN_ID + ".view.ProblemsView"; //$NON-NLS-1$

    /** The viewer */
    private TreeViewer treeViewer;

    /** The content provider of the viewer */
    private ProblemsViewContentProvider contentProvider;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        parent.setLayout( gridLayout );

        Label overviewLabel = new Label( parent, SWT.NULL );
        overviewLabel.setText( "X error(s), X warning(s)" );
        overviewLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initViewer( parent );

        // Adding the controller
        new ProblemsViewController( this );
    }


    /**
     * Initializes the Viewer
     */
    private void initViewer( Composite parent )
    {
        treeViewer = new TreeViewer( parent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL );
        Tree tree = treeViewer.getTree();
        tree.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        tree.setHeaderVisible( true );
        tree.setLinesVisible( true );
        TreeColumn descriptionColumn = new TreeColumn( tree, SWT.LEFT );
        descriptionColumn.setText( "Description" );
        descriptionColumn.setWidth( 500 );
        TreeColumn resourceColumn = new TreeColumn( tree, SWT.LEFT );
        resourceColumn.setText( "Resource" );
        resourceColumn.setWidth( 100 );
        contentProvider = new ProblemsViewContentProvider( treeViewer );
        treeViewer.setContentProvider( contentProvider );
        treeViewer.setLabelProvider( new ProblemsViewLabelProvider() );
        treeViewer.setInput( new ProblemsViewRoot() );
        treeViewer.expandAll();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
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
        treeViewer.setInput( new ProblemsViewRoot() );
        treeViewer.expandAll();
    }
}
