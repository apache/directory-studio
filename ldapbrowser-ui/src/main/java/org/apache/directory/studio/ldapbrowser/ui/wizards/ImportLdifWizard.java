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

import org.apache.directory.studio.ldapbrowser.core.jobs.ImportLdifJob;
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
 * This class implements the Import LDIF Wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportLdifWizard extends Wizard implements IImportWizard
{

    /** The main page. */
    private ImportLdifMainWizardPage mainPage;

    /** The ldif filename. */
    private String ldifFilename;

    /** The import connection. */
    private IBrowserConnection importConnection;

    /** The enable logging flag. */
    private boolean enableLogging;

    /** The log filename. */
    private String logFilename;

    /** The update if entry exists flag. */
    private boolean updateIfEntryExists;

    /** The continue on error flag. */
    private boolean continueOnError;


    /**
     * Creates a new instance of ImportLdifWizard.
     */
    public ImportLdifWizard()
    {
        super();
        setWindowTitle( "LDIF Import" );
    }


    /**
     * Creates a new instance of ImportLdifWizard.
     * 
     * @param importConnection the import connection
     */
    public ImportLdifWizard( IBrowserConnection importConnection )
    {
        super.setWindowTitle( "LDIF Import" );
        this.importConnection = importConnection;
    }


    /**
     * Gets the ID of the Import LDIF Wizard
     * 
     * @return The ID of the Import LDIF Wizard
     */
    public static String getId()
    {
        return BrowserUIConstants.WIZARD_IMPORT_LDIF;
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
        mainPage = new ImportLdifMainWizardPage( ImportLdifMainWizardPage.class.getName(), this );
        addPage( mainPage );
    }


    /**
     * {@inheritDoc}
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        PlatformUI.getWorkbench().getHelpSystem().setHelp( mainPage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_ldifimport_wizard" );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        mainPage.saveDialogSettings();

        if ( ldifFilename != null && !"".equals( ldifFilename ) )
        {
            File ldifFile = new File( ldifFilename );

            if ( enableLogging )
            {
                File logFile = new File( logFilename );
                new ImportLdifJob( importConnection, ldifFile, logFile, updateIfEntryExists, continueOnError )
                    .execute();
            }
            else
            {
                new ImportLdifJob( importConnection, ldifFile, updateIfEntryExists, continueOnError ).execute();
            }

            return true;
        }
        return false;
    }


    /**
     * Gets the import connection.
     * 
     * @return the import connection
     */
    public IBrowserConnection getImportConnection()
    {
        return importConnection;
    }


    /**
     * Sets the import connection.
     * 
     * @param importConnection the import connection
     */
    public void setImportConnection( IBrowserConnection importConnection )
    {
        this.importConnection = importConnection;
    }


    /**
     * Sets the ldif filename.
     * 
     * @param ldifFilename the ldif filename
     */
    public void setLdifFilename( String ldifFilename )
    {
        this.ldifFilename = ldifFilename;
    }


    /**
     * Sets the update if entry exists flag.
     * 
     * @param updateIfEntryExists the update if entry exists flag
     */
    public void setUpdateIfEntryExists( boolean updateIfEntryExists )
    {
        this.updateIfEntryExists = updateIfEntryExists;
    }


    /**
     * Sets the continue on error flag.
     * 
     * @param continueOnError the continue on error flag
     */
    public void setContinueOnError( boolean continueOnError )
    {
        this.continueOnError = continueOnError;
    }


    /**
     * Sets the log filename.
     * 
     * @param logFilename the log filename
     */
    public void setLogFilename( String logFilename )
    {
        this.logFilename = logFilename;
    }


    /**
     * Sets the enable logging flag.
     * 
     * @param b the enable logging flag
     */
    public void setEnableLogging( boolean b )
    {
        this.enableLogging = b;
    }

}
