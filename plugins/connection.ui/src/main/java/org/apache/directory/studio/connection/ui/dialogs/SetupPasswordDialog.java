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

package org.apache.directory.studio.connection.ui.dialogs;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * The SetupPasswordDialog is used to ask the user for a setup password 
 * with a second text widget for verification purposes.
 * <p>
 * It has a useful checkbox that can show/hide the typed passwords.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SetupPasswordDialog extends Dialog
{
    /** The title of the dialog */
    private String title;

    /** The message to display, or <code>null</code> if none */
    private String message;

    /** The input value; the empty string by default */
    private String value = "";//$NON-NLS-1$

    // UI Widgets
    private Button okButton;
    private Text passwordText;
    private Button showPasswordCheckbox;
    private Text verifyPasswordText;
    private Button showVerifyPasswordCheckbox;


    /**
     * Creates a new instance of CredentialsDialog.
     * 
     * @param parentShell the parent shell
     * @param title the title
     * @param message the dialog message
     * @param initialValue the initial value
     */
    public SetupPasswordDialog( Shell parentShell, String title, String message, String initialValue )
    {
        super( parentShell );
        this.title = title;
        this.message = message;

        if ( initialValue == null )
        {
            value = "";//$NON-NLS-1$
        }
        else
        {
            value = initialValue;
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );

        if ( title != null )
        {
            shell.setText( title );
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        validate();
    }


    /**
     * {@inheritDoc}
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            value = passwordText.getText();
        }
        else
        {
            value = null;
        }
        super.buttonPressed( buttonId );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        // Composite
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
        layout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
        layout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
        layout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
        composite.setLayout( layout );
        GridData compositeGridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        compositeGridData.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( compositeGridData );

        // Message
        if ( message != null )
        {
            Label messageLabel = BaseWidgetUtils.createWrappedLabel( composite, message, 1 );
            GridData messageLabelGridData = new GridData( SWT.FILL, SWT.CENTER, true, true );
            messageLabelGridData.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
            messageLabel.setLayoutData( messageLabelGridData );
        }

        // Password Group
        Group passwordGroup = BaseWidgetUtils.createGroup( composite,
            Messages.getString( "SetupPasswordDialog.Password" ), 1 ); //$NON-NLS-1$
        passwordGroup.setLayout( new GridLayout( 2, false ) );

        // Password Text
        BaseWidgetUtils.createLabel( passwordGroup, Messages.getString( "SetupPasswordDialog.PasswordColon" ), 1 ); //$NON-NLS-1$
        passwordText = BaseWidgetUtils.createText( passwordGroup, value, 1 );
        passwordText.setEchoChar( '\u2022' );
        passwordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        // Show Password Checkbox
        BaseWidgetUtils.createLabel( passwordGroup, "", 1 ); //$NON-NLS-1$
        showPasswordCheckbox = BaseWidgetUtils.createCheckbox( passwordGroup,
            Messages.getString( "SetupPasswordDialog.ShowPassword" ), 1 ); //$NON-NLS-1$
        showPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
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
        } );

        // Verify Text
        BaseWidgetUtils.createLabel( passwordGroup, Messages.getString( "SetupPasswordDialog.VerifyPasswordColon" ), 1 ); //$NON-NLS-1$
        verifyPasswordText = BaseWidgetUtils.createText( passwordGroup, value, 1 );
        verifyPasswordText.setEchoChar( '\u2022' );
        verifyPasswordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        // Show Verify Password Checkbox
        BaseWidgetUtils.createLabel( passwordGroup, "", 1 ); //$NON-NLS-1$
        showVerifyPasswordCheckbox = BaseWidgetUtils.createCheckbox( passwordGroup,
            Messages.getString( "SetupPasswordDialog.ShowPassword" ), 1 ); //$NON-NLS-1$
        showVerifyPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( showVerifyPasswordCheckbox.getSelection() )
                {
                    verifyPasswordText.setEchoChar( '\0' );
                }
                else
                {
                    verifyPasswordText.setEchoChar( '\u2022' );
                }
            }
        } );

        // Setting focus
        passwordText.setFocus();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Returns the string typed into this password dialog.
     * 
     * @return the input string
     */
    public String getPassword()
    {
        return value;
    }


    /**
     * Validates the input.
     */
    private void validate()
    {
        String password = passwordText.getText();
        String verifyPassword = verifyPasswordText.getText();

        okButton.setEnabled( ( !"".equals( password ) ) && ( password.equals( verifyPassword ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
