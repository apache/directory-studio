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


import java.lang.reflect.InvocationTargetException;

import org.apache.directory.server.config.ConfigWriter;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.LoadConfigurationRunnable;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.SaveConfigurationRunnable;
import org.apache.directory.studio.common.core.jobs.StudioJob;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;


/**
 * This class implements the Server Configuration Editor. This editor expose
 * 6 pages into a form with 6 tags :
 * <ul>
 * <li>Overview : the basic configuration</li>
 * <li>LDAP/LDAPS : the configuration for the LDAP/S server</li>
 * <li>Kerberos : the configuration for the Kerberos server</li>
 * <li>Partitions : The partitions configuration</li>
 * <li>PasswordPolicy : The password policy configuration</li>
 * <li>Replication : The replicationconfiguration</li>
 * </ul> 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerConfigurationEditor extends FormEditor implements IPageChangedListener
{
    /** The Editor ID */
    public static final String ID = ServerConfigurationEditor.class.getName();

    /** The flag indicating if the editor is dirty */
    private boolean dirty = false;

    /** The configuration bean */
    private ConfigBean configBean;

    /** The pages */
    private LoadingPage loadingPage;
    private OverviewPage overviewPage;
    private LdapLdapsServersPage ldapLdapsServersPage;
    private KerberosServerPage kerberosServerPage;
    private PartitionsPage partitionsPage;
    private PasswordPoliciesPage passwordPolicyPage;
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

        addPageChangedListener( this );

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
    public void pageChanged( PageChangedEvent event )
    {
        Object selectedPage = event.getSelectedPage();

        if ( selectedPage instanceof ServerConfigurationEditorPage )
        {
            ( ( ServerConfigurationEditorPage ) selectedPage ).refreshUI();
        }
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
        }

        showOrHideTabFolder();
    }


    /**
     * Shows or hides the tab folder depending on
     * the number of pages.
     */
    private void showOrHideTabFolder()
    {
        Composite container = getContainer();
        
        if ( container instanceof CTabFolder )
        {
            CTabFolder folder = ( CTabFolder ) container;
            
            if ( getPageCount() == 1 )
            {
                folder.setTabHeight( 0 );
            }
            else
            {
                folder.setTabHeight( -1 );
            }
            
            folder.layout( true, true );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
        // Saving pages
        doSavePages( monitor );

        // Saving the configuration using a job
        StudioJob<StudioRunnableWithProgress> job = new StudioJob<StudioRunnableWithProgress>(
            new SaveConfigurationRunnable( this ) );
        job.schedule();
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
                        monitor.beginTask(
                            Messages.getString( "ServerConfigurationEditor.SavingServerConfiguration" ), IProgressMonitor.UNKNOWN ); //$NON-NLS-1$
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
     * @param monitor the monitor to use
     * @throws Exception
     */
    public boolean doSaveAs( IProgressMonitor monitor ) throws Exception
    {
        // Saving pages
        doSavePages( monitor );

        // Saving the configuration as a new file and getting the associated new editor input
        IEditorInput newInput = ServerConfigurationEditorUtils.saveAs( monitor, getSite().getShell(),
            getEditorInput(), getConfigWriter(), true );

        // Checking if the 'save as' is successful 
        boolean success = newInput != null;
        
        if ( success )
        {
            // Setting the new input to the editor
            setInput( newInput );

            // Resetting the dirty state of the editor
            setDirty( false );

            // Updating the title and tooltip texts
            Display.getDefault().syncExec( new Runnable()
            {
                public void run()
                {
                    setPartName( getEditorInput().getName() );
                }
            } );
        }

        return success;
    }


    /**
     * Saves the pages.
     *
     * @param monitor the monitor
     */
    private void doSavePages( IProgressMonitor monitor )
    {
        if ( partitionsPage != null )
        {
            partitionsPage.doSave( monitor );
        }
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
     * @param dirty the 'dirty' flag
     */
    public void setDirty( boolean dirty )
    {
        this.dirty = dirty;

        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                firePropertyChange( PROP_DIRTY );
            }
        } );
    }


    /**
     * Gets the configuration bean.
     *
     * @return the configuration bean
     */
    public ConfigBean getConfigBean()
    {
        return configBean;
    }


    /**
     * Sets the configuration.
     *
     * @param configBean the configuration bean
     */
    public void setConfiguration( ConfigBean configBean )
    {
        this.configBean = configBean;
    }


    /**
     * Resets the configuration and refresh the UI.
     *
     * @param configBean the configuration bean
     */
    public void resetConfiguration( ConfigBean configBean )
    {
        setConfiguration( configBean );

        setDirty( true );

        overviewPage.refreshUI();
        ldapLdapsServersPage.refreshUI();
        kerberosServerPage.refreshUI();
        partitionsPage.refreshUI();
        passwordPolicyPage.refreshUI();
        replicationPage.refreshUI();
    }


    /**
     * This method is called by the job responsible for loading the 
     * configuration when it has been fully and correctly loaded.
     *
     * @param configBean the loaded configuration bean
     */
    public void configurationLoaded( ConfigBean configBean )
    {
        setConfiguration( configBean );

        hideLoadingPageAndDisplayConfigPages();
    }


    /**
     * This method is called by the job responsible for loading the
     * configuration when it failed to load it.
     *
     * @param exception the exception
     */
    public void configurationLoadFailed( Exception exception )
    {
        // Overriding the default dirty setting 
        // (especially in the case of a new configuration file)
        setDirty( false );

        hideLoadingPageAndDisplayErrorPage( exception );
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
            passwordPolicyPage = new PasswordPoliciesPage( this );
            addPage( passwordPolicyPage );
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

        showOrHideTabFolder();
    }


    /**
     * Hides the loading page and displays the error page.
     *
     * @param exception
     *      the exception
     */
    private void hideLoadingPageAndDisplayErrorPage( Exception exception )
    {
        // Removing the loading page
        removePage( 0 );

        // Adding the error page
        try
        {
            addPage( new ErrorPage( this, exception ) );
        }
        catch ( PartInitException e )
        {
        }

        // Activating the first page
        setActivePage( 0 );

        showOrHideTabFolder();
    }


    /**
     * Set a particular page as active if it is found in the pages vector.
     *
     * @param pageClass the class of the page
     */
    public void showPage( Class<?> pageClass )
    {
        if ( pageClass != null )
        {
            for ( Object page : pages )
            {
                if ( pageClass.isInstance( page ) )
                {
                    setActivePage( pages.indexOf( page ) );
                    
                    return;
                }
            }
        }
    }


    /**
     * Gets the configuration writer.
     *
     * @return the configuration writer
     * @throws Exception
     */
    public ConfigWriter getConfigWriter() throws Exception
    {
        return new ConfigWriter( ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager(), configBean );
    }
}
