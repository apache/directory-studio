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
package org.apache.directory.studio.apacheds.configuration.editor.v152;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.configuration.editor.SavableFormPage;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.editor.v152.dialogs.BinaryAttributeDialog;
import org.apache.directory.studio.apacheds.configuration.model.v152.ServerConfigurationV152;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
public class GeneralPage extends FormPage implements SavableFormPage
{
    /** The Page ID*/
    public static final String ID = ServerConfigurationEditor.ID + ".V152.GeneralPage";

    /** The Page Title */
    private static final String TITLE = "General";

    /** The Binary Attribute List */
    private List<String> binaryAttributes;

    // UI Fields
    private Text principalText;
    private Text passwordText;
    private Button showPasswordCheckbox;
    private Button allowAnonymousAccessCheckbox;
    private Text maxTimeLimitText;
    private Text maxSizeLimitText;
    private Text synchPeriodText;
    private Text maxThreadsText;
    private Button enableAccesControlCheckbox;
    private Button enableKerberosCheckbox;
    private Button enableChangePasswordCheckbox;
    private Button denormalizeOpAttrCheckbox;
    private TableViewer binaryAttributesTableViewer;
    private Button binaryAttributesAddButton;
    private Button binaryAttributesEditButton;
    private Button binaryAttributesDeleteButton;
    private Button enableLdapCheckbox;
    private Text ldapPortText;
    private Button enableLdapsCheckbox;
    private Text ldapsPortText;
    private Text kerberosPortText;
    private Button enableNtpCheckbox;
    private Text ntpPortText;
    private Button enableDnsCheckbox;
    private Text dnsPortText;
    private Text changePasswordPortText;

    private CheckboxTableViewer supportedMechanismsTableViewer;
    private Button selectAllSupportedMechanismsButton;
    private Button deselectAllSupportedMechanismsButton;


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

        Composite leftComposite = toolkit.createComposite( parent );
        GridLayout leftCompositeGridLayout = new GridLayout();
        leftCompositeGridLayout.marginHeight = leftCompositeGridLayout.marginWidth = 0;
        leftComposite.setLayout( leftCompositeGridLayout );
        TableWrapData leftCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        leftCompositeTableWrapData.grabHorizontal = true;
        leftComposite.setLayoutData( leftCompositeTableWrapData );

        createAdministratorSettingsSection( leftComposite, toolkit );
        createProtocolsSection( leftComposite, toolkit );
        createSupportedAuthenticationMechanismsSection( leftComposite, toolkit );

        Composite rightComposite = toolkit.createComposite( parent );
        GridLayout rightCompositeGridLayout = new GridLayout();
        rightCompositeGridLayout.marginHeight = rightCompositeGridLayout.marginWidth = 0;
        rightComposite.setLayout( rightCompositeGridLayout );
        TableWrapData rightCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        rightCompositeTableWrapData.grabHorizontal = true;
        rightComposite.setLayoutData( rightCompositeTableWrapData );

        createBinaryAttributesSection( rightComposite, toolkit );
        createLimitsSection( rightComposite, toolkit );
        createOptionsSection( rightComposite, toolkit );

        initFromInput();
        addListeners();
    }


    /**
     * Creates the Administrator Settings Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createAdministratorSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Administrator settings" );
        section.setDescription( "Set the settings about the administrator of the server." );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

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
        showPasswordCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );
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
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Limits" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Options" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        client.setLayout( new GridLayout() );
        section.setClient( client );

        // Allow Anonymous Access
        allowAnonymousAccessCheckbox = toolkit.createButton( client, "Allow Anonymous Access", SWT.CHECK );
        allowAnonymousAccessCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );

        // Enable Access Control
        enableAccesControlCheckbox = toolkit.createButton( client, "Enable Access Control", SWT.CHECK );
        enableAccesControlCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );

        // Denormalize Operational Attributes
        denormalizeOpAttrCheckbox = toolkit.createButton( client, "Denormalize Operational Attributes", SWT.CHECK );
        denormalizeOpAttrCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );
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
        section.setDescription( "Set attribute type names and OID's if you want them to be handled as binary content." );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
     * Creates the Supported Authentication Mechanisms Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createSupportedAuthenticationMechanismsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Supported Authentication Mechanisms" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        Table supportedMechanismsTable = toolkit.createTable( client, SWT.CHECK );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 76;
        supportedMechanismsTable.setLayoutData( gd );
        supportedMechanismsTableViewer = new CheckboxTableViewer( supportedMechanismsTable );
        supportedMechanismsTableViewer.setContentProvider( new ArrayContentProvider() );
        supportedMechanismsTableViewer.setInput( new String[]
            { "SIMPLE", "CRAM-MD5", "DIGEST-MD5", "GSSAPI" } );

        selectAllSupportedMechanismsButton = toolkit.createButton( client, "Select All", SWT.PUSH );
        selectAllSupportedMechanismsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        deselectAllSupportedMechanismsButton = toolkit.createButton( client, "Deselect All", SWT.PUSH );
        deselectAllSupportedMechanismsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
    }


    /**
     * Creates the Protocols Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createProtocolsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Protocols" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        client.setLayout( new GridLayout( 2, true ) );
        section.setClient( client );

        // LDAP
        Composite ldapProtocolComposite = createProtocolComposite( toolkit, client );
        enableLdapCheckbox = toolkit.createButton( ldapProtocolComposite, "Enable LDAP", SWT.CHECK );
        enableLdapCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false, 3, 1 ) );
        toolkit.createLabel( ldapProtocolComposite, "    " );
        toolkit.createLabel( ldapProtocolComposite, "Port:" );
        ldapPortText = createPortText( toolkit, ldapProtocolComposite );

        // LDAPS
        Composite ldapsProtocolComposite = createProtocolComposite( toolkit, client );
        enableLdapsCheckbox = toolkit.createButton( ldapsProtocolComposite, "Enable LDAPS", SWT.CHECK );
        enableLdapsCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false, 3, 1 ) );
        toolkit.createLabel( ldapsProtocolComposite, "    " );
        toolkit.createLabel( ldapsProtocolComposite, "Port:" );
        ldapsPortText = createPortText( toolkit, ldapsProtocolComposite );

        // Kerberos
        Composite kerberosProtocolComposite = createProtocolComposite( toolkit, client );
        enableKerberosCheckbox = toolkit.createButton( kerberosProtocolComposite, "Enable Kerberos", SWT.CHECK );
        enableKerberosCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false, 3, 1 ) );
        toolkit.createLabel( kerberosProtocolComposite, "    " );
        toolkit.createLabel( kerberosProtocolComposite, "Port:" );
        kerberosPortText = createPortText( toolkit, kerberosProtocolComposite );

        // NTP
        Composite ntpProtocolComposite = createProtocolComposite( toolkit, client );
        enableNtpCheckbox = toolkit.createButton( ntpProtocolComposite, "Enable NTP", SWT.CHECK );
        enableNtpCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false, 3, 1 ) );
        toolkit.createLabel( ntpProtocolComposite, "    " );
        toolkit.createLabel( ntpProtocolComposite, "Port:" );
        ntpPortText = createPortText( toolkit, ntpProtocolComposite );

        // DNS
        Composite dnsProtocolComposite = createProtocolComposite( toolkit, client );
        enableDnsCheckbox = toolkit.createButton( dnsProtocolComposite, "Enable DNS", SWT.CHECK );
        enableDnsCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false, 3, 1 ) );
        toolkit.createLabel( dnsProtocolComposite, "    " );
        toolkit.createLabel( dnsProtocolComposite, "Port:" );
        dnsPortText = createPortText( toolkit, dnsProtocolComposite );

        // Change Password
        Composite changePasswordProtocolComposite = createProtocolComposite( toolkit, client );
        enableChangePasswordCheckbox = toolkit.createButton( changePasswordProtocolComposite, "Enable Change Password",
            SWT.CHECK );
        enableChangePasswordCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false, 3, 1 ) );
        toolkit.createLabel( changePasswordProtocolComposite, "    " );
        toolkit.createLabel( changePasswordProtocolComposite, "Port:" );
        changePasswordPortText = createPortText( toolkit, changePasswordProtocolComposite );
    }


    /**
     * Creates a Protocol Composite : a Composite composed of a GridLayout with
     * 3 columns and marginHeight and marginWidth set to 0.
     *
     * @param toolkit
     *      the toolkit
     * @param parent
     *      the parent
     * @return
     *      a Protocol Composite
     */
    private Composite createProtocolComposite( FormToolkit toolkit, Composite parent )
    {
        Composite protocolComposite = toolkit.createComposite( parent );
        GridLayout protocolGridLayout = new GridLayout( 3, false );
        protocolGridLayout.marginHeight = protocolGridLayout.marginWidth = 0;
        toolkit.paintBordersFor( protocolComposite );
        protocolComposite.setLayout( protocolGridLayout );

        return protocolComposite;
    }


    /**
     * Creates a Text that can be used to enter a port number.
     *
     * @param toolkit
     *      the toolkit
     * @param parent
     *      the parent
     * @return
     *      a Text that can be used to enter a port number
     */
    private Text createPortText( FormToolkit toolkit, Composite parent )
    {
        Text portText = toolkit.createText( parent, "" );
        GridData gd = new GridData( SWT.NONE, SWT.NONE, false, false );
        gd.widthHint = 42;
        portText.setLayoutData( gd );
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
        portText.setTextLimit( 5 );

        return portText;
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        ServerConfigurationV152 configuration = ( ServerConfigurationV152 ) ( ( ServerConfigurationEditor ) getEditor() )
            .getServerConfiguration();

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

        // Binary Attributes
        binaryAttributes = configuration.getBinaryAttributes();
        binaryAttributesTableViewer.setInput( binaryAttributes );

        // LDAP Protocol
        enableLdapCheckbox.setSelection( true );
        ldapPortText.setEnabled( enableLdapCheckbox.getSelection() );
        ldapPortText.setText( "" + configuration.getLdapPort() );

        // LDAPS Protocol
        enableLdapsCheckbox.setSelection( configuration.isEnableLdaps() );
        ldapsPortText.setEnabled( enableLdapsCheckbox.getSelection() );
        ldapsPortText.setText( "" + configuration.getLdapsPort() );

        // Kerberos Protocol
        enableKerberosCheckbox.setSelection( configuration.isEnableKerberos() );
        kerberosPortText.setEnabled( enableKerberosCheckbox.getSelection() );
        kerberosPortText.setText( "" + configuration.getKerberosPort() );

        // NTP Protocol
        enableNtpCheckbox.setSelection( configuration.isEnableNtp() );
        ntpPortText.setEnabled( enableNtpCheckbox.getSelection() );
        ntpPortText.setText( "" + configuration.getNtpPort() );

        // DNS Protocol
        enableDnsCheckbox.setSelection( configuration.isEnableDns() );
        dnsPortText.setEnabled( enableDnsCheckbox.getSelection() );
        dnsPortText.setText( "" + configuration.getDnsPort() );

        // Change Password Protocol
        enableChangePasswordCheckbox.setSelection( configuration.isEnableChangePassword() );
        changePasswordPortText.setEnabled( enableChangePasswordCheckbox.getSelection() );
        changePasswordPortText.setText( "" + configuration.getChangePasswordPort() );

        // Max Time Limit
        maxTimeLimitText.setText( "" + configuration.getMaxTimeLimit() );

        // Max Size Limit
        maxSizeLimitText.setText( "" + configuration.getMaxSizeLimit() );

        // Synchronization Period
        synchPeriodText.setText( "" + configuration.getSynchronizationPeriod() );

        // Max Threads
        maxThreadsText.setText( "" + configuration.getMaxThreads() );

        supportedMechanismsTableViewer.setCheckedElements( configuration.getSupportedMechanisms().toArray() );

        // Allow Anonymous Access
        allowAnonymousAccessCheckbox.setSelection( configuration.isAllowAnonymousAccess() );

        // Enable Access Control
        enableAccesControlCheckbox.setSelection( configuration.isEnableAccessControl() );

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

        selectAllSupportedMechanismsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                supportedMechanismsTableViewer.setAllChecked( true );
            }
        } );

        deselectAllSupportedMechanismsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                supportedMechanismsTableViewer.setAllChecked( false );
            }
        } );

        principalText.addModifyListener( modifyListener );
        passwordText.addModifyListener( modifyListener );

        enableLdapCheckbox.addSelectionListener( selectionListener );
        enableLdapCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                ldapPortText.setEnabled( enableLdapCheckbox.getSelection() );
            }
        } );
        ldapPortText.addModifyListener( modifyListener );
        enableLdapsCheckbox.addSelectionListener( selectionListener );
        enableLdapsCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                ldapsPortText.setEnabled( enableLdapsCheckbox.getSelection() );
            }
        } );
        ldapsPortText.addModifyListener( modifyListener );
        enableKerberosCheckbox.addSelectionListener( selectionListener );
        enableKerberosCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                kerberosPortText.setEnabled( enableKerberosCheckbox.getSelection() );
            }
        } );
        kerberosPortText.addModifyListener( modifyListener );
        enableNtpCheckbox.addSelectionListener( selectionListener );
        enableNtpCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                ntpPortText.setEnabled( enableNtpCheckbox.getSelection() );
            }
        } );
        ntpPortText.addModifyListener( modifyListener );
        enableDnsCheckbox.addSelectionListener( selectionListener );
        enableDnsCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                dnsPortText.setEnabled( enableDnsCheckbox.getSelection() );
            }
        } );
        dnsPortText.addModifyListener( modifyListener );
        enableChangePasswordCheckbox.addSelectionListener( selectionListener );
        enableChangePasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                changePasswordPortText.setEnabled( enableChangePasswordCheckbox.getSelection() );
            }
        } );
        changePasswordPortText.addModifyListener( modifyListener );

        supportedMechanismsTableViewer.addCheckStateListener( new ICheckStateListener()
        {
            public void checkStateChanged( CheckStateChangedEvent event )
            {
                setEditorDirty();
            }
        } );

        selectAllSupportedMechanismsButton.addSelectionListener( selectionListener );
        deselectAllSupportedMechanismsButton.addSelectionListener( selectionListener );

        binaryAttributesTableViewer.addSelectionChangedListener( binaryAttributesTableViewerListener );
        binaryAttributesTableViewer.addDoubleClickListener( binaryAttributesTableViewerDoubleClickListener );
        binaryAttributesAddButton.addSelectionListener( binaryAttributesAddButtonListener );
        binaryAttributesEditButton.addSelectionListener( binaryAttributesEditButtonListener );
        binaryAttributesDeleteButton.addSelectionListener( binaryAttributesDeleteButtonListener );

        maxTimeLimitText.addModifyListener( modifyListener );
        maxSizeLimitText.addModifyListener( modifyListener );
        synchPeriodText.addModifyListener( modifyListener );
        maxThreadsText.addModifyListener( modifyListener );

        allowAnonymousAccessCheckbox.addSelectionListener( selectionListener );
        enableAccesControlCheckbox.addSelectionListener( selectionListener );
        denormalizeOpAttrCheckbox.addSelectionListener( selectionListener );
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
        ServerConfigurationV152 configuration = ( ServerConfigurationV152 ) ( ( ServerConfigurationEditor ) getEditor() )
            .getServerConfiguration();

        configuration.setPrincipal( principalText.getText() );
        configuration.setPassword( passwordText.getText() );

        configuration.setBinaryAttributes( binaryAttributes );

        configuration.setLdapPort( Integer.parseInt( ldapPortText.getText() ) );
        configuration.setEnableLdaps( enableLdapsCheckbox.getSelection() );
        configuration.setLdapsPort( Integer.parseInt( ldapsPortText.getText() ) );
        configuration.setEnableKerberos( enableKerberosCheckbox.getSelection() );
        configuration.setKerberosPort( Integer.parseInt( kerberosPortText.getText() ) );
        configuration.setEnableNtp( enableNtpCheckbox.getSelection() );
        configuration.setNtpPort( Integer.parseInt( ntpPortText.getText() ) );
        configuration.setEnableDns( enableDnsCheckbox.getSelection() );
        configuration.setDnsPort( Integer.parseInt( dnsPortText.getText() ) );
        configuration.setEnableChangePassword( enableChangePasswordCheckbox.getSelection() );
        configuration.setChangePasswordPort( Integer.parseInt( changePasswordPortText.getText() ) );

        configuration.setMaxTimeLimit( Integer.parseInt( maxTimeLimitText.getText() ) );
        configuration.setMaxSizeLimit( Integer.parseInt( maxSizeLimitText.getText() ) );
        configuration.setSynchronizationPeriod( Long.parseLong( synchPeriodText.getText() ) );
        configuration.setMaxThreads( Integer.parseInt( maxThreadsText.getText() ) );

        List<String> supportedMechanismsList = new ArrayList<String>();
        for ( Object supportedMechanism : supportedMechanismsTableViewer.getCheckedElements() )
        {
            supportedMechanismsList.add( ( String ) supportedMechanism );
        }
        configuration.setSupportedMechanisms( supportedMechanismsList );

        configuration.setAllowAnonymousAccess( allowAnonymousAccessCheckbox.getSelection() );
        configuration.setEnableAccessControl( enableAccesControlCheckbox.getSelection() );
        configuration.setDenormalizeOpAttr( denormalizeOpAttrCheckbox.getSelection() );
    }
}
