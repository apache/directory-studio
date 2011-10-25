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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandler;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.io.ProjectsImportException;
import org.apache.directory.studio.schemaeditor.model.io.ProjectsImporter;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to import schema projects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportProjectsWizard extends Wizard implements IImportWizard
{
    // The pages of the wizard
    private ImportProjectsWizardPage page;

    /** The ProjectsHandler */
    private ProjectsHandler projectsHandler;


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        // Creating pages
        page = new ImportProjectsWizardPage();

        // Adding pages
        addPage( page );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        // Saving the dialog settings
        page.saveDialogSettings();

        // Getting the projects to be imported
        final File[] selectedProjectFiles = page.getSelectedProjectFiles();
        try
        {
            getContainer().run( false, false, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor )
                {
                    monitor.beginTask(
                        Messages.getString( "ImportProjectsWizard.ImportingProjects" ), selectedProjectFiles.length ); //$NON-NLS-1$

                    for ( File projectFile : selectedProjectFiles )
                    {
                        monitor.subTask( projectFile.getName() );
                        try
                        {
                            Project project = ProjectsImporter.getProject( new FileInputStream( projectFile ),
                                projectFile.getAbsolutePath() );
                            projectsHandler.addProject( project );
                        }
                        catch ( ProjectsImportException e )
                        {
                            PluginUtils
                                .logError(
                                    NLS
                                        .bind(
                                            Messages.getString( "ImportProjectsWizard.ErrorImportingProject" ), new String[] { projectFile.getName() } ), e ); //$NON-NLS-1$
                            ViewUtils
                                .displayErrorMessageBox(
                                    Messages.getString( "ImportProjectsWizard.ImportError" ), //$NON-NLS-1$
                                    NLS
                                        .bind(
                                            Messages.getString( "ImportProjectsWizard.ErrorImportingProject" ), new String[] { projectFile.getName() } ) ); //$NON-NLS-1$
                        }
                        catch ( FileNotFoundException e )
                        {
                            PluginUtils
                                .logError(
                                    NLS
                                        .bind(
                                            Messages.getString( "ImportProjectsWizard.ErrorImportingProject" ), new String[] { projectFile.getName() } ), e ); //$NON-NLS-1$
                            ViewUtils
                                .displayErrorMessageBox(
                                    Messages.getString( "ImportProjectsWizard.ImportError" ), //$NON-NLS-1$
                                    NLS
                                        .bind(
                                            Messages.getString( "ImportProjectsWizard.ErrorImportingProject" ), new String[] { projectFile.getName() } ) ); //$NON-NLS-1$
                        }
                        monitor.worked( 1 );
                    }

                    monitor.done();
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

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor( true );

        projectsHandler = Activator.getDefault().getProjectsHandler();
    }
}
