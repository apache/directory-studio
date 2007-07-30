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


import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.model.io.OpenLdapSchemaFileImportException;
import org.apache.directory.studio.apacheds.schemaeditor.model.io.OpenLdapSchemaFileImporter;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaChecker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to import schemas from OpenLdap format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportSchemasFromOpenLdapWizard extends Wizard implements IImportWizard
{
    public static final String ID = Activator.PLUGIN_ID + ".wizards.ImportSchemasFromOpenLdapWizard";

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
        final File[] selectedSchemasFiles = page.getSelectedSchemaFiles();

        schemaChecker.disableModificationsListening();

        try
        {
            getContainer().run( true, false, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor )
                {
                    monitor.beginTask( "Importing schemas: ", selectedSchemasFiles.length );

                    for ( File schemaFile : selectedSchemasFiles )
                    {
                        monitor.subTask( schemaFile.getName() );
                        try
                        {
                            Schema schema = OpenLdapSchemaFileImporter.getSchema( schemaFile.getAbsolutePath() );
                            schemaHandler.addSchema( schema );
                        }
                        catch ( OpenLdapSchemaFileImportException e )
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
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
