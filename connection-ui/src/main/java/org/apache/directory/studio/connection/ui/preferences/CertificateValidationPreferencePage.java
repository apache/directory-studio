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

package org.apache.directory.studio.connection.ui.preferences;


import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.widgets.CertificateListComposite;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The certificate validation preference page is used to manage trusted certificates.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CertificateValidationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    /** The verify certificates button. */
    private Button verifyCertificatesButton;

    /** The tab folder. */
    private TabFolder tabFolder;

    /** The composite containing permanent trusted certificates */
    private CertificateListComposite permanentCLComposite;

    /** The composite containing temporary trusted certificates */
    private CertificateListComposite sessionCLComposite;


    /**
     * 
     * Creates a new instance of MainPreferencePage.
     */
    public CertificateValidationPreferencePage()
    {
        super( Messages.getString( "CertificateValidationPreferencePage.CertificateValidation" ) ); //$NON-NLS-1$
        super.setPreferenceStore( ConnectionUIPlugin.getDefault().getPreferenceStore() );
        //super.setDescription( Messages.getString( "SecurityPreferencePage.GeneralSettings" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // enable/disable certificate validation
        Preferences preferences = ConnectionCorePlugin.getDefault().getPluginPreferences();
        boolean validateCertificates = preferences
            .getBoolean( ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES );
        verifyCertificatesButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "CertificateValidationPreferencePage.ValidateCertificates" ), 1 ); //$NON-NLS-1$
        verifyCertificatesButton.setSelection( validateCertificates );
        verifyCertificatesButton.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                tabFolder.setEnabled( verifyCertificatesButton.getSelection() );
            }
        } );

        // certificate list widget
        tabFolder = new TabFolder( composite, SWT.TOP );
        GridLayout mainLayout = new GridLayout();
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        tabFolder.setLayout( mainLayout );
        tabFolder.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        permanentCLComposite = new CertificateListComposite( tabFolder, SWT.NONE );
        permanentCLComposite.setInput( ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager() );
        TabItem permanentTab = new TabItem( tabFolder, SWT.NONE, 0 );
        permanentTab.setText( Messages.getString( "CertificateValidationPreferencePage.PermanentTrusted" ) ); //$NON-NLS-1$
        permanentTab.setControl( permanentCLComposite );

        sessionCLComposite = new CertificateListComposite( tabFolder, SWT.NONE );
        sessionCLComposite.setInput( ConnectionCorePlugin.getDefault().getSessionTrustStoreManager() );
        TabItem sessionTab = new TabItem( tabFolder, SWT.NONE, 1 );
        sessionTab.setText( Messages.getString( "CertificateValidationPreferencePage.TemporaryTrusted" ) ); //$NON-NLS-1$
        sessionTab.setControl( sessionCLComposite );

        tabFolder.setEnabled( verifyCertificatesButton.getSelection() );
        return composite;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        verifyCertificatesButton.setSelection( ConnectionCorePlugin.getDefault().getPluginPreferences()
            .getDefaultBoolean( ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES ) );
        ConnectionCorePlugin.getDefault().savePluginPreferences();
        super.performDefaults();
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES, verifyCertificatesButton.getSelection() );
        ConnectionCorePlugin.getDefault().savePluginPreferences();
        return true;
    }

}
