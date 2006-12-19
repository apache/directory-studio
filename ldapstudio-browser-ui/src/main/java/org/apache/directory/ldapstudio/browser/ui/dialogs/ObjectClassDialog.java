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

package org.apache.directory.ldapstudio.browser.ui.dialogs;


import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class ObjectClassDialog extends Dialog
{

    public static final String DIALOG_TITLE = "Object Class Editor";

    public static final double MAX_WIDTH = 250.0;

    public static final double MAX_HEIGHT = 250.0;

    private Schema schema;

    private String initialValue;

    private Combo objectClassCombo;

    private String returnValue;


    public ObjectClassDialog( Shell parentShell, Schema schema, String initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.initialValue = initialValue;
        this.schema = schema;
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
        shell.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_TEXTEDITOR ) );
    }


    protected void createButtonsForButtonBar( Composite parent )
    {
        super.createButtonsForButtonBar( parent );
    }


    protected void okPressed()
    {
        this.returnValue = this.objectClassCombo.getText();
        super.okPressed();
    }


    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        // combo widget
        String[] allOcNames = schema.getObjectClassDescriptionNames();
        Arrays.sort( allOcNames );
        objectClassCombo = BaseWidgetUtils.createReadonlyCombo( composite, allOcNames, 0, 1 );
        objectClassCombo.setText( initialValue );
        objectClassCombo.setVisibleItemCount( 20 );

        applyDialogFont( composite );
        return composite;
    }


    public String getObjectClass()
    {
        return this.returnValue;
    }
}
