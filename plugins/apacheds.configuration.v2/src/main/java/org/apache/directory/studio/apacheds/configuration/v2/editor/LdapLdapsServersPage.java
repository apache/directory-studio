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


import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.LdapServerBean;
import org.apache.directory.server.config.beans.TransportBean;
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
    private static final int LDAPS_DEFAULT_PORT = 10636;

    private static final int LDAP_DEFAULT_PORT = 10389;

    private static final String TRANSPORT_ID_LDAP = "ldap";

    private static final String TRANSPORT_ID_LDAPS = "ldaps";

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
    private Button authMechGssSpnegoCheckbox;
    private Text saslHostText;
    private Text saslPrincipalText;
    private Text saslSearchBaseDnText;

    // UI Control Listeners
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
            getLdapsServerTransportBean().setSystemPort( Integer.parseInt( ldapPortText.getText() ) );
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
                // TODO Auto-generated catch block
                e1.printStackTrace();

            }
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
        GridLayout glayout = new GridLayout( 3, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        Button enableTlsCheckbox = toolkit.createButton( composite, "Enable TLS", SWT.CHECK );
        enableTlsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        Button enableServerSidePasswordHashingCheckbox = toolkit.createButton( composite,
            "Enable sever-side password hashing",
            SWT.CHECK );
        enableServerSidePasswordHashingCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );
        toolkit.createLabel( composite, "Hashing Method:" );
        Combo hashingMethodCombo = new Combo( composite, SWT.DROP_DOWN | SWT.READ_ONLY );
        hashingMethodCombo.setItems( new String[]
            { "SSHA", "MD5" } );
        toolkit.adapt( hashingMethodCombo );
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

        // Search Base Dn
        toolkit.createLabel( composite, "Search Base Dn:" );
        saslSearchBaseDnText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        saslSearchBaseDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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

        // Max Size Limit Text
        addDirtyListener( maxSizeLimitText );

        // Auth Mechanisms Simple Checkbox
        addDirtyListener( authMechSimpleCheckbox );

        // Auth Mechanisms CRAM-MD5 Checkbox
        addDirtyListener( authMechCramMd5Checkbox );

        // Auth Mechanisms DIGEST-MD5 Checkbox
        addDirtyListener( authMechDigestMd5Checkbox );

        // Auth Mechanisms GSSAPI Checkbox
        addDirtyListener( authMechGssapiCheckbox );

        // Auth Mechanisms NTLM Checkbox
        addDirtyListener( authMechNtlmCheckbox );

        // Auth Mechanisms GSS SPENEGO Checkbox
        addDirtyListener( authMechGssSpnegoCheckbox );

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

        // Max Size Limit Text
        removeDirtyListener( maxSizeLimitText );

        // Auth Mechanisms Simple Checkbox
        removeDirtyListener( authMechSimpleCheckbox );

        // Auth Mechanisms CRAM-MD5 Checkbox
        removeDirtyListener( authMechCramMd5Checkbox );

        // Auth Mechanisms DIGEST-MD5 Checkbox
        removeDirtyListener( authMechDigestMd5Checkbox );

        // Auth Mechanisms GSSAPI Checkbox
        removeDirtyListener( authMechGssapiCheckbox );

        // Auth Mechanisms NTLM Checkbox
        removeDirtyListener( authMechNtlmCheckbox );

        // Auth Mechanisms GSS SPENEGO Checkbox
        removeDirtyListener( authMechGssSpnegoCheckbox );

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

        TransportBean ldapServerTransportBean = getLdapServerTransportBean();
        setSelection( enableLdapCheckbox, ldapServerTransportBean.isEnabled() );
        setText( ldapPortText, ldapServerTransportBean.getSystemPort() + "" );

        TransportBean ldapsServerTransportBean = getLdapsServerTransportBean();
        setSelection( enableLdapsCheckbox, ldapsServerTransportBean.isEnabled() );
        setText( ldapsPortText, ldapsServerTransportBean.getSystemPort() + "" );

        LdapServerBean ldapServerBean = getLdapServerBean();
        setText( saslHostText, ldapServerBean.getLdapServerSaslHost() );
        setText( saslPrincipalText, ldapServerBean.getLdapServerSaslPrincipal() );
        setText( saslSearchBaseDnText, ldapServerBean.getSearchBaseDn().toString() );

        addListeners();
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
    /**
     * TODO getLdapServerTransportBean.
     *
     * @return
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

            // Port
            if ( TRANSPORT_ID_LDAP.equals( id ) )
            {
                transportBean.setSystemPort( LDAP_DEFAULT_PORT );
            }
            else if ( TRANSPORT_ID_LDAPS.equals( id ) )
            {
                transportBean.setSystemPort( LDAPS_DEFAULT_PORT );
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
