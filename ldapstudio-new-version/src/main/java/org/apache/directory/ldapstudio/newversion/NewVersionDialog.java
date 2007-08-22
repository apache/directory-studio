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

package org.apache.directory.ldapstudio.newversion;


import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Manage Aliases Dialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewVersionDialog extends Dialog
{
    /** the Dialog Settings ID for the New Version Dialog */
    public static final String DIALOG_SETTINGS_ID = NewVersionDialog.class.getName() + ".dialogSettings";

    /** The ID for the "More Information" button */
    private static final int MORE_INFORMATION_ID = 0;
    /** The ID for the "Close" button */
    private static final int CLOSE_ID = 1;

    // UI Fields
    private Button dontShowMessageAgainButton;


    /**
     * Creates a new instance of NewVersionDialog.
     */
    public NewVersionDialog()
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "New Version" );
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        Label imageLabel = new Label( composite, SWT.CENTER | SWT.BORDER );
        imageLabel.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            "resources/graphics/new_version.png" ).createImage() );
        imageLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        dontShowMessageAgainButton = new Button( composite, SWT.CHECK );
        dontShowMessageAgainButton.setText( "Don't open this window again." );
        dontShowMessageAgainButton.setLayoutData( new GridData( SWT.LEFT, SWT.BOTTOM, false, true ) );

        return composite;
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, MORE_INFORMATION_ID, "More Information", true );
        createButton( parent, CLOSE_ID, "Close", false );
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed( int buttonId )
    {
        if ( dontShowMessageAgainButton.getSelection() )
        {
            Activator.getDefault().getDialogSettings().put( DIALOG_SETTINGS_ID, true );
        }

        if ( buttonId == MORE_INFORMATION_ID )
        {
            try
            {
                Activator
                    .getDefault()
                    .getWorkbench()
                    .getBrowserSupport()
                    .getExternalBrowser()
                    .openURL(
                        new URL(
                            "http://directory.apache.org/studio/migration-from-apache-ldap-studio-to-apache-directory-studio.html" ) );
            }
            catch ( PartInitException e )
            {
            }
            catch ( MalformedURLException e )
            {
            }

            close();
        }
        else if ( buttonId == CLOSE_ID )
        {
            close();
        }
    }
}
