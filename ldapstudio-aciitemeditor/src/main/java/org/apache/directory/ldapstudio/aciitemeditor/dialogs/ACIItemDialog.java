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


import java.text.ParseException;

import org.apache.directory.ldapstudio.aciitemeditor.ACIItemValueWithContext;
import org.apache.directory.ldapstudio.aciitemeditor.Activator;
import org.apache.directory.ldapstudio.aciitemeditor.widgets.ACIItemTabFolderComposite;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * The main dialog of the ACI item editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIItemDialog extends Dialog
{

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
     * @param initialValue the initial ACI item to edit, or null to 
     *        create a new ACI item
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
     * {@inheritDoc}
     * 
     * This implementation additionally adds the Format button.
     */
    protected Control createButtonBar( Composite parent )
    {
        Composite composite = ( Composite ) super.createButtonBar( parent );
        super.createButton( composite, 987654321, "Format", false );
        return composite;
    }
    
    /**
     * {@inheritDoc}
     * 
     * This implementation checks if the Format button was pressed.
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == 987654321 )
        {
            tabFolderComposite.format();
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
            IStatus status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, 1, Messages
                .getString( "ACIItemDialog.error.invalidSyntax" ), pe ); //$NON-NLS-1$
            ErrorDialog.openError( getShell(), Messages.getString( "ACIItemDialog.error.title" ), null, status ); //$NON-NLS-1$
        }
    }


    /**
     * Creates the tabFolderComposite.
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
