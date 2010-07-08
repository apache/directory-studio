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
package org.apache.directory.studio.aciitemeditor.widgets;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.shared.ldap.aci.GrantAndDenial;
import org.apache.directory.shared.ldap.aci.ItemPermission;
import org.apache.directory.shared.ldap.aci.UserClass;
import org.apache.directory.studio.aciitemeditor.ACIItemValueWithContext;
import org.apache.directory.studio.aciitemeditor.Activator;
import org.apache.directory.studio.aciitemeditor.dialogs.ItemPermissionDialog;
import org.apache.directory.studio.aciitemeditor.model.UserClassWrapper;
import org.eclipse.jface.viewers.ArrayContentProvider;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;


/**
 * This composite contains GUI elements to add, edit and delete ACI item permissions.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ACIItemItemPermissionsComposite extends Composite
{

    /** The context. */
    private ACIItemValueWithContext context;

    /** The inner composite for all the content */
    private Composite composite = null;

    /** The description label */
    private Label label = null;

    /** The table control for the table viewer */
    private Table table = null;

    /** The table viewer containing all item classes */
    private TableViewer tableViewer = null;

    /** The composite containing the buttons */
    private Composite buttonComposite = null;

    /** The add button */
    private Button addButton = null;

    /** The edit button */
    private Button editButton = null;

    /** The delete button */
    private Button deleteButton = null;

    /** The selected item permissions, input of the table viewer */
    private List<ItemPermissionWrapper> itemPermissionWrappers = new ArrayList<ItemPermissionWrapper>();

    /**
     * ItemPermissionWrappers are used as input of the table viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private class ItemPermissionWrapper
    {
        /** The item permission bean. */
        private ItemPermission itemPermission;


        /**
         * Creates a new instance of ItemPermissionWrapper.
         *
         * @param itemClassClass
         */
        private ItemPermissionWrapper( ItemPermission itemPermission )
        {
            this.itemPermission = itemPermission;
        }


        /**
         * Returns a user-friedly string, displayed in the table.
         * 
         * @return the string
         */
        public String toString()
        {
            if ( itemPermission == null )
            {
                return "<UNKNOWN>"; //$NON-NLS-1$
            }
            else
            {
                StringBuffer buffer = new StringBuffer();
                if ( itemPermission.getPrecedence() > -1 )
                {
                    buffer.append( '(' );
                    buffer.append( itemPermission.getPrecedence() );
                    buffer.append( ')' );
                    buffer.append( ' ' );
                }
                for ( Iterator<UserClass> it = ( ( Collection<UserClass> ) itemPermission.getUserClasses() ).iterator(); it
                    .hasNext(); )
                {
                    UserClass uc = it.next();
                    String s = UserClassWrapper.classToDisplayMap.get( uc.getClass() );
                    buffer.append( s );

                    if ( it.hasNext() )
                    {
                        buffer.append( ',' );
                    }
                }
                buffer.append( ':' );
                buffer.append( ' ' );
                for ( Iterator<GrantAndDenial> it = ( ( Collection<GrantAndDenial> ) itemPermission
                    .getGrantsAndDenials() ).iterator(); it.hasNext(); )
                {
                    GrantAndDenial gd = it.next();
                    buffer.append( gd.isGrant() ? '+' : '-' );
                    buffer.append( gd.getMicroOperation().getName() );

                    if ( it.hasNext() )
                    {
                        buffer.append( ',' );
                    }
                }

                String s = buffer.toString();
                s = s.replace( '\r', ' ' );
                s = s.replace( '\n', ' ' );
                if ( s.length() > 50 )
                {
                    String temp = s;
                    s = temp.substring( 0, 25 );
                    s = s + "..."; //$NON-NLS-1$
                    s = s + temp.substring( temp.length() - 25, temp.length() );
                }
                return s;
            }
        }
    }


    /**
     * 
     * Creates a new instance of ACIItemItemPermissionsComposite.
     *
     * @param parent
     * @param style
     */
    public ACIItemItemPermissionsComposite( Composite parent, int style )
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
        label.setText( Messages.getString( "ACIItemItemPermissionsComposite.description" ) ); //$NON-NLS-1$
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

        table = new Table( composite, SWT.BORDER );
        table.setHeaderVisible( false );
        table.setLayoutData( tableGridData );
        table.setLinesVisible( false );
        tableViewer = new TableViewer( table );
        tableViewer.setContentProvider( new ArrayContentProvider() );
        tableViewer.setLabelProvider( new LabelProvider() );
        tableViewer.setInput( itemPermissionWrappers );

        tableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                itemPermissionSelected();
            }
        } );

        tableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                editItemPermission();
            }
        } );
    }


    /**
     * This method initializes buttons  
     *
     */
    private void createButtonComposite()
    {
        GridData deleteButtonGridData = new GridData();
        deleteButtonGridData.horizontalAlignment = GridData.FILL;
        deleteButtonGridData.grabExcessHorizontalSpace = false;
        deleteButtonGridData.verticalAlignment = GridData.BEGINNING;
        deleteButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridData editButtonGridData = new GridData();
        editButtonGridData.horizontalAlignment = GridData.FILL;
        editButtonGridData.grabExcessHorizontalSpace = false;
        editButtonGridData.verticalAlignment = GridData.BEGINNING;
        editButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridData addButtonGridData = new GridData();
        addButtonGridData.horizontalAlignment = GridData.FILL;
        addButtonGridData.grabExcessHorizontalSpace = false;
        addButtonGridData.verticalAlignment = GridData.BEGINNING;
        addButtonGridData.widthHint = Activator.getButtonWidth( this );

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
        addButton.setText( Messages.getString( "ACIItemItemPermissionsComposite.add.button" ) ); //$NON-NLS-1$
        addButton.setLayoutData( addButtonGridData );
        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addItemPermission();
            }
        } );

        editButton = new Button( buttonComposite, SWT.NONE );
        editButton.setText( Messages.getString( "ACIItemItemPermissionsComposite.edit.button" ) ); //$NON-NLS-1$
        editButton.setLayoutData( editButtonGridData );
        editButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                editItemPermission();
            }
        } );
        editButton.setEnabled( false );

        deleteButton = new Button( buttonComposite, SWT.NONE );
        deleteButton.setText( Messages.getString( "ACIItemItemPermissionsComposite.delete.button" ) ); //$NON-NLS-1$
        deleteButton.setLayoutData( deleteButtonGridData );
        deleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                deleteItemPermission();
            }
        } );
        deleteButton.setEnabled( false );

    }


    /**
     * Shows or hides this composite.
     * 
     * @param visible true if visible
     */
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
        ( ( GridData ) getLayoutData() ).heightHint = visible ? -1 : 0;
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
     * Sets the item permissions. 
     *
     * @param itemPermissions
     */
    public void setItemPermissions( Collection<ItemPermission> itemPermissions )
    {
        itemPermissionWrappers.clear();

        for ( ItemPermission itemPermission : itemPermissions )
        {
            ItemPermissionWrapper itemPermissionWrapper = new ItemPermissionWrapper( itemPermission );

            itemPermissionWrappers.add( itemPermissionWrapper );
        }

        tableViewer.refresh();
    }


    /**
     * Returns the item permissions as selected by the user.
     *
     * @return the item permissions
     */
    public Collection<ItemPermission> getItemPermissions()
    {
        Collection<ItemPermission> itemPermissions = new ArrayList<ItemPermission>();

        for ( ItemPermissionWrapper itemPermissionWrapper : itemPermissionWrappers )
        {
            itemPermissions.add( itemPermissionWrapper.itemPermission );
        }

        return itemPermissions;
    }


    /**
     * 
     * @return the item permission that is selected in the table viewer, or null.
     */
    private ItemPermissionWrapper getSelectedItemPermissionWrapper()
    {
        ItemPermissionWrapper itemPermissionWrapper = null;

        IStructuredSelection selection = ( IStructuredSelection ) tableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            Object element = selection.getFirstElement();
            if ( element instanceof ItemPermissionWrapper )
            {
                itemPermissionWrapper = ( ItemPermissionWrapper ) element;
            }
        }

        return itemPermissionWrapper;
    }


    /**
     * Opens the ItemPermissionDialog and adds the composed 
     * item permission to the list.
     */
    private void addItemPermission()
    {
        ItemPermissionDialog dialog = new ItemPermissionDialog( getShell(), null, context );
        if ( dialog.open() == ItemPermissionDialog.OK && dialog.getItemPermission() != null )
        {
            ItemPermissionWrapper itemPermissionWrapper = new ItemPermissionWrapper( dialog.getItemPermission() );
            itemPermissionWrappers.add( itemPermissionWrapper );

            tableViewer.refresh();
        }
    }


    /**
     * Opens the ItemPermissionDialog with the currently selected
     * item permission and puts the modified item permission into the list.
     */
    private void editItemPermission()
    {
        ItemPermissionWrapper oldItemPermissionWrapper = getSelectedItemPermissionWrapper();
        if ( oldItemPermissionWrapper != null )
        {
            ItemPermissionDialog dialog = new ItemPermissionDialog( getShell(),
                oldItemPermissionWrapper.itemPermission, context );
            if ( dialog.open() == ItemPermissionDialog.OK )
            {
                oldItemPermissionWrapper.itemPermission = dialog.getItemPermission();
                tableViewer.refresh();
            }
        }
    }


    /**
     * Deletes the currently selected item permission from list.
     */
    private void deleteItemPermission()
    {
        ItemPermissionWrapper itemPermissionWrapper = getSelectedItemPermissionWrapper();
        if ( itemPermissionWrapper != null )
        {
            itemPermissionWrappers.remove( itemPermissionWrapper );
            tableViewer.refresh();
        }
    }


    /**
     * Called when an item permission is selected in table viewer.
     * Updates the enabled/disabled state of the buttons.
     */
    private void itemPermissionSelected()
    {
        ItemPermissionWrapper itemPermissionWrapper = getSelectedItemPermissionWrapper();

        if ( itemPermissionWrapper == null )
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

}
