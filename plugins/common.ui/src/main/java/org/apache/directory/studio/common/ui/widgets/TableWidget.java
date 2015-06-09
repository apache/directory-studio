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
package org.apache.directory.studio.common.ui.widgets;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * The TableWidget provides a table viewer to add/edit/remove an element.
 *
 * <pre>
 * +--------------------------------------+
 * | Element 1                            | (Add... )
 * | Element 2                            | (Edit...)
 * |                                      | (Delete )
 * +--------------------------------------+
 * </pre>
 * 
 * The elements are ordered. 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TableWidget<E> extends AbstractWidget
{
    /** The element list */
    private List<E> elements = new ArrayList<E>();
    
    /** The associated Dialog for the addition or edition of elements */
    private AddEditDialog<E> elementDialog;

    /** The LabelProvider for the elements */
    private LabelProvider labelProvider;

    /** A flag set to tell if we have a Edit button */
    private boolean hasEdit;

    // UI widgets
    private Composite composite;
    private Table elementTable;
    private TableViewer elementTableViewer;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    // A listener on the Elements table, that modifies the button when an Element is selected
    private ISelectionChangedListener tableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) elementTableViewer.getSelection();

            if ( hasEdit )
            {
                editButton.setEnabled( !selection.isEmpty() );
            }
            
            deleteButton.setEnabled( !selection.isEmpty() );
        }
    };
    
    // A listener on the Element table, that reacts to a doubleClick : it's opening the Element editor
    private IDoubleClickListener tableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editElement();
        }
    };
    
    // A listener on the Add button, which opens the Element addition editor
    private SelectionListener addButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addElement();
        }
    };
    
    // A listener on the Edit button, that open the Element editor
    private SelectionListener editButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editElement();
        }
    };
    
    // A listener on the Delete button, which delete the selected Element
    private SelectionListener deleteButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteElement();
        }
    };


    /**
     * Creates a new instance of TableWidget.
     */
    public TableWidget()
    {
    }


    /**
     * Creates the Table widget. It's a Table and three buttons :
     * <pre>
     * +--------------------------------------+
     * | Element 1                            | (Add... )
     * | Element 2                            | (Edit...)
     * |                                      | (Delete )
     * +--------------------------------------+
     * </pre>
     * </pre>
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     */
    public void createWidgetWithEdit( Composite parent, FormToolkit toolkit )
    {
        createWidget( parent, toolkit, true );
    }
    
    
    /**
     * Creates the Table widget. It's a Table and two buttons :
     * <pre>
     * +--------------------------------------+
     * | Element 1                            | (Add... )
     * | Element 2                            | (Delete )
     * |                                      |
     * +--------------------------------------+
     * </pre>
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     */
    public void createWidgetNoEdit( Composite parent, FormToolkit toolkit )
    {
        createWidget( parent, toolkit, false );
    }


    /**
     * Creates the Table widget. It's a Table and two or three button :
     * <pre>
     * +--------------------------------------+
     * | Element 1                            | (Add... )
     * | Element 2                            | (Edit...)
     * |                                      | (Delete )
     * +--------------------------------------+
     * </pre>
     * or :
     * <pre>
     * +--------------------------------------+
     * | Element 1                            | (Add... )
     * | Element 2                            | (Delete )
     * |                                      |
     * +--------------------------------------+
     * </pre>
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     */
    private void createWidget( Composite parent, FormToolkit toolkit, boolean hasEdit )
    {
        this.hasEdit = hasEdit;
        
        // Composite
        if ( toolkit != null )
        {
            composite = toolkit.createComposite( parent );
        }
        else
        {
            composite = new Composite( parent, SWT.NONE );
        }
        
        // First, define a grid of 2 columns (one for the table, one for the buttons)
        GridLayout compositeGridLayout = new GridLayout( 2, false );
        compositeGridLayout.marginHeight = compositeGridLayout.marginWidth = 0;
        composite.setLayout( compositeGridLayout );

        // Create the Element Table and Table Viewer
        if ( toolkit != null )
        {
            elementTable = toolkit.createTable( composite, SWT.NULL );
        }
        else
        {
            elementTable = new Table( composite, SWT.NULL );
        }
        
        // Define the table size and height. It will span on 3 lines.
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        elementTable.setLayoutData( gd );
        
        // Create the index TableViewer
        elementTableViewer = new TableViewer( elementTable );
        elementTableViewer.setContentProvider( new ArrayContentProvider() );
        
        // The LabelProvider
        elementTableViewer.setLabelProvider( labelProvider );
        
        // Listeners : we want to catch changes and double clicks (if we have an edit button)
        if ( hasEdit )
        {
            elementTableViewer.addSelectionChangedListener( tableViewerSelectionChangedListener );
            elementTableViewer.addDoubleClickListener( tableViewerDoubleClickListener );
        }
        
        // Inject the existing elements
        elementTableViewer.setInput( elements );

        // Create the Add Button and its listener
        if ( toolkit != null )
        {
            addButton = toolkit.createButton( composite, "Add...", SWT.PUSH );
        }
        else
        {
            addButton = BaseWidgetUtils.createButton( composite, "Add...", 1 );
        }
        
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        addButton.addSelectionListener( addButtonListener );

        // Create the Edit Button and its listener, if requested
        if ( hasEdit )
        {
            if ( toolkit != null )
            {
                editButton = toolkit.createButton( composite, "Edit...", SWT.PUSH );
            }
            else
            {
                editButton = BaseWidgetUtils.createButton( composite, "Edit...", SWT.PUSH );
            }
            
            // It's not enabled unless we have selected an element
            editButton.setEnabled( false );
            editButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
            editButton.addSelectionListener( editButtonListener );
        }

        // Create the Delete Button and its listener
        if ( toolkit != null )
        {
            deleteButton = toolkit.createButton( composite, "Delete", SWT.PUSH );
        }
        else
        {
            deleteButton = BaseWidgetUtils.createButton( composite, "Delete", SWT.PUSH );
        }
        
        // It's not selected unless we have selected an index
        deleteButton.setEnabled( false );
        deleteButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        deleteButton.addSelectionListener( deleteButtonListener );
    }


    /**
     * Returns the primary control associated with this widget.
     *
     * @return the primary control associated with this widget.
     */
    public Control getControl()
    {
        return composite;
    }


    /**
     * Sets the Elements.
     *
     * @param elements the elements
     */
    public void setElements( List<E> elements )
    {
        if ( ( elements != null ) && ( elements.size() > 0 ) )
        {
            this.elements.addAll( elements );
        }

        elementTableViewer.refresh();
    }


    /**
     * Gets the elements.
     *
     * @return the elements
     */
    public List<E> getElements()
    {
        if ( elements != null )
        {
            List<E> copy = new ArrayList<E>( elements.size() );
            System.arraycopy( elements, 0, copy, 0, elements.size() );
            
            return copy;
        }
        
        return null;
    }


    /**
     * This method is called when the 'Add...' button is clicked.
     */
    private void addElement()
    {
        elementDialog.addNewElement();

        if ( elementDialog.open() == Dialog.OK )
        {
            E newElement = elementDialog.getNewElement();
            
            if ( !elements.contains( newElement ) )
            {
                String elementStr = newElement.toString();
                elements.add( newElement );
                elementTableViewer.refresh();
                elementTableViewer.setSelection( new StructuredSelection( elementStr ) );
            }
            
            notifyListeners();
        }
    }


    /**
     * This method is called when the 'Edit...' button is clicked
     * or the table viewer is double-clicked.
     */
    private void editElement()
    {
        StructuredSelection selection = ( StructuredSelection ) elementTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            E selectedElement = (E)selection.getFirstElement();
            elementDialog.setEditedElement( selectedElement );

            // Open the element dialog, with the selected index
            if ( elementDialog.open() == Dialog.OK )
            {
                E newElement = elementDialog.getNewElement();
                
                if ( elements.contains( newElement ) )
                {
                    // Remove the original element
                    elements.remove( selectedElement );
                    
                    // Replace the existing element with the new one
                    elements.remove( newElement );
                    elements.add( newElement );
                }
                else
                {
                    // We will remove the modified element, and replace it with the new element
                    elements.remove( selectedElement );
                    elements.add( newElement );
                }

                elementTableViewer.refresh();
                elementTableViewer.setSelection( new StructuredSelection( newElement.toString() ) );

                notifyListeners();
            }
        }
    }


    /**
     * This method is called when the 'Delete' button is clicked.
     */
    private void deleteElement()
    {
        StructuredSelection selection = ( StructuredSelection ) elementTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            E selectedElement = ( E ) selection.getFirstElement();

            elements.remove( selectedElement );
            elementTableViewer.refresh();
            notifyListeners();
        }
    }

    
    /**
     * @param elementDialog the elementDialog to set
     */
    public void setElementDialog( AddEditDialog<E> elementDialog )
    {
        this.elementDialog = elementDialog;
    }

    
    /**
     * @param labelProvider the labelProvider to set
     */
    public void setLabelProvider( LabelProvider labelProvider )
    {
        this.labelProvider = labelProvider;
    }
}
