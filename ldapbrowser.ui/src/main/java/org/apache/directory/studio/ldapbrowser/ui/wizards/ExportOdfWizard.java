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


import org.apache.directory.studio.ldapbrowser.core.jobs.ExportOdfRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Wizard for Exporting to ODF.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportOdfWizard extends ExportBaseWizard
{

    /** The from page, used to select the exported data. */
    private ExportOdfFromWizardPage fromPage;

    /** The to page, used to select the target file. */
    private ExportOdfToWizardPage toPage;


    /**
     * Creates a new instance of ExportOdfWizard.
     */
    public ExportOdfWizard()
    {
        super( Messages.getString( "ExportOdfWizard.OdfExport" ) ); //$NON-NLS-1$
    }


    /**
     * Gets the ID of the Export ODF Wizard
     * 
     * @return The ID of the Export ODF Wizard
     */
    public static String getId()
    {
        return BrowserUIConstants.WIZARD_EXPORT_ODF;
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        fromPage = new ExportOdfFromWizardPage( ExportOdfFromWizardPage.class.getName(), this );
        addPage( fromPage );
        toPage = new ExportOdfToWizardPage( ExportOdfToWizardPage.class.getName(), this );
        addPage( toPage );
    }


    /**
     * {@inheritDoc}
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        // set help context ID
        PlatformUI.getWorkbench().getHelpSystem()
            .setHelp( fromPage.getControl(), BrowserUIConstants.PLUGIN_ID + "." + "tools_odfexport_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
        PlatformUI.getWorkbench().getHelpSystem()
            .setHelp( toPage.getControl(), BrowserUIConstants.PLUGIN_ID + "." + "tools_odfexport_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        fromPage.saveDialogSettings();
        toPage.saveDialogSettings();
        boolean exportDn = this.fromPage.isExportDn();

        new StudioBrowserJob( new ExportOdfRunnable( exportFilename, search.getBrowserConnection(),
            search.getSearchParameter(), exportDn ) ).execute();

        return true;
    }

}
