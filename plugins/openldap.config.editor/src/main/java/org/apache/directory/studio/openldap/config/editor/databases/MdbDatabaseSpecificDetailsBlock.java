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


import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.apache.directory.studio.openldap.common.ui.widgets.BooleanWithDefaultWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.DirectoryBrowserWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.UnixPermissionsWidget;
import org.apache.directory.studio.openldap.config.model.database.OlcMdbConfig;
import org.apache.directory.studio.openldap.config.model.widgets.IndicesWidget;


/**
 * This class implements a block for Memory-Mapped DB Specific Details.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MdbDatabaseSpecificDetailsBlock extends AbstractDatabaseSpecificDetailsBlock<OlcMdbConfig>
{
    // UI Widgets
    private DirectoryBrowserWidget directoryBrowserWidget;
    private UnixPermissionsWidget modeUnixPermissionsWidget;
    private IndicesWidget indicesWidget;
    private Text maxReadersText;
    private Text maxSizeText;
    private Text searchStackDepthText;
    private Text checkpointText;
    private BooleanWithDefaultWidget disableSynchronousDatabaseWritesBooleanWithDefaultWidget;


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
        indicesWidget.createWidget( databaseIndexesComposite, toolkit );
        indicesWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
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

        // Max Readers Text
        toolkit.createLabel( databaseLimitsComposite, "Maximum Readers:" );
        maxReadersText = createIntegerText( toolkit, databaseLimitsComposite );
        maxReadersText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Max Size Text
        toolkit.createLabel( databaseLimitsComposite, "Maximum Size:" );
        maxSizeText = createIntegerText( toolkit, databaseLimitsComposite );
        maxSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Search Stack Depth Text
        toolkit.createLabel( databaseLimitsComposite, "Search Stack Depth:" );
        searchStackDepthText = createIntegerText( toolkit, databaseLimitsComposite );
        searchStackDepthText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

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
            indicesWidget.setIndices( database.getOlcDbIndex() );

            // Max Readers Text
            Integer maxReaders = database.getOlcDbMaxReaders();
            maxReadersText.setText( ( maxReaders == null ) ? "" : "" + maxReaders ); //$NON-NLS-1$

            // Max Size Text
            Long maxSize = database.getOlcDbMaxSize();
            maxSizeText.setText( ( maxSize == null ) ? "" : "" + maxSize ); //$NON-NLS-1$

            // Search Stack Depth Text
            Integer searchStackDepth = database.getOlcDbSearchStack();
            searchStackDepthText.setText( ( searchStackDepth == null ) ? "" : "" + searchStackDepth ); //$NON-NLS-1$

            // Checkpoint Text
            String checkpoint = database.getOlcDbCheckpoint();
            checkpointText.setText( ( checkpoint == null ) ? "" : checkpoint ); //$NON-NLS-1$

            // Disable Synchronous Database Writes Widget
            disableSynchronousDatabaseWritesBooleanWithDefaultWidget.setValue( database.getOlcDbNoSync() );
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
        searchStackDepthText.removeModifyListener( dirtyModifyListener );
        checkpointText.removeModifyListener( dirtyModifyListener );

        disableSynchronousDatabaseWritesBooleanWithDefaultWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
    }


    /**
     * Creates a Text that can be used to enter an integer.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that can be used to enter a port number
     */
    protected Text createIntegerText( FormToolkit toolkit, Composite parent )
    {
        Text integerText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        integerText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        return integerText;
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

        // Indices Widget
        database.clearOlcDbIndex();
        for ( String index : indicesWidget.getIndices() )
        {
            database.addOlcDbIndex( index );
        }

        // Cache Size Text
        try
        {
            database.setOlcDbMaxReaders( Integer.parseInt( maxReadersText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbMaxReaders( null );
        }

        // Cache Free Text
        try
        {
            database.setOlcDbMaxSize( Long.parseLong( maxSizeText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            database.setOlcDbMaxSize( null );
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

        // Checkpoint Interval Text
        database.setOlcDbCheckpoint( checkpointText.getText() );

        // Disable Synchronous Database Writes Widget
        database.setOlcDbNoSync( disableSynchronousDatabaseWritesBooleanWithDefaultWidget.getValue() );
    }
}
