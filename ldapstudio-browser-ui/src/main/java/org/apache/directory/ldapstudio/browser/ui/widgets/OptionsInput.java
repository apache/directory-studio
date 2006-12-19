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

package org.apache.directory.ldapstudio.browser.ui.widgets;


import java.util.Arrays;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


public class OptionsInput extends BrowserWidget
{

    private String title;

    private Group titleGroup;

    private String defaultRawValue;

    private String defaultDisplayValue;

    private Button defaultButton;

    private String[] otherRawValues;

    private String[] otherDisplayValues;

    private Button otherButton;

    private Combo otherCombo;

    private String initialRawValue;

    private boolean asGroup;

    private boolean allowCustomInput;


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

        // buttons = new Button[rawValues.length];
        // for (int i = 0; i < rawValues.length; i++) {
        // final String rawValue = rawValues[i];
        // Composite buttonComposite =
        // BaseWidgetUtils.createColumnContainer(composite, 1, 1);
        // buttons[i] = BaseWidgetUtils.createRadiobutton(buttonComposite,
        // displayValues[i], 1);
        // buttons[i].addSelectionListener(new SelectionAdapter() {
        // public void widgetSelected(SelectionEvent e) {
        // for (int j = 0; j < buttons.length; j++) {
        // if(buttons[j] != e.widget) {
        // buttons[j].setSelection(false);
        // }
        // }
        // otherButton.setSelection(false);
        // otherText.setEnabled(false);
        // selectedRawValue = rawValue;
        // notifyListeners();
        // }
        // });
        // }
        //
        // Composite otherComposite =
        // BaseWidgetUtils.createColumnContainer(composite, 2, 1);
        // otherButton = BaseWidgetUtils.createRadiobutton(otherComposite,
        // "Other:", 1);
        // otherButton.addSelectionListener(new SelectionAdapter() {
        // public void widgetSelected(SelectionEvent e) {
        // for (int j = 0; j < buttons.length; j++) {
        // buttons[j].setSelection(false);
        // }
        // otherText.setEnabled(true);
        // selectedRawValue = otherText.getText();
        // notifyListeners();
        // }
        // });
        // otherText = BaseWidgetUtils.createText(otherComposite, "", 2, 1);
        // otherText.setEnabled(otherButton.getSelection());
        // otherText.addModifyListener(new ModifyListener() {
        // public void modifyText(ModifyEvent e) {
        // selectedRawValue = otherText.getText();
        // notifyListeners();
        // }
        // });
        //		
        // Composite predefinedComposite =
        // BaseWidgetUtils.createColumnContainer(composite, 2, 1);
        // predefinedButton =
        // BaseWidgetUtils.createRadiobutton(predefinedComposite, "", 1);
        // predefinedButton.addSelectionListener(new SelectionAdapter() {
        // public void widgetSelected(SelectionEvent e) {
        // predefinedCombo.setEnabled(true);
        // otherButton.setSelection(false);
        // otherText.setEnabled(false);
        // notifyListeners();
        // }
        // });
        // predefinedCombo =
        // BaseWidgetUtils.createReadonlyCombo(predefinedComposite,
        // displayValues, 0, 1);
        // predefinedCombo.addModifyListener(new ModifyListener() {
        // public void modifyText(ModifyEvent e) {
        // notifyListeners();
        // }
        // });
        //		
        // Composite otherComposite =
        // BaseWidgetUtils.createColumnContainer(composite, 2, 1);
        // otherButton = BaseWidgetUtils.createRadiobutton(otherComposite,
        // "Other:", 1);
        // otherButton.addSelectionListener(new SelectionAdapter() {
        // public void widgetSelected(SelectionEvent e) {
        // predefinedButton.setSelection(false);
        // predefinedCombo.setEnabled(false);
        // otherText.setEnabled(true);
        // notifyListeners();
        // }
        // });
        // otherText = BaseWidgetUtils.createText(otherComposite, "", 2, 1);
        // otherText.setEnabled(otherButton.getSelection());
        // otherText.addModifyListener(new ModifyListener() {
        // public void modifyText(ModifyEvent e) {
        // notifyListeners();
        // }
        // });

        setRawValue( initialRawValue );
    }


    public String getRawValue()
    {
        if ( this.defaultButton.getSelection() )
        {
            return this.defaultRawValue;
        }
        else
        {
            String t = this.otherCombo.getText();
            for ( int i = 0; i < this.otherDisplayValues.length; i++ )
            {
                if ( t.equals( this.otherDisplayValues[i] ) )
                {
                    return this.otherRawValues[i];
                }
            }
            return t;
        }
    }


    public void setRawValue( String rawValue )
    {
        int index = Arrays.asList( this.otherRawValues ).indexOf( rawValue );
        if ( index == -1 )
        {
            index = Arrays.asList( this.otherDisplayValues ).indexOf( rawValue );
        }

        if ( this.defaultRawValue.equals( rawValue ) )
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

    // public String getRawValue() {
    // if(predefinedButton.getSelection()) {
    // int index = predefinedCombo.getSelectionIndex();
    // return rawValues[index];
    // }
    //		
    // return otherText.getText();
    // }
    //	
    // public void setRawValue(String rawValue) {
    //		
    // int index = Arrays.asList(rawValues).indexOf(rawValue);
    // if(index > -1) {
    // predefinedButton.setSelection(true);
    // predefinedCombo.setEnabled(true);
    // predefinedCombo.select(index);
    // otherButton.setSelection(false);
    // otherText.setEnabled(false);
    // }
    // else {
    // predefinedButton.setSelection(false);
    // predefinedCombo.setEnabled(false);
    // otherButton.setSelection(true);
    // otherText.setEnabled(true);
    // otherText.setText(rawValue);
    // }
    // }

    // public String getSelectedRawValue() {
    // for (int i = 0; i < buttons.length; i++) {
    // Button button = buttons[i];
    // if(button.getSelection()) {
    // return rawValues[i];
    // }
    // }
    // return otherText.getText();
    // }
    //	
    // public void setSelectedRawValue(String rawValue) {
    // this.selectedRawValue = rawValue;
    //		
    // for (int i = 0; i < buttons.length; i++) {
    // Button button = buttons[i];
    // button.setSelection(rawValue.equals(rawValues[i]));
    // }
    // otherButton.setSelection(!Arrays.asList(rawValues).contains(rawValue));
    // otherText.setText(otherButton.getSelection() ? rawValue : "");
    // }

}
