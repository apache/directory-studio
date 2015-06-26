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
import java.util.Comparator;
import java.util.List;

import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.Messages;
import org.apache.directory.studio.common.ui.TableDecorator;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
 * <pre>
 * Note : This class contain codes from the Apache PDF box project ('sort' method)
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TableWidget<E> extends AbstractWidget
{
    /** The element list */
    private List<E> elements = new ArrayList<E>();

    /** A flag set to tell if we have a Edit button */
    private boolean hasEdit;
    
    /** A flag set when the table is ordered (ie, it has a Up and Down buttons) */
    private boolean isOrdered;
    
    /** The flag set when the table is ordered */
    private static final boolean ORDERED = true;
    
    /** The flag set when the table is not ordered */
    private static final boolean UNORDERED = false;

    // UI widgets
    private Composite composite;
    private Table elementTable;
    private TableViewer elementTableViewer;
    
    // The buttons
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Button upButton;
    private Button downButton;
    
    /** The decorator */
    private TableDecorator<E> decorator;

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
            
            if ( isOrdered )
            {
                int selectionLine = elementTableViewer.getTable().getSelectionIndex();

                // We can't enable the UP button when we don't have any element in the table,
                // or when we have only one, or when the selection is the first one in the table
                upButton.setEnabled( !selection.isEmpty() && ( elements.size() > 1 ) && ( selectionLine > 0 ) );
                
                // We can't enable the DOWN button when we don't have any element in the table,
                // or when we have only one element, or when the selected element is the last one
                downButton.setEnabled( !selection.isEmpty() && ( elements.size() > 1 ) && ( selectionLine < elements.size() - 1 ) );
            }
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
    
    
    // A listener on the Up button, that move the selected elemnt up one position
    private SelectionListener upButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            upElement();
        }
    };
    
    
    // A listener on the Down button, that move the selected element down one position
    private SelectionListener downButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            downElement();
        }
    };


    /**
     * Creates a new instance of TableWidget.
     * 
     * @param decorator the decoartor to use, containing the Dialog comparator and labelProvider
     */
    public TableWidget( TableDecorator<E> decorator )
    {
        this.decorator = decorator;
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
        createWidget( parent, toolkit, true, UNORDERED );
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
        createWidget( parent, toolkit, false, UNORDERED );
    }


    /**
     * Creates the ordered Table widget. It's a Table and five buttons :
     * <pre>
     * +--------------------------------------+
     * | Element 1                            | (Add... )
     * | Element 2                            | (Edit...)
     * |                                      | (Delete )
     * |                                      | ---------
     * |                                      | (Up... )
     * |                                      | (Down.. )
     * +--------------------------------------+
     * </pre>
     * The 'Up' and 'Down' buttons are used to order the elements.
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     */
    public void createOrderedWidgetWithEdit( Composite parent, FormToolkit toolkit )
    {
        createWidget( parent, toolkit, true, ORDERED );
    }


    /**
     * Creates the Table widget. It's a Table and four buttons :
     * <pre>
     * +--------------------------------------+
     * | Element 1                            | (Add... )
     * | Element 2                            | (Delete )
     * |                                      | ---------
     * |                                      | (Up... )
     * |                                      | (Down.. )
     * +--------------------------------------+
     * </pre>
     * The 'Up' and 'Down' buttons are used to order the elements.
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     */
    public void createOrderedWidgetNoEdit( Composite parent, FormToolkit toolkit )
    {
        createWidget( parent, toolkit, true, ORDERED );
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
     * If the table is ordered, we have two additional buttons to re-organize
     * the elements at will :
     * <pre>
     * ...
     * |                                      | ---------
     * |                                      | (Up... )
     * |                                      | (Down.. )
     * +--------------------------------------+
     * </pre>
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     * @param hasEdit the flag set when the Edit button is present
     * @param isOrdered the flag set when we have the Up and Down buttons
     */
    private void createWidget( Composite parent, FormToolkit toolkit, boolean hasEdit, boolean isOrdered )
    {
        this.hasEdit = hasEdit;
        this.isOrdered = isOrdered;
        
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
        
        // Define the table size and height. It will span on 3 to 5 lines,
        // depending on the number of buttons
        int nbLinesSpan = 3;
        
        if ( isOrdered )
        {
            nbLinesSpan += 2;
        }
        
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, nbLinesSpan );
        gd.heightHint = 20;
        gd.widthHint = 100;
        elementTable.setLayoutData( gd );
        
        // Create the index TableViewer
        elementTableViewer = new TableViewer( elementTable );
        elementTableViewer.setContentProvider( new ArrayContentProvider() );
        
        // The LabelProvider
        elementTableViewer.setLabelProvider( decorator );
        elementTableViewer.addSelectionChangedListener( tableViewerSelectionChangedListener );
        
        // Listeners : we want to catch changes and double clicks (if we have an edit button)
        if ( hasEdit )
        {
            elementTableViewer.addDoubleClickListener( tableViewerDoubleClickListener );
        }
        
        // Inject the existing elements
        elementTableViewer.setInput( elements );

        // Create the Add Button and its listener
        if ( toolkit != null )
        {
            addButton = toolkit.createButton( composite, Messages.getString( "CommonUIWidgets.AddButton" ), SWT.PUSH );
        }
        else
        {
            addButton = BaseWidgetUtils.createButton( composite, Messages.getString( "CommonUIWidgets.AddButton" ), 1 );
        }
        
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        addButton.addSelectionListener( addButtonListener );

        // Create the Edit Button and its listener, if requested
        if ( hasEdit )
        {
            if ( toolkit != null )
            {
                editButton = toolkit.createButton( composite, Messages.getString( "CommonUIWidgets.EditButton" ), SWT.PUSH );
            }
            else
            {
                editButton = BaseWidgetUtils.createButton( composite, Messages.getString( "CommonUIWidgets.EditButton" ), SWT.PUSH );
            }
            
            // It's not enabled unless we have selected an element
            editButton.setEnabled( false );
            editButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
            editButton.addSelectionListener( editButtonListener );
        }

        // Create the Delete Button and its listener
        if ( toolkit != null )
        {
            deleteButton = toolkit.createButton( composite, Messages.getString( "CommonUIWidgets.DeleteButton" ), SWT.PUSH );
        }
        else
        {
            deleteButton = BaseWidgetUtils.createButton( composite, Messages.getString( "CommonUIWidgets.DeleteButton" ), SWT.PUSH );
        }
        
        // It's not selected unless we have selected an index
        deleteButton.setEnabled( false );
        deleteButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        deleteButton.addSelectionListener( deleteButtonListener );

        // Create the Up and Down button, if requested
        if ( isOrdered )
        {

            // Create the Up Button and its listener
            if ( toolkit != null )
            {
                upButton = toolkit.createButton( composite, Messages.getString( "CommonUIWidgets.UpButton" ), SWT.PUSH );
            }
            else
            {
                upButton = BaseWidgetUtils.createButton( composite, Messages.getString( "CommonUIWidgets.UpButton" ), SWT.PUSH );
            }
            
            // It's not selected unless we have selected an index
            upButton.setEnabled( false );
            upButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
            upButton.addSelectionListener( upButtonListener );


            // Create the Down Button and its listener
            if ( toolkit != null )
            {
                downButton = toolkit.createButton( composite, Messages.getString( "CommonUIWidgets.DownButton" ), SWT.PUSH );
            }
            else
            {
                downButton = BaseWidgetUtils.createButton( composite, Messages.getString( "CommonUIWidgets.DownButton" ), SWT.PUSH );
            }
            
            // It's not selected unless we have selected an index
            downButton.setEnabled( false );
            downButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
            downButton.addSelectionListener( downButtonListener );
}
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

    /* --------------------------------------------------------------------------------------------------------------- */
    /* Taken from the Apache PdfBox project,                                                                           */
    /* @author UWe Pachler                                                                                             */
    /* --------------------------------------------------------------------------------------------------------------- */
    /**
     * Sorts the given list using the given comparator.
     * 
     * @param list list to be sorted
     * @param cmp comparator used to compare the object swithin the list
     */
    public static <T> void sort( List<T> list, Comparator<T> cmp )
    {
        int size = list.size();
        
        if ( size < 2 )
        {
            return;
        }
        
        quicksort( list, cmp, 0, size - 1 );
    }
    
    
    private static <T> void quicksort( List<T> list, Comparator<T> cmp, int left, int right )
    {
        if ( left < right )
        {
            int splitter = split( list, cmp, left, right );
            quicksort( list, cmp, left, splitter - 1 );
            quicksort( list, cmp, splitter + 1, right );
        }
    }
    
    
    private static <T> void swap( List<T> list, int i, int j )
    {
        T tmp = list.get( i );
        list.set( i, list.get( j ) );
        list.set( j, tmp );
    }
    
    
    private static <T> int split( List<T> list, Comparator<T> cmp, int left, int right )
    {
        int i = left;
        int j = right - 1;
        T pivot = list.get( right );
        
        do
        {
            while ( ( cmp.compare( list.get( i ), pivot ) <= 0 ) && ( i < right ) )
            {
                ++i;
            }
            
            while ( ( cmp.compare( pivot, list.get( j ) ) <= 0 ) && ( j > left ) )
            {
                --j;
            }
            
            if ( i < j )
            {
                swap( list, i, j );
            }
        } while ( i < j );
        
        if ( cmp.compare( pivot, list.get( i ) ) < 0 )
        {
            swap( list, i, right );
        }
        
        return i;
    }
    /* --------------------------------------------------------------------------------------------------------------- */
    /* End of the QuickSort implementation taken  from the Apache PdfBox project,                                      */
    /* --------------------------------------------------------------------------------------------------------------- */

    
    /**
     * Sets the Elements.
     *
     * @param elements the elements
     */
    public void setElements( List<E> elements )
    {
        if ( ( elements != null ) && ( elements.size() > 0 ) )
        {
            sort( elements, decorator );
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
            
            copy.addAll( elements );
            
            return copy;
        }
        
        return null;
    }


    /**
     * This method is called when the 'Add...' button is clicked.
     */
    private void addElement()
    {
        AddEditDialog<E> dialog = decorator.getDialog();
        dialog.setAdd();
        dialog.addNewElement();
        dialog.setElements( elements );
        
        // Inject the position if we have a selected value
        StructuredSelection selection = ( StructuredSelection ) elementTableViewer.getSelection();
        
        if ( !selection.isEmpty() )
        {
            int insertionPos = elementTableViewer.getTable().getSelectionIndex();
            
            dialog.setSelectedPosition( insertionPos );
        }
        else
        {
            // The element will be added at the end of the table
            dialog.setSelectedPosition( elements.size() );
        }

        // Open the Dialog, and process the addition if it went fine
        if ( decorator.getDialog().open() == Dialog.OK )
        {
            E newElement = decorator.getDialog().getEditedElement();
            
            if ( !elements.contains( newElement ) )
            {
                String elementStr = newElement.toString();
                int pos = 0;
                
                if ( isOrdered )
                { 
                    // The table is ordered, find the right position to insert the element
                    for ( E element : elements )
                    {
                        if ( decorator.compare( element, newElement ) > 0 )
                        {
                            break;
                        }
                        else
                        {
                            pos++;
                        }
                    }
                }
                else
                {
                    if ( selection.isEmpty() )
                    {
                        // no selected element, add at the end
                        pos = elements.size();
                    }
                    else
                    {
                        pos = elementTableViewer.getTable().getSelectionIndex() + 1;
                    }
                }
                
                elements.add( pos, newElement );
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
            AddEditDialog<E> dialog = decorator.getDialog();
            dialog.setEdit();

            E selectedElement = (E)selection.getFirstElement();
            int editPosition = elementTableViewer.getTable().getSelectionIndex();
            dialog.setEditedElement( selectedElement );
            dialog.setSelectedPosition( editPosition );

            // Open the element dialog, with the selected index
            if ( decorator.getDialog().open() == Dialog.OK )
            {
                E newElement = dialog.getEditedElement();
                
                if ( !isOrdered )
                {
                    // Check to see if the modified element does not already exist
                    if ( elements.contains( newElement ) )
                    {
                        // Remove the original element
                        elements.remove( selectedElement );
                        
                        // Replace the existing element with the new one
                        elements.remove( newElement );
    
                        int pos = 0;
                        
                        for ( E element : elements )
                        {
                            if ( decorator.compare( element, newElement ) > 0 )
                            {
                                break;
                            }
                            else
                            {
                                pos++;
                            }
                        }
                        
                        elements.add( pos, newElement );
                    }
                    else
                    {
                        // We will remove the modified element, and replace it with the new element
                        // Replace the old element by the new one
                        elements.remove( editPosition );
                        elements.add( editPosition, newElement );
                    }
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
     * This method is called when the 'Up...' button is clicked
     */
    private void upElement()
    {
        StructuredSelection selection = ( StructuredSelection ) elementTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            // Get the line the selected element is in. We will move it up 
            int selectionLine = elementTableViewer.getTable().getSelectionIndex();
            
            // The selected element
            E selectedElement = (E)selection.getFirstElement();
            
            // Decrease the prefix
            ((OrderedElement)selectedElement).decrementPrefix();
            
            // Just swap the elements which is just before with the selected one
            E previousElement = getElements().get( selectionLine - 1 );

            // Increase the prefix
            ((OrderedElement)previousElement).incrementPrefix();
            
            elements.remove( selectionLine - 1 );
            elements.add( selectionLine, previousElement );
            
            // Refresh the table now
            elementTableViewer.refresh();
            elementTableViewer.setSelection( new StructuredSelection( selectedElement ) );

            notifyListeners();
        }
    }


    /**
     * This method is called when the 'Down...' button is clicked
     * or the table viewer is double-clicked.
     */
    private void downElement()
    {
        StructuredSelection selection = ( StructuredSelection ) elementTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            // Get the line the selected element is in. We will move it down 
            int selectionLine = elementTableViewer.getTable().getSelectionIndex();
            
            // The selected element
            E selectedElement = (E)selection.getFirstElement();

            // Increase the prefix
            ((OrderedElement)selectedElement).incrementPrefix();
            
            // Just swap the elements which is just after with the selected one
            E previousElement = getElements().get( selectionLine + 1 );

            // Decrease the prefix
            ((OrderedElement)previousElement).decrementPrefix();

            elements.remove( selectionLine + 1 );
            elements.add( selectionLine, previousElement );
            
            // refresh the table now
            elementTableViewer.refresh();
            elementTableViewer.setSelection( new StructuredSelection( selectedElement ) );

            notifyListeners();
        }
    }
}
