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

package org.apache.directory.studio.ldapbrowser.common.wizards;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.directory.shared.ldap.schema.syntax.AttributeTypeDescription;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.ListContentProposalProvider;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
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
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;


/**
 * The AttributeTypeWizardPage provides a combo to select the attribute type,
 * some filter and a preview field.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeWizardPage extends WizardPage
{

    /** The parent wizard. */
    private AttributeWizard wizard;

    /** The initial show subschema attributes only. */
    private boolean initialShowSubschemaAttributesOnly;

    /** The initial hide existing attributes. */
    private boolean initialHideExistingAttributes;

    /** The parsed attribute type. */
    private String parsedAttributeType;

    /** The possible attribute types. */
    private String[] possibleAttributeTypes;

    /** The possible attribute types applicable to the entry's schema only. */
    private String[] possibleAttributeTypesSubschemaOnly;

    /** The possible attribute types applicable to the entry's schema only, existing attributes are hidden. */
    private String[] possibleAttributeTypesSubschemaOnlyAndExistingHidden;

    /** The attribute type combo. */
    private Combo attributeTypeCombo;

    /** The attribute type content proposal adapter */
    private ContentProposalAdapter attributeTypeCPA;

    /** The show subschem attributes only button. */
    private Button showSubschemAttributesOnlyButton;

    /** The hide existing attributes button. */
    private Button hideExistingAttributesButton;

    /** The preview text. */
    private Text previewText;


    /**
     * Creates a new instance of AttributeTypeWizardPage.
     * 
     * @param pageName the page name
     * @param initialEntry the initial entry
     * @param initialAttributeDescription the initial attribute description
     * @param initialShowSubschemaAttributesOnly the initial show subschema attributes only
     * @param initialHideExistingAttributes the initial hide existing attributes
     * @param wizard the wizard
     */
    public AttributeTypeWizardPage( String pageName, IEntry initialEntry, String initialAttributeDescription,
        boolean initialShowSubschemaAttributesOnly, boolean initialHideExistingAttributes, AttributeWizard wizard )
    {
        super( pageName );
        super.setTitle( "Attribute Type" );
        super.setDescription( "Please enter or select the attribute type." );
        // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
        super.setPageComplete( false );

        this.wizard = wizard;
        this.initialShowSubschemaAttributesOnly = initialShowSubschemaAttributesOnly;
        this.initialHideExistingAttributes = initialHideExistingAttributes;

        Collection<AttributeTypeDescription> atds = initialEntry.getBrowserConnection().getSchema()
            .getAttributeTypeDescriptions();
        Collection<String> atdNames = SchemaUtils.getNames( atds );
        possibleAttributeTypes = atdNames.toArray( new String[atdNames.size()] );
        Arrays.sort( possibleAttributeTypes );
        possibleAttributeTypesSubschemaOnly = initialEntry.getSubschema().getAllAttributeNames();
        Arrays.sort( possibleAttributeTypesSubschemaOnly );

        Set<String> set = new HashSet<String>( Arrays.asList( initialEntry.getSubschema().getAllAttributeNames() ) );
        IAttribute[] existingAttributes = initialEntry.getAttributes();
        for ( int i = 0; existingAttributes != null && i < existingAttributes.length; i++ )
        {
            set.remove( existingAttributes[i].getDescription() );
        }
        possibleAttributeTypesSubschemaOnlyAndExistingHidden = ( String[] ) set.toArray( new String[set.size()] );
        Arrays.sort( possibleAttributeTypesSubschemaOnlyAndExistingHidden );

        String attributeDescription = initialAttributeDescription;
        if ( attributeDescription == null )
        {
            attributeDescription = "";
        }
        String[] attributeDescriptionComponents = attributeDescription.split( ";" );
        parsedAttributeType = attributeDescriptionComponents[0];
    }


    /**
     * Validates this page.
     */
    private void validate()
    {
        previewText.setText( wizard.getAttributeDescription() );
        setPageComplete( !"".equals( attributeTypeCombo.getText() ) );
    }


    /**
     * {@inheritDoc}
     */
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
        if ( visible )
        {
            validate();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        BaseWidgetUtils.createLabel( composite, "Attribute type:", 1 );

        // attribute combo with field decoration and content proposal
        attributeTypeCombo = BaseWidgetUtils.createCombo( composite, new String[0], -1, 1 );
        attributeTypeCombo.setVisibleItemCount( 20 );
        attributeTypeCombo.setItems( possibleAttributeTypes );
        attributeTypeCombo.setText( parsedAttributeType );
        attributeTypeCPA = new ContentAssistCommandAdapter( attributeTypeCombo, new ComboContentAdapter(),
            new ListContentProposalProvider( possibleAttributeTypes ), null, null, true );
        attributeTypeCPA.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );
        attributeTypeCPA.setFilterStyle( ContentProposalAdapter.FILTER_NONE );
        attributeTypeCPA.setAutoActivationCharacters( null );
        attributeTypeCPA.setAutoActivationDelay( 0 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        showSubschemAttributesOnlyButton = BaseWidgetUtils.createCheckbox( composite, "Show subschema attributes only",
            1 );
        showSubschemAttributesOnlyButton.setSelection( initialShowSubschemaAttributesOnly );

        BaseWidgetUtils.createSpacer( composite, 1 );
        hideExistingAttributesButton = BaseWidgetUtils.createCheckbox( composite, "Hide existing attributes", 1 );
        hideExistingAttributesButton.setSelection( initialHideExistingAttributes );

        Label l = new Label( composite, SWT.NONE );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.horizontalSpan = 2;
        l.setLayoutData( gd );

        BaseWidgetUtils.createLabel( composite, "Preview:", 1 );
        previewText = BaseWidgetUtils.createReadonlyText( composite, "", 1 );

        // attribute type listener
        attributeTypeCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        // filter listener
        showSubschemAttributesOnlyButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateFilter();
                validate();
            }
        } );
        hideExistingAttributesButton.addSelectionListener( new SelectionAdapter()
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


    /**
     * Updates the filter.
     */
    private void updateFilter()
    {
        // enable/disable filter buttons
        hideExistingAttributesButton.setEnabled( showSubschemAttributesOnlyButton.getSelection() );
        if ( possibleAttributeTypesSubschemaOnly.length == 0 )
        {
            showSubschemAttributesOnlyButton.setSelection( false );
            showSubschemAttributesOnlyButton.setEnabled( false );
        }
        if ( possibleAttributeTypesSubschemaOnlyAndExistingHidden.length == 0 )
        {
            hideExistingAttributesButton.setEnabled( false );
            hideExistingAttributesButton.setSelection( false );
        }

        // update combo items and proposals
        String value = attributeTypeCombo.getText();
        if ( hideExistingAttributesButton.getSelection() && showSubschemAttributesOnlyButton.getSelection() )
        {
            attributeTypeCombo.setItems( possibleAttributeTypesSubschemaOnlyAndExistingHidden );
        }
        else if ( showSubschemAttributesOnlyButton.getSelection() )
        {
            attributeTypeCombo.setItems( possibleAttributeTypesSubschemaOnly );
        }
        else
        {
            attributeTypeCombo.setItems( possibleAttributeTypes );
        }
        attributeTypeCombo.setText( value );
    }


    /**
     * Gets the attribute type.
     * 
     * @return the attribute type
     */
    String getAttributeType()
    {
        if ( attributeTypeCombo == null | attributeTypeCombo.isDisposed() )
        {
            return "";
        }

        return attributeTypeCombo.getText();
    }

}
