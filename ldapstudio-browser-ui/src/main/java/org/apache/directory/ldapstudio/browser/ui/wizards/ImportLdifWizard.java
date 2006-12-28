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

import org.apache.directory.ldapstudio.browser.core.jobs.ImportLdifJob;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


public class ImportLdifWizard extends Wizard implements IImportWizard
{

    private ImportLdifMainWizardPage mainPage;

    private String ldifFilename;

    private IConnection importConnection;

    private boolean enableLogging;

    private String logFilename;

    private boolean continueOnError;


    public ImportLdifWizard()
    {
        super();
        super.setWindowTitle( "LDIF Import" );
    }


    public ImportLdifWizard( IConnection selectedConnection )
    {
        super.setWindowTitle( "LDIF Import" );
        this.importConnection = selectedConnection;
    }


    public static String getId()
    {
        return ImportLdifWizard.class.getName();
    }


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


    public void addPages()
    {
        mainPage = new ImportLdifMainWizardPage( ImportLdifMainWizardPage.class.getName(), this );
        addPage( mainPage );

//        PlatformUI.getWorkbench().getHelpSystem().setHelp( getContainer().getShell(),
//            BrowserUIPlugin.PLUGIN_ID + "." + "tools_ldifimport_wizard" );
    }


    public boolean performFinish()
    {

        this.mainPage.saveDialogSettings();

        if ( this.ldifFilename != null && !"".equals( this.ldifFilename ) )
        {
            File ldifFile = new File( this.ldifFilename );

            if ( this.enableLogging )
            {
                File logFile = new File( this.logFilename );
                new ImportLdifJob( this.importConnection, ldifFile, logFile, this.continueOnError ).execute();
            }
            else
            {
                new ImportLdifJob( this.importConnection, ldifFile, this.continueOnError ).execute();
            }

            return true;
        }
        return false;
    }


    public IConnection getImportConnection()
    {
        return importConnection;
    }


    public void setImportConnection( IConnection importConnection )
    {
        this.importConnection = importConnection;
    }


    public void setLdifFilename( String ldifFilename )
    {
        this.ldifFilename = ldifFilename;
    }


    public void setContinueOnError( boolean continueOnError )
    {
        this.continueOnError = continueOnError;
    }


    public void setLogFilename( String logFilename )
    {
        this.logFilename = logFilename;
    }


    public void setEnableLogging( boolean b )
    {
        this.enableLogging = b;
    }

}
