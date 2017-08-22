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
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.apache.directory.studio.openldap.common.ui.widgets.BooleanWithDefaultWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.DirectoryBrowserWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.FileBrowserWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.UnixPermissionsWidget;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;
import org.apache.directory.studio.openldap.config.editor.dialogs.DbConfigurationDialog;
import org.apache.directory.studio.openldap.config.model.database.OlcBdbConfig;
import org.apache.directory.studio.openldap.config.model.database.OlcBdbConfigLockDetectEnum;
import org.apache.directory.studio.openldap.config.model.widgets.IndicesWidget;
import org.apache.directory.studio.openldap.config.model.widgets.LockDetectWidget;


/**
 * This class implements a block for Berkeley DB Database Specific Details.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BerkeleyDbDatabaseSpecificDetailsBlock<BDB extends OlcBdbConfig> extends
    AbstractDatabaseSpecificDetailsBlock<BDB>
{
    // UI Widgets
    private DirectoryBrowserWidget directoryBrowserWidget;
    private UnixPermissionsWidget modeUnixPermissionsWidget;
    private Button editConfigurationButton;
    private FileBrowserWidget cryptFileBrowserWidget;
    private Text cryptKeyText;
    private Text sharedMemoryKeyText;
    private IndicesWidget indicesWidget;
    private BooleanWithDefaultWidget linearIndexBooleanWithDefaultWidget;
    private Text cacheSizeText;
    private Text cacheFreeText;
    private Text dnCacheSizeText;
    private Text idlCacheSizeText;
    private Text searchStackDepthText;
    private Text pageSizeText;
    private Text checkpointText;
    private BooleanWithDefaultWidget disableSynchronousDatabaseWritesBooleanWithDefaultWidget;
    private BooleanWithDefaultWidget allowReadsOfUncommitedDataBooleanWithDefaultWidget;
    private LockDetectWidget lockDetectWidget;

    // Listeners
    private SelectionListener editConfigurationButtonSelectionListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            DbConfigurationDialog dialog = new DbConfigurationDialog( editConfigurationButton.getShell(),
                database.getOlcDbConfig().toArray( new String[0] ) );
            
            if ( dialog.open() == DbConfigurationDialog.OK )
            {
                List<String> newConfiguration = new ArrayList<>();

                String[] configurationFromDialog = dialog.getConfiguration();

                if ( configurationFromDialog.length > 0 )
                {
                    for ( String configurationLineFromDialog : configurationFromDialog )
                    {
                        newConfiguration.add( configurationLineFromDialog );
                    }
                }

                database.setOlcDbConfig( newConfiguration );
                detailsPage.setEditorDirty();
            }
        }
    };


    /**
     * Creates a new instance of BdbDatabaseSpecificDetailsBlock.
     * 
     * @param databaseDetailsPage the database details page 
     * @param database the database
     * @param browserConnection the connection
     */
    public BerkeleyDbDatabaseSpecificDetailsBlock( DatabasesDetailsPage detailsPage, BDB database,
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
        createDatabaseCacheSection( composite, toolkit );
        createDatabaseLimitsSection( composite, toolkit );
        createDatabaseOptionsSection( composite, toolkit );

        return composite;
    }


    /**
     * Creates the database configuration section.
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    private void createDatabaseConfigurationSection( Composite parent, FormToolkit toolkit )
    {
        // Database Configuration Section
        Section databaseConfigurationSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseConfigurationSection.setText( "Database Configuration" );
        databaseConfigurationSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite databaseConfigurationComposite = toolkit.createComposite( databaseConfigurationSection );
        toolkit.paintBordersFor( databaseConfigurationComposite );
        databaseConfigurationComposite.setLayout( new GridLayout( 2, false ) );
        databaseConfigurationSection.setClient( databaseConfigurationComposite );

        // Directory Text
        toolkit.createLabel( databaseConfigurationComposite, "Directory:" );
        Composite directoryComposite = toolkit.createComposite( databaseConfigurationComposite );
        GridLayout directoryCompositeGridLayout = new GridLayout( 2, false );
        directoryCompositeGridLayout.marginHeight = directoryCompositeGridLayout.marginWidth = 0;
        directoryCompositeGridLayout.verticalSpacing = 0;
        directoryComposite.setLayout( directoryCompositeGridLayout );
        directoryComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        directoryBrowserWidget = new DirectoryBrowserWidget( "" );
        directoryBrowserWidget.createWidget( directoryComposite, toolkit );

        // Mode Text
        toolkit.createLabel( databaseConfigurationComposite, "Mode:" );
        modeUnixPermissionsWidget = new UnixPermissionsWidget();
        modeUnixPermissionsWidget.create( databaseConfigurationComposite, toolkit );
        modeUnixPermissionsWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Configuration Text
        toolkit.createLabel( databaseConfigurationComposite, "Configuration:" );
        editConfigurationButton = toolkit.createButton( databaseConfigurationComposite, "Edit Configuration...",
            SWT.PUSH );
        editConfigurationButton.addSelectionListener( editConfigurationButtonSelectionListener );

        // Crypt File Text
        toolkit.createLabel( databaseConfigurationComposite, "Crypt File:" );
        Composite cryptFileComposite = toolkit.createComposite( databaseConfigurationComposite );
        GridLayout cryptFileCompositeGridLayout = new GridLayout( 2, false );
        cryptFileCompositeGridLayout.marginHeight = cryptFileCompositeGridLayout.marginWidth = 0;
        cryptFileCompositeGridLayout.verticalSpacing = 0;
        cryptFileComposite.setLayout( cryptFileCompositeGridLayout );
        cryptFileComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        cryptFileBrowserWidget = new FileBrowserWidget( "", new String[0],
            FileBrowserWidget.TYPE_OPEN );
        cryptFileBrowserWidget.createWidget( cryptFileComposite, toolkit );

        // Crypt Key Text
        toolkit.createLabel( databaseConfigurationComposite, "Crypt Key:" );
        cryptKeyText = toolkit.createText( databaseConfigurationComposite, "" );
        cryptKeyText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Shared Memory Key Text
        toolkit.createLabel( databaseConfigurationComposite, "Shared Memory Key:" );
        sharedMemoryKeyText = BaseWidgetUtils.createIntegerText( toolkit, databaseConfigurationComposite );
        sharedMemoryKeyText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the database indexes section.
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    private void createDatabaseIndexesSection( Composite parent, FormToolkit toolkit )
    {
        // Database Indices Section
        Section databaseIndexesSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseIndexesSection.setText( "Database Indices" );
        databaseIndexesSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite databaseIndexesComposite = toolkit.createComposite( databaseIndexesSection );
        toolkit.paintBordersFor( databaseIndexesComposite );
        databaseIndexesComposite.setLayout( new GridLayout( 2, false ) );
        databaseIndexesSection.setClient( databaseIndexesComposite );

        // Indices Widget
        indicesWidget = new IndicesWidget( browserConnection );
        indicesWidget.createWidgetWithEdit( databaseIndexesComposite, toolkit );
        indicesWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Linear Indexes Widget
        toolkit.createLabel( databaseIndexesComposite, "Linear Index:" );
        linearIndexBooleanWithDefaultWidget = new BooleanWithDefaultWidget( false );
        linearIndexBooleanWithDefaultWidget.create( databaseIndexesComposite, toolkit );
        linearIndexBooleanWithDefaultWidget.getControl().setLayoutData(
            new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the database cache section.
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    private void createDatabaseCacheSection( Composite parent, FormToolkit toolkit )
    {
        // Database Cache Section
        Section databaseCacheSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseCacheSection.setText( "Database Cache" );
        databaseCacheSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite databaseCacheComposite = toolkit.createComposite( databaseCacheSection );
        toolkit.paintBordersFor( databaseCacheComposite );
        databaseCacheComposite.setLayout( new GridLayout( 2, false ) );
        databaseCacheSection.setClient( databaseCacheComposite );

        // Cache Size Text
        toolkit.createLabel( databaseCacheComposite, "Cache Size:" );
        cacheSizeText = BaseWidgetUtils.createIntegerText( toolkit, databaseCacheComposite );
        cacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Cache Free Text
        toolkit.createLabel( databaseCacheComposite, "Cache Free:" );
        cacheFreeText = BaseWidgetUtils.createIntegerText( toolkit, databaseCacheComposite );
        cacheFreeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // DN Cache Size Text
        toolkit.createLabel( databaseCacheComposite, "DN Cache Size:" );
        dnCacheSizeText = BaseWidgetUtils.createIntegerText( toolkit, databaseCacheComposite );
        dnCacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // IDL Cache Size Text
        toolkit.createLabel( databaseCacheComposite, "IDL Cache Size:" );
        idlCacheSizeText = BaseWidgetUtils.createIntegerText( toolkit, databaseCacheComposite );
        idlCacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the database limits section.
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

        // Search Stack Depth Text
        toolkit.createLabel( databaseLimitsComposite, "Search Stack Depth:" );
        searchStackDepthText = BaseWidgetUtils.createIntegerText( toolkit, databaseLimitsComposite );
        searchStackDepthText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Page Size Text
        toolkit.createLabel( databaseLimitsComposite, "Page Size:" );
        pageSizeText = toolkit.createText( databaseLimitsComposite, "" );
        pageSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Checkpoint Text
        toolkit.createLabel( databaseLimitsComposite, "Checkpoint Interval:" );
        checkpointText = toolkit.createText( databaseLimitsComposite, "" );
        checkpointText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the database options section.
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

        // Allow Reads Of Uncommited Data Widget
        toolkit.createLabel( databaseOptionsComposite, "Allow Reads Of Uncommited Data:" );
        allowReadsOfUncommitedDataBooleanWithDefaultWidget = new BooleanWithDefaultWidget( false );
        allowReadsOfUncommitedDataBooleanWithDefaultWidget.create( databaseOptionsComposite, toolkit );
        allowReadsOfUncommitedDataBooleanWithDefaultWidget.getControl().setLayoutData(
            new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Deadlock Detection Algorithm Widget
        toolkit.createLabel( databaseOptionsComposite, "Deadlock Detection Algorithm:" );
        lockDetectWidget = new LockDetectWidget();
        lockDetectWidget.createWidget( databaseOptionsComposite );
        lockDetectWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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

            // Crypt File Text
            String cryptFile = database.getOlcDbCryptFile();
            cryptFileBrowserWidget.setFilename( ( cryptFile == null ) ? "" : cryptFile );

            // Crypt Key Text
            byte[] cryptKey = database.getOlcDbCryptKey();
            cryptKeyText.setText( ( cryptKey == null ) ? "" : new String( cryptKey ) ); //$NON-NLS-1$

            // Shared Memory Key Text
            Integer sharedMemoryKey = database.getOlcDbShmKey();
            sharedMemoryKeyText.setText( ( sharedMemoryKey == null ) ? "" : "" + sharedMemoryKey ); //$NON-NLS-1$

            // Indices Text
            //indicesWidget.setIndices( database.getOlcDbIndex() );

            // Linear Index Widget
            linearIndexBooleanWithDefaultWidget.setValue( database.getOlcDbLinearIndex() );

            // Cache Size Text
            Integer cacheSize = database.getOlcDbCacheSize();
            cacheSizeText.setText( ( cacheSize == null ) ? "" : "" + cacheSize ); //$NON-NLS-1$

            // Cache Free Text
            Integer cacheFree = database.getOlcDbCacheFree();
            cacheFreeText.setText( ( cacheFree == null ) ? "" : "" + cacheFree ); //$NON-NLS-1$

            // DN Cache Size Text
            Integer dnCacheSize = database.getOlcDbDNcacheSize();
            dnCacheSizeText.setText( ( dnCacheSize == null ) ? "" : "" + dnCacheSize ); //$NON-NLS-1$

            // IDL Cache Size Text
            Integer idlCacheSize = database.getOlcDbIDLcacheSize();
            idlCacheSizeText.setText( ( idlCacheSize == null ) ? "" : "" + idlCacheSize ); //$NON-NLS-1$

            // Search Stack Depth Text
            Integer searchStackDepth = database.getOlcDbSearchStack();
            searchStackDepthText.setText( ( searchStackDepth == null ) ? "" : "" + searchStackDepth ); //$NON-NLS-1$

            // Page Size Text
            List<String> pageSize = database.getOlcDbPageSize();
            pageSizeText.setText( ( pageSize == null ) ? "" : OpenLdapConfigurationPluginUtils.concatenate( pageSize ) ); //$NON-NLS-1$

            // Checkpoint Text
            String checkpoint = database.getOlcDbCheckpoint();
            checkpointText.setText( ( checkpoint == null ) ? "" : checkpoint ); //$NON-NLS-1$

            // Disable Synchronous Database Writes Widget
            disableSynchronousDatabaseWritesBooleanWithDefaultWidget.setValue( database.getOlcDbNoSync() );

            // Allow Reads Of Uncommited Data Widget
            allowReadsOfUncommitedDataBooleanWithDefaultWidget.setValue( database.getOlcDbDirtyRead() );

            // Deadlock Detection Algorithm Text
            lockDetectWidget.setValue( OlcBdbConfigLockDetectEnum.fromString( database.getOlcDbLockDetect() ) );
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
        editConfigurationButton.addSelectionListener( editConfigurationButtonSelectionListener );
        cryptFileBrowserWidget.addWidgetModifyListener( dirtyWidgetModifyListener );
        cryptKeyText.addModifyListener( dirtyModifyListener );
        sharedMemoryKeyText.addModifyListener( dirtyModifyListener );

        indicesWidget.addWidgetModifyListener( dirtyWidgetModifyListener );
        linearIndexBooleanWithDefaultWidget.addWidgetModifyListener( dirtyWidgetModifyListener );

        cacheSizeText.addModifyListener( dirtyModifyListener );
        cacheFreeText.addModifyListener( dirtyModifyListener );
        dnCacheSizeText.addModifyListener( dirtyModifyListener );
        idlCacheSizeText.addModifyListener( dirtyModifyListener );

        searchStackDepthText.addModifyListener( dirtyModifyListener );
        pageSizeText.addModifyListener( dirtyModifyListener );
        checkpointText.addModifyListener( dirtyModifyListener );

        disableSynchronousDatabaseWritesBooleanWithDefaultWidget.addWidgetModifyListener( dirtyWidgetModifyListener );
        allowReadsOfUncommitedDataBooleanWithDefaultWidget.addWidgetModifyListener( dirtyWidgetModifyListener );
        lockDetectWidget.addWidgetModifyListener( dirtyWidgetModifyListener );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        directoryBrowserWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
        modeUnixPermissionsWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
        editConfigurationButton.removeSelectionListener( editConfigurationButtonSelectionListener );
        cryptFileBrowserWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
        cryptKeyText.removeModifyListener( dirtyModifyListener );
        sharedMemoryKeyText.removeModifyListener( dirtyModifyListener );

        indicesWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
        linearIndexBooleanWithDefaultWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );

        cacheSizeText.removeModifyListener( dirtyModifyListener );
        cacheFreeText.removeModifyListener( dirtyModifyListener );
        dnCacheSizeText.removeModifyListener( dirtyModifyListener );
        idlCacheSizeText.removeModifyListener( dirtyModifyListener );

        searchStackDepthText.removeModifyListener( dirtyModifyListener );
        pageSizeText.removeModifyListener( dirtyModifyListener );
        checkpointText.removeModifyListener( dirtyModifyListener );

        disableSynchronousDatabaseWritesBooleanWithDefaultWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
        allowReadsOfUncommitedDataBooleanWithDefaultWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
        lockDetectWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        // Directory Text
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

        // Mode Text
        database.setOlcDbMode( modeUnixPermissionsWidget.getValue() );

        // Crypt File Text
        String cryptFile = cryptFileBrowserWidget.getFilename();

        if ( Strings.isEmpty( cryptFile ) )
        {
            database.setOlcDbCryptFile( null );
        }
        else
        {
            database.setOlcDbCryptFile( cryptFile );
        }

        cryptFileBrowserWidget.saveDialogSettings();

        // Crypt Key Text
        String cryptKey = cryptKeyText.getText();

        if ( Strings.isEmpty( cryptKey ) )
        {
            database.setOlcDbCryptKey( null );
        }
        else
        {
            database.setOlcDbCryptKey( cryptKey.getBytes() );
        }

        // Shared Memory Key Text
        try
        {
            database.setOlcDbShmKey( Integer.parseInt( sharedMemoryKeyText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbShmKey( null );
        }

        // Indices Widget
        database.clearOlcDbIndex();
        /*
        for ( String index : indicesWidget.getIndices() )
        {
            database.addOlcDbIndex( index );
        }
        */

        // Linear Index Widget
        database.setOlcDbLinearIndex( linearIndexBooleanWithDefaultWidget.getValue() );

        // Cache Size Text
        try
        {
            database.setOlcDbCacheSize( Integer.parseInt( cacheSizeText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbCacheSize( null );
        }

        // Cache Free Text
        try
        {
            database.setOlcDbCacheFree( Integer.parseInt( cacheFreeText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbCacheFree( null );
        }

        // DN Cache Size Text
        try
        {
            database.setOlcDbDNcacheSize( Integer.parseInt( dnCacheSizeText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbDNcacheSize( null );
        }

        // IDL Cache Size Text
        try
        {
            database.setOlcDbIDLcacheSize( Integer.parseInt( idlCacheSizeText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbIDLcacheSize( null );
        }

        // Search Stack Depth Text
        try
        {
            database.setOlcDbSearchStack( Integer.parseInt( searchStackDepthText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbSearchStack( null );
        }

        // Page Size Text
        //TODO

        // Checkpoint Text
        database.setOlcDbCheckpoint( checkpointText.getText() );

        // Disable Synchronous Database Writes Widget
        database.setOlcDbNoSync( disableSynchronousDatabaseWritesBooleanWithDefaultWidget.getValue() );

        // Allow Reads Of Uncommited Data Widget
        database.setOlcDbDirtyRead( allowReadsOfUncommitedDataBooleanWithDefaultWidget.getValue() );

        // Deadlock Detection Algorithm Text
        OlcBdbConfigLockDetectEnum lockDetect = lockDetectWidget.getValue();

        if ( lockDetect != null )
        {
            database.setOlcDbLockDetect( lockDetect.toString() );
        }
        else
        {
            database.setOlcDbLockDetect( null );
        }
    }
}
