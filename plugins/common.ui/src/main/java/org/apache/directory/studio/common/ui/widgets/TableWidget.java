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
import org.eclipse.swt.widgets.Label;
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
    
    /** The current selection index, if any */
    private int currentSelection;

    /** A flag set to tell if we have a Edit button */
    private boolean hasEdit;
    
    /** A flag set when the table is ordered (ie, it has a Up and Down buttons) */
    private boolean isOrdered;
    
    /** A flag that says if teh table is enabled or disabled */
    private boolean isEnabled = true;
    
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
            if ( isEnabled )
            {
                int selectionLine = elementTableViewer.getTable().getSelectionIndex();

                if ( selectionLine == currentSelection )
                {
                    // We have selected the line twice, deselect the line
                    elementTableViewer.getTable().deselect( selectionLine );
                    currentSelection = -1;
                }
                else
                {
                    currentSelection = selectionLine;
                    StructuredSelection selection = ( StructuredSelection ) elementTableViewer.getSelection();
        
                    if ( hasEdit )
                    {
                        editButton.setEnabled( !selection.isEmpty() );
                    }
                    
                    deleteButton.setEnabled( !selection.isEmpty() );
                    
                    if ( isOrdered )
                    {
                        // We can't enable the UP button when we don't have any element in the table,
                        // or when we have only one, or when the selection is the first one in the table
                        upButton.setEnabled( !selection.isEmpty() && ( elements.size() > 1 ) && ( selectionLine > 0 ) );
                        
                        // We can't enable the DOWN button when we don't have any element in the table,
                        // or when we have only one element, or when the selected element is the last one
                        downButton.setEnabled( !selection.isEmpty() && ( elements.size() > 1 ) && ( selectionLine < elements.size() - 1 ) );
                    }
                }
            }
        }
    };
    
    
    // A listener on the Element table, that reacts to a doubleClick : it's opening the Element editor
    private IDoubleClickListener tableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            if ( isEnabled )
            {
                editElement();
            }
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
     * @param decorator the decorator to use, containing the Dialog comparator and labelProvider
     */
    public TableWidget( TableDecorator<E> decorator )
    {
        this.decorator = decorator;
        currentSelection = -1;
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
        
        // First, define a grid of 3 columns (two for the table, one for the buttons)
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
            // If it's an ordered table, we add 3 line s: one for Up, one for Down and one for the separator
            nbLinesSpan += 3;
        }
        
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, nbLinesSpan );
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

        GridData buttonGd = new GridData( SWT.FILL, SWT.FILL, false, false, 1, 1 );
        buttonGd.widthHint = 60;

        // Create the Add Button and its listener
        if ( toolkit != null )
        {
            addButton = toolkit.createButton( composite, Messages.getString( "CommonUIWidgets.AddButton" ), SWT.PUSH );
            addButton.setLayoutData( buttonGd );
        }
        else
        {
            addButton = BaseWidgetUtils.createButton( composite, Messages.getString( "CommonUIWidgets.AddButton" ), 1 );
            addButton.setLayoutData( buttonGd );
        }
        
        addButton.setLayoutData( buttonGd );
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
            editButton.setLayoutData( buttonGd );
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
        deleteButton.setLayoutData( buttonGd );
        deleteButton.addSelectionListener( deleteButtonListener );

        // Create the Up and Down button, if requested
        if ( isOrdered )
        {
            Label separator = BaseWidgetUtils.createSeparator( composite, 1 );
            separator.setLayoutData( new GridData( SWT.NONE, SWT.BEGINNING, false, false ) );

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
            upButton.setLayoutData( buttonGd );
            //upButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
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
            downButton.setLayoutData( buttonGd );
            //downButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
            downButton.addSelectionListener( downButtonListener );
        }
    }

    
    /**
     * Enable the table (Buttons will be active, except the Edit and Delete ones, actions on the table will be active)
     */
    public void enable()
    {
        if ( addButton != null )
        {
            addButton.setEnabled( true );
        }
        
        if ( upButton != null )
        {
            upButton.setEnabled( true );
        }
        
        if ( downButton != null )
        {
            downButton.setEnabled( true );
        }
        
        isEnabled = true;
    }
    

    /**
     * Disable the table (Buttons will be inactive, actions on the table will be inactive)
     */
    public void disable()
    {
        if ( addButton != null )
        {
            addButton.setEnabled( false );
        }
        
        if ( deleteButton != null )
        {
            deleteButton.setEnabled( false );
        }
        
        if ( editButton != null )
        {
            editButton.setEnabled( false );
        }
        
        if ( upButton != null )
        {
            upButton.setEnabled( false );
        }
        
        if ( downButton != null )
        {
            downButton.setEnabled( false );
        }
        
        isEnabled = false;
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
        this.elements.clear();
        
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
        int insertionPos = elements.size();
        
        if ( !selection.isEmpty() )
        {
            insertionPos = elementTableViewer.getTable().getSelectionIndex();
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
                    // The table is ordered, insert the element at the right position
                    ((OrderedElement)newElement).setPrefix( insertionPos );
                    elements.add( insertionPos, newElement );
                    
                    // Move up the following elements
                    for ( int i = insertionPos + 1; i < elements.size(); i++ )
                    {
                        E element = elements.get( i );
                        ((OrderedElement)element).incrementPrefix();
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

                    elements.add( pos, newElement );
                }
                
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
                else
                {
                    // Remove the original element
                    elements.remove( selectedElement );

                    elements.add( editPosition, newElement );
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
            // If the table is ordered, we need to decrement the prefix of all the following elements
            if ( isOrdered )
            { 
                int selectedPosition = elementTableViewer.getTable().getSelectionIndex();
                
                for ( int i = selectedPosition + 1; i < elements.size(); i++ )
                {
                    E nextElement = elements.get( i );
                    ((OrderedElement)nextElement).decrementPrefix();
                    elements.set( i - 1, nextElement );
                }
                
                elements.remove( elements.size() - 1 );
            }
            else
            {
                int selectedPosition = elementTableViewer.getTable().getSelectionIndex();
                elements.remove( selectedPosition );
            }
            
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
