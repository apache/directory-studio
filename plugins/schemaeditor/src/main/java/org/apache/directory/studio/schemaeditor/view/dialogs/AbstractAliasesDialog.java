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

package org.apache.directory.studio.schemaeditor.view.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.util.Strings;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
 * This class implements dialog to manage aliases.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractAliasesDialog extends Dialog
{
    /** The aliases List */
    private List<String> initialLowerCasedAliases = new ArrayList<String>();
    private List<String> aliases = new ArrayList<String>();
    private List<String> lowerCasedAliases = new ArrayList<String>();

    /** The listener used to override the listerner on the RETURN key */
    private Listener returnKeyListener = new Listener()
    {
        public void handleEvent( Event event )
        {
            if ( event.detail == SWT.TRAVERSE_RETURN )
            {
                event.detail = SWT.TRAVERSE_TAB_NEXT;
                closeTableEditor();
            }
        }
    };

    // UI Fields
    private Table aliasesTable;
    private TableEditor tableEditor;
    private Button addButton;
    private Button editButton;
    private Button removeButton;
    private Composite errorComposite;
    private Image errorImage;
    private Label errorLabel;


    /**
     * Creates a new instance of AbstractAliasesDialog.
     *
     * @param aliases an array of aliases
     */
    public AbstractAliasesDialog( List<String> aliases )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        if ( aliases != null )
        {
            for ( String alias : aliases )
            {
                initialLowerCasedAliases.add( Strings.toLowerCase( alias ) );
                this.aliases.add( alias );
                lowerCasedAliases.add( Strings.toLowerCase( alias ) );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        // Creating the composite
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Aliases Label
        Label aliasesLabel = new Label( composite, SWT.NONE );
        aliasesLabel.setText( Messages.getString( "AbstractAliasesDialog.Aliases" ) ); //$NON-NLS-1$
        aliasesLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, true, 2, 1 ) );

        // Aliases Table
        aliasesTable = new Table( composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        gridData.heightHint = 90;
        gridData.minimumHeight = 90;
        gridData.widthHint = 200;
        gridData.minimumWidth = 200;
        aliasesTable.setLayoutData( gridData );

        // Aliases Table Editor
        tableEditor = new TableEditor( aliasesTable );
        tableEditor.horizontalAlignment = SWT.LEFT;
        tableEditor.grabHorizontal = true;
        tableEditor.minimumWidth = 200;

        // Add Button
        addButton = new Button( composite, SWT.PUSH );
        addButton.setText( Messages.getString( "AbstractAliasesDialog.Add" ) ); //$NON-NLS-1$
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );

        // Edit Button
        editButton = new Button( composite, SWT.PUSH );
        editButton.setText( Messages.getString( "AbstractAliasesDialog.Edit" ) ); //$NON-NLS-1$
        editButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        editButton.setEnabled( false );

        // Remove Button
        removeButton = new Button( composite, SWT.PUSH );
        removeButton.setText( Messages.getString( "AbstractAliasesDialog.Remove" ) ); //$NON-NLS-1$
        removeButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        removeButton.setEnabled( false );

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
        errorLabel.setText( getAliasAlreadyExistsErrorMessage() );

        // Filling the Table with the given aliases
        fillAliasesTable();

        // Listeners initialization
        initListeners();

        // Checking the aliases
        checkAliases();

        return composite;
    }


    /**
     * Fills in the Aliases Table from the aliases list 
     */
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
                if ( ( e.keyCode == SWT.DEL ) || ( e.keyCode == Action.findKeyCode( "BACKSPACE" ) ) ) //$NON-NLS-1$
                {
                    removeSelectedAliases();
                    fillAliasesTable();
                    updateButtonsState();
                    checkAliases();
                }
            }
        } );
        aliasesTable.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                closeTableEditor();
                updateButtonsState();
            }
        } );
        aliasesTable.addListener( SWT.MouseDoubleClick, new Listener()
        {
            public void handleEvent( Event event )
            {
                openTableEditor( aliasesTable.getItem( aliasesTable.getSelectionIndex() ) );
            }
        } );

        // Aliases Table's Popup Menu
        Menu menu = new Menu( getShell(), SWT.POP_UP );
        aliasesTable.setMenu( menu );
        MenuItem removeMenuItem = new MenuItem( menu, SWT.PUSH );
        removeMenuItem.setText( Messages.getString( "AbstractAliasesDialog.Remove" ) ); //$NON-NLS-1$
        removeMenuItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_TOOL_DELETE ) );
        removeMenuItem.addListener( SWT.Selection, new Listener()
        {
            public void handleEvent( Event event )
            {
                removeSelectedAliases();
                fillAliasesTable();
                updateButtonsState();
                checkAliases();
            }
        } );

        // Add Button
        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addANewAlias();
            }
        } );

        // Edit Button
        editButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                openTableEditor( aliasesTable.getItem( aliasesTable.getSelectionIndex() ) );
            }
        } );

        // Remove Button
        removeButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                removeSelectedAliases();
                fillAliasesTable();
                updateButtonsState();
                checkAliases();
            }
        } );
    }


    /**
     * Updates the state of the buttons.
     */
    private void updateButtonsState()
    {
        if ( aliasesTable.getSelectionCount() >= 1 )
        {
            editButton.setEnabled( true );
            removeButton.setEnabled( true );
        }
        else
        {
            editButton.setEnabled( false );
            removeButton.setEnabled( false );
        }
    }


    /**
     * Removes the selected aliases.
     */
    private void removeSelectedAliases()
    {
        TableItem[] selectedItems = aliasesTable.getSelection();
        for ( TableItem item : selectedItems )
        {
            aliases.remove( item.getText() );
            lowerCasedAliases.remove( Strings.toLowerCase( item.getText() ) );
        }
    }


    /**
     * Adds a new alias
     */
    private void addANewAlias()
    {
        TableItem item = new TableItem( aliasesTable, SWT.NONE );
        item.setText( "" ); //$NON-NLS-1$
        openTableEditor( item );
    }


    /**
     * Opens the {@link TableEditor} on the given {@link TableItem}.
     *
     * @param item
     *      the {@link TableItem}
     */
    private void openTableEditor( TableItem item )
    {
        // Clean up any previous editor control
        Control oldEditor = tableEditor.getEditor();
        if ( oldEditor != null )
            oldEditor.dispose();

        if ( item == null )
            return;

        // The control that will be the editor must be a child of the Table
        Text newEditor = new Text( aliasesTable, SWT.NONE );
        newEditor.setText( item.getText() );
        newEditor.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                saveTableEditorText();
            }
        } );
        newEditor.addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( ( e.keyCode == Action.findKeyCode( "RETURN" ) ) || ( e.keyCode == SWT.KEYPAD_CR ) ) //$NON-NLS-1$
                {
                    closeTableEditor();
                }
            }
        } );
        newEditor.selectAll();
        newEditor.setFocus();
        tableEditor.setEditor( newEditor, item, 0 );
        Activator.getDefault().getWorkbench().getDisplay().addFilter( SWT.Traverse, returnKeyListener );
    }


    /**
     * Saves the {@link TableEditor} text.
     */
    private void saveTableEditorText()
    {
        Text text = ( Text ) tableEditor.getEditor();
        if ( text != null )
        {
            TableItem item = tableEditor.getItem();
            String oldText = item.getText();
            String newText = text.getText();
            if ( !oldText.equals( newText ) )
            {
                aliases.remove( oldText );
                lowerCasedAliases.remove( Strings.toLowerCase( oldText ) );
                if ( !newText.equals( "" ) ) //$NON-NLS-1$
                {
                    aliases.add( newText );
                    lowerCasedAliases.add( Strings.toLowerCase( newText ) );
                }
                item.setText( newText );
            }
        }
        checkAliases();
    }


    /**
     * Closes the {@link TableEditor}.
     */
    private void closeTableEditor()
    {
        Text text = ( Text ) tableEditor.getEditor();
        if ( text != null )
        {
            saveTableEditorText();
            text.dispose();
        }
        Activator.getDefault().getWorkbench().getDisplay().removeFilter( SWT.Traverse, returnKeyListener );
    }


    /**
     * Checks the aliases.
     */
    private void checkAliases()
    {
        errorComposite.setVisible( false );

        for ( String alias : aliases )
        {
            if ( ( isAliasAlreadyTaken( alias ) )
                && ( !initialLowerCasedAliases.contains( Strings.toLowerCase( alias ) ) ) )
            {
                errorComposite.setVisible( true );
                errorLabel.setText( getAliasAlreadyExistsErrorMessage() );
                return;
            }
            else if ( !PluginUtils.verifyName( alias ) )
            {
                errorComposite.setVisible( true );
                errorLabel.setText( NLS.bind( Messages.getString( "AbstractAliasesDialog.InvalidAlias" ), new String[] //$NON-NLS-1$
                    { alias } ) );
                return;
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "AbstractAliasesDialog.EditAlias" ) ); //$NON-NLS-1$
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
     * Gets the error message in the case where an identical alias already exists.
     *
     * @return the error message
     */
    protected abstract String getAliasAlreadyExistsErrorMessage();


    /**
     * Checks if the given alias is already taken.
     *
     * @return <code>true</code> if the given alias is already taken,
     *         <code>false</code> if not.
     */
    protected abstract boolean isAliasAlreadyTaken( String alias );
}
