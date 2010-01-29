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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


/**
 * This class is a base implementation of the page to select the target export file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class ExportBaseToPage extends WizardPage
{

    /** The wizard. */
    protected ExportBaseWizard wizard;

    /** The file browser widget. */
    protected FileBrowserWidget fileBrowserWidget;

    /** The overwrite file button. */
    protected Button overwriteFileButton;


    /**
     * Creates a new instance of ExportBaseToPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public ExportBaseToPage( String pageName, ExportBaseWizard wizard )
    {
        super( pageName );
        setPageComplete( false );
        setTitle( NLS.bind( Messages.getString( "ExportBaseToPage.FileType" ), getFileType() ) ); //$NON-NLS-1$
        setDescription( NLS.bind( Messages.getString( "ExportBaseToPage.PleaseEnterTargetFile" ), getFileType() ) ); //$NON-NLS-1$

        this.wizard = wizard;
    }


    /**
     * Validates this page. This method is responsible for displaying errors, 
     * as well as enabling/disabling the "Finish" button
     */
    protected void validate()
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
            setErrorMessage( NLS.bind(
                Messages.getString( "ExportBaseToPage.ErrorNotAFile" ), new String[] { getFileType() } ) ); //$NON-NLS-1$
            ok = false;
        }
        else if ( file.exists() && !overwriteFileButton.getSelection() )
        {
            setErrorMessage( NLS
                .bind(
                    Messages.getString( "ExportBaseToPage.ErrorFileExists" ), new String[] { getFileType(), getFileType(), getFileType() } ) ); //$NON-NLS-1$
            ok = false;
        }
        else if ( file.exists() && !file.canWrite() )
        {
            setErrorMessage( NLS.bind(
                Messages.getString( "ExportBaseToPage.ErrorFileNotWritable" ), new String[] { getFileType() } ) ); //$NON-NLS-1$
            ok = false;
        }
        else if ( file.getParentFile() == null )
        {
            setErrorMessage( NLS.bind(
                Messages.getString( "ExportBaseToPage.ErrorDirectoryNotWritable" ), new String[] { getFileType() } ) ); //$NON-NLS-1$
            ok = false;
        }

        if ( ok )
        {
            setErrorMessage( null );
        }

        setPageComplete( ok && wizard.getExportFilename() != null && !"".equals( wizard.getExportFilename() ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite composite )
    {
        // Export file
        BaseWidgetUtils.createLabel( composite, NLS.bind(
            Messages.getString( "ExportBaseToPage.FileTypeColon" ), getFileType() ), 1 ); //$NON-NLS-1$
        fileBrowserWidget = new FileBrowserWidget( NLS.bind(
            Messages.getString( "ExportBaseToPage.SelectFileType" ), new String[] { getFileType() } ), getExtensions(), //$NON-NLS-1$
            FileBrowserWidget.TYPE_SAVE );
        fileBrowserWidget.createWidget( composite );
        fileBrowserWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                wizard.setExportFilename( fileBrowserWidget.getFilename() );
                validate();
            }
        } );
        BaseWidgetUtils.createRadioIndent( composite, 1 );
        overwriteFileButton = BaseWidgetUtils.createCheckbox( composite, NLS.bind( Messages
            .getString( "ExportBaseToPage.OverwriteExistingFile" ), new String[] { getFileType() } ), 2 ); //$NON-NLS-1$
        overwriteFileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                validate();
            }
        } );

        fileBrowserWidget.setFocus();
        setControl( composite );
        validate();
    }


    /**
     * Gets the valid file extensions.
     * 
     * @return the valid file extensions
     */
    protected abstract String[] getExtensions();


    /**
     * Gets the file type.
     * 
     * @return the file type
     */
    protected abstract String getFileType();


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        fileBrowserWidget.saveDialogSettings();
    }

}
