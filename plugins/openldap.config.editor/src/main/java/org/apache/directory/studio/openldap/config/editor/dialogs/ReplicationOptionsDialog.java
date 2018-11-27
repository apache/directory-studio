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


import java.text.ParseException;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.FilterWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import org.apache.directory.studio.openldap.syncrepl.Interval;
import org.apache.directory.studio.openldap.syncrepl.KeepAlive;
import org.apache.directory.studio.openldap.syncrepl.Retry;
import org.apache.directory.studio.openldap.syncrepl.SchemaChecking;
import org.apache.directory.studio.openldap.syncrepl.SyncData;
import org.apache.directory.studio.openldap.syncrepl.SyncRepl;
import org.apache.directory.studio.openldap.syncrepl.Type;


/**
 * The ReplicationOptionsDialog is used to edit the replication options of a SyncRepl consumer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReplicationOptionsDialog extends Dialog
{
    /** The SyncRepl value */
    private SyncRepl syncRepl;

    /** The connection */
    private IBrowserConnection browserConnection;

    // UI widgets
    private ScrolledComposite scrolledComposite;
    private Composite composite;
    private ComboViewer replicationTypeComboViewer;
    private Spinner intervalDaysSpinner;
    private Spinner intervalHoursSpinner;
    private Spinner intervalMinutesSpinner;
    private Spinner intervalSecondsSpinner;
    private Text retryText;
    private Button editRetryButton;
    private Spinner keepAliveIdleSpinner;
    private Spinner keepAliveProbesSpinner;
    private Spinner keepAliveIntervalSpinner;
    private Text sizeLimitText;
    private Text timeLimitText;
    private Text networkTimeoutText;
    private Text timeoutText;
    private Button enableSchemaCheckingCheckbox;
    private Button enableDeltaSyncReplCheckbox;
    private ComboViewer syncDataComboViewer;
    private EntryWidget logBaseDnEntryWidget;
    private FilterWidget logFilterWidget;

    // Listeners
    private VerifyListener integerVerifyListener = event ->
        {
            if ( !event.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
            {
                event.doit = false;
            }
        };

    private SelectionListener editRetryButtonListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            // Getting the retry value
            String retryValue = null;
            
            if ( syncRepl != null )
            {
                Retry retry = syncRepl.getRetry();

                if ( retry != null )
                {
                    retryValue = retry.toString();
                }
            }

            // Creating and displaying a dialog to edit the retry value
            InputDialog dialog = new InputDialog( editRetryButton.getShell(), "Edit Retry Value", "Specify the retry value as a list of the <retry interval> and <# of retries> pairs:",
                retryValue, newText ->
                    {
                        try
                        {
                            Retry.parse( newText );
                        }
                        catch ( ParseException pe )
                        {
                            return pe.getMessage();
                        }

                        return null;
                    } );

            if ( InputDialog.OK == dialog.open() )
            {
                try
                {
                    syncRepl.setRetry( Retry.parse( dialog.getValue() ) );
                }
                catch ( ParseException e1 )
                {
                    syncRepl.setRetry( null );
                }

                retryText.setText( getRetryValue() );
            }
        }
    };

    private SelectionListener enableDeltaSyncReplCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            boolean isChecked = enableDeltaSyncReplCheckbox.getSelection();

            syncDataComboViewer.getControl().setEnabled( isChecked );
            logBaseDnEntryWidget.setEnabled( isChecked );
            logFilterWidget.setEnabled( isChecked );
        }
    };


    /**
     * Creates a new instance of OverlayDialog.
     * 
     * @param parentShell the parent shell
     * @param index the index
     * @param browserConnection the connection
     */
    public ReplicationOptionsDialog( Shell parentShell, SyncRepl syncRepl, IBrowserConnection browserConnection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.browserConnection = browserConnection;

        if ( syncRepl != null )
        {
            this.syncRepl = syncRepl.copy();
        }
        else
        {
            this.syncRepl = createDefaultSyncRepl();
        }
    }


    /**
     * Creates a default SyncRepl configuration.
     *
     * @return a default SyncRepl configuration
     */
    private SyncRepl createDefaultSyncRepl()
    {
        return new SyncRepl();
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Replication Options" );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void okPressed()
    {
        saveToSyncRepl();

        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent )
    {
        // Creating the scrolled composite
        scrolledComposite = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        scrolledComposite.setExpandHorizontal( true );
        scrolledComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Creating the composite and attaching it to the scrolled composite
        composite = new Composite( scrolledComposite, SWT.NONE );
        composite.setLayout( new GridLayout() );
        scrolledComposite.setContent( composite );

        createReplicationConsumerGroup( composite );
        createDeltaSyncReplGroup( composite );

        initFromSyncRepl();

        applyDialogFont( scrolledComposite );
        composite.setSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

        return scrolledComposite;
    }


    /**
     * Creates the replication consumer group.
     *
     * @param parent the parent composite
     */
    private void createReplicationConsumerGroup( Composite parent )
    {
        // Replication Provider Group
        Group group = BaseWidgetUtils.createGroup( parent, "Replication Consumer", 1 );
        group.setLayout( new GridLayout( 3, false ) );
        group.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Replication Type
        BaseWidgetUtils.createLabel( group, "Replication Type:", 1 );
        replicationTypeComboViewer = new ComboViewer( group );
        replicationTypeComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        replicationTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        replicationTypeComboViewer.setLabelProvider( new LabelProvider()
        {
            @Override
            public String getText( Object element )
            {
                if ( element instanceof Type )
                {
                    Type type = ( Type ) element;

                    switch ( type )
                    {
                        case REFRESH_AND_PERSIST:
                            return "Refresh And Persist";
                        case REFRESH_ONLY:
                            return "Refresh Only";
                    }
                }

                return super.getText( element );
            }
        } );
        replicationTypeComboViewer.setInput( new Type[]
            { Type.REFRESH_AND_PERSIST, Type.REFRESH_ONLY } );

        // Interval
        createIntervalComposites( group );

        // Retry
        BaseWidgetUtils.createLabel( group, "Retry:", 1 );
        retryText = BaseWidgetUtils.createReadonlyText( group, "", 1 );
        retryText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        editRetryButton = BaseWidgetUtils.createButton( group, "Edit...", 1 );
        editRetryButton.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 1, 1 ) );

        // Keep Alive
        createKeepAliveComposites( group );

        // Size Limit
        BaseWidgetUtils.createLabel( group, "Size Limit:", 1 );
        sizeLimitText = BaseWidgetUtils.createText( group, "", 1 );
        sizeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Time Limit
        BaseWidgetUtils.createLabel( group, "Time Limit:", 1 );
        timeLimitText = BaseWidgetUtils.createText( group, "", 1 );
        timeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Network Timeout
        BaseWidgetUtils.createLabel( group, "Network Timeout:", 1 );
        networkTimeoutText = BaseWidgetUtils.createText( group, "", 1 );
        networkTimeoutText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Timeout
        BaseWidgetUtils.createLabel( group, "Timeout:", 1 );
        timeoutText = BaseWidgetUtils.createText( group, "", 1 );
        timeoutText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Schema Checking
        enableSchemaCheckingCheckbox = BaseWidgetUtils.createCheckbox( group, "Enable Schema Checking", 2 );
    }


    private void createIntervalComposites( Composite parent )
    {
        // Interval Label Composite
        Composite intervalLabelComposite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        intervalLabelComposite.setLayoutData( new GridData() );

        // Interval Label
        BaseWidgetUtils.createLabel( intervalLabelComposite, "Interval:", 1 );
        BaseWidgetUtils.createLabel( intervalLabelComposite, "", 1 );

        // Interval Composite
        Composite intervalComposite = BaseWidgetUtils.createColumnContainer( parent, 2, 2 );
        GridLayout intervalCompositeGridLayout = new GridLayout( 4, false );
        intervalCompositeGridLayout.marginWidth = intervalCompositeGridLayout.marginHeight = 0;
        intervalCompositeGridLayout.verticalSpacing = 0;
        intervalComposite.setLayout( intervalCompositeGridLayout );

        // Interval Days Spinner
        intervalDaysSpinner = new Spinner( intervalComposite, SWT.BORDER );
        intervalDaysSpinner.setMinimum( 0 );
        intervalDaysSpinner.setMaximum( 99999 );

        // Interval Hours Spinner
        intervalHoursSpinner = new Spinner( intervalComposite, SWT.BORDER );
        intervalHoursSpinner.setMinimum( 0 );
        intervalHoursSpinner.setMaximum( 23 );

        // Interval Minutes Spinner
        intervalMinutesSpinner = new Spinner( intervalComposite, SWT.BORDER );
        intervalMinutesSpinner.setMinimum( 0 );
        intervalMinutesSpinner.setMaximum( 59 );

        // Interval Seconds Spinner
        intervalSecondsSpinner = new Spinner( intervalComposite, SWT.BORDER );
        intervalSecondsSpinner.setMinimum( 0 );
        intervalSecondsSpinner.setMaximum( 59 );

        // Days Hours Minutes Seconds Labels
        BaseWidgetUtils.createLabel( intervalComposite, "Days", 1 );
        BaseWidgetUtils.createLabel( intervalComposite, "Hours", 1 );
        BaseWidgetUtils.createLabel( intervalComposite, "Minutes", 1 );
        BaseWidgetUtils.createLabel( intervalComposite, "Seconds", 1 );
    }


    private void createKeepAliveComposites( Composite parent )
    {
        // Keep Alive Label Composite
        Composite keepAliveLabelComposite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        keepAliveLabelComposite.setLayoutData( new GridData() );

        // Keep Alive Label
        BaseWidgetUtils.createLabel( keepAliveLabelComposite, "Keep Alive:", 1 );
        BaseWidgetUtils.createLabel( keepAliveLabelComposite, "", 1 );

        // Keep Alive Composite
        Composite keepAliveComposite = BaseWidgetUtils.createColumnContainer( parent, 2, 2 );
        GridLayout keepAliveCompositeGridLayout = new GridLayout( 3, false );
        keepAliveCompositeGridLayout.marginWidth = keepAliveCompositeGridLayout.marginHeight = 0;
        keepAliveCompositeGridLayout.verticalSpacing = 0;
        keepAliveComposite.setLayout( keepAliveCompositeGridLayout );

        // Keep Alive Idle Spinner
        keepAliveIdleSpinner = new Spinner( keepAliveComposite, SWT.BORDER );
        keepAliveIdleSpinner.setMinimum( 0 );
        keepAliveIdleSpinner.setMaximum( 99999 );

        // Keep Alive Probes Spinner
        keepAliveProbesSpinner = new Spinner( keepAliveComposite, SWT.BORDER );
        keepAliveProbesSpinner.setMinimum( 0 );
        keepAliveProbesSpinner.setMaximum( 99999 );

        // Keep Alive Interval Spinner
        keepAliveIntervalSpinner = new Spinner( keepAliveComposite, SWT.BORDER );
        keepAliveIntervalSpinner.setMinimum( 0 );
        keepAliveIntervalSpinner.setMaximum( 99999 );

        // Idle Probes Interval Labels
        BaseWidgetUtils.createLabel( keepAliveComposite, "Idle", 1 );
        BaseWidgetUtils.createLabel( keepAliveComposite, "Probes", 1 );
        BaseWidgetUtils.createLabel( keepAliveComposite, "Interval", 1 );
    }


    private void createDeltaSyncReplGroup( Composite parent )
    {
        // Replication Provider Group
        Group group = BaseWidgetUtils.createGroup( parent, "Delta SyncRepl Configuration", 1 );
        group.setLayout( new GridLayout( 3, false ) );
        group.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Enable Delta SyncRepl Checkbox
        enableDeltaSyncReplCheckbox = BaseWidgetUtils.createCheckbox( group, "Enable Delta SyncRepl", 3 );

        // Sync Data Combo Viewer
        BaseWidgetUtils.createLabel( group, "Sync Data:", 1 );
        syncDataComboViewer = new ComboViewer( group );
        syncDataComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        syncDataComboViewer.setContentProvider( new ArrayContentProvider() );
        syncDataComboViewer.setLabelProvider( new LabelProvider()
        {
            @Override
            public String getText( Object element )
            {
                if ( element instanceof SyncData )
                {
                    SyncData syncData = ( SyncData ) element;

                    switch ( syncData )
                    {
                        case ACCESSLOG:
                            return "Access Log";
                        case CHANGELOG:
                            return "Change Log";
                        case DEFAULT:
                            return "Default";
                    }
                }

                return super.getText( element );
            }
        } );
        syncDataComboViewer.setInput( new SyncData[]
            { SyncData.ACCESSLOG, SyncData.CHANGELOG } );

        // Log Base DN Text
        BaseWidgetUtils.createLabel( group, "Log Base DN:", 1 );
        logBaseDnEntryWidget = new EntryWidget( browserConnection, Dn.EMPTY_DN );
        logBaseDnEntryWidget.createWidget( group );

        // Log Filter Text
        BaseWidgetUtils.createLabel( group, "Log Filter:", 1 );
        logFilterWidget = new FilterWidget();
        logFilterWidget.setBrowserConnection( browserConnection );
        logFilterWidget.createWidget( group );
    }


    /**
     * Initializes the dialog using the SyncRepl object.
     */
    private void initFromSyncRepl()
    {
        if ( syncRepl != null )
        {
            // Replication Type
            Type replicationType = syncRepl.getType();

            if ( replicationType != null )
            {
                replicationTypeComboViewer.setSelection( new StructuredSelection( replicationType ) );
            }
            else
            {
                replicationTypeComboViewer.setSelection( new StructuredSelection( Type.REFRESH_AND_PERSIST ) );
            }

            // Interval
            Interval interval = syncRepl.getInterval();

            if ( interval != null )
            {
                intervalDaysSpinner.setSelection( interval.getDays() );
                intervalHoursSpinner.setSelection( interval.getHours() );
                intervalMinutesSpinner.setSelection( interval.getMinutes() );
                intervalSecondsSpinner.setSelection( interval.getSeconds() );
            }

            // Retry
            retryText.setText( getRetryValue() );

            // Keep Alive
            KeepAlive keepAlive = syncRepl.getKeepAlive();

            if ( keepAlive != null )
            {
                keepAliveIdleSpinner.setSelection( keepAlive.getIdle() );
                keepAliveProbesSpinner.setSelection( keepAlive.getProbes() );
                keepAliveIntervalSpinner.setSelection( keepAlive.getInterval() );
            }

            // Size Limit
            int sizeLimit = syncRepl.getSizeLimit();

            if ( sizeLimit != -1 )
            {
                sizeLimitText.setText( Integer.toString( sizeLimit ) );
            }

            // Time Limit
            int timeLimit = syncRepl.getTimeLimit();

            if ( timeLimit != -1 )
            {
                timeLimitText.setText( Integer.toString( timeLimit ) );
            }

            // Network Timeout
            int networkTimeout = syncRepl.getNetworkTimeout();

            if ( networkTimeout != -1 )
            {
                networkTimeoutText.setText( Integer.toString( networkTimeout ) );
            }

            // Timeout
            int timeout = syncRepl.getTimeout();

            if ( timeout != -1 )
            {
                timeoutText.setText( Integer.toString( timeout ) );
            }

            // Enable Schema Checking
            SchemaChecking schemaChecking = syncRepl.getSchemaChecking();

            if ( schemaChecking != null )
            {
                enableSchemaCheckingCheckbox.setSelection( schemaChecking == SchemaChecking.ON );
            }

            // Sync Data
            SyncData syncData = syncRepl.getSyncData();

            if ( syncData != null && ( ( syncData == SyncData.ACCESSLOG ) || ( syncData == SyncData.CHANGELOG ) ) )
            {
                enableDeltaSyncReplCheckbox.setSelection( true );
                syncDataComboViewer.setSelection( new StructuredSelection( syncData ) );
            }
            else
            {
                syncDataComboViewer.setSelection( new StructuredSelection( SyncData.ACCESSLOG ) );
                syncDataComboViewer.getControl().setEnabled( false );
                logBaseDnEntryWidget.setEnabled( false );
                logFilterWidget.setEnabled( false );
            }

            // Log Base DN
            String logBaseDn = syncRepl.getLogBase();

            if ( logBaseDn != null )
            {
                try
                {
                    logBaseDnEntryWidget.setInput( browserConnection, new Dn( logBaseDn ) );
                }
                catch ( LdapInvalidDnException e )
                {
                    // Silent
                }
            }

            // Log Filter
            String logFilter = syncRepl.getLogFilter();

            if ( logFilter != null )
            {
                logFilterWidget.setFilter( logFilter );
            }

            addListeners();
        }
    }


    /**
     * Gets the retry value.
     *
     * @return the retry value
     */
    private String getRetryValue()
    {
        if ( syncRepl != null )
        {

            Retry retry = syncRepl.getRetry();

            if ( retry != null )
            {
                return retry.toString();
            }
        }
        return "(none)";
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        editRetryButton.addSelectionListener( editRetryButtonListener );
        sizeLimitText.addVerifyListener( integerVerifyListener );
        timeLimitText.addVerifyListener( integerVerifyListener );
        networkTimeoutText.addVerifyListener( integerVerifyListener );
        timeoutText.addVerifyListener( integerVerifyListener );
        enableDeltaSyncReplCheckbox.addSelectionListener( enableDeltaSyncReplCheckboxListener );
    }


    /**
     * Saves the content of the dialog to the SyncRepl object.
     */
    private void saveToSyncRepl()
    {
        if ( syncRepl != null )
        {
            // Replication Type
            syncRepl.setType( getReplicationType() );

            // Interval
            syncRepl.setInterval( getInterval() );

            // Retry
            // TODO

            // Keep Alive
            syncRepl.setKeepAlive( getKeepAlive() );

            // Size Limit
            String sizeLimitString = sizeLimitText.getText();

            if ( ( sizeLimitString != null ) && ( !"".equals( sizeLimitString ) ) )
            {
                try
                {
                    int sizeLimit = Integer.parseInt( sizeLimitString );
                    syncRepl.setSizeLimit( sizeLimit );
                }
                catch ( NumberFormatException e )
                {
                    // Silent (will never happen)
                }
            }
            else
            {
                syncRepl.setSizeLimit( -1 );
            }

            // Time Limit
            String timeLimitString = timeLimitText.getText();

            if ( ( timeLimitString != null ) && ( !"".equals( timeLimitString ) ) )
            {
                try
                {
                    int timeLimit = Integer.parseInt( timeLimitString );
                    syncRepl.setTimeLimit( timeLimit );
                }
                catch ( NumberFormatException e )
                {
                    // Silent (will never happen)
                }
            }
            else
            {
                syncRepl.setTimeLimit( -1 );
            }

            // Network Timeout
            String networkTimeoutString = networkTimeoutText.getText();

            if ( ( networkTimeoutString != null ) && ( !"".equals( networkTimeoutString ) ) )
            {
                try
                {
                    int networkTimeout = Integer.parseInt( networkTimeoutString );
                    syncRepl.setNetworkTimeout( networkTimeout );
                }
                catch ( NumberFormatException e )
                {
                    // Silent (will never happen)
                }
            }
            else
            {
                syncRepl.setNetworkTimeout( -1 );
            }

            // Timeout
            String timeoutString = timeoutText.getText();

            if ( ( timeoutString != null ) && ( !"".equals( timeoutString ) ) )
            {
                try
                {
                    int timeout = Integer.parseInt( timeoutString );
                    syncRepl.setTimeout( timeout );
                }
                catch ( NumberFormatException e )
                {
                    // Silent (will never happen)
                }
            }
            else
            {
                syncRepl.setTimeout( -1 );
            }

            // Enable Schema Checking
            syncRepl.setSchemaChecking( getSchemaChecking() );

            // Sync Data
            syncRepl.setSyncData( getSyncData() );

            // Log Base DN
            Dn logBaseDn = logBaseDnEntryWidget.getDn();

            if ( ( logBaseDn != null ) && ( !Dn.EMPTY_DN.equals( logBaseDn ) ) )
            {
                syncRepl.setLogBase( logBaseDn.getName() );
            }
            else
            {
                syncRepl.setLogBase( null );
            }

            // Log Filter
            String logFilter = logFilterWidget.getFilter();

            if ( ( logBaseDn != null ) && ( !"".equals( logFilter ) ) )
            {
                syncRepl.setLogFilter( logFilter );
            }
            else
            {
                syncRepl.setLogFilter( null );
            }
        }
    }


    /**
     * Gets the replication type.
     *
     * @return the replication type
     */
    private Type getReplicationType()
    {
        StructuredSelection selection = ( StructuredSelection ) replicationTypeComboViewer.getSelection();

        if ( ( selection != null ) && ( !selection.isEmpty() ) )
        {
            return ( Type ) selection.getFirstElement();
        }

        return null;
    }


    /**
     * Gets the interval.
     *
     * @return the interval
     */
    private Interval getInterval()
    {
        int days = intervalDaysSpinner.getSelection();
        int hours = intervalHoursSpinner.getSelection();
        int minutes = intervalMinutesSpinner.getSelection();
        int seconds = intervalSecondsSpinner.getSelection();

        if ( ( days != 0 ) || ( hours != 0 ) || ( minutes != 0 ) || ( seconds != 0 ) )
        {
            return new Interval( days, hours, minutes, seconds );
        }

        return null;
    }


    /**
     * Gets the keep alive.
     *
     * @return the keep alive
     */
    private KeepAlive getKeepAlive()
    {
        int idle = keepAliveIdleSpinner.getSelection();
        int probes = keepAliveProbesSpinner.getSelection();
        int interval = keepAliveIntervalSpinner.getSelection();

        if ( ( idle != 0 ) || ( probes != 0 ) || ( interval != 0 ) )
        {
            return new KeepAlive( idle, probes, interval );
        }

        return null;
    }


    /**
     * Gets the schema checking.
     *
     * @return the schema checking
     */
    private SchemaChecking getSchemaChecking()
    {
        if ( enableSchemaCheckingCheckbox.getSelection() )
        {
            return SchemaChecking.ON;
        }
        else
        {
            return SchemaChecking.OFF;
        }
    }


    /**
     * Gets the sync data.
     *
     * @return the sync data
     */
    private SyncData getSyncData()
    {
        if ( enableDeltaSyncReplCheckbox.getSelection() )
        {
            StructuredSelection selection = ( StructuredSelection ) syncDataComboViewer.getSelection();

            if ( ( selection != null ) && ( !selection.isEmpty() ) )
            {
                return ( SyncData ) selection.getFirstElement();
            }
        }

        return null;
    }


    /**
     * Gets the SyncRepl value.
     *
     * @return the SyncRepl value
     */
    public SyncRepl getSyncRepl()
    {
        return syncRepl;
    }
}
