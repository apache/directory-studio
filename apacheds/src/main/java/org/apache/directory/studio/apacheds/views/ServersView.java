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
package org.apache.directory.studio.apacheds.views;


import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.actions.DeleteAction;
import org.apache.directory.studio.apacheds.actions.NewServerInstanceAction;
import org.apache.directory.studio.apacheds.actions.OpenAction;
import org.apache.directory.studio.apacheds.actions.PropertiesAction;
import org.apache.directory.studio.apacheds.actions.RenameAction;
import org.apache.directory.studio.apacheds.actions.ServerInstanceRunAction;
import org.apache.directory.studio.apacheds.actions.ServerInstanceStopAction;
import org.apache.directory.studio.apacheds.model.ServerInstance;
import org.apache.directory.studio.apacheds.model.ServersHandler;
import org.apache.directory.studio.apacheds.model.ServersHandlerListener;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
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
 * @version $Rev$, $Date$
 */
public class ServersView extends ViewPart
{
    /** The ID of the view */
    public static final String ID = "org.apache.directory.studio.apacheds.serversView";

    /** The tree*/
    private Tree tree;

    /** The table viewer */
    private ServersTableViewer tableViewer;

    /** The view instance */
    private ServersView instance;

    /** Token used to activate and deactivate shortcuts in the view */
    private IContextActivation contextActivation;

    private static final String TAG_COLUMN_WIDTH = "columnWidth";
    protected int[] columnWidths;

    // Actions
    private NewServerInstanceAction newServer;
    private ServerInstanceRunAction run;
    private ServerInstanceStopAction stop;
    private PropertiesAction properties;
    private DeleteAction delete;
    private OpenAction open;
    private RenameAction rename;

    // Listeners
    private ServersHandlerListener serversHandlerListener = new ServersHandlerListener()
    {
        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.model.ServersHandlerListener#serverInstanceAdded(org.apache.directory.studio.apacheds.model.ServerInstance)
         */
        public void serverInstanceAdded( ServerInstance serverInstance )
        {
            tableViewer.refresh();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.model.ServersHandlerListener#serverInstanceRemoved(org.apache.directory.studio.apacheds.model.ServerInstance)
         */
        public void serverInstanceRemoved( ServerInstance serverInstance )
        {
            tableViewer.refresh();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.model.ServersHandlerListener#serverInstanceUpdated(org.apache.directory.studio.apacheds.model.ServerInstance)
         */
        public void serverInstanceUpdated( ServerInstance serverInstance )
        {
            tableViewer.refresh();
        }
    };


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
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
        serverColumn.setText( "Server" );
        serverColumn.setWidth( columnWidths[0] );
        serverColumn.addSelectionListener( getHeaderListener( 0 ) );
        tree.setSortColumn( serverColumn );
        tree.setSortDirection( SWT.UP );

        TreeColumn stateColumn = new TreeColumn( tree, SWT.SINGLE );
        stateColumn.setText( "State" );
        stateColumn.setWidth( columnWidths[1] );
        stateColumn.addSelectionListener( getHeaderListener( 1 ) );

        // Creating the viewer
        tableViewer = new ServersTableViewer( tree );

        initActions();
        initToolbar();
        initContextMenu();
        initListeners();
    }


    /**
     * Gets a header listener for the given column.
     * 
     * @param col
     *      the column
     * @return
     *      a header listener for the given column
     */
    private SelectionListener getHeaderListener( final int col )
    {
        return new SelectionAdapter()
        {
            /**
             * Handles the case of user selecting the header area.
             */
            public void widgetSelected( SelectionEvent e )
            {
                if ( tableViewer == null )
                    return;
                TreeColumn column = ( TreeColumn ) e.widget;
                tableViewer.resortTable( column, col );
            }
        };
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
     */
    public void init( IViewSite site, IMemento memento ) throws PartInitException
    {
        super.init( site, memento );
        columnWidths = new int[]
            { 200, 60 };
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
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
        newServer = new NewServerInstanceAction();

        run = new ServerInstanceRunAction( this );
        run.setEnabled( false );

        stop = new ServerInstanceStopAction( this );
        stop.setEnabled( false );

        properties = new PropertiesAction( this );
        properties.setEnabled( false );

        open = new OpenAction( this );
        open.setEnabled( false );

        delete = new DeleteAction( this );
        delete.setEnabled( false );

        rename = new RenameAction( this );
        rename.setEnabled( false );
    }


    /**
     * Initializes the toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add( newServer );
        toolbar.add( new Separator() );
        toolbar.add( run );
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
                MenuManager newManager = new MenuManager( "&New" );
                newManager.add( newServer );
                manager.add( newManager );
                manager.add( open );
                manager.add( new Separator() );
                manager.add( delete );
                manager.add( rename );
                manager.add( new Separator() );
                manager.add( run );
                manager.add( stop );
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
        ServersHandler serversHandler = ServersHandler.getDefault();
        serversHandler.addListener( serversHandlerListener );

        tableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                open.run();
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
                        commandService.getCommand( open.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( delete.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( rename.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( run.getActionDefinitionId() ).setHandler( null );
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
                    contextActivation = contextService.activateContext( ApacheDsPluginConstants.CONTEXTS_SERVERS_VIEW );

                    ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
                        ICommandService.class );
                    if ( commandService != null )
                    {
                        commandService.getCommand( newServer.getActionDefinitionId() ).setHandler(
                            new ActionHandler( newServer ) );
                        commandService.getCommand( open.getActionDefinitionId() )
                            .setHandler( new ActionHandler( open ) );
                        commandService.getCommand( delete.getActionDefinitionId() ).setHandler(
                            new ActionHandler( delete ) );
                        commandService.getCommand( rename.getActionDefinitionId() ).setHandler(
                            new ActionHandler( rename ) );
                        commandService.getCommand( run.getActionDefinitionId() ).setHandler( new ActionHandler( run ) );
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
            ServerInstance serverInstance = ( ServerInstance ) selection.getFirstElement();

            switch ( serverInstance.getState() )
            {
                case STARTED:
                    run.setEnabled( false );
                    stop.setEnabled( true );
                    break;
                case STARTING:
                    run.setEnabled( false );
                    stop.setEnabled( false );
                    break;
                case STOPPED:
                    run.setEnabled( true );
                    stop.setEnabled( false );
                    break;
                case STOPPING:
                    run.setEnabled( false );
                    stop.setEnabled( false );
                    break;
                case UNKNONW:
                    run.setEnabled( false );
                    stop.setEnabled( false );
                    break;
            }

            open.setEnabled( true );
            delete.setEnabled( true );
            properties.setEnabled( true );
            rename.setEnabled( true );
        }
        else
        {
            run.setEnabled( false );
            stop.setEnabled( false );
            open.setEnabled( false );
            delete.setEnabled( false );
            properties.setEnabled( false );
            rename.setEnabled( false );
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    public void dispose()
    {
        ServersHandler serversHandler = ServersHandler.getDefault();
        serversHandler.removeListener( serversHandlerListener );

        super.dispose();
    }
}
