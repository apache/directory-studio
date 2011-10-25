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

import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgressAdapter;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.io.XMLSchemaFileImportException;
import org.apache.directory.studio.schemaeditor.model.io.XMLSchemaFileImporter;
import org.apache.directory.studio.schemaeditor.model.io.XMLSchemaFileImporter.SchemaFileType;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaChecker;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to import schemas from XML format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportSchemasFromXmlWizard extends Wizard implements IImportWizard
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The SchemaChecker */
    private SchemaChecker schemaChecker;

    // The pages of the wizard
    private ImportSchemasFromXmlWizardPage page;


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        // Creating pages
        page = new ImportSchemasFromXmlWizardPage();

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

        // Getting the schemas to be imported
        final File[] selectedSchemasFiles = page.getSelectedSchemaFiles();
        schemaChecker.disableModificationsListening();

        StudioConnectionRunnableWithProgress runnable = new StudioConnectionRunnableWithProgressAdapter()
        {
            public void run( StudioProgressMonitor monitor )
            {
                monitor.beginTask(
                    Messages.getString( "ImportSchemasFromXmlWizard.ImportingSchemas" ), selectedSchemasFiles.length ); //$NON-NLS-1$

                for ( File schemaFile : selectedSchemasFiles )
                {
                    monitor.subTask( schemaFile.getName() );
                    try
                    {
                        SchemaFileType schemaFileType = XMLSchemaFileImporter.getSchemaFileType( new FileInputStream(
                            schemaFile ), schemaFile.getAbsolutePath() );
                        switch ( schemaFileType )
                        {
                            case SINGLE:
                                Schema importedSchema = XMLSchemaFileImporter.getSchema( new FileInputStream(
                                    schemaFile ), schemaFile.getAbsolutePath() );
                                importedSchema
                                    .setProject( Activator.getDefault().getProjectsHandler().getOpenProject() );
                                schemaHandler.addSchema( importedSchema );
                                break;
                            case MULTIPLE:
                                Schema[] schemas = XMLSchemaFileImporter.getSchemas( new FileInputStream( schemaFile ),
                                    schemaFile.getAbsolutePath() );
                                for ( Schema schema : schemas )
                                {
                                    schema.setProject( Activator.getDefault().getProjectsHandler().getOpenProject() );
                                    schemaHandler.addSchema( schema );
                                }
                                break;
                        }
                    }
                    catch ( XMLSchemaFileImportException e )
                    {
                        reportError( e, schemaFile, monitor );
                    }
                    catch ( FileNotFoundException e )
                    {
                        reportError( e, schemaFile, monitor );
                    }
                    monitor.worked( 1 );
                }

                monitor.done();
                schemaChecker.enableModificationsListening();
            }


            /**
             * Reports the error raised.
             *
             * @param e
             *      the exception
             * @param schemaFile
             *      the schema file
             * @param monitor
             *      the monitor the error is reported to
             * 
             */
            private void reportError( Exception e, File schemaFile, StudioProgressMonitor monitor )
            {
                String message = NLS.bind(
                    Messages.getString( "ImportSchemasFromXmlWizard.ErrorImportingSchema" ), schemaFile.getName() ); //$NON-NLS-1$
                monitor.reportError( message, e );
            }


            public String getName()
            {
                return Messages.getString( "ImportSchemasFromXmlWizard.ImportingSchemas" ); //$NON-NLS-1$
            }

        };
        RunnableContextRunner.execute( runnable, getContainer(), true );
        schemaChecker.enableModificationsListening();

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor( true );
        schemaHandler = Activator.getDefault().getSchemaHandler();
        schemaChecker = Activator.getDefault().getSchemaChecker();
    }
}
