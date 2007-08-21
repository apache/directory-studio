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
package org.apache.directory.studio.apacheds.schemaeditor.view.wizards;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginUtils;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project;
import org.apache.directory.studio.apacheds.schemaeditor.model.io.ProjectsExporter;
import org.apache.directory.studio.apacheds.schemaeditor.view.ViewUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to export schema projects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportProjectsWizard extends Wizard implements IExportWizard
{
    public static final String ID = Activator.PLUGIN_ID + ".wizards.ExportProjectsWizard";

    /** The selected projects */
    private Project[] selectedProjects = new Project[0];

    // The pages of the wizard
    private ExportProjectsWizardPage page;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        // Creating pages
        page = new ExportProjectsWizardPage();
        page.setSelectedProjects( selectedProjects );

        // Adding pages
        addPage( page );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        final Project[] selectedProjects = page.getSelectedProjects();

        final String exportDirectory = page.getExportDirectory();
        try
        {
            getContainer().run( true, true, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor )
                {
                    monitor.beginTask( "Exporting project: ", selectedProjects.length );
                    for ( Project project : selectedProjects )
                    {
                        monitor.subTask( project.getName() );

                        try
                        {
                            BufferedWriter buffWriter = new BufferedWriter( new FileWriter( exportDirectory + "/"
                                + project.getName() + ".schemaproject" ) );
                            buffWriter.write( ProjectsExporter.toXml( project ) );
                            buffWriter.close();
                        }
                        catch ( IOException e )
                        {
                            PluginUtils.logError( "An error occured when saving the project " + project.getName() + ".",
                                e );
                            ViewUtils.displayErrorMessageBox( "Error", "An error occured when saving the project "
                                + project.getName() + "." );
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
     * Sets the selected projects.
     *
     * @param projects
     *      the projects
     */
    public void setSelectedProjects( Project[] projects )
    {
        selectedProjects = projects;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }
}
