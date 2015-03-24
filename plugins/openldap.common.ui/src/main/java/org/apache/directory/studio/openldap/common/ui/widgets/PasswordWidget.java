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
package org.apache.directory.studio.openldap.common.ui.widgets;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.common.ui.dialogs.PasswordDialog;


/**
 * The PasswordWidget provides a label to display the password, an edit button 
 * and a 'Show Password' button to show/hide the password.
 */
public class PasswordWidget extends BrowserWidget
{
    /** The password */
    private byte[] password;

    /** The flag to show the "None" checkbox or not */
    private boolean showNoneCheckbox;

    /** The flag indicating if the password should be shown or not */
    private boolean showPassword;

    // UI widgets
    private Composite composite;
    private Button noneCheckbox;
    private Text passwordText;
    private Button editButton;
    private Button showPasswordCheckbox;


    /**
     * Creates a new instance of PasswordWidget.
     */
    public PasswordWidget()
    {
    }


    /**
     * Creates a new instance of PasswordWidget.
     *
     * @param showNoneButton the flag to show the "None" checkbox
     */
    public PasswordWidget( boolean showNoneCheckbox )
    {
        this.showNoneCheckbox = showNoneCheckbox;
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
        GridLayout compositeGridLayout = new GridLayout( getNumberOfColumnsForComposite(), false );
        compositeGridLayout.marginHeight = compositeGridLayout.marginWidth = 0;
        compositeGridLayout.verticalSpacing = 0;
        composite.setLayout( compositeGridLayout );

        // None Checbox
        if ( showNoneCheckbox )
        {
            if ( toolkit != null )
            {
                noneCheckbox = toolkit.createButton( composite, "None", SWT.CHECK );
            }
            else
            {
                noneCheckbox = BaseWidgetUtils.createCheckbox( composite, "None", 1 );
            }
            noneCheckbox.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    noneCheckboxSelected( noneCheckbox.getSelection() );
                    notifyListeners();
                }
            } );
        }

        // Password Text
        if ( toolkit != null )
        {
            passwordText = toolkit.createText( composite, "", SWT.NONE );
        }
        else
        {
            passwordText = BaseWidgetUtils.createReadonlyText( composite, "", 1 );
        }
        passwordText.setEditable( false );
        GridData gd = new GridData( SWT.FILL, SWT.CENTER, true, false );
        gd.widthHint = 50;
        passwordText.setLayoutData( gd );

        // Setting the echo char for the password text
        if ( showPassword )
        {
            passwordText.setEchoChar( '\0' );
        }
        else
        {
            passwordText.setEchoChar( '\u2022' );
        }

        // Edit Button
        if ( toolkit != null )
        {
            editButton = toolkit.createButton( composite, "Edit Password...", SWT.PUSH );
        }
        else
        {
            editButton = BaseWidgetUtils.createButton( composite, "Edit Password...", 1 );
            editButton.setLayoutData( new GridData() );
        }
        editButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                editButtonAction();
            }
        } );

        // Show Password Checkbox
        if ( toolkit != null )
        {
            if ( showNoneCheckbox )
            {
                toolkit.createLabel( composite, "" );
            }

            showPasswordCheckbox = toolkit.createButton( composite, "Show Password", SWT.CHECK );
        }
        else
        {
            if ( showNoneCheckbox )
            {
                BaseWidgetUtils.createLabel( composite, "", 1 );
            }

            showPasswordCheckbox = BaseWidgetUtils.createCheckbox( composite, "Show Password",
                getNumberOfColumnsForComposite() );
        }
        GridData showPasswordCheckboxGridData = new GridData();
        showPasswordCheckboxGridData.horizontalSpan = getNumberOfColumnsForComposite() - 1;
        showPasswordCheckbox.setLayoutData( showPasswordCheckboxGridData );
        showPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                showPasswordAction();
            }
        } );

        noneCheckboxSelected( showNoneCheckbox );
    }


    /**
     * Gets the number of columns for the composite.
     *
     * @return the number of columns for the composite
     */
    private int getNumberOfColumnsForComposite()
    {
        if ( showNoneCheckbox )
        {
            return 3;
        }
        else
        {
            return 2;
        }
    }


    /**
     * This method is called when the "None" checkbox is clicked.
     */
    private void noneCheckboxSelected( boolean state )
    {
        editButton.setEnabled( !state );
        showPasswordCheckbox.setEnabled( !state );
    }


    /**
     * This action is called when the 'Edit...' button is clicked.
     */
    private void editButtonAction()
    {
        // Creating and displaying a password dialog
        PasswordDialog passwordDialog = new PasswordDialog( editButton.getShell(), password );
        if ( passwordDialog.open() == Dialog.OK )
        {
            if ( passwordDialog.getNewPassword() != password )
            {
                byte[] password = passwordDialog.getNewPassword();
                if ( ( password != null ) && ( password.length > 0 ) )
                {
                    this.password = password;
                    passwordText.setText( new String( password ) );
                    notifyListeners();
                }
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
            passwordText.setEchoChar( '\0' );
        }
        else
        {
            passwordText.setEchoChar( '\u2022' );
        }
    }


    /**
     * Sets the password.
     *
     * @param password the password
     */
    public void setPassword( byte[] password )
    {
        this.password = password;

        if ( showNoneCheckbox )
        {
            boolean noneSelected = ( password == null );
            noneCheckbox.setSelection( noneSelected );
            noneCheckboxSelected( noneSelected );
        }

        // Updating the password text field
        if ( ( password != null ) && ( password.length > 0 ) )
        {
            passwordText.setText( new String( password ) );
        }
        else
        {
            passwordText.setText( "" ); //$NON-NLS-1$
        }
    }


    /**
     * Gets the password.
     *
     * @return the password
     */
    public byte[] getPassword()
    {
        if ( showNoneCheckbox && noneCheckbox.getSelection() )
        {
            return null;
        }

        if ( ( password != null ) && ( password.length > 0 ) )
        {
            return password;
        }

        return null;
    }


    /**
     * Gets the password as string.
     *
     * @return the password as string
     */
    public String getPasswordAsString()
    {
        if ( showNoneCheckbox && noneCheckbox.getSelection() )
        {
            return null;
        }

        if ( ( password != null ) && ( password.length > 0 ) )
        {
            return new String( password );
        }

        return null;
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
     * Sets the enabled state of the widget.
     *
     * @param enabled true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean enabled )
    {
        if ( ( editButton != null ) && ( !editButton.isDisposed() ) )
        {
            if ( showNoneCheckbox )
            {
                noneCheckbox.setEnabled( enabled );
                noneCheckboxSelected( noneCheckbox.getSelection() && enabled );
            }
            else
            {
                editButton.setEnabled( enabled );
                showPasswordCheckbox.setEnabled( enabled );
            }
        }
    }
}
