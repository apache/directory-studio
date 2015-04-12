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
package org.apache.directory.studio.openldap.config.editor.databases;


import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.common.ui.widgets.BooleanWithDefaultWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.EntryWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.PasswordWidget;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.editor.dialogs.ReplicationConsumerDialog;
import org.apache.directory.studio.openldap.config.model.OlcOverlayConfig;
import org.apache.directory.studio.openldap.config.model.database.OlcBdbConfig;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;
import org.apache.directory.studio.openldap.config.model.database.OlcHdbConfig;
import org.apache.directory.studio.openldap.config.model.database.OlcLdifConfig;
import org.apache.directory.studio.openldap.config.model.database.OlcMdbConfig;
import org.apache.directory.studio.openldap.config.model.database.OlcNullConfig;
import org.apache.directory.studio.openldap.config.model.database.OlcRelayConfig;
import org.apache.directory.studio.openldap.syncrepl.Provider;
import org.apache.directory.studio.openldap.syncrepl.SyncRepl;
import org.apache.directory.studio.openldap.syncrepl.SyncReplParser;
import org.apache.directory.studio.openldap.syncrepl.SyncReplParserException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the Details Page of the Server Configuration Editor for the Database type
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabasesDetailsPage implements IDetailsPage
{
    /** The editable database types array */
    private static DatabaseTypeEnum[] EDITABLE_DATABASE_TYPES = new DatabaseTypeEnum[]
        {
            DatabaseTypeEnum.NONE,
            DatabaseTypeEnum.FRONTEND,
            DatabaseTypeEnum.CONFIG,
            DatabaseTypeEnum.BDB,
            DatabaseTypeEnum.DB_PERL,
            DatabaseTypeEnum.DB_SOCKET,
            DatabaseTypeEnum.HDB,
            DatabaseTypeEnum.MDB,
            DatabaseTypeEnum.LDAP,
            DatabaseTypeEnum.LDIF,
            DatabaseTypeEnum.META,
            DatabaseTypeEnum.MONITOR,
            DatabaseTypeEnum.NDB,
            DatabaseTypeEnum.NULL,
            DatabaseTypeEnum.PASSWD,
            DatabaseTypeEnum.RELAY,
            DatabaseTypeEnum.SHELL,
            DatabaseTypeEnum.SQL
    };

    /** The frontend database type array */
    private static DatabaseTypeEnum[] FRONTEND_DATABASE_TYPES = new DatabaseTypeEnum[]
        {
            DatabaseTypeEnum.FRONTEND
    };

    /** The frontend database type array */
    private static DatabaseTypeEnum[] CONFIG_DATABASE_TYPES = new DatabaseTypeEnum[]
        {
            DatabaseTypeEnum.CONFIG
    };

    /** The class instance */
    private DatabasesDetailsPage instance;

    /** The associated Master Details Block */
    private DatabasesMasterDetailsBlock masterDetailsBlock;

    /** The database wrapper */
    private DatabaseWrapper databaseWrapper;

    /** The database specific details block */
    private DatabaseSpecificDetailsBlock databaseSpecificDetailsBlock;

    /** The browser connection */
    private IBrowserConnection browserConnection;

    /** The dirty flag */
    private boolean dirty = false;

    // UI widgets
    private Composite parentComposite;
    private FormToolkit toolkit;
    private ComboViewer databaseTypeComboViewer;
    private EntryWidget suffixEntryWidget;

    /** The olcRootDN attribute */
    private EntryWidget rootDnEntryWidget;

    /** The olcRootPW attribute */
    private PasswordWidget rootPasswordWidget;

    /** The olcReadOnly attribute */
    private BooleanWithDefaultWidget readOnlyBooleanWithDefaultWidget;

    /** The olcHidden attribute */
    private BooleanWithDefaultWidget hiddenBooleanWithDefaultWidget;

    /** The associated overlays */
    private TableViewer overlaysTableViewer;
    private Button addOverlayButton;
    private Button editOverlayButton;
    private Button deleteOverlayButton;

    private Section specificSettingsSection;
    private Composite specificSettingsSectionComposite;
    private Composite databaseSpecificDetailsComposite;

    /** The Syncrepl part */
    private TableViewer replicationConsumersTableViewer;
    private Button addReplicationConsumerButton;
    private Button editReplicationConsumerButton;
    private Button deleteReplicationConsumerButton;

    // Listeners
    /**
     * A listener for changes on the Overlay table 
     */
    private ISelectionChangedListener overlaysTableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            updateOverlaysTableButtonsState();
        }
    };

    /**
     * A listener for selections on the Overlay table 
     */
    private IDoubleClickListener overlaysTableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editOverlayButtonAction();
        }
    };

    /**
     * A listener for the Overlay table Add button 
     */
    private SelectionListener addOverlayButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addOverlayButtonAction();
        }
    };

    /**
     * A listener for the Overlay table Edit button 
     */
    private SelectionListener editOverlayButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editOverlayButtonAction();
        }
    };

    /**
     * A listener for the Overlay table Delete button 
     */
    private SelectionListener deleteOverlayButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteOverlayButtonAction();
        }
    };

    /**
     * A listener for changes on the Replication Consumers table 
     */
    private ISelectionChangedListener replicationConsumersTableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            updateReplicationConsumersTableButtonsState();
        }
    };

    /**
     * A listener for selections on the Replication Consumers table 
     */
    private IDoubleClickListener replicationConsumersTableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editReplicationConsumerButtonAction();
        }
    };

    /**
     * A listener for the Replication Consumers table Add button 
     */
    private SelectionListener addReplicationConsumerButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addReplicationConsumerButtonAction();
        }
    };

    /**
     * A listener for the Replication Consumers table Edit button 
     */
    private SelectionListener editReplicationConsumerButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editReplicationConsumerButtonAction();
        }
    };

    /**
     * A listener for the Replication Consumers table Delete button 
     */
    private SelectionListener deleteReplicationConsumerButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteReplicationConsumerButtonAction();
        }
    };

    /**
     * The listener that manage the specific database parameters
     */
    private ISelectionChangedListener databaseTypeComboViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            DatabaseTypeEnum type = ( DatabaseTypeEnum ) ( ( StructuredSelection ) databaseTypeComboViewer
                .getSelection() )
                .getFirstElement();

            if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
            {
                OlcDatabaseConfig database = databaseWrapper.getDatabase();

                switch ( type )
                {
                    case BDB:
                        OlcBdbConfig newBdbDatabase = new OlcBdbConfig();
                        copyDatabaseProperties( database, newBdbDatabase );
                        databaseWrapper.setDatabase( newBdbDatabase );
                        databaseSpecificDetailsBlock = new BerkeleyDbDatabaseSpecificDetailsBlock<OlcBdbConfig>(
                            instance, newBdbDatabase, browserConnection );
                        break;
                    case HDB:
                        OlcHdbConfig newHdbDatabase = new OlcHdbConfig();
                        copyDatabaseProperties( database, newHdbDatabase );
                        databaseWrapper.setDatabase( newHdbDatabase );
                        databaseSpecificDetailsBlock = new BerkeleyDbDatabaseSpecificDetailsBlock<OlcHdbConfig>(
                            instance, newHdbDatabase, browserConnection );
                        break;

                    case MDB:
                        // The MDB database
                        OlcMdbConfig newMdbDatabase = new OlcMdbConfig();
                        copyDatabaseProperties( database, newMdbDatabase );
                        databaseWrapper.setDatabase( newMdbDatabase );
                        databaseSpecificDetailsBlock = new MdbDatabaseSpecificDetailsBlock( instance, newMdbDatabase,
                            browserConnection );
                        break;

                    case LDIF:
                        OlcLdifConfig newLdifDatabase = new OlcLdifConfig();
                        copyDatabaseProperties( database, newLdifDatabase );
                        databaseWrapper.setDatabase( newLdifDatabase );
                        databaseSpecificDetailsBlock = new LdifDatabaseSpecificDetailsBlock( instance,
                            newLdifDatabase );
                        break;
                    //                    case LDAP:
                    //                        OlcLDAPConfig newLdapDatabase = new OlcLDAPConfig();
                    //                        copyDatabaseProperties( database, newLdapDatabase );
                    //                        databaseWrapper.setDatabase( newLdapDatabase );
                    //                        // databaseSpecificDetailsBlock = new LdapDatabaseSpecificDetailsBlock( newLdapDatabase ); // TODO
                    //                        break;
                    case NULL:
                        OlcNullConfig newNullDatabase = new OlcNullConfig();
                        copyDatabaseProperties( database, newNullDatabase );
                        databaseWrapper.setDatabase( newNullDatabase );
                        databaseSpecificDetailsBlock = new NullDatabaseSpecificDetailsBlock( instance,
                            newNullDatabase );
                        break;
                    case RELAY:
                        OlcRelayConfig newRelayDatabase = new OlcRelayConfig();
                        copyDatabaseProperties( database, newRelayDatabase );
                        databaseWrapper.setDatabase( newRelayDatabase );
                        databaseSpecificDetailsBlock = new RelayDatabaseSpecificDetailsBlock( instance,
                            newRelayDatabase, browserConnection );
                        break;
                    case NONE:
                        OlcDatabaseConfig newNoneDatabase = new OlcDatabaseConfig();
                        copyDatabaseProperties( database, newNoneDatabase );
                        databaseWrapper.setDatabase( newNoneDatabase );
                        databaseSpecificDetailsBlock = null;
                        break;
                    default:
                        break;
                }

                updateDatabaseSpecificSettingsSection();

                setEditorDirty();
            }
        }
    };

    /** 
     * The modify listener which set the editor dirty 
     **/
    private WidgetModifyListener dirtyWidgetModifyListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            setEditorDirty();
        }
    };


    /**
     * Creates a new instance of PartitionDetailsPage.
     *
     * @param pmdb
     *      the associated Master Details Block
     */
    public DatabasesDetailsPage( DatabasesMasterDetailsBlock pmdb )
    {
        instance = this;
        masterDetailsBlock = pmdb;

        // Getting the browser connection associated with the connection in the configuration
        browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( masterDetailsBlock.getPage().getConfiguration().getConnection() );
    }


    /**
     * {@inheritDoc}
     */
    public void createContents( Composite parent )
    {
        parentComposite = parent;
        parent.setLayout( new GridLayout() );

        createGeneralSettingsSection( parent, toolkit );
        createOverlaySettingsSection( parent, toolkit );
        createDatabaseSpecificSettingsSection( parent, toolkit );
        createReplicationConsumersSettingsSection( parent, toolkit );
    }


    /**
     * Creates the General Settings Section. This will expose the following attributes :
     * <ul>
     * <li></li>
     * </ul>
     *
     * @param parent the parent composite
     * @param toolkit the toolkit to use
     */
    private void createGeneralSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "Database General Settings" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gl = new GridLayout( 2, false );
        gl.marginRight = 18;
        composite.setLayout( gl );
        section.setClient( composite );

        // Database Type
        toolkit.createLabel( composite, "Database Type:" );
        Combo databaseTypeCombo = new Combo( composite, SWT.READ_ONLY | SWT.SINGLE );
        databaseTypeCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        databaseTypeComboViewer = new ComboViewer( databaseTypeCombo );
        databaseTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        databaseTypeComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof DatabaseTypeEnum )
                {
                    DatabaseTypeEnum databaseType = ( DatabaseTypeEnum ) element;

                    return databaseType.getName();
                }

                return super.getText( element );
            }
        } );

        // Suffix DN
        toolkit.createLabel( composite, "Suffix:" );
        suffixEntryWidget = new EntryWidget( browserConnection, null, true );
        suffixEntryWidget.createWidget( composite, toolkit );
        suffixEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ControlDecoration suffixTextDecoration = new ControlDecoration( suffixEntryWidget.getControl(), SWT.CENTER
            | SWT.RIGHT );
        suffixTextDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        suffixTextDecoration.setMarginWidth( 4 );
        suffixTextDecoration
            .setDescriptionText( "The DN suffix of queries that will be passed to this backend database." );

        // Root DN
        toolkit.createLabel( composite, "Root DN:" );
        rootDnEntryWidget = new EntryWidget( browserConnection, null, true );
        rootDnEntryWidget.createWidget( composite, toolkit );
        rootDnEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ControlDecoration rootDnTextDecoration = new ControlDecoration( rootDnEntryWidget.getControl(), SWT.CENTER
            | SWT.RIGHT );
        rootDnTextDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        rootDnTextDecoration.setMarginWidth( 4 );
        rootDnTextDecoration
            .setDescriptionText( "The DN that is not subject to access control or administrative limit restrictions for operations on this database." );

        // Root Password
        Label rootPasswordLabel = toolkit.createLabel( composite, "Root Password:" );
        rootPasswordLabel.setLayoutData( new GridData( SWT.NONE, SWT.TOP, false, false ) );
        rootPasswordWidget = new PasswordWidget( true );
        rootPasswordWidget.createWidget( composite, toolkit );
        rootPasswordWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ControlDecoration rootPasswordTextDecoration = new ControlDecoration( rootPasswordWidget.getControl(),
            SWT.TOP | SWT.RIGHT );
        rootPasswordTextDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        rootPasswordTextDecoration.setMarginWidth( 4 );
        rootPasswordTextDecoration
            .setDescriptionText( "The password for the the Root DN." );

        // Read Only
        toolkit.createLabel( composite, "Read Only:" );
        readOnlyBooleanWithDefaultWidget = new BooleanWithDefaultWidget();
        readOnlyBooleanWithDefaultWidget.create( composite, toolkit );
        readOnlyBooleanWithDefaultWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ControlDecoration readOnlyCheckboxDecoration = new ControlDecoration(
            readOnlyBooleanWithDefaultWidget.getControl(), SWT.CENTER | SWT.RIGHT );
        readOnlyCheckboxDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        readOnlyCheckboxDecoration.setMarginWidth( 4 );
        readOnlyCheckboxDecoration
            .setDescriptionText( "Sets the database into \"read-only\" mode. Any attempts to modify the database will return an \"unwilling to perform\" error." );

        // Hidden
        toolkit.createLabel( composite, "Hidden:" );
        hiddenBooleanWithDefaultWidget = new BooleanWithDefaultWidget( false );
        hiddenBooleanWithDefaultWidget.create( composite, toolkit );
        hiddenBooleanWithDefaultWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ControlDecoration hiddenCheckboxDecoration = new ControlDecoration(
            hiddenBooleanWithDefaultWidget.getControl(), SWT.CENTER | SWT.RIGHT );
        hiddenCheckboxDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        hiddenCheckboxDecoration.setMarginWidth( 4 );
        hiddenCheckboxDecoration
            .setDescriptionText( "Sets whether the database will be used to answer queries." );
    }


    /**
     * Creates the Overlay Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createOverlaySettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR );
        section.setText( "Overlays Settings" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        composite.setLayout( new GridLayout( 2, false ) );
        section.setClient( composite );

        // Creating the Table and Table Viewer
        Table table = toolkit.createTable( composite, SWT.NULL );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData( gd );

        overlaysTableViewer = new TableViewer( table );
        overlaysTableViewer.setContentProvider( new ArrayContentProvider() );
        overlaysTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof OlcOverlayConfig )
                {
                    return getOverlayText( ( OlcOverlayConfig ) element );
                }

                return super.getText( element );
            };
        } );

        // Creating the buttons
        addOverlayButton = toolkit.createButton( composite, "Add...", SWT.PUSH );
        addOverlayButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        editOverlayButton = toolkit.createButton( composite, "Edit...", SWT.PUSH );
        editOverlayButton.setEnabled( false );
        editOverlayButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        deleteOverlayButton = toolkit.createButton( composite, "Delete...", SWT.PUSH );
        deleteOverlayButton.setEnabled( false );
        deleteOverlayButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
    }


    /**
     * Gets the overlay text.
     *
     * @param overlay the overlay
     * @return the text corresponding to the overlay
     */
    private String getOverlayText( OlcOverlayConfig overlay )
    {
        return OverlayDialog.getOverlayDisplayName( overlay );
    }


    /**
     * Creates the Database Specific Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createDatabaseSpecificSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creating the Section
        specificSettingsSection = toolkit.createSection( parent, Section.TWISTIE | Section.EXPANDED
            | Section.TITLE_BAR );
        specificSettingsSection.setText( "Database Specific Settings" );
        specificSettingsSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Creating the Composite
        specificSettingsSectionComposite = toolkit.createComposite( specificSettingsSection );
        toolkit.paintBordersFor( specificSettingsSectionComposite );
        GridLayout gd = new GridLayout();
        gd.marginHeight = gd.marginWidth = 0;
        gd.verticalSpacing = gd.horizontalSpacing = 0;
        specificSettingsSectionComposite.setLayout( gd );
        specificSettingsSection.setClient( specificSettingsSectionComposite );
    }


    /**
     * Creates the Replication Consumers Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createReplicationConsumersSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR );
        section.setText( "Replication Consumers Settings" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        composite.setLayout( new GridLayout( 2, false ) );
        section.setClient( composite );

        // Creating the Table and Table Viewer
        Table table = toolkit.createTable( composite, SWT.NULL );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData( gd );

        replicationConsumersTableViewer = new TableViewer( table );
        replicationConsumersTableViewer.setContentProvider( new ArrayContentProvider() );
        replicationConsumersTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof String )
                {
                    return getReplicationConsumerText( ( String ) element );
                }

                return super.getText( element );
            };
        } );

        // Creating the buttons
        addReplicationConsumerButton = toolkit.createButton( composite, "Add...", SWT.PUSH );
        addReplicationConsumerButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        editReplicationConsumerButton = toolkit.createButton( composite, "Edit...", SWT.PUSH );
        editReplicationConsumerButton.setEnabled( false );
        editReplicationConsumerButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        deleteReplicationConsumerButton = toolkit.createButton( composite, "Delete...", SWT.PUSH );
        deleteReplicationConsumerButton.setEnabled( false );
        deleteReplicationConsumerButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
    }


    /**
     * Gets the replication consumer text.
     *
     * @param syncReplValue the replication consumer value
     * @return the text corresponding to the replication consumer
     */
    private String getReplicationConsumerText( String syncReplValue )
    {
        if ( syncReplValue != null )
        {
            try
            {
                // Parsing the SyncRepl value
                SyncReplParser parser = new SyncReplParser();
                SyncRepl syncRepl = parser.parse( syncReplValue );

                // Creating a string builder to hold the SyncRepl description
                StringBuilder sb = new StringBuilder();

                // Replica ID
                sb.append( "rid=" + syncRepl.getRid() );

                // Provider
                Provider provider = syncRepl.getProvider();

                if ( provider != null )
                {
                    sb.append( " provider=" + provider.toString() );
                }

                // Search Base
                String searchBase = syncRepl.getSearchBase();

                if ( ( searchBase != null ) && ( !"".equals( searchBase ) ) )
                {
                    sb.append( " searchBase=\"" + searchBase + "\"" );
                }

                sb.append( " (and more options)" );

                return sb.toString();
            }
            catch ( SyncReplParserException e )
            {
                // Silent
            }
        }

        return syncReplValue;
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;

        if ( ssel.size() == 1 )
        {
            databaseWrapper = ( DatabaseWrapper ) ssel.getFirstElement();
        }
        else
        {
            databaseWrapper = null;
        }

        refresh();
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
        {
            OlcDatabaseConfig database = databaseWrapper.getDatabase();

            // Suffix
            database.clearOlcSuffix();
            Dn suffixDn = suffixEntryWidget.getDn();

            if ( suffixDn != null )
            {
                database.addOlcSuffix( suffixDn );
            }

            // Root DN
            database.setOlcRootDN( rootDnEntryWidget.getDn() );

            // Root Password
            String rootPassword = rootPasswordWidget.getPasswordAsString();

            if ( Strings.isNotEmpty( rootPassword ) )
            {
                database.setOlcRootPW( rootPassword );
            }

            // Read Only
            database.setOlcReadOnly( readOnlyBooleanWithDefaultWidget.getValue() );

            // Hidden
            database.setOlcHidden( hiddenBooleanWithDefaultWidget.getValue() );

            // Database specific details block
            if ( databaseSpecificDetailsBlock != null )
            {
                databaseSpecificDetailsBlock.commit( onSave );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void initialize( IManagedForm form )
    {
        toolkit = form.getToolkit();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return dirty;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isStale()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        //        suffixText.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
        {
            OlcDatabaseConfig database = databaseWrapper.getDatabase();

            // Enabling or disabling UI elements depending on the database, dealing with
            // the two specific Database (Frontend and Config)
            if ( isFrontendDatabase( database ) )
            {
                databaseTypeComboViewer.getControl().setEnabled( false );
                suffixEntryWidget.setEnabled( false );
                rootDnEntryWidget.setEnabled( false );
                rootPasswordWidget.setEnabled( false );
                readOnlyBooleanWithDefaultWidget.setEnabled( false );
                hiddenBooleanWithDefaultWidget.setEnabled( false );
            }
            else if ( isConfigDatabase( database ) )
            {
                databaseTypeComboViewer.getControl().setEnabled( false );
                suffixEntryWidget.setEnabled( false );
                rootDnEntryWidget.setEnabled( true );
                rootPasswordWidget.setEnabled( true );
                readOnlyBooleanWithDefaultWidget.setEnabled( false );
                hiddenBooleanWithDefaultWidget.setEnabled( false );
            }
            else
            {
                databaseTypeComboViewer.getControl().setEnabled( true );
                suffixEntryWidget.setEnabled( true );
                rootDnEntryWidget.setEnabled( true );
                rootPasswordWidget.setEnabled( true );
                readOnlyBooleanWithDefaultWidget.setEnabled( true );
                hiddenBooleanWithDefaultWidget.setEnabled( true );
            }

            // Suffixes
            List<Dn> suffixesDnList = database.getOlcSuffix();
            Dn suffixDn = null;

            if ( suffixesDnList.size() == 1 )
            {
                suffixDn = suffixesDnList.get( 0 );
            }

            suffixEntryWidget.setInput( suffixDn );

            // Root DN
            Dn rootDn = database.getOlcRootDN();
            rootDnEntryWidget.setInput( rootDn );

            // Root PW
            String rootPassword = database.getOlcRootPW();
            rootPasswordWidget.setPassword( ( rootPassword == null ) ? null : rootPassword.getBytes() );
            //            rootPasswordText.setText( ( rootPassword == null ) ? "" : rootPassword ); //$NON-NLS-1$

            // Read Only
            readOnlyBooleanWithDefaultWidget.setValue( database.getOlcReadOnly() );

            // Hidden
            hiddenBooleanWithDefaultWidget.setValue( database.getOlcHidden() );

            // Overlays
            refreshOverlaysTableViewer();

            // Replication Consumers
            refreshReplicationConsumersTableViewer();

            //
            // Specific Settings
            //
            // OlcHdbConfig Type
            if ( database instanceof OlcHdbConfig )
            {
                databaseTypeComboViewer.setInput( EDITABLE_DATABASE_TYPES );
                databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.HDB ) );
                databaseSpecificDetailsBlock = new BerkeleyDbDatabaseSpecificDetailsBlock<OlcHdbConfig>( instance,
                    ( OlcHdbConfig ) database, browserConnection );
            }
            // OlcBdbConfig Type
            else if ( database instanceof OlcBdbConfig )
            {
                databaseTypeComboViewer.setInput( EDITABLE_DATABASE_TYPES );
                databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.BDB ) );
                databaseSpecificDetailsBlock = new BerkeleyDbDatabaseSpecificDetailsBlock<OlcBdbConfig>( instance,
                    ( OlcBdbConfig ) database, browserConnection );
            }
            // OlcMdbConfig Type
            else if ( database instanceof OlcMdbConfig )
            {
                databaseTypeComboViewer.setInput( EDITABLE_DATABASE_TYPES );
                databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.MDB ) );
                databaseSpecificDetailsBlock = new MdbDatabaseSpecificDetailsBlock( instance,
                    ( OlcMdbConfig ) database, browserConnection );
            }
            // OlcLdifConfig Type
            else if ( database instanceof OlcLdifConfig )
            {
                databaseTypeComboViewer.setInput( EDITABLE_DATABASE_TYPES );
                databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.LDIF ) );
                databaseSpecificDetailsBlock = new LdifDatabaseSpecificDetailsBlock( instance,
                    ( OlcLdifConfig ) database );
            }
            // OlcNullConfig Type
            else if ( database instanceof OlcNullConfig )
            {
                databaseTypeComboViewer.setInput( EDITABLE_DATABASE_TYPES );
                databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.NULL ) );
                databaseSpecificDetailsBlock = new NullDatabaseSpecificDetailsBlock( instance,
                    ( OlcNullConfig ) database );
            }
            // OlcRelayConfig Type
            else if ( database instanceof OlcRelayConfig )
            {
                databaseTypeComboViewer.setInput( EDITABLE_DATABASE_TYPES );
                databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.RELAY ) );
                databaseSpecificDetailsBlock = new RelayDatabaseSpecificDetailsBlock( instance,
                    ( OlcRelayConfig ) database, browserConnection );
            }
            // None of these types
            else
            {
                // Looking for the frontend database
                if ( isFrontendDatabase( database ) )
                {
                    databaseTypeComboViewer.setInput( FRONTEND_DATABASE_TYPES );
                    databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.FRONTEND ) );
                    databaseSpecificDetailsBlock = new FrontendDatabaseSpecificDetailsBlock( instance, database,
                        browserConnection );
                }
                // Looking for the config database
                else if ( isConfigDatabase( database ) )
                {
                    databaseTypeComboViewer.setInput( CONFIG_DATABASE_TYPES );
                    databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.CONFIG ) );
                    databaseSpecificDetailsBlock = null;
                }
                // Any other type of database
                else
                {
                    databaseTypeComboViewer.setInput( EDITABLE_DATABASE_TYPES );
                    databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.NONE ) );
                    databaseSpecificDetailsBlock = null;
                }
            }

            updateDatabaseSpecificSettingsSection();
        }

        addListeners();
    }


    /**
     * Refreshes overlays table viewer.
     */
    private void refreshOverlaysTableViewer()
    {
        if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
        {
            overlaysTableViewer.setInput( databaseWrapper.getDatabase().getOverlays().toArray() );
        }
        else
        {
            overlaysTableViewer.setInput( new Object[0] );
        }

        updateOverlaysTableButtonsState();
    }


    /**
     * Updates the state of the overlays table buttons.
     */
    private void updateOverlaysTableButtonsState()
    {
        StructuredSelection selection = ( StructuredSelection ) overlaysTableViewer.getSelection();

        editOverlayButton.setEnabled( !selection.isEmpty() );
        deleteOverlayButton.setEnabled( !selection.isEmpty() );
    }


    /**
     * Refreshes replication consumers table viewer.
     */
    private void refreshReplicationConsumersTableViewer()
    {
        if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
        {
            replicationConsumersTableViewer.setInput( databaseWrapper.getDatabase().getOlcSyncrepl() );
        }
        else
        {
            replicationConsumersTableViewer.setInput( new Object[0] );
        }

        updateReplicationConsumersTableButtonsState();
    }


    /**
     * Updates the state of the replication consumers table buttons.
     */
    private void updateReplicationConsumersTableButtonsState()
    {
        StructuredSelection selection = ( StructuredSelection ) replicationConsumersTableViewer.getSelection();

        editReplicationConsumerButton.setEnabled( !selection.isEmpty() );
        deleteReplicationConsumerButton.setEnabled( !selection.isEmpty() );
    }


    /**
     * Adds listeners to UI widgets.
     */
    private void addListeners()
    {
        addModifyListener( suffixEntryWidget, dirtyWidgetModifyListener );
        addModifyListener( rootDnEntryWidget, dirtyWidgetModifyListener );
        addModifyListener( rootPasswordWidget, dirtyWidgetModifyListener );
        addModifyListener( readOnlyBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        addModifyListener( hiddenBooleanWithDefaultWidget, dirtyWidgetModifyListener );

        addSelectionChangedListener( databaseTypeComboViewer, databaseTypeComboViewerSelectionChangedListener );

        addDoubleClickListener( overlaysTableViewer, overlaysTableViewerDoubleClickListener );
        addSelectionChangedListener( overlaysTableViewer, overlaysTableViewerSelectionChangedListener );
        addSelectionListener( addOverlayButton, addOverlayButtonListener );
        addSelectionListener( editOverlayButton, editOverlayButtonListener );
        addSelectionListener( deleteOverlayButton, deleteOverlayButtonListener );

        addDoubleClickListener( replicationConsumersTableViewer, replicationConsumersTableViewerDoubleClickListener );
        addSelectionChangedListener( replicationConsumersTableViewer,
            replicationConsumersTableViewerSelectionChangedListener );
        addSelectionListener( addReplicationConsumerButton, addReplicationConsumerButtonListener );
        addSelectionListener( editReplicationConsumerButton, editReplicationConsumerButtonListener );
        addSelectionListener( deleteReplicationConsumerButton, deleteReplicationConsumerButtonListener );
    }


    /**
     * Removes listeners from UI widgets.
     */
    private void removeListeners()
    {
        removeModifyListener( suffixEntryWidget, dirtyWidgetModifyListener );
        removeModifyListener( rootDnEntryWidget, dirtyWidgetModifyListener );
        removeModifyListener( rootPasswordWidget, dirtyWidgetModifyListener );
        removeModifyListener( readOnlyBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        removeModifyListener( hiddenBooleanWithDefaultWidget, dirtyWidgetModifyListener );

        removeSelectionChangedListener( databaseTypeComboViewer, databaseTypeComboViewerSelectionChangedListener );

        removeDoubleClickListener( overlaysTableViewer, overlaysTableViewerDoubleClickListener );
        removeSelectionChangedListener( overlaysTableViewer, overlaysTableViewerSelectionChangedListener );
        removeSelectionListener( addOverlayButton, addOverlayButtonListener );
        removeSelectionListener( editOverlayButton, editOverlayButtonListener );
        removeSelectionListener( deleteOverlayButton, deleteOverlayButtonListener );

        removeDoubleClickListener( replicationConsumersTableViewer, replicationConsumersTableViewerDoubleClickListener );
        removeSelectionChangedListener( replicationConsumersTableViewer,
            replicationConsumersTableViewerSelectionChangedListener );
        removeSelectionListener( addReplicationConsumerButton, addReplicationConsumerButtonListener );
        removeSelectionListener( editReplicationConsumerButton, editReplicationConsumerButtonListener );
        removeSelectionListener( deleteReplicationConsumerButton, deleteReplicationConsumerButtonListener );
    }


    /**
     * Disposes the inner specific settings composite.
     */
    private void disposeSpecificSettingsComposite()
    {
        if ( ( databaseSpecificDetailsComposite != null ) && !( databaseSpecificDetailsComposite.isDisposed() ) )
        {
            databaseSpecificDetailsComposite.dispose();
        }

        databaseSpecificDetailsComposite = null;
    }


    /**
     * Updates the database specific settings section.
     */
    private void updateDatabaseSpecificSettingsSection()
    {
        // Disposing existing specific settings composite
        disposeSpecificSettingsComposite();

        // Create the specific settings block content
        if ( databaseSpecificDetailsBlock != null )
        {
            databaseSpecificDetailsComposite = databaseSpecificDetailsBlock.createBlockContent(
                specificSettingsSectionComposite,
                toolkit );
            databaseSpecificDetailsBlock.refresh();
        }

        parentComposite.layout( true, true );

        // Making the section visible or not
        specificSettingsSection.setVisible( databaseSpecificDetailsBlock != null );
    }


    /**
     * Action launched when the add overlay button is clicked.
     */
    private void addOverlayButtonAction()
    {
        OverlayDialog dialog = new OverlayDialog( addOverlayButton.getShell(), true );
        dialog.setBrowserConnection( browserConnection );

        if ( dialog.open() == OverlayDialog.OK )
        {
            OlcOverlayConfig overlay = dialog.getOverlay();

            if ( overlay != null )
            {
                // Updating the 'overlay' value with the ordering prefix
                overlay.setOlcOverlay( "{" + getNewOverlayOrderingValue() + "}" + overlay.getOlcOverlay() );

                if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
                {
                    databaseWrapper.getDatabase().addOverlay( overlay );
                    refreshOverlaysTableViewer();
                    setEditorDirty();
                }
            }
        }
    }


    /**
     * Gets the new overlay ordering value.
     *
     * @return the new overlay ordering value
     */
    private int getNewOverlayOrderingValue()
    {
        return getMaxOverlayOrderingValue() + 1;
    }


    /**
     * Gets the maximum ordering value.
     *
     * @return the maximum ordering value
     */
    private int getMaxOverlayOrderingValue()
    {
        int maxOrderingValue = -1;

        if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
        {
            for ( OlcOverlayConfig overlay : databaseWrapper.getDatabase().getOverlays() )
            {
                if ( OpenLdapConfigurationPluginUtils
                    .hasOrderingPrefix( overlay.getOlcOverlay() ) )
                {
                    int overlayOrderingValue = OpenLdapConfigurationPluginUtils.getOrderingPrefix( overlay
                        .getOlcOverlay() );

                    if ( overlayOrderingValue > maxOrderingValue )
                    {
                        maxOrderingValue = overlayOrderingValue;
                    }
                }
            }
        }

        return maxOrderingValue;
    }


    /**
     * Action launched when the edit overlay button is clicked, or
     * when the overlays table viewer is double-clicked.
     */
    private void editOverlayButtonAction()
    {
        StructuredSelection selection = ( StructuredSelection ) overlaysTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            OlcOverlayConfig selectedOverlay = ( OlcOverlayConfig ) selection.getFirstElement();

            OverlayDialog dialog = new OverlayDialog( addOverlayButton.getShell() );
            dialog.setBrowserConnection( browserConnection );
            dialog.setOverlay( selectedOverlay );

            if ( dialog.open() == OverlayDialog.OK )
            {
                refreshOverlaysTableViewer();
                setEditorDirty();
            }
        }
    }


    /**
     * Action launched when the delete overlay button is clicked.
     */
    private void deleteOverlayButtonAction()
    {
        StructuredSelection selection = ( StructuredSelection ) overlaysTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            OlcOverlayConfig overlay = ( OlcOverlayConfig ) selection.getFirstElement();

            if ( MessageDialog.openConfirm( overlaysTableViewer.getControl().getShell(), "Delete Overlay?",
                NLS.bind( "Are you sure you want to delete the ''{0}'' overlay?", getOverlayText( overlay ) ) ) )
            {
                if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
                {
                    databaseWrapper.getDatabase().removeOverlay( overlay );
                    refreshOverlaysTableViewer();
                    setEditorDirty();
                }
            }
        }
    }


    /**
     * Indicates if the given database is the frontend one.
     *
     * @param database the database
     * @return <code>true</code> if the given database if the frontend one,
     *         <code>false</code> if not.
     */
    private boolean isFrontendDatabase( OlcDatabaseConfig database )
    {
        if ( database != null )
        {
            int orderingPrefix = OpenLdapConfigurationPluginUtils.getOrderingPrefix( database.getOlcDatabase() );
            String databaseName = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( database.getOlcDatabase() );

            return ( ( orderingPrefix == -1 ) && "frontend".equalsIgnoreCase( databaseName ) );

        }

        return false;
    }


    /**
     * Indicates if the given database is the config one.
     *
     * @param database the database
     * @return <code>true</code> if the given database if the config one,
     *         <code>false</code> if not.
     */
    private boolean isConfigDatabase( OlcDatabaseConfig database )
    {
        if ( database != null )
        {
            int orderingPrefix = OpenLdapConfigurationPluginUtils.getOrderingPrefix( database.getOlcDatabase() );
            String databaseName = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( database.getOlcDatabase() );

            return ( ( orderingPrefix == 0 ) && "config".equalsIgnoreCase( databaseName ) );

        }

        return false;
    }


    /**
     * Action launched when the add replication consumer button is clicked.
     */
    private void addReplicationConsumerButtonAction()
    {
        ReplicationConsumerDialog dialog = new ReplicationConsumerDialog( addReplicationConsumerButton.getShell(),
            browserConnection );

        if ( dialog.open() == OverlayDialog.OK )
        {
            SyncRepl syncRepl = dialog.getSyncRepl();

            if ( syncRepl != null )
            {
                if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
                {
                    String newSyncReplValue = dialog.getSyncRepl().toString();

                    databaseWrapper.getDatabase().addOlcSyncrepl( newSyncReplValue );
                    refreshReplicationConsumersTableViewer();
                    
                    replicationConsumersTableViewer.setSelection( new StructuredSelection( newSyncReplValue ) );
                    setEditorDirty();
                }
            }
        }
    }


    /**
     * Action launched when the edit replication consumer button is clicked, or
     * when the replication consumers table viewer is double-clicked.
     */
    private void editReplicationConsumerButtonAction()
    {
        StructuredSelection selection = ( StructuredSelection ) replicationConsumersTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            // Getting the raw SyncRepl value
            String syncReplValue = ( String ) selection.getFirstElement();
            String syncReplValueWithoutOrderingPrefix = syncReplValue;

            // Getting the ordering prefix (if it exists)
            int orderingPrefix = OpenLdapConfigurationPluginUtils.getOrderingPrefix( syncReplValue );

            // If an ordering prefix was found, lets remove it before parsing the string
            if ( orderingPrefix > 0 )
            {
                syncReplValueWithoutOrderingPrefix = OpenLdapConfigurationPluginUtils
                    .stripOrderingPrefix( syncReplValue );
            }

            // Parsing the SyncRepl value
            SyncReplParser parser = new SyncReplParser();
            try
            {
                SyncRepl syncRepl = parser.parse( syncReplValueWithoutOrderingPrefix );

                // Opening a replication consumer dialog
                ReplicationConsumerDialog dialog = new ReplicationConsumerDialog(
                    addReplicationConsumerButton.getShell(), syncRepl, browserConnection );

                if ( dialog.open() == OverlayDialog.OK )
                {
                    if ( ( databaseWrapper != null ) && ( databaseWrapper.getDatabase() != null ) )
                    {
                        String newSyncReplValue = dialog.getSyncRepl().toString();

                        // Add back the ordering prefix if it was present
                        if ( orderingPrefix > 0 )
                        {
                            newSyncReplValue = "{" + orderingPrefix + "}" + newSyncReplValue;
                        }

                        OlcDatabaseConfig databaseConfig = databaseWrapper.getDatabase();
                        List<String> newOlcSyncrepls = databaseConfig.getOlcSyncrepl();
                        newOlcSyncrepls.remove( syncReplValue );
                        newOlcSyncrepls.add( newSyncReplValue );
                        databaseConfig.setOlcSyncrepl( newOlcSyncrepls );
                        refreshReplicationConsumersTableViewer();
                        replicationConsumersTableViewer.setSelection( new StructuredSelection( newSyncReplValue ) );
                        setEditorDirty();
                    }
                }
            }
            catch ( SyncReplParserException e )
            {
                CommonUIUtils
                    .openErrorDialog( NLS
                        .bind(
                            "The replication consumer definition could not be read correctly.\nThe following error occured:\n {0}",
                            e ) );
            }
        }
    }


    /**
     * Action launched when the delete replication consumer button is clicked.
     */
    private void deleteReplicationConsumerButtonAction()
    {
        StructuredSelection selection = ( StructuredSelection ) replicationConsumersTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            String syncReplValue = ( String ) selection.getFirstElement();

            if ( MessageDialog.openConfirm( overlaysTableViewer.getControl().getShell(), "Delete Overlay?",
                NLS.bind( "Are you sure you want to delete the ''{0}'' replication consumer ?",
                    getReplicationConsumerText( syncReplValue ) ) ) )
            {

                if ( databaseWrapper != null )
                {
                    OlcDatabaseConfig databaseConfig = databaseWrapper.getDatabase();
                    
                    if( databaseConfig != null )
                    { 
                        List<String> newOlcSynrepls = databaseConfig.getOlcSyncrepl();
                        newOlcSynrepls.remove( syncReplValue );
                        databaseConfig.setOlcSyncrepl( newOlcSynrepls );
                        refreshReplicationConsumersTableViewer();
                        setEditorDirty();
                    }
                }
            }
        }
    }


    /**
     * Adds a modify listener to the given Text.
     *
     * @param text
     *      the Text control
     * @param listener
     *      the listener
     */
    protected void addModifyListener( Text text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.addModifyListener( listener );
        }
    }


    /**
     * Adds a modify listener to the given BrowserWidget.
     *
     * @param widget
     *      the widget
     * @param listener
     *      the listener
     */
    protected void addModifyListener( BrowserWidget widget, WidgetModifyListener listener )
    {
        if ( ( widget != null ) && ( listener != null ) )
        {
            widget.addWidgetModifyListener( listener );
        }
    }


    /**
     * Adds a selection listener to the given Button.
     *
     * @param button
     *      the Button control
     * @param listener
     *      the listener
     */
    protected void addSelectionListener( Button button, SelectionListener listener )
    {
        if ( ( button != null ) && ( !button.isDisposed() ) && ( listener != null ) )
        {
            button.addSelectionListener( listener );
        }
    }


    /**
     * Adds a selection changed listener to the given Viewer.
     *
     * @param viewer
     *      the Viewer control
     * @param listener
     *      the listener
     */
    protected void addSelectionChangedListener( Viewer viewer, ISelectionChangedListener listener )
    {
        if ( ( viewer != null ) && ( !viewer.getControl().isDisposed() ) && ( listener != null ) )
        {
            viewer.addSelectionChangedListener( listener );
        }
    }


    /**
     * Adds a double-click listener to the given Viewer.
     *
     * @param viewer
     *      the Viewer control
     * @param listener
     *      the listener
     */
    protected void addDoubleClickListener( TableViewer viewer, IDoubleClickListener listener )
    {
        if ( ( viewer != null ) && ( !viewer.getControl().isDisposed() ) && ( listener != null ) )
        {
            viewer.addDoubleClickListener( listener );
        }
    }


    /**
     * Removes a modify listener to the given Text.
     *
     * @param text
     *      the Text control
     * @param listener
     *      the listener
     */
    protected void removeModifyListener( Text text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.removeModifyListener( listener );
        }
    }


    /**
     * Adds a modify listener to the given BrowserWidget.
     *
     * @param widget
     *      the widget
     * @param listener
     *      the listener
     */
    protected void removeModifyListener( BrowserWidget widget, WidgetModifyListener listener )
    {
        if ( ( widget != null ) && ( listener != null ) )
        {
            widget.removeWidgetModifyListener( listener );
        }
    }


    /**
     * Removes a selection listener to the given Button.
     *
     * @param button
     *      the Button control
     * @param listener
     *      the listener
     */
    protected void removeSelectionListener( Button button, SelectionListener listener )
    {
        if ( ( button != null ) && ( !button.isDisposed() ) && ( listener != null ) )
        {
            button.removeSelectionListener( listener );
        }
    }


    /**
     * Removes a selection changed listener to the given Button.
     *
     * @param button
     *      the Button control
     * @param listener
     *      the listener
     */
    protected void removeSelectionChangedListener( Viewer viewer, ISelectionChangedListener listener )
    {
        if ( ( viewer != null ) && ( !viewer.getControl().isDisposed() ) && ( listener != null ) )
        {
            viewer.removeSelectionChangedListener( listener );
        }
    }


    /**
     * Removes a selection changed listener to the given Button.
     *
     * @param button
     *      the Button control
     * @param listener
     *      the listener
     */
    protected void removeDoubleClickListener( TableViewer viewer, IDoubleClickListener listener )
    {
        if ( ( viewer != null ) && ( !viewer.getControl().isDisposed() ) && ( listener != null ) )
        {
            viewer.removeDoubleClickListener( listener );
        }
    }


    /**
     * Sets the associated editor dirty.
     */
    public void setEditorDirty()
    {
        masterDetailsBlock.setEditorDirty();
    }


    /**
     * Copies database properties from one instance to the other.
     *
     * @param original the original database
     * @param destination the destination database
     */
    private void copyDatabaseProperties( OlcDatabaseConfig original, OlcDatabaseConfig destination )
    {
        if ( ( original != null ) && ( destination != null ) )
        {
            // Special case for the 'olcDatabase' property
            destination.setOlcDatabase( "{"
                + OpenLdapConfigurationPluginUtils.getOrderingPrefix( original.getOlcDatabase() ) + "}"
                + destination.getOlcDatabaseType() );

            // All other properties
            destination.setOlcAccess( original.getOlcAccess() );
            destination.setOlcAddContentAcl( original.getOlcAddContentAcl() );
            destination.setOlcHidden( original.getOlcHidden() );
            destination.setOlcLastMod( original.getOlcLastMod() );
            destination.setOlcLimits( original.getOlcLimits() );
            destination.setOlcMaxDerefDepth( original.getOlcMaxDerefDepth() );
            destination.setOlcMirrorMode( original.getOlcMirrorMode() );
            destination.setOlcMonitoring( original.getOlcMonitoring() );
            destination.setOlcPlugin( original.getOlcPlugin() );
            destination.setOlcReadOnly( original.getOlcReadOnly() );
            destination.setOlcReplica( original.getOlcReplica() );
            destination.setOlcReplicaArgsFile( original.getOlcReplicaArgsFile() );
            destination.setOlcReplicaPidFile( original.getOlcReplicaPidFile() );
            destination.setOlcReplicationInterval( original.getOlcReplicationInterval() );
            destination.setOlcReplogFile( original.getOlcReplogFile() );
            destination.setOlcRequires( original.getOlcRequires() );
            destination.setOlcRestrict( original.getOlcRestrict() );
            destination.setOlcRootDN( original.getOlcRootDN() );
            destination.setOlcRootPW( original.getOlcRootPW() );
            destination.setOlcSchemaDN( original.getOlcSchemaDN() );
            destination.setOlcSecurity( original.getOlcSecurity() );
            destination.setOlcSizeLimit( original.getOlcSizeLimit() );
            destination.setOlcSubordinate( original.getOlcSubordinate() );
            destination.setOlcSuffix( original.getOlcSuffix() );
            destination.setOlcSyncrepl( original.getOlcSyncrepl() );
            destination.setOlcTimeLimit( original.getOlcTimeLimit() );
            destination.setOlcUpdateDN( original.getOlcUpdateDN() );
            destination.setOlcUpdateRef( original.getOlcUpdateRef() );
            destination.setOverlays( original.getOverlays() );
        }
    }
}
