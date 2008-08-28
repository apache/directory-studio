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

package org.apache.directory.studio.ldapbrowser.ui.dialogs;


import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldifparser.LdifUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class EncoderDecoderDialog extends Dialog
{

    public static final String DIALOG_TITLE = "LDAP Encoder/Decoder";

    private Text iso88591Text;

    private Text iso88591HexText;

    private Text utf8Text;

    private Text utf8HexText;

    private Text base64Text;

    private Text errorText;

    private boolean inModify = false;


    public EncoderDecoderDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        //shell.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_IMAGEEDITOR ) );
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite2 = ( Composite ) super.createDialogArea( parent );
        GridData gd1 = new GridData( GridData.FILL_BOTH );
        gd1.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd1.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite2.setLayoutData( gd1 );

        Composite composite = BaseWidgetUtils.createColumnContainer( composite2, 2, 1 );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        Label iso8859Label = new Label( composite, SWT.NONE );
        iso8859Label.setText( "ISO-8859-1:" );
        iso88591Text = new Text( composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        GridData gd = new GridData( GridData.FILL_BOTH );
        iso88591Text.setLayoutData( gd );

        Label iso8859HexLabel = new Label( composite, SWT.NONE );
        iso8859HexLabel.setText( "ISO-8859-1 Hex:" );
        iso88591HexText = new Text( composite, SWT.BORDER | SWT.READ_ONLY );
        iso88591HexText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        Label utf8Label = new Label( composite, SWT.NONE );
        utf8Label.setText( "UTF-8:" );
        utf8Text = new Text( composite, SWT.BORDER );
        utf8Text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        Label utf8HexLabel = new Label( composite, SWT.NONE );
        utf8HexLabel.setText( "UTF-8 Hex:" );
        utf8HexText = new Text( composite, SWT.BORDER | SWT.READ_ONLY );
        utf8HexText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        Label base64Label = new Label( composite, SWT.NONE );
        base64Label.setText( "BASE-64:" );
        base64Text = new Text( composite, SWT.BORDER );
        base64Text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        errorText = new Text( composite, SWT.BORDER | SWT.READ_ONLY );
        gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 2;
        errorText.setLayoutData( gd );

        iso88591Text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                if ( !inModify )
                {
                    inModify = true;
                    try
                    {
                        String iso = iso88591Text.getText();
                        byte[] isoHex = iso.getBytes( "ISO-8859-1" );
                        byte[] utf8 = LdifUtils.utf8encode( iso );
                        String utf8String = new String( utf8, "ISO-8859-1" );
                        String base64 = LdifUtils.base64encode( utf8 );

                        iso88591HexText.setText( LdifUtils.hexEncode( isoHex ) );
                        utf8Text.setText( utf8String );
                        utf8HexText.setText( LdifUtils.hexEncode( utf8 ) );
                        base64Text.setText( base64 );
                        errorText.setText( "" );
                    }
                    catch ( Exception ex )
                    {
                        errorText.setText( ex.getMessage() );
                        ex.printStackTrace();
                    }
                    finally
                    {
                        inModify = false;
                    }
                }
            }
        } );

        utf8Text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                if ( !inModify )
                {
                    inModify = true;
                    try
                    {
                        String utf8String = utf8Text.getText();
                        byte[] utf8 = utf8String.getBytes( "ISO-8859-1" );
                        String iso = LdifUtils.utf8decode( utf8 );
                        byte[] isoHex = iso.getBytes( "ISO-8859-1" );
                        String base64 = LdifUtils.base64encode( utf8 );

                        iso88591Text.setText( iso );
                        iso88591HexText.setText( LdifUtils.hexEncode( isoHex ) );
                        utf8HexText.setText( LdifUtils.hexEncode( utf8 ) );
                        base64Text.setText( base64 );
                        errorText.setText( "" );
                    }
                    catch ( Exception ex )
                    {
                        errorText.setText( ex.getMessage() );
                        ex.printStackTrace();
                    }
                    finally
                    {
                        inModify = false;
                    }
                }
            }
        } );

        base64Text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                if ( !inModify )
                {
                    inModify = true;
                    try
                    {
                        String base64 = base64Text.getText();
                        byte[] utf8 = LdifUtils.base64decodeToByteArray( base64 );
                        String utf8String = new String( utf8, "ISO-8859-1" );
                        String iso = LdifUtils.utf8decode( utf8 );
                        byte[] isoHex = iso.getBytes( "ISO-8859-1" );

                        iso88591Text.setText( iso );
                        iso88591HexText.setText( LdifUtils.hexEncode( isoHex ) );
                        utf8Text.setText( utf8String );
                        utf8HexText.setText( LdifUtils.hexEncode( utf8 ) );
                        errorText.setText( "" );
                    }
                    catch ( Exception ex )
                    {
                        errorText.setText( ex.getMessage() );
                        ex.printStackTrace();
                    }
                    finally
                    {
                        inModify = false;
                    }
                }
            }
        } );

        return composite;
    }

}
