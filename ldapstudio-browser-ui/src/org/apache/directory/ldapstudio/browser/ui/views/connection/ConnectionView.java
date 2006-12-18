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


public class ConnectionView extends ViewPart
{

    private ConnectionConfiguration configuration;

    private ConnectionViewActionGroup actionGroup;

    private ConnectionWidget mainWidget;

    private ConnectionViewUniversalListener universalListener;


    public static String getId()
    {
        return ConnectionView.class.getName();
    }


    public ConnectionView()
    {
        super();
    }


    public void setFocus()
    {
        mainWidget.getViewer().getControl().setFocus();
    }


    public void dispose()
    {
        if ( this.configuration != null )
        {
            this.actionGroup.dispose();
            this.actionGroup = null;
            this.configuration.dispose();
            this.configuration = null;
            this.universalListener.dispose();
            this.universalListener = null;
            this.mainWidget.dispose();
            this.mainWidget = null;
            getSite().setSelectionProvider( null );
        }

        super.dispose();
    }


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
        this.configuration = new ConnectionConfiguration();

        // create main widget
        this.mainWidget = new ConnectionWidget( this.configuration, getViewSite().getActionBars() );
        this.mainWidget.createWidget( composite );
        this.mainWidget.setInput( BrowserCorePlugin.getDefault().getConnectionManager() );

        // create actions and context menu (and register global actions)
        this.actionGroup = new ConnectionViewActionGroup( this );
        this.actionGroup.fillToolBar( this.mainWidget.getToolBarManager() );
        this.actionGroup.fillMenu( this.mainWidget.getMenuManager() );
        this.actionGroup.enableGlobalActionHandlers( getViewSite().getActionBars() );
        this.actionGroup.fillContextMenu( this.configuration.getContextMenuManager( this.mainWidget.getViewer() ) );

        // create the listener
        getSite().setSelectionProvider( this.mainWidget.getViewer() );
        this.universalListener = new ConnectionViewUniversalListener( this );

        // default selection
        IConnection[] connections = BrowserCorePlugin.getDefault().getConnectionManager().getConnections();
        if ( connections.length > 0 )
        {
            ISelection selection = new StructuredSelection( connections[0] );
            this.mainWidget.getViewer().setSelection( selection );
            this.universalListener.selectionChanged( this, selection );
        }

    }


    public ConnectionViewActionGroup getActionGroup()
    {
        return actionGroup;
    }


    public ConnectionConfiguration getConfiguration()
    {
        return configuration;
    }


    public ConnectionWidget getMainWidget()
    {
        return mainWidget;
    }


    public ConnectionViewUniversalListener getUniversalListener()
    {
        return universalListener;
    }

}
