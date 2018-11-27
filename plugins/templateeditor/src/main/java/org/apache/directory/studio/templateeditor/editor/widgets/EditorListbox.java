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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateListbox;
import org.apache.directory.studio.templateeditor.model.widgets.ValueItem;


/**
 * This class implements an editor list box.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorListbox extends EditorWidget<TemplateListbox>
{
    /** The list viewer */
    private ListViewer listViewer;

    /** The selection listener */
    private ISelectionChangedListener selectionListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) listViewer.getSelection();
            if ( !selection.isEmpty() )
            {
                // Deleting the old attribute
                deleteAttribute();

                // Re-creating the attribute with the selected values
                Iterator<?> iterator = selection.iterator();
                while ( iterator.hasNext() )
                {
                    ValueItem item = ( ValueItem ) iterator.next();
                    addAttributeValue( ( String ) item.getValue() );
                }
            }
        }
    };


    /**
     * Creates a new instance of EditorListbox.
     * 
     * @param editor
     *      the associated editor
     * @param templateListbox
     *            the associated template list box
     * @param toolkit
     *      the associated toolkit
     */
    public EditorListbox( IEntryEditor editor, TemplateListbox templateListbox, FormToolkit toolkit )
    {
        super( templateListbox, editor, toolkit );
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
     *            the parent composite
     * @return the associated composite
     */
    private Composite initWidget( Composite parent )
    {
        // Getting the style of the listbox
        int style = SWT.BORDER /*| SWT.V_SCROLL | SWT.H_SCROLL*/;
        if ( getWidget().isMultipleSelection() )
        {
            style |= SWT.MULTI;
        }
        else
        {
            style |= SWT.SINGLE;
        }

        // Creating the list
        List list = new List( parent, style );
        list.setLayoutData( getGridata() );

        // Creating the associated viewer
        listViewer = new ListViewer( list );
        listViewer.getList().setEnabled( getWidget().isEnabled() );
        listViewer.setContentProvider( new ArrayContentProvider() );
        listViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                return ( ( ValueItem ) element ).getLabel();
            }
        } );

        listViewer.setInput( getWidget().getItems() );

        return parent;
    }


    /**
     * Updates the widget's content.
     */
    private void updateWidget()
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.getValueSize() > 0 ) )
        {
            // Registering the values of the items in a map for easy
            // access
            Map<Object, ValueItem> itemsMap = new HashMap<Object, ValueItem>();
            for ( ValueItem valueItem : getWidget().getItems() )
            {
                itemsMap.put( valueItem.getValue(), valueItem );
            }

            // Creating a list of the selected objects
            java.util.List<ValueItem> selectedList = new ArrayList<ValueItem>();

            // Checking each value
            for ( IValue value : attribute.getValues() )
            {
                ValueItem valueItem = itemsMap.get( value.getRawValue() );
                if ( valueItem != null )
                {
                    selectedList.add( valueItem );
                }
            }

            // Setting the selection to the viewer
            if ( selectedList.size() > 0 )
            {
                listViewer.setSelection( new StructuredSelection( selectedList.toArray() ) );
            }
        }
        else
        {
            listViewer.setSelection( null );
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        listViewer.addSelectionChangedListener( selectionListener );
    }


    /**
    * Adds the listeners.
    */
    private void removeListeners()
    {
        listViewer.removeSelectionChangedListener( selectionListener );
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
        // Nothing to do
    }
}