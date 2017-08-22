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

import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.config.editor.wrappers.DbIndexDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.DbIndexWrapper;
import org.apache.directory.studio.openldap.config.model.database.OlcMdbConfig;
import org.apache.directory.studio.openldap.common.ui.widgets.BooleanWithDefaultWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.DirectoryBrowserWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.UnixPermissionsWidget;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class implements a block for Memory-Mapped DB Specific Details. The GUI will
 * look like :
 * 
 * <pre>
 * .--------------------------------------------------------------------.
 * | Database Specific Settings                                         |
 * +--------------------------------------------------------------------+
 * | .----------------------------------------------------------------. |
 * | |v MDB Configuration                                             | |
 * | +----------------------------------------------------------------+ |
 * | | Directory : [////////////////////////////[v] (Browse)          | |
 * | | Mode :      [--------(0000)               ] (Edit Permissions) | |
 * | +----------------------------------------------------------------+ |
 * |                                                                    |
 * | v Database indices                                                 |
 * |  +----------------------------------------------+                  |
 * |  | indice 1                                     | (Add)            |
 * |  | indice 2                                     | (Edit)           |
 * |  | ...                                          | (Delete)         |
 * |  +----------------------------------------------+                  |
 * |                                                                    |
 * | v Database Limits                                                  |
 * |  Maximum Readers :     [                         ]                 |
 * |  Maximum Size :        [                         ]                 |
 * |  Maximum Entry Size :  [                         ]                 | (2.4.41)
 * |  Search Stack Depth :  [                         ]                 |
 * |  Checkpoint Interval : [                         ]                 |
 * |                                                                    |
 * | v Database Options                                                 |
 * |  Disable Synchronous Database Writes : [----------]                |
 * |  Environment Flags :                                               | (2.4.33)
 * |    +----------------------------------------------+                |
 * |    | Flag 1                                       | (Add)          |
 * |    | Flag 2                                       | (Edit)         |
 * |    | ...                                          | (Delete)       |
 * |    +----------------------------------------------+                |
 * +--------------------------------------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MdbDatabaseSpecificDetailsBlock extends AbstractDatabaseSpecificDetailsBlock<OlcMdbConfig>
{
    // UI Widgets
    /** The olcDbDirectory attribute (String) */
    private DirectoryBrowserWidget directoryBrowserWidget;

    /** The olcDbCheckpoint attribute (String) */
    private Text checkpointText;

    /** The olcDbEnvFlags attribute (String, multi-values) */
    private Text envFlagsText;

    /** The olcDbIndex attribute (String, multi-values) */
    private TableWidget<DbIndexWrapper> indicesWidget;

    /** The olcMaxEntrySize attribute (Integer) No yet available (2.4.41) */
    private Text maxEntrySizeText;

    /** The olcDbMaxReaders attribute (Integer) */
    private Text maxReadersText;

    /** The olcMaxSize attribute (Long) */
    private Text maxSizeText;

    /** The olcDbMode attribute (String) */
    private UnixPermissionsWidget modeUnixPermissionsWidget;

    /** The olcDbNoSync attribute (Boolean) */
    private BooleanWithDefaultWidget disableSynchronousDatabaseWritesBooleanWithDefaultWidget;

    /** The olcDbSearchStack attribute( Integer) */
    private Text searchStackDepthText;
    
    
    /**
     * The olcAllows listener
     */
    private WidgetModifyListener indexesListener = event ->
        {
            List<String> indices = new ArrayList<>();
            
            for ( DbIndexWrapper dbIndex : indicesWidget.getElements() )
            {
                indices.add( dbIndex.toString() );
            }
            
            database.setOlcDbIndex( indices );
        };
        
    /**
     * Creates a new instance of MdbDatabaseSpecificDetailsBlock.
     * 
     * @param databaseDetailsPage the database details page 
     * @param database the database
     * @param browserConnection the connection
     */
    public MdbDatabaseSpecificDetailsBlock( DatabasesDetailsPage detailsPage, OlcMdbConfig database,
        IBrowserConnection browserConnection )
    {
        super( detailsPage, database, browserConnection );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createBlockContent( Composite parent, FormToolkit toolkit )
    {
        // Composite
        Composite composite = toolkit.createComposite( parent );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        createDatabaseConfigurationSection( composite, toolkit );
        createDatabaseIndexesSection( composite, toolkit );
        createDatabaseLimitsSection( composite, toolkit );
        createDatabaseOptionsSection( composite, toolkit );

        return composite;
    }


    /**
     * Creates the database configuration section. We manage the following configuration elements :
     * <ul>
     * <li>Directory : the directory on disk where the file will be stored</li>
     * <li>mode : the file mode for this directory</li>
     * </ul>
     * It covers the following attributes :
     * <ul>
     * <li>olcDbDirectory</li>
     * <li>olcDbMode</li>
     * </ul>
     *
     * <pre>
     * .------------------------------------------------------------------.
     * |v MDB Configuration                                               |
     * +------------------------------------------------------------------+
     * | Directory : [///////////////////////////////] (Browse)           |
     * | Mode :      [///////////////////////////////] (Edit Permissions) |
     * +------------------------------------------------------------------+
     * </pre
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    private void createDatabaseConfigurationSection( Composite parent, FormToolkit toolkit )
    {
        // Database Configuration Section
        Section databaseConfigurationSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseConfigurationSection.setText( Messages.getString( "OpenLDAPMDBConfiguration.Section" ) );
        databaseConfigurationSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite databaseConfigurationComposite = toolkit.createComposite( databaseConfigurationSection );
        toolkit.paintBordersFor( databaseConfigurationComposite );
        databaseConfigurationComposite.setLayout( new GridLayout( 2, false ) );
        databaseConfigurationSection.setClient( databaseConfigurationComposite );

        // Directory Text. This is a MUST attribute (it will be red and bold)
        Label olcDirectory = toolkit.createLabel( databaseConfigurationComposite, Messages.getString( "OpenLDAPMDBConfiguration.Directory" ) );
        olcDirectory.setForeground( CommonUIConstants.RED_COLOR );
        FontDescriptor boldDescriptor = FontDescriptor.createFrom( olcDirectory.getFont() ).setStyle( SWT.BOLD );
        Font boldFont = boldDescriptor.createFont( olcDirectory.getDisplay() );
        olcDirectory.setFont( boldFont );
        Composite directoryComposite = toolkit.createComposite( databaseConfigurationComposite );
        GridLayout directoryCompositeGridLayout = new GridLayout( 2, false );
        directoryCompositeGridLayout.marginHeight = directoryCompositeGridLayout.marginWidth = 0;
        directoryCompositeGridLayout.verticalSpacing = 0;
        directoryComposite.setLayout( directoryCompositeGridLayout );
        directoryComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        directoryBrowserWidget = new DirectoryBrowserWidget( "" );
        directoryBrowserWidget.createWidget( directoryComposite, toolkit );

        // Mode Text
        toolkit.createLabel( databaseConfigurationComposite, Messages.getString( "OpenLDAPMDBConfiguration.Mode" ) );
        modeUnixPermissionsWidget = new UnixPermissionsWidget();
        modeUnixPermissionsWidget.create( databaseConfigurationComposite, toolkit );
        modeUnixPermissionsWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the database indexes section.
     * It covers the following attribute :
     * <ul>
     * <li>olcDbIndex</li>
     * </ul>
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    private void createDatabaseIndexesSection( Composite parent, FormToolkit toolkit )
    {
        // Database Indices Section
        Section databaseIndexesSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseIndexesSection.setText( Messages.getString( "OpenLDAPMDBConfiguration.IndicesSection" ) );
        databaseIndexesSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite databaseIndexesComposite = toolkit.createComposite( databaseIndexesSection );
        toolkit.paintBordersFor( databaseIndexesComposite );
        databaseIndexesComposite.setLayout( new GridLayout( 2, false ) );
        databaseIndexesSection.setClient( databaseIndexesComposite );

        // Indices Widget
        indicesWidget = new TableWidget<>( new DbIndexDecorator( null, browserConnection ) );
        indicesWidget.createWidgetWithEdit( databaseIndexesComposite, toolkit );
        indicesWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        
        indicesWidget.addWidgetModifyListener( indexesListener );
    }


    /**
     * Creates the database limits section.
     * It covers the following attributes :
     * <ul>
     * <li>olcDbCheckpoint</li>
     * <li>olcDbMaxEntrySize (for OpenLDAP 2.4.41)</li>
     * <li>olcDbMaxReaders</li>
     * <li>olcDbMaxSize</li>
     * <li>olcDbSearchStack</li>
     * </ul>
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    private void createDatabaseLimitsSection( Composite parent, FormToolkit toolkit )
    {
        // Database Limits Section
        Section databaseLimitsSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseLimitsSection.setText( "Database Limits" );
        databaseLimitsSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite databaseLimitsComposite = toolkit.createComposite( databaseLimitsSection );
        toolkit.paintBordersFor( databaseLimitsComposite );
        databaseLimitsComposite.setLayout( new GridLayout( 2, false ) );
        databaseLimitsSection.setClient( databaseLimitsComposite );

        // Max Readers Text
        toolkit.createLabel( databaseLimitsComposite, "Maximum Readers:" );
        maxReadersText = BaseWidgetUtils.createIntegerText( toolkit, databaseLimitsComposite );
        maxReadersText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Max Size Text
        toolkit.createLabel( databaseLimitsComposite, "Maximum Size:" );
        maxSizeText = BaseWidgetUtils.createIntegerText( toolkit, databaseLimitsComposite );
        maxSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDbMaxEntrySize" ) )
        {
            // Max Entry Size Text
            toolkit.createLabel( databaseLimitsComposite, "Maximum Entry Size:" );
            maxEntrySizeText = BaseWidgetUtils.createIntegerText( toolkit, databaseLimitsComposite );
            maxEntrySizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        }

        // Search Stack Depth Text
        toolkit.createLabel( databaseLimitsComposite, "Search Stack Depth:" );
        searchStackDepthText = BaseWidgetUtils.createIntegerText( toolkit, databaseLimitsComposite );
        searchStackDepthText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Checkpoint Text
        toolkit.createLabel( databaseLimitsComposite, "Checkpoint Interval:" );
        checkpointText = toolkit.createText( databaseLimitsComposite, "" );
        checkpointText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the database options section. 
     * It covers the following attributes :
     * <ul>
     * <li>olcDbNoSync</li>
     * <li>olcDbEnvFlags (for OpenLDAP 2.4.33)</li>
     * </ul>
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    private void createDatabaseOptionsSection( Composite parent, FormToolkit toolkit )
    {
        // Database Options Section
        Section databaseOptionsSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseOptionsSection.setText( "Database Options" );
        databaseOptionsSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite databaseOptionsComposite = toolkit.createComposite( databaseOptionsSection );
        toolkit.paintBordersFor( databaseOptionsComposite );
        databaseOptionsComposite.setLayout( new GridLayout( 2, false ) );
        databaseOptionsSection.setClient( databaseOptionsComposite );

        // Disable Synchronous Database Writes Widget
        toolkit.createLabel( databaseOptionsComposite, "Disable Synchronous Database Writes:" );
        disableSynchronousDatabaseWritesBooleanWithDefaultWidget = new BooleanWithDefaultWidget( false );
        disableSynchronousDatabaseWritesBooleanWithDefaultWidget.create( databaseOptionsComposite, toolkit );
        disableSynchronousDatabaseWritesBooleanWithDefaultWidget.getControl().setLayoutData(
            new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Env flags here...
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        if ( database != null )
        {
            // Directory Text
            String directory = database.getOlcDbDirectory();
            directoryBrowserWidget.setDirectoryPath( ( directory == null ) ? "" : directory );

            // Mode Text
            String mode = database.getOlcDbMode();
            modeUnixPermissionsWidget.setValue( mode );

            // Indices Text
            List<DbIndexWrapper> dbIndexWrappers = new ArrayList<>();
            
            for ( String index : database.getOlcDbIndex() )
            {
                dbIndexWrappers.add( new DbIndexWrapper( index ) );
            }
            
            indicesWidget.setElements( dbIndexWrappers );

            // Max Readers Text
            Integer maxReaders = database.getOlcDbMaxReaders();
            maxReadersText.setText( ( maxReaders == null ) ? "" : maxReaders.toString() ); //$NON-NLS-1$

            // Max Size Text
            Long maxSize = database.getOlcDbMaxSize();
            maxSizeText.setText( ( maxSize == null ) ? "" : maxSize.toString() ); //$NON-NLS-1$

            // Search Stack Depth Text
            Integer searchStackDepth = database.getOlcDbSearchStack();
            searchStackDepthText.setText( ( searchStackDepth == null ) ? "" : searchStackDepth.toString() ); //$NON-NLS-1$

            // Checkpoint Text
            String checkpoint = database.getOlcDbCheckpoint();
            checkpointText.setText( ( checkpoint == null ) ? "" : checkpoint ); //$NON-NLS-1$

            // Disable Synchronous Database Writes Widget
            disableSynchronousDatabaseWritesBooleanWithDefaultWidget.setValue( database.getOlcDbNoSync() );

            // MaxEntrySize Text
            if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDbMaxEntrySize" ) )
            {
                // Max Entry Size Text
                Integer maxEntrySize = database.getOlcDbMaxEntrySize();
                
                if ( maxEntrySize != null )
                {
                    maxEntrySizeText.setText( maxEntrySize.toString() );
                }
            }
        }

        addListeners();
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        directoryBrowserWidget.addWidgetModifyListener( dirtyWidgetModifyListener );
        modeUnixPermissionsWidget.addWidgetModifyListener( dirtyWidgetModifyListener );

        indicesWidget.addWidgetModifyListener( dirtyWidgetModifyListener );

        maxReadersText.addModifyListener( dirtyModifyListener );
        maxSizeText.addModifyListener( dirtyModifyListener );

        if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDbMaxEntrySize" ) )
        {
            maxEntrySizeText.addModifyListener( dirtyModifyListener );
        }

        searchStackDepthText.addModifyListener( dirtyModifyListener );
        checkpointText.addModifyListener( dirtyModifyListener );

        disableSynchronousDatabaseWritesBooleanWithDefaultWidget.addWidgetModifyListener( dirtyWidgetModifyListener );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        directoryBrowserWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
        modeUnixPermissionsWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );

        indicesWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );

        maxReadersText.removeModifyListener( dirtyModifyListener );
        maxSizeText.removeModifyListener( dirtyModifyListener );

        if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDbMaxEntrySize" ) )
        {
            maxEntrySizeText.removeModifyListener( dirtyModifyListener );
        }

        searchStackDepthText.removeModifyListener( dirtyModifyListener );
        checkpointText.removeModifyListener( dirtyModifyListener );

        disableSynchronousDatabaseWritesBooleanWithDefaultWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        // Directory
        String directory = directoryBrowserWidget.getDirectoryPath();

        if ( Strings.isEmpty( directory ) )
        {
            database.setOlcDbDirectory( null );
        }
        else
        {
            database.setOlcDbDirectory( directory );
        }

        directoryBrowserWidget.saveDialogSettings();

        // Mode
        database.setOlcDbMode( modeUnixPermissionsWidget.getValue() );

        // Indices
        database.clearOlcDbIndex();

        for ( DbIndexWrapper dbIndexWrapper : indicesWidget.getElements() )
        {
            database.addOlcDbIndex( dbIndexWrapper.toString() );
        }

        // Max readers
        try
        {
            database.setOlcDbMaxReaders( Integer.parseInt( maxReadersText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbMaxReaders( null );
        }

        // Max Size
        try
        {
            database.setOlcDbMaxSize( Long.parseLong( maxSizeText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbMaxSize( null );
        }

        // Max Entry Size
        if ( browserConnection.getSchema().hasAttributeTypeDescription( "olcDbMaxEntrySize" ) )
        {
            try
            {
                database.setOlcDbMaxEntrySize( Integer.parseInt( maxEntrySizeText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                database.setOlcDbMaxEntrySize( null );
            }
        }

        // Search Stack Depth
        try
        {
            database.setOlcDbSearchStack( Integer.parseInt( searchStackDepthText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbSearchStack( null );
        }

        // Checkpoint Interval
        database.setOlcDbCheckpoint( checkpointText.getText() );

        // Disable Synchronous Database Writes
        database.setOlcDbNoSync( disableSynchronousDatabaseWritesBooleanWithDefaultWidget.getValue() );
    }
}
