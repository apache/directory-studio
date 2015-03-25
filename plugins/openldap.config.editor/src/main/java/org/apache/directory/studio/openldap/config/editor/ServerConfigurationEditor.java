package org.apache.directory.studio.openldap.config.editor;


import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

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

import org.apache.directory.studio.openldap.config.editor.databases.DatabasesPage;
import org.apache.directory.studio.openldap.config.editor.overlays.OverlaysPage;
import org.apache.directory.studio.openldap.config.jobs.LoadConfigurationRunnable;
import org.apache.directory.studio.openldap.config.model.OpenLdapConfiguration;
import org.apache.directory.studio.openldap.config.model.io.SaveConfigurationRunnable;


/**
 * This class implements the Server Configuration Editor.
 */
public class ServerConfigurationEditor extends FormEditor implements IPageChangedListener
{
    /** The Editor ID */
    public static final String ID = ServerConfigurationEditor.class.getName();

    /** The flag indicating if the editor is dirty */
    private boolean dirty = false;

    /** The configuration */
    private OpenLdapConfiguration configuration;

    // The pages
    private LoadingPage loadingPage;
    private OverviewPage overviewPage;
    private DatabasesPage databasesPage;
    private OverlaysPage overlaysPage;


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
     * @param monitor
     *      the monitor to use
     * @throws Exception
     */
    public boolean doSaveAs( IProgressMonitor monitor ) throws Exception
    {
        // Saving the configuration as a new file and getting the associated new editor input
        IEditorInput newInput = null; //ServerConfigurationEditorUtils.saveAs( monitor, getSite().getShell(),
        //getEditorInput(), getConfigWriter() );

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
     * @return
     *      the configuration
     */
    public OpenLdapConfiguration getConfiguration()
    {
        return configuration;
    }


    /*
     * Sets the configuration.
     *
     * @param configuration
     *      the configuration
     */
    public void setConfiguration( OpenLdapConfiguration configuration )
    {
        this.configuration = configuration;
    }


    /**
     * Resets the configuration and refresh the UI.
     *
     * @param configBean
     *      the configuration bean
     */
    public void resetConfiguration( OpenLdapConfiguration configuration )
    {
        setConfiguration( configuration );

        setDirty( true );

        overviewPage.refreshUI();
        databasesPage.refreshUI();
        overlaysPage.refreshUI();
        //        ldapLdapsServersPage.refreshUI();
        //        kerberosServerPage.refreshUI();
        //        partitionsPage.refreshUI();
        //        replicationPage.refreshUI();
    }


    /**
     * This method is called by the job responsible for loading the 
     * configuration when it has been fully and correctly loaded.
     *
     * @param configBean
     *      the loaded configuration bean
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
     * @param exception
     *      the exception
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
            databasesPage = new DatabasesPage( this );
            addPage( databasesPage );
            overlaysPage = new OverlaysPage( this );
            addPage( overlaysPage );
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
     * @param pageClass
     *      the class of the page
     */
    public void showPage( Class<?> pageClass )
    {
        if ( pageClass != null )
        {
            Enumeration<Object> enumeration = pages.elements();
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
}
