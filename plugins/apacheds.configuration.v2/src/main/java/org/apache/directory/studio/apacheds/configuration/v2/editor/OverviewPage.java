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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.server.config.beans.ChangePasswordServerBean;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.KdcServerBean;
import org.apache.directory.server.config.beans.LdapServerBean;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
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

    // UI Fields
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

        initUI();
        
        addListeners();
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
        partitionsTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return ApacheDS2ConfigurationPlugin.getDefault().getImage(
                    ApacheDS2ConfigurationPluginConstants.IMG_PARTITION );
            };
        } );

        openPartitionsConfigurationLink = toolkit.createHyperlink( composite,
            "Advanced Partitions configuration...", SWT.NONE );
        openPartitionsConfigurationLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false,
            gridLayout.numColumns, 1 ) );
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
    }


    private void initUI()
    {
        ConfigBean configBean = getConfigBean();

        DirectoryServiceBean directoryServiceBean = configBean.getDirectoryServiceBean();

        LdapServerBean ldapServerBean = directoryServiceBean.getLdapServerBean();
        KdcServerBean kdcServerBean = directoryServiceBean.getKdcServerBean();
        ChangePasswordServerBean changePasswordServerBean = directoryServiceBean.getChangePasswordServerBean();

        enableLdapCheckbox.setSelection( ldapServerBean.isEnabled() );
        ldapPortText.setText( ldapServerBean.getTransports()[0].getSystemPort() + "" );

        enableLdapsCheckbox.setSelection( true );
        ldapsPortText.setText( "10636" );

        enableKerberosCheckbox.setSelection( kdcServerBean.isEnabled() );
        kerberosPortText.setText( "" + kdcServerBean.getTransports()[0].getSystemPort() );

        enableChangePasswordCheckbox.setSelection( changePasswordServerBean.isEnabled() );
        changePasswordPortText.setText( "" + changePasswordServerBean.getTransports()[0].getSystemPort() );

        partitionsLabel.setText( "There are 2 partitions defined:" );
        List<String> partitionsList = new ArrayList<String>();
        partitionsList.add( "dc=example,dc=com (id=example)" );
        partitionsList.add( "ou=system (id=system)" );
        partitionsTableViewer.setInput( partitionsList.toArray() );

        allowAnonymousAccessCheckbox.setSelection( directoryServiceBean.isDsAllowAnonymousAccess() );
        enableAccesControlCheckbox.setSelection( directoryServiceBean.isDsAccessControlEnabled() );
    }


    /**
     * Adds listeners to UI Controls.
     */
    private void addListeners()
    {
        addDirtyListener( enableLdapCheckbox );
        addDirtyListener( ldapPortText );
        addDirtyListener( enableLdapsCheckbox );
        addDirtyListener( ldapsPortText );
        addDirtyListener( enableKerberosCheckbox );
        addDirtyListener( kerberosPortText );
        addDirtyListener( enableChangePasswordCheckbox );
        addDirtyListener( allowAnonymousAccessCheckbox );
        addDirtyListener( enableAccesControlCheckbox );
    }
}
