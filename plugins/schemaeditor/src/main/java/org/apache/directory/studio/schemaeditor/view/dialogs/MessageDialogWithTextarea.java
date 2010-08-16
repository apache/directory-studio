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
package org.apache.directory.studio.schemaeditor.view.dialogs;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Message dialog with an text area.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MessageDialogWithTextarea extends MessageDialog
{

    private String detailMessage;
    private Text textArea;


    /**
     * Instantiates a dialog.
     * 
     * @param parentShell the parent shell
     * @param title the title
     * @param message the message
     * @param detailMessage the detail message
     */
    public MessageDialogWithTextarea( Shell parentShell, String title, String message, String detailMessage )
    {
        super( parentShell, title, null, message, INFORMATION, new String[]
            { IDialogConstants.OK_LABEL }, OK );
        setShellStyle( SWT.RESIZE );
        this.detailMessage = detailMessage;
    }


    @Override
    protected Control createCustomArea( Composite parent )
    {
        textArea = new Text( parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
        textArea.setFont( JFaceResources.getFont( JFaceResources.TEXT_FONT ) );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( ( int ) ( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 ) );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 4 );
        textArea.setLayoutData( gd );
        //textArea.setBackground( parent.getBackground() );
        textArea.setText( detailMessage );

        return textArea;
    }

}
