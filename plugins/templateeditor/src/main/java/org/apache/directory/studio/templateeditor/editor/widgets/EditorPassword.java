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


import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.valueeditors.password.PasswordDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.model.widgets.TemplatePassword;


/**
 * This class implements an editor spinner.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorPassword extends EditorWidget<TemplatePassword>
{
    /** The current password value */
    private byte[] currentPassword;

    /** The password text field */
    private Text passwordTextField;

    /** The "Edit..." button */
    private ToolItem editToolItem;

    /** The "Show Password" checkbox*/
    private Button showPasswordCheckbox;


    /**
     * Creates a new instance of EditorPassword.
     * 
     * @param editor
     *      the associated editor
     * @param templatePassword
     *      the associated template password
     * @param toolkit
     *      the associated toolkit
     */
    public EditorPassword( IEntryEditor editor, TemplatePassword templatePassword, FormToolkit toolkit )
    {
        super( templatePassword, editor, toolkit );
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
        Composite composite = getToolkit().createComposite( parent );
        composite.setLayoutData( getGridata() );

        // Calculating the number of columns needed
        int numberOfColumns = 1;
        if ( getWidget().isShowEditButton() )
        {
            numberOfColumns++;
        }

        // Creating the layout
        GridLayout gl = new GridLayout( numberOfColumns, false );
        gl.marginHeight = gl.marginWidth = 0;
        gl.horizontalSpacing = gl.verticalSpacing = 0;
        composite.setLayout( gl );

        // Creating the password text field
        passwordTextField = getToolkit().createText( composite, null, SWT.BORDER );
        passwordTextField.setEditable( false );
        GridData gd = new GridData( SWT.FILL, SWT.CENTER, true, false );
        gd.widthHint = 50;
        passwordTextField.setLayoutData( gd );

        // Setting the echo char for the password text field
        if ( getWidget().isHidden() )
        {
            passwordTextField.setEchoChar( '\u2022' );
        }
        else
        {
            passwordTextField.setEchoChar( '\0' );
        }

        // Creating the edit password button
        if ( getWidget().isShowEditButton() )
        {
            ToolBar toolbar = new ToolBar( composite, SWT.HORIZONTAL | SWT.FLAT );

            editToolItem = new ToolItem( toolbar, SWT.PUSH );
            editToolItem.setToolTipText( Messages.getString( "EditorPassword.EditPassword" ) ); //$NON-NLS-1$
            editToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                EntryTemplatePluginConstants.IMG_TOOLBAR_EDIT_PASSWORD ) );
        }

        // Creating the show password checkbox
        if ( getWidget().isShowShowPasswordCheckbox() )
        {
            showPasswordCheckbox = getToolkit().createButton( composite,
                Messages.getString( "EditorPassword.ShowPassword" ), SWT.CHECK ); //$NON-NLS-1$
        }

        return composite;
    }


    /**
     * Updates the widget's content.
     */
    private void updateWidget()
    {
        // Getting the current password value in the attribute
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.getValueSize() > 0 ) )
        {
            currentPassword = attribute.getValues()[0].getBinaryValue();
        }
        else
        {
            currentPassword = null;
        }

        // Updating the password text field
        if ( currentPassword != null )
        {
            passwordTextField.setText( new String( currentPassword ) );
        }
        else
        {
            passwordTextField.setText( "" ); //$NON-NLS-1$
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        // Edit Password toolbar item
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

        // Show Password checkbox
        if ( ( showPasswordCheckbox != null ) && ( !showPasswordCheckbox.isDisposed() ) )
        {
            showPasswordCheckbox.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    showPasswordAction();
                }
            } );
        }
    }


    /**
     * This method is called when the edit tool item is clicked.
     */
    private void editToolItemAction()
    {
        // Creating and displaying a password dialog
        PasswordDialog passwordDialog = new PasswordDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getShell(), currentPassword, getEntry() );
        if ( passwordDialog.open() == Dialog.OK )
        {
            if ( passwordDialog.getNewPassword() != currentPassword )
            {
                currentPassword = passwordDialog.getNewPassword();
                passwordTextField.setText( new String( currentPassword ) );
                updateEntry();
            }
        }
    }


    /**
     * This action is called when the 'Show Password' checkbox is clicked.
     */
    private void showPasswordAction()
    {
        if ( showPasswordCheckbox.getSelection() )
        {
            passwordTextField.setEchoChar( '\0' );
        }
        else
        {
            passwordTextField.setEchoChar( '\u2022' );
        }
    }


    /**
     * This method is called when the entry has been updated in the UI.
     */
    private void updateEntry()
    {
        // Getting the  attribute
        IAttribute attribute = getAttribute();
        if ( attribute == null )
        {
            if ( ( currentPassword != null ) && ( currentPassword.length != 0 ) )
            {
                // Creating a new attribute with the value
                addNewAttribute( currentPassword );
            }
        }
        else
        {
            if ( ( currentPassword != null ) && ( currentPassword.length != 0 ) )
            {
                // Modifying the existing attribute
                modifyAttributeValue( currentPassword );
            }
            else
            {
                // Deleting the attribute
                deleteAttribute();
            }
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
