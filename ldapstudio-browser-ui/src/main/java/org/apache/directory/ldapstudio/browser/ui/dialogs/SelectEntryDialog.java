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


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserActionGroup;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserConfiguration;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserUniversalListener;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserWidget;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class SelectEntryDialog extends Dialog
{

    private String title;

    private Image image;

    private IConnection connection;

    private IEntry initialEntry;

    private IEntry selectedEntry;

    private BrowserConfiguration configuration;

    private BrowserUniversalListener universalListener;

    private BrowserActionGroup actionGroup;

    private BrowserWidget mainWidget;


    public SelectEntryDialog( Shell parentShell, String title, IConnection connection, IEntry initialEntry )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.title = title;
        this.connection = connection;
        this.initialEntry = initialEntry;
        this.selectedEntry = null;
    }


    public SelectEntryDialog( Shell parentShell, String title, Image image, IConnection connection, IEntry initialEntry )
    {
        this( parentShell, title, connection, initialEntry );
        this.image = image;
    }


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( title );
        shell.setImage( image );
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
        this.selectedEntry = initialEntry;
        super.okPressed();
    }


    protected void cancelPressed()
    {
        this.selectedEntry = null;
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
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        // create configuration
        this.configuration = new BrowserConfiguration();

        // create main widget
        this.mainWidget = new BrowserWidget( this.configuration, null );
        this.mainWidget.createWidget( composite );
        this.mainWidget.setInput( new IEntry[]
            { this.connection.getRootDSE() } );

        // create actions and context menu (and register global actions)
        this.actionGroup = new BrowserActionGroup( this.mainWidget, this.configuration );
        this.actionGroup.fillToolBar( this.mainWidget.getToolBarManager() );
        this.actionGroup.fillMenu( this.mainWidget.getMenuManager() );
        this.actionGroup.fillContextMenu( this.mainWidget.getContextMenuManager() );
        this.actionGroup.activateGlobalActionHandlers();

        // create the listener
        this.universalListener = new BrowserUniversalListener( this.mainWidget.getViewer() );

        this.mainWidget.getViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                if ( !event.getSelection().isEmpty() )
                {
                    Object o = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                    if ( o instanceof IEntry )
                    {
                        initialEntry = ( IEntry ) o;
                    }
                }
            }
        } );

        this.mainWidget.getViewer().expandToLevel( 2 );
        if ( this.initialEntry != null )
        {
            IEntry entry = this.initialEntry;
            this.mainWidget.getViewer().reveal( entry );
            this.mainWidget.getViewer().refresh( entry, true );
            this.mainWidget.getViewer().setSelection( new StructuredSelection( entry ), true );
            this.mainWidget.getViewer().setSelection( new StructuredSelection( entry ), true );
        }

        applyDialogFont( composite );

        this.mainWidget.setFocus();

        return composite;

    }


    public IEntry getSelectedEntry()
    {
        return this.selectedEntry;
    }

}
