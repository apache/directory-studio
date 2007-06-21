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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the General WizardPage of the NewAttributeTypeWizard.
 * <p>
 * It is used to let the user enter general information about the
 * attribute type he wants to create (schema, OID, aliases an description).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewAttributeTypeGeneralWizardPage extends WizardPage
{
    /**
     * Creates a new instance of NewAttributeTypeGeneralWizardPage.
     */
    protected NewAttributeTypeGeneralWizardPage()
    {
        super( "General" );
        setTitle( "Attribute Type" );
        setDescription( "Create a new attribute type." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_ATTRIBUTE_TYPE_NEW_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Schema Group
        Group schemaGroup = new Group( composite, SWT.NONE );
        schemaGroup.setText( "Schema" );
        schemaGroup.setLayout( new GridLayout( 2, false ) );
        schemaGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Schema
        Label schemaLabel = new Label( schemaGroup, SWT.NONE );
        schemaLabel.setText( "Schema:" );
        Combo schemaCombo = new Combo( schemaGroup, SWT.READ_ONLY );
        schemaCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ComboViewer schemaComboViewer = new ComboViewer( schemaCombo );
        schemaComboViewer.setLabelProvider( new LabelProvider() );
        schemaComboViewer.setContentProvider( new ArrayContentProvider() );

        // Naming and Description Group
        Group namingDescriptionGroup = new Group( composite, SWT.NONE );
        namingDescriptionGroup.setText( "Naming and Description" );
        namingDescriptionGroup.setLayout( new GridLayout( 3, false ) );
        namingDescriptionGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // OID
        Label oidLabel = new Label( namingDescriptionGroup, SWT.NONE );
        oidLabel.setText( "OID:" );
        Text oidText = new Text( namingDescriptionGroup, SWT.BORDER );
        oidText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Aliases
        Label aliasesLabel = new Label( namingDescriptionGroup, SWT.NONE );
        aliasesLabel.setText( "Aliases:" );
        Text aliasesText = new Text( namingDescriptionGroup, SWT.BORDER );
        aliasesText.setEnabled( false );
        aliasesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Button aliasesButton = new Button( namingDescriptionGroup, SWT.PUSH );
        aliasesButton.setText( "Edit" );

        // Description
        Label descriptionLabel = new Label( namingDescriptionGroup, SWT.NONE );
        descriptionLabel.setText( "Description:" );
        Text descriptionText = new Text( namingDescriptionGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL );
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        descriptionGridData.heightHint = 67;
        descriptionText.setLayoutData( descriptionGridData );

        setControl( composite );
    }
}
