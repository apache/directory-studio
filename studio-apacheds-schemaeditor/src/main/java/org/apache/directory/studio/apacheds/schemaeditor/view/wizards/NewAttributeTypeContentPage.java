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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the Content WizardPage of the NewAttributeTypeWizard.
 * <p>
 * It is used to let the user enter content information about the
 * attribute type he wants to create (superior, usage, syntax an properties).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewAttributeTypeContentPage extends WizardPage
{
    /**
     * Creates a new instance of NewAttributeTypeContentPage.
     */
    protected NewAttributeTypeContentPage()
    {
        super( "Options" );
        setTitle( "Attribute Type Content" );
        setDescription( "Please enter the superior, usage, syntax and properties for the attribute type." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_ATTRIBUTE_TYPE_NEW_WIZARD ) );
    }


    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Superior and Usage Group
        Group superiorUsageGroup = new Group( composite, SWT.NONE );
        superiorUsageGroup.setText( "Superior and Usage" );
        superiorUsageGroup.setLayout( new GridLayout( 3, false ) );
        superiorUsageGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false) );
        
        // Superior
        Label superiorLabel = new Label( superiorUsageGroup, SWT.NONE );
        superiorLabel.setText( "Superior:" );
        Text superiorText = new Text( superiorUsageGroup, SWT.BORDER );
        superiorText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Button superiorButton = new Button( superiorUsageGroup, SWT.PUSH );
        superiorButton.setText( "Choose" );
        superiorButton.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false) );

        // Usage
        Label usageLabel = new Label( superiorUsageGroup, SWT.NONE );
        usageLabel.setText( "Usage:" );
        Combo usageCombo = new Combo( superiorUsageGroup, SWT.READ_ONLY );
        usageCombo.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );
        ComboViewer usageComboViewer = new ComboViewer( usageCombo );
        usageComboViewer.setLabelProvider( new LabelProvider() );
        usageComboViewer.setContentProvider( new ArrayContentProvider() );
        usageComboViewer.setInput( new String[]
            { "Directory Operation", "Distributed Operation", " DSA Operation", "User Applications" } );

        // Syntax Group
        Group syntaxGroup = new Group( composite, SWT.NONE );
        syntaxGroup.setText( "Syntax" );
        syntaxGroup.setLayout( new GridLayout( 2, false ) );
        syntaxGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false) );
        
        // Syntax
        Label syntaxLabel = new Label( syntaxGroup, SWT.NONE );
        syntaxLabel.setText( "Syntax:" );
        Combo syntaxCombo = new Combo( syntaxGroup, SWT.BORDER );
        syntaxCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Syntax Length
        Label lengthLabel = new Label( syntaxGroup, SWT.NONE );
        lengthLabel.setText( "Length:" );
        Spinner lengthSpinner = new Spinner( syntaxGroup, SWT.BORDER );
        lengthSpinner.setIncrement( 1 );
        lengthSpinner.setMinimum( 0 );
        lengthSpinner.setMaximum( Integer.MAX_VALUE );
        GridData lengthSpinnerGridData = new GridData( SWT.NONE, SWT.NONE, false, false );
        lengthSpinnerGridData.widthHint = 42;
        lengthSpinner.setLayoutData( lengthSpinnerGridData );

        // Properties Group
        Group propertiesGroup = new Group( composite, SWT.NONE );
        propertiesGroup.setText( "Properties" );
        propertiesGroup.setLayout( new GridLayout() );
        propertiesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false) );
        
        // Obsolete
        new Label( composite, SWT.NONE );
        Button obsoleteCheckbox = new Button( propertiesGroup, SWT.CHECK );
        obsoleteCheckbox.setText( "Obsolete" );

        // Single value
        new Label( composite, SWT.NONE );
        Button singleValueCheckbox = new Button( propertiesGroup, SWT.CHECK );
        singleValueCheckbox.setText( "Single Value" );

        // Collective
        new Label( composite, SWT.NONE );
        Button collectiveCheckbox = new Button( propertiesGroup, SWT.CHECK );
        collectiveCheckbox.setText( "Collective" );

        // No User Modification
        new Label( composite, SWT.NONE );
        Button noUserModificationCheckbox = new Button( propertiesGroup, SWT.CHECK );
        noUserModificationCheckbox.setText( "No User Modification" );

        setControl( composite );
    }
}
