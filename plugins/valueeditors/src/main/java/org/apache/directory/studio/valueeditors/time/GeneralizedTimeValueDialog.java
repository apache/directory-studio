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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.apache.directory.api.util.GeneralizedTime;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
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
 */
public class GeneralizedTimeValueDialog extends Dialog
{
    /** The value */
    private GeneralizedTime value;

    /** The list, containing all time zones, bound to the combo viewer */
    private ArrayList<TimeZone> allTimezonesList = new ArrayList<TimeZone>();

    /** The UTC times zones map */
    private Map<Integer, TimeZone> utcTimezonesMap = new HashMap<Integer, TimeZone>();

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
    private Text rawValueText;

    // Raw validator
    private Label rawValueValidatorImage;

    // Discard fraction checkbox
    private Button discardFractionCheckbox;

    /** The OK button of the dialog */
    private Button okButton;

    //
    // Listeners
    //

    /**
     * The modify listener of the hours field.
     */
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

    /**
     * The modify listener of the minutes field.
     */
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

    /**
     * The modify listener of the seconds field.
     */
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

    /**
     * The selection listener of the calendar.
     */
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

    /**
     * The selection changed listener of the time zone combo.
     */
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

    /**
     * The modify listener of the raw field.
     */
    private ModifyListener rawValueModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            try
            {
                value = new GeneralizedTime( rawValueText.getText() );

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

    private SelectionListener discardFractionCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            removeListeners();
            updateRawFields();
            addListeners();
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
        shell.setText( Messages.getString( "GeneralizedTimeValueDialog.DateAndTimeEditor" ) ); //$NON-NLS-1$
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
        // Checking if we need to discard the fraction
        if ( discardFractionCheckbox.getSelection() )
        {
            // Removing the fraction from the value
            Calendar calendar = value.getCalendar();
            calendar.set( Calendar.MILLISECOND, 0 );

            value = new GeneralizedTime( calendar );
        }

        // Saving the dialog settings
        ValueEditorsActivator.getDefault().getDialogSettings()
            .put( ValueEditorsConstants.DIALOGSETTING_KEY_DATE_EDITOR_DISCARD_FRACTION,
                discardFractionCheckbox.getSelection() );

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
        createRawValueDialogArea( dualComposite );

        // Getting the dialog settings
        discardFractionCheckbox.setSelection( ValueEditorsActivator.getDefault().getDialogSettings()
            .getBoolean( ValueEditorsConstants.DIALOGSETTING_KEY_DATE_EDITOR_DISCARD_FRACTION ) );

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
        timeLabel.setText( Messages.getString( "GeneralizedTimeValueDialog.Time" ) ); //$NON-NLS-1$

        Composite rightComposite = BaseWidgetUtils.createColumnContainer( parent, 5, 1 );

        // Hours
        hoursSpinner = new Spinner( rightComposite, SWT.BORDER );
        hoursSpinner.setMinimum( 0 );
        hoursSpinner.setMaximum( 23 );
        hoursSpinner.setTextLimit( 2 );
        hoursSpinner.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );

        Label label1 = BaseWidgetUtils.createLabel( rightComposite, ":", 1 ); //$NON-NLS-1$
        label1.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, false ) );

        // Minutes
        minutesSpinner = new Spinner( rightComposite, SWT.BORDER );
        minutesSpinner.setMinimum( 0 );
        minutesSpinner.setMaximum( 59 );
        minutesSpinner.setTextLimit( 2 );
        minutesSpinner.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false ) );

        Label label2 = BaseWidgetUtils.createLabel( rightComposite, ":", 1 ); //$NON-NLS-1$
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
        Label dateLabel = BaseWidgetUtils.createLabel( parent,
            Messages.getString( "GeneralizedTimeValueDialog.Date" ), 1 ); //$NON-NLS-1$
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
        BaseWidgetUtils.createLabel( parent, Messages.getString( "GeneralizedTimeValueDialog.Timezone" ), 1 ); //$NON-NLS-1$

        // Combo viewer
        timezoneComboViewer = new ComboViewer( parent );
        GridData timezoneGridData = new GridData( SWT.FILL, SWT.CENTER, true, false );
        timezoneGridData.widthHint = 50;
        timezoneComboViewer.getCombo().setLayoutData( timezoneGridData );

        // Adding ContentProvider, LabelProvider
        timezoneComboViewer.setContentProvider( new ArrayContentProvider() );
        timezoneComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                return ( ( TimeZone ) element ).getID();
            }
        } );

        // Initializing the time zones list and map
        initAllTimezones();

        timezoneComboViewer.setInput( allTimezonesList );
    }


    /**
     * Initializes all the time zones.
     */
    private void initAllTimezones()
    {
        initUtcTimezones();
        initContinentsAndCitiesTimezones();
    }


    /**
     * Initializes all the "UTC+/-xxxx" time zones.
     */
    private void initUtcTimezones()
    {
        addUtcTimezone( "UTC-12", -1 * ( 12 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-11", -1 * ( 11 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-10", -1 * ( 10 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-9:30", -1 * ( ( ( 9 * 60 ) + 30 ) * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-9", -1 * ( 9 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-8", -1 * ( 8 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-7", -1 * ( 7 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-6", -1 * ( 6 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-5", -1 * ( 5 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-4:30", -1 * ( ( ( 4 * 60 ) + 30 ) * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-4", -1 * ( 4 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-3:30", -1 * ( ( ( 3 * 60 ) + 30 ) * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-3", -1 * ( 3 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-2", -1 * ( 2 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC-1", -1 * ( 1 * 60 * 60 * 1000 ) ); //$NON-NLS-1$
        addUtcTimezone( "UTC", 0 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+1", 1 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+2", 2 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+3", 3 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+3:30", ( ( 3 * 60 ) + 30 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+4", 4 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+4:30", ( ( 4 * 60 ) + 30 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+5", 5 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+5:30", ( ( 5 * 60 ) + 30 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+5:45", ( ( 5 * 60 ) + 45 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+6", 6 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+6:30", ( ( 6 * 60 ) + 30 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+7", 7 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+8", 8 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+8:45", ( ( 8 * 60 ) + 45 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+9", 9 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+9:30", ( ( 9 * 60 ) + 30 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+10", 10 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+10:30", ( ( 10 * 60 ) + 30 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+11", 11 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+11:30", ( ( 11 * 60 ) + 30 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+12", 12 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+12:45", ( ( 12 * 60 ) + 45 ) * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+13", 13 * 60 * 60 * 1000 ); //$NON-NLS-1$
        addUtcTimezone( "UTC+14", 14 * 60 * 60 * 1000 ); //$NON-NLS-1$
    }


    /**
     * Adds an UTC time zone.
     *
     * @param tz
     *      a time zone to add
     */
    private void addUtcTimezone( String id, int rawOffset )
    {
        TimeZone tz = rawOffset == 0 ? TimeZone.getTimeZone( "UTC" ) : new SimpleTimeZone( rawOffset, id ); //$NON-NLS-1$

        allTimezonesList.add( tz );
        utcTimezonesMap.put( rawOffset, tz );
    }


    /**
     * Initializes all the continents and cities time zones.
     */
    private void initContinentsAndCitiesTimezones()
    {
        List<TimeZone> continentsAndCitiesTimezonesList = new ArrayList<TimeZone>();

        // Getting all e time zones from the following continents :
        //     * Africa
        //     * America
        //     * Asia
        //     * Atlantic
        //     * Australia
        //     * Europe
        //     * Indian
        //     * Pacific
        for ( String timezoneId : TimeZone.getAvailableIDs() )
        {
            if ( timezoneId.matches( "^(Africa|America|Asia|Atlantic|Australia|Europe|Indian|Pacific)/.*" ) ) //$NON-NLS-1$
            {
                continentsAndCitiesTimezonesList.add( TimeZone.getTimeZone( timezoneId ) );
            }
        }

        // Sorting the list by ID
        Collections.sort( continentsAndCitiesTimezonesList, new Comparator<TimeZone>()
        {
            public int compare( final TimeZone a, final TimeZone b )
            {
                return a.getID().compareTo( b.getID() );
            }
        } );

        allTimezonesList.addAll( continentsAndCitiesTimezonesList );
    }


    /**
     * Creates the "Raw value" dialog area.
     *
     * @param parent
     *      the parent composite
     */
    private void createRawValueDialogArea( Composite parent )
    {
        // Separator
        Label separatorLabel = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        separatorLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );

        // Label
        BaseWidgetUtils.createLabel( parent, Messages.getString( "GeneralizedTimeValueDialog.RawValue" ), 1 ); //$NON-NLS-1$

        // Raw composite
        Composite rawValueComposite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        // Text
        rawValueText = BaseWidgetUtils.createText( rawValueComposite, "", 1 ); //$NON-NLS-1$

        // Validator image
        rawValueValidatorImage = new Label( rawValueComposite, SWT.NONE );

        // Discard fraction checkbox
        discardFractionCheckbox = BaseWidgetUtils.createCheckbox( parent,
            Messages.getString( "GeneralizedTimeValueDialog.DiscardFraction" ), 2 );

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
        TimeZone timezone = utcTimezonesMap.get( new Integer( calendar.getTimeZone().getRawOffset() ) );
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
        // Raw value
        if ( discardFractionCheckbox.getSelection() )
        {
            rawValueText.setText( value.toGeneralizedTimeWithoutFraction() );
        }
        else
        {
            rawValueText.setText( value.toGeneralizedTime() );
        }

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
            rawValueValidatorImage.setImage( ValueEditorsActivator.getDefault().getImage(
                ValueEditorsConstants.IMG_TEXTFIELD_OK ) );
        }
        else
        {
            rawValueValidatorImage.setImage( ValueEditorsActivator.getDefault().getImage(
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
        rawValueText.addModifyListener( rawValueModifyListener );

        // Discard fraction checkbox
        discardFractionCheckbox.addSelectionListener( discardFractionCheckboxSelectionListener );
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
        rawValueText.removeModifyListener( rawValueModifyListener );

        // Discard fraction checkbox
        discardFractionCheckbox.removeSelectionListener( discardFractionCheckboxSelectionListener );
    }


    /**
     * Updates the value using the non raw fields.
     */
    private void updateValueFromNonRawFields()
    {
        // Retain the format of the GeneralizedTime value 
        // by only updating its calendar object.
        Calendar calendar = value.getCalendar();

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
            calendar.setTimeZone( ( TimeZone ) selection.getFirstElement() );
        }
    }


    /**
     * Gets the {@link GeneralizedTime} value.
     *
     * @return
     *      the {@link GeneralizedTime} value
     */
    public GeneralizedTime getGeneralizedTime()
    {
        return value;
    }
}
