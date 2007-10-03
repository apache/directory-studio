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

package org.apache.directory.studio.connection.ui.widgets;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.jobs.CheckNetworkParameterJob;
import org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;


/**
 * The NetworkParameterPage is used the edit the network parameters of a
 * connection.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NetworkParameterPage extends AbstractConnectionParameterPage
{

    /** The connection name text widget */
    private Text nameText;

    /** The host name combo with the history of recently used host names */
    private Combo hostCombo;

    /** The host combo with the history of recently used ports */
    private Combo portCombo;

    /** The combo to select the encryption method */
    private Combo encryptionMethodCombo;

    /** The button to check the connection parameters */
    private Button checkConnectionButton;


    /**
     * Creates a new instance of NetworkParameterPage.
     */
    public NetworkParameterPage()
    {
    }


    /**
     * Gets the connection name.
     * 
     * @return the connectio name
     */
    private String getName()
    {
        return nameText.getText();
    }


    /**
     * Gets the host name.
     * 
     * @return the host name
     */
    private String getHostName()
    {
        return hostCombo.getText();
    }


    /**
     * Gets the port.
     * 
     * @return the port
     */
    private int getPort()
    {
        return Integer.parseInt( portCombo.getText() );
    }


    /**
     * Gets the encyrption method.
     * 
     * @return the encyrption method
     */
    private ConnectionParameter.EncryptionMethod getEncyrptionMethod()
    {
        switch ( encryptionMethodCombo.getSelectionIndex() )
        {
            case 1:
                return ConnectionParameter.EncryptionMethod.LDAPS;
            case 2:
                return ConnectionParameter.EncryptionMethod.START_TLS;
            default:
                return ConnectionParameter.EncryptionMethod.NONE;
        }
    }


    /**
     * Gets a temporary connection with all conection parameter 
     * entered in this page. 
     *
     * @return a test connection
     */
    private Connection getTestConnection()
    {
        ConnectionParameter cp = new ConnectionParameter( null, getHostName(), getPort(), getEncyrptionMethod(),
            ConnectionParameter.AuthenticationMethod.NONE, null, null, null, null );
        Connection conn = new Connection( cp );
        return conn;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#createComposite(org.eclipse.swt.widgets.Composite)
     */
    public void createComposite( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Composite nameComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( nameComposite, "Connection name:", 1 );
        nameText = BaseWidgetUtils.createText( nameComposite, "", 1 );
        nameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        BaseWidgetUtils.createSpacer( composite, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, "Network Parameter", 1 );

        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 3, 1 );
        BaseWidgetUtils.createLabel( groupComposite, "Hostname:", 1 );
        String[] hostHistory = HistoryUtils.load( ConnectionUIConstants.DIALOGSETTING_KEY_HOST_HISTORY );
        hostCombo = BaseWidgetUtils.createCombo( groupComposite, hostHistory, -1, 2 );
        hostCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        BaseWidgetUtils.createLabel( groupComposite, "Port:", 1 );
        String[] portHistory = HistoryUtils.load( ConnectionUIConstants.DIALOGSETTING_KEY_PORT_HISTORY );
        portCombo = BaseWidgetUtils.createCombo( groupComposite, portHistory, -1, 2 );
        portCombo.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent event )
            {
                if ( !event.text.matches( "[0-9]*" ) )
                {
                    event.doit = false;
                }
                if ( portCombo.getText().length() > 4 && event.text.length() > 0 )
                {
                    event.doit = false;
                }
            }
        } );
        portCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        String[] encMethods = new String[]
            { "No encryption", "Use SSL encryption (ldaps://)", "Use StartTLS extension" };
        int index = 0;
        BaseWidgetUtils.createLabel( groupComposite, "Encryption method:", 1 );
        encryptionMethodCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, encMethods, index, 2 );
        encryptionMethodCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );
        BaseWidgetUtils.createSpacer( groupComposite, 1 );
        BaseWidgetUtils
            .createLabel(
                groupComposite,
                "Warning: The current version doesn't support certificate validation, \nbe aware of invalid certificates or man-in-the-middle attacks!",
                2 );

        BaseWidgetUtils.createSpacer( groupComposite, 2 );
        checkConnectionButton = new Button( groupComposite, SWT.PUSH );
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        gd.verticalAlignment = SWT.BOTTOM;
        checkConnectionButton.setLayoutData( gd );
        checkConnectionButton.setText( "Check Network Parameter" );
        checkConnectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                Connection connection = getTestConnection();
                CheckNetworkParameterJob job = new CheckNetworkParameterJob( connection );
                RunnableContextJobAdapter.execute( job, runnableContext );
                if ( job.getExternalResult().isOK() )
                {
                    MessageDialog.openInformation( Display.getDefault().getActiveShell(), "Check Network Parameter",
                        "The connection was established successfully." );
                }
            }
        } );

        validate();
        nameText.setFocus();
    }


    /**
     * Called when an input field was modified.
     */
    private void connectionPageModified()
    {
        // validate()
        validate();

        // fire
        fireConnectionPageModified();
    }


    /**
     * Validates the input fields after each modification.
     */
    private void validate()
    {
        // set enabled/disabled state of check connection button
        checkConnectionButton.setEnabled( !hostCombo.getText().equals( "" ) && !portCombo.getText().equals( "" ) );

        // validate input fields
        message = null;
        errorMessage = null;
        if ( "".equals( portCombo.getText() ) )
        {
            message = "Please enter a port. The default LDAP port is 389.";
        }
        if ( "".equals( hostCombo.getText() ) )
        {
            message = "Please enter a hostname.";
        }
        if ( "".equals( nameText.getText() ) )
        {
            message = "Please enter a connection name.";
        }
        if ( ConnectionCorePlugin.getDefault().getConnectionManager().getConnectionByName( nameText.getText() ) != null
            && ( connectionParameter == null || !nameText.getText().equals( connectionParameter.getName() ) ) )
        {
            errorMessage = "A connection named '" + nameText.getText() + "' already exists.";
        }
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#loadParameters(org.apache.directory.studio.connection.core.ConnectionParameter)
     */
    public void loadParameters( ConnectionParameter parameter )
    {
        connectionParameter = parameter;

        nameText.setText( parameter.getName() );
        hostCombo.setText( parameter.getHost() );
        portCombo.setText( Integer.toString( parameter.getPort() ) );
        int index = parameter.getEncryptionMethod() == EncryptionMethod.LDAPS ? 1
            : parameter.getEncryptionMethod() == EncryptionMethod.START_TLS ? 2 : 0;
        encryptionMethodCombo.select( index );

        connectionPageModified();
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#saveParameters(org.apache.directory.studio.connection.core.ConnectionParameter)
     */
    public void saveParameters( ConnectionParameter parameter )
    {
        parameter.setName( getName() );
        parameter.setHost( getHostName() );
        parameter.setPort( getPort() );
        parameter.setEncryptionMethod( getEncyrptionMethod() );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#saveDialogSettings()
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( ConnectionUIConstants.DIALOGSETTING_KEY_HOST_HISTORY, hostCombo.getText() );
        HistoryUtils.save( ConnectionUIConstants.DIALOGSETTING_KEY_PORT_HISTORY, portCombo.getText() );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#setFocus()
     */
    public void setFocus()
    {
        nameText.setFocus();
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#areParametersModifed()
     */
    public boolean areParametersModifed()
    {
        return isReconnectionRequired() || !( connectionParameter.getName().equals( getName() ) );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#isReconnectionRequired()
     */
    public boolean isReconnectionRequired()
    {
        return connectionParameter == null || !( connectionParameter.getHost().equals( getHostName() ) )
            || connectionParameter.getPort() != getPort()
            || connectionParameter.getEncryptionMethod() != getEncyrptionMethod();
    }

}
