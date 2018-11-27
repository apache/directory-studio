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
package org.apache.directory.studio.templateeditor.view.wizards;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
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

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;


/**
 * This class implements the wizard page for the wizard for importing new 
 * templates from the disk.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportTemplatesWizardPage extends AbstractWizardPage
{
    // UI Fields
    private Text fromDirectoryText;
    private Button fromDirectoryButton;
    private CheckboxTableViewer templateFilesTableViewer;
    private Button templateFilesTableSelectAllButton;
    private Button templateFilesTableDeselectAllButton;


    /**
     * Creates a new instance of ImportTemplatesWizardPage.
     */
    public ImportTemplatesWizardPage()
    {
        super( "ImportTemplatesWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "ImportTemplatesWizardPage.WizardPageTitle" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "ImportTemplatesWizardPage.WizardPageDescription" ) ); //$NON-NLS-1$
        setImageDescriptor( EntryTemplatePlugin.getDefault().getImageDescriptor(
            EntryTemplatePluginConstants.IMG_IMPORT_TEMPLATES_WIZARD ) );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // From Directory Group
        Group fromDirectoryGroup = new Group( composite, SWT.NONE );
        fromDirectoryGroup.setText( Messages.getString( "ImportTemplatesWizardPage.FromDirectory" ) ); //$NON-NLS-1$
        fromDirectoryGroup.setLayout( new GridLayout( 3, false ) );
        fromDirectoryGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // From Directory
        Label fromDirectoryLabel = new Label( fromDirectoryGroup, SWT.NONE );
        fromDirectoryLabel.setText( Messages.getString( "ImportTemplatesWizardPage.FromDirectoryColon" ) ); //$NON-NLS-1$
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
        fromDirectoryButton.setText( Messages.getString( "ImportTemplatesWizardPage.Browse" ) ); //$NON-NLS-1$
        fromDirectoryButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                chooseFromDirectory();
            }
        } );

        // Template files Group
        Group templatesFilesGroup = new Group( composite, SWT.NONE );
        templatesFilesGroup.setText( Messages.getString( "ImportTemplatesWizardPage.TemplateFiles" ) ); //$NON-NLS-1$
        templatesFilesGroup.setLayout( new GridLayout( 2, false ) );
        templatesFilesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Template Files Viewer
        templateFilesTableViewer = new CheckboxTableViewer( new Table( templatesFilesGroup, SWT.BORDER | SWT.CHECK
            | SWT.FULL_SELECTION ) );
        GridData templateFilesTableViewerGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
        templateFilesTableViewerGridData.heightHint = 125;
        templateFilesTableViewer.getTable().setLayoutData( templateFilesTableViewerGridData );
        templateFilesTableViewer.setContentProvider( new ArrayContentProvider() );
        templateFilesTableViewer.setLabelProvider( new LabelProvider()
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
                    return EntryTemplatePlugin.getDefault().getImage( EntryTemplatePluginConstants.IMG_TEMPLATE );
                }

                // Default
                return super.getImage( element );
            }
        } );
        templateFilesTableViewer.addCheckStateListener( new ICheckStateListener()
        {
            public void checkStateChanged( CheckStateChangedEvent event )
            {
                dialogChanged();
            }
        } );
        templateFilesTableSelectAllButton = new Button( templatesFilesGroup, SWT.PUSH );
        templateFilesTableSelectAllButton.setText( Messages.getString( "ImportTemplatesWizardPage.SelectAll" ) ); //$NON-NLS-1$
        templateFilesTableSelectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        templateFilesTableSelectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                templateFilesTableViewer.setAllChecked( true );
                dialogChanged();
            }
        } );
        templateFilesTableDeselectAllButton = new Button( templatesFilesGroup, SWT.PUSH );
        templateFilesTableDeselectAllButton.setText( Messages.getString( "ImportTemplatesWizardPage.DeselectAll" ) ); //$NON-NLS-1$
        templateFilesTableDeselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        templateFilesTableDeselectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                templateFilesTableViewer.setAllChecked( false );
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
     * This method is called when the 'Browse...' button is selected.
     */
    private void chooseFromDirectory()
    {
        DirectoryDialog dialog = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        dialog.setText( Messages.getString( "ImportTemplatesWizardPage.ChooseFolder" ) ); //$NON-NLS-1$
        dialog.setMessage( Messages.getString( "ImportTemplatesWizardPage.SelectTheFolderFromWhichImportTheFiles" ) ); //$NON-NLS-1$
        if ( "".equals( fromDirectoryText.getText() ) ) //$NON-NLS-1$
        {
            dialog.setFilterPath( EntryTemplatePlugin.getDefault().getPreferenceStore().getString(
                EntryTemplatePluginConstants.DIALOG_IMPORT_TEMPLATES ) );
        }
        else
        {
            dialog.setFilterPath( fromDirectoryText.getText() );
        }

        String selectedDirectory = dialog.open();
        if ( selectedDirectory != null )
        {
            fromDirectoryText.setText( selectedDirectory );
            fillInTemplatesTable( selectedDirectory );
        }
    }


    /**
     * Fills in the templates table with the files found in the given path.
     *
     * @param path
     *      the path to search schema files in
     */
    private void fillInTemplatesTable( String path )
    {
        List<File> files = new ArrayList<File>();
        File selectedDirectory = new File( path );
        if ( selectedDirectory.exists() )
        {
            // Filter for xml files
            FilenameFilter filter = new FilenameFilter()
            {
                public boolean accept( File dir, String name )
                {
                    return name.endsWith( ".xml" ); //$NON-NLS-1$
                }
            };

            for ( File file : selectedDirectory.listFiles( filter ) )
            {
                files.add( file );
            }
        }

        templateFilesTableViewer.setInput( files );
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Templates table
        if ( templateFilesTableViewer.getCheckedElements().length == 0 )
        {
            displayErrorMessage( Messages
                .getString( "ImportTemplatesWizardPage.OneOrSeveralTemplateFilesMustBeSelected" ) ); //$NON-NLS-1$
            return;
        }

        displayErrorMessage( null );
    }


    /**
     * Gets the selected template files.
     *
     * @return
     *      the selected templates files
     */
    public File[] getSelectedTemplateFiles()
    {
        Object[] selectedTemplateFiles = templateFilesTableViewer.getCheckedElements();

        List<File> templateFiles = new ArrayList<File>();
        for ( Object selectedTemplateFile : selectedTemplateFiles )
        {
            templateFiles.add( ( File ) selectedTemplateFile );
        }

        return templateFiles.toArray( new File[0] );
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        EntryTemplatePlugin.getDefault().getPreferenceStore().putValue(
            EntryTemplatePluginConstants.DIALOG_IMPORT_TEMPLATES, fromDirectoryText.getText() );
    }
}
