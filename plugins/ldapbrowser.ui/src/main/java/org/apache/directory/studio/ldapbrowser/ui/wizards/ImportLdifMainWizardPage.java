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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import java.io.File;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.FileBrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.BrowserConnectionWidget;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * This class implements the Main Page of the LDIF Import Wizard
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportLdifMainWizardPage extends WizardPage
{

    /** The continue on error flag key */
    public static final String CONTINUE_ON_ERROR_DIALOGSETTING_KEY = ImportLdifMainWizardPage.class.getName()
        + ".continueOnError"; //$NON-NLS-1$

    /** The update if entry exists flag key */
    public static final String UPDATE_IF_ENTRY_EXISTS_DIALOGSETTING_KEY = ImportLdifMainWizardPage.class.getName()
        + ".updateIfEntryExists"; //$NON-NLS-1$

    /** The valid extension. */
    private static final String[] EXTENSIONS = new String[]
        { "*.ldif", "*" }; //$NON-NLS-1$ //$NON-NLS-2$

    /** The valid log extension. */
    private static final String[] LOG_EXTENSIONS = new String[]
        { "*.ldif.log", "*" }; //$NON-NLS-1$ //$NON-NLS-2$

    /** The wizard. */
    private ImportLdifWizard wizard;

    /** The ldif file browser widget. */
    private FileBrowserWidget ldifFileBrowserWidget;

    /** The browser connection widget. */
    private BrowserConnectionWidget browserConnectionWidget;

    /** The enable logging button. */
    private Button enableLoggingButton;

    /** The use default logfile button. */
    private Button useDefaultLogfileButton;

    /** The use custom logfile button. */
    private Button useCustomLogfileButton;

    /** The custom logfile name. */
    private String customLogfileName;

    /** The log file browser widget. */
    private FileBrowserWidget logFileBrowserWidget;

    /** The overwrite logfile button. */
    private Button overwriteLogfileButton;

    /** The update if entry exists button. */
    private Button updateIfEntryExistsButton;

    /** The continue on error button. */
    private Button continueOnErrorButton;


    /**
     * Creates a new instance of ImportLdifMainWizardPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public ImportLdifMainWizardPage( String pageName, ImportLdifWizard wizard )
    {
        super( pageName );
        setTitle( Messages.getString( "ImportLdifMainWizardPage.LDIFImport" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "ImportLdifMainWizardPage.PleaseSelectConnectionAndLDIF" ) ); //$NON-NLS-1$
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_IMPORT_LDIF_WIZARD ) );
        setPageComplete( false );

        this.wizard = wizard;
    }


    /**
     * Validates the page. This method is responsible for displaying errors, 
     * as well as enabling/disabling the "Finish" button
     */
    private void validate()
    {
        boolean ok = true;

        File ldifFile = new File( ldifFileBrowserWidget.getFilename() );
        if ( "".equals( ldifFileBrowserWidget.getFilename() ) ) //$NON-NLS-1$
        {
            setErrorMessage( null );
            ok = false;
        }
        else if ( !ldifFile.isFile() || !ldifFile.exists() )
        {
            setErrorMessage( Messages.getString( "ImportLdifMainWizardPage.ErrorSelectedLDIFNotExist" ) ); //$NON-NLS-1$
            ok = false;
        }
        else if ( !ldifFile.canRead() )
        {
            setErrorMessage( Messages.getString( "ImportLdifMainWizardPage.ErrorSelectedLDIFNotReadable" ) ); //$NON-NLS-1$
            ok = false;
        }
        else if ( enableLoggingButton.getSelection() )
        {
            File logFile = new File( logFileBrowserWidget.getFilename() );
            File logFileDirectory = logFile.getParentFile();

            if ( logFile.equals( ldifFile ) )
            {
                setErrorMessage( Messages.getString( "ImportLdifMainWizardPage.ErrorLDIFAndLogEqual" ) ); //$NON-NLS-1$
                ok = false;
            }
            else if ( logFile.isDirectory() )
            {
                setErrorMessage( Messages.getString( "ImportLdifMainWizardPage.ErrorSelectedLogFileNotFile" ) ); //$NON-NLS-1$
                ok = false;
            }
            else if ( logFile.exists() && !overwriteLogfileButton.getSelection() )
            {
                setErrorMessage( Messages.getString( "ImportLdifMainWizardPage.ErrorSelectedLogFileExist" ) ); //$NON-NLS-1$
                ok = false;
            }
            else if ( logFile.exists() && !logFile.canWrite() )
            {
                setErrorMessage( Messages.getString( "ImportLdifMainWizardPage.ErrorSelectedLogFileNotWritable" ) ); //$NON-NLS-1$
                ok = false;
            }
            else if ( logFile.getParentFile() == null )
            {
                setErrorMessage( Messages
                    .getString( "ImportLdifMainWizardPage.ErrorSelectedLogFileDirectoryNotWritable" ) ); //$NON-NLS-1$
                ok = false;
            }
            else if ( !logFile.exists() && ( logFileDirectory == null || !logFileDirectory.canWrite() ) )
            {
                setErrorMessage( Messages
                    .getString( "ImportLdifMainWizardPage.ErrorSelectedLogFileDirectoryNotWritable" ) ); //$NON-NLS-1$
                ok = false;
            }
        }

        if ( wizard.getImportConnection() == null )
        {
            setErrorMessage( Messages
                .getString( "ImportLdifMainWizardPage.ErrorNoConnectionSelected" ) ); //$NON-NLS-1$
            ok = false;
        }

        if ( ok )
        {
            setErrorMessage( null );
        }
        setPageComplete( ok );
        getContainer().updateButtons();
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        // LDIF file
        BaseWidgetUtils.createLabel( composite, Messages.getString( "ImportLdifMainWizardPage.LDIFFile" ), 1 ); //$NON-NLS-1$
        ldifFileBrowserWidget = new FileBrowserWidget(
            Messages.getString( "ImportLdifMainWizardPage.SelectLDIFFile" ), EXTENSIONS, FileBrowserWidget.TYPE_OPEN ); //$NON-NLS-1$
        ldifFileBrowserWidget.createWidget( composite );
        ldifFileBrowserWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                wizard.setLdifFilename( ldifFileBrowserWidget.getFilename() );
                if ( useDefaultLogfileButton.getSelection() )
                {
                    logFileBrowserWidget.setFilename( ldifFileBrowserWidget.getFilename() + ".log" ); //$NON-NLS-1$
                }
                validate();
            }
        } );

        // Connection
        BaseWidgetUtils.createLabel( composite, Messages.getString( "ImportLdifMainWizardPage.ImportTo" ), 1 ); //$NON-NLS-1$
        browserConnectionWidget = new BrowserConnectionWidget( wizard.getImportConnection() );
        browserConnectionWidget.createWidget( composite );
        browserConnectionWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                wizard.setImportConnection( browserConnectionWidget.getBrowserConnection() );
                validate();
            }
        } );

        // Logging
        Composite loggingOuterComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 3 );
        Group loggingGroup = BaseWidgetUtils.createGroup( loggingOuterComposite, Messages
            .getString( "ImportLdifMainWizardPage.Logging" ), 1 ); //$NON-NLS-1$
        Composite loggingContainer = BaseWidgetUtils.createColumnContainer( loggingGroup, 3, 1 );

        enableLoggingButton = BaseWidgetUtils.createCheckbox( loggingContainer, Messages
            .getString( "ImportLdifMainWizardPage.EnableLogging" ), 3 ); //$NON-NLS-1$
        enableLoggingButton.setSelection( true );
        wizard.setEnableLogging( enableLoggingButton.getSelection() );
        enableLoggingButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                wizard.setEnableLogging( enableLoggingButton.getSelection() );
                useDefaultLogfileButton.setEnabled( enableLoggingButton.getSelection() );
                useCustomLogfileButton.setEnabled( enableLoggingButton.getSelection() );
                logFileBrowserWidget.setEnabled( enableLoggingButton.getSelection()
                    && useCustomLogfileButton.getSelection() );
                overwriteLogfileButton.setEnabled( enableLoggingButton.getSelection() );
                validate();
            }
        } );

        BaseWidgetUtils.createRadioIndent( loggingContainer, 1 );
        useDefaultLogfileButton = BaseWidgetUtils.createRadiobutton( loggingContainer, Messages
            .getString( "ImportLdifMainWizardPage.UseDefaultLogFile" ), 2 ); //$NON-NLS-1$
        useDefaultLogfileButton.setSelection( true );
        useDefaultLogfileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                String temp = customLogfileName;
                logFileBrowserWidget.setFilename( ldifFileBrowserWidget.getFilename() + ".log" ); //$NON-NLS-1$
                logFileBrowserWidget.setEnabled( false );
                customLogfileName = temp;
                validate();
            }
        } );

        BaseWidgetUtils.createRadioIndent( loggingContainer, 1 );
        useCustomLogfileButton = BaseWidgetUtils.createRadiobutton( loggingContainer, Messages
            .getString( "ImportLdifMainWizardPage.UseCustomLogFile" ), 2 ); //$NON-NLS-1$
        useCustomLogfileButton.setSelection( false );
        useCustomLogfileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                logFileBrowserWidget.setFilename( customLogfileName != null ? customLogfileName : "" ); //$NON-NLS-1$
                logFileBrowserWidget.setEnabled( true );
                validate();
            }
        } );

        BaseWidgetUtils.createRadioIndent( loggingContainer, 1 );
        logFileBrowserWidget = new FileBrowserWidget(
            Messages.getString( "ImportLdifMainWizardPage.SelectLogFile" ), LOG_EXTENSIONS, FileBrowserWidget.TYPE_SAVE ); //$NON-NLS-1$
        logFileBrowserWidget.createWidget( loggingContainer );
        logFileBrowserWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                customLogfileName = logFileBrowserWidget.getFilename();
                wizard.setLogFilename( customLogfileName );
                validate();
            }
        } );
        logFileBrowserWidget.setEnabled( false );

        BaseWidgetUtils.createRadioIndent( loggingContainer, 1 );
        overwriteLogfileButton = BaseWidgetUtils.createCheckbox( loggingContainer, Messages
            .getString( "ImportLdifMainWizardPage.OverwriteExistingLogFile" ), 2 ); //$NON-NLS-1$
        overwriteLogfileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                validate();
            }
        } );

        // Options
        Composite optionsOuterComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 3 );
        Group optionsGroup = BaseWidgetUtils.createGroup( optionsOuterComposite, Messages
            .getString( "ImportLdifMainWizardPage.Options" ), 1 ); //$NON-NLS-1$
        Composite optionsContainer = BaseWidgetUtils.createColumnContainer( optionsGroup, 3, 1 );

        updateIfEntryExistsButton = BaseWidgetUtils.createCheckbox( optionsContainer, Messages
            .getString( "ImportLdifMainWizardPage.UpdateExistingEntires" ), //$NON-NLS-1$
            3 );
        updateIfEntryExistsButton
            .setToolTipText( Messages.getString( "ImportLdifMainWizardPage.OptionsAppliesForLdif" ) ); //$NON-NLS-1$
        if ( BrowserUIPlugin.getDefault().getDialogSettings().get( UPDATE_IF_ENTRY_EXISTS_DIALOGSETTING_KEY ) == null )
        {
            BrowserUIPlugin.getDefault().getDialogSettings().put( UPDATE_IF_ENTRY_EXISTS_DIALOGSETTING_KEY, false );
        }
        updateIfEntryExistsButton.setSelection( BrowserUIPlugin.getDefault().getDialogSettings().getBoolean(
            UPDATE_IF_ENTRY_EXISTS_DIALOGSETTING_KEY ) );
        wizard.setUpdateIfEntryExists( updateIfEntryExistsButton.getSelection() );
        updateIfEntryExistsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                wizard.setUpdateIfEntryExists( updateIfEntryExistsButton.getSelection() );
                validate();
            }
        } );

        continueOnErrorButton = BaseWidgetUtils.createCheckbox( optionsContainer, Messages
            .getString( "ImportLdifMainWizardPage.ContinueOnError" ), 3 ); //$NON-NLS-1$
        if ( BrowserUIPlugin.getDefault().getDialogSettings().get( CONTINUE_ON_ERROR_DIALOGSETTING_KEY ) == null )
        {
            BrowserUIPlugin.getDefault().getDialogSettings().put( CONTINUE_ON_ERROR_DIALOGSETTING_KEY, false );
        }
        continueOnErrorButton.setSelection( BrowserUIPlugin.getDefault().getDialogSettings().getBoolean(
            CONTINUE_ON_ERROR_DIALOGSETTING_KEY ) );
        wizard.setContinueOnError( continueOnErrorButton.getSelection() );
        continueOnErrorButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                wizard.setContinueOnError( continueOnErrorButton.getSelection() );
                validate();
            }
        } );

        setControl( composite );
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        ldifFileBrowserWidget.saveDialogSettings();
        BrowserUIPlugin.getDefault().getDialogSettings().put( UPDATE_IF_ENTRY_EXISTS_DIALOGSETTING_KEY,
            updateIfEntryExistsButton.getSelection() );
        BrowserUIPlugin.getDefault().getDialogSettings().put( CONTINUE_ON_ERROR_DIALOGSETTING_KEY,
            continueOnErrorButton.getSelection() );
    }

}