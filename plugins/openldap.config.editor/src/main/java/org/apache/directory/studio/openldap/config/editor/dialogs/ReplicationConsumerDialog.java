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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.FilterWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import org.apache.directory.studio.openldap.common.ui.dialogs.AttributeDialog;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.syncrepl.BindMethod;
import org.apache.directory.studio.openldap.syncrepl.Provider;
import org.apache.directory.studio.openldap.syncrepl.SaslMechanism;
import org.apache.directory.studio.openldap.syncrepl.Scope;
import org.apache.directory.studio.openldap.syncrepl.StartTls;
import org.apache.directory.studio.openldap.syncrepl.SyncRepl;
import org.apache.directory.studio.openldap.syncrepl.Type;


/**
 * The ReplicationConsumerDialog is used to edit the configuration of a SyncRepl consumer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReplicationConsumerDialog extends Dialog
{
    /** The Simple Authentication tab item index */
    private static final int SIMPLE_AUTHENTICATION_TAB_ITEM_INDEX = 0;

    /** The SASL Authentication tab item index */
    private static final int SASL_AUTHENTICATION_TAB_ITEM_INDEX = 1;

    /** The SyncRepl value */
    private SyncRepl syncRepl;

    /** The connection */
    private IBrowserConnection browserConnection;

    private List<String> attributes = new ArrayList<String>();

    // UI widgets
    private Button okButton;
    private ScrolledComposite scrolledComposite;
    private Composite composite;
    private Text replicaIdText;
    private ComboViewer replicationTypeComboViewer;
    private Button configureReplicationButton;
    private Text hostText;
    private Text portText;
    private ComboViewer encryptionMethodComboViewer;
    private Button configureStartTlsButton;
    private TabFolder authenticationTabFolder;
    private Text bindDnText;
    private Text credentialsText;
    private Button showCredentialsCheckbox;
    private Label saslAuthenticationLabel;
    private Button configureSaslAuthenticationButton;
    private EntryWidget searchBaseDnEntryWidget;
    private FilterWidget filterWidget;
    private ComboViewer scopeComboViewer;
    private TableViewer attributesTableViewer;
    private Button addAttributeButton;
    private Button editAttributeButton;
    private Button deleteAttributeButton;
    private Button attributesOnlyCheckbox;

    // Listeners
    private VerifyListener integerVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
            }
        }
    };

    private ModifyListener replicatIdTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String replicaId = replicaIdText.getText();

            if ( ( replicaId != null ) && ( !"".equals( replicaId ) ) )
            {
                syncRepl.setRid( replicaId );
            }
            else
            {
                syncRepl.setRid( null );
            }

            updateOkButtonEnableState();
        }
    };

    private ISelectionChangedListener replicationTypeComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            syncRepl.setType( getReplicationType() );
        }
    };

    private SelectionListener configureReplicationButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ReplicationOptionsDialog dialog = new ReplicationOptionsDialog( getShell(), syncRepl, browserConnection );
            if ( dialog.open() == ReplicationOptionsDialog.OK )
            {
                syncRepl = dialog.getSyncRepl();
                refreshUI();
            }
        }
    };

    private ModifyListener hostTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            syncRepl.setProvider( getProvider() );

            updateOkButtonEnableState();
        }
    };

    private ModifyListener portTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            syncRepl.setProvider( getProvider() );
        }
    };

    private ISelectionChangedListener encryptionMethodComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            syncRepl.setProvider( getProvider() );

            // Getting the selected encryption method
            EncryptionMethod encryptionMethod = getEncryptionMethod();

            if ( ( encryptionMethod == EncryptionMethod.NO_ENCRYPTION )
                || ( encryptionMethod == EncryptionMethod.SSL_ENCRYPTION_LDAPS ) )
            {
                configureStartTlsButton.setEnabled( false );
            }
            else if ( encryptionMethod == EncryptionMethod.START_TLS_EXTENSION )
            {
                configureStartTlsButton.setEnabled( true );
            }
        }
    };

    private SelectionListener configureStartTlsButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // TODO
        }
    };

    private SelectionListener authenticationTabFolderListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // Simple Authentication
            if ( authenticationTabFolder.getSelectionIndex() == SIMPLE_AUTHENTICATION_TAB_ITEM_INDEX )
            {
                syncRepl.setBindMethod( BindMethod.SIMPLE );

                // Reseting SASL authentication parameters
                syncRepl.setSaslMech( null );
                syncRepl.setAuthcid( null );
                syncRepl.setAuthzid( null );
                syncRepl.setCredentials( null );
                syncRepl.setRealm( null );
                syncRepl.setSecProps( null );
            }
            // SASL Authentication
            else if ( authenticationTabFolder.getSelectionIndex() == SASL_AUTHENTICATION_TAB_ITEM_INDEX )
            {
                syncRepl.setBindMethod( BindMethod.SASL );

                // Reseting simple authentication parameters
                syncRepl.setBindDn( null );
                syncRepl.setCredentials( null );
            }

            refreshUI();
        };
    };

    private ModifyListener bindDnTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String bindDn = bindDnText.getText();

            if ( ( bindDn != null ) && ( !"".equals( bindDn ) ) )
            {
                syncRepl.setBindDn( bindDn );
            }
            else
            {
                syncRepl.setBindDn( null );
            }
        }
    };

    private ModifyListener credentialsTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String credentials = credentialsText.getText();

            if ( ( credentials != null ) && ( !"".equals( credentials ) ) )
            {
                syncRepl.setCredentials( credentials );
            }
            else
            {
                syncRepl.setCredentials( null );
            }
        }
    };

    private SelectionListener showCredentialsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            if ( showCredentialsCheckbox.getSelection() )
            {
                credentialsText.setEchoChar( '\0' );
            }
            else
            {
                credentialsText.setEchoChar( '\u2022' );
            }
        }
    };

    private SelectionListener configureSaslAuthenticationButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ReplicationSaslDialog dialog = new ReplicationSaslDialog( getShell(), syncRepl, browserConnection );
            if ( dialog.open() == ReplicationSaslDialog.OK )
            {
                syncRepl = dialog.getSyncRepl();
                refreshUI();
            }
        }
    };

    private WidgetModifyListener searchBaseDnEntryWidgetListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            Dn searchBaseDn = searchBaseDnEntryWidget.getDn();

            if ( ( searchBaseDn != null ) && ( !Dn.EMPTY_DN.equals( searchBaseDn ) ) )
            {
                syncRepl.setSearchBase( searchBaseDn.getName() );
            }
            else
            {
                syncRepl.setSearchBase( null );
            }

            updateOkButtonEnableState();
        }
    };

    private WidgetModifyListener filterWidgetListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            String filter = filterWidget.getFilter();

            if ( ( filter != null ) && ( !"".equals( filter ) ) )
            {
                syncRepl.setFilter( filter );
            }
            else
            {
                syncRepl.setFilter( null );
            }
        }
    };

    private ISelectionChangedListener scopeComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            syncRepl.setScope( getScope() );
        }
    };

    private ISelectionChangedListener attributesTableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            updateAttributesTableButtonsState();
        }
    };

    private IDoubleClickListener attributesTableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editAttributeButtonAction();
        }
    };

    private SelectionListener addAttributeButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addAttributeButtonAction();
        }
    };

    private SelectionListener editAttributeButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editAttributeButtonAction();
        }
    };

    private SelectionListener deleteAttributeButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteAttributeButtonAction();
        }
    };

    private SelectionListener attributesOnlyCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            syncRepl.setAttrsOnly( attributesOnlyCheckbox.getSelection() );
        };
    };


    /**
     * Creates a new instance of OverlayDialog.
     * 
     * @param parentShell the parent shell
     * @param index the index
     * @param browserConnection the connection
     */
    public ReplicationConsumerDialog( Shell parentShell, IBrowserConnection browserConnection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.browserConnection = browserConnection;
        this.syncRepl = createDefaultSyncRepl();
    }


    /**
     * Creates a new instance of OverlayDialog.
     * 
     * @param parentShell the parent shell
     * @param index the index
     * @param browserConnection the connection
     */
    public ReplicationConsumerDialog( Shell parentShell, SyncRepl syncRepl, IBrowserConnection browserConnection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.browserConnection = browserConnection;

        if ( syncRepl != null )
        {
            this.syncRepl = syncRepl.copy();
        }
        else
        {
            this.syncRepl = createDefaultSyncRepl();
        }
    }


    /**
     * Creates a default SyncRepl configuration.
     *
     * @return a default SyncRepl configuration
     */
    private SyncRepl createDefaultSyncRepl()
    {
        SyncRepl syncRepl = new SyncRepl();

        return syncRepl;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Replication Consumer" );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        updateOkButtonEnableState();
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        // Creating the scrolled composite
        scrolledComposite = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        scrolledComposite.setExpandHorizontal( true );
        scrolledComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Creating the composite and attaching it to the scrolled composite
        composite = new Composite( scrolledComposite, SWT.NONE );
        composite.setLayout( new GridLayout() );
        scrolledComposite.setContent( composite );

        createReplicationConsumerGroup( composite );
        createReplicationProviderGroup( composite );
        createReplicationAuthenticationGroup( composite );
        createReplicationDataGroup( composite );

        refreshUI();

        applyDialogFont( scrolledComposite );
        composite.setSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

        return scrolledComposite;
    }


    /**
     * Creates the replication consumer group.
     *
     * @param parent the parent composite
     */
    private void createReplicationConsumerGroup( Composite parent )
    {
        // Replication Provider Group
        Group group = BaseWidgetUtils.createGroup( parent, "Replication Consumer", 1 );
        GridLayout groupGridLayout = new GridLayout( 2, false );
        group.setLayout( groupGridLayout );
        group.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Replica ID
        BaseWidgetUtils.createLabel( group, "Replica ID:", 1 );
        replicaIdText = BaseWidgetUtils.createText( group, "", 1 );
        replicaIdText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Replication Type
        BaseWidgetUtils.createLabel( group, "Replication Type:", 1 );
        replicationTypeComboViewer = new ComboViewer( group );
        replicationTypeComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        replicationTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        replicationTypeComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof Type )
                {
                    Type type = ( Type ) element;

                    switch ( type )
                    {
                        case REFRESH_AND_PERSIST:
                            return "Refresh And Persist";
                        case REFRESH_ONLY:
                            return "Refresh Only";
                    }
                }

                return super.getText( element );
            }
        } );
        replicationTypeComboViewer.setInput( new Type[]
            { Type.REFRESH_AND_PERSIST, Type.REFRESH_ONLY } );

        // Configure Replication Options Button
        BaseWidgetUtils.createLabel( group, "", 1 );
        configureReplicationButton = BaseWidgetUtils.createButton( group, "Configure Replication Options...", 1 );
        configureReplicationButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    }


    /**
     * Creates the replication provider group.
     *
     * @param parent the parent composite
     */
    private void createReplicationProviderGroup( Composite parent )
    {
        // Replication Provider Group
        Group group = BaseWidgetUtils.createGroup( parent, "Replication Provider Connection", 1 );
        GridLayout groupGridLayout = new GridLayout( 2, false );
        group.setLayout( groupGridLayout );
        group.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Host
        BaseWidgetUtils.createLabel( group, "Provider Host:", 1 );
        hostText = BaseWidgetUtils.createText( group, "", 1 );
        hostText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Port
        BaseWidgetUtils.createLabel( group, "Provider Port:", 1 );
        portText = BaseWidgetUtils.createText( group, "", 1 );
        portText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Encryption Method
        BaseWidgetUtils.createLabel( group, "Encryption Method:", 1 );
        encryptionMethodComboViewer = new ComboViewer( group );
        encryptionMethodComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        encryptionMethodComboViewer.setContentProvider( new ArrayContentProvider() );
        encryptionMethodComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof EncryptionMethod )
                {
                    EncryptionMethod encryptionMethod = ( EncryptionMethod ) element;

                    switch ( encryptionMethod )
                    {
                        case NO_ENCRYPTION:
                            return "No Encryption";
                        case SSL_ENCRYPTION_LDAPS:
                            return "Use SSL Encryption (ldaps://)";
                        case START_TLS_EXTENSION:
                            return "Use Start TLS Extension";
                    }
                }

                return super.getText( element );
            }
        } );
        encryptionMethodComboViewer.setInput( new EncryptionMethod[]
            {
                EncryptionMethod.NO_ENCRYPTION,
                EncryptionMethod.SSL_ENCRYPTION_LDAPS,
                EncryptionMethod.START_TLS_EXTENSION } );

        // Configure Start TLS Button
        BaseWidgetUtils.createLabel( group, "", 1 );
        configureStartTlsButton = BaseWidgetUtils.createButton( group, "Configure Start TLS...", 1 );
        configureStartTlsButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        configureStartTlsButton.setEnabled( false );
    }


    /**
     * Creates the replication authentication group.
     *
     * @param parent the parent composite
     */
    private void createReplicationAuthenticationGroup( Composite parent )
    {
        // Replication Provider Group
        Group group = BaseWidgetUtils.createGroup( parent, "Authentication", 1 );
        GridLayout groupGridLayout = new GridLayout( 2, false );
        group.setLayout( groupGridLayout );
        group.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Authentication
        authenticationTabFolder = new TabFolder( group, SWT.TOP );
        authenticationTabFolder.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );

        // Simple Authentication Composite
        Composite simpleAuthenticationComposite = new Composite( authenticationTabFolder, SWT.NONE );
        simpleAuthenticationComposite.setLayout( new GridLayout( 2, false ) );
        simpleAuthenticationComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Bind DN Text
        BaseWidgetUtils.createLabel( simpleAuthenticationComposite, "Bind DN:", 1 );
        bindDnText = BaseWidgetUtils.createText( simpleAuthenticationComposite, "", 1 );
        bindDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Credentials Text
        BaseWidgetUtils.createLabel( simpleAuthenticationComposite, "Credentials:", 1 );
        credentialsText = BaseWidgetUtils.createText( simpleAuthenticationComposite, "", 1 );
        credentialsText.setEchoChar( '\u2022' );

        // Show Credentials Checkbox
        BaseWidgetUtils.createLabel( simpleAuthenticationComposite, "", 1 );
        showCredentialsCheckbox = BaseWidgetUtils.createCheckbox( simpleAuthenticationComposite, "Show Credentials", 1 );

        // Simple Authentication TabItem
        TabItem simpleAuthenticationTabItem = new TabItem( authenticationTabFolder, SWT.NONE,
            SIMPLE_AUTHENTICATION_TAB_ITEM_INDEX );
        simpleAuthenticationTabItem.setText( "Simple Authentication" );
        simpleAuthenticationTabItem.setControl( simpleAuthenticationComposite );

        // SASL Authentication Composite
        Composite saslAuthenticationComposite = new Composite( authenticationTabFolder, SWT.NONE );
        saslAuthenticationComposite.setLayout( new GridLayout() );
        saslAuthenticationComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // SASL Authentication Wrapped Label
        saslAuthenticationLabel = new Label( saslAuthenticationComposite, SWT.WRAP | SWT.CENTER );
        saslAuthenticationLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, true ) );

        // Configure SASL Authentication Button
        configureSaslAuthenticationButton = BaseWidgetUtils.createButton( saslAuthenticationComposite,
            "Configure SASL Authentication...", 1 );
        configureSaslAuthenticationButton.setLayoutData( new GridData( SWT.CENTER, SWT.BOTTOM, true, false ) );

        // SASL Authentication TabItem
        TabItem saslAuthenticationTabItem = new TabItem( authenticationTabFolder, SWT.NONE,
            SASL_AUTHENTICATION_TAB_ITEM_INDEX );
        saslAuthenticationTabItem.setText( "SASL Authentication" );
        saslAuthenticationTabItem.setControl( saslAuthenticationComposite );
    }


    /**
     * Creates the replication data group.
     *
     * @param parent the parent composite
     */
    private void createReplicationDataGroup( Composite parent )
    {
        // Replication Data Group
        Group group = BaseWidgetUtils.createGroup( parent, "Replication Data Configuration", 1 );
        GridLayout groupGridLayout = new GridLayout( 3, false );
        groupGridLayout.verticalSpacing = groupGridLayout.marginHeight = 0;
        group.setLayout( groupGridLayout );
        group.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Search Base DN Text
        BaseWidgetUtils.createLabel( group, "Search Base DN:", 1 );
        searchBaseDnEntryWidget = new EntryWidget( browserConnection, Dn.EMPTY_DN );
        searchBaseDnEntryWidget.createWidget( group );

        // Filter Text
        BaseWidgetUtils.createLabel( group, "Filter:", 1 );
        filterWidget = new FilterWidget();
        filterWidget.setBrowserConnection( browserConnection );
        filterWidget.createWidget( group );

        // Scope Combo Viewer
        BaseWidgetUtils.createLabel( group, "Scope:", 1 );
        scopeComboViewer = new ComboViewer( group );
        scopeComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        scopeComboViewer.setContentProvider( new ArrayContentProvider() );
        scopeComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof Scope )
                {
                    Scope scope = ( Scope ) element;

                    switch ( scope )
                    {
                        case BASE:
                            return "Base";
                        case ONE:
                            return "One Level";
                        case SUB:
                            return "Subtree";
                        case SUBORD:
                            return "Subordinate Subtree";
                    }
                }

                return super.getText( element );
            }
        } );
        scopeComboViewer.setInput( new Scope[]
            { Scope.SUB, Scope.SUBORD, Scope.ONE, Scope.BASE } );

        // Attributes Table Viewer
        BaseWidgetUtils.createLabel( group, "Attributes:", 1 );
        Composite attributesTableComposite = new Composite( group, SWT.NONE );
        attributesTableComposite.setLayout( new GridLayout( 2, false ) );
        attributesTableComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
        attributesTableViewer = new TableViewer( attributesTableComposite );
        attributesTableViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 ) );
        attributesTableViewer.setContentProvider( new ArrayContentProvider() );
        attributesTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( SchemaConstants.ALL_OPERATIONAL_ATTRIBUTES.equals( element ) )
                {
                    return SchemaConstants.ALL_OPERATIONAL_ATTRIBUTES + " " + "<all operational attributes>";
                }
                else if ( SchemaConstants.ALL_USER_ATTRIBUTES.equals( element ) )
                {
                    return SchemaConstants.ALL_USER_ATTRIBUTES + " " + "<all user attributes>";
                }

                return super.getText( element );
            };


            public Image getImage( Object element )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_ATTRIBUTE );
            }
        } );
        attributesTableViewer.setInput( attributes );

        // Add Attribute Button
        addAttributeButton = BaseWidgetUtils.createButton( attributesTableComposite, "Add...", 1 );
        addAttributeButton.setLayoutData( createNewButtonGridData() );

        // Edit Attribute Button
        editAttributeButton = BaseWidgetUtils.createButton( attributesTableComposite, "Edit...", 1 );
        editAttributeButton.setEnabled( false );
        editAttributeButton.setLayoutData( createNewButtonGridData() );

        // Delete Attribute Button
        deleteAttributeButton = BaseWidgetUtils.createButton( attributesTableComposite, "Delete", 1 );
        deleteAttributeButton.setEnabled( false );
        deleteAttributeButton.setLayoutData( createNewButtonGridData() );

        // Attributes Only Checkbox
        BaseWidgetUtils.createLabel( group, "", 1 ); //$NON-NLS-1$
        attributesOnlyCheckbox = BaseWidgetUtils.createCheckbox( group, "Attributes Only (no values)", 1 );
        attributesOnlyCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    }


    /**
     * Updates the state of the attributes table buttons.
     */
    private void updateAttributesTableButtonsState()
    {
        StructuredSelection selection = ( StructuredSelection ) attributesTableViewer.getSelection();

        editAttributeButton.setEnabled( !selection.isEmpty() );
        deleteAttributeButton.setEnabled( !selection.isEmpty() );
    }


    /**
     * Action launched when the add attribute button is clicked.
     */
    private void addAttributeButtonAction()
    {
        AttributeDialog dialog = new AttributeDialog( addAttributeButton.getShell(), browserConnection );
        if ( dialog.open() == AttributeDialog.OK )
        {
            String attribute = dialog.getAttribute();

            attributes.add( attribute );
            syncRepl.setAttributes( attributes.toArray( new String[0] ) );
            attributesTableViewer.refresh();
            attributesTableViewer.setSelection( new StructuredSelection( attribute ) );
        }
    }


    /**
     * Action launched when the edit attribute button is clicked.
     */
    private void editAttributeButtonAction()
    {
        StructuredSelection selection = ( StructuredSelection ) attributesTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            String selectedAttribute = ( String ) selection.getFirstElement();
            AttributeDialog dialog = new AttributeDialog( editAttributeButton.getShell(), browserConnection,
                selectedAttribute );
            if ( dialog.open() == AttributeDialog.OK )
            {
                String attribute = dialog.getAttribute();
                int selectedAttributeIndex = attributes.indexOf( selectedAttribute );

                attributes.remove( selectedAttributeIndex );
                attributes.add( selectedAttributeIndex, attribute );
                syncRepl.setAttributes( attributes.toArray( new String[0] ) );
                attributesTableViewer.refresh();
                attributesTableViewer.setSelection( new StructuredSelection( attribute ) );
            }
        }
    }


    /**
     * Action launched when the delete attribute button is clicked.
     */
    private void deleteAttributeButtonAction()
    {
        StructuredSelection selection = ( StructuredSelection ) attributesTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            String selectedAttribute = ( String ) selection.getFirstElement();
            attributes.remove( selectedAttribute );
            syncRepl.setAttributes( attributes.toArray( new String[0] ) );
            attributesTableViewer.refresh();

            //            updateAttributesTableButtonsState();
        }
    }


    /**
     * Create a new button grid data.
     *
     * @return the new button grid data
     */
    private GridData createNewButtonGridData()
    {
        GridData gd = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        gd.widthHint = IDialogConstants.BUTTON_WIDTH;
        return gd;
    }


    private void refreshUI()
    {
        if ( syncRepl != null )
        {
            removeListeners();

            //
            // Replication Consumer
            //

            // Replica ID
            String replicaId = syncRepl.getRid();

            if ( replicaId != null )
            {
                replicaIdText.setText( replicaId );
            }
            else
            {
                replicaIdText.setText( "" );
            }

            // Replication Type
            Type replicationType = syncRepl.getType();

            if ( replicationType != null )
            {
                replicationTypeComboViewer.setSelection( new StructuredSelection( replicationType ) );
            }
            else
            {
                replicationTypeComboViewer.setSelection( new StructuredSelection( Type.REFRESH_AND_PERSIST ) );
            }

            //
            // Replication Provider Connection
            //

            // Provider
            Provider provider = syncRepl.getProvider();

            if ( provider != null )
            {
                // Provider Host
                String providerHost = provider.getHost();

                if ( providerHost != null )
                {
                    hostText.setText( providerHost );
                }
                else
                {
                    hostText.setText( "" );
                }

                // Provider Port
                int providerPort = provider.getPort();

                if ( providerPort != Provider.NO_PORT )
                {
                    portText.setText( "" + providerPort );
                }
                else
                {
                    portText.setText( "" );
                }

                // Encryption Type
                boolean isLdaps = provider.isLdaps();
                StartTls startTls = syncRepl.getStartTls();

                if ( isLdaps && ( startTls == null ) )
                {
                    // SSL Encryption (LDAPS)
                    encryptionMethodComboViewer.setSelection( new StructuredSelection(
                        EncryptionMethod.SSL_ENCRYPTION_LDAPS ) );
                }
                else if ( !isLdaps && ( startTls != null ) )
                {
                    // Start TLS
                    encryptionMethodComboViewer.setSelection( new StructuredSelection(
                        EncryptionMethod.START_TLS_EXTENSION ) );
                }
                else
                {
                    // No Encryption Type
                    encryptionMethodComboViewer
                        .setSelection( new StructuredSelection( EncryptionMethod.NO_ENCRYPTION ) );
                }
            }
            else
            {
                hostText.setText( "" );
                portText.setText( "" );
                encryptionMethodComboViewer.setSelection( new StructuredSelection( EncryptionMethod.NO_ENCRYPTION ) );
            }

            //
            // Authentication
            //
            BindMethod bindMethod = syncRepl.getBindMethod();

            if ( ( bindMethod == null ) || ( bindMethod == BindMethod.SIMPLE ) )
            {
                // Simple Authentication
                authenticationTabFolder.setSelection( SIMPLE_AUTHENTICATION_TAB_ITEM_INDEX );

                // Bind DN
                String bindDn = syncRepl.getBindDn();

                if ( bindDn != null )
                {
                    bindDnText.setText( bindDn );
                }
                else
                {
                    bindDnText.setText( "" );
                }

                // Credentials
                String credentials = syncRepl.getCredentials();

                if ( credentials != null )
                {
                    credentialsText.setText( credentials );
                }
                else
                {
                    credentialsText.setText( "" );
                }

                // SASL Authentication Label
                saslAuthenticationLabel.setText( getSaslAuthenticationLabelText() );
            }
            else
            {
                // SASL Authentication
                authenticationTabFolder.setSelection( SASL_AUTHENTICATION_TAB_ITEM_INDEX );

                // SASL Authentication Label
                saslAuthenticationLabel.setText( getSaslAuthenticationLabelText() );
                saslAuthenticationLabel.update();

                // Simple Authentication fields
                bindDnText.setText( "" );
                credentialsText.setText( "" );
            }

            //
            // Replication Data Configuration
            //

            // Search Base DN
            String searchBaseDn = syncRepl.getSearchBase();

            if ( searchBaseDn != null )
            {
                try
                {
                    searchBaseDnEntryWidget.setInput( browserConnection, new Dn( searchBaseDn ) );
                }
                catch ( LdapInvalidDnException e )
                {
                    // Silent
                    searchBaseDnEntryWidget.setInput( browserConnection, Dn.EMPTY_DN );
                }
            }
            else
            {
                searchBaseDnEntryWidget.setInput( browserConnection, Dn.EMPTY_DN );
            }

            // Filter
            String filter = syncRepl.getFilter();

            if ( filter != null )
            {
                filterWidget.setFilter( filter );
            }
            else
            {
                filterWidget.setFilter( "" );
            }

            // Scope
            Scope scope = syncRepl.getScope();

            if ( scope != null )
            {
                scopeComboViewer.setSelection( new StructuredSelection( scope ) );
            }
            else
            {
                scopeComboViewer.setSelection( new StructuredSelection( Scope.SUB ) );
            }

            // Attributes
            String[] attributes = syncRepl.getAttributes();
            this.attributes.clear();

            if ( attributes != null )
            {
                this.attributes.addAll( Arrays.asList( attributes ) );
                attributesTableViewer.refresh();
            }

            // Attributes Only
            attributesOnlyCheckbox.setSelection( syncRepl.isAttrsOnly() );

            addListeners();
        }
    }


    /**
     * Gets the SASL authentication label text.
     *
     * @return the text for the SASL authentication label
     */
    private String getSaslAuthenticationLabelText()
    {
        // SASL Mechanism
        String saslMechanismString = syncRepl.getSaslMech();
        String saslMechanismTitle = "(none)";
        try
        {
            SaslMechanism saslMechanism = SaslMechanism.parse( saslMechanismString );
            saslMechanismTitle = saslMechanism.getTitle();
        }
        catch ( ParseException e )
        {
            // Silent
        }

        // Authentication ID
        String authenticationId = syncRepl.getAuthcid();

        if ( ( authenticationId != null ) && ( !"".equals( authenticationId ) ) )
        {
            return NLS.bind( "Authentication ID is ''{0}'', with ''{1}'' SASL mechanism.", authenticationId,
                saslMechanismTitle );
        }

        // Authorization ID
        String authorizationId = syncRepl.getAuthzid();

        if ( ( authorizationId != null ) && ( !"".equals( authorizationId ) ) )
        {
            return NLS.bind( "Authorization ID is ''{0}'', with ''{1}'' SASL mechanism.", authorizationId,
                saslMechanismTitle );
        }

        return "SASL Authentication isn't configured.";
    }


    /**
     * Gets the replication type.
     *
     * @return the replication type
     */
    private Type getReplicationType()
    {
        StructuredSelection selection = ( StructuredSelection ) replicationTypeComboViewer.getSelection();

        if ( ( selection != null ) && ( !selection.isEmpty() ) )
        {
            return ( Type ) selection.getFirstElement();
        }

        return null;
    }


    /**
     * Gets the provider.
     *
     * @return the provider
     */
    private Provider getProvider()
    {
        Provider provider = new Provider();

        // Host
        String host = hostText.getText();

        if ( ( host != null ) && ( !"".equals( host ) ) )
        {
            provider.setHost( host );
        }
        else
        {
            provider.setHost( null );
        }

        // Port
        String portString = portText.getText();

        if ( ( host != null ) && ( !"".equals( host ) ) )
        {
            try
            {
                provider.setPort( Integer.parseInt( portString ) );
            }
            catch ( NumberFormatException e )
            {
                // Silent
                provider.setPort( Provider.NO_PORT );
            }
        }
        else
        {
            provider.setPort( Provider.NO_PORT );
        }

        // Encryption Type
        provider.setLdaps( EncryptionMethod.SSL_ENCRYPTION_LDAPS == getEncryptionMethod() );

        return provider;
    }


    /**
     * Gets the encryption method.
     *
     * @return the encryption method
     */
    private EncryptionMethod getEncryptionMethod()
    {
        StructuredSelection selection = ( StructuredSelection ) encryptionMethodComboViewer.getSelection();

        if ( ( selection != null ) && ( !selection.isEmpty() ) )
        {
            return ( EncryptionMethod ) selection.getFirstElement();
        }

        return null;
    }


    /**
     * Gets the scope.
     *
     * @return the scope
     */
    private Scope getScope()
    {
        StructuredSelection selection = ( StructuredSelection ) scopeComboViewer.getSelection();

        if ( ( selection != null ) && ( !selection.isEmpty() ) )
        {
            return ( Scope ) selection.getFirstElement();
        }

        return null;
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        replicaIdText.addModifyListener( replicatIdTextListener );
        replicaIdText.addVerifyListener( integerVerifyListener );
        replicationTypeComboViewer.addSelectionChangedListener( replicationTypeComboViewerListener );
        configureReplicationButton.addSelectionListener( configureReplicationButtonListener );
        hostText.addModifyListener( hostTextListener );
        portText.addModifyListener( portTextListener );
        portText.addVerifyListener( integerVerifyListener );
        encryptionMethodComboViewer.addSelectionChangedListener( encryptionMethodComboViewerListener );
        configureStartTlsButton.addSelectionListener( configureStartTlsButtonListener );
        authenticationTabFolder.addSelectionListener( authenticationTabFolderListener );
        bindDnText.addModifyListener( bindDnTextListener );
        credentialsText.addModifyListener( credentialsTextListener );
        showCredentialsCheckbox.addSelectionListener( showCredentialsCheckboxListener );
        configureSaslAuthenticationButton.addSelectionListener( configureSaslAuthenticationButtonListener );
        searchBaseDnEntryWidget.addWidgetModifyListener( searchBaseDnEntryWidgetListener );
        filterWidget.addWidgetModifyListener( filterWidgetListener );
        scopeComboViewer.addSelectionChangedListener( scopeComboViewerListener );
        attributesTableViewer.addSelectionChangedListener( attributesTableViewerSelectionChangedListener );
        attributesTableViewer.addDoubleClickListener( attributesTableViewerDoubleClickListener );
        addAttributeButton.addSelectionListener( addAttributeButtonListener );
        editAttributeButton.addSelectionListener( editAttributeButtonListener );
        deleteAttributeButton.addSelectionListener( deleteAttributeButtonListener );
        attributesOnlyCheckbox.addSelectionListener( attributesOnlyCheckboxListener );
    }


    /**
     * Removes listeners.
     */
    private void removeListeners()
    {
        replicaIdText.removeModifyListener( replicatIdTextListener );
        replicaIdText.removeVerifyListener( integerVerifyListener );
        replicationTypeComboViewer.removeSelectionChangedListener( replicationTypeComboViewerListener );
        configureReplicationButton.removeSelectionListener( configureReplicationButtonListener );
        hostText.removeModifyListener( hostTextListener );
        portText.removeModifyListener( portTextListener );
        portText.removeVerifyListener( integerVerifyListener );
        encryptionMethodComboViewer.removeSelectionChangedListener( encryptionMethodComboViewerListener );
        configureStartTlsButton.removeSelectionListener( configureStartTlsButtonListener );
        authenticationTabFolder.removeSelectionListener( authenticationTabFolderListener );
        bindDnText.removeModifyListener( bindDnTextListener );
        credentialsText.removeModifyListener( credentialsTextListener );
        showCredentialsCheckbox.removeSelectionListener( showCredentialsCheckboxListener );
        configureSaslAuthenticationButton.removeSelectionListener( configureSaslAuthenticationButtonListener );
        searchBaseDnEntryWidget.removeWidgetModifyListener( searchBaseDnEntryWidgetListener );
        filterWidget.removeWidgetModifyListener( filterWidgetListener );
        scopeComboViewer.removeSelectionChangedListener( scopeComboViewerListener );
        attributesTableViewer.removeSelectionChangedListener( attributesTableViewerSelectionChangedListener );
        attributesTableViewer.removeDoubleClickListener( attributesTableViewerDoubleClickListener );
        addAttributeButton.removeSelectionListener( addAttributeButtonListener );
        editAttributeButton.removeSelectionListener( editAttributeButtonListener );
        deleteAttributeButton.removeSelectionListener( deleteAttributeButtonListener );
        attributesOnlyCheckbox.removeSelectionListener( attributesOnlyCheckboxListener );
    }


    /**
     * Updates the OK button 'enable' state.
     */
    private void updateOkButtonEnableState()
    {
        // Replica ID
        String replicaId = replicaIdText.getText();

        if ( ( replicaId == null ) || "".equals( replicaId ) )
        {
            okButton.setEnabled( false );
            return;
        }

        // Host
        String host = hostText.getText();

        if ( ( host == null ) || "".equals( host ) )
        {
            okButton.setEnabled( false );
            return;
        }

        // Search Base DN
        Dn searchBaseDn = searchBaseDnEntryWidget.getDn();

        if ( ( searchBaseDn == null ) || Dn.EMPTY_DN.equals( searchBaseDn ) )
        {
            // TODO add another check
            // The Search Base DN must be within the database naming context
            okButton.setEnabled( false );
            return;
        }

        okButton.setEnabled( true );
    }


    /**
     * Gets the SyncRepl value.
     *
     * @return the SyncRepl value
     */
    public SyncRepl getSyncRepl()
    {
        return syncRepl;
    }

    /**
     * Enum used for the Encryption Method selected by the user.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private enum EncryptionMethod
    {
        NO_ENCRYPTION,
        SSL_ENCRYPTION_LDAPS,
        START_TLS_EXTENSION
    }
}
