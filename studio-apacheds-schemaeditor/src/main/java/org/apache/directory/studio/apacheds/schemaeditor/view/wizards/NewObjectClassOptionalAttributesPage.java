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
 * This class represents the Optional Attribute Types WizardPage of the NewObjectClassWizard.
 * <p>
 * It is used to let the user specify the optional attribute types for the object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewObjectClassOptionalAttributesPage extends WizardPage
{
    /**
     * Creates a new instance of NewObjectClassOptionalAttributesPage.
     */
    protected NewObjectClassOptionalAttributesPage()
    {
        super( "NewObjectClassOptionalAttributesPage" );
        setTitle( "Optional Attribute Types" );
        setDescription( "Please specify the optional attribute types for the object class." );
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

        // Optional Attribute Types Group
        Group optionalAttributeTypesGroup = new Group( composite, SWT.NONE );
        optionalAttributeTypesGroup.setText( "Optional Attribute Types" );
        optionalAttributeTypesGroup.setLayout( new GridLayout( 2, false ) );
        optionalAttributeTypesGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Optional Attribute Types
        Table optionalAttributeTypesTable = new Table( optionalAttributeTypesGroup, SWT.BORDER );
        GridData optionalAttributeTypesTableGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
        optionalAttributeTypesTableGridData.heightHint = 100;
        optionalAttributeTypesTable.setLayoutData( optionalAttributeTypesTableGridData );
        TableViewer optionalAttributeTypesTableViewer = new TableViewer( optionalAttributeTypesTable );
        optionalAttributeTypesTableViewer.setLabelProvider( new LabelProvider() );
        optionalAttributeTypesTableViewer.setContentProvider( new ArrayContentProvider() );
        Button optionalAttributeTypesAddButton = new Button( optionalAttributeTypesGroup, SWT.PUSH );
        optionalAttributeTypesAddButton.setText( "Add..." );
        optionalAttributeTypesAddButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        Button optionalAttributeTypesRemoveButton = new Button( optionalAttributeTypesGroup, SWT.PUSH );
        optionalAttributeTypesRemoveButton.setText( "Remove" );
        optionalAttributeTypesRemoveButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );

        setControl( composite );
    }
}
