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

package org.apache.directory.ldapstudio.browser.ui.views.connection;


import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionConfiguration;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionWidget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the connection view. It displays all connections.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionView extends ViewPart
{

    /** The configuration. */
    private ConnectionConfiguration configuration;

    /** The actions */
    private ConnectionViewActionGroup actionGroup;

    /** The main widget */
    private ConnectionWidget mainWidget;

    /** The listeners */
    private ConnectionViewUniversalListener universalListener;


    /**
     * Returns the connection view ID.
     * 
     * @return the connection view ID.
     */
    public static String getId()
    {
        return ConnectionView.class.getName();
    }


    /**
     * Creates a new instance of ConnectionView.
     */
    public ConnectionView()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation sets focus to the viewer's control.
     */
    public void setFocus()
    {
        mainWidget.getViewer().getControl().setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( configuration != null )
        {
            actionGroup.dispose();
            actionGroup = null;
            configuration.dispose();
            configuration = null;
            universalListener.dispose();
            universalListener = null;
            mainWidget.dispose();
            mainWidget = null;
            getSite().setSelectionProvider( null );
        }

        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout( layout );

        PlatformUI.getWorkbench().getHelpSystem().setHelp( composite,
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_connections_view" );

        // create configuration
        configuration = new ConnectionConfiguration();

        // create main widget
        mainWidget = new ConnectionWidget( configuration, getViewSite().getActionBars() );
        mainWidget.createWidget( composite );
        mainWidget.setInput( BrowserCorePlugin.getDefault().getConnectionManager() );

        // create actions and context menu (and register global actions)
        actionGroup = new ConnectionViewActionGroup( this );
        actionGroup.fillToolBar( mainWidget.getToolBarManager() );
        actionGroup.fillMenu( mainWidget.getMenuManager() );
        actionGroup.enableGlobalActionHandlers( getViewSite().getActionBars() );
        actionGroup.fillContextMenu( configuration.getContextMenuManager( mainWidget.getViewer() ) );

        // create the listener
        getSite().setSelectionProvider( mainWidget.getViewer() );
        universalListener = new ConnectionViewUniversalListener( this );

        // default selection
        IConnection[] connections = BrowserCorePlugin.getDefault().getConnectionManager().getConnections();
        if ( connections.length > 0 )
        {
            ISelection selection = new StructuredSelection( connections[0] );
            mainWidget.getViewer().setSelection( selection );
            //this.universalListener.selectionChanged( this, selection );
        }

    }


    /**
     * Selects the given object in the tree. The object
     * must be an IConnection.
     * 
     * @param obj the object to select
     */
    public void select( Object obj )
    {
        if ( obj instanceof IConnection )
        {
            IConnection connection = ( IConnection ) obj;

            mainWidget.getViewer().reveal( connection );
            mainWidget.getViewer().refresh( connection, true );
            mainWidget.getViewer().setSelection( new StructuredSelection( connection ), true );
        }
    }


    /**
     * Gets the action group.
     * 
     * @return the action group
     */
    public ConnectionViewActionGroup getActionGroup()
    {
        return actionGroup;
    }


    /**
     * Gets the configuration.
     * 
     * @return the configuration
     */
    public ConnectionConfiguration getConfiguration()
    {
        return configuration;
    }


    /**
     * Gets the main widget.
     * 
     * @return the main widget
     */
    public ConnectionWidget getMainWidget()
    {
        return mainWidget;
    }


    /**
     * Gets the universal listener.
     * 
     * @return the universal listener
     */
    public ConnectionViewUniversalListener getUniversalListener()
    {
        return universalListener;
    }

}
