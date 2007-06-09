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

package org.apache.directory.ldapstudio.valueeditors.address;


import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsActivator;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class AddressDialog extends Dialog
{

    public static final String DIALOG_TITLE = "Address Editor";

    public static final double MAX_WIDTH = 250.0;

    public static final double MAX_HEIGHT = 250.0;

    private String initialValue;

    private String returnValue;

    private Text text;


    public AddressDialog( Shell parentShell, String initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.initialValue = initialValue;
        this.returnValue = null;
    }


    public boolean close()
    {
        return super.close();
    }


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_ADDRESSEDITOR ) );
    }


    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    protected void okPressed()
    {
        this.returnValue = this.text.getText();
        this.returnValue = this.returnValue.replaceAll( "\n", "\\$" );
        this.returnValue = this.returnValue.replaceAll( "\r", "\\$" );
        this.returnValue = this.returnValue.replaceAll( "\\$\\$", "\\$" );
        super.okPressed();
    }


    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        // text widget
        text = new Text( composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
        text.setText( this.initialValue.replaceAll( "\\$", BrowserCoreConstants.LINE_SEPARATOR ) );
        // GridData gd = new GridData(GridData.GRAB_HORIZONTAL |
        // GridData.HORIZONTAL_ALIGN_FILL);
        gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        text.setLayoutData( gd );

        applyDialogFont( composite );
        return composite;
    }


    public String getText()
    {
        return this.returnValue;
    }

}
