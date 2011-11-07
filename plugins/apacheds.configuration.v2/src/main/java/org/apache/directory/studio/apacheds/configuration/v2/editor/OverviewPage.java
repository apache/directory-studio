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
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.KdcServerBean;
import org.apache.directory.server.config.beans.PartitionBean;
import org.apache.directory.server.config.beans.TransportBean;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
    private Button enableKerberosCheckbox;
    private Text kerberosPortText;
    private Button enableChangePasswordCheckbox;
    private Text changePasswordPortText;
    private Hyperlink openLdapConfigurationLink;
    private Hyperlink openKerberosConfigurationLink;
    private Hyperlink openPartitionsConfigurationLink;
    private Hyperlink openOptionsConfigurationLink;
    private Label partitionsLabel;
    private TableViewer partitionsTableViewer;
    private Button allowAnonymousAccessCheckbox;
    private Button enableAccesControlCheckbox;

    // UI Control Listeners
    private SelectionAdapter enableLdapCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            LdapLdapsServersPage.getLdapServerTransportBean( getDirectoryServiceBean() ).setEnabled(
                enableLdapCheckbox.getSelection() );
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
        createPartitionsSection( toolkit, leftComposite );
        createKerberosServerSection( toolkit, rightComposite );
        createOptionsSection( toolkit, rightComposite );

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

        openLdapConfigurationLink = toolkit.createHyperlink( composite,
            "Advanced LDAP/LDAPS configuration...", SWT.NONE );
        openLdapConfigurationLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
        openLdapConfigurationLink.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                getServerConfigurationEditor().showPage( LdapLdapsServersPage.class );
            }
        } );
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

        openKerberosConfigurationLink = toolkit.createHyperlink( composite,
            "Advanced Kerberos configuration...", SWT.NONE );
        openKerberosConfigurationLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
        openKerberosConfigurationLink.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                getServerConfigurationEditor().showPage( KerberosServerPage.class );
            }
        } );
    }


    private void createPartitionsSection( FormToolkit toolkit, Composite parent )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "Partitions" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        partitionsLabel = toolkit.createLabel( composite, "" );
        partitionsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        Table partitionsTable = toolkit.createTable( composite, SWT.NULL );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.heightHint = 45;
        partitionsTable.setLayoutData( gd );
        partitionsTableViewer = new TableViewer( partitionsTable );
        partitionsTableViewer.setContentProvider( new ArrayContentProvider() );
        partitionsTableViewer.setLabelProvider( PartitionsPage.PARTITIONS_LABEL_PROVIDER );

        openPartitionsConfigurationLink = toolkit.createHyperlink( composite,
            "Advanced Partitions configuration...", SWT.NONE );
        openPartitionsConfigurationLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
        openPartitionsConfigurationLink.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                getServerConfigurationEditor().showPage( PartitionsPage.class );
            }
        } );
    }


    private void createOptionsSection( FormToolkit toolkit, Composite parent )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "Options" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Allow Anonymous Access
        allowAnonymousAccessCheckbox = toolkit.createButton( composite, "Allow Anonymous Access", SWT.CHECK );
        allowAnonymousAccessCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );

        // Enable Access Control
        enableAccesControlCheckbox = toolkit.createButton( composite, "Enable Access Control", SWT.CHECK );
        enableAccesControlCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false ) );

        openOptionsConfigurationLink = toolkit.createHyperlink( composite,
            "Advanced Options configuration...", SWT.NONE );
        openOptionsConfigurationLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
        openOptionsConfigurationLink.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                getServerConfigurationEditor().showPage( null );
            }
        } );
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

        addDirtyListener( enableKerberosCheckbox );
        addDirtyListener( kerberosPortText );
        addDirtyListener( enableChangePasswordCheckbox );
        addDirtyListener( allowAnonymousAccessCheckbox );
        addDirtyListener( enableAccesControlCheckbox );
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

        removeDirtyListener( enableKerberosCheckbox );
        removeDirtyListener( kerberosPortText );
        removeDirtyListener( enableChangePasswordCheckbox );
        removeDirtyListener( allowAnonymousAccessCheckbox );
        removeDirtyListener( enableAccesControlCheckbox );
    }


    /**
     * {@inheritDoc}
     */
    protected void refreshUI()
    {
        removeListeners();

        ConfigBean configBean = getConfigBean();

        DirectoryServiceBean directoryServiceBean = configBean.getDirectoryServiceBean();

        KdcServerBean kdcServerBean = directoryServiceBean.getKdcServerBean();
        ChangePasswordServerBean changePasswordServerBean = directoryServiceBean.getChangePasswordServerBean();

        TransportBean ldapServerTransportBean = LdapLdapsServersPage.getLdapServerTransportBean( directoryServiceBean );
        setSelection( enableLdapCheckbox, ldapServerTransportBean.isEnabled() );
        setText( ldapPortText, ldapServerTransportBean.getSystemPort() + "" );

        TransportBean ldapsServerTransportBean = LdapLdapsServersPage
            .getLdapsServerTransportBean( directoryServiceBean );
        setSelection( enableLdapsCheckbox, ldapsServerTransportBean.isEnabled() );
        setText( ldapsPortText, ldapsServerTransportBean.getSystemPort() + "" );

        enableKerberosCheckbox.setSelection( kdcServerBean.isEnabled() );
        kerberosPortText.setText( "" + kdcServerBean.getTransports()[0].getSystemPort() );

        enableChangePasswordCheckbox.setSelection( changePasswordServerBean.isEnabled() );
        changePasswordPortText.setText( "" + changePasswordServerBean.getTransports()[0].getSystemPort() );

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

        allowAnonymousAccessCheckbox.setSelection( directoryServiceBean.isDsAllowAnonymousAccess() );
        enableAccesControlCheckbox.setSelection( directoryServiceBean.isDsAccessControlEnabled() );

        addListeners();
    }
}
