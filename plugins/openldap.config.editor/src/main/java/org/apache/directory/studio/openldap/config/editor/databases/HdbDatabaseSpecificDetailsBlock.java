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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.apache.directory.studio.openldap.config.model.OlcBdbConfig;


/**
 * This class implements a block for HDB Database Specific Details.
 */
public class HdbDatabaseSpecificDetailsBlock implements DatabaseSpecificDetailsBlock
{
    /** The database */
    private OlcBdbConfig database;

    // UI Widgets
    private Text directoryText;
    private Text modeText;
    private Text configurationText;
    private Text cryptFileText;
    private Text cryptKeyText;
    private Text sharedMemoryKeyText;
    private Text indexesText;
    private Button linearIndexesCheckbox;
    private Text cacheSizeText;
    private Text cacheFreeText;
    private Text dnCacheSizeText;
    private Text IdlCacheSizeText;
    private Text searchStackDepthText;
    private Text pageSizeText;
    private Text checkpointIntervalText;
    private Button disableSynchronousDatabaseWritesCheckbox;
    private Button allowReadsOfUncommitedDataCheckbox;
    private Text deadlockDetectionAlgorithmText;


    /**
     * Creates a new instance of BdbDatabaseSpecificDetailsBlock.
     *
     * @param database the database
     */
    public HdbDatabaseSpecificDetailsBlock( OlcBdbConfig database )
    {
        this.database = database;
    }


    /**
     * {@inheritDoc}
     */
    public void createFormContent( Composite parent, FormToolkit toolkit )
    {
        createDatabaseConfigurationSection( parent, toolkit );
        createDatabaseIndexesSection( parent, toolkit );
        createDatabaseCacheSection( parent, toolkit );
        createDatabaseLimitsSection( parent, toolkit );
        createDatabaseOptionsSection( parent, toolkit );

    }


    private void createDatabaseConfigurationSection( Composite parent, FormToolkit toolkit )
    {
        // Database Configuration Section
        Section databaseConfigurationSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseConfigurationSection.setText( "Database Configuration" );
        databaseConfigurationSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        Composite databaseConfigurationComposite = toolkit.createComposite( databaseConfigurationSection );
        toolkit.paintBordersFor( databaseConfigurationComposite );
        databaseConfigurationComposite.setLayout( new GridLayout( 2, false ) );
        databaseConfigurationSection.setClient( databaseConfigurationComposite );

        // Directory Text
        toolkit.createLabel( databaseConfigurationComposite, "Directory:" );
        directoryText = toolkit.createText( databaseConfigurationComposite, "" );
        directoryText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Mode Text
        toolkit.createLabel( databaseConfigurationComposite, "Mode:" );
        modeText = toolkit.createText( databaseConfigurationComposite, "" );
        modeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Configuration Text
        toolkit.createLabel( databaseConfigurationComposite, "Configuration:" );
        configurationText = toolkit.createText( databaseConfigurationComposite, "" );
        configurationText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Crypt File Text
        toolkit.createLabel( databaseConfigurationComposite, "Crypt File:" );
        cryptFileText = toolkit.createText( databaseConfigurationComposite, "" );
        cryptFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Crypt Key Text
        toolkit.createLabel( databaseConfigurationComposite, "Crypt Key:" );
        cryptKeyText = toolkit.createText( databaseConfigurationComposite, "" );
        cryptKeyText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Shared Memory Key Text
        toolkit.createLabel( databaseConfigurationComposite, "Shared Memory Key:" );
        sharedMemoryKeyText = toolkit.createText( databaseConfigurationComposite, "" );
        sharedMemoryKeyText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    private void createDatabaseIndexesSection( Composite parent, FormToolkit toolkit )
    {
        // Database Indexes Section
        Section databaseIndexesSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseIndexesSection.setText( "Database Indexes" );
        databaseIndexesSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        Composite databaseIndexesComposite = toolkit.createComposite( databaseIndexesSection );
        toolkit.paintBordersFor( databaseIndexesComposite );
        databaseIndexesComposite.setLayout( new GridLayout( 2, false ) );
        databaseIndexesSection.setClient( databaseIndexesComposite );

        // Indexes Text
        toolkit.createLabel( databaseIndexesComposite, "Indexes:" );
        indexesText = toolkit.createText( databaseIndexesComposite, "" );
        indexesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Linear Indexes Button
        linearIndexesCheckbox = toolkit.createButton( databaseIndexesComposite, "Linear Indexes", SWT.CHECK );
        linearIndexesCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
    }


    private void createDatabaseCacheSection( Composite parent, FormToolkit toolkit )
    {
        // Database Cache Section
        Section databaseCacheSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseCacheSection.setText( "Database Cache" );
        databaseCacheSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        Composite databaseCacheComposite = toolkit.createComposite( databaseCacheSection );
        toolkit.paintBordersFor( databaseCacheComposite );
        databaseCacheComposite.setLayout( new GridLayout( 2, false ) );
        databaseCacheSection.setClient( databaseCacheComposite );

        // Cache Size Text
        toolkit.createLabel( databaseCacheComposite, "Cache Size:" );
        cacheSizeText = toolkit.createText( databaseCacheComposite, "" );
        cacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Cache Free Text
        toolkit.createLabel( databaseCacheComposite, "Cache Free:" );
        cacheFreeText = toolkit.createText( databaseCacheComposite, "" );
        cacheFreeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // DN Cache Size Text
        toolkit.createLabel( databaseCacheComposite, "DN Cache Size:" );
        dnCacheSizeText = toolkit.createText( databaseCacheComposite, "" );
        dnCacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // IDL Cache Size Text
        toolkit.createLabel( databaseCacheComposite, "IDL Cache Size:" );
        IdlCacheSizeText = toolkit.createText( databaseCacheComposite, "" );
        IdlCacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    private void createDatabaseLimitsSection( Composite parent, FormToolkit toolkit )
    {
        // Database Limits Section
        Section databaseLimitsSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseLimitsSection.setText( "Database Limits" );
        databaseLimitsSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        Composite databaseLimitsComposite = toolkit.createComposite( databaseLimitsSection );
        toolkit.paintBordersFor( databaseLimitsComposite );
        databaseLimitsComposite.setLayout( new GridLayout( 2, false ) );
        databaseLimitsSection.setClient( databaseLimitsComposite );

        // Search Stack Depth Text
        toolkit.createLabel( databaseLimitsComposite, "Search Stack Depth:" );
        searchStackDepthText = toolkit.createText( databaseLimitsComposite, "" );
        searchStackDepthText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Page Size Text
        toolkit.createLabel( databaseLimitsComposite, "Page Size:" );
        pageSizeText = toolkit.createText( databaseLimitsComposite, "" );
        pageSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Checkpoint Interval Text
        toolkit.createLabel( databaseLimitsComposite, "Checkpoint Interval:" );
        checkpointIntervalText = toolkit.createText( databaseLimitsComposite, "" );
        checkpointIntervalText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    private void createDatabaseOptionsSection( Composite parent, FormToolkit toolkit )
    {
        // Database Options Section
        Section databaseOptionsSection = toolkit.createSection( parent, Section.TWISTIE );
        databaseOptionsSection.setText( "Database Options" );
        databaseOptionsSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        Composite databaseOptionsComposite = toolkit.createComposite( databaseOptionsSection );
        toolkit.paintBordersFor( databaseOptionsComposite );
        databaseOptionsComposite.setLayout( new GridLayout( 2, false ) );
        databaseOptionsSection.setClient( databaseOptionsComposite );

        // Disable Synchronous Database Writes Button
        disableSynchronousDatabaseWritesCheckbox = toolkit.createButton( databaseOptionsComposite,
            "Disable Synchronous Database Writes", SWT.CHECK );
        disableSynchronousDatabaseWritesCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Allow Reads Of Uncommited Data Button
        allowReadsOfUncommitedDataCheckbox = toolkit.createButton( databaseOptionsComposite,
            "Allow Reads Of Uncommited Data",
            SWT.CHECK );
        allowReadsOfUncommitedDataCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Deadlock Detection Algorithm Text
        toolkit.createLabel( databaseOptionsComposite, "Deadlock Detection Algorithm:" );
        deadlockDetectionAlgorithmText = toolkit.createText( databaseOptionsComposite, "" );
        deadlockDetectionAlgorithmText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        if ( database == null )
        {
            // Blank out all fields
            // TODO
        }
        else
        {
            // Directory Text
            String directory = database.getOlcDbDirectory();
            directoryText.setText( ( directory == null ) ? "" : directory ); //$NON-NLS-1$

            // Mode Text
            String mode = database.getOlcDbMode();
            modeText.setText( ( mode == null ) ? "" : mode ); //$NON-NLS-1$

            // Configuration Text
            List<String> configuration = database.getOlcDbConfig();
            configurationText.setText( ( configuration == null ) ? "" : concatenate( configuration ) ); //$NON-NLS-1$

            // Crypt File Text
            String cryptFile = database.getOlcDbCryptFile();
            cryptFileText.setText( ( cryptFile == null ) ? "" : cryptFile ); //$NON-NLS-1$

            // Crypt Key Text
            byte[] cryptKey = database.getOlcDbCryptKey();
            modeText.setText( ( cryptKey == null ) ? "" : new String( cryptKey ) ); //$NON-NLS-1$ TODO

            // Shared Memory Key Text
            int sharedMemoryKey = database.getOlcDbShmKey();
            sharedMemoryKeyText.setText( "" + sharedMemoryKey ); //$NON-NLS-1$ TODO

            // Indexes Text
            List<String> indexes = database.getOlcDbIndex();
            indexesText.setText( ( indexes == null ) ? "" : concatenate( indexes ) ); //$NON-NLS-1$

            // Linear Indexes Button
            boolean linearIndexes = database.getOlcDbLinearIndex();
            linearIndexesCheckbox.setSelection( linearIndexes );

            // Cache Size Text
            int cacheSize = database.getOlcDbCacheSize();
            cacheSizeText.setText( "" + cacheSize ); //$NON-NLS-1$ TODO

            // Cache Free Text
            int cacheFree = database.getOlcDbCacheFree();
            cacheFreeText.setText( "" + cacheFree ); //$NON-NLS-1$ TODO

            // DN Cache Size Text
            int dnCacheSize = database.getOlcDbDNcacheSize();
            dnCacheSizeText.setText( "" + dnCacheSize ); //$NON-NLS-1$ TODO

            // IDL Cache Size Text
            int idlCacheSize = database.getOlcDbIDLcacheSize();
            IdlCacheSizeText.setText( "" + idlCacheSize ); //$NON-NLS-1$ TODO

            // Search Stack Depth Text
            int searchStackDepth = database.getOlcDbSearchStack();
            searchStackDepthText.setText( "" + searchStackDepth ); //$NON-NLS-1$ TODO

            // Page Size Text
            List<String> pageSize = database.getOlcDbPageSize();
            pageSizeText.setText( ( pageSize == null ) ? "" : concatenate( pageSize ) ); //$NON-NLS-1$

            // Checkpoint Interval Text
            String checkpointInterval = database.getOlcDbCheckpoint();
            checkpointIntervalText.setText( ( checkpointInterval == null ) ? "" : checkpointInterval ); //$NON-NLS-1$

            // Disable Synchronous Database Writes Button
            boolean disableSynchronousDatabaseWrites = database.getOlcDbNoSync();
            disableSynchronousDatabaseWritesCheckbox.setSelection( disableSynchronousDatabaseWrites );

            // Allow Reads Of Uncommited Data Button
            boolean allowReadsOfUncommitedData = database.getOlcDbDirtyRead();
            allowReadsOfUncommitedDataCheckbox.setSelection( allowReadsOfUncommitedData );

            // Deadlock Detection Algorithm Text
            String deadlockDetectionAlgorithm = database.getOlcDbLockDetect();
            deadlockDetectionAlgorithmText
                .setText( ( deadlockDetectionAlgorithm == null ) ? "" : deadlockDetectionAlgorithm ); //$NON-NLS-1$
        }
    }


    private String concatenate( List<String> list )
    {
        StringBuilder sb = new StringBuilder();

        for ( String string : list )
        {
            sb.append( string );
            sb.append( ", " );
        }

        if ( sb.length() > 1 )
        {
            sb.deleteCharAt( sb.length() - 1 );
            sb.deleteCharAt( sb.length() - 1 );
        }

        return sb.toString();
    }
}
