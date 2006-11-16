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

package org.apache.directory.ldapstudio.importexport.view;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;

import org.apache.directory.ldapstudio.importexport.Activator;
import org.apache.directory.ldapstudio.importexport.Messages;
import org.apache.directory.server.tools.ToolCommandListener;
import org.apache.directory.server.tools.commands.exportcmd.ExportCommandExecutor;
import org.apache.directory.server.tools.util.ListenerParameter;
import org.apache.directory.server.tools.util.Parameter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the Export Wizards.
 * Export Wizard is composed of two pages : one for general settings of the export,
 * and one that is a preview of entries that are going to be exported
 */
public class ExportWizard extends Wizard implements INewWizard
{
    // The logger
    private static Logger logger = LoggerFactory.getLogger( ExportWizard.class );
    
    // The two pages of the Wizard
    private ExportWizardFirstPage exportWizardFirstPage;
    private ExportWizardSecondPage exportWizardSecondPage;
    
    // The fetched entries
    private NamingEnumeration entries;
    
    // Necessary fields to store the result of the User Input in the pages of the wizard
    private String scopeString;
    private int scope;
    private String exportPoint;
    private String destinationFile;
    
    // These fields are used when an error occurs while a progress monitor is running.
    // Since we cannot use the setErrorMessage method while a progress monitor is running,
    // we fire an error flag and store the error message.
    private boolean exportError = false;
    private String exportErrorMessage = ""; //$NON-NLS-1$
    
    // The counter used to show how many entries were exported
    private int entriesCounter;
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        setWindowTitle( Messages.getString("ExportWizard.Export") ); //$NON-NLS-1$
        setHelpAvailable( false );
        setNeedsProgressMonitor( true );
        logger.info( "Initializing Export Wizard" ); //$NON-NLS-1$
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        exportWizardFirstPage = new ExportWizardFirstPage();
        exportWizardSecondPage = new ExportWizardSecondPage();
        addPage(exportWizardFirstPage);
        addPage(exportWizardSecondPage);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish()
    {
        logger.info( "Starting Export" ); //$NON-NLS-1$
        // Setting error notification variable to default
        exportError = false;
        exportErrorMessage = ""; //$NON-NLS-1$
        
        // Getting information from the first page
        exportPoint = exportWizardFirstPage.getExportPoint(); 
        scope = exportWizardFirstPage.getScope();
        if ( scope == SearchControls.OBJECT_SCOPE )
        {
            scopeString = ExportCommandExecutor.SCOPE_OBJECT;
        }
        else if ( scope == SearchControls.ONELEVEL_SCOPE )
        {
            scopeString = ExportCommandExecutor.SCOPE_ONELEVEL;
        }
        else if ( scope == SearchControls.SUBTREE_SCOPE )
        {
            scopeString = ExportCommandExecutor.SCOPE_SUBTREE;
        }
        destinationFile = exportWizardFirstPage.getDestinationFile();
        
        if (logger.isDebugEnabled())
        {
            logger.debug( "Export Point: {}", exportPoint ); //$NON-NLS-1$
            logger.debug( "Scope: {}", scope ); //$NON-NLS-1$
            logger.debug( "Destination File: {}", destinationFile ); //$NON-NLS-1$
        }
        
        // Checking if the file doesn't already exist
        // We need to do that check separately from the rest of the code
        // because once in the IRunnableWithProgress.run method, UI can't be accessed
        // until the end of the method
        if ( destinationFile == null )
        {
            displayErrorMessage( Messages.getString("ExportWizard.An_error_has_occurred_Export_couldnt_be_completed" ) ); //$NON-NLS-1$
            return false;
        }
        File checkFile = new File( destinationFile );
        
        if ( checkFile.exists() )
        {
            logger.info( "The file {} already exists.", checkFile.getName()); //$NON-NLS-1$
            
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.ICON_QUESTION | SWT.YES | SWT.NO );
            messageBox.setText( Messages.getString("ExportWizard.Replacing_existing_file") ); //$NON-NLS-1$
            messageBox.setMessage( Messages.getString("ExportWizard.The_file") + checkFile.getName() + Messages.getString("ExportWizard.Already_exists") ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( SWT.YES != messageBox.open() )
            {
                logger.info( "The user doesn't want to replace existing file. The export stops here." ); //$NON-NLS-1$
                return false;
            }
            else
            {
                // Erasing the file
                logger.debug( "Deleting existing file ({}).", checkFile.getAbsolutePath() ); //$NON-NLS-1$
                checkFile.delete();
            }
        }
        else
        {
            // File doesn't exists, but does parent directories exists ?
            String parentPathName = checkFile.getParent();
            if ( parentPathName == null )
            {
                displayErrorMessage( Messages.getString("ExportWizard.The_path_to_the_destination_file_seems_to_be_wrong") ); //$NON-NLS-1$
                logger.debug( "The path to the destination file seems to be wrong. Path is null." ); //$NON-NLS-1$
                return false;
            }
            else
            {
                File parent = new File( parentPathName );
                if ( ! parent.exists() )
                {
                    logger.info( "Target directory does not exist." ); //$NON-NLS-1$
                    
                    MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.ICON_QUESTION | SWT.YES | SWT.NO );
                    messageBox.setText( Messages.getString("ExportWizard.Create_directory") ); //$NON-NLS-1$
                    messageBox.setMessage( Messages.getString("ExportWizard.Target_directory_doesnt_exist") ); //$NON-NLS-1$
                    if ( SWT.YES != messageBox.open() )
                    {
                        logger.info( "The user doesn't want to create target directory. The export stops here." ); //$NON-NLS-1$
                        return false;
                    }
                    else
                    {
                        // Creating target directory (and its sub-directories if necessary)
                        logger.debug( "Constructing needed directory structure ({}).", parent.getAbsolutePath() ); //$NON-NLS-1$
                        parent.mkdirs();
                    }
                }
            }
        }
        
        // Setting up the anonymous class to execute the progress monitor
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( IProgressMonitor monitor )
            {
                monitor.beginTask( Messages.getString("ExportWizard.Writing_file"), IProgressMonitor.UNKNOWN ); //$NON-NLS-1$
                performFinish( monitor );
                monitor.done();
            }
        };
        
        // Lauching the export command
        logger.info( "Starting to export" ); //$NON-NLS-1$
        try
        {
            getContainer().run(true, false, op);
        }
        catch ( InvocationTargetException e1 )
        {
            exportError = true;
            exportErrorMessage = Messages.getString("ExportWizard.An_error_has_occurred_Export_couldnt_be_completed"); //$NON-NLS-1$
        }
        catch ( InterruptedException e1 )
        {
            exportError = true;
            exportErrorMessage = Messages.getString("ExportWizard.An_error_has_occurred_Export_couldnt_be_completed"); //$NON-NLS-1$
        }
        
        if ( exportError )
        {
            logger.error( exportErrorMessage );
            displayErrorMessage( exportErrorMessage );
            return false;
        }
        else
        {
            logger.info( "Export successful." ); //$NON-NLS-1$
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.ICON_INFORMATION );
            messageBox.setText( "Export successful" ); //$NON-NLS-1$
            messageBox.setMessage( entriesCounter + "entries were succesfully exported." ); //$NON-NLS-1$
            messageBox.open();
            return true;
        }
    }
    
    /**
     * Performs the export. Fetches entries and creates the corresponding LDIF file
     * @param monitor the monitor to use to show progress
     */
    private void performFinish(final IProgressMonitor monitor )
    {
        // Initialization
        entriesCounter = 1;
        
        // Preparing the call of the Export Command tool
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        if (logger.isDebugEnabled())
        {
            logger.debug( "Connecting to LDAP server" ); //$NON-NLS-1$
            logger.debug( "Host: {}", store.getString( ServerPreferencePage.HOST ) ); //$NON-NLS-1$
            logger.debug( "Port: {}", store.getString( ServerPreferencePage.PORT ) ); //$NON-NLS-1$
            logger.debug( "User DN: {}", store.getString( ServerPreferencePage.USER_DN ) ); //$NON-NLS-1$
            logger.debug( "Base DN: {}", store.getString( ServerPreferencePage.BASE_BN ) ); //$NON-NLS-1$
            logger.debug( "Authentication: simple" ); //$NON-NLS-1$
        }
        
        // Initialization of the parameters
        Parameter hostParam = new Parameter( ExportCommandExecutor.HOST_PARAMETER, store.getString( ServerPreferencePage.HOST ) ); //$NON-NLS-1$
        Parameter portParam = new Parameter( ExportCommandExecutor.PORT_PARAMETER, new Integer( store.getString( ServerPreferencePage.PORT ) ) ); //$NON-NLS-1$
        Parameter userParam = new Parameter( ExportCommandExecutor.USER_PARAMETER, store.getString( ServerPreferencePage.USER_DN ) ); //$NON-NLS-1$
        Parameter passwordParam = new Parameter( ExportCommandExecutor.PASSWORD_PARAMETER, store.getString( ServerPreferencePage.PASSWORD ) ); //$NON-NLS-1$
        Parameter authParam = new Parameter( ExportCommandExecutor.AUTH_PARAMETER, "simple" ); //$NON-NLS-1$ //$NON-NLS-2$
        Parameter baseDNParam = new Parameter( ExportCommandExecutor.BASEDN_PARAMETER, store.getString( ServerPreferencePage.BASE_BN ) ); //$NON-NLS-1$
        Parameter exportPointParam = new Parameter( ExportCommandExecutor.EXPORTPOINT_PARAMETER, exportPoint ); //$NON-NLS-1$
        Parameter scopeParam = new Parameter( ExportCommandExecutor.SCOPE_PARAMETER, scopeString ); //$NON-NLS-1$
        Parameter fileParam = new Parameter( ExportCommandExecutor.FILE_PARAMETER, destinationFile ); //$NON-NLS-1$

        // Initialization of the listeners
        ListenerParameter[] listenerParameters = initListeners( monitor );
        
        // Executing the command
        ExportCommandExecutor executor = new ExportCommandExecutor();
        executor.execute( new Parameter[]{ hostParam, portParam, userParam, passwordParam, authParam, baseDNParam, exportPointParam, scopeParam, fileParam },
                            listenerParameters );
        
        logger.info( "File written on disk." ); //$NON-NLS-1$
    }
    
    /**
     * Returns the array of ListenerParameter for the Export Command
     * @param monitor the progress monitor to update
     * @return the array of ListenerParameter for the Export Command
     */
    private ListenerParameter[] initListeners(final IProgressMonitor monitor)
    {
        ListenerParameter entryWrittenListener = new ListenerParameter( ExportCommandExecutor.ENTRYWRITTENLISTENER_PARAMETER, new ToolCommandListener(){ //$NON-NLS-1$
            public void notify( Serializable arg0 )
            {
                // Updating the progress monitor
                if ( entriesCounter == 1 )
                {
                    monitor.setTaskName( Messages.getString("ExportWizard.Writing_file_to_disk_1_entry_done")); //$NON-NLS-1$
                }
                else
                {
                    monitor.setTaskName( Messages.getString("ExportWizard.Writing_file_to_disk") + entriesCounter + Messages.getString("ExportWizard.Entries_done")); //$NON-NLS-1$ //$NON-NLS-2$
                }
                entriesCounter++;
                logger.debug( "-> Entry written" ); //$NON-NLS-1$      
            }
        });
        
        ListenerParameter exceptionListener = new ListenerParameter( ExportCommandExecutor.EXCEPTIONLISTENER_PARAMETER, new ToolCommandListener(){ //$NON-NLS-1$
            public void notify( Serializable o )
            {
                if ( o instanceof CommunicationException )
                {
                    CommunicationException ce = ( CommunicationException ) o;
                    logger.error( "Connection to server failed", ce ); //$NON-NLS-1$
                    exportError = true;
                    exportErrorMessage = Messages.getString("ExportWizard.Connection_to_server_failed"); //$NON-NLS-1$
                    
                }
                else if ( o instanceof NameNotFoundException )
                {
                    NameNotFoundException nnfe = ( NameNotFoundException ) o;
                    logger.error( "An error occurred when resolving Export Point.", nnfe ); //$NON-NLS-1$
                    exportError = true;
                    exportErrorMessage = Messages.getString("ExportWizard.Error_occurred_resolving_Export_Point"); //$NON-NLS-1$
                    
                }
                else if ( o instanceof NamingException )
                {
                    NamingException ne = ( NamingException ) o;
                    logger.error( "An error occurred while retrieving entries.", ne ); //$NON-NLS-1$
                    exportError = true;
                    exportErrorMessage = Messages.getString("ExportWizard.Error_occurred_retrieving_entries"); //$NON-NLS-1$
                    
                }
                else if ( o instanceof IOException )
                {
                    IOException ioe = ( IOException ) o;
                    logger.error( "An error occurred while writing the file to disk.", ioe ); //$NON-NLS-1$
                    exportError = true;
                    exportErrorMessage = Messages.getString("ExportWizard.Error_occurred_writing_file_to_disk_File_could_not_be_saved"); //$NON-NLS-1$
                    
                }
                else
                {
                    exportError = true;
                    exportErrorMessage = Messages.getString("ExportWizard.An_unknown_error_has_occured_File_could_not_be_saved"); //$NON-NLS-1$
                }
            }
        });
        
        return new ListenerParameter[] { entryWrittenListener, exceptionListener};
    }
 
    @Override
    public boolean canFinish()
    {
        return exportWizardFirstPage.isPageComplete();
    }
    
    /**
     * Fetches the entries on the server. The result is accessible using the getEntries() method.
     * @throws NamingException 
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public void fetchEntries() throws NamingException
    {
        logger.debug( "Starting to fetch entries" ); //$NON-NLS-1$
        // Reseting entries fetched from last search
        resetEntries();
        
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        // Connecting to the LDAP Server
        if (logger.isDebugEnabled())
        {
            logger.debug( "Connecting to LDAP server" ); //$NON-NLS-1$
            logger.debug( "Host: {}",store.getString( ServerPreferencePage.HOST ) ); //$NON-NLS-1$
            logger.debug( "Port: {}",store.getString( ServerPreferencePage.PORT ) ); //$NON-NLS-1$
            logger.debug( "User DN: {}",store.getString( ServerPreferencePage.USER_DN ) ); //$NON-NLS-1$
            logger.debug( "Base DN: {}",store.getString( ServerPreferencePage.BASE_BN ) ); //$NON-NLS-1$
            logger.debug( "Authentication: simple" ); //$NON-NLS-1$
        }
        Hashtable env = new Hashtable();
        env.put( Context.SECURITY_PRINCIPAL, store.getString( ServerPreferencePage.USER_DN ) );
        env.put( Context.SECURITY_CREDENTIALS, store.getString( ServerPreferencePage.PASSWORD ) );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" ); //$NON-NLS-1$
        env.put( Context. PROVIDER_URL, "ldap://" + store.getString( ServerPreferencePage.HOST ) + ":" + store.getString( ServerPreferencePage.PORT ) + "/" + store.getString( ServerPreferencePage.BASE_BN ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" ); //$NON-NLS-1$
        DirContext ctx = new InitialDirContext(env);
        
        // Setting up search scope
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope( scope );
        
        // Fetching entries
        entries = ctx.search( exportPoint, "(objectClass=*)", ctls); //$NON-NLS-1$
        
        logger.debug( "Entries fetched" ); //$NON-NLS-1$
    }
    
    /**
     * Displays the given message as an error message on the current page
     * @param msg   the message to display
     */
    private void displayErrorMessage(String msg)
    {
        WizardPage page = ( WizardPage ) getContainer().getCurrentPage();
        page.setErrorMessage( msg );     
    }
    
    /**
     * Returns the entries matching the user request.
     * @return the entries matching the user request
     */
    public NamingEnumeration getEntries()
    {
        return entries;
    }
    
    /**
     * Resets already fetched entries.
     */
    public void resetEntries()
    {
        entries = null;
    }
    
    public void setExportPoint( String exportPoint )
    {
        this.exportPoint = exportPoint;
    }

    public void setDestinationFile( String destinationFile )
    {
        this.destinationFile = destinationFile;
    }

    public void setScope( int scope )
    {
        this.scope = scope;
    }
        
    /**
     * Returns the first page of the wizard
     * @return the first page of the wizard
     */
    public ExportWizardFirstPage getFirstPage()
    {
        return exportWizardFirstPage;
    }
}
