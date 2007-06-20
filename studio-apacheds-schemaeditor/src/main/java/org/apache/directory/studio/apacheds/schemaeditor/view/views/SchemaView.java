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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaViewController;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddAliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.Difference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyCollectiveDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveAliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.view.widget.DifferencesWidget;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaViewRoot;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


/**
 * This class represents the SchemaView. 
 * It is used to display the Schema and its elements (Schemas, AttributeTypes 
 * and ObjectClasses).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaView extends ViewPart
{
    /** The ID of the View */
    public static final String ID = Activator.PLUGIN_ID + ".view.SchemasView"; //$NON-NLS-1$

    /** The viewer */
    private TreeViewer viewer;

    /** The content provider of the viewer */
    private SchemaViewContentProvider contentProvider;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        initViewer( parent );

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( viewer );

        // Adding the controller
        new SchemaViewController( this );
        
        DifferencesWidget differencesWidget = new DifferencesWidget();
        differencesWidget.createWidget( parent );
        
        List<Difference> differences = new ArrayList<Difference>();
        differences.add( new AddAliasDifference(null, null, "toto") );
        differences.add( new ModifyCollectiveDifference(null, null, false, true) );
        differences.add( new RemoveAliasDifference(null, null, "tata") );
        differencesWidget.setInput( differences );
    }


    /**
     * Initializes the Viewer
     */
    private void initViewer( Composite parent )
    {
        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        contentProvider = new SchemaViewContentProvider( viewer );
        viewer.setContentProvider( contentProvider );
        viewer.setLabelProvider( new DecoratingLabelProvider( new SchemaViewLabelProvider(), Activator.getDefault()
            .getWorkbench().getDecoratorManager().getLabelDecorator() ) );
        viewer.setInput( new SchemaViewRoot() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        // TODO Auto-generated method stub

    }


    /**
     * Gets the TreeViewer.
     *
     * @return
     *      the TreeViewer
     */
    public TreeViewer getViewer()
    {
        return viewer;
    }


    /**
     * Reloads the Viewer
     */
    public void reloadViewer()
    {
        viewer.setInput( new SchemaViewRoot() );
    }
}
