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

package org.apache.directory.studio.ldapbrowser.ui.dialogs;


import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.codec.api.LdapApiService;
import org.apache.directory.api.ldap.codec.api.LdapApiServiceFactory;
import org.apache.directory.api.ldap.extras.extended.pwdModify.PasswordModifyRequest;
import org.apache.directory.api.ldap.extras.extended.pwdModify.PasswordModifyResponse;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.dialogs.MessageDialogWithTextarea;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedOperationRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
` * The PasswordModifyExtendedOperationDialog is used to ask for input for the RFC 3062 LDAP Password Modify Extended Operation.
 * <pre>
 * .------------------------------------------------- -.
 * |        Password Modify Extended Operation         |
 * +---------------------------------------------------+
 * | User identity: [                                ] |
 * |                [ ] Use bind user identity         |
 * | Old password:  [                                ] |
 * |                [ ] Old password not available     |
 * | New password:  [                                ] |
 * |                [ ] Generate new password          |
 * |                [ ] Show passwords                 |
 * |                                                   |
 * |                                 (Cancel) (  OK  ) |
 * .___________________________________________________.
 * </pre>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordModifyExtendedOperationDialog extends Dialog
{
    private IBrowserConnection connection;
    private IEntry entry;

    private Dn userIdentity = null;
    private String oldPassword = StringUtils.EMPTY;
    private String newPassword = StringUtils.EMPTY;

    private EntryWidget entryWidget;
    private Button useBindUserIdentityCheckbox;
    private Text oldPasswordText;
    private Button noOldPasswordCheckbox;
    private Text newPasswordText;
    private Button generateNewPasswordCheckbox;
    private Button showPasswordsCheckbox;


    public PasswordModifyExtendedOperationDialog( Shell parentShell, IBrowserConnection connection, IEntry entry )
    {
        super( parentShell );
        this.connection = connection;
        this.entry = entry;
        if ( entry != null )
        {
            this.userIdentity = entry.getDn();
        }
    }


    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );

        shell.setText( Messages.getString( "PasswordModifyExtendedOperationDialog.Title" ) ); //$NON-NLS-1$ );
    }


    @Override
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            userIdentity = entryWidget.getDn();
            oldPassword = oldPasswordText.getText();
            newPassword = newPasswordText.getText();

            // Build extended request
            LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
            PasswordModifyRequest request = ( PasswordModifyRequest ) ldapApiService.getExtendedRequestFactories()
                .get( PasswordModifyRequest.EXTENSION_OID ).newRequest();
            if ( !useBindUserIdentityCheckbox.getSelection() )
            {
                request.setUserIdentity( Strings.getBytesUtf8( userIdentity.getName() ) );
            }
            if ( !noOldPasswordCheckbox.getSelection() )
            {
                request.setOldPassword( Strings.getBytesUtf8( oldPassword ) );
            }
            if ( !generateNewPasswordCheckbox.getSelection() )
            {
                request.setNewPassword( Strings.getBytesUtf8( newPassword ) );
            }
            ExtendedOperationRunnable runnable = new ExtendedOperationRunnable( connection, request );

            // Execute extended operations
            ProgressMonitorDialog dialog = new ProgressMonitorDialog( getShell() );
            IStatus status = RunnableContextRunner.execute( runnable, dialog, true );

            // Check for error status
            if ( !status.isOK() )
            {
                // Error already handled, don't close dialog
                return;
            }

            // Update entry
            if ( entry != null )
            {
                EventRegistry.fireEntryUpdated( new EntryModificationEvent( entry.getBrowserConnection(), entry ),
                    this );
            }

            // Show generated password
            PasswordModifyResponse response = ( PasswordModifyResponse ) runnable.getResponse();
            if ( response.getGenPassword() != null )
            {
                String generatedPassword = Strings.utf8ToString( response.getGenPassword() );
                new MessageDialogWithTextarea( getShell(),
                    Messages.getString( "PasswordModifyExtendedOperationDialog.GeneratedPasswordTitle" ),
                    Messages.getString( "PasswordModifyExtendedOperationDialog.GeneratedPasswordMessage" ),
                    generatedPassword ).open();
            }

            // Continue to close dialog
        }
        else
        {
            userIdentity = null;
            oldPassword = null;
            newPassword = null;
        }

        super.buttonPressed( buttonId );
    }

    /**
     * The listener for the "use bind user identity" checkbox
     */
    private SelectionAdapter useBindUserIdentityCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent event )
        {
            if ( useBindUserIdentityCheckbox.getSelection() )
            {
                entryWidget.setInput( connection, null );
                entryWidget.setEnabled( false );
            }
            else
            {
                entryWidget.setEnabled( true );
            }
            validate();
        }
    };

    /**
     * The listener for the "no old password" checkbox
     */
    private SelectionAdapter noOldPasswordCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent event )
        {
            if ( noOldPasswordCheckbox.getSelection() )
            {
                oldPasswordText.setText( StringUtils.EMPTY );
                oldPasswordText.setEnabled( false );
            }
            else
            {
                oldPasswordText.setEnabled( true );
            }
            validate();
        }
    };
    /**
     * The listener for the "generate new password" checkbox
     */
    private SelectionAdapter generateNewPasswordCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent event )
        {
            if ( generateNewPasswordCheckbox.getSelection() )
            {
                newPasswordText.setText( StringUtils.EMPTY );
                newPasswordText.setEnabled( false );
            }
            else
            {
                newPasswordText.setEnabled( true );
            }
            validate();
        }
    };

    /**
     * The listener for the "show passwords" checkbox
     */
    private SelectionAdapter showPasswordsCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent event )
        {
            if ( showPasswordsCheckbox.getSelection() )
            {
                oldPasswordText.setEchoChar( '\0' );
                newPasswordText.setEchoChar( '\0' );
            }
            else
            {
                oldPasswordText.setEchoChar( '\u2022' );
                newPasswordText.setEchoChar( '\u2022' );
            }
        }
    };


    @Override
    protected Control createContents( Composite parent )
    {
        Control contents = super.createContents( parent );
        validate();
        return contents;
    }


    @Override
    protected Control createDialogArea( Composite parent )
    {
        // Composite
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 3, false );
        layout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
        layout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
        layout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
        layout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
        composite.setLayout( layout );
        GridData compositeGridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        compositeGridData.widthHint = convertHorizontalDLUsToPixels(
            IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH * 3 / 2 );
        composite.setLayoutData( compositeGridData );

        // User identity
        BaseWidgetUtils.createLabel( composite,
            Messages.getString( "PasswordModifyExtendedOperationDialog.UserIdentity" ), 1 ); //$NON-NLS-1$
        entryWidget = new EntryWidget( connection, userIdentity );
        entryWidget.addWidgetModifyListener( event -> validate() );
        entryWidget.createWidget( composite );

        // Use bind user identity checkbox
        BaseWidgetUtils.createLabel( composite, StringUtils.EMPTY, 1 );
        useBindUserIdentityCheckbox = BaseWidgetUtils.createCheckbox( composite,
            Messages.getString( "PasswordModifyExtendedOperationDialog.UseBindUserIdentity" ), 2 ); //$NON-NLS-1$
        useBindUserIdentityCheckbox.addSelectionListener( useBindUserIdentityCheckboxListener );

        // Old password text
        BaseWidgetUtils.createLabel( composite,
            Messages.getString( "PasswordModifyExtendedOperationDialog.OldPassword" ), 1 ); //$NON-NLS-1$
        oldPasswordText = BaseWidgetUtils.createText( composite, oldPassword, 2 );
        oldPasswordText.setEchoChar( '\u2022' );
        oldPasswordText.addModifyListener( event -> validate() );

        // No old password checkbox
        BaseWidgetUtils.createLabel( composite, StringUtils.EMPTY, 1 );
        noOldPasswordCheckbox = BaseWidgetUtils.createCheckbox( composite,
            Messages.getString( "PasswordModifyExtendedOperationDialog.NoOldPassword" ), 2 ); //$NON-NLS-1$
        noOldPasswordCheckbox.addSelectionListener( noOldPasswordCheckboxListener );

        // New password text
        BaseWidgetUtils.createLabel( composite,
            Messages.getString( "PasswordModifyExtendedOperationDialog.NewPassword" ), 1 ); //$NON-NLS-1$
        newPasswordText = BaseWidgetUtils.createText( composite, newPassword, 2 );
        newPasswordText.setEchoChar( '\u2022' );
        newPasswordText.addModifyListener( event -> validate() );

        // Generate new password checkbox
        BaseWidgetUtils.createLabel( composite, StringUtils.EMPTY, 1 );
        generateNewPasswordCheckbox = BaseWidgetUtils.createCheckbox( composite,
            Messages.getString( "PasswordModifyExtendedOperationDialog.GenerateNewPassword" ), 2 ); //$NON-NLS-1$
        generateNewPasswordCheckbox.addSelectionListener( generateNewPasswordCheckboxListener );

        // Show password checkbox
        BaseWidgetUtils.createLabel( composite, StringUtils.EMPTY, 1 );
        showPasswordsCheckbox = BaseWidgetUtils.createCheckbox( composite,
            Messages.getString( "PasswordModifyExtendedOperationDialog.ShowPasswords" ), 2 ); //$NON-NLS-1$
        showPasswordsCheckbox.addSelectionListener( showPasswordsCheckboxListener );

        applyDialogFont( composite );

        return composite;
    }


    private void validate()
    {
        if ( getButton( IDialogConstants.OK_ID ) != null )
        {
            boolean userIdentityInputValid = useBindUserIdentityCheckbox.getSelection()
                || ( entryWidget.getDn() != null && !entryWidget.getDn().isEmpty() );
            boolean oldPasswordInputValid = noOldPasswordCheckbox.getSelection()
                || !oldPasswordText.getText().isEmpty();
            boolean newPasswordInputValid = generateNewPasswordCheckbox.getSelection()
                || !newPasswordText.getText().isEmpty();
            getButton( IDialogConstants.OK_ID )
                .setEnabled( userIdentityInputValid && oldPasswordInputValid && newPasswordInputValid );
        }
    }


    /**
     * Returns the user identity.
     * 
     * @return the user identity, may be null if dialog was canceled
     */
    public Dn getUserIdentity()
    {
        return userIdentity;
    }


    /**
     * Returns the old password.
     * 
     * @return the old password, may be empty, null if dialog was canceled
     */
    public String getOldPassword()
    {
        return oldPassword;
    }


    /**
     * Returns the new password.
     * 
     * @return the new password, may be empty, null if dialog was canceled
     */
    public String getNewPassword()
    {
        return newPassword;
    }
}
