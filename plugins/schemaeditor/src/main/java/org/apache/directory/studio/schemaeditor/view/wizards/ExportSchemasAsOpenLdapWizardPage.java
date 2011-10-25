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
 * This class represents the WizardPage of the ExportSchemasAsOpenLdapWizard.
 * <p>
 * It is used to let the user enter the informations about the
 * schemas he wants to export and where to export.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportSchemasAsOpenLdapWizardPage extends AbstractWizardPage
{
    /** The selected schemas */
    private Schema[] selectedSchemas = new Schema[0];

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    // UI Fields
    private CheckboxTableViewer schemasTableViewer;
    private Button schemasTableSelectAllButton;
    private Button schemasTableDeselectAllButton;
    private Label exportDirectoryLabel;
    private Text exportDirectoryText;
    private Button exportDirectoryButton;


    /**
     * Creates a new instance of ExportSchemasAsXmlWizardPage.
     */
    protected ExportSchemasAsOpenLdapWizardPage()
    {
        super( "ExportSchemasAsOpenLdapWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.ExportSchemaAsOpenLDAP" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.PleaseSelectSchemaExportOpenLDAP" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_SCHEMAS_EXPORT_WIZARD ) );
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Schemas Group
        Group schemasGroup = new Group( composite, SWT.NONE );
        schemasGroup.setText( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.Schemas" ) ); //$NON-NLS-1$
        schemasGroup.setLayout( new GridLayout( 2, false ) );
        schemasGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Schemas TableViewer
        Label schemasLabel = new Label( schemasGroup, SWT.NONE );
        schemasLabel.setText( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.SelectSchemasToExport" ) ); //$NON-NLS-1$
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
                    return ( ( Schema ) element ).getSchemaName();
                }

                // Default
                return super.getText( element );
            }


            public Image getImage( Object element )
            {
                if ( element instanceof Schema )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_SCHEMA );
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
        schemasTableSelectAllButton.setText( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.SelectAll" ) ); //$NON-NLS-1$
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
        schemasTableDeselectAllButton.setText( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.DeselectAll" ) ); //$NON-NLS-1$
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
        exportDestinationGroup.setText( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.ExportDestination" ) ); //$NON-NLS-1$
        exportDestinationGroup.setLayout( new GridLayout( 3, false ) );
        exportDestinationGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        exportDirectoryLabel = new Label( exportDestinationGroup, SWT.NONE );
        exportDirectoryLabel.setText( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.Directory" ) ); //$NON-NLS-1$
        exportDirectoryText = new Text( exportDestinationGroup, SWT.BORDER );
        exportDirectoryText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        exportDirectoryText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );
        exportDirectoryButton = new Button( exportDestinationGroup, SWT.PUSH );
        exportDirectoryButton.setText( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.Browse" ) ); //$NON-NLS-1$
        exportDirectoryButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                chooseExportDirectory();
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
        // Filling the Schemas table
        if ( schemaHandler != null )
        {
            List<Schema> schemas = new ArrayList<Schema>();
            schemas.addAll( schemaHandler.getSchemas() );

            Collections.sort( schemas, new Comparator<Schema>()
            {
                public int compare( Schema o1, Schema o2 )
                {
                    return o1.getSchemaName().compareToIgnoreCase( o2.getSchemaName() );
                }
            } );

            schemasTableViewer.setInput( schemas );

            // Setting the selected schemas
            schemasTableViewer.setCheckedElements( selectedSchemas );
        }

        displayErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * This method is called when the exportMultipleFiles 'browse' button is selected.
     */
    private void chooseExportDirectory()
    {
        DirectoryDialog dialog = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        dialog.setText( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.ChooseFolder" ) ); //$NON-NLS-1$
        dialog.setMessage( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.SelectFolderToExport" ) ); //$NON-NLS-1$
        if ( "".equals( exportDirectoryText.getText() ) ) //$NON-NLS-1$
        {
            dialog.setFilterPath( Activator.getDefault().getPreferenceStore().getString(
                PluginConstants.FILE_DIALOG_EXPORT_SCHEMAS_OPENLDAP ) );
        }
        else
        {
            dialog.setFilterPath( exportDirectoryText.getText() );
        }

        String selectedDirectory = dialog.open();
        if ( selectedDirectory != null )
        {
            exportDirectoryText.setText( selectedDirectory );
        }
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Checking if a Schema Project is open
        if ( schemaHandler == null )
        {
            displayErrorMessage( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.ErrorNoOpenSchemaProject" ) ); //$NON-NLS-1$
            return;
        }

        // Schemas table
        if ( schemasTableViewer.getCheckedElements().length == 0 )
        {
            displayErrorMessage( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.ErrorNoSchemaSelected" ) ); //$NON-NLS-1$
            return;
        }

        // Export Directory
        String directory = exportDirectoryText.getText();
        if ( ( directory == null ) || ( directory.equals( "" ) ) ) //$NON-NLS-1$
        {
            displayErrorMessage( Messages.getString( "ExportSchemasAsOpenLdapWizardPage.ErrorNotDirectorySelected" ) ); //$NON-NLS-1$
            return;
        }
        else
        {
            File directoryFile = new File( directory );
            if ( !directoryFile.exists() )
            {
                displayErrorMessage( Messages
                    .getString( "ExportSchemasAsOpenLdapWizardPage.ErrorSelectedDirectoryNotExists" ) ); //$NON-NLS-1$
                return;
            }
            else if ( !directoryFile.isDirectory() )
            {
                displayErrorMessage( Messages
                    .getString( "ExportSchemasAsOpenLdapWizardPage.ErrorSelectedDirectoryNotDirectory" ) ); //$NON-NLS-1$
                return;
            }
            else if ( !directoryFile.canWrite() )
            {
                displayErrorMessage( Messages
                    .getString( "ExportSchemasAsOpenLdapWizardPage.ErrorSelectedDirectoryNotWritable" ) ); //$NON-NLS-1$
                return;
            }
        }

        displayErrorMessage( null );
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
     * Gets the export directory.
     *
     * @return
     *      the export directory
     */
    public String getExportDirectory()
    {
        return exportDirectoryText.getText();
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        Activator.getDefault().getPreferenceStore().putValue( PluginConstants.FILE_DIALOG_EXPORT_SCHEMAS_OPENLDAP,
            exportDirectoryText.getText() );
    }
}
