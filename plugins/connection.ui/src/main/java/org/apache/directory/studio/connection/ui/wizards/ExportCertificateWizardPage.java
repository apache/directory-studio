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

package org.apache.directory.studio.connection.ui.wizards;


import java.io.File;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Export Certificate wizard page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportCertificateWizardPage extends WizardPage
{
    // UI widgets
    private Text fileText;
    private Button browseButton;
    private Button overwriteFileButton;
    private ComboViewer formatComboViewer;


    /**
     * Creates a new instance of NewConnectionWizard.
     * 
     * @param page the page
     * @param wizard the wizard
     */
    public ExportCertificateWizardPage()
    {
        super( "ExportCertificateWizardPage" );
        setTitle( Messages.getString( "ExportCertificateWizardPage.ExportCertificate" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "ExportCertificateWizardPage.PleaseSelectFileAndFormat" ) ); //$NON-NLS-1$ 
        setImageDescriptor( ConnectionUIPlugin.getDefault().getImageDescriptor(
            ConnectionUIConstants.IMG_CERTIFICATE_EXPORT_WIZARD ) );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        // Creating the composite
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Creating the file's group widget
        Group fileGroup = BaseWidgetUtils.createGroup( composite,
            Messages.getString( "ExportCertificateWizardPage.File" ), 1 ); //$NON-NLS-1$
        fileGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite fileComposite = BaseWidgetUtils.createColumnContainer( fileGroup, 2, 1 );
        fileComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Creating the file's text widget
        fileText = BaseWidgetUtils.createText( fileComposite, "", 1 );
        fileText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        fileText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        // Creating the file's 'Browse' button widget
        browseButton = BaseWidgetUtils.createButton( fileComposite,
            Messages.getString( "ExportCertificateWizardPage.Browse" ), 1 ); //$NON-NLS-1$
        browseButton.addSelectionListener( new SelectionAdapter()
        {
            /**
             * {@inheritDoc}
             */
            public void widgetSelected( SelectionEvent e )
            {
                chooseExportFile();
                validate();
            }
        } );

        // Creating the file's 'Overwrite' button widget
        overwriteFileButton = BaseWidgetUtils.createCheckbox( fileComposite,
            Messages.getString( "ExportCertificateWizardPage.OverwriteExistingFile" ), 2 ); //$NON-NLS-1$
        overwriteFileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                validate();
            }
        } );

        // Creating the format's group widget
        Group formatGroup = BaseWidgetUtils.createGroup( composite,
            Messages.getString( "ExportCertificateWizardPage.Format" ), 1 ); //$NON-NLS-1$
        formatGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Creating the format's combo viewer widget
        formatComboViewer = new ComboViewer( formatGroup );
        formatComboViewer.setContentProvider( new ArrayContentProvider() );
        formatComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof CertificateExportFormat )
                {
                    CertificateExportFormat format = ( CertificateExportFormat ) element;

                    switch ( format )
                    {
                        case DER:
                            return "X509 Certificat DER";
                        case PEM:
                            return "X509 Certificat PEM";
                    }
                }

                return super.getText( element );
            }
        } );
        formatComboViewer.setInput( new CertificateExportFormat[]
            { CertificateExportFormat.DER, CertificateExportFormat.PEM } );
        formatComboViewer.setSelection( new StructuredSelection( CertificateExportFormat.DER ) );
        formatComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        displayErrorMessage( null );
        setPageComplete( false );

        setControl( composite );
    }


    /**
     * Validates the page.
     */
    private void validate()
    {
        File file = new File( fileText.getText() );
        if ( file.isDirectory() )
        {
            displayErrorMessage( Messages.getString( "ExportCertificateWizardPage.ErrorFileNotAFile" ) ); //$NON-NLS-1$
            return;
        }
        else if ( file.exists() && !overwriteFileButton.getSelection() )
        {
            displayErrorMessage( Messages.getString( "ExportCertificateWizardPage.ErrorFileAlreadyExists" ) ); //$NON-NLS-1$
            return;
        }
        else if ( file.exists() && !file.canWrite() )
        {
            displayErrorMessage( Messages.getString( "ExportCertificateWizardPage.ErrorFileNotWritable" ) ); //$NON-NLS-1$
            return;
        }
        else if ( file.getParentFile() == null )
        {
            displayErrorMessage( Messages.getString( "ExportCertificateWizardPage.ErrorFileDirectoryNotWritable" ) ); //$NON-NLS-1$
            return;
        }

        displayErrorMessage( null );
    }


    /**
     * Displays an error message and set the page status as incomplete
     * if the message is not null.
     *
     * @param message the message to display
     */
    protected void displayErrorMessage( String message )
    {
        setMessage( null, DialogPage.NONE );
        setErrorMessage( message );
        setPageComplete( message == null );
    }


    /**
     * This method is called when the 'browse' button is selected.
     */
    private void chooseExportFile()
    {
        FileDialog dialog = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE );
        dialog.setText( Messages.getString( "ExportCertificateWizardPage.ChooseFile" ) ); //$NON-NLS-1$
        if ( !"".equals( fileText.getText() ) ) //$NON-NLS-1$
        {
            dialog.setFilterPath( fileText.getText() );
        }

        String selectedFile = dialog.open();
        if ( selectedFile != null )
        {
            fileText.setText( selectedFile );
        }
    }


    /**
     * Returns the export file.
     *
     * @return the export file
     */
    public File getExportFile()
    {
        return new File( fileText.getText() );
    }


    /**
     * Gets the certificate export format.
     *
     * @return the certificate export format
     */
    public CertificateExportFormat getCertificateExportFormat()
    {
        StructuredSelection selection = ( StructuredSelection ) formatComboViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            return ( CertificateExportFormat ) selection.getFirstElement();
        }

        // Default format
        return CertificateExportFormat.DER;
    }

    /**
     * This enum represents the various certificate export formats.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    enum CertificateExportFormat
    {
        DER, PEM
    }
}