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
package org.apache.directory.studio.openldap.config.editor.pages;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.wrappers.SsfWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.TcpBufferWrapperLabelProvider;


/**
 * This class represents the Security Page of the Server Configuration Editor. It covers
 * the TLS configuration, the SASL configuration and some othe rseci-urity parameters.
 * <ul>
 *   <li> TLS :
 *     <ul>
 *       <li>olcTLSCACertificateFile</li>
 *       <li>olcTLSCACertificatePath</li>
 *       <li>olcTLSCertificateFile</li>
 *       <li>olcTLSCertificateKeyFile</li>
 *       <li>olcTLSCipherSuite</li>
 *       <li>olcTLSCrlCheck</li>
 *       <li>olcTLSCrlFile</li>
 *       <li>olcTLSDhParamFile</li>
 *       <li>olcTLSProtocolMin</li>
 *       <li>olcTLSRandFile</li>
 *       <li>olcTLSVerifyClient></li>
 *     </ul>
 *   </li>
 *   <li> SASL :
 *     <ul>
 *       <li>olcSaslAuxProps</li>
 *       <li>olcSaslHost</li>
 *       <li>olcSaslRealm</li>
 *       <li>olcSaslSecProps</li>
 *     </ul>
 *   </li>
 *   <li> Miscellaneous :
 *     <ul>
 *       <li>olcLocalSsf</li>
 *       <li>olcPasswordCryptSaltFormat</li>
 *       <li>olcPasswordHash</li>
 *       <li>olcSecurity</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <pre>
 *   +------------------------------------------------------------------------------------------------------+
 *   | Security Configuration                                                                               |
 *   +------------------------------------------------------------------------------------------------------+
 *   | .-----------------------------------------------. .------------------------------------------------. |
 *   | |V TLS Configuration                            | |V SASL  Configuration                           | |
 *   | +-----------------------------------------------+ +------------------------------------------------+ |
 *   | | TLS Certificate File :     [                ] | | SASL Host :                [                 ] | |
 *   | | TLS Certificate Key File : [                ] | | SASL Realm :               [                 ] | |
 *   | | TLS CA Certificate File :  [                ] | | SASL Auxprops plugin :     [                 ] | |
 *   | | TLS CA Certificate Path :  [                ] | | SASL Security Properties : [                 ] | |
 *   | | TLS Cipher Suite  :        [                ] | +------------------------------------------------+ |
 *   | | TLS CRL Check  :           [=============]    | .------------------------------------------------. |
 *   | | TLS CRL File  :            [                ] | |V Miscellaneous Security Parameters             | |
 *   | | TLS DH Parameter File :    [                ] | +------------------------------------------------+ |
 *   | | TLS Minimum Protocol  :    [=============]    | | Local SSF : [   ]  pWD Crypt Salt : [        ] | |
 *   | | TLS Random Bits File  :    [                ] | |                                                | |
 *   | | TLS Verify Client  :       [=============]    | | Password Hash  :                               | |
 *   | +-----------------------------------------------+ | +----------------------------------+           | |
 *   |                                                   | |                                  | (Add)     | |
 *   |                                                   | |                                  | (Edit)    | |
 *   |                                                   | |                                  | (Delete)  | |
 *   |                                                   | +----------------------------------+           | |
 *   |                                                   | Security  :                                    | |
 *   |                                                   | +----------------------------------+           | |
 *   |                                                   | |                                  | (Add)     | |
 *   |                                                   | |                                  | (Edit)    | |
 *   |                                                   | |                                  | (Delete)  | |
 *   |                                                   | +----------------------------------+           | |
 *   |                                                   +------------------------------------------------+ |
 *   +------------------------------------------------------------------------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SecurityPage extends OpenLDAPServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = SecurityPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "OpenLDAPSecurityPage.Title" ); //$NON-NLS-1$"Overview";"Security";

    // UI Controls for the TLS part
    /** The olcTLSCACertificateFile Text */
    private Text tlsCaCertificateFileText;

    /** The olcTLSCACertificatePath Text */
    private Text tlsCaCertificatePathText;

    /** The olcTLSCertificateFile Text */
    private Text tlsCertificateFileText;

    /** The olcTLSCertificateKeyFile Text */
    private Text tlsCertificateKeyFileText;

    /** The olcTLSCipherSuite Text */
    private Text tlsCipherSuiteText;

    /** The olcTLSCrlCheck Text */
    private Combo tlsCrlCheckCombo;

    /** The olcTLSCrlFile Text */
    private Text tlsCrlFileText;

    /** The olcTLSDhParamFile Text */
    private Text tlsDhParamFileText;

    /** The olcTLSProtocolMin Text */
    private Combo tlsProtocolMinCombo;

    /** The olcTLSRandFile Text */
    private Text tlsRandFileText;

    /** The olcTLSVerifyClient Text */
    private Combo tlsVerifyClientCombo;

    // UI Controls for the SASL part
    /** The olcSaslAuxProps */
    private Text saslAuxPropsText;

    /** The olcSaslHost */
    private Text saslHostText;

    /** The olcSaslRealm */
    private Text saslRealmText;

    /** The olcSaslSecProps */
    private Text saslSecPropsText;
    
    // UI Controls for the Misc part
    /** The olcLocalSSF */
    private Text localSsfText;

    /** The olcPasswordCryptSaltFormat */
    private Text passwordCryptSaltFormatText;

    /** The olcPasswordHash */
    private TableWidget<String> passwordHashTableWidget;

    /** The olcSecurity table widget */
    private TableWidget<SsfWrapper> securityTableWidget;

    /** The CRL Checks */
    private static final String[] crlChecks = new String[]
        {
        "---",
        "none",
        "peer",
        "all"
        };

    /** The list of supported protocols */
    private static final String[] protocols = new String[]
        {
        "---",
        "3.0",
        "3.1",
        "3.2"
        };

    /** The list of VerifyClient choices protocols */
    private static final String[] verifyClients = new String[]
        {
        "---",
        "never",
        "allow",
        "try",
        "demand",
        "hard",
        "true"
        };
    
    /**
     * Creates a new instance of SecurityPage.
     *
     * @param editor the associated editor
     */
    public SecurityPage( OpenLDAPServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * Creates the OpenLDAP Security config Tab. It contains 2 rows, with
     * 2 columns :
     * 
     * <pre>
     * +-----------------------------------+---------------------------------+
     * |                                   |                                 |
     * |                                   |              SASL               |
     * |                                   |                                 |
     * |              TLS                  +---------------------------------+
     * |                                   |                                 |
     * |                                   |         miscellaneous           |
     * |                                   |                                 |
     * +-----------------------------------+---------------------------------+
     * </pre>
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        TableWrapLayout twl = new TableWrapLayout();
        twl.numColumns = 2;
        twl.makeColumnsEqualWidth = true;
        parent.setLayout( twl );

        // The TLS part
        Composite tlsComposite = toolkit.createComposite( parent );
        tlsComposite.setLayout( new GridLayout() );
        TableWrapData tlsCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 2, 1 );
        tlsCompositeTableWrapData.grabHorizontal = true;
        tlsComposite.setLayoutData( tlsCompositeTableWrapData );

        // The SASL part
        Composite saslComposite = toolkit.createComposite( parent );
        saslComposite.setLayout( new GridLayout() );
        TableWrapData saslCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        saslCompositeTableWrapData.grabHorizontal = true;
        saslComposite.setLayoutData( saslCompositeTableWrapData );

        // The MISC part
        Composite miscComposite = toolkit.createComposite( parent );
        miscComposite.setLayout( new GridLayout() );
        TableWrapData miscCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        miscCompositeTableWrapData.grabHorizontal = true;
        miscComposite.setLayoutData( miscCompositeTableWrapData );

        // Now, create the sections
        createTlsSection( toolkit, tlsComposite );
        createSaslSection( toolkit, saslComposite );
        createMiscSection( toolkit, miscComposite );
    }


    /**
     * Creates the TLS section. This section is a grid with 4 columns,
     * <ul>
     * <li>olcTLSCACertificateFile</li>
     * <li>olcTLSCACertificatePath</li>
     * <li>olcTLSCertificateFile</li>
     * <li>olcTLSCertificateKeyFile</li>
     * <li>olcTLSCipherSuite</li>
     * <li>olcTLSCrlCheck</li>
     * <li>olcTLSCrlFile</li>
     * <li>olcTLSDhParamFile</li>
     * <li>olcTLSProtocolMin</li>
     * <li>olcTLSRandFile</li>
     * <li>olcTLSVerifyClient></li>
     * </ul>
     * 
     * <pre>
     * .-----------------------------------------------.
     * |V TLS parameters                               |
     * +-----------------------------------------------+
     * |                                               |
     * | TLS Certificate File :     [                ] |
     * | TLS Certificate Key File : [                ] |
     * | TLS CA Certificate File :  [                ] |
     * | TLS CA Certificate Path :  [                ] |
     * | TLS Cipher Suite  :        [                ] |
     * | TLS CRL Check  :           [=============]    |
     * | TLS CRL File  :            [                ] |
     * | TLS DH Parameter File :    [                ] |
     * | TLS Minimum Protocol  :    [=============]    |
     * | TLS Random Bits File  :    [                ] |
     * | TLS Verify Client  :       [=============]    |
     * +-----------------------------------------------+
     * </pre>
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createTlsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPSecurityPage.TlsSection" ) );

        // The content
        Composite tlsSectionComposite = createSectionComposite( toolkit, section, 2, false );

        // The tlsCertificateFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCertificateFile" ) ); //$NON-NLS-1$
        tlsCertificateFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsCertificateFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The tlsCertificateKeyFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCertificateKeyFile" ) ); //$NON-NLS-1$
        tlsCertificateKeyFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsCertificateKeyFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The tlsCaCertificateFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCACertificateFile" ) ); //$NON-NLS-1$
        tlsCaCertificateFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsCaCertificateFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The tlsCaCertificatePath parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCACertificatePath" ) ); //$NON-NLS-1$
        tlsCaCertificatePathText = toolkit.createText( tlsSectionComposite, "" );
        tlsCaCertificatePathText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The tlsCipherSuite parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCipherSuite" ) ); //$NON-NLS-1$
        tlsCipherSuiteText = toolkit.createText( tlsSectionComposite, "" );
        tlsCipherSuiteText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The tlsCRLCheck parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCRLCheck" ) ); //$NON-NLS-1$
        tlsCrlCheckCombo = BaseWidgetUtils.createCombo( tlsSectionComposite, crlChecks, -1, 1 );
        //tlsCipherSuiteText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The tlsCRLFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCRLFile" ) ); //$NON-NLS-1$
        tlsCrlFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsCrlFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The tlsDHParamFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSDHParamFile" ) ); //$NON-NLS-1$
        tlsDhParamFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsDhParamFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The tlsProtocolMin parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSProtocolMin" ) ); //$NON-NLS-1$
        tlsProtocolMinCombo = BaseWidgetUtils.createCombo( tlsSectionComposite, protocols, -1, 1 );

        // The tlsRandFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSRandFile" ) ); //$NON-NLS-1$
        tlsRandFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsRandFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The tlsProtocolMin parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSVerifyClient" ) ); //$NON-NLS-1$
        tlsVerifyClientCombo = BaseWidgetUtils.createCombo( tlsSectionComposite, verifyClients, -1, 1 );
    }


    /**
     * Creates the SASL section. This section is a grid with 4 columns,
     * <ul>
     * <li>olcSaslAuxProps</li>
     * <li>olcSaslHost</li>
     * <li>olcSaslRealm</li>
     * <li>olcSaslSecProps</li>
     * </ul>
     * 
     * <pre>
     * .-----------------------------------------------.
     * |V SASL Parameters                              |
     * +-----------------------------------------------+
     * | SASL Host :                [                ] |
     * | SASL Realm :               [                ] |
     * | SASL Auxprops plugin :     [                ] |
     * | SASL Security Properties : [                ] |
     * +-----------------------------------------------+
     * </pre>
     * 
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createSaslSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPSecurityPage.SaslSection" ) );

        // The content
        Composite saslSectionComposite = createSectionComposite( toolkit, section, 2, false );

        // The saslHost parameter
        toolkit.createLabel( saslSectionComposite, Messages.getString( "OpenLDAPSecurityPage.SaslHost" ) ); //$NON-NLS-1$
        saslHostText = toolkit.createText( saslSectionComposite, "" );
        saslHostText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The saslRealm parameter
        toolkit.createLabel( saslSectionComposite, Messages.getString( "OpenLDAPSecurityPage.SaslRealm" ) ); //$NON-NLS-1$
        saslRealmText = toolkit.createText( saslSectionComposite, "" );
        saslRealmText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The saslAuxProps parameter
        toolkit.createLabel( saslSectionComposite, Messages.getString( "OpenLDAPSecurityPage.SaslAuxProps" ) ); //$NON-NLS-1$
        saslAuxPropsText = toolkit.createText( saslSectionComposite, "" );
        saslAuxPropsText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );

        // The saslSecProps parameter
        toolkit.createLabel( saslSectionComposite, Messages.getString( "OpenLDAPSecurityPage.SaslSecProps" ) ); //$NON-NLS-1$
        saslSecPropsText = toolkit.createText( saslSectionComposite, "" );
        saslSecPropsText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );
    }


    /**
     * Creates the Misc section. This section is a grid with 4 columns,
     * <ul>
     * <li>olcLocalSsf</li>
     * <li>olcPasswordCryptSaltFormat</li>
     * <li>olcPasswordHash</li>
     * <li>olcSecurity</li>
     * </ul>
     * 
     * <pre>
     * .-------------------------------------------------------------------------------.
     * |V Miscellaneous Security Parameters                                            |
     * +-------------------------------------------------------------------------------+
     * | Local SSF : [   ]  Password Crypt Salt format : [                           ] |
     * |                                                                               |
     * | Password Hash  :                                                              |
     * | +-----------------------------------------------------------------+           |
     * | |                                                                 | (Add)     |
     * | |                                                                 | (Edit)    |
     * | |                                                                 | (Delete)  |
     * | +-----------------------------------------------------------------+           |
     * | Security  :                                                                   |
     * | +-----------------------------------------------------------------+           |
     * | |                                                                 | (Add)     |
     * | |                                                                 | (Edit)    |
     * | |                                                                 | (Delete)  |
     * | +-----------------------------------------------------------------+           |
     * +-------------------------------------------------------------------------------+
     * </pre>
     * 
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createMiscSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPSecurityPage.MiscSection" ) );

        // The content
        Composite miscSectionComposite = createSectionComposite( toolkit, section, 4, false );

        // The LocalSSF parameter
        toolkit.createLabel( miscSectionComposite, Messages.getString( "OpenLDAPSecurityPage.LocalSSF" ) ); //$NON-NLS-1$
        localSsfText = toolkit.createText( miscSectionComposite, "" );

        // The PasswordCryptSaltFormat parameter
        toolkit.createLabel( miscSectionComposite, Messages.getString( "OpenLDAPSecurityPage.PasswordCryptSaltFormat" ) ); //$NON-NLS-1$
        passwordCryptSaltFormatText = toolkit.createText( miscSectionComposite, "" );
        
        // A blank line
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );

        // The PasswordHash widget
        Label passwordHashLabel = toolkit.createLabel( miscSectionComposite, Messages.getString( "OpenLDAPSecurityPage.PasswordHash" ) ); //$NON-NLS-1$
        passwordHashLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 4, 1 ) );
        
        passwordHashTableWidget = new TableWidget<String>();
        passwordHashTableWidget.setLabelProvider( new TcpBufferWrapperLabelProvider() );
        //passwordHashTableWidget.setElementDialog( "" );

        passwordHashTableWidget.createWidget( miscSectionComposite, toolkit );
        passwordHashTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        
        // A blank line
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );

        // The Security widget
        Label securityLabel = toolkit.createLabel( miscSectionComposite, Messages.getString( "OpenLDAPSecurityPage.Security" ) ); //$NON-NLS-1$
        securityLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 4, 1 ) );
        
        securityTableWidget = new TableWidget<SsfWrapper>();
        securityTableWidget.setLabelProvider( new TcpBufferWrapperLabelProvider() );
        //securityTableWidget.setElementDialog( "" );

        securityTableWidget.createWidget( miscSectionComposite, toolkit );
        securityTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
    }


    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
    }
}
