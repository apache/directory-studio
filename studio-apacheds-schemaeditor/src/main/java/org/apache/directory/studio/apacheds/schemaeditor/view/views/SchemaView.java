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

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaViewController;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.*;
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
    public static final String ID = Activator.PLUGIN_ID + ".view.SchemaView"; //$NON-NLS-1$

    /** The viewer */
    private TreeViewer treeViewer;

    /** The content provider of the viewer */
    private SchemaViewContentProvider contentProvider;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        initViewer( parent );

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( treeViewer );

        // Adding the controller
        new SchemaViewController( this );

        //        DifferencesWidget differencesWidget = new DifferencesWidget();
        //        differencesWidget.createWidget( parent );
        //        
        //        List<Difference> differences = new ArrayList<Difference>();
        //        differences.add( new AddAliasDifference(null, null, "alias1") );
        //        differences.add( new RemoveAliasDifference(null, null, "alias2") );
        //        differences.add( new AddDescriptionDifference(null, null, "Description") );
        //        differences.add( new ModifyDescriptionDifference(null, null, "Old description", "New Description") );
        //        differences.add( new RemoveDescriptionDifference(null, null, "Description") );
        //        differences.add( new AddEqualityDifference(null, null, "equality") );
        //        differences.add( new ModifyEqualityDifference(null, null, "old equality", "new equality") );
        //        differences.add( new RemoveEqualityDifference(null, null, "equality") );
        //        differences.add( new AddMandatoryATDifference(null, null, "name") );
        //        differences.add( new RemoveMandatoryATDifference(null, null, "name2") );
        //        differences.add( new AddOptionalATDifference(null, null, "name") );
        //        differences.add( new RemoveOptionalATDifference(null, null, "name2") );
        //        differences.add( new AddOrderingDifference(null, null, "ordering") );
        //        differences.add( new ModifyOrderingDifference(null, null, "old ordering", "new ordering") );
        //        differences.add( new RemoveOrderingDifference(null, null, "ordering") );
        //        differences.add( new AddSubstringDifference(null, null, "substring") );
        //        differences.add( new ModifySubstringDifference(null, null, "old substring", "new substring") );
        //        differences.add( new RemoveSubstringDifference(null, null, "substring") );
        //        differences.add( new AddSuperiorATDifference(null, null, "supAT") );
        //        differences.add( new ModifySuperiorATDifference(null, null, "oldSupAT", "newSupAT") );
        //        differences.add( new RemoveSuperiorATDifference(null, null, "supAT") );
        //        differences.add( new AddSuperiorOCDifference(null, null, "supOC") );
        //        differences.add( new RemoveSuperiorOCDifference(null, null, "supOC") );
        //        differences.add( new AddSyntaxDifference(null, null, "syntax") );
        //        differences.add( new ModifySyntaxDifference(null, null, "syntax1", "syntax2") );
        //        differences.add( new RemoveSyntaxDifference(null, null, "syntax") );
        //        differences.add( new AddSyntaxLengthDifference(null, null, 1234) );
        //        differences.add( new ModifySyntaxLengthDifference(null, null, 1234, 12345) );
        //        differences.add( new RemoveSyntaxLengthDifference(null, null, 1234) );
        //        differences.add( new ModifyClassTypeDifference(null, null, ObjectClassTypeEnum.AUXILIARY, ObjectClassTypeEnum.ABSTRACT) );
        //        differences.add( new ModifyCollectiveDifference(null, null, false, true) );
        //        differences.add( new ModifyNoUserModificationDifference(null, null, true, false) );
        //        differences.add( new ModifyObsoleteDifference(null, null, true, false) );
        //        differences.add( new ModifySingleValueDifference(null, null, true, false) );
        //        differences.add( new ModifyUsageDifference(null, null, UsageEnum.DISTRIBUTED_OPERATION, UsageEnum.DSA_OPERATION) );
        //        differencesWidget.setInput( differences );
    }


    /**
     * Initializes the Viewer
     */
    private void initViewer( Composite parent )
    {
        treeViewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        contentProvider = new SchemaViewContentProvider( treeViewer );
        treeViewer.setContentProvider( contentProvider );
        treeViewer.setLabelProvider( new DecoratingLabelProvider( new SchemaViewLabelProvider(), Activator.getDefault()
            .getWorkbench().getDecoratorManager().getLabelDecorator() ) );
        treeViewer.setInput( new SchemaViewRoot() );
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
        treeViewer.setInput( new SchemaViewRoot() );
    }
}
