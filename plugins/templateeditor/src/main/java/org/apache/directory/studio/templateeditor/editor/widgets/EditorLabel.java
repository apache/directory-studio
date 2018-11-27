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
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateLabel;
import org.apache.directory.studio.templateeditor.model.widgets.WidgetAlignment;


/**
 * This class implements an editor label.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorLabel extends EditorWidget<TemplateLabel>
{
    /** The label widget */
    private Text label;


    /**
     * Creates a new instance of EditorLabel.
     * 
     * @param editor
     *      the associated editor
     * @param templateLabel
     *      the associated template label
     * @param toolkit
     *      the associated toolkit
     */
    public EditorLabel( IEntryEditor editor, TemplateLabel templateLabel, FormToolkit toolkit )
    {
        super( templateLabel, editor, toolkit );
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
        // Creating the label
        label = new Text( parent, getStyle() );
        label.setEditable( false );
        label.setBackground( parent.getBackground() );

        // Setting the layout data
        GridData gd = getGridata();
        label.setLayoutData( gd );

        // Calculating height for multiple rows
        int numberOfRows = getWidget().getNumberOfRows();
        if ( numberOfRows != 1 )
        {
            GC gc = new GC( parent );
            
            try
            {
                gc.setFont( label.getFont() );
                FontMetrics fontMetrics = gc.getFontMetrics();
                gd.heightHint = fontMetrics.getHeight() * numberOfRows;
            }
            finally
            {
                gc.dispose();
            }
        }

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
        int style = SWT.WRAP;

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
        // Checking if we need to display a value taken from the entry
        // or use the given value
        String attributeType = getWidget().getAttributeType();
        if ( ( attributeType != null ) || ( "".equals( attributeType ) ) ) //$NON-NLS-1$
        {
            IEntry entry = getEntry();
            if ( entry != null )
            {
                // Getting the text to display
                String text = EditorWidgetUtils.getConcatenatedValues( entry, attributeType );

                // Replacing '$' by '/n' if needed
                if ( getWidget().isDollarSignIsNewLine() )
                {
                    text = text.replace( '$', '\n' );
                }

                // Assigning the text to the label
                label.setText( text );
            }
        }
        else
        {
            // Getting the text to display
            String text = getWidget().getValue();

            // Replacing '$' by '/n' if needed
            if ( getWidget().isDollarSignIsNewLine() )
            {
                text = text.replace( '$', '\n' );
            }

            // Assigning the text to the label
            label.setText( text );
        }

        // Forcing the re-layout of the label from its parent 
        label.getParent().layout();
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
        label.dispose();
    }
}