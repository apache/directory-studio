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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The connections preference page contains general settings for LDAP connections.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private Combo networkProviderCombo;
    private Text ldapContextFactoryText;
    private Button useKrb5SystemPropertiesButton;
    private Label krb5LoginModuleNoteLabel;
    private Text krb5LoginModuleText;
    private Label krb5LoginModuleLabel;


    /**
     * Creates a new instance of ConnectionsPreferencePage.
     */
    public ConnectionsPreferencePage()
    {
        super( Messages.getString( "ConnectionsPreferencePage.Connections" ) ); //$NON-NLS-1$
        super.setPreferenceStore( ConnectionUIPlugin.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString( "ConnectionsPreferencePage.GeneralSettings" ) ); //$NON-NLS-1$
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

        Group defaultNetworkProviderGroup = BaseWidgetUtils.createGroup(
            BaseWidgetUtils.createColumnContainer( composite, 1, 1 ), "Default Network Provider", 1 );
        NetworkProvider defaultNetworkProvider = ConnectionCorePlugin.getDefault().getDefaultNetworkProvider();
        String[] networkProviders = new String[]
            { "Apache Directory LDAP Client API", "JNDI (Java Naming and Directory Interface)" };
        networkProviderCombo = BaseWidgetUtils.createReadonlyCombo( defaultNetworkProviderGroup,
            networkProviders, 0, 2 );
        networkProviderCombo.select( defaultNetworkProvider == NetworkProvider.APACHE_DIRECTORY_LDAP_API ? 0
            : 1 );

        Group ldapContextFactoryGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite,
            1, 1 ), Messages.getString( "ConnectionsPreferencePage.ContextFactory" ), 1 ); //$NON-NLS-1$
        Preferences preferences = ConnectionCorePlugin.getDefault().getPluginPreferences();
        String ldapCtxFactory = preferences.getString( ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY );
        String defaultLdapCtxFactory = preferences
            .getDefaultString( ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY );
        String ldapCtxFactoryNote = NLS.bind( Messages
            .getString( "ConnectionsPreferencePage.SystemDetectedContextFactory" ), defaultLdapCtxFactory ); //$NON-NLS-1$
        ldapContextFactoryText = BaseWidgetUtils.createText( ldapContextFactoryGroup, ldapCtxFactory, 1 );
        BaseWidgetUtils.createWrappedLabel( ldapContextFactoryGroup, ldapCtxFactoryNote, 1 );

        Group krb5SettingsGroup = BaseWidgetUtils.createGroup(
            BaseWidgetUtils.createColumnContainer( composite, 1, 1 ), Messages
                .getString( "ConnectionsPreferencePage.Krb5Settings" ), 1 ); //$NON-NLS-1$

        boolean useKrb5SystemProperties = preferences
            .getBoolean( ConnectionCoreConstants.PREFERENCE_USE_KRB5_SYSTEM_PROPERTIES );
        useKrb5SystemPropertiesButton = BaseWidgetUtils.createCheckbox( krb5SettingsGroup, Messages
            .getString( "ConnectionsPreferencePage.UseKrb5SystemProperties" ), 1 ); //$NON-NLS-1$
        useKrb5SystemPropertiesButton.setToolTipText( Messages
            .getString( "ConnectionsPreferencePage.UseKrb5SystemPropertiesTooltip" ) ); //$NON-NLS-1$
        useKrb5SystemPropertiesButton.setSelection( useKrb5SystemProperties );

        krb5LoginModuleLabel = BaseWidgetUtils.createLabel( krb5SettingsGroup, Messages
            .getString( "ConnectionsPreferencePage.Krb5LoginModule" ), 1 ); //$NON-NLS-1$
        String krb5LoginModule = preferences.getString( ConnectionCoreConstants.PREFERENCE_KRB5_LOGIN_MODULE );
        String defaultKrb5LoginModule = preferences
            .getDefaultString( ConnectionCoreConstants.PREFERENCE_KRB5_LOGIN_MODULE );
        String krb5LoginModuleNote = NLS.bind( Messages
            .getString( "ConnectionsPreferencePage.SystemDetectedContextFactory" ), defaultKrb5LoginModule ); //$NON-NLS-1$
        krb5LoginModuleText = BaseWidgetUtils.createText( krb5SettingsGroup, krb5LoginModule, 1 );
        krb5LoginModuleNoteLabel = BaseWidgetUtils.createWrappedLabel( krb5SettingsGroup, krb5LoginModuleNote, 1 );

        useKrb5SystemPropertiesButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        } );

        validate();

        return composite;
    }


    private void validate()
    {
        krb5LoginModuleLabel.setEnabled( !useKrb5SystemPropertiesButton.getSelection() );
        krb5LoginModuleText.setEnabled( !useKrb5SystemPropertiesButton.getSelection() );
        krb5LoginModuleNoteLabel.setEnabled( !useKrb5SystemPropertiesButton.getSelection() );
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        NetworkProvider defaultdDefaultNetworkProvider = ConnectionCorePlugin.getDefault().getNetworkProvider(
            ConnectionCorePlugin.getDefault().getPluginPreferences()
                .getDefaultInt( ConnectionCoreConstants.PREFERENCE_DEFAULT_NETWORK_PROVIDER ) );
        networkProviderCombo.select( defaultdDefaultNetworkProvider == NetworkProvider.APACHE_DIRECTORY_LDAP_API ? 0
            : 1 );

        ldapContextFactoryText.setText( ConnectionCorePlugin.getDefault().getPluginPreferences().getDefaultString(
            ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY ) );
        krb5LoginModuleText.setText( ConnectionCorePlugin.getDefault().getPluginPreferences().getDefaultString(
            ConnectionCoreConstants.PREFERENCE_KRB5_LOGIN_MODULE ) );
        useKrb5SystemPropertiesButton.setSelection( ConnectionCorePlugin.getDefault().getPluginPreferences()
            .getDefaultBoolean( ConnectionCoreConstants.PREFERENCE_USE_KRB5_SYSTEM_PROPERTIES ) );

        super.performDefaults();
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        ConnectionCorePlugin.getDefault().getPluginPreferences()
            .setValue( ConnectionCoreConstants.PREFERENCE_DEFAULT_NETWORK_PROVIDER,
                getNetworkProviderValue() );
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY, ldapContextFactoryText.getText() );
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_KRB5_LOGIN_MODULE, krb5LoginModuleText.getText() );
        ConnectionCorePlugin.getDefault().getPluginPreferences()
            .setValue( ConnectionCoreConstants.PREFERENCE_USE_KRB5_SYSTEM_PROPERTIES,
                useKrb5SystemPropertiesButton.getSelection() );

        ConnectionCorePlugin.getDefault().savePluginPreferences();

        return true;
    }


    /**
     * Gets the encyrption method.
     * 
     * @return the encyrption method
     */
    private int getNetworkProviderValue()
    {
        switch ( networkProviderCombo.getSelectionIndex() )
        {
            case 1:
                return ConnectionCoreConstants.PREFERENCE_NETWORK_PROVIDER_JNDI;
            default:
                return ConnectionCoreConstants.PREFERENCE_NETWORK_PROVIDER_APACHE_DIRECTORY_LDAP_API;
        }
    }
}
