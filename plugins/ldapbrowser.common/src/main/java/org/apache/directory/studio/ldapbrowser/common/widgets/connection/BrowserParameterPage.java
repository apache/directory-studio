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
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.api.ldap.model.url.LdapUrl.Extension;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
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
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * The BrowserParameterPage is used the edit the browser specific parameters of a
 * connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserParameterPage extends AbstractConnectionParameterPage
{

    private static final String X_BASE_DN = "X-BASE-Dn"; //$NON-NLS-1$

    private static final String X_COUNT_LIMIT = "X-COUNT-LIMIT"; //$NON-NLS-1$

    private static final String X_TIME_LIMIT = "X-TIME-LIMIT"; //$NON-NLS-1$

    private static final String X_ALIAS_HANDLING = "X-ALIAS-HANDLING"; //$NON-NLS-1$

    private static final String X_ALIAS_HANDLING_FINDING = "FINDING"; //$NON-NLS-1$

    private static final String X_ALIAS_HANDLING_SEARCHING = "SEARCHING"; //$NON-NLS-1$

    private static final String X_ALIAS_HANDLING_NEVER = "NEVER"; //$NON-NLS-1$

    private static final String X_REFERRAL_HANDLING = "X-REFERRAL-HANDLING"; //$NON-NLS-1$

    private static final String X_REFERRAL_HANDLING_IGNORE = "IGNORE"; //$NON-NLS-1$

    private static final String X_REFERRAL_HANDLING_FOLLOW = "FOLLOW"; //$NON-NLS-1$

    private static final String X_MANAGE_DSA_IT = "X-MANAGE-DSA-IT"; //$NON-NLS-1$

    private static final String X_FETCH_SUBENTRIES = "X-FETCH-SUBENTRIES"; //$NON-NLS-1$

    private static final String X_FETCH_OPERATIONAL_ATTRIBUTES = "X-FETCH-OPERATIONAL-ATTRIBUTES"; //$NON-NLS-1$

    private static final String X_PAGED_SEARCH = "X-PAGED-SEARCH"; //$NON-NLS-1$

    private static final String X_PAGED_SEARCH_SIZE = "X-PAGED-SEARCH-SIZE"; //$NON-NLS-1$

    private static final String X_PAGED_SEARCH_SCROLL_MODE = "X-PAGED-SEARCH-SCROLL-MODE"; //$NON-NLS-1$

    /** The checkbox to fetch the base Dn's from namingContexts whenever opening the connection */
    private Button autoFetchBaseDnsButton;

    /** The button to fetch the base Dn's from namingContexts attribute */
    private Button fetchBaseDnsButton;

    /** The combo that displays the fetched base Dn's */
    private Combo baseDNCombo;

    /** The widget with the count and time limits */
    private LimitWidget limitWidget;

    /** The widget to select the alias dereferencing method */
    private AliasesDereferencingWidget aliasesDereferencingWidget;

    /** The widget to select the referrals handling method */
    private ReferralsHandlingWidget referralsHandlingWidget;

    /** The ManageDsaIT control button. */
    private Button manageDsaItButton;

    /** The fetch subentries button. */
    private Button fetchSubentriesButton;

    /** The paged search button. */
    private Button pagedSearchButton;

    /** The paged search size label. */
    private Label pagedSearchSizeLabel;

    /** The paged search size text. */
    private Text pagedSearchSizeText;

    /** The paged search scroll mode button. */
    private Button pagedSearchScrollModeButton;

    /** The fetch operational attributes button. */
    private Button fetchOperationalAttributesButton;


    /**
     * Creates a new instance of BrowserParameterPage.
     */
    public BrowserParameterPage()
    {
    }


    /**
     * Returns true if base Dn's should be fetched
     * whenever opening the connection.
     * 
     * @return true, if base Dn's should be fetched
     */
    private boolean isAutoFetchBaseDns()
    {
        return autoFetchBaseDnsButton.getSelection();
    }


    /**
     * Gets the base Dn.
     * 
     * @return the base Dn
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
     * Returns true if ManageDsaIT control should be used
     * while browsing.
     * 
     * @return true, if ManageDsaIT control should be used
     */
    private boolean manageDsaIT()
    {
        return manageDsaItButton.getSelection();
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
     * Returns true if operational attributes should be fetched
     * while browsing.
     * 
     * @return true, if operational attributes should be fetched
     */
    private boolean isFetchOperationalAttributes()
    {
        return fetchOperationalAttributesButton.getSelection();
    }


    /**
     * Returns true if paged search should be used
     * while browsing.
     * 
     * @return true, if paged search should be used
     */
    private boolean isPagedSearch()
    {
        return pagedSearchButton.getSelection();
    }


    /**
     * Gets the paged search size.
     * 
     * @return the paged search size
     */
    private int getPagedSearchSize()
    {
        int pageSize;
        try
        {
            pageSize = new Integer( pagedSearchSizeText.getText() ).intValue();
        }
        catch ( NumberFormatException e )
        {
            pageSize = 100;
        }
        return pageSize;
    }


    /**
     * Returns true if scroll mode should be used
     * for paged search.
     * 
     * @return true, if scroll mode should be used
     */
    private boolean isPagedSearchScrollMode()
    {
        return pagedSearchScrollModeButton.getSelection();
    }


    /**
     * Gets a temporary connection with all connection parameter 
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
        addControlInput( parent );
        addFeaturesInput( parent );
    }


    /**
     * Adds the base Dn input.
     * 
     * @param parent the parent
     */
    private void addBaseDNInput( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite,
            Messages.getString( "BrowserParameterPage.BaseDNGroup" ), 1 ); //$NON-NLS-1$
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 3, 1 );
        GridData gd;

        autoFetchBaseDnsButton = BaseWidgetUtils.createCheckbox( groupComposite, Messages
            .getString( "BrowserParameterPage.GetBaseDNsFromRootDSE" ), 2 ); //$NON-NLS-1$
        autoFetchBaseDnsButton.setSelection( true );

        fetchBaseDnsButton = new Button( groupComposite, SWT.PUSH );
        fetchBaseDnsButton.setText( Messages.getString( "BrowserParameterPage.FetchBaseDNs" ) ); //$NON-NLS-1$
        fetchBaseDnsButton.setEnabled( true );
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        fetchBaseDnsButton.setLayoutData( gd );

        BaseWidgetUtils.createLabel( groupComposite, Messages.getString( "BrowserParameterPage.BaseDN" ), 1 ); //$NON-NLS-1$
        baseDNCombo = BaseWidgetUtils.createCombo( groupComposite, new String[0], 0, 2 );
    }


    /**
     * Adds the control input.
     * 
     * @param parent the parent
     */
    private void addControlInput( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, Messages.getString( "BrowserParameterPage.Controls" ), 1 ); //$NON-NLS-1$
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        // ManageDsaIT control
        manageDsaItButton = BaseWidgetUtils.createCheckbox( groupComposite, Messages
            .getString( "BrowserParameterPage.ManageDsaItWhileBrowsing" ), 1 ); //$NON-NLS-1$
        manageDsaItButton.setToolTipText( Messages.getString( "BrowserParameterPage.ManageDsaItWhileBrowsingTooltip" ) ); //$NON-NLS-1$
        manageDsaItButton.setSelection( false );

        // fetch subentries control
        fetchSubentriesButton = BaseWidgetUtils.createCheckbox( groupComposite, Messages
            .getString( "BrowserParameterPage.FetchSubentriesWhileBrowsing" ), 1 ); //$NON-NLS-1$
        fetchSubentriesButton.setToolTipText( Messages
            .getString( "BrowserParameterPage.FetchSubentriesWhileBrowsingTooltip" ) ); //$NON-NLS-1$
        fetchSubentriesButton.setSelection( false );

        // paged search control
        Composite sprcComposite = BaseWidgetUtils.createColumnContainer( groupComposite, 4, 1 );
        pagedSearchButton = BaseWidgetUtils.createCheckbox( sprcComposite, Messages
            .getString( "BrowserParameterPage.PagedSearch" ), 1 ); //$NON-NLS-1$
        pagedSearchButton.setToolTipText( Messages.getString( "BrowserParameterPage.PagedSearchTooltip" ) ); //$NON-NLS-1$

        pagedSearchSizeLabel = BaseWidgetUtils.createLabel( sprcComposite, Messages
            .getString( "BrowserParameterPage.PageSize" ), 1 ); //$NON-NLS-1$
        pagedSearchSizeText = BaseWidgetUtils.createText( sprcComposite, "100", 5, 1 ); //$NON-NLS-1$
        pagedSearchScrollModeButton = BaseWidgetUtils.createCheckbox( sprcComposite, Messages
            .getString( "BrowserParameterPage.ScrollMode" ), 1 ); //$NON-NLS-1$
        pagedSearchScrollModeButton.setToolTipText( Messages.getString( "BrowserParameterPage.ScrollModeTooltip" ) ); //$NON-NLS-1$
        pagedSearchScrollModeButton.setSelection( true );
    }


    /**
     * Adds the features input.
     * 
     * @param parent the parent
     */
    private void addFeaturesInput( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, Messages.getString( "BrowserParameterPage.Features" ), 1 ); //$NON-NLS-1$
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        // fetch operational attributes feature
        fetchOperationalAttributesButton = BaseWidgetUtils.createCheckbox( groupComposite, Messages
            .getString( "BrowserParameterPage.FetchOperationalAttributesWhileBrowsing" ), 1 ); //$NON-NLS-1$
        fetchOperationalAttributesButton.setToolTipText( Messages
            .getString( "BrowserParameterPage.FetchOperationalAttributesWhileBrowsingTooltip" ) ); //$NON-NLS-1$
        fetchOperationalAttributesButton.setSelection( false );
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

        referralsHandlingWidget = new ReferralsHandlingWidget( Connection.ReferralHandlingMethod.FOLLOW_MANUALLY );
        referralsHandlingWidget.createWidget( composite, true );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#validate()
     */
    protected void validate()
    {
        // set enabled/disabled state of fields and buttons
        baseDNCombo.setEnabled( !isAutoFetchBaseDns() );
        pagedSearchSizeLabel.setEnabled( isPagedSearch() );
        pagedSearchSizeText.setEnabled( isPagedSearch() );
        pagedSearchScrollModeButton.setEnabled( isPagedSearch() );

        // validate input fields
        message = null;
        infoMessage = null;
        errorMessage = null;
        if ( !isAutoFetchBaseDns() )
        {
            if ( !Dn.isValid(getBaseDN()) )
            {
                message = Messages.getString( "BrowserParameterPage.EnterValidBaseDN" ); //$NON-NLS-1$
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
        baseDNCombo.setText( baseDn != null ? baseDn : "" ); //$NON-NLS-1$

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

        boolean manageDsaIT = parameter.getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT );
        manageDsaItButton.setSelection( manageDsaIT );

        boolean fetchSubentries = parameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES );
        fetchSubentriesButton.setSelection( fetchSubentries );

        boolean pagedSearch = parameter.getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH );
        pagedSearchButton.setSelection( pagedSearch );
        String pagedSearchSize = parameter
            .getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE );
        pagedSearchSizeText.setText( pagedSearchSize != null ? pagedSearchSize : "100" ); //$NON-NLS-1$
        boolean pagedSearchScrollMode = parameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE );
        pagedSearchScrollModeButton.setSelection( pagedSearch ? pagedSearchScrollMode : true );

        boolean fetchOperationalAttributes = parameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_OPERATIONAL_ATTRIBUTES );
        fetchOperationalAttributesButton.setSelection( fetchOperationalAttributes );
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

                        String msg = Messages.getString( "BrowserParameterPage.BaseDNResult" ); //$NON-NLS-1$
                        for ( String baseDN : baseDNs )
                        {
                            msg += "\n  - " + baseDN; //$NON-NLS-1$
                        }
                        MessageDialog.openInformation( Display.getDefault().getActiveShell(), Messages
                            .getString( "BrowserParameterPage.FetchBaseDNs" ), msg ); //$NON-NLS-1$
                    }
                    else
                    {
                        MessageDialog.openWarning( Display.getDefault().getActiveShell(), Messages
                            .getString( "BrowserParameterPage.FetchBaseDNs" ), //$NON-NLS-1$
                            Messages.getString( "BrowserParameterPage.NoBaseDNReturnedFromServer" ) ); //$NON-NLS-1$
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

        manageDsaItButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
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

        pagedSearchButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                connectionPageModified();
            }
        } );
        pagedSearchSizeText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
        pagedSearchSizeText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                connectionPageModified();
            }
        } );

        fetchOperationalAttributesButton.addSelectionListener( new SelectionAdapter()
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

        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, manageDsaIT() );
        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES,
            isFetchSubentries() );
        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH, isPagedSearch() );
        parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE,
            getPagedSearchSize() );
        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE,
            isPagedSearchScrollMode() );
        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_OPERATIONAL_ATTRIBUTES,
            isFetchOperationalAttributes() );
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

        boolean manageDsaIT = connectionParameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT );
        boolean fetchSubentries = connectionParameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES );
        boolean pagedSearch = connectionParameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH );
        int pagedSearchSize = connectionParameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE );
        boolean pagedSearchScrollMode = connectionParameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE );

        return isReconnectionRequired() || countLimit != getCountLimit() || timeLimit != getTimeLimit()
            || manageDsaIT != manageDsaIT() || fetchSubentries != isFetchSubentries() || pagedSearch != isPagedSearch()
            || pagedSearchSize != getPagedSearchSize() || pagedSearchScrollMode != isPagedSearchScrollMode();
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
        boolean fetchOperationalAttributes = connectionParameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_OPERATIONAL_ATTRIBUTES );

        return fetchBaseDns != isAutoFetchBaseDns() || !StringUtils.equals( baseDn, getBaseDN() )
            || referralsHandlingMethod != getReferralsHandlingMethod()
            || aliasesDereferencingMethod != getAliasesDereferencingMethod()
            || fetchOperationalAttributes != isFetchOperationalAttributes();
    }


    /**
     * {@inheritDoc}
     */
    public void mergeParametersToLdapURL( ConnectionParameter parameter, LdapUrl ldapUrl )
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
            case FOLLOW_MANUALLY:
                // default
                break;
            case IGNORE:
                ldapUrl.getExtensions().add( new Extension( false, X_REFERRAL_HANDLING, X_REFERRAL_HANDLING_IGNORE ) );
                break;
            case FOLLOW:
                ldapUrl.getExtensions().add( new Extension( false, X_REFERRAL_HANDLING, X_REFERRAL_HANDLING_FOLLOW ) );
                break;
        }

        // ManageDsaIT control
        boolean manageDsaIt = parameter.getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT );
        if ( manageDsaIt )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_MANAGE_DSA_IT, null ) );
        }

        // fetch subentries
        boolean fetchSubentries = parameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES );
        if ( fetchSubentries )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_FETCH_SUBENTRIES, null ) );
        }

        // paged search
        boolean pagedSearch = parameter.getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH );
        if ( pagedSearch )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_PAGED_SEARCH, null ) );
            ldapUrl.getExtensions().add(
                new Extension( false, X_PAGED_SEARCH_SIZE, parameter
                    .getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE ) ) );
            boolean pagedSearchScrollMode = parameter
                .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE );
            if ( pagedSearchScrollMode )
            {
                ldapUrl.getExtensions().add( new Extension( false, X_PAGED_SEARCH_SCROLL_MODE, null ) );
            }
        }

        // fetch operational attributes
        boolean fetchOperationalAttributes = parameter
            .getExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_OPERATIONAL_ATTRIBUTES );
        if ( fetchOperationalAttributes )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_FETCH_OPERATIONAL_ATTRIBUTES, null ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void mergeLdapUrlToParameters( LdapUrl ldapUrl, ConnectionParameter parameter )
    {
        // base Dn, get from Root DSE if absent, may be empty
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

        // referral handling, FOLLOW_MANUALLY if unknown or absent
        String referral = ldapUrl.getExtensionValue( X_REFERRAL_HANDLING );
        if ( StringUtils.isNotEmpty( referral ) && X_REFERRAL_HANDLING_IGNORE.equalsIgnoreCase( referral ) )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
                Connection.ReferralHandlingMethod.IGNORE.getOrdinal() );
        }
        else if ( StringUtils.isNotEmpty( referral ) && X_REFERRAL_HANDLING_FOLLOW.equalsIgnoreCase( referral ) )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
                Connection.ReferralHandlingMethod.FOLLOW.getOrdinal() );
        }
        else
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
                Connection.ReferralHandlingMethod.FOLLOW_MANUALLY.getOrdinal() );
        }

        // ManageDsaIT control
        Extension manageDsaIT = ldapUrl.getExtension( X_MANAGE_DSA_IT );
        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, manageDsaIT != null );

        // fetch subentries
        Extension fetchSubentries = ldapUrl.getExtension( X_FETCH_SUBENTRIES );
        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES,
            fetchSubentries != null );

        // paged search
        Extension pagedSearch = ldapUrl.getExtension( X_PAGED_SEARCH );
        String pagedSearchSize = ldapUrl.getExtensionValue( X_PAGED_SEARCH_SIZE );
        Extension pagedSearchScrollMode = ldapUrl.getExtension( X_PAGED_SEARCH_SCROLL_MODE );
        if ( pagedSearch != null )
        {
            parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH,
                pagedSearch != null );
            try
            {
                parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE,
                    new Integer( pagedSearchSize ).intValue() );
            }
            catch ( NumberFormatException e )
            {
                parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE, 100 );
            }
            parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE,
                pagedSearchScrollMode != null );
        }

        // fetch operational attributes
        Extension fetchOperationalAttributes = ldapUrl.getExtension( X_FETCH_OPERATIONAL_ATTRIBUTES );
        parameter.setExtendedBoolProperty( IBrowserConnection.CONNECTION_PARAMETER_FETCH_OPERATIONAL_ATTRIBUTES,
            fetchOperationalAttributes != null );
    }
}
