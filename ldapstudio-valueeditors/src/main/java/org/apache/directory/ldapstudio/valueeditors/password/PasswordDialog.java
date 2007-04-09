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

package org.apache.directory.ldapstudio.valueeditors.password;


import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.common.jobs.RunnableContextJobAdapter;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.core.jobs.CheckBindJob;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.Password;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsActivator;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsConstants;
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


public class PasswordDialog extends Dialog
{

    public static final String DIALOG_TITLE = "Password Editor";

    public static final String[] HASH_METHODS =
        { Password.HASH_METHOD_SHA, Password.HASH_METHOD_SSHA, Password.HASH_METHOD_MD5, Password.HASH_METHOD_SMD5,
            Password.HASH_METHOD_CRYPT, Password.HASH_METHOD_NO };

    public static final int CURRENT_TAB = 0;

    public static final int NEW_TAB = 1;

    public static final String SELECTED_TAB_DIALOGSETTINGS_KEY = PasswordDialog.class.getName() + ".tab";

    public static final String SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY = PasswordDialog.class.getName() + ".hashMethod";

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

    private byte[] returnPassword;

    private Button okButton;


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


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_PASSWORDEDITOR ) );
    }


    protected void okPressed()
    {
        // create password
        if ( newPassword != null )
        {
            this.returnPassword = this.newPassword.toBytes();
        }
        else
        {
            this.returnPassword = null;
        }

        // save selected hash method to dialog settings, selected tab will be
        // saved int close()
        ValueEditorsActivator.getDefault().getDialogSettings().put( SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY,
            this.newPasswordHashMethodCombo.getText() );

        super.okPressed();
    }


    public boolean close()
    {
        // save selected tab to dialog settings
        ValueEditorsActivator.getDefault().getDialogSettings().put( SELECTED_TAB_DIALOGSETTINGS_KEY,
            this.tabFolder.getSelectionIndex() );

        return super.close();
    }


    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        // load dialog settings
        try
        {
            int tabIndex = ValueEditorsActivator.getDefault().getDialogSettings().getInt( SELECTED_TAB_DIALOGSETTINGS_KEY );
            if ( this.currentPassword == null || this.currentPassword.toBytes().length == 0 )
            {
                tabIndex = NEW_TAB;
            }
            this.tabFolder.setSelection( tabIndex );
        }
        catch ( Exception e )
        {
        }
        try
        {
            String hashMethod = ValueEditorsActivator.getDefault().getDialogSettings()
                .get( SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY );
            if ( Arrays.asList( HASH_METHODS ).contains( hashMethod ) )
            {
                this.newPasswordHashMethodCombo.setText( hashMethod );
            }
        }
        catch ( Exception e )
        {
        }

        // update on load
        updateTabFolder();
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 3 / 2;
        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) / 2;
        composite.setLayoutData( gd );

        this.tabFolder = new TabFolder( composite, SWT.TOP );
        GridLayout mainLayout = new GridLayout();
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        this.tabFolder.setLayout( mainLayout );
        this.tabFolder.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        this.tabFolder.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateTabFolder();
            }
        } );

        // current password
        if ( this.currentPassword != null && this.currentPassword.toBytes().length > 0 )
        {
            currentPasswordContainer = new Composite( this.tabFolder, SWT.NONE );
            GridLayout currentLayout = new GridLayout( 2, false );
            currentLayout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
            currentLayout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
            currentLayout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
            currentLayout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
            currentPasswordContainer.setLayout( currentLayout );

            BaseWidgetUtils.createLabel( currentPasswordContainer, "Current Password:", 1 );
            currentPasswordText = BaseWidgetUtils.createReadonlyText( currentPasswordContainer, "", 1 );

            /* Label dummy = */new Label( currentPasswordContainer, SWT.NONE );
            Composite currentPasswordDetailContainer = BaseWidgetUtils.createColumnContainer( currentPasswordContainer,
                2, 1 );
            BaseWidgetUtils.createLabel( currentPasswordDetailContainer, "Hash Method:", 1 );
            currentPasswordHashMethodText = BaseWidgetUtils.createLabeledText( currentPasswordDetailContainer, "", 1 );
            BaseWidgetUtils.createLabel( currentPasswordDetailContainer, "Password (Hex):", 1 );
            currentPasswordValueHexText = BaseWidgetUtils.createLabeledText( currentPasswordDetailContainer, "", 1 );
            BaseWidgetUtils.createLabel( currentPasswordDetailContainer, "Salt (Hex):", 1 );
            currentPasswordSaltHexText = BaseWidgetUtils.createLabeledText( currentPasswordDetailContainer, "", 1 );

            BaseWidgetUtils.createLabel( currentPasswordContainer, "Verify Password:", 1 );
            testPasswordText = BaseWidgetUtils.createPasswordText( currentPasswordContainer, "", 1 );
            testPasswordText.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    updateCurrentPasswordGroup();
                }
            } );

            /* Label dummyLabel = */new Label( currentPasswordContainer, SWT.NONE );
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

            this.currentTab = new TabItem( this.tabFolder, SWT.NONE );
            this.currentTab.setText( "Current Password" );
            this.currentTab.setControl( currentPasswordContainer );
        }

        // new password
        newPasswordContainer = new Composite( this.tabFolder, SWT.NONE );
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

        this.newTab = new TabItem( this.tabFolder, SWT.NONE );
        this.newTab.setText( "New Password" );
        this.newTab.setControl( newPasswordContainer );

        applyDialogFont( composite );
        return composite;
    }


    private void updateCurrentPasswordGroup()
    {
        if ( this.currentPassword != null )
        {
            this.currentPasswordHashMethodText.setText( Utils.getNonNullString( this.currentPassword.getHashMethod() ) );
            this.currentPasswordValueHexText.setText( Utils.getNonNullString( this.currentPassword
                .getHashedPasswordAsHexString() ) );
            this.currentPasswordSaltHexText
                .setText( Utils.getNonNullString( this.currentPassword.getSaltAsHexString() ) );
            this.currentPasswordText.setText( this.currentPassword.toString() );
        }

        this.testPasswordText.setEnabled( this.currentPassword != null
            && this.currentPassword.getHashedPassword() != null && this.currentPassword.toBytes().length > 0 );
        this.verifyPasswordButton.setEnabled( this.testPasswordText.isEnabled()
            && !"".equals( this.testPasswordText.getText() ) );
        this.bindPasswordButton.setEnabled( this.testPasswordText.isEnabled()
            && !"".equals( this.testPasswordText.getText() ) && this.entry != null );

        if ( this.verifyPasswordButton.isEnabled() )
            getShell().setDefaultButton( this.verifyPasswordButton );
        else
            getShell().setDefaultButton( this.okButton );
        // this.currentPasswordText.getParent().layout();
    }


    private void verifyCurrentPassword()
    {
        String testPassword = this.testPasswordText.getText();
        if ( this.currentPassword != null )
        {
            if ( this.currentPassword.verify( testPassword ) )
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


    private void bindCurrentPassword()
    {

        if ( !"".equals( this.testPasswordText.getText() ) && this.entry != null )
        {

            IConnection connection = ( IConnection ) this.entry.getConnection().clone();;
            connection.setName( null );
            connection.setBindPrincipal( this.entry.getDn().toString() );
            connection.setBindPassword( this.testPasswordText.getText() );
            connection.setAuthMethod( IConnection.AUTH_SIMPLE );

            CheckBindJob job = new CheckBindJob( connection );
            RunnableContextJobAdapter.execute( job );
            if ( job.getExternalResult().isOK() )
            {
                MessageDialog.openInformation( Display.getDefault().getActiveShell(), "Check Authentication",
                    "The authentication was successful." );
            }

        }
    }


    private void updateNewPasswordGroup()
    {
        this.newPassword = new Password( this.newPasswordHashMethodCombo.getText(), this.newPasswordText.getText() );
        if ( !"".equals( this.newPasswordText.getText() ) || this.newPassword.getHashMethod() == null )
        {
            newPasswordPreviewValueHexText.setText( Utils.getNonNullString( this.newPassword
                .getHashedPasswordAsHexString() ) );
            newPasswordPreviewSaltHexText.setText( Utils.getNonNullString( this.newPassword.getSaltAsHexString() ) );
            newPasswordPreviewText.setText( this.newPassword.toString() );
            newSaltButton.setEnabled( this.newPassword.getSalt() != null );
            this.okButton.setEnabled( true );
            getShell().setDefaultButton( this.okButton );
        }
        else
        {
            this.newPassword = null;
            newPasswordPreviewValueHexText.setText( Utils.getNonNullString( null ) );
            newPasswordPreviewSaltHexText.setText( Utils.getNonNullString( null ) );
            newPasswordPreviewText.setText( Utils.getNonNullString( null ) );
            newSaltButton.setEnabled( false );
            this.okButton.setEnabled( false );
        }
    }


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
     * 
     * 
     * @return Returns the password, either encypted by the selected
     *         algorithm or as plain text.
     */
    public byte[] getNewPassword()
    {
        return this.returnPassword;
    }

}
