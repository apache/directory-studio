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
package org.apache.directory.studio.templateeditor.editor.widgets;


import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateCheckbox;


/**
 * This class implements an editor checkbox.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorCheckbox extends EditorWidget<TemplateCheckbox>
{
    /** The checkbox */
    private Button checkbox;

    /** The enum used to determine the state of a checkbox*/
    private enum CheckboxState
    {
        UNCHECKED, CHECKED, GRAYED
    }

    /** Constant for the 'false' string value */
    private static final String FALSE_STRING_VALUE = "FALSE"; //$NON-NLS-1$

    /** Constant for the 'true' string value */
    private static final String TRUE_STRING_VALUE = "TRUE"; //$NON-NLS-1$

    /** The current state of the checkbox */
    private CheckboxState currentState = CheckboxState.UNCHECKED;

    /** The selection listener */
    private SelectionListener selectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // Changing the state of the checkbox
            changeCheckboxState();

            // Getting the value
            boolean value = checkbox.getSelection();

            IAttribute attribute = getAttribute();
            String checkedValue = getWidget().getCheckedValue();
            String uncheckedValue = getWidget().getUncheckedValue();
            if ( attribute == null )
            {
                // The attribute does not exist

                if ( ( checkedValue == null ) && ( uncheckedValue == null ) )
                {
                    // Creating a new attribute with the value
                    addNewAttribute( ( value ? TRUE_STRING_VALUE : FALSE_STRING_VALUE ) );
                }
                else if ( ( checkedValue != null ) && ( uncheckedValue == null ) && value )
                {
                    // Creating a new attribute with the value
                    addNewAttribute( checkedValue );
                }
                else if ( ( checkedValue == null ) && ( uncheckedValue != null ) && !value )
                {
                    // Creating a new attribute with the value
                    addNewAttribute( uncheckedValue );
                }
                else if ( ( checkedValue != null ) && ( uncheckedValue != null ) )
                {
                    // Creating a new attribute with the value
                    addNewAttribute( ( value ? checkedValue : uncheckedValue ) );
                }
            }
            else
            {
                // The attribute exists

                if ( ( checkedValue == null ) && ( uncheckedValue == null ) )
                {
                    // Modifying the attribute
                    modifyAttributeValue( ( value ? TRUE_STRING_VALUE : FALSE_STRING_VALUE ) );
                }
                else if ( ( checkedValue != null ) && ( uncheckedValue == null ) )
                {
                    if ( value )
                    {
                        // Modifying the attribute
                        modifyAttributeValue( checkedValue );
                    }
                    else
                    {
                        // Deleting the attribute
                        deleteAttribute();
                    }
                }
                else if ( ( checkedValue == null ) && ( uncheckedValue != null ) )
                {
                    if ( value )
                    {
                        // Deleting the attribute
                    }
                    else
                    {
                        // Modifying the attribute
                        modifyAttributeValue( uncheckedValue );
                    }
                }
                else if ( ( checkedValue != null ) && ( uncheckedValue != null ) )
                {
                    // Modifying the attribute
                    modifyAttributeValue( ( value ? checkedValue : uncheckedValue ) );
                }
            }
        }
    };


    /**
     * Creates a new instance of EditorCheckbox.
     * 
     * @param editor
     *      the associated editor
     * @param templateCheckbox
     *      the associated template checkbox
     * @param toolkit
     *      the associated toolkit
     */
    public EditorCheckbox( IEntryEditor editor, TemplateCheckbox templateCheckbox, FormToolkit toolkit  )
    {
        super( templateCheckbox, editor, toolkit );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createWidget( Composite parent )
    {
        // Creating and initializing the widget UI
        Composite composite = initWidget( parent );

        // Updating the widget's content
        updateWidget();

        // Adding the listeners
        addListeners();

        return composite;
    }


    /**
     * Creates and initializes the widget UI.
     *
     * @param parent
     *      the parent composite
     * @return
     *      the associated composite
     */
    private Composite initWidget( Composite parent )
    {
        // Creating the checkbox
        checkbox = getToolkit().createButton( parent, getWidget().getLabel(), SWT.CHECK );
        checkbox.setLayoutData( getGridata() );
        checkbox.setEnabled( getWidget().isEnabled() );

        return parent;
    }


    /**
     * Updates the widget's content.
     */
    private void updateWidget()
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.isString() ) && ( attribute.getValueSize() > 0 ) )
        {
            setCheckboxState( attribute.getStringValue() );
        }
    }


    /**
     * Sets the state of the checkbox.
     *
     * @param value
     *      the value
     */
    private void setCheckboxState( String value )
    {
        String checkedValue = getWidget().getCheckedValue();
        String uncheckedValue = getWidget().getUncheckedValue();

        if ( ( checkedValue == null ) && ( uncheckedValue == null ) )
        {
            if ( TRUE_STRING_VALUE.equalsIgnoreCase( value ) )
            {
                setCheckboxCheckedState();
            }
            else if ( FALSE_STRING_VALUE.equalsIgnoreCase( value ) )
            {
                setCheckboxUncheckedState();
            }
            else
            {
                setCheckboxGrayedState();
            }
        }
        else if ( ( checkedValue != null ) && ( uncheckedValue == null ) )
        {
            if ( checkedValue.equals( value ) )
            {
                setCheckboxCheckedState();
            }
            else
            {
                setCheckboxUncheckedState();
            }
        }
        else if ( ( checkedValue == null ) && ( uncheckedValue != null ) )
        {
            if ( uncheckedValue.equals( value ) )
            {
                setCheckboxUncheckedState();
            }
            else
            {
                setCheckboxCheckedState();
            }
        }
        else if ( ( checkedValue != null ) && ( uncheckedValue != null ) )
        {
            if ( checkedValue.equals( value ) )
            {
                setCheckboxCheckedState();
            }
            else if ( uncheckedValue.equals( value ) )
            {
                setCheckboxUncheckedState();
            }
            else
            {
                setCheckboxGrayedState();
            }
        }
    }


    /**
     * Sets the checkbox in checked state.
     */
    private void setCheckboxCheckedState()
    {
        checkbox.setGrayed( false );
        checkbox.setSelection( true );
        currentState = CheckboxState.CHECKED;
    }


    /**
     * Sets the checkbox in unchecked state.
     *
     */
    private void setCheckboxUncheckedState()
    {
        checkbox.setGrayed( false );
        checkbox.setSelection( false );
        currentState = CheckboxState.UNCHECKED;
    }


    /**
     * Sets the checkbox in grayed state.
     */
    private void setCheckboxGrayedState()
    {
        checkbox.setGrayed( true );
        checkbox.setSelection( true );
        currentState = CheckboxState.GRAYED;
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        if ( ( checkbox != null ) && ( !checkbox.isDisposed() ) )
        {
            checkbox.addSelectionListener( selectionListener );
        }
    }


    /**
     * Changes the state of the checkbox.
     */
    private void changeCheckboxState()
    {
        switch ( currentState )
        {
            case UNCHECKED:
                setCheckboxCheckedState();
                currentState = CheckboxState.CHECKED;
                break;
            case CHECKED:
                setCheckboxUncheckedState();
                currentState = CheckboxState.UNCHECKED;
                break;
            case GRAYED:
                setCheckboxCheckedState();
                currentState = CheckboxState.CHECKED;
                break;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        updateWidget();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }
}