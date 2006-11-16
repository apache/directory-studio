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

package org.apache.directory.ldapstudio.schemas.view.editors;


import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class ManageAliasesDialog extends Dialog
{

    private Table aliases_table;
    private Text newAlias_text;
    private Button newAlias_button;
    private String[] aliasesList;
    private boolean disableEditing;
    private boolean dirty = false;


    public ManageAliasesDialog( Shell parent, String[] list, boolean disableEditing )
    {
        super( parent );
        this.aliasesList = list;
        this.disableEditing = disableEditing;
    }


    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "ManageAliasesDialog.Manage_aliases" ) ); //$NON-NLS-1$
    }


    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 2, false );
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        // ALIASES Label
        Label aliases_label = new Label( composite, SWT.NONE );
        aliases_label.setText( Messages.getString( "ManageAliasesDialog.Aliases" ) ); //$NON-NLS-1$
        aliases_label.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, true, 2, 1 ) );

        // ALIASES Table
        aliases_table = new Table( composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true, 2, 1 );
        gridData.heightHint = 100;
        gridData.minimumHeight = 100;
        gridData.widthHint = 200;
        gridData.minimumWidth = 200;
        aliases_table.setLayoutData( gridData );

        // ADD Label
        Label add_label = new Label( composite, SWT.NONE );
        add_label.setText( Messages.getString( "ManageAliasesDialog.Add_an_alias" ) ); //$NON-NLS-1$
        add_label.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, true, 2, 1 ) );

        // NEW ALIAS Field
        newAlias_text = new Text( composite, SWT.BORDER );
        newAlias_text.setLayoutData( new GridData( GridData.FILL, SWT.CENTER, true, false ) );

        // Add Button
        newAlias_button = new Button( composite, SWT.PUSH );
        newAlias_button.setText( Messages.getString( "ManageAliasesDialog.Add" ) ); //$NON-NLS-1$

        // Table initialization
        initAliases_table();

        // Listeners initialization
        initListeners();

        // Setting the focus to the text field
        newAlias_text.setFocus();

        return composite;
    }


    private void initAliases_table()
    {
        for ( int i = 0; i < aliasesList.length; i++ )
        {
            String aliasName = aliasesList[i];
            TableItem newItem = new TableItem( aliases_table, SWT.NONE );
            newItem.setText( aliasName );
        }
    }


    private void initListeners()
    {
        if ( this.disableEditing )
        {
            aliases_table.setEnabled( false );
            newAlias_text.setEnabled( false );
            newAlias_button.setEnabled( false );
        }
        else
        {
            aliases_table.addKeyListener( new KeyListener()
            {
                public void keyPressed( KeyEvent e )
                {
                    if ( ( e.keyCode == SWT.DEL ) || ( e.keyCode == Action.findKeyCode( "BACKSPACE" ) ) ) { //$NON-NLS-1$
                        // NOTE: I couldn't find the corresponding Identificator in the SWT.SWT Class,
                        // so I Used JFace Action fineKeyCode method to get the Backspace keycode.
                        aliases_table.remove( aliases_table.getSelectionIndices() );
                        // Setting the Dialog has dirty
                        dirty = true;
                    }
                }


                public void keyReleased( KeyEvent e )
                {
                }
            } );

            // Aliases Table's Popup Menu
            Menu menu = new Menu( getShell(), SWT.POP_UP );
            aliases_table.setMenu( menu );
            MenuItem deleteMenuItem = new MenuItem( menu, SWT.PUSH );
            deleteMenuItem.setText( Messages.getString( "ManageAliasesDialog.Delete" ) ); //$NON-NLS-1$
            // Adding the listener
            deleteMenuItem.addListener( SWT.Selection, new Listener()
            {
                public void handleEvent( Event event )
                {
                    aliases_table.remove( aliases_table.getSelectionIndices() );
                    // Setting the Dialog has dirty
                    dirty = true;
                }
            } );

            // NEW ALIAS Field
            newAlias_text.addTraverseListener( new TraverseListener()
            {
                public void keyTraversed( TraverseEvent e )
                {
                    if ( e.detail == SWT.TRAVERSE_RETURN )
                    {
                        addANewAlias();
                    }
                }
            } );

            // ADD Button
            newAlias_button.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    addANewAlias();
                }
            } );
        }
    }


    private void addANewAlias()
    {
        if ( newAlias_text.getText().length() != 0 )
        {
            TableItem newItem = new TableItem( aliases_table, SWT.NONE );
            newItem.setText( newAlias_text.getText() );
            newAlias_text.setText( "" ); //$NON-NLS-1$
            aliases_table.deselectAll();
            aliases_table.select( aliases_table.getItemCount() - 1 );
            newAlias_text.setFocus();
            // Setting the Dialog has dirty
            this.dirty = true;
        }
    }


    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    protected void okPressed()
    {
        // We create the new Array containing all the aliases
        ArrayList<String> aliases = new ArrayList<String>();
        for ( int i = 0; i < aliases_table.getItemCount(); i++ )
        {
            aliases.add( aliases_table.getItem( i ).getText() );
        }
        // Then we store it
        this.aliasesList = aliases.toArray( new String[0] );

        super.okPressed();
    }


    public String[] getAliasesList()
    {
        return aliasesList;
    }


    public boolean isDirty()
    {
        return this.dirty;
    }
}
