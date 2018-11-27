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
package org.apache.directory.studio.openldap.config.actions;


import java.io.File;

import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.DirectoryDialog;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.editor.OpenLdapServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.model.OpenLdapConfiguration;
import org.apache.directory.studio.openldap.config.model.io.ConfigurationReader;


/**
 * This class implements the create connection action for an OpenLDAP server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorImportConfigurationAction extends Action
{
    /** The associated editor */
    private OpenLdapServerConfigurationEditor editor;


    /**
     * Creates a new instance of EditorImportConfigurationAction.
     *
     * @param editor
     *      the associated editor
     */
    public EditorImportConfigurationAction( OpenLdapServerConfigurationEditor editor )
    {
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_IMPORT );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Import Configuration";
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            // Checking if the editor has unsaved modifications
            if ( editor.isDirty() )
            {
                // Requiring a confirmation from the user before discarding the unsaved modifications
                if ( !MessageDialog
                    .openConfirm(
                        editor.getSite().getShell(),
                        "Unsaved Modifications",
                        "The configuration has unsaved modifications. All recent changes will be lost. Are you sure you want to continue?" ) )
                {
                    return;
                }
            }

            // The path of the directory
            String path = null;

            // Opening a dialog for directory selection
            DirectoryDialog dialog = new DirectoryDialog( editor.getSite().getShell() );
            dialog.setText( "Select Configuration Directory" );
            dialog.setFilterPath( System.getProperty( "user.home" ) );

            while ( true )
            {
                // Opening the dialog
                path = dialog.open();

                // Checking the returned path
                if ( path == null )
                {
                    // Cancel button has been clicked
                    return;
                }

                // Getting the directory indicated by the user
                final File directory = new File( path );

                // Checking if the directory exists
                if ( !directory.exists() )
                {
                    CommonUIUtils.openErrorDialog( "The directory does not exist." );
                    continue;
                }

                // Checking if the location is a directory
                if ( !directory.isDirectory() )
                {
                    CommonUIUtils.openErrorDialog( "The location is not a directory." );
                    continue;
                }

                // Checking if the directory is writable
                if ( !directory.canRead() )
                {
                    CommonUIUtils.openErrorDialog( "The directory is not writable." );
                    continue;
                }

                // The directory meets all requirements
                break;
            }

            // Checking the directory
            File configurationDirectory = new File( path );
            if ( !configurationDirectory.exists() || !configurationDirectory.isDirectory()
                || !configurationDirectory.canRead() )
            {
                // This is not a valid directory
                return;
            }

            // Requiring a confirmation from the user
            if ( !MessageDialog
                .openConfirm(
                    editor.getSite().getShell(),
                    "Overwrite Existing Configuration",
                    "Are you sure you want to overwrite the existing configuration with the contents of the selected file?" ) )
            {
                return;
            }

            // Reading the configuration of the file
            OpenLdapConfiguration configuration = ConfigurationReader.readConfiguration( configurationDirectory );

            // Resetting the configuration back to the editor
            editor.resetConfiguration( configuration );
        }
        catch ( Exception e )
        {
            MessageDialog
                .openError(
                    editor.getSite().getShell(),
                    "Error Importing Configuration File",
                    NLS.bind(
                        "An error occurred when importing the selected file:\n{0}\n\nIt does not seem to be a correct LDIF configuration file.",
                        e.getMessage() ) );
        }
    }
}
