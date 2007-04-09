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
package org.apache.directory.ldapstudio.aciitemeditor.widgets;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.directory.ldapstudio.aciitemeditor.ACIItemValueWithContext;
import org.apache.directory.ldapstudio.aciitemeditor.Activator;
import org.apache.directory.ldapstudio.aciitemeditor.dialogs.MultiValuedDialog;
import org.apache.directory.ldapstudio.aciitemeditor.model.ProtectedItemWrapper;
import org.apache.directory.ldapstudio.aciitemeditor.model.ProtectedItemWrapperFactory;
import org.apache.directory.ldapstudio.valueeditors.AbstractDialogStringValueEditor;
import org.apache.directory.shared.ldap.aci.ProtectedItem;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;


/**
 * This composite contains GUI elements to edit ACI item protected items.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIItemProtectedItemsComposite extends Composite
{

    /** The context. */
    private ACIItemValueWithContext context;
    
    /** The inner composite for all the content */
    private Composite composite = null;

    /** The description label */
    private Label label = null;

    /** The table control for the table viewer */
    private Table table = null;

    /** The table viewer containing all protected items */
    private CheckboxTableViewer tableViewer = null;

    /** The composite containing the buttons */
    private Composite buttonComposite = null;

    /** The edit button */
    private Button editButton = null;

    /** The select all button */
    private Button selectAllButton = null;

    /** The deselect all button */
    private Button deselectAllButton = null;

    /** The reverse button */
    private Button reverseSelectionButton = null;

    /** The possible protected items, used as input for the table viewer */
    private ProtectedItemWrapper[] protectedItemWrappers = ProtectedItemWrapperFactory.createProtectedItemWrappers(); 
        
        
    /**
     * Creates a new instance of ACIItemProtectedItemsComposite.
     *
     * @param parent
     * @param style
     */
    public ACIItemProtectedItemsComposite( Composite parent, int style )
    {
        super( parent, style );

        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout( layout );

        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = GridData.CENTER;
        setLayoutData( layoutData );

        createComposite();
    }


    /**
     * This method initializes composite	
     *
     */
    private void createComposite()
    {

        GridData labelGridData = new GridData();
        labelGridData.horizontalSpan = 2;
        labelGridData.verticalAlignment = GridData.CENTER;
        labelGridData.grabExcessHorizontalSpace = true;
        labelGridData.horizontalAlignment = GridData.FILL;

        GridLayout gridLayout = new GridLayout();
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.numColumns = 2;

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalSpan = 1;
        gridData.verticalAlignment = GridData.BEGINNING;

        composite = new Composite( this, SWT.NONE );
        composite.setLayoutData( gridData );
        composite.setLayout( gridLayout );

        label = new Label( composite, SWT.NONE );
        label.setText( Messages.getString( "ACIItemProtectedItemsComposite.description" ) ); //$NON-NLS-1$
        label.setLayoutData( labelGridData );

        createTable();

        createButtonComposite();
    }


    /**
     * This method initializes table and table viewer
     *
     */
    private void createTable()
    {
        GridData tableGridData = new GridData();
        tableGridData.grabExcessHorizontalSpace = true;
        tableGridData.verticalAlignment = GridData.FILL;
        tableGridData.horizontalAlignment = GridData.FILL;
        //tableGridData.heightHint = 100;

        table = new Table( composite, SWT.BORDER | SWT.CHECK );
        table.setHeaderVisible( false );
        table.setLayoutData( tableGridData );
        table.setLinesVisible( false );
        tableViewer = new CheckboxTableViewer( table );
        tableViewer.setContentProvider( new ArrayContentProvider() );
        tableViewer.setLabelProvider( new ProtectedItemsLabelProvider() );
        tableViewer.setInput( protectedItemWrappers );

        tableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                protectedItemSelected();
            }
        } );
        tableViewer.addCheckStateListener( new ICheckStateListener()
        {
            public void checkStateChanged( CheckStateChangedEvent event )
            {
                protectedItemChecked();
            }
        } );
        tableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                if ( editButton.isEnabled() )
                {
                    editProtectedItem();
                }
            }
        } );
    }


    /**
     * This method initializes buttons	
     *
     */
    private void createButtonComposite()
    {
        GridData reverseSelectionButtonGridData = new GridData();
        reverseSelectionButtonGridData.horizontalAlignment = GridData.FILL;
        reverseSelectionButtonGridData.grabExcessHorizontalSpace = false;
        reverseSelectionButtonGridData.verticalAlignment = GridData.BEGINNING;
        reverseSelectionButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridData deselectAllButtonGridData = new GridData();
        deselectAllButtonGridData.horizontalAlignment = GridData.FILL;
        deselectAllButtonGridData.grabExcessHorizontalSpace = false;
        deselectAllButtonGridData.verticalAlignment = GridData.BEGINNING;
        deselectAllButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridData selectAllButtonGridData = new GridData();
        selectAllButtonGridData.horizontalAlignment = GridData.FILL;
        selectAllButtonGridData.grabExcessHorizontalSpace = false;
        selectAllButtonGridData.verticalAlignment = GridData.BEGINNING;
        selectAllButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridData editButtonGridData = new GridData();
        editButtonGridData.horizontalAlignment = GridData.FILL;
        editButtonGridData.grabExcessHorizontalSpace = false;
        editButtonGridData.verticalAlignment = GridData.BEGINNING;
        editButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = false;
        gridData.verticalAlignment = GridData.FILL;

        buttonComposite = new Composite( composite, SWT.NONE );
        buttonComposite.setLayoutData( gridData );
        buttonComposite.setLayout( gridLayout );

        editButton = new Button( buttonComposite, SWT.NONE );
        editButton.setText( Messages.getString( "ACIItemProtectedItemsComposite.edit.button" ) ); //$NON-NLS-1$
        editButton.setLayoutData( editButtonGridData );
        editButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                editProtectedItem();
            }
        } );
        editButton.setEnabled( false );

        selectAllButton = new Button( buttonComposite, SWT.NONE );
        selectAllButton.setText( Messages.getString( "ACIItemProtectedItemsComposite.selectAll.button" ) ); //$NON-NLS-1$
        selectAllButton.setLayoutData( selectAllButtonGridData );
        selectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                tableViewer.setCheckedElements( protectedItemWrappers );
                refreshTable();
            }
        } );

        deselectAllButton = new Button( buttonComposite, SWT.NONE );
        deselectAllButton.setText( Messages.getString( "ACIItemProtectedItemsComposite.deselectAll.button" ) ); //$NON-NLS-1$
        deselectAllButton.setLayoutData( deselectAllButtonGridData );
        deselectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                tableViewer.setCheckedElements( new ProtectedItem[0] );
                refreshTable();
            }
        } );

        reverseSelectionButton = new Button( buttonComposite, SWT.NONE );
        reverseSelectionButton.setText( Messages.getString( "ACIItemProtectedItemsComposite.revers.button" ) ); //$NON-NLS-1$
        reverseSelectionButton.setLayoutData( reverseSelectionButtonGridData );
        reverseSelectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                List<Object> elements = new ArrayList<Object>();
                elements.addAll( Arrays.asList( protectedItemWrappers ) );
                elements.removeAll( Arrays.asList( tableViewer.getCheckedElements() ) );
                tableViewer.setCheckedElements( elements.toArray() );
                refreshTable();
            }
        } );

    }

    /**
     * The label provider used for this table viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class ProtectedItemsLabelProvider extends LabelProvider
    {
        /**
         * Returns the error icon if the protected item is checked and invalid.
         */
        public Image getImage( Object element )
        {
            if ( element instanceof ProtectedItemWrapper )
            {
                ProtectedItemWrapper wrapper = ( ProtectedItemWrapper ) element;
                if ( tableViewer.getChecked( wrapper ) )
                {
                    try
                    {
                        wrapper.getProtectedItem();
                    }
                    catch ( ParseException e )
                    {
                        return Activator.getDefault().getImage(
                            Messages.getString( "ACIItemProtectedItemsComposite.error.icon" ) ); //$NON-NLS-1$
                    }
                }
            }

            return null;
        }
    }

    
    /**
     * Sets the context.
     * 
     * @param context the context
     */
    public void setContext( ACIItemValueWithContext context ) 
    {
        this.context = context;
    }
    

    /**
     * Sets the protected items. 
     *
     * @param protectedItems
     */
    public void setProtectedItems( Collection<ProtectedItem> protectedItems )
    {
        // reset first
        for ( ProtectedItemWrapper protectedItemWrapper : protectedItemWrappers )
        {
            tableViewer.setChecked( protectedItemWrapper, false );
        }

        for ( ProtectedItem item : protectedItems )
        {
            for ( ProtectedItemWrapper protectedItemWrapper : protectedItemWrappers )
            {
                
                if ( protectedItemWrapper.getClazz() == item.getClass() )
                {
                    protectedItemWrapper.setProtectedItem( item );
                    tableViewer.setChecked( protectedItemWrapper, true );
                }
            }
        }

        refreshTable();
    }


    /**
     * Returns the protected items as selected by the user.
     *
     * @return the protected items
     * @throws ParseException if the protected items or its values are not valid.
     */
    public Collection<ProtectedItem> getProtectedItems() throws ParseException
    {

        Collection<ProtectedItem> protectedItems = new ArrayList<ProtectedItem>();

        for ( ProtectedItemWrapper protectedItemWrapper : protectedItemWrappers )
        {
            if ( tableViewer.getChecked( protectedItemWrapper ) )
            {
                ProtectedItem protectedItem = protectedItemWrapper.getProtectedItem();
                protectedItems.add( protectedItem );
            }
        }

        return protectedItems;
    }


    /**
     * Shows or hides this composite.
     * 
     * @see org.eclipse.swt.widgets.control#setVisible(boolean)
     */
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
        ( ( GridData ) getLayoutData() ).heightHint = visible ? -1 : 0;
    }


    /**
     * 
     * @return the protected item that is selected in the table viewer, or null.
     */
    private ProtectedItemWrapper getSelectedProtectedItemWrapper()
    {
        ProtectedItemWrapper protectedItemWrapper = null;

        IStructuredSelection selection = ( IStructuredSelection ) tableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            Object element = selection.getFirstElement();
            if ( element instanceof ProtectedItemWrapper )
            {
                protectedItemWrapper = ( ProtectedItemWrapper ) element;
            }
        }

        return protectedItemWrapper;
    }


    /**
     * Called, when a protected item is selected in the table viewer.
     * - enables/disables the edit button
     *
     */
    private void protectedItemSelected()
    {
        ProtectedItemWrapper protectedItemWrapper = getSelectedProtectedItemWrapper();

        if ( protectedItemWrapper == null || !protectedItemWrapper.isEditable() )
        {
            editButton.setEnabled( false );
        }
        else
        {
            editButton.setEnabled( true );
        }
    }


    /**
     * Called, when a protected item checkbox is checked or unchecked.
     */
    private void protectedItemChecked()
    {
        refreshTable();
    }


    /**
     * Called, when pushing the edit button. Opens the text editor.
     *
     */
    private void editProtectedItem()
    {
        ProtectedItemWrapper protectedItemWrapper = getSelectedProtectedItemWrapper();
        
        AbstractDialogStringValueEditor valueEditor = protectedItemWrapper.getValueEditor();
        if(valueEditor != null)
        {
            if(protectedItemWrapper.isMultivalued())
            {
                MultiValuedDialog dialog = new MultiValuedDialog( getShell(), protectedItemWrapper.getDisplayName(), protectedItemWrapper.getValues(), context, valueEditor );
                dialog.open();
                refreshTable();
            }
            else
            {
                List<String> values = protectedItemWrapper.getValues();
                String oldValue = values.isEmpty() ? null : values.get( 0 );
                if ( oldValue == null )
                {
                    oldValue = "";
                }
                Object oldRawValue = valueEditor.getRawValue( context.getConnection(), oldValue );
                
                CellEditor cellEditor = valueEditor.getCellEditor();
                cellEditor.setValue( oldRawValue );
                cellEditor.activate();
                Object newRawValue = cellEditor.getValue();
                
                if(newRawValue != null) 
                {
                    String newValue = (String) valueEditor.getStringOrBinaryValue( newRawValue );
                    
                    values.clear();
                    values.add( newValue );
                    tableViewer.refresh();
                }
            }
        }
    }
    
    
    /**
     * Refreshes the table viewer.
     */
    private void refreshTable()
    {
        tableViewer.refresh();
    }


}
