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

package org.apache.directory.studio.valueeditors.time;


import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.directory.shared.ldap.util.GeneralizedTime;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


/**
 * This class provides a dialog to define a generalized time.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class GeneralizedTimeValueDialog extends Dialog
{
    /** The value */
    private GeneralizedTime value;

    private List<GeneralizedTimeTimeZones> timezonesList = null;

    private Map<Integer, GeneralizedTimeTimeZones> timezonesMap = null;

    //
    // UI Fields
    //

    // Time
    private Spinner hoursSpinner;
    private Spinner minutesSpinner;
    private Spinner secondsSpinner;

    // Date
    private DateTime dateCalendar;

    // Time zone
    private ComboViewer timezoneComboViewer;

    // Raw value
    private Text rawText;

    // Raw validator
    private Label rawValidatorImage;

    /** The OK button of the dialog */
    private Button okButton;

    //
    // Listeners
    //

    private ModifyListener hoursModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            updateValueFromNonRawFields();

            removeListeners();
            updateRawFields();
            addListeners();
        }
    };

    private ModifyListener minutesModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            updateValueFromNonRawFields();

            removeListeners();
            updateRawFields();
            addListeners();
        }
    };

    private ModifyListener secondsModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            updateValueFromNonRawFields();

            removeListeners();
            updateRawFields();
            addListeners();
        }
    };

    private SelectionListener dateSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            updateValueFromNonRawFields();

            removeListeners();
            updateRawFields();
            addListeners();
        }
    };

    private ISelectionChangedListener timezoneSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            updateValueFromNonRawFields();

            removeListeners();
            updateRawFields();
            addListeners();
        }
    };

    private ModifyListener rawModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            try
            {
                value = new GeneralizedTime( rawText.getText() );

                removeListeners();
                updateNonRawFields();
                addListeners();

                validateRawValue( true );
            }
            catch ( ParseException e1 )
            {
                validateRawValue( false );

                return;
            }
        }
    };


    /**
     * Creates a new instance of GeneralizedTimeValueDialog.
     * 
     * @param parentShell
     *      the parent shell
     * @param value
     *      the initial value
     */
    public GeneralizedTimeValueDialog( Shell parentShell, GeneralizedTime value )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.value = value;

        // If the initial value is null, we take the current date/time
        if ( this.value == null )
        {
            this.value = new GeneralizedTime( Calendar.getInstance() );
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Generalized Time Editor" );
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_GENERALIZEDTIMEEDITOR ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        // Main composites
        Composite composite = ( Composite ) super.createDialogArea( parent );
        Composite dualComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );

        // Creating dialog areas
        createTimeDialogArea( dualComposite );
        createDateDialogArea( dualComposite );
        createTimeZoneDialogArea( dualComposite );
        createRawDialogArea( dualComposite );

        // Initializing with initial value
        initWithInitialValue();

        // Adding listeners
        addListeners();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Creates the "Time" dialog area.
     *
     * @param parent
     *      the parent composite
     */
    private void createTimeDialogArea( Composite parent )
    {
        // Label
        Label timeLabel = new Label( parent, SWT.NONE );
        timeLabel.setText( "Time:" );

        Composite rightComposite = BaseWidgetUtils.createColumnContainer( parent, 5, 1 );

        // Hours
        hoursSpinner = new Spinner( rightComposite, SWT.BORDER );
        hoursSpinner.setMinimum( 0 );
        hoursSpinner.setMaximum( 23 );
        hoursSpinner.setTextLimit( 2 );
        hoursSpinner.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );

        Label label1 = BaseWidgetUtils.createLabel( rightComposite, ":", 1 );
        label1.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, false ) );

        // Minutes
        minutesSpinner = new Spinner( rightComposite, SWT.BORDER );
        minutesSpinner.setMinimum( 0 );
        minutesSpinner.setMaximum( 59 );
        minutesSpinner.setTextLimit( 2 );
        minutesSpinner.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false ) );

        Label label2 = BaseWidgetUtils.createLabel( rightComposite, ":", 1 );
        label2.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, false ) );

        // Seconds
        secondsSpinner = new Spinner( rightComposite, SWT.BORDER );
        secondsSpinner.setMinimum( 0 );
        secondsSpinner.setMaximum( 59 );
        secondsSpinner.setTextLimit( 2 );
        secondsSpinner.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );
    }


    /**
     * Creates the "Date" dialog area.
     *
     * @param parent
     *      the parent composite
     */
    private void createDateDialogArea( Composite parent )
    {
        // Label
        Label dateLabel = BaseWidgetUtils.createLabel( parent, "Date:", 1 );
        dateLabel.setLayoutData( new GridData( SWT.NONE, SWT.TOP, false, false ) );

        Composite rightComposite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Calendar
        dateCalendar = new DateTime( rightComposite, SWT.CALENDAR | SWT.BORDER );
        dateCalendar.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    }


    /**
     * Creates the "Time Zone" dialog area.
     *
     * @param parent
     *      the parent composite
     */
    private void createTimeZoneDialogArea( Composite parent )
    {
        // Label
        BaseWidgetUtils.createLabel( parent, "Time zone:", 1 );

        // Combo viewer
        timezoneComboViewer = new ComboViewer( parent );
        timezoneComboViewer.getCombo().setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Adding ContentProvider, LabelProvider
        timezoneComboViewer.setContentProvider( new ArrayContentProvider() );
        timezoneComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                return ( ( GeneralizedTimeTimeZones ) element ).getName();
            }
        } );

        // Initializing the time zones list and map
        timezonesList = GeneralizedTimeTimeZones.getAllTimezones();
        timezonesMap = new HashMap<Integer, GeneralizedTimeTimeZones>();
        for ( GeneralizedTimeTimeZones timezone : timezonesList )
        {
            timezonesMap.put( new Integer( timezone.getRawOffset() ), timezone );
        }

        timezoneComboViewer.setInput( timezonesList );
    }


    /**
     * Creates the "Time Zone" dialog area.
     *
     * @param parent
     *      the parent composite
     */
    private void createRawDialogArea( Composite parent )
    {
        // Separator
        Label separatorLabel = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        separatorLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );

        // Label
        BaseWidgetUtils.createLabel( parent, "Raw:", 1 );

        // Raw composite
        Composite rawComposite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        // Text
        rawText = BaseWidgetUtils.createText( rawComposite, "", 1 );

        // Validator image
        rawValidatorImage = new Label( rawComposite, SWT.NONE );
        validateRawValue( true );
    }


    /**
     * Initializes the UI with the value.
     */
    private void initWithInitialValue()
    {
        updateNonRawFields();
        updateRawFields();
    }


    /**
     * Update the non-raw UI fields.
     */
    private void updateNonRawFields()
    {
        Calendar calendar = value.getCalendar();

        // Time
        hoursSpinner.setSelection( calendar.get( Calendar.HOUR_OF_DAY ) );
        minutesSpinner.setSelection( calendar.get( Calendar.MINUTE ) );
        secondsSpinner.setSelection( calendar.get( Calendar.SECOND ) );

        // Date
        dateCalendar.setDate( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar
            .get( Calendar.DAY_OF_MONTH ) );

        // Time zone
        GeneralizedTimeTimeZones timezone = timezonesMap.get( new Integer( calendar.getTimeZone().getRawOffset() ) );
        if ( timezone == null )
        {
            timezoneComboViewer.setSelection( null );
        }
        else
        {
            timezoneComboViewer.setSelection( new StructuredSelection( timezone ) );
        }
    }


    /**
     * Update the raw UI fields.
     */
    private void updateRawFields()
    {
        // Raw
        rawText.setText( value.toGeneralizedTime() );

        validateRawValue( true );
    }


    /**
     * Validates the raw value.
     *
     * @param bool
     *      <code>true</code> to set the raw value as valid
     *      <code>false</code> to set the raw value as invalid
     */
    private void validateRawValue( boolean bool )
    {
        if ( bool )
        {
            rawValidatorImage.setImage( ValueEditorsActivator.getDefault().getImage(
                ValueEditorsConstants.IMG_TEXTFIELD_OK ) );
        }
        else
        {
            rawValidatorImage.setImage( ValueEditorsActivator.getDefault().getImage(
                ValueEditorsConstants.IMG_TEXTFIELD_ERROR ) );
        }

        if ( okButton != null && !okButton.isDisposed() )
        {
            okButton.setEnabled( bool );
        }
    }


    /**
     * Adds the listeners to the UI fields.
     */
    private void addListeners()
    {
        // Hours
        hoursSpinner.addModifyListener( hoursModifyListener );

        // Minutes
        minutesSpinner.addModifyListener( minutesModifyListener );

        // Seconds
        secondsSpinner.addModifyListener( secondsModifyListener );

        // Calendar
        dateCalendar.addSelectionListener( dateSelectionListener );

        // Time zone
        timezoneComboViewer.addSelectionChangedListener( timezoneSelectionChangedListener );

        // Raw value
        rawText.addModifyListener( rawModifyListener );
    }


    /**
     * Removes the listeners from the UI fields.
     */
    private void removeListeners()
    {
        // Hours
        hoursSpinner.removeModifyListener( hoursModifyListener );

        // Minutes
        minutesSpinner.removeModifyListener( minutesModifyListener );

        // Seconds
        secondsSpinner.removeModifyListener( secondsModifyListener );

        // Calendar
        dateCalendar.removeSelectionListener( dateSelectionListener );

        // Time zone
        timezoneComboViewer.removeSelectionChangedListener( timezoneSelectionChangedListener );

        // Raw value
        rawText.removeModifyListener( rawModifyListener );
    }


    /**
     * Updates the value using the non raw fields.
     */
    private void updateValueFromNonRawFields()
    {
        Calendar calendar = Calendar.getInstance();

        // Time
        calendar.set( Calendar.HOUR_OF_DAY, hoursSpinner.getSelection() );
        calendar.set( Calendar.MINUTE, minutesSpinner.getSelection() );
        calendar.set( Calendar.SECOND, secondsSpinner.getSelection() );

        // Date
        calendar.set( Calendar.YEAR, dateCalendar.getYear() );
        calendar.set( Calendar.MONTH, dateCalendar.getMonth() );
        calendar.set( Calendar.DAY_OF_MONTH, dateCalendar.getDay() );

        // Time zone
        StructuredSelection selection = ( StructuredSelection ) timezoneComboViewer.getSelection();
        if ( ( selection != null ) && ( !selection.isEmpty() ) )
        {
            GeneralizedTimeTimeZones timezone = ( GeneralizedTimeTimeZones ) selection.getFirstElement();
            String[] timezoneIds = TimeZone.getAvailableIDs( timezone.getRawOffset() );
            if ( ( timezoneIds != null ) && ( timezoneIds.length > 0 ) )
            {
                calendar.setTimeZone( TimeZone.getTimeZone( timezoneIds[0] ) );
            }
        }
        else
        {
            calendar.setTimeZone( value.getCalendar().getTimeZone() );
        }

        // Replacing the value
        value = new GeneralizedTime( calendar );
    }


    /**
     * Gets the {@link GeneralizedTime}.
     *
     * @return
     */
    public GeneralizedTime getGeneralizedTime()
    {
        return value;
    }
}
