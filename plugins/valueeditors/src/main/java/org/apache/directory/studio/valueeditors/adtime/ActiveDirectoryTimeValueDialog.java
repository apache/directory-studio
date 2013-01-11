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
package org.apache.directory.studio.valueeditors.adtime;


import java.util.Calendar;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


/**
 * This class provides a dialog to edit an Active Directory Date & Time value.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ActiveDirectoryTimeValueDialog extends Dialog
{
    /** The value */
    private long value;

    //
    // UI Fields
    //

    // Time
    private Spinner hoursSpinner;
    private Spinner minutesSpinner;
    private Spinner secondsSpinner;

    // Date
    private DateTime dateCalendar;

    // Raw value
    private Text rawValueText;

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
     * The modify listener of the raw field.
     */
    private ModifyListener rawValueModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            try
            {
                value = Long.parseLong( rawValueText.getText() );

                removeListeners();
                updateNonRawFields();
                addListeners();
            }
            catch ( NumberFormatException e1 )
            {
                return;
            }
        }
    };

    private VerifyListener rawValueVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            // Prevent the user from entering anything but an integer
            if ( !e.text.matches( "(-)?([0-9])*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
            }
        }
    };


    /**
     * Creates a new instance of ActiveDirectoryTimeValueDialog.
     * 
     * @param parentShell
     *      the parent shell
     * @param value
     *      the initial value
     */
    public ActiveDirectoryTimeValueDialog( Shell parentShell, long value )
    {
        this( parentShell );
        this.value = value;
    }


    /**
     * Creates a new instance of ActiveDirectoryTimeValueDialog.
     * 
     * @param parentShell
     *      the parent shell
     * @param value
     *      the initial value
     */
    public ActiveDirectoryTimeValueDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.value = ActiveDirectoryTimeUtils.convertToActiveDirectoryTime( Calendar.getInstance() );
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString( "ActiveDirectoryTimeValueDialog.DateAndTimeEditor" ) ); //$NON-NLS-1$
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_GENERALIZEDTIMEEDITOR ) );
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
        createRawValueDialogArea( dualComposite );

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
        timeLabel.setText( Messages.getString( "ActiveDirectoryTimeValueDialog.Time" ) ); //$NON-NLS-1$

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
            Messages.getString( "ActiveDirectoryTimeValueDialog.Date" ), 1 ); //$NON-NLS-1$
        dateLabel.setLayoutData( new GridData( SWT.NONE, SWT.TOP, false, false ) );

        Composite rightComposite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Calendar
        dateCalendar = new DateTime( rightComposite, SWT.CALENDAR | SWT.BORDER );
        dateCalendar.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
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
        BaseWidgetUtils.createLabel( parent, Messages.getString( "ActiveDirectoryTimeValueDialog.RawValue" ), 1 ); //$NON-NLS-1$

        // Text
        rawValueText = BaseWidgetUtils.createText( parent, "", 1 ); //$NON-NLS-1$
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
        // Getting the calendar 
        Calendar calendar = getCalendarFromValue();

        // Time
        hoursSpinner.setSelection( calendar.get( Calendar.HOUR_OF_DAY ) );
        minutesSpinner.setSelection( calendar.get( Calendar.MINUTE ) );
        secondsSpinner.setSelection( calendar.get( Calendar.SECOND ) );

        // Date
        dateCalendar.setDate( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar
            .get( Calendar.DAY_OF_MONTH ) );
    }


    /**
     * Update the raw UI fields.
     */
    private void updateRawFields()
    {
        // Raw value
        rawValueText.setText( "" + value ); //$NON-NLS-1$
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

        // Raw value
        rawValueText.addModifyListener( rawValueModifyListener );
        rawValueText.addVerifyListener( rawValueVerifyListener );
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

        // Raw value
        rawValueText.removeModifyListener( rawValueModifyListener );
        rawValueText.removeVerifyListener( rawValueVerifyListener );
    }


    /**
     * Updates the value using the non raw fields.
     */
    private void updateValueFromNonRawFields()
    {
        // Getting the calendar 
        Calendar calendar = getCalendarFromValue();

        // Time
        calendar.set( Calendar.HOUR_OF_DAY, hoursSpinner.getSelection() );
        calendar.set( Calendar.MINUTE, minutesSpinner.getSelection() );
        calendar.set( Calendar.SECOND, secondsSpinner.getSelection() );

        // Date
        calendar.set( Calendar.YEAR, dateCalendar.getYear() );
        calendar.set( Calendar.MONTH, dateCalendar.getMonth() );
        calendar.set( Calendar.DAY_OF_MONTH, dateCalendar.getDay() );

        value = ActiveDirectoryTimeUtils.convertToActiveDirectoryTime( calendar );
    }


    /**
     * Gets a calendar from the current value.
     *
     * @return a calendar corresponding to the current value
     */
    private Calendar getCalendarFromValue()
    {
        return ActiveDirectoryTimeUtils.convertToCalendar( value );
    }


    /**
     * Gets the value.
     *
     * @return
     *      the value
     */
    public long getValue()
    {
        return value;
    }
}
