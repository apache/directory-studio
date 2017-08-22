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


import org.apache.directory.api.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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
public class ServerIdDialog extends AddEditDialog<ServerIdWrapper>
{
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
     */
    public ServerIdDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }
    
    
    /**
     * The listener for the ID Text
     */
    private ModifyListener idTextListener = event ->
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
                serverIdText.setText( getEditedElement().toString() );

                // The value must be between 0 and 4095, and it must not already exists
                if ( ( idValue < 0 ) || ( idValue > 4096 ) )
                {
                    serverIdText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    okButton.setEnabled( false );
                    
                    return;
                }
                else
                {
                    // Be sure the value is not already taken
                    for ( ServerIdWrapper serverIdWrapper : getElements() )
                    {
                        if ( serverIdWrapper.getServerId() == idValue )
                        {
                            serverIdText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                            okButton.setEnabled( false );
                            
                            return;
                        }
                    }
                }
                
                serverIdText.setText( idText.getText() + ' ' + urlText.getText() );
                serverIdText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getEditedElement().setServerId( idValue );
                okButton.setEnabled( true );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                serverIdText.setText( getEditedElement().toString() );
                serverIdText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                okButton.setEnabled( false );
            }
        };
    
    
    /**
     * The listener for the URL Text
     */
    private ModifyListener urlTextListener = event ->
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
                String urlStr = urlText.getText();
                new LdapUrl( urlStr );

                getEditedElement().setUrl( urlStr );
                serverIdText.setText( idText.getText() + ' ' + urlStr );
                serverIdText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                okButton.setEnabled( true );
            }
            catch ( LdapURLEncodingException luee )
            {
                serverIdText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                okButton.setEnabled( false );
            }
        };
            

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "ServerId" );
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
    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        createServerIdEditGroup( composite );
        createServerIdShowGroup( composite );

        initDialog();
        addListeners();

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

        // URL Text
        BaseWidgetUtils.createLabel( serverIdGroup, "URL:", 1 );
        urlText = BaseWidgetUtils.createText( serverIdGroup, "", 1 );
        urlText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
    protected void initDialog()
    {
        ServerIdWrapper editedElement = getEditedElement();
        
        if ( editedElement != null )
        {
            idText.setText( Integer.toString( editedElement.getServerId() ) );
            
            String url = editedElement.getUrl();
            
            if ( url == null )
            {
                urlText.setText( "" );
            }
            else
            {
                urlText.setText( editedElement.getUrl() );
            }
        }
    }


    /**
     * Add a new Element that will be edited
     */
    public void addNewElement()
    {
        setEditedElement( new ServerIdWrapper( "" ) );
    }


    public void addNewElement( ServerIdWrapper editedElement )
    {
        ServerIdWrapper newElement = editedElement.clone();
        setEditedElement( newElement );
        
    }

    
    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        idText.addModifyListener( idTextListener );
        urlText.addModifyListener( urlTextListener );
    }
}
