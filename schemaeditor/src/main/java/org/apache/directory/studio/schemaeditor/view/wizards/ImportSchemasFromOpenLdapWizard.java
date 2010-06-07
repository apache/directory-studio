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

import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioRunnableWithProgressAdapter;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.io.OpenLdapSchemaFileImportException;
import org.apache.directory.studio.schemaeditor.model.io.OpenLdapSchemaFileImporter;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaChecker;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to import schemas from OpenLdap format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportSchemasFromOpenLdapWizard extends Wizard implements IImportWizard
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The SchemaChecker */
    private SchemaChecker schemaChecker;

    // The pages of the wizard
    private ImportSchemasFromOpenLdapWizardPage page;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        // Creating pages
        page = new ImportSchemasFromOpenLdapWizardPage();

        // Adding pages
        addPage( page );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        // Saving the dialog settings
        page.saveDialogSettings();

        // Getting the schemas to be imported
        final File[] selectedSchemasFiles = page.getSelectedSchemaFiles();
        schemaChecker.disableModificationsListening();

        StudioRunnableWithProgress runnable = new StudioRunnableWithProgressAdapter()
        {
            public void run( StudioProgressMonitor monitor )
            {
                monitor
                    .beginTask(
                        Messages.getString( "ImportSchemasFromOpenLdapWizard.ImportingSchemas" ), selectedSchemasFiles.length ); //$NON-NLS-1$

                for ( File schemaFile : selectedSchemasFiles )
                {
                    monitor.subTask( schemaFile.getName() );
                    try
                    {
                        Schema schema = OpenLdapSchemaFileImporter.getSchema( new FileInputStream( schemaFile ),
                            schemaFile.getAbsolutePath() );
                        schema.setProject( Activator.getDefault().getProjectsHandler().getOpenProject() );
                        schemaHandler.addSchema( schema );
                    }
                    catch ( OpenLdapSchemaFileImportException e )
                    {
                        reportError( e, schemaFile, monitor );
                    }
                    catch ( FileNotFoundException e )
                    {
                        reportError( e, schemaFile, monitor );
                    }
                    monitor.worked( 1 );
                }
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
                    Messages.getString( "ImportSchemasFromOpenLdapWizard.ErrorImportingSchema" ), schemaFile.getName() ); //$NON-NLS-1$
                monitor.reportError( message, e );
            }


            public String getName()
            {
                return Messages.getString( "ImportSchemasFromOpenLdapWizard.ImportingSchemas" ); //$NON-NLS-1$
            }

        };
        RunnableContextRunner.execute( runnable, getContainer(), true );

        schemaChecker.enableModificationsListening();

        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor( true );
        schemaHandler = Activator.getDefault().getSchemaHandler();
        schemaChecker = Activator.getDefault().getSchemaChecker();
    }
}
