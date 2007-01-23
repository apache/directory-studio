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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.core.jobs.ExportCsvJob;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;


public class ExportCsvWizard extends ExportBaseWizard
{

    private ExportCsvFromWizardPage fromPage;

    private ExportCsvToWizardPage toPage;


    public ExportCsvWizard()
    {
        super( "CSV Export" );
    }


    public static String getId()
    {
        return ExportCsvWizard.class.getName();
    }


    public void addPages()
    {
        fromPage = new ExportCsvFromWizardPage( ExportCsvFromWizardPage.class.getName(), this );
        addPage( fromPage );
        toPage = new ExportCsvToWizardPage( ExportCsvToWizardPage.class.getName(), this );
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
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_csvexport_wizard" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( toPage.getControl(),
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_csvexport_wizard" );
    }


    public boolean performFinish()
    {

        this.fromPage.saveDialogSettings();
        this.toPage.saveDialogSettings();
        boolean exportDn = this.fromPage.isExportDn();

        ExportCsvJob ecj = new ExportCsvJob( this.exportFilename, this.search.getConnection(), this.search
            .getSearchParameter(), exportDn );
        ecj.execute();

        return true;
    }

}
