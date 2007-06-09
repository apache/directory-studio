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

package org.apache.directory.ldapstudio.browser.common.widgets;


import java.util.Arrays;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * The OptionsInput could be used to select one option out of several options.
 * It consists of two radio buttons. With the first radio button you could 
 * select the most likely default option. With the second radio button a combo 
 * is activated where you could select another option from a drop-down list.
 * <p>
 * Both, the default option and the options in the drop-down list have a raw
 * value that is returned by {@link #getRawValue()} and a display value
 * that is shown to the user. 
 * <p>
 * If the initial raw value is equal to the default raw value then the 
 * default radio is checked and the drop-down list is disabled. Otherwise 
 * the second radio is checked, the drop-down list is enabled and the
 * initial value is selected. 
 * <p>
 * The OptionsInput is used by {@link TextFormatsPreferencePage}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OptionsInput extends BrowserWidget
{

    /** The option's title */
    private String title;

    /** The group, only used when asGroup is true */
    private Group titleGroup;

    /** The default raw value */
    private String defaultRawValue;

    /** The default display value */
    private String defaultDisplayValue;

    /** The radio button to select the default value */
    private Button defaultButton;

    /** The other raw values */
    private String[] otherRawValues;

    /** The other display values */
    private String[] otherDisplayValues;

    /** The radio button to select a value from drop-down list */
    private Button otherButton;

    /** The combo with the other values */
    private Combo otherCombo;

    /** The initial raw value */
    private String initialRawValue;

    /** If true the options are aggregated in a group widget */
    private boolean asGroup;

    /** If true it is possible to enter a custom value into the combo field */
    private boolean allowCustomInput;


    /**
     * Creates a new instance of OptionsInput.
     *
     * @param title the option's title
     * @param defaultDisplayValue the default display value
     * @param defaultRawValue the default raw value
     * @param otherDisplayValues the other display values
     * @param otherRawValues the other raw vaues
     * @param initialRawValue the initial raw value
     * @param asGroup a flag indicating if the options should be 
     *                aggregated in a group widget
     * @param allowCustomInput true to make it possible to enter a 
     *                         custom value into the combo field
     */
    public OptionsInput( String title, String defaultDisplayValue, String defaultRawValue, String[] otherDisplayValues,
        String[] otherRawValues, String initialRawValue, boolean asGroup, boolean allowCustomInput )
    {
        super();
        this.title = title;
        this.defaultDisplayValue = defaultDisplayValue;
        this.defaultRawValue = defaultRawValue;
        this.otherDisplayValues = otherDisplayValues;
        this.otherRawValues = otherRawValues;
        this.initialRawValue = initialRawValue;
        this.asGroup = asGroup;
        this.allowCustomInput = allowCustomInput;
    }


    /**
     * Creates the widget.
     *
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {

        Composite composite;
        if ( asGroup )
        {
            titleGroup = BaseWidgetUtils.createGroup( parent, title, 1 );
            composite = BaseWidgetUtils.createColumnContainer( titleGroup, 1, 1 );
        }
        else
        {
            composite = parent;
            Composite labelComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
            BaseWidgetUtils.createLabel( labelComposite, title + ":", 1 );
        }

        Composite defaultComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
        defaultButton = BaseWidgetUtils.createRadiobutton( defaultComposite, defaultDisplayValue, 1 );
        defaultButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                otherButton.setSelection( false );
                otherCombo.setEnabled( false );
                notifyListeners();
            }
        } );

        Composite otherComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        otherButton = BaseWidgetUtils.createRadiobutton( otherComposite, "Other: ", 1 );
        otherButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                defaultButton.setSelection( false );
                otherCombo.setEnabled( true );
                notifyListeners();
            }
        } );

        if ( allowCustomInput )
        {
            otherCombo = BaseWidgetUtils.createCombo( otherComposite, otherDisplayValues, 0, 1 );
        }
        else
        {
            otherCombo = BaseWidgetUtils.createReadonlyCombo( otherComposite, otherDisplayValues, 0, 1 );
        }
        otherCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                notifyListeners();
            }
        } );

        setRawValue( initialRawValue );
    }


    /**
     * Gets the raw value. Either the default value or
     * the selected value from the combo.
     *
     * @return the raw value
     */
    public String getRawValue()
    {
        if ( defaultButton.getSelection() )
        {
            return defaultRawValue;
        }
        else
        {
            String t = otherCombo.getText();
            for ( int i = 0; i < otherDisplayValues.length; i++ )
            {
                if ( t.equals( otherDisplayValues[i] ) )
                {
                    return otherRawValues[i];
                }
            }
            return t;
        }
    }


    /**
     * Sets the raw value.
     *
     * @param rawValue the raw value
     */
    public void setRawValue( String rawValue )
    {
        int index = Arrays.asList( otherRawValues ).indexOf( rawValue );
        if ( index == -1 )
        {
            index = Arrays.asList( otherDisplayValues ).indexOf( rawValue );
        }

        if ( defaultRawValue.equals( rawValue ) )
        {
            defaultButton.setSelection( true );
            otherButton.setSelection( false );
            otherCombo.setEnabled( false );
            otherCombo.select( index );
        }
        else if ( index > -1 )
        {
            defaultButton.setSelection( false );
            otherButton.setSelection( true );
            otherCombo.setEnabled( true );
            otherCombo.select( index );
        }
        else
        {
            defaultButton.setSelection( false );
            otherButton.setSelection( true );
            otherCombo.setEnabled( true );
            otherCombo.setText( rawValue );
        }
    }

}
