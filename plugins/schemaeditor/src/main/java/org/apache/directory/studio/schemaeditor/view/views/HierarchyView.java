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


import java.util.List;

import org.apache.directory.shared.ldap.model.schema.MutableAttributeTypeImpl;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.HierarchyViewController;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Hierarchy View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class HierarchyView extends ViewPart
{
    /** The view's ID */
    public static final String ID = PluginConstants.VIEW_HIERARCHY_VIEW_ID;

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
        overviewLabel.setText( "" ); //$NON-NLS-1$
        overviewLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Separator Label
        Label separatorLabel = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        separatorLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initViewer( parent );

        controller = new HierarchyViewController( this );

        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent, PluginConstants.PLUGIN_ID + "." + "hierarchy_view" ); //$NON-NLS-1$ //$NON-NLS-2$
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
        viewer.setLabelProvider( new DecoratingLabelProvider( new HierarchyViewLabelProvider( viewer ), Activator
            .getDefault().getWorkbench().getDecoratorManager().getLabelDecorator() ) );
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
            overviewLabel.setText( "" ); //$NON-NLS-1$
        }
        else
        {
            if ( input instanceof MutableAttributeTypeImpl )
            {
                setOverviewLabel( ( MutableAttributeTypeImpl ) input );
            }
            else if ( input instanceof ObjectClass )
            {
                setOverviewLabel( ( ObjectClass ) input );
            }
            else
            {
                overviewLabel.setText( "" ); //$NON-NLS-1$
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

        List<String> names = object.getNames();
        if ( ( names != null ) && ( names.size() > 0 ) )
        {
            sb.append( ViewUtils.concateAliases( names ) );
        }
        else
        {
            sb.append( Messages.getString( "HierarchyView.None" ) ); //$NON-NLS-1$
        }
        sb.append( NLS.bind(
            Messages.getString( "HierarchyView.Schema" ), new String[] { object.getOid(), object.getSchemaName() } ) ); //$NON-NLS-1$

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
