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


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
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


/**
 * Dialog to display binary data in hex format. It could be 
 * used to load and save binary data from and to disk.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class HexDialog extends Dialog
{

    /** The default title. */
    private static final String DIALOG_TITLE = Messages.getString( "HexDialog.HexEditor" ); //$NON-NLS-1$

    /** The button ID for the edit as text button. */
    private static final int EDIT_AS_TEXT_BUTTON_ID = 9997;

    /** The button ID for the load button. */
    private static final int LOAD_BUTTON_ID = 9998;

    /** The button ID for the save button. */
    private static final int SAVE_BUTTON_ID = 9999;

    /** The current data. */
    private byte[] currentData;

    /** The return data. */
    private byte[] returnData;

    /** The text field with the binary data. */
    private Text hexText;


    /**
     * Creates a new instance of HexDialog.
     * 
     * @param parentShell the parent shell
     * @param initialData the initial data
     */
    public HexDialog( Shell parentShell, byte[] initialData )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.currentData = initialData;
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            returnData = currentData;
        }
        else if ( buttonId == EDIT_AS_TEXT_BUTTON_ID )
        {
            // Checking if the data is "text-editable"
            if ( isEditable( currentData ) )
            {
                TextDialog dialog = new TextDialog( getShell(), new String( currentData ) );

                if ( dialog.open() == TextDialog.OK )
                {
                    String text = dialog.getText();
                    currentData = text.getBytes();
                    hexText.setText( toFormattedHex( currentData ) );
                }
            }
            else
            {
                CommonUIUtils.openErrorDialog( Messages.getString( "HexDialog.NonTextEditable" ) ); //$NON-NLS-1$
            }
        }
        else if ( buttonId == SAVE_BUTTON_ID )
        {
            FileDialog fileDialog = new FileDialog( getShell(), SWT.SAVE );
            fileDialog.setText( Messages.getString( "HexDialog.SaveData" ) ); //$NON-NLS-1$
            String returnedFileName = fileDialog.open();
            if ( returnedFileName != null )
            {
                try
                {
                    File file = new File( returnedFileName );
                    FileUtils.writeByteArrayToFile( file, currentData );
                }
                catch ( IOException e )
                {
                    ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                        new Status( IStatus.ERROR, BrowserCommonConstants.PLUGIN_ID, IStatus.ERROR, Messages
                            .getString( "HexDialog.CantWriteToFile" ), e ) ); //$NON-NLS-1$
                }
            }
        }
        else if ( buttonId == LOAD_BUTTON_ID )
        {
            FileDialog fileDialog = new FileDialog( getShell(), SWT.OPEN );
            fileDialog.setText( Messages.getString( "HexDialog.LoadData" ) ); //$NON-NLS-1$
            String returnedFileName = fileDialog.open();
            if ( returnedFileName != null )
            {
                try
                {
                    File file = new File( returnedFileName );
                    currentData = FileUtils.readFileToByteArray( file );
                    hexText.setText( toFormattedHex( currentData ) );
                }
                catch ( IOException e )
                {
                    ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                        new Status( IStatus.ERROR, BrowserCommonConstants.PLUGIN_ID, IStatus.ERROR, Messages
                            .getString( "HexDialog.CantReadFile" ), e ) ); //$NON-NLS-1$
                }
            }
        }
        else
        {
            returnData = null;
        }

        super.buttonPressed( buttonId );
    }


    /**
     * Small helper.
     */
    private boolean isEditable( byte[] b )
    {
        if ( b == null )
        {
            return false;
        }

        try
        {
            return !( new String( b, "UTF-8" ).contains( "\uFFFD" ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            return false;
        }
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_HEXEDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, EDIT_AS_TEXT_BUTTON_ID, Messages.getString( "HexDialog.EditAsText" ), false ); //$NON-NLS-1$
        createButton( parent, LOAD_BUTTON_ID, Messages.getString( "HexDialog.LoadDataButton" ), false ); //$NON-NLS-1$
        createButton( parent, SAVE_BUTTON_ID, Messages.getString( "HexDialog.SaveDataButton" ), false ); //$NON-NLS-1$
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );

        hexText = new Text( composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
        hexText.setFont( JFaceResources.getFont( JFaceResources.TEXT_FONT ) );

        hexText.setText( toFormattedHex( currentData ) );
        // GridData gd = new GridData(GridData.GRAB_HORIZONTAL |
        // GridData.HORIZONTAL_ALIGN_FILL);
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( ( int ) ( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH * 1.6 ) );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        hexText.setLayoutData( gd );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Formats the binary data in two colums. One containing the hex
     * presentation and one containting the ASCII presentation of each byte.
     * 
     * 91 a1 08 23 42 b1 c1 15  52 d1 f0 24 33 62 72 82     ...#B... R..$3br.
     * 09 0a 16 17 18 19 1a 25  26 27 28 29 2a 34 35 36     .......% &'()*456 
     * 
     * @param data the data
     * 
     * @return the formatted string
     */
    private String toFormattedHex( byte[] data )
    {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < data.length; i++ )
        {
            // get byte
            int b = ( int ) data[i];
            if ( b < 0 )
            {
                b = 256 + b;
            }

            // format to hex, optionally prepend a 0
            String s = Integer.toHexString( b );
            if ( s.length() == 1 )
            {
                s = "0" + s; //$NON-NLS-1$
            }

            // space between hex numbers
            sb.append( s ).append( " " ); //$NON-NLS-1$

            // extra space after 8 hex numbers
            if ( ( i + 1 ) % 8 == 0 )
            {
                sb.append( " " ); //$NON-NLS-1$
            }

            // if end of data is reached then fill with spaces
            if ( i == data.length - 1 )
            {
                while ( ( i + 1 ) % 16 != 0 )
                {
                    sb.append( "   " ); //$NON-NLS-1$
                    if ( ( i + 1 ) % 8 == 0 )
                    {
                        sb.append( " " ); //$NON-NLS-1$
                    }
                    i++;
                }
                sb.append( " " ); //$NON-NLS-1$
            }

            // print ASCII characters after 16 hex numbers 
            if ( ( i + 1 ) % 16 == 0 )
            {
                sb.append( "   " ); //$NON-NLS-1$
                for ( int x = i - 16 + 1; x <= i && x < data.length; x++ )
                {
                    // print ASCII charachter if printable
                    // otherwise print a dot
                    if ( data[x] > 32 && data[x] < 127 )
                    {
                        sb.append( ( char ) data[x] );
                    }
                    else
                    {
                        sb.append( '.' );
                    }

                    // space after 8 characters 
                    if ( ( x + 1 ) % 8 == 0 )
                    {
                        sb.append( " " ); //$NON-NLS-1$
                    }
                }
            }

            // start new line after 16 hex numbers
            if ( ( i + 1 ) % 16 == 0 )
            {
                sb.append( "\r\n" ); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }


    /**
     * Gets the data.
     * 
     * @return the data
     */
    public byte[] getData()
    {
        return returnData;
    }
}
