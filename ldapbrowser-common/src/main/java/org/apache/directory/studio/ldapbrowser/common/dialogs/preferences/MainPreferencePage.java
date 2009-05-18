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

package org.apache.directory.studio.ldapbrowser.common.dialogs.preferences;


import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The main preference page contains general settings for the LDAP browser.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private Text jndiLdapContextProvider;

    private Button verifyCertificatesButton;


    /**
     * 
     * Creates a new instance of MainPreferencePage.
     */
    public MainPreferencePage()
    {
        super( Messages.getString( "MainPreferencePage.LDAP" ) ); //$NON-NLS-1$
        super.setPreferenceStore( BrowserCommonActivator.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString( "MainPreferencePage.GeneralSettings" ) ); //$NON-NLS-1$
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

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        Group group = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ), Messages
            .getString( "MainPreferencePage.ContextProvider" ), 1 ); //$NON-NLS-1$

        Preferences preferences = ConnectionCorePlugin.getDefault().getPluginPreferences();
        String ldapCtxFactory = preferences.getString( ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY );
        String defaultLdapCtxFactory = preferences
            .getDefaultString( ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY );
        String note = NLS.bind(
            Messages.getString( "MainPreferencePage.SystemDetectedContextFactory" ), defaultLdapCtxFactory ); //$NON-NLS-1$

        jndiLdapContextProvider = BaseWidgetUtils.createText( group, ldapCtxFactory, 1 );
        BaseWidgetUtils.createWrappedLabel( group, note, 1 );

        boolean validateCertificates = preferences
            .getBoolean( ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES );
        verifyCertificatesButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "MainPreferencePage.ValidateCertificates" ), 1 ); //$NON-NLS-1$
        verifyCertificatesButton.setSelection( validateCertificates );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        jndiLdapContextProvider.setText( ConnectionCorePlugin.getDefault().getPluginPreferences().getDefaultString(
            ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY ) );
        verifyCertificatesButton.setSelection( ConnectionCorePlugin.getDefault().getPluginPreferences()
            .getDefaultBoolean( ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES ) );
        super.performDefaults();
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY, jndiLdapContextProvider.getText() );
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES, verifyCertificatesButton.getSelection() );
        return true;
    }

}
