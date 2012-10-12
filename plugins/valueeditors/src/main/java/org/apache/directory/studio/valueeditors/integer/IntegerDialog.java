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


import java.math.BigDecimal;

import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * This class provides a dialog to enter or choose an integer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class IntegerDialog extends Dialog
{
    /** The initial value */
    private BigDecimal initialValue;

    /** The value */
    private BigDecimal value;

    /** The text */
    private Text text;


    /**
     * Creates a new instance of IntegerDialog.
     * 
     * @param parentShell the parent shell
     * @param initialValue the initial value
     */
    public IntegerDialog( Shell parentShell, BigDecimal initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.initialValue = initialValue;
        this.value = new BigDecimal( initialValue.toString() );
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
        //        returnValue = spinner.getSelection();
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        // Composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        composite.setLayout( new GridLayout( 3, false ) );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        // - Button
        Button minusButton = new Button( composite, SWT.PUSH );
        minusButton.setText( "-" );
        minusButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addToValue( -1 );
                text.selectAll();
            }
        } );

        // Text
        text = new Text( composite, SWT.BORDER );
        text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updateValueFromText();
            }
        } );
        text.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                // Prevent the user from entering anything but an integer
                if ( !e.text.matches( "(-)?([0-9])*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
        text.addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( e.keyCode == SWT.ARROW_UP )
                {
                    addToValue( 1 );
                    e.doit = false;
                    text.selectAll();
                }
                else if ( e.keyCode == SWT.ARROW_DOWN )
                {
                    addToValue( -1 );
                    e.doit = false;
                    text.selectAll();
                }
                else if ( e.keyCode == SWT.PAGE_UP )
                {
                    addToValue( 100 );
                    e.doit = false;
                    text.selectAll();
                }
                else if ( e.keyCode == SWT.PAGE_DOWN )
                {
                    addToValue( -100 );
                    e.doit = false;
                    text.selectAll();
                }
            }
        } );

        // + Button
        Button plusButton = new Button( composite, SWT.PUSH );
        plusButton.setText( "+" );
        plusButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addToValue( 1 );
                text.selectAll();
            }
        } );

        updateTextValue();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Updates the text value.
     */
    private void updateTextValue()
    {
        text.setText( value.toString() );
    }


    /**
     * Adds the given integer to the value.
     *
     * @param i the integer
     */
    private void addToValue( int i )
    {
        value = value.add( new BigDecimal( i ) );

        updateTextValue();
    }


    /**
     * Updates the value from the text's value.
     */
    private void updateValueFromText()
    {
        try
        {
            BigDecimal newValue = new BigDecimal( text.getText() );
            value = newValue;
        }
        catch ( NumberFormatException e )
        {
            // Nothing to do
        }
    }


    /**
     * Gets the integer.
     * 
     * @return the integer
     */
    public BigDecimal getInteger()
    {
        return value;
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
        return !initialValue.equals( value );
    }
}
