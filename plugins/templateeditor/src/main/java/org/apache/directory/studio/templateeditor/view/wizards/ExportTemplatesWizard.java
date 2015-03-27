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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;
import org.apache.directory.studio.templateeditor.model.Template;
import org.apache.directory.studio.templateeditor.model.parser.TemplateIO;


/**
 * This class implements the wizard for exporting templates to the disk.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportTemplatesWizard extends Wizard implements IImportWizard
{
    /** The wizard page */
    private ExportTemplatesWizardPage page;

    /** The pre-checked objects */
    private Object[] preCheckedObjects;


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        page = new ExportTemplatesWizardPage( preCheckedObjects );
        addPage( page );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        // Saving the dialog settings
        page.saveDialogSettings();

        // Getting the selected templates and export directory
        final Template[] selectedTemplates = page.getSelectedTemplates();
        final File exportDirectory = new File( page.getExportDirectory() );

        // Creating a list where all the template files that could not be
        // exported will be stored
        final List<Template> failedTemplates = new ArrayList<Template>();

        if ( selectedTemplates != null )
        {
            try
            {
                getContainer().run( false, false, new IRunnableWithProgress()
                {
                    public void run( IProgressMonitor monitor )
                    {
                        for ( Template selectedTemplate : selectedTemplates )
                        {
                            try
                            {
                                // Creating the output stream
                                FileOutputStream fos = new FileOutputStream( new File( exportDirectory,
                                    selectedTemplate.getId() + ".xml" ) ); //$NON-NLS-1$

                                // Exporting the template
                                TemplateIO.save( selectedTemplate, fos );
                                fos.close();
                            }
                            catch ( FileNotFoundException e )
                            {
                                // Logging the error
                                EntryTemplatePluginUtils
                                    .logError(
                                        e,
                                        Messages
                                            .getString( "ExportTemplatesWizard.TheTemplateCouldNotBeExportedBecauseOfTheFollowingError" ), //$NON-NLS-1$
                                        selectedTemplate.getTitle(), selectedTemplate.getId(), e.getMessage() );

                                // Adding the template to the failed templates list
                                failedTemplates.add( selectedTemplate );
                            }
                            catch ( IOException e )
                            {
                                // Logging the error
                                EntryTemplatePluginUtils
                                    .logError(
                                        e,
                                        Messages
                                            .getString( "ExportTemplatesWizard.TheTemplateCouldNotBeExportedBecauseOfTheFollowingError" ), //$NON-NLS-1$
                                        selectedTemplate.getTitle(), selectedTemplate.getId(), e.getMessage() );

                                // Adding the template to the failed templates list
                                failedTemplates.add( selectedTemplate );
                            }
                        }
                    }
                } );
            }
            catch ( InvocationTargetException e )
            {
                // Nothing to do (it will never occur)
            }
            catch ( InterruptedException e )
            {
                // Nothing to do.
            }
        }

        // Handling the templates that could not be exported
        if ( failedTemplates.size() > 0 )
        {
            String title = null;
            String message = null;

            // Only one template could not be imported
            if ( failedTemplates.size() == 1 )
            {
                // Getting the failed template
                Template failedTemplate = failedTemplates.get( 0 );

                // Creating the title and message
                title = Messages.getString( "ExportTemplatesWizard.ATemplateCouldNotBeExported" ); //$NON-NLS-1$
                message = MessageFormat.format( Messages
                    .getString( "ExportTemplatesWizard.TheTemplateCouldNotBeExported" ), failedTemplate //$NON-NLS-1$
                    .getTitle(), failedTemplate.getId() );
            }
            // Several templates could not be imported
            else
            {
                title = Messages.getString( "ExportTemplatesWizard.SeveralTemplatesCouldNotBeExported" ); //$NON-NLS-1$
                message = Messages.getString( "ExportTemplatesWizard.TheFollowingTemplatesCouldNotBeExported" ); //$NON-NLS-1$
                for ( Template failedTemplate : failedTemplates )
                {
                    message += EntryTemplatePluginUtils.LINE_SEPARATOR + "    - " //$NON-NLS-1$
                        + MessageFormat.format( "{0} ({1})", failedTemplate.getTitle(), failedTemplate.getId() ); //$NON-NLS-1$
                }
            }

            // Common ending message
            message += EntryTemplatePluginUtils.LINE_SEPARATOR + EntryTemplatePluginUtils.LINE_SEPARATOR
                + Messages.getString( "ExportTemplatesWizard.SeeTheLogsFileForMoreInformation" ); //$NON-NLS-1$

            // Creating and opening the dialog
            MessageDialog dialog = new MessageDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                title, null, message, MessageDialog.ERROR, new String[]
                    { IDialogConstants.OK_LABEL }, MessageDialog.OK );
            dialog.open();
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // Nothing to do.
    }


    /**
     * Sets the objects that should be pre-checked.
     *
     * @param objects
     *      the pre-checked objects
     */
    public void setPreCheckedObjects( Object[] objects )
    {
        this.preCheckedObjects = objects;
    }
}
