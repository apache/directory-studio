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

import org.apache.directory.server.config.beans.ChangePasswordServerBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.KdcServerBean;
import org.apache.directory.server.config.beans.PartitionBean;
import org.apache.directory.server.config.beans.TransportBean;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the General Page of the Server Configuration Editor.
 * 
 * The Overview tab exposes 4 panels, in 2 columns :
 * 
 * <pre>
 * +-------------------------------------------------------------------------------+
 * | +------------------------------------+ +------------------------------------+ |
 * | | +--------------------------------+ | | +--------------------------------+ | |
 * | | |                                | | | |                                | | |
 * | | |    LDAP/LDAPS Servers          | | | |    Kerberos Server             | | |
 * | | |                                | | | |                                | | |
 * | | +--------------------------------+ | | +--------------------------------+ | |
 * | | +--------------------------------+ | | +--------------------------------+ | |
 * | | |                                | | | |                                | | |
 * | | |    Partitions                  | | | |    Options                     | | |
 * | | |                                | | | |                                | | |
 * | | +--------------------------------+ | | +--------------------------------+ | |
 * | +------------------------------------+ +------------------------------------+ |
 * +-------------------------------------------------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OverviewPage extends ServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = OverviewPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "OverviewPage.Overview" ); //$NON-NLS-1$

    // UI Controls
    /** LDAP Server controls */
    private Button enableLdapCheckbox;
    private Text ldapPortText;
    private Text ldapAddressText;
    private Button enableLdapsCheckbox;
    private Text ldapsPortText;
    private Text ldapsAddressText;
    // This links open the extended LDAP/DLAPS configuration tab
    private Hyperlink openLdapConfigurationLink;
    
    /** Kerberos Server controls */
    private Button enableKerberosCheckbox;
    private Text kerberosPortText;
    private Button enableChangePasswordCheckbox;
    private Text changePasswordPortText;
    // This link opens the advanced kerberos configuration tab 
    private Hyperlink openKerberosConfigurationLink;
    
    /** The Partitions controls */
    private Label partitionsLabel;
    private TableViewer partitionsTableViewer;
    // This link open the advanced partitions configuration Tab */
    private Hyperlink openPartitionsConfigurationLink;

    /** The Options controls */
    private Button allowAnonymousAccessCheckbox;
    private Button enableAccessControlCheckbox;

    // UI Control Listeners
    private SelectionAdapter enableLdapCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            LdapLdapsServersPage.getLdapServerTransportBean( getDirectoryServiceBean() ).setEnabled(
                enableLdapCheckbox.getSelection() );
            setEnabled( ldapPortText, enableLdapCheckbox.getSelection() );
            setEnabled( ldapAddressText, enableLdapCheckbox.getSelection() );
        }
    };

    
    /**
     * The Ldap port modify listener
     */
    private ModifyListener ldapPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            LdapLdapsServersPage.getLdapServerTransportBean( getDirectoryServiceBean() ).setSystemPort(
                Integer.parseInt( ldapPortText.getText() ) );
        }
    };

    
    /**
     * The Ldap address modify listener
     */
    private ModifyListener ldapAddressTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            LdapLdapsServersPage.getLdapServerTransportBean( getDirectoryServiceBean() ).setTransportAddress( 
                ldapAddressText.getText() );
        }
    };
    
    
    private SelectionAdapter enableLdapsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            LdapLdapsServersPage.getLdapsServerTransportBean( getDirectoryServiceBean() ).setEnabled(
                enableLdapsCheckbox.getSelection() );
            setEnabled( ldapsPortText, enableLdapsCheckbox.getSelection() );
            setEnabled( ldapsAddressText, enableLdapsCheckbox.getSelection() );
        }
    };

    
    /**
     * The Ldaps port modify listener
     */
    private ModifyListener ldapsPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            LdapLdapsServersPage.getLdapsServerTransportBean( getDirectoryServiceBean() ).setSystemPort(
                Integer.parseInt( ldapsPortText.getText() ) );
        }
    };

    
    /**
     * The Ldaps address modify listener
     */
    private ModifyListener ldapsAddressTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            LdapLdapsServersPage.getLdapsServerTransportBean( getDirectoryServiceBean() ).setTransportAddress( 
                ldapsAddressText.getText() );
        }
    };
    
    private HyperlinkAdapter openLdapConfigurationLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            getServerConfigurationEditor().showPage( LdapLdapsServersPage.class );
        }
    };
    private SelectionAdapter enableKerberosCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            KerberosServerPage.enableKerberosServer( getDirectoryServiceBean(), enableKerberosCheckbox.getSelection() );
            setEnabled( kerberosPortText, enableKerberosCheckbox.getSelection() );
        }
    };
    private ModifyListener kerberosPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            KdcServerBean kdcServerBean = getConfigBean().getDirectoryServiceBean().getKdcServerBean();
            kdcServerBean.getTransports()[0].setSystemPort( Integer.parseInt( kerberosPortText.getText() ) );
        }
    };
    private SelectionAdapter enableChangePasswordCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ChangePasswordServerBean changePasswordServerBean = getConfigBean().getDirectoryServiceBean()
                .getChangePasswordServerBean();
            changePasswordServerBean.setEnabled( enableChangePasswordCheckbox.getSelection() );
            setEnabled( changePasswordPortText, enableChangePasswordCheckbox.getSelection() );
        }
    };
    private ModifyListener changePasswordPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            ChangePasswordServerBean changePasswordServerBean = getConfigBean().getDirectoryServiceBean()
                .getChangePasswordServerBean();
            changePasswordServerBean.getTransports()[0].setSystemPort( Integer.parseInt( changePasswordPortText
                .getText() ) );
        }
    };
    private HyperlinkAdapter openKerberosConfigurationLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            getServerConfigurationEditor().showPage( KerberosServerPage.class );
        }
    };
    private HyperlinkAdapter openPartitionsConfigurationLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            getServerConfigurationEditor().showPage( PartitionsPage.class );
        }
    };
    private SelectionAdapter allowAnonymousAccessCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            DirectoryServiceBean directoryServiceBean = getConfigBean().getDirectoryServiceBean();
            directoryServiceBean.setDsAllowAnonymousAccess( allowAnonymousAccessCheckbox.getSelection() );
        }
    };
    private SelectionAdapter enableAccessControlCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            DirectoryServiceBean directoryServiceBean = getConfigBean().getDirectoryServiceBean();
            directoryServiceBean.setDsAccessControlEnabled( enableAccessControlCheckbox.getSelection() );
        }
    };


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public OverviewPage( ServerConfigurationEditor editor )
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
        createLdapLdapsServersSection( toolkit, leftComposite );
        createPartitionsSection( toolkit, leftComposite );
        createKerberosChangePasswordServersSection( toolkit, rightComposite );
        createOptionsSection( toolkit, rightComposite );

        // Refreshing the UI
        refreshUI();
    }


    /**
     * Creates the LDAP and LDAPS Servers section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createLdapLdapsServersSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "OverviewPage.LdapLdapsServers" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 4, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Enable LDAP Server Checkbox
        enableLdapCheckbox = toolkit.createButton( composite,
            Messages.getString( "OverviewPage.EnableLdapServer" ), SWT.CHECK ); //$NON-NLS-1$
        enableLdapCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );

        // LDAP Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "OverviewPage.Port" ) ); //$NON-NLS-1$
        ldapPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "10389" ); //$NON-NLS-1$

        // LDAP Server Address Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "OverviewPage.Address" ) ); //$NON-NLS-1$
        ldapAddressText = createAddressText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "0.0.0.0" ); //$NON-NLS-1$

        // Enable LDAPS Server Checkbox
        enableLdapsCheckbox = toolkit.createButton( composite,
            Messages.getString( "OverviewPage.EnableLdapsServer" ), SWT.CHECK ); //$NON-NLS-1$
        enableLdapsCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );

        // LDAPS Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "OverviewPage.Port" ) ); //$NON-NLS-1$
        ldapsPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "10636" ); //$NON-NLS-1$

        // LDAPS Server Address Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "OverviewPage.Address" ) ); //$NON-NLS-1$
        ldapsAddressText = createAddressText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "0.0.0.0" ); //$NON-NLS-1$

        // LDAP Configuration Link
        openLdapConfigurationLink = toolkit.createHyperlink( composite,
            Messages.getString( "OverviewPage.AdvancedLdapLdapsConfiguration" ), SWT.NONE ); //$NON-NLS-1$
        openLdapConfigurationLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
        openLdapConfigurationLink.addHyperlinkListener( openLdapConfigurationLinkListener );
    }


    /**
     * Creates the Kerberos and Change Password Servers section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createKerberosChangePasswordServersSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "OverviewPage.KerberosServer" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 4, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Enable Kerberos Server Checkbox
        enableKerberosCheckbox = toolkit.createButton( composite,
            Messages.getString( "OverviewPage.EnableKerberosServer" ), SWT.CHECK ); //$NON-NLS-1$
        enableKerberosCheckbox
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );

        // Kerberos Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "OverviewPage.Port" ) ); //$NON-NLS-1$
        kerberosPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "60088" ); //$NON-NLS-1$

        // Enable Change Password Server Checkbox
        enableChangePasswordCheckbox = toolkit.createButton( composite,
            Messages.getString( "OverviewPage.EnableKerberosChangePasswordServer" ), //$NON-NLS-1$
            SWT.CHECK );
        enableChangePasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );

        // Change Password Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, Messages.getString( "OverviewPage.Port" ) ); //$NON-NLS-1$
        changePasswordPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "60464" ); //$NON-NLS-1$

        // Kerberos Configuration Link
        openKerberosConfigurationLink = toolkit.createHyperlink( composite,
            Messages.getString( "OverviewPage.AdvancedKerberosConfiguration" ), SWT.NONE ); //$NON-NLS-1$
        openKerberosConfigurationLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
        openKerberosConfigurationLink.addHyperlinkListener( openKerberosConfigurationLinkListener );
    }


    /**
     * Creates the Partitions section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createPartitionsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "OverviewPage.Partitions" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Partitions Label
        partitionsLabel = toolkit.createLabel( composite, "" ); //$NON-NLS-1$
        partitionsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Partitions Table Viewer
        Table partitionsTable = toolkit.createTable( composite, SWT.NULL );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.heightHint = 45;
        partitionsTable.setLayoutData( gd );
        partitionsTableViewer = new TableViewer( partitionsTable );
        partitionsTableViewer.setContentProvider( new ArrayContentProvider() );
        partitionsTableViewer.setLabelProvider( PartitionsPage.PARTITIONS_LABEL_PROVIDER );
        partitionsTableViewer.setComparator( PartitionsPage.PARTITIONS_COMPARATOR );

        // Partitions Configuration Link
        openPartitionsConfigurationLink = toolkit.createHyperlink( composite,
            Messages.getString( "OverviewPage.AdvancedPartitionsConfiguration" ), SWT.NONE ); //$NON-NLS-1$
        openPartitionsConfigurationLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
        openPartitionsConfigurationLink.addHyperlinkListener( openPartitionsConfigurationLinkListener );
    }


    /**
     * Creates the Options section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createOptionsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "OverviewPage.Options" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Allow Anonymous Access Checkbox
        allowAnonymousAccessCheckbox = toolkit.createButton( composite,
            Messages.getString( "OverviewPage.AllowAnonymousAccess" ), SWT.CHECK ); //$NON-NLS-1$
        allowAnonymousAccessCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );

        // Enable Access Control Checkbox
        enableAccessControlCheckbox = toolkit.createButton( composite,
            Messages.getString( "OverviewPage.EnableAccessControl" ), SWT.CHECK ); //$NON-NLS-1$
        enableAccessControlCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );
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

        // Enable LDAPS Checkbox
        addDirtyListener( enableLdapsCheckbox );
        addSelectionListener( enableLdapsCheckbox, enableLdapsCheckboxListener );

        // LDAPS Port Text
        addDirtyListener( ldapsPortText );
        addModifyListener( ldapsPortText, ldapsPortTextListener );

        // LDAPS Address Text
        addDirtyListener( ldapsAddressText );
        addModifyListener( ldapsAddressText, ldapsAddressTextListener );

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

        // Allow Anonymous Access Checkbox
        addDirtyListener( allowAnonymousAccessCheckbox );
        addSelectionListener( allowAnonymousAccessCheckbox, allowAnonymousAccessCheckboxListener );

        // Enable Access Control Checkbox
        addDirtyListener( enableAccessControlCheckbox );
        addSelectionListener( enableAccessControlCheckbox, enableAccessControlCheckboxListener );
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

        // Enable LDAPS Checkbox
        removeDirtyListener( enableLdapsCheckbox );
        removeSelectionListener( enableLdapsCheckbox, enableLdapsCheckboxListener );

        // LDAPS Port Text
        removeDirtyListener( ldapsPortText );
        removeModifyListener( ldapsPortText, ldapsPortTextListener );

        // LDAPS Address Text
        removeDirtyListener( ldapsAddressText );
        removeModifyListener( ldapsAddressText, ldapsAddressTextListener );

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

        // Allow Anonymous Access Checkbox
        removeDirtyListener( allowAnonymousAccessCheckbox );
        removeSelectionListener( allowAnonymousAccessCheckbox, allowAnonymousAccessCheckboxListener );

        // Enable Access Control Checkbox
        removeDirtyListener( enableAccessControlCheckbox );
        removeSelectionListener( enableAccessControlCheckbox, enableAccessControlCheckboxListener );
    }


    /**
     * {@inheritDoc}
     */
    protected void refreshUI()
    {
        if ( isInitialized() )
        {
            removeListeners();

            DirectoryServiceBean directoryServiceBean = getDirectoryServiceBean();

            // LDAP Server
            TransportBean ldapServerTransportBean = LdapLdapsServersPage
                .getLdapServerTransportBean( directoryServiceBean );
            setSelection( enableLdapCheckbox, ldapServerTransportBean.isEnabled() );
            setEnabled( ldapPortText, enableLdapCheckbox.getSelection() );
            setText( ldapPortText, Integer.toString( ldapServerTransportBean.getSystemPort() ) );
            setEnabled( ldapAddressText, enableLdapCheckbox.getSelection() );
            setText( ldapAddressText, ldapServerTransportBean.getTransportAddress() );

            // LDAPS Server
            TransportBean ldapsServerTransportBean = LdapLdapsServersPage
                .getLdapsServerTransportBean( directoryServiceBean );
            setSelection( enableLdapsCheckbox, ldapsServerTransportBean.isEnabled() );
            setEnabled( ldapsPortText, enableLdapsCheckbox.getSelection() );
            setText( ldapsPortText, Integer.toString( ldapsServerTransportBean.getSystemPort() ) );
            setEnabled( ldapsAddressText, enableLdapsCheckbox.getSelection() );
            setText( ldapsAddressText, ldapsServerTransportBean.getTransportAddress() );

            // Kerberos Server
            KdcServerBean kdcServerBean = KerberosServerPage.getKdcServerBean( directoryServiceBean );
            setSelection( enableKerberosCheckbox, kdcServerBean.isEnabled() );
            setEnabled( kerberosPortText, enableKerberosCheckbox.getSelection() );
            setText( kerberosPortText, "" + kdcServerBean.getTransports()[0].getSystemPort() ); //$NON-NLS-1$

            // Change Password Server
            ChangePasswordServerBean changePasswordServerBean = KerberosServerPage
                .getChangePasswordServerBean( directoryServiceBean );
            setSelection( enableChangePasswordCheckbox, changePasswordServerBean.isEnabled() );
            setEnabled( changePasswordPortText, enableChangePasswordCheckbox.getSelection() );
            setText( changePasswordPortText, "" + changePasswordServerBean.getTransports()[0].getSystemPort() ); //$NON-NLS-1$

            // Partitions
            List<PartitionBean> partitions = directoryServiceBean.getPartitions();
            if ( partitions.size() == 1 )
            {
                partitionsLabel.setText( Messages.getString( "OverviewPage.ThereIsOnePartitionDefined" ) ); //$NON-NLS-1$
            }
            else
            {
                partitionsLabel.setText( NLS.bind(
                    Messages.getString( "OverviewPage.ThereAreXPartitionsDefined" ), partitions.size() ) ); //$NON-NLS-1$
            }
            partitionsTableViewer.setInput( partitions.toArray() );

            // Options
            allowAnonymousAccessCheckbox.setSelection( directoryServiceBean.isDsAllowAnonymousAccess() );
            enableAccessControlCheckbox.setSelection( directoryServiceBean.isDsAccessControlEnabled() );

            addListeners();
        }
    }
}
