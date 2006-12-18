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

package org.apache.directory.ldapstudio.browser.ui.dialogs.properties;


import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionPageModifyListener;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionPageWrapper;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.PropertyPage;


public class ConnectionPropertyPage extends PropertyPage implements ConnectionPageModifyListener
{

    private TabFolder tabFolder;

    private TabItem networkTab;

    private TabItem authTab;

    private TabItem optionsTab;

    private ConnectionPageWrapper cpw;


    public ConnectionPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    public void connectionPageModified()
    {
        validate();
    }


    public void setMessage( String message )
    {
        super.setMessage( message, PropertyPage.WARNING );
        getContainer().updateTitle();
        getContainer().updateMessage();
        validate();
    }


    public void setErrorMessage( String errorMessage )
    {
        super.setErrorMessage( errorMessage );
        getContainer().updateTitle();
        getContainer().updateMessage();
        validate();
    }


    public IConnection getRealConnection()
    {
        return getConnection( getElement() );
    }


    private void validate()
    {
        setValid( getMessage() == null && getErrorMessage() == null );
    }


    static IConnection getConnection( Object element )
    {
        IConnection connection = null;
        if ( element instanceof IAdaptable )
        {
            connection = ( IConnection ) ( ( IAdaptable ) element ).getAdapter( IConnection.class );
        }
        return connection;
    }


    protected Control createContents( Composite parent )
    {

        IConnection connection = ( IConnection ) getConnection( getElement() );
        if ( connection != null )
        {
            super.setMessage( "Connection " + Utils.shorten( connection.getName(), 30 ) );
        }

        this.tabFolder = new TabFolder( parent, SWT.TOP );

        this.cpw = new ConnectionPageWrapper( this, null );

        Composite networkComposite = new Composite( this.tabFolder, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        networkComposite.setLayout( gl );
        cpw.addMainInput( connection.getName(), connection.getHost(), connection.getPort(), connection
            .getEncryptionMethod(), networkComposite );
        this.networkTab = new TabItem( this.tabFolder, SWT.NONE );
        this.networkTab.setText( "Network Parameter" );
        this.networkTab.setControl( networkComposite );

        Composite authComposite = new Composite( this.tabFolder, SWT.NONE );
        gl = new GridLayout( 1, false );
        authComposite.setLayout( gl );
        cpw.addAuthenticationMethodInput( connection.getAuthMethod(), authComposite );
        cpw.addSimpleAuthInput( connection.getBindPassword() != null,
            connection.getBindPrincipal() != null ? connection.getBindPrincipal().toString() : "", connection
                .getBindPassword() != null ? connection.getBindPassword() : "", authComposite );
        this.authTab = new TabItem( this.tabFolder, SWT.NONE );
        this.authTab.setText( "Authentification" );
        this.authTab.setControl( authComposite );

        Composite optionsComposite = new Composite( this.tabFolder, SWT.NONE );
        gl = new GridLayout( 1, false );
        optionsComposite.setLayout( gl );
        cpw.addBaseDNInput( connection.isFetchBaseDNs(), connection.getBaseDN().toString(), optionsComposite );
        cpw.addLimitInput( connection.getCountLimit(), connection.getTimeLimit(), connection
            .getAliasesDereferencingMethod(), connection.getReferralsHandlingMethod(), optionsComposite );

        this.optionsTab = new TabItem( this.tabFolder, SWT.NONE );
        this.optionsTab.setText( "Options" );
        this.optionsTab.setControl( optionsComposite );

        return tabFolder;
    }


    public boolean performOk()
    {

        IConnection connection = ( IConnection ) getConnection( getElement() );

        if ( connection instanceof IConnection )
        {
            connection.setName( cpw.getName() );
            connection.setHost( cpw.getHostName() );
            connection.setPort( cpw.getPort() );
            connection.setEncryptionMethod( cpw.getEncyrptionMethod() );

            connection.setAuthMethod( cpw.getAuthenticationMethod() );

            connection.setFetchBaseDNs( cpw.isAutoFetchBaseDns() );
            try
            {
                connection.setBaseDN( new DN( cpw.getBaseDN() ) );
            }
            catch ( NameException e )
            {
            }
            connection.setCountLimit( cpw.getCountLimit() );
            connection.setTimeLimit( cpw.getTimeLimit() );
            connection.setAliasesDereferencingMethod( cpw.getAliasesDereferencingMethod() );
            connection.setReferralsHandlingMethod( cpw.getReferralsHandlingMethod() );
        }

        if ( connection.getAuthMethod() == IConnection.AUTH_ANONYMOUS )
        {
            connection.setBindPrincipal( null );
            connection.setBindPassword( null );
        }
        if ( connection.getAuthMethod() == IConnection.AUTH_SIMPLE )
        {
            try
            {
                connection.setBindPrincipal( cpw.getSimpleAuthBindDN() );
                connection
                    .setBindPassword( cpw.isSaveSimpleAuthBindPassword() ? cpw.getSimpleAuthBindPassword() : null );
            }
            catch ( Exception e )
            {
            }
        }

        cpw.saveDialogSettings();
        return true;
    }

}
