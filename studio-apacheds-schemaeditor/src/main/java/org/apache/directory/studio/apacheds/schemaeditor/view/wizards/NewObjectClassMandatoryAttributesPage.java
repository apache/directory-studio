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
package org.apache.directory.studio.apacheds.schemaeditor.view.wizards;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * This class represents the Mandatory Attribute Types WizardPage of the NewObjectClassWizard.
 * <p>
 * It is used to let the user specify the mandatory attribute types for the object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewObjectClassMandatoryAttributesPage extends WizardPage
{
    /**
     * Creates a new instance of NewObjectClassMandatoryAttributesPage.
     */
    protected NewObjectClassMandatoryAttributesPage()
    {
        super( "NewObjectClassMandatoryAttributesPage" );
        setTitle( "Mandatory Attribute Types" );
        setDescription( "Please specify the mandatory attribute types for the object class." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_OBJECT_CLASS_NEW_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Mandatory Attribute Types Group
        Group mandatoryAttributeTypesGroup = new Group( composite, SWT.NONE );
        mandatoryAttributeTypesGroup.setText( "Mandatory Attribute Types" );
        mandatoryAttributeTypesGroup.setLayout( new GridLayout( 2, false ) );
        mandatoryAttributeTypesGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        
        // Mandatory Attribute Types
        Table mandatoryAttributeTypesTable = new Table( mandatoryAttributeTypesGroup, SWT.BORDER );
        GridData mandatoryAttributeTypesTableGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
        mandatoryAttributeTypesTableGridData.heightHint = 100;
        mandatoryAttributeTypesTable.setLayoutData( mandatoryAttributeTypesTableGridData );
        TableViewer mandatoryAttributeTypesTableViewer = new TableViewer( mandatoryAttributeTypesTable );
        mandatoryAttributeTypesTableViewer.setLabelProvider( new LabelProvider() );
        mandatoryAttributeTypesTableViewer.setContentProvider( new ArrayContentProvider() );
        Button mandatoryAttributeTypesAddButton = new Button( mandatoryAttributeTypesGroup, SWT.PUSH );
        mandatoryAttributeTypesAddButton.setText( "Add..." );
        mandatoryAttributeTypesAddButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        Button mandatoryAttributeTypesRemoveButton = new Button( mandatoryAttributeTypesGroup, SWT.PUSH );
        mandatoryAttributeTypesRemoveButton.setText( "Remove" );
        mandatoryAttributeTypesRemoveButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        
        setControl( composite );
    }
}
