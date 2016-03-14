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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateSpinner;


/**
 * This class implements an editor spinner.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorSpinner extends EditorWidget<TemplateSpinner>
{
    /** The spinner */
    private Spinner spinner;

    /** The selection listener */
    private SelectionListener selectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // Getting the value
            String value = spinner.getText();

            // Updating the attribute's value
            updateAttributeValue( value );
        }
    };


    /**
     * Creates a new instance of EditorSpinner.
     *
     * @param editor
     *      the associated editor
     * @param templateSpinner
     *      the associated template spinner
     * @param toolkit
     *      the associated toolkit
     */
    public EditorSpinner( IEntryEditor editor, TemplateSpinner templateSpinner, FormToolkit toolkit )
    {
        super( templateSpinner, editor, toolkit );
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
        // Creating the spinner
        spinner = new Spinner( parent, SWT.BORDER );
        spinner.setLayoutData( getGridata() );

        // Setting the spinner values
        spinner.setDigits( getWidget().getDigits() );
        spinner.setIncrement( getWidget().getIncrement() );
        spinner.setMaximum( getWidget().getMaximum() );
        spinner.setMinimum( getWidget().getMinimum() );
        spinner.setPageIncrement( getWidget().getPageIncrement() );

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
            try
            {
                spinner.setSelection( Integer.parseInt( attribute.getStringValue() ) );
            }
            catch ( NumberFormatException e )
            {
                // Nothing to do, we fail gracefully
            }
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        // Adding the listener
        spinner.addSelectionListener( selectionListener );
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