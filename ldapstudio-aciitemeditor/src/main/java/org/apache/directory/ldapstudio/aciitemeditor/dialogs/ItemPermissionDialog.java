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

package org.apache.directory.ldapstudio.aciitemeditor.dialogs;


import java.util.Collection;

import org.apache.directory.ldapstudio.aciitemeditor.ACIItemValueWithContext;
import org.apache.directory.ldapstudio.aciitemeditor.Activator;
import org.apache.directory.ldapstudio.aciitemeditor.widgets.ACIItemGrantsAndDenialsComposite;
import org.apache.directory.ldapstudio.aciitemeditor.widgets.ACIItemUserClassesComposite;
import org.apache.directory.shared.ldap.aci.GrantAndDenial;
import org.apache.directory.shared.ldap.aci.ItemPermission;
import org.apache.directory.shared.ldap.aci.UserClass;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;


/**
 * A dialog to compose item permissions.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ItemPermissionDialog extends Dialog
{
    
    /** The context */
    private ACIItemValueWithContext context;
    
    /** The initial value, passed by the constructor */
    private ItemPermission initialItemPermission;

    /** The resulting value returned by getItemPermission() */
    private ItemPermission returnItemPermission;

    /** The precedence checkbox to enable/disable spinner */
    private Button precedenceCheckbox = null;

    /** The precedence spinner */
    private Spinner precedenceSpinner = null;

    /** The widget with user classes table */
    private ACIItemUserClassesComposite userClassesComposite;

    /** The widget with grants and denials table */
    private ACIItemGrantsAndDenialsComposite grantsAndDenialsComposite;


    /**
     * Creates a new instance of ItemPermissionDialog.
     *
     * @param parentShell the shell
     * @param initialValue the initial item permission to edit, 
     *        or null to create a new item permission
     */
    public ItemPermissionDialog( Shell parentShell, ItemPermission initialItemPermission, ACIItemValueWithContext context )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.context = context;
        this.initialItemPermission = initialItemPermission;
        this.returnItemPermission = null;
    }


    /**
     * Sets the dialog image and text.
     * 
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString("ItemPermissionDialog.dialog.text") ); //$NON-NLS-1$
        shell.setImage( Activator.getDefault().getImage( Messages.getString("ItemPermissionDialog.dialog.icon") ) ); //$NON-NLS-1$
    }


    /**
     * Reimplementation: Checks for valid syntax and sets the return value.
     */
    protected void okPressed()
    {
        try
        {
            int precedence = precedenceCheckbox.getSelection() ? precedenceSpinner.getSelection() : -1;
            Collection<UserClass> userClasses = userClassesComposite.getUserClasses();
            Collection<GrantAndDenial> grantsAndDenials = grantsAndDenialsComposite.getGrantsAndDenials();
            returnItemPermission = new ItemPermission( precedence, grantsAndDenials, userClasses );
            super.okPressed();
        }
        catch ( Exception e )
        {
            MessageDialog.openError( getShell(), Messages.getString("ItemPermissionDialog.error.invalidItemPermission"), e.getMessage() ); //$NON-NLS-1$
        }
    }


    /**
     * Creates all the dialog content.
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 4 / 3;
        composite.setLayoutData( gd );

        // precedence
        Composite spinnerComposite = new Composite( composite, SWT.NONE );
        spinnerComposite.setLayout( new GridLayout( 2, false ) );
        spinnerComposite.setLayoutData( new GridData() );
        precedenceCheckbox = new Button( spinnerComposite, SWT.CHECK );
        precedenceCheckbox.setText( Messages.getString("ItemPermissionDialog.precedence.label") ); //$NON-NLS-1$
        precedenceCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                precedenceSpinner.setEnabled( precedenceCheckbox.getSelection() );
            }
        } );
        precedenceSpinner = new Spinner( spinnerComposite, SWT.BORDER );
        precedenceSpinner.setMinimum( 0 );
        precedenceSpinner.setMaximum( 255 );
        precedenceSpinner.setDigits( 0 );
        precedenceSpinner.setIncrement( 1 );
        precedenceSpinner.setPageIncrement( 10 );
        precedenceSpinner.setSelection( 0 );
        precedenceSpinner.setEnabled( false );
        GridData precedenceGridData = new GridData();
        precedenceGridData.grabExcessHorizontalSpace = true;
        precedenceGridData.verticalAlignment = GridData.CENTER;
        precedenceGridData.horizontalAlignment = GridData.BEGINNING;
        precedenceGridData.widthHint = 3 * 12;
        precedenceSpinner.setLayoutData( precedenceGridData );

        // user classes
        userClassesComposite = new ACIItemUserClassesComposite( composite, SWT.NONE );
        userClassesComposite.setContext( context );

        // grants and denial
        grantsAndDenialsComposite = new ACIItemGrantsAndDenialsComposite( composite, SWT.NONE );

        // set initial values
        if ( initialItemPermission != null )
        {
            if ( initialItemPermission.getPrecedence() > -1 )
            {
                precedenceCheckbox.setSelection( true );
                precedenceSpinner.setEnabled( true );
                precedenceSpinner.setSelection( initialItemPermission.getPrecedence() );
            }
            userClassesComposite.setUserClasses( initialItemPermission.getUserClasses() );
            grantsAndDenialsComposite.setGrantsAndDenials( initialItemPermission.getGrantsAndDenials() );
        }

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Returns the item permission. Returns null if Cancel button was pressed.
     *
     * @return the composed item permission or null
     */
    public ItemPermission getItemPermission()
    {
        return returnItemPermission;
    }

}
