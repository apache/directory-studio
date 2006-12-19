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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class AttributeTypeWizardPage extends WizardPage
{

    private AttributeWizard wizard;

    private boolean initialShowSubschemaAttributesOnly;

    private boolean initialHideExistingAttributes;

    private String initialAttributeDescription;

    private IEntry initialEntry;

    private String parsedAttributeType;

    private String[] possibleAttributeTypes;

    private String[] possibleAttributeTypesSubschemaOnly;

    private String[] possibleAttributeTypesSubschemaOnlyAndExistingHidden;

    private Combo attributeTypeCombo;

    private Button showSubschemAttributesOnlyButton;

    private Button hideExistingAttributesButton;

    private Text previewText;


    public AttributeTypeWizardPage( String pageName, IEntry initialEntry, String initialAttributeDescription,
        boolean initialShowSubschemaAttributesOnly, boolean initialHideExistingAttributes, AttributeWizard wizard )
    {
        super( pageName );
        super.setTitle( "Attribute Type" );
        super.setDescription( "Please enter or select the attribute type." );
        // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
        super.setPageComplete( false );

        this.wizard = wizard;
        this.initialEntry = initialEntry;
        this.initialAttributeDescription = initialAttributeDescription;
        this.initialShowSubschemaAttributesOnly = initialShowSubschemaAttributesOnly;
        this.initialHideExistingAttributes = initialHideExistingAttributes;

        this.possibleAttributeTypes = this.initialEntry.getConnection().getSchema().getAttributeTypeDescriptionNames();
        Arrays.sort( this.possibleAttributeTypes );
        this.possibleAttributeTypesSubschemaOnly = this.initialEntry.getSubschema().getAllAttributeNames();
        Arrays.sort( this.possibleAttributeTypesSubschemaOnly );

        Set set = new HashSet( Arrays.asList( this.initialEntry.getSubschema().getAllAttributeNames() ) );
        IAttribute[] existingAttributes = this.initialEntry.getAttributes();
        for ( int i = 0; existingAttributes != null && i < existingAttributes.length; i++ )
        {
            set.remove( existingAttributes[i].getDescription() );
        }
        this.possibleAttributeTypesSubschemaOnlyAndExistingHidden = ( String[] ) set.toArray( new String[set.size()] );
        Arrays.sort( this.possibleAttributeTypesSubschemaOnlyAndExistingHidden );

        String attributeDescription = this.initialAttributeDescription;
        if ( attributeDescription == null )
            attributeDescription = "";
        String[] attributeDescriptionComponents = attributeDescription.split( ";" );
        this.parsedAttributeType = attributeDescriptionComponents[0];

    }


    private void validate()
    {
        this.previewText.setText( wizard.getAttributeDescription() );
        this.setPageComplete( !"".equals( this.attributeTypeCombo.getText() ) );
    }


    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
        if ( visible )
        {
            this.validate();
        }
    }


    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        BaseWidgetUtils.createLabel( composite, "Attribute type:", 1 );
        this.attributeTypeCombo = BaseWidgetUtils.createCombo( composite, possibleAttributeTypes, -1, 1 );
        this.attributeTypeCombo.setText( parsedAttributeType );

        BaseWidgetUtils.createSpacer( composite, 1 );
        this.showSubschemAttributesOnlyButton = BaseWidgetUtils.createCheckbox( composite,
            "Show subschema attributes only", 1 );
        this.showSubschemAttributesOnlyButton.setSelection( initialShowSubschemaAttributesOnly );

        BaseWidgetUtils.createSpacer( composite, 1 );
        this.hideExistingAttributesButton = BaseWidgetUtils.createCheckbox( composite, "Hide existing attributes", 1 );
        this.hideExistingAttributesButton.setSelection( initialHideExistingAttributes );

        Label l = new Label( composite, SWT.NONE );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.horizontalSpan = 2;
        l.setLayoutData( gd );

        /* this.previewLabel = */BaseWidgetUtils.createLabel( composite, "Preview:", 1 );
        this.previewText = BaseWidgetUtils.createReadonlyText( composite, "", 1 );

        // attribute type listener
        this.attributeTypeCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        // filter listener
        this.showSubschemAttributesOnlyButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateFilter();
                validate();
            }
        } );
        this.hideExistingAttributesButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateFilter();
                validate();
            }
        } );
        updateFilter();

        setControl( composite );
    }


    private void updateFilter()
    {
        // enable/disable filter buttons
        this.hideExistingAttributesButton.setEnabled( this.showSubschemAttributesOnlyButton.getSelection() );
        if ( this.possibleAttributeTypesSubschemaOnly.length == 0 )
        {
            this.showSubschemAttributesOnlyButton.setSelection( false );
            this.showSubschemAttributesOnlyButton.setEnabled( false );
        }
        if ( this.possibleAttributeTypesSubschemaOnlyAndExistingHidden.length == 0 )
        {
            this.hideExistingAttributesButton.setEnabled( false );
            this.hideExistingAttributesButton.setSelection( false );
        }

        // update filters
        String value = this.attributeTypeCombo.getText();
        if ( this.hideExistingAttributesButton.getSelection() && this.showSubschemAttributesOnlyButton.getSelection() )
        {
            this.attributeTypeCombo.setItems( this.possibleAttributeTypesSubschemaOnlyAndExistingHidden );
        }
        else if ( this.showSubschemAttributesOnlyButton.getSelection() )
        {
            this.attributeTypeCombo.setItems( this.possibleAttributeTypesSubschemaOnly );
        }
        else
        {
            this.attributeTypeCombo.setItems( this.possibleAttributeTypes );
        }
        this.attributeTypeCombo.setText( value );
    }


    String getAttributeType()
    {

        if ( this.attributeTypeCombo == null | this.attributeTypeCombo.isDisposed() )
        {
            return "";
        }

        return attributeTypeCombo.getText();
    }

}
