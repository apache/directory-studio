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

package org.apache.directory.ldapstudio.view;


import java.net.MalformedURLException;
import java.net.URL;

import org.apache.directory.ldapstudio.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This class defines the Welcome View.
 */
public class WelcomeView extends ViewPart
{
    public static final String ID = "org.apache.directory.ldapstudio.view.WelcomeView"; //$NON-NLS-1$


    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl( Composite parent )
    {
        Composite container = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout( 2, true );
        container.setLayout( layout );

        // Welcome Label
        Label welcomeLabel = new Label( container, SWT.CENTER );
        welcomeLabel.setFont( new Font( null, "Georgia", 13, SWT.BOLD ) ); //$NON-NLS-1$
        welcomeLabel.setText( Messages.getString( "WelcomeView.Welcome_message" ) ); //$NON-NLS-1$
        welcomeLabel.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true, 2, 1 ) );

        // LDAP Browser Plugin Image & Button
        Image ldapBrowserImage = new Image( PlatformUI.getWorkbench().getDisplay(), getClass().getResourceAsStream(
            "ldapstudio_ldap-browser-plugin.png" ) ); //$NON-NLS-1$
        Button ldapBrowserButton = new Button( container, SWT.PUSH );
        ldapBrowserButton.setImage( ldapBrowserImage );
        ldapBrowserButton.setLayoutData( new GridData( SWT.CENTER, SWT.NONE, false, false ) );
        ldapBrowserButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
                    PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(
                        "org.apache.directory.ldapstudio.browser.perspective" ) ); //$NON-NLS-1$
            }
        } );

        // Schemas Editor Plugin Image & Button
        Image schemasEditorImage = new Image( PlatformUI.getWorkbench().getDisplay(), getClass().getResourceAsStream(
            "ldapstudio_schemas-editor-plugin.png" ) ); //$NON-NLS-1$
        Button schemasEditorButton = new Button( container, SWT.PUSH );
        schemasEditorButton.setImage( schemasEditorImage );
        schemasEditorButton.setLayoutData( new GridData( SWT.CENTER, SWT.NONE, false, false ) );
        schemasEditorButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
                    PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(
                        "org.apache.directory.ldapstudio.schemas.perspective" ) ); //$NON-NLS-1$
            }
        } );

        // LDAP Browser Plugin Label
        Label ldapBrowserLabel = new Label( container, SWT.NONE );
        ldapBrowserLabel.setFont( new Font( null, "Georgia", 13, SWT.BOLD ) ); //$NON-NLS-1$
        ldapBrowserLabel.setText( Messages.getString("WelcomeView.LDAP_Browser_Plugin") ); //$NON-NLS-1$
        ldapBrowserLabel.setLayoutData( new GridData( SWT.CENTER, SWT.NONE, true, true ) );

        // Schemas Editor Plugin Label
        Label schemasEditorLabel = new Label( container, SWT.NONE );
        schemasEditorLabel.setFont( new Font( null, "Georgia", 13, SWT.BOLD ) ); //$NON-NLS-1$
        schemasEditorLabel.setText( Messages.getString("WelcomeView.Schemas_Editor_Plugin") ); //$NON-NLS-1$
        schemasEditorLabel.setLayoutData( new GridData( SWT.CENTER, SWT.NONE, true, true ) );

        // Apache Software Foundation Image
        Image asfLogoImange = new Image( PlatformUI.getWorkbench().getDisplay(), getClass().getResourceAsStream(
            "asf-logo.gif" ) ); //$NON-NLS-1$
        Button asfLogoButton = new Button( container, SWT.PUSH );
        asfLogoButton.setImage( asfLogoImange );
        asfLogoButton.setLayoutData( new GridData( SWT.CENTER, SWT.NONE, false, false, 2, 1 ) );
        asfLogoButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                try
                {
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(
                        new URL( Messages.getString("WelcomeView.Apache.org_url") ) ); //$NON-NLS-1$
                }
                catch ( PartInitException e1 )
                {
                    // Displaying an error
                    MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        Messages.getString("WelcomeView.Error"), Messages.getString("WelcomeView.LDAP_Studio_was_unable_to_open_www.apache.org") ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                catch ( MalformedURLException e1 )
                {
                    // Will never be thrown
                }
            }
        } );

        // Copyright Label
        Label copyrightLabel = new Label( container, SWT.NONE );
        copyrightLabel.setText( Messages.getString("WelcomeView.Copyright") ); //$NON-NLS-1$
        copyrightLabel.setLayoutData( new GridData( SWT.CENTER, SWT.NONE, false, false, 2, 1 ) );
    }


    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        // Does nothing
    }
}