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


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.api.ldap.model.constants.SupportedSaslMechanisms;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.ExtendedOpHandlerBean;
import org.apache.directory.server.config.beans.InterceptorBean;
import org.apache.directory.server.config.beans.LdapServerBean;
import org.apache.directory.server.config.beans.SaslMechHandlerBean;
import org.apache.directory.server.config.beans.TcpTransportBean;
import org.apache.directory.server.config.beans.TransportBean;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
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
 * <pre>
 * +-------------------------------------------------------------------------------+
 * | +------------------------------------+ +------------------------------------+ |
 * | | .--------------------------------. | | .--------------------------------. | |
 * | | |V LDAP/LDAPS servers            | | | |V Supported Authn Mechanisms    | | |
 * | | +--------------------------------+ | | +--------------------------------+ | |
 * | | | [X] Enabled LDAP Server        | | | | [X] Simple      [X] GSSAPI     | | |
 * | | |  Address  : [////////////////] | | | | [X] CRAM-MD5    [X] Digest-MD5 | | |
 * | | |  Port     : [/////////]        | | | | [X] NTLM                       | | |
 * | | |  nbThreads: [/////////]        | | | |   Provider : [///////////////] | | |
 * | | |  backLog  : [/////////]        | | | | [X] GSS_SPNEGO                 | | |
 * | | | [X] Enabled LDAPS Server       | | | |   Provider : [///////////////] | | |
 * | | |  Address  : [////////////////] | | | | [X] Delegated                  | | |
 * | | |  Port     : [/////////]        | | | |   Host    : [////////////////] | | |
 * | | |  nbThreads: [/////////]        | | | |   Port    : [/////]            | | |
 * | | |  backLog  : [/////////]        | | | |   Ssl     : [X]                | | |
 * | | +--------------------------------+ | | |   Base DN : [////////////////] | | |
 * | | .--------------------------------. | | +--------------------------------+ | |
 * | | |V Server limits                 | | | .--------------------------------. | |
 * | | +--------------------------------+ | | |V SASL Settings                 | | |
 * | | |    Max time limit : [////////] | | | +--------------------------------+ | |
 * | | |    Max size limit : [////////] | | | | SASL Host      : [///////////] | | |
 * | | |    Max PDU size   : [////////] | | | | SASL Principal : [///////////] | | |
 * | | +--------------------------------+ | | | Search Base DN : [///////////] | | |
 * | | .--------------------------------. | | | SASL realms    :               | | |
 * | | |V SSL/Start TLS keystore        | | | |   +-----------------+          | | |
 * | | +--------------------------------+ | | |   |                 | (add)    | | |
 * | | |  keystore : [////////] (browse)| | | |   |                 | (edit)   | | |
 * | | |  password : [////////////////] | | | |   |                 | (delete) | | |
 * | | |             [X] Show password  | | | |   +-----------------+          | | |
 * | | +--------------------------------+ | | +--------------------------------+ | |
 * | | .--------------------------------. | |                                    | |
 * | | |V SSL Advanced Settings         | | |                                    | |
 * | | +--------------------------------+ | |                                    | |
 * | | |  [X] Require Client Auth       | | |                                    | |
 * | | |    [X] Request Client Auth     | | |                                    | |
 * | | |  Ciphers suite :               | | |                                    | |
 * | | |   +--------------------------+ | | |                                    | |
 * | | |   |[X] xyz                   | | | |                                    | |
 * | | |   |[X] abc                   | | | |                                    | |
 * | | |   |[X] def                   | | | |                                    | |
 * | | |   +--------------------------+ | | |                                    | |
 * | | | Enabled protocols :            | | |                                    | |
 * | | | [X] SSLv3  [X] TLSv1           | | |                                    | |
 * | | |        [X] TLSv1.1 [X] TLSv1.2 | | |                                    | |
 * | | +--------------------------------+ | |                                    | |
 * | | .--------------------------------. | |                                    | |
 * | | |V Advanced                      | | |                                    | |
 * | | +--------------------------------+ | |                                    | |
 * | | | [X] Enable TLS                 | | |                                    | |
 * | | | [X] Enable ServerSide PWD hash | | |                                    | |
 * | | |      hashing method {========} | | |                                    | |
 * | | | Replication pinger sleep [XXX] | | |                                    | |
 * | | | Disk sync delay [XXX]          | | |                                    | |
 * | | +--------------------------------+ | |                                    | |
 * | +------------------------------------+ +------------------------------------+ |
 * +-------------------------------------------------------------------------------+
 * </pre>
 * 
 * We manage the following parameters :
 * LDAP server controls. We manage :
 * <ul>
 * <li>the address</li>
 * <li>the port</li>
 * <li>the number of dedicated threads</li>
 * <li>the backlog size</li>
 * </ul> 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapLdapsServersPage extends ServerConfigurationEditorPage
{
    private static final int DEFAULT_NB_THREADS = 4;
    private static final int DEFAULT_BACKLOG_SIZE = 50;
    private static final String TRANSPORT_ID_LDAP = "ldap"; //$NON-NLS-1$
    public static final String TRANSPORT_ID_LDAPS = "ldaps"; //$NON-NLS-1$
    private static final String SASL_MECHANISMS_SIMPLE = "SIMPLE"; //$NON-NLS-1$
    private static final String SSL_V3 = "SSLv3";
    private static final String TLS_V1_0 = "TLSv1";
    private static final String TLS_V1_1 = "TLSv1.1";
    private static final String TLS_V1_2 = "TLSv1.2";
    private static final String START_TLS_HANDLER_ID = "starttlshandler"; //$NON-NLS-1$
    private static final String START_TLS_HANDLER_CLASS = "org.apache.directory.server.ldap.handlers.extended.StartTlsHandler"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_ID = "passwordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA512 = "org.apache.directory.server.core.hash.Ssha512PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA512 = "org.apache.directory.server.core.hash.Sha512PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA384 = "org.apache.directory.server.core.hash.Ssha384PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA384 = "org.apache.directory.server.core.hash.Sha384PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA256 = "org.apache.directory.server.core.hash.Ssha256PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA256 = "org.apache.directory.server.core.hash.Sha256PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_CRYPT = "org.apache.directory.server.core.hash.CryptPasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_SMD5 = "org.apache.directory.server.core.hash.Smd5PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_MD5 = "org.apache.directory.server.core.hash.Md5PasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA = "org.apache.directory.server.core.hash.SshaPasswordHashingInterceptor"; //$NON-NLS-1$
    private static final String HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA = "org.apache.directory.server.core.hash.ShaPasswordHashingInterceptor"; //$NON-NLS-1$

    /** The Page ID*/
    public static final String ID = LdapLdapsServersPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "LdapLdapsServersPage.LdapLdapsServers" ); //$NON-NLS-1$

    // UI Controls
    /** 
     * LDAP server controls. We manage :
     * <ul>
     * <li>the address</li>
     * <li>the port</li>
     * <li>the number of dedicated threads</li>
     * <li>the backlog size</li>
     * </ul> 
     **/
    private Button enableLdapCheckbox;
    private Text ldapPortText;
    private Text ldapAddressText;
    private Text ldapNbThreadsText;
    private Text ldapBackLogSizeText;
    
    /** LDAPS server controls */
    private Button enableLdapsCheckbox;
    private Text ldapsPortText;
    private Text ldapsAddressText;
    private Text ldapsNbThreadsText;
    private Text ldapsBackLogSizeText;
    private Button needClientAuthCheckbox;
    private Button wantClientAuthCheckbox;
    private boolean wantClientAuthStatus;
    
    /** The CiphersSuite controls */
    private CheckboxTableViewer ciphersSuiteTableViewer;
    
    /** The EnabledProtocols controls */
    private Button sslv3Checkbox;
    private Button tlsv1_0Checkbox;
    private Button tlsv1_1Checkbox;
    private Button tlsv1_2Checkbox;
    
    /** LDAP limits */
    private Text maxTimeLimitText;
    private Text maxSizeLimitText;
    private Text maxPduSizeText;
    
    /** The supported authentication controls */
    private Button authMechSimpleCheckbox;
    private Button authMechCramMd5Checkbox;
    private Button authMechDigestMd5Checkbox;
    private Button authMechGssapiCheckbox;
    private Button authMechNtlmCheckbox;
    private Text authMechNtlmText;
    private Button authMechGssSpnegoCheckbox;
    private Text authMechGssSpnegoText;

    /** The SASL controls */
    private Text saslHostText;
    private Text saslPrincipalText;
    private Text saslSearchBaseDnText;
    private TableViewer saslRealmsTableViewer;
    private Button addSaslRealmsButton;
    private Button editSaslRealmsButton;
    private Button deleteSaslRealmsButton;
    
    /** The Advanced controls */
    private Button enableTlsCheckbox;
    private Button enableServerSidePasswordHashingCheckbox;
    private ComboViewer hashingMethodComboViewer;
    private Text keystoreFileText;
    private Button keystoreFileBrowseButton;
    private Text keystorePasswordText;
    private Button showPasswordCheckbox;
    private Text replicationPingerSleepText;
    private Text diskSynchronizationDelayText;

    // UI Controls Listeners
    /**
     * The LDAP transport checkbox listener. When checked, we enable the following 
     * widgets :
     * <ul>
     * <li>Port</li>
     * <li>Address</li>
     * <li>NbThreads</li>
     * <li>BackLog</li>
     * </ul>
     */
    private SelectionAdapter enableLdapCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            boolean enabled = enableLdapCheckbox.getSelection();
            
            getLdapServerTransportBean().setEnabled( enabled );
            setEnabled( ldapPortText, enabled );
            setEnabled( ldapAddressText, enabled );
            setEnabled( ldapNbThreadsText, enabled );
            setEnabled( ldapBackLogSizeText, enabled );
        }
    };
    
    
    /**
     * The LDAP port modify listener
     */
    private ModifyListener ldapPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            try
            {
                int port = Integer.parseInt( ldapPortText.getText() );
                
                getLdapServerTransportBean().setSystemPort( port );
            }
            catch ( NumberFormatException nfe )
            {
                System.out.println( "Wrong LDAP TCP Port : it must be an integer" );
            }
        }
    };

    
    /**
     * The LDAP address modify listener
     */
    private ModifyListener ldapAddressTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerTransportBean().setTransportAddress( ldapAddressText.getText() );
        }
    };

    
    /**
     * The LDAP nbThreads modify listener
     */
    private ModifyListener ldapNbThreadsTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            try
            {
                int nbThreads = Integer.parseInt( ldapNbThreadsText.getText() );
                
                getLdapServerTransportBean().setTransportNbThreads( nbThreads );
            }
            catch ( NumberFormatException nfe )
            {
                System.out.println( "Wrong LDAP NbThreads : it must be an integer" );
            }
        }
    };

    
    /**
     * The LDAP BackLogSize modify listener
     */
    private ModifyListener ldapBackLogSizeTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            try
            {
                int backLogSize = Integer.parseInt( ldapBackLogSizeText.getText() );
                
                getLdapServerTransportBean().setTransportBackLog( backLogSize );
            }
            catch ( NumberFormatException nfe )
            {
                System.out.println( "Wrong LDAP BackLog size : it must be an integer" );
            }
        }
    };
    
    
    /**
     * The LDAPS transport checkbox listener. When checked, we enable the following 
     * controls :
     * <ul>
     * <li>Port</li>
     * <li>Address</li>
     * <li>NbThreads</li>
     * <li>BackLog</li>
     * <li>needClientAuth</li>
     * <li>wantClientAuth</li>
     * <li>Cipher suite (and associated buttons)</li>
     * <li>Enabled Protocols (and associated buttons)</li>
     * </ul>
     */
    private SelectionAdapter enableLdapsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            boolean enabled = enableLdapsCheckbox.getSelection();
            
            getLdapsServerTransportBean().setEnabled( enabled );
            setEnabled( ldapsPortText, enabled );
            setEnabled( ldapsAddressText, enabled );
            setEnabled( ldapsNbThreadsText, enabled );
            setEnabled( ldapsBackLogSizeText, enabled );
        }
    };
    
    
    /**
     * The LDAPS port modify listener
     */
    private ModifyListener ldapsPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            try
            {
                int port = Integer.parseInt( ldapsPortText.getText() );
                
                getLdapsServerTransportBean().setSystemPort( port );
            }
            catch ( NumberFormatException nfe )
            {
                System.out.println( "Wrong LDAPS Port : it must be an integer" );
            }
        }
    };
    
    
    /**
     * The LDAPS address modify listener
     */
    private ModifyListener ldapsAddressTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapsServerTransportBean().setTransportAddress( ldapsAddressText.getText() );
        }
    };

    
    /**
     * The LDAPS nbThreads modify listener
     */
    private ModifyListener ldapsNbThreadsTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            try
            {
                int nbThreads = Integer.parseInt( ldapsNbThreadsText.getText() );
                
                getLdapsServerTransportBean().setTransportNbThreads( nbThreads );
            }
            catch ( NumberFormatException nfe )
            {
                System.out.println( "Wrong LDAPS NbThreads : it must be an integer" );
            }
        }
    };

    
    /**
     * The LDAPS BackLogSize modify listener
     */
    private ModifyListener ldapsBackLogSizeTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            try
            {
                int backLogSize = Integer.parseInt( ldapsBackLogSizeText.getText() );
                
                getLdapsServerTransportBean().setTransportBackLog( backLogSize );
            }
            catch ( NumberFormatException nfe )
            {
                System.out.println( "Wrong LDAPS BackLog size : it must be an integer" );
            }
        }
    };
    
    
    /**
     * As listener for the NeedClientAuth checkbox : we have to check the 
     * WantClientAuth checkbox when the NeedClientAuth is selected.
     */
    private SelectionAdapter needClientAuthListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            boolean enabled = needClientAuthCheckbox.getSelection();

            // Inject the flag in the config
            TransportBean ldapTransport = getLdapServerTransportBean();
            
            if ( ldapTransport!= null )
            {
                ldapTransport.setWantClientAuth( enabled );
            }
            
            TransportBean ldapsTransport = getLdapsServerTransportBean();
            
            if ( ldapsTransport!= null )
            {
                ldapsTransport.setWantClientAuth( enabled );
            }

            // Turn on/off the NeedClientAuth
            if ( enabled )
            {
                wantClientAuthCheckbox.setSelection( enabled );
            }
            else
            {
                // restore the previous value
                wantClientAuthCheckbox.setSelection( wantClientAuthStatus );
            }
            
            // And disable it or enable it
            setEnabled( wantClientAuthCheckbox, !enabled );
            
            // last, 
        }
    };
    
    
    /**
     * As listener for the WantClientAuth checkbox
     */
    private SelectionAdapter wantClientAuthListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            boolean enabled = wantClientAuthCheckbox.getSelection();

            // Inject the flag in the config - for all the transports, as
            // it may be for SSL or startTLS - 
            TransportBean ldapTransport =  getLdapServerTransportBean();
            
            if ( ldapTransport != null )
            {
                ldapTransport.setWantClientAuth( enabled );
            }

            TransportBean ldapsTransport =  getLdapsServerTransportBean();
            
            if ( ldapsTransport != null )
            {
                ldapsTransport.setWantClientAuth( enabled );
            }

            // Keep a track of the WantClientAuth flag
            wantClientAuthStatus = enabled;
        }
    };

    
    /**
     * The SASL Host modify listener
     */
    private ModifyListener saslHostTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setLdapServerSaslHost( saslHostText.getText() );
        }
    };
    
    
    /**
     * The SASL principal modify listener
     */
    private ModifyListener saslPrincipalTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setLdapServerSaslPrincipal( saslPrincipalText.getText() );
        }
    };

    
    /**
     * The SASL search Base DN modify listener
     */
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
    
    
    /**
     * SASL realms Table change
     */
    private ISelectionChangedListener saslRealmsTableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) saslRealmsTableViewer.getSelection();

            editSaslRealmsButton.setEnabled( !selection.isEmpty() );
            deleteSaslRealmsButton.setEnabled( !selection.isEmpty() );
        }
    };
    
    
    /**
     * SaslRealms Table double-click
     */
    private IDoubleClickListener saslRealmsTableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editSaslRealmsAction();
        }
    };
    

    /**
     * Add SASL realms button
     */
    private SelectionListener addSaslRealmsButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            InputDialog dialog = new InputDialog( editSaslRealmsButton.getShell(),
                Messages.getString( "LdapLdapsServersPage.Add" ), //$NON-NLS-1$
                Messages.getString( "LdapLdapsServersPage.SaslRealms" ), //$NON-NLS-1$
                null, null );

            if ( dialog.open() == InputDialog.OK )
            {
                String newSaslRealms = dialog.getValue();

                getLdapServerBean().addSaslRealms( newSaslRealms );

                saslRealmsTableViewer.refresh();
                saslRealmsTableViewer.setSelection( new StructuredSelection( newSaslRealms ) );

                setEditorDirty();
            }
        }
    };
    
    
    /**
     * Edit SASL realms button
     */
    private SelectionListener editSaslRealmsButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editSaslRealmsAction();
        }
    };
    
    
    /**
     * Delete SASL realms button
     */
    private SelectionListener deleteSaslRealmsButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            String selectedSaslRealms = getSelectedSaslRealms();

            if ( selectedSaslRealms != null )
            {
                getLdapServerBean().getLdapServerSaslRealms().remove( selectedSaslRealms );
                saslRealmsTableViewer.refresh();

                setEditorDirty();
            }
        }
    };

    
    /**
     * The AuthMech Simple checkbox listener
     */
    private SelectionAdapter authMechSimpleCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SASL_MECHANISMS_SIMPLE, authMechSimpleCheckbox.getSelection() );
        };
    };
    
    
    /**
     * The AuthMech GSSAPI checkbox listener
     */
    private SelectionAdapter authMechGssapiCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.GSSAPI,
                authMechGssapiCheckbox.getSelection() );
        };
    };

    
    /**
     * The AuthMech CRAM-MD5 checkbox listener
     */
    private SelectionAdapter authMechCramMd5CheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.CRAM_MD5,
                authMechCramMd5Checkbox.getSelection() );
        };
    };
    
    
    /**
     * The AuthMech Digest MD5 checkbox listener
     */
    private SelectionAdapter authMechDigestMd5CheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.DIGEST_MD5,
                authMechDigestMd5Checkbox.getSelection() );
        };
    };
    
    
    /**
     * The AuthMech GSS-SPNEGO checkbox listener
     */
    private SelectionAdapter authMechGssSpnegoCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.GSS_SPNEGO,
                authMechGssSpnegoCheckbox.getSelection() );
            setEnabled( authMechGssSpnegoText, authMechGssSpnegoCheckbox.getSelection() );
        };
    };

    
    /**
     * The AuthMech GSS-SPNEGO text listener
     */
    private ModifyListener authMechGssSpnegoTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            setNtlmMechProviderSupportedAuthenticationMechanism( SupportedSaslMechanisms.GSS_SPNEGO,
                authMechGssSpnegoText.getText() );
        }
    };
    
    
    /**
     * The AuthMech NTLM checkbox listener
     */
    private SelectionAdapter authMechNtlmCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableSupportedAuthenticationMechanism( SupportedSaslMechanisms.NTLM,
                authMechNtlmCheckbox.getSelection() );
            setEnabled( authMechNtlmText, authMechNtlmCheckbox.getSelection() );
        };
    };
    
    
    /**
     * The AuthMech NTLM  text listener
     */
    private ModifyListener authMechNtlmTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            setNtlmMechProviderSupportedAuthenticationMechanism( SupportedSaslMechanisms.NTLM,
                authMechNtlmText.getText() );
        }
    };
    
    
    /**
     * The maximum time for a SearchRequest's response
     */
    private ModifyListener maxTimeLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setLdapServerMaxTimeLimit( Integer.parseInt( maxTimeLimitText.getText() ) );
        }
    };
    
    
    /**
     * The maximum size for a SearchRequest's response
     */
    private ModifyListener maxSizeLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setLdapServerMaxSizeLimit( Integer.parseInt( maxSizeLimitText.getText() ) );
        }
    };
    
    
    /**
     * The maximum size for a request PDU
     */
    private ModifyListener maxPduSizeTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setMaxPDUSize( Integer.parseInt( maxPduSizeText.getText() ) );
        }
    };
    
    
    /**
     * Tells if TLS is enabled
     */
    private SelectionAdapter enableTlsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEnableTls( enableTlsCheckbox.getSelection() );
        }
    };
    
    
    /**
     * Tell the server to hash the passwords
     */
    private SelectionAdapter enableServerSidePasswordHashingCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            if ( enableServerSidePasswordHashingCheckbox.getSelection() )
            {
                enableHashingPasswordInterceptor();
            }
            else
            {
                disableHashingPasswordInterceptor();
            }

            setEnabled( hashingMethodComboViewer.getCombo(), enableServerSidePasswordHashingCheckbox.getSelection() );
        }
    };
    
    
    /**
     * The list of method to use to hash the passwords
     */
    private ISelectionChangedListener hashingMethodComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            updateHashingMethod();
        }
    };
    
    
    /**
     * The keyStore file listener
     */
    private ModifyListener keystoreFileTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String keystoreFile = keystoreFileText.getText();

            if ( ( keystoreFile == null ) || ( keystoreFile.length() == 0 ) )
            {
                getLdapServerBean().setLdapServerKeystoreFile( null );
            }
            else
            {
                getLdapServerBean().setLdapServerKeystoreFile( keystoreFile );
            }
        }
    };
    
    
    /**
     * Let the user browse the disk to find the keystore file
     */
    private SelectionListener keystoreFileBrowseButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent event )
        {
            FileDialog fileDialog = new FileDialog( keystoreFileBrowseButton.getShell(), SWT.OPEN );

            File file = new File( keystoreFileText.getText() );
            
            if ( file.isFile() )
            {
                fileDialog.setFilterPath( file.getParent() );
                fileDialog.setFileName( file.getName() );
            }
            else if ( file.isDirectory() )
            {
                fileDialog.setFilterPath( file.getPath() );
            }
            else
            {
                fileDialog.setFilterPath( null );
            }

            String returnedFileName = fileDialog.open();
            
            if ( returnedFileName != null )
            {
                keystoreFileText.setText( returnedFileName );
                setEditorDirty();
            }
        }
    };
    
    
    /**
     * The keystore password listener
     */
    private ModifyListener keystorePasswordTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String keystorePassword = keystorePasswordText.getText();

            if ( ( keystorePassword == null ) || ( keystorePassword.length() == 0 ) )
            {
                getLdapServerBean().setLdapServerCertificatePassword( null );
            }
            else
            {
                getLdapServerBean().setLdapServerCertificatePassword( keystorePassword );
            }
        }
    };
    
    
    /**
     * The keystore password checkbox listener
     */
    private SelectionListener showPasswordCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            if ( showPasswordCheckbox.getSelection() )
            {
                keystorePasswordText.setEchoChar( '\0' );
            }
            else
            {
                keystorePasswordText.setEchoChar( '\u2022' );
            }
        }
    };

    
    /**
     * Ciphers Suite Table change
     */
    private ICheckStateListener ciphersSuiteTableViewerListener = new ICheckStateListener()
    {
        public void checkStateChanged( CheckStateChangedEvent event )
        {
            TransportBean transport = getLdapTransportBean( TRANSPORT_ID_LDAP );
            
            if ( transport == null )
            {
                transport = getLdapTransportBean( TRANSPORT_ID_LDAPS );
            }
            
            if ( transport == null )
            {
                // TODO : the list should be disabled
                return;
            }
            
            // Checking if the last cipher is being unchecked
            if ( transport.getEnabledCiphers() == null )
            {
                // Ok, we don't have any selected cipher, which means all of them are selected
                transport.setEnabledCiphers( SupportedCipher.supportedCipherNamesJava8 );
            }
            if ( ( transport.getEnabledCiphers().size() == 1 ) && ( event.getChecked() == false ) )
            {
                // Displaying an error to the user
                CommonUIUtils.openErrorDialog( Messages
                    .getString( "LdapLdapsServersPage.AtLeastOneCipherMustBeSelected" ) );

                // Reverting the current checked state
                ciphersSuiteTableViewer.setChecked( event.getElement(), !event.getChecked() );

                // Exiting
                return;
            }

            // Setting the editor as dirty
            setEditorDirty();

            // Clearing previous cipher suite
            transport.getEnabledCiphers().clear();

            // Getting all selected encryption types
            Object[] selectedCipherObjects = ciphersSuiteTableViewer.getCheckedElements();

            // Adding each selected cipher
            for ( Object cipher : selectedCipherObjects )
            {
                if ( cipher instanceof SupportedCipher )
                {
                    SupportedCipher supportedCipher = ( SupportedCipher ) cipher;

                    transport.getEnabledCiphers().add( supportedCipher.getCipher() );
                }
            }
        }
    };
    
    
    /**
     * Enable SSLV3
     */
    private SelectionAdapter sslv3CheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setProtocol( sslv3Checkbox.getSelection(), "SSLv3" );
        }
    };
    
    
    /**
     * Enable TLS V1
     */
    private SelectionAdapter tlsv1_0CheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setProtocol( tlsv1_0Checkbox.getSelection(), "TLSV1" );
        }
    };
    
    
    /**
     * Enable TLS V1.1
     */
    private SelectionAdapter tlsv1_1CheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setProtocol( tlsv1_1Checkbox.getSelection(), "TLSV1.1" );
        }
    };
    
    
    /**
     * Enable TLS V1.2
     */
    private SelectionAdapter tlsv1_2CheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setProtocol( tlsv1_2Checkbox.getSelection(), "TLSV1.2" );
        }
    };

    
    /**
     * The replication ping Sleep modify listener
     */
    private ModifyListener replicationPingerSleepTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getLdapServerBean().setReplPingerSleep( Integer.parseInt( replicationPingerSleepText.getText() ) );
        }
    };
    
    
    /**
     * The disk synchronization delay modify listener
     */
    private ModifyListener diskSynchronizationDelayTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getDirectoryServiceBean().setDsSyncPeriodMillis( Long.parseLong( diskSynchronizationDelayText.getText() ) );
        }
    };


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor the associated editor
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
        twl.makeColumnsEqualWidth = true;
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
        createSslStartTlsKeystoreSection( toolkit, leftComposite );
        createSslAdvancedSettingsSection( toolkit, leftComposite );
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
        // Creation of the section, expanded
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        section.setText( Messages.getString( "LdapLdapsServersPage.LdapLdapsServers" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 4, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Enable LDAP Server Checkbox
        enableLdapCheckbox = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.EnableLdapServer" ), SWT.CHECK ); //$NON-NLS-1$
        enableLdapCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );

        // LDAP Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.Port" ) ); //$NON-NLS-1$
        ldapPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, Integer.toString( DEFAULT_PORT_LDAP ) );

        // LDAP Server Address Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.Address" ) ); //$NON-NLS-1$
        ldapAddressText = createAddressText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, DEFAULT_ADDRESS );

        // LDAP Server nbThreads Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.NbThreads" ) ); //$NON-NLS-1$
        ldapNbThreadsText = createNbThreadsText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite,  Integer.toString( DEFAULT_NB_THREADS ) );

        // LDAP Server backlog Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.BackLogSize" ) ); //$NON-NLS-1$
        ldapBackLogSizeText = createBackLogSizeText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite,  Integer.toString( DEFAULT_BACKLOG_SIZE ) );

        // Enable LDAPS Server Checkbox
        enableLdapsCheckbox = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.EnableLdapsServer" ), SWT.CHECK ); //$NON-NLS-1$
        enableLdapsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );

        // LDAPS Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.Port" ) ); //$NON-NLS-1$
        ldapsPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, Integer.toString( DEFAULT_PORT_LDAPS ) );

        // LDAPS Server Address Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.Address" ) ); //$NON-NLS-1$
        ldapsAddressText = createAddressText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, DEFAULT_ADDRESS );

        // LDAPS Server nbThreads Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.NbThreads" ) ); //$NON-NLS-1$
        ldapsNbThreadsText = createNbThreadsText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, Integer.toString( DEFAULT_NB_THREADS ) );

        // LDAPS Server backlog Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.BackLogSize" ) ); //$NON-NLS-1$
        ldapsBackLogSizeText = createBackLogSizeText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, Integer.toString( DEFAULT_BACKLOG_SIZE ) );
    }


    /**
     * Creates the Limits Section
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createLimitsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section, compacted
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.TWISTIE | Section.COMPACT );
        section.setText( Messages.getString( "LdapLdapsServersPage.Limits" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Max. Time Limit Text
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.MaxTimeLimit" ) ); //$NON-NLS-1$
        maxTimeLimitText = createIntegerText( toolkit, composite );
        maxTimeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Max. Size Limit Text
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.MaxSizeLimit" ) ); //$NON-NLS-1$
        maxSizeLimitText = createIntegerText( toolkit, composite );
        maxSizeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Max. PDU Size Text
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.MaxPduSize" ) ); //$NON-NLS-1$
        maxPduSizeText = createIntegerText( toolkit, composite );
        maxPduSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the SSL/Start TLS Section
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createSslStartTlsKeystoreSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section, compacted
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.TWISTIE | Section.COMPACT );
        section.setText( Messages.getString( "LdapLdapsServersPage.SslStartTlsKeystore" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 3, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Keystore File Text
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.Keystore" ) ); //$NON-NLS-1$
        keystoreFileText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( keystoreFileText, new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        keystoreFileBrowseButton = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.Browse" ), SWT.PUSH ); //$NON-NLS-1$

        // Password Text
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.Password" ) ); //$NON-NLS-1$
        keystorePasswordText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        keystorePasswordText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        keystorePasswordText.setEchoChar( '\u2022' );

        // Show Password Checkbox
        toolkit.createLabel( composite, "" ); //$NON-NLS-1$
        showPasswordCheckbox = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.ShowPassword" ), SWT.CHECK ); //$NON-NLS-1$
        showPasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        showPasswordCheckbox.setSelection( false );
    }


    /**
     * Creates the SSL/Start TLS Section. We will deal with the following parameters :
     * <ul>
     * <li>needClientAuth</li>
     * <li>wantClientAuth</li>
     * <li>enabledProtocols</li>
     * <li>enabledCiphersSuite</li>
     * </ul>
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createSslAdvancedSettingsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section, compacted
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.TWISTIE | Section.COMPACT );
        section.setText( Messages.getString( "LdapLdapsServersPage.SslAdvancedSettings" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 4, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Enable LDAPS needClientAuth Checkbox
        needClientAuthCheckbox = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.NeedClientAuth" ), SWT.CHECK ); //$NON-NLS-1$
        needClientAuthCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 1 ) );

        // Enable LDAPS wantClientAuth Checkbox. As the WantClientAuth is dependent on
        // the NeedClientAuth, we move it one column to the right
        toolkit.createLabel( composite, TABULATION );
        wantClientAuthCheckbox = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.WantClientAuth" ), SWT.CHECK ); //$NON-NLS-1$
        wantClientAuthCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Ciphers Suite label 
        Label ciphersLabel = toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.CiphersSuite" ), SWT.WRAP  ); //$NON-NLS-1$
        setBold( ciphersLabel );
        ciphersLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, glayout.numColumns, 1 ) );

        // Ciphers Suites Table Viewer
        ciphersSuiteTableViewer = new CheckboxTableViewer( new Table( composite, SWT.BORDER | SWT.CHECK ) );
        ciphersSuiteTableViewer.setContentProvider( new ArrayContentProvider() );
        ciphersSuiteTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object cipher )
            {
                if ( cipher instanceof SupportedCipher )
                {
                    SupportedCipher supportedCipher = ( SupportedCipher ) cipher;

                    return supportedCipher.getCipher();
                }

                return super.getText( cipher );
            }
        } );
        
        List<SupportedCipher> supportedCiphers = new ArrayList<SupportedCipher>();
        
        for ( SupportedCipher supportedCipher : SupportedCipher.SUPPORTED_CIPHERS )
        {
            if ( supportedCipher.isJava8Implemented() )
            {
                supportedCiphers.add( supportedCipher );
            }
        }
        
        ciphersSuiteTableViewer.setInput( supportedCiphers );
        GridData ciphersSuiteTableViewerGridData = new GridData( SWT.FILL, SWT.NONE, true, false, glayout.numColumns, 5 );
        ciphersSuiteTableViewerGridData.heightHint = 60;
        ciphersSuiteTableViewer.getControl().setLayoutData( ciphersSuiteTableViewerGridData );

        // Enabled Protocols label 
        Label protocolsLabel = toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.EnabledProtocols" ), SWT.WRAP  ); //$NON-NLS-1$
        setBold( protocolsLabel );
        protocolsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, glayout.numColumns, 1 ) );

        // Enabled Protocols
        // SSL V3
        sslv3Checkbox = toolkit.createButton( composite, "SSLv3", SWT.CHECK ); //$NON-NLS-1$
        sslv3Checkbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // TLS 1.0
        tlsv1_0Checkbox = toolkit.createButton( composite, "TLSv1", SWT.CHECK ); //$NON-NLS-1$
        tlsv1_0Checkbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // TLS 1.1
        tlsv1_1Checkbox = toolkit.createButton( composite, "TLSv1.1", SWT.CHECK ); //$NON-NLS-1$
        tlsv1_1Checkbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        
        // TLS 1.2
        tlsv1_2Checkbox = toolkit.createButton( composite, "TLSv1.2", SWT.CHECK ); //$NON-NLS-1$
        tlsv1_2Checkbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    }


    /**
     * Creates the Advanced Section
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createAdvancedSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.TWISTIE | Section.COMPACT );
        section.setText( Messages.getString( "LdapLdapsServersPage.Advanced" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Enable TLS Checkbox
        enableTlsCheckbox = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.EnableTls" ), SWT.CHECK ); //$NON-NLS-1$
        enableTlsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Enable Server-side Password Hashing Checkbox
        enableServerSidePasswordHashingCheckbox = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.EnableServerSidePasswordHashing" ), //$NON-NLS-1$
            SWT.CHECK );
        enableServerSidePasswordHashingCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Server-side Password Hashing Composite 
        Composite hashingMethodComposite = toolkit.createComposite( composite );
        hashingMethodComposite.setLayout( new GridLayout( 3, false ) );
        hashingMethodComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Server-side Password Hashing Combo
        toolkit.createLabel( hashingMethodComposite, Messages.getString( "LdapLdapsServersPage.HashingMethod" ) ); //$NON-NLS-1$
        Combo hashingMethodCombo = new Combo( hashingMethodComposite, SWT.READ_ONLY | SWT.SINGLE );
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

                    return hashingMethod.getName();
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
                LdapSecurityConstants.HASH_METHOD_SSHA512,
                LdapSecurityConstants.HASH_METHOD_PKCS5S2
        };
        
        hashingMethodComboViewer.setInput( hashingMethods );
        setSelection( hashingMethodComboViewer, LdapSecurityConstants.HASH_METHOD_SSHA );
        toolkit.createLabel( hashingMethodComposite, "   " ); //$NON-NLS-1$
        Label defaultLabel = createDefaultValueLabel( toolkit, hashingMethodComposite, "SSHA" ); //$NON-NLS-1$
        defaultLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Replication Pinger Sleep
        toolkit.createLabel( composite, "Replication Pinger Sleep (sec):" );
        replicationPingerSleepText = createIntegerText( toolkit, composite );
        replicationPingerSleepText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Disk Synchronization Delay
        toolkit.createLabel( composite, "Disk Synchronization Delay (ms):" );
        diskSynchronizationDelayText = createIntegerText( toolkit, composite );
        diskSynchronizationDelayText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Supported Authentication Mechanisms Section
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createSupportedAuthenticationMechanismsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "LdapLdapsServersPage.SupportedAuthenticationMechanisms" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        toolkit.paintBordersFor( composite );
        composite.setLayout( new GridLayout( 2, true ) );
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
        toolkit.createLabel( composite, "" ); //$NON-NLS-1$
        Composite authMechNtlmComposite = toolkit.createComposite( composite );
        authMechNtlmComposite.setLayout( new GridLayout( 3, false ) );
        toolkit.createLabel( authMechNtlmComposite, "   " ); //$NON-NLS-1$
        toolkit.createLabel( authMechNtlmComposite, Messages.getString( "LdapLdapsServersPage.Provider" ) ); //$NON-NLS-1$
        authMechNtlmText = toolkit.createText( authMechNtlmComposite, "" ); //$NON-NLS-1$
        authMechNtlmText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        authMechNtlmComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false, 2, 1 ) );

        // GSS-SPNEGO Checkbox and Text
        authMechGssSpnegoCheckbox = toolkit.createButton( composite, "GSS-SPNEGO", SWT.CHECK ); //$NON-NLS-1$
        authMechGssSpnegoCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        toolkit.createLabel( composite, "" ); //$NON-NLS-1$
        Composite authMechGssSpnegoComposite = toolkit.createComposite( composite );
        authMechGssSpnegoComposite.setLayout( new GridLayout( 3, false ) );
        toolkit.createLabel( authMechGssSpnegoComposite, "   " ); //$NON-NLS-1$
        toolkit.createLabel( authMechGssSpnegoComposite, Messages.getString( "LdapLdapsServersPage.Provider" ) ); //$NON-NLS-1$
        authMechGssSpnegoText = toolkit.createText( authMechGssSpnegoComposite, "" ); //$NON-NLS-1$
        authMechGssSpnegoText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        authMechGssSpnegoComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    }


    /**
     * Creates the SASL Settings Section
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createSaslSettingsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.TWISTIE | Section.COMPACT );
        section.setText( Messages.getString( "LdapLdapsServersPage.SaslSettings" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 3, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // SASL Host Text
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.SaslHost" ) ); //$NON-NLS-1$
        saslHostText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( saslHostText, new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        Label defaultSaslHostLabel = createDefaultValueLabel( toolkit, composite, "ldap.example.com" ); //$NON-NLS-1$
        defaultSaslHostLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // SASL Principal Text
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.SaslPrincipal" ) ); //$NON-NLS-1$
        saslPrincipalText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( saslPrincipalText, new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        Label defaultSaslPrincipalLabel = createDefaultValueLabel( toolkit, composite,
            "ldap/ldap.example.com@EXAMPLE.COM" ); //$NON-NLS-1$
        defaultSaslPrincipalLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Search Base Dn Text
        toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.SearchBaseDn" ) ); //$NON-NLS-1$
        saslSearchBaseDnText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        setGridDataWithDefaultWidth( saslSearchBaseDnText, new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        Label defaultSaslSearchBaseDnLabel = createDefaultValueLabel( toolkit, composite, "ou=users,dc=example,dc=com" ); //$NON-NLS-1$
        defaultSaslSearchBaseDnLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // SASL Realms label 
        Label saslRealmsLabel = toolkit.createLabel( composite, Messages.getString( "LdapLdapsServersPage.SaslRealms" ), SWT.WRAP  ); //$NON-NLS-1$
        setBold( saslRealmsLabel );
        saslRealmsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, glayout.numColumns, 1 ) );

        // SASL realms Table Viewer
        saslRealmsTableViewer = new TableViewer( composite );
        saslRealmsTableViewer.setContentProvider( new ArrayContentProvider() );
        GridData saslRealmsTableViewerGridData = new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 3 );
        saslRealmsTableViewerGridData.heightHint = 60;
        saslRealmsTableViewer.getControl().setLayoutData( saslRealmsTableViewerGridData );

        // Add SASL realms Button
        addSaslRealmsButton = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.Add" ), SWT.PUSH ); //$NON-NLS-1$
        addSaslRealmsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false, 1, 1 ) );

        // Edit SASL realms Button
        editSaslRealmsButton = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.Edit" ), SWT.PUSH ); //$NON-NLS-1$
        editSaslRealmsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false, 1, 1 ) );
        editSaslRealmsButton.setEnabled( false );

        // Delete SASL realms Button
        deleteSaslRealmsButton = toolkit.createButton( composite,
            Messages.getString( "LdapLdapsServersPage.Delete" ), SWT.PUSH ); //$NON-NLS-1$
        deleteSaslRealmsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false, 1, 1 ) );
        deleteSaslRealmsButton.setEnabled( false );
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

        // LDAP Address Text
        addDirtyListener( ldapAddressText );
        addModifyListener( ldapAddressText, ldapAddressTextListener );

        // LDAP nbThreads Text
        addDirtyListener( ldapNbThreadsText );
        addModifyListener( ldapNbThreadsText, ldapNbThreadsTextListener );

        // LDAP BackLogSize Text
        addDirtyListener( ldapBackLogSizeText );
        addModifyListener( ldapBackLogSizeText, ldapBackLogSizeTextListener );

        // Enable LDAPS Checkbox
        addDirtyListener( enableLdapsCheckbox );
        addSelectionListener( enableLdapsCheckbox, enableLdapsCheckboxListener );

        // LDAPS Address Text
        addDirtyListener( ldapsAddressText );
        addModifyListener( ldapsAddressText, ldapsAddressTextListener );

        // LDAPS Port Text
        addDirtyListener( ldapsPortText );
        addModifyListener( ldapsPortText, ldapsPortTextListener );

        // LDAPS nbThreads Text
        addDirtyListener( ldapsNbThreadsText );
        addModifyListener( ldapsNbThreadsText, ldapsNbThreadsTextListener );

        // LDAPS BackLogSize Text
        addDirtyListener( ldapsBackLogSizeText );
        addModifyListener( ldapsBackLogSizeText, ldapsBackLogSizeTextListener );
        
        // Enable wantClientAuth Checkbox
        addDirtyListener( wantClientAuthCheckbox );
        addSelectionListener( wantClientAuthCheckbox, wantClientAuthListener );

        // Enable needClientAuth Checkbox
        addDirtyListener( needClientAuthCheckbox );
        addSelectionListener( needClientAuthCheckbox, needClientAuthListener );

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

        // Keystore File Text
        addDirtyListener( keystoreFileText );
        addModifyListener( keystoreFileText, keystoreFileTextListener );

        // Keystore File Browse Button
        addSelectionListener( keystoreFileBrowseButton, keystoreFileBrowseButtonSelectionListener );

        // Password Text
        addDirtyListener( keystorePasswordText );
        addModifyListener( keystorePasswordText, keystorePasswordTextListener );

        // Show Password Checkbox
        addSelectionListener( showPasswordCheckbox, showPasswordCheckboxSelectionListener );

        // SASL Host Text
        addDirtyListener( saslHostText );
        addModifyListener( saslHostText, saslHostTextListener );

        // SASL Principal Text
        addDirtyListener( saslPrincipalText );
        addModifyListener( saslPrincipalText, saslPrincipalTextListener );

        // SASL Seach Base Dn Text
        addDirtyListener( saslSearchBaseDnText );
        addModifyListener( saslSearchBaseDnText, saslSearchBaseDnTextListener );

        // SASL Realms Table Viewer
        addSelectionChangedListener( saslRealmsTableViewer, saslRealmsTableViewerSelectionChangedListener );
        addDoubleClickListener( saslRealmsTableViewer, saslRealmsTableViewerDoubleClickListener );
        addSelectionListener( editSaslRealmsButton, editSaslRealmsButtonListener );
        addSelectionListener( addSaslRealmsButton, addSaslRealmsButtonListener );
        addSelectionListener( deleteSaslRealmsButton, deleteSaslRealmsButtonListener );

        // Max Time Limit Text
        addDirtyListener( maxTimeLimitText );
        addModifyListener( maxTimeLimitText, maxTimeLimitTextListener );

        // Max Size Limit Text
        addDirtyListener( maxSizeLimitText );
        addModifyListener( maxSizeLimitText, maxSizeLimitTextListener );

        // Max PDU Size Text
        addDirtyListener( maxPduSizeText );
        addModifyListener( maxPduSizeText, maxPduSizeTextListener );

        // Enable TLS Checkbox
        addDirtyListener( enableTlsCheckbox );
        addSelectionListener( enableTlsCheckbox, enableTlsCheckboxListener );

        // Hashing Password Checkbox
        addDirtyListener( enableServerSidePasswordHashingCheckbox );
        addSelectionListener( enableServerSidePasswordHashingCheckbox, enableServerSidePasswordHashingCheckboxListener );

        // Hashing Method Combo Viewer
        addDirtyListener( hashingMethodComboViewer );
        addSelectionChangedListener( hashingMethodComboViewer, hashingMethodComboViewerListener );

        // Advanced SSL Cipher Suites
        ciphersSuiteTableViewer.addCheckStateListener( ciphersSuiteTableViewerListener );

        // Advanced SSL Enabled Protocols
        // Enable sslv3 Checkbox
        addDirtyListener( sslv3Checkbox );
        addSelectionListener( sslv3Checkbox, sslv3CheckboxListener );

        // Enable tlsv1 Checkbox
        addDirtyListener( tlsv1_0Checkbox );
        addSelectionListener( tlsv1_0Checkbox, tlsv1_0CheckboxListener );

        // Enable tlsv1.1 Checkbox
        addDirtyListener( tlsv1_1Checkbox );
        addSelectionListener( tlsv1_1Checkbox, tlsv1_1CheckboxListener );

        // Enable tlsv1.2 Checkbox
        addDirtyListener( tlsv1_2Checkbox );
        addSelectionListener( tlsv1_2Checkbox, tlsv1_2CheckboxListener );

        // Replication Pinger Sleep
        addDirtyListener( replicationPingerSleepText );
        addModifyListener( replicationPingerSleepText, replicationPingerSleepTextListener );

        // Disk Synchronization Delay
        addDirtyListener( diskSynchronizationDelayText );
        addModifyListener( diskSynchronizationDelayText, diskSynchronizationDelayTextListener );
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

        // LDAP Address Text
        removeDirtyListener( ldapAddressText );
        removeModifyListener( ldapAddressText, ldapAddressTextListener );

        // LDAP NbThreads Text
        removeDirtyListener( ldapNbThreadsText );
        removeModifyListener( ldapNbThreadsText, ldapNbThreadsTextListener );

        // LDAP BackLogSize Text
        removeDirtyListener( ldapBackLogSizeText );
        removeModifyListener( ldapBackLogSizeText, ldapBackLogSizeTextListener );

        // Enable LDAPS Checkbox
        removeDirtyListener( enableLdapsCheckbox );
        removeSelectionListener( enableLdapsCheckbox, enableLdapsCheckboxListener );

        // LDAPS Port Text
        removeDirtyListener( ldapsPortText );
        removeModifyListener( ldapsPortText, ldapsPortTextListener );

        // LDAPS Address Text
        removeDirtyListener( ldapsAddressText );
        removeModifyListener( ldapsAddressText, ldapsAddressTextListener );

        // LDAPS NbThreads Text
        removeDirtyListener( ldapsNbThreadsText );
        removeModifyListener( ldapsNbThreadsText, ldapsNbThreadsTextListener );

        // LDAPS BackLogSize Text
        removeDirtyListener( ldapsBackLogSizeText );
        removeModifyListener( ldapsBackLogSizeText, ldapsBackLogSizeTextListener );
        
        // Enable wantClientAuth Checkbox
        removeDirtyListener( wantClientAuthCheckbox );
        removeSelectionListener( wantClientAuthCheckbox, wantClientAuthListener );

        // Enable needClientAuth Checkbox
        removeDirtyListener( needClientAuthCheckbox );
        removeSelectionListener( needClientAuthCheckbox, needClientAuthListener );

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

        // Keystore File Text
        removeDirtyListener( keystoreFileText );
        removeModifyListener( keystoreFileText, keystoreFileTextListener );

        // Keystore File Browse Button
        removeSelectionListener( keystoreFileBrowseButton, keystoreFileBrowseButtonSelectionListener );

        // Password Text
        removeDirtyListener( keystorePasswordText );
        removeModifyListener( keystorePasswordText, keystorePasswordTextListener );

        // Show Password Checkbox
        removeSelectionListener( showPasswordCheckbox, showPasswordCheckboxSelectionListener );

        // SASL Host Text
        removeDirtyListener( saslHostText );
        removeModifyListener( saslHostText, saslHostTextListener );

        // SASL Principal Text
        removeDirtyListener( saslPrincipalText );
        removeModifyListener( saslPrincipalText, saslPrincipalTextListener );

        // SASL Seach Base Dn Text
        removeDirtyListener( saslSearchBaseDnText );
        removeModifyListener( saslSearchBaseDnText, saslSearchBaseDnTextListener );
        
        // SASL Realms
        removeSelectionChangedListener( saslRealmsTableViewer, saslRealmsTableViewerSelectionChangedListener );
        removeDoubleClickListener( saslRealmsTableViewer, saslRealmsTableViewerDoubleClickListener );
        
        // SASL Realms add/edit/delete buttons
        removeSelectionListener( addSaslRealmsButton, addSaslRealmsButtonListener );
        removeSelectionListener( editSaslRealmsButton, editSaslRealmsButtonListener );
        removeSelectionListener( deleteSaslRealmsButton, deleteSaslRealmsButtonListener );
        
        // Max Time Limit Text
        removeDirtyListener( maxTimeLimitText );
        removeModifyListener( maxTimeLimitText, maxTimeLimitTextListener );

        // Max Size Limit Text
        removeDirtyListener( maxSizeLimitText );
        removeModifyListener( maxSizeLimitText, maxSizeLimitTextListener );

        // Max PDU Size Text
        removeDirtyListener( maxPduSizeText );
        removeModifyListener( maxPduSizeText, maxPduSizeTextListener );

        // Hashing Password Checkbox
        removeDirtyListener( enableServerSidePasswordHashingCheckbox );
        removeSelectionListener( enableServerSidePasswordHashingCheckbox,
            enableServerSidePasswordHashingCheckboxListener );

        // Hashing Method Combo Viewer
        removeDirtyListener( hashingMethodComboViewer );
        removeSelectionChangedListener( hashingMethodComboViewer, hashingMethodComboViewerListener );

        // Advanced SSL Cipher Suites
        ciphersSuiteTableViewer.removeCheckStateListener( ciphersSuiteTableViewerListener );

        // Advanced SSL Enabled Protocols SSL v3
        removeDirtyListener( sslv3Checkbox );
        removeSelectionListener( sslv3Checkbox, sslv3CheckboxListener );

        // Advanced SSL Enabled Protocols TLS v1
        removeDirtyListener( tlsv1_0Checkbox );
        removeSelectionListener( tlsv1_0Checkbox, tlsv1_0CheckboxListener );

        // Advanced SSL Enabled Protocols TLS v1.1
        removeDirtyListener( tlsv1_1Checkbox );
        removeSelectionListener( tlsv1_1Checkbox, tlsv1_1CheckboxListener );

        // Advanced SSL Enabled Protocols TLS v1.2
        removeDirtyListener( tlsv1_2Checkbox );
        removeSelectionListener( tlsv1_2Checkbox, tlsv1_2CheckboxListener );


        // Advanced SSL Enabled Protocols add/edit/delete buttons removal

        // Replication Pinger Sleep
        removeDirtyListener( replicationPingerSleepText );
        removeModifyListener( replicationPingerSleepText, replicationPingerSleepTextListener );

        // Disk Synchronization Delay
        removeDirtyListener( diskSynchronizationDelayText );
        removeModifyListener( diskSynchronizationDelayText, diskSynchronizationDelayTextListener );
    }


    /**
     * {@inheritDoc}
     */
    protected void refreshUI()
    {
        if ( isInitialized() )
        {
            removeListeners();

            // LDAP Server ------------------------------------------------------------------------
            TransportBean ldapServerTransportBean = getLdapServerTransportBean();
            setSelection( enableLdapCheckbox, ldapServerTransportBean.isEnabled() );
            
            boolean ldapEnabled = enableLdapCheckbox.getSelection();

            setEnabled( ldapPortText, ldapEnabled );
            setText( ldapPortText, Integer.toString( ldapServerTransportBean.getSystemPort() ) );

            setEnabled( ldapAddressText, ldapEnabled );
            setText( ldapAddressText, ldapServerTransportBean.getTransportAddress() );

            setEnabled( ldapNbThreadsText, ldapEnabled );
            setText( ldapNbThreadsText, Integer.toString( ldapServerTransportBean.getTransportNbThreads() ) );
            
            setEnabled( ldapBackLogSizeText, ldapEnabled );
            setText( ldapBackLogSizeText, Integer.toString( ldapServerTransportBean.getTransportBackLog() ) );

            // LDAPS Server -----------------------------------------------------------------------
            TransportBean ldapsServerTransportBean = getLdapsServerTransportBean();
            setSelection( enableLdapsCheckbox, ldapsServerTransportBean.isEnabled() );

            boolean ldapsEnabled = enableLdapsCheckbox.getSelection();

            setEnabled( ldapsPortText, ldapsEnabled );
            setText( ldapsPortText, Integer.toString( ldapsServerTransportBean.getSystemPort() ) );

            setEnabled( ldapsAddressText, ldapsEnabled );
            setText( ldapsAddressText, ldapsServerTransportBean.getTransportAddress() );

            setEnabled( ldapsNbThreadsText, ldapsEnabled );
            setText( ldapsNbThreadsText, Integer.toString( ldapsServerTransportBean.getTransportNbThreads() ) );
            
            setEnabled( ldapsBackLogSizeText, ldapsEnabled );
            setText( ldapsBackLogSizeText, Integer.toString( ldapsServerTransportBean.getTransportBackLog() ) );

            // SASL Properties --------------------------------------------------------------------
            LdapServerBean ldapServerBean = getLdapServerBean();
            setText( saslHostText, ldapServerBean.getLdapServerSaslHost() );
            setText( saslPrincipalText, ldapServerBean.getLdapServerSaslPrincipal() );
            setText( saslSearchBaseDnText, ldapServerBean.getSearchBaseDn().toString() );
            saslRealmsTableViewer.setInput( ldapServerBean.getLdapServerSaslRealms() );
            saslRealmsTableViewer.refresh();

            // Keystore Properties
            setText( keystoreFileText, ldapServerBean.getLdapServerKeystoreFile() );
            setText( keystorePasswordText, ldapServerBean.getLdapServerCertificatePassword() );

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
                else if ( SupportedSaslMechanisms.CRAM_MD5.equalsIgnoreCase( saslMechHandler.getSaslMechName() ) )
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
            setText( maxTimeLimitText, Integer.toString( ldapServerBean.getLdapServerMaxTimeLimit() ) );
            setText( maxSizeLimitText, Integer.toString( ldapServerBean.getLdapServerMaxSizeLimit() ) );
            setText( maxPduSizeText, Integer.toString( ldapServerBean.getMaxPDUSize() ) );

            // Enable TLS Checkbox
            setSelection( enableTlsCheckbox, getTlsExtendedOpHandlerBean().isEnabled() );

            // Hashing Password widgets
            InterceptorBean hashingMethodInterceptor = getHashingPasswordInterceptor();
            
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
                    setEnabled( hashingMethodComboViewer.getCombo(),
                        enableServerSidePasswordHashingCheckbox.getSelection() );
                    setSelection( hashingMethodComboViewer, hashingMethod );
                }
                else
                {
                    // Couldn't determine which hashing method is used
                    setSelection( enableServerSidePasswordHashingCheckbox, false );
                    setEnabled( hashingMethodComboViewer.getCombo(),
                        enableServerSidePasswordHashingCheckbox.getSelection() );
                    setSelection( hashingMethodComboViewer, LdapSecurityConstants.HASH_METHOD_SSHA );
                }
            }

            // SSL/Start TLS Cipher Suites
            List<String> enabledCiphers = ldapServerTransportBean.getEnabledCiphers();
            List<SupportedCipher> supportedCiphers = new ArrayList<SupportedCipher>();
            
            if ( enabledCiphers == null )
            {
                // We don't have any selected ciphers. Propose the full list
                for ( SupportedCipher cipher : SupportedCipher.supportedCiphersJava8 )
                {
                    supportedCiphers.add( cipher );
                }
            }
            else
            {
                for ( String supportedCipher : enabledCiphers )
                {
                    SupportedCipher cipher = SupportedCipher.getByName( supportedCipher );

                    if ( cipher != null )
                    {
                        supportedCiphers.add( cipher );
                    }
                }
            }
            
            ciphersSuiteTableViewer.setCheckedElements( supportedCiphers.toArray() );
            ciphersSuiteTableViewer.refresh();
            
            // SSL/Start TLS Enabled Protocols
            // Check if we have a LDAP transport
            TransportBean transportBean = getLdapTransportBean( TRANSPORT_ID_LDAP );
            
            if ( transportBean == null )
            {
                // No LDAP transport, check the LDAPS transport
                transportBean = getLdapTransportBean( TRANSPORT_ID_LDAPS );
            }
            
            if ( transportBean != null )
            {
                // Ok, process the enabled protocols now
                List<String> enabledProtocols = transportBean.getEnabledProtocols();
                
                if ( enabledProtocols != null )
                {
                    for ( String enabledProtocol : transportBean.getEnabledProtocols() )
                    {
                        if ( SSL_V3.equalsIgnoreCase( enabledProtocol ) )
                        {
                            setSelection( sslv3Checkbox, true );
                        }
                        else if ( TLS_V1_0.equalsIgnoreCase( enabledProtocol ) )
                        {
                            setSelection( tlsv1_0Checkbox, true );
                        }
                        else if ( TLS_V1_1.equalsIgnoreCase( enabledProtocol ) )
                        {
                            setSelection( tlsv1_1Checkbox, true );
                        }
                        else if ( TLS_V1_2.equalsIgnoreCase( enabledProtocol ) )
                        {
                            setSelection( tlsv1_2Checkbox, true );
                        }
                    }
                }
            }
            
            // Replication Pinger Sleep
            setText( replicationPingerSleepText, Integer.toString( ldapServerBean.getReplPingerSleep() ) );

            // Disk Synchronization Delay
            setText( diskSynchronizationDelayText, Long.toString( getDirectoryServiceBean().getDsSyncPeriodMillis() ) );

            addListeners();
        }
    }


    /**
     * Unchecks all supported authentication mechanisms checkboxes.
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
     * @return the LDAP Server bean
     */
    private LdapServerBean getLdapServerBean()
    {
        return getLdapServerBean( getDirectoryServiceBean() );
    }


    /**
     * Gets the LDAP Server bean for a given DirectoryService, or create a new one.
     *
     * @param directoryServiceBean the directory service bean
     * @return the LDAP Server bean
     */
    public static LdapServerBean getLdapServerBean( DirectoryServiceBean directoryServiceBean )
    {
        LdapServerBean ldapServerBean = directoryServiceBean.getLdapServerBean();

        if ( ldapServerBean == null )
        {
            // We don't have any LdapServer associated with this DirectoryService, create one
            ldapServerBean = new LdapServerBean();
            directoryServiceBean.addServers( ldapServerBean );
        }

        return ldapServerBean;
    }


    /**
     * Gets the LDAP Server transport bean.
     *
     * @return the LDAP Server transport bean
     */
    private TransportBean getLdapServerTransportBean()
    {
        return getLdapTransportBean( TRANSPORT_ID_LDAP );
    }


    /**
     * Gets the LDAP Server transport bean.
     *
     * @param directoryServiceBean the directory service bean
     * @return the LDAP Server transport bean
     */
    public static TransportBean getLdapServerTransportBean( DirectoryServiceBean directoryServiceBean )
    {
        return getLdapTransportBean( directoryServiceBean, TRANSPORT_ID_LDAP );
    }


    /**
     * Gets the LDAPS Server transport bean.
     *
     * @return the LDAPS Server transport bean
     */
    private TransportBean getLdapsServerTransportBean()
    {
        return getLdapTransportBean( TRANSPORT_ID_LDAPS );
    }


    /**
     * Gets the LDAPS Server transport bean.
     *
     * @param directoryServiceBean the directory service bean
     * @return the LDAPS Server transport bean
     */
    public static TransportBean getLdapsServerTransportBean( DirectoryServiceBean directoryServiceBean )
    {
        return getLdapTransportBean( directoryServiceBean, TRANSPORT_ID_LDAPS );
    }


    /**
     * Gets a LDAP transport bean based on its id.
     *
     * @param id the transport id
     * @return the corresponding transport bean
     */
    private TransportBean getLdapTransportBean( String id )
    {
        return getLdapTransportBean( getDirectoryServiceBean(), id );
    }


    /**
     * Gets a LDAP server transport bean based on its id
     *
     * @param directoryServiceBean the directory service bean
     * @param id the transport id
     * @return the corresponding transport bean
     */
    public static TransportBean getLdapTransportBean( DirectoryServiceBean directoryServiceBean, String id )
    {
        // First fetch the LdapServer bean
        LdapServerBean ldapServerBean = getLdapServerBean( directoryServiceBean );

        TransportBean transportBean = null;

        // Looking for the transports for this server
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
            // Creating a TCP transport bean
            transportBean = new TcpTransportBean();
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
     * Gets the hashing password interceptor if it can be found.
     *
     * @return the hashing password interceptor, or <code>null</code>
     */
    private InterceptorBean getHashingPasswordInterceptor()
    {
        // Looking for the password hashing interceptor
        for ( InterceptorBean interceptor : getDirectoryServiceBean().getInterceptors() )
        {
            if ( HASHING_PASSWORD_INTERCEPTOR_ID.equalsIgnoreCase( interceptor.getInterceptorId() ) )
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
            String interceptorClassName = interceptor.getInterceptorClassName();

            if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SHA;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SSHA;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_MD5 ) )
            {
                return LdapSecurityConstants.HASH_METHOD_MD5;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_SMD5 ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SMD5;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_CRYPT ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SMD5;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA256 ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SHA256;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA256 ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SSHA256;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA384 ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SHA384;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA384 ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SSHA384;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA512 ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SHA512;
            }
            else if ( interceptorClassName.equalsIgnoreCase( HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA512 ) )
            {
                return LdapSecurityConstants.HASH_METHOD_SSHA512;
            }
        }

        return null;
    }


    /**
     * Gets the key derivation interceptor order.
     *
     * @return the key derivation interceptor order
     */
    private int getKeyDerivationInterceptorOrder()
    {
        // Looking for the key derivation interceptor
        for ( InterceptorBean interceptor : getDirectoryServiceBean().getInterceptors() )
        {
            if ( "keyDerivationInterceptor".equalsIgnoreCase( interceptor.getInterceptorId() ) ) //$NON-NLS-1$
            {
                return interceptor.getInterceptorOrder();
            }
        }

        // No key derivation interceptor was found
        return 0;
    }


    /**
     * Enables the hashing password interceptor.
     */
    private void enableHashingPasswordInterceptor()
    {
        // Getting the hashing password interceptor
        InterceptorBean hashingPasswordInterceptor = getHashingPasswordInterceptor();

        // If we didn't found one, we need to create it
        if ( hashingPasswordInterceptor == null )
        {
            // Creating a new hashing password interceptor
            hashingPasswordInterceptor = createHashingPasswordInterceptor();
        }

        // Enabling the interceptor
        hashingPasswordInterceptor.setEnabled( true );
    }


    /**
     * Creates a new hashing password interceptor.
     *
     * @return a new hashing password interceptor
     */
    private InterceptorBean createHashingPasswordInterceptor()
    {
        InterceptorBean hashingPasswordInterceptor = new InterceptorBean();

        // Interceptor ID
        hashingPasswordInterceptor.setInterceptorId( HASHING_PASSWORD_INTERCEPTOR_ID );

        // Interceptor FQCN
        hashingPasswordInterceptor.setInterceptorClassName( getFqcnForHashingMethod( getSelectedHashingMethod() ) );

        // Getting the order of the key derivation interceptor
        int keyDerivationInterceptorOrder = getKeyDerivationInterceptorOrder();

        // Assigning the order of the hashing password interceptor
        // It's order is: keyDerivationInterceptorOrder + 1
        hashingPasswordInterceptor.setInterceptorOrder( keyDerivationInterceptorOrder + 1 );

        // Updating the order of the interceptors after the key derivation interceptor
        for ( InterceptorBean interceptor : getDirectoryServiceBean().getInterceptors() )
        {
            if ( interceptor.getInterceptorOrder() > keyDerivationInterceptorOrder )
            {
                interceptor.setInterceptorOrder( interceptor.getInterceptorOrder() + 1 );
            }
        }

        // Adding the hashing password interceptor            
        getDirectoryServiceBean().addInterceptors( hashingPasswordInterceptor );

        return hashingPasswordInterceptor;
    }


    /**
     * Disables the hashing password interceptor.
     */
    private void disableHashingPasswordInterceptor()
    {
        // Getting the hashing password interceptor
        InterceptorBean hashingPasswordInterceptor = getHashingPasswordInterceptor();

        if ( hashingPasswordInterceptor != null )
        {
            // Disabling the interceptor
            hashingPasswordInterceptor.setEnabled( false );
        }
    }
    
    
    /**
     * Update the hashingPassword inteceptor with the selected hashing method
     */
    private void updateHashingMethod()
    {
        // Getting the hashing password interceptor
        InterceptorBean hashingPasswordInterceptor = getHashingPasswordInterceptor();

        if ( hashingPasswordInterceptor != null )
        {
            // Updating the hashing method
            hashingPasswordInterceptor.setInterceptorClassName( getFqcnForHashingMethod( getSelectedHashingMethod() ) );
        }
    }


    /**
     * Gets the FQCN for the given hashing method.
     *
     * @param hashingMethod the hashing method
     * @return the corresponding FQCN
     */
    private String getFqcnForHashingMethod( LdapSecurityConstants hashingMethod )
    {
        switch ( hashingMethod )
        {
            case HASH_METHOD_MD5:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_MD5;
                
            case HASH_METHOD_SMD5:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_SMD5;
                
            case HASH_METHOD_CRYPT:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_CRYPT;
                
            case HASH_METHOD_SHA256:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA256;
                
            case HASH_METHOD_SSHA256:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA256;
                
            case HASH_METHOD_SHA384:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA384;
                
            case HASH_METHOD_SSHA384:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA384;
                
            case HASH_METHOD_SHA512:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA512;
                
            case HASH_METHOD_SSHA512:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA512;
                
            case HASH_METHOD_SHA:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_SHA;
                
            case HASH_METHOD_SSHA:
            default:
                return HASHING_PASSWORD_INTERCEPTOR_FQCN_SSHA;
        }
    }
    
    


    /**
     * Gets the selected hashing method.
     *
     * @return the selected hashing method
     */
    private LdapSecurityConstants getSelectedHashingMethod()
    {
        StructuredSelection selection = ( StructuredSelection ) hashingMethodComboViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            return ( LdapSecurityConstants ) selection.getFirstElement();
        }

        return null;
    }
    
    


    /**
     * Enables/disables SSLV3.
     *
     * @param enabled the enabled state
     */
    private void setProtocol( boolean enabled, String protocol )
    {
        if ( enabled )
        {
            // We have to compute the new list of enabled protocols
            List<String> enabledProtocols = getLdapTransportBean( TRANSPORT_ID_LDAP ).getEnabledProtocols();
            
            if ( enabledProtocols == null )
            {
                enabledProtocols = new ArrayList<String>();
            }
            
            if ( !enabledProtocols.contains( protocol ) )
            {
                enabledProtocols.add( protocol );
            }
            
            getLdapTransportBean( TRANSPORT_ID_LDAP ).setEnabledProtocols( enabledProtocols );
            getLdapTransportBean( TRANSPORT_ID_LDAPS ).setEnabledProtocols( enabledProtocols );
        }
        else
        {
            // We have to compute the new list of enabled protocols
            List<String> enabledProtocols = getLdapTransportBean( TRANSPORT_ID_LDAP ).getEnabledProtocols();
            
            enabledProtocols.remove( protocol );
            getLdapTransportBean( TRANSPORT_ID_LDAP ).setEnabledProtocols( enabledProtocols );
            getLdapTransportBean( TRANSPORT_ID_LDAPS ).setEnabledProtocols( enabledProtocols );
        }
    }


    /**
     * Gets the first SASL realms Table 
     *
     * @return the first Enabled Protocols Table
     */
    private String getSelectedSaslRealms()
    {
        StructuredSelection selection = ( StructuredSelection ) saslRealmsTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            return ( String ) selection.getFirstElement();
        }

        return null;
    }


    /**
     * This method is called when the edit Sasl realms button is clicked,
     * or when the table viewer is double clicked.
     */
    private void editSaslRealmsAction()
    {
        String selectedSaslRealms = getSelectedSaslRealms();

        if ( selectedSaslRealms != null )
        {
            InputDialog dialog = new InputDialog( editSaslRealmsButton.getShell(),
                Messages.getString( "LdapLdapsServersPage.Edit" ), //$NON-NLS-1$
                Messages.getString( "LdapLdapsServersPage.SaslRealms" ), //$NON-NLS-1$
                selectedSaslRealms, null );

            if ( dialog.open() == InputDialog.OK )
            {
                String newSaslRealms = dialog.getValue();

                getLdapServerBean().getLdapServerSaslRealms().remove( selectedSaslRealms );
                getLdapServerBean().addSaslRealms( newSaslRealms );

                saslRealmsTableViewer.refresh();
                saslRealmsTableViewer.setSelection( new StructuredSelection( newSaslRealms ) );

                setEditorDirty();
            }
        }
    }
}
