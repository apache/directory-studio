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
package org.apache.directory.studio.apacheds.configuration.editor.v150;


import java.util.List;

import org.apache.directory.studio.apacheds.configuration.editor.SaveableFormPage;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.editor.v150.dialogs.BinaryAttributeDialog;
import org.apache.directory.studio.apacheds.configuration.model.v150.ServerConfigurationV150;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class GeneralPage extends FormPage implements SaveableFormPage
{
    /** The Page ID*/
    public static final String ID = ServerConfigurationEditor.ID + ".BasicPage";

    /** The Page Title */
    private static final String TITLE = "General";

    /** The Binary Attribute List */
    private List<String> binaryAttributes;

    // UI Fields
    private Text portText;
    private Combo authenticationCombo;
    private Text principalText;
    private Text passwordText;
    private Button showPasswordCheckbox;
    private Button allowAnonymousAccessCheckbox;
    private Text maxTimeLimitText;
    private Text maxSizeLimitText;
    private Text synchPeriodText;
    private Text maxThreadsText;
    private Button enableAccesControlCheckbox;
    private Button enableNTPCheckbox;
    private Button enableKerberosCheckbox;
    private Button enableChangePasswordCheckbox;
    private Button denormalizeOpAttrCheckbox;
    private TableViewer binaryAttributesTableViewer;
    private Button binaryAttributesAddButton;
    private Button binaryAttributesEditButton;
    private Button binaryAttributesDeleteButton;


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public GeneralPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        form.setText( "General" );

        Composite parent = form.getBody();
        TableWrapLayout twl = new TableWrapLayout();
        twl.numColumns = 2;
        parent.setLayout( twl );
        FormToolkit toolkit = managedForm.getToolkit();

        createSettingsSection( parent, toolkit );
        createBinaryAttributesSection( parent, toolkit );
        createLimitsSection( parent, toolkit );
        createOptionsSection( parent, toolkit );

        initFromInput();
        addListeners();
    }


    /**
     * Creates the Settings Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Settings" );
        section.setDescription( "Set the settings of the server." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Port
        toolkit.createLabel( client, "Port:" );
        portText = toolkit.createText( client, "" );
        portText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        portText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        // Authentication
        toolkit.createLabel( client, "Authentication:" );
        authenticationCombo = new Combo( client, SWT.SIMPLE );
        authenticationCombo.setItems( new String[]
            { "Simple" } );
        authenticationCombo.setText( "Simple" );
        authenticationCombo.setEnabled( false );
        authenticationCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Principal
        toolkit.createLabel( client, "Principal:" );
        principalText = toolkit.createText( client, "" );
        principalText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Password
        toolkit.createLabel( client, "Password:" );
        passwordText = toolkit.createText( client, "" );
        passwordText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        passwordText.setEchoChar( '\u2022' );

        // Show Password
        toolkit.createLabel( client, "" );
        showPasswordCheckbox = toolkit.createButton( client, "Show password", SWT.CHECK );
        showPasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        showPasswordCheckbox.setSelection( false );
        showPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( showPasswordCheckbox.getSelection() )
                {
                    passwordText.setEchoChar( '\0' );
                }
                else
                {
                    passwordText.setEchoChar( '\u2022' );
                }
            }
        } );

        // Allow Anonymous Access
        allowAnonymousAccessCheckbox = toolkit.createButton( client, "Allow Anonymous Access", SWT.CHECK );
        allowAnonymousAccessCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
    }


    /**
     * Creates the Limits Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createLimitsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Limits" );
        section.setDescription( "Set the limits of the server." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Max. Time Limit
        toolkit.createLabel( client, "Max. Time Limit:" );
        maxTimeLimitText = toolkit.createText( client, "" );
        maxTimeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        maxTimeLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        // Max. Size Limit
        toolkit.createLabel( client, "Max. Size Limit:" );
        maxSizeLimitText = toolkit.createText( client, "" );
        maxSizeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        maxSizeLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        // Synchronization Period
        toolkit.createLabel( client, "Synchronization Period:" );
        synchPeriodText = toolkit.createText( client, "" );
        synchPeriodText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        synchPeriodText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        // Max. Threads
        toolkit.createLabel( client, "Max. Threads:" );
        maxThreadsText = toolkit.createText( client, "" );
        maxThreadsText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        maxThreadsText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
    }


    /**
     * Creates the Options Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createOptionsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Options" );
        section.setDescription( "Set the options of the server." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout();
        client.setLayout( glayout );
        section.setClient( client );

        // Enable Access Control
        enableAccesControlCheckbox = toolkit.createButton( client, "Enable Access Control", SWT.CHECK );
        enableAccesControlCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Enable NTP
        enableNTPCheckbox = toolkit.createButton( client, "Enable NTP", SWT.CHECK );
        enableNTPCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Denormalize Operational Attributes
        denormalizeOpAttrCheckbox = toolkit.createButton( client, "Denormalize Operational Attributes", SWT.CHECK );
        denormalizeOpAttrCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Enable Kerberos
        enableKerberosCheckbox = toolkit.createButton( client, "Enable Kerberos", SWT.CHECK );
        enableKerberosCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        enableKerberosCheckbox.setEnabled( false );

        // Enable Change Password
        enableChangePasswordCheckbox = toolkit.createButton( client, "Enable Change Password", SWT.CHECK );
        enableChangePasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        enableChangePasswordCheckbox.setEnabled( false );
    }


    /**
     * Creates the Options Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createBinaryAttributesSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Binary Attributes" );
        section
            .setDescription( "Set attribute type names and OID's if you want an them to be handled as binary content." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        Table binaryAttributesTable = toolkit.createTable( client, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 103;
        binaryAttributesTable.setLayoutData( gd );
        binaryAttributesTableViewer = new TableViewer( binaryAttributesTable );
        binaryAttributesTableViewer.setContentProvider( new ArrayContentProvider() );
        binaryAttributesTableViewer.setLabelProvider( new LabelProvider() );

        GridData buttonsGD = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        buttonsGD.widthHint = IDialogConstants.BUTTON_WIDTH;

        binaryAttributesAddButton = toolkit.createButton( client, "Add...", SWT.PUSH );
        binaryAttributesAddButton.setLayoutData( buttonsGD );

        binaryAttributesEditButton = toolkit.createButton( client, "Edit...", SWT.PUSH );
        binaryAttributesEditButton.setEnabled( false );
        binaryAttributesEditButton.setLayoutData( buttonsGD );

        binaryAttributesDeleteButton = toolkit.createButton( client, "Delete", SWT.PUSH );
        binaryAttributesDeleteButton.setEnabled( false );
        binaryAttributesDeleteButton.setLayoutData( buttonsGD );
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        ServerConfigurationV150 configuration = ( ServerConfigurationV150 ) ( ( ServerConfigurationEditor ) getEditor() )
            .getServerConfiguration();

        binaryAttributes = configuration.getBinaryAttributes();
        binaryAttributesTableViewer.setInput( binaryAttributes );

        // Port
        portText.setText( "" + configuration.getPort() );

        // Principal
        String principal = configuration.getPrincipal();
        if ( principal != null )
        {
            principalText.setText( principal );
        }

        // Password
        String password = configuration.getPassword();
        if ( password != null )
        {
            passwordText.setText( password );
        }

        // Allow Anonymous Access
        allowAnonymousAccessCheckbox.setSelection( configuration.isAllowAnonymousAccess() );

        // Max Time Limit
        maxTimeLimitText.setText( "" + configuration.getMaxTimeLimit() );

        // Max Size Limit
        maxSizeLimitText.setText( "" + configuration.getMaxSizeLimit() );

        // Synchronization Period
        synchPeriodText.setText( "" + configuration.getSynchronizationPeriod() );

        // Max Threads
        maxThreadsText.setText( "" + configuration.getMaxThreads() );

        // Enable Access Control
        enableAccesControlCheckbox.setSelection( configuration.isEnableAccessControl() );

        // Enable NTP
        enableNTPCheckbox.setSelection( configuration.isEnableNTP() );

        // Enable Kerberos
        enableKerberosCheckbox.setSelection( configuration.isEnableKerberos() );

        // Enable Change Password
        enableChangePasswordCheckbox.setSelection( configuration.isEnableChangePassword() );

        // Denormalize Op Attr
        denormalizeOpAttrCheckbox.setSelection( configuration.isDenormalizeOpAttr() );
    }


    /**
     * Add listeners to UI fields.
     */
    private void addListeners()
    {
        // The Modify Listener
        ModifyListener modifyListener = new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        };

        //  The Selection Listener
        SelectionListener selectionListener = new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setEditorDirty();
            }
        };

        // The ISelectionChangedListener for the Binary Attributes Table
        ISelectionChangedListener binaryAttributesTableViewerListener = new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                binaryAttributesEditButton.setEnabled( !event.getSelection().isEmpty() );
                binaryAttributesDeleteButton.setEnabled( !event.getSelection().isEmpty() );
            }
        };

        // The IDoubleClickListener for the Binary Attributes Table
        IDoubleClickListener binaryAttributesTableViewerDoubleClickListener = new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                editSelectedBinaryAttribute();
            }
        };

        // The SelectionListener for the Binary Attributes Add Button
        SelectionListener binaryAttributesAddButtonListener = new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                BinaryAttributeDialog dialog = new BinaryAttributeDialog( "" );
                if ( Dialog.OK == dialog.open() && dialog.isDirty() )
                {
                    String newAttribute = dialog.getAttribute();
                    if ( newAttribute != null && !"".equals( newAttribute )
                        && !binaryAttributes.contains( newAttribute ) )
                    {
                        binaryAttributes.add( newAttribute );

                        binaryAttributesTableViewer.refresh();
                        setEditorDirty();
                    }
                }
            }
        };

        // The SelectionListener for the Binary Attributes Edit Button
        SelectionListener binaryAttributesEditButtonListener = new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                editSelectedBinaryAttribute();
            }
        };

        // The SelectionListener for the Binary Attributes Delete Button
        SelectionListener binaryAttributesDeleteButtonListener = new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                StructuredSelection selection = ( StructuredSelection ) binaryAttributesTableViewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    String attribute = ( String ) selection.getFirstElement();
                    binaryAttributes.remove( attribute );

                    binaryAttributesTableViewer.refresh();
                    setEditorDirty();
                }
            }
        };

        portText.addModifyListener( modifyListener );
        authenticationCombo.addModifyListener( modifyListener );
        principalText.addModifyListener( modifyListener );
        passwordText.addModifyListener( modifyListener );
        allowAnonymousAccessCheckbox.addSelectionListener( selectionListener );
        maxTimeLimitText.addModifyListener( modifyListener );
        maxSizeLimitText.addModifyListener( modifyListener );
        synchPeriodText.addModifyListener( modifyListener );
        maxThreadsText.addModifyListener( modifyListener );
        enableAccesControlCheckbox.addSelectionListener( selectionListener );
        enableNTPCheckbox.addSelectionListener( selectionListener );
        enableKerberosCheckbox.addSelectionListener( selectionListener );
        enableChangePasswordCheckbox.addSelectionListener( selectionListener );
        denormalizeOpAttrCheckbox.addSelectionListener( selectionListener );
        binaryAttributesTableViewer.addSelectionChangedListener( binaryAttributesTableViewerListener );
        binaryAttributesTableViewer.addDoubleClickListener( binaryAttributesTableViewerDoubleClickListener );
        binaryAttributesAddButton.addSelectionListener( binaryAttributesAddButtonListener );
        binaryAttributesEditButton.addSelectionListener( binaryAttributesEditButtonListener );
        binaryAttributesDeleteButton.addSelectionListener( binaryAttributesDeleteButtonListener );
    }


    /**
     * Opens a Binary Attribute Dialog with the selected Attribute Value Object in the
     * Binary Attributes Table Viewer.
     */
    private void editSelectedBinaryAttribute()
    {
        StructuredSelection selection = ( StructuredSelection ) binaryAttributesTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            String oldAttribute = ( String ) selection.getFirstElement();

            BinaryAttributeDialog dialog = new BinaryAttributeDialog( oldAttribute );
            if ( Dialog.OK == dialog.open() && dialog.isDirty() )
            {
                binaryAttributes.remove( oldAttribute );

                String newAttribute = dialog.getAttribute();
                if ( newAttribute != null && !"".equals( newAttribute ) && !binaryAttributes.contains( newAttribute ) )
                {
                    binaryAttributes.add( newAttribute );
                }

                binaryAttributesTableViewer.refresh();
                setEditorDirty();
            }
        }
    }


    /**
     * Sets the Editor as dirty.
     */
    private void setEditorDirty()
    {
        ( ( ServerConfigurationEditor ) getEditor() ).setDirty( true );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.editor.SavableWizardPage#save()
     */
    public void save()
    {
        ServerConfigurationV150 serverConfiguration = ( ServerConfigurationV150 ) ( ( ServerConfigurationEditor ) getEditor() )
            .getServerConfiguration();

        serverConfiguration.setPort( Integer.parseInt( portText.getText() ) );
        serverConfiguration.setPrincipal( principalText.getText() );
        serverConfiguration.setPassword( passwordText.getText() );
        serverConfiguration.setAllowAnonymousAccess( allowAnonymousAccessCheckbox.getSelection() );
        serverConfiguration.setMaxTimeLimit( Integer.parseInt( maxTimeLimitText.getText() ) );
        serverConfiguration.setMaxSizeLimit( Integer.parseInt( maxSizeLimitText.getText() ) );
        serverConfiguration.setSynchronizationPeriod( Long.parseLong( synchPeriodText.getText() ) );
        serverConfiguration.setMaxThreads( Integer.parseInt( maxThreadsText.getText() ) );
        serverConfiguration.setEnableAccessControl( enableAccesControlCheckbox.getSelection() );
        serverConfiguration.setEnableNTP( enableNTPCheckbox.getSelection() );
        serverConfiguration.setEnableKerberos( enableKerberosCheckbox.getSelection() );
        serverConfiguration.setEnableChangePassword( enableChangePasswordCheckbox.getSelection() );
        serverConfiguration.setDenormalizeOpAttr( denormalizeOpAttrCheckbox.getSelection() );
    }

}
