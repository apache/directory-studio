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


import java.io.File;

import org.apache.directory.studio.ldapbrowser.core.jobs.ImportDsmlJob;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Import DSML Wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportDsmlWizard extends Wizard implements IImportWizard
{
    /** Page Title */
    public static final String WIZARD_TITLE = "DSML Import";

    /** The connection attached to the import */
    private IBrowserConnection importConnection;

    /** The main page of the wizard */
    private ImportDsmlMainWizardPage mainPage;

    /** The DSML Filename */
    private String dsmlFilename;

    /** The Save Filename */
    private String responseFilename;

    /** The Save Response flag */
    private boolean saveResponse;


    /**
     * Creates a new instance of ImportDsmlWizard.
     */
    public ImportDsmlWizard()
    {
        super();
        setWindowTitle( WIZARD_TITLE );
    }


    /**
     * Creates a new instance of ImportDsmlWizard.
     * @param selectedConnection
     *          The connection to use
     */
    public ImportDsmlWizard( IBrowserConnection selectedConnection )
    {
        setWindowTitle( WIZARD_TITLE );
        this.importConnection = selectedConnection;
    }


    /**
     * Gets the ID of the Import DSML Wizard
     * @return The ID of the Import DSML Wizard
     */
    public static String getId()
    {
        return BrowserUIConstants.WIZARD_IMPORT_DSML;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        mainPage.saveDialogSettings();

        if ( dsmlFilename != null && !"".equals( dsmlFilename ) )
        {
            File dsmlFile = new File( dsmlFilename );

            if ( saveResponse )
            {
                File responseFile = new File( responseFilename );
                new ImportDsmlJob( importConnection, dsmlFile, responseFile ).execute();
            }
            else
            {
                new ImportDsmlJob( importConnection, dsmlFile ).execute();
            }

            return true;
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        Object o = selection.getFirstElement();
        if ( o instanceof IEntry )
        {
            importConnection = ( ( IEntry ) o ).getBrowserConnection();
        }
        else if ( o instanceof ISearchResult )
        {
            importConnection = ( ( ISearchResult ) o ).getEntry().getBrowserConnection();
        }
        else if ( o instanceof IBookmark )
        {
            importConnection = ( ( IBookmark ) o ).getBrowserConnection();
        }
        else if ( o instanceof IAttribute )
        {
            importConnection = ( ( IAttribute ) o ).getEntry().getBrowserConnection();
        }
        else if ( o instanceof IValue )
        {
            importConnection = ( ( IValue ) o ).getAttribute().getEntry().getBrowserConnection();
        }
        else if ( o instanceof IBrowserConnection )
        {
            importConnection = ( IBrowserConnection ) o;
        }
        else
        {
            importConnection = null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        mainPage = new ImportDsmlMainWizardPage( ImportDsmlMainWizardPage.class.getName(), this );
        addPage( mainPage );
    }


    /**
     * {@inheritDoc}
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        // set help context ID
        PlatformUI.getWorkbench().getHelpSystem().setHelp( mainPage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_dsmlimport_wizard" );
    }


    /**
     * Get the connection attached to the Import
     * @return The connection attached to the Import
     */
    public IBrowserConnection getImportConnection()
    {
        return importConnection;
    }


    /**
     * Sets the connection attached to the Import
     * @param connection
     *          The connection attached to the Import
     */
    public void setImportConnection( IBrowserConnection connection )
    {
        this.importConnection = connection;
    }


    /**
     * Sets the DSML Filename
     * @param dsmlFilename
     *          The DSML Filename
     */
    public void setDsmlFilename( String dsmlFilename )
    {
        this.dsmlFilename = dsmlFilename;
    }


    /**
     * Sets the Save Filename
     * @param saveFilename
     *          The Save Filename
     */
    public void setResponseFilename( String saveFilename )
    {
        this.responseFilename = saveFilename;
    }


    /**
     * Sets the SaveResponse flag
     * @param b
     *          The SaveResponse flag
     */
    public void setSaveResponse( boolean b )
    {
        this.saveResponse = b;
    }
}
