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
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.io.XMLSchemaFileImportException;
import org.apache.directory.studio.schemaeditor.model.io.XMLSchemaFileImporter;
import org.apache.directory.studio.schemaeditor.model.io.XMLSchemaFileImporter.SchemaFileType;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaChecker;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to import schemas from XML format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportSchemasFromXmlWizard extends Wizard implements IImportWizard
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The SchemaChecker */
    private SchemaChecker schemaChecker;

    // The pages of the wizard
    private ImportSchemasFromXmlWizardPage page;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        // Creating pages
        page = new ImportSchemasFromXmlWizardPage();

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
        try
        {
            getContainer().run( false, false, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor )
                {
                    monitor
                        .beginTask(
                            Messages.getString( "ImportSchemasFromXmlWizard.ImportingSchemas" ), selectedSchemasFiles.length ); //$NON-NLS-1$

                    for ( File schemaFile : selectedSchemasFiles )
                    {
                        monitor.subTask( schemaFile.getName() );
                        try
                        {
                            SchemaFileType schemaFileType = XMLSchemaFileImporter.getSchemaFileType(
                                new FileInputStream( schemaFile ), schemaFile.getAbsolutePath() );
                            switch ( schemaFileType )
                            {
                                case SINGLE:
                                    Schema importedSchema = XMLSchemaFileImporter.getSchema( new FileInputStream(
                                        schemaFile ), schemaFile.getAbsolutePath() );
                                    schemaHandler.addSchema( importedSchema );
                                    break;
                                case MULTIPLE:
                                    Schema[] schemas = XMLSchemaFileImporter.getSchemas( new FileInputStream(
                                        schemaFile ), schemaFile.getAbsolutePath() );
                                    for ( Schema schema : schemas )
                                    {
                                        schemaHandler.addSchema( schema );
                                    }
                                    break;
                            }
                        }
                        catch ( XMLSchemaFileImportException e )
                        {
                            PluginUtils
                                .logError(
                                    NLS
                                        .bind(
                                            Messages.getString( "ImportSchemasFromXmlWizard.ErrorImportingSchema" ), new File[] { schemaFile } ), e ); //$NON-NLS-1$
                            ViewUtils
                                .displayErrorMessageBox(
                                    Messages.getString( "ImportSchemasFromXmlWizard.Error" ), //$NON-NLS-1$
                                    NLS
                                        .bind(
                                            Messages.getString( "ImportSchemasFromXmlWizard.ErrorImportingSchema" ), new File[] { schemaFile } ) ); //$NON-NLS-1$
                        }
                        catch ( FileNotFoundException e )
                        {
                            PluginUtils
                                .logError(
                                    NLS
                                        .bind(
                                            Messages.getString( "ImportSchemasFromXmlWizard.ErrorImportingSchema" ), new File[] { schemaFile } ), e ); //$NON-NLS-1$
                            ViewUtils
                                .displayErrorMessageBox(
                                    Messages.getString( "ImportSchemasFromXmlWizard.Error" ), //$NON-NLS-1$
                                    NLS
                                        .bind(
                                            Messages.getString( "ImportSchemasFromXmlWizard.ErrorImportingSchema" ), new File[] { schemaFile } ) ); //$NON-NLS-1$
                        }
                        monitor.worked( 1 );
                    }

                    monitor.done();
                    schemaChecker.enableModificationsListening();
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
