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
package org.apache.directory.studio.apacheds.configuration.wizards;


import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationVersionEnum;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


/**
 * This class represents the WizardPage of the New ApacheDS Configuration File Wizard.
 * <p>
 * It is used to let the user choose the target version for the configuration file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewApacheDSConfigurationFileWizardPage extends WizardPage
{
    /** Version 1.5.0 */
    private static final String VERSION_1_5_0 = "1.5.0"; //$NON-NLS-1$
    /** Version 1.5.1 */
    private static final String VERSION_1_5_1 = "1.5.1"; //$NON-NLS-1$
    /** Version 1.5.2 */
    private static final String VERSION_1_5_2 = "1.5.2"; //$NON-NLS-1$
    /** Version 1.5.3 */
    private static final String VERSION_1_5_3 = "1.5.3"; //$NON-NLS-1$
    /** Version 1.5.4 */
    private static final String VERSION_1_5_4 = "1.5.4"; //$NON-NLS-1$
    /** Version 1.5.5 */
    private static final String VERSION_1_5_5 = "1.5.5"; //$NON-NLS-1$
    /** Version 1.5.5 */
    private static final String VERSION_1_5_6 = "1.5.6"; //$NON-NLS-1$

    // UI Fields
    private Combo versionCombo;


    /**
     * Creates a new instance of NewApacheDSConfigurationFileWizardPage.
     */
    public NewApacheDSConfigurationFileWizardPage()
    {
        super( NewApacheDSConfigurationFileWizardPage.class.getCanonicalName() );
        setTitle( Messages.getString( "NewApacheDSConfigurationFileWizardPage.CreateConfigurationFile" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewApacheDSConfigurationFileWizardPage.SelectTargetVersion" ) ); //$NON-NLS-1$
        setImageDescriptor( ApacheDSConfigurationPlugin.getDefault().getImageDescriptor(
            ApacheDSConfigurationPluginConstants.IMG_NEW_SERVER_CONFIGURATION_FILE_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        composite.setLayout( new GridLayout() );

        // Target Version Group
        Group targetVersionGroup = new Group( composite, SWT.NONE );
        targetVersionGroup.setText( Messages.getString( "NewApacheDSConfigurationFileWizardPage.TargetVersion" ) ); //$NON-NLS-1$
        targetVersionGroup.setLayout( new GridLayout( 2, false ) );
        targetVersionGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Version Label
        Label versionLabel = new Label( targetVersionGroup, SWT.NONE );
        versionLabel.setText( Messages.getString( "NewApacheDSConfigurationFileWizardPage.ApacheDSVersion" ) ); //$NON-NLS-1$

        // Version Combo
        versionCombo = new Combo( targetVersionGroup, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );
        versionCombo.setItems( new String[]
            { VERSION_1_5_6, VERSION_1_5_5, VERSION_1_5_4, VERSION_1_5_3, VERSION_1_5_2, VERSION_1_5_1, VERSION_1_5_0 } );
        versionCombo.select( 0 );

        setControl( composite );
    }


    /**
     * Gets the target version for the configuration file.
     *
     * @return
     *      the target version for the configuration file
     */
    public ServerConfigurationVersionEnum getTargetVersion()
    {
        // Getting the selection
        String selection = versionCombo.getItem( versionCombo.getSelectionIndex() );

        // Checking the version
        if ( selection.equals( VERSION_1_5_6 ) )
        {
            return ServerConfigurationVersionEnum.VERSION_1_5_6;
        }
        else if ( selection.equals( VERSION_1_5_5 ) )
        {
            return ServerConfigurationVersionEnum.VERSION_1_5_5;
        }
        else if ( selection.equals( VERSION_1_5_4 ) )
        {
            return ServerConfigurationVersionEnum.VERSION_1_5_4;
        }
        else if ( selection.equals( VERSION_1_5_3 ) )
        {
            return ServerConfigurationVersionEnum.VERSION_1_5_3;
        }
        else if ( selection.equals( VERSION_1_5_2 ) )
        {
            return ServerConfigurationVersionEnum.VERSION_1_5_2;
        }
        else if ( selection.equals( VERSION_1_5_1 ) )
        {
            return ServerConfigurationVersionEnum.VERSION_1_5_1;
        }
        else if ( selection.equals( VERSION_1_5_0 ) )
        {
            return ServerConfigurationVersionEnum.VERSION_1_5_0;
        }

        // Default
        return ServerConfigurationVersionEnum.VERSION_1_5_6;
    }
}