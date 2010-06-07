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

package org.apache.directory.studio.ldapbrowser.common.dialogs;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.ui.widgets.ConnectionActionGroup;
import org.apache.directory.studio.connection.ui.widgets.ConnectionConfiguration;
import org.apache.directory.studio.connection.ui.widgets.ConnectionUniversalListener;
import org.apache.directory.studio.connection.ui.widgets.ConnectionWidget;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog to select an {@link IBrowserConnection}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SelectBrowserConnectionDialog extends Dialog
{

    /** The title. */
    private String title;

    /** The initial browser connection. */
    private IBrowserConnection initialBrowserConnection;

    /** The selected browser connection. */
    private IBrowserConnection selectedBrowserConnection;

    /** The connection configuration. */
    private ConnectionConfiguration connectionConfiguration;

    /** The connection universal listener. */
    private ConnectionUniversalListener connectionUniversalListener;

    /** The connection action group. */
    private ConnectionActionGroup connectionActionGroup;

    /** The connection main widget. */
    private ConnectionWidget connectionMainWidget;


    /**
     * Creates a new instance of SelectConnectionDialog.
     * 
     * @param parentShell the parent shell
     * @param title the title
     * @param initialBrowserConnection the initial browser connection
     */
    public SelectBrowserConnectionDialog( Shell parentShell, String title, IBrowserConnection initialBrowserConnection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.title = title;
        this.initialBrowserConnection = initialBrowserConnection;
        this.selectedBrowserConnection = null;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( title );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    public boolean close()
    {
        if ( connectionMainWidget != null )
        {
            connectionConfiguration.dispose();
            connectionConfiguration = null;
            connectionActionGroup.deactivateGlobalActionHandlers();
            connectionActionGroup.dispose();
            connectionActionGroup = null;
            connectionUniversalListener.dispose();
            connectionUniversalListener = null;
            connectionMainWidget.dispose();
            connectionMainWidget = null;
        }
        return super.close();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        selectedBrowserConnection = initialBrowserConnection;
        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     */
    protected void cancelPressed()
    {
        selectedBrowserConnection = null;
        super.cancelPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridLayout gl = new GridLayout();
        composite.setLayout( gl );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        composite.setLayoutData( gd );

        // create configuration
        connectionConfiguration = new ConnectionConfiguration();

        // create main widget
        connectionMainWidget = new ConnectionWidget( connectionConfiguration, null );
        connectionMainWidget.createWidget( composite );
        connectionMainWidget.setInput( ConnectionCorePlugin.getDefault().getConnectionFolderManager() );

        // create actions and context menu (and register global actions)
        connectionActionGroup = new ConnectionActionGroup( connectionMainWidget, connectionConfiguration );
        connectionActionGroup.fillToolBar( connectionMainWidget.getToolBarManager() );
        connectionActionGroup.fillMenu( connectionMainWidget.getMenuManager() );
        connectionActionGroup.fillContextMenu( connectionMainWidget.getContextMenuManager() );
        connectionActionGroup.activateGlobalActionHandlers();

        // create the listener
        connectionUniversalListener = new ConnectionUniversalListener( connectionMainWidget.getViewer() );

        connectionMainWidget.getViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                if ( !event.getSelection().isEmpty() )
                {
                    Object o = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                    if ( o instanceof Connection )
                    {
                        Connection connection = ( Connection ) o;
                        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                            .getBrowserConnection( connection );
                        initialBrowserConnection = browserConnection;

                    }
                }
            }
        } );

        connectionMainWidget.getViewer().addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                if ( !event.getSelection().isEmpty() )
                {
                    Object o = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                    if ( o instanceof Connection )
                    {
                        Connection connection = ( Connection ) o;
                        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                            .getBrowserConnection( connection );
                        initialBrowserConnection = browserConnection;
                        okPressed();
                    }
                }
            }
        } );

        if ( initialBrowserConnection != null )
        {
            Connection connection = initialBrowserConnection.getConnection();
            if ( connection != null )
            {
                connectionMainWidget.getViewer().reveal( connection );
                connectionMainWidget.getViewer().setSelection( new StructuredSelection( connection ), true );
            }
        }

        applyDialogFont( composite );

        connectionMainWidget.setFocus();

        return composite;

    }


    /**
     * Gets the selected browser connection or null if the dialog was canceled.
     * 
     * @return the selected browser connection or null if the dialog was canceled
     */
    public IBrowserConnection getSelectedBrowserConnection()
    {
        return selectedBrowserConnection;
    }

}
