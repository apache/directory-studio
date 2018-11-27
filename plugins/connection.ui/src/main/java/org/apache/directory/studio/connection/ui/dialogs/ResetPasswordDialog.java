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


import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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
    private String currentPassword = StringUtils.EMPTY; //$NON-NLS-1$

    /** The new password value; the empty string by default */
    private String newPassword = StringUtils.EMPTY; //$NON-NLS-1$

    // UI Widgets
    /** The OK button */
    private Button okButton;

    /** The Current Password Text */
    private Text currentPasswordText;
    
    /** The Show Current Password Checkbox */
    private Button showCurrentPasswordCheckbox;

    /** The New Password Text */
    private Text newPasswordText;
    
    /** The Show New Password checkbox */
    private Button showNewPasswordCheckbox;

    /** The Verify New Password Text */
    private Text verifyNewPasswordText;
    
    /** The Show Verify New Password button */
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
            currentPassword = StringUtils.EMPTY; //$NON-NLS-1$
        }
        else
        {
            currentPassword = initialValue;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );

        shell.setText( CommonUIUtils.getTextValue( title ) );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        validate();
    }


    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
        Group currentPasswordGroup = BaseWidgetUtils.createGroup( composite,
            Messages.getString( "ResetPasswordDialog.CurrentPassword" ), 1 ); //$NON-NLS-1$
        currentPasswordGroup.setLayout( new GridLayout( 2, false ) );

        // Current Password Text
        BaseWidgetUtils.createLabel( currentPasswordGroup,
            Messages.getString( "ResetPasswordDialog.CurrentPasswordColon" ), 1 ); //$NON-NLS-1$
        currentPasswordText = BaseWidgetUtils.createText( currentPasswordGroup, StringUtils.EMPTY, 1 ); //$NON-NLS-1$
        currentPasswordText.setEchoChar( '\u2022' );
        currentPasswordText.addModifyListener( event -> validate() );

        // Show Current Password Checkbox
        BaseWidgetUtils.createLabel( currentPasswordGroup, StringUtils.EMPTY, 1 ); //$NON-NLS-1$
        showCurrentPasswordCheckbox = BaseWidgetUtils.createCheckbox( currentPasswordGroup,
            Messages.getString( "ResetPasswordDialog.ShowPassword" ), 1 ); //$NON-NLS-1$
        showCurrentPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected( SelectionEvent event )
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
        Group newPasswordGroup = BaseWidgetUtils.createGroup( composite,
            Messages.getString( "ResetPasswordDialog.NewPassword" ), 1 ); //$NON-NLS-1$
        newPasswordGroup.setLayout( new GridLayout( 2, false ) );

        // New Password Text
        BaseWidgetUtils.createLabel( newPasswordGroup, Messages.getString( "ResetPasswordDialog.NewPasswordColon" ), 1 ); //$NON-NLS-1$
        newPasswordText = BaseWidgetUtils.createText( newPasswordGroup, StringUtils.EMPTY, 1 ); //$NON-NLS-1$
        newPasswordText.setEchoChar( '\u2022' );
        newPasswordText.addModifyListener( event -> validate() );

        // Show New Password Checkbox
        BaseWidgetUtils.createLabel( newPasswordGroup, StringUtils.EMPTY, 1 ); //$NON-NLS-1$
        showNewPasswordCheckbox = BaseWidgetUtils.createCheckbox( newPasswordGroup,
            Messages.getString( "ResetPasswordDialog.ShowPassword" ), 1 ); //$NON-NLS-1$
        showNewPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected( SelectionEvent event )
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
        BaseWidgetUtils.createLabel( newPasswordGroup,
            Messages.getString( "ResetPasswordDialog.VerifyNewPasswordColon" ), 1 ); //$NON-NLS-1$
        verifyNewPasswordText = BaseWidgetUtils.createText( newPasswordGroup, StringUtils.EMPTY, 1 ); //$NON-NLS-1$
        verifyNewPasswordText.setEchoChar( '\u2022' );
        verifyNewPasswordText.addModifyListener( event -> validate() );

        // Show Verify New Password Checkbox
        BaseWidgetUtils.createLabel( newPasswordGroup, StringUtils.EMPTY, 1 ); //$NON-NLS-1$
        showVerifyNewPasswordCheckbox = BaseWidgetUtils.createCheckbox( newPasswordGroup,
            Messages.getString( "ResetPasswordDialog.ShowPassword" ), 1 ); //$NON-NLS-1$
        showVerifyNewPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected( SelectionEvent event )
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
        String currentPwd = currentPasswordText.getText();
        String newPwd = newPasswordText.getText();
        String verifyNewPwd = verifyNewPasswordText.getText();

        okButton.setEnabled( !Strings.isEmpty( currentPwd ) && !Strings.isEmpty( newPwd ) && 
                newPwd.equals( verifyNewPwd ) && !currentPwd.equals( newPwd ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
