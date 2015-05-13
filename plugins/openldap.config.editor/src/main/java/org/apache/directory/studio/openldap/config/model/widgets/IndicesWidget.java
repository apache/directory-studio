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
    private Table indexTable;
    private TableViewer indexTableViewer;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    // A listener on the Index table, that modifies the button when a index is selected
    private ISelectionChangedListener tableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) indexTableViewer.getSelection();

            editButton.setEnabled( !selection.isEmpty() );
            deleteButton.setEnabled( !selection.isEmpty() );
        }
    };
    
    // A listener on the Index table, that reacts to a doubleClick : it's opening the index editor
    private IDoubleClickListener tableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editIndex();
        }
    };
    
    // A listener on the Add button, which opens the index addition editor
    private SelectionListener addButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addIndex();
        }
    };
    
    // A listener on the Edit button, that open the index editor
    private SelectionListener editButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editIndex();
        }
    };
    
    // A listener on the Delete button, which delete the selected index
    private SelectionListener deleteButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteIndex();
        }
    };


    /**
     * Creates a new instance of IndicesWidget.
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
     * Creates the Index widget. It's a Table and three button :
     * <pre>
     * +--------------------------------------+
     * | Index 1                              | (Add... )
     * | Index 2                              | (Edit...)
     * |                                      | (Delete )
     * +--------------------------------------+
     * </pre>
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
        
        // First, define a grid of 2 columns
        GridLayout compositeGridLayout = new GridLayout( 2, false );
        compositeGridLayout.marginHeight = compositeGridLayout.marginWidth = 0;
        composite.setLayout( compositeGridLayout );

        // Create the index Table and Table Viewer
        if ( toolkit != null )
        {
            indexTable = toolkit.createTable( composite, SWT.NULL );
        }
        else
        {
            indexTable = new Table( composite, SWT.NULL );
        }
        
        // Define the table size and height. It will span on 3 lines.
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        indexTable.setLayoutData( gd );
        
        // Create the index TableViewer
        indexTableViewer = new TableViewer( indexTable );
        indexTableViewer.setContentProvider( new ArrayContentProvider() );
        
        // The LabelProvider
        indexTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_INDEX );
            }
        } );
        
        // Listeners : we want to catch changes and double clicks
        indexTableViewer.addSelectionChangedListener( tableViewerSelectionChangedListener );
        indexTableViewer.addDoubleClickListener( tableViewerDoubleClickListener );
        
        // Inject the existing indices
        indexTableViewer.setInput( indices );

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

        // Create the Edit Button and its listener
        if ( toolkit != null )
        {
            editButton = toolkit.createButton( composite, "Edit...", SWT.PUSH );
        }
        else
        {
            editButton = BaseWidgetUtils.createButton( composite, "Edit...", SWT.PUSH );
        }
        
        // It's not enabled unless we have selected an index
        editButton.setEnabled( false );
        editButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        editButton.addSelectionListener( editButtonListener );

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

        indexTableViewer.refresh();
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
        IndexDialog dialog = new IndexDialog( addButton.getShell(), null, browserConnection );
        
        if ( dialog.open() == IndexDialog.OK )
        {
            OlcDbIndex newIndex = dialog.getNewIndex();
            indices.add( newIndex.toString() );
            indexTableViewer.refresh();
            indexTableViewer.setSelection( new StructuredSelection( newIndex.toString() ) );
            notifyListeners();
        }
    }


    /**
     * This method is called when the 'Edit...' button is clicked
     * or the table viewer is double-clicked.
     */
    private void editIndex()
    {
        StructuredSelection selection = ( StructuredSelection ) indexTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            String selectedIndex = ( String ) selection.getFirstElement();

            // Open the index dialog, with the selected index
            IndexDialog dialog = new IndexDialog( addButton.getShell(), new OlcDbIndex( selectedIndex ),
                browserConnection );
            
            if ( dialog.open() == IndexDialog.OK )
            {
                OlcDbIndex newIndex = dialog.getNewIndex();
                int selectedIndexPosition = indices.indexOf( selectedIndex );
                
                // We will remove the modifie dindex, and replace it with the new index
                indices.remove( selectedIndex );
                indices.add( selectedIndexPosition, newIndex.toString() );
                indexTableViewer.refresh();
                indexTableViewer.setSelection( new StructuredSelection( newIndex.toString() ) );
                notifyListeners();
            }
        }
    }


    /**
     * This method is called when the 'Delete' button is clicked.
     */
    private void deleteIndex()
    {
        StructuredSelection selection = ( StructuredSelection ) indexTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            String selectedIndex = ( String ) selection.getFirstElement();

            indices.remove( selectedIndex );
            indexTableViewer.refresh();
            notifyListeners();
        }
    }
}
