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

package org.apache.directory.studio.connection.ui.wizards;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.connection.ui.wizards.ExportCertificateWizardPage.CertificateExportFormat;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;


/**
 * The ExportCertificateWizard is used to export a certificate.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportCertificateWizard extends Wizard
{
    /** The certificate */
    private X509Certificate certificate;

    /** The wizard page */
    private ExportCertificateWizardPage page;


    /**
     * Creates a new instance of ExportCertificateWizard.
     * 
     * @param certificate the certificate
     */
    public ExportCertificateWizard( X509Certificate certificate )
    {
        this.certificate = certificate;
        setWindowTitle( Messages.getString( "ExportCertificateWizard.ExportCertificate" ) ); //$NON-NLS-1$
        setNeedsProgressMonitor( false );
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        page = new ExportCertificateWizardPage();
        addPage( page );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        // Getting the export format
        CertificateExportFormat format = page.getCertificateExportFormat();

        try
        {
            switch ( format )
            {
                case DER:
                    return exportAsDerFormat();
                case PEM:
                    return exportAsPemFormat();
            }
        }
        catch ( Exception e )
        {
            MessageDialog.openError( getShell(),
                Messages.getString( "ExportCertificateWizard.ErrorDialogTitle" ), //$NON-NLS-1$
                NLS.bind( Messages.getString( "ExportCertificateWizard.ErrorDialogMessage" ),
                    e.getMessage() ) );
            return false;
        }

        return false;
    }


    /**
     * Exports the certificate as DER format.
     *
     * @return <code>true</code> if the export is successful
     * @throws CertificateEncodingException
     * @throws IOException
     */
    private boolean exportAsDerFormat() throws CertificateEncodingException, IOException
    {
        // Getting the export file
        File exportFile = page.getExportFile();

        // Exporting the certificate
        FileUtils.writeByteArrayToFile( exportFile, certificate.getEncoded() );

        return true;
    }


    /**
     * Exports the certificate as PEM format.
     *
     * @return <code>true</code> if the export is successful
     * @throws CertificateEncodingException
     * @throws IOException
     */
    private boolean exportAsPemFormat() throws CertificateEncodingException, IOException
    {
        // Getting the export file
        File exportFile = page.getExportFile();

        // Exporting the certificate
        FileOutputStream fos = new FileOutputStream( exportFile );
        OutputStreamWriter osw = new OutputStreamWriter( fos, Charset.forName( "UTF-8" ) ); //$NON-NLS-1$
        osw.write( "-----BEGIN CERTIFICATE-----\n" ); //$NON-NLS-1$
        osw.write( stripLineToNChars( new String( Base64.encodeBase64( certificate.getEncoded() ),
            Charset.forName( "UTF-8" ) ), 64 ) ); //$NON-NLS-1$
        osw.write( "\n-----END CERTIFICATE-----\n" ); //$NON-NLS-1$
        osw.flush();
        fos.close();

        return true;
    }


    /**
     * Strips the String every n specified characters
     * 
     * @param str the string to strip
     * @param nbChars the number of characters
     * @return the stripped String
     */
    public static String stripLineToNChars( String str, int nbChars )
    {
        int strLength = str.length();

        if ( strLength <= nbChars )
        {
            return str;
        }

        // We will first compute the new size of the result
        // It's at least nbChars chars plus one for \n
        int charsPerLine = nbChars;

        int remaining = ( strLength - nbChars ) % charsPerLine;

        int nbLines = 1 + ( ( strLength - nbChars ) / charsPerLine ) + ( remaining == 0 ? 0 : 1 );

        int nbCharsTotal = strLength + nbLines + nbLines - 2;

        char[] buffer = new char[nbCharsTotal];
        char[] orig = str.toCharArray();

        int posSrc = 0;
        int posDst = 0;

        System.arraycopy( orig, posSrc, buffer, posDst, nbChars );
        posSrc += nbChars;
        posDst += nbChars;

        for ( int i = 0; i < nbLines - 2; i++ )
        {
            buffer[posDst++] = '\n';

            System.arraycopy( orig, posSrc, buffer, posDst, charsPerLine );
            posSrc += charsPerLine;
            posDst += charsPerLine;
        }

        buffer[posDst++] = '\n';
        System.arraycopy( orig, posSrc, buffer, posDst, remaining == 0 ? charsPerLine : remaining );

        return new String( buffer );
    }
}
