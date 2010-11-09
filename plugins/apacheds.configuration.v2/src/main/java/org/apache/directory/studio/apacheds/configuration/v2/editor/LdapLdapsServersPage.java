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


import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
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
    /** The Page ID*/
    public static final String ID = LdapLdapsServersPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = "LDAP/LDAPS Servers";

    // UI Fields
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
    private Button authMechGssSpnegoCheckbox;
    private CheckboxTableViewer supportedMechanismsTableViewer;
    private Button editSupportedMechanismButton;
    private Button selectAllSupportedMechanismsButton;
    private Button deselectAllSupportedMechanismsButton;
    private Text saslHostText;
    private Text saslPrincipalText;
    private Text saslSearchBaseDnText;


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public LdapLdapsServersPage( FormEditor editor )
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

        initUI();
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
        GridLayout glayout = new GridLayout( 3, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        Button enableTlsCheckbox = toolkit.createButton( composite, "Enable TLS", SWT.CHECK );
        enableTlsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        Button enableServerSidePasswordHashingCheckbox = toolkit.createButton( composite, "Enable sever-side password hashing",
            SWT.CHECK );
        enableServerSidePasswordHashingCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );
        toolkit.createLabel( composite, "Hashing Method:" );
        Text hashingMethodText = toolkit.createText( composite, "" );
        hashingMethodText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 3, true );
        composite.setLayout( glayout );
        section.setClient( composite );

        authMechSimpleCheckbox = toolkit.createButton( composite, "Simple", SWT.CHECK );
        authMechCramMd5Checkbox = toolkit.createButton( composite, "CRAM-MD5", SWT.CHECK );
        authMechDigestMd5Checkbox = toolkit.createButton( composite, "DIGEST-MD5", SWT.CHECK );
        authMechGssapiCheckbox = toolkit.createButton( composite, "GSSAPI", SWT.CHECK );
        authMechNtlmCheckbox = toolkit.createButton( composite, "NTLM", SWT.CHECK );
        authMechGssSpnegoCheckbox = toolkit.createButton( composite, "GSS_SPNEGO", SWT.CHECK );

        // Supported Authentication Mechanisms Table
        //        Table supportedMechanismsTable = toolkit.createTable( composite, SWT.CHECK );
        //        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        //        gd.heightHint = 110;
        //        supportedMechanismsTable.setLayoutData( gd );
        //        supportedMechanismsTableViewer = new CheckboxTableViewer( supportedMechanismsTable );
        //        supportedMechanismsTableViewer.setContentProvider( new ArrayContentProvider() );
        //        supportedMechanismsTableViewer.setInput( new String[]
        //            { "Simple", "CRAM-MD5", "DIGEST-MD5", "GSSAPI", "NTLM", "GSS_SPNEGO" } );
        //
        //        // Edit Button
        //        editSupportedMechanismButton = toolkit.createButton( composite, "Edit", SWT.PUSH );
        //        editSupportedMechanismButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        //        editSupportedMechanismButton.setEnabled( false );
        //
        //        // Select All Button
        //        selectAllSupportedMechanismsButton = toolkit.createButton( composite, "Select All", SWT.PUSH );
        //        selectAllSupportedMechanismsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        //
        //        // Deselect All Button
        //        deselectAllSupportedMechanismsButton = toolkit.createButton( composite, "Deselect All", SWT.PUSH );
        //        deselectAllSupportedMechanismsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
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
        saslHostText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslHostLabel = createDefaultValueLabel( toolkit, composite, "ldap.example.com" );
        defaultSaslHostLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // SASL Principal
        toolkit.createLabel( composite, "SASL Principal:" );
        saslPrincipalText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        saslPrincipalText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslPrincipalLabel = createDefaultValueLabel( toolkit, composite,
            "ldap/ldap.example.com@EXAMPLE.COM" );
        defaultSaslPrincipalLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Search Base DN
        toolkit.createLabel( composite, "Search Base DN:" );
        saslSearchBaseDnText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        saslSearchBaseDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Label defaultSaslSearchBaseDnLabel = createDefaultValueLabel( toolkit, composite, "ou=users,dc=example,dc=com" );
        defaultSaslSearchBaseDnLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
    }


    private void initUI()
    {
        enableLdapCheckbox.setSelection( true );
        ldapPortText.setText( "10389" );

        enableLdapsCheckbox.setSelection( true );
        ldapsPortText.setText( "10636" );

        saslHostText.setText( "ldap.example.com" );
        saslPrincipalText.setText( "ldap/ldap.example.com@EXAMPLE.COM" );
        saslSearchBaseDnText.setText( "ou=users,dc=example,dc=com" );
    }
}
