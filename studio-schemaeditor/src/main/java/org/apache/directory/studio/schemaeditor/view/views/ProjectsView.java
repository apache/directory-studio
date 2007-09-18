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
import org.apache.directory.studio.schemaeditor.controller.ProjectsViewController;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This class represents the ProjectsView. 
 * It is used to display the projects the user has created.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProjectsView extends ViewPart
{
    /** The ID of the View */
    public static final String ID = Activator.PLUGIN_ID + ".view.ProjectsView"; //$NON-NLS-1$

    /** The viewer */
    private TableViewer tableViewer;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent,
            Activator.PLUGIN_ID + "." + "projects_view" );
        
        initViewer( parent );

        // Adding the controller
        new ProjectsViewController( this );
    }


    /**
     * Initializes the Viewer
     */
    private void initViewer( Composite parent )
    {
        tableViewer = new TableViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        tableViewer.setContentProvider( new ProjectsViewContentProvider( tableViewer ) );
        tableViewer.setLabelProvider( new DecoratingLabelProvider( new ProjectsViewLabelProvider(), Activator
            .getDefault().getWorkbench().getDecoratorManager().getLabelDecorator() ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        tableViewer.getTable().setFocus();
    }


    /**
     * Gets the TableViewer.
     *
     * @return
     *      the TableViewer
     */
    public TableViewer getViewer()
    {
        return tableViewer;
    }
}
