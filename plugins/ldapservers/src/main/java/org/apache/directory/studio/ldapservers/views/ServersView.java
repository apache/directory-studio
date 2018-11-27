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
package org.apache.directory.studio.ldapservers.views;


import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.LdapServersManagerListener;
import org.apache.directory.studio.ldapservers.LdapServersPluginConstants;
import org.apache.directory.studio.ldapservers.actions.DeleteAction;
import org.apache.directory.studio.ldapservers.actions.NewServerAction;
import org.apache.directory.studio.ldapservers.actions.OpenConfigurationAction;
import org.apache.directory.studio.ldapservers.actions.PropertiesAction;
import org.apache.directory.studio.ldapservers.actions.RenameAction;
import org.apache.directory.studio.ldapservers.actions.StartAction;
import org.apache.directory.studio.ldapservers.actions.StopAction;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Servers view.
 * <p>
 * It displays the list of Apache Directory Servers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServersView extends ViewPart
{
    /** The ID of the view */
    //    public static final String ID = ApacheDsPluginConstants.VIEW_SERVERS_VIEW; // TODO

    /** The tree*/
    private Tree tree;

    /** The table viewer */
    private ServersTableViewer tableViewer;

    /** The view instance */
    private ServersView instance;

    /** Token used to activate and deactivate shortcuts in the view */
    private IContextActivation contextActivation;

    private static final String TAG_COLUMN_WIDTH = "columnWidth"; //$NON-NLS-1$
    protected int[] columnWidths;

    // Actions
    private NewServerAction newServer;
    private OpenConfigurationAction openConfiguration;
    private DeleteAction delete;
    private RenameAction rename;
    private StartAction start;
    private StopAction stop;
    private PropertiesAction properties;

    // Listeners
    private LdapServersManagerListener ldapServersManagerListener = new LdapServersManagerListener()
    {
        public void serverAdded( LdapServer server )
        {
            asyncRefresh();
        }


        public void serverRemoved( LdapServer server )
        {
            asyncRefresh();
        }


        public void serverUpdated( LdapServer server )
        {
            asyncRefresh();
        }
    };


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {
        instance = this;

        // Creating the Tree
        tree = new Tree( parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL );
        tree.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        tree.setHeaderVisible( true );
        tree.setLinesVisible( false );

        // Adding columns
        TreeColumn serverColumn = new TreeColumn( tree, SWT.SINGLE );
        serverColumn.setText( Messages.getString( "ServersView.server" ) ); //$NON-NLS-1$
        serverColumn.setWidth( columnWidths[0] );
        serverColumn.addSelectionListener( getColumnSelectionListener( 0 ) );
        tree.setSortColumn( serverColumn );
        tree.setSortDirection( SWT.UP );

        TreeColumn stateColumn = new TreeColumn( tree, SWT.SINGLE );
        stateColumn.setText( Messages.getString( "ServersView.state" ) ); //$NON-NLS-1$
        stateColumn.setWidth( columnWidths[1] );
        stateColumn.addSelectionListener( getColumnSelectionListener( 1 ) );

        // Creating the viewer
        tableViewer = new ServersTableViewer( tree );

        initActions();
        initToolbar();
        initContextMenu();
        initListeners();

        // set help context
        // TODO
        //        PlatformUI.getWorkbench().getHelpSystem()
        //            .setHelp( parent, ApacheDsPluginConstants.PLUGIN_ID + "." + "gettingstarted_views_servers" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public void init( IViewSite site, IMemento memento ) throws PartInitException
    {
        super.init( site, memento );
        columnWidths = new int[]
            { 150, 80 };
        for ( int i = 0; i < 2; i++ )
        {
            if ( memento != null )
            {
                Integer in = memento.getInteger( TAG_COLUMN_WIDTH + i );
                if ( in != null && in.intValue() > 5 )
                {
                    columnWidths[i] = in.intValue();
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void saveState( IMemento memento )
    {
        TreeColumn[] tc = tableViewer.getTree().getColumns();
        for ( int i = 0; i < 2; i++ )
        {
            int width = tc[i].getWidth();
            if ( width != 0 )
            {
                memento.putInteger( TAG_COLUMN_WIDTH + i, width );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        if ( tree != null )
        {
            tree.setFocus();
        }
    }


    /**
     * Initializes the actions.
     */
    private void initActions()
    {
        newServer = new NewServerAction();

        openConfiguration = new OpenConfigurationAction( this );
        openConfiguration.setEnabled( false );

        delete = new DeleteAction( this );
        delete.setEnabled( false );

        rename = new RenameAction( this );
        rename.setEnabled( false );

        start = new StartAction( this );
        start.setEnabled( false );

        stop = new StopAction( this );
        stop.setEnabled( false );

        properties = new PropertiesAction( this );
        properties.setEnabled( false );
    }


    /**
     * Initializes the toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add( newServer );
        toolbar.add( new Separator() );
        toolbar.add( start );
        toolbar.add( stop );
    }


    /**
     * Initializes the Context Menu.
     */
    private void initContextMenu()
    {
        MenuManager contextMenu = new MenuManager( "" ); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown( true );
        contextMenu.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                MenuManager newManager = new MenuManager( Messages.getString( "ServersView.new" ) ); //$NON-NLS-1$
                newManager.add( newServer );
                manager.add( newManager );
                manager.add( openConfiguration );
                manager.add( new Separator() );
                manager.add( delete );
                manager.add( rename );
                manager.add( new Separator() );
                manager.add( start );
                manager.add( stop );
                manager.add( new Separator() );
                manager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
                manager.add( new Separator() );
                manager.add( new Separator() );
                manager.add( properties );
            }
        } );

        // set the context menu to the table viewer
        tableViewer.getControl().setMenu( contextMenu.createContextMenu( tableViewer.getControl() ) );

        // register the context menu to enable extension actions
        getSite().registerContextMenu( contextMenu, tableViewer );
    }


    /**
     * Initializes the listeners
     */
    private void initListeners()
    {
        LdapServersManager serversHandler = LdapServersManager.getDefault();
        serversHandler.addListener( ldapServersManagerListener );

        tableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                openConfiguration.run();
            }
        } );

        tableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                updateActionsStates();
            }
        } );

        // Initializing the PartListener
        getSite().getPage().addPartListener( new IPartListener2()
        {
            /**
              * This implementation deactivates the shortcuts when the part is deactivated.
              */
            public void partDeactivated( IWorkbenchPartReference partRef )
            {
                if ( partRef.getPart( false ) == instance && contextActivation != null )
                {
                    ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
                        ICommandService.class );
                    if ( commandService != null )
                    {
                        commandService.getCommand( newServer.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( openConfiguration.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( delete.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( rename.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( start.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( stop.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( properties.getActionDefinitionId() ).setHandler( null );
                    }

                    IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                        IContextService.class );
                    contextService.deactivateContext( contextActivation );
                    contextActivation = null;
                }
            }


            /**
             * This implementation activates the shortcuts when the part is activated.
             */
            public void partActivated( IWorkbenchPartReference partRef )
            {
                if ( partRef.getPart( false ) == instance )
                {
                    IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                        IContextService.class );
                    contextActivation = contextService
                        .activateContext( LdapServersPluginConstants.CONTEXTS_SERVERS_VIEW );

                    ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
                        ICommandService.class );
                    if ( commandService != null )
                    {
                        commandService.getCommand( newServer.getActionDefinitionId() ).setHandler(
                            new ActionHandler( newServer ) );
                        commandService.getCommand( openConfiguration.getActionDefinitionId() ).setHandler(
                            new ActionHandler( openConfiguration ) );
                        commandService.getCommand( delete.getActionDefinitionId() ).setHandler(
                            new ActionHandler( delete ) );
                        commandService.getCommand( rename.getActionDefinitionId() ).setHandler(
                            new ActionHandler( rename ) );
                        commandService.getCommand( start.getActionDefinitionId() ).setHandler(
                            new ActionHandler( start ) );
                        commandService.getCommand( stop.getActionDefinitionId() )
                            .setHandler( new ActionHandler( stop ) );
                        commandService.getCommand( properties.getActionDefinitionId() ).setHandler(
                            new ActionHandler( properties ) );
                    }
                }
            }


            public void partBroughtToTop( IWorkbenchPartReference partRef )
            {
            }


            public void partClosed( IWorkbenchPartReference partRef )
            {
            }


            public void partHidden( IWorkbenchPartReference partRef )
            {
            }


            public void partInputChanged( IWorkbenchPartReference partRef )
            {
            }


            public void partOpened( IWorkbenchPartReference partRef )
            {
            }


            public void partVisible( IWorkbenchPartReference partRef )
            {
            }
        } );
    }


    /**
     * Enables or disables the actions according to the current selection 
     * in the viewer.
     */
    public void updateActionsStates()
    {
        // Getting the selection
        StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            LdapServer server = ( LdapServer ) selection.getFirstElement();

            switch ( server.getStatus() )
            {
                case STARTED:
                    start.setEnabled( false );
                    stop.setEnabled( true );
                    break;
                case REPAIRING:
                case STARTING:
                    start.setEnabled( false );
                    stop.setEnabled( false );
                    break;
                case STOPPED:
                    start.setEnabled( true );
                    stop.setEnabled( false );
                    break;
                case STOPPING:
                    start.setEnabled( false );
                    stop.setEnabled( false );
                    break;
                case UNKNOWN:
                    start.setEnabled( false );
                    stop.setEnabled( false );
                    break;
            }

            openConfiguration.setEnabled( server.getLdapServerAdapterExtension().isOpenConfigurationActionEnabled() );
            delete.setEnabled( true );
            rename.setEnabled( true );
            properties.setEnabled( true );
        }
        else
        {
            openConfiguration.setEnabled( false );
            delete.setEnabled( false );
            rename.setEnabled( false );
            start.setEnabled( false );
            stop.setEnabled( false );
            properties.setEnabled( false );
        }
    }


    /**
     * Gets the table viewer.
     *
     * @return
     *      the table viewer
     */
    public TreeViewer getViewer()
    {
        return tableViewer;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        LdapServersManager.getDefault().removeListener( ldapServersManagerListener );

        super.dispose();
    }


    /**
     * Refreshes the Servers View asynchronously.
     */
    private void asyncRefresh()
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                tableViewer.refresh();
            }
        } );
    }


    private SelectionListener getColumnSelectionListener( final int column )
    {
        return new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                TreeColumn treeColumn = ( TreeColumn ) e.widget;
                tableViewer.sort( treeColumn, column );
            }
        };
    }
}
