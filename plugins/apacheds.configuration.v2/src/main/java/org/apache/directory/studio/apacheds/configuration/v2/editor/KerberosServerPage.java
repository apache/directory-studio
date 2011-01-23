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


import org.apache.directory.server.config.beans.ChangePasswordServerBean;
import org.apache.directory.server.config.beans.KdcServerBean;
import org.apache.directory.server.config.beans.TransportBean;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.Dn;
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
    private static final String TITLE = "Kerberos Server";

    // UI Controls
    private Button enableKerberosCheckbox;
    private Text kerberosPortText;
    private Button enableChangePasswordCheckbox;
    private Text changePasswordPortText;
    private Text kdcPrincipalText;
    private Text primaryKdcRealmText;
    private Text kdcSearchBaseDnText;
    private Text encryptionTypesText;
    private Button allowClockSkewCheckbox;
    private Button verifyBodyChecksumCheckbox;
    private Button allowEmptyAddressesCheckbox;
    private Button allowForwardableAddressesCheckbox;
    private Button requirePreAuthByEncryptedTimestampCheckbox;
    private Button allowPostdatedTicketsCheckbox;
    private Button allowRenewableTicketsCheckbox;
    private Text maximumRenewableLifetimeText;
    private Text maximumTicketLifetimeText;

    // UI Controls Listeners
    private SelectionAdapter enableKerberosCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getKdcServerBean().setEnabled( enableKerberosCheckbox.getSelection() );
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
    private ModifyListener kdcPrincipalTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getKdcServerBean().setKrbKdcPrincipal( kdcPrincipalText.getText() );
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
                // TODO Auto-generated catch block
                e1.printStackTrace();
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
            getKdcServerBean().setKrbMaximumRenewableLifetime(
                Integer.parseInt( maximumRenewableLifetimeText.getText() ) );
        }
    };
    private ModifyListener maximumTicketLifetimeTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getKdcServerBean()
                .setKrbMaximumTicketLifetime( Integer.parseInt( maximumTicketLifetimeText.getText() ) );
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

        Composite leftComposite = toolkit.createComposite( parent );
        leftComposite.setLayout( new GridLayout() );
        TableWrapData leftCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        leftCompositeTableWrapData.grabHorizontal = true;
        leftComposite.setLayoutData( leftCompositeTableWrapData );

        Composite rightComposite = toolkit.createComposite( parent );
        rightComposite.setLayout( new GridLayout() );
        TableWrapData rightCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        rightCompositeTableWrapData.grabHorizontal = true;
        rightComposite.setLayoutData( rightCompositeTableWrapData );

        createKerberosServerSection( toolkit, leftComposite );
        createKerberosSettingsSection( toolkit, rightComposite );
        createTicketSettingsSection( toolkit, leftComposite );

        refreshUI();
    }


    private void createKerberosServerSection( FormToolkit toolkit, Composite parent )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "Kerberos Server" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 4, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        enableKerberosCheckbox = toolkit.createButton( composite, "Enable Kerberos Server", SWT.CHECK );
        enableKerberosCheckbox
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, "Port:" );
        kerberosPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "60088" );

        enableChangePasswordCheckbox = toolkit.createButton( composite, "Enable Kerberos Change Password Server",
            SWT.CHECK );
        enableChangePasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, "Port:" );
        changePasswordPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "60464" );
    }


    /**
     * Creates the Kerberos Settings Section
     *
     * @param toolkit
     *      the toolkit to use
     * @param parent
     *      the parent composite
     */
    private void createKerberosSettingsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "Kerberos Settings" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // KDC Principal
        toolkit.createLabel( composite, "KDC Principal:" );
        kdcPrincipalText = toolkit.createText( composite, "" );
        kdcPrincipalText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslHostLabel = createDefaultValueLabel( toolkit, composite, "krbtgt/EXAMPLE.COM@EXAMPLE.COM" );
        defaultSaslHostLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // SASL Principal
        toolkit.createLabel( composite, "Primary KDC Realm:" );
        primaryKdcRealmText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        primaryKdcRealmText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslPrincipalLabel = createDefaultValueLabel( toolkit, composite,
            "EXAMPLE.COM" );
        defaultSaslPrincipalLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Search Base Dn
        toolkit.createLabel( composite, "Search Base Dn:" );
        kdcSearchBaseDnText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        kdcSearchBaseDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslSearchBaseDnLabel = createDefaultValueLabel( toolkit, composite, "ou=users,dc=example,dc=com" );
        defaultSaslSearchBaseDnLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Encryption Types
        toolkit.createLabel( composite, "Encryption Types:" );
        encryptionTypesText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        encryptionTypesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultEncryptionTypesLabel = createDefaultValueLabel( toolkit, composite, "des-cbc-md5" );
        defaultEncryptionTypesLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
    }


    /**
     * Creates the Tickets Settings Section
     *
     * @param toolkit
     *      the toolkit to use
     * @param parent
     *      the parent composite
     */
    private void createTicketSettingsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "Ticket Settings" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, true );
        composite.setLayout( glayout );
        section.setClient( composite );

        allowClockSkewCheckbox = toolkit.createButton( composite, "Allow Clock Skew", SWT.CHECK );
        verifyBodyChecksumCheckbox = toolkit.createButton( composite, "Verify Body Checksum", SWT.CHECK );

        allowEmptyAddressesCheckbox = toolkit.createButton( composite, "Allow Empty Addresses", SWT.CHECK );
        allowForwardableAddressesCheckbox = toolkit.createButton( composite, "Allow Forwardable Addresses",
            SWT.CHECK );

        requirePreAuthByEncryptedTimestampCheckbox = toolkit.createButton( composite,
            "Require Pre-Authentication By Encrypted TimeStamp", SWT.CHECK );
        allowPostdatedTicketsCheckbox = toolkit.createButton( composite, "Allow Postdated Tickets", SWT.CHECK );

        allowRenewableTicketsCheckbox = toolkit.createButton( composite, "Allow Renewable Tickets", SWT.CHECK );
        toolkit.createLabel( composite, "" );

        Composite maximumRenewableLifetimeComposite = toolkit.createComposite( composite );
        maximumRenewableLifetimeComposite.setLayout( new GridLayout( 2, false ) );
        toolkit.createLabel( maximumRenewableLifetimeComposite, "Maximum Renewable Lifetime:" );
        maximumRenewableLifetimeText = createIntegerText( toolkit, maximumRenewableLifetimeComposite );

        Composite maximumTicketLifetimeComposite = toolkit.createComposite( composite );
        maximumTicketLifetimeComposite.setLayout( new GridLayout( 2, false ) );
        toolkit.createLabel( maximumTicketLifetimeComposite, "Maximum Ticket Lifetime:" );
        maximumTicketLifetimeText = createIntegerText( toolkit, maximumTicketLifetimeComposite );
    }


    /**
     * {@inheritDoc}
     */
    protected void refreshUI()
    {
        removeListeners();

        KdcServerBean kdcServerBean = getKdcServerBean();
        ChangePasswordServerBean changePasswordServerBean = getChangePasswordServerBean();

        setSelection( enableKerberosCheckbox, kdcServerBean.isEnabled() );
        setText(kerberosPortText,"" + getKdcServerTransportBean().getSystemPort() );

        setSelection(enableChangePasswordCheckbox, changePasswordServerBean.isEnabled() );
        setText(changePasswordPortText, "" + getChangePasswordServerTransportBean().getSystemPort() );

        setText(kdcPrincipalText, kdcServerBean.getKrbKdcPrincipal().toString() );
        setText(kdcSearchBaseDnText, kdcServerBean.getSearchBaseDn().toString() );
        setText(encryptionTypesText, kdcServerBean.getKrbEncryptionTypes().toString() );

        setSelection(verifyBodyChecksumCheckbox, kdcServerBean.isKrbBodyChecksumVerified() );
        setSelection( allowEmptyAddressesCheckbox, kdcServerBean.isKrbEmptyAddressesAllowed() );
        setSelection(allowForwardableAddressesCheckbox, kdcServerBean.isKrbForwardableAllowed() );
        setSelection(requirePreAuthByEncryptedTimestampCheckbox, kdcServerBean.isKrbPaEncTimestampRequired() );
        setSelection( allowPostdatedTicketsCheckbox, kdcServerBean.isKrbPostdatedAllowed() );
        setSelection(allowRenewableTicketsCheckbox, kdcServerBean.isKrbRenewableAllowed() );
        setText(maximumRenewableLifetimeText, kdcServerBean.getKrbMaximumRenewableLifetime() + "" );
        setText(maximumTicketLifetimeText, kdcServerBean.getKrbMaximumTicketLifetime() + "" );

        addListeners();
    }


    /**
     * Adds listeners to UI Controls.
     */
    private void addListeners()
    {
        // Enable Kerberos Checkbox
        addDirtyListener( enableKerberosCheckbox );
        addSelectionListener( enableKerberosCheckbox, enableKerberosCheckboxListener );

        // Kerberos Port Text
        addDirtyListener( kerberosPortText );
        addModifyListener( kerberosPortText, kerberosPortTextListener );

        // Enable Change Password Checkbox
        addDirtyListener( enableChangePasswordCheckbox );
        addSelectionListener( enableChangePasswordCheckbox, enableChangePasswordCheckboxListener );

        // Change Password Port Text
        addDirtyListener( changePasswordPortText );
        addModifyListener( changePasswordPortText, changePasswordPortTextListener );

        // KDC Principal Text
        addDirtyListener( kdcPrincipalText );
        addModifyListener( kdcPrincipalText, kdcPrincipalTextListener );

        // Primary KDC Text
        addDirtyListener( primaryKdcRealmText );
        addModifyListener( primaryKdcRealmText, primaryKdcRealmTextListener );

        // KDC Search Base Dn Text
        addDirtyListener( kdcSearchBaseDnText );
        addModifyListener( kdcSearchBaseDnText, kdcSearchBaseDnTextListener );

        // Encryption Types Text
        addDirtyListener( encryptionTypesText );
        // TODO A Text Control is probably not the most appropriate one

        addDirtyListener( allowClockSkewCheckbox );
        // TODO A Checkbox Control is probably not the most appropriate one

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
    }


    /**
     * Removes listeners to UI Controls.
     */
    private void removeListeners()
    {
        // Enable Kerberos Checkbox
        removeDirtyListener( enableKerberosCheckbox );
        removeSelectionListener( enableKerberosCheckbox, enableKerberosCheckboxListener );

        // Kerberos Port Text
        removeDirtyListener( kerberosPortText );
        removeModifyListener( kerberosPortText, kerberosPortTextListener );

        // Enable Change Password Checkbox
        removeDirtyListener( enableChangePasswordCheckbox );
        removeSelectionListener( enableChangePasswordCheckbox, enableChangePasswordCheckboxListener );

        // Change Password Port Text
        removeDirtyListener( changePasswordPortText );
        removeModifyListener( changePasswordPortText, changePasswordPortTextListener );

        // KDC Principal Text
        removeDirtyListener( kdcPrincipalText );
        removeModifyListener( kdcPrincipalText, kdcPrincipalTextListener );

        // Primary KDC Text
        removeDirtyListener( primaryKdcRealmText );
        removeModifyListener( primaryKdcRealmText, primaryKdcRealmTextListener );

        // KDC Search Base Dn Text
        removeDirtyListener( kdcSearchBaseDnText );
        removeModifyListener( kdcSearchBaseDnText, kdcSearchBaseDnTextListener );

        // Encryption Types Text
        removeDirtyListener( encryptionTypesText );
        // TODO A Text Control is probably not the most appropriate one

        removeDirtyListener( allowClockSkewCheckbox );
        // TODO A Checkbox Control is probably not the most appropriate one

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
    }


    /**
     * Gets the KDC Server bean.
     *
     * @return
     *      the KDC Server bean
     */
    private KdcServerBean getKdcServerBean()
    {
        KdcServerBean kdcServerBean = getDirectoryServiceBean().getKdcServerBean();

        if ( kdcServerBean == null )
        {
            kdcServerBean = new KdcServerBean();
            getDirectoryServiceBean().addServers( kdcServerBean );
        }

        return kdcServerBean;
    }


    /**
     * Gets the Change Password Server bean.
     *
     * @return
     *      the Change Password Server bean
     */
    private ChangePasswordServerBean getChangePasswordServerBean()
    {
        ChangePasswordServerBean changePasswordServerBean = getDirectoryServiceBean().getChangePasswordServerBean();

        if ( changePasswordServerBean == null )
        {
            changePasswordServerBean = new ChangePasswordServerBean();
            getDirectoryServiceBean().addServers( changePasswordServerBean );
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
                if ( "tcp".equals( kdcServerTransportBean.getTransportId() ) ) // TODO can either 'tcp' or 'udp'
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
            transportBean.setTransportId( "tcp" ); // TODO can either 'tcp' or 'udp'
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
                if ( "tcp".equals( changePasswordServerTransportBean.getTransportId() ) ) // TODO can either 'tcp' or 'udp'
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
            transportBean.setTransportId( "tcp" ); // TODO can either 'tcp' or 'udp'
            changePasswordServerBean.addTransports( transportBean );
        }

        return transportBean;
    }
}
