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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import org.apache.directory.studio.ldapbrowser.core.jobs.ExportXlsJob;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Wizard for Exporting to Excel
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportExcelWizard extends ExportBaseWizard
{

    /** The from page, used to select the exported data. */
    private ExportExcelFromWizardPage fromPage;

    /** The to page, used to select the target file. */
    private ExportExcelToWizardPage toPage;


    /**
     * Creates a new instance of ExportExcelWizard.
     */
    public ExportExcelWizard()
    {
        super( "Excel Export" );
    }


    /**
     * Gets the ID of the Export Excel Wizard
     * 
     * @return The ID of the Export Excel Wizard
     */
    public static String getId()
    {
        return ExportExcelWizard.class.getName();
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        fromPage = new ExportExcelFromWizardPage( ExportExcelFromWizardPage.class.getName(), this );
        addPage( fromPage );
        toPage = new ExportExcelToWizardPage( ExportExcelToWizardPage.class.getName(), this );
        addPage( toPage );
    }


    /**
     * {@inheritDoc}
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        // set help context ID
        PlatformUI.getWorkbench().getHelpSystem().setHelp( fromPage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_excelexport_wizard" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( toPage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_excelexport_wizard" );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        fromPage.saveDialogSettings();
        toPage.saveDialogSettings();
        boolean exportDn = this.fromPage.isExportDn();

        ExportXlsJob eej = new ExportXlsJob( exportFilename, search.getBrowserConnection(), search.getSearchParameter(),
            exportDn );
        eej.execute();

        return true;
    }

}
