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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.common.ui.wrappers.StringValueWrapper;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.common.ui.model.DatabaseTypeEnum;
import org.apache.directory.studio.openldap.common.ui.model.RequireConditionEnum;
import org.apache.directory.studio.openldap.common.ui.model.RestrictOperationEnum;
import org.apache.directory.studio.openldap.common.ui.widgets.BooleanWithDefaultWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.EntryWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.PasswordWidget;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.editor.dialogs.ReplicationConsumerDialog;
import org.apache.directory.studio.openldap.config.editor.dialogs.SizeLimitDialog;
import org.apache.directory.studio.openldap.config.editor.wrappers.DatabaseWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.DnDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.DnWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitsDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitsWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.RequireConditionDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.RestrictOperationDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.SsfDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.SsfWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.TimeLimitDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.TimeLimitWrapper;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
 * This class represents the Details Page of the Server Configuration Editor for the Database type. 
 * We have 6 sections to manage :
 * <ul>
 * <li>general :</li>
 *   <ul>
 *     <li>olcSuffix(DN, SV/MV)</li>
 *     <li>olcRootDN(DN, SV)</li>
 *     <li>olcRootPw(String, SV)</li>
 *     <li></li>
 *   </ul>
 * <li>limits :</li>
 *   <ul>
 *     <li>olcSizeLimit(String, SV)</li>
 *     <li>olcTimeLimit(String, MV)</li>
 *     <li>olcLimits(String, MV, Ordered)</li>
 *     <li>olcMaxDerefDepth(Integer, SV)</li>
 *   </ul>
 * <li>security :</li>
 *   <ul>
 *     <li>olcHidden(Boolean)</li>
 *     <li>olcReadOnly(Boolean)</li>
 *     <li>olcRequires(String, MV)</li>
 *     <li>olcRestrict(String, MV)</li>
 *     <li>olcSecurity(String, MV)</li>
 *   </ul>
 * <li>access</li>
 *   <ul>
 *     <li>olcAccess(String, MV, Ordered)</li>
 *     <li>olcAddContentAcl(Boolean)</li>
 *   </ul>
 * <li>replication :</li>
 *   <ul>
 *     <li>olcMirrorMode(Boolean)</li>
 *     <li>olcSyncrepl(String, MV, Ordered)</li>
 *     <li>olcSyncUseSubentry(Boolean)</li>
 *     <li>olcUpdateDN(DN, SV)</li>
 *     <li>olcUpdateRef(String, MV)</li>
 *     <li>olcReplica(String, MV, Ordered)</li>
 *     <li>olcReplicaArgsFile(String, SV)</li>
 *     <li>olcReplicaPidFile(String, SV)</li>
 *     <li>olcReplicationInterval(Integer, SV)</li>
 *     <li>olcReplogFile(String, SV)</li>
 *   </ul>
 * <li>options :</li>
 *   <ul>
 *     <li>olcLastMod(Boolean)</li>
 *     <li>olcMonitoring(Boolean)</li>
 *     <li>olcPlugin(String, MV)</li>
 *     <li>olcExtraArgs(String, MV)</li>
 *     <li>olcSchemaDN(DN, SV)</li>
 *     <li>olcSubordinate(String, SV)</li>
 *   </ul>
 * </ul>
 * 
 * <pre>
 * +--------------------------------------------------------+
 * | .----------------------------------------------------. |
 * | |V XXXX Database general                             | |
 * | +----------------------------------------------------+ |
 * | | Root DN :       [ ] none [///////////////////////] | |
 * | | Root password : [ ] none [///////////////////////] | |
 * | | Suffix :                                           | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   +------------------------------------+           | |
 * | +----------------------------------------------------+ |
 * |                                                        |
 * | .----------------------------------------------------. |
 * | |V XXXX Database limits                              | |
 * | +----------------------------------------------------+ |
 * | | Size limit : [//////////] (Edit...)                | |
 * | | Max Deref Depth : [///]                            | |
 * | | Timelimit :                                        | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   +------------------------------------+           | |
 * | | Limits :                                           | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   |                                    | --------  | |
 * | |   |                                    | (Up...)   | |
 * | |   |                                    | (Down...) | |
 * | |   +------------------------------------+           | |
 * | +----------------------------------------------------+ |
 * |                                                        |
 * | .----------------------------------------------------. |
 * | |V XXXX Database security                            | |
 * | +----------------------------------------------------+ |
 * | | Hidden : [ ]                       Read Only : [ ] | |
 * | | Requires :                                         | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Delete)  | |
 * | |   |                                    |           | |
 * | |   +------------------------------------+           | |
 * | | Restrict :                                         | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Delete)  | |
 * | |   |                                    |           | |
 * | |   +------------------------------------+           | |
 * | | Security Strength Factors :                        | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   +------------------------------------+           | |
 * | +----------------------------------------------------+ |
 * |                                                        |
 * | .----------------------------------------------------. |
 * | |V XXXX Database access                              | |
 * | +----------------------------------------------------+ |
 * | | Add content ACL : [ ]                              | |
 * | | ACLs :                                             | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   |                                    | --------  | |
 * | |   |                                    | (Up...)   | |
 * | |   |                                    | (Down...) | |
 * | |   +------------------------------------+           | |
 * | +----------------------------------------------------+ |
 * |                                                        |
 * | .----------------------------------------------------. |
 * | |V XXXX Database replication                         | |
 * | +----------------------------------------------------+ |
 * | | MirrorMode :    [ ]     Use Subentry : [ ]         | |
 * | | Replication Interval :  [///]                      | |
 * | | Replication Args[fileo:e[////////////////////////] | |
 * | | Replication PID file :  [////////////////////////] | |
 * | | Replication Log file :  [////////////////////////] | |
 * | | Update DN : [-------------------------->] (Browse) | |
 * | | Update References :                                | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   +------------------------------------+           | |
 * | | Replicas :                                         | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   |                                    | --------  | |
 * | |   |                                    | (Up...)   | |
 * | |   |                                    | (Down...) | |
 * | |   +------------------------------------+           | |
 * | | Syncrepls :                                        | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   |                                    | --------  | |
 * | |   |                                    | (Up...)   | |
 * | |   |                                    | (Down...) | |
 * | |   +------------------------------------+           | |
 * | +----------------------------------------------------+ |
 * |                                                        |
 * | .----------------------------------------------------. |
 * | |V XXXX Database options                             | |
 * | +----------------------------------------------------+ |
 * | | Last Modification : [ ] Monitoring : [ ]           | |
 * | | Schema DN : [-------------------------->] (Browse) | |
 * | | Subordinate : [//////////////////////////////////] | |
 * | | Plugins :                                          | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   +------------------------------------+           | |
 * | | Extras Arguments :                                 | |
 * | |   +------------------------------------+           | |
 * | |   |                                    | (Add...)  | |
 * | |   |                                    | (Edit...) | |
 * | |   |                                    | (Delete)  | |
 * | |   +------------------------------------+           | |
 * | +----------------------------------------------------+ |
 * +--------------------------------------------------------+
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabasesDetailsPage implements IDetailsPage
{
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
    //private ComboViewer databaseTypeComboViewer;
    
    // UI General sesstings widgets
    /** The olcSuffixDN attribute */
    private TableWidget<DnWrapper> suffixDnTableWidget;

    /** The olcRootDN attribute */
    private EntryWidget rootDnEntryWidget;

    /** The olcRootPW attribute */
    private PasswordWidget rootPasswordWidget;

    // UI limits settings widgets
    /** The olcSizeLimit */
    private Text sizeLimitText;
    
    /** The SizeLimit edit Button */
    private Button sizeLimitEditButton;

    /** The olcMaxDerefDepth parameter */
    private Text maxDerefDepthText;

    /** The olcTimeLimit Table */
    private TableWidget<TimeLimitWrapper> timeLimitTableWidget;

    /** The olcLimits Table */
    private TableWidget<LimitsWrapper> limitsTableWidget;

    /** The olcSchemaDN attribute */
    private EntryWidget schemaDnEntryWidget;

    // The Security UI Widgets
    /** The olcReadOnly attribute */
    private Button readOnlyButton;

    /** The olcHidden attribute */
    private Button hiddenButton;
    
    /** The olcRequires parameter */
    private TableWidget<RequireConditionEnum> requireConditionTableWidget;

    /** The olcRestrict parameter */
    private TableWidget<RestrictOperationEnum> restrictOperationTableWidget;
    
    /** The olcSecurity table widget */
    private TableWidget<SsfWrapper> securityTableWidget;
    
    // The Access UI Widgets
    /** The olcAddContentAcl Button */
    private Button addContentAclCheckbox;

    /** The olcAcess Table */
    private TableWidget<StringValueWrapper> aclsTableWidget;



    /** The associated overlays */
    private TableViewer overlaysTableViewer;
    private Button addOverlayButton;
    private Button editOverlayButton;
    private Button deleteOverlayButton;

    /** The specific configuration for each type of database */
    private Section specificSettingsSection;
    private Composite specificSettingsSectionComposite;
    private Composite databaseSpecificDetailsComposite;

    /** The olcMirrorMode flag */
    private BooleanWithDefaultWidget mirrorModeBooleanWithDefaultWidget;
    
    /** The olcDisabled flag (only available in OpenDLAP 2.4.36) */
    private BooleanWithDefaultWidget disabledBooleanWithDefaultWidget;
    
    /** The olcLastMod flag */
    private BooleanWithDefaultWidget lastModBooleanWithDefaultWidget;
    
    /** The olMonitoring flag */
    private BooleanWithDefaultWidget monitoringBooleanWithDefaultWidget;
    
    /** The Syncrepl consumer part */
    private TableViewer replicationConsumersTableViewer;
    private Button addReplicationConsumerButton;
    private Button editReplicationConsumerButton;
    private Button deleteReplicationConsumerButton;
    
    
    /**
     * The olcSuffixDn listener
     *
    private WidgetModifyListener suffixDnTableWidgetListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> suffixDns = new ArrayList<String>();
            
            for ( DnWrapper dnWrapper : suffixDnTableWidget.getElements() )
            {
                suffixDns.add( dnWrapper.toString() );
            }
            
            getConfiguration().getGlobal().setOlcRequires( requires );
        }
    };
    
    
    /**
     * The listener for the sizeLimit Text
     */
    private SelectionListener sizeLimitEditSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            SizeLimitDialog dialog = new SizeLimitDialog( sizeLimitText.getShell(), sizeLimitText.getText() );

            if ( dialog.open() == OverlayDialog.OK )
            {
                String newSizeLimitStr = dialog.getNewLimit();
                
                if ( newSizeLimitStr != null )
                {
                    sizeLimitText.setText( newSizeLimitStr );
                }
            }
        }
    };

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
     *
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
     * The modify listener which set the editor dirty 
     **/
    private SelectionListener dirtySelectionListener = new SelectionListener()
    {
        
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            setEditorDirty();
        }
        
        @Override
        public void widgetDefaultSelected( SelectionEvent e )
        {
            // TODO Auto-generated method stub
        }
    };
    
    
    /**
     * A modify listener for text zones when they have been modified
     */
    protected ModifyListener dirtyModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            setEditorDirty();
        }
    };

    
    /**
     * The olcHidden listener
     */
    private SelectionListener hiddenButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEditorDirty();
        }
    };

    
    /**
     * The olcReadOnly listener
     */
    private SelectionListener readOnlyButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEditorDirty();
        }
    };
    
    
    /**
     * The olcRequires listener
     */
    private WidgetModifyListener requireConditionListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> requires = new ArrayList<String>();
            
            for ( RequireConditionEnum requireCondition : requireConditionTableWidget.getElements() )
            {
                requires.add( requireCondition.getName() );
            }
            
            //getConfiguration().getGlobal().setOlcRequires( requires );
        }
    };
    
    
    /**
     * The olcRestrict listener
     */
    private WidgetModifyListener restrictOperationListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> restricts = new ArrayList<String>();
            
            for ( RestrictOperationEnum restrictOperation : restrictOperationTableWidget.getElements() )
            {
                restricts.add( restrictOperation.getName() );
            }
            
            //getConfiguration().getGlobal().setOlcRestrict( restricts );
        }
    };
    
    
    /**
     * The olcSecurity listener
     */
    private WidgetModifyListener securityListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> ssfWrappers = new ArrayList<String>();
            
            for ( SsfWrapper ssfWrapper : securityTableWidget.getElements() )
            {
                ssfWrappers.add( ssfWrapper.toString() );
            }
            
            //getConfiguration().getGlobal().setOlcSecurity( ssfWrappers );
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
     * Creates the Database general configuration panel. We have 8 sections :
     * <ul>
     * <li>General</li>
     * <li>Limits</li>
     * <li>Security</li>
     * <li>Access</li>
     * <li>Overlays</li>
     * <li>Replication</li>
     * <li>Options</li>
     * <li>Specific</li>
     * </ul>
     * {@inheritDoc}
     */
    public void createContents( Composite parent )
    {
        parentComposite = parent;
        parent.setLayout( new GridLayout() );

        createGeneralSettingsSection( parent, toolkit );
        createLimitsSettingsSection( parent, toolkit );
        createSecuritySettingsSection( parent, toolkit );
        createAccessSettingsSection( parent, toolkit );
        createOverlaySettingsSection( parent, toolkit );
        createReplicationConsumersSettingsSection( parent, toolkit );
        //createOptionsSettingsSection( parent, toolkit );
        createDatabaseSpecificSettingsSection( parent, toolkit );
    }


    /**
     * Creates the General Settings Section. This will expose the following attributes :
     * <ul>
     * <li>olcRootDN</li>
     * <li>olcRootPW</li>
     * <li>olcSuffix</li>
     * </ul>
     * 
     * <pre>
     * .----------------------------------------------------.
     * |V XXXX Database general                             |
     * +----------------------------------------------------+
     * | Root DN :       [ ] none [///////////////////////] |
     * | Root password : [ ] none [///////////////////////] |
     * | Suffix :                                           |
     * |   +------------------------------------+           |
     * |   |                                    | (Add...)  |
     * |   |                                    | (Edit...) |
     * |   |                                    | (Delete)  |
     * |   +------------------------------------+           |
     * +----------------------------------------------------+
     * </pre>
     *
     * @param parent the parent composite
     * @param toolkit the toolkit to use
     */
    private void createGeneralSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR );
        section.setText( Messages.getString( "OpenLDAPMasterDetail.GeneralSettings" ) );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gl = new GridLayout( 2, false );
        gl.marginRight = 18;
        composite.setLayout( gl );
        section.setClient( composite );

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

        // Database Type
        /*
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
        */

        // Suffix DN. We may have more than one
        toolkit.createLabel( composite, "Suffix:" );
        suffixDnTableWidget = new TableWidget<DnWrapper>( new DnDecorator( composite.getShell() ) );

        suffixDnTableWidget.createWidgetWithEdit( composite, toolkit );
        suffixDnTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        //addModifyListener( suffixDnTableWidget, suffixTableViewerListener );

        ControlDecoration suffixTextDecoration = new ControlDecoration( suffixDnTableWidget.getControl(), SWT.CENTER
            | SWT.RIGHT );
        suffixTextDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        suffixTextDecoration.setMarginWidth( 4 );
        suffixTextDecoration
            .setDescriptionText( "The DN suffix of queries that will be passed to this backend database." );

        /*

        // Schema DN
        toolkit.createLabel( composite, "Schema DN:" );
        schemaDnEntryWidget = new EntryWidget( browserConnection, null, true );
        schemaDnEntryWidget.createWidget( composite, toolkit );
        schemaDnEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ControlDecoration schemaDnTextDecoration = new ControlDecoration( schemaDnEntryWidget.getControl(), SWT.CENTER
            | SWT.RIGHT );
        schemaDnTextDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        schemaDnTextDecoration.setMarginWidth( 4 );
        schemaDnTextDecoration.setDescriptionText( 
            "Specify the distinguished name for the subschema subentry that controls the entries on this server" );

        // mirrorMode
        toolkit.createLabel( composite, "Mirror Mode:" );
        mirrorModeBooleanWithDefaultWidget = new BooleanWithDefaultWidget();
        mirrorModeBooleanWithDefaultWidget.create( composite, toolkit );
        mirrorModeBooleanWithDefaultWidget.setValue( false );
        mirrorModeBooleanWithDefaultWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ControlDecoration mirrorModeCheckboxDecoration = new ControlDecoration(
            mirrorModeBooleanWithDefaultWidget.getControl(), SWT.CENTER | SWT.RIGHT );
        mirrorModeCheckboxDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        mirrorModeCheckboxDecoration.setMarginWidth( 4 );
        mirrorModeCheckboxDecoration
            .setDescriptionText( "Sets the database in MirrorMode. This is only useful in a Multi-Master configuration" );
        
        // disabled (only in OpenLDAP 2.4.36)
        if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDisabled" ) )
        {
            toolkit.createLabel( composite, "Disabled:" );
            disabledBooleanWithDefaultWidget = new BooleanWithDefaultWidget();
            disabledBooleanWithDefaultWidget.create( composite, toolkit );
            disabledBooleanWithDefaultWidget.setValue( false );
            disabledBooleanWithDefaultWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
            ControlDecoration disabledCheckboxDecoration = new ControlDecoration(
                disabledBooleanWithDefaultWidget.getControl(), SWT.CENTER | SWT.RIGHT );
            disabledCheckboxDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
                OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
            disabledCheckboxDecoration.setMarginWidth( 4 );
            disabledCheckboxDecoration.setDescriptionText( "Disable this Database" );
        }
        
        // The olcLastMod parameter : Controls whether slapd will automatically maintain the 
        // modifiersName, modifyTimestamp, creatorsName, and createTimestamp attributes for entries
        toolkit.createLabel( composite, "Last Modifier:" );
        lastModBooleanWithDefaultWidget = new BooleanWithDefaultWidget();
        lastModBooleanWithDefaultWidget.create( composite, toolkit );
        lastModBooleanWithDefaultWidget.setValue( false );
        lastModBooleanWithDefaultWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ControlDecoration lastModCheckboxDecoration = new ControlDecoration(
            lastModBooleanWithDefaultWidget.getControl(), SWT.CENTER | SWT.RIGHT );
        lastModCheckboxDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        lastModCheckboxDecoration.setMarginWidth( 4 );
        lastModCheckboxDecoration.setDescriptionText( "Controls whether slapd will automatically maintain the modifiersName, modifyTimestamp, creatorsName, and createTimestamp attributes for entries" );
        
        // The olcMonitoring parameter
        toolkit.createLabel( composite, "Monitoring:" );
        monitoringBooleanWithDefaultWidget = new BooleanWithDefaultWidget();
        monitoringBooleanWithDefaultWidget.create( composite, toolkit );
        monitoringBooleanWithDefaultWidget.setValue( false );
        monitoringBooleanWithDefaultWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        ControlDecoration monitoringCheckboxDecoration = new ControlDecoration(
            monitoringBooleanWithDefaultWidget.getControl(), SWT.CENTER | SWT.RIGHT );
        monitoringCheckboxDecoration.setImage( OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_INFORMATION ).createImage() );
        monitoringCheckboxDecoration.setMarginWidth( 4 );
        monitoringCheckboxDecoration.setDescriptionText( "Controls whether monitoring is enabled for this database" );
        */
    }
    
    
    /**
     * Creates the Limits Settings Section. This will expose the following attributes :
     *   <ul>
     *     <li>olcSizeLimit(String, SV)</li>
     *     <li>olcTimeLimit(String, MV)</li>
     *     <li>olcLimits(String, MV, Ordered)</li>
     *     <li>olcMaxDerefDepth(Integer, SV)</li>
     *   </ul>
     * 
     * <pre>
     * .----------------------------------------------------.
     * |V XXXX Database limits                              |
     * +----------------------------------------------------+
     * | Size limit : [//////////] (Edit...)                | 
     * | Max Deref Depth : [///]                            |
     * | Timelimit :                                        |
     * |   +------------------------------------+           |
     * |   |                                    | (Add...)  |
     * |   |                                    | (Edit...) |
     * |   |                                    | (Delete)  |
     * |   +------------------------------------+           |
     * | Limits :                                           |
     * |   +------------------------------------+           |
     * |   |                                    | (Add...)  |
     * |   |                                    | (Edit...) |
     * |   |                                    | (Delete)  |
     * |   |                                    | --------  |
     * |   |                                    | (Up...)   |
     * |   |                                    | (Down...) |
     * |   +------------------------------------+           |
     * +----------------------------------------------------+
     * </pre>
     * 
     * @param parent the parent composite
     * @param toolkit the toolkit to use
     */
    private void createLimitsSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TWISTIE | Section.COMPACT | Section.TITLE_BAR );
        section.setText( Messages.getString( "OpenLDAPMasterDetail.LimitsSettings" ) );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gl = new GridLayout( 5, false );
        gl.marginRight = 18;
        composite.setLayout( gl );
        section.setClient( composite );
        
        // The olcSizeLimit parameter.
        toolkit.createLabel( composite, 
            Messages.getString( "OpenLDAPMasterDetail.SizeLimit" ) ); //$NON-NLS-1$
        sizeLimitText = toolkit.createText( composite, "" );
        sizeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // The SizeLimit edit button
        sizeLimitEditButton = BaseWidgetUtils.createButton( composite, 
            Messages.getString( "OpenLDAPMasterDetail.Edit" ), 1 ); //$NON-NLS-1$
        sizeLimitEditButton.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );
        sizeLimitEditButton.addSelectionListener( sizeLimitEditSelectionListener );
        
        // The olcMaxDerefDepth edit button
        toolkit.createLabel( composite, 
            Messages.getString( "OpenLDAPMasterDetail.MaxDerefDepth" ) ); //$NON-NLS-1$
        maxDerefDepthText = BaseWidgetUtils.createIntegerText( toolkit, composite,
            "Specifies the maximum number of aliases to dereference when trying to resolve an entry" );
        maxDerefDepthText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        
        // The olcTimeLimit parameter
        Label timeLimitLabel = toolkit.createLabel( composite, 
            Messages.getString( "OpenLDAPMasterDetail.TimeLimit" ) ); //$NON-NLS-1$
        timeLimitLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 5, 1 ) );
        
        timeLimitTableWidget = new TableWidget<TimeLimitWrapper>( 
            new TimeLimitDecorator( composite.getShell() ) );

        timeLimitTableWidget.createWidgetWithEdit( composite, toolkit );
        timeLimitTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 5, 1 ) );
        addModifyListener( timeLimitTableWidget, timeLimitListener );

        // The olcLimits parameter.
        Label limitsLabel = toolkit.createLabel( composite, 
            Messages.getString( "OpenLDAPMasterDetail.Limits" ) ); //$NON-NLS-1$
        limitsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 5, 1 ) );
        
        limitsTableWidget = new TableWidget<LimitsWrapper>( 
            new LimitsDecorator( composite.getShell(), "Limits" ) );

        limitsTableWidget.createOrderedWidgetWithEdit( composite, toolkit );
        limitsTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 5, 1 ) );
        addModifyListener( limitsTableWidget, limitsListener );
    }
    
    
    /**
     * Creates the Security Settings Section. This will expose the following attributes :
     *   <ul>
     *     <li>olcHidden(Boolean, SV)</li>
     *     <li>olcReadOnly(Boolean, SV)</li>
     *     <li>olcRequires(String, MV, Ordered)</li>
     *     <li>olcRestrict(Integer, SV)</li>
     *     <li>olcSecurity</li>
     *   </ul>
     * 
     * <pre>
     * .----------------------------------------------------.
     * |V XXXX Database security                            |
     * +----------------------------------------------------+
     * | Hidden : [ ]                       Read Only : [ ] |
     * | Requires :                                         |
     * |   +------------------------------------+           |
     * |   |                                    | (Add...)  |
     * |   |                                    | (Delete)  |
     * |   |                                    |           |
     * |   +------------------------------------+           |
     * | Restrict :                                         |
     * |   +------------------------------------+           |
     * |   |                                    | (Add...)  |
     * |   |                                    | (Delete)  |
     * |   |                                    |           |
     * |   +------------------------------------+           |
     * | Security Strength Factors :                        |
     * |   +------------------------------------+           |
     * |   |                                    | (Add...)  |
     * |   |                                    | (Edit...) |
     * |   |                                    | (Delete)  |
     * |   +------------------------------------+           |
     * +----------------------------------------------------+
     * </pre>
     * 
     * @param parent the parent composite
     * @param toolkit the toolkit to use
     */
    private void createSecuritySettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TWISTIE | Section.COMPACT | Section.TITLE_BAR );
        section.setText( Messages.getString( "OpenLDAPMasterDetail.SecuritySettings" ) );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite securityComposite = toolkit.createComposite( section );
        toolkit.paintBordersFor( securityComposite );
        GridLayout gl = new GridLayout( 4, true );
        gl.marginRight = 18;
        securityComposite.setLayout( gl );
        section.setClient( securityComposite );

        // The OlcHidden button
        hiddenButton = BaseWidgetUtils.createCheckbox( securityComposite, 
            Messages.getString( "OpenLDAPMasterDetail.Hidden" ), 2 );
        hiddenButton.addSelectionListener( hiddenButtonSelectionListener );

        // The OlcReadOnly button
        readOnlyButton = BaseWidgetUtils.createCheckbox( securityComposite, 
            Messages.getString( "OpenLDAPMasterDetail.ReadOnly" ), 2 );
        readOnlyButton.addSelectionListener( readOnlyButtonSelectionListener );
        
        // The olcRequires parameter label
        Label requireConditionLabel = toolkit.createLabel( securityComposite, 
            Messages.getString( "OpenLDAPMasterDetail.RequireCondition" ) ); //$NON-NLS-1$
        requireConditionLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcRestrict parameter label
        Label restrictOperationLabel = toolkit.createLabel( securityComposite,
            Messages.getString( "OpenLDAPMasterDetail.RestrictOperation" ) ); //$NON-NLS-1$
        restrictOperationLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcRequires parameter table
        requireConditionTableWidget = new TableWidget<RequireConditionEnum>( 
            new RequireConditionDecorator( securityComposite.getShell() ) );

        requireConditionTableWidget.createWidgetNoEdit( securityComposite, toolkit );
        requireConditionTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( requireConditionTableWidget, requireConditionListener );

        // The olcRestrict parameter table
        restrictOperationTableWidget = new TableWidget<RestrictOperationEnum>( 
            new RestrictOperationDecorator( securityComposite.getShell() ) );

        restrictOperationTableWidget.createWidgetNoEdit( securityComposite, toolkit );
        restrictOperationTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( restrictOperationTableWidget, restrictOperationListener );
        
        // The olcSecurity parameter table
        Label securityLabel = toolkit.createLabel( securityComposite, Messages.getString( "OpenLDAPMasterDetail.Security" ) ); //$NON-NLS-1$
        securityLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 4, 1 ) );
        
        securityTableWidget = new TableWidget<SsfWrapper>( new SsfDecorator( securityComposite.getShell() ) );

        securityTableWidget.createWidgetWithEdit( securityComposite, toolkit );
        securityTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        addModifyListener( securityTableWidget, securityListener );
    }


    /**
     * Creates the Access Settings Section. This will expose the following attributes :
     *   <ul>
     *     <li>olcAccess(String, MV, Ordered)</li>
     *     <li>olcAddContentAcl(Boolean)</li>
     *   </ul>
     * 
     * <pre>
     * .----------------------------------------------------.
     * |V XXXX Database access                              |
     * +----------------------------------------------------+
     * | Add content ACL : [ ]                              |
     * | ACLs :                                             |
     * |   +------------------------------------+           |
     * |   |                                    | (Add...)  |
     * |   |                                    | (Edit...) |
     * |   |                                    | (Delete)  |
     * |   |                                    | --------  |
     * |   |                                    | (Up...)   |
     * |   |                                    | (Down...) |
     * |   +------------------------------------+           |
     * +----------------------------------------------------+
     * </pre>
     * 
     * @param parent the parent composite
     * @param toolkit the toolkit to use
     */
    private void createAccessSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creating the Section
        Section accessSection = toolkit.createSection( parent, Section.TWISTIE | Section.COMPACT | Section.TITLE_BAR );
        accessSection.setText( Messages.getString( "OpenLDAPMasterDetail.AccessSettings" ) );
        accessSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite accessComposite = toolkit.createComposite( accessSection );
        toolkit.paintBordersFor( accessComposite );
        GridLayout gl = new GridLayout( 2, true );
        gl.marginRight = 18;
        accessComposite.setLayout( gl );
        accessSection.setClient( accessComposite );
        
        // The olcAddContentAcl Button
        addContentAclCheckbox = BaseWidgetUtils.createCheckbox( accessComposite, "Add Content ACL", 2 );
        
        // The olcAccess Table
        Label aclsLabel = toolkit.createLabel( accessComposite, 
            Messages.getString( "OpenLDAPMasterDetail.ACLs" ) ); //$NON-NLS-1$
        aclsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        
        /*
        aclsTableWidget = new TableWidget<StringValueWrapper>( 
            new LimitsDecorator( accessComposite.getShell(), "ACLs" ) );

        aclsTableWidget.createOrderedWidgetWithEdit( accessComposite, toolkit );
        aclsTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        addModifyListener( aclsTableWidget, aclsListener );
        */
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
        return OverlayDialog.getOverlayType( overlay ).getName();
    }


    /**
     * Creates the Database Specific Settings Section
     *
     * @param parent the parent composite
     * @param toolkit the toolkit to use
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

            // Suffixes
            database.clearOlcSuffix();
            List<DnWrapper> dnWrappers = suffixDnTableWidget.getElements();
            Dn[] suffixDns = new Dn[dnWrappers.size()];
            int pos = 0;
            
            for ( DnWrapper dnWrapper : dnWrappers )
            {
                suffixDns[pos++] = dnWrapper.getDn();
            }

            database.addOlcSuffix( suffixDns );

            // Root DN
            database.setOlcRootDN( rootDnEntryWidget.getDn() );

            // Root Password
            String rootPassword = rootPasswordWidget.getPasswordAsString();

            if ( Strings.isNotEmpty( rootPassword ) )
            {
                database.setOlcRootPW( rootPassword );
            }
            
            // Schema DN
            //database.setOlcSchemaDN( schemaDnEntryWidget.getDn() );

            // Read Only
            //database.setOlcReadOnly( readOnlyBooleanWithDefaultWidget.getValue() );

            // Hidden
            //database.setOlcHidden( hiddenBooleanWithDefaultWidget.getValue() );

            // Database specific details block
            if ( databaseSpecificDetailsBlock != null )
            {
                databaseSpecificDetailsBlock.commit( onSave );
            }
            
            // MirrorMode
            //database.setOlcMirrorMode( mirrorModeBooleanWithDefaultWidget.getValue() );
            
            // Disabled
            if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDisabled" ) )
            {
                database.setOlcDisabled( disabledBooleanWithDefaultWidget.getValue() );
            }
            
            // LastMod
            //database.setOlcLastMod( lastModBooleanWithDefaultWidget.getValue() );
            
            // AddAclContent
            //database.setOlcAddContentAcl( addContentAclBooleanWithDefaultWidget.getValue() );
            
            // Monitoring
            if ( ( database instanceof OlcHdbConfig ) || ( database instanceof OlcBdbConfig ) )
            {
                //database.setOlcMonitoring( monitoringBooleanWithDefaultWidget.getValue() );
            }
            
            // MaxDerefDepth
            //if ( ( maxDerefDepthText.getText() != null ) && ( maxDerefDepthText.getText().length() > 0 ) )
            {
                //database.setOlcMaxDerefDepth( Integer.parseInt( maxDerefDepthText.getText() ) );
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
                //databaseTypeComboViewer.getControl().setEnabled( false );
                suffixDnTableWidget.disable();
                rootDnEntryWidget.setEnabled( false );
                rootPasswordWidget.setEnabled( false );
                //schemaDnEntryWidget.setEnabled( false );
                //readOnlyBooleanWithDefaultWidget.setEnabled( false );
                //hiddenBooleanWithDefaultWidget.setEnabled( false );
                //mirrorModeBooleanWithDefaultWidget.setEnabled( false );
                
                // Disabled
                if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDisabled" ) )
                {
                    disabledBooleanWithDefaultWidget.setEnabled( false );
                }

                //lastModBooleanWithDefaultWidget.setEnabled( false );
                //addContentAclBooleanWithDefaultWidget.setEnabled( false );
                //monitoringBooleanWithDefaultWidget.setEnabled( false );
                //maxDerefDepthText.setEnabled( false );
            }
            else if ( isConfigDatabase( database ) )
            {
                //databaseTypeComboViewer.getControl().setEnabled( false );
                suffixDnTableWidget.enable();
                rootDnEntryWidget.setEnabled( true );
                rootPasswordWidget.setEnabled( true );
                //schemaDnEntryWidget.setEnabled( true );
                //readOnlyBooleanWithDefaultWidget.setEnabled( false );
                //hiddenBooleanWithDefaultWidget.setEnabled( false );
                //mirrorModeBooleanWithDefaultWidget.setEnabled( true );
                
                // Disabled
                if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDisabled" ) )
                {
                    disabledBooleanWithDefaultWidget.setEnabled( false );
                }
                
                //lastModBooleanWithDefaultWidget.setEnabled( false );
                //addContentAclBooleanWithDefaultWidget.setEnabled( false );
                //monitoringBooleanWithDefaultWidget.setEnabled( false );
                //maxDerefDepthText.setEnabled( false );
            }
            else
            {
                //databaseTypeComboViewer.getControl().setEnabled( true );
                suffixDnTableWidget.enable();
                rootDnEntryWidget.setEnabled( true );
                rootPasswordWidget.setEnabled( true );
                //schemaDnEntryWidget.setEnabled( true );
                //readOnlyBooleanWithDefaultWidget.setEnabled( true );
                //hiddenBooleanWithDefaultWidget.setEnabled( true );
                //mirrorModeBooleanWithDefaultWidget.setEnabled( true );
                
                // Disabled
                if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDisabled" ) )
                {
                    disabledBooleanWithDefaultWidget.setEnabled( true );
                }
                
                //lastModBooleanWithDefaultWidget.setEnabled( true );
                //addContentAclBooleanWithDefaultWidget.setEnabled( true );
                
                if ( ( database instanceof OlcHdbConfig ) || ( database instanceof OlcBdbConfig ) )
                {
                    //monitoringBooleanWithDefaultWidget.setEnabled( true );
                }
                else
                {
                    //monitoringBooleanWithDefaultWidget.setEnabled( false );
                }
                
                //maxDerefDepthText.setEnabled( true );
            }

            // Suffixes
            suffixDnTableWidget.getElements().clear();
            
            List<Dn> suffixesDnList = database.getOlcSuffix();
            List<DnWrapper> dnWrappers = new ArrayList<DnWrapper>();
            
            for ( Dn dn : suffixesDnList )
            {
                dnWrappers.add( new DnWrapper( dn ) );
            }
            
            suffixDnTableWidget.setElements( dnWrappers );

            // Root DN
            Dn rootDn = database.getOlcRootDN();
            rootDnEntryWidget.setInput( rootDn );

            // Root PW
            String rootPassword = database.getOlcRootPW();
            rootPasswordWidget.setPassword( ( rootPassword == null ) ? null : rootPassword.getBytes() );
            //            rootPasswordText.setText( ( rootPassword == null ) ? "" : rootPassword ); //$NON-NLS-1$

            // Read Only
            //readOnlyBooleanWithDefaultWidget.setValue( database.getOlcReadOnly() );

            // Hidden
            //hiddenBooleanWithDefaultWidget.setValue( database.getOlcHidden() );
            
            // Mirror Mode
            //mirrorModeBooleanWithDefaultWidget.setValue( database.getOlcMirrorMode() );
            
            // Disabled
            if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDisabled" ) )
            {
                disabledBooleanWithDefaultWidget.setValue( database.getOlcDisabled() );
            }

            // LastMod
            //lastModBooleanWithDefaultWidget.setValue( database.getOlcLastMod() );
            
            // AddAclContent
            //addContentAclBooleanWithDefaultWidget.setValue( database.getOlcAddContentAcl() );

            // Monitoring
            if ( ( database instanceof OlcHdbConfig ) || ( database instanceof OlcBdbConfig ) )
            {
                //monitoringBooleanWithDefaultWidget.setValue( database.getOlcMonitoring() );
            }
            
            //maxDerefDepthText.setText( database.getOlcMaxDerefDepth() == null ? "" : Integer.toString( database.getOlcMaxDerefDepth() ) );

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
                //databaseTypeComboViewer.setInput( DatabaseTypeEnum.values() );
                //databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.HDB ) );
                databaseSpecificDetailsBlock = new BerkeleyDbDatabaseSpecificDetailsBlock<OlcHdbConfig>( instance,
                    ( OlcHdbConfig ) database, browserConnection );
            }
            // OlcBdbConfig Type
            else if ( database instanceof OlcBdbConfig )
            {
                //databaseTypeComboViewer.setInput( DatabaseTypeEnum.values() );
                //databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.BDB ) );
                databaseSpecificDetailsBlock = new BerkeleyDbDatabaseSpecificDetailsBlock<OlcBdbConfig>( instance,
                    ( OlcBdbConfig ) database, browserConnection );
            }
            // OlcMdbConfig Type
            else if ( database instanceof OlcMdbConfig )
            {
                //databaseTypeComboViewer.setInput( DatabaseTypeEnum.values() );
                //databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.MDB ) );
                databaseSpecificDetailsBlock = new MdbDatabaseSpecificDetailsBlock( instance,
                    ( OlcMdbConfig ) database, browserConnection );
            }
            // OlcLdifConfig Type
            else if ( database instanceof OlcLdifConfig )
            {
                //databaseTypeComboViewer.setInput( DatabaseTypeEnum.values() );
                //databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.LDIF ) );
                databaseSpecificDetailsBlock = new LdifDatabaseSpecificDetailsBlock( instance,
                    ( OlcLdifConfig ) database );
            }
            // OlcNullConfig Type
            else if ( database instanceof OlcNullConfig )
            {
                //databaseTypeComboViewer.setInput( DatabaseTypeEnum.values() );
                //databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.NULL ) );
                databaseSpecificDetailsBlock = new NullDatabaseSpecificDetailsBlock( instance,
                    ( OlcNullConfig ) database );
            }
            // OlcRelayConfig Type
            else if ( database instanceof OlcRelayConfig )
            {
                //databaseTypeComboViewer.setInput( DatabaseTypeEnum.values() );
                //databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.RELAY ) );
                databaseSpecificDetailsBlock = new RelayDatabaseSpecificDetailsBlock( instance,
                    ( OlcRelayConfig ) database, browserConnection );
            }
            // None of these types
            else
            {
                // Looking for the frontend database
                if ( isFrontendDatabase( database ) )
                {
                    //databaseTypeComboViewer.setInput( FRONTEND_DATABASE_TYPES );
                    //databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.FRONTEND ) );
                    databaseSpecificDetailsBlock = new FrontendDatabaseSpecificDetailsBlock( instance, database,
                        browserConnection );
                }
                // Looking for the config database
                else if ( isConfigDatabase( database ) )
                {
                    //databaseTypeComboViewer.setInput( CONFIG_DATABASE_TYPES );
                    //databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.CONFIG ) );
                    databaseSpecificDetailsBlock = null;
                }
                // Any other type of database
                else
                {
                    //databaseTypeComboViewer.setInput( DatabaseTypeEnum.values() );
                    //databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseTypeEnum.NONE ) );
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
        // The Database general listeners
        addModifyListener( rootDnEntryWidget, dirtyWidgetModifyListener );
        addModifyListener( rootPasswordWidget, dirtyWidgetModifyListener );
        addModifyListener( suffixDnTableWidget, dirtyWidgetModifyListener );

        // The Database limit listeners
        addModifyListener( sizeLimitText, dirtyModifyListener );
        addModifyListener( maxDerefDepthText, dirtyModifyListener );
        addModifyListener( timeLimitTableWidget, dirtyWidgetModifyListener );
        addModifyListener( limitsTableWidget, dirtyWidgetModifyListener );
        
        
        // TODO...
        //addModifyListener( schemaDnEntryWidget, dirtyWidgetModifyListener );
        //addModifyListener( readOnlyBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        //addModifyListener( hiddenBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        //addModifyListener( mirrorModeBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        
        if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDisabled" ) )
        {
            addModifyListener( disabledBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        }

        addModifyListener( lastModBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        addSelectionListener( addContentAclCheckbox, dirtySelectionListener );
        addModifyListener( monitoringBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        //maxDerefDepthText.addModifyListener( dirtyModifyListener );

        //addSelectionChangedListener( databaseTypeComboViewer, databaseTypeComboViewerSelectionChangedListener );

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
        // The Database general listeners
        removeModifyListener( rootDnEntryWidget, dirtyWidgetModifyListener );
        removeModifyListener( rootPasswordWidget, dirtyWidgetModifyListener );
        removeModifyListener( suffixDnTableWidget, dirtyWidgetModifyListener );

        // The Database limit listeners
        removeModifyListener( sizeLimitText, dirtyModifyListener );
        removeModifyListener( maxDerefDepthText, dirtyModifyListener );
        removeModifyListener( timeLimitTableWidget, dirtyWidgetModifyListener );
        removeModifyListener( limitsTableWidget, dirtyWidgetModifyListener );

        
        removeModifyListener( suffixDnTableWidget, dirtyWidgetModifyListener );
        //removeModifyListener( schemaDnEntryWidget, dirtyWidgetModifyListener );
        //removeModifyListener( readOnlyBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        //removeModifyListener( hiddenBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        //removeModifyListener( mirrorModeBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        
        if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDisabled" ) )
        {
            removeModifyListener( disabledBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        }

        removeModifyListener( lastModBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        removeSelectionListener( addContentAclCheckbox, dirtySelectionListener );
        removeModifyListener( monitoringBooleanWithDefaultWidget, dirtyWidgetModifyListener );
        //maxDerefDepthText.removeModifyListener( dirtyModifyListener );

        //removeSelectionChangedListener( databaseTypeComboViewer, databaseTypeComboViewerSelectionChangedListener );

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
     * @param text the Text control
     * @param listener the listener
     */
    protected void addModifyListener( Text text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.addModifyListener( listener );
        }
    }


    /**
     * Adds a modify listener to the given TableWidget.
     *
     * @param table the Text control
     * @param listener the listener
     */
    protected void addModifyListener( TableWidget<?> tabelWidget, WidgetModifyListener listener )
    {
        if ( ( tabelWidget != null ) && ( listener != null ) )
        {
            tabelWidget.addWidgetModifyListener( listener );
        }
    }


    /**
     * Adds a modify listener to the given BrowserWidget.
     *
     * @param widget the widget
     * @param listener the listener
     */
    protected void addModifyListener( AbstractWidget widget, WidgetModifyListener listener )
    {
        if ( ( widget != null ) && ( listener != null ) )
        {
            widget.addWidgetModifyListener( listener );
        }
    }
    
    
    /**
     * The olcTimeLimit listener
     */
    private WidgetModifyListener timeLimitListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> timeLimits = new ArrayList<String>();
            
            for ( TimeLimitWrapper timeLimitWrapper : timeLimitTableWidget.getElements() )
            {
                timeLimits.add( timeLimitWrapper.toString() );
            }
            
            OlcDatabaseConfig databaseConfig = databaseWrapper.getDatabase();

            databaseConfig.setOlcTimeLimit( timeLimits );
        }
    };
    
    
    /**
     * The olcLimits listener
     */
    private WidgetModifyListener limitsListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> limits = new ArrayList<String>();
            
            for ( LimitsWrapper limitWrapper : limitsTableWidget.getElements() )
            {
                limits.add( limitWrapper.toString() );
            }
            
            OlcDatabaseConfig databaseConfig = databaseWrapper.getDatabase();

            databaseConfig.setOlcLimits( limits );
        }
    };


    /**
     * Adds a selection listener to the given Button.
     *
     * @param button the Button control
     * @param listener the listener
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
     * @param viewer the Viewer control
     * @param listener the listener
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
     * @param viewer the Viewer control
     * @param listener the listener
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
     * @param text the Text control
     * @param listener the listener
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
     * @param widget the widget
     * @param listener the listener
     */
    protected void removeModifyListener( AbstractWidget widget, WidgetModifyListener listener )
    {
        if ( ( widget != null ) && ( listener != null ) )
        {
            widget.removeWidgetModifyListener( listener );
        }
    }


    /**
     * Removes a selection listener to the given Button.
     *
     * @param button the Button control
     * @param listener the listener
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
     * @param viewer the Viewer
     * @param listener the listener
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
     * @param viewer the TableViewer
     * @param listener the listener
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
