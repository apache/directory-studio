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

package org.apache.directory.studio.ldapbrowser.common.dialogs;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Dialog with an text area.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TextDialog extends Dialog
{

    /** The dialog title. */
    private static final String DIALOG_TITLE = Messages.getString( "TextDialog.TextEditor" ); //$NON-NLS-1$

    /** The initial value. */
    private String initialValue;

    /** The return value. */
    private String returnValue;

    /** The text area. */
    private Text text;


    /**
     * Creates a new instance of TextDialog.
     * 
     * @param parentShell the parent shell
     * @param initialValue the initial value
     */
    public TextDialog( Shell parentShell, String initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.initialValue = initialValue;
        this.returnValue = null;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_TEXTEDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        returnValue = text.getText();
        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        // text widget
        text = new Text( composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
        text.setText( this.initialValue );
        // GridData gd = new GridData(GridData.GRAB_HORIZONTAL |
        // GridData.HORIZONTAL_ALIGN_FILL);
        gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        text.setLayoutData( gd );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Gets the text.
     * 
     * @return the text
     */
    public String getText()
    {
        return returnValue;
    }

}
