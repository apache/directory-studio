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


import java.lang.reflect.InvocationTargetException;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginUtils;
import org.apache.directory.studio.apacheds.schemaeditor.controller.ProjectsHandler;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project;
import org.apache.directory.studio.apacheds.schemaeditor.model.ProjectType;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to create a new Project.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewProjectWizard extends Wizard implements INewWizard
{
    public static final String ID = Activator.PLUGIN_ID + ".wizards.NewProjectWizard";

    // The pages of the wizard
    private NewProjectWizardInformationPage informationPage;
    private NewProjectWizardConnectionSelectionPage connectionSelectionPage;
    private NewProjectWizardSchemasSelectionPage schemasSelectionPage;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        // Creating pages
        informationPage = new NewProjectWizardInformationPage();
        connectionSelectionPage = new NewProjectWizardConnectionSelectionPage();
        schemasSelectionPage = new NewProjectWizardSchemasSelectionPage();

        // Adding pages
        addPage( informationPage );
        addPage( connectionSelectionPage );
        addPage( schemasSelectionPage );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        final Project project = new Project( informationPage.getProjectType(), informationPage.getProjectName() );
        if ( informationPage.getProjectType().equals( ProjectType.APACHE_DIRECTORY_SERVER ) )
        {
            Connection connection = connectionSelectionPage.getSelectedConnection();
            project.setConnection( connection );
            try
            {
                getContainer().run( true, true, new IRunnableWithProgress()
                {
                    public void run( IProgressMonitor monitor )
                    {
                        project.fetchOnlineSchema( new StudioProgressMonitor( monitor ) );
                    }
                } );
            }
            catch ( InvocationTargetException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch ( InterruptedException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if ( informationPage.getProjectType().equals( ProjectType.OFFLINE ) )
        {
            String[] selectedSchemas = schemasSelectionPage.getSelectedSchemas();
            if ( selectedSchemas != null )
            {
                for ( String selectedSchema : selectedSchemas )
                {
                    Schema schema = PluginUtils.loadCoreSchema( selectedSchema );
                    if ( schema != null )
                    {
                        project.getSchemaHandler().addSchema( schema );
                    }
                }
            }
        }

        ProjectsHandler projectsHandler = Activator.getDefault().getProjectsHandler();
        projectsHandler.addProject( project );
        projectsHandler.openProject( project );

        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    public IWizardPage getNextPage( IWizardPage page )
    {
        if ( page.equals( informationPage ) )
        {
            if ( informationPage.getProjectType().equals( ProjectType.APACHE_DIRECTORY_SERVER ) )
            {
                return connectionSelectionPage;
            }
            else if ( informationPage.getProjectType().equals( ProjectType.OFFLINE ) )
            {
                return schemasSelectionPage;
            }

            // Default
            return null;
        }

        // Default
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    public IWizardPage getPreviousPage( IWizardPage page )
    {
        if ( ( page.equals( connectionSelectionPage ) ) || ( page.equals( schemasSelectionPage ) ) )
        {
            return informationPage;
        }

        // Default
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    public boolean canFinish()
    {
        IWizardPage currentPage = getContainer().getCurrentPage();

        if ( currentPage.equals( informationPage ) )
        {
            return false;
        }
        else if ( currentPage.equals( schemasSelectionPage ) )
        {
            return true;
        }
        else if ( currentPage.equals( connectionSelectionPage ) )
        {
            return connectionSelectionPage.isPageComplete();
        }
        else
        {
            return false;
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor( true );
    }
}
