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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.ProjectType;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.io.GenericSchemaConnector;
import org.apache.directory.studio.schemaeditor.model.io.SchemaConnector;
import org.apache.directory.studio.schemaeditor.view.widget.CoreSchemasSelectionWidget.ServerTypeEnum;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to create a new Project.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewProjectWizard extends Wizard implements INewWizard
{
    public static final String ID = PluginConstants.NEW_WIZARD_NEW_PROJECT_WIZARD;

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
        String projectName = informationPage.getProjectName();
        ProjectType projectType = informationPage.getProjectType();

        // Creating the project
        final Project project = new Project( projectType, projectName );

        if ( projectType.equals( ProjectType.ONLINE ) )
        // Project is an "Online Project"
        {
            // Setting the connection to use
            project.setConnection( connectionSelectionPage.getSelectedConnection() );

            RunnableContextRunner.execute( new StudioRunnableWithProgress()
            {

                public void run( StudioProgressMonitor monitor )
                {
                    // Getting the correct SchemaConnector for this connection
                    List<SchemaConnector> correctSchemaConnectors = getCorrectSchemaConnectors(
                        project.getConnection(), monitor );

                    // If no suitable SchemaConnector has been found, we display an
                    // error message and return false;
                    if ( correctSchemaConnectors.size() == 0 )
                    {
                        monitor.reportError(
                            "No suitable SchemaConnector has been found for the choosen Directory Server.", //$NON-NLS-1$
                            new NoSuitableSchemaConnectorException() );
                    }

                    // Check if generic schema connector is included, then remove it to use a specific one
                    if ( correctSchemaConnectors.size() > 1 )
                    {
                        for ( SchemaConnector schemaConnector : correctSchemaConnectors )
                        {
                            if ( schemaConnector instanceof GenericSchemaConnector )
                            {
                                correctSchemaConnectors.remove( schemaConnector );
                                break;
                            }
                        }
                    }

                    // Getting the correct SchemaConnector
                    SchemaConnector correctSchemaConnector = null;
                    if ( correctSchemaConnectors.size() == 1 )
                    {
                        correctSchemaConnector = correctSchemaConnectors.get( 0 );
                    }
                    else
                    {
                        // TODO display a dialog in which the user can select the correct schema connector
                    }

                    project.setSchemaConnector( correctSchemaConnector );

                    // Fetching the Online Schema
                    project.fetchOnlineSchema( monitor );
                }


                public String getName()
                {
                    return null;
                }


                public Object[] getLockedObjects()
                {
                    return null;
                }


                public String getErrorMessage()
                {
                    return null;
                }


                public Connection[] getConnections()
                {
                    return null;
                }

            }, getContainer(), true );
        }
        else if ( projectType.equals( ProjectType.OFFLINE ) )
        // Project is an "Offline Project"
        {
            // Getting the selected 'core' schemas
            String[] selectedSchemas = schemasSelectionPage.getSelectedSchemas();
            ServerTypeEnum serverType = schemasSelectionPage.getServerType();
            if ( ( selectedSchemas != null ) && ( serverType != null ) )
            {
                SchemaHandler schemaHandler = project.getSchemaHandler();
                for ( String selectedSchema : selectedSchemas )
                {
                    Schema schema = PluginUtils.loadCoreSchema( serverType, selectedSchema );
                    if ( schema != null )
                    {
                        schema.setProject( project );
                        schemaHandler.addSchema( schema );
                    }
                }
            }
        }

        ProjectsHandler projectsHandler = Activator.getDefault().getProjectsHandler();
        projectsHandler.addProject( project );
        projectsHandler.openProject( project );

        return true;
    }


    /**
     * Gets the List of suitable SchemaConnectors
     *
     * @param connection
     *      the connection to test the SchemaConnectors with
     * @return
     *      the List of suitable SchemaConnectors
     */
    private List<SchemaConnector> getCorrectSchemaConnectors( Connection connection, StudioProgressMonitor monitor )
    {
        List<SchemaConnector> suitableSchemaConnectors = new ArrayList<SchemaConnector>();

        // Looping on the SchemaConnectors
        List<SchemaConnector> schemaConectors = PluginUtils.getSchemaConnectors();
        for ( SchemaConnector schemaConnector : schemaConectors )
        {
            // Testing if the SchemaConnector is suitable for this connection
            if ( schemaConnector.isSuitableConnector( connection, monitor ) )
            {
                suitableSchemaConnectors.add( schemaConnector );
            }
        }

        return suitableSchemaConnectors;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    public IWizardPage getNextPage( IWizardPage page )
    {
        if ( page.equals( informationPage ) )
        {
            if ( informationPage.getProjectType().equals( ProjectType.ONLINE ) )
            {
                return connectionSelectionPage;
            }
            else if ( informationPage.getProjectType().equals( ProjectType.OFFLINE ) )
            {
                return schemasSelectionPage;
            }
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

    class NoSuitableSchemaConnectorException extends Exception
    {
        private static final long serialVersionUID = 1L;
    }
}
