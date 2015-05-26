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


import java.util.List;

import org.apache.directory.studio.openldap.config.editor.Messages;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.model.OlcGlobal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the Options Page of the Server Configuration Editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OptionsPage extends OpenLDAPServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = OptionsPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = "Options";

    // UI Controls
    /** The olcPluginLogFile parameter */
    private Text pluginLogFileText; 
    
    private Text numberSecondsClosingIdleConnectionText;
    private Text numberSecondsClosingConnectionWithOutstandingWriteText;
    private Text authenticationAuxpropPluginsText;
    private Text saslHostText;
    private Text saslRealmText;
    private Text saslSecurityPropertiesText;
    private Text certificateAuthoritiesFileText;
    private Text certificateAuthoritiesPathText;
    private Text serverCertificateFileText;
    private Text serverPrivateKeyFileText;
    private Text cipherSuiteText;
    private Text certificateRevocationListFileText;
    private Text certificateRevocationListLevelText;
    private Text diffieHellmanParametersFileText;
    private Text randomBitsFileText;
    private Text incomingCertificatesVerificationLevelText;
    private Text keyLengthForOrderedIntegerIndicesText;
    private Text maximumLengthForSubinitialAndSubfinalIndicesText;
    private Text minimumLengthForSubinitialAndSubfinalIndicesText;
    private Text lengthUsedForSubanyIndicesText;
    private Text stepsUsedInSubanyIndexLookupsText;
    private Text authUsernamesToDnRewriteRuleText;
    private Text proxyAuthorizationPolicyText;
    private Text authzUsernamesToDnRegexpText;


    /**
     * Creates a new instance of OptionsPage.
     *
     * @param editor the associated editor
     */
    public OptionsPage( OpenLDAPServerConfigurationEditor editor )
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

        createLogsSection( toolkit, leftComposite );
        createAuthenticationAndAuthorizationSection( toolkit, leftComposite );
        createConnectionLimitsSection( toolkit, leftComposite );
        createIndicesSection( toolkit, leftComposite );
        createSaslSection( toolkit, rightComposite );
        createTlsSection( toolkit, rightComposite );

        refreshUI();
    }


    /**
     * Creates the Logs section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createLogsSection( FormToolkit toolkit, Composite parent )
    {
        // The Logs section, which can be expanded or compacted
        Section section = createSection( toolkit, parent, Messages.getString( "OptionsPage.LogTitle" ) );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // Plugin Log File Text
        toolkit.createLabel( composite, "Plugin Log File:" );
        pluginLogFileText = toolkit.createText( composite, "" );
        pluginLogFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Connection Limits section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createConnectionLimitsSection( FormToolkit toolkit, Composite parent )
    {
        Section section = createSection( toolkit, parent, "Connection Limits" );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // Number Seconds Closing Idle Connection Text
        toolkit.createLabel( composite, "Number of seconds before closing an idle connection:" );
        numberSecondsClosingIdleConnectionText = toolkit.createText( composite, "" );
        numberSecondsClosingIdleConnectionText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Number Seconds Closing Connection With Outstanding Write Text
        toolkit.createLabel( composite, "Number of seconds before closing a connection with an outstanding write:" );
        numberSecondsClosingConnectionWithOutstandingWriteText = toolkit.createText( composite, "" );
        numberSecondsClosingConnectionWithOutstandingWriteText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true,
            false ) );
    }


    /**
     * Creates the SASL section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createSaslSection( FormToolkit toolkit, Composite parent )
    {
        Section section = createSection( toolkit, parent, "SASL" );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // Authentication Auxprop Plugins Text
        toolkit.createLabel( composite, "Authentication auxprop plugins:" );
        authenticationAuxpropPluginsText = toolkit.createText( composite, "" );
        authenticationAuxpropPluginsText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SASL Host Text
        toolkit.createLabel( composite, "SASL host:" );
        saslHostText = toolkit.createText( composite, "" );
        saslHostText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SASL Realm Text
        toolkit.createLabel( composite, "SASL realm:" );
        saslRealmText = toolkit.createText( composite, "" );
        saslRealmText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SASL Security Properties Text
        toolkit.createLabel( composite, "SASL security properties:" );
        saslSecurityPropertiesText = toolkit.createText( composite, "" );
        saslSecurityPropertiesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the TLS section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createTlsSection( FormToolkit toolkit, Composite parent )
    {
        Section section = createSection( toolkit, parent, "TLS" );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // Certificate Authorities File Text
        toolkit.createLabel( composite, "Certificate Authorities file:" );
        certificateAuthoritiesFileText = toolkit.createText( composite, "" );
        certificateAuthoritiesFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Certificate Authorities Path Text
        toolkit.createLabel( composite, "Certificate Authorities path:" );
        certificateAuthoritiesPathText = toolkit.createText( composite, "" );
        certificateAuthoritiesPathText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Server Certificate File Text
        toolkit.createLabel( composite, "Server certificate file:" );
        serverCertificateFileText = toolkit.createText( composite, "" );
        serverCertificateFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Server Private Key File Text
        toolkit.createLabel( composite, "Server private key file:" );
        serverPrivateKeyFileText = toolkit.createText( composite, "" );
        serverPrivateKeyFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Cipher Suite Text
        toolkit.createLabel( composite, "Cipher suite:" );
        cipherSuiteText = toolkit.createText( composite, "" );
        cipherSuiteText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Certificate Revocation List File Text
        toolkit.createLabel( composite, "Certificate revocation list file:" );
        certificateRevocationListFileText = toolkit.createText( composite, "" );
        certificateRevocationListFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Certificate Revocation List Level Text
        toolkit.createLabel( composite, "Certificate revocation list level:" );
        certificateRevocationListLevelText = toolkit.createText( composite, "" );
        certificateRevocationListLevelText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Diffie-Hellman Parameters File Text
        toolkit.createLabel( composite, "Diffie-Hellman parameters file:" );
        diffieHellmanParametersFileText = toolkit.createText( composite, "" );
        diffieHellmanParametersFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Random Bits File Text
        toolkit.createLabel( composite, "Certificate revocation list level:" );
        randomBitsFileText = toolkit.createText( composite, "" );
        randomBitsFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Incoming Certificates Verification Level Text
        toolkit.createLabel( composite, "Incoming certificates verification level:" );
        incomingCertificatesVerificationLevelText = toolkit.createText( composite, "" );
        incomingCertificatesVerificationLevelText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Indices section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createIndicesSection( FormToolkit toolkit, Composite parent )
    {
        Section section = createSection( toolkit, parent, "Indices" );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // Key Length for Ordered Integer Indices Text
        toolkit.createLabel( composite, "Key length for ordered integer indices:" );
        keyLengthForOrderedIntegerIndicesText = toolkit.createText( composite, "" );
        keyLengthForOrderedIntegerIndicesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Maximum Length For Subinitial And Subfinal Indices Text
        toolkit.createLabel( composite, "Maximum length for subinitial and subfinal indices:" );
        maximumLengthForSubinitialAndSubfinalIndicesText = toolkit.createText( composite, "" );
        maximumLengthForSubinitialAndSubfinalIndicesText
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Minimum Length For Subinitial And Subfinal Indices Text
        toolkit.createLabel( composite, "Minimum length for subinitial and subfinal indices:" );
        minimumLengthForSubinitialAndSubfinalIndicesText = toolkit.createText( composite, "" );
        minimumLengthForSubinitialAndSubfinalIndicesText
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Length Used For Subany Indices Text
        toolkit.createLabel( composite, "Length used for subany indices:" );
        lengthUsedForSubanyIndicesText = toolkit.createText( composite, "" );
        lengthUsedForSubanyIndicesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Steps Used In Subany Index Lookups Text
        toolkit.createLabel( composite, "Steps used in subany index lookups:" );
        stepsUsedInSubanyIndexLookupsText = toolkit.createText( composite, "" );
        stepsUsedInSubanyIndexLookupsText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Authentication & Authorization section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createAuthenticationAndAuthorizationSection( FormToolkit toolkit, Composite parent )
    {
        Section section = createSection( toolkit, parent, "Authentication & Authorization" );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // Authentication Usernames To DN Rewrite Rule Text
        toolkit.createLabel( composite, "Authentication rewrite rule to convert simple user names to an LDAP DN:" );
        authUsernamesToDnRewriteRuleText = toolkit.createText( composite, "" );
        authUsernamesToDnRewriteRuleText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Proxy Authorization Policy Text
        toolkit.createLabel( composite, "Proxy authorization policy text:" );
        proxyAuthorizationPolicyText = toolkit.createText( composite, "" );
        proxyAuthorizationPolicyText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Authorization Usernames To DN Regexp Text
        toolkit.createLabel( composite, "Authorization Regexp to convert simple user names to an LDAP DN:" );
        authzUsernamesToDnRegexpText = toolkit.createText( composite, "" );
        authzUsernamesToDnRegexpText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }

    
    /**
     * Adds listeners to UI Controls.
     */
    private void addListeners()
    {
    }

    
    /**
     * Removes listeners to UI Controls.
     */
    private void removeListeners()
    {
    }
    

    /**
     * Creates a section with the given text.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     * @param text the text
     * @return a section with the given text
     *
    private Section createSection( FormToolkit toolkit, Composite parent, String text )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED );
        section.setText( text );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        
        return section;
    }


    /**
     * Creates a composite for the given section.
     *
     * @param toolkit the toolkit
     * @param section the section
     * @param numColumns the number of columns in the grid
     * @param makeColumnsEqualWidth whether or not the columns will have equal width
     * 
     * @return a composite for the given section.
     */
    private Composite createSectionComposite( FormToolkit toolkit, Section section, int numColumns,
        boolean makeColumnsEqualWidth )
    {
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( numColumns, makeColumnsEqualWidth );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );
        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
        if ( isInitialized() )
        {
            removeListeners();

            // Getting the global configuration object
            OlcGlobal global = getConfiguration().getGlobal();

            if ( global != null )
            {
                //
                // Assigning values to UI Controls
                //

                // Plugin Log File Text
                String pluginLogFile = global.getOlcPluginLogFile();

                if ( pluginLogFile != null )
                {
                    pluginLogFileText.setText( pluginLogFile );
                }
                else
                {
                    pluginLogFileText.setText( "" );
                }

                // Number Seconds Closing Idle Connection Text
                Integer numberSecondsClosingIdleConnection = global.getOlcIdleTimeout();

                if ( numberSecondsClosingIdleConnection != null )
                {
                    numberSecondsClosingIdleConnectionText.setText( numberSecondsClosingIdleConnection + "" );
                }
                else
                {
                    numberSecondsClosingIdleConnectionText.setText( "" );
                }

                // Number Seconds Closing Connection With Outstanding Write Text
                Integer numberSecondsClosingConnectionWithOutstandingWrite = global.getOlcWriteTimeout();

                if ( numberSecondsClosingConnectionWithOutstandingWrite != null )
                {
                    numberSecondsClosingConnectionWithOutstandingWriteText
                        .setText( numberSecondsClosingConnectionWithOutstandingWrite + "" );
                }
                else
                {
                    numberSecondsClosingConnectionWithOutstandingWriteText.setText( "" );
                }

                // Authentication Auxprop Plugins Text
                String authenticationAuxpropPlugins = global.getOlcSaslAuxprops();

                if ( authenticationAuxpropPlugins != null )
                {
                    authenticationAuxpropPluginsText.setText( authenticationAuxpropPlugins );
                }
                else
                {
                    authenticationAuxpropPluginsText.setText( "" );
                }

                // SASL Host Text
                String saslHost = global.getOlcSaslHost();

                if ( saslHost != null )
                {
                    saslHostText.setText( saslHost );
                }
                else
                {
                    saslHostText.setText( "" );
                }

                // SASL Realm Text
                String saslRealm = global.getOlcSaslRealm();

                if ( saslRealm != null )
                {
                    saslRealmText.setText( saslRealm );
                }
                else
                {
                    saslRealmText.setText( "" );
                }

                // SASL Security Properties Text
                String saslSecurityProperties = global.getOlcSaslSecProps();

                if ( saslSecurityProperties != null )
                {
                    saslSecurityPropertiesText.setText( saslSecurityProperties );
                }
                else
                {
                    saslSecurityPropertiesText.setText( "" );
                }

                // Certificate Authorities File Text
                String certificateAuthoritiesFile = global.getOlcTLSCACertificateFile();

                if ( certificateAuthoritiesFile != null )
                {
                    certificateAuthoritiesFileText.setText( certificateAuthoritiesFile );
                }
                else
                {
                    certificateAuthoritiesFileText.setText( "" );
                }

                // Certificate Authorities Path Text
                String certificateAuthoritiesPath = global.getOlcTLSCACertificatePath();

                if ( certificateAuthoritiesPath != null )
                {
                    certificateAuthoritiesPathText.setText( certificateAuthoritiesPath );
                }
                else
                {
                    certificateAuthoritiesPathText.setText( "" );
                }

                // Server Certificate File Text
                String serverCertificateFile = global.getOlcTLSCertificateFile();

                if ( serverCertificateFile != null )
                {
                    serverCertificateFileText.setText( serverCertificateFile );
                }
                else
                {
                    serverCertificateFileText.setText( "" );
                }

                // Server Private Key File Text
                String serverPrivateKeyFile = global.getOlcTLSCertificateKeyFile();

                if ( serverPrivateKeyFile != null )
                {
                    serverPrivateKeyFileText.setText( serverPrivateKeyFile );
                }
                else
                {
                    serverPrivateKeyFileText.setText( "" );
                }

                // Cipher Suite Text
                String cipherSuite = global.getOlcTLSCipherSuite();

                if ( cipherSuite != null )
                {
                    cipherSuiteText.setText( cipherSuite );
                }
                else
                {
                    cipherSuiteText.setText( "" );
                }

                // Certificate Revocation List File Text
                String certificateRevocationListFile = global.getOlcTLSCRLFile();

                if ( certificateRevocationListFile != null )
                {
                    certificateRevocationListFileText.setText( certificateRevocationListFile );
                }
                else
                {
                    certificateRevocationListFileText.setText( "" );
                }

                // Certificate Revocation List Level Text
                String certificateRevocationListLevel = global.getOlcTLSCRLCheck();

                if ( certificateRevocationListLevel != null )
                {
                    certificateRevocationListLevelText.setText( certificateRevocationListLevel );
                }
                else
                {
                    certificateRevocationListLevelText.setText( "" );
                }

                // Diffie-Hellman Parameters File Text
                String diffieHellmanParametersFile = global.getOlcTLSDHParamFile();

                if ( diffieHellmanParametersFile != null )
                {
                    diffieHellmanParametersFileText.setText( diffieHellmanParametersFile );
                }
                else
                {
                    diffieHellmanParametersFileText.setText( "" );
                }

                // Random Bits File Text
                String randomBitsFile = global.getOlcTLSRandFile();

                if ( randomBitsFile != null )
                {
                    randomBitsFileText.setText( randomBitsFile );
                }
                else
                {
                    randomBitsFileText.setText( "" );
                }

                // Incoming Certificates Verification Level Text
                String incomingCertificatesVerificationLevel = global.getOlcTLSVerifyClient();

                if ( incomingCertificatesVerificationLevel != null )
                {
                    incomingCertificatesVerificationLevelText.setText( incomingCertificatesVerificationLevel );
                }
                else
                {
                    incomingCertificatesVerificationLevelText.setText( "" );
                }

                // Key Length for Ordered Integer Indices Text
                Integer keyLengthForOrderedIntegerIndices = global.getOlcIndexIntLen();

                if ( keyLengthForOrderedIntegerIndices != null )
                {
                    keyLengthForOrderedIntegerIndicesText.setText( keyLengthForOrderedIntegerIndices + "" );
                }
                else
                {
                    keyLengthForOrderedIntegerIndicesText.setText( "" );
                }

                // Maximum Length For Subinitial And Subfinal Indices Text
                Integer maximumLengthForSubinitialAndSubfinalIndices = global.getOlcIndexSubstrIfMaxLen();

                if ( maximumLengthForSubinitialAndSubfinalIndices != null )
                {
                    maximumLengthForSubinitialAndSubfinalIndicesText
                        .setText( maximumLengthForSubinitialAndSubfinalIndices + "" );
                }
                else
                {
                    maximumLengthForSubinitialAndSubfinalIndicesText.setText( "" );
                }

                // Minimum Length For Subinitial And Subfinal Indices Text
                Integer minimumLengthForSubinitialAndSubfinalIndices = global.getOlcIndexSubstrIfMinLen();

                if ( minimumLengthForSubinitialAndSubfinalIndices != null )
                {
                    minimumLengthForSubinitialAndSubfinalIndicesText
                        .setText( minimumLengthForSubinitialAndSubfinalIndices + "" );
                }
                else
                {
                    minimumLengthForSubinitialAndSubfinalIndicesText.setText( "" );
                }

                // Length Used For Subany Indices Text
                Integer lengthUsedForSubanyIndices = global.getOlcIndexSubstrAnyLen();
                if ( lengthUsedForSubanyIndices != null )
                {
                    lengthUsedForSubanyIndicesText.setText( lengthUsedForSubanyIndices + "" );
                }
                else
                {
                    lengthUsedForSubanyIndicesText.setText( "" );
                }

                // Steps Used In Subany Index Lookups Text
                Integer stepsUsedInSubanyIndexLookups = global.getOlcIndexSubstrAnyStep();

                if ( stepsUsedInSubanyIndexLookups != null )
                {
                    stepsUsedInSubanyIndexLookupsText.setText( stepsUsedInSubanyIndexLookups + "" );
                }
                else
                {
                    stepsUsedInSubanyIndexLookupsText.setText( "" );
                }

                // Authentication Usernames To DN Rewrite Rule Text
                List<String> authUsernamesToDnRewriteRule = global.getOlcAuthIDRewrite();

                if ( authUsernamesToDnRewriteRule != null )
                {
                    authUsernamesToDnRewriteRuleText.setText( authUsernamesToDnRewriteRule + "" );
                }
                else
                {
                    authUsernamesToDnRewriteRuleText.setText( "" );
                }

                // Proxy Authorization Policy Text
                String proxyAuthorizationPolicy = global.getOlcAuthzPolicy();

                if ( proxyAuthorizationPolicy != null )
                {
                    proxyAuthorizationPolicyText.setText( proxyAuthorizationPolicy );
                }
                else
                {
                    proxyAuthorizationPolicyText.setText( "" );
                }

                // Authorization Usernames To DN Regexp Text
                List<String> authzUsernamesToDnRegexp = global.getOlcAuthzRegexp();

                if ( authzUsernamesToDnRegexp != null )
                {
                    authzUsernamesToDnRegexpText.setText( authzUsernamesToDnRegexp + "" );
                }
                else
                {
                    authzUsernamesToDnRegexpText.setText( "" );
                }

                addListeners();
            }
        }
    }
}
