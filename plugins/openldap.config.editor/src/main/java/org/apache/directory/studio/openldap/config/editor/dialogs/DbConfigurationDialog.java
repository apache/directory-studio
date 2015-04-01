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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;


/**
 * The DbConfigurationDialog is used to edit the (BDB) database configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DbConfigurationDialog extends Dialog
{
    /** The OS specific line separator character */
    private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    /** The configuration */
    private String[] configuration;

    // UI widgets
    private Text text;


    /**
     * Creates a new instance of DbConfigurationDialog.
     * 
     * @param parentShell the parent shell
     * @param initialConfiguration the initial configuration
     */
    public DbConfigurationDialog( Shell parentShell, String[] initialConfiguration )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.configuration = initialConfiguration;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Database Configuration Editor" );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        if ( ( text.getText() != null ) && ( text.getText().length() > 0 ) )
        {
            List<String> newConfiguration = new ArrayList<String>();

            String[] splittedConfiguration = text.getText().split( LINE_SEPARATOR );
            for ( int i = 0; i < splittedConfiguration.length; i++ )
            {
                newConfiguration.add( "{" + i + "}" + splittedConfiguration[i] );
            }

            configuration = newConfiguration.toArray( new String[0] );
        }

        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        // text widget
        text = new Text( composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
        gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 );
        text.setLayoutData( gd );

        text.setText( prepareInitialConfiguration() );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Prepares the initial configuration string.
     *
     * @return the initial configuration string
     */
    private String prepareInitialConfiguration()
    {
        StringBuilder sb = new StringBuilder();

        if ( ( configuration != null ) && ( configuration.length > 0 ) )
        {
            for ( String line : configuration )
            {
                sb.append( OpenLdapConfigurationPluginUtils.stripOrderingPrefix( line ) );
                sb.append( LINE_SEPARATOR );
            }
        }

        return sb.toString();
    }


    /**
     * Gets the configuration.
     * 
     * @return the configuration
     */
    public String[] getConfiguration()
    {
        return configuration;
    }
}
