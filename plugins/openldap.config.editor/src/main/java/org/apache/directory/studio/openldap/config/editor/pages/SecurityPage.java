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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.openldap.common.ui.model.PasswordHashEnum;
import org.apache.directory.studio.openldap.config.editor.OpenLdapServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.editor.dialogs.SaslSecPropsDialog;
import org.apache.directory.studio.openldap.config.editor.wrappers.PasswordHashDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.SsfWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.SsfDecorator;
import org.apache.directory.studio.openldap.config.model.OlcGlobal;


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
 *   | | TLS Certificate File :     [                ] | | SASL Host :                [       ]           | |
 *   | | TLS Certificate Key File : [                ] | | SASL Realm :               [       ]           | |
 *   | | TLS CA Certificate File :  [                ] | | SASL Auxprops plugin :     [       ]           | |
 *   | | TLS CA Certificate Path :  [                ] | | SASL Security Properties : [       ] (Edit...) | |
 *   | | TLS Cipher Suite  :        [                ] | +------------------------------------------------+ |
 *   | | TLS CRL Check  :           [=============]    | .------------------------------------------------. |
 *   | | TLS CRL File  :            [                ] | |V Miscellaneous Security Parameters             | |
 *   | | TLS DH Parameter File :    [                ] | +------------------------------------------------+ |
 *   | | TLS Minimum Protocol  :    [=============]    | | Local SSF : [   ]  pWD Crypt Salt : [        ] | |
 *   | | TLS Random Bits File  :    [                ] | |                                                | |
 *   | | TLS Verify Client  :       [=============]    | | Password Hash  :                               | |
 *   | +-----------------------------------------------+ | +----------------------------------+           | |
 *   |                                                   | |                                  | (Add)     | |
 *   |                                                   | |                                  | (Delete)  | |
 *   |                                                   | |                                  |           | |
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
    private static final String TITLE = Messages.getString( "OpenLDAPSecurityPage.Title" ); //$NON-NLS-1$

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
    private Button saslSecPropsEditButton;
    
    // UI Controls for the Misc part
    /** The olcLocalSSF */
    private Text localSsfText;

    /** The olcPasswordCryptSaltFormat */
    private Text passwordCryptSaltFormatText;

    /** The olcPasswordHash */
    private TableWidget<PasswordHashEnum> passwordHashTableWidget;

    /** The olcSecurity table widget */
    private TableWidget<SsfWrapper> securityTableWidget;

    /** A constant for the no-selection in Combo */
    private static final String NO_CHOICE = "---";
    
    /** The CRL Checks */
    private static final String[] crlChecks = new String[]
        {
        NO_CHOICE,
        "none",
        "peer",
        "all"
        };

    /** The list of supported protocols */
    private static final String[] protocols = new String[]
        {
        NO_CHOICE,
        "3.0",
        "3.1",
        "3.2"
        };

    /** The list of VerifyClients */
    private static final String[] verifyClients = new String[]
        {
        NO_CHOICE,
        "never",
        "allow",
        "try",
        "demand",
        "hard",
        "true"
        };
    
    
    /**
     * The olcLocalSSF listener
     */
    private ModifyListener localSsfListener = event ->
        {
            if ( !Strings.isEmpty( localSsfText.getText() ) )
            {
                getConfiguration().getGlobal().setOlcLocalSSF( Integer.valueOf( localSsfText.getText() ) );
            }
        };    
    
    /**
     * The olcPasswordCryptSaltFormat listener
     */
    private ModifyListener passwordCryptSaltFormatListener = event ->
        getConfiguration().getGlobal().setOlcPasswordCryptSaltFormat( passwordCryptSaltFormatText.getText() );
    
    
    /**
     * The olcPasswordHash listener
     */
    private WidgetModifyListener passwordHashListener = event -> 
        {
            List<String> passwordHashes = new ArrayList<>();
            
            for ( PasswordHashEnum passwordHash : passwordHashTableWidget.getElements() )
            {
                passwordHashes.add( passwordHash.getName() );
            }
            
            getConfiguration().getGlobal().setOlcPasswordHash( passwordHashes );
        };
    
    
    /**
     * The olcSecurity listener
     */
    private WidgetModifyListener securityListener = event ->
        {
            List<String> ssfWrappers = new ArrayList<>();
            
            for ( SsfWrapper ssfWrapper : securityTableWidget.getElements() )
            {
                ssfWrappers.add( ssfWrapper.toString() );
            }
            
            getConfiguration().getGlobal().setOlcSecurity( ssfWrappers );
        };

    
    /**
     * The olcTlsCertificateFile listener
     */
    private ModifyListener tlsCertificateFileTextListener = event ->
        getConfiguration().getGlobal().setOlcTLSCertificateFile( tlsCertificateFileText.getText() );
    
    
    /**
     * The olcTlsCertificateKeyFile listener
     */
    private ModifyListener tlsCertificateKeyFileTextListener = event ->
        getConfiguration().getGlobal().setOlcTLSCertificateKeyFile( tlsCertificateKeyFileText.getText() );
    
    
    /**
     * The olcTlsCACertificateFile listener
     */
    private ModifyListener tlsCaCertificateFileTextListener = event ->
        getConfiguration().getGlobal().setOlcTLSCACertificateFile( tlsCaCertificateFileText.getText() );
    
    
    /**
     * The olcTlsCACertificatePath listener
     */
    private ModifyListener tlsCaCertificatePathTextListener = event ->
        getConfiguration().getGlobal().setOlcTLSCACertificatePath( tlsCaCertificatePathText.getText() );
    
    
    /**
     * The olcTlsCipherSuite listener
     */
    private ModifyListener tlsCipherSuiteTextListener = event ->
        getConfiguration().getGlobal().setOlcTLSCipherSuite( tlsCipherSuiteText.getText() );
    
    
    /**
     * The olcTlsCrlFile listener
     */
    private ModifyListener tlsCrlFileTextListener = event ->
        getConfiguration().getGlobal().setOlcTLSCRLFile( tlsCrlFileText.getText() );
    
    
    /**
     * The olcTlsCrlCheck listener
     */
    private SelectionListener tlsCrlCheckComboListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            getConfiguration().getGlobal().setOlcTLSCRLCheck( tlsCrlCheckCombo.getText() );
        }
    };
    
    
    /**
     * The olcTlsDhParamFile listener
     */
    private ModifyListener tlsDhParamFileTextListener = event ->
        getConfiguration().getGlobal().setOlcTLSDHParamFile(  tlsDhParamFileText.getText() );
    
    
    /**
     * The olcTlsRandFile listener
     */
    private ModifyListener tlsRandFileTextListener = event ->
        getConfiguration().getGlobal().setOlcTLSRandFile(  tlsRandFileText.getText() );
    
    
    /**
     * The olcTlsProtocolMin listener
     */
    private SelectionListener tlsProtocolMinComboListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            getConfiguration().getGlobal().setOlcTLSProtocolMin( tlsProtocolMinCombo.getText() );
        }
    };
    
    
    /**
     * The olcTlsVerifyClient listener
     */
    private SelectionListener tlsVerifyClientComboListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            getConfiguration().getGlobal().setOlcTLSVerifyClient( tlsVerifyClientCombo.getText() );
        }
    };
    
    
    /**
     * The olcSaslAuxProps listener
     */
    private ModifyListener saslAuxPropsTextListener = event ->
        getConfiguration().getGlobal().setOlcSaslAuxprops( saslAuxPropsText.getText() );
    
    
    /**
     * The olcSaslHost listener
     */
    private ModifyListener saslHostTextListener = event ->
        getConfiguration().getGlobal().setOlcSaslHost( saslHostText.getText() );
    
    
    /**
     * The olcSaslrealm listener
     */
    private ModifyListener saslRealmTextListener = event ->
        getConfiguration().getGlobal().setOlcSaslRealm( saslRealmText.getText() );
    
    
    /**
     * The listener for the SaslSecProps Text
     */
    private SelectionListener saslSecPropsEditSelectionListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            SaslSecPropsDialog dialog = new SaslSecPropsDialog( saslSecPropsText.getShell(), saslSecPropsText.getText() );

            if ( dialog.open() == OverlayDialog.OK )
            {
                String saslSecPropsValue = dialog.getSaslSecPropsValue();
                
                if ( saslSecPropsValue != null )
                {
                    saslSecPropsText.setText( saslSecPropsValue );
                }
                
                getConfiguration().getGlobal().setOlcSaslSecProps( saslSecPropsValue );
            }
        }
    };

    /**
     * Creates a new instance of SecurityPage.
     *
     * @param editor the associated editor
     */
    public SecurityPage( OpenLdapServerConfigurationEditor editor )
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
        addModifyListener( tlsCertificateFileText, tlsCertificateFileTextListener );

        // The tlsCertificateKeyFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCertificateKeyFile" ) ); //$NON-NLS-1$
        tlsCertificateKeyFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsCertificateKeyFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );
        addModifyListener( tlsCertificateKeyFileText, tlsCertificateKeyFileTextListener );

        // The tlsCaCertificateFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCACertificateFile" ) ); //$NON-NLS-1$
        tlsCaCertificateFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsCaCertificateFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );
        addModifyListener( tlsCaCertificateFileText, tlsCaCertificateFileTextListener );

        // The tlsCaCertificatePath parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCACertificatePath" ) ); //$NON-NLS-1$
        tlsCaCertificatePathText = toolkit.createText( tlsSectionComposite, "" );
        tlsCaCertificatePathText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );
        addModifyListener( tlsCaCertificatePathText, tlsCaCertificatePathTextListener );

        // The tlsCipherSuite parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCipherSuite" ) ); //$NON-NLS-1$
        tlsCipherSuiteText = toolkit.createText( tlsSectionComposite, "" );
        tlsCipherSuiteText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );
        addModifyListener( tlsCipherSuiteText, tlsCipherSuiteTextListener );

        // The tlsDHParamFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSDHParamFile" ) ); //$NON-NLS-1$
        tlsDhParamFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsDhParamFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );
        addModifyListener( tlsDhParamFileText, tlsDhParamFileTextListener );

        // The tlsRandFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSRandFile" ) ); //$NON-NLS-1$
        tlsRandFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsRandFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );
        addModifyListener( tlsRandFileText, tlsRandFileTextListener );

        // The tlsCRLFile parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCRLFile" ) ); //$NON-NLS-1$
        tlsCrlFileText = toolkit.createText( tlsSectionComposite, "" );
        tlsCrlFileText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );
        addModifyListener( tlsCrlFileText, tlsCrlFileTextListener );

        // The tlsCRLCheck parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSCRLCheck" ) ); //$NON-NLS-1$
        tlsCrlCheckCombo = BaseWidgetUtils.createCombo( tlsSectionComposite, crlChecks, -1, 1 );
        tlsCrlCheckCombo.addSelectionListener( tlsCrlCheckComboListener );

        // The tlsProtocolMin parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSProtocolMin" ) ); //$NON-NLS-1$
        tlsProtocolMinCombo = BaseWidgetUtils.createCombo( tlsSectionComposite, protocols, -1, 1 );
        tlsProtocolMinCombo.addSelectionListener( tlsProtocolMinComboListener );

        // The tlsProtocolMin parameter
        toolkit.createLabel( tlsSectionComposite, Messages.getString( "OpenLDAPSecurityPage.TLSVerifyClient" ) ); //$NON-NLS-1$
        tlsVerifyClientCombo = BaseWidgetUtils.createCombo( tlsSectionComposite, verifyClients, -1, 1 );
        tlsVerifyClientCombo.addSelectionListener( tlsVerifyClientComboListener );
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
     * .---------------------------------------------------------.
     * |V SASL Parameters                                        |
     * +---------------------------------------------------------+
     * | SASL Host :                [                ]           |
     * | SASL Realm :               [                ]           |
     * | SASL Auxprops plugin :     [                ]           |
     * | SASL Security Properties : [                ] (Edit...) |
     * +---------------------------------------------------------+
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
        Composite saslSectionComposite = createSectionComposite( toolkit, section, 3, false );

        // The saslHost parameter
        toolkit.createLabel( saslSectionComposite, Messages.getString( "OpenLDAPSecurityPage.SaslHost" ) ); //$NON-NLS-1$
        saslHostText = toolkit.createText( saslSectionComposite, "" );
        saslHostText.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        addModifyListener( saslHostText, saslHostTextListener );

        // The saslRealm parameter
        toolkit.createLabel( saslSectionComposite, Messages.getString( "OpenLDAPSecurityPage.SaslRealm" ) ); //$NON-NLS-1$
        saslRealmText = toolkit.createText( saslSectionComposite, "" );
        saslRealmText.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        addModifyListener( saslRealmText, saslRealmTextListener );

        // The saslAuxProps parameter
        toolkit.createLabel( saslSectionComposite, Messages.getString( "OpenLDAPSecurityPage.SaslAuxProps" ) ); //$NON-NLS-1$
        saslAuxPropsText = toolkit.createText( saslSectionComposite, "" );
        saslAuxPropsText.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        addModifyListener( saslAuxPropsText, saslAuxPropsTextListener );

        // The saslSecProps parameter
        toolkit.createLabel( saslSectionComposite, Messages.getString( "OpenLDAPSecurityPage.SaslSecProps" ) ); //$NON-NLS-1$
        saslSecPropsText = toolkit.createText( saslSectionComposite, "" );
        saslSecPropsText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        saslSecPropsEditButton = BaseWidgetUtils.createButton( saslSectionComposite, 
            Messages.getString( "OpenLDAPSecurityPage.Edit" ), 1 );
        saslSecPropsEditButton.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );
        saslSecPropsEditButton.addSelectionListener( saslSecPropsEditSelectionListener );
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
     * | |                                                                 | (Delete)  |
     * | |                                                                 |           |
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
        addModifyListener( localSsfText, localSsfListener );

        // The PasswordCryptSaltFormat parameter
        toolkit.createLabel( miscSectionComposite, Messages.getString( "OpenLDAPSecurityPage.PasswordCryptSaltFormat" ) ); //$NON-NLS-1$
        passwordCryptSaltFormatText = toolkit.createText( miscSectionComposite, "" );
        addModifyListener( passwordCryptSaltFormatText, passwordCryptSaltFormatListener );
        
        // A blank line
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );

        // The PasswordHash widget
        Label passwordHashLabel = toolkit.createLabel( miscSectionComposite, Messages.getString( "OpenLDAPSecurityPage.PasswordHash" ) ); //$NON-NLS-1$
        passwordHashLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 4, 1 ) );
        
        passwordHashTableWidget = new TableWidget<>( new PasswordHashDecorator( miscSectionComposite.getShell() ) );

        passwordHashTableWidget.createWidgetNoEdit( miscSectionComposite, toolkit );
        passwordHashTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        addModifyListener( passwordHashTableWidget, passwordHashListener );

        // A blank line
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );
        toolkit.createLabel( miscSectionComposite, "" );

        // The Security widget
        Label securityLabel = toolkit.createLabel( miscSectionComposite, Messages.getString( "OpenLDAPSecurityPage.Security" ) ); //$NON-NLS-1$
        securityLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 4, 1 ) );
        
        securityTableWidget = new TableWidget<>( new SsfDecorator( miscSectionComposite.getShell() ) );

        securityTableWidget.createWidgetWithEdit( miscSectionComposite, toolkit );
        securityTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        addModifyListener( securityTableWidget, securityListener );
    }


    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
        removeListeners();

        // Getting the global configuration object
        OlcGlobal global = getConfiguration().getGlobal();

        if ( global != null )
        {
            //
            // Assigning values to UI Controls
            //

            // Authentication Auxprop Plugins Text
            BaseWidgetUtils.setValue( global.getOlcSaslAuxprops(), saslAuxPropsText );

            // SASL Host Text
            BaseWidgetUtils.setValue( global.getOlcSaslHost(), saslHostText );

            // SASL Realm Text
            BaseWidgetUtils.setValue( global.getOlcSaslRealm(), saslRealmText );

            // SASL Security Properties Text
            BaseWidgetUtils.setValue( global.getOlcSaslSecProps(), saslSecPropsText );

            // TLS CA Certificate File Text
            BaseWidgetUtils.setValue( global.getOlcTLSCACertificateFile(), tlsCaCertificateFileText );

            // TLS CA Certificate Path Text
            BaseWidgetUtils.setValue( global.getOlcTLSCACertificatePath(), tlsCaCertificatePathText );

            // TLS Certificate File Text
            BaseWidgetUtils.setValue( global.getOlcTLSCertificateFile(), tlsCertificateFileText );

            // TLS Certificate Key File Text
            BaseWidgetUtils.setValue( global.getOlcTLSCertificateKeyFile(), tlsCertificateKeyFileText );

            // Cipher Suite Text
            BaseWidgetUtils.setValue( global.getOlcTLSCipherSuite(), tlsCipherSuiteText );

            // Certificate Revocation List File Text
            BaseWidgetUtils.setValue( global.getOlcTLSCRLFile(), tlsCrlFileText );

            // Certificate Check List Level Combo
            String tlsCrlCheck = global.getOlcTLSCRLCheck();

            if ( tlsCrlCheck != null )
            {
                // Select the right one
                boolean found = false;
                
                for ( String check : crlChecks )
                {
                    if ( check.equalsIgnoreCase( tlsCrlCheck ) )
                    {
                        tlsCrlCheckCombo.setText( check );
                        found = true;
                        break;
                    }
                }
                
                if ( !found )
                {
                    tlsVerifyClientCombo.setText( NO_CHOICE );
                }
            }
            else
            {
                tlsCrlCheckCombo.setText( NO_CHOICE );
            }

            // Diffie-Hellman Parameters File Text
            BaseWidgetUtils.setValue( global.getOlcTLSDHParamFile(), tlsDhParamFileText );

            // TLS Random Bits File Text
            BaseWidgetUtils.setValue( global.getOlcTLSRandFile(), tlsRandFileText );

            // TLS Incoming Certificates Verification Level Combo
            String tlsVerifyClient = global.getOlcTLSVerifyClient();

            if ( tlsVerifyClient != null )
            {
                // Select the right one
                boolean found = false;
                
                for ( String verify : verifyClients )
                {
                    if ( verify.equalsIgnoreCase( tlsVerifyClient ) )
                    {
                        tlsVerifyClientCombo.setText( verify );
                        found = true;
                        break;
                    }
                }
                
                if ( !found )
                {
                    tlsVerifyClientCombo.setText( NO_CHOICE );
                }
            }
            else
            {
                tlsVerifyClientCombo.setText( NO_CHOICE );
            }

            // TLS Protocol Min Combo
            String tlsProtocolMin = global.getOlcTLSProtocolMin();

            if ( tlsProtocolMin != null )
            {
                // Select the right one
                boolean found = false;
                
                for ( String protocol : protocols )
                {
                    if ( protocol.equalsIgnoreCase( tlsProtocolMin ) )
                    {
                        tlsProtocolMinCombo.setText( protocol );
                        found = true;
                        break;
                    }
                }
                
                if ( !found )
                {
                    tlsProtocolMinCombo.setText( NO_CHOICE );
                }
            }
            else
            {
                tlsProtocolMinCombo.setText( NO_CHOICE );
            }

            // Local SSF Text
            BaseWidgetUtils.setValue( global.getOlcLocalSSF(), localSsfText );

            // Password Crypt Format Text
            BaseWidgetUtils.setValue( global.getOlcPasswordCryptSaltFormat(), passwordCryptSaltFormatText );

            // Password Hash Table Widget
            List<String> passwordHashes = global.getOlcPasswordHash();
            List<PasswordHashEnum> hashes = new ArrayList<>();

            if ( passwordHashes != null )
            {
                for ( String passwordHashName : passwordHashes )
                {
                    hashes.add( PasswordHashEnum.getPasswordHash( passwordHashName ) );
                }
            }

            passwordHashTableWidget.setElements( hashes );

            // Security Table Widget
            List<String> features = global.getOlcSecurity();
            List<SsfWrapper> ssfWrappers = new ArrayList<>();

            if ( features != null )
            {
                for ( String feature : features )
                {
                    ssfWrappers.add( new SsfWrapper( feature ) );
                }
            }

            securityTableWidget.setElements( ssfWrappers );
        }
        
        addListeners();
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        addDirtyListener( localSsfText );
        addDirtyListener( passwordCryptSaltFormatText );
        addDirtyListener( passwordHashTableWidget );
        addDirtyListener( saslAuxPropsText );
        addDirtyListener( saslHostText );
        addDirtyListener( saslRealmText );
        addDirtyListener( saslSecPropsText );
        addDirtyListener( securityTableWidget );
        addDirtyListener( tlsCaCertificateFileText );
        addDirtyListener( tlsCaCertificatePathText );
        addDirtyListener( tlsCertificateFileText );
        addDirtyListener( tlsCertificateKeyFileText );
        addDirtyListener( tlsCipherSuiteText );
        addDirtyListener( tlsCrlCheckCombo );
        addDirtyListener( tlsCrlFileText );
        addDirtyListener( tlsDhParamFileText );
        addDirtyListener( tlsProtocolMinCombo );
        addDirtyListener( tlsRandFileText );
        addDirtyListener( tlsVerifyClientCombo );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        removeDirtyListener( localSsfText );
        removeDirtyListener( passwordCryptSaltFormatText );
        removeDirtyListener( passwordHashTableWidget );
        removeDirtyListener( saslAuxPropsText );
        removeDirtyListener( saslHostText );
        removeDirtyListener( saslRealmText );
        removeDirtyListener( saslSecPropsText );
        removeDirtyListener( securityTableWidget );
        removeDirtyListener( tlsCaCertificateFileText );
        removeDirtyListener( tlsCaCertificatePathText );
        removeDirtyListener( tlsCertificateFileText );
        removeDirtyListener( tlsCertificateKeyFileText );
        removeDirtyListener( tlsCipherSuiteText );
        removeDirtyListener( tlsCrlCheckCombo );
        removeDirtyListener( tlsCrlFileText );
        removeDirtyListener( tlsDhParamFileText );
        removeDirtyListener( tlsProtocolMinCombo );
        removeDirtyListener( tlsRandFileText );
        removeDirtyListener( tlsVerifyClientCombo );
    }
}
