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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.directory.server.config.ConfigWriter;
import org.apache.directory.server.config.ConfigurationException;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.shared.ldap.entry.DefaultEntry;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.EntryBasedConfigurationPartition;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.LoadConfigurationRunnable;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.PartitionsDiffComputer;
import org.apache.directory.studio.common.core.jobs.StudioJob;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;


/**
 * This class implements the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerConfigurationEditor extends FormEditor
{
    /** The Editor ID */
    public static final String ID = ServerConfigurationEditor.class.getName();

    /** The flag indicating if the editor is dirty */
    private boolean dirty = false;

    /** The configuration bean */
    private ConfigBean configBean;

    // The pages
    private LoadingPage loadingPage;
    private OverviewPage overviewPage;
    private LdapLdapsServersPage ldapLdapsServersPage;
    private KerberosServerPage kerberosServerPage;
    private PartitionsPage partitionsPage;
    private ReplicationPage replicationPage;


    /**
     * {@inheritDoc}
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.init( site, input );
        setPartName( input.getName() );

        // Checking if the input is a new server configuration file
        if ( input instanceof NewServerConfigurationInput )
        {
            // New server configuration file have a dirty state 
            // set to true since they are not saved yet
            setDirty( true );
        }

        readConfiguration();
    }


    /**
     * Reads the configuration
     */
    private void readConfiguration()
    {
        // Creating and scheduling the job to load the configuration
        StudioJob<StudioRunnableWithProgress> job = new StudioJob<StudioRunnableWithProgress>(
            new LoadConfigurationRunnable( this ) );
        job.schedule();
    }


    /**
     * {@inheritDoc}
     */
    protected void addPages()
    {
        try
        {
            loadingPage = new LoadingPage( this );
            addPage( loadingPage );
        }
        catch ( PartInitException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
        if ( dirty )
        {
            monitor.beginTask( "Saving the server configuration", IProgressMonitor.UNKNOWN );

            try
            {
                IEditorInput input = getEditorInput();
                String inputClassName = input.getClass().getName();
                boolean success = false;
                if ( input instanceof FileEditorInput )
                // FileEditorInput class is used when the file is opened
                // from a project in the workspace.
                {
                    // Saving the ServerConfiguration to disk
                    saveConfiguration( ( FileEditorInput ) input, monitor );
                    success = true;
                }
                // If the input is a ConnectionServerConfigurationInput, then we 
                // read the server configuration from the selected connection
                if ( input instanceof ConnectionServerConfigurationInput )
                {
                    // Saving the ServerConfiguration to the connection
                    saveConfiguration( ( ConnectionServerConfigurationInput ) input, monitor );
                    success = true;
                }
                else if ( input instanceof IPathEditorInput )
                {
                    // Saving the ServerConfiguration to disk
                    saveConfiguration( ( ( IPathEditorInput ) input ).getPath().toFile() );
                    success = true;
                }
                else if ( inputClassName.equals( "org.eclipse.ui.internal.editors.text.JavaFileEditorInput" ) //$NON-NLS-1$
                    || inputClassName.equals( "org.eclipse.ui.ide.FileStoreEditorInput" ) ) //$NON-NLS-1$
                // The class 'org.eclipse.ui.internal.editors.text.JavaFileEditorInput'
                // is used when opening a file from the menu File > Open... in Eclipse 3.2.x
                // The class 'org.eclipse.ui.ide.FileStoreEditorInput' is used when
                // opening a file from the menu File > Open... in Eclipse 3.3.x
                {
                    // Saving the ServerConfiguration to disk
                    saveConfiguration( input.getToolTipText() );
                    success = true;
                }
                else if ( input instanceof NewServerConfigurationInput )
                {
                    // The 'ServerConfigurationEditorInput' class is used when a
                    // new Server Configuration File is created.

                    // We are saving this as if it is a "Save as..." action.
                    success = doSaveAs( monitor );
                }

                setDirty( !success );
                monitor.done();
            }
            catch ( Exception e )
            {
                // TODO
                e.printStackTrace();
            }
        }
    }


    /**
     * Saves the configuration.
     *
     * @param input
     *      the file editor input
     * @param monitor
     *      the monitor
     * @throws Exception
     */
    private void saveConfiguration( FileEditorInput input, IProgressMonitor monitor ) throws Exception
    {
        input.getFile().setContents( new ByteArrayInputStream( getConfigWriter().writeToString().getBytes() ), true,
            true, monitor );
    }


    /**
     * Saves the configuration.
     *
     * @param input
     *      the connection server configuration input
     * @param monitor
     *      the monitor
     * @return
     *      <code>true</code> if the operation is successful,
     *      <code>false</code> if not
     * @throws ConfigurationException 
     * @throws Exception
     */
    private void saveConfiguration( ConnectionServerConfigurationInput input, IProgressMonitor monitor )
        throws ConfigurationException, Exception
    {
        // Getting the original configuration partition
        EntryBasedConfigurationPartition originalPartition = input.getOriginalPartition();

        // Creating a new configuration partition
        SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();
        EntryBasedConfigurationPartition newconfigurationPartition = new EntryBasedConfigurationPartition(
            schemaManager );
        newconfigurationPartition.initialize();
        List<LdifEntry> convertedLdifEntries = getConfigWriter().getConvertedLdifEntries();
        for ( LdifEntry ldifEntry : convertedLdifEntries )
        {
            newconfigurationPartition.addEntry( new DefaultEntry( schemaManager, ldifEntry.getEntry() ) );
        }

        // Comparing both partitions to get the list of modifications to be applied
        PartitionsDiffComputer partitionsDiffComputer = new PartitionsDiffComputer();
        partitionsDiffComputer.setOriginalPartition( originalPartition );
        partitionsDiffComputer.setDestinationPartition( newconfigurationPartition );
        List<LdifEntry> modificationsList = partitionsDiffComputer.computeModifications( new String[]
            { "*" } );

        System.out.println( modificationsList );

        // Building the resulting LDIF
        StringBuilder modificationsLdif = new StringBuilder();
        for ( LdifEntry ldifEntry : modificationsList )
        {
            modificationsLdif.append( ldifEntry.toString() );
        }

        // Getting the browser connection associated with the 
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( input.getConnection() );

        // Creating a StudioProgressMonitor to run the LDIF with
        StudioProgressMonitor studioProgressMonitor = new StudioProgressMonitor( monitor );

        // Updating the configuration with the resulting LDIF
        ExecuteLdifRunnable.executeLdif( browserConnection, modificationsLdif.toString(), true, true,
            studioProgressMonitor );

        // Checking if there were errors during the execution of the LDIF
        if ( studioProgressMonitor.errorsReported() )
        {
            // TODO handle error
        }
        else
        {
            System.out.println( "swapping partition" );

            // Swapping the new configuration partition
            input.setOriginalPartition( newconfigurationPartition );
        }
    }


    /**
     * Saves the configuration.
     *
     * @param file
     *      the file
     * @throws Exception
     */
    private void saveConfiguration( File file ) throws Exception
    {
        getConfigWriter().writeToFile( file );
    }


    /**
     * Saves the configuration.
     *
     * @param path
     *      the path
     * @throws Exception
     */
    private void saveConfiguration( String path ) throws Exception
    {
        saveConfiguration( new File( path ) );
    }


    /**
     * {@inheritDoc}
     */
    public void doSaveAs()
    {
        try
        {
            getSite().getWorkbenchWindow().run( false, false, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                    try
                    {
                        monitor.beginTask( "Saving Server Configuration", IProgressMonitor.UNKNOWN );
                        doSaveAs( monitor );
                        monitor.done();
                    }
                    catch ( Exception e )
                    {
                        // TODO handle the exception
                    }
                }
            } );
        }
        catch ( Exception e )
        {
            // TODO handle the exception
            e.printStackTrace();
        }
    }


    /**
     * Performs the "Save as..." action.
     *
     * @param monitor
     *      the monitor to use
     * @throws Exception
     */
    private boolean doSaveAs( IProgressMonitor monitor ) throws Exception
    {
        // Saving the configuration as a new file and getting the associated new editor input
        IEditorInput newInput = ServerConfigurationEditorUtils.doSaveAs( monitor, getSite().getShell(),
            getEditorInput(), getConfigWriter() );

        // Checking if the 'save as' is successful 
        boolean success = newInput != null;
        if ( success )
        {
            // Setting the new input to the editor
            setInput( newInput );

            // Resetting the dirty state of the editor
            setDirty( false );

            // Updating the title and tooltip texts
            setPartName( getEditorInput().getName() );
        }

        return success;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return dirty;
    }


    /**
     * Sets the 'dirty' flag.
     *
     * @param dirty
     *      the 'dirty' flag
     */
    public void setDirty( boolean dirty )
    {
        this.dirty = dirty;
        firePropertyChange( PROP_DIRTY );
    }


    /**
     * Gets the configuration bean.
     *
     * @return
     *      the configuration bean
     */
    public ConfigBean getConfigBean()
    {
        return configBean;
    }


    /**
     * Sets the configuration.
     *
     * @param configBean
     *      the configuration bean
     */
    public void setConfiguration( ConfigBean configBean )
    {
        this.configBean = configBean;
    }


    /**
     * Resets the configuration and refresh the UI.
     *
     * @param configBean
     *      the configuration bean
     */
    public void resetConfiguration( ConfigBean configBean )
    {
        setConfiguration( configBean );

        overviewPage.refreshUI();
        ldapLdapsServersPage.refreshUI();
        kerberosServerPage.refreshUI();
        partitionsPage.refreshUI();
        replicationPage.refreshUI();
    }


    /**
     * This method is called by the job responsible for loading the configuration.
     *
     * @param configBean
     *      the loaded configuration bean
     */
    public void configurationLoaded( ConfigBean configBean )
    {
        setConfiguration( configBean );

        hideLoadingPageAndDisplayConfigPages();
    }


    /**
     * Hides the loading page and displays the standard configuration pages.
     */
    private void hideLoadingPageAndDisplayConfigPages()
    {
        // Removing the loading page
        removePage( 0 );

        // Adding the configuration pages
        try
        {
            overviewPage = new OverviewPage( this );
            addPage( overviewPage );
            ldapLdapsServersPage = new LdapLdapsServersPage( this );
            addPage( ldapLdapsServersPage );
            kerberosServerPage = new KerberosServerPage( this );
            addPage( kerberosServerPage );
            partitionsPage = new PartitionsPage( this );
            addPage( partitionsPage );
            replicationPage = new ReplicationPage( this );
            addPage( replicationPage );
        }
        catch ( PartInitException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Activating the first page
        setActivePage( 0 );
    }


    /**
     * Gets the configuration writer.
     *
     * @return
     *      the configuration writer
     * @throws Exception
     */
    public ConfigWriter getConfigWriter() throws Exception
    {
        return new ConfigWriter( ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager(), configBean );
    }
}
