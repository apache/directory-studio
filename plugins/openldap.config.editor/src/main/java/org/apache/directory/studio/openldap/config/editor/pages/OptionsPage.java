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
package org.apache.directory.studio.openldap.config.editor.pages;


import java.util.List;

import org.apache.directory.studio.openldap.config.editor.Messages;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.model.OlcGlobal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the Options Page of the Server Configuration Editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OptionsPage extends OpenLDAPServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = OptionsPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = "Options";

    // UI Controls
    /** The olcPluginLogFile parameter */
    private Text pluginLogFileText; 
    private Text authUsernamesToDnRewriteRuleText;
    private Text proxyAuthorizationPolicyText;
    private Text authzUsernamesToDnRegexpText;


    /**
     * Creates a new instance of OptionsPage.
     *
     * @param editor the associated editor
     */
    public OptionsPage( OpenLDAPServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    
    /**
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        TableWrapLayout twl = new TableWrapLayout();
        twl.numColumns = 2;
        twl.makeColumnsEqualWidth = true;
        parent.setLayout( twl );

        Composite leftComposite = toolkit.createComposite( parent );
        leftComposite.setLayout( new GridLayout() );
        TableWrapData leftCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        leftCompositeTableWrapData.grabHorizontal = true;
        leftComposite.setLayoutData( leftCompositeTableWrapData );

        Composite rightComposite = toolkit.createComposite( parent );
        rightComposite.setLayout( new GridLayout() );
        TableWrapData rightCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        rightCompositeTableWrapData.grabHorizontal = true;
        rightComposite.setLayoutData( rightCompositeTableWrapData );

        createLogsSection( toolkit, leftComposite );
        createAuthenticationAndAuthorizationSection( toolkit, leftComposite );

        refreshUI();
    }


    /**
     * Creates the Logs section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createLogsSection( FormToolkit toolkit, Composite parent )
    {
        // The Logs section, which can be expanded or compacted
        Section section = createSection( toolkit, parent, Messages.getString( "OptionsPage.LogTitle" ) );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // Plugin Log File Text
        toolkit.createLabel( composite, "Plugin Log File:" );
        pluginLogFileText = toolkit.createText( composite, "" );
        pluginLogFileText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Authentication & Authorization section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createAuthenticationAndAuthorizationSection( FormToolkit toolkit, Composite parent )
    {
        Section section = createSection( toolkit, parent, "Authentication & Authorization" );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // Authentication Usernames To DN Rewrite Rule Text
        toolkit.createLabel( composite, "Authentication rewrite rule to convert simple user names to an LDAP DN:" );
        authUsernamesToDnRewriteRuleText = toolkit.createText( composite, "" );
        authUsernamesToDnRewriteRuleText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Proxy Authorization Policy Text
        toolkit.createLabel( composite, "Proxy authorization policy text:" );
        proxyAuthorizationPolicyText = toolkit.createText( composite, "" );
        proxyAuthorizationPolicyText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Authorization Usernames To DN Regexp Text
        toolkit.createLabel( composite, "Authorization Regexp to convert simple user names to an LDAP DN:" );
        authzUsernamesToDnRegexpText = toolkit.createText( composite, "" );
        authzUsernamesToDnRegexpText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }

    
    /**
     * Adds listeners to UI Controls.
     */
    private void addListeners()
    {
    }

    
    /**
     * Removes listeners to UI Controls.
     */
    private void removeListeners()
    {
    }
    

    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
        if ( isInitialized() )
        {
            removeListeners();

            // Getting the global configuration object
            OlcGlobal global = getConfiguration().getGlobal();

            if ( global != null )
            {
                //
                // Assigning values to UI Controls
                //

                // Plugin Log File Text
                String pluginLogFile = global.getOlcPluginLogFile();

                if ( pluginLogFile != null )
                {
                    pluginLogFileText.setText( pluginLogFile );
                }
                else
                {
                    pluginLogFileText.setText( "" );
                }

                // Authentication Usernames To DN Rewrite Rule Text
                List<String> authUsernamesToDnRewriteRule = global.getOlcAuthIDRewrite();

                if ( authUsernamesToDnRewriteRule != null )
                {
                    authUsernamesToDnRewriteRuleText.setText( authUsernamesToDnRewriteRule + "" );
                }
                else
                {
                    authUsernamesToDnRewriteRuleText.setText( "" );
                }

                // Proxy Authorization Policy Text
                String proxyAuthorizationPolicy = global.getOlcAuthzPolicy();

                if ( proxyAuthorizationPolicy != null )
                {
                    proxyAuthorizationPolicyText.setText( proxyAuthorizationPolicy );
                }
                else
                {
                    proxyAuthorizationPolicyText.setText( "" );
                }

                // Authorization Usernames To DN Regexp Text
                List<String> authzUsernamesToDnRegexp = global.getOlcAuthzRegexp();

                if ( authzUsernamesToDnRegexp != null )
                {
                    authzUsernamesToDnRegexpText.setText( authzUsernamesToDnRegexp + "" );
                }
                else
                {
                    authzUsernamesToDnRegexpText.setText( "" );
                }

                addListeners();
            }
        }
    }
}
