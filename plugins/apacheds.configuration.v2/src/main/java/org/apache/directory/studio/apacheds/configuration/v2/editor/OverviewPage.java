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
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OverviewPage extends ServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = OverviewPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = "Overview";

    // UI Controls
    private Button enableLdapCheckbox;
    private Text ldapPortText;
    private Button enableLdapsCheckbox;
    private Text ldapsPortText;
    private Hyperlink openLdapConfigurationLink;
    private Button enableKerberosCheckbox;
    private Text kerberosPortText;
    private Button enableChangePasswordCheckbox;
    private Text changePasswordPortText;
    private Hyperlink openKerberosConfigurationLink;
    private Label partitionsLabel;
    private TableViewer partitionsTableViewer;
    private Hyperlink openPartitionsConfigurationLink;
    private Button allowAnonymousAccessCheckbox;
    private Button enableAccessControlCheckbox;
    private Hyperlink openOptionsConfigurationLink;

    // UI Control Listeners
    private SelectionAdapter enableLdapCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            LdapLdapsServersPage.getLdapServerTransportBean( getDirectoryServiceBean() ).setEnabled(
                enableLdapCheckbox.getSelection() );
            setEnabled( ldapPortText, enableLdapCheckbox.getSelection() );
        }
    };
    private ModifyListener ldapPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            LdapLdapsServersPage.getLdapServerTransportBean( getDirectoryServiceBean() ).setSystemPort(
                Integer.parseInt( ldapPortText.getText() ) );
        }
    };
    private SelectionAdapter enableLdapsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            LdapLdapsServersPage.getLdapsServerTransportBean( getDirectoryServiceBean() ).setEnabled(
                enableLdapsCheckbox.getSelection() );
            setEnabled( ldapsPortText, enableLdapsCheckbox.getSelection() );
        }
    };
    private ModifyListener ldapsPortTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            LdapLdapsServersPage.getLdapsServerTransportBean( getDirectoryServiceBean() ).setSystemPort(
                Integer.parseInt( ldapsPortText.getText() ) );
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
            KdcServerBean kdcServerBean = getConfigBean().getDirectoryServiceBean().getKdcServerBean();
            kdcServerBean.setEnabled( enableKerberosCheckbox.getSelection() );
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
    private HyperlinkAdapter openOptionsConfigurationLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            getServerConfigurationEditor().showPage( null );
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

        // LDAP Configuration Link
        openLdapConfigurationLink = toolkit.createHyperlink( composite,
            "Advanced LDAP/LDAPS configuration...", SWT.NONE );
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
        section.setText( "Kerberos Server" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 4, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Enable Kerberos Server Checkbox
        enableKerberosCheckbox = toolkit.createButton( composite, "Enable Kerberos Server", SWT.CHECK );
        enableKerberosCheckbox
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, gridLayout.numColumns, 1 ) );

        // Kerberos Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, "Port:" );
        kerberosPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "60088" ); //$NON-NLS-1$

        // Enable Change Password Server Checkbox
        enableChangePasswordCheckbox = toolkit.createButton( composite, "Enable Kerberos Change Password Server",
            SWT.CHECK );
        enableChangePasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );

        // Change Password Server Port Text
        toolkit.createLabel( composite, TABULATION );
        toolkit.createLabel( composite, "Port:" );
        changePasswordPortText = createPortText( toolkit, composite );
        createDefaultValueLabel( toolkit, composite, "60464" ); //$NON-NLS-1$

        // Kerberos Configuration Link
        openKerberosConfigurationLink = toolkit.createHyperlink( composite,
            "Advanced Kerberos configuration...", SWT.NONE );
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
        section.setText( "Partitions" );
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

        // Partitions Configuration Link
        openPartitionsConfigurationLink = toolkit.createHyperlink( composite,
            "Advanced Partitions configuration...", SWT.NONE );
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
        section.setText( "Options" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Allow Anonymous Access Checkbox
        allowAnonymousAccessCheckbox = toolkit.createButton( composite, "Allow Anonymous Access", SWT.CHECK );
        allowAnonymousAccessCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );

        // Enable Access Control Checkbox
        enableAccessControlCheckbox = toolkit.createButton( composite, "Enable Access Control", SWT.CHECK );
        enableAccessControlCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );

        // Options Configuration Link
        openOptionsConfigurationLink = toolkit.createHyperlink( composite,
            "Advanced Options configuration...", SWT.NONE );
        openOptionsConfigurationLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
        openOptionsConfigurationLink.addHyperlinkListener( openOptionsConfigurationLinkListener );
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

        // Enable LDAPS Checkbox
        removeDirtyListener( enableLdapsCheckbox );
        removeSelectionListener( enableLdapsCheckbox, enableLdapsCheckboxListener );

        // LDAPS Port Text
        removeDirtyListener( ldapsPortText );
        removeModifyListener( ldapsPortText, ldapsPortTextListener );

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
        removeListeners();

        DirectoryServiceBean directoryServiceBean = getDirectoryServiceBean();

        // LDAP Server
        TransportBean ldapServerTransportBean = LdapLdapsServersPage.getLdapServerTransportBean( directoryServiceBean );
        setSelection( enableLdapCheckbox, ldapServerTransportBean.isEnabled() );
        setEnabled( ldapPortText, enableLdapCheckbox.getSelection() );
        setText( ldapPortText, ldapServerTransportBean.getSystemPort() + "" ); //$NON-NLS-1$

        // LDAPS Server
        TransportBean ldapsServerTransportBean = LdapLdapsServersPage
            .getLdapsServerTransportBean( directoryServiceBean );
        setSelection( enableLdapsCheckbox, ldapsServerTransportBean.isEnabled() );
        setEnabled( ldapsPortText, enableLdapsCheckbox.getSelection() );
        setText( ldapsPortText, ldapsServerTransportBean.getSystemPort() + "" ); //$NON-NLS-1$

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
            partitionsLabel.setText( "There is one partition defined:" );
        }
        else
        {
            partitionsLabel.setText( NLS.bind( "There are {0} partitions defined:", partitions.size() ) );
        }
        partitionsTableViewer.setInput( partitions.toArray() );

        // Options
        allowAnonymousAccessCheckbox.setSelection( directoryServiceBean.isDsAllowAnonymousAccess() );
        enableAccessControlCheckbox.setSelection( directoryServiceBean.isDsAccessControlEnabled() );

        addListeners();
    }
}
