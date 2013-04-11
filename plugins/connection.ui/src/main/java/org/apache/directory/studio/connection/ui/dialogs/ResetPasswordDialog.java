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
 * The ResetPasswordDialog is used to ask the user his current password 
 * (for verification purposes) and a new (verified) password.
 * <p>
 * It has a useful checkbox that can show/hide the typed passwords.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ResetPasswordDialog extends Dialog
{
    /** The title of the dialog */
    private String title;

    /** The message to display, or <code>null</code> if none */
    private String message;

    /** The current password value; the empty string by default */
    private String currentPassword = ""; //$NON-NLS-1$

    /** The new password value; the empty string by default */
    private String newPassword = ""; //$NON-NLS-1$

    // UI Widgets
    private Button okButton;
    private Text currentPasswordText;
    private Button showCurrentPasswordCheckbox;
    private Text newPasswordText;
    private Button showNewPasswordCheckbox;
    private Text verifyNewPasswordText;
    private Button showVerifyNewPasswordCheckbox;


    /**
     * Creates a new instance of CredentialsDialog.
     * 
     * @param parentShell the parent shell
     * @param title the title
     * @param message the dialog message
     * @param initialValue the initial value
     */
    public ResetPasswordDialog( Shell parentShell, String title, String message, String initialValue )
    {
        super( parentShell );
        this.title = title;
        this.message = message;

        if ( initialValue == null )
        {
            currentPassword = ""; //$NON-NLS-1$
        }
        else
        {
            currentPassword = initialValue;
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
            currentPassword = currentPasswordText.getText();
            newPassword = newPasswordText.getText();
        }
        else
        {
            currentPassword = null;
            newPassword = null;
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
            GridData messageLabelGridData = new GridData( SWT.FILL, SWT.CENTER, true, true, 2, 1 );
            messageLabelGridData.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
            messageLabel.setLayoutData( messageLabelGridData );
        }

        // Current Password Group
        Group currentPasswordGroup = BaseWidgetUtils.createGroup( composite, "Current password", 1 );
        currentPasswordGroup.setLayout( new GridLayout( 2, false ) );

        // Current Password Text
        BaseWidgetUtils.createLabel( currentPasswordGroup, "Current password:", 1 );
        currentPasswordText = BaseWidgetUtils.createText( currentPasswordGroup, "", 1 );
        currentPasswordText.setEchoChar( '\u2022' );
        currentPasswordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        // Show Current Password Checkbox
        BaseWidgetUtils.createLabel( currentPasswordGroup, "", 1 );
        showCurrentPasswordCheckbox = BaseWidgetUtils.createCheckbox( currentPasswordGroup, "Show password", 1 );
        showCurrentPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( showCurrentPasswordCheckbox.getSelection() )
                {
                    currentPasswordText.setEchoChar( '\0' );
                }
                else
                {
                    currentPasswordText.setEchoChar( '\u2022' );
                }
            }
        } );

        // New Password Group
        Group newPasswordGroup = BaseWidgetUtils.createGroup( composite, "New password", 1 );
        newPasswordGroup.setLayout( new GridLayout( 2, false ) );

        // New Password Text
        BaseWidgetUtils.createLabel( newPasswordGroup, "New password:", 1 );
        newPasswordText = BaseWidgetUtils.createText( newPasswordGroup, "", 1 );
        newPasswordText.setEchoChar( '\u2022' );
        newPasswordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        // Show New Password Checkbox
        BaseWidgetUtils.createLabel( newPasswordGroup, "", 1 );
        showNewPasswordCheckbox = BaseWidgetUtils.createCheckbox( newPasswordGroup, "Show password", 1 );
        showNewPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( showNewPasswordCheckbox.getSelection() )
                {
                    newPasswordText.setEchoChar( '\0' );
                }
                else
                {
                    newPasswordText.setEchoChar( '\u2022' );
                }
            }
        } );

        // Verify Text
        BaseWidgetUtils.createLabel( newPasswordGroup, "Verify new password:", 1 );
        verifyNewPasswordText = BaseWidgetUtils.createText( newPasswordGroup, "", 1 );
        verifyNewPasswordText.setEchoChar( '\u2022' );
        verifyNewPasswordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        // Show Verify New Password Checkbox
        BaseWidgetUtils.createLabel( newPasswordGroup, "", 1 );
        showVerifyNewPasswordCheckbox = BaseWidgetUtils.createCheckbox( newPasswordGroup, "Show password", 1 );
        showVerifyNewPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( showVerifyNewPasswordCheckbox.getSelection() )
                {
                    verifyNewPasswordText.setEchoChar( '\0' );
                }
                else
                {
                    verifyNewPasswordText.setEchoChar( '\u2022' );
                }
            }
        } );

        // Setting focus
        currentPasswordGroup.setFocus();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Returns current password.
     * 
     * @return the current password
     */
    public String getCurrentPassword()
    {
        return currentPassword;
    }


    /**
     * Returns new password.
     * 
     * @return the new password
     */
    public String getNewPassword()
    {
        return newPassword;
    }


    /**
     * Validates the input.
     */
    private void validate()
    {
        String currentPassword = currentPasswordText.getText();
        String newPassword = newPasswordText.getText();
        String verifyNewPassword = verifyNewPasswordText.getText();

        okButton
            .setEnabled( ( !"".equals( currentPassword ) ) && ( !"".equals( newPassword ) ) && ( newPassword.equals( verifyNewPassword ) ) && ( !currentPassword.equals( newPassword ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
