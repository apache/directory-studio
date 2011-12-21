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

package org.apache.directory.studio.valueeditors.certificate;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.connection.ui.widgets.CertificateInfoComposite;
import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog to display a X.509 certificate. It could be 
 * used to load and save the certificate from and to disk.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CertificateDialog extends Dialog
{

    /** The default title. */
    private static final String DIALOG_TITLE = Messages.getString( "CertificateDialog.CertificateDialog" ); //$NON-NLS-1$

    /** The button ID for the load button. */
    private static final int LOAD_BUTTON_ID = 9998;

    /** The button ID for the save button. */
    private static final int SAVE_BUTTON_ID = 9999;

    /** The current certificate binary data. */
    private byte[] currentData;

    /** The current certificate. */
    private X509Certificate currentCertificate;

    /** The return data, only set if OK button is pressed, null otherwise. */
    private byte[] returnData;

    /** The certificate info composite. */
    private CertificateInfoComposite certificateInfoComposite;


    /**
     * Creates a new instance of CertificateDialog.
     * 
     * @param parentShell the parent shell
     * @param initialData the initial data
     */
    public CertificateDialog( Shell parentShell, byte[] initialData )
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
        else if ( buttonId == SAVE_BUTTON_ID )
        {
            FileDialog fileDialog = new FileDialog( getShell(), SWT.SAVE );
            fileDialog.setText( Messages.getString( "CertificateDialog.SaveCertificate" ) ); //$NON-NLS-1$
            // fileDialog.setFilterExtensions(new String[]{"*.pem"});
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
                        new Status( IStatus.ERROR, ValueEditorsConstants.PLUGIN_ID, IStatus.ERROR, Messages
                            .getString( "CertificateDialog.CantWriteToFile" ), e ) ); //$NON-NLS-1$
                }
            }
        }
        else if ( buttonId == LOAD_BUTTON_ID )
        {
            FileDialog fileDialog = new FileDialog( getShell(), SWT.OPEN );
            fileDialog.setText( Messages.getString( "CertificateDialog.LoadCertificate" ) ); //$NON-NLS-1$
            String returnedFileName = fileDialog.open();
            if ( returnedFileName != null )
            {
                try
                {
                    File file = new File( returnedFileName );
                    currentData = FileUtils.readFileToByteArray( file );
                    updateInput();
                }
                catch ( IOException e )
                {
                    ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                        new Status( IStatus.ERROR, ValueEditorsConstants.PLUGIN_ID, IStatus.ERROR, Messages
                            .getString( "CertificateDialog.CantReadFile" ), e ) ); //$NON-NLS-1$
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
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_CERTIFICATEEDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, LOAD_BUTTON_ID, Messages.getString( "CertificateDialog.LoadCertificateButton" ), false ); //$NON-NLS-1$
        createButton( parent, SAVE_BUTTON_ID, Messages.getString( "CertificateDialog.SaveCertificateButton" ), false ); //$NON-NLS-1$
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

        Composite certificateInfoContainer = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH * 3 / 2 );
        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        certificateInfoContainer.setLayoutData( gd );

        certificateInfoComposite = new CertificateInfoComposite( certificateInfoContainer, SWT.NONE );
        if ( currentData != null && currentData.length > 0 )
        {
            updateInput();
        }

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Parses the certificate binary data and updates the input.
     */
    private void updateInput()
    {
        try
        {
            // parse the certificate
            currentCertificate = generateCertificate( currentData );

            // update the byte[], this must be done for the case that 
            // the certificate loaded from file is in PEM format
            currentData = currentCertificate.getEncoded();

            // set the input and update button
            certificateInfoComposite.setInput( new X509Certificate[]
                { currentCertificate } );
            if ( getButton( IDialogConstants.OK_ID ) != null )
            {
                getButton( IDialogConstants.OK_ID ).setEnabled( true );
            }
        }
        catch ( Exception e )
        {
            ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                new Status( IStatus.ERROR, ValueEditorsConstants.PLUGIN_ID, IStatus.ERROR, Messages
                    .getString( "CertificateDialog.CantParseCertificate" ), //$NON-NLS-1$
                    e ) );
            if ( getButton( IDialogConstants.OK_ID ) != null )
            {
                getButton( IDialogConstants.OK_ID ).setEnabled( false );
            }
        }
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


    public static String getCertificateInfo( byte[] data )
    {
        try
        {
            X509Certificate certificate = generateCertificate( data );
            if ( certificate != null )
            {
                String name = certificate.getSubjectX500Principal().getName();
                int version = certificate.getVersion();
                String type = certificate.getType();
                return type + "v" + version + ": " + name; //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                return NLS.bind( Messages.getString( "CertificateDialog.InvalidCertificate" ), data.length ); //$NON-NLS-1$
            }
        }
        catch ( Exception e )
        {
            return NLS.bind( Messages.getString( "CertificateDialog.InvalidCertificate" ), data.length ); //$NON-NLS-1$
        }
    }


    private static X509Certificate generateCertificate( byte[] data ) throws CertificateException
    {
        CertificateFactory cf = CertificateFactory.getInstance( "X.509" ); //$NON-NLS-1$
        Certificate certificate = cf.generateCertificate( new ByteArrayInputStream( data ) );
        if ( certificate instanceof X509Certificate )
        {
            return ( X509Certificate ) certificate;
        }

        return null;
    }

}
