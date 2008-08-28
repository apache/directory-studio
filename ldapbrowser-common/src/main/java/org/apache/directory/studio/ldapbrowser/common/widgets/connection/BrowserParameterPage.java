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


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.shared.ldap.util.LdapURL.Extension;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.AliasesDereferencingWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.LimitWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.ReferralsHandlingWidget;
import org.apache.directory.studio.ldapbrowser.core.jobs.FetchBaseDNsRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BrowserConnection;
import org.eclipse.core.runtime.IStatus;
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

    private static final String X_BASE_DN = "X-BASE-DN";

    private static final String X_COUNT_LIMIT = "X-COUNT-LIMIT";

    private static final String X_TIME_LIMIT = "X-TIME-LIMIT";

    private static final String X_ALIAS_HANDLING = "X-ALIAS-HANDLING";

    private static final String X_ALIAS_HANDLING_FINDING = "FINDING";

    private static final String X_ALIAS_HANDLING_SEARCHING = "SEARCHING";

    private static final String X_ALIAS_HANDLING_NEVER = "NEVER";

    private static final String X_REFERRAL_HANDLING = "X-REFERRAL-HANDLING";

    private static final String X_REFERRAL_HANDLING_IGNORE = "IGNORE";

    private static final String X_REFERRAL_HANDLING_MANAGE = "MANAGE";

    private static final String X_FETCH_SUBENTRIES = "X-FETCH-SUBENTRIES";

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

    /** The fetch subentries button. */
    private Button fetchSubentriesButton;


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
    private Connection.AliasDereferencingMethod getAliasesDereferencingMethod()
    {
        return aliasesDereferencingWidget.getAliasesDereferencingMethod();
    }


    /**
     * Gets the referrals handling method.
     * 
     * @return the referrals handling method
     */
    private Connection.ReferralHandlingMethod getReferralsHandlingMethod()
    {
        return referralsHandlingWidget.getReferralsHandlingMethod();
    }


    /**
     * Returns true if subentries should be fetched
     * while browsing.
     * 
     * @return true, if subentries should be fetched
     */
    private boolean isFetchSubentries()
    {
        return fetchSubentriesButton.getSelection();
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
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#createComposite(org.eclipse.swt.widgets.Composite)
     */
    protected void createComposite( Composite parent )
    {
        addBaseDNInput( parent );
        addLimitInput( parent );
        addOptionsInput( parent );
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

        fetchBaseDnsButton = new Button( groupComposite, SWT.PUSH );
        fetchBaseDnsButton.setText( "Fetch Base DNs" );
        fetchBaseDnsButton.setEnabled( true );
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        fetchBaseDnsButton.setLayoutData( gd );

        BaseWidgetUtils.createLabel( groupComposite, "Base DN:", 1 );
        baseDNCombo = BaseWidgetUtils.createCombo( groupComposite, new String[0], 0, 2 );
    }


    /**
     * Adds the options input.
     * 
     * @param parent the parent
     */
    private void addOptionsInput( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, "Options", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        fetchSubentriesButton = BaseWidgetUtils.createCheckbox( groupComposite,
            "Fetch subentries while browsing (requires additional search request)", 1 );
        fetchSubentriesButton.setSelection( false );
    }


    /**
     * Adds the limit input.
     * 
     * @param parent the parent
     */
    public void addLimitInput( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        limitWidget = new LimitWidget( 1000, 0 );
        limitWidget.createWidget( composite );

        aliasesDereferencingWidget = new AliasesDereferencingWidget( Connection.AliasDereferencingMethod.ALWAYS );
        aliasesDereferencingWidget.createWidget( composite );

        referralsHandlingWidget = new ReferralsHandlingWidget( Connection.ReferralHandlingMethod.FOLLOW );
        referralsHandlingWidget.createWidget( composite );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#validate()
     */
    protected void validate()
    {
        // set enabled/disabled state of fields and buttons
        baseDNCombo.setEnabled( !isAutoFetchBaseDns() );

        // validate input fields
        message = null;
        infoMessage = null;
        errorMessage = null;
        if ( !isAutoFetchBaseDns() )
        {
            if ( !LdapDN.isValid( getBaseDN() ) )
            {
                message = "Please enter a valid base DN.";
            }
        }
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#loadParameters(org.apache.directory.studio.connection.core.ConnectionParameter)
     */
    protected void loadParameters( ConnectionParameter parameter )
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

        int referralsHandlingMethodOrdinal = parameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        Connection.ReferralHandlingMethod referralsHandlingMethod = Connection.ReferralHandlingMethod
            .getByOrdinal( referralsHandlingMethodOrdinal );
        referralsHandlingWidget.setReferralsHandlingMethod( referralsHandlingMethod );

        int aliasesDereferencingMethodOrdinal = parameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD );
        Connection.AliasDereferencingMethod aliasesDereferencingMethod = Connection.AliasDereferencingMethod
            .getByOrdinal( aliasesDereferencingMethodOrdinal );
        aliasesDereferencingWidget.setAliasesDereferencingMethod( aliasesDereferencingMethod );

        boolean fetchSubentries = parameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES );
        fetchSubentriesButton.setSelection( fetchSubentries );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#initListeners()
     */
    protected void initListeners()
    {
        autoFetchBaseDnsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                connectionPageModified();
            }
        } );

        fetchBaseDnsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                Connection connection = getTestConnection();
                IBrowserConnection browserConnection = new BrowserConnection( connection );

                FetchBaseDNsRunnable runnable = new FetchBaseDNsRunnable( browserConnection );
                IStatus status = RunnableContextRunner.execute( runnable, runnableContext, true );
                if ( status.isOK() )
                {
                    if ( !runnable.getBaseDNs().isEmpty() )
                    {
                        List<String> baseDNs = runnable.getBaseDNs();
                        baseDNCombo.setItems( baseDNs.toArray( new String[baseDNs.size()] ) );
                        baseDNCombo.select( 0 );

                        String msg = "The server returned the following base DNs:";
                        for ( String baseDN : baseDNs )
                        {
                            msg += "\n  - " + baseDN;
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

        baseDNCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        fetchSubentriesButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                connectionPageModified();
            }
        } );
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
            getReferralsHandlingMethod().getOrdinal() );
        parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            getAliasesDereferencingMethod().getOrdinal() );
        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES,
            isFetchSubentries() );
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

        return isReconnectionRequired() || countLimit != getCountLimit() || timeLimit != getTimeLimit();
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
        int referralsHandlingMethodOrdinal = connectionParameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        Connection.ReferralHandlingMethod referralsHandlingMethod = Connection.ReferralHandlingMethod
            .getByOrdinal( referralsHandlingMethodOrdinal );
        int aliasesDereferencingMethodOrdinal = connectionParameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD );
        Connection.AliasDereferencingMethod aliasesDereferencingMethod = Connection.AliasDereferencingMethod
            .getByOrdinal( aliasesDereferencingMethodOrdinal );
        boolean fetchSubentries = connectionParameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES );

        return fetchBaseDns != isAutoFetchBaseDns() || !StringUtils.equals( baseDn, getBaseDN() )
            || referralsHandlingMethod != getReferralsHandlingMethod()
            || aliasesDereferencingMethod != getAliasesDereferencingMethod() || fetchSubentries != isFetchSubentries();
    }


    /**
     * {@inheritDoc}
     */
    public void mergeParametersToLdapURL( ConnectionParameter parameter, LdapURL ldapUrl )
    {
        boolean fetchBaseDns = parameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_BASE_DNS );
        String baseDn = parameter.getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_BASE_DN );
        if ( !fetchBaseDns && StringUtils.isNotEmpty( baseDn ) )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_BASE_DN, baseDn ) );
        }

        int countLimit = parameter.getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_COUNT_LIMIT );
        if ( countLimit != 0 )
        {
            ldapUrl.getExtensions().add(
                new Extension( false, X_COUNT_LIMIT, parameter
                    .getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_COUNT_LIMIT ) ) );
        }

        int timeLimit = parameter.getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_TIME_LIMIT );
        if ( timeLimit != 0 )
        {
            ldapUrl.getExtensions().add(
                new Extension( false, X_TIME_LIMIT, parameter
                    .getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_TIME_LIMIT ) ) );
        }

        int aliasesDereferencingMethodOrdinal = parameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD );
        Connection.AliasDereferencingMethod aliasesDereferencingMethod = Connection.AliasDereferencingMethod
            .getByOrdinal( aliasesDereferencingMethodOrdinal );
        switch ( aliasesDereferencingMethod )
        {
            case ALWAYS:
                // default
                break;
            case FINDING:
                ldapUrl.getExtensions().add( new Extension( false, X_ALIAS_HANDLING, X_ALIAS_HANDLING_FINDING ) );
                break;
            case SEARCH:
                ldapUrl.getExtensions().add( new Extension( false, X_ALIAS_HANDLING, X_ALIAS_HANDLING_SEARCHING ) );
                break;
            case NEVER:
                ldapUrl.getExtensions().add( new Extension( false, X_ALIAS_HANDLING, X_ALIAS_HANDLING_NEVER ) );
                break;
        }

        int referralsHandlingMethodOrdinal = parameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        Connection.ReferralHandlingMethod referralsHandlingMethod = Connection.ReferralHandlingMethod
            .getByOrdinal( referralsHandlingMethodOrdinal );
        switch ( referralsHandlingMethod )
        {
            case FOLLOW:
                // default
                break;
            case IGNORE:
                ldapUrl.getExtensions().add( new Extension( false, X_REFERRAL_HANDLING, X_REFERRAL_HANDLING_IGNORE ) );
                break;
            case MANAGE:
                ldapUrl.getExtensions().add( new Extension( false, X_REFERRAL_HANDLING, X_REFERRAL_HANDLING_MANAGE ) );
                break;
        }

        boolean fetchSubentries = parameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES );
        if ( fetchSubentries )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_FETCH_SUBENTRIES, null ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void mergeLdapUrlToParameters( LdapURL ldapUrl, ConnectionParameter parameter )
    {
        // base DN, get from Root DSE if absent, may be empty 
        String baseDn = ldapUrl.getExtensionValue( X_BASE_DN );
        if ( baseDn == null )
        {
            parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_BASE_DNS, true );
            parameter.setExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_BASE_DN, null );
        }
        else
        {
            parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_BASE_DNS, false );
            parameter.setExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_BASE_DN, baseDn );
        }

        // count limit, 1000 if non-numeric or absent 
        String countLimit = ldapUrl.getExtensionValue( X_COUNT_LIMIT );
        try
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_COUNT_LIMIT, new Integer(
                countLimit ).intValue() );
        }
        catch ( NumberFormatException e )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_COUNT_LIMIT, 0 );
        }

        // time limit, 0 if non-numeric or absent 
        String timeLimit = ldapUrl.getExtensionValue( X_TIME_LIMIT );
        try
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_TIME_LIMIT, new Integer(
                timeLimit ).intValue() );
        }
        catch ( NumberFormatException e )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_TIME_LIMIT, 0 );
        }

        // alias handling, ALWAYS if unknown or absent
        String alias = ldapUrl.getExtensionValue( X_ALIAS_HANDLING );
        if ( StringUtils.isNotEmpty( alias ) && X_ALIAS_HANDLING_FINDING.equalsIgnoreCase( alias ) )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
                Connection.AliasDereferencingMethod.FINDING.getOrdinal() );
        }
        else if ( StringUtils.isNotEmpty( alias ) && X_ALIAS_HANDLING_SEARCHING.equalsIgnoreCase( alias ) )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
                Connection.AliasDereferencingMethod.SEARCH.getOrdinal() );
        }
        else if ( StringUtils.isNotEmpty( alias ) && X_ALIAS_HANDLING_NEVER.equalsIgnoreCase( alias ) )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
                Connection.AliasDereferencingMethod.NEVER.getOrdinal() );
        }
        else
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
                Connection.AliasDereferencingMethod.ALWAYS.getOrdinal() );
        }

        // referral handling, FOLLOW if unknown or absent
        String referral = ldapUrl.getExtensionValue( X_REFERRAL_HANDLING );
        if ( StringUtils.isNotEmpty( referral ) && X_REFERRAL_HANDLING_IGNORE.equalsIgnoreCase( referral ) )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
                Connection.ReferralHandlingMethod.IGNORE.getOrdinal() );
        }
        else if ( StringUtils.isNotEmpty( referral ) && X_REFERRAL_HANDLING_MANAGE.equalsIgnoreCase( referral ) )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
                Connection.ReferralHandlingMethod.MANAGE.getOrdinal() );
        }
        else
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
                Connection.ReferralHandlingMethod.FOLLOW.getOrdinal() );
        }

        // fetch subentries
        Extension fetchSubentries = ldapUrl.getExtension( X_FETCH_SUBENTRIES );
        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES,
            fetchSubentries != null );
    }
}
