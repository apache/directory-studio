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
package org.apache.directory.ldapstudio.proxy.view.wizards;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.dialogs.SelectConnectionDialog;
import org.apache.directory.ldapstudio.proxy.view.BaseWidgetUtils;
import org.eclipse.core.internal.registry.BundleHelper;
import org.eclipse.core.internal.registry.osgi.EclipseBundleListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo;
import org.eclipse.core.runtime.internal.adaptor.PluginParser.PluginInfo;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.framework.internal.core.BundleContextImpl;
import org.eclipse.osgi.framework.internal.core.BundleLoader;
import org.eclipse.osgi.framework.internal.core.BundleLoaderProxy;
import org.eclipse.osgi.internal.baseadaptor.BundleInstall;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;


/**
 * This class implements the Connect Wizard Settings Page.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectWizardSettingsPage extends WizardPage
{
    private Combo proxyPortCombo;
    private Button useConnectionButton;
    private Text connectionText;
    private Button connectionButton;
    private Button useThisSettingsButton;
    private Combo settingsHostnameCombo;
    private Combo settingsPortCombo;
    private IConnection selectedConnection;


    /**
     * Creates a new instance of ConnectWizardSettingsPage.
     */
    public ConnectWizardSettingsPage()
    {
        super( ConnectWizardSettingsPage.class.getName() );
        setTitle( "Connect Wizard" );
        setDescription( "Specify the settings for the LDAP Proxy." );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );

        Group proxyGroup = BaseWidgetUtils.createGroup( composite, "LDAP Proxy", 1 );
        proxyGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite proxyGroupComposite = BaseWidgetUtils.createColumnContainer( proxyGroup, 2, 1 );

        BaseWidgetUtils.createLabel( proxyGroupComposite, "Proxy port:", 1 );
        proxyPortCombo = BaseWidgetUtils.createCombo( proxyGroupComposite, new String[0], -1, 1 );

        Group serverGroup = BaseWidgetUtils.createGroup( composite, "LDAP Server", 1 );
        serverGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        useConnectionButton = BaseWidgetUtils.createRadiobutton( serverGroup, "Use settings from an existing connection:", 1 );
        
        Composite connectionComposite = BaseWidgetUtils.createColumnContainer( serverGroup, 4, 1 );

        BaseWidgetUtils.createRadioIndent( connectionComposite, 1 );
        BaseWidgetUtils.createLabel( connectionComposite, "Connection:", 1 );
        connectionText = BaseWidgetUtils.createReadonlyText( connectionComposite, "", 1 );
        connectionButton = BaseWidgetUtils.createButton( connectionComposite, "B&rowse...", 1 );
        
        Label connectionInformationLabel = BaseWidgetUtils.createLabel( connectionComposite, "(Warning: This feature requires the LDAP Browser Plugin.)", 1 );
        connectionInformationLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, true, false, 4, 1 ) );

        useThisSettingsButton = BaseWidgetUtils.createRadiobutton( serverGroup, "Use this settings:", 1 );
        
        Composite settingsComposite = BaseWidgetUtils.createColumnContainer( serverGroup, 3, 1 );
        BaseWidgetUtils.createRadioIndent( settingsComposite, 1 );
        BaseWidgetUtils.createLabel( settingsComposite, "Hostname:", 1 );
        settingsHostnameCombo = BaseWidgetUtils.createCombo( settingsComposite, new String[0], -1, 1 );

        BaseWidgetUtils.createRadioIndent( settingsComposite, 1 );
        BaseWidgetUtils.createLabel( settingsComposite, "Port:", 1 );
        settingsPortCombo = BaseWidgetUtils.createCombo( settingsComposite, new String[0], -1, 1 );

        setControl( parent );
        
        checkBrowserPluginAvailability();
        
        initFieldsFromPreferences();
        
        setUiEnableState();
        
        initListeners();
    }
    
    private void checkBrowserPluginAvailability()
    {
        if ( !isPluginAvailable( "org.apache.directory.ldapstudio.browser.core" ) || !isPluginAvailable( "org.apache.directory.ldapstudio.browser.ui" ) )
        {
            useConnectionButton.setEnabled( false );
            connectionButton.setEnabled( false );
            connectionText.setEnabled( false );
        }
    }


    private void initFieldsFromPreferences()
    {
        useConnectionButton.setSelection( useConnectionButton.isEnabled() );
        useThisSettingsButton.setSelection( !useConnectionButton.isEnabled() );
    }


    /**
     * Initializes the listeners.
     */
    private void initListeners()
    {
        useConnectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setUiEnableState();
            }
        } );
        
        useThisSettingsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setUiEnableState();
            }
        } );
        
        connectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                SelectConnectionDialog dialog = new SelectConnectionDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Select Connection",
                    null );
                dialog.open();
                IConnection connection = dialog.getSelectedConnection();
                setConnection( connection );
            }
        } );
    }
    
    /**
     * Enables the UI fields.
     */
    private void setUiEnableState()
    {
        if ( ( useConnectionButton.isEnabled()  ) && ( useConnectionButton.getSelection() ) )
        {
            connectionText.setEnabled( true );
            connectionButton.setEnabled( true );
            settingsHostnameCombo.setEnabled( false );
            settingsPortCombo.setEnabled( false );
        }
        else
        {
            connectionText.setEnabled( false );
            connectionButton.setEnabled( false );
            settingsHostnameCombo.setEnabled( true );
            settingsPortCombo.setEnabled( true );
        }
    }
    
    /**
     * Checks if the given plugin is available (installed and active).
     * The plugin is actived if it's not already active.
     *
     * @param bundleId
     *      the id of the plugin
     * @return
     *      true if the given plugin is available, false if not.
     */
    public boolean isPluginAvailable( String bundleId )
    {
        Bundle pluginBundle = Platform.getBundle( bundleId );
        
        if ( pluginBundle == null )
        {
            return false;
        }
        
        if ( BundleUtility.isActive( pluginBundle ) )
        {
            return true;
        }
        else
        {
            try
            {
                pluginBundle.start();
            }
            catch ( BundleException e )
            {
                return false;
            }
            
            return BundleUtility.isActive( pluginBundle ); 
        }
    }
    
    public void setConnection( IConnection connection )
    {
        this.selectedConnection = connection;
        connectionText.setText( this.selectedConnection != null ? this.selectedConnection.getName() : "" );
    }
}
