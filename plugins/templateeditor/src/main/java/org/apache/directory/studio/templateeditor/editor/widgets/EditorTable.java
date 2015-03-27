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


import java.util.Comparator;

import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateTable;


/**
 * This class implements an editor table.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorTable extends EditorWidget<TemplateTable>
{
    /** The widget's composite */
    private Composite composite;

    /** The table viewer */
    private TableViewer tableViewer;

    /** The 'Add...' button */
    private ToolItem addToolItem;

    /** The 'Edit...' button */
    private ToolItem editToolItem;

    /** The 'Delete...' button */
    private ToolItem deleteToolItem;


    /**
     * Creates a new instance of EditorTable.
     * 
     * @param editor
     *      the associated editor
     * @param templateTable
     *      the associated template table
     * @param toolkit
     *      the associated toolkit
     */
    public EditorTable( IEntryEditor editor, TemplateTable templateTable, FormToolkit toolkit )
    {
        super( templateTable, editor, toolkit );
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
        // Creating the widget composite
        composite = getToolkit().createComposite( parent );
        composite.setLayoutData( getGridata() );

        // Creating the layout
        GridLayout gl = new GridLayout( ( needsToolbar() ? 2 : 1 ), false );
        gl.marginHeight = gl.marginWidth = 0;
        gl.horizontalSpacing = gl.verticalSpacing = 0;
        composite.setLayout( gl );

        // Table Viewer
        Table table = getToolkit().createTable( composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        table.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        tableViewer = new TableViewer( table );
        tableViewer.setContentProvider( new ArrayContentProvider() );
        tableViewer.setComparator( new ViewerComparator( new Comparator<String>()
        {
            public int compare( String s1, String s2 )
            {
                if ( s1 == null )
                {
                    return 1;
                }
                else if ( s2 == null )
                {
                    return -1;
                }
                else
                {
                    return s1.compareToIgnoreCase( s2 );
                }
            }
        } ) );

        // Toolbar (if needed)
        if ( needsToolbar() )
        {
            ToolBar toolbar = new ToolBar( composite, SWT.VERTICAL );
            toolbar.setLayoutData( new GridData( SWT.NONE, SWT.FILL, false, true ) );

            // Add Button
            if ( getWidget().isShowAddButton() )
            {
                addToolItem = new ToolItem( toolbar, SWT.PUSH );
                addToolItem.setToolTipText( Messages.getString( "EditorTable.Add" ) ); //$NON-NLS-1$
                addToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                    EntryTemplatePluginConstants.IMG_TOOLBAR_ADD_VALUE ) );
            }

            // Edit Button
            if ( getWidget().isShowEditButton() )
            {
                editToolItem = new ToolItem( toolbar, SWT.PUSH );
                editToolItem.setToolTipText( Messages.getString( "EditorTable.Edit" ) ); //$NON-NLS-1$
                editToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                    EntryTemplatePluginConstants.IMG_TOOLBAR_EDIT_VALUE ) );
                editToolItem.setEnabled( false );
            }

            // Delete Button
            if ( getWidget().isShowDeleteButton() )
            {
                deleteToolItem = new ToolItem( toolbar, SWT.PUSH );
                deleteToolItem.setToolTipText( Messages.getString( "EditorTable.Delete" ) ); //$NON-NLS-1$
                deleteToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                    EntryTemplatePluginConstants.IMG_TOOLBAR_DELETE_VALUE ) );
                deleteToolItem.setEnabled( false );
            }
        }

        return composite;
    }


    /**
     * Indicates if the widget needs a toolbar for actions.
     *
     * @return
     *      <code>true</code> if the widget needs a toolbar for actions,
     *      <code>false</code> if not
     */
    private boolean needsToolbar()
    {
        return getWidget().isShowAddButton() || getWidget().isShowEditButton() || getWidget().isShowDeleteButton();
    }


    /**
     * Updates the widget's content.
     */
    private void updateWidget()
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.isString() ) && ( attribute.getValueSize() > 0 ) )
        {
            tableViewer.setInput( attribute.getStringValues() );
        }
        else
        {
            tableViewer.setInput( new String[0] );
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        // Add button
        if ( ( addToolItem != null ) && ( !addToolItem.isDisposed() ) )
        {
            addToolItem.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    addToolItemAction();
                }
            } );
        }

        // Edit button
        if ( ( editToolItem != null ) && ( !editToolItem.isDisposed() ) )
        {
            editToolItem.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    editToolItemAction();
                }
            } );
        }

        // Delete button
        if ( ( deleteToolItem != null ) && ( !deleteToolItem.isDisposed() ) )
        {
            deleteToolItem.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    deleteToolItemAction();
                }
            } );
        }

        // Table Viewer
        if ( ( tableViewer != null ) && ( !tableViewer.getTable().isDisposed() ) )
        {
            tableViewer.addSelectionChangedListener( new ISelectionChangedListener()
            {
                public void selectionChanged( SelectionChangedEvent event )
                {
                    updateButtonsStates();
                }
            } );

            tableViewer.addDoubleClickListener( new IDoubleClickListener()
            {
                public void doubleClick( DoubleClickEvent event )
                {
                    if ( ( editToolItem != null ) && ( !editToolItem.isDisposed() ) )
                    {
                        editToolItemAction();
                    }
                }
            } );
        }
    }


    /**
     * This method is called when the 'Add...' toolbar item is clicked.
     */
    private void addToolItemAction()
    {
        TextDialog textDialog = new TextDialog( tableViewer.getTable().getShell(), "" ); //$NON-NLS-1$
        if ( textDialog.open() == Dialog.OK )
        {
            String value = textDialog.getText();

            addAttributeValue( value );
            tableViewer.setSelection( new StructuredSelection( value ) );
        }
    }


    /**
     * This method is called when the 'Edit...' toolbar item is clicked.
     */
    private void editToolItemAction()
    {
        StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            String selectedValue = ( String ) selection.getFirstElement();

            IAttribute attribute = getAttribute();
            if ( ( attribute != null ) && ( attribute.isString() ) && ( attribute.getValueSize() > 0 ) )
            {
                IValue value = null;

                for ( IValue attributeValue : attribute.getValues() )
                {
                    if ( selectedValue.equals( attributeValue.getStringValue() ) )
                    {
                        value = attributeValue;
                        break;
                    }
                }

                if ( value != null )
                {
                    TextDialog textDialog = new TextDialog( tableViewer.getTable().getShell(), selectedValue );
                    if ( textDialog.open() == Dialog.OK )
                    {
                        String newValue = textDialog.getText();
                        if ( !selectedValue.equals( newValue ) )
                        {
                            deleteAttributeValue( selectedValue );
                            addAttributeValue( newValue );
                            tableViewer.setSelection( new StructuredSelection( newValue ) );
                        }
                    }
                }
            }
        }
    }


    /**
     * This method is called when the 'Delete...' toolbar item is clicked.
     */
    private void deleteToolItemAction()
    {
        StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            // Launching a confirmation dialog
            if ( MessageDialog.openConfirm( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages
                .getString( "EditorTable.Confirmation" ), Messages.getString( "EditorTable.ConfirmationDeleteValue" ) ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                deleteAttributeValue( ( String ) selection.getFirstElement() );
            }
        }
    }


    /**
     * Updates the states of the buttons.
     */
    private void updateButtonsStates()
    {
        StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

        if ( ( editToolItem != null ) && ( !editToolItem.isDisposed() ) )
        {
            editToolItem.setEnabled( !selection.isEmpty() );
        }

        if ( ( deleteToolItem != null ) && ( !deleteToolItem.isDisposed() ) )
        {
            deleteToolItem.setEnabled( !selection.isEmpty() );
        }
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