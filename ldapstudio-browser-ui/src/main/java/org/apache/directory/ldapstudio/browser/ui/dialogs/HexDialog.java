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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.ExceptionHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class HexDialog extends Dialog
{

    public static final String DIALOG_TITLE = "Hex Editor";

    public static final double MAX_WIDTH = 550.0;

    public static final double MAX_HEIGHT = 550.0;

    public static final int LOAD_BUTTON_ID = 9998;

    public static final int SAVE_BUTTON_ID = 9999;

    private byte[] currentData;

    private byte[] returnData;

    private Text hexText;


    public HexDialog( Shell parentShell, byte[] initialData )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.currentData = initialData;
    }


    public boolean close()
    {
        return super.close();
    }


    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            this.returnData = this.currentData;
        }
        else if ( buttonId == SAVE_BUTTON_ID )
        {
            FileDialog fileDialog = new FileDialog( getShell(), SWT.SAVE );
            fileDialog.setText( "Save Data" );
            // fileDialog.setFilterExtensions(new String[]{"*.jpg"});
            String returnedFileName = fileDialog.open();
            if ( returnedFileName != null )
            {
                try
                {
                    File file = new File( returnedFileName );
                    FileOutputStream out = new FileOutputStream( file );
                    out.write( currentData );
                    out.flush();
                    out.close();
                }
                catch ( FileNotFoundException e )
                {
                    new ExceptionHandler().handleException( new Status( IStatus.ERROR, BrowserUIPlugin.PLUGIN_ID,
                        IStatus.ERROR, "Can't write to file", e ) );
                }
                catch ( IOException e )
                {
                    new ExceptionHandler().handleException( new Status( IStatus.ERROR, BrowserUIPlugin.PLUGIN_ID,
                        IStatus.ERROR, "Can't write to file", e ) );
                }
            }
        }
        else if ( buttonId == LOAD_BUTTON_ID )
        {
            FileDialog fileDialog = new FileDialog( getShell(), SWT.OPEN );
            fileDialog.setText( "Load Data" );
            String returnedFileName = fileDialog.open();
            if ( returnedFileName != null )
            {
                try
                {
                    File file = new File( returnedFileName );
                    FileInputStream in = new FileInputStream( file );
                    ByteArrayOutputStream out = new ByteArrayOutputStream( ( int ) file.length() );
                    byte[] buf = new byte[4096];
                    int len;
                    while ( ( len = in.read( buf ) ) > 0 )
                    {
                        out.write( buf, 0, len );
                    }
                    this.currentData = out.toByteArray();
                    hexText.setText( toFormattedHex( this.currentData ) );
                    out.close();
                    in.close();
                }
                catch ( FileNotFoundException e )
                {
                    new ExceptionHandler().handleException( new Status( IStatus.ERROR, BrowserUIPlugin.PLUGIN_ID,
                        IStatus.ERROR, "Can't read file", e ) );
                }
                catch ( IOException e )
                {
                    new ExceptionHandler().handleException( new Status( IStatus.ERROR, BrowserUIPlugin.PLUGIN_ID,
                        IStatus.ERROR, "Can't read file", e ) );
                }
            }
        }
        else
        {
            this.returnData = null;
        }

        super.buttonPressed( buttonId );
    }


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_HEXEDITOR ) );
    }


    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, LOAD_BUTTON_ID, "Load Data...", false );
        createButton( parent, SAVE_BUTTON_ID, "Save Data...", false );
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );

        hexText = new Text( composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
        hexText.setFont( JFaceResources.getFont( JFaceResources.TEXT_FONT ) );

        hexText.setText( toFormattedHex( this.currentData ) );
        // GridData gd = new GridData(GridData.GRAB_HORIZONTAL |
        // GridData.HORIZONTAL_ALIGN_FILL);
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( ( int ) ( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH * 1.6 ) );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        hexText.setLayoutData( gd );

        applyDialogFont( composite );
        return composite;
    }


    private String toFormattedHex( byte[] data )
    {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < data.length; i++ )
        {
            int b = ( int ) data[i];
            if ( b < 0 )
                b = 256 + b;
            String s = Integer.toHexString( b );
            if ( s.length() == 1 )
                s = "0" + s;
            sb.append( s ).append( " " );
            if ( ( i + 1 ) % 8 == 0 )
                sb.append( " " );

            if ( i == data.length - 1 )
            {
                while ( ( i + 1 ) % 16 != 0 )
                {
                    sb.append( "   " );
                    if ( ( i + 1 ) % 8 == 0 )
                        sb.append( " " );
                    i++;
                }
                sb.append( " " );
            }

            if ( ( i + 1 ) % 16 == 0 )
            {
                sb.append( "   " );
                for ( int x = i - 16 + 1; x <= i && x < data.length; x++ )
                {
                    if ( data[x] > 32 && data[x] < 127 )
                        sb.append( ( char ) data[x] );
                    else
                        sb.append( '.' );
                    if ( ( x + 1 ) % 8 == 0 )
                        sb.append( " " );
                }
            }

            if ( ( i + 1 ) % 16 == 0 )
            {
                sb.append( "\r\n" );
            }
        }
        return sb.toString();
    }


    public byte[] getData()
    {
        return this.returnData;
    }
}
