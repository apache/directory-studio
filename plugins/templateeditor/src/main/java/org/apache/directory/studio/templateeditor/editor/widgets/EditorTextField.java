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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateTextField;
import org.apache.directory.studio.templateeditor.model.widgets.WidgetAlignment;


/**
 * This class implements an editor text field.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorTextField extends EditorWidget<TemplateTextField>
{
    /** The text field */
    private Text textfield;

    /** The modify listener */
    private ModifyListener modifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            // Getting the value
            String value = textfield.getText();

            // Replacing '$' by '/n' if needed
            if ( getWidget().isDollarSignIsNewLine() )
            {
                value = value.replace( '\n', '$' );
            }

            // Updating the attribute's value
            updateAttributeValue( value );
        }
    };


    /**
     * Creates a new instance of EditorTextField.
     * 
     * @param editor
     *      the associated editor
     * @param templateTextField
     *      the associated template text field
     * @param toolkit
     *      the associated toolkit
     */
    public EditorTextField( IEntryEditor editor, TemplateTextField templateTextField, FormToolkit toolkit )
    {
        super( templateTextField, editor, toolkit );
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
        // Creating the text field
        textfield = getToolkit().createText( parent, "", getStyle() ); //$NON-NLS-1$
        GridData gd = getGridata();
        textfield.setLayoutData( gd );

        // Setting the characters limit
        if ( getWidget().getCharactersLimit() != -1 )
        {
            textfield.setTextLimit( getWidget().getCharactersLimit() );
        }

        // Calculating height for multiple rows
        int numberOfRows = getWidget().getNumberOfRows();
        if ( numberOfRows != 1 )
        {
            GC gc = new GC( parent );
            
            try
            {
                gc.setFont( textfield.getFont() );
                FontMetrics fontMetrics = gc.getFontMetrics();
                gd.heightHint = fontMetrics.getHeight() * numberOfRows;
            }
            finally
            {
                gc.dispose();
            }
        }
        
        textfield.pack();

        return parent;
    }


    /**
     * Gets the style of the widget.
     *
     * @return
     *      the style of the widget
     */
    private int getStyle()
    {
        int style = SWT.BORDER | SWT.WRAP;

        // Multiple lines?
        if ( getWidget().getNumberOfRows() == 1 )
        {
            style |= SWT.SINGLE;
        }
        else
        {
            style |= SWT.MULTI;
        }

        // Horizontal alignment set to end?
        if ( getWidget().getHorizontalAlignment() == WidgetAlignment.END )
        {
            style |= SWT.RIGHT;
        }
        // Horizontal alignment set to center?
        else if ( getWidget().getHorizontalAlignment() == WidgetAlignment.CENTER )
        {
            style |= SWT.CENTER;
        }

        return style;
    }


    /**
     * Updates the widget's content.
     */
    private void updateWidget()
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.isString() ) && ( attribute.getValueSize() > 0 ) )
        {
            // Saving the current caret position by getting the current selection
            Point selection = textfield.getSelection();

            // Getting the text to display
            String text = attribute.getStringValue();

            // Replacing '$' by '/n' (if needed)
            if ( getWidget().isDollarSignIsNewLine() )
            {
                text = text.replace( '$', '\n' );
            }

            // Assigning the text to the label
            textfield.setText( text );

            // Restoring the current caret position
            textfield.setSelection( selection );
        }
        else
        {
            // There's no value to display
            textfield.setText( "" ); //$NON-NLS-1$
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        if ( ( textfield != null ) && ( !textfield.isDisposed() ) )
        {
            textfield.addModifyListener( modifyListener );
        }
    }


    /**
     * Removes the listeners.
     */
    private void removeListeners()
    {
        if ( ( textfield != null ) && ( !textfield.isDisposed() ) )
        {
            textfield.removeModifyListener( modifyListener );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        removeListeners();
        updateWidget();
        addListeners();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        removeListeners();
    }
}