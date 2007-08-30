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


import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.controller.HierarchyViewController;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.view.ViewUtils;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Hierarchy View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HierarchyView extends ViewPart
{
    /** The view's ID */
    public static final String ID = Activator.PLUGIN_ID + ".view.HierarchyView"; //$NON-NLS-1$

    /** The tree viewer */
    private TreeViewer viewer;

    /** The controller */
    private HierarchyViewController controller;

    /** The Overview label */
    private Label overviewLabel;


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

        // Overview Label
        overviewLabel = new Label( parent, SWT.WRAP );
        overviewLabel.setText( "" );
        overviewLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Separator Label
        Label separatorLabel = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        separatorLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initViewer( parent );

        controller = new HierarchyViewController( this );
    }


    /**
     * Initializes the Viewer
     *
     * @param parent
     *      the parent Composite
     */
    private void initViewer( Composite parent )
    {
        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        viewer.setContentProvider( new HierarchyViewContentProvider() );
        viewer.setLabelProvider( new DecoratingLabelProvider( new HierarchyViewLabelProvider(), Activator.getDefault()
            .getWorkbench().getDecoratorManager().getLabelDecorator() ) );
        viewer.getTree().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        viewer.getTree().setEnabled( false );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }


    /**
     * Gets the TreeViewer
     *
     * @return
     *      the TreeViewer
     */
    public TreeViewer getViewer()
    {
        return viewer;
    }


    /**
     * Refreshes the viewer.
     */
    public void refresh()
    {
        viewer.refresh();
        viewer.expandAll();
    }


    public void setInput( Object input )
    {
        viewer.setInput( input );
        viewer.expandAll();
        if ( input == null )
        {
            overviewLabel.setText( "" );
        }
        else
        {
            if ( input instanceof AttributeTypeImpl )
            {
                setOverviewLabel( ( AttributeTypeImpl ) input );
            }
            else if ( input instanceof ObjectClassImpl )
            {
                setOverviewLabel( ( ObjectClassImpl ) input );
            }
            else
            {
                overviewLabel.setText( "" );
            }
        }
    }


    /**
     * Set the overview label for the given schema object.
     *
     * @param object
     *      the schema object
     */
    private void setOverviewLabel( SchemaObject object )
    {
        StringBuffer sb = new StringBuffer();

        String[] names = object.getNames();
        if ( ( names != null ) && ( names.length > 0 ) )
        {
            sb.append( ViewUtils.concateAliases( names ) );
        }
        else
        {
            sb.append( "(None)" );
        }
        sb.append( " (" );
        sb.append( object.getOid() );
        sb.append( ")  -  Schema:" );
        sb.append( object.getSchema() );

        overviewLabel.setText( sb.toString() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    public void dispose()
    {
        controller.dispose();

        super.dispose();
    }
}
