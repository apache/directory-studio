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
package org.apache.directory.studio.connection.ui.dialogs;


import java.security.cert.X509Certificate;

import org.apache.directory.studio.connection.ui.widgets.CertificateInfoComposite;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog to show dialog information.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CertificateInfoDialog extends Dialog
{

    /** The title. */
    private String title;

    /** The certificate chain. */
    private X509Certificate[] certificateChain;


    /**
     * Creates a new instance of CertificateInfoDialog.
     * 
     * @param parentShell the parent shell
     * @param certificateChain the certificate chain
     */
    public CertificateInfoDialog( Shell parentShell, X509Certificate[] certificateChain )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.title = Messages.getString( "CertificateInfoDialog.CertificateViewer" ); //$NON-NLS-1$
        this.certificateChain = certificateChain;
    }


    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( title );
    }


    @Override
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.CANCEL_ID, "close"/*IDialogConstants.CLOSE_LABEL*/, false );
    }


    @Override
    protected Control createDialogArea( final Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH * 3 / 2 );
        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        CertificateInfoComposite certificateInfoComposite = new CertificateInfoComposite( composite, SWT.NONE );
        certificateInfoComposite.setInput( certificateChain );

        applyDialogFont( composite );
        return composite;
    }

}
