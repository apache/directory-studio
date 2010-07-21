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


import org.apache.directory.studio.ldapbrowser.core.jobs.ExportDsmlJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExportDsmlJob.ExportDsmlJobType;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Wizard for Exporting to DSML
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportDsmlWizard extends ExportBaseWizard
{

    /** The title. */
    public static final String WIZARD_TITLE = Messages.getString( "ExportDsmlWizard.DSMLExport" ); //$NON-NLS-1$

    /** The from page, used to select the exported data. */
    private ExportDsmlFromWizardPage fromPage;

    /** The to page, used to select the target file. */
    private ExportDsmlToWizardPage toPage;

    private ExportDsmlWizardSaveAsType saveAsType = ExportDsmlWizardSaveAsType.RESPONSE;

    /**
     * This enum contains the two possible export types.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum ExportDsmlWizardSaveAsType
    {
        RESPONSE, REQUEST
    };


    /**
     * Creates a new instance of ExportDsmlWizard.
     */
    public ExportDsmlWizard()
    {
        super( WIZARD_TITLE );
    }


    /**
     * Gets the ID of the Export DSML Wizard
     * @return The ID of the Export DSML Wizard
     */
    public static String getId()
    {
        return BrowserUIConstants.WIZARD_EXPORT_DSML;
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        fromPage = new ExportDsmlFromWizardPage( ExportDsmlFromWizardPage.class.getName(), this );
        addPage( fromPage );
        toPage = new ExportDsmlToWizardPage( ExportDsmlToWizardPage.class.getName(), this );
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
            BrowserUIConstants.PLUGIN_ID + "." + "tools_dsmlexport_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
        PlatformUI.getWorkbench().getHelpSystem().setHelp( toPage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_dsmlexport_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        fromPage.saveDialogSettings();
        toPage.saveDialogSettings();

        switch ( saveAsType )
        {
            case RESPONSE:
                new ExportDsmlJob( exportFilename, search.getBrowserConnection(), search.getSearchParameter(),
                    ExportDsmlJobType.RESPONSE ).execute();
                break;
            case REQUEST:
                new ExportDsmlJob( exportFilename, search.getBrowserConnection(), search.getSearchParameter(),
                    ExportDsmlJobType.REQUEST ).execute();
                break;
        }

        return true;
    }


    /**
     * Gets the "Save as" type.
     *
     * @return
     *      the "Save as" type
     */
    public ExportDsmlWizardSaveAsType getSaveAsType()
    {
        return saveAsType;
    }


    /**
     * Sets the "Save as" type.
     *
     * @param saveAsType
     *      the "Save as" type
     */
    public void setSaveAsType( ExportDsmlWizardSaveAsType saveAsType )
    {
        this.saveAsType = saveAsType;
    }
}
