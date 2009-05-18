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

import org.apache.directory.studio.connection.core.ICertificateHandler;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog to ask for certificate trust.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CertificateTrustDialog extends Dialog
{

    /** The title. */
    private String title;

    /** The trust level. */
    private ICertificateHandler.TrustLevel trustLevel;

    /** The certificate chain. */
    private X509Certificate[] certificateChain;

    /** The "Don't trust" button. */
    private Button trustNotButton;

    /** The "Trust in current session" button. */
    private Button trustSessionButton;

    /** The "Trust permanent" button. */
    private Button trustPermanentButton;


    /**
     * Creates a new instance of CertificateTrustDialog.
     * 
     * @param parentShell the parent shell
     * @param certificateChain the certificate chain
     */
    public CertificateTrustDialog( Shell parentShell, X509Certificate[] certificateChain )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.title = Messages.getString( "CertificateTrustDialog.CertificateTrust" ); //$NON-NLS-1$
        this.certificateChain = certificateChain;
        this.trustLevel = null;
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
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
    }


    /**
     * Gets the trust level.
     * 
     * @return the trust level
     */
    public ICertificateHandler.TrustLevel getTrustLevel()
    {
        return trustLevel;
    }


    @Override
    protected Control createDialogArea( final Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridLayout gl = new GridLayout();
        composite.setLayout( gl );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        composite.setLayoutData( gd );

        BaseWidgetUtils.createWrappedLabel( composite, Messages.getString( "CertificateTrustDialog.Description" ), 1 ); //$NON-NLS-1$
        BaseWidgetUtils.createWrappedLabel( composite, Messages.getString( "CertificateTrustDialog.TheDnIs" ), 1 ); //$NON-NLS-1$

        Label issuerDNLabel = BaseWidgetUtils.createWrappedLabel( composite, "", 1 ); //$NON-NLS-1$
        if ( ( certificateChain != null ) && ( certificateChain.length > 0 ) )
        {
            issuerDNLabel.setText( certificateChain[0].getIssuerX500Principal().getName() );
        }
        else
        {
            issuerDNLabel.setText( "Unknown" ); //$NON-NLS-1$
        }

        trustNotButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "CertificateTrustDialog.DoNotTrust" ), 1 ); //$NON-NLS-1$
        trustNotButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( final SelectionEvent e )
            {
                CertificateTrustDialog.this.trustLevel = ICertificateHandler.TrustLevel.Not;
            }
        } );

        trustSessionButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "CertificateTrustDialog.TrustForThisSession" ), 1 ); //$NON-NLS-1$

        trustSessionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( final SelectionEvent e )
            {
                CertificateTrustDialog.this.trustLevel = ICertificateHandler.TrustLevel.Session;
            }
        } );

        trustPermanentButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "CertificateTrustDialog.AlwaysTrust" ), 1 ); //$NON-NLS-1$
        trustPermanentButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( final SelectionEvent e )
            {
                CertificateTrustDialog.this.trustLevel = ICertificateHandler.TrustLevel.Permanent;
            }
        } );

        // default settings
        trustNotButton.setSelection( true );
        trustLevel = ICertificateHandler.TrustLevel.Not;

        return composite;
    }

}
