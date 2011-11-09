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


import java.util.List;

import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.LdapServerBean;
import org.apache.directory.server.config.beans.SaslMechHandlerBean;
import org.apache.directory.server.config.beans.TransportBean;
import org.apache.directory.shared.ldap.model.constants.SupportedSaslMechanisms;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapLdapsServersPage extends ServerConfigurationEditorPage
{
    private static final String DEFAULT_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT_LDAPS = 10636;
    private static final int DEFAULT_PORT_LDAP = 10389;
    private static final String TRANSPORT_ID_LDAP = "ldap";
    private static final String TRANSPORT_ID_LDAPS = "ldaps";
    private static final String SASL_MECHANISMS_SIMPLE = "SIMPLE";

    /** The Page ID*/
    public static final String ID = LdapLdapsServersPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = "LDAP/LDAPS Servers";

    // UI Controls
    private Button enableLdapCheckbox;
    private Text ldapPortText;
    private Button enableLdapsCheckbox;
    private Text ldapsPortText;
    private Text maxTimeLimitText;
    private Text maxSizeLimitText;
    private Button authMechSimpleCheckbox;
    private Button authMechCramMd5Checkbox;
    private Button authMechDigestMd5Checkbox;
    private Button authMechGssapiCheckbox;
    private Button authMechNtlmCheckbox;
    private Text authMechNtlmText;
    private Button authMechGssSpnegoCheckbox;
    private Text authMechGssSpnegoText;
    private Text saslHostText;
    private Text saslPrincipalText;
    private Text saslSearchBaseDnText;

    // UI Controls Listeners
    private SelectionAdapter enableLdapCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getLdapServerTransportBean().setEnabled( enableLdapCheckbox.getSelection() );
        }
    };
    private ModifyListener ldapPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerTransportBean().setSystemPort( Integer.parseInt( ldapPortText.getText() ) );
        }
    };
    private SelectionAdapter enableLdapsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getLdapsServerTransportBean().setEnabled( enableLdapsCheckbox.getSelection() );
        }
    };
    private ModifyListener ldapsPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapsServerTransportBean().setSystemPort( Integer.parseInt( ldapsPortText.getText() ) );
        }
    };
    private ModifyListener saslHostTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setLdapServerSaslHost( saslHostText.getText() );
        }
    };
    private ModifyListener saslPrincipalTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setLdapServerSaslPrincipal( saslPrincipalText.getText() );
        }
    };
    private ModifyListener saslSearchBaseDnTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String searchBaseDnValue = saslSearchBaseDnText.getText();

            try
            {
                Dn searchBaseDn = new Dn( searchBaseDnValue );
                getLdapServerBean().setSearchBaseDn( searchBaseDn );
            }
            catch ( LdapInvalidDnException e1 )
            {
                // Stay silent
            }
        }
    };
    private SelectionAdapter authMechSimpleCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SASL_MECHANISMS_SIMPLE, authMechSimpleCheckbox.getSelection() );
        };
    };
    private SelectionAdapter authMechGssapiCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.GSSAPI,
                authMechGssapiCheckbox.getSelection() );
        };
    };
    private SelectionAdapter authMechCramMd5CheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.CRAM_MD5,
                authMechCramMd5Checkbox.getSelection() );
        };
    };
    private SelectionAdapter authMechDigestMd5CheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.DIGEST_MD5,
                authMechDigestMd5Checkbox.getSelection() );
        };
    };
    private SelectionAdapter authMechGssSpnegoCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.GSS_SPNEGO,
                authMechGssSpnegoCheckbox.getSelection() );
            authMechGssSpnegoText.setEnabled( authMechGssSpnegoCheckbox.getSelection() );
        };
    };
    private ModifyListener authMechGssSpnegoTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            setNtlmMechProviderSupportedAuthenticationMechanism( SupportedSaslMechanisms.GSS_SPNEGO,
                authMechGssSpnegoText.getText() );
        }
    };
    private SelectionAdapter authMechNtlmCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.NTLM,
                authMechNtlmCheckbox.getSelection() );
            authMechNtlmText.setEnabled( authMechNtlmCheckbox.getSelection() );
        };
    };
    private ModifyListener authMechNtlmTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            setNtlmMechProviderSupportedAuthenticationMechanism( SupportedSaslMechanisms.NTLM,
                authMechNtlmText.getText() );
        }
    };
    private ModifyListener maxTimeLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setLdapServerMaxTimeLimit( Integer.parseInt( maxTimeLimitText.getText() ) );
        }
    };
    private ModifyListener maxSizeLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setLdapServerMaxSizeLimit( Integer.parseInt( maxSizeLimitText.getText() ) );
        }
    };


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public LdapLdapsServersPage( ServerConfigurationEditor editor )
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

        createLdapServerSection( toolkit, leftComposite );
        createLimitsSection( toolkit, leftComposite );
        createAdvancedSection( toolkit, leftComposite );
        createSupportedAuthenticationMechanismsSection( toolkit, rightComposite );
        createSaslSettingsSection( toolkit, rightComposite );

        refreshUI();
    }


    private void createLdapServerSection( FormToolkit toolkit, Composite parent )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "LDAP/LDAPS Servers" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 4, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        enableLdapCheckbox = toolkit.createButton( composite, "Enable LDAP Server", SWT.CHECK );
        enableLdapCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, "Port:" );
        ldapPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "10389" );

        enableLdapsCheckbox = toolkit.createButton( composite, "Enable LDAPS Server", SWT.CHECK );
        enableLdapsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, "Port:" );
        ldapsPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "10636" );
    }


    /**
     * Creates the Limits Section
     *
     * @param toolkit
     *      the toolkit to use
     * @param parent
     *      the parent composite
     */
    private void createLimitsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "Limits" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Max. Time Limit
        toolkit.createLabel( composite, "Max Time Limit (ms):" );
        maxTimeLimitText = createIntegerText( toolkit, composite );
        maxTimeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Max. Size Limit
        toolkit.createLabel( composite, "Max Size Limit (entries):" );
        maxSizeLimitText = createIntegerText( toolkit, composite );
        maxSizeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Advanced Section
     *
     * @param toolkit
     *      the toolkit to use
     * @param parent
     *      the parent composite
     */
    private void createAdvancedSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.TWISTIE | Section.COMPACT );
        section.setText( "Advanced" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        Button enableTlsCheckbox = toolkit.createButton( composite, "Enable TLS", SWT.CHECK );
        enableTlsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        Button enableServerSidePasswordHashingCheckbox = toolkit.createButton( composite,
            "Enable sever-side password\nhashing",
            SWT.CHECK );
        enableServerSidePasswordHashingCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );
        toolkit.createLabel( composite, "Hashing Method:" );
        Combo hashingMethodCombo = new Combo( composite, SWT.DROP_DOWN | SWT.READ_ONLY );
        hashingMethodCombo.setItems( new String[]
            { "SSHA", "MD5" } );
        toolkit.adapt( hashingMethodCombo );
        hashingMethodCombo.setText( "SSHA" );
        hashingMethodCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        createDefaultValueLabel( toolkit, composite, "SSHA" );
    }


    /**
     * Creates the Supported Authentication Mechanisms Section
     *
     * @param toolkit
     *      the toolkit to use
     * @param parent
     *      the parent composite
     */
    private void createSupportedAuthenticationMechanismsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "Supported Authentication Mechanisms" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        toolkit.paintBordersFor( composite );
        composite.setLayout( new GridLayout( 4, true ) );
        section.setClient( composite );

        // Simple
        authMechSimpleCheckbox = toolkit.createButton( composite, "Simple", SWT.CHECK );
        authMechSimpleCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // GSSAPI
        authMechGssapiCheckbox = toolkit.createButton( composite, "GSSAPI", SWT.CHECK );
        authMechGssapiCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // CRAM-MD5
        authMechCramMd5Checkbox = toolkit.createButton( composite, "CRAM-MD5", SWT.CHECK );
        authMechCramMd5Checkbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // DIGEST-MD5
        authMechDigestMd5Checkbox = toolkit.createButton( composite, "DIGEST-MD5", SWT.CHECK );
        authMechDigestMd5Checkbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // NTLM
        authMechNtlmCheckbox = toolkit.createButton( composite, "NTLM", SWT.CHECK );
        authMechNtlmCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        Composite authMechNtlmComposite = toolkit.createComposite( composite );
        authMechNtlmComposite.setLayout( new GridLayout( 2, false ) );
        toolkit.createLabel( authMechNtlmComposite, "Provider:" );
        authMechNtlmText = toolkit.createText( authMechNtlmComposite, "" );
        authMechNtlmText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        authMechNtlmComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false, 3, 1 ) );

        // GSS-SPENEGO
        authMechGssSpnegoCheckbox = toolkit.createButton( composite, "GSS-SPNEGO", SWT.CHECK );
        authMechGssSpnegoCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        Composite authMechGssSpnegoComposite = toolkit.createComposite( composite );
        authMechGssSpnegoComposite.setLayout( new GridLayout( 2, false ) );
        toolkit.createLabel( authMechGssSpnegoComposite, "Provider:" );
        authMechGssSpnegoText = toolkit.createText( authMechGssSpnegoComposite, "" );
        authMechGssSpnegoText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        authMechGssSpnegoComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );
    }


    /**
     * Creates the SASL Settings Section
     *
     * @param toolkit
     *      the toolkit to use
     * @param parent
     *      the parent composite
     */
    private void createSaslSettingsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "SASL Settings" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // SASL Host
        toolkit.createLabel( composite, "SASL Host:" );
        saslHostText = toolkit.createText( composite, "" );
        setGridDataWithDefaultWidth( saslHostText, new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslHostLabel = createDefaultValueLabel( toolkit, composite, "ldap.example.com" );
        defaultSaslHostLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // SASL Principal
        toolkit.createLabel( composite, "SASL Principal:" );
        saslPrincipalText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( saslPrincipalText, new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslPrincipalLabel = createDefaultValueLabel( toolkit, composite,
            "ldap/ldap.example.com@EXAMPLE.COM" );
        defaultSaslPrincipalLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Search Base Dn
        toolkit.createLabel( composite, "Search Base Dn:" );
        saslSearchBaseDnText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( saslSearchBaseDnText, new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslSearchBaseDnLabel = createDefaultValueLabel( toolkit, composite, "ou=users,dc=example,dc=com" );
        defaultSaslSearchBaseDnLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
    }


    /**
     * Adds listeners to UI Controls.
     */
    private void addListeners()
    {
        // Enable LDAP Checkbox
        addDirtyListener( enableLdapCheckbox );
        addSelectionListener( enableLdapCheckbox, enableLdapCheckboxListener );

        // LDAP Port Text
        addDirtyListener( ldapPortText );
        addModifyListener( ldapPortText, ldapPortTextListener );

        // Enable LDAPS Checkbox
        addDirtyListener( enableLdapsCheckbox );
        addSelectionListener( enableLdapsCheckbox, enableLdapsCheckboxListener );

        // LDAPS Port Text
        addDirtyListener( ldapsPortText );
        addModifyListener( ldapsPortText, ldapsPortTextListener );

        // Max Time Limit Text
        addDirtyListener( maxTimeLimitText );
        addModifyListener( maxTimeLimitText, maxTimeLimitTextListener );

        // Max Size Limit Text
        addDirtyListener( maxSizeLimitText );
        addModifyListener( maxSizeLimitText, maxSizeLimitTextListener );

        // Auth Mechanisms Simple Checkbox
        addDirtyListener( authMechSimpleCheckbox );
        addSelectionListener( authMechSimpleCheckbox, authMechSimpleCheckboxListener );

        // Auth Mechanisms GSSAPI Checkbox
        addDirtyListener( authMechGssapiCheckbox );
        addSelectionListener( authMechGssapiCheckbox, authMechGssapiCheckboxListener );

        // Auth Mechanisms CRAM-MD5 Checkbox
        addDirtyListener( authMechCramMd5Checkbox );
        addSelectionListener( authMechCramMd5Checkbox, authMechCramMd5CheckboxListener );

        // Auth Mechanisms DIGEST-MD5 Checkbox
        addDirtyListener( authMechDigestMd5Checkbox );
        addSelectionListener( authMechDigestMd5Checkbox, authMechDigestMd5CheckboxListener );

        // Auth Mechanisms NTLM Checkbox
        addDirtyListener( authMechNtlmCheckbox );
        addSelectionListener( authMechNtlmCheckbox, authMechNtlmCheckboxListener );
        addModifyListener( authMechNtlmText, authMechNtlmTextListener );

        // Auth Mechanisms GSS SPENEGO Checkbox
        addDirtyListener( authMechGssSpnegoCheckbox );
        addSelectionListener( authMechGssSpnegoCheckbox, authMechGssSpnegoCheckboxListener );
        addModifyListener( authMechGssSpnegoText, authMechGssSpnegoTextListener );

        // SASL Host Text
        addDirtyListener( saslHostText );
        addModifyListener( saslHostText, saslHostTextListener );

        // SASL Principal Text
        addDirtyListener( saslPrincipalText );
        addModifyListener( saslPrincipalText, saslPrincipalTextListener );

        // SASL Seach Base Dn Text
        addDirtyListener( saslSearchBaseDnText );
        addModifyListener( saslSearchBaseDnText, saslSearchBaseDnTextListener );
    }


    /**
     * Removes listeners to UI Controls.
     */
    private void removeListeners()
    {
        // Enable LDAP Checkbox
        removeDirtyListener( enableLdapCheckbox );
        removeSelectionListener( enableLdapCheckbox, enableLdapCheckboxListener );

        // LDAP Port Text
        removeDirtyListener( ldapPortText );
        removeModifyListener( ldapPortText, ldapPortTextListener );

        // Enable LDAPS Checkbox
        removeDirtyListener( enableLdapsCheckbox );
        removeSelectionListener( enableLdapsCheckbox, enableLdapsCheckboxListener );

        // LDAPS Port Text
        removeDirtyListener( ldapsPortText );
        removeModifyListener( ldapsPortText, ldapsPortTextListener );

        // Max Time Limit Text
        removeDirtyListener( maxTimeLimitText );
        removeModifyListener( maxTimeLimitText, maxTimeLimitTextListener );

        // Max Size Limit Text
        removeDirtyListener( maxSizeLimitText );
        removeModifyListener( maxSizeLimitText, maxSizeLimitTextListener );

        // Auth Mechanisms Simple Checkbox
        removeDirtyListener( authMechSimpleCheckbox );
        removeSelectionListener( authMechSimpleCheckbox, authMechSimpleCheckboxListener );

        // Auth Mechanisms CRAM-MD5 Checkbox
        removeDirtyListener( authMechCramMd5Checkbox );
        removeSelectionListener( authMechCramMd5Checkbox, authMechCramMd5CheckboxListener );

        // Auth Mechanisms DIGEST-MD5 Checkbox
        removeDirtyListener( authMechDigestMd5Checkbox );
        removeSelectionListener( authMechDigestMd5Checkbox, authMechDigestMd5CheckboxListener );

        // Auth Mechanisms GSSAPI Checkbox
        removeDirtyListener( authMechGssapiCheckbox );
        removeSelectionListener( authMechGssapiCheckbox, authMechGssapiCheckboxListener );

        // Auth Mechanisms NTLM Checkbox
        removeDirtyListener( authMechNtlmCheckbox );
        removeSelectionListener( authMechNtlmCheckbox, authMechNtlmCheckboxListener );
        removeModifyListener( authMechNtlmText, authMechNtlmTextListener );

        // Auth Mechanisms GSS SPENEGO Checkbox
        removeDirtyListener( authMechGssSpnegoCheckbox );
        removeSelectionListener( authMechGssSpnegoCheckbox, authMechGssSpnegoCheckboxListener );
        removeModifyListener( authMechGssSpnegoText, authMechGssSpnegoTextListener );

        // SASL Host Text
        removeDirtyListener( saslHostText );
        removeModifyListener( saslHostText, saslHostTextListener );

        // SASL Principal Text
        removeDirtyListener( saslPrincipalText );
        removeModifyListener( saslPrincipalText, saslPrincipalTextListener );

        // SASL Seach Base Dn Text
        removeDirtyListener( saslSearchBaseDnText );
        removeModifyListener( saslSearchBaseDnText, saslSearchBaseDnTextListener );
    }


    /**
     * {@inheritDoc}
     */
    protected void refreshUI()
    {
        removeListeners();

        // LDAP Server
        TransportBean ldapServerTransportBean = getLdapServerTransportBean();
        setSelection( enableLdapCheckbox, ldapServerTransportBean.isEnabled() );
        setText( ldapPortText, ldapServerTransportBean.getSystemPort() + "" );

        // LDAPS Server
        TransportBean ldapsServerTransportBean = getLdapsServerTransportBean();
        setSelection( enableLdapsCheckbox, ldapsServerTransportBean.isEnabled() );
        setText( ldapsPortText, ldapsServerTransportBean.getSystemPort() + "" );

        // SASL Properties
        LdapServerBean ldapServerBean = getLdapServerBean();
        setText( saslHostText, ldapServerBean.getLdapServerSaslHost() );
        setText( saslPrincipalText, ldapServerBean.getLdapServerSaslPrincipal() );
        setText( saslSearchBaseDnText, ldapServerBean.getSearchBaseDn().toString() );

        // Supported Auth Mechanisms
        List<SaslMechHandlerBean> saslMechHandlers = ldapServerBean.getSaslMechHandlers();
        uncheckAllSupportedAuthenticationMechanisms();
        for ( SaslMechHandlerBean saslMechHandler : saslMechHandlers )
        {
            if ( SASL_MECHANISMS_SIMPLE.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                authMechSimpleCheckbox.setSelection( saslMechHandler.isEnabled() );
            }
            else if ( SupportedSaslMechanisms.GSSAPI.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                authMechGssapiCheckbox.setSelection( saslMechHandler.isEnabled() );
            }
            if ( SupportedSaslMechanisms.CRAM_MD5.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                authMechCramMd5Checkbox.setSelection( saslMechHandler.isEnabled() );
            }
            else if ( SupportedSaslMechanisms.DIGEST_MD5.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                authMechDigestMd5Checkbox.setSelection( saslMechHandler.isEnabled() );
            }
            else if ( SupportedSaslMechanisms.GSS_SPNEGO.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                authMechGssSpnegoCheckbox.setSelection( saslMechHandler.isEnabled() );
                authMechGssSpnegoText.setEnabled( saslMechHandler.isEnabled() );
                authMechGssSpnegoText.setText( saslMechHandler.getNtlmMechProvider() );
            }
            else if ( SupportedSaslMechanisms.NTLM.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                authMechNtlmCheckbox.setSelection( saslMechHandler.isEnabled() );
                authMechNtlmText.setEnabled( saslMechHandler.isEnabled() );
                authMechNtlmText.setText( saslMechHandler.getNtlmMechProvider() );
            }
        }

        // Limits
        setText( maxTimeLimitText, "" + ldapServerBean.getLdapServerMaxTimeLimit() );
        setText( maxSizeLimitText, "" + ldapServerBean.getLdapServerMaxSizeLimit() );

        addListeners();
    }


    /**
     * Unchecks all supported authentication mechanisns checkboxes.
     */
    private void uncheckAllSupportedAuthenticationMechanisms()
    {
        authMechSimpleCheckbox.setSelection( false );
        authMechCramMd5Checkbox.setSelection( false );
        authMechDigestMd5Checkbox.setSelection( false );
        authMechGssapiCheckbox.setSelection( false );
        authMechNtlmCheckbox.setSelection( false );
        authMechNtlmText.setEnabled( false );
        authMechGssSpnegoCheckbox.setSelection( false );
        authMechGssSpnegoText.setEnabled( false );
    }


    /**
     * Sets the enabled flag for the given support authentication mechanism.
     *
     * @param mechanismName the mechanism name
     * @param enabled the enabled flag
     */
    private void setEnableSupportedAuthenticationMechanism( String mechanismName, boolean enabled )
    {
        List<SaslMechHandlerBean> saslMechHandlers = getLdapServerBean().getSaslMechHandlers();
        for ( SaslMechHandlerBean saslMechHandler : saslMechHandlers )
        {
            if ( mechanismName.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                saslMechHandler.setEnabled( enabled );
                return;
            }
        }
    }


    /**
     * Sets the NTLM mechanism provider for the given support authentication mechanism.
     *
     * @param mechanismName the mechanism name
     * @param ntlmMechProvider the NTLM mechanism provider
     */
    private void setNtlmMechProviderSupportedAuthenticationMechanism( String mechanismName, String ntlmMechProvider )
    {
        List<SaslMechHandlerBean> saslMechHandlers = getLdapServerBean().getSaslMechHandlers();
        for ( SaslMechHandlerBean saslMechHandler : saslMechHandlers )
        {
            if ( mechanismName.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                saslMechHandler.setNtlmMechProvider( ntlmMechProvider );
                return;
            }
        }
    }


    /**
     * Gets the LDAP Server bean.
     *
     * @return
     *      the LDAP Server bean
     */
    private LdapServerBean getLdapServerBean()
    {
        return getLdapServerBean( getDirectoryServiceBean() );
    }


    /**
     * Gets the LDAP Server bean.
     *
     * @param directoryServiceBean
     *      the directory service bean
     * @return
     *      the LDAP Server bean
     */
    public static LdapServerBean getLdapServerBean( DirectoryServiceBean directoryServiceBean )
    {
        LdapServerBean ldapServerBean = directoryServiceBean.getLdapServerBean();

        if ( ldapServerBean == null )
        {
            ldapServerBean = new LdapServerBean();
            directoryServiceBean.addServers( ldapServerBean );
        }

        return ldapServerBean;
    }


    /**
     * Gets the LDAP Server transport bean.
     *
     * @return
     *      the LDAP Server transport bean
     */
    private TransportBean getLdapServerTransportBean()
    {
        return getTransportBean( TRANSPORT_ID_LDAP );
    }


    /**
     * Gets the LDAP Server transport bean.
     *
     * @param directoryServiceBean
     *      the directory service bean
     * @return
     *      the LDAP Server transport bean
     */
    public static TransportBean getLdapServerTransportBean( DirectoryServiceBean directoryServiceBean )
    {
        return getTransportBean( directoryServiceBean, TRANSPORT_ID_LDAP );
    }


    /**
     * Gets the LDAPS Server transport bean.
     *
     * @return
     *      the LDAPS Server transport bean
     */
    private TransportBean getLdapsServerTransportBean()
    {
        return getTransportBean( TRANSPORT_ID_LDAPS );
    }


    /**
     * Gets the LDAPS Server transport bean.
     *
     * @param directoryServiceBean
     *      the directory service bean
     * @return
     *      the LDAPS Server transport bean
     */
    public static TransportBean getLdapsServerTransportBean( DirectoryServiceBean directoryServiceBean )
    {
        return getTransportBean( directoryServiceBean, TRANSPORT_ID_LDAPS );
    }


    /**
     * Gets a transport bean based on its id.
     *
     * @param id
     *      the id
     * @return
     *      the corresponding transport bean
     */
    private TransportBean getTransportBean( String id )
    {
        return getTransportBean( getDirectoryServiceBean(), id );
    }


    /**
     * Gets a transport bean based on its id.
     *
     * @param directoryServiceBean
     *      the directory service bean
     * @param id
     *      the id
     * @return
     *      the corresponding transport bean
     */
    public static TransportBean getTransportBean( DirectoryServiceBean directoryServiceBean, String id )
    {
        LdapServerBean ldapServerBean = getLdapServerBean( directoryServiceBean );

        TransportBean transportBean = null;

        // Looking for the transport in the list
        TransportBean[] ldapServerTransportBeans = ldapServerBean.getTransports();
        if ( ldapServerTransportBeans != null )
        {
            for ( TransportBean ldapServerTransportBean : ldapServerTransportBeans )
            {
                if ( id.equals( ldapServerTransportBean.getTransportId() ) )
                {
                    transportBean = ldapServerTransportBean;
                    break;
                }
            }
        }

        // No corresponding transport has been found
        if ( transportBean == null )
        {
            // Creating a transport bean
            transportBean = new TransportBean();
            ldapServerBean.addTransports( transportBean );

            // ID
            transportBean.setTransportId( id );

            // Address
            transportBean.setTransportAddress( DEFAULT_ADDRESS );

            // Port
            if ( TRANSPORT_ID_LDAP.equals( id ) )
            {
                transportBean.setSystemPort( DEFAULT_PORT_LDAP );
            }
            else if ( TRANSPORT_ID_LDAPS.equals( id ) )
            {
                transportBean.setSystemPort( DEFAULT_PORT_LDAPS );
            }

            // SSL
            if ( TRANSPORT_ID_LDAPS.equals( id ) )
            {
                transportBean.setTransportEnableSSL( true );
            }
        }

        return transportBean;
    }
}
