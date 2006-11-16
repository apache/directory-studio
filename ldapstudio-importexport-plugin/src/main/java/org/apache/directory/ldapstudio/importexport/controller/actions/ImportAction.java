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

package org.apache.directory.ldapstudio.importexport.controller.actions;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.ldapstudio.importexport.Activator;
import org.apache.directory.ldapstudio.importexport.Messages;
import org.apache.directory.ldapstudio.importexport.view.ImportEntriesOnErrorDialog;
import org.apache.directory.ldapstudio.importexport.view.ServerPreferencePage;
import org.apache.directory.server.tools.ToolCommandListener;
import org.apache.directory.server.tools.commands.importcmd.ImportCommandExecutor;
import org.apache.directory.server.tools.util.ListenerParameter;
import org.apache.directory.server.tools.util.Parameter;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the Import Action. It uses the Apache DS Tools
 * library to import entries from a LDIF file.
 */
public class ImportAction implements IWorkbenchWindowActionDelegate, IRunnableWithProgress
{
    // The logger
    private static Logger logger = LoggerFactory.getLogger( ImportAction.class );
    
    // Fields used to call the Import Command
    private String host;
    private int port;
    private String userDN;
    private String password;
    private Parameter[] params;
    private ListenerParameter[] listenerParams;

    // Flag to know if the preferences are filled and correct
    private boolean preferencesOk = false;

    // The progress monitor to update
    private IProgressMonitor monitor;

    // Fields used for displaying error message after the progress monitor is closed,
    // since we can't update UI when the progress monitor is working
    private boolean hasRaisedAnException = false;
    private Exception exceptionRaised;
    
    private List<String> addedEntries;
    private List<String> errorEntries;
    private int numberOfEntriesInFile = 0;
    private int progressMonitorCount = 0;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    /**
     * This method is run when the menu item is clicked
     */
    public void run( IAction action )
    {
        logger.info( "Starting Import Action" ); //$NON-NLS-1$
        
        // Re-setting up fields to defaults for another import
        numberOfEntriesInFile = 0;
        progressMonitorCount = 0;
        addedEntries = new ArrayList<String>();
        errorEntries = new ArrayList<String>();
        hasRaisedAnException = false;
        exceptionRaised = null;
        
        // We first look at prefs to see if we can communicate correctly with the server
        processPreferences();

        if ( !preferencesOk )
        {
            // If prefs are not well filled, we stop here. It's no use to try importing.
            return;
        }

        // We prompt user for the input file
        FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN );
        fd.setText( Messages.getString("ImportAction.Open_a_LDIF_file") ); //$NON-NLS-1$
        fd.setFilterPath( System.getProperty( "user.home" ) ); //$NON-NLS-1$
        fd.setFilterExtensions( new String[]
            { "*.ldif;*.LDIF", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$
        fd.setFilterNames( new String[]
            { Messages.getString("ImportAction.LDIF_Files"), Messages.getString("ImportAction.All_files") } ); //$NON-NLS-1$ //$NON-NLS-2$

        String selected = fd.open();
        if ( selected == null )
        {
            // User has canceled the dialog, we stop
            logger.info( "User has canceled the Open Dialog" ); //$NON-NLS-1$
            return;
        }
                
        File ldifFile = new File( selected );
        if ( !ldifFile.exists() )
        {
            logger.error( "The selected file \"" + ldifFile.getName() + "\" doesn't exist. Stoping Import Action." ); //$NON-NLS-1$ //$NON-NLS-2$
            showAlertError( Messages.getString("ImportAction.The_Selected_file") + ldifFile.getName() + Messages.getString("ImportAction.Doesnt_exist") ); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }

        // We parse the input file once to count how many entries it contains
        logger.info( "Parsing file " + ldifFile.getName() + "(" + ldifFile.getAbsolutePath() + ")" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        try
        {
            numberOfEntriesInFile = getNumberOfEntries( ldifFile, selected );
        }
        catch ( NamingException ne )
        {
            logger.error( "An error occured while parsing the LDIF File. " + ne.getMessage() ); //$NON-NLS-1$
            showAlertError( Messages.getString("ImportAction.Error_occurred_parsing_file") + ne.getMessage() ); //$NON-NLS-1$
            return;
        }

        // Preparing the call of theImport Command of the Apache DS Tools
        // Parameters
        Parameter param_ldifFile = new Parameter( ImportCommandExecutor.FILE_PARAMETER, ldifFile );
        Parameter param_host = new Parameter( ImportCommandExecutor.HOST_PARAMETER, host );
        Parameter param_port = new Parameter( ImportCommandExecutor.PORT_PARAMETER, new Integer( port ) );
        Parameter param_user = new Parameter( ImportCommandExecutor.USER_PARAMETER, userDN );
        Parameter param_password = new Parameter( ImportCommandExecutor.PASSWORD_PARAMETER, password );
        Parameter param_auth = new Parameter( ImportCommandExecutor.AUTH_PARAMETER, "simple" ); //$NON-NLS-1$
        Parameter param_ignoreErrors = new Parameter( ImportCommandExecutor.IGNOREERRORS_PARAMETER, new Boolean( true ) );
        params = new Parameter[]{ param_ldifFile, param_host, param_port, param_user, param_password, param_auth, param_ignoreErrors };
        
        // Listeners
        listenerParams = initListeners();
        
        ProgressMonitorDialog dialog = new ProgressMonitorDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        
        try
        {
            dialog.run( true, false, this );
        }
        catch ( InvocationTargetException ex )
        {
            logger.error( "An error has occured when running the import. ", ex ); //$NON-NLS-1$
            showAlertError( Messages.getString("ImportAction.An_error_has_occurred") ); //$NON-NLS-1$
            return;
        }
        catch ( InterruptedException ex )
        {
            logger.error( "An error has occured when running the import. ", ex ); //$NON-NLS-1$
            showAlertError( Messages.getString("ImportAction.An_error_has_occurred") ); //$NON-NLS-1$
            return;
        }
        
        if ( !isImportSuccessful() )
        {
            // Testing if the import has raised an exception
            if ( hasRaisedAnException )
            {
                logger.error( "Import is not successful, an error has occurred. ", exceptionRaised ); //$NON-NLS-1$
                showAlertError( exceptionRaised.getMessage() );
                hasRaisedAnException = false;
            }
            else
            {
                // If the import has not raised an exception and the import is not successful
                // then it means that some entries could not have been imported
                logger.error( "Some entries could not have been imported "); //$NON-NLS-1$
                ImportEntriesOnErrorDialog errorDialog = new ImportEntriesOnErrorDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), addedEntries, errorEntries );
                errorDialog.open();
            }
        }
        else
        {
            logger.info( "Import succesful" ); //$NON-NLS-1$
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.ICON_INFORMATION );
            messageBox.setText( Messages.getString("ImportAction.Import_successful") ); //$NON-NLS-1$
            messageBox.setMessage( numberOfEntriesInFile + Messages.getString("ImportAction.Entries_were_successfully_added") ); //$NON-NLS-1$
            messageBox.open();
        }
        
        logger.info( "Stopping Import Action" ); //$NON-NLS-1$
    }   

    /**
     * Returns the array of ListenerParameter for the Import Command
     * @return the array of ListenerParameter for the Import Command
     */
    private ListenerParameter[] initListeners()
    {
        ListenerParameter exceptionListener = new ListenerParameter( ImportCommandExecutor.EXCEPTIONLISTENER_PARAMETER, new ToolCommandListener() { //$NON-NLS-1$
            public void notify( Serializable o )
            {
                if ( o instanceof Exception )
                {
                    logger.error( "An exception was raised during the import.", o); //$NON-NLS-1$
                    hasRaisedAnException = true;
                    exceptionRaised = ( Exception ) o;
                }
            } 
        });
        ListenerParameter entryAddedListener = new ListenerParameter( ImportCommandExecutor.ENTRYADDEDLISTENER_PARAMETER, new ToolCommandListener() { //$NON-NLS-1$
            public void notify( Serializable o )
            {
                if ( o instanceof String )
                {
                    String entry = ( String ) o;
                    logger.debug( "Entry added: " + entry ); //$NON-NLS-1$
                    addedEntries.add( entry );
                    updateProgressMonitor();
                }
                
            } 
        });
        ListenerParameter entryAddFailedListener = new ListenerParameter( ImportCommandExecutor.ENTRYADDFAILEDLISTENER_PARAMETER, new ToolCommandListener() { //$NON-NLS-1$
            public void notify( Serializable o )
            {
                if ( o instanceof String )
                {
                    String entry = ( String ) o;
                    logger.debug( "Entry add failed: " + entry ); //$NON-NLS-1$
                    errorEntries.add( entry );
                    updateProgressMonitor();
                }
            }     
        });
        
        return new ListenerParameter[] { exceptionListener, entryAddedListener, entryAddFailedListener } ;
    }
    

    /**
     * Returns the number of entries in a LDIF file
     * @param ldifFile
     * @param path
     * @return
     * @throws NamingException
     */
    private int getNumberOfEntries( File ldifFile, String path ) throws NamingException
    {
        LdifReader ldifReader;
        int counter = 0;
        
        ldifReader = new LdifReader( ldifFile );

        List entries = ldifReader.parseLdifFile( path );

        Iterator entriesIterator = entries.iterator();
        while ( entriesIterator.hasNext() )
        {
            counter++;
            entriesIterator.next();
        }
        return counter;
    }

    /**
     * Verifies that the preferences are Ok.
     */
    private void processPreferences()
    {
        logger.info( "Processing Preferences" ); //$NON-NLS-1$
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        // HOST
        String prefHost = store.getString( ServerPreferencePage.HOST );
        if ( ( prefHost == null ) || ( "".equals( prefHost ) ) ) //$NON-NLS-1$
        {
            logger.error( "Host value in the preference page is empty." ); //$NON-NLS-1$
            showAlertError( Messages.getString("ImportAction.Host_empy") ); //$NON-NLS-1$
            openPreferenceWindow();
            return;
        }
        host = prefHost;

        // PORT
        String prefPort = store.getString( ServerPreferencePage.PORT );
        int portValue;
        if ( ( prefPort == null ) || ( "".equals( prefPort ) ) ) //$NON-NLS-1$
        {
            logger.error( "Port value in the preference page is empty." ); //$NON-NLS-1$
            showAlertError( Messages.getString("ImportAction.Port_empty") ); //$NON-NLS-1$
            openPreferenceWindow();
            return;
        }
        else
        {
            try
            {
                portValue = Integer.parseInt( prefPort );
            }
            catch ( NumberFormatException nfe )
            {
                logger.error( "Port value in the preference page does not seem to be a number." ); //$NON-NLS-1$
                showAlertError( Messages.getString("ImportAction.Port_not_a_number") ); //$NON-NLS-1$
                openPreferenceWindow();
                return;
            }
        }
        port = portValue;

        // USER-DN
        String prefUserDN = store.getString( ServerPreferencePage.USER_DN );
        if ( ( prefUserDN == null ) || ( "".equals( prefUserDN ) ) ) //$NON-NLS-1$
        {
            logger.error( "User DN value in the preference page is empty." ); //$NON-NLS-1$
            showAlertError( Messages.getString("ImportAction.UserDN_empty") ); //$NON-NLS-1$
            openPreferenceWindow();
            return;
        }
        userDN = prefUserDN;

        // PASSWORD
        String prefPassword = store.getString( ServerPreferencePage.PASSWORD );
        password = prefPassword;

        // Server preferences are clean
        preferencesOk = true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    /**
     * This method is run by the ProgressMonitorDialog and is used to display the progress window
     */
    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
    {
        this.monitor = monitor;
        monitor.beginTask( Messages.getString("ImportAction.Importing_entries"), numberOfEntriesInFile ); //$NON-NLS-1$
        monitor.subTask( Messages.getString("ImportAction.Adding_entries_1_on") + numberOfEntriesInFile + Messages.getString("ImportAction.Parenthesis_close") ); //$NON-NLS-1$ //$NON-NLS-2$
        progressMonitorCount++;

        // Logging the Import Command Call
        logger.debug( "Calling the Apache DS Tools Import command with the following parameters:" ); //$NON-NLS-1$
        if ( logger.isDebugEnabled() )
        {
            for ( int i = 0; i < params.length; i++ )
            {
                Parameter param = params[i];
                if ( !param.getName().equals( "password" ) ) //$NON-NLS-1$
                {
                    logger.debug( "Parameter - name: " + param.getName() + " - value: " + param.getValue() ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            for ( int i = 0; i < listenerParams.length; i++ )
            {
                ListenerParameter param = listenerParams[i];
                logger.debug( "ListenerParameter - name: " + param.getName() ); //$NON-NLS-1$
            }
        }
        
        
        // Calling the Apache DS Tools Import Command
        ImportCommandExecutor cmd = new ImportCommandExecutor();
        cmd.execute( params, listenerParams );
        
        monitor.subTask( Messages.getString("ImportAction.Done") ); //$NON-NLS-1$
        monitor.done();
    }   

    /**
     * Updates the progress monitor.
     * Notifies that a work unit of the import task has been completed.
     */
    private void updateProgressMonitor()
    {
        monitor.worked( 1 );
        monitor.subTask( Messages.getString("ImportAction.Adding_entries_x") + progressMonitorCount++ + Messages.getString("ImportAction.Adding_on") + numberOfEntriesInFile + Messages.getString("ImportAction.Parenthesis_close") ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    } 

    /**
     * Displays an Error Message Box with the provided message
     * @param msg
     *              the message to be displayed
     */
    private void showAlertError( String msg )
    {
        MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            SWT.ICON_ERROR );
        messageBox.setText( Messages.getString("ImportAction.Import_error") ); //$NON-NLS-1$
        messageBox.setMessage( msg );
        messageBox.open();
    }

    /**
     * Opens the Server Configuration preference page
     */
    private void openPreferenceWindow()
    {
        PreferenceDialog pd = new PreferenceDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            PlatformUI.getWorkbench().getPreferenceManager() );
        pd.setSelectedNode( "org.apache.directory.ldapstudio.importexport.server" ); //$NON-NLS-1$
        pd.open();
    }
    
    /**
     * Returns true if the import was successful
     * @return true if the import was successful
     */
    private boolean isImportSuccessful()
    {
        return ( ( !hasRaisedAnException ) && ( exceptionRaised == null) && ( errorEntries.size() == 0 ) );
    }
    
    public void selectionChanged( IAction action, ISelection selection )
    {
        // This method does nothing, but is needed by the IWorkbenchWindowActionDelegate Interface
    }

    public void dispose()
    {
        // This method does nothing, but is needed by the IWorkbenchWindowActionDelegate Interface
    }

    public void init( IWorkbenchWindow window )
    {
        // This method does nothing, but is needed by the IWorkbenchWindowActionDelegate Interface
    }
}
