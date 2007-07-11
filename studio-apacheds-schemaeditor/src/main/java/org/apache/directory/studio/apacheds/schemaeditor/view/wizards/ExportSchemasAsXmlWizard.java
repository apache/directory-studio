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

import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.model.io.XMLSchemaFileExporter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to export schemas as XML.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportSchemasAsXmlWizard extends Wizard implements IExportWizard
{
    // The pages of the wizard
    private ExportSchemasAsXmlWizardPage page;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        // Creating pages
        page = new ExportSchemasAsXmlWizardPage();

        // Adding pages
        addPage( page );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        Schema[] selectedSchemas = page.getSelectedSchemas();

        int exportType = page.getExportType();
        if ( exportType == ExportSchemasAsXmlWizardPage.EXPORT_MULTIPLE_FILES )
        {
            String exportDirectory = page.getExportDirectory();

            for ( Schema schema : selectedSchemas )
            {
                try
                {
                    BufferedWriter buffWriter = new BufferedWriter( new FileWriter( exportDirectory + "/"
                        + schema.getName() + ".xml" ) );
                    buffWriter.write( XMLSchemaFileExporter.toSourceCode( schema ) );
                    buffWriter.close();
                }
                catch ( IOException e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        else if ( exportType == ExportSchemasAsXmlWizardPage.EXPORT_SINGLE_FILE )
        {

        }

        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // Nothing to do
    }
}
