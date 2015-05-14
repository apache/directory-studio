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
import org.apache.directory.studio.openldap.config.editor.dialogs.ServerIdDialog;
import org.apache.directory.studio.openldap.config.editor.pages.ServerIdWrapper;


/**
 * The ServerIdTable Widget provides a table viewer to add/edit/remove ServerId from
 * a list of ServerId.
 * <pre>
 * +--------------------------------------+
 * | ServerId 1                           | (Add... )
 * | ServerId 2                           | (Edit...)
 * |                                      | (Delete )
 * +--------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerIdTableWidget extends BrowserWidget
{
    /** The ServerId list */
    private List<ServerIdWrapper> serverIds = new ArrayList<ServerIdWrapper>();

    // UI widgets
    private Composite composite;
    private Table table;
    private TableViewer tableViewer;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    
    /**
     *  A listener on the ServerIds table, that modifies the button when an ServerId is selected
     */
    private ISelectionChangedListener tableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

            editButton.setEnabled( !selection.isEmpty() );
            deleteButton.setEnabled( !selection.isEmpty() );
        }
    };
    
    /**
     * A listener on the ServerIds table, that reacts to a doubleClick : it's opening the ServerId editor
     */
    private IDoubleClickListener tableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editServerId();
        }
    };
    
    
    /**
     *  A listener on the Add button, which opens the ServerId addition editor
     */
    private SelectionListener addButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addServerId();
        }
    };
    
    /**
     *  A listener on the Edit button, that open the ServerId editor
     */
    private SelectionListener editButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editServerId();
        }
    };
    
    /**
     *  A listener on the Delete button, which delete the selected ServerId
     */
    private SelectionListener deleteButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteServerId();
        }
    };


    /**
     * Creates a new instance of ServerIdTableWidget.
     */
    public ServerIdTableWidget()
    {
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
     * Creates the ServerId widget. It's a Table and three button :
     * <pre>
     * +--------------------------------------+
     * | ServerId 1                           | (Add... )
     * | ServerId 2                           | (Edit...)
     * |                                      | (Delete )
     * +--------------------------------------+
     * </pre>
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     */
    public void createWidget( Composite parent, FormToolkit toolkit )
    {
        // Compositea
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

        // Create the Element Table and Table Viewer
        if ( toolkit != null )
        {
            table = toolkit.createTable( composite, SWT.BORDER );
        }
        else
        {
            table = new Table( composite, SWT.BORDER );
        }
        
        // Define the table size and height. It will span on 3 lines.
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData( gd );
        
        // Create the Elements TableViewer
        tableViewer = new TableViewer( table );
        tableViewer.setContentProvider( new ArrayContentProvider() );
        
        // The LabelProvider
        tableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_LDAP_SERVER );
            }
        } );
        
        // Listeners : we want to catch changes and double clicks
        tableViewer.addSelectionChangedListener( tableViewerSelectionChangedListener );
        tableViewer.addDoubleClickListener( tableViewerDoubleClickListener );
        
        // Inject the existing indices
        tableViewer.setInput( serverIds );

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
        
        // It's not enabled unless we have selected a serverId
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
        
        // It's not selected unless we have selected a ServerId
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
     * Sets the ServerIds.
     *
     * @param serverIds the ServerIds
     */
    public void setElements( List<ServerIdWrapper> serverIds )
    {
        if ( ( serverIds != null ) && ( serverIds.size() > 0 ) )
        {
            this.serverIds.addAll( serverIds );
        }

        tableViewer.refresh();
    }


    /**
     * Gets the ServerIds.
     *
     * @return the ServerIds
     */
    public List<ServerIdWrapper> getServerIds()
    {
        return serverIds;
    }
    
    
    /**
     * Insert the modified or added ServerID at the right place in the ServerID table
     * @param newServerId
     */
    private void insertServerId( ServerIdWrapper newServerId )
    {
        // Search for the inclusion position
        int pos = 0;

        for ( ServerIdWrapper serverIdWrapper : serverIds )
        {
            if ( serverIdWrapper.getServerId() > newServerId.getServerId() )
            {
                serverIds.add( pos, newServerId );
                break;
            }
            else
            {
                pos++;
            }
        }
        
        // Special case : the value has to be added at the end
        if ( pos == serverIds.size() )
        {
            serverIds.add( newServerId );
        }

    }


    /**
     * This method is called when the 'Add...' button is clicked.
     */
    private void addServerId()
    {
        ServerIdDialog dialog = new ServerIdDialog( addButton.getShell(), serverIds, (ServerIdWrapper)null );
        
        if ( dialog.open() == ServerIdDialog.OK )
        {
            ServerIdWrapper newServerId = dialog.getNewServerId();
            
            // If the user clicked on OK but the value was invalid, we will receive a Null value
            if ( newServerId != null )
            {
                String serverIdString = newServerId.toString();
                
                insertServerId( newServerId );
                tableViewer.refresh();
                tableViewer.setSelection( new StructuredSelection( serverIdString ) );
                notifyListeners();
            }
        }
    }


    /**
     * This method is called when the 'Edit...' button is clicked
     * or the table viewer is double-clicked.
     */
    private void editServerId()
    {
        StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            ServerIdWrapper selectedServerId = ( ServerIdWrapper ) selection.getFirstElement();

            // Open the ServerID dialog, with the selected serverId
            ServerIdDialog dialog = new ServerIdDialog( addButton.getShell(), serverIds, selectedServerId );
            
            if ( dialog.open() == ServerIdDialog.OK )
            {
                ServerIdWrapper newServerId = dialog.getNewServerId();
                
                // We will remove the modifies serverId, and replace it with the new serverId, at the right position
                serverIds.remove( selectedServerId );
                
                insertServerId( newServerId );
                tableViewer.refresh();
                tableViewer.setSelection( new StructuredSelection( newServerId.toString() ) );
                notifyListeners();
            }
        }
    }


    /**
     * This method is called when the 'Delete' button is clicked. It removes the selected
     * ServerId from the list.
     */
    private void deleteServerId()
    {
        StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            ServerIdWrapper selectedIndex = ( ServerIdWrapper ) selection.getFirstElement();

            serverIds.remove( selectedIndex );
            tableViewer.refresh();
            notifyListeners();
        }
    }
}
