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
import org.apache.directory.studio.templateeditor.view.preferences.PreferencesTemplatesManager;


/**
 * This class implements the wizard for importing new templates from the disk.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportTemplatesWizard extends Wizard implements IImportWizard
{
    /** The wizard page */
    private ImportTemplatesWizardPage page;

    /** The templates manager */
    private PreferencesTemplatesManager manager;


    /**
     * Creates a new instance of ImportTemplatesWizard.
     *
     */
    public ImportTemplatesWizard( PreferencesTemplatesManager manager )
    {
        this.manager = manager;
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        page = new ImportTemplatesWizardPage();
        addPage( page );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        // Saving the dialog settings
        page.saveDialogSettings();

        // Getting the templates to be imported
        final File[] selectedTemplateFiles = page.getSelectedTemplateFiles();

        // Creating a list where all the template files that could not be
        // imported will be stored
        final List<File> failedTemplates = new ArrayList<File>();

        // Running the code to add the templates in a separate container
        // with progress monitor
        try
        {
            getContainer().run( false, false, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor )
                {
                    for ( File selectedTemplateFile : selectedTemplateFiles )
                    {
                        if ( !manager.addTemplate( selectedTemplateFile ) )
                        {
                            failedTemplates.add( selectedTemplateFile );
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

        // Handling the templates that could not be added
        if ( failedTemplates.size() > 0 )
        {
            String title = null;
            String message = null;

            // Only one template could not be imported
            if ( failedTemplates.size() == 1 )
            {
                title = Messages.getString( "ImportTemplatesWizard.ATemplateCouldNotBeImported" ); //$NON-NLS-1$
                message = MessageFormat.format( Messages
                    .getString( "ImportTemplatesWizard.TheTemplateCouldNotBeImported" ), failedTemplates.get( 0 ) //$NON-NLS-1$
                    .getAbsolutePath() );
            }
            // Several templates could not be imported
            else
            {
                title = Messages.getString( "ImportTemplatesWizard.SeveralTemplatesCouldNotBeImported" ); //$NON-NLS-1$
                message = Messages.getString( "ImportTemplatesWizard.TheFollowingTemplatesCouldNotBeImported" ); //$NON-NLS-1$
                for ( File failedTemplate : failedTemplates )
                {
                    message += EntryTemplatePluginUtils.LINE_SEPARATOR + "    - " + failedTemplate.getAbsolutePath(); //$NON-NLS-1$
                }
            }

            // Common ending message
            message += EntryTemplatePluginUtils.LINE_SEPARATOR + EntryTemplatePluginUtils.LINE_SEPARATOR
                + Messages.getString( "ImportTemplatesWizard.SeeTheLogsFileForMoreInformation" ); //$NON-NLS-1$

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
}
