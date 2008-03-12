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
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * This class represents the WizardPage of the ImportSchemasFromOpenLdapWizard.
 * <p>
 * It is used to let the user enter the informations about the
 * schemas he wants to import.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportSchemasFromOpenLdapWizardPage extends AbstractWizardPage
{
    // UI Fields
    private Text fromDirectoryText;
    private Button fromDirectoryButton;
    private CheckboxTableViewer schemaFilesTableViewer;
    private Button schemaFilesTableSelectAllButton;
    private Button schemaFilesTableDeselectAllButton;


    /**
     * Creates a new instance of ImportSchemasFromOpenLdapWizardPage.
     */
    protected ImportSchemasFromOpenLdapWizardPage()
    {
        super( "ImportSchemasFromOpenLdapWizardPage" );
        setTitle( "Import schemas from OpenLdap files" );
        setDescription( "Please select the OpenLdap schema files to import." );
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_SCHEMAS_IMPORT_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // From Directory Group
        Group fromDirectoryGroup = new Group( composite, SWT.NONE );
        fromDirectoryGroup.setText( "From directory" );
        fromDirectoryGroup.setLayout( new GridLayout( 3, false ) );
        fromDirectoryGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // From Directory
        Label fromDirectoryLabel = new Label( fromDirectoryGroup, SWT.NONE );
        fromDirectoryLabel.setText( "From directory:" );
        fromDirectoryText = new Text( fromDirectoryGroup, SWT.BORDER );
        fromDirectoryText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        fromDirectoryText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );
        fromDirectoryButton = new Button( fromDirectoryGroup, SWT.PUSH );
        fromDirectoryButton.setText( "Browse..." );
        fromDirectoryButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                chooseFromDirectory();
            }
        } );

        // Schema Files Group
        Group schemaFilesGroup = new Group( composite, SWT.NONE );
        schemaFilesGroup.setText( "Schema files" );
        schemaFilesGroup.setLayout( new GridLayout( 2, false ) );
        schemaFilesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Schema Files
        schemaFilesTableViewer = new CheckboxTableViewer( new Table( schemaFilesGroup, SWT.BORDER | SWT.CHECK
            | SWT.FULL_SELECTION ) );
        GridData schemasTableViewerGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
        schemasTableViewerGridData.heightHint = 125;
        schemaFilesTableViewer.getTable().setLayoutData( schemasTableViewerGridData );
        schemaFilesTableViewer.setContentProvider( new ArrayContentProvider() );
        schemaFilesTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof File )
                {
                    return ( ( File ) element ).getName();
                }

                // Default
                return super.getText( element );
            }


            public Image getImage( Object element )
            {
                if ( element instanceof File )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_SCHEMA );
                }

                // Default
                return super.getImage( element );
            }
        } );
        schemaFilesTableViewer.addCheckStateListener( new ICheckStateListener()
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
        schemaFilesTableSelectAllButton = new Button( schemaFilesGroup, SWT.PUSH );
        schemaFilesTableSelectAllButton.setText( "Select All" );
        schemaFilesTableSelectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        schemaFilesTableSelectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                schemaFilesTableViewer.setAllChecked( true );
                dialogChanged();
            }
        } );
        schemaFilesTableDeselectAllButton = new Button( schemaFilesGroup, SWT.PUSH );
        schemaFilesTableDeselectAllButton.setText( "Deselect All" );
        schemaFilesTableDeselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        schemaFilesTableDeselectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                schemaFilesTableViewer.setAllChecked( false );
                dialogChanged();
            }
        } );

        initFields();
        dialogChanged();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {
        displayErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * This method is called when the exportMultipleFiles 'browse' button is selected.
     */
    private void chooseFromDirectory()
    {
        DirectoryDialog dialog = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        dialog.setText( "Choose Folder" );
        dialog.setMessage( "Select the folder from which import the files." );
        if ( "".equals( fromDirectoryText.getText() ) )
        {
            dialog.setFilterPath( Activator.getDefault().getPreferenceStore().getString(
                PluginConstants.FILE_DIALOG_IMPORT_SCHEMAS_OPENLDAP ) );
        }
        else
        {
            dialog.setFilterPath( fromDirectoryText.getText() );
        }

        String selectedDirectory = dialog.open();
        if ( selectedDirectory != null )
        {
            fromDirectoryText.setText( selectedDirectory );
            fillInSchemaFilesTable( selectedDirectory );
        }
    }


    /**
     * Fills in the SchemaFilesTable with the schema files found in the given path.
     *
     * @param path
     *      the path to search schema files in
     */
    private void fillInSchemaFilesTable( String path )
    {
        List<File> schemaFiles = new ArrayList<File>();
        File selectedDirectory = new File( path );
        if ( selectedDirectory.exists() )
        {
            for ( File file : selectedDirectory.listFiles() )
            {
                String fileName = file.getName();
                if ( fileName.endsWith( ".schema" ) )
                {
                    schemaFiles.add( file );
                }
            }
        }

        schemaFilesTableViewer.setInput( schemaFiles );
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Checking if a Schema Project is open
        if ( Activator.getDefault().getSchemaHandler() == null )
        {
            displayErrorMessage( "A Schema Project must be open to import schemas from OpenLDAP files." );
            return;
        }

        // Import Directory
        String directory = fromDirectoryText.getText();
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
            else if ( !directoryFile.canRead() )
            {
                displayErrorMessage( "The selected directory is not readable." );
                return;
            }
        }

        // Schemas table
        if ( schemaFilesTableViewer.getCheckedElements().length == 0 )
        {
            displayErrorMessage( "One or several schema files must be selected." );
            return;
        }

        displayErrorMessage( null );
    }


    /**
     * Gets the selected schema files.
     *
     * @return
     *      the selected schema files
     */
    public File[] getSelectedSchemaFiles()
    {
        Object[] selectedSchemaFile = schemaFilesTableViewer.getCheckedElements();

        List<File> schemaFiles = new ArrayList<File>();
        for ( Object schemaFile : selectedSchemaFile )
        {
            schemaFiles.add( ( File ) schemaFile );
        }

        return schemaFiles.toArray( new File[0] );
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        Activator.getDefault().getPreferenceStore().putValue( PluginConstants.FILE_DIALOG_IMPORT_SCHEMAS_OPENLDAP,
            fromDirectoryText.getText() );
    }
}
