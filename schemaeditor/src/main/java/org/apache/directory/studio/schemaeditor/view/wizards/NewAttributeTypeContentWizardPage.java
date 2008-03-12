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
package org.apache.directory.studio.schemaeditor.view.wizards;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;
import org.apache.directory.studio.schemaeditor.view.dialogs.AttributeTypeSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


/**
 * This class represents the Content WizardPage of the NewAttributeTypeWizard.
 * <p>
 * It is used to let the user enter content information about the
 * attribute type he wants to create (superior, usage, syntax and properties).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewAttributeTypeContentWizardPage extends AbstractWizardPage
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    // The Usage values
    private static final String DIRECTORY_OPERATION = "Directory Operation";
    private static final String DISTRIBUTED_OPERATION = "Distributed Operation";
    private static final String DSA_OPERATION = "DSA Operation";
    private static final String USER_APPLICATIONS = "User Applications";

    // UI Fields
    private Text superiorText;
    private Button superiorButton;
    private ComboViewer usageComboViewer;
    private ComboViewer syntaxComboViewer;
    private Spinner lengthSpinner;
    private Button obsoleteCheckbox;
    private Button singleValueCheckbox;
    private Button collectiveCheckbox;
    private Button noUserModificationCheckbox;


    /**
     * Creates a new instance of NewAttributeTypeContentWizardPage.
     */
    protected NewAttributeTypeContentWizardPage()
    {
        super( "NewAttributeTypeContentWizardPage" );
        setTitle( "Attribute Type Content" );
        setDescription( "Please enter the superior, usage, syntax and properties for the attribute type." );
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_ATTRIBUTE_TYPE_NEW_WIZARD ) );
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Superior and Usage Group
        Group superiorUsageGroup = new Group( composite, SWT.NONE );
        superiorUsageGroup.setText( "Superior and Usage" );
        superiorUsageGroup.setLayout( new GridLayout( 3, false ) );
        superiorUsageGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Superior
        Label superiorLabel = new Label( superiorUsageGroup, SWT.NONE );
        superiorLabel.setText( "Superior:" );
        superiorText = new Text( superiorUsageGroup, SWT.BORDER );
        superiorText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        superiorText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent arg0 )
            {
                verifySuperior();
            }
        } );
        superiorButton = new Button( superiorUsageGroup, SWT.PUSH );
        superiorButton.setText( "Choose..." );
        superiorButton.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );
        superiorButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
                if ( dialog.open() == Dialog.OK )
                {
                    AttributeTypeImpl selectedAT = dialog.getSelectedAttributeType();
                    String[] aliases = selectedAT.getNames();
                    if ( ( aliases != null ) && ( aliases.length > 0 ) )
                    {
                        superiorText.setText( aliases[0] );
                    }
                    else
                    {
                        superiorText.setText( selectedAT.getOid() );
                    }
                }
            }
        } );

        // Usage
        Label usageLabel = new Label( superiorUsageGroup, SWT.NONE );
        usageLabel.setText( "Usage:" );
        Combo usageCombo = new Combo( superiorUsageGroup, SWT.READ_ONLY );
        usageCombo.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );
        usageComboViewer = new ComboViewer( usageCombo );
        usageComboViewer.setLabelProvider( new LabelProvider() );
        usageComboViewer.setContentProvider( new ArrayContentProvider() );
        usageComboViewer.setInput( new String[]
            { DIRECTORY_OPERATION, DISTRIBUTED_OPERATION, DSA_OPERATION, USER_APPLICATIONS } );
        usageComboViewer.setSelection( new StructuredSelection( USER_APPLICATIONS ) );

        // Syntax Group
        Group syntaxGroup = new Group( composite, SWT.NONE );
        syntaxGroup.setText( "Syntax" );
        syntaxGroup.setLayout( new GridLayout( 2, false ) );
        syntaxGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Syntax
        Label syntaxLabel = new Label( syntaxGroup, SWT.NONE );
        syntaxLabel.setText( "Syntax:" );
        Combo syntaxCombo = new Combo( syntaxGroup, SWT.READ_ONLY );
        syntaxCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        syntaxComboViewer = new ComboViewer( syntaxCombo );
        syntaxComboViewer.setContentProvider( new ArrayContentProvider() );
        syntaxComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof SyntaxImpl )
                {
                    SyntaxImpl syntax = ( SyntaxImpl ) element;

                    String name = syntax.getName();
                    if ( name != null )
                    {
                        return name + "  -  (" + syntax.getOid() + ")";
                    }
                    else
                    {
                        return "(None)  -  (" + syntax.getOid() + ")";
                    }
                }

                return super.getText( element );
            }
        } );

        // Syntax Length
        Label lengthLabel = new Label( syntaxGroup, SWT.NONE );
        lengthLabel.setText( "Length:" );
        lengthSpinner = new Spinner( syntaxGroup, SWT.BORDER );
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
        propertiesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Obsolete
        new Label( composite, SWT.NONE );
        obsoleteCheckbox = new Button( propertiesGroup, SWT.CHECK );
        obsoleteCheckbox.setText( "Obsolete" );

        // Single value
        new Label( composite, SWT.NONE );
        singleValueCheckbox = new Button( propertiesGroup, SWT.CHECK );
        singleValueCheckbox.setText( "Single Value" );

        // Collective
        new Label( composite, SWT.NONE );
        collectiveCheckbox = new Button( propertiesGroup, SWT.CHECK );
        collectiveCheckbox.setText( "Collective" );

        // No User Modification
        new Label( composite, SWT.NONE );
        noUserModificationCheckbox = new Button( propertiesGroup, SWT.CHECK );
        noUserModificationCheckbox.setText( "No User Modification" );

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI fields.
     */
    @SuppressWarnings("unchecked")
    private void initFields()
    {
        if ( schemaHandler != null )
        {
            // Getting the syntaxes
            List<Object> syntaxes = new ArrayList( schemaHandler.getSyntaxes() );
            // Adding the (None) Syntax
            String none = "(None)";
            syntaxes.add( none );

            // Sorting the syntaxes
            Collections.sort( syntaxes, new Comparator<Object>()
            {
                public int compare( Object o1, Object o2 )
                {
                    if ( ( o1 instanceof SyntaxImpl ) && ( o2 instanceof SyntaxImpl ) )
                    {
                        String[] o1Names = ( ( SyntaxImpl ) o1 ).getNames();
                        String[] o2Names = ( ( SyntaxImpl ) o2 ).getNames();

                        // Comparing the First Name
                        if ( ( o1Names != null ) && ( o2Names != null ) )
                        {
                            if ( ( o1Names.length > 0 ) && ( o2Names.length > 0 ) )
                            {
                                return o1Names[0].compareToIgnoreCase( o2Names[0] );
                            }
                            else if ( ( o1Names.length == 0 ) && ( o2Names.length > 0 ) )
                            {
                                return "".compareToIgnoreCase( o2Names[0] );
                            }
                            else if ( ( o1Names.length > 0 ) && ( o2Names.length == 0 ) )
                            {
                                return o1Names[0].compareToIgnoreCase( "" );
                            }
                        }
                    }
                    else if ( ( o1 instanceof String ) && ( o2 instanceof SyntaxImpl ) )
                    {
                        return Integer.MIN_VALUE;
                    }
                    else if ( ( o1 instanceof SyntaxImpl ) && ( o2 instanceof String ) )
                    {
                        return Integer.MAX_VALUE;
                    }

                    // Default
                    return o1.toString().compareToIgnoreCase( o2.toString() );
                }
            } );

            // Setting the input
            syntaxComboViewer.setInput( syntaxes );
            syntaxComboViewer.setSelection( new StructuredSelection( none ) );
        }
    }


    /**
     * Verifies if the superior exists and displays an error if not.
     */
    private void verifySuperior()
    {
        String superior = superiorText.getText();
        if ( ( superior != null ) && ( !superior.equals( "" ) ) )
        {
            if ( schemaHandler.getAttributeType( superiorText.getText() ) == null )
            {
                displayErrorMessage( "The superior attribute type does not exist." );
                return;
            }
        }

        displayErrorMessage( null );
    }


    /**
     * Gets the superior value.
     *
     * @return
     *      the superior value
     */
    public String getSuperiorValue()
    {
        String superior = superiorText.getText();
        if ( ( superior != null ) && ( !superior.equals( "" ) ) )
        {
            return superior;
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets the usage value.
     *
     * @return
     *      the usage value
     */
    public UsageEnum getUsageValue()
    {
        StructuredSelection selection = ( StructuredSelection ) usageComboViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            String selectedUsage = ( String ) selection.getFirstElement();
            if ( selectedUsage.equals( DIRECTORY_OPERATION ) )
            {
                return UsageEnum.DIRECTORY_OPERATION;
            }
            else if ( selectedUsage.equals( DISTRIBUTED_OPERATION ) )
            {
                return UsageEnum.DISTRIBUTED_OPERATION;
            }
            else if ( selectedUsage.equals( DSA_OPERATION ) )
            {
                return UsageEnum.DSA_OPERATION;
            }
            else if ( selectedUsage.equals( USER_APPLICATIONS ) )
            {
                return UsageEnum.USER_APPLICATIONS;
            }
            else
            {
                return UsageEnum.USER_APPLICATIONS;
            }
        }
        else
        {
            return UsageEnum.USER_APPLICATIONS;
        }
    }


    /**
     * Gets the syntax value.
     *
     * @return
     *      the syntax value
     */
    public String getSyntax()
    {
        Object selection = ( ( StructuredSelection ) syntaxComboViewer.getSelection() ).getFirstElement();

        if ( selection instanceof SyntaxImpl )
        {
            return ( ( SyntaxImpl ) selection ).getOid();
        }

        return null;
    }


    /**
     * Gets the syntax length value.
     *
     * @return
     *      the syntax length value
     */
    public int getSyntaxLengthValue()
    {
        return lengthSpinner.getSelection();
    }


    /**
     * Gets the 'Obsolete' value.
     *
     * @return
     *      the 'Obsolete' value
     */
    public boolean getObsoleteValue()
    {
        return obsoleteCheckbox.getSelection();
    }


    /**
     * Gets the 'Single Value' value
     *
     * @return
     *      the 'Single Value' value
     */
    public boolean getSingleValueValue()
    {
        return singleValueCheckbox.getSelection();
    }


    /**
     * Gets the 'Collective' value.
     *
     * @return
     *      the 'Collective' value
     */
    public boolean getCollectiveValue()
    {
        return collectiveCheckbox.getSelection();
    }


    /**
     * Gets the 'No User Modification' value.
     *
     * @return
     *      the 'No User Modification' value
     */
    public boolean getNoUserModificationValue()
    {
        return noUserModificationCheckbox.getSelection();
    }
}
