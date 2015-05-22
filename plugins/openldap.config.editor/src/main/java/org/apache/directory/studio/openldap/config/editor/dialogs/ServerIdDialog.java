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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.apache.directory.studio.openldap.config.editor.wrappers.ServerIdWrapper;


/**
 * The ServerIdDialog is used to edit a ServerID, which can be an integer, an hexadecimal number,
 * optionally followed by an URL. The dialog overlay is like :
 * 
 * <pre>
 * +---------------------------------------+
 * |  ServerID Input                       |
 * | .-----------------------------------. |
 * | | ID  : [    ]                      | |
 * | | URL : [                         ] | |
 * | '-----------------------------------' |
 * | .-----------------------------------. |
 * | | ServerId : <////////////////////> | |
 * | '-----------------------------------' |
 * |                                       |
 * |  (cancel)                       (OK)  |
 * +---------------------------------------+
 * 
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerIdDialog extends Dialog
{
    /** The ServerId */
    private ServerIdWrapper serverId;

    /** The new serverId */
    private ServerIdWrapper newServerId;

    /** The list of existing ServerID */
    List<ServerIdWrapper> serverIdList;
    
    // UI widgets
    /** The ID Text */
    private Text idText;
    
    /** The URL text */
    private Text urlText;
    
    /** The resulting serverID Text, or an error message */
    private Text serverIdText;


    /**
     * Create a new instance of the ServerIdDialog
     * 
     * @param parentShell The parent Shell
     * @param serverId The instance containing the ServerID data
     */
    public ServerIdDialog( Shell parentShell, List<ServerIdWrapper> serverIdList, ServerIdWrapper serverId )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.serverId = serverId;
        this.serverIdList = serverIdList;
        
        if ( serverIdList == null )
        {
            this.serverIdList = new ArrayList<ServerIdWrapper>();
        }
    }


    /**
     * Create a new instance of the ServerIdDialog
     * 
     * @param parentShell The parent Shell
     * @param serverIdStr : The string containing the serverID
     */
    public ServerIdDialog( Shell parentShell, String serverIdStr )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.serverId = new ServerIdWrapper( serverIdStr );
    }
    
    
    /**
     * The listener for the ID Text
     */
    private ModifyListener idTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = serverIdText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            
            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            try
            {
                int idValue = Integer.parseInt( idText.getText() );

                // The value must be between 0 and 4095, and it must not already exists
                if ( ( idValue < 0 ) || ( idValue > 4096 ) )
                {
                    System.out.println( "Wrong ID : it must be a value in [0..4095]" );
                    serverIdText.setText( "Wrong ID : it must be a value in [0..4095]" );
                    serverIdText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    okButton.setEnabled( false );
                    return;
                }
                else
                {
                    // Be sure the value is not already taken
                    for ( ServerIdWrapper serverIdWrapper : serverIdList )
                    {
                        if ( serverIdWrapper.getServerId() == idValue )
                        {
                            System.out.println( "Wrong ServerID : already taken" );
                            serverIdText.setText( "Wrong ID : it's already taken by another Server ID" );
                            serverIdText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                            okButton.setEnabled( false );
                            return;
                        }
                    }
                }
                
                serverIdText.setText( idText.getText() + ' ' + urlText.getText() );
                serverIdText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                okButton.setEnabled( true );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                System.out.println( "Wrong ServerID : it must be an integer" );
                serverIdText.setText( "Wrong ID : it must be an integer in [0..4095]" );
                serverIdText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                okButton.setEnabled( false );
            }
        }
    };
    
    
    /**
     * The listener for the URL Text
     */
    private ModifyListener urlTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = serverIdText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            
            
            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            try
            {
                new LdapUrl( urlText.getText() );
                
                serverIdText.setText( idText.getText() + ' ' + urlText.getText() );
                serverIdText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                okButton.setEnabled( true );
            }
            catch ( LdapURLEncodingException luee )
            {
                System.out.println( "Wrong ServerID : the URL is invalid" );
                serverIdText.setText( "Wrong ServerID : the URL is invalid" );
                serverIdText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                okButton.setEnabled( false );
            }

        }
    };
            

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "ServerId" );
    }


    /**
     * We have to check that the ID does not already exist.
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        // Creating the new index
        String id = idText.getText();
        String url = urlText.getText();
        int idValue = Integer.valueOf( id );
        
        newServerId = new ServerIdWrapper( idValue , url );
        super.okPressed();
    }


    /**
     * Create the Dialog for ServerID :
     * <pre>
     * +---------------------------------------+
     * |  ServerID                             |
     * | .-----------------------------------. |
     * | | ID  : [    ]                      | |
     * | | URL : [                         ] | |
     * | '-----------------------------------' |
     * | .-----------------------------------. |
     * | | ServerId : <////////////////////> | |
     * | '-----------------------------------' |
     * |                                       |
     * |  (cancel)                       (OK)  |
     * +---------------------------------------+
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        createServerIdEditGroup( composite );
        createServerIdShowGroup( composite );

        initFromServerId();

        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the ServerID input group. This is the part of the dialog
     * where one can insert the ServerID values:
     * 
     * <pre>
     * ServerID Input
     * .-----------------------------------.
     * | ID  : [    ]                      |
     * | URL : [                         ] |
     * '-----------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createServerIdEditGroup( Composite parent )
    {
        // ServerID Group
        Group serverIdGroup = BaseWidgetUtils.createGroup( parent, "ServerID input", 1 );
        GridLayout serverIdGroupGridLayout = new GridLayout( 2, false );
        serverIdGroup.setLayout( serverIdGroupGridLayout );
        serverIdGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // ServerID Text
        BaseWidgetUtils.createLabel( serverIdGroup, "ID:", 1 );
        idText = BaseWidgetUtils.createText( serverIdGroup, "", 1 );
        idText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        idText.addModifyListener( idTextListener );

        // URL Text
        BaseWidgetUtils.createLabel( serverIdGroup, "URL:", 1 );
        urlText = BaseWidgetUtils.createText( serverIdGroup, "", 1 );
        urlText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        urlText.addModifyListener( urlTextListener );
    }


    /**
     * Creates the ServerID show group. This is the part of the dialog
     * where the real ServerID is shown, or an error message if the ServerID
     * is invalid.
     * 
     * <pre>
     * .-----------------------------------.
     * | ServerID  : <///////////////////> |
     * '-----------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createServerIdShowGroup( Composite parent )
    {
        // ServerId Group
        Group serverIdGroup = BaseWidgetUtils.createGroup( parent, "", 1 );
        GridLayout serverIdGroupGridLayout = new GridLayout( 2, false );
        serverIdGroup.setLayout( serverIdGroupGridLayout );
        serverIdGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // ServerID Text
        serverIdText = BaseWidgetUtils.createText( serverIdGroup, "", 1 );
        serverIdText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Initializes the UI from the ServerId
     */
    private void initFromServerId()
    {
        if ( serverId != null )
        {
            idText.setText( Integer.toString( serverId.getServerId() ) );
            
            String url = serverId.getUrl();
            
            if ( url == null )
            {
                urlText.setText( "" );
            }
            else
            {
                urlText.setText( serverId.getUrl() );
            }
        }
    }


    /**
     * Gets the new ServerId.
     *
     * @return the new serverID
     */
    public ServerIdWrapper getNewServerId()
    {
        return newServerId;
    }
}
