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


import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.jobs.CheckBindRunnable;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.Password;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
 */
public class PasswordDialog extends Dialog
{
    /** The constant for no hash method */
    private static final String NO_HASH_METHOD = "NO-HASH-METHOD";

    /** The supported hash methods */
    private static final Object[] HASH_METHODS =
        {
            LdapSecurityConstants.HASH_METHOD_SHA,
            LdapSecurityConstants.HASH_METHOD_SHA256,
            LdapSecurityConstants.HASH_METHOD_SHA384,
            LdapSecurityConstants.HASH_METHOD_SHA512,
            LdapSecurityConstants.HASH_METHOD_SSHA,
            LdapSecurityConstants.HASH_METHOD_SSHA256,
            LdapSecurityConstants.HASH_METHOD_SSHA384,
            LdapSecurityConstants.HASH_METHOD_SSHA512,
            LdapSecurityConstants.HASH_METHOD_MD5,
            LdapSecurityConstants.HASH_METHOD_SMD5,
            LdapSecurityConstants.HASH_METHOD_PKCS5S2,
            LdapSecurityConstants.HASH_METHOD_CRYPT,
            LdapSecurityConstants.HASH_METHOD_CRYPT_MD5,
            LdapSecurityConstants.HASH_METHOD_CRYPT_SHA256,
            LdapSecurityConstants.HASH_METHOD_CRYPT_SHA512,
            NO_HASH_METHOD };

    /** Constant for the Current Password tab */
    private static final int CURRENT_TAB = 0;

    /** Constant for the New Password tab */
    private static final int NEW_TAB = 1;

    /** Constant for the selected tab dialog settings key */
    private static final String SELECTED_TAB_DIALOGSETTINGS_KEY = PasswordDialog.class.getName() + ".tab"; //$NON-NLS-1$

    /** Constant for the selected hash method dialog settings key */
    private static final String SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY = PasswordDialog.class.getName()
        + ".hashMethod"; //$NON-NLS-1$

    /** The display mode */
    private DisplayMode displayMode;

    /** The associated entry for binding */
    private IEntry entry;

    /** The current password */
    private Password currentPassword;

    /** The new password */
    private Password newPassword;

    /** The return password */
    private byte[] returnPassword;

    // UI widgets
    private Button okButton;
    private TabFolder tabFolder;
    private TabItem currentPasswordTab;
    private Composite currentPasswordComposite;
    private Text currentPasswordText;
    private Text currentPasswordHashMethodText;
    private Text currentPasswordValueHexText;
    private Text currentPasswordSaltHexText;
    private Button showCurrentPasswordDetailsButton;
    private Text testPasswordText;
    private Text testBindDnText;
    private Button showTestPasswordDetailsButton;
    private Button verifyPasswordButton;
    private Button bindPasswordButton;
    private TabItem newPasswordTab;
    private Composite newPasswordComposite;
    private Text newPasswordText;
    private Text confirmNewPasswordText;
    private ComboViewer newPasswordHashMethodComboViewer;
    private Text newPasswordPreviewText;
    private Text newPasswordPreviewValueHexText;
    private Text newPasswordPreviewSaltHexText;
    private Button newSaltButton;
    private Button showNewPasswordDetailsButton;


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
        shell.setText( Messages.getString( "PasswordDialog.PasswordEditor" ) ); //$NON-NLS-1$
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
        // saved on close()
        LdapSecurityConstants selectedHashMethod = getSelectedNewPasswordHashMethod();

        if ( selectedHashMethod == null )
        {
            ValueEditorsActivator.getDefault().getDialogSettings().put( SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY,
                NO_HASH_METHOD );
        }
        else
        {
            ValueEditorsActivator.getDefault().getDialogSettings().put( SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY,
                selectedHashMethod.getName() );
        }

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
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
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
            String hashMethodName = ValueEditorsActivator.getDefault().getDialogSettings().get(
                SELECTED_HASH_METHOD_DIALOGSETTINGS_KEY );

            LdapSecurityConstants hashMethod = LdapSecurityConstants.getAlgorithm( hashMethodName );

            if ( ( hashMethod == null ) || NO_HASH_METHOD.equals( hashMethodName ) )
            {
                newPasswordHashMethodComboViewer.setSelection( new StructuredSelection( NO_HASH_METHOD ) );
            }
            else
            {
                newPasswordHashMethodComboViewer.setSelection( new StructuredSelection( hashMethod ) );
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
        // Composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 3 / 2;
        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 2 / 3;
        composite.setLayoutData( gd );

        // Tab folder
        tabFolder = new TabFolder( composite, SWT.TOP );
        tabFolder.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        tabFolder.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateTabFolder();
            }
        } );

        // Checking the current password
        if ( currentPassword != null && currentPassword.toBytes().length > 0 )
        {
            // Setting the display mode
            displayMode = DisplayMode.CURRENT_AND_NEW_PASSWORD;

            // Creating the current password tab
            createCurrentPasswordTab();
        }
        else
        {
            // Setting the display mode
            displayMode = DisplayMode.NEW_PASSWORD_ONLY;
        }

        // Creating the new password tab
        createNewPasswordTab();

        addListeners();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Creates the current password tab.
     */
    private void createCurrentPasswordTab()
    {
        // Current password composite
        currentPasswordComposite = new Composite( tabFolder, SWT.NONE );
        GridLayout currentLayout = new GridLayout( 2, false );
        currentLayout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
        currentLayout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
        currentLayout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
        currentLayout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
        currentPasswordComposite.setLayout( currentLayout );
        currentPasswordComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Current password text
        BaseWidgetUtils.createLabel( currentPasswordComposite, Messages
            .getString( "PasswordDialog.CurrentPassword" ) + ":", 1 ); //$NON-NLS-1$//$NON-NLS-2$
        currentPasswordText = BaseWidgetUtils.createReadonlyText( currentPasswordComposite, "", 1 ); //$NON-NLS-1$

        // Current password details composite
        new Label( currentPasswordComposite, SWT.NONE );
        Composite currentPasswordDetailsComposite = BaseWidgetUtils.createColumnContainer( currentPasswordComposite,
            2, 1 );

        // Current password hash method label
        BaseWidgetUtils.createLabel( currentPasswordDetailsComposite,
            Messages.getString( "PasswordDialog.HashMethod" ), 1 ); //$NON-NLS-1$
        currentPasswordHashMethodText = BaseWidgetUtils.createLabeledText( currentPasswordDetailsComposite, "", 1 ); //$NON-NLS-1$

        // Current password hex label
        BaseWidgetUtils.createLabel( currentPasswordDetailsComposite, Messages
            .getString( "PasswordDialog.PasswordHex" ), 1 ); //$NON-NLS-1$
        currentPasswordValueHexText = BaseWidgetUtils.createLabeledText( currentPasswordDetailsComposite, "", 1 ); //$NON-NLS-1$

        // Current password salt hex label
        BaseWidgetUtils.createLabel( currentPasswordDetailsComposite,
            Messages.getString( "PasswordDialog.SaltHex" ), 1 ); //$NON-NLS-1$
        currentPasswordSaltHexText = BaseWidgetUtils.createLabeledText( currentPasswordDetailsComposite, "", 1 ); //$NON-NLS-1$

        // Show current password details button
        showCurrentPasswordDetailsButton = BaseWidgetUtils.createCheckbox( currentPasswordDetailsComposite, Messages
            .getString( "PasswordDialog.ShowCurrentPasswordDetails" ), 2 ); //$NON-NLS-1$

        // Verify password text
        BaseWidgetUtils
            .createLabel( currentPasswordComposite, Messages.getString( "PasswordDialog.VerifyPassword" ), 1 ); //$NON-NLS-1$
        testPasswordText = BaseWidgetUtils.createText( currentPasswordComposite, "", 1 ); //$NON-NLS-1$

        // Verify password details composite
        new Label( currentPasswordComposite, SWT.NONE );
        Composite testPasswordDetailsComposite = BaseWidgetUtils.createColumnContainer( currentPasswordComposite, 2,
            1 );

        // Bind DN label
        BaseWidgetUtils.createLabel( testPasswordDetailsComposite, Messages.getString( "PasswordDialog.BindDn" ), 1 ); //$NON-NLS-1$
        testBindDnText = BaseWidgetUtils.createLabeledText( testPasswordDetailsComposite, "", 1 ); //$NON-NLS-1$

        // Show verify password details button
        showTestPasswordDetailsButton = BaseWidgetUtils.createCheckbox( testPasswordDetailsComposite, Messages
            .getString( "PasswordDialog.ShowTestPasswordDetails" ), 2 ); //$NON-NLS-1$

        // Verify password buttons composite
        new Label( currentPasswordComposite, SWT.NONE );
        Composite verifyPasswordButtonsComposite = BaseWidgetUtils.createColumnContainer( currentPasswordComposite,
            2, 1 );

        // Verify button
        verifyPasswordButton = BaseWidgetUtils.createButton( verifyPasswordButtonsComposite, Messages
            .getString( "PasswordDialog.Verify" ), 1 ); //$NON-NLS-1$
        verifyPasswordButton.setEnabled( false );

        // Bind button
        bindPasswordButton = BaseWidgetUtils.createButton( verifyPasswordButtonsComposite, Messages
            .getString( "PasswordDialog.Bind" ), 1 ); //$NON-NLS-1$
        bindPasswordButton.setEnabled( false );

        // Current password tab
        currentPasswordTab = new TabItem( tabFolder, SWT.NONE );
        currentPasswordTab.setText( Messages.getString( "PasswordDialog.CurrentPassword" ) ); //$NON-NLS-1$
        currentPasswordTab.setControl( currentPasswordComposite );
    }


    /**
     * Creates the new password tab.
     */
    private void createNewPasswordTab()
    {
        // New password composite
        newPasswordComposite = new Composite( tabFolder, SWT.NONE );
        GridLayout newLayout = new GridLayout( 2, false );
        newLayout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
        newLayout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
        newLayout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
        newLayout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
        newPasswordComposite.setLayout( newLayout );

        // New password text
        BaseWidgetUtils.createLabel( newPasswordComposite, Messages.getString( "PasswordDialog.EnterNewPassword" ), 1 ); //$NON-NLS-1$
        newPasswordText = BaseWidgetUtils.createText( newPasswordComposite, "", 1 ); //$NON-NLS-1$

        // Confirm new password text
        BaseWidgetUtils
            .createLabel( newPasswordComposite, Messages.getString( "PasswordDialog.ConfirmNewPassword" ), 1 ); //$NON-NLS-1$
        confirmNewPasswordText = BaseWidgetUtils.createText( newPasswordComposite, "", 1 ); //$NON-NLS-1$

        // New password hashing method combo
        BaseWidgetUtils.createLabel( newPasswordComposite, Messages.getString( "PasswordDialog.SelectHashMethod" ), 1 ); //$NON-NLS-1$
        newPasswordHashMethodComboViewer = new ComboViewer( newPasswordComposite );
        newPasswordHashMethodComboViewer.setContentProvider( new ArrayContentProvider() );
        newPasswordHashMethodComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                String hashMethod = getHashMethodName( element );

                if ( !"".equals( hashMethod ) )
                {
                    return hashMethod;
                }

                return super.getText( element );
            }
        } );
        newPasswordHashMethodComboViewer.setInput( HASH_METHODS );
        newPasswordHashMethodComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // New password preview text
        BaseWidgetUtils.createLabel( newPasswordComposite, Messages.getString( "PasswordDialog.PasswordPreview" ), 1 ); //$NON-NLS-1$
        newPasswordPreviewText = BaseWidgetUtils.createReadonlyText( newPasswordComposite, "", 1 ); //$NON-NLS-1$

        // New salt button
        newSaltButton = BaseWidgetUtils.createButton( newPasswordComposite, Messages
            .getString( "PasswordDialog.NewSalt" ), 1 ); //$NON-NLS-1$
        newSaltButton.setLayoutData( new GridData() );
        newSaltButton.setEnabled( false );

        // New password preview details composite
        Composite newPasswordPreviewDetailsComposite = BaseWidgetUtils.createColumnContainer( newPasswordComposite, 2,
            1 );

        // New password preview hex label
        BaseWidgetUtils.createLabel( newPasswordPreviewDetailsComposite,
            Messages.getString( "PasswordDialog.PasswordHex" ), 1 ); //$NON-NLS-1$
        newPasswordPreviewValueHexText = BaseWidgetUtils.createLabeledText( newPasswordPreviewDetailsComposite, ":", 1 ); //$NON-NLS-1$

        // New password preview salt hex label
        BaseWidgetUtils.createLabel( newPasswordPreviewDetailsComposite,
            Messages.getString( "PasswordDialog.SaltHex" ), 1 ); //$NON-NLS-1$
        newPasswordPreviewSaltHexText = BaseWidgetUtils.createLabeledText( newPasswordPreviewDetailsComposite, "", 1 ); //$NON-NLS-1$

        // Show new password details button
        showNewPasswordDetailsButton = BaseWidgetUtils.createCheckbox( newPasswordPreviewDetailsComposite, Messages
            .getString( "PasswordDialog.ShowNewPasswordDetails" ), 2 ); //$NON-NLS-1$

        // New password tab
        newPasswordTab = new TabItem( tabFolder, SWT.NONE );
        newPasswordTab.setText( Messages.getString( "PasswordDialog.NewPassword" ) ); //$NON-NLS-1$
        newPasswordTab.setControl( newPasswordComposite );
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        if ( displayMode == DisplayMode.CURRENT_AND_NEW_PASSWORD )
        {
            showCurrentPasswordDetailsButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent arg0 )
                {
                    updateCurrentPasswordGroup();
                }
            } );

            testPasswordText.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    updateCurrentPasswordGroup();
                }
            } );

            showTestPasswordDetailsButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent arg0 )
                {
                    updateCurrentPasswordGroup();
                }
            } );

            verifyPasswordButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent event )
                {
                    verifyCurrentPassword();
                }
            } );

            bindPasswordButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent event )
                {
                    bindCurrentPassword();
                }
            } );
        }

        newPasswordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updateNewPasswordGroup();
            }
        } );

        confirmNewPasswordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updateNewPasswordGroup();
            }
        } );

        newPasswordHashMethodComboViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                updateNewPasswordGroup();
            }
        } );

        newSaltButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                updateNewPasswordGroup();
            }
        } );

        showNewPasswordDetailsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                updateNewPasswordGroup();
            }
        } );
    }


    /**
     * Updates the current password tab.
     */
    private void updateCurrentPasswordGroup()
    {
        // set current password to the UI widgets
        if ( currentPassword != null )
        {
            currentPasswordHashMethodText.setText( getCurrentPasswordHashMethodName() );
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
        testBindDnText.setText( entry != null ? entry.getDn().getName() : Utils.getNonNullString( null ) );
        if ( showTestPasswordDetailsButton.getSelection() )
        {
            testPasswordText.setEchoChar( '\0' );
        }
        else
        {
            testPasswordText.setEchoChar( '\u2022' );
        }
        verifyPasswordButton.setEnabled( testPasswordText.isEnabled() && !"".equals( testPasswordText.getText() ) ); //$NON-NLS-1$
        bindPasswordButton.setEnabled( testPasswordText.isEnabled() && !"".equals( testPasswordText.getText() ) //$NON-NLS-1$
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

        okButton.setEnabled( false );
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
                MessageDialog dialog = new MessageDialog(
                    getShell(),
                    Messages.getString( "PasswordDialog.PasswordVerification" ), getShell().getImage(), //$NON-NLS-1$
                    Messages.getString( "PasswordDialog.PasswordVerifiedSuccessfully" ), MessageDialog.INFORMATION, new String[] //$NON-NLS-1$
                        { IDialogConstants.OK_LABEL }, 0 );
                dialog.open();
            }
            else
            {
                IStatus status = new Status( IStatus.ERROR, ValueEditorsConstants.PLUGIN_ID, 1,
                    Messages.getString( "PasswordDialog.PasswordVerificationFailed" ), null ); //$NON-NLS-1$
                ConnectionUIPlugin.getDefault().getExceptionHandler().handleException( status );
            }
        }
    }


    /**
     * Binds to the directory using the test password.
     */
    private void bindCurrentPassword()
    {
        if ( !"".equals( testPasswordText.getText() ) && entry != null //$NON-NLS-1$
            && entry.getBrowserConnection().getConnection() != null )
        {
            Connection connection = ( Connection ) entry.getBrowserConnection().getConnection().clone();
            connection.getConnectionParameter().setName( null );
            connection.getConnectionParameter().setBindPrincipal( entry.getDn().getName() );
            connection.getConnectionParameter().setBindPassword( testPasswordText.getText() );
            connection.getConnectionParameter().setAuthMethod( AuthenticationMethod.SIMPLE );

            CheckBindRunnable runnable = new CheckBindRunnable( connection );
            IStatus status = RunnableContextRunner.execute( runnable, null, true );
            if ( status.isOK() )
            {
                MessageDialog.openInformation( Display.getDefault().getActiveShell(), Messages
                    .getString( "PasswordDialog.CheckAuthentication" ), //$NON-NLS-1$
                    Messages.getString( "PasswordDialog.AuthenticationSuccessful" ) ); //$NON-NLS-1$
            }
        }
    }


    /**
     * Updates the new password tab.
     */
    private void updateNewPasswordGroup()
    {
        // set new password to the UI widgets
        newPassword = new Password( getSelectedNewPasswordHashMethod(), newPasswordText.getText() );
        if ( !"".equals( newPasswordText.getText() ) //$NON-NLS-1$
            && newPasswordText.getText().equals( confirmNewPasswordText.getText() ) )
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
            newPasswordText.setEchoChar( '\0' );
            confirmNewPasswordText.setEchoChar( '\0' );
            newPasswordPreviewText.setEchoChar( '\0' );
            newPasswordPreviewValueHexText.setEchoChar( '\0' );
            newPasswordPreviewSaltHexText.setEchoChar( '\0' );
        }
        else
        {
            newPasswordText.setEchoChar( '\u2022' );
            confirmNewPasswordText.setEchoChar( '\u2022' );
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
                updateCurrentPasswordGroup();
                testPasswordText.setFocus();
            }
            else if ( tabFolder.getSelectionIndex() == NEW_TAB )
            {
                updateNewPasswordGroup();
                newPasswordText.setFocus();
            }
        }
    }


    /**
     * Gets the selected new password hash method.
     *
     * @return the selected new password hash method
     */
    private LdapSecurityConstants getSelectedNewPasswordHashMethod()
    {
        StructuredSelection selection = ( StructuredSelection ) newPasswordHashMethodComboViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            Object selectedObject = selection.getFirstElement();

            if ( selectedObject instanceof LdapSecurityConstants )
            {
                return ( LdapSecurityConstants ) selectedObject;
            }
        }

        return null;
    }


    /**
     * Gets the name of the hash method.
     *
     * @param o the hash method object
     * @return the name of the hash method
     */
    private String getHashMethodName( Object o )
    {
        if ( o instanceof LdapSecurityConstants )
        {
            LdapSecurityConstants hashMethod = ( LdapSecurityConstants ) o;

            return hashMethod.getName();
        }
        else if ( ( o instanceof String ) && NO_HASH_METHOD.equals( o ) )
        {
            return BrowserCoreMessages.model__no_hash;
        }

        return null;
    }


    /**
     * Gets the current password hash method name.
     *
     * @return the current password hash method name
     */
    private String getCurrentPasswordHashMethodName()
    {
        LdapSecurityConstants hashMethod = currentPassword.getHashMethod();

        if ( hashMethod != null )
        {
            return Utils.getNonNullString( getHashMethodName( hashMethod ) );
        }
        else
        {
            return Utils.getNonNullString( getHashMethodName( NO_HASH_METHOD ) );
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

    /**
     * This enum contains the display modes for the dialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private enum DisplayMode
    {
        CURRENT_AND_NEW_PASSWORD,
        NEW_PASSWORD_ONLY
    }
}
