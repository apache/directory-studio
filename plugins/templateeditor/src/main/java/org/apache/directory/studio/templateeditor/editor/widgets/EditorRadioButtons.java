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


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateRadioButtons;
import org.apache.directory.studio.templateeditor.model.widgets.ValueItem;


/**
 * This class implements an editor radio buttons.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorRadioButtons extends EditorWidget<TemplateRadioButtons>
{
    /** The map of (ValueItem,Button) elements used in the UI */
    private Map<ValueItem, Button> valueItemsToButtonsMap = new HashMap<ValueItem, Button>();
    private Map<Button, ValueItem> buttonsToValueItemsMap = new HashMap<Button, ValueItem>();

    /** The currently selected item */
    private ValueItem selectedItem;

    /** The selection listener */
    private SelectionListener selectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // Saving the selected item
            selectedItem = buttonsToValueItemsMap.get( e.getSource() );

            // Updating the entry
            updateEntry();
        }
    };


    /**
     * Creates a new instance of EditorRadioButtons.
     * 
     * @param editor
     *      the associated editor
     * @param templateRadioButtons
     *      the associated template radio buttons
     * @param toolkit
     *      the associated toolkit
     */
    public EditorRadioButtons( IEntryEditor editor, TemplateRadioButtons templateRadioButtons,
        FormToolkit toolkit )
    {
        super( templateRadioButtons, editor, toolkit );
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
        // Creating the widget composite
        Composite composite = getToolkit().createComposite( parent );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( getGridata() );

        // Creating the layout
        GridLayout gl = new GridLayout();
        gl.marginHeight = gl.marginWidth = 0;
        gl.horizontalSpacing = gl.verticalSpacing = 0;
        composite.setLayout( gl );

        // Creating the Radio Buttons
        for ( ValueItem valueItem : getWidget().getButtons() )
        {
            Button button = getToolkit().createButton( composite, valueItem.getLabel(), SWT.RADIO );
            button.setEnabled( getWidget().isEnabled() );
            valueItemsToButtonsMap.put( valueItem, button );
            buttonsToValueItemsMap.put( button, valueItem );
        }

        return composite;
    }


    /**
     * Updates the widget's content.
     */
    private void updateWidget()
    {
        // Getting the attribute value
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.isString() ) && ( attribute.getValueSize() > 0 ) )
        {
            String value = attribute.getStringValue();
            for ( ValueItem valueItem : valueItemsToButtonsMap.keySet() )
            {
                Button button = valueItemsToButtonsMap.get( valueItem );
                if ( button != null && !button.isDisposed() )
                {
                    button.setSelection( value.equals( valueItem.getValue() ) );
                }
            }
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        for ( final ValueItem valueItem : valueItemsToButtonsMap.keySet() )
        {
            Button button = valueItemsToButtonsMap.get( valueItem );
            if ( button != null )
            {
                button.addSelectionListener( selectionListener );
            }
        }
    }


    /**
     * This method is called when the entry has been updated in the UI.
     */
    private void updateEntry()
    {
        // Getting the  attribute
        IAttribute attribute = getAttribute();
        if ( attribute == null )
        {
            if ( selectedItem != null )
            {
                // Creating a new attribute with the value
                addNewAttribute( selectedItem.getValue() );
            }
        }
        else
        {
            if ( ( selectedItem != null ) && ( !selectedItem.equals( "" ) ) ) //$NON-NLS-1$
            {
                // Modifying the existing attribute
                modifyAttributeValue( selectedItem.getValue() );
            }
            else
            {
                // Deleting the attribute
                deleteAttribute();
            }
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