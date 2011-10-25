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
 */
public class ImportSchemasFromXmlWizardPage extends AbstractWizardPage
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
    protected ImportSchemasFromXmlWizardPage()
    {
        super( "ImportSchemasFromXmlWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "ImportSchemasFromXmlWizardPage.ImportSchemasFromXML" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "ImportSchemasFromXmlWizardPage.SelectXMLSchemaToImport" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_SCHEMAS_IMPORT_WIZARD ) );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // From Directory Group
        Group fromDirectoryGroup = new Group( composite, SWT.NONE );
        fromDirectoryGroup.setText( Messages.getString( "ImportSchemasFromXmlWizardPage.FromDirectory" ) ); //$NON-NLS-1$
        fromDirectoryGroup.setLayout( new GridLayout( 3, false ) );
        fromDirectoryGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // From Directory
        Label fromDirectoryLabel = new Label( fromDirectoryGroup, SWT.NONE );
        fromDirectoryLabel.setText( Messages.getString( "ImportSchemasFromXmlWizardPage.FromDirectoryColon" ) ); //$NON-NLS-1$
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
        fromDirectoryButton.setText( Messages.getString( "ImportSchemasFromXmlWizardPage.Browse" ) ); //$NON-NLS-1$
        fromDirectoryButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                chooseFromDirectory();
            }
        } );

        // Schema Files Group
        Group schemaFilesGroup = new Group( composite, SWT.NONE );
        schemaFilesGroup.setText( Messages.getString( "ImportSchemasFromXmlWizardPage.SchemaFiles" ) ); //$NON-NLS-1$
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
        schemaFilesTableSelectAllButton.setText( Messages.getString( "ImportSchemasFromXmlWizardPage.SelectAll" ) ); //$NON-NLS-1$
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
        schemaFilesTableDeselectAllButton.setText( Messages.getString( "ImportSchemasFromXmlWizardPage.DeselectAll" ) ); //$NON-NLS-1$
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
        dialog.setText( Messages.getString( "ImportSchemasFromXmlWizardPage.ChooseFolder" ) ); //$NON-NLS-1$
        dialog.setMessage( Messages.getString( "ImportSchemasFromXmlWizardPage.SelectFolderToImportFrom" ) ); //$NON-NLS-1$
        if ( "".equals( fromDirectoryText.getText() ) ) //$NON-NLS-1$
        {
            dialog.setFilterPath( Activator.getDefault().getPreferenceStore().getString(
                PluginConstants.FILE_DIALOG_IMPORT_SCHEMAS_XML ) );
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
                if ( fileName.endsWith( ".xml" ) ) //$NON-NLS-1$
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
            displayErrorMessage( Messages.getString( "ImportSchemasFromXmlWizardPage.ErrorNoSchemaProjectOpen" ) ); //$NON-NLS-1$
            return;
        }

        // Export Directory
        String directory = fromDirectoryText.getText();
        if ( ( directory == null ) || ( directory.equals( "" ) ) ) //$NON-NLS-1$
        {
            displayErrorMessage( Messages.getString( "ImportSchemasFromXmlWizardPage.ErrorNoDirectorySelected" ) ); //$NON-NLS-1$
            return;
        }
        else
        {
            File directoryFile = new File( directory );
            if ( !directoryFile.exists() )
            {
                displayErrorMessage( Messages
                    .getString( "ImportSchemasFromXmlWizardPage.ErrorSelectedDirectoryNotExists" ) ); //$NON-NLS-1$
                return;
            }
            else if ( !directoryFile.isDirectory() )
            {
                displayErrorMessage( Messages
                    .getString( "ImportSchemasFromXmlWizardPage.ErrorSelectedDirectoryNotDirectory" ) ); //$NON-NLS-1$
                return;
            }
            else if ( !directoryFile.canRead() )
            {
                displayErrorMessage( Messages
                    .getString( "ImportSchemasFromXmlWizardPage.ErrorSelectedDirectoryNotReadable" ) ); //$NON-NLS-1$
                return;
            }
        }

        // Schemas table
        if ( schemaFilesTableViewer.getCheckedElements().length == 0 )
        {
            displayErrorMessage( Messages.getString( "ImportSchemasFromXmlWizardPage.ErrorNoSchemaSelected" ) ); //$NON-NLS-1$
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
        Activator.getDefault().getPreferenceStore().putValue( PluginConstants.FILE_DIALOG_IMPORT_SCHEMAS_XML,
            fromDirectoryText.getText() );
    }
}
