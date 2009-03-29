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

package org.apache.directory.studio.ldapbrowser.ui.editors.schemabrowser;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.BrowserConnectionWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


/**
 * A contribution item that adds a BrowserConnectionWidget with connections to the toolbar.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserConnectionWidgetContributionItem extends ContributionItem implements ConnectionUpdateListener
{
    /** The schema page */
    private SchemaPage schemaPage;

    /** The tool item */
    private ToolItem toolitem;

    /** The tool item composite */
    private Composite toolItemComposite;
    private BrowserConnectionWidget browserConnectionWidget;


    /**
     * Creates a new instance of ConnectionContributionItem.
     *
     * @param schemaPage the schema page
     */
    public BrowserConnectionWidgetContributionItem( SchemaPage schemaPage )
    {
        this.schemaPage = schemaPage;
    }


    /**
     * Creates and returns the control for this contribution item
     * under the given parent composite.
     *
     * @param parent the parent composite
     * @return the new control
     */
    private Control createControl( Composite parent )
    {
        // Creating the ToolItem Composite
        toolItemComposite = new Composite( parent, SWT.NONE );
        GridLayout gridLayout = new GridLayout( 2, false );
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        toolItemComposite.setLayout( gridLayout );
        toolItemComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Creating the Browser Connection Widget
        browserConnectionWidget = new BrowserConnectionWidget();
        browserConnectionWidget.createWidget( toolItemComposite );
        browserConnectionWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                schemaPage.getSchemaBrowser().setInput( new SchemaBrowserInput( getConnection(), null ) );
            }
        } );

        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionUIPlugin.getDefault().getEventRunner() );

        // Initializing the width for the toolbar item
        toolitem.setWidth( 250 );

        return toolItemComposite;
    }


    /**
     * @see org.eclipse.jface.action.ContributionItem#dispose()
     */
    public void dispose()
    {
        ConnectionEventRegistry.removeConnectionUpdateListener( this );
        toolItemComposite.dispose();
        toolItemComposite = null;
        browserConnectionWidget = null;
    }


    /**
     * The control item implementation of this <code>IContributionItem</code>
     * method calls the <code>createControl</code> method.
     *
     * @param parent the parent of the control to fill
     */
    public final void fill( Composite parent )
    {
        createControl( parent );
    }


    /**
     * The control item implementation of this <code>IContributionItem</code>
     * method throws an exception since controls cannot be added to menus.
     *
     * @param parent the menu
     * @param index menu index
     */
    public final void fill( Menu parent, int index )
    {
        Assert.isTrue( false, Messages.getString( "BrowserConnectionWidgetContributionItem.CantAddControl" ) );//$NON-NLS-1$
    }


    /**
     * The control item implementation of this <code>IContributionItem</code>
     * method calls the <code>createControl</code>  method to
     * create a control under the given parent, and then creates
     * a new tool item to hold it.
     *
     * @param parent the ToolBar to add the new control to
     * @param index the index
     */
    public void fill( ToolBar parent, int index )
    {
        toolitem = new ToolItem( parent, SWT.SEPARATOR, index );
        Control control = createControl( parent );
        toolitem.setControl( control );
    }


    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public IBrowserConnection getConnection()
    {
        return browserConnectionWidget.getBrowserConnection();
    }


    /**
     * Sets the connection.
     *
     * @param connection the connection
     */
    public void setConnection( IBrowserConnection connection )
    {
        browserConnectionWidget.setBrowserConnection( connection );
    }


    /**
     * Updates the enabled state.
     */
    public void updateEnabledState()
    {
        browserConnectionWidget.setEnabled( !schemaPage.isShowDefaultSchema() );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionUpdated(org.apache.directory.studio.connection.core.Connection)
     */
    public final void connectionUpdated( Connection connection )
    {
        IBrowserConnection selectedConnection = browserConnectionWidget.getBrowserConnection();
        if ( connection.equals( selectedConnection.getConnection() ) )
        {
            browserConnectionWidget.setBrowserConnection( browserConnectionWidget.getBrowserConnection() );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionAdded(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionAdded( Connection connection )
    {
        // Nothing to do
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionRemoved(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionRemoved( Connection connection )
    {
        IBrowserConnection selectedConnection = browserConnectionWidget.getBrowserConnection();
        if ( connection.equals( selectedConnection.getConnection() ) )
        {
            schemaPage.getSchemaBrowser().setInput( new SchemaBrowserInput( null, null ) );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionOpened(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionOpened( Connection connection )
    {
        // Nothing to do
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionClosed(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionClosed( Connection connection )
    {
        // Nothing to do
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderModified(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
        // Nothing to do
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderAdded(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderAdded( ConnectionFolder connectionFolder )
    {
        // Nothing to do
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderRemoved(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderRemoved( ConnectionFolder connectionFolder )
    {
        // Nothing to do
    }

}