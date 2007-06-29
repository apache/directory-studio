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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the Content WizardPage of the ObjectClassWizard.
 * <p>
 * It is used to let the user enter content information about the
 * attribute type he wants to create (superiors, class type, and properties).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewObjectClassContentWizardPage extends WizardPage
{
    /**
     * Creates a new instance of NewAttributeTypeContentWizardPage.
     */
    protected NewObjectClassContentWizardPage()
    {
        super( "NewObjectClassContentWizardPage" );
        setTitle( "Object Class Content" );
        setDescription( "Please enter the superiors, class type  and properties for the object class." );
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

        // Superiors
        Group superiorsGroup = new Group( composite, SWT.NONE );
        superiorsGroup.setText( "Superiors" );
        superiorsGroup.setLayout( new GridLayout( 2, false ) );
        superiorsGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Superiors
        Table superiorsTable = new Table( superiorsGroup, SWT.BORDER );
        GridData superiorsTableGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
        superiorsTableGridData.heightHint = 100;
        superiorsTable.setLayoutData( superiorsTableGridData );
        TableViewer superiorsTableViewer = new TableViewer( superiorsTable );
        superiorsTableViewer.setLabelProvider( new LabelProvider() );
        superiorsTableViewer.setContentProvider( new ArrayContentProvider() );
        Button superiorsAddButton = new Button( superiorsGroup, SWT.PUSH );
        superiorsAddButton.setText( "Add..." );
        superiorsAddButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        Button superiorsRemoveButton = new Button( superiorsGroup, SWT.PUSH );
        superiorsRemoveButton.setText( "Remove" );
        superiorsRemoveButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );

        // Class Type Group
        Group classTypeGroup = new Group( composite, SWT.NONE );
        classTypeGroup.setText( "Class Type" );
        classTypeGroup.setLayout( new GridLayout( 5, false ) );
        classTypeGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Class Type
        Label classTypeLable = new Label( classTypeGroup, SWT.NONE );
        classTypeLable.setText( "Class Type:" );
        new Label( classTypeGroup, SWT.NONE ).setText( "          " );
        Button structuralRadio = new Button( classTypeGroup, SWT.RADIO );
        structuralRadio.setText( "Structural" );
        GridData structuralRadioGridData = new GridData( SWT.LEFT, SWT.NONE, false, false );
        structuralRadioGridData.widthHint = 115;
        structuralRadio.setLayoutData( structuralRadioGridData );
        Button abstractRadio = new Button( classTypeGroup, SWT.RADIO );
        abstractRadio.setText( "Abstract" );
        GridData abstractRadioGridData = new GridData( SWT.LEFT, SWT.NONE, false, false );
        abstractRadioGridData.widthHint = 115;
        abstractRadio.setLayoutData( structuralRadioGridData );
        Button auxiliaryRadio = new Button( classTypeGroup, SWT.RADIO );
        auxiliaryRadio.setText( "Auxiliary" );
        GridData auxiliaryRadioGridData = new GridData( SWT.LEFT, SWT.NONE, false, false );
        auxiliaryRadioGridData.widthHint = 115;
        auxiliaryRadio.setLayoutData( structuralRadioGridData );

        // Properties Group
        Group propertiesGroup = new Group( composite, SWT.NONE );
        propertiesGroup.setText( "Properties" );
        propertiesGroup.setLayout( new GridLayout() );
        propertiesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Obsolete
        new Label( composite, SWT.NONE );
        Button obsoleteCheckbox = new Button( propertiesGroup, SWT.CHECK );
        obsoleteCheckbox.setText( "Obsolete" );

        setControl( composite );
    }
}
