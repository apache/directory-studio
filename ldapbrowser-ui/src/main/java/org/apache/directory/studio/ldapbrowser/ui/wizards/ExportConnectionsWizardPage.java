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

import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.FileBrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


/**
 * This class implements the page used to select the connections to export.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportConnectionsWizardPage extends WizardPage
{
    // UI widgets
    //    private CheckboxTreeViewer connectionsTreeViewer;
    //    private ConnectionContentProvider contentProvider;
    private FileBrowserWidget fileBrowserWidget;
    private Button overwriteFileButton;


    /**
     * Creates a new instance of ExportConnectionsWizardPage.
     */
    protected ExportConnectionsWizardPage()
    {
        super( ExportConnectionsWizardPage.class.getName() );
        setTitle( Messages.getString( "ExportConnectionsWizardPage.ExportConnections" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "ExportConnectionsWizardPage.DefineConnectionsExport" ) ); //$NON-NLS-1$
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
            BrowserUIConstants.IMG_EXPORT_CONNECTIONS_WIZARD ) );
        setPageComplete( false );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        // Main Composite
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        //        // Connections Group
        //        Group connectionsGroup = BaseWidgetUtils.createGroup( composite, "Connections", 1 );
        //        Composite connectionsGroupComposite = BaseWidgetUtils.createColumnContainer( connectionsGroup, 2, 1 );
        //
        //        // Connections Label
        //        BaseWidgetUtils.createLabel( connectionsGroupComposite, "Select the connections to export: ", 1 );
        //        BaseWidgetUtils.createSpacer( connectionsGroupComposite, 1 );
        //
        //        // Connections TreeViewer
        //        connectionsTreeViewer = new CheckboxTreeViewer( new Tree( connectionsGroupComposite, SWT.BORDER | SWT.CHECK
        //            | SWT.FULL_SELECTION ) );
        //        GridData connectionsTableViewerGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
        //        connectionsTableViewerGridData.heightHint = 125;
        //        connectionsTreeViewer.getTree().setLayoutData( connectionsTableViewerGridData );
        //        contentProvider = new ConnectionContentProvider();
        //        connectionsTreeViewer.setContentProvider( contentProvider );
        //        connectionsTreeViewer.setLabelProvider( new ConnectionLabelProvider() );
        //        connectionsTreeViewer.setInput( ConnectionCorePlugin.getDefault().getConnectionFolderManager() );
        //        connectionsTreeViewer.addCheckStateListener( new ICheckStateListener()
        //        {
        //            public void checkStateChanged( CheckStateChangedEvent event )
        //            {
        //                Object checkedElement = event.getElement();
        //                Object[] children = contentProvider.getChildren( checkedElement );
        //                if ( ( children != null ) && ( children.length > 0 ) )
        //                {
        //                    for ( Object child : children )
        //                    {
        //                        connectionsTreeViewer.setChecked( child, event.getChecked() );
        //                    }
        //                }
        //            }
        //        } );
        //
        //        // Selection Buttons
        //        Button selectAllButton = BaseWidgetUtils.createButton( connectionsGroupComposite, "Select All", 1 );
        //        selectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        //        selectAllButton.addSelectionListener( new SelectionAdapter()
        //        {
        //            public void widgetSelected( SelectionEvent e )
        //            {
        //                connectionsTreeViewer.setAllChecked( true );
        //                validate();
        //            }
        //        } );
        //        Button deselectAllButton = BaseWidgetUtils.createButton( connectionsGroupComposite, "Deselect All", 1 );
        //        deselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        //        deselectAllButton.addSelectionListener( new SelectionAdapter()
        //        {
        //            public void widgetSelected( SelectionEvent e )
        //            {
        //                connectionsTreeViewer.setAllChecked( false );
        //                validate();
        //            }
        //        } );

        // Destination Group
        //        Group destinationGroup = BaseWidgetUtils.createGroup( composite, "Destination", 1 );
        //        Composite destinationGroupComposite = BaseWidgetUtils.createColumnContainer( destinationGroup, 3, 1 );

        // Destination File
        BaseWidgetUtils.createLabel( composite, Messages.getString( "ExportConnectionsWizardPage.ToFile" ), 1 ); //$NON-NLS-1$
        fileBrowserWidget = new FileBrowserWidget(
            Messages.getString( "ExportConnectionsWizardPage.ChooseFile" ), new String[] //$NON-NLS-1$
                { "lbc" }, FileBrowserWidget.TYPE_SAVE ); //$NON-NLS-1$
        fileBrowserWidget.createWidget( composite );
        fileBrowserWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );

        BaseWidgetUtils.createRadioIndent( composite, 1 );
        overwriteFileButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "ExportConnectionsWizardPage.OverwriteExistingFile" ), 2 ); //$NON-NLS-1$
        overwriteFileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                validate();
            }
        } );

        setControl( composite );
    }


    /**
     * Validates this page. This method is responsible for displaying errors, 
     * as well as enabling/disabling the "Finish" button
     */
    private void validate()
    {
        boolean ok = true;
        File file = new File( fileBrowserWidget.getFilename() );
        if ( "".equals( fileBrowserWidget.getFilename() ) ) //$NON-NLS-1$
        {
            setErrorMessage( null );
            ok = false;
        }
        else if ( file.isDirectory() )
        {
            setErrorMessage( Messages.getString( "ExportConnectionsWizardPage.ErrorFileNotAFile" ) ); //$NON-NLS-1$
            ok = false;
        }
        else if ( file.exists() && !overwriteFileButton.getSelection() )
        {
            setErrorMessage( Messages.getString( "ExportConnectionsWizardPage.ErrorFileAlreadyExists" ) ); //$NON-NLS-1$
            ok = false;
        }
        else if ( file.exists() && !file.canWrite() )
        {
            setErrorMessage( Messages.getString( "ExportConnectionsWizardPage.ErrorFileNotWritable" ) ); //$NON-NLS-1$
            ok = false;
        }
        else if ( file.getParentFile() == null )
        {
            setErrorMessage( Messages.getString( "ExportConnectionsWizardPage.ErrorFileDirectoryNotWritable" ) ); //$NON-NLS-1$
            ok = false;
        }

        if ( ok )
        {
            setErrorMessage( null );
        }

        setPageComplete( ok );
    }


    /**
     * Gets the export file name.
     * 
     * @return
     *      the export file name
     */
    public String getExportFileName()
    {
        return fileBrowserWidget.getFilename();
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        fileBrowserWidget.saveDialogSettings();
    }
}
