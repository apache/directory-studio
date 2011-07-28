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
package org.apache.directory.studio.aciitemeditor.dialogs;


import java.text.ParseException;

import org.apache.directory.studio.aciitemeditor.ACIITemConstants;
import org.apache.directory.studio.aciitemeditor.ACIItemValueWithContext;
import org.apache.directory.studio.aciitemeditor.Activator;
import org.apache.directory.studio.aciitemeditor.widgets.ACIItemTabFolderComposite;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * The main dialog of the ACI item editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ACIItemDialog extends Dialog
{
    private static final int FORMAT_BUTTON = 987654321;
    private static final int CHECK_SYNTAX_BUTTON = 876543210;

    /** The context containing the initial value, passed by the constructor */
    private ACIItemValueWithContext context;

    /** The resulting value returned by getACIItemValue() */
    private String returnValue;

    /** The child composite with the tabs */
    private ACIItemTabFolderComposite tabFolderComposite;


    /**
     * Creates a new instance of ACIItemDialog.
     * 
     * @param parentShell the shell
     * @param context the context
     */
    public ACIItemDialog( Shell parentShell, ACIItemValueWithContext context )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );

        assert context != null;
        assert context.getACIItemValue() != null;
        assert context.getConnection() != null;

        this.context = context;

        this.returnValue = null;
    }


    /**
     * Sets the dialog image and text.
     * 
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString( "ACIItemDialog.dialog.text" ) ); //$NON-NLS-1$
        shell.setImage( Activator.getDefault().getImage( Messages.getString( "ACIItemDialog.dialog.icon" ) ) ); //$NON-NLS-1$
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, FORMAT_BUTTON, Messages.getString( "ACIItemDialog.button.format" ), false ); //$NON-NLS-1$
        createButton( parent, CHECK_SYNTAX_BUTTON, Messages.getString( "ACIItemDialog.button.checkSyntax" ), false ); //$NON-NLS-1$
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
                MessageDialog
                    .openInformation(
                        getShell(),
                        Messages.getString( "ACIItemDialog.syntaxOk.title" ), Messages.getString( "ACIItemDialog.syntaxOk.text" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch ( ParseException pe )
            {
                IStatus status = new Status( IStatus.ERROR, ACIITemConstants.PLUGIN_ID, 1, Messages
                    .getString( "ACIItemDialog.error.invalidSyntax" ), pe ); //$NON-NLS-1$
                ErrorDialog.openError( getShell(), Messages.getString( "ACIItemDialog.error.title" ), null, status ); //$NON-NLS-1$
            }
        }

        // call super implementation
        super.buttonPressed( buttonId );
    }


    /**
     * Reimplementation: Checks for valid syntax first and sets the return value.
     */
    protected void okPressed()
    {
        try
        {
            this.returnValue = tabFolderComposite.getInput();
            super.okPressed();
        }
        catch ( ParseException pe )
        {
            IStatus status = new Status( IStatus.ERROR, ACIITemConstants.PLUGIN_ID, 1, Messages
                .getString( "ACIItemDialog.error.invalidSyntax" ), pe ); //$NON-NLS-1$
            ErrorDialog.openError( getShell(), Messages.getString( "ACIItemDialog.error.title" ), null, status ); //$NON-NLS-1$
        }
    }


    /**
     * Creates the tabFolderComposite.
     * 
     * @param parent the parent
     * 
     * @return the control
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 4 / 3;
        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 4 / 3;
        composite.setLayoutData( gd );

        tabFolderComposite = new ACIItemTabFolderComposite( composite, SWT.NONE );

        // set initial value
        if ( context != null )
        {
            tabFolderComposite.setContext( context );
            tabFolderComposite.setInput( context.getACIItemValue() );
        }

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Returns the string representation of the ACI item. Returns
     * null if Cancel button was pressed.
     *
     * @return the string representation of the ACI item or null
     */
    public String getACIItemValue()
    {
        return returnValue;
    }

}
