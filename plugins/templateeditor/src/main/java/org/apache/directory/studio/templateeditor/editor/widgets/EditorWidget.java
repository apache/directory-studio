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


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateWidget;
import org.apache.directory.studio.templateeditor.model.widgets.WidgetAlignment;


/**
 * This abstract class implements the base for an editor widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class EditorWidget<E extends TemplateWidget>
{
    /** The widget*/
    private E widget;

    /** The associated editor */
    private IEntryEditor entryEditor;

    /** The toolkit */
    private FormToolkit toolkit;


    /**
     * Creates a new instance of EditorWidget.
     *
     * @param widget
     *      the widget
     * @param entryEditor
     *      the entryEditor
     */
    public EditorWidget( E widget, IEntryEditor entryEditor, FormToolkit toolkit )
    {
        this.widget = widget;
        this.entryEditor = entryEditor;
        this.toolkit = toolkit;
    }


    /**
     * Gets the widget.
     *
     * @return
     *      the widget
     */
    public E getWidget()
    {
        return widget;
    }


    /**
     * Creates the widget.
     *
     * @param parent
     *      the parent widget
     * @return
     *      the associated composite
     */
    public Composite createWidget( Composite parent )
    {
        return parent;
    }


    /**
     * Gets the associated editor page.
     *
     * @return
     *      the associated editor page
     */
    public IEntryEditor getEditor()
    {
        return entryEditor;
    }


    /**
     * Gets the toolkit.
     *
     * @return
     *      the form toolkit
     */
    public FormToolkit getToolkit()
    {
        return toolkit;
    }


    /**
     * Gets the entry.
     *
     * @return
     *      the entry
     */
    protected IEntry getEntry()
    {
        EntryEditorInput input = getEditor().getEntryEditorInput();
        return input.getSharedWorkingCopy( getEditor() );
    }


    /**
     * Updates the widget.
     */
    public abstract void update();


    /**
     * Disposes the widget
     */
    public abstract void dispose();


    /**
     * Gets the {@link GridData} object associated with the widget properties.
     *
     * @return
     *      the {@link GridData} object associated with the widget properties
     */
    protected GridData getGridata()
    {
        // Creating the grid data with alignment and grab excess values
        GridData gd = new GridData( convertWidgetAlignmentToSWTValue( widget.getHorizontalAlignment() ),
            convertWidgetAlignmentToSWTValue( widget.getVerticalAlignment() ), widget.isGrabExcessHorizontalSpace(),
            widget.isGrabExcessVerticalSpace(), widget.getHorizontalSpan(), widget.getVerticalSpan() );

        // Setting width (if needed)
        if ( widget.getImageWidth() != TemplateWidget.DEFAULT_SIZE )
        {
            gd.widthHint = widget.getImageWidth();
        }

        // Setting height (if needed)
        if ( widget.getImageHeight() != TemplateWidget.DEFAULT_SIZE )
        {
            gd.heightHint = widget.getImageHeight();
        }

        return gd;
    }


    /**
     * Converts the given widget alignment to the equivalent value in SWT.
     *
     * @param alignment
     *      the widget alignment
     * @return
     *      the given widget alignment to the equivalent value in SWT
     */
    private static int convertWidgetAlignmentToSWTValue( WidgetAlignment alignment )
    {
        switch ( alignment )
        {
            case NONE:
                return SWT.NONE;
            case BEGINNING:
                return SWT.BEGINNING;
            case CENTER:
                return SWT.CENTER;
            case END:
                return SWT.END;
            case FILL:
                return SWT.FILL;
            default:
                return SWT.NONE;
        }
    }


    /**
     * Gets the attribute from the entry.
     * <p>
     * The attribute type description from the associated 
     * widget is used to get the attribute from the entry.
     *
     * @return
     *      the attribute from the entry
     */
    protected IAttribute getAttribute()
    {
        if ( ( getEntry() != null ) && ( getWidget() != null ) )
        {
            return getEntry().getAttribute( getWidget().getAttributeType() );
        }

        return null;
    }


    /**
     * Updates the attribute's value on the entry.
     *
     * @param value
     *      the value
     */
    protected void updateAttributeValue( Object value )
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute == null ) )
        {
            if ( !"".equals( value ) ) //$NON-NLS-1$
            {
                // Creating a new attribute with the value
                addNewAttribute( value );
            }
        }
        else
        {
            if ( !"".equals( value ) ) //$NON-NLS-1$
            {
                // Modifying the existing attribute
                modifyAttributeValue( value );
            }
            else
            {
                // Deleting the attribute
                deleteAttribute();
            }
        }
    }


    /**
     * Adds a new attribute (based on the attribute type value from the widget)
     * with the given value.
     *
     * @param value
     *      the value
     */
    protected void addNewAttribute( Object value )
    {
        if ( ( getEntry() != null ) && ( getWidget() != null ) )
        {
            Attribute newAttribute = new Attribute( getEntry(), getWidget().getAttributeType() );
            newAttribute.addValue( new Value( newAttribute, value ) );
            getEntry().addAttribute( newAttribute );
        }
    }


    /**
     * Modifies the attribute value with the given one.
     *
     * @param value
     *      the value
     */
    protected void modifyAttributeValue( Object value )
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.getValueSize() > 0 ) )
        {
            attribute.deleteValue( attribute.getValues()[0] );
            attribute.addValue( new Value( attribute, value ) );
        }
    }


    /**
     * Deletes the attribute.
     */
    protected void deleteAttribute()
    {
        if ( ( getEntry() != null ) && ( getWidget() != null ) && ( getAttribute() != null ) )
        {
            getEntry().deleteAttribute( getAttribute() );
        }
    }


    /**
     * Adds a value to the attribute.
     *
     * @param value
     *      the value to add
     */
    protected void addAttributeValue( String value )
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.isString() ) )
        {
            attribute.addValue( new Value( attribute, value ) );
        }
        else
        {
            addNewAttribute( value );
        }
    }


    /**
     * Deletes a value from the attribute.
     *
     * @param value
     *      the value to delete
     */
    protected void deleteAttributeValue( String value )
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.isString() ) && ( attribute.getValueSize() > 0 ) )
        {
            for ( IValue attributeValue : attribute.getValues() )
            {
                if ( attributeValue.getStringValue().equals( value ) )
                {
                    attribute.deleteValue( attributeValue );
                    break;
                }
            }
        }
    }
}
