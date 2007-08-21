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

package org.apache.directory.studio.apacheds.schemaeditor.view.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Manage Aliases Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EditAliasesDialog extends Dialog
{
    /** The aliases List */
    private List<String> aliases;
    private List<String> aliasesLowerCased;

    /** The dirty flag */
    private boolean dirty = false;

    // UI Fields
    private Table aliasesTable;
    private Text newAliasText;
    private Button newAliasAddButton;
    private Composite errorComposite;
    private Image errorImage;
    private Label errorLabel;


    /**
     * Creates a new instance of EditAliasesDialog.
     *
     * @param aliases
     *      the array containing the aliases
     */
    public EditAliasesDialog( String[] aliases )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.aliases = new ArrayList<String>();
        aliasesLowerCased = new ArrayList<String>();
        for ( String alias : aliases )
        {
            this.aliases.add( alias );
            aliasesLowerCased.add( alias.toLowerCase() );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Edit Aliases" );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // ALIASES Label
        Label aliases_label = new Label( composite, SWT.NONE );
        aliases_label.setText( "Aliases" );
        aliases_label.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, true, 2, 1 ) );

        // ALIASES Table
        aliasesTable = new Table( composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 );
        gridData.heightHint = 100;
        gridData.minimumHeight = 100;
        gridData.widthHint = 200;
        gridData.minimumWidth = 200;
        aliasesTable.setLayoutData( gridData );

        // ADD Label
        Label add_label = new Label( composite, SWT.NONE );
        add_label.setText( "Add an alias" );
        add_label.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, true, 2, 1 ) );

        // NEW ALIAS Field
        newAliasText = new Text( composite, SWT.BORDER );
        newAliasText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Add Button
        newAliasAddButton = new Button( composite, SWT.PUSH );
        newAliasAddButton.setText( "Add" );
        newAliasAddButton.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );
        newAliasAddButton.setEnabled( false );

        // Error Composite
        errorComposite = new Composite( composite, SWT.NONE );
        errorComposite.setLayout( new GridLayout( 2, false ) );
        errorComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
        errorComposite.setVisible( false );

        // Error Image
        errorImage = PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
        Label label = new Label( errorComposite, SWT.NONE );
        label.setImage( errorImage );
        label.setSize( 16, 16 );

        // Error Label
        errorLabel = new Label( errorComposite, SWT.NONE );
        errorLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        errorLabel.setText( "An element with the same alias already exists." );

        // Table initialization
        fillAliasesTable();

        // Listeners initialization
        initListeners();

        // Setting the focus to the text field
        newAliasText.setFocus();

        return composite;
    }


    /**
     * Fills in the Aliases Table from the aliases list     */
    private void fillAliasesTable()
    {
        aliasesTable.removeAll();
        aliasesTable.setItemCount( 0 );
        for ( String alias : aliases )
        {
            TableItem newItem = new TableItem( aliasesTable, SWT.NONE );
            newItem.setText( alias );
        }
    }


    /**
     * Initializes the Listeners.
     */
    private void initListeners()
    {
        aliasesTable.addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( ( e.keyCode == SWT.DEL ) || ( e.keyCode == Action.findKeyCode( "BACKSPACE" ) ) ) { //$NON-NLS-1$
                    // NOTE: I couldn't find the corresponding Identificator in the SWT.SWT Class,
                    // so I Used JFace Action fineKeyCode method to get the Backspace keycode.

                    removeAliases();
                }
            }
        } );

        // Aliases Table's Popup Menu
        Menu menu = new Menu( getShell(), SWT.POP_UP );
        aliasesTable.setMenu( menu );
        MenuItem deleteMenuItem = new MenuItem( menu, SWT.PUSH );
        deleteMenuItem.setText( "Delete" );
        deleteMenuItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_TOOL_DELETE ) );
        // Adding the listener
        deleteMenuItem.addListener( SWT.Selection, new Listener()
        {
            public void handleEvent( Event event )
            {
                removeAliases();
            }
        } );

        // NEW ALIAS Field
        newAliasText.addTraverseListener( new TraverseListener()
        {
            public void keyTraversed( TraverseEvent e )
            {
                if ( e.detail == SWT.TRAVERSE_RETURN )
                {
                    String text = newAliasText.getText();

                    if ( "".equals( text ) ) //$NON-NLS-1$
                    {
                        close();
                    }
                    else if ( ( !aliasesLowerCased.contains( text.toLowerCase() ) ) //$NON-NLS-1$
                        && ( !Activator.getDefault().getSchemaHandler().isAliasOrOidAlreadyTaken( text ) ) )
                    {
                        addANewAlias();
                    }
                }
            }
        } );
        newAliasText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                errorComposite.setVisible( false );
                newAliasAddButton.setEnabled( true );
                String text = newAliasText.getText();

                if ( "".equals( text ) ) //$NON-NLS-1$
                {
                    newAliasAddButton.setEnabled( false );
                }
                else if ( aliasesLowerCased.contains( text.toLowerCase() ) )
                {
                    errorComposite.setVisible( true );
                    errorLabel.setText( "This alias already exists in the list." );
                    newAliasAddButton.setEnabled( false );
                }
                else if ( Activator.getDefault().getSchemaHandler().isAliasOrOidAlreadyTaken( text ) )
                {
                    errorComposite.setVisible( true );
                    errorLabel.setText( "An element with the same alias already exists." );
                    newAliasAddButton.setEnabled( false );
                }
                else if ( !PluginUtils.verifyName( text ) )
                {
                    errorComposite.setVisible( true );
                    errorLabel.setText( "Invalid Alias." );
                    newAliasAddButton.setEnabled( false );
                }
            }
        } );

        // ADD Button
        newAliasAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addANewAlias();
            }
        } );

    }


    /**
     * Removes the selected aliases in the Aliases Table from the Aliases List.
     */
    private void removeAliases()
    {
        TableItem[] selectedItems = aliasesTable.getSelection();
        for ( TableItem item : selectedItems )
        {
            aliases.remove( item.getText() );
            aliasesLowerCased.remove( item.getText().toLowerCase() );
        }
        dirty = true;
        fillAliasesTable();
    }


    /**
     * Adds a new alias
     */
    private void addANewAlias()
    {
        if ( newAliasText.getText().length() != 0 )
        {
            aliases.add( newAliasText.getText() );
            aliasesLowerCased.add( newAliasText.getText().toLowerCase() );
            fillAliasesTable();
            newAliasText.setText( "" ); //$NON-NLS-1$
            newAliasText.setFocus();
            this.dirty = true;
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * Returns the aliases.
     *  
     * @return
     *      the aliases
     */
    public String[] getAliases()
    {
        return aliases.toArray( new String[0] );
    }


    /**
     * Gets the Dirty flag of the dialog
     *
     * @return
     *      the dirty flag of the dialog
     */
    public boolean isDirty()
    {
        return dirty;
    }
}
