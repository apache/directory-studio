/*
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
package org.apache.directory.studio.openldap.common.ui.dialogs;


import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.Password;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.apache.directory.studio.valueeditors.password.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * The PasswordDialog is used from the password value editor to view the current password
 * and to enter a new password.
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
            LdapSecurityConstants.HASH_METHOD_CRYPT,
            NO_HASH_METHOD };

    /** The current password */
    private Password currentPassword;

    /** The new password */
    private Password newPassword;

    /** The return password*/
    private byte[] returnPassword;

    // UI Widgets
    private Button okButton;
    private Group currentPasswordGroup;
    private Text currentPasswordText;
    private Text currentPasswordHashMethodText;
    private Text currentPasswordValueHexText;
    private Text currentPasswordSaltHexText;
    private Button showCurrentPasswordDetailsButton;
    private Group newPasswordGroup;
    private Text newPasswordText;
    private ComboViewer newPasswordHashMethodComboViewer;
    private Text newPasswordPreviewText;
    private Button newSaltButton;
    private Text newPasswordPreviewValueHexText;
    private Text newPasswordPreviewSaltHexText;
    private Button showNewPasswordDetailsButton;


    /**
     * Creates a new instance of PasswordDialog.
     * 
     * @param parentShell the parent shell
     * @param currentPassword the current password, null if none
     * @param entry the entry used to bind 
     */
    public PasswordDialog( Shell parentShell, byte[] currentPassword )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );

        if ( currentPassword != null )
        {
            this.currentPassword = new Password( currentPassword );
        }
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

        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        if ( hasCurrentPassword() )
        {
            updateCurrentPasswordGroup();
        }

        updateNewPasswordGroup();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        //        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );// * 2 / 3;
        composite.setLayoutData( gd );

        if ( hasCurrentPassword() )
        {
            createCurrentPasswordGroup( composite );
        }

        createNewPasswordGroup( composite );

        addListeners();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Creates the current password group.
     *
     * @param parent the parent composite
     */
    private void createCurrentPasswordGroup( Composite parent )
    {
        currentPasswordGroup = BaseWidgetUtils.createGroup( parent, "Current Password", 1 );
        currentPasswordGroup.setLayout( new GridLayout( 2, false ) );
        currentPasswordGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Current password text
        BaseWidgetUtils.createLabel( currentPasswordGroup, Messages
            .getString( "PasswordDialog.CurrentPassword" ) + ":", 1 ); //$NON-NLS-1$//$NON-NLS-2$
        currentPasswordText = BaseWidgetUtils.createReadonlyText( currentPasswordGroup, "", 1 ); //$NON-NLS-1$

        // Current password details composite
        new Label( currentPasswordGroup, SWT.NONE );
        Composite currentPasswordDetailsComposite = BaseWidgetUtils.createColumnContainer( currentPasswordGroup,
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
    }


    /**
     * Creates the new password group.
     *
     * @param parent the parent composite
     */
    private void createNewPasswordGroup( Composite parent )
    {
        newPasswordGroup = BaseWidgetUtils.createGroup( parent, "New Password", 1 );
        newPasswordGroup.setLayout( new GridLayout( 2, false ) );
        newPasswordGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // New password text
        BaseWidgetUtils.createLabel( newPasswordGroup, Messages.getString( "PasswordDialog.EnterNewPassword" ), 1 ); //$NON-NLS-1$
        newPasswordText = BaseWidgetUtils.createText( newPasswordGroup, "", 1 ); //$NON-NLS-1$

        // New password hashing method combo
        BaseWidgetUtils.createLabel( newPasswordGroup, Messages.getString( "PasswordDialog.SelectHashMethod" ), 1 ); //$NON-NLS-1$
        newPasswordHashMethodComboViewer = new ComboViewer( newPasswordGroup );
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
        newPasswordHashMethodComboViewer.setSelection( new StructuredSelection( NO_HASH_METHOD ) );
        newPasswordHashMethodComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // New password preview text
        BaseWidgetUtils.createLabel( newPasswordGroup, Messages.getString( "PasswordDialog.PasswordPreview" ), 1 ); //$NON-NLS-1$
        newPasswordPreviewText = BaseWidgetUtils.createReadonlyText( newPasswordGroup, "", 1 ); //$NON-NLS-1$

        // New salt button
        newSaltButton = BaseWidgetUtils.createButton( newPasswordGroup, Messages
            .getString( "PasswordDialog.NewSalt" ), 1 ); //$NON-NLS-1$
        newSaltButton.setLayoutData( new GridData() );
        newSaltButton.setEnabled( false );

        // New password preview details composite
        Composite newPasswordPreviewDetailsComposite = BaseWidgetUtils.createColumnContainer( newPasswordGroup, 2,
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
    }


    /**
     * Updates the new password tab.
     */
    private void updateNewPasswordGroup()
    {
        // set new password to the UI widgets
        newPassword = new Password( getSelectedNewPasswordHashMethod(), newPasswordText.getText() );
        if ( !"".equals( newPasswordText.getText() ) || newPassword.getHashMethod() == null ) //$NON-NLS-1$
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
            newPasswordPreviewText.setEchoChar( '\0' );
            newPasswordPreviewValueHexText.setEchoChar( '\0' );
            newPasswordPreviewSaltHexText.setEchoChar( '\0' );
        }
        else
        {
            newPasswordText.setEchoChar( '\u2022' );
            newPasswordPreviewText.setEchoChar( newPasswordPreviewText.getText()
                .equals( Utils.getNonNullString( null ) ) ? '\0' : '\u2022' );
            newPasswordPreviewValueHexText.setEchoChar( newPasswordPreviewValueHexText.getText().equals(
                Utils.getNonNullString( null ) ) ? '\0' : '\u2022' );
            newPasswordPreviewSaltHexText.setEchoChar( newPasswordPreviewSaltHexText.getText().equals(
                Utils.getNonNullString( null ) ) ? '\0' : '\u2022' );
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        if ( hasCurrentPassword() )
        {
            showCurrentPasswordDetailsButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent arg0 )
                {
                    updateCurrentPasswordGroup();
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
     * Indicates if the dialog has a current password.
     *
     * @return <code>true</code> if the dialog has a current password,
     *         <code>false</code> if not.
     */
    private boolean hasCurrentPassword()
    {
        return ( ( currentPassword != null ) && ( currentPassword.toBytes().length > 0 ) );
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
     * @return the password, either encrypted by the selected
     *         algorithm or as plain text.
     */
    public byte[] getNewPassword()
    {
        return returnPassword;
    }
}
