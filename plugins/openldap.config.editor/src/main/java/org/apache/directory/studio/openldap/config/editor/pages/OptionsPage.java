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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.openldap.common.ui.model.AllowFeatureEnum;
import org.apache.directory.studio.openldap.common.ui.model.DisallowFeatureEnum;
import org.apache.directory.studio.openldap.common.ui.model.RequireConditionEnum;
import org.apache.directory.studio.openldap.common.ui.model.RestrictOperationEnum;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.wrappers.AllowFeatureDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.AuthIdRewriteWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.AuthzRegexpWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.DisallowFeatureDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.RequireConditionDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.RestrictOperationDecorator;
import org.apache.directory.studio.openldap.config.model.OlcGlobal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the Options Page of the Server Configuration Editor. We manage the 
 * following parameters :
 * <ul>
 *   <li>Operations and features :
 *     <ul>
 *       <li>olcAllows</li>
 *       <li>olcDisallows</li>
 *       <li>olcRequires</li>
 *       <li>olcRestrict</li>
 *     </ul>
 *   </li>
 *   <li>Authorization regexp & rewrite rules :
 *     <ul>
 *       <li>olcAuthIDRewrite</li>
 *       <li>olcAuthzRegexp</li>
 *     </ul>
 *   </li>
 *   <li>Miscellaneous options :
 *     <ul>
 *       <li>olcArgsFile</li>
 *       <li>olcPluginLogFile</li>
 *       <li>olcReferral</li>
 *       <li>olcAuthzPolicy</li>
 *       <li>olcRootDSE</li>
 *       <li>olcReadOnly</li>
 *       <li>olcGentleHUP</li>
 *       <li>olcReadOnly</li>
 *       <li>olcReverseLookup</li>
 *     </ul>
 *   </li>
 * </ul>
 * Here is the content of this page :
 * <pre>
 * .-----------------------------------------------------------------------------------.
 * | Options                                                                           |
 * +-----------------------------------------------------------------------------------+
 * | .-------------------------------------------------------------------------------. |
 * | |V Operations & Features                                                        | |
 * | +-------------------------------------------------------------------------------+ |
 * | |                                                                               | |
 * | | Allowed Features :                     Disallowed Features :                  | |
 * | | +--------------------------+           +--------------------------+           | |
 * | | | xyz                      | (Add...)  | xyz                      | (Add...)  | |
 * | | | abcde                    |           | abcde                    |           | |
 * | | | aaa                      | (Delete)  | aaa                      | (Delete)  | |
 * | | +--------------------------+           +--------------------------+           | |
 * | |                                                                               | |
 * | | Required Conditions :                  Restricted Operations :                | |
 * | | +--------------------------+           +--------------------------+           | |
 * | | | xyz                      | (Add...)  | xyz                      | (Add...)  | |
 * | | | abcde                    |           | abcde                    |           | |
 * | | | aaa                      | (Delete)  | aaa                      | (Delete)  | |
 * | | +--------------------------+           +--------------------------+           | |
 * | +-------------------------------------------------------------------------------+ |
 * | .-------------------------------------------------------------------------------. |
 * | |V Authorization regexps & rewrite rules                                        | |
 * | +-------------------------------------------------------------------------------+ |
 * | | AuthId Rewrite Rules :                 Authorization regexps :                | |
 * | | +--------------------------+           +--------------------------+           | |
 * | | | xyz                      | (Add...)  | xyz                      | (Add...)  | |
 * | | | abcde                    |           | abcde                    |           | |
 * | | | aaa                      | (Edit...) | aaa                      | (Edit...) | |
 * | | |                          |           |                          |           | |
 * | | |                          | (Delete)  |                          | (Delete)  | |
 * | | +--------------------------+           +--------------------------+           | |
 * | +-------------------------------------------------------------------------------+ |
 * | .-------------------------------------------------------------------------------. |
 * | |V Miscellaneous options                                                        | |
 * | +-------------------------------------------------------------------------------+ |
 * | | Args File : [///////////////////////]  Plugin Log File : [//////////////////] | |
 * | |                                                                               | |
 * | | Referral :  [///////////////////////]  Authz Policy :    [------------------] | |
 * | |                                                                               | |
 * | | Root DSE :  [///////////////////////]  GentleHUP :       [X]                  | |
 * | |                                                                               | |
 * | | Read Only : [X]                        Reverse Lookup :  [X]                  | |
 * | +-------------------------------------------------------------------------------+ |
 * +-----------------------------------------------------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OptionsPage extends OpenLDAPServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = OptionsPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "OpenLDAPOptionsPage.Title" );

    // UI Controls
    // Operations and features
    /** The olcAllows parameter */
    private TableWidget<AllowFeatureEnum> allowFeatureTableWidget;

    /** The olcDisallows parameter */
    private TableWidget<DisallowFeatureEnum> disallowFeatureTableWidget;
    
    /** The olcRequires parameter */
    private TableWidget<RequireConditionEnum> requireConditionTableWidget;

    /** The olcRestrict parameter */
    private TableWidget<RestrictOperationEnum> restrictOperationTableWidget;

    // The Authz regexp and rewrite rules
    /** The olcAuthIDRewrite parameter */
    private TableWidget<AuthIdRewriteWrapper> authIdRewriteTableWidget;

    /** The olcAuthzRegexp parameter */
    private TableWidget<AuthzRegexpWrapper> authzRegexpTableWidget;

    
    // The miscellaneous parameters
    /** The olcArgsFile parameter */
    private Text argsFileText; 

    /** The olcPluginLogFile parameter */
    private Text pluginLogFileText; 

    /** The olcReferral parameter */
    private Text referralText; 

    /** The olcRootDSE parameter */
    private Text rootDseText; 

    /** The olcAuthzPolicy parameter */
    private Combo authzPolicyCombo; 

    /** The olcGentleHUP parameter */
    private Button gentleHupCheckbox;

    /** The olcReadOnly parameter */
    private Button readOnlyCheckbox;

    /** The olcReverseLookup parameter */
    private Button reverseLookupCheckbox;
    
    
    /**
     * The olcAllows listener
     */
    private WidgetModifyListener allowFeatureListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> allowFeatures = new ArrayList<String>();
            
            for ( AllowFeatureEnum allowFeature : allowFeatureTableWidget.getElements() )
            {
                allowFeatures.add( allowFeature.getName() );
            }
            
            getConfiguration().getGlobal().setOlcAllows( allowFeatures );
        }
    };
    
    
    /**
     * The olcDisallows listener
     */
    private WidgetModifyListener disallowFeatureListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> disallowFeatures = new ArrayList<String>();
            
            for ( DisallowFeatureEnum disallowFeature : disallowFeatureTableWidget.getElements() )
            {
                disallowFeatures.add( disallowFeature.getName() );
            }
            
            getConfiguration().getGlobal().setOlcDisallows( disallowFeatures );
        }
    };
    
    
    /**
     * The olcRequires listener
     */
    private WidgetModifyListener requireConditionListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> requires = new ArrayList<String>();
            
            for ( RequireConditionEnum requireCondition : requireConditionTableWidget.getElements() )
            {
                requires.add( requireCondition.getName() );
            }
            
            getConfiguration().getGlobal().setOlcRequires( requires );
        }
    };
    
    
    /**
     * The olcRestrict listener
     */
    private WidgetModifyListener restrictOperationListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> restricts = new ArrayList<String>();
            
            for ( RestrictOperationEnum restrictOperation : restrictOperationTableWidget.getElements() )
            {
                restricts.add( restrictOperation.getName() );
            }
            
            getConfiguration().getGlobal().setOlcRestrict( restricts );
        }
    };

    
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
     * Creates the OpenLDAP options config Tab. It contains 3 sections :
     * 
     * <pre>
     * +-----------------------------+
     * |                             |
     * |    Features & Operations    |
     * |                             |
     * +-----------------------------+
     * |                             |
     * |   Authz regxep  & rewrite   |
     * |                             |
     * +-----------------------------+
     * |                             |
     * |        Miscellaneous        |
     * |                             |
     * +-----------------------------+
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        createFeaturesAndOperationsSection( toolkit, parent );
        //createAuthzRegexpAndRewriteSection( toolkit, parent );
        //createMiscellaneousSection( toolkit, parent );
    }


    /**
     * Creates the Features & Operations section.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createFeaturesAndOperationsSection( FormToolkit toolkit, Composite parent )
    {
        // The Features & Operations section, which can be expanded or compacted
        Section section = createSection( toolkit, parent, 
            Messages.getString( "OpenLDAPOptionsPage.FeaturesAndOperationsSection" ) );
        Composite composite = createSectionComposite( toolkit, section, 4, false );

        // The olcAllows parameter label
        Label allowFeatureLabel = toolkit.createLabel( composite, 
            Messages.getString( "OpenLDAPOptionsPage.AllowFeature" ) ); //$NON-NLS-1$
        allowFeatureLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcDisallows parameter label
        Label disallowFeatureLabel = toolkit.createLabel( composite,
            Messages.getString( "OpenLDAPOptionsPage.DisallowFeature" ) ); //$NON-NLS-1$
        disallowFeatureLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcAllows parameter table
        allowFeatureTableWidget = new TableWidget<AllowFeatureEnum>( new AllowFeatureDecorator( composite.getShell() ) );

        allowFeatureTableWidget.createWidgetNoEdit( composite, toolkit );
        allowFeatureTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( allowFeatureTableWidget, allowFeatureListener );

        // The olcDisallows parameter table
        disallowFeatureTableWidget = new TableWidget<DisallowFeatureEnum>( new DisallowFeatureDecorator( composite.getShell() ) );

        disallowFeatureTableWidget.createWidgetNoEdit( composite, toolkit );
        disallowFeatureTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( disallowFeatureTableWidget, disallowFeatureListener );

        // The olcRequires parameter label
        Label requireConditionLabel = toolkit.createLabel( composite, 
            Messages.getString( "OpenLDAPOptionsPage.RequireCondition" ) ); //$NON-NLS-1$
        requireConditionLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcRestrict parameter label
        Label restrictOperationLabel = toolkit.createLabel( composite,
            Messages.getString( "OpenLDAPOptionsPage.RestrictOperation" ) ); //$NON-NLS-1$
        restrictOperationLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcRequires parameter table
        requireConditionTableWidget = new TableWidget<RequireConditionEnum>( new RequireConditionDecorator( composite.getShell() ) );

        requireConditionTableWidget.createWidgetNoEdit( composite, toolkit );
        requireConditionTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( requireConditionTableWidget, requireConditionListener );

        // The olcRestrict parameter table
        restrictOperationTableWidget = new TableWidget<RestrictOperationEnum>( new RestrictOperationDecorator( composite.getShell() ) );

        restrictOperationTableWidget.createWidgetNoEdit( composite, toolkit );
        restrictOperationTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( restrictOperationTableWidget, restrictOperationListener );
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

        /*
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
        */
    }

    
    /**
     * Adds listeners to UI Controls.
     */
    private void addListeners()
    {
        addDirtyListener( allowFeatureTableWidget );
        addDirtyListener( disallowFeatureTableWidget );
        addDirtyListener( requireConditionTableWidget );
        addDirtyListener( restrictOperationTableWidget );
    }

    
    /**
     * Removes listeners to UI Controls.
     */
    private void removeListeners()
    {
        removeDirtyListener( allowFeatureTableWidget );
        removeDirtyListener( disallowFeatureTableWidget );
        removeDirtyListener( requireConditionTableWidget );
        removeDirtyListener( restrictOperationTableWidget );
    }
    

    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
        removeListeners();

        // Getting the global configuration object
        OlcGlobal global = getConfiguration().getGlobal();

        if ( global != null )
        {
            //
            // Assigning values to UI Controls
            //

            // Allow Feature Table Widget
            List<String> allowedFeatures = global.getOlcAllows();

            if ( allowedFeatures != null )
            {
                List<AllowFeatureEnum> alloweds = new ArrayList<AllowFeatureEnum>();
                
                for ( String allowedFeature : allowedFeatures )
                {
                    alloweds.add( AllowFeatureEnum.getFeature( allowedFeature ) );
                }
                
                allowFeatureTableWidget.setElements( alloweds );
            }
            else
            {
                allowFeatureTableWidget.setElements( new ArrayList<AllowFeatureEnum>() );
            }

            // Disallow Feature Table Widget
            List<String> disallowedFeatures = global.getOlcDisallows();

            if ( disallowedFeatures != null )
            {
                List<DisallowFeatureEnum> disalloweds = new ArrayList<DisallowFeatureEnum>();
                
                for ( String disallowedFeature : disallowedFeatures )
                {
                    disalloweds.add( DisallowFeatureEnum.getFeature( disallowedFeature ) );
                }
                
                disallowFeatureTableWidget.setElements( disalloweds );
            }
            else
            {
                disallowFeatureTableWidget.setElements( new ArrayList<DisallowFeatureEnum>() );
            }

            // Require Condition Table Widget
            List<String> requireConditions = global.getOlcRequires();

            if ( requireConditions != null )
            {
                List<RequireConditionEnum> requires = new ArrayList<RequireConditionEnum>();
                
                for ( String requireCondition : requireConditions )
                {
                    requires.add( RequireConditionEnum.getCondition( requireCondition ) );
                }
                
                requireConditionTableWidget.setElements( requires );
            }
            else
            {
                requireConditionTableWidget.setElements( new ArrayList<RequireConditionEnum>() );
            }

            // Restrict Operation Condition Table Widget
            List<String> restrictOperations = global.getOlcRestrict();

            if ( restrictOperations != null )
            {
                List<RestrictOperationEnum> restricts = new ArrayList<RestrictOperationEnum>();
                
                for ( String restrictOperation : restrictOperations )
                {
                    restricts.add( RestrictOperationEnum.getOperation( restrictOperation ) );
                }
                
                restrictOperationTableWidget.setElements( restricts );
            }
            else
            {
                restrictOperationTableWidget.setElements( new ArrayList<RestrictOperationEnum>() );
            }

            addListeners();
        }
    }
}
