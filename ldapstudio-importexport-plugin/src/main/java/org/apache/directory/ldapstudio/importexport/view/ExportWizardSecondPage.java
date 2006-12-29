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

import java.lang.reflect.InvocationTargetException;

import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.apache.directory.ldapstudio.importexport.Messages;
import org.apache.directory.ldapstudio.importexport.Plugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the second page of the Export Wizard.
 * On this page, the user can see which Entries are going to be exported,
 * according to the information entered in the previous page. 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportWizardSecondPage extends WizardPage
{
    // The logger
    private static Logger logger = LoggerFactory.getLogger( ExportWizardSecondPage.class );
    
    // These fields are used when an error occurs while a progress monitor is running.
    // Since we cannot use the setErrorMessage method while a progress monitor is running,
    // we fire an error flag and store the error message.
    private boolean errorOccurred = false;
    private String errorMessage = ""; //$NON-NLS-1$
    
    private boolean connected = false;
    
    private Table table;
    private Label label;

    private int entriesCounter;

    /**
     * Default constructor
     */
    protected ExportWizardSecondPage()
    {
        super( "ExportAsLDIF2" ); //$NON-NLS-1$
        setTitle( Messages.getString("ExportWizardSecondPage.Corresponding_entries") ); //$NON-NLS-1$
        setDescription( Messages.getString("ExportWizardSecondPage.Wizard_Page_Description") ); //$NON-NLS-1$
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Plugin.ID, ImageKeys.WIZARD_EXPORT ) );
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite container = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        container.setLayout( layout );
        layout.numColumns = 1;
        
        table = new Table( container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
            SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
        table.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
        
        label = new Label( container, SWT.NONE );
        label.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        
        setControl( table );
    }
    
    
    /**
     * This method is called each time the page is shown or hidden.
     */
    public void setVisible( boolean visible )
    {
        if ( visible )
        {
            setErrorMessage( null );

            showEntries();
            
        }
        super.setVisible( visible );
    }
    
    private void showEntries() {
        logger.info( "Starting to show entries" ); //$NON-NLS-1$
       
        // Resetting previous displays
        table.clearAll();
        // SWT Table seems buggy. The ClearAll method doesn't really clears the Table.
        // ItemCount must be set to 0 to really reset the table.
        table.setItemCount( 0 );
        label.setText( "" ); //$NON-NLS-1$
        connected = false;
        errorOccurred = false;
        errorMessage = "";  //$NON-NLS-1$
        
        ExportWizard wizard = ( ExportWizard ) getWizard();
        wizard.setExportPoint( wizard.getFirstPage().getExportPoint() );
        wizard.setScope( wizard.getFirstPage().getScope() );
        wizard.setDestinationFile( wizard.getFirstPage().getDestinationFile() );
        
        // Setting up the anonymous class to execute the progress monitor
        IRunnableWithProgress rwp = new IRunnableWithProgress() {
            public void run( IProgressMonitor monitor )
            {
                monitor.beginTask( "Fetching entries", IProgressMonitor.UNKNOWN ); //$NON-NLS-1$
                fetchEntries( monitor );
                monitor.done();
            }
        };
        
        // Running the Runnable with progress
        try
        {
            getContainer().run(true, false, rwp);
        }
        catch ( InvocationTargetException e1 )
        {
            errorOccurred = true;
            errorMessage = Messages.getString("ExportWizard.An_error_has_occurred_Export_couldnt_be_completed"); //$NON-NLS-1$
        }
        catch ( InterruptedException e1 )
        {
            errorOccurred = true;
            errorMessage = Messages.getString("ExportWizard.An_error_has_occurred_Export_couldnt_be_completed"); //$NON-NLS-1$
        }
     
        if ( connected )
        {
            // Getting corresponding entries
            final NamingEnumeration entries = wizard.getEntries();

            // Setting up the anonymous class to execute the progress monitor
            IRunnableWithProgress op = new IRunnableWithProgress() {
                public void run( IProgressMonitor monitor )
                {
                    monitor.beginTask( "Fetching entries", IProgressMonitor.UNKNOWN ); //$NON-NLS-1$
                    try
                    {
                        showEntries( monitor, entries );
                    }
                    catch ( NamingException e )
                    {
                        errorOccurred = true;
                        errorMessage = Messages.getString("ExportWizardSecondPage.Error_occurred_retreiving_entries"); //$NON-NLS-1$
                    }
                    monitor.done();
                }
            };
            
            // Running the Runnable with progress
            try
            {
                getContainer().run(true, false, op);
            }
            catch ( InvocationTargetException e1 )
            {
                errorOccurred = true;
                errorMessage = Messages.getString("ExportWizard.An_error_has_occurred_Export_couldnt_be_completed"); //$NON-NLS-1$
            }
            catch ( InterruptedException e1 )
            {
                errorOccurred = true;
                errorMessage = Messages.getString("ExportWizard.An_error_has_occurred_Export_couldnt_be_completed"); //$NON-NLS-1$
            }
        }
        
        // Getting number of entries
        int numberOfEntries = table.getItemCount();
        
        // Displaying number of entries
        if ( numberOfEntries == 0 )
        {
            label.setText( Messages.getString("ExportWizardSecondPage.No_corresponding_entries_found") );            //$NON-NLS-1$
        }
        else if ( numberOfEntries == 1 )
        {
            label.setText( Messages.getString("ExportWizardSecondPage.1_entry_found") ); //$NON-NLS-1$
        }
        else
        {
            label.setText( numberOfEntries + Messages.getString("ExportWizardSecondPage.x_entries_found") ); //$NON-NLS-1$
        }
        
        // Displaying Error Message
        if ( errorOccurred )
        {
            logger.error( errorMessage );
            displayErrorMessage( errorMessage );
        }
    }
    

    private void fetchEntries( IProgressMonitor monitor )
    {
        // Connecting to the server and fetching entries
        try
        {
            ExportWizard wizard = ( ExportWizard ) getWizard();
            wizard.fetchEntries();
            connected = true;
        }
        catch ( CommunicationException ce )
        {
            errorOccurred = true;
            errorMessage = Messages.getString("ExportWizardSecondPage.Connection_to_server_failed"); //$NON-NLS-1$
            connected = false;
        }
        catch ( NameNotFoundException nnfe )
        {
            errorOccurred = true;
            errorMessage = Messages.getString("ExportWizardSecondPage.Error_occurred_resolving_Export_Point"); //$NON-NLS-1$
            connected = false;
        }
        catch ( NamingException ne )
        {
            errorOccurred = true;
            errorMessage = Messages.getString("ExportWizardSecondPage.Error_occurred_retreiving_entries"); //$NON-NLS-1$
            connected = false;
        }
    }
    
    private void showEntries( IProgressMonitor monitor, NamingEnumeration entries ) throws NamingException
    {
        // Adding entries to the table
        logger.debug( "Adding entries to the table" ); //$NON-NLS-1$
        entriesCounter = 1;
        while (entries.hasMore()) {
            final SearchResult sr = (SearchResult) entries.next();
            // We need to use asyncExec to be able to access the UI. See http://wiki.eclipse.org/index.php/FAQ_Why_do_I_get_an_invalid_thread_access_exception%3F
            Display.getDefault().asyncExec(new Runnable() {
                public void run()
                {
                    TableItem item2 = new TableItem( table, SWT.NONE );
                    item2.setText( sr.getNameInNamespace() );
                    item2.setImage( AbstractUIPlugin.imageDescriptorFromPlugin(Plugin.ID, ImageKeys.ENTRY).createImage() ); 
                    
                }});
            logger.debug( "fetching : " + sr.getNameInNamespace() ); //$NON-NLS-1$
            monitor.setTaskName( Messages.getString("ExportWizardSecondPage.Fetching_entries") + " " + entriesCounter + Messages.getString("ExportWizardSecondPage.entries_retrieved") ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            entriesCounter++;
        }            
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
}
