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


import java.io.File;

import org.apache.directory.ldapstudio.browser.core.jobs.ImportDsmlJob;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

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
    private IConnection importConnection;
    
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
        super.setWindowTitle( WIZARD_TITLE );
    }
    
    /**
     * Creates a new instance of ImportDsmlWizard.
     * @param selectedConnection
     *          The connection to use
     */
    public ImportDsmlWizard( IConnection selectedConnection )
    {
        super.setWindowTitle( WIZARD_TITLE );
        this.importConnection = selectedConnection;
    }
    
    /**
     * Gets the ID of the Import DSML Wizard
     * @return The ID of the Import DSML Wizard
     */
    public static String getId()
    {
        return ImportDsmlWizard.class.getName();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {

        this.mainPage.saveDialogSettings();

        if ( this.dsmlFilename != null && !"".equals( this.dsmlFilename ) )
        {
            File dsmlFile = new File( this.dsmlFilename );

            if ( this.saveResponse )
            {
                File responseFile = new File( this.responseFilename );
                new ImportDsmlJob( this.importConnection, dsmlFile, responseFile ).execute();
            }
            else
            {
                new ImportDsmlJob( this.importConnection, dsmlFile ).execute();
            }

            return true;
        }
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        Object o = selection.getFirstElement();
        if ( o instanceof IEntry )
        {
            this.importConnection = ( ( IEntry ) o ).getConnection();
        }
        else if ( o instanceof ISearchResult )
        {
            this.importConnection = ( ( ISearchResult ) o ).getEntry().getConnection();
        }
        else if ( o instanceof IBookmark )
        {
            this.importConnection = ( ( IBookmark ) o ).getConnection();
        }
        else if ( o instanceof IAttribute )
        {
            this.importConnection = ( ( IAttribute ) o ).getEntry().getConnection();
        }
        else if ( o instanceof IValue )
        {
            this.importConnection = ( ( IValue ) o ).getAttribute().getEntry().getConnection();
        }
        else if ( o instanceof IConnection )
        {
            this.importConnection = ( IConnection ) o;
        }
        else
        {
            this.importConnection = null;
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        mainPage = new ImportDsmlMainWizardPage( ImportDsmlMainWizardPage.class.getName(), this );
        addPage( mainPage );

    }
    
    /**
     * Get the connection attached to the Import
     * @return The connection attached to the Import
     */
    public IConnection getImportConnection()
    {
        return importConnection;
    }
    
    /**
     * Sets the connection attached to the Import
     * @param connection
     *          The connection attached to the Import
     */
    public void setImportConnection( IConnection connection )
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
