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
package org.apache.directory.studio.openldap.config.model.widgets;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.editor.dialogs.IndexDialog;
import org.apache.directory.studio.openldap.config.model.OlcDbIndex;


/**
 * The IndicesWidget provides a table viewer to add/edit/remove an index.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class IndicesWidget extends BrowserWidget
{
    /** The indices list */
    private List<String> indices = new ArrayList<String>();

    /** The connection */
    private IBrowserConnection browserConnection;

    // UI widgets
    private Composite composite;
    private Table table;
    private TableViewer tableViewer;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    // Listeners
    private ISelectionChangedListener tableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

            editButton.setEnabled( !selection.isEmpty() );
            deleteButton.setEnabled( !selection.isEmpty() );
        }
    };
    private IDoubleClickListener tableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editIndex();
        }
    };
    private SelectionListener addButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addIndex();
        }
    };
    private SelectionListener editButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editIndex();
        }
    };
    private SelectionListener deleteButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteIndex();
        }
    };


    /**
     * Creates a new instance of LockDetectWidget.
     *
     * @param connection the browserConnection
     */
    public IndicesWidget( IBrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {
        createWidget( parent, null );
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     */
    public void createWidget( Composite parent, FormToolkit toolkit )
    {
        // Composite
        if ( toolkit != null )
        {
            composite = toolkit.createComposite( parent );
        }
        else
        {
            composite = new Composite( parent, SWT.NONE );
        }
        GridLayout compositeGridLayout = new GridLayout( 2, false );
        compositeGridLayout.marginHeight = compositeGridLayout.marginWidth = 0;
        composite.setLayout( compositeGridLayout );

        // Table and Table Viewer
        if ( toolkit != null )
        {
            table = toolkit.createTable( composite, SWT.NULL );
        }
        else
        {
            table = new Table( composite, SWT.NULL );
        }
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData( gd );
        tableViewer = new TableViewer( table );
        tableViewer.setContentProvider( new ArrayContentProvider() );
        tableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_INDEX );
            }
        } );
        tableViewer.addSelectionChangedListener( tableViewerSelectionChangedListener );
        tableViewer.addDoubleClickListener( tableViewerDoubleClickListener );
        tableViewer.setInput( indices );

        // Add Button
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

        // Edit Button
        if ( toolkit != null )
        {
            editButton = toolkit.createButton( composite, "Edit...", SWT.PUSH );
        }
        else
        {
            editButton = BaseWidgetUtils.createButton( composite, "Edit...", SWT.PUSH );
        }
        editButton.setEnabled( false );
        editButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        editButton.addSelectionListener( editButtonListener );

        // Delete Button
        if ( toolkit != null )
        {
            deleteButton = toolkit.createButton( composite, "Delete", SWT.PUSH );
        }
        else
        {
            deleteButton = BaseWidgetUtils.createButton( composite, "Delete", SWT.PUSH );
        }
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
     * Sets the indices.
     *
     * @param indices the indices
     */
    public void setIndices( List<String> indices )
    {
        if ( ( indices != null ) && ( indices.size() > 0 ) )
        {
            this.indices.addAll( indices );
        }

        tableViewer.refresh();
    }


    /**
     * Gets the indices.
     *
     * @return the indices
     */
    public List<String> getIndices()
    {
        return indices;
    }


    /**
     * This method is called when the 'Add...' button is clicked.
     */
    private void addIndex()
    {
        IndexDialog dialog = new IndexDialog( addButton.getShell(), null,
            browserConnection );
        if ( dialog.open() == IndexDialog.OK )
        {
            OlcDbIndex newIndex = dialog.getNewIndex();
            indices.add( newIndex.toString() );
            tableViewer.refresh();
            tableViewer.setSelection( new StructuredSelection( newIndex.toString() ) );
            notifyListeners();
        }
    }


    /**
     * This method is called when the 'Edit...' button is clicked
     * or the table viewer is double-clicked.
     */
    private void editIndex()
    {
        StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            String selectedIndex = ( String ) selection.getFirstElement();

            IndexDialog dialog = new IndexDialog( addButton.getShell(), new OlcDbIndex( selectedIndex ),
                browserConnection );
            if ( dialog.open() == IndexDialog.OK )
            {
                OlcDbIndex newIndex = dialog.getNewIndex();
                int selectedIndexPosition = indices.indexOf( selectedIndex );
                indices.remove( selectedIndex );
                indices.add( selectedIndexPosition, newIndex.toString() );
                tableViewer.refresh();
                tableViewer.setSelection( new StructuredSelection( newIndex.toString() ) );
                notifyListeners();
            }
        }
    }


    /**
     * This method is called when the 'Delete' button is clicked.
     */
    private void deleteIndex()
    {
        StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            String selectedIndex = ( String ) selection.getFirstElement();

            indices.remove( selectedIndex );
            tableViewer.refresh();
            notifyListeners();
        }
    }
}
