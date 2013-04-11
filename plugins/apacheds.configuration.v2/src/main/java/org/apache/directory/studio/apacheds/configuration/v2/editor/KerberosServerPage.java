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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.config.beans.ChangePasswordServerBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.InterceptorBean;
import org.apache.directory.server.config.beans.KdcServerBean;
import org.apache.directory.server.config.beans.TransportBean;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class KerberosServerPage extends ServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = KerberosServerPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "KerberosServerPage.KerberosServer" ); //$NON-NLS-1$

    /** The encryption types supported by ApacheDS */
    private static final EncryptionType[] SUPPORTED_ENCRYPTION_TYPES = new EncryptionType[]
        {
            EncryptionType.DES_CBC_MD5,
            EncryptionType.DES3_CBC_SHA1_KD,
            EncryptionType.AES128_CTS_HMAC_SHA1_96,
            EncryptionType.AES256_CTS_HMAC_SHA1_96,
            EncryptionType.RC4_HMAC
    };

    // UI Controls
    private Button enableKerberosCheckbox;
    private Text kerberosPortText;
    private Button enableChangePasswordCheckbox;
    private Text changePasswordPortText;
    private Text primaryKdcRealmText;
    private Text kdcSearchBaseDnText;
    private CheckboxTableViewer encryptionTypesTableViewer;
    private Button verifyBodyChecksumCheckbox;
    private Button allowEmptyAddressesCheckbox;
    private Button allowForwardableAddressesCheckbox;
    private Button requirePreAuthByEncryptedTimestampCheckbox;
    private Button allowPostdatedTicketsCheckbox;
    private Button allowRenewableTicketsCheckbox;
    private Text maximumRenewableLifetimeText;
    private Text maximumTicketLifetimeText;
    private Text allowableClockSkewText;

    // UI Controls Listeners
    private SelectionAdapter enableKerberosCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            enableKerberosServer( getDirectoryServiceBean(), enableKerberosCheckbox.getSelection() );

            setEnabled( kerberosPortText, enableKerberosCheckbox.getSelection() );
        }
    };
    private ModifyListener kerberosPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getKdcServerTransportBean().setSystemPort( Integer.parseInt( kerberosPortText.getText() ) );
        }
    };
    private SelectionAdapter enableChangePasswordCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getChangePasswordServerBean().setEnabled( enableChangePasswordCheckbox.getSelection() );
            setEnabled( changePasswordPortText, enableChangePasswordCheckbox.getSelection() );
        }
    };
    private ModifyListener changePasswordPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getChangePasswordServerTransportBean().setSystemPort(
                Integer.parseInt( changePasswordPortText.getText() ) );
        }
    };
    private ModifyListener primaryKdcRealmTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getKdcServerBean().setKrbPrimaryRealm( primaryKdcRealmText.getText() );
        }
    };
    private ModifyListener kdcSearchBaseDnTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String searchBaseDnValue = kdcSearchBaseDnText.getText();

            try
            {
                Dn searchBaseDn = new Dn( searchBaseDnValue );
                getKdcServerBean().setSearchBaseDn( searchBaseDn );
            }
            catch ( LdapInvalidDnException e1 )
            {
                // Stay silent
            }
        }
    };
    private ICheckStateListener encryptionTypesTableViewerListener = new ICheckStateListener()
    {
        public void checkStateChanged( CheckStateChangedEvent event )
        {
            // Checking if the last encryption type is being unchecked
            if ( ( getKdcServerBean().getKrbEncryptionTypes().size() == 1 ) && ( event.getChecked() == false ) )
            {
                // Displaying an error to the user
                CommonUIUtils.openErrorDialog( Messages
                    .getString( "KerberosServerPage.AtLeastOneEncryptionTypeMustBeSelected" ) ); //$NON-NLS-1$

                // Reverting the current checked state
                encryptionTypesTableViewer.setChecked( event.getElement(), !event.getChecked() );

                // Exiting
                return;
            }

            // Setting the editor as dirty
            setEditorDirty();

            // Clearing previous encryption types
            getKdcServerBean().getKrbEncryptionTypes().clear();

            // Getting all selected encryption types
            Object[] selectedEncryptionTypeObjects = encryptionTypesTableViewer.getCheckedElements();

            // Adding each encryption type
            for ( Object encryptionTypeObject : selectedEncryptionTypeObjects )
            {
                if ( encryptionTypeObject instanceof EncryptionType )
                {
                    EncryptionType encryptionType = ( EncryptionType ) encryptionTypeObject;

                    getKdcServerBean().addKrbEncryptionTypes( encryptionType.getName() );
                }
            }
        }
    };
    private SelectionAdapter verifyBodyChecksumCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getKdcServerBean().setKrbBodyChecksumVerified( verifyBodyChecksumCheckbox.getSelection() );
        }
    };
    private SelectionAdapter allowEmptyAddressesCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getKdcServerBean().setKrbEmptyAddressesAllowed( allowEmptyAddressesCheckbox.getSelection() );
        }
    };
    private SelectionAdapter allowForwardableAddressesCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getKdcServerBean().setKrbForwardableAllowed( allowForwardableAddressesCheckbox.getSelection() );
        }
    };
    private SelectionAdapter requirePreAuthByEncryptedTimestampCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getKdcServerBean().setKrbPaEncTimestampRequired(
                requirePreAuthByEncryptedTimestampCheckbox.getSelection() );
        }
    };
    private SelectionAdapter allowPostdatedTicketsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getKdcServerBean().setKrbPostdatedAllowed( allowPostdatedTicketsCheckbox.getSelection() );
        }
    };
    private SelectionAdapter allowRenewableTicketsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getKdcServerBean().setKrbRenewableAllowed( allowRenewableTicketsCheckbox.getSelection() );
        }
    };
    private ModifyListener maximumRenewableLifetimeTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getKdcServerBean()
                .setKrbMaximumRenewableLifetime( Long.parseLong( maximumRenewableLifetimeText.getText() ) );
        }
    };
    private ModifyListener maximumTicketLifetimeTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getKdcServerBean().setKrbMaximumTicketLifetime( Long.parseLong( maximumTicketLifetimeText.getText() ) );
        }
    };
    private ModifyListener allowableClockSkewTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getKdcServerBean().setKrbAllowableClockSkew( Long.parseLong( allowableClockSkewText.getText() ) );
        }
    };


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public KerberosServerPage( ServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        TableWrapLayout twl = new TableWrapLayout();
        twl.numColumns = 2;
        parent.setLayout( twl );

        // Left Composite
        Composite leftComposite = toolkit.createComposite( parent );
        leftComposite.setLayout( new GridLayout() );
        TableWrapData leftCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        leftCompositeTableWrapData.grabHorizontal = true;
        leftComposite.setLayoutData( leftCompositeTableWrapData );

        // Right Composite
        Composite rightComposite = toolkit.createComposite( parent );
        rightComposite.setLayout( new GridLayout() );
        TableWrapData rightCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        rightCompositeTableWrapData.grabHorizontal = true;
        rightComposite.setLayoutData( rightCompositeTableWrapData );

        // Creating the sections
        createKerberosServerSection( toolkit, leftComposite );
        createKerberosSettingsSection( toolkit, leftComposite );
        createTicketSettingsSection( toolkit, rightComposite );

        // Refreshing the UI
        refreshUI();
    }


    /**
     * Creates the Kerberos Server section.
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createKerberosServerSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "KerberosServerPage.KerberosServer" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 4, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Enable Kerberos Server Checkbox
        enableKerberosCheckbox = toolkit.createButton( composite,
            Messages.getString( "KerberosServerPage.EnableKerberosServer" ), SWT.CHECK ); //$NON-NLS-1$
        enableKerberosCheckbox
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );

        // Kerberos Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "KerberosServerPage.Port" ) ); //$NON-NLS-1$
        kerberosPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "60088" ); //$NON-NLS-1$

        // Enable Change Password Server Checkbox
        enableChangePasswordCheckbox = toolkit.createButton( composite,
            Messages.getString( "KerberosServerPage.EnableKerberosChangePassword" ), //$NON-NLS-1$
            SWT.CHECK );
        enableChangePasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );

        // Change Password Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "KerberosServerPage.Port" ) ); //$NON-NLS-1$
        changePasswordPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "60464" ); //$NON-NLS-1$
    }


    /**
     * Creates the Kerberos Settings section
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createKerberosSettingsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "KerberosServerPage.KerberosSettings" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // SASL Principal Text
        toolkit.createLabel( composite, Messages.getString( "KerberosServerPage.PrimaryKdcRealm" ) ); //$NON-NLS-1$
        primaryKdcRealmText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( primaryKdcRealmText, new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslPrincipalLabel = createDefaultValueLabel( toolkit, composite, "EXAMPLE.COM" ); //$NON-NLS-1$
        defaultSaslPrincipalLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Search Base Dn Text
        toolkit.createLabel( composite, Messages.getString( "KerberosServerPage.SearchBaseDn" ) ); //$NON-NLS-1$
        kdcSearchBaseDnText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( kdcSearchBaseDnText, new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslSearchBaseDnLabel = createDefaultValueLabel( toolkit, composite, "ou=users,dc=example,dc=com" ); //$NON-NLS-1$
        defaultSaslSearchBaseDnLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Encryption Types Table Viewer
        Label encryptionTypesLabel = toolkit.createLabel( composite,
            Messages.getString( "KerberosServerPage.EncryptionTypes" ) ); //$NON-NLS-1$
        encryptionTypesLabel.setLayoutData( new GridData( SWT.BEGINNING, SWT.TOP, false, false ) );
        encryptionTypesTableViewer = new CheckboxTableViewer( new Table( composite, SWT.BORDER | SWT.CHECK ) );
        encryptionTypesTableViewer.setContentProvider( new ArrayContentProvider() );
        encryptionTypesTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof EncryptionType )
                {
                    EncryptionType encryptionType = ( EncryptionType ) element;

                    return encryptionType.getName().toUpperCase();
                }

                return super.getText( element );
            }
        } );
        encryptionTypesTableViewer.setInput( SUPPORTED_ENCRYPTION_TYPES );
        GridData encryptionTypesTableViewerGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        encryptionTypesTableViewerGridData.heightHint = 60;
        encryptionTypesTableViewer.getControl().setLayoutData( encryptionTypesTableViewerGridData );
    }


    /**
     * Creates the Tickets Settings section
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createTicketSettingsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "KerberosServerPage.TicketSettings" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout layout = new GridLayout( 2, false );
        composite.setLayout( layout );
        section.setClient( composite );

        // Verify Body Checksum Checkbox
        verifyBodyChecksumCheckbox = toolkit.createButton( composite,
            Messages.getString( "KerberosServerPage.VerifyBodyChecksum" ), SWT.CHECK ); //$NON-NLS-1$
        verifyBodyChecksumCheckbox
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, layout.numColumns, 1 ) );

        // Allow Empty Addresse Checkbox
        allowEmptyAddressesCheckbox = toolkit.createButton( composite,
            Messages.getString( "KerberosServerPage.AllowEmptyAddresses" ), SWT.CHECK ); //$NON-NLS-1$
        allowEmptyAddressesCheckbox
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, layout.numColumns, 1 ) );

        // Allow Forwardable Addresses Checkbox
        allowForwardableAddressesCheckbox = toolkit.createButton( composite,
            Messages.getString( "KerberosServerPage.AllowForwadableAddresses" ), //$NON-NLS-1$
            SWT.CHECK );
        allowForwardableAddressesCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            layout.numColumns, 1 ) );

        // Require Pre-Authentication By Encrypted Timestamp Checkbox
        requirePreAuthByEncryptedTimestampCheckbox = toolkit.createButton( composite,
            Messages.getString( "KerberosServerPage.RequirePreAuthentication" ), SWT.CHECK ); //$NON-NLS-1$
        requirePreAuthByEncryptedTimestampCheckbox
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, layout.numColumns, 1 ) );

        // Allow Postdated Tickets Checkbox
        allowPostdatedTicketsCheckbox = toolkit.createButton( composite,
            Messages.getString( "KerberosServerPage.AllowPostdatedTickets" ), SWT.CHECK ); //$NON-NLS-1$
        allowPostdatedTicketsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, layout.numColumns,
            1 ) );

        // Allow Renewable Tickets Checkbox
        allowRenewableTicketsCheckbox = toolkit.createButton( composite,
            Messages.getString( "KerberosServerPage.AllowRenewableTickets" ), SWT.CHECK ); //$NON-NLS-1$
        allowRenewableTicketsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, layout.numColumns,
            1 ) );

        // Max Renewable Lifetime Text
        toolkit.createLabel( composite, Messages.getString( "KerberosServerPage.MaxRenewableLifetime" ) ); //$NON-NLS-1$
        maximumRenewableLifetimeText = createIntegerText( toolkit, composite );
        setGridDataWithDefaultWidth( maximumRenewableLifetimeText, new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Max Ticket Lifetime Text
        toolkit.createLabel( composite, Messages.getString( "KerberosServerPage.MaxTicketLifetime" ) ); //$NON-NLS-1$
        maximumTicketLifetimeText = createIntegerText( toolkit, composite );
        setGridDataWithDefaultWidth( maximumTicketLifetimeText, new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Allowable Clock Skew Text
        toolkit.createLabel( composite, Messages.getString( "KerberosServerPage.AllowableClockSkew" ) ); //$NON-NLS-1$
        allowableClockSkewText = createIntegerText( toolkit, composite );
        setGridDataWithDefaultWidth( allowableClockSkewText, new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void refreshUI()
    {
        removeListeners();

        // Kerberos Server
        KdcServerBean kdcServerBean = getKdcServerBean();
        setSelection( enableKerberosCheckbox, kdcServerBean.isEnabled() );
        setEnabled( kerberosPortText, enableKerberosCheckbox.getSelection() );
        setText( kerberosPortText, "" + getKdcServerTransportBean().getSystemPort() ); //$NON-NLS-1$

        // Change Password Checkbox
        ChangePasswordServerBean changePasswordServerBean = getChangePasswordServerBean();
        setSelection( enableChangePasswordCheckbox, changePasswordServerBean.isEnabled() );
        setEnabled( changePasswordPortText, enableChangePasswordCheckbox.getSelection() );
        setText( changePasswordPortText, "" + getChangePasswordServerTransportBean().getSystemPort() ); //$NON-NLS-1$

        // Kerberos Settings
        setText( primaryKdcRealmText, kdcServerBean.getKrbPrimaryRealm() );
        setText( kdcSearchBaseDnText, kdcServerBean.getSearchBaseDn().toString() );

        // Encryption Types
        List<String> encryptionTypesNames = kdcServerBean.getKrbEncryptionTypes();
        List<EncryptionType> encryptionTypes = new ArrayList<EncryptionType>();
        for ( String encryptionTypesName : encryptionTypesNames )
        {
            EncryptionType encryptionType = EncryptionType.getByName( encryptionTypesName );

            if ( !EncryptionType.UNKNOWN.equals( encryptionType ) )
            {
                encryptionTypes.add( encryptionType );
            }
        }
        encryptionTypesTableViewer.setCheckedElements( encryptionTypes.toArray() );

        // Ticket Settings
        setSelection( verifyBodyChecksumCheckbox, kdcServerBean.isKrbBodyChecksumVerified() );
        setSelection( allowEmptyAddressesCheckbox, kdcServerBean.isKrbEmptyAddressesAllowed() );
        setSelection( allowForwardableAddressesCheckbox, kdcServerBean.isKrbForwardableAllowed() );
        setSelection( requirePreAuthByEncryptedTimestampCheckbox, kdcServerBean.isKrbPaEncTimestampRequired() );
        setSelection( allowPostdatedTicketsCheckbox, kdcServerBean.isKrbPostdatedAllowed() );
        setSelection( allowRenewableTicketsCheckbox, kdcServerBean.isKrbRenewableAllowed() );
        setText( maximumRenewableLifetimeText, kdcServerBean.getKrbMaximumRenewableLifetime() + "" ); //$NON-NLS-1$
        setText( maximumTicketLifetimeText, kdcServerBean.getKrbMaximumTicketLifetime() + "" ); //$NON-NLS-1$
        setText( allowableClockSkewText, kdcServerBean.getKrbAllowableClockSkew() + "" ); //$NON-NLS-1$

        addListeners();
    }


    /**
     * Adds listeners to UI Controls.
     */
    private void addListeners()
    {
        // Enable Kerberos Server Checkbox
        addDirtyListener( enableKerberosCheckbox );
        addSelectionListener( enableKerberosCheckbox, enableKerberosCheckboxListener );

        // Kerberos Server Port Text
        addDirtyListener( kerberosPortText );
        addModifyListener( kerberosPortText, kerberosPortTextListener );

        // Enable Change Password Server Checkbox
        addDirtyListener( enableChangePasswordCheckbox );
        addSelectionListener( enableChangePasswordCheckbox, enableChangePasswordCheckboxListener );

        // Change Password Server Port Text
        addDirtyListener( changePasswordPortText );
        addModifyListener( changePasswordPortText, changePasswordPortTextListener );

        // Primary KDC Text
        addDirtyListener( primaryKdcRealmText );
        addModifyListener( primaryKdcRealmText, primaryKdcRealmTextListener );

        // KDC Search Base Dn Text
        addDirtyListener( kdcSearchBaseDnText );
        addModifyListener( kdcSearchBaseDnText, kdcSearchBaseDnTextListener );

        // Encryption Types Table Viewer
        encryptionTypesTableViewer.addCheckStateListener( encryptionTypesTableViewerListener );

        // Verify Body Checksum Checkbox
        addDirtyListener( verifyBodyChecksumCheckbox );
        addSelectionListener( verifyBodyChecksumCheckbox, verifyBodyChecksumCheckboxListener );

        // Allow Empty Addresses Checkbox
        addDirtyListener( allowEmptyAddressesCheckbox );
        addSelectionListener( allowEmptyAddressesCheckbox, allowEmptyAddressesCheckboxListener );

        // Allow Forwardable Addresses Checkbox
        addDirtyListener( allowForwardableAddressesCheckbox );
        addSelectionListener( allowForwardableAddressesCheckbox, allowForwardableAddressesCheckboxListener );

        // Require Pre-Authentication By Encrypted Timestamp Checkbox
        addDirtyListener( requirePreAuthByEncryptedTimestampCheckbox );
        addSelectionListener( requirePreAuthByEncryptedTimestampCheckbox,
            requirePreAuthByEncryptedTimestampCheckboxListener );

        // Allow Postdated Tickets Checkbox
        addDirtyListener( allowPostdatedTicketsCheckbox );
        addSelectionListener( allowPostdatedTicketsCheckbox, allowPostdatedTicketsCheckboxListener );

        // Allow Renewable Tickets Checkbox
        addDirtyListener( allowRenewableTicketsCheckbox );
        addSelectionListener( allowRenewableTicketsCheckbox, allowRenewableTicketsCheckboxListener );

        // Maximum Renewable Lifetime Text
        addDirtyListener( maximumRenewableLifetimeText );
        addModifyListener( maximumRenewableLifetimeText, maximumRenewableLifetimeTextListener );

        // Maximum Ticket Lifetime Text
        addDirtyListener( maximumTicketLifetimeText );
        addModifyListener( maximumTicketLifetimeText, maximumTicketLifetimeTextListener );

        // Allowable Clock Skew Text
        addDirtyListener( allowableClockSkewText );
        addModifyListener( allowableClockSkewText, allowableClockSkewTextListener );
    }


    /**
     * Removes listeners to UI Controls.
     */
    private void removeListeners()
    {
        // Enable Kerberos Server Checkbox
        removeDirtyListener( enableKerberosCheckbox );
        removeSelectionListener( enableKerberosCheckbox, enableKerberosCheckboxListener );

        // Kerberos Server Port Text
        removeDirtyListener( kerberosPortText );
        removeModifyListener( kerberosPortText, kerberosPortTextListener );

        // Enable Change Password Server Checkbox
        removeDirtyListener( enableChangePasswordCheckbox );
        removeSelectionListener( enableChangePasswordCheckbox, enableChangePasswordCheckboxListener );

        // Change Password Server Port Text
        removeDirtyListener( changePasswordPortText );
        removeModifyListener( changePasswordPortText, changePasswordPortTextListener );

        // Primary KDC Text
        removeDirtyListener( primaryKdcRealmText );
        removeModifyListener( primaryKdcRealmText, primaryKdcRealmTextListener );

        // KDC Search Base Dn Text
        removeDirtyListener( kdcSearchBaseDnText );
        removeModifyListener( kdcSearchBaseDnText, kdcSearchBaseDnTextListener );

        // Encryption Types Table Viewer
        encryptionTypesTableViewer.removeCheckStateListener( encryptionTypesTableViewerListener );

        // Verify Body Checksum Checkbox
        removeDirtyListener( verifyBodyChecksumCheckbox );
        removeSelectionListener( verifyBodyChecksumCheckbox, verifyBodyChecksumCheckboxListener );

        // Allow Empty Addresses Checkbox
        removeDirtyListener( allowEmptyAddressesCheckbox );
        removeSelectionListener( allowEmptyAddressesCheckbox, allowEmptyAddressesCheckboxListener );

        // Allow Forwardable Addresses Checkbox
        removeDirtyListener( allowForwardableAddressesCheckbox );
        removeSelectionListener( allowForwardableAddressesCheckbox, allowForwardableAddressesCheckboxListener );

        // Require Pre-Authentication By Encrypted Timestamp Checkbox
        removeDirtyListener( requirePreAuthByEncryptedTimestampCheckbox );
        removeSelectionListener( requirePreAuthByEncryptedTimestampCheckbox,
            requirePreAuthByEncryptedTimestampCheckboxListener );

        // Allow Postdated Tickets Checkbox
        removeDirtyListener( allowPostdatedTicketsCheckbox );
        removeSelectionListener( allowPostdatedTicketsCheckbox, allowPostdatedTicketsCheckboxListener );

        // Allow Renewable Tickets Checkbox
        removeDirtyListener( allowRenewableTicketsCheckbox );
        removeSelectionListener( allowRenewableTicketsCheckbox, allowRenewableTicketsCheckboxListener );

        // Maximum Renewable Lifetime Text
        removeDirtyListener( maximumRenewableLifetimeText );
        removeModifyListener( maximumRenewableLifetimeText, maximumRenewableLifetimeTextListener );

        // Maximum Ticket Lifetime Text
        removeDirtyListener( maximumTicketLifetimeText );
        removeModifyListener( maximumTicketLifetimeText, maximumTicketLifetimeTextListener );

        // Allowable Clock Skew Text
        removeDirtyListener( allowableClockSkewText );
        removeModifyListener( allowableClockSkewText, allowableClockSkewTextListener );
    }


    /**
     * Gets the KDC Server bean.
     *
     * @return
     *      the KDC Server bean
     */
    private KdcServerBean getKdcServerBean()
    {
        return getKdcServerBean( getDirectoryServiceBean() );
    }


    /**
     * Gets the KDC Server bean.
     *
     * @param directoryServiceBean
     *      the directory service bean
     * @return
     *      the KDC Server bean
     */
    public static KdcServerBean getKdcServerBean( DirectoryServiceBean directoryServiceBean )
    {
        KdcServerBean kdcServerBean = directoryServiceBean.getKdcServerBean();

        if ( kdcServerBean == null )
        {
            kdcServerBean = new KdcServerBean();
            directoryServiceBean.addServers( kdcServerBean );
        }

        return kdcServerBean;
    }


    /**
     * Enables the Kerberos Server.
     *
     * @param directoryServiceBean the directory service bean
     * @param enableKerberosServer the enable kerberos flag
     */
    public static void enableKerberosServer( DirectoryServiceBean directoryServiceBean, boolean enableKerberosServer )
    {
        // Enabling the KDC Server
        getKdcServerBean( directoryServiceBean ).setEnabled( enableKerberosServer );

        // Getting the Key Derivation Interceptor
        InterceptorBean keyDerivationInterceptor = getKeyDerivationInterceptor( directoryServiceBean );

        if ( keyDerivationInterceptor != null )
        {
            // Enabling the Key Derivation Interceptor
            keyDerivationInterceptor.setEnabled( enableKerberosServer );
        }
    }


    /**
     * Gets the Change Password Server bean.
     *
     * @return
     *      the Change Password Server bean
     */
    private ChangePasswordServerBean getChangePasswordServerBean()
    {
        return getChangePasswordServerBean( getDirectoryServiceBean() );
    }


    /**
     * Gets the Change Password Server bean.
     *
     * @param directoryServiceBean
     *      the directory service bean
     * @return
     *      the Change Password Server bean
     */
    public static ChangePasswordServerBean getChangePasswordServerBean( DirectoryServiceBean directoryServiceBean )
    {
        ChangePasswordServerBean changePasswordServerBean = directoryServiceBean.getChangePasswordServerBean();

        if ( changePasswordServerBean == null )
        {
            changePasswordServerBean = new ChangePasswordServerBean();
            directoryServiceBean.addServers( changePasswordServerBean );
        }

        return changePasswordServerBean;
    }


    /**
     * Gets the KDC Server Transport bean.
     * 
     * @return
     *       the KDC Server Transport bean
     */
    private TransportBean getKdcServerTransportBean()
    {
        KdcServerBean kdcServerBean = getKdcServerBean();

        TransportBean transportBean = null;

        // Looking for the transport in the list
        TransportBean[] kdcServerTransportBeans = kdcServerBean.getTransports();
        if ( kdcServerTransportBeans != null )
        {
            for ( TransportBean kdcServerTransportBean : kdcServerTransportBeans )
            {
                if ( ( "tcp".equals( kdcServerTransportBean.getTransportId() ) ) //$NON-NLS-1$
                    || ( "udp".equals( kdcServerTransportBean.getTransportId() ) ) ) //$NON-NLS-1$
                {
                    transportBean = kdcServerTransportBean;
                    break;
                }
            }
        }

        // No corresponding transport has been found
        if ( transportBean == null )
        {
            transportBean = new TransportBean();
            transportBean.setTransportId( "tcp" ); // TODO can either 'tcp' or 'udp' //$NON-NLS-1$
            kdcServerBean.addTransports( transportBean );
        }

        return transportBean;
    }


    /**
     * Gets the Change Password Server Transport bean.
     * 
     * @return
     *       the Change Password Server Transport bean
     */
    private TransportBean getChangePasswordServerTransportBean()
    {
        ChangePasswordServerBean changePasswordServerBean = getChangePasswordServerBean();

        TransportBean transportBean = null;

        // Looking for the transport in the list
        TransportBean[] changePasswordServerTransportBeans = changePasswordServerBean.getTransports();
        if ( changePasswordServerTransportBeans != null )
        {
            for ( TransportBean changePasswordServerTransportBean : changePasswordServerTransportBeans )
            {
                if ( "tcp".equals( changePasswordServerTransportBean.getTransportId() ) ) // TODO can either 'tcp' or 'udp' //$NON-NLS-1$
                {
                    transportBean = changePasswordServerTransportBean;
                    break;
                }
            }
        }

        // No corresponding transport has been found
        if ( transportBean == null )
        {
            transportBean = new TransportBean();
            transportBean.setTransportId( "tcp" ); // TODO can either 'tcp' or 'udp' //$NON-NLS-1$
            changePasswordServerBean.addTransports( transportBean );
        }

        return transportBean;
    }


    /**
     * Gets the Key Derivation Interceptor.
     *
     * @return the Key Derivation Interceptor.
     */
    private static InterceptorBean getKeyDerivationInterceptor( DirectoryServiceBean directoryServiceBean )
    {
        if ( directoryServiceBean != null )
        {
            List<InterceptorBean> interceptors = directoryServiceBean.getInterceptors();

            for ( InterceptorBean interceptor : interceptors )
            {
                if ( "org.apache.directory.server.core.kerberos.KeyDerivationInterceptor".equalsIgnoreCase( interceptor
                    .getInterceptorClassName() ) )
                {
                    return interceptor;
                }
            }
        }

        return null;
    }
}
