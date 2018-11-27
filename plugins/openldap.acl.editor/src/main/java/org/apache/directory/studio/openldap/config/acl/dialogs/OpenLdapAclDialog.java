/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.dialogs;


import java.text.ParseException;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPlugin;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPluginConstants;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.widgets.OpenLdapAclTabFolderComposite;


/**
 * The OpenLDAP ACL Dialog is used to edit ACL values on an OpenLDAP server connection.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclDialog extends Dialog
{
    /** The ID for the 'Format' button */
    private static final int FORMAT_BUTTON = 999999;

    /** The ID for the 'Check Syntax' button */
    private static final int CHECK_SYNTAX_BUTTON = 999998;

    /** The context containing the initial value, passed by the constructor */
    private OpenLdapAclValueWithContext context;

    /** The ACL value */
    private String aclValue;

    /** The precendence checkbox */
    private Button precedenceCheckbox;

    /** The precedence spinner */
    private Spinner precedenceSpinner;

    /** The precedence flag */
    private int precedence;

    /** The tab folder composite */
    private OpenLdapAclTabFolderComposite tabFolderComposite;


    /**
     * A listener on the Precedence Checkbox. It will enable or disable the precedence spinner.
     */
    private SelectionAdapter precedenceCheckBoxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            precedenceSpinner.setEnabled( precedenceCheckbox.getSelection() );
        }
    };
    
    
    /**
     * The precedence spinner modify listener
     */
    private ModifyListener precedenceSpinnerModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            precedence = precedenceSpinner.getSelection();
        }
    };

    
    /**
     * Creates a new instance of OpenLdapAclDialog.
     * 
     * @param parentShell the parent shell
     * @param context the ACL context
     */
    public OpenLdapAclDialog( Shell parentShell, OpenLdapAclValueWithContext context )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.context = context;
        context.setAclDialog( this );
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "OpenLDAP ACL Editor" );
        shell.setImage( OpenLdapAclEditorPlugin.getDefault().getImage( OpenLdapAclEditorPluginConstants.IMG_EDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, FORMAT_BUTTON, "Format", false );
        createButton( parent, CHECK_SYNTAX_BUTTON, "Check Syntax", false );
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation checks if the Format button was pressed.
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == FORMAT_BUTTON )
        {
            tabFolderComposite.format();
        }
        if ( buttonId == CHECK_SYNTAX_BUTTON )
        {
            try
            {
                tabFolderComposite.getInput();
                MessageDialog.openInformation( getShell(), "Correct Syntax", "Correct Syntax" );
            }
            catch ( ParseException pe )
            {
                IStatus status = new Status( IStatus.ERROR, OpenLdapAclEditorPluginConstants.PLUGIN_ID, 1,
                    "Invalid syntax.", pe );
                ErrorDialog.openError( getShell(), "Syntax Error", null, status );
            }
        }

        // call super implementation
        super.buttonPressed( buttonId );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        try
        {
            aclValue = tabFolderComposite.getInput();
            tabFolderComposite.saveWidgetSettings();

            super.okPressed();
        }
        catch ( ParseException pe )
        {
            IStatus status = new Status( IStatus.ERROR, OpenLdapAclEditorPluginConstants.PLUGIN_ID, 1,
                "Invalid syntax.", pe );
            ErrorDialog.openError( getShell(), "Syntax Error", null, status );
        }
    }
    
    
    /**
     * Return the OK button instance
     */
    public Button getOKButton()
    {
        return getButton( IDialogConstants.OK_ID );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 4 / 3;
        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 4 / 3;
        composite.setLayoutData( gd );

        // Creating UI : first the precedence
        createPrecedenceGroup( composite );
        
        // the tab for the source/visual editor 
        createTabFolderComposite( composite );

        // Setting default focus on the composite
        composite.setFocus();

        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the precedence group. We handle the checkbox and the value for precedence.
     * 
     * <pre>
     * +-----------------------------------------+
     * | [ ] Precedence : [---] 8                |
     * +-----------------------------------------+
     * </pre>
     * @param parent the parent composite
     */
    private void createPrecedenceGroup( Composite parent )
    {
        // Precendence group
        Group precendenceGroup = BaseWidgetUtils.createGroup( parent, "", 1 );
        GridLayout precedenceGroupLayout = new GridLayout( 2, false );
        precedenceGroupLayout.horizontalSpacing = precedenceGroupLayout.verticalSpacing = 10;
        precendenceGroup.setLayout( precedenceGroupLayout );
        GridData gd2 = new GridData( SWT.FILL, SWT.NONE, true, false );
        precendenceGroup.setLayoutData( gd2 );

        // Precendence values
        precedence = context.getPrecedence();
        
        // Precedence checkbox
        precedenceCheckbox = BaseWidgetUtils.createCheckbox( precendenceGroup, "Precedence:", 1 );
        precedenceCheckbox.setSelection( context.hasPrecedence() );
        precedenceCheckbox.addSelectionListener( precedenceCheckBoxSelectionListener );

        // Precedence spinner
        precedenceSpinner = new Spinner( precendenceGroup, SWT.BORDER );
        precedenceSpinner.setEnabled( context.hasPrecedence() );
        precedenceSpinner.setSelection( precedence );
        precedenceSpinner.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, true, false ) );
        precedenceSpinner.addModifyListener( precedenceSpinnerModifyListener );
    }


    /**
     * Creates the tab folder composite.
     *
     * @param parent the parent composite
     */
    private void createTabFolderComposite( Composite parent )
    {
        // Creating the tab folder composite
        tabFolderComposite = new OpenLdapAclTabFolderComposite( parent, context, SWT.NONE );
        tabFolderComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
    }


    /**
     * Gets the ACL value.
     * 
     * @return the ACL value
     */
    public String getAclValue()
    {
        return aclValue;
    }


    /**
     * @return the precedence value
     */
    public int getPrecedence()
    {
        return precedence;
    }


    /**
     * @return whether precedence is used or not
     */
    public boolean hasPrecedence()
    {
        return precedence != -1;
    }
}
