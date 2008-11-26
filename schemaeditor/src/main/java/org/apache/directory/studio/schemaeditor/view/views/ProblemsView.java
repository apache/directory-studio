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
import org.apache.directory.studio.schemaeditor.controller.ProblemsViewController;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaChecker;
import org.apache.directory.studio.schemaeditor.view.wrappers.ProblemsViewRoot;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
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
    public static final String ID = PluginConstants.VIEW_PROBLEMS_VIEW_ID;

    /** The viewer */
    private TreeViewer treeViewer;

    /** The content provider of the viewer */
    private ProblemsViewContentProvider contentProvider;

    /** The overview label */
    private Label overviewLabel;

    /** The SchemaChecker */
    private SchemaChecker schemaChecker;

    /** The Controller */
    private ProblemsViewController controller;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent, PluginConstants.PLUGIN_ID + "." + "problems_view" ); //$NON-NLS-1$ //$NON-NLS-2$

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

        // Overview Label
        overviewLabel = new Label( parent, SWT.NULL );
        setErrorsAndWarningsCount( 0, 0 );
        overviewLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Separator Label
        Label separatorLabel = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        separatorLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Viewer
        initViewer( parent );

        // Adding the controller
        controller = new ProblemsViewController( this );
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
        descriptionColumn.setText( Messages.getString( "ProblemsView.Description" ) ); //$NON-NLS-1$
        descriptionColumn.setWidth( 500 );
        TreeColumn resourceColumn = new TreeColumn( tree, SWT.LEFT );
        resourceColumn.setText( Messages.getString( "ProblemsView.Resource" ) ); //$NON-NLS-1$
        resourceColumn.setWidth( 100 );
        contentProvider = new ProblemsViewContentProvider();
        treeViewer.setContentProvider( contentProvider );
        treeViewer.setLabelProvider( new ProblemsViewLabelProvider() );
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

        schemaChecker = Activator.getDefault().getSchemaChecker();
        if ( schemaChecker != null )
        {
            setErrorsAndWarningsCount( schemaChecker.getErrors().size(), schemaChecker.getWarnings().size() );
        }
        else
        {
            setErrorsAndWarningsCount( 0, 0 );
        }
    }


    /**
     * Refresh the overview label with the number of errors and warnings.
     *
     * @param errors
     *      the number of errors
     * @param warnings
     *      the number of warnings
     */
    public void setErrorsAndWarningsCount( int errors, int warnings )
    {
        StringBuffer sb = new StringBuffer();

        sb.append( errors );
        sb.append( " " ); //$NON-NLS-1$
        if ( errors > 1 )
        {
            sb.append( Messages.getString( "ProblemsView.Errors" ) ); //$NON-NLS-1$
        }
        else
        {
            sb.append( Messages.getString( "ProblemsView.Error" ) ); //$NON-NLS-1$
        }

        sb.append( ", " ); //$NON-NLS-1$

        sb.append( warnings );
        sb.append( " " ); //$NON-NLS-1$
        if ( warnings > 1 )
        {
            sb.append( Messages.getString( "ProblemsView.Warnings" ) ); //$NON-NLS-1$
        }
        else
        {
            sb.append( Messages.getString( "ProblemsView.Warning" ) ); //$NON-NLS-1$
        }

        overviewLabel.setText( sb.toString() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    public void dispose()
    {
        controller.dispose();
    }
}
