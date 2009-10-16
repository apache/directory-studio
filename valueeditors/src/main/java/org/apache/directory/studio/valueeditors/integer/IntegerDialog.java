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

package org.apache.directory.studio.valueeditors.integer;


import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;


/**
 * This class provides a dialog to enter or choose an integer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class IntegerDialog extends Dialog
{

    /** The initial value. */
    private int initialValue;

    /** The return value. */
    private int returnValue;

    /** The spinner to select an integer */
    private Spinner spinner = null;


    /**
     * Creates a new instance of IntegerDialog.
     * 
     * @param parentShell the parent shell
     * @param initialValue the initial value
     */
    public IntegerDialog( Shell parentShell, int initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.initialValue = initialValue;
        this.returnValue = -1;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString( "IntegerDialog.IntegerEditor" ) ); //$NON-NLS-1$
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_INTEGEREDITOR ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        returnValue = spinner.getSelection();
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        composite.setLayout( new GridLayout() );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        spinner = new Spinner( composite, SWT.BORDER );
        spinner.setMinimum( Integer.MIN_VALUE );
        spinner.setMaximum( Integer.MAX_VALUE );
        spinner.setDigits( 0 );
        spinner.setIncrement( 1 );
        spinner.setPageIncrement( 100 );
        spinner.setSelection( initialValue );
        spinner.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Gets the integer.
     * 
     * @return the integer
     */
    public int getInteger()
    {
        return returnValue;
    }


    /**
     * Indicates if the dialog is dirty.
     *
     * @return
     *      <code>true</code> if the dialog is dirty,
     *      <code>false</code> if not.
     */
    public boolean isDirty()
    {
        return initialValue != returnValue;
    }
}
