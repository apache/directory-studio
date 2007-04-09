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
package org.apache.directory.ldapstudio.aciitemeditor.dialogs;


import java.util.List;

import org.apache.directory.ldapstudio.aciitemeditor.ACIItemValueWithContext;
import org.apache.directory.ldapstudio.aciitemeditor.Activator;
import org.apache.directory.ldapstudio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;


/**
 * Dialog to edit user classes or protected items with multiple values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MultiValuedDialog extends Dialog
{
    /** The dialog title */
    private String displayName;

    /** The value editor */
    private AbstractDialogStringValueEditor valueEditor;

    /** The values, may be empty. */
    private List<String> values;

    /** The context */
    private ACIItemValueWithContext context;

    /** The inner composite for all the content */
    private Composite composite = null;

    /** The table control for the table viewer */
    private Table table = null;

    /** The table viewer containing all user classes */
    private TableViewer tableViewer = null;

    /** The composite containing the buttons */
    private Composite buttonComposite = null;

    /** The add button */
    private Button addButton = null;

    /** The edit button */
    private Button editButton = null;

    /** The delete button */
    private Button deleteButton = null;


    /**
     * Creates a new instance of MultiValuedDialog.
     *
     * @param parentShell the shell
     * @param displayName the display name of the edited element
     * @param values a modifyable list of values
     * @param context the context
     * @param valueEditor the detail value editor
     */
    public MultiValuedDialog( Shell parentShell, String displayName, List<String> values,
        ACIItemValueWithContext context, AbstractDialogStringValueEditor valueEditor )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );

        this.displayName = displayName;
        this.values = values;
        this.context = context;
        this.valueEditor = valueEditor;
    }


    /**
     * {@inheritDoc}
     * 
     * Sets the dialog title.
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString("MultiValuedDialog.dialog.titlePrefix") + displayName ); //$NON-NLS-1$
        shell.setImage( Activator.getDefault().getImage( Messages.getString("MultiValuedDialog.dialog.icon") ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     * 
     * Creates only a OK button.
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
    }


    /** 
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        // create composite
        composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        composite.setLayoutData( gd );
        GridLayout layout = ( GridLayout ) composite.getLayout();
        layout.makeColumnsEqualWidth = false;
        layout.numColumns = 2;

        createTable();

        createButtonComposite();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * This method initializes table and table viewer
     */
    private void createTable()
    {
        GridData tableGridData = new GridData( GridData.FILL_BOTH );
        tableGridData.grabExcessHorizontalSpace = true;
        tableGridData.verticalAlignment = GridData.FILL;
        tableGridData.horizontalAlignment = GridData.FILL;
        //tableGridData.heightHint = 100;

        table = new Table( composite, SWT.BORDER );
        table.setHeaderVisible( false );
        table.setLayoutData( tableGridData );
        table.setLinesVisible( false );
        tableViewer = new TableViewer( table );
        tableViewer.setContentProvider( new ArrayContentProvider() );
        tableViewer.setLabelProvider( new LabelProvider() );
        tableViewer.setInput( values );

        tableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                valueSelected();
            }
        } );

        tableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                editValue();
            }
        } );
    }


    /**
     * This method initializes buttons  
     */
    private void createButtonComposite()
    {
        GridData deleteButtonGridData = new GridData();
        deleteButtonGridData.horizontalAlignment = GridData.FILL;
        deleteButtonGridData.grabExcessHorizontalSpace = false;
        deleteButtonGridData.verticalAlignment = GridData.BEGINNING;
        deleteButtonGridData.widthHint = Activator.getButtonWidth( composite );

        GridData editButtonGridData = new GridData();
        editButtonGridData.horizontalAlignment = GridData.FILL;
        editButtonGridData.grabExcessHorizontalSpace = false;
        editButtonGridData.verticalAlignment = GridData.BEGINNING;
        editButtonGridData.widthHint = Activator.getButtonWidth( composite );

        GridData addButtonGridData = new GridData();
        addButtonGridData.horizontalAlignment = GridData.FILL;
        addButtonGridData.grabExcessHorizontalSpace = false;
        addButtonGridData.verticalAlignment = GridData.BEGINNING;
        addButtonGridData.widthHint = Activator.getButtonWidth( composite );

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

        addButton = new Button( buttonComposite, SWT.NONE );
        addButton.setText( Messages.getString( "MultiValuedDialog.button.add" ) ); //$NON-NLS-1$
        addButton.setLayoutData( addButtonGridData );
        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addValue();
            }
        } );

        editButton = new Button( buttonComposite, SWT.NONE );
        editButton.setText( Messages.getString( "MultiValuedDialog.button.edit" ) ); //$NON-NLS-1$
        editButton.setLayoutData( editButtonGridData );
        editButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                editValue();
            }
        } );
        editButton.setEnabled( false );

        deleteButton = new Button( buttonComposite, SWT.NONE );
        deleteButton.setText( Messages.getString( "MultiValuedDialog.button.delete" ) ); //$NON-NLS-1$
        deleteButton.setLayoutData( deleteButtonGridData );
        deleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                deleteValue();
            }
        } );
        deleteButton.setEnabled( false );

    }


    /**
     * Opens the editor and adds the new value to the list.
     */
    private void addValue()
    {
        Object oldRawValue = valueEditor.getRawValue( context.getConnection(), "" ); //$NON-NLS-1$

        CellEditor cellEditor = valueEditor.getCellEditor();
        cellEditor.setValue( oldRawValue );
        cellEditor.activate();
        Object newRawValue = cellEditor.getValue();

        if ( newRawValue != null )
        {
            String newValue = ( String ) valueEditor.getStringOrBinaryValue( newRawValue );

            values.add( newValue );
            tableViewer.refresh();
        }
    }


    /**
     * Opens the editor with the currently selected
     * value and puts the modified value into the list.
     */
    private void editValue()
    {
        String oldValue = getSelectedValue();
        if ( oldValue != null )
        {
            Object oldRawValue = valueEditor.getRawValue( context.getConnection(), oldValue );

            CellEditor cellEditor = valueEditor.getCellEditor();
            cellEditor.setValue( oldRawValue );
            cellEditor.activate();
            Object newRawValue = cellEditor.getValue();

            if ( newRawValue != null )
            {
                String newValue = ( String ) valueEditor.getStringOrBinaryValue( newRawValue );

                values.remove( oldValue );
                values.add( newValue );
                tableViewer.refresh();
            }
        }
    }


    /**
     * Deletes the currently selected value from list.
     */
    private void deleteValue()
    {
        String value = getSelectedValue();
        if ( value != null )
        {
            values.remove( value );
            tableViewer.refresh();
        }
    }


    /**
     * Called when value is selected in table viewer.
     * Updates the enabled/disabled state of the buttons.
     */
    private void valueSelected()
    {
        String value = getSelectedValue();

        if ( value == null )
        {
            editButton.setEnabled( false );
            deleteButton.setEnabled( false );
        }
        else
        {
            editButton.setEnabled( true );
            deleteButton.setEnabled( true );
        }
    }


    /**
     * @return the value that is selected in the table viewer, or null.
     */
    private String getSelectedValue()
    {
        String value = null;

        IStructuredSelection selection = ( IStructuredSelection ) tableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            Object element = selection.getFirstElement();
            if ( element instanceof String )
            {
                value = ( String ) element;
            }
        }

        return value;
    }

}
