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


import org.apache.directory.studio.ldapbrowser.core.jobs.ExportLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Wizard for Exporting to LDIF
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportLdifWizard extends ExportBaseWizard
{

    /** The from page, used to select the exported data. */
    private ExportLdifFromWizardPage fromPage;

    /** The to page, used to select the target file. */
    private ExportLdifToWizardPage toPage;


    /**
     * Creates a new instance of ExportLdifWizard.
     */
    public ExportLdifWizard()
    {
        super( Messages.getString( "ExportLdifWizard.LDIFExport" ) ); //$NON-NLS-1$
    }


    /**
     * Gets the ID of the Export LDIF Wizard
     * 
     * @return The ID of the Export LDIF Wizard
     */
    public static String getId()
    {
        return BrowserUIConstants.WIZARD_EXPORT_LDIF;
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        fromPage = new ExportLdifFromWizardPage( ExportLdifFromWizardPage.class.getName(), this );
        addPage( fromPage );
        toPage = new ExportLdifToWizardPage( ExportLdifToWizardPage.class.getName(), this );
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
            .setHelp( fromPage.getControl(), BrowserUIConstants.PLUGIN_ID + "." + "tools_ldifexport_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
        PlatformUI.getWorkbench().getHelpSystem()
            .setHelp( toPage.getControl(), BrowserUIConstants.PLUGIN_ID + "." + "tools_ldifexport_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        fromPage.saveDialogSettings();
        toPage.saveDialogSettings();

        new StudioBrowserJob( new ExportLdifRunnable( exportFilename, search.getBrowserConnection(),
            search.getSearchParameter() ) ).execute();

        return true;
    }

}
