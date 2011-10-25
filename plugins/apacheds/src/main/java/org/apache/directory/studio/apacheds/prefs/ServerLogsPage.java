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
package org.apache.directory.studio.apacheds.prefs;


import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * This class implements the Servers Logs preference page for the ApacheDS plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerLogsPage extends PreferencePage implements IWorkbenchPreferencePage
{
    // UI fields
    private Combo levelCombo;
    private Text patternText;
    private Label previewLabel;
    private Group conversionPatternGroup;


    /**
     * Creates a new instance of ServerLogsPage.
     */
    public ServerLogsPage()
    {
        super( Messages.getString( "ServerLogsPage.ServerLogs" ) ); //$NON-NLS-1$
        setPreferenceStore( ApacheDsPlugin.getDefault().getPreferenceStore() );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        // Composite
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Colors And Fonts Link
        Link colorsAndFontsLink = new Link( composite, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.BEGINNING, true, false );
        gd.widthHint = 150;
        colorsAndFontsLink.setLayoutData( gd );
        colorsAndFontsLink.setText( Messages.getString( "ServerLogsPage.DefaultColorsAndFontsSettings" ) ); //$NON-NLS-1$
        colorsAndFontsLink.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                PreferencesUtil.createPreferenceDialogOn( getShell(), ApacheDsPlugin.getDefault().getPluginProperties()
                    .getString( "Pref_ColorsAndFontsPage_id" ), null, null ); //$NON-NLS-1$
            }
        } );

        // Log Level Group
        Group logLevelGroup = new Group( composite, SWT.NONE );
        logLevelGroup.setText( Messages.getString( "ServerLogsPage.LogLevel" ) ); //$NON-NLS-1$
        logLevelGroup.setLayout( new GridLayout( 2, false ) );
        logLevelGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Level Label
        Label levelLabel = new Label( logLevelGroup, SWT.NONE );
        levelLabel.setText( Messages.getString( "ServerLogsPage.Level" ) ); //$NON-NLS-1$

        //  Level Combo
        levelCombo = new Combo( logLevelGroup, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE );
        levelCombo.add( Messages.getString( "ServerLogsPage.Debug" ), 0 ); //$NON-NLS-1$
        levelCombo.add( Messages.getString( "ServerLogsPage.Info" ), 1 ); //$NON-NLS-1$
        levelCombo.add( Messages.getString( "ServerLogsPage.Warning" ), 2 ); //$NON-NLS-1$
        levelCombo.add( Messages.getString( "ServerLogsPage.Error" ), 3 ); //$NON-NLS-1$
        levelCombo.add( Messages.getString( "ServerLogsPage.Fatal" ), 4 ); //$NON-NLS-1$

        // Warning Label
        new Label( logLevelGroup, SWT.NONE ); // Filler
        Label warningLabel = new Label( logLevelGroup, SWT.WRAP );
        warningLabel.setText( Messages.getString( "ServerLogsPage.WarnLogLevel" ) ); //$NON-NLS-1$
        warningLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Conversion Pattern Group
        conversionPatternGroup = new Group( composite, SWT.NONE );
        conversionPatternGroup.setText( Messages.getString( "ServerLogsPage.ConverstionPattern" ) ); //$NON-NLS-1$
        conversionPatternGroup.setLayout( new GridLayout( 2, false ) );
        conversionPatternGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Pattern Label
        Label patternLabel = new Label( conversionPatternGroup, SWT.NONE );
        patternLabel.setText( Messages.getString( "ServerLogsPage.Pattern" ) ); //$NON-NLS-1$

        // Pattern Text
        patternText = new Text( conversionPatternGroup, SWT.BORDER );
        patternText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        patternText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updatePreviewLabel();
            }
        } );

        // Preview
        Label label = new Label( conversionPatternGroup, SWT.NONE );
        label.setText( Messages.getString( "ServerLogsPage.Preview" ) ); //$NON-NLS-1$
        previewLabel = new Label( conversionPatternGroup, SWT.NONE );
        previewLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initFromPreferences();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Updates the preview label.
     */
    private void updatePreviewLabel()
    {
        LoggingEvent loggingEvent = new LoggingEvent(
            Messages.getString( "ServerLogsPage.CategoryClass" ), Logger.getLogger( "logger" ), Level.INFO, //$NON-NLS-1$ //$NON-NLS-2$
            Messages.getString( "ServerLogsPage.LoggingMessage" ), new Exception( Messages.getString( "ServerLogsPage.AnException" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        previewLabel.setText( new PatternLayout( patternText.getText() ).format( loggingEvent ) );
    }


    /**
     * Initializes the UI fields from the preferences values.
     */
    private void initFromPreferences()
    {
        IPreferenceStore store = getPreferenceStore();

        // Level
        String level = store.getString( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL );
        if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_DEBUG.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 0 );
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_INFO.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 1 );
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_WARN.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 2 );
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_ERROR.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 3 );
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_FATAL.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 4 );
        }

        // Pattern
        patternText.setText( store.getString( ApacheDsPluginConstants.PREFS_SERVER_LOGS_PATTERN ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        IPreferenceStore store = getPreferenceStore();

        // Level
        String level = store.getDefaultString( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL );
        if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_DEBUG.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 0 );
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_INFO.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 1 );
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_WARN.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 2 );
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_ERROR.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 3 );
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_FATAL.equalsIgnoreCase( level ) )
        {
            levelCombo.select( 4 );
        }

        // Pattern
        patternText.setText( store.getDefaultString( ApacheDsPluginConstants.PREFS_SERVER_LOGS_PATTERN ) );

        super.performDefaults();
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        IPreferenceStore store = getPreferenceStore();

        // Level
        int level = levelCombo.getSelectionIndex();

        store.getDefaultString( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL );
        if ( level == 0 )
        {
            store.setValue( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL,
                ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_DEBUG );
        }
        else if ( level == 1 )
        {
            store.setValue( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL,
                ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_INFO );
        }
        else if ( level == 2 )
        {
            store.setValue( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL,
                ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_WARN );
        }
        else if ( level == 3 )
        {
            store.setValue( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL,
                ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_ERROR );
        }
        else if ( level == 4 )
        {
            store.setValue( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL,
                ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_FATAL );
        }

        // Pattern
        store.setValue( ApacheDsPluginConstants.PREFS_SERVER_LOGS_PATTERN, patternText.getText() );

        return super.performOk();
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
        // Nothing to do
    }
}
