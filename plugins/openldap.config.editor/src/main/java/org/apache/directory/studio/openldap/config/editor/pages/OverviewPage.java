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
package org.apache.directory.studio.openldap.config.editor.pages;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.openldap.common.ui.model.LogLevelEnum;
import org.apache.directory.studio.openldap.common.ui.dialogs.LogLevelDialog;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.overlays.ModuleWrapper;
import org.apache.directory.studio.openldap.config.editor.overlays.ModuleWrapperLabelProvider;
import org.apache.directory.studio.openldap.config.editor.overlays.ModuleWrapperViewerSorter;
import org.apache.directory.studio.openldap.config.editor.pages.OverlaysPage;
import org.apache.directory.studio.openldap.config.editor.wrappers.DatabaseWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.DatabaseWrapperLabelProvider;
import org.apache.directory.studio.openldap.config.editor.wrappers.DatabaseWrapperViewerSorter;
import org.apache.directory.studio.openldap.config.editor.wrappers.ServerIdDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.ServerIdWrapper;
import org.apache.directory.studio.openldap.config.model.OlcModuleList;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the General Page of the Server Configuration Editor. It exposes some
 * of the configured elements, and allow the user to configure some basic parameters :
 * <ul>
 * <li>olcServerID</li>
 * <li>olcConfigDir</li>
 * <li>olcConfigFile</li>
 * <li>olcPidFile</li>
 * <li>olcLogFile</li>
 * <li>olcLogLevel</li>
 * </ul>
 * 
 * The olcConfigFile is not handled, it's deprecated.
 * <pre>
 * .-----------------------------------------------------------------------------------.
 * | Overview                                                                          |
 * +-----------------------------------------------------------------------------------+
 * | .-------------------------------------------------------------------------------. |
 * | |V Global parameters                                                            | |
 * | +-------------------------------------------------------------------------------+ |
 * | | Server ID  :                                                                  | |
 * | | +-----------------------------------------------------------------+           | |
 * | | |                                                                 | (Add)     | |
 * | | |                                                                 | (Edit)    | |
 * | | |                                                                 | (Delete)  | |
 * | | +-----------------------------------------------------------------+           | |
 * | |                                                                               | |
 * | | Configuration Dir : [                ]  Pid File  : [                ]        | |
 * | | Log File          : [                ]  Log Level : [                ]  (edit)| |
 * | +-------------------------------------------------------------------------------+ |
 * |                                                                                   |
 * | .---------------------------------------.  .------------------------------------. |
 * | |V Databases                            |  |V Overlays                          | |
 * | +---------------------------------------+  +------------------------------------+ |
 * | | +----------------------------------+  |  | +--------------------------------+ | |
 * | | | abc                              |  |  | | module 1                       | | |
 * | | | xyz                              |  |  | | module 2                       | | |
 * | | +----------------------------------+  |  | +--------------------------------+ | |
 * | | <Advanced databases configuration>    |  | <Overlays configuration>           | |
 * | +---------------------------------------+  +------------------------------------+ |
 * |                                                                                   |
 * | .-------------------------------------------------------------------------------. |
 * | |V Configuration detail                                                         | |
 * | +-------------------------------------------------------------------------------+ |
 * | | <Security configuration>               <Tunning configuration>                | |
 * | | <Schemas configuration>                <Options configuration>                | |
 * | +-------------------------------------------------------------------------------+ |
 * +-----------------------------------------------------------------------------------+
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OverviewPage extends OpenLDAPServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = OverviewPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "OpenLDAPOverviewPage.Title" ); //$NON-NLS-1$"Overview";
    
    // UI Controls
    /** The serverID wrapper */
    private List<ServerIdWrapper> serverIdWrappers = new ArrayList<ServerIdWrapper>();
    
    /** The Widget used to manage ServerID */
    private TableWidget<ServerIdWrapper> serverIdTableWidget;

    /** olcConfigDir */
    private Text configDirText;
    
    /** olcPidFile */
    private Text pidFileText;
    
    /** olcLogFile */
    private Text logFileText;
    
    /** olcLogLevel */
    private Text logLevelText;
    private Button logLevelEditButton;
    
    /** The table listing all the existing databases */
    private TableViewer databaseViewer;

    /** The database wrappers */
    private List<DatabaseWrapper> databaseWrappers = new ArrayList<DatabaseWrapper>();

    /** This link opens the Databases configuration tab */ 
    private Hyperlink databasesPageLink;
    
    /** The table listing all the existing modules */
    private TableViewer moduleViewer;

    /** The module wrappers */
    private List<ModuleWrapper> moduleWrappers = new ArrayList<ModuleWrapper>();

    /** This link opens the Overlays configuration tab */
    private Hyperlink overlaysPageLink;

    /** This link opens the Security configuration tab */ 
    private Hyperlink securityPageLink;

    /** This link opens the Tuning configuration tab */
    private Hyperlink tuningPageLink;

    /** This link opens the Schema configuration tab */
    private Hyperlink schemaPageLink;

    /** This link opens the Options configuration tab */
    private Hyperlink optionsPageLink;
    
    
    /**
     * The olcLogFile listener
     */
    private ModifyListener logFileTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getConfiguration().getGlobal().setOlcLogFile( logFileText.getText() );
        }
    };
    
    
    /**
     * The olcConfigDir listener
     */
    private ModifyListener configDirTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getConfiguration().getGlobal().setOlcConfigDir( configDirText.getText() );
        }
    };
    
    
    /**
     * The olcPidFile listener
     */
    private ModifyListener pidFileTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getConfiguration().getGlobal().setOlcPidFile( pidFileText.getText() );
        }
    };

    /**
     * The olcLogLevl listener
     */
    private SelectionListener logLevelEditButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // Creating and opening a LogLevel dialog
            String logLevelStr = logLevelText.getText();
            int logLevelValue = LogLevelEnum.NONE.getValue();
            
            if ( !Strings.isEmpty( logLevelStr ) )
            {
                logLevelValue = LogLevelEnum.parseLogLevel( logLevelStr );
            }
            
            LogLevelDialog dialog = new LogLevelDialog( logLevelEditButton.getShell(), logLevelValue );

            if ( LogLevelDialog.OK == dialog.open() )
            {
                logLevelStr = LogLevelEnum.getLogLevelText( dialog.getLogLevelValue() );
                logLevelText.setText( logLevelStr );
                List<String> logLevelList = new ArrayList<String>();
                logLevelList.add( logLevelStr );
                getConfiguration().getGlobal().setOlcLogLevel( logLevelList );
            }
        }
    };

    
    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor the associated editor
     */
    public OverviewPage( OpenLDAPServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }

    
    /**
     * Databases configuration hyper link adapter
     */
    private HyperlinkAdapter databasesPageLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            getServerConfigurationEditor().showPage( DatabasesPage.class );
        }
    };

    
    /**
     * Overlays configuration hyper link adapter
     */
    private HyperlinkAdapter overlaysPageLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            getServerConfigurationEditor().showPage( OverlaysPage.class );
        }
    };

    
    /**
     * Security configuration hyper link adapter
     */
    private HyperlinkAdapter securityPageLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            getServerConfigurationEditor().showPage( SecurityPage.class );
        }
    };

    
    /**
     * Tuning configuration hyper link adapter
     */
    private HyperlinkAdapter tuningPageLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            getServerConfigurationEditor().showPage( TuningPage.class );
        }
    };

    
    /**
     * Schema configuration hyper link adapter
     */
    private HyperlinkAdapter schemaPageLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            //getServerConfigurationEditor().showPage( SchemaPage.class );
        }
    };

    
    /**
     * Options configuration hyper link adapter
     */
    private HyperlinkAdapter optionsPageLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            getServerConfigurationEditor().showPage( OptionsPage.class );
        }
    };

    
    // The listener for the ServerIdTableWidget
    private WidgetModifyListener serverIdTableWidgetListener = new WidgetModifyListener()
    {
        @Override
        public void widgetModified( WidgetModifyEvent event )
        {
            // Process the parameter modification
            TableWidget<ServerIdWrapper> serverIdWrapperTable = (TableWidget<ServerIdWrapper>)event.getSource();
            List<String> serverIds = new ArrayList<String>();
            
            for ( Object serverIdWrapper : serverIdWrapperTable.getElements() )
            {
                String str = serverIdWrapper.toString();
                serverIds.add( str );
            }
            
            getConfiguration().getGlobal().setOlcServerID( serverIds );
        }
    };


    /**
     * Creates the global Overview OpenLDAP config Tab. It contains 3 rows, with
     * one or two sections in each :
     * 
     * <pre>
     * +---------------------------------------------------------------------+
     * |                                                                     |
     * | Global parameters                                                   |
     * |                                                                     |
     * +-----------------------------------+---------------------------------+
     * |                                   |                                 |
     * | Databases                         | Overlays                        |
     * |                                   |                                 |
     * +-----------------------------------+---------------------------------+
     * |                                                                     |
     * | Configuration links                                                 |
     * |                                                                     |
     * +---------------------------------------------------------------------+
     * </pre>
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        TableWrapLayout twl = new TableWrapLayout();
        twl.numColumns = 2;
        parent.setLayout( twl );

        // The upper part
        Composite upperComposite = toolkit.createComposite( parent );
        upperComposite.setLayout( new GridLayout() );
        TableWrapData leftCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 2 );
        leftCompositeTableWrapData.grabHorizontal = true;
        upperComposite.setLayoutData( leftCompositeTableWrapData );

        // The middle left part
        Composite middleLeftComposite = toolkit.createComposite( parent );
        middleLeftComposite.setLayout( new GridLayout() );
        TableWrapData middleLeftCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        middleLeftCompositeTableWrapData.grabHorizontal = true;
        middleLeftComposite.setLayoutData( middleLeftCompositeTableWrapData );

        // The middle right part
        Composite middleRightComposite = toolkit.createComposite( parent );
        middleRightComposite.setLayout( new GridLayout() );
        TableWrapData middleRightCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        middleRightCompositeTableWrapData.grabHorizontal = true;
        middleRightComposite.setLayoutData( middleRightCompositeTableWrapData );

        // The lower part
        Composite lowerComposite = toolkit.createComposite( parent );
        lowerComposite.setLayout( new GridLayout() );
        TableWrapData lowerCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 2 );
        lowerCompositeTableWrapData.grabHorizontal = true;
        lowerComposite.setLayoutData( lowerCompositeTableWrapData );

        // Now, create the sections
        createGlobalSection( toolkit, upperComposite );
        createDatabasesSection( toolkit, middleLeftComposite );
        createOverlaysSection( toolkit, middleRightComposite );
        createConfigDetailsLinksSection( toolkit, lowerComposite );

        refreshUI();
    }


    /**
     * Creates the global section. This section is a grid with 4 columns,
     * where we configure the global options. We support the configuration
     * of those parameters :
     * <ul>
     * <li>olcServerID</li>
     * <li>olcConfigDir</li>
     * <li>olcPidFile</li>
     * <li>olcLogFile</li>
     * <li>olcLogLevel</li>
     * </ul>
     * 
     * <pre>
     * .-------------------------------------------------------------------------------.
     * |V Global parameters                                                            |
     * +-------------------------------------------------------------------------------+
     * | Server ID  :                                                                  |
     * | +-----------------------------------------------------------------+           |
     * | |                                                                 | (Add)     |
     * | |                                                                 | (Edit)    |
     * | |                                                                 | (Delete)  |
     * | +-----------------------------------------------------------------+           |
     * |                                                                               |
     * | Configuration Dir : [                ]  Pid File  : [                ]        |
     * | Log File          : [                ]  Log Level : [                ]  (edit)|
     * +-------------------------------------------------------------------------------+
     * </pre>
     * 
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createGlobalSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPOverviewPage.GlobalSection" ) );

        // The content
        Composite globalSectionComposite = toolkit.createComposite( section );
        toolkit.paintBordersFor( globalSectionComposite );
        GridLayout gridLayout = new GridLayout( 5, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        globalSectionComposite.setLayout( gridLayout );
        section.setClient( globalSectionComposite );

        // The ServerID parameter.
        Label serverIdLabel = toolkit.createLabel( globalSectionComposite, Messages.getString( "OpenLDAPOverviewPage.ServerID" ) ); //$NON-NLS-1$
        serverIdLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 5, 1 ) );

        // The ServerID widget
        serverIdTableWidget = new TableWidget<ServerIdWrapper>( new ServerIdDecorator( section.getShell() ) );

        serverIdTableWidget.createWidgetWithEdit( globalSectionComposite, toolkit );
        serverIdTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 5, 1 ) );
        serverIdTableWidget.addWidgetModifyListener( serverIdTableWidgetListener );

        // One blank line
        for ( int i = 0; i < gridLayout.numColumns; i++ )
        {
            toolkit.createLabel( globalSectionComposite, TABULATION );
        }
        
        // The ConfigDir parameter
        toolkit.createLabel( globalSectionComposite, Messages.getString( "OpenLDAPOverviewPage.ConfigDir" ) ); //$NON-NLS-1$
        configDirText = createConfigDirText( toolkit, globalSectionComposite );
        
        // The PidFile parameter
        toolkit.createLabel( globalSectionComposite, Messages.getString( "OpenLDAPOverviewPage.PidFile" ) ); //$NON-NLS-1$
        pidFileText = createPidFileText( toolkit, globalSectionComposite );
        
        // The LogFile parameter
        toolkit.createLabel( globalSectionComposite, Messages.getString( "OpenLDAPOverviewPage.LogFile" ) ); //$NON-NLS-1$
        logFileText = createLogFileText( toolkit, globalSectionComposite );
        
        // The LogLevel parameter
        toolkit.createLabel( globalSectionComposite, Messages.getString( "OpenLDAPOverviewPage.LogLevel" ) );
        logLevelText = BaseWidgetUtils.createText( globalSectionComposite, "", 1 );
        logLevelText.setEditable( false );
        logLevelEditButton = BaseWidgetUtils.createButton( globalSectionComposite, 
            Messages.getString( "OpenLDAPSecurityPage.EditLogLevels" ), 1 );
        logLevelEditButton.addSelectionListener( logLevelEditButtonSelectionListener );
    }
    
    
    /**
     * Creates the Databases section. It only expose the existing databases,
     * they can't be changed.
     * 
     * <pre>
     * .------------------------------------.
     * |V Databases                         |
     * +------------------------------------+
     * | +-------------------------------+  |
     * | | abc                           |  |
     * | | xyz                           |  |
     * | +-------------------------------+  |
     * | <Advanced databases configuration> |
     * +------------------------------------+
     * </pre>
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createDatabasesSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPOverviewPage.DatabasesSection" ) );
        
        // The content
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // The inner composite
        Composite databaseComposite = toolkit.createComposite( section );
        databaseComposite.setLayout( new GridLayout( 1, false ) );
        toolkit.paintBordersFor( databaseComposite );
        section.setClient( databaseComposite );
        section.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Creating the Table and Table Viewer
        Table table = toolkit.createTable( databaseComposite, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 5 );
        gd.heightHint = 100;
        gd.widthHint = 100;
        table.setLayoutData( gd );

        databaseViewer = new TableViewer( table );
        databaseViewer.setContentProvider( new ArrayContentProvider() );
        databaseViewer.setLabelProvider( new DatabaseWrapperLabelProvider() );
        databaseViewer.setSorter( new DatabaseWrapperViewerSorter() );

        // Databases Page Link
        databasesPageLink = toolkit.createHyperlink( databaseComposite,
            Messages.getString( "OpenLDAPOverviewPage.DatabasesPageLink" ), SWT.NONE ); //$NON-NLS-1$
        databasesPageLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 1 ) );
        databasesPageLink.addHyperlinkListener( databasesPageLinkListener );
    }
    
    
    /**
     * Creates the Overlays section. It only expose the existing overlays,
     * they can't be changed.
     * 
     * <pre>
     * .------------------------------------.
     * |V Overlays                         |
     * +------------------------------------+
     * | +-------------------------------+  |
     * | | abc                           |  |
     * | | xyz                           |  |
     * | +-------------------------------+  |
     * | <Advanced Overlays configuration>  |
     * +------------------------------------+
     * </pre>
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createOverlaysSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPOverviewPage.OverlaysSection" ) );
        
        // The content
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // The inner composite
        Composite overlayComposite = toolkit.createComposite( section );
        overlayComposite.setLayout( new GridLayout( 1, false ) );
        toolkit.paintBordersFor( overlayComposite );
        section.setClient( overlayComposite );
        section.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Creating the Table and Table Viewer
        Table table = toolkit.createTable( overlayComposite, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 5 );
        gd.heightHint = 100;
        gd.widthHint = 100;
        table.setLayoutData( gd );

        moduleViewer = new TableViewer( table );
        moduleViewer.setContentProvider( new ArrayContentProvider() );
        moduleViewer.setLabelProvider( new ModuleWrapperLabelProvider() );
        moduleViewer.setSorter( new ModuleWrapperViewerSorter() );

        // Overlays Page Link
        overlaysPageLink = toolkit.createHyperlink( overlayComposite,
            Messages.getString( "OpenLDAPOverviewPage.OverlaysPageLink" ), SWT.NONE ); //$NON-NLS-1$
        overlaysPageLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 1 ) );
        overlaysPageLink.addHyperlinkListener( overlaysPageLinkListener );
    }


    /**
     * Creates the configuration details section. It just links to some other pages
     * 
     * <pre>
     * .------------------------------------------------------------------------.
     * |V Configuration detail                                                  |
     * +------------------------------------------------------------------------+
     * | <Security configuration>                                               |
     * | <Tunning configuration>                                                |
     * | <Options configuration>                                                |
     * | <Schemas configuration>                                                |
     * +------------------------------------------------------------------------+ |
     * </pre>
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createConfigDetailsLinksSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPOverviewPage.ConfigDetailsSection" ) );

        // The content
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 2, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Security Page Link
        securityPageLink = toolkit.createHyperlink( composite,
            Messages.getString( "OpenLDAPOverviewPage.SecurityPageLink" ), SWT.NONE ); //$NON-NLS-1$
        securityPageLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 1 ) );
        securityPageLink.addHyperlinkListener( securityPageLinkListener );

        // Tuning Page Link
        tuningPageLink = toolkit.createHyperlink( composite,
            Messages.getString( "OpenLDAPOverviewPage.TuningPageLink" ), SWT.NONE ); //$NON-NLS-1$
        tuningPageLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 1 ) );
        tuningPageLink.addHyperlinkListener( tuningPageLinkListener );

        // Schema Page Link
        schemaPageLink = toolkit.createHyperlink( composite,
            Messages.getString( "OpenLDAPOverviewPage.SchemaPageLink" ), SWT.NONE ); //$NON-NLS-1$
        schemaPageLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 1 ) );
        schemaPageLink.addHyperlinkListener( schemaPageLinkListener );

        // Options Page Link
        optionsPageLink = toolkit.createHyperlink( composite,
            Messages.getString( "OpenLDAPOverviewPage.OptionsPageLink" ), SWT.NONE ); //$NON-NLS-1$
        optionsPageLink.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 1 ) );
        optionsPageLink.addHyperlinkListener( optionsPageLinkListener );
    }

    
    /**
     * Creates a Text that can be used to enter an ConfigDir.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that can be used to enter a config Dir
     */
    private Text createConfigDirText( FormToolkit toolkit, Composite parent )
    {
        final Text configDirText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        GridData gd = new GridData( SWT.NONE, SWT.NONE, false, false );
        gd.widthHint = 300;
        configDirText.setLayoutData( gd );
        
        // No more than 512 digits
        configDirText.setTextLimit( 512 );
        
        // The associated listener
        addModifyListener( configDirText, configDirTextListener );

        return configDirText;
    }


    /**
     * Creates a Text that can be used to enter a PID file.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that can be used to enter a PID file
     */
    private Text createPidFileText( FormToolkit toolkit, Composite parent )
    {
        final Text pidFileText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        GridData gd = new GridData( SWT.FILL, SWT.NONE, false, true, 2, 1 );
        gd.widthHint = 300;
        pidFileText.setLayoutData( gd );
        
        // No more than 512 digits
        pidFileText.setTextLimit( 512 );
        
        // The associated listener
        addModifyListener( pidFileText, pidFileTextListener );
        
        return pidFileText;
    }


    /**
     * Creates a Text that can be used to enter a Log file.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that can be used to enter a Log file
     */
    private Text createLogFileText( FormToolkit toolkit, Composite parent )
    {
        final Text logFileText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        GridData gd = new GridData( SWT.NONE, SWT.NONE, false, false );
        gd.widthHint = 300;
        logFileText.setLayoutData( gd );
        
        // No more than 512 digits
        logFileText.setTextLimit( 512 );
        
        // The associated listener
        addModifyListener( logFileText, logFileTextListener );

        return logFileText;
    }


    /**
     * Get the various LogLevel values, and concatenate them in a String
     */
    private String getLogLevel()
    {
        List<String> logLevelList = getConfiguration().getGlobal().getOlcLogLevel();
        
        if ( logLevelList == null )
        {
            return "none";
        }
        
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();
        
        for ( String logLevel : logLevelList )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( " " );
            }
            
            sb.append( logLevel );
        }
        
        return sb.toString();
    }
    

    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
        if ( isInitialized() )
        { 
            removeListeners();

            // Update the ServerIDText
            // Update the DatabaseTableViewer
            serverIdWrappers.clear();

            for ( String serverIdWrapper : getConfiguration().getGlobal().getOlcServerID() )
            {
                serverIdWrappers.add( new ServerIdWrapper( serverIdWrapper ) );
            }

            serverIdTableWidget.setElements( serverIdWrappers );
            
            // Update the ConfigDirText
            configDirText.setText( getConfiguration().getGlobal().getOlcConfigDir() );

            // Update the LogFIleText
            String logFile = getConfiguration().getGlobal().getOlcLogFile();
            
            if ( logFile != null )
            {
                logFileText.setText( logFile );
            }
            else
            {
                logFileText.setText( "<stderr>" );
            }

            // Update the DatabaseTableViewer
            databaseWrappers.clear();

            for ( OlcDatabaseConfig database : getConfiguration().getDatabases() )
            {
                databaseWrappers.add( new DatabaseWrapper( database ) );
            }

            databaseViewer.setInput( databaseWrappers );

            // Update the OverlaysTableViewer
            moduleWrappers.clear();
            
            for ( OlcModuleList moduleList : getConfiguration().getModules() )
            {
                List<String> modules = moduleList.getOlcModuleLoad();
                int index = OpenLdapConfigurationPluginUtils.getOrderingPostfix( moduleList.getCn().get( 0 ) );
                
                if ( modules != null )
                {
                    for ( String module : modules )
                    {
                        int order = OpenLdapConfigurationPluginUtils.getOrderingPrefix( module );
                        String strippedModule = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( module );
                        String strippedModuleListName = OpenLdapConfigurationPluginUtils.stripOrderingPostfix( moduleList.getCn().get( 0 ) );
                        moduleWrappers.add( new ModuleWrapper( strippedModuleListName, index, strippedModule, moduleList.
                            getOlcModulePath(), order ) );
                    }
                }
            }

            moduleViewer.setInput( moduleWrappers );
            
            // Update the LogLevelWidget
            String logLevels = getLogLevel();
            logLevelText.setText( logLevels );

            addListeners();
        }
    }

    
    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        // The serverID Text 
        removeDirtyListener( serverIdTableWidget );

        // The configDir Text 
        removeDirtyListener( configDirText );

        // The pidFile Text 
        removeDirtyListener( pidFileText );

        // The logFile Text 
        removeDirtyListener( logFileText );

        // The LogLevel Widget 
        removeDirtyListener( logLevelText );
    }

    
    /**
     * Adds listeners to UI Controls.
     */
    private void addListeners()
    {
        // The serverID Text 
        addDirtyListener( serverIdTableWidget );

        // The configDir Text 
        addDirtyListener( configDirText );

        // The pidFile Text 
        addDirtyListener( pidFileText );

        // The logFile Text 
        addDirtyListener( logFileText );

        // The LogLevel Widget 
        addDirtyListener( logLevelText );
    }
}
