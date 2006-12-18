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

package org.apache.directory.ldapstudio.browser.ui.dialogs;


import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.URL;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionActionGroup;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionConfiguration;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionUniversalListener;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionWidget;

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


public class SelectReferralConnectionDialog extends Dialog
{

    private String title;

    private URL referralUrl;

    private IConnection selectedConnection;

    private ConnectionConfiguration configuration;

    private ConnectionUniversalListener universalListener;

    private ConnectionActionGroup actionGroup;

    private ConnectionWidget mainWidget;


    public SelectReferralConnectionDialog( Shell parentShell, URL referralUrl )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.title = "Select Referral Connection";
        this.referralUrl = referralUrl;
        this.selectedConnection = null;
    }


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( title );
    }


    public boolean close()
    {
        if ( this.mainWidget != null )
        {
            this.configuration.dispose();
            this.configuration = null;
            this.actionGroup.deactivateGlobalActionHandlers();
            this.actionGroup.dispose();
            this.actionGroup = null;
            this.universalListener.dispose();
            this.universalListener = null;
            this.mainWidget.dispose();
            this.mainWidget = null;
        }
        return super.close();
    }


    protected void okPressed()
    {
        super.okPressed();
    }


    protected void cancelPressed()
    {
        this.selectedConnection = null;
        super.cancelPressed();
    }


    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridLayout gl = new GridLayout();
        composite.setLayout( gl );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        composite.setLayoutData( gd );

        BaseWidgetUtils.createWrappedLabeledText( composite, "Please select a connection to handle referral "
            + referralUrl, 1 );

        // create configuration
        this.configuration = new ConnectionConfiguration();

        // create main widget
        this.mainWidget = new ConnectionWidget( this.configuration, null );
        this.mainWidget.createWidget( composite );
        this.mainWidget.setInput( BrowserCorePlugin.getDefault().getConnectionManager() );

        // create actions and context menu (and register global actions)
        this.actionGroup = new ConnectionActionGroup( this.mainWidget, this.configuration );
        this.actionGroup.fillToolBar( this.mainWidget.getToolBarManager() );
        this.actionGroup.fillMenu( this.mainWidget.getMenuManager() );
        this.actionGroup.fillContextMenu( this.mainWidget.getContextMenuManager() );
        this.actionGroup.activateGlobalActionHandlers();

        // create the listener
        this.universalListener = new ConnectionUniversalListener( this.mainWidget.getViewer() );

        this.mainWidget.getViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                if ( !event.getSelection().isEmpty() )
                {
                    Object o = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                    if ( o instanceof IConnection )
                    {
                        selectedConnection = ( IConnection ) o;
                    }
                }
            }
        } );

        this.mainWidget.getViewer().addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                if ( !event.getSelection().isEmpty() )
                {
                    Object o = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                    if ( o instanceof IConnection )
                    {
                        selectedConnection = ( IConnection ) o;
                        okPressed();
                    }
                }
            }
        } );

        if ( this.referralUrl != null )
        {
            IConnection[] connections = BrowserCorePlugin.getDefault().getConnectionManager().getConnections();
            for ( int i = 0; i < connections.length; i++ )
            {
                IConnection connection = connections[i];
                URL connectionUrl = connection.getUrl();
                if ( connectionUrl != null && referralUrl.toString().startsWith( connectionUrl.toString() ) )
                {
                    this.mainWidget.getViewer().reveal( connection );
                    this.mainWidget.getViewer().setSelection( new StructuredSelection( connection ), true );
                }
            }
        }

        applyDialogFont( composite );

        this.mainWidget.setFocus();

        return composite;

    }


    public IConnection getReferralConnection()
    {
        return this.selectedConnection;
    }

}
