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
package org.apache.directory.studio.apacheds.schemaeditor.view.wizards;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the WizardPage of the ImportProjectsWizard.
 * <p>
 * It is used to let the user enter the informations about the
 * schemas he wants to import.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportProjectsWizardPage extends WizardPage
{
    // UI Fields
    private Text fromDirectoryText;
    private Button fromDirectoryButton;
    private CheckboxTableViewer projectFilesTableViewer;
    private Button projectFilesTableSelectAllButton;
    private Button projectFilesTableDeselectAllButton;


    /**
     * Creates a new instance of ImportSchemasFromOpenLdapWizardPage.
     */
    protected ImportProjectsWizardPage()
    {
        super( "ImportProjectsWizardPage" );
        setTitle( "Import schema projects" );
        setDescription( "Please select the schema project to import." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_PROJECT_IMPORT_WIZARD ) );
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
        schemaFilesGroup.setText( "Schema project files" );
        schemaFilesGroup.setLayout( new GridLayout( 2, false ) );
        schemaFilesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Schema Files
        projectFilesTableViewer = new CheckboxTableViewer( new Table( schemaFilesGroup, SWT.BORDER | SWT.CHECK
            | SWT.FULL_SELECTION ) );
        GridData schemasTableViewerGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
        schemasTableViewerGridData.heightHint = 125;
        projectFilesTableViewer.getTable().setLayoutData( schemasTableViewerGridData );
        projectFilesTableViewer.setContentProvider( new ArrayContentProvider() );
        projectFilesTableViewer.setLabelProvider( new LabelProvider()
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
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_SCHEMA )
                        .createImage();
                }

                // Default
                return super.getImage( element );
            }
        } );
        projectFilesTableViewer.addCheckStateListener( new ICheckStateListener()
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
        projectFilesTableSelectAllButton = new Button( schemaFilesGroup, SWT.PUSH );
        projectFilesTableSelectAllButton.setText( "Select All" );
        projectFilesTableSelectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        projectFilesTableSelectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                projectFilesTableViewer.setAllChecked( true );
                dialogChanged();
            }
        } );
        projectFilesTableDeselectAllButton = new Button( schemaFilesGroup, SWT.PUSH );
        projectFilesTableDeselectAllButton.setText( "Deselect All" );
        projectFilesTableDeselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        projectFilesTableDeselectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                projectFilesTableViewer.setAllChecked( false );
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
                if ( fileName.endsWith( ".schemaproject" ) )
                {
                    schemaFiles.add( file );
                }
            }
        }

        projectFilesTableViewer.setInput( schemaFiles );
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Export Directory
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
        if ( projectFilesTableViewer.getCheckedElements().length == 0 )
        {
            displayErrorMessage( "One or several schema project files must be selected." );
            return;
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
     * Gets the selected project files.
     *
     * @return
     *      the selected project files
     */
    public File[] getSelectedProjectFiles()
    {
        Object[] selectedProjectFile = projectFilesTableViewer.getCheckedElements();

        List<File> schemaFiles = new ArrayList<File>();
        for ( Object projectFile : selectedProjectFile )
        {
            schemaFiles.add( ( File ) projectFile );
        }

        return schemaFiles.toArray( new File[0] );
    }

}
