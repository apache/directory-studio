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

package org.apache.directory.studio.connection.ui.dialogs;


import java.util.List;

import org.apache.directory.api.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.ui.widgets.ConnectionActionGroup;
import org.apache.directory.studio.connection.ui.widgets.ConnectionConfiguration;
import org.apache.directory.studio.connection.ui.widgets.ConnectionUniversalListener;
import org.apache.directory.studio.connection.ui.widgets.ConnectionWidget;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog to select the connection of a referral.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SelectReferralConnectionDialog extends Dialog
{
    /** The dialog title */
    private String title;

    /** The list of available referrals */ 
    private List<String> referralUrls;

    /** The selected connection */
    private Connection selectedConnection;

    /** The connection configuration */
    private ConnectionConfiguration configuration;

    /** The connection listener */
    private ConnectionUniversalListener universalListener;

    /** The connection action group */
    private ConnectionActionGroup actionGroup;

    /** The connection widget */
    private ConnectionWidget mainWidget;


    /**
     * Creates a new instance of SelectReferralConnectionDialog.
     * 
     * @param parentShell the parent shell
     * @param referralUrl the referral URL
     */
    public SelectReferralConnectionDialog( Shell parentShell, List<String> referralUrls )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.title = Messages.getString( "SelectReferralConnectionDialog.SelectReferralConenction" ); //$NON-NLS-1$
        this.referralUrls = referralUrls;
        this.selectedConnection = null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( title );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean close()
    {
        if ( mainWidget != null )
        {
            configuration.dispose();
            configuration = null;
            actionGroup.deactivateGlobalActionHandlers();
            actionGroup.dispose();
            actionGroup = null;
            universalListener.dispose();
            universalListener = null;
            mainWidget.dispose();
            mainWidget = null;
        }
        
        return super.close();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void cancelPressed()
    {
        selectedConnection = null;
        super.cancelPressed();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent )
    {
        Button okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        okButton.setFocus();
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        validate();
    }


    private void validate()
    {
        if ( getButton( IDialogConstants.OK_ID ) != null )
        {
            getButton( IDialogConstants.OK_ID ).setEnabled( selectedConnection != null );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridLayout gridLayout = new GridLayout();
        composite.setLayout( gridLayout );
        GridData gridData = new GridData( GridData.FILL_BOTH );
        gridData.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gridData.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        composite.setLayoutData( gridData );

        BaseWidgetUtils.createWrappedLabeledText( composite, Messages
            .getString( "SelectReferralConnectionDialog.SelectConnectionToHandleReferral" ), 1 ); //$NON-NLS-1$
        
        for ( String url : referralUrls )
        {
            BaseWidgetUtils.createWrappedLabeledText( composite, " - " + url, 1 ); //$NON-NLS-1$
        }

        // create configuration
        configuration = new ConnectionConfiguration();

        // create main widget
        mainWidget = new ConnectionWidget( configuration, null );
        mainWidget.createWidget( composite );
        mainWidget.setInput( ConnectionCorePlugin.getDefault().getConnectionFolderManager() );

        // create actions and context menu (and register global actions)
        actionGroup = new ConnectionActionGroup( mainWidget, configuration );
        actionGroup.fillToolBar( mainWidget.getToolBarManager() );
        actionGroup.fillMenu( mainWidget.getMenuManager() );
        actionGroup.fillContextMenu( mainWidget.getContextMenuManager() );
        actionGroup.activateGlobalActionHandlers();

        // create the listener
        universalListener = new ConnectionUniversalListener( mainWidget.getViewer() );

        mainWidget.getViewer().addSelectionChangedListener( event ->
            {
                selectedConnection = null;
                
                if ( !event.getSelection().isEmpty() )
                {
                    Object object = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                    
                    if ( object instanceof Connection )
                    {
                        selectedConnection = ( Connection ) object;
                    }
                }
                
                validate();
            } );

        mainWidget.getViewer().addDoubleClickListener( event ->
            {
                selectedConnection = null;
                
                if ( !event.getSelection().isEmpty() )
                {
                    Object object = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                    
                    if ( object instanceof Connection )
                    {
                        selectedConnection = ( Connection ) object;
                    }
                }
                
                validate();
            } );

        if ( referralUrls != null )
        {
            Connection[] connections = ConnectionCorePlugin.getDefault().getConnectionManager().getConnections();
            
            for ( Connection connection : connections )
            {
                LdapUrl connectionUrl = connection.getUrl();
                String normalizedConnectionUrl = Utils.getSimpleNormalizedUrl( connectionUrl );
                
                for ( String url : referralUrls )
                {
                    try
                    {
                        if ( ( url != null ) &&
                             Utils.getSimpleNormalizedUrl( new LdapUrl( url ) ).equals( normalizedConnectionUrl ) )
                        {
                            mainWidget.getViewer().reveal( connection );
                            mainWidget.getViewer().setSelection( new StructuredSelection( connection ), true );
                            break;
                        }
                    }
                    catch ( LdapURLEncodingException e )
                    {
                        // Will never occur
                    }
                }
            }
        }

        applyDialogFont( composite );
        validate();

        return composite;
    }


    /**
     * Gets the referral connection.
     * 
     * @return the referral connection
     */
    public Connection getReferralConnection()
    {
        return selectedConnection;
    }
}
