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
package org.apache.directory.studio.openldap.config.editor;


import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import org.apache.directory.studio.common.core.jobs.StudioJob;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.openldap.config.editor.databases.ConfigPage;
import org.apache.directory.studio.openldap.config.editor.databases.FrontendPage;
import org.apache.directory.studio.openldap.config.editor.pages.DatabasesPage;
import org.apache.directory.studio.openldap.config.editor.pages.ErrorPage;
import org.apache.directory.studio.openldap.config.editor.pages.LoadingPage;
import org.apache.directory.studio.openldap.config.editor.pages.OpenLDAPServerConfigurationEditorPage;
import org.apache.directory.studio.openldap.config.editor.pages.OptionsPage;
import org.apache.directory.studio.openldap.config.editor.pages.OverviewPage;
import org.apache.directory.studio.openldap.config.editor.pages.SecurityPage;
import org.apache.directory.studio.openldap.config.editor.pages.TuningPage;
import org.apache.directory.studio.openldap.config.jobs.LoadConfigurationRunnable;
import org.apache.directory.studio.openldap.config.model.OpenLdapConfiguration;
import org.apache.directory.studio.openldap.config.model.io.SaveConfigurationRunnable;
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
 * This class implements the Server Configuration Editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLDAPServerConfigurationEditor extends FormEditor implements IPageChangedListener
{
    /** The Editor ID */
    public static final String ID = OpenLDAPServerConfigurationEditor.class.getName();

    /** The flag indicating if the editor is dirty */
    private boolean dirty = false;

    /** The configuration */
    private OpenLdapConfiguration configuration;

    // The pages for the Open LDAP configuration
    /** The Overview page */
    private OverviewPage overviewPage;
    
    /** The page which is used for loading the configuration */
    private LoadingPage loadingPage;
    
    /** The Frontend database page */
    private FrontendPage frontendPage;
    
    /** The Config database page */
    private ConfigPage configPage;
    
    /** The page showing the user's databases */
    private DatabasesPage databasesPage;
    
    /** The options page */
    private OptionsPage optionsPage;
    
    /** The Security page */
    private SecurityPage securityPage;
    
    /** The Tuning page */
    private TuningPage tuningPage;


    /**
     * {@inheritDoc}
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.init( site, input );
        setPartName( input.getName() );

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

        if ( selectedPage instanceof OpenLDAPServerConfigurationEditorPage )
        {
            ( ( OpenLDAPServerConfigurationEditorPage ) selectedPage ).refreshUI();
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
     * the number of pages. If we have one page
     * only, then we show the tab, otherwise, we hide
     * it.
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
     * @param monitor the monitor to use
     * @throws Exception
     */
    public void doSaveAs( IProgressMonitor monitor ) throws Exception
    {
        // Saving pages
        doSavePages( monitor );

        // Saving the configuration as a new file and getting the associated new editor input
        IEditorInput newInput = OpenLDAPServerConfigurationEditorUtils.saveAs( getConfiguration(), true );

        // Checking if the 'save as' is successful 
        if ( newInput != null )
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
    }


    /**
     * Saves the pages.
     *
     * @param monitor the monitor
     */
    private void doSavePages( IProgressMonitor monitor )
    {
        if ( databasesPage != null )
        {
            databasesPage.doSave( monitor );
        }
        
        if ( frontendPage != null )
        {
            frontendPage.doSave( monitor );
        }
        
        if ( securityPage != null )
        {
            securityPage.doSave( monitor );
        }
        
        if ( tuningPage != null )
        {
            tuningPage.doSave( monitor );
        }
        
        if ( configPage != null )
        {
            configPage.doSave( monitor );
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
     * Gets the configuration.
     *
     * @return the configuration
     */
    public OpenLdapConfiguration getConfiguration()
    {
        return configuration;
    }


    /**
     * Sets the configuration.
     *
     * @param configuration the configuration
     */
    public void setConfiguration( OpenLdapConfiguration configuration )
    {
        this.configuration = configuration;
    }


    /**
     * Resets the configuration and refresh the UI.
     *
     * @param configBean the configuration bean
     */
    public void resetConfiguration( OpenLdapConfiguration configuration )
    {
        setConfiguration( configuration );

        setDirty( true );

        overviewPage.refreshUI();
        optionsPage.refreshUI();
        databasesPage.refreshUI();
        frontendPage.refreshUI();
        securityPage.refreshUI();
        tuningPage.refreshUI();
        configPage.refreshUI();
    }


    /**
     * This method is called by the job responsible for loading the 
     * configuration when it has been fully and correctly loaded.
     *
     * @param configBean the loaded configuration bean
     */
    public void configurationLoaded( OpenLdapConfiguration configuration )
    {
        setConfiguration( configuration );

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
            addPage( overviewPage);
            
            databasesPage = new DatabasesPage( this );
            addPage( databasesPage );
            
            securityPage = new SecurityPage( this );
            addPage( securityPage );
            
            tuningPage = new TuningPage( this );
            addPage( tuningPage );
            
            optionsPage = new OptionsPage( this );
            addPage( optionsPage );
        }
        catch ( PartInitException e )
        {
            // Will never happen
        }

        // Activating the first page
        setActivePage( 0 );

        showOrHideTabFolder();
    }


    /**
     * Hides the loading page and displays the error page.
     *
     * @param exception the exception
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
            Enumeration<?> enumeration = pages.elements();
            
            while ( enumeration.hasMoreElements() )
            {
                Object page = enumeration.nextElement();
                
                if ( pageClass.isInstance( page ) )
                {
                    setActivePage( pages.indexOf( page ) );
                    return;
                }
            }
        }
    }


    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public Connection getConnection()
    {
        IEditorInput editorInput = getEditorInput();

        if ( editorInput instanceof ConnectionServerConfigurationInput )
        {
            return ( ( ConnectionServerConfigurationInput ) editorInput ).getConnection();
        }

        return null;
    }
}
