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
import org.apache.directory.server.config.beans.ExtendedOpHandlerBean;
import org.apache.directory.server.config.beans.InterceptorBean;
import org.apache.directory.server.config.beans.LdapServerBean;
import org.apache.directory.server.config.beans.SaslMechHandlerBean;
import org.apache.directory.server.config.beans.TransportBean;
import org.apache.directory.shared.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.shared.ldap.model.constants.SupportedSaslMechanisms;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
    private static final String DEFAULT_ADDRESS = "0.0.0.0"; //$NON-NLS-1$
    private static final int DEFAULT_PORT_LDAPS = 10636;
    private static final int DEFAULT_PORT_LDAP = 10389;
    private static final String TRANSPORT_ID_LDAP = "ldap"; //$NON-NLS-1$
    private static final String TRANSPORT_ID_LDAPS = "ldaps"; //$NON-NLS-1$
    private static final String SASL_MECHANISMS_SIMPLE = "SIMPLE"; //$NON-NLS-1$
    private static final String START_TLS_HANDLER_ID = "starttlshandler"; //$NON-NLS-1$
    private static final String START_TLS_HANDLER_CLASS = "org.apache.directory.server.ldap.handlers.extended.StartTlsHandler"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_SSHA512 = "org.apache.directory.server.core.hash.Ssha512PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_SHA512 = "org.apache.directory.server.core.hash.Sha512PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_SSHA384 = "org.apache.directory.server.core.hash.Ssha384PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_SHA384 = "org.apache.directory.server.core.hash.Sha384PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_SSHA256 = "org.apache.directory.server.core.hash.Ssha256PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_SHA256 = "org.apache.directory.server.core.hash.Sha256PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_CRYPT = "org.apache.directory.server.core.hash.CryptPasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_SMD5 = "org.apache.directory.server.core.hash.Smd5PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_MD5 = "org.apache.directory.server.core.hash.Md5PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_SSHA = "org.apache.directory.server.core.hash.SshaPasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String FQCN_HASHING_INTERCEPTOR_SHA = "org.apache.directory.server.core.hash.ShaPasswordHashingInterceptor"; //$NON-NLS-1$

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
    private Button enableTlsCheckbox;
    private Button enableServerSidePasswordHashingCheckbox;
    private ComboViewer hashingMethodComboViewer;

    // UI Controls Listeners
    private SelectionAdapter enableLdapCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getLdapServerTransportBean().setEnabled( enableLdapCheckbox.getSelection() );
            setEnabled( ldapPortText, enableLdapCheckbox.getSelection() );
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
            setEnabled( ldapsPortText, enableLdapsCheckbox.getSelection() );
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
            setEnabled( authMechGssSpnegoText, authMechGssSpnegoCheckbox.getSelection() );
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
            setEnabled( authMechNtlmText, authMechNtlmCheckbox.getSelection() );
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
    private SelectionAdapter enableTlsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableTls( enableTlsCheckbox.getSelection() );
        }
    };
    private SelectionAdapter enableServerSidePasswordHashingCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnabled( hashingMethodComboViewer.getCombo(), enableServerSidePasswordHashingCheckbox.getSelection() );
            if ( enableServerSidePasswordHashingCheckbox.getSelection() )
            {
                deleteHashingMethodInterceptor();
                addHashingMethodInterceptor();
            }
            else
            {
                deleteHashingMethodInterceptor();
            }
        }
    };
    private ISelectionChangedListener hashingMethodComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            deleteHashingMethodInterceptor();
            addHashingMethodInterceptor();
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
        createLdapServerSection( toolkit, leftComposite );
        createLimitsSection( toolkit, leftComposite );
        createAdvancedSection( toolkit, leftComposite );
        createSupportedAuthenticationMechanismsSection( toolkit, rightComposite );
        createSaslSettingsSection( toolkit, rightComposite );

        // Refreshing the UI
        refreshUI();
    }


    /**
     * Creates the LDAP/LDAPS section.
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createLdapServerSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "LDAP/LDAPS Servers" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 4, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Enable LDAP Server Checkbox
        enableLdapCheckbox = toolkit.createButton( composite, "Enable LDAP Server", SWT.CHECK );
        enableLdapCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );

        // LDAP Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, "Port:" );
        ldapPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "10389" ); //$NON-NLS-1$

        // Enable LDAPS Server Checkbox
        enableLdapsCheckbox = toolkit.createButton( composite, "Enable LDAPS Server", SWT.CHECK );
        enableLdapsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );

        // LDAPS Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, "Port:" );
        ldapsPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "10636" ); //$NON-NLS-1$
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

        // Max. Time Limit Text
        toolkit.createLabel( composite, "Max Time Limit (ms):" );
        maxTimeLimitText = createIntegerText( toolkit, composite );
        maxTimeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Max. Size Limit Text
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
        GridLayout glayout = new GridLayout( 3, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Enable TLS Checkbox
        enableTlsCheckbox = toolkit.createButton( composite, "Enable TLS", SWT.CHECK );
        enableTlsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Enable Server-side Password Hashing Checkbox
        enableServerSidePasswordHashingCheckbox = toolkit.createButton( composite,
            "Enable server-side password\nhashing",
            SWT.CHECK );
        enableServerSidePasswordHashingCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Server-side Password Hashing Combo
        toolkit.createLabel( composite, "   " ); //$NON-NLS-1$
        toolkit.createLabel( composite, "Hashing Method:" );
        Combo hashingMethodCombo = new Combo( composite, SWT.READ_ONLY | SWT.SINGLE );
        hashingMethodCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        toolkit.adapt( hashingMethodCombo );
        hashingMethodComboViewer = new ComboViewer( hashingMethodCombo );
        hashingMethodComboViewer.setContentProvider( new ArrayContentProvider() );
        hashingMethodComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof LdapSecurityConstants )
                {
                    LdapSecurityConstants hashingMethod = ( LdapSecurityConstants ) element;

                    switch ( hashingMethod )
                    {
                        case HASH_METHOD_SHA:
                            return "SHA"; //$NON-NLS-1$
                        case HASH_METHOD_SSHA:
                            return "SSHA"; //$NON-NLS-1$
                        case HASH_METHOD_MD5:
                            return "MD5"; //$NON-NLS-1$
                        case HASH_METHOD_SMD5:
                            return "SMD5"; //$NON-NLS-1$
                        case HASH_METHOD_CRYPT:
                            return "CRYPT"; //$NON-NLS-1$
                        case HASH_METHOD_SHA256:
                            return "SHA-256"; //$NON-NLS-1$
                        case HASH_METHOD_SSHA256:
                            return "SSHA-256"; //$NON-NLS-1$
                        case HASH_METHOD_SHA384:
                            return "SHA-384"; //$NON-NLS-1$
                        case HASH_METHOD_SSHA384:
                            return "SSHA-384"; //$NON-NLS-1$
                        case HASH_METHOD_SHA512:
                            return "SHA-512"; //$NON-NLS-1$
                        case HASH_METHOD_SSHA512:
                            return "SSHA-512"; //$NON-NLS-1$
                    }
                }

                return super.getText( element );
            }
        } );
        Object[] hashingMethods = new Object[]
            {
                LdapSecurityConstants.HASH_METHOD_SHA,
                LdapSecurityConstants.HASH_METHOD_SSHA,
                LdapSecurityConstants.HASH_METHOD_MD5,
                LdapSecurityConstants.HASH_METHOD_SMD5,
                LdapSecurityConstants.HASH_METHOD_CRYPT,
                LdapSecurityConstants.HASH_METHOD_SHA256,
                LdapSecurityConstants.HASH_METHOD_SSHA256,
                LdapSecurityConstants.HASH_METHOD_SHA384,
                LdapSecurityConstants.HASH_METHOD_SSHA384,
                LdapSecurityConstants.HASH_METHOD_SHA512,
                LdapSecurityConstants.HASH_METHOD_SSHA512
        };
        hashingMethodComboViewer.setInput( hashingMethods );
        setSelection( hashingMethodComboViewer, LdapSecurityConstants.HASH_METHOD_SSHA );
        toolkit.createLabel( composite, "   " ); //$NON-NLS-1$
        Label defaultLabel = createDefaultValueLabel( toolkit, composite, "SSHA" ); //$NON-NLS-1$
        defaultLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
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

        // Simple Checkbox
        authMechSimpleCheckbox = toolkit.createButton( composite, "Simple", SWT.CHECK ); //$NON-NLS-1$
        authMechSimpleCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // GSSAPI Checkbox
        authMechGssapiCheckbox = toolkit.createButton( composite, "GSSAPI", SWT.CHECK ); //$NON-NLS-1$
        authMechGssapiCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // CRAM-MD5 Checkbox
        authMechCramMd5Checkbox = toolkit.createButton( composite, "CRAM-MD5", SWT.CHECK ); //$NON-NLS-1$
        authMechCramMd5Checkbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // DIGEST-MD5 Checkbox
        authMechDigestMd5Checkbox = toolkit.createButton( composite, "DIGEST-MD5", SWT.CHECK ); //$NON-NLS-1$
        authMechDigestMd5Checkbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // NTLM Checkbox and Text
        authMechNtlmCheckbox = toolkit.createButton( composite, "NTLM", SWT.CHECK ); //$NON-NLS-1$
        authMechNtlmCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        Composite authMechNtlmComposite = toolkit.createComposite( composite );
        authMechNtlmComposite.setLayout( new GridLayout( 2, false ) );
        toolkit.createLabel( authMechNtlmComposite, "Provider:" );
        authMechNtlmText = toolkit.createText( authMechNtlmComposite, "" ); //$NON-NLS-1$
        authMechNtlmText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        authMechNtlmComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false, 3, 1 ) );

        // GSS-SPNEGO Checkbox and Text
        authMechGssSpnegoCheckbox = toolkit.createButton( composite, "GSS-SPNEGO", SWT.CHECK );
        authMechGssSpnegoCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        Composite authMechGssSpnegoComposite = toolkit.createComposite( composite );
        authMechGssSpnegoComposite.setLayout( new GridLayout( 2, false ) );
        toolkit.createLabel( authMechGssSpnegoComposite, "Provider:" );
        authMechGssSpnegoText = toolkit.createText( authMechGssSpnegoComposite, "" ); //$NON-NLS-1$
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

        // SASL Host Text
        toolkit.createLabel( composite, "SASL Host:" );
        saslHostText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( saslHostText, new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslHostLabel = createDefaultValueLabel( toolkit, composite, "ldap.example.com" ); //$NON-NLS-1$
        defaultSaslHostLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // SASL Principal Text
        toolkit.createLabel( composite, "SASL Principal:" );
        saslPrincipalText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( saslPrincipalText, new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslPrincipalLabel = createDefaultValueLabel( toolkit, composite,
            "ldap/ldap.example.com@EXAMPLE.COM" ); //$NON-NLS-1$
        defaultSaslPrincipalLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Search Base Dn Text
        toolkit.createLabel( composite, "Search Base Dn:" );
        saslSearchBaseDnText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( saslSearchBaseDnText, new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslSearchBaseDnLabel = createDefaultValueLabel( toolkit, composite, "ou=users,dc=example,dc=com" ); //$NON-NLS-1$
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

        // Auth Mechanisms NTLM Text
        addDirtyListener( authMechNtlmText );
        addModifyListener( authMechNtlmText, authMechNtlmTextListener );

        // Auth Mechanisms GSS SPNEGO Checkbox
        addDirtyListener( authMechGssSpnegoCheckbox );
        addSelectionListener( authMechGssSpnegoCheckbox, authMechGssSpnegoCheckboxListener );
        addModifyListener( authMechGssSpnegoText, authMechGssSpnegoTextListener );

        // Auth Mechanisms GSS SPNEGO Text
        addDirtyListener( authMechGssSpnegoText );
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

        // Max Time Limit Text
        addDirtyListener( maxTimeLimitText );
        addModifyListener( maxTimeLimitText, maxTimeLimitTextListener );

        // Max Size Limit Text
        addDirtyListener( maxSizeLimitText );
        addModifyListener( maxSizeLimitText, maxSizeLimitTextListener );

        // Enable TLS Checkbox
        addDirtyListener( enableTlsCheckbox );
        addSelectionListener( enableTlsCheckbox, enableTlsCheckboxListener );

        // Hashing Method Checkbox
        addDirtyListener( enableServerSidePasswordHashingCheckbox );
        addSelectionListener( enableServerSidePasswordHashingCheckbox, enableServerSidePasswordHashingCheckboxListener );

        // Hashing Method Combo Viewer
        addDirtyListener( hashingMethodComboViewer );
        addSelectionChangedListener( hashingMethodComboViewer, hashingMethodComboViewerListener );
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

        // Auth Mechanisms NTLM Text
        removeDirtyListener( authMechNtlmText );
        removeModifyListener( authMechNtlmText, authMechNtlmTextListener );

        // Auth Mechanisms GSS SPNEGO Checkbox
        removeDirtyListener( authMechGssSpnegoCheckbox );
        removeSelectionListener( authMechGssSpnegoCheckbox, authMechGssSpnegoCheckboxListener );
        removeModifyListener( authMechGssSpnegoText, authMechGssSpnegoTextListener );

        // Auth Mechanisms GSS SPNEGO Text
        removeDirtyListener( authMechGssSpnegoText );
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

        // Max Time Limit Text
        removeDirtyListener( maxTimeLimitText );
        removeModifyListener( maxTimeLimitText, maxTimeLimitTextListener );

        // Max Size Limit Text
        removeDirtyListener( maxSizeLimitText );
        removeModifyListener( maxSizeLimitText, maxSizeLimitTextListener );

        // Hashing Method Checkbox
        removeDirtyListener( enableServerSidePasswordHashingCheckbox );
        removeSelectionListener( enableServerSidePasswordHashingCheckbox,
            enableServerSidePasswordHashingCheckboxListener );

        // Hashing Method Combo Viewer
        removeDirtyListener( hashingMethodComboViewer );
        removeSelectionChangedListener( hashingMethodComboViewer, hashingMethodComboViewerListener );
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
        setEnabled( ldapPortText, enableLdapCheckbox.getSelection() );
        setText( ldapPortText, ldapServerTransportBean.getSystemPort() + "" ); //$NON-NLS-1$

        // LDAPS Server
        TransportBean ldapsServerTransportBean = getLdapsServerTransportBean();
        setSelection( enableLdapsCheckbox, ldapsServerTransportBean.isEnabled() );
        setEnabled( enableLdapsCheckbox, enableLdapsCheckbox.getSelection() );
        setText( ldapsPortText, ldapsServerTransportBean.getSystemPort() + "" ); //$NON-NLS-1$

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
                setSelection( authMechSimpleCheckbox, saslMechHandler.isEnabled() );
            }
            else if ( SupportedSaslMechanisms.GSSAPI.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                setSelection( authMechGssapiCheckbox, saslMechHandler.isEnabled() );
            }
            if ( SupportedSaslMechanisms.CRAM_MD5.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                setSelection( authMechCramMd5Checkbox, saslMechHandler.isEnabled() );
            }
            else if ( SupportedSaslMechanisms.DIGEST_MD5.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                setSelection( authMechDigestMd5Checkbox, saslMechHandler.isEnabled() );
            }
            else if ( SupportedSaslMechanisms.GSS_SPNEGO.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                setSelection( authMechGssSpnegoCheckbox, saslMechHandler.isEnabled() );
                setEnabled( authMechGssSpnegoText, saslMechHandler.isEnabled() );
                setText( authMechGssSpnegoText, saslMechHandler.getNtlmMechProvider() );
            }
            else if ( SupportedSaslMechanisms.NTLM.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
            {
                setSelection( authMechNtlmCheckbox, saslMechHandler.isEnabled() );
                setEnabled( authMechNtlmText, saslMechHandler.isEnabled() );
                setText( authMechNtlmText, saslMechHandler.getNtlmMechProvider() );
            }
        }

        // Limits
        setText( maxTimeLimitText, "" + ldapServerBean.getLdapServerMaxTimeLimit() ); //$NON-NLS-1$
        setText( maxSizeLimitText, "" + ldapServerBean.getLdapServerMaxSizeLimit() ); //$NON-NLS-1$

        // Enable TLS Checkbox
        setSelection( enableTlsCheckbox, getTlsExtendedOpHandlerBean().isEnabled() );

        // Hashing Method widgets
        InterceptorBean hashingMethodInterceptor = getHashingMethodInterceptor();
        if ( hashingMethodInterceptor == null )
        {
            // No hashing method interceptor
            setSelection( enableServerSidePasswordHashingCheckbox, false );
            setEnabled( hashingMethodComboViewer.getCombo(), enableServerSidePasswordHashingCheckbox.getSelection() );
            setSelection( hashingMethodComboViewer, LdapSecurityConstants.HASH_METHOD_SSHA );
        }
        else
        {
            LdapSecurityConstants hashingMethod = getHashingMethodFromInterceptor( hashingMethodInterceptor );
            if ( hashingMethod != null )
            {
                // Setting selection for the hashing method
                setSelection( enableServerSidePasswordHashingCheckbox, hashingMethodInterceptor.isEnabled() );
                setEnabled( hashingMethodComboViewer.getCombo(), enableServerSidePasswordHashingCheckbox.getSelection() );
                setSelection( hashingMethodComboViewer, hashingMethod );
            }
            else
            {
                // Couldn't determine which hashing method is used
                setSelection( enableServerSidePasswordHashingCheckbox, false );
                setEnabled( hashingMethodComboViewer.getCombo(), enableServerSidePasswordHashingCheckbox.getSelection() );
                setSelection( hashingMethodComboViewer, LdapSecurityConstants.HASH_METHOD_SSHA );
            }
        }

        addListeners();
    }


    /**
     * Unchecks all supported authentication mechanisns checkboxes.
     */
    private void uncheckAllSupportedAuthenticationMechanisms()
    {
        setSelection( authMechSimpleCheckbox, false );
        setSelection( authMechCramMd5Checkbox, false );
        setSelection( authMechDigestMd5Checkbox, false );
        setSelection( authMechGssapiCheckbox, false );
        setSelection( authMechNtlmCheckbox, false );
        setEnabled( authMechNtlmText, false );
        setSelection( authMechGssSpnegoCheckbox, false );
        setEnabled( authMechGssSpnegoText, false );
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


    /**
     * Enables/disables TLS.
     *
     * @param enabled the enabled state
     */
    private void setEnableTls( boolean enabled )
    {
        getTlsExtendedOpHandlerBean().setEnabled( enabled );
    }


    /**
     * Gets the TLS extended operation handler.
     *
     * @return the TLS extended operation handler
     */
    private ExtendedOpHandlerBean getTlsExtendedOpHandlerBean()
    {
        // Getting the LDAP Server
        LdapServerBean ldapServerBean = getLdapServerBean();

        // Getting the list of extended operation handlers
        List<ExtendedOpHandlerBean> extendedOpHandlers = ldapServerBean.getExtendedOps();
        for ( ExtendedOpHandlerBean extendedOpHandlerBean : extendedOpHandlers )
        {
            // Looking for the Start TLS extended operation handler 
            if ( START_TLS_HANDLER_ID.equalsIgnoreCase( extendedOpHandlerBean.getExtendedOpId() ) )
            {
                return extendedOpHandlerBean;
            }
        }

        // We haven't found a corresponding extended operation handler,
        // we need to create it
        ExtendedOpHandlerBean extendedOpHandlerBean = new ExtendedOpHandlerBean();
        extendedOpHandlerBean.setExtendedOpId( START_TLS_HANDLER_ID );
        extendedOpHandlerBean.setExtendedOpHandlerClass( START_TLS_HANDLER_CLASS );
        extendedOpHandlerBean.setEnabled( false );
        extendedOpHandlers.add( extendedOpHandlerBean );
        return extendedOpHandlerBean;
    }


    /**
     * Creates the hashing method interceptor.
     *
     * @param hashingMethod the hashing method
     * @return the corresponding hashing method interceptor
     */
    private InterceptorBean createHashingMethodInterceptor( LdapSecurityConstants hashingMethod )
    {
        InterceptorBean hashingMethodInterceptor = new InterceptorBean();

        switch ( hashingMethod )
        {
            case HASH_METHOD_SHA:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_SHA );
                break;
            case HASH_METHOD_SSHA:
            default:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_SSHA );
                break;
            case HASH_METHOD_MD5:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_MD5 );
                break;
            case HASH_METHOD_SMD5:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_SMD5 );
                break;
            case HASH_METHOD_CRYPT:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_CRYPT );
                break;
            case HASH_METHOD_SHA256:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_SHA256 );
                break;
            case HASH_METHOD_SSHA256:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_SSHA256 );
                break;
            case HASH_METHOD_SHA384:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_SHA384 );
                break;
            case HASH_METHOD_SSHA384:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_SSHA384 );
                break;
            case HASH_METHOD_SHA512:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_SHA512 );
                break;
            case HASH_METHOD_SSHA512:
                hashingMethodInterceptor.setInterceptorClassName( FQCN_HASHING_INTERCEPTOR_SSHA512 );
                break;
        }

        hashingMethodInterceptor.setInterceptorId( getHashingMethodInterceptorId( hashingMethodInterceptor
            .getInterceptorClassName() ) );
        hashingMethodInterceptor.setEnabled( true );

        return hashingMethodInterceptor;
    }


    /**
     * Gets the hashing method interceptor id from the given 
     * fully qualified class name (FQCN).
     *
     * @param fqcn the fqcn
     * @return the hashing method interceptor id
     */
    private String getHashingMethodInterceptorId( String fqcn )
    {
        if ( fqcn != null )
        {
            String id = fqcn.replace( "org.apache.directory.server.core.hash.", "" ); //$NON-NLS-1$ //$NON-NLS-2$

            if ( id.length() > 0 )
            {
                char firstChar = id.charAt( 0 );
                char lowerCasedFirstChar = Character.toLowerCase( firstChar );

                return lowerCasedFirstChar + id.substring( 1 );
            }

            return id;
        }

        return null;
    }


    /**
     * Gets the hashing method interceptor if it can be found.
     *
     * @return the hashing method interceptor, or <code>null</code>
     */
    private InterceptorBean getHashingMethodInterceptor()
    {
        List<InterceptorBean> interceptors = getDirectoryServiceBean().getInterceptors();
        for ( InterceptorBean interceptor : interceptors )
        {
            String interceptorId = interceptor.getInterceptorId();

            if ( interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SHA ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SSHA ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_MD5 ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SMD5 ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_CRYPT ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SHA256 ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SSHA256 ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SHA384 ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SSHA384 ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SHA512 ) )
                || interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SSHA512 ) ) )
            {
                return interceptor;
            }
        }

        return null;
    }


    /**
     * Gets the hashing method from the interceptor.
     *
     * @param interceptor the interceptor
     * @return the hashing method from the interceptor
     */
    private LdapSecurityConstants getHashingMethodFromInterceptor( InterceptorBean interceptor )
    {
        if ( interceptor != null )
        {
            String interceptorId = interceptor.getInterceptorId();

            if ( interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SHA ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SHA;
            }
            else if ( interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SSHA ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SSHA;
            }
            else if ( interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_MD5 ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_MD5;
            }
            else if ( interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SMD5 ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SMD5;
            }
            else if ( interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_CRYPT ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SMD5;
            }
            else if ( interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SHA256 ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SHA256;
            }
            else if ( interceptorId
                .equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SSHA256 ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SSHA256;
            }
            else if ( interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SHA384 ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SHA384;
            }
            else if ( interceptorId
                .equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SSHA384 ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SSHA384;
            }
            else if ( interceptorId.equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SHA512 ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SHA512;
            }
            else if ( interceptorId
                .equalsIgnoreCase( getHashingMethodInterceptorId( FQCN_HASHING_INTERCEPTOR_SSHA512 ) ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SSHA512;
            }
        }

        return null;
    }


    /**
     * Adds a new hashing method interceptor, based on the current selection.
     */
    private void addHashingMethodInterceptor()
    {
        StructuredSelection selection = ( StructuredSelection ) hashingMethodComboViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            LdapSecurityConstants hashingMethod = ( LdapSecurityConstants ) selection.getFirstElement();
            if ( hashingMethod != null )
            {
                // Creating the hashing method interceptor
                InterceptorBean hashingMethodInterceptor = createHashingMethodInterceptor( hashingMethod );

                // Getting the order of the authentication interceptor
                int authenticationInterceptorOrder = getAuthenticationInterceptorOrder();

                // Assigning the order of the hashing method interceptor
                // It's order is: authenticationInterceptorOrder + 1
                hashingMethodInterceptor.setInterceptorOrder( authenticationInterceptorOrder + 1 );

                // Getting the interceptors list
                List<InterceptorBean> interceptors = getDirectoryServiceBean().getInterceptors();

                // Updating the order of the interceptors after the authentication interceptor
                for ( InterceptorBean interceptor : interceptors )
                {
                    if ( interceptor.getInterceptorOrder() > authenticationInterceptorOrder )
                    {
                        interceptor.setInterceptorOrder( interceptor.getInterceptorOrder() + 1 );
                    }
                }

                // Adding the hashing interceptor to the list                
                interceptors.add( hashingMethodInterceptor );
            }
        }
    }


    /**
     * Deletes the hashing method interceptor.
     */
    private void deleteHashingMethodInterceptor()
    {
        // Getting the hashing method interceptor
        InterceptorBean hashingMethodInterceptor = getHashingMethodInterceptor();

        if ( hashingMethodInterceptor != null )
        {
            // Getting the order of the hashing method interceptor
            int hashingMethodInterceptorOrder = hashingMethodInterceptor.getInterceptorOrder();

            // Getting the interceptors list
            List<InterceptorBean> interceptors = getDirectoryServiceBean().getInterceptors();

            // Updating the order of the interceptors after the hashing method interceptor
            for ( InterceptorBean interceptor : interceptors )
            {
                if ( interceptor.getInterceptorOrder() > hashingMethodInterceptorOrder )
                {
                    interceptor.setInterceptorOrder( interceptor.getInterceptorOrder() - 1 );
                }
            }

            // Removing the hashing interceptor to the list                
            interceptors.remove( hashingMethodInterceptor );
        }
    }


    /**
     * Gets the authentication interceptor order.
     *
     * @return the authentication interceptor order
     */
    private int getAuthenticationInterceptorOrder()
    {
        // Getting the list of interceptors
        List<InterceptorBean> interceptors = getDirectoryServiceBean().getInterceptors();
        for ( InterceptorBean interceptor : interceptors )
        {
            // Looking for the authentication interceptor
            if ( "org.apache.directory.server.core.authn.AuthenticationInterceptor".equalsIgnoreCase( interceptor //$NON-NLS-1$
                .getInterceptorClassName() ) )
            {
                // We found the authentication interceptor
                return interceptor.getInterceptorOrder();
            }
        }

        // No authentication interceptor was found
        return 0;
    }
}
