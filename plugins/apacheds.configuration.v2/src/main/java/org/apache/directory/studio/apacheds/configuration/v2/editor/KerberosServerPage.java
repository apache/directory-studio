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


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
public class KerberosServerPage extends ServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = KerberosServerPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = "Kerberos Server";

    // UI Fields
    private Button enableKerberosCheckbox;
    private Text kerberosPortText;
    private Button enableChangePasswordCheckbox;
    private Text changePasswordPortText;

    private Text kdcPrincipalText;

    private Text primaryKdcRealmText;

    private Text kdcSearchBaseDnText;

    private Text encryptionTypesText;


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public KerberosServerPage( FormEditor editor )
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

        initUI();
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

        // Search Base DN
        toolkit.createLabel( composite, "Search Base DN:" );
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

        Button allowClockSkewButton = toolkit.createButton( composite, "Allow Clock Skew", SWT.CHECK );
        Button verifyBodyChecksumButton = toolkit.createButton( composite, "Verify Body Checksum", SWT.CHECK );

        Button allowEmptyAddressesButton = toolkit.createButton( composite, "Allow Empty Addresses", SWT.CHECK );
        Button allowForwardableAddressesButton = toolkit.createButton( composite, "Allow Forwardable Addresses",
            SWT.CHECK );

        Button requirePreAuthenticationByEncryptedTimeStampButton = toolkit.createButton( composite,
            "Require Pre-Authentication By Encrypted TimeStamp", SWT.CHECK );
        Button allowPostdatedTicketsButtons = toolkit.createButton( composite, "Allow Postdated Tickets", SWT.CHECK );

        Button allowRenewableTicketsButton = toolkit.createButton( composite, "Allow Renewable Tickets", SWT.CHECK );
        toolkit.createLabel( composite, "" );

        Composite maximumRenewableLifetimeComposite = toolkit.createComposite( composite );
        maximumRenewableLifetimeComposite.setLayout( new GridLayout( 2, false ) );
        toolkit.createLabel( maximumRenewableLifetimeComposite, "Maximum Renewable Lifetime:" );
        Text maximumRenewableLifetimeText = createIntegerText( toolkit, maximumRenewableLifetimeComposite );

        Composite maximumTicketLifetimeComposite = toolkit.createComposite( composite );
        maximumTicketLifetimeComposite.setLayout( new GridLayout( 2, false ) );
        toolkit.createLabel( maximumTicketLifetimeComposite, "Maximum Ticket Lifetime:" );
        Text maximumTicketLifetimeText = createIntegerText( toolkit, maximumTicketLifetimeComposite );

    }


    private void initUI()
    {
        enableKerberosCheckbox.setSelection( true );
        kerberosPortText.setText( "60088" );

        enableChangePasswordCheckbox.setSelection( true );
        changePasswordPortText.setText( "60464" );
    }
}
