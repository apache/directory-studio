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

package org.apache.directory.studio.valueeditors.password;


import java.util.Arrays;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.jobs.CheckBindRunnable;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.Password;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;


/**
 * The PasswordDialog is used from the password value editor to view the current password
 * and to enter a new password.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PasswordDialog extends Dialog
{

    /** The supported hash methods */
    private static final String[] HASH_METHODS =
        { Password.HASH_METHOD_SHA, Password.HASH_METHOD_SSHA, Password.HASH_METHOD_MD5, Password.HASH_METHOD_SMD5,
            Password.HASH_METHOD_CRYPT, Password.HASH_METHOD_NO };

    private static final int CURRENT_TAB = 0;

    private static final int NEW_TAB = 1;

    private static final String SELECTED_TAB_DIALOGSETTINGS_KEY = PasswordDialog.class.getName() + ".tab";

    private static final String SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY = PasswordDialog.class.getName()
        + ".hashMethod";

    private TabFolder tabFolder;

    private TabItem currentTab;

    private TabItem newTab;

    private IEntry entry;

    private Password currentPassword;

    private Composite currentPasswordContainer;

    private Text currentPasswordText;

    private Text currentPasswordHashMethodText;

    private Text currentPasswordValueHexText;

    private Text currentPasswordSaltHexText;

    private Button showCurrentPasswordDetailsButton;

    private Text testPasswordText;

    private Button verifyPasswordButton;

    private Button bindPasswordButton;

    private Password newPassword;

    private Composite newPasswordContainer;

    private Text newPasswordText;

    private Combo newPasswordHashMethodCombo;

    private Text newPasswordPreviewText;

    private Text newPasswordPreviewValueHexText;

    private Text newPasswordPreviewSaltHexText;

    private Button newSaltButton;

    private Button showNewPasswordDetailsButton;

    private byte[] returnPassword;

    private Button okButton;


    /**
     * Creates a new instance of PasswordDialog.
     * 
     * @param parentShell the parent shell
     * @param currentPassword the current password, null if none
     * @param entry the entry used to bind 
     */
    public PasswordDialog( Shell parentShell, byte[] currentPassword, IEntry entry )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );

        try
        {
            this.currentPassword = currentPassword != null ? new Password( currentPassword ) : null;
        }
        catch ( IllegalArgumentException e )
        {
        }
        this.entry = entry;

        this.returnPassword = null;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Password Editor" );
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_PASSWORDEDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        // create password
        if ( newPassword != null )
        {
            returnPassword = newPassword.toBytes();
        }
        else
        {
            returnPassword = null;
        }

        // save selected hash method to dialog settings, selected tab will be
        // saved int close()
        ValueEditorsActivator.getDefault().getDialogSettings().put( SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY,
            newPasswordHashMethodCombo.getText() );

        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    public boolean close()
    {
        // save selected tab to dialog settings
        ValueEditorsActivator.getDefault().getDialogSettings().put( SELECTED_TAB_DIALOGSETTINGS_KEY,
            tabFolder.getSelectionIndex() );

        return super.close();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        // load dialog settings
        try
        {
            int tabIndex = ValueEditorsActivator.getDefault().getDialogSettings().getInt(
                SELECTED_TAB_DIALOGSETTINGS_KEY );
            if ( currentPassword == null || currentPassword.toBytes().length == 0 )
            {
                tabIndex = NEW_TAB;
            }
            tabFolder.setSelection( tabIndex );
        }
        catch ( Exception e )
        {
        }
        try
        {
            String hashMethod = ValueEditorsActivator.getDefault().getDialogSettings().get(
                SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY );
            if ( Arrays.asList( HASH_METHODS ).contains( hashMethod ) )
            {
                newPasswordHashMethodCombo.setText( hashMethod );
            }
        }
        catch ( Exception e )
        {
        }

        // update on load
        updateTabFolder();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 3 / 2;
        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) / 2;
        composite.setLayoutData( gd );

        tabFolder = new TabFolder( composite, SWT.TOP );
        GridLayout mainLayout = new GridLayout();
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        tabFolder.setLayout( mainLayout );
        tabFolder.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        tabFolder.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateTabFolder();
            }
        } );

        // current password
        if ( currentPassword != null && currentPassword.toBytes().length > 0 )
        {
            currentPasswordContainer = new Composite( tabFolder, SWT.NONE );
            GridLayout currentLayout = new GridLayout( 2, false );
            currentLayout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
            currentLayout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
            currentLayout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
            currentLayout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
            currentPasswordContainer.setLayout( currentLayout );

            BaseWidgetUtils.createLabel( currentPasswordContainer, "Current Password:", 1 );
            currentPasswordText = BaseWidgetUtils.createReadonlyText( currentPasswordContainer, "", 1 );

            new Label( currentPasswordContainer, SWT.NONE );
            Composite currentPasswordDetailContainer = BaseWidgetUtils.createColumnContainer( currentPasswordContainer,
                2, 1 );
            BaseWidgetUtils.createLabel( currentPasswordDetailContainer, "Hash Method:", 1 );
            currentPasswordHashMethodText = BaseWidgetUtils.createLabeledText( currentPasswordDetailContainer, "", 1 );
            BaseWidgetUtils.createLabel( currentPasswordDetailContainer, "Password (Hex):", 1 );
            currentPasswordValueHexText = BaseWidgetUtils.createLabeledText( currentPasswordDetailContainer, "", 1 );
            BaseWidgetUtils.createLabel( currentPasswordDetailContainer, "Salt (Hex):", 1 );
            currentPasswordSaltHexText = BaseWidgetUtils.createLabeledText( currentPasswordDetailContainer, "", 1 );
            showCurrentPasswordDetailsButton = BaseWidgetUtils.createCheckbox( currentPasswordDetailContainer,
                "Show current password details", 1 );
            showCurrentPasswordDetailsButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent arg0 )
                {
                    updateCurrentPasswordGroup();
                }
            } );

            BaseWidgetUtils.createLabel( currentPasswordContainer, "Verify Password:", 1 );
            testPasswordText = BaseWidgetUtils.createPasswordText( currentPasswordContainer, "", 1 );
            testPasswordText.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    updateCurrentPasswordGroup();
                }
            } );

            new Label( currentPasswordContainer, SWT.NONE );
            Composite verifyPasswordButtonContainer = BaseWidgetUtils.createColumnContainer( currentPasswordContainer,
                2, 1 );
            verifyPasswordButton = BaseWidgetUtils.createButton( verifyPasswordButtonContainer, "Verify", 1 );
            verifyPasswordButton.setEnabled( false );
            verifyPasswordButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent event )
                {
                    verifyCurrentPassword();
                }
            } );
            bindPasswordButton = BaseWidgetUtils.createButton( verifyPasswordButtonContainer, "Bind", 1 );
            bindPasswordButton.setEnabled( false );
            bindPasswordButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent event )
                {
                    bindCurrentPassword();
                }
            } );

            currentTab = new TabItem( tabFolder, SWT.NONE );
            currentTab.setText( "Current Password" );
            currentTab.setControl( currentPasswordContainer );
        }

        // new password
        newPasswordContainer = new Composite( tabFolder, SWT.NONE );
        GridLayout newLayout = new GridLayout( 2, false );
        newLayout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
        newLayout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
        newLayout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
        newLayout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
        newPasswordContainer.setLayout( newLayout );

        BaseWidgetUtils.createLabel( newPasswordContainer, "Enter New Password:", 1 );
        newPasswordText = BaseWidgetUtils.createPasswordText( newPasswordContainer, "", 1 );
        newPasswordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updateNewPasswordGroup();
            }
        } );

        BaseWidgetUtils.createLabel( newPasswordContainer, "Select Hash Method:", 1 );
        newPasswordHashMethodCombo = BaseWidgetUtils.createReadonlyCombo( newPasswordContainer, HASH_METHODS, 0, 1 );
        newPasswordHashMethodCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                updateNewPasswordGroup();
            }
        } );

        BaseWidgetUtils.createLabel( newPasswordContainer, "Password Preview:", 1 );
        newPasswordPreviewText = BaseWidgetUtils.createReadonlyText( newPasswordContainer, "", 1 );

        newSaltButton = BaseWidgetUtils.createButton( newPasswordContainer, "New Salt", 1 );
        newSaltButton.setEnabled( false );
        newSaltButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                updateNewPasswordGroup();
            }
        } );
        Composite newPasswordPreviewDetailContainer = BaseWidgetUtils
            .createColumnContainer( newPasswordContainer, 2, 1 );
        BaseWidgetUtils.createLabel( newPasswordPreviewDetailContainer, "Password (Hex):", 1 );
        newPasswordPreviewValueHexText = BaseWidgetUtils.createLabeledText( newPasswordPreviewDetailContainer, ":", 1 );
        BaseWidgetUtils.createLabel( newPasswordPreviewDetailContainer, "Salt (Hex):", 1 );
        newPasswordPreviewSaltHexText = BaseWidgetUtils.createLabeledText( newPasswordPreviewDetailContainer, "", 1 );
        showNewPasswordDetailsButton = BaseWidgetUtils.createCheckbox( newPasswordPreviewDetailContainer,
            "Show new password details", 1 );
        showNewPasswordDetailsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                updateNewPasswordGroup();
            }
        } );

        newTab = new TabItem( tabFolder, SWT.NONE );
        newTab.setText( "New Password" );
        newTab.setControl( newPasswordContainer );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Updates the current password tab.
     */
    private void updateCurrentPasswordGroup()
    {
        // set current password to the UI widgets
        if ( currentPassword != null )
        {
            currentPasswordHashMethodText.setText( Utils.getNonNullString( currentPassword.getHashMethod() ) );
            currentPasswordValueHexText.setText( Utils
                .getNonNullString( currentPassword.getHashedPasswordAsHexString() ) );
            currentPasswordSaltHexText.setText( Utils.getNonNullString( currentPassword.getSaltAsHexString() ) );
            currentPasswordText.setText( currentPassword.toString() );
        }

        // show password details?
        if ( showCurrentPasswordDetailsButton.getSelection() )
        {
            currentPasswordText.setEchoChar( '\0' );
            currentPasswordValueHexText.setEchoChar( '\0' );
            currentPasswordSaltHexText.setEchoChar( '\0' );
        }
        else
        {
            currentPasswordText.setEchoChar( '\u2022' );
            currentPasswordValueHexText.setEchoChar( '\u2022' );
            currentPasswordSaltHexText.setEchoChar( currentPasswordSaltHexText.getText().equals(
                Utils.getNonNullString( null ) ) ? '\0' : '\u2022' );
        }

        // enable/disable test field and buttons
        testPasswordText.setEnabled( currentPassword != null && currentPassword.getHashedPassword() != null
            && currentPassword.toBytes().length > 0 );
        verifyPasswordButton.setEnabled( testPasswordText.isEnabled() && !"".equals( testPasswordText.getText() ) );
        bindPasswordButton.setEnabled( testPasswordText.isEnabled() && !"".equals( testPasswordText.getText() )
            && entry != null && entry.getBrowserConnection().getConnection() != null );

        // default dialog button
        if ( verifyPasswordButton.isEnabled() )
        {
            getShell().setDefaultButton( verifyPasswordButton );
        }
        else
        {
            getShell().setDefaultButton( okButton );
        }
    }


    /**
     * Verifies the current password.
     */
    private void verifyCurrentPassword()
    {
        String testPassword = testPasswordText.getText();
        if ( currentPassword != null )
        {
            if ( currentPassword.verify( testPassword ) )
            {
                MessageDialog dialog = new MessageDialog( getShell(), "Password Verification", getShell().getImage(),
                    "Password verified sucessfully", MessageDialog.INFORMATION, new String[]
                        { IDialogConstants.OK_LABEL }, 0 );
                dialog.open();
            }
            else
            {
                MessageDialog dialog = new MessageDialog( getShell(), "Password Verification", getShell().getImage(),
                    "Password verification failed", MessageDialog.ERROR, new String[]
                        { IDialogConstants.OK_LABEL }, 0 );
                dialog.open();
            }
        }
    }


    /**
     * Binds to the directory using the test password.
     */
    private void bindCurrentPassword()
    {
        if ( !"".equals( testPasswordText.getText() ) && entry != null
            && entry.getBrowserConnection().getConnection() != null )
        {
            Connection connection = ( Connection ) entry.getBrowserConnection().getConnection().clone();
            connection.setName( null );
            connection.setBindPrincipal( entry.getDn().getUpName() );
            connection.setBindPassword( testPasswordText.getText() );
            connection.setAuthMethod( AuthenticationMethod.SIMPLE );

            CheckBindRunnable runnable = new CheckBindRunnable( connection );
            IStatus status = RunnableContextRunner.execute( runnable, null, true );
            if ( status.isOK() )
            {
                MessageDialog.openInformation( Display.getDefault().getActiveShell(), "Check Authentication",
                    "The authentication was successful." );
            }
        }
    }


    /**
     * Updates the new password tab.
     */
    private void updateNewPasswordGroup()
    {
        // set new password to the UI widgets
        newPassword = new Password( newPasswordHashMethodCombo.getText(), newPasswordText.getText() );
        if ( !"".equals( newPasswordText.getText() ) || newPassword.getHashMethod() == null )
        {
            newPasswordPreviewValueHexText
                .setText( Utils.getNonNullString( newPassword.getHashedPasswordAsHexString() ) );
            newPasswordPreviewSaltHexText.setText( Utils.getNonNullString( newPassword.getSaltAsHexString() ) );
            newPasswordPreviewText.setText( newPassword.toString() );
            newSaltButton.setEnabled( newPassword.getSalt() != null );
            okButton.setEnabled( true );
            getShell().setDefaultButton( okButton );
        }
        else
        {
            newPassword = null;
            newPasswordPreviewValueHexText.setText( Utils.getNonNullString( null ) );
            newPasswordPreviewSaltHexText.setText( Utils.getNonNullString( null ) );
            newPasswordPreviewText.setText( Utils.getNonNullString( null ) );
            newSaltButton.setEnabled( false );
            okButton.setEnabled( false );
        }

        // show password details?
        if ( showNewPasswordDetailsButton.getSelection() )
        {
            newPasswordPreviewText.setEchoChar( '\0' );
            newPasswordPreviewValueHexText.setEchoChar( '\0' );
            newPasswordPreviewSaltHexText.setEchoChar( '\0' );
        }
        else
        {
            newPasswordPreviewText.setEchoChar( newPasswordPreviewText.getText()
                .equals( Utils.getNonNullString( null ) ) ? '\0' : '\u2022' );
            newPasswordPreviewValueHexText.setEchoChar( newPasswordPreviewValueHexText.getText().equals(
                Utils.getNonNullString( null ) ) ? '\0' : '\u2022' );
            newPasswordPreviewSaltHexText.setEchoChar( newPasswordPreviewSaltHexText.getText().equals(
                Utils.getNonNullString( null ) ) ? '\0' : '\u2022' );
        }
    }


    /**
     * Updates the tab folder and the tabs.
     */
    private void updateTabFolder()
    {
        if ( testPasswordText != null && newPasswordText != null )
        {
            if ( tabFolder.getSelectionIndex() == CURRENT_TAB )
            {
                testPasswordText.setFocus();
            }
            else if ( tabFolder.getSelectionIndex() == NEW_TAB )
            {
                newPasswordText.setFocus();
            }
            updateCurrentPasswordGroup();
            updateNewPasswordGroup();
        }
    }


    /**
     * Gets the new password.
     * 
     * @return the password, either encypted by the selected
     *         algorithm or as plain text.
     */
    public byte[] getNewPassword()
    {
        return returnPassword;
    }

}
