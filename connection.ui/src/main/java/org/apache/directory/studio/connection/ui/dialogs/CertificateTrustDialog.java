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
import java.util.List;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.ICertificateHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog to ask for certificate trust.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CertificateTrustDialog extends Dialog
{

    /** The title. */
    private String title;

    /** The trust level. */
    private ICertificateHandler.TrustLevel trustLevel;

    /** The host */
    private String host;

    /** The certificate chain. */
    private X509Certificate[] certificateChain;

    /** The causes of failed certificate validation. */
    private List<ICertificateHandler.FailCause> failCauses;

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
     * @param host the host
     * @param certificateChain the certificate chain
     * @param failCauses the causes of failed certificate validation
     */
    public CertificateTrustDialog( Shell parentShell, String host, X509Certificate[] certificateChain,
        List<ICertificateHandler.FailCause> failCauses )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.title = Messages.getString( "CertificateTrustDialog.CertificateTrust" ); //$NON-NLS-1$
        this.host = host;
        this.certificateChain = certificateChain;
        this.failCauses = failCauses;
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
        createButton( parent, IDialogConstants.DETAILS_ID, Messages
            .getString( "CertificateTrustDialog.ViewCertificate" ), false );
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
    }


    @Override
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.DETAILS_ID )
        {
            new CertificateInfoDialog( getShell(), certificateChain ).open();
        }
        super.buttonPressed( buttonId );
    }


    @Override
    protected Control createDialogArea( final Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        composite.setLayoutData( gd );

        BaseWidgetUtils.createWrappedLabel( composite, NLS.bind( Messages
            .getString( "CertificateTrustDialog.InvalidCertificate" ), host ), 1 ); //$NON-NLS-1$

        // failed cause
        Composite failedCauseContainer = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
        for ( ICertificateHandler.FailCause failCause : failCauses )
        {
            // BaseWidgetUtils.createRadioIndent( failedCauseContainer, 1 );
            switch ( failCause )
            {
                case SelfSignedCertificate:
                    BaseWidgetUtils.createWrappedLabel( failedCauseContainer, Messages
                        .getString( "CertificateTrustDialog.SelfSignedCertificate" ), 1 ); //$NON-NLS-1$
                    break;
                case CertificateExpired:
                    BaseWidgetUtils.createWrappedLabel( failedCauseContainer, Messages
                        .getString( "CertificateTrustDialog.CertificateExpired" ), 1 ); //$NON-NLS-1$
                    break;
                case CertificateNotYetValid:
                    BaseWidgetUtils.createWrappedLabel( failedCauseContainer, Messages
                        .getString( "CertificateTrustDialog.CertificateNotYetValid" ), 1 ); //$NON-NLS-1$
                    break;
                case NoValidCertificationPath:
                    BaseWidgetUtils.createWrappedLabel( failedCauseContainer, Messages
                        .getString( "CertificateTrustDialog.NoValidCertificationPath" ), 1 ); //$NON-NLS-1$
                    break;
                case HostnameVerificationFailed:
                    BaseWidgetUtils.createWrappedLabel( failedCauseContainer, Messages
                        .getString( "CertificateTrustDialog.HostnameVerificationFailed" ), 1 ); //$NON-NLS-1$
                    break;
            }
        }

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        BaseWidgetUtils.createWrappedLabel( composite, NLS.bind( Messages
            .getString( "CertificateTrustDialog.ChooseTrustLevel" ), host ), 1 ); //$NON-NLS-1$

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


    /**
     * Gets the trust level.
     * 
     * @return the trust level
     */
    public ICertificateHandler.TrustLevel getTrustLevel()
    {
        return trustLevel;
    }

}
