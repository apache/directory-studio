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
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.ProjectType;
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
 * This class represents the WizardPage of the ExportProjectsWizard.
 * <p>
 * It is used to let the user enter the informations about the
 * schemas projects he wants to export and where to export.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportProjectsWizardPage extends AbstractWizardPage
{
    /** The selected projects */
    private Project[] selectedProjects = new Project[0];

    // UI Fields
    private CheckboxTableViewer projectsTableViewer;
    private Button projectsTableSelectAllButton;
    private Button projectsTableDeselectAllButton;
    private Label exportDirectoryLabel;
    private Text exportDirectoryText;
    private Button exportDirectoryButton;


    /**
     * Creates a new instance of ExportSchemasAsXmlWizardPage.
     */
    protected ExportProjectsWizardPage()
    {
        super( "ExportProjectsWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "ExportProjectsWizardPage.ExportSchemaProjects" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "ExportProjectsWizardPage.PleaseSelectSchemaProjects" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_PROJECT_EXPORT_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Projects Group
        Group schemaProjectsGroup = new Group( composite, SWT.NONE );
        schemaProjectsGroup.setText( Messages.getString( "ExportProjectsWizardPage.SchemaProjects" ) ); //$NON-NLS-1$
        schemaProjectsGroup.setLayout( new GridLayout( 2, false ) );
        schemaProjectsGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Projects TableViewer
        Label projectsLabel = new Label( schemaProjectsGroup, SWT.NONE );
        projectsLabel.setText( Messages.getString( "ExportProjectsWizardPage.SelectSchemaProjects" ) ); //$NON-NLS-1$
        projectsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        projectsTableViewer = new CheckboxTableViewer( new Table( schemaProjectsGroup, SWT.BORDER | SWT.CHECK
            | SWT.FULL_SELECTION ) );
        GridData projectsTableViewerGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
        projectsTableViewerGridData.heightHint = 125;
        projectsTableViewer.getTable().setLayoutData( projectsTableViewerGridData );
        projectsTableViewer.setContentProvider( new ArrayContentProvider() );
        projectsTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof Project )
                {
                    return ( ( Project ) element ).getName();
                }

                // Default
                return super.getText( element );
            }


            public Image getImage( Object element )
            {
                if ( element instanceof Project )
                {
                    ProjectType type = ( ( Project ) element ).getType();
                    switch ( type )
                    {
                        case OFFLINE:
                            return Activator.getDefault().getImage( PluginConstants.IMG_PROJECT_OFFLINE_CLOSED );
                        case ONLINE:
                            return Activator.getDefault().getImage( PluginConstants.IMG_PROJECT_ONLINE_CLOSED );
                    }
                }

                // Default
                return super.getImage( element );
            }
        } );
        projectsTableViewer.addCheckStateListener( new ICheckStateListener()
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
        projectsTableSelectAllButton = new Button( schemaProjectsGroup, SWT.PUSH );
        projectsTableSelectAllButton.setText( Messages.getString( "ExportProjectsWizardPage.SelectAll" ) ); //$NON-NLS-1$
        projectsTableSelectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        projectsTableSelectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                projectsTableViewer.setAllChecked( true );
                dialogChanged();
            }
        } );
        projectsTableDeselectAllButton = new Button( schemaProjectsGroup, SWT.PUSH );
        projectsTableDeselectAllButton.setText( Messages.getString( "ExportProjectsWizardPage.DeselectAll" ) ); //$NON-NLS-1$
        projectsTableDeselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        projectsTableDeselectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                projectsTableViewer.setAllChecked( false );
                dialogChanged();
            }
        } );

        // Export Destination Group
        Group exportDestinationGroup = new Group( composite, SWT.NULL );
        exportDestinationGroup.setText( Messages.getString( "ExportProjectsWizardPage.ExportDestination" ) ); //$NON-NLS-1$
        exportDestinationGroup.setLayout( new GridLayout( 3, false ) );
        exportDestinationGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        exportDirectoryLabel = new Label( exportDestinationGroup, SWT.NONE );
        exportDirectoryLabel.setText( Messages.getString( "ExportProjectsWizardPage.Directory" ) ); //$NON-NLS-1$
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
        exportDirectoryButton.setText( Messages.getString( "ExportProjectsWizardPage.Browse" ) ); //$NON-NLS-1$
        exportDirectoryButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                chooseExportDirectory();
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
        List<Project> projects = new ArrayList<Project>();
        projects.addAll( Activator.getDefault().getProjectsHandler().getProjects() );
        Collections.sort( projects, new Comparator<Project>()
        {
            public int compare( Project o1, Project o2 )
            {
                return o1.getName().compareToIgnoreCase( o2.getName() );
            }

        } );
        projectsTableViewer.setInput( projects );

        // Setting the selected projects
        projectsTableViewer.setCheckedElements( selectedProjects );

        displayErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * This method is called when the exportMultipleFiles 'browse' button is selected.
     */
    private void chooseExportDirectory()
    {
        DirectoryDialog dialog = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        dialog.setText( Messages.getString( "ExportProjectsWizardPage.ChooseFolder" ) ); //$NON-NLS-1$
        dialog.setMessage( Messages.getString( "ExportProjectsWizardPage.SelectFolderToExportTo" ) ); //$NON-NLS-1$
        if ( "".equals( exportDirectoryText.getText() ) ) //$NON-NLS-1$
        {
            dialog.setFilterPath( Activator.getDefault().getPreferenceStore().getString(
                PluginConstants.FILE_DIALOG_EXPORT_PROJECTS ) );
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
        // Schemas table
        if ( projectsTableViewer.getCheckedElements().length == 0 )
        {
            displayErrorMessage( Messages.getString( "ExportProjectsWizardPage.ErrorNoSchemaSelected" ) ); //$NON-NLS-1$
            return;
        }

        // Export Directory
        String directory = exportDirectoryText.getText();
        if ( ( directory == null ) || ( directory.equals( "" ) ) ) //$NON-NLS-1$
        {
            displayErrorMessage( Messages.getString( "ExportProjectsWizardPage.ErrorNoDirectorySelected" ) ); //$NON-NLS-1$
            return;
        }
        else
        {
            File directoryFile = new File( directory );
            if ( !directoryFile.exists() )
            {
                displayErrorMessage( Messages.getString( "ExportProjectsWizardPage.SelectedDirectoryNotExists" ) ); //$NON-NLS-1$
                return;
            }
            else if ( !directoryFile.isDirectory() )
            {
                displayErrorMessage( Messages.getString( "ExportProjectsWizardPage.SelectedDirectoryNotDirectory" ) ); //$NON-NLS-1$
                return;
            }
            else if ( !directoryFile.canWrite() )
            {
                displayErrorMessage( Messages.getString( "ExportProjectsWizardPage.SelectedDirectoryNotWritable" ) ); //$NON-NLS-1$
                return;
            }
        }

        displayErrorMessage( null );
    }


    /**
     * Gets the selected projects.
     *
     * @return
     *      the selected projects
     */
    public Project[] getSelectedProjects()
    {
        Object[] selectedProjects = projectsTableViewer.getCheckedElements();

        List<Project> schemas = new ArrayList<Project>();
        for ( Object project : selectedProjects )
        {
            schemas.add( ( Project ) project );
        }

        return schemas.toArray( new Project[0] );
    }


    /**
     * Sets the selected projects.
     *
     * @param projects
     *      the projects
     */
    public void setSelectedProjects( Project[] projects )
    {
        selectedProjects = projects;
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
        Activator.getDefault().getPreferenceStore().putValue( PluginConstants.FILE_DIALOG_EXPORT_PROJECTS,
            exportDirectoryText.getText() );
    }
}
