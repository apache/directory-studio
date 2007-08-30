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

package org.apache.directory.studio.ldapbrowser.common.widgets.connection;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.jobs.RunnableContextJobAdapter;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.AliasesDereferencingWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.LimitWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.ReferralsHandlingWidget;
import org.apache.directory.studio.ldapbrowser.core.internal.model.BrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.jobs.FetchBaseDNsJob;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.NameException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;


/**
 * The BrowserParameterPage is used the edit the browser specific parameters of a
 * connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserParameterPage extends AbstractConnectionParameterPage
{

    /** The checkbox to fetch the base DN's from namingContexts whenever opening the connection */
    private Button autoFetchBaseDnsButton;

    /** The button to fetch the base DN's from namingContexts attribute */
    private Button fetchBaseDnsButton;

    /** The combo that displays the fetched base DN's */
    private Combo baseDNCombo;

    /** The widget with the count and time limits */
    private LimitWidget limitWidget;

    /** The widget to select the alias dereferencing method */
    private AliasesDereferencingWidget aliasesDereferencingWidget;

    /** The widget to select the referrals handling method */
    private ReferralsHandlingWidget referralsHandlingWidget;


    /**
     * Creates a new instance of BrowserParameterPage.
     */
    public BrowserParameterPage()
    {
    }


    /**
     * Returns true if base DN's should be fetched 
     * whenever opening the connection.
     * 
     * @return true, if base DN's should be fetched
     */
    private boolean isAutoFetchBaseDns()
    {
        return autoFetchBaseDnsButton.getSelection();
    }


    /**
     * Gets the base DN.
     * 
     * @return the base DN
     */
    private String getBaseDN()
    {
        return isAutoFetchBaseDns() ? null : baseDNCombo.getText();
    }


    /**
     * Gets the count limit.
     * 
     * @return the count limit
     */
    private int getCountLimit()
    {
        return limitWidget.getCountLimit();
    }


    /**
     * Gets the time limit.
     * 
     * @return the time limit
     */
    private int getTimeLimit()
    {
        return limitWidget.getTimeLimit();
    }


    /**
     * Gets the aliases dereferencing method.
     * 
     * @return the aliases dereferencing method
     */
    private int getAliasesDereferencingMethod()
    {
        return aliasesDereferencingWidget.getAliasesDereferencingMethod();
    }


    /**
     * Gets the referrals handling method.
     * 
     * @return the referrals handling method
     */
    private int getReferralsHandlingMethod()
    {
        return referralsHandlingWidget.getReferralsHandlingMethod();
    }


    /**
     * Gets a temporary connection with all conection parameter 
     * entered in this page. 
     *
     * @return a test connection
     */
    private Connection getTestConnection()
    {
        ConnectionParameter cp = connectionParameterPageModifyListener.getTestConnectionParameters();
        Connection conn = new Connection( cp );
        return conn;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#createComposite(org.eclipse.swt.widgets.Composite)
     */
    public void createComposite( Composite parent )
    {
        addBaseDNInput( parent );
        addLimitInput( parent );
        validate();
    }


    /**
     * Adds the base DN input.
     * 
     * @param parent the parent
     */
    private void addBaseDNInput( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, "Base DN", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 3, 1 );
        GridData gd;

        autoFetchBaseDnsButton = BaseWidgetUtils.createCheckbox( groupComposite, "Get base DNs from Root DSE", 2 );
        autoFetchBaseDnsButton.setSelection( true );
        autoFetchBaseDnsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                connectionPageModified();
            }
        } );

        fetchBaseDnsButton = new Button( groupComposite, SWT.PUSH );
        fetchBaseDnsButton.setText( "Fetch Base DNs" );
        fetchBaseDnsButton.setEnabled( true );
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        fetchBaseDnsButton.setLayoutData( gd );
        fetchBaseDnsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                Connection connection = getTestConnection();
                IBrowserConnection browserConnection = new BrowserConnection( connection );

                FetchBaseDNsJob job = new FetchBaseDNsJob( browserConnection );
                RunnableContextJobAdapter.execute( job, runnableContext );
                if ( job.getExternalResult().isOK() )
                {
                    if ( job.getBaseDNs().length > 0 )
                    {
                        String[] baseDNs = job.getBaseDNs();
                        baseDNCombo.setItems( baseDNs );
                        baseDNCombo.select( 0 );

                        String msg = "The server returned the following base DNs:";
                        for ( int i = 0; i < baseDNs.length; i++ )
                        {
                            msg += "\n  - " + baseDNs[i];
                        }
                        MessageDialog.openInformation( Display.getDefault().getActiveShell(), "Fetch Base DNs", msg );
                    }
                    else
                    {
                        MessageDialog.openWarning( Display.getDefault().getActiveShell(), "Fetch Base DNs",
                            "No base DN returned from server. Please enter the base DN manually." );
                        autoFetchBaseDnsButton.setSelection( false );
                    }
                }
            }
        } );

        BaseWidgetUtils.createLabel( groupComposite, "Base DN:", 1 );
        baseDNCombo = BaseWidgetUtils.createCombo( groupComposite, new String[0], 0, 2 );
        baseDNCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );
    }


    /**
     * Adds the limit input.
     * 
     * @param parent the parent
     */
    public void addLimitInput( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        limitWidget = new LimitWidget();
        limitWidget.createWidget( composite );

        aliasesDereferencingWidget = new AliasesDereferencingWidget();
        aliasesDereferencingWidget.createWidget( composite );

        referralsHandlingWidget = new ReferralsHandlingWidget();
        referralsHandlingWidget.createWidget( composite );
    }


    /**
     * Called when an input field was modified.
     */
    private void connectionPageModified()
    {
        validate();
        fireConnectionPageModified();
    }


    /**
     * Validates the input fields after each modification.
     */
    private void validate()
    {
        // set enabled/disabled state of fields and buttons
        baseDNCombo.setEnabled( !isAutoFetchBaseDns() );

        // validate input fields
        message = null;
        errorMessage = null;
        if ( !isAutoFetchBaseDns() )
        {
            try
            {
                new DN( getBaseDN() );
            }
            catch ( NameException e )
            {
                message = "Please enter a valid base DN.";
            }
        }
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#loadParameters(org.apache.directory.studio.connection.core.ConnectionParameter)
     */
    public void loadParameters( ConnectionParameter parameter )
    {
        this.connectionParameter = parameter;

        boolean fetchBaseDns = parameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_BASE_DNS );
        autoFetchBaseDnsButton.setSelection( fetchBaseDns );
        String baseDn = parameter.getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_BASE_DN );
        baseDNCombo.setText( baseDn != null ? baseDn : "" );

        int countLimit = parameter.getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_COUNT_LIMIT );
        limitWidget.setCountLimit( countLimit );
        int timeLimit = parameter.getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_TIME_LIMIT );
        limitWidget.setTimeLimit( timeLimit );

        int referralsHandlingMethod = parameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        referralsHandlingWidget.setReferralsHandlingMethod( referralsHandlingMethod );
        int aliasesDereferencingMethod = parameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD );
        aliasesDereferencingWidget.setAliasesDereferencingMethod( aliasesDereferencingMethod );

        connectionPageModified();
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#saveParameters(org.apache.directory.studio.connection.core.ConnectionParameter)
     */
    public void saveParameters( ConnectionParameter parameter )
    {
        parameter
            .setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_BASE_DNS, isAutoFetchBaseDns() );
        parameter.setExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_BASE_DN, getBaseDN() );
        parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_COUNT_LIMIT, getCountLimit() );
        parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_TIME_LIMIT, getTimeLimit() );
        parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            getReferralsHandlingMethod() );
        parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            getAliasesDereferencingMethod() );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#saveDialogSettings()
     */
    public void saveDialogSettings()
    {
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#setFocus()
     */
    public void setFocus()
    {
        baseDNCombo.setFocus();
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#areParametersModifed()
     */
    public boolean areParametersModifed()
    {
        int countLimit = connectionParameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_COUNT_LIMIT );
        int timeLimit = connectionParameter.getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_TIME_LIMIT );
        int referralsHandlingMethod = connectionParameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        int aliasesDereferencingMethod = connectionParameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD );

        return isReconnectionRequired() || countLimit != getCountLimit() || timeLimit != getTimeLimit()
            || referralsHandlingMethod != getReferralsHandlingMethod()
            || aliasesDereferencingMethod != getAliasesDereferencingMethod();
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#isReconnectionRequired()
     */
    public boolean isReconnectionRequired()
    {
        if ( connectionParameter == null )
        {
            return true;
        }

        boolean fetchBaseDns = connectionParameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_BASE_DNS );
        String baseDn = connectionParameter.getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_BASE_DN );
        return fetchBaseDns != isAutoFetchBaseDns() || !( baseDn.equals( getBaseDN() ) );
    }

}
