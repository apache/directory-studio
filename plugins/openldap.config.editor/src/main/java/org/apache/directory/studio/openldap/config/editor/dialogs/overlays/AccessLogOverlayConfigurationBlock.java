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
package org.apache.directory.studio.openldap.config.editor.dialogs.overlays;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.FilterWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.apache.directory.studio.openldap.common.ui.dialogs.AttributeDialog;
import org.apache.directory.studio.openldap.common.ui.model.LogOperation;
import org.apache.directory.studio.openldap.common.ui.widgets.EntryWidget;
import org.apache.directory.studio.openldap.common.ui.widgets.LogOperationsWidget;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.editor.dialogs.AbstractOverlayDialogConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.editor.dialogs.PurgeTimeSpan;
import org.apache.directory.studio.openldap.config.model.overlay.OlcAccessLogConfig;


/**
 * This class implements a block for the configuration of the Access Log overlay.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AccessLogOverlayConfigurationBlock extends AbstractOverlayDialogConfigurationBlock<OlcAccessLogConfig>
{
    /** The attributes list */
    private List<String> attributes = new ArrayList<String>();

    // UI widgets
    private EntryWidget databaseEntryWidget;
    private Button onlyLogSuccessfulRequestsCheckbox;
    private LogOperationsWidget logOperationsWidget;
    private TableViewer attributesTableViewer;
    private Button addAttributeButton;
    private Button deleteAttributeButton;
    private FilterWidget filterWidget;
    private Spinner purgeAgeDaysSpinner;
    private Spinner purgeAgeHoursSpinner;
    private Spinner purgeAgeMinutesSpinner;
    private Spinner purgeAgeSecondsSpinner;
    private Spinner purgeIntervalDaysSpinner;
    private Spinner purgeIntervalHoursSpinner;
    private Spinner purgeIntervalMinutesSpinner;
    private Spinner purgeIntervalSecondsSpinner;

    // Listeners
    private ISelectionChangedListener attributesTableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            deleteAttributeButton.setEnabled( !attributesTableViewer.getSelection().isEmpty() );
        }
    };
    private SelectionListener addAttributeButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeDialog dialog = new AttributeDialog( addAttributeButton.getShell(), browserConnection );
            if ( dialog.open() == AttributeDialog.OK )
            {
                String attribute = dialog.getAttribute();

                if ( !attributes.contains( attribute ) )
                {
                    attributes.add( attribute );
                    attributesTableViewer.refresh();
                    attributesTableViewer.setSelection( new StructuredSelection( attribute ) );
                }
            }
        }
    };
    private SelectionListener deleteAttributeButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) attributesTableViewer.getSelection();

            if ( !selection.isEmpty() )
            {
                String selectedAttribute = ( String ) selection.getFirstElement();

                attributes.remove( selectedAttribute );
                attributesTableViewer.refresh();
            }
        }
    };


    /**
     * Creates a new instance of AccessLogOverlayConfigurationBlock.
     *
     * @param dialog the overlay dialog
     * @param browserConnection the browser connection
     */
    public AccessLogOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection browserConnection )
    {
        super( dialog, browserConnection );
        setOverlay( new OlcAccessLogConfig() );
    }


    /**
     * Creates a new instance of AccessLogOverlayConfigurationBlock.
     *
     * @param dialog the overlay dialog
     * @param browserConnection the browser connection
     * @param overlay the access log overlay
     */
    public AccessLogOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection browserConnection,
        OlcAccessLogConfig overlay )
    {
        super( dialog, browserConnection );

        if ( overlay == null )
        {
            overlay = new OlcAccessLogConfig();
        }

        setOverlay( overlay );
    }


    /**
     * {@inheritDoc}
     */
    public void createBlockContent( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        // Database
        BaseWidgetUtils.createLabel( composite, "Database:", 1 );
        databaseEntryWidget = new EntryWidget( getDialog().getBrowserConnection() );
        databaseEntryWidget.createWidget( composite );
        databaseEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Successful requests
        onlyLogSuccessfulRequestsCheckbox = BaseWidgetUtils.createCheckbox( composite, "Only log successful requests",
            3 );

        createLogOperationsGroup( composite );
        createAttributesGroup( composite );
        createFilterGroup( composite );
        createPurgeGroup( composite );
    }


    /**
     * Creates the log operations group.
     *
     * @param parent the parent composite
     */
    private void createLogOperationsGroup( Composite parent )
    {
        // Log Operations Group
        Group logOperationsGroup = BaseWidgetUtils.createGroup( parent, "Log Operations", 3 );

        // Log Operations Widget
        logOperationsWidget = new LogOperationsWidget();
        logOperationsWidget.create( logOperationsGroup );
        logOperationsWidget.getControl().setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );
        List<LogOperation> logOperations = new ArrayList<LogOperation>();
        logOperations.add( LogOperation.ADD );
        logOperations.add( LogOperation.ABANDON );
        logOperations.add( LogOperation.SESSION );
        logOperationsWidget.setInput( logOperations );
    }


    /**
     * Creates the attributes group.
     *
     * @param parent the parent composite
     */
    private void createAttributesGroup( Composite parent )
    {
        // Attributes Group
        Group attributesGroup = BaseWidgetUtils.createGroup( parent, "Attributes", 3 );
        GridLayout attributesCompositeGridLayout = new GridLayout( 2, false );
        attributesCompositeGridLayout.verticalSpacing = 0;
        attributesGroup.setLayout( attributesCompositeGridLayout );

        // Attributes TableViewer
        attributesTableViewer = new TableViewer( attributesGroup );
        GridData tableViewerGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        tableViewerGridData.heightHint = 20;
        tableViewerGridData.widthHint = 100;
        attributesTableViewer.getControl().setLayoutData( tableViewerGridData );
        attributesTableViewer.setContentProvider( new ArrayContentProvider() );
        attributesTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_ATTRIBUTE );
            }
        } );
        attributesTableViewer.setInput( attributes );
        attributesTableViewer.addSelectionChangedListener( attributesTableViewerSelectionChangedListener );

        // Attribute Add Button
        addAttributeButton = BaseWidgetUtils.createButton( attributesGroup, "Add...", 1 );
        addAttributeButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        addAttributeButton.addSelectionListener( addAttributeButtonSelectionListener );

        // Attribute Delete Button
        deleteAttributeButton = BaseWidgetUtils.createButton( attributesGroup, "Delete", 1 );
        deleteAttributeButton.setEnabled( false );
        deleteAttributeButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        deleteAttributeButton.addSelectionListener( deleteAttributeButtonSelectionListener );
    }


    /**
     * Creates the filter group.
     *
     * @param parent the parent composite
     */
    private void createFilterGroup( Composite parent )
    {
        // Filter Group
        Group filterGroup = BaseWidgetUtils.createGroup( parent, "Filter", 3 );
        GridLayout filterGroupGridLayout = new GridLayout( 2, false );
        filterGroupGridLayout.marginHeight = 0;
        filterGroupGridLayout.verticalSpacing = 0;
        filterGroup.setLayout( filterGroupGridLayout );

        filterWidget = new FilterWidget();
        filterWidget.setFilter( "" );
        filterWidget.setBrowserConnection( getDialog().getBrowserConnection() );
        filterWidget.createWidget( filterGroup );
    }


    /**
     * Creates the purge group.
     *
     * @param parent the parent composite
     */
    private void createPurgeGroup( Composite parent )
    {
        // Purge Group
        Group purgeGroup = BaseWidgetUtils.createGroup( parent, "Log Purge", 3 );
        GridLayout purgeCompositeGridLayout = new GridLayout( 5, false );
        purgeCompositeGridLayout.verticalSpacing = 0;
        purgeGroup.setLayout( purgeCompositeGridLayout );

        // Age Label
        BaseWidgetUtils.createLabel( purgeGroup, "Age:", 1 );

        // Age Days Spinner
        purgeAgeDaysSpinner = new Spinner( purgeGroup, SWT.BORDER );
        purgeAgeDaysSpinner.setMinimum( 0 );
        purgeAgeDaysSpinner.setMaximum( 99999 );

        // Age Hours Spinner
        purgeAgeHoursSpinner = new Spinner( purgeGroup, SWT.BORDER );
        purgeAgeHoursSpinner.setMinimum( 0 );
        purgeAgeHoursSpinner.setMaximum( 23 );

        // Age Minutes Spinner
        purgeAgeMinutesSpinner = new Spinner( purgeGroup, SWT.BORDER );
        purgeAgeMinutesSpinner.setMinimum( 0 );
        purgeAgeMinutesSpinner.setMaximum( 59 );

        // Age Seconds Spinner
        purgeAgeSecondsSpinner = new Spinner( purgeGroup, SWT.BORDER );
        purgeAgeSecondsSpinner.setMinimum( 0 );
        purgeAgeSecondsSpinner.setMaximum( 59 );

        // Interval Label
        BaseWidgetUtils.createLabel( purgeGroup, "Interval:", 1 );

        // Interval Days Spinner
        purgeIntervalDaysSpinner = new Spinner( purgeGroup, SWT.BORDER );
        purgeIntervalDaysSpinner.setMinimum( 0 );
        purgeIntervalDaysSpinner.setMaximum( 99999 );

        // Interval Hours Spinner
        purgeIntervalHoursSpinner = new Spinner( purgeGroup, SWT.BORDER );
        purgeIntervalHoursSpinner.setMinimum( 0 );
        purgeIntervalHoursSpinner.setMaximum( 23 );

        // Interval Minutes Spinner
        purgeIntervalMinutesSpinner = new Spinner( purgeGroup, SWT.BORDER );
        purgeIntervalMinutesSpinner.setMinimum( 0 );
        purgeIntervalMinutesSpinner.setMaximum( 59 );

        // Interval Seconds Spinner
        purgeIntervalSecondsSpinner = new Spinner( purgeGroup, SWT.BORDER );
        purgeIntervalSecondsSpinner.setMinimum( 0 );
        purgeIntervalSecondsSpinner.setMaximum( 59 );

        // Days Hours Minutes Seconds Labels
        BaseWidgetUtils.createSpacer( purgeGroup, 1 );
        BaseWidgetUtils.createLabel( purgeGroup, "Days", 1 );
        BaseWidgetUtils.createLabel( purgeGroup, "Hours", 1 );
        BaseWidgetUtils.createLabel( purgeGroup, "Minutes", 1 );
        BaseWidgetUtils.createLabel( purgeGroup, "Seconds", 1 );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        if ( overlay != null )
        {
            // Database
            databaseEntryWidget.setInput( overlay.getOlcAccessLogDB() );

            //
            // Only log successful requests
            //
            Boolean onlyLogSuccessfulRequests = overlay.getOlcAccessLogSuccess();

            if ( onlyLogSuccessfulRequests != null )
            {
                onlyLogSuccessfulRequestsCheckbox.setSelection( onlyLogSuccessfulRequests.booleanValue() );
            }
            else
            {
                onlyLogSuccessfulRequestsCheckbox.setSelection( false );
            }

            //
            // Log operations
            //
            List<String> logOperationsValues = overlay.getOlcAccessLogOps();

            if ( ( logOperationsValues != null ) && ( logOperationsValues.size() > 0 ) )
            {
                logOperationsWidget.setInput( getAccessLogOperations( logOperationsValues ) );
            }
            else
            {
                logOperationsWidget.setInput( null );
            }

            //
            // Attributes
            //
            List<String> attributeValues = overlay.getOlcAccessLogOldAttr();

            if ( ( attributeValues != null ) && ( attributeValues.size() > 0 ) )
            {
                for ( String attribute : attributeValues )
                {
                    attributes.add( attribute );
                }
            }

            attributesTableViewer.refresh();

            //
            // Filter
            //
            String filter = overlay.getOlcAccessLogOld();

            if ( filter != null )
            {
                filterWidget.setFilter( filter );
            }

            //
            // Purge
            //
            String accessLogPurge = overlay.getOlcAccessLogPurge();

            if ( !Strings.isEmpty( accessLogPurge ) )
            {
                // Splitting age and interval purge time spans
                String[] accessLogPurgeValues = accessLogPurge.split( " " );

                // Checking if we got the appropriate number of members
                if ( accessLogPurgeValues.length == 2 )
                {
                    try
                    {
                        // Purge age time span
                        PurgeTimeSpan purgeAgeTimeSpan = new PurgeTimeSpan( accessLogPurgeValues[0] );
                        purgeAgeDaysSpinner.setSelection( purgeAgeTimeSpan.getDays() );
                        purgeAgeHoursSpinner.setSelection( purgeAgeTimeSpan.getHours() );
                        purgeAgeMinutesSpinner.setSelection( purgeAgeTimeSpan.getMinutes() );
                        purgeAgeSecondsSpinner.setSelection( purgeAgeTimeSpan.getSeconds() );

                        // Purge interval time span
                        PurgeTimeSpan purgeIntervalTimeSpan = new PurgeTimeSpan( accessLogPurgeValues[1] );
                        purgeIntervalDaysSpinner.setSelection( purgeIntervalTimeSpan.getDays() );
                        purgeIntervalHoursSpinner.setSelection( purgeIntervalTimeSpan.getHours() );
                        purgeIntervalMinutesSpinner.setSelection( purgeIntervalTimeSpan.getMinutes() );
                        purgeIntervalSecondsSpinner.setSelection( purgeIntervalTimeSpan.getSeconds() );
                    }
                    catch ( ParseException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else
                {
                    // We didn't have 2 members in the string
                    // TODO error
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void save()
    {
        if ( overlay != null )
        {
            // Database
            overlay.setOlcAccessLogDB( databaseEntryWidget.getDn() );

            // Only log successful requests
            overlay.setOlcAccessLogSuccess( onlyLogSuccessfulRequestsCheckbox.getSelection() );

            // Log operations
            overlay.setOlcAccessLogOps( getAccessLogOperationsValues() );

            // Attributes
            overlay.setOlcAccessLogOldAttr( attributes );

            // Filter
            overlay.setOlcAccessLogOld( filterWidget.getFilter() );

            // Purge
            overlay.setOlcAccessLogPurge( getPurgeValue() );
        }

        // Saving dialog settings
        databaseEntryWidget.saveDialogSettings();
        filterWidget.saveDialogSettings();
    }


    /**
     * Gets the access log operations values.
     *
     * @return the access log operations values
     */
    private List<String> getAccessLogOperationsValues()
    {
        List<String> accessLogOperations = new ArrayList<String>();
        List<LogOperation> logOperations = logOperationsWidget.getSelectedOperationsList();

        for ( LogOperation logOperation : logOperations )
        {
            // Converting log operation to string
            accessLogOperations.add( logOperation.toString() );
        }

        return accessLogOperations;
    }


    /**
     * Gets the access log operations.
     *
     * @return the access log operations
     */
    private List<LogOperation> getAccessLogOperations( List<String> logOperationsValues )
    {
        List<LogOperation> logOperations = new ArrayList<LogOperation>();

        for ( String logOperationValue : logOperationsValues )
        {
            // Converting log operation from a string
            LogOperation logOperation = LogOperation.fromString( logOperationValue );

            if ( logOperation != null )
            {
                logOperations.add( logOperation );
            }
        }

        return logOperations;
    }


    /**
     * Gets the purge value.
     *
     * @return the purge value
     */
    private String getPurgeValue()
    {
        return getPurgeAgeTimeSpan().toString() + " " + getPurgeIntervalTimeSpan().toString();
    }


    /**
     * Gets the purge age time span.
     *
     * @return the purge age time span
     */
    private PurgeTimeSpan getPurgeAgeTimeSpan()
    {
        PurgeTimeSpan purgeAgeTimeSpan = new PurgeTimeSpan();

        purgeAgeTimeSpan.setDays( purgeAgeDaysSpinner.getSelection() );
        purgeAgeTimeSpan.setHours( purgeAgeHoursSpinner.getSelection() );
        purgeAgeTimeSpan.setMinutes( purgeAgeMinutesSpinner.getSelection() );
        purgeAgeTimeSpan.setSeconds( purgeAgeSecondsSpinner.getSelection() );

        return purgeAgeTimeSpan;
    }


    /**
     * Gets the purge interval time span.
     *
     * @return the purge interval time span
     */
    private PurgeTimeSpan getPurgeIntervalTimeSpan()
    {
        PurgeTimeSpan purgeInternalTimeSpan = new PurgeTimeSpan();

        purgeInternalTimeSpan.setDays( purgeIntervalDaysSpinner.getSelection() );
        purgeInternalTimeSpan.setHours( purgeIntervalHoursSpinner.getSelection() );
        purgeInternalTimeSpan.setMinutes( purgeIntervalMinutesSpinner.getSelection() );
        purgeInternalTimeSpan.setSeconds( purgeIntervalSecondsSpinner.getSelection() );

        return purgeInternalTimeSpan;
    }
}
