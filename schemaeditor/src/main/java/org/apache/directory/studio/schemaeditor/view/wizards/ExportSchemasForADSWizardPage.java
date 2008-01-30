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
package org.apache.directory.studio.schemaeditor.view.wizards;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the WizardPage of the ExportSchemasForADSWizard.
 * <p>
 * It is used to let the user enter the informations about the
 * schemas he wants to export and where to export.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportSchemasForADSWizardPage extends WizardPage
{
    /** The selected schemas */
    private Schema[] selectedSchemas = new Schema[0];

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    public static final int EXPORT_MULTIPLE_FILES = 0;
    public static final int EXPORT_SINGLE_FILE = 1;

    // UI Fields
    private CheckboxTableViewer schemasTableViewer;
    private Button schemasTableSelectAllButton;
    private Button schemasTableDeselectAllButton;
    private Button exportMultipleFilesRadio;
    private Label exportMultipleFilesLabel;
    private Text exportMultipleFilesText;
    private Button exportMultipleFilesButton;
    private Button exportSingleFileRadio;
    private Label exportSingleFileLabel;
    private Text exportSingleFileText;
    private Button exportSingleFileButton;


    /**
     * Creates a new instance of ExportSchemasAsXmlWizardPage.
     */
    protected ExportSchemasForADSWizardPage()
    {
        super( "ExportSchemasForADSWizardPage" );
        setTitle( "Export schemas for Apache DS" );
        setDescription( "Please select the schemas to export for Apache DS." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_SCHEMAS_EXPORT_FOR_ADS_WIZARD ) );
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Schemas Group
        Group schemasGroup = new Group( composite, SWT.NONE );
        schemasGroup.setText( "Schemas" );
        schemasGroup.setLayout( new GridLayout( 2, false ) );
        schemasGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Schemas TableViewer
        Label schemasLabel = new Label( schemasGroup, SWT.NONE );
        schemasLabel.setText( "Select the schemas to export:" );
        schemasLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        schemasTableViewer = new CheckboxTableViewer( new Table( schemasGroup, SWT.BORDER | SWT.CHECK
            | SWT.FULL_SELECTION ) );
        GridData schemasTableViewerGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
        schemasTableViewerGridData.heightHint = 125;
        schemasTableViewer.getTable().setLayoutData( schemasTableViewerGridData );
        schemasTableViewer.setContentProvider( new ArrayContentProvider() );
        schemasTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof Schema )
                {
                    return ( ( Schema ) element ).getName();
                }

                // Default
                return super.getText( element );
            }


            public Image getImage( Object element )
            {
                if ( element instanceof Schema )
                {
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_SCHEMA )
                        .createImage();
                }

                // Default
                return super.getImage( element );
            }
        } );
        schemasTableViewer.addCheckStateListener( new ICheckStateListener()
        {
            /**
             * Notifies of a change to the checked state of an element.
             *
             * @param event
             *      event object describing the change
             */
            public void checkStateChanged( CheckStateChangedEvent event )
            {
                dialogChanged();
            }
        } );
        schemasTableSelectAllButton = new Button( schemasGroup, SWT.PUSH );
        schemasTableSelectAllButton.setText( "Select All" );
        schemasTableSelectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        schemasTableSelectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                schemasTableViewer.setAllChecked( true );
                dialogChanged();
            }
        } );
        schemasTableDeselectAllButton = new Button( schemasGroup, SWT.PUSH );
        schemasTableDeselectAllButton.setText( "Deselect All" );
        schemasTableDeselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        schemasTableDeselectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                schemasTableViewer.setAllChecked( false );
                dialogChanged();
            }
        } );

        // Export Destination Group
        Group exportDestinationGroup = new Group( composite, SWT.NULL );
        exportDestinationGroup.setText( "Export Destination" );
        exportDestinationGroup.setLayout( new GridLayout( 4, false ) );
        exportDestinationGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Export Multiple Files
        exportMultipleFilesRadio = new Button( exportDestinationGroup, SWT.RADIO );
        exportMultipleFilesRadio.setText( "Export each schema as a separate file." );
        exportMultipleFilesRadio.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        exportMultipleFilesRadio.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                exportMultipleFilesSelected();
                dialogChanged();
            }
        } );
        Label exportMultipleFilesFiller = new Label( exportDestinationGroup, SWT.NONE );
        exportMultipleFilesFiller.setText( "    " );
        exportMultipleFilesLabel = new Label( exportDestinationGroup, SWT.NONE );
        exportMultipleFilesLabel.setText( "Directory:" );
        exportMultipleFilesText = new Text( exportDestinationGroup, SWT.BORDER );
        exportMultipleFilesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        exportMultipleFilesText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );
        exportMultipleFilesButton = new Button( exportDestinationGroup, SWT.PUSH );
        exportMultipleFilesButton.setText( "Browse..." );
        exportMultipleFilesButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                chooseExportDirectory();
                dialogChanged();
            }
        } );

        // Export Single File
        exportSingleFileRadio = new Button( exportDestinationGroup, SWT.RADIO );
        exportSingleFileRadio.setText( "Export the schemas as a single file." );
        exportSingleFileRadio.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        exportSingleFileRadio.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                exportSingleFileSelected();
                dialogChanged();
            }
        } );
        Label exportSingleFileFiller = new Label( exportDestinationGroup, SWT.NONE );
        exportSingleFileFiller.setText( "    " );
        exportSingleFileLabel = new Label( exportDestinationGroup, SWT.NONE );
        exportSingleFileLabel.setText( "Export File:" );
        exportSingleFileText = new Text( exportDestinationGroup, SWT.BORDER );
        exportSingleFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        exportSingleFileText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );
        exportSingleFileButton = new Button( exportDestinationGroup, SWT.PUSH );
        exportSingleFileButton.setText( "Browse..." );
        exportSingleFileButton.addSelectionListener( new SelectionAdapter()
        {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            public void widgetSelected( SelectionEvent e )
            {
                chooseExportFile();
                dialogChanged();
            }
        } );

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {
        // Filling the Schemas table
        if ( schemaHandler != null )
        {
            List<Schema> schemas = new ArrayList<Schema>();
            schemas.addAll( schemaHandler.getSchemas() );

            Collections.sort( schemas, new Comparator<Schema>()
            {
                public int compare( Schema o1, Schema o2 )
                {
                    return o1.getName().compareToIgnoreCase( o2.getName() );
                }
            } );

            schemasTableViewer.setInput( schemas );

            // Setting the selected schemas
            schemasTableViewer.setCheckedElements( selectedSchemas );
        }

        // Selecting the Multiple Files choice
        exportMultipleFilesSelected();

        displayErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * This method is called when the exportMultipleFiles radio button is selected.
     */
    private void exportMultipleFilesSelected()
    {
        exportMultipleFilesRadio.setSelection( true );
        exportMultipleFilesLabel.setEnabled( true );
        exportMultipleFilesText.setEnabled( true );
        exportMultipleFilesButton.setEnabled( true );

        exportSingleFileRadio.setSelection( false );
        exportSingleFileLabel.setEnabled( false );
        exportSingleFileText.setEnabled( false );
        exportSingleFileButton.setEnabled( false );
    }


    /**
     * This method is called when the exportSingleFile radio button is selected.
     */
    private void exportSingleFileSelected()
    {
        exportMultipleFilesRadio.setSelection( false );
        exportMultipleFilesLabel.setEnabled( false );
        exportMultipleFilesText.setEnabled( false );
        exportMultipleFilesButton.setEnabled( false );

        exportSingleFileRadio.setSelection( true );
        exportSingleFileLabel.setEnabled( true );
        exportSingleFileText.setEnabled( true );
        exportSingleFileButton.setEnabled( true );
    }


    /**
     * This method is called when the exportMultipleFiles 'browse' button is selected.
     */
    private void chooseExportDirectory()
    {
        DirectoryDialog dialog = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        dialog.setText( "Choose Folder" );
        dialog.setMessage( "Select the folder in which export the files." );
        if ( "".equals( exportMultipleFilesText.getText() ) )
        {
            dialog.setFilterPath( Activator.getDefault().getPreferenceStore().getString(
                PluginConstants.FILE_DIALOG_EXPORT_SCHEMAS_APACHE_DS ) );
        }
        else
        {
            dialog.setFilterPath( exportMultipleFilesText.getText() );
        }

        String selectedDirectory = dialog.open();
        if ( selectedDirectory != null )
        {
            exportMultipleFilesText.setText( selectedDirectory );
        }
    }


    /**
     * This method is called when the exportSingleFile 'browse' button is selected.
     */
    private void chooseExportFile()
    {
        FileDialog dialog = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE );
        dialog.setText( "Select File" );
        dialog.setFilterExtensions( new String[]
            { "*.ldif", "*" } );
        dialog.setFilterNames( new String[]
            { "LDIF Files", "All Files" } );
        if ( "".equals( exportSingleFileText.getText() ) )
        {
            dialog.setFilterPath( Activator.getDefault().getPreferenceStore().getString(
                PluginConstants.FILE_DIALOG_EXPORT_SCHEMAS_APACHE_DS ) );
        }
        else
        {
            dialog.setFilterPath( exportSingleFileText.getText() );
        }

        String selectedFile = dialog.open();
        if ( selectedFile != null )
        {
            exportSingleFileText.setText( selectedFile );
        }
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Schemas table
        if ( schemasTableViewer.getCheckedElements().length == 0 )
        {
            displayErrorMessage( "One or several schemas must be selected." );
            return;
        }

        // Export option
        if ( exportMultipleFilesRadio.getSelection() )
        {
            String directory = exportMultipleFilesText.getText();
            if ( ( directory == null ) || ( directory.equals( "" ) ) )
            {
                displayErrorMessage( "A directory must be selected." );
                return;
            }
            else
            {
                File directoryFile = new File( directory );
                if ( !directoryFile.exists() )
                {
                    displayErrorMessage( "The selected directory does not exist." );
                    return;
                }
                else if ( !directoryFile.isDirectory() )
                {
                    displayErrorMessage( "The selected directory is not a directory." );
                    return;
                }
                else if ( !directoryFile.canWrite() )
                {
                    displayErrorMessage( "The selected directory is not writable." );
                    return;
                }
            }
        }
        else if ( exportSingleFileRadio.getSelection() )
        {
            String exportFile = exportSingleFileText.getText();
            if ( ( exportFile == null ) || ( exportFile.equals( "" ) ) )
            {
                displayErrorMessage( "A file must be selected." );
                return;
            }
            else
            {
                File file = new File( exportFile );
                if ( !file.getParentFile().canWrite() )
                {
                    displayErrorMessage( "The selected file is not writable." );
                    return;
                }
            }
        }

        displayErrorMessage( null );
    }


    /**
     * Displays an error message and set the page status as incomplete
     * if the message is not null.
     *
     * @param message
     *      the message to display
     */
    private void displayErrorMessage( String message )
    {
        setErrorMessage( message );
        setPageComplete( message == null );
    }


    /**
     * Gets the selected schemas.
     *
     * @return
     *      the selected schemas
     */
    public Schema[] getSelectedSchemas()
    {
        Object[] selectedSchemas = schemasTableViewer.getCheckedElements();

        List<Schema> schemas = new ArrayList<Schema>();
        for ( Object schema : selectedSchemas )
        {
            schemas.add( ( Schema ) schema );
        }

        return schemas.toArray( new Schema[0] );
    }


    /**
     * Sets the selected projects.
     *
     * @param schemas
     *      the schemas
     */
    public void setSelectedSchemas( Schema[] schemas )
    {
        selectedSchemas = schemas;
    }


    /**
     * Gets the type of export.
     * <p>
     * Values can either EXPORT_MULTIPLE_FILES or EXPORT_SINGLE_FILE.
     * 
     * @return
     *      the type of export
     */
    public int getExportType()
    {
        if ( exportMultipleFilesRadio.getSelection() )
        {
            return EXPORT_MULTIPLE_FILES;
        }
        else if ( exportSingleFileRadio.getSelection() )
        {
            return EXPORT_SINGLE_FILE;
        }

        // Default 
        return EXPORT_MULTIPLE_FILES;
    }


    /**
     * Gets the export directory.
     *
     * @return
     *      the export directory
     */
    public String getExportDirectory()
    {
        return exportMultipleFilesText.getText();
    }


    /**
     * Gets the export file.
     *
     * @return
     *      the export file
     */
    public String getExportFile()
    {
        return exportSingleFileText.getText();
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        if ( exportMultipleFilesRadio.getSelection() )
        {
            Activator.getDefault().getPreferenceStore().putValue( PluginConstants.FILE_DIALOG_EXPORT_SCHEMAS_APACHE_DS,
                exportMultipleFilesText.getText() );
        }
        else
        {
            Activator.getDefault().getPreferenceStore().putValue( PluginConstants.FILE_DIALOG_EXPORT_SCHEMAS_APACHE_DS,
                new File( exportSingleFileText.getText() ).getParent() );
        }
    }
}
