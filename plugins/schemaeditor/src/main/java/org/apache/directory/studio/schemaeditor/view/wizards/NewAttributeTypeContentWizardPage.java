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

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.view.dialogs.AttributeTypeSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
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
 */
public class NewAttributeTypeContentWizardPage extends AbstractWizardPage
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

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
        super( "NewAttributeTypeContentWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "NewAttributeTypeContentWizardPage.AttributTypeContent" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewAttributeTypeContentWizardPage.EnterAttributeTypeContent" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_ATTRIBUTE_TYPE_NEW_WIZARD ) );
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Superior and Usage Group
        Group superiorUsageGroup = new Group( composite, SWT.NONE );
        superiorUsageGroup.setText( Messages.getString( "NewAttributeTypeContentWizardPage.SuperiorAndUsage" ) ); //$NON-NLS-1$
        superiorUsageGroup.setLayout( new GridLayout( 3, false ) );
        superiorUsageGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Superior
        Label superiorLabel = new Label( superiorUsageGroup, SWT.NONE );
        superiorLabel.setText( Messages.getString( "NewAttributeTypeContentWizardPage.Superior" ) ); //$NON-NLS-1$
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
        superiorButton.setText( Messages.getString( "NewAttributeTypeContentWizardPage.Choose" ) ); //$NON-NLS-1$
        superiorButton.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );
        superiorButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
                if ( dialog.open() == Dialog.OK )
                {
                    AttributeType selectedAT = dialog.getSelectedAttributeType();
                    List<String> aliases = selectedAT.getNames();
                    if ( ( aliases != null ) && ( aliases.size() > 0 ) )
                    {
                        superiorText.setText( aliases.get( 0 ) );
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
        usageLabel.setText( Messages.getString( "NewAttributeTypeContentWizardPage.Usage" ) ); //$NON-NLS-1$
        Combo usageCombo = new Combo( superiorUsageGroup, SWT.READ_ONLY );
        usageCombo.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );
        usageComboViewer = new ComboViewer( usageCombo );
        usageComboViewer.setLabelProvider( new LabelProvider() );
        usageComboViewer.setContentProvider( new ArrayContentProvider() );
        usageComboViewer
            .setInput( new String[]
                {
                    Messages.getString( "NewAttributeTypeContentWizardPage.DirectoryOperation" ), Messages.getString( "NewAttributeTypeContentWizardPage.DistributedOperation" ), Messages.getString( "NewAttributeTypeContentWizardPage.DSAOperation" ), Messages.getString( "NewAttributeTypeContentWizardPage.UserApplications" ) } ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        usageComboViewer.setSelection( new StructuredSelection( Messages
            .getString( "NewAttributeTypeContentWizardPage.UserApplications" ) ) ); //$NON-NLS-1$

        // Syntax Group
        Group syntaxGroup = new Group( composite, SWT.NONE );
        syntaxGroup.setText( Messages.getString( "NewAttributeTypeContentWizardPage.Syntax" ) ); //$NON-NLS-1$
        syntaxGroup.setLayout( new GridLayout( 2, false ) );
        syntaxGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Syntax
        Label syntaxLabel = new Label( syntaxGroup, SWT.NONE );
        syntaxLabel.setText( Messages.getString( "NewAttributeTypeContentWizardPage.SyntaxColon" ) ); //$NON-NLS-1$
        Combo syntaxCombo = new Combo( syntaxGroup, SWT.READ_ONLY );
        syntaxCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        syntaxComboViewer = new ComboViewer( syntaxCombo );
        syntaxComboViewer.setContentProvider( new ArrayContentProvider() );
        syntaxComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof LdapSyntax )
                {
                    LdapSyntax syntax = ( LdapSyntax ) element;

                    String name = syntax.getName();
                    if ( name != null )
                    {
                        return NLS
                            .bind(
                                Messages.getString( "NewAttributeTypeContentWizardPage.NameOID" ), new String[] { name, syntax.getOid() } ); //$NON-NLS-1$
                    }
                    else
                    {
                        return NLS
                            .bind(
                                Messages.getString( "NewAttributeTypeContentWizardPage.NoneOID" ), new String[] { syntax.getOid() } ); //$NON-NLS-1$
                    }
                }

                return super.getText( element );
            }
        } );

        // Syntax Length
        Label lengthLabel = new Label( syntaxGroup, SWT.NONE );
        lengthLabel.setText( Messages.getString( "NewAttributeTypeContentWizardPage.Length" ) ); //$NON-NLS-1$
        lengthSpinner = new Spinner( syntaxGroup, SWT.BORDER );
        lengthSpinner.setIncrement( 1 );
        lengthSpinner.setMinimum( 0 );
        lengthSpinner.setMaximum( Integer.MAX_VALUE );
        GridData lengthSpinnerGridData = new GridData( SWT.NONE, SWT.NONE, false, false );
        lengthSpinnerGridData.widthHint = 42;
        lengthSpinner.setLayoutData( lengthSpinnerGridData );

        // Properties Group
        Group propertiesGroup = new Group( composite, SWT.NONE );
        propertiesGroup.setText( Messages.getString( "NewAttributeTypeContentWizardPage.Properties" ) ); //$NON-NLS-1$
        propertiesGroup.setLayout( new GridLayout() );
        propertiesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Obsolete
        new Label( composite, SWT.NONE );
        obsoleteCheckbox = new Button( propertiesGroup, SWT.CHECK );
        obsoleteCheckbox.setText( Messages.getString( "NewAttributeTypeContentWizardPage.Obsolete" ) ); //$NON-NLS-1$

        // Single value
        new Label( composite, SWT.NONE );
        singleValueCheckbox = new Button( propertiesGroup, SWT.CHECK );
        singleValueCheckbox.setText( Messages.getString( "NewAttributeTypeContentWizardPage.SingleValue" ) ); //$NON-NLS-1$

        // Collective
        new Label( composite, SWT.NONE );
        collectiveCheckbox = new Button( propertiesGroup, SWT.CHECK );
        collectiveCheckbox.setText( Messages.getString( "NewAttributeTypeContentWizardPage.Collective" ) ); //$NON-NLS-1$

        // No User Modification
        new Label( composite, SWT.NONE );
        noUserModificationCheckbox = new Button( propertiesGroup, SWT.CHECK );
        noUserModificationCheckbox
            .setText( Messages.getString( "NewAttributeTypeContentWizardPage.NoUserModifcation" ) ); //$NON-NLS-1$

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
            String none = Messages.getString( "NewAttributeTypeContentWizardPage.None" ); //$NON-NLS-1$
            syntaxes.add( none );

            // Sorting the syntaxes
            Collections.sort( syntaxes, new Comparator<Object>()
            {
                public int compare( Object o1, Object o2 )
                {
                    if ( ( o1 instanceof LdapSyntax ) && ( o2 instanceof LdapSyntax ) )
                    {
                        List<String> o1Names = ( ( LdapSyntax ) o1 ).getNames();
                        List<String> o2Names = ( ( LdapSyntax ) o2 ).getNames();

                        // Comparing the First Name
                        if ( ( o1Names != null ) && ( o2Names != null ) )
                        {
                            if ( ( o1Names.size() > 0 ) && ( o2Names.size() > 0 ) )
                            {
                                return o1Names.get( 0 ).compareToIgnoreCase( o2Names.get( 0 ) );
                            }
                            else if ( ( o1Names.size() == 0 ) && ( o2Names.size() > 0 ) )
                            {
                                return "".compareToIgnoreCase( o2Names.get( 0 ) ); //$NON-NLS-1$
                            }
                            else if ( ( o1Names.size() > 0 ) && ( o2Names.size() == 0 ) )
                            {
                                return o1Names.get( 0 ).compareToIgnoreCase( "" ); //$NON-NLS-1$
                            }
                        }
                    }
                    else if ( ( o1 instanceof String ) && ( o2 instanceof LdapSyntax ) )
                    {
                        return Integer.MIN_VALUE;
                    }
                    else if ( ( o1 instanceof LdapSyntax ) && ( o2 instanceof String ) )
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
        if ( ( superior != null ) && ( !superior.equals( "" ) ) ) //$NON-NLS-1$
        {
            if ( schemaHandler.getAttributeType( superiorText.getText() ) == null )
            {
                displayErrorMessage( Messages
                    .getString( "NewAttributeTypeContentWizardPage.ErrorSuperiorAttributeTypeNotExists" ) ); //$NON-NLS-1$
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
        if ( ( superior != null ) && ( !superior.equals( "" ) ) ) //$NON-NLS-1$
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
            if ( selectedUsage.equals( Messages.getString( "NewAttributeTypeContentWizardPage.DirectoryOperation" ) ) ) //$NON-NLS-1$
            {
                return UsageEnum.DIRECTORY_OPERATION;
            }
            else if ( selectedUsage.equals( Messages
                .getString( "NewAttributeTypeContentWizardPage.DistributedOperation" ) ) ) //$NON-NLS-1$
            {
                return UsageEnum.DISTRIBUTED_OPERATION;
            }
            else if ( selectedUsage.equals( Messages.getString( "NewAttributeTypeContentWizardPage.DSAOperation" ) ) ) //$NON-NLS-1$
            {
                return UsageEnum.DSA_OPERATION;
            }
            else if ( selectedUsage.equals( Messages.getString( "NewAttributeTypeContentWizardPage.UserApplications" ) ) ) //$NON-NLS-1$
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

        if ( selection instanceof LdapSyntax )
        {
            return ( ( LdapSyntax ) selection ).getOid();
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
