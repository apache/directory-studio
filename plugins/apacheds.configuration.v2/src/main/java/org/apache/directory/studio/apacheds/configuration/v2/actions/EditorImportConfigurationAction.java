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

package org.apache.directory.studio.apacheds.configuration.v2.actions;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.LoadConfigurationRunnable;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;


/**
 * This class implements the create connection action for an ApacheDS 2.0 server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorImportConfigurationAction extends Action
{
    private static final String DIALOG_TITLE = Messages
        .getString( "EditorImportConfigurationAction.SelectConfigurationFile" ); //$NON-NLS-1$

    /** The associated editor */
    private ServerConfigurationEditor editor;


    /**
     * Creates a new instance of EditorImportConfigurationAction.
     *
     * @param editor the associated editor
     */
    public EditorImportConfigurationAction( ServerConfigurationEditor editor )
    {
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return ApacheDS2ConfigurationPlugin.getDefault().getImageDescriptor(
            ApacheDS2ConfigurationPluginConstants.IMG_IMPORT );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "EditorImportConfigurationAction.ImportConfiguration" ); //$NON-NLS-1$
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
                        Messages.getString( "EditorImportConfigurationAction.UnsavedModifications" ), //$NON-NLS-1$
                        Messages
                            .getString( "EditorImportConfigurationAction.ConfigurationHasUnsavedModificationsSureToContinue" ) ) ) //$NON-NLS-1$
                {
                    return;
                }
            }

            // The input stream that will be used to load the configuration
            InputStream inputStream = null;

            // detect IDE or RCP:
            boolean isIDE = CommonUIUtils.isIDEEnvironment();
            
            if ( isIDE )
            {
                // Opening a dialog for file selection
                ElementTreeSelectionDialog dialog = createWorkspaceFileSelectionDialog();
                
                if ( dialog.open() == Dialog.OK )
                {
                    // Getting the input stream for the selected file
                    Object firstResult = dialog.getFirstResult();
                    
                    if ( ( firstResult != null ) && ( firstResult instanceof IFile ) )
                    {
                        inputStream = ( ( IFile ) firstResult ).getContents();
                    }
                }
                else
                {
                    // Cancel button has been clicked
                    return;
                }
            }
            else
            {
                // Opening a dialog for file selection
                FileDialog dialog = new FileDialog( editor.getSite().getShell(), SWT.OPEN | SWT.SINGLE );
                dialog.setText( DIALOG_TITLE );
                dialog.setFilterPath( System.getProperty( "user.home" ) ); //$NON-NLS-1$
                String filePath = dialog.open();
                
                if ( filePath == null )
                {
                    // Cancel button has been clicked
                    return;
                }

                // Checking the file
                File file = new File( filePath );
                
                if ( !file.exists() || !file.isFile() || !file.canRead() )
                {
                    // This is not a valid file
                    return;
                }

                // Getting the input stream for the selected file
                inputStream = new FileInputStream( file );
            }

            // Checking if we found an input stream
            if ( inputStream == null )
            {
                return;
            }

            // Requiring a confirmation from the user
            if ( !MessageDialog
                .openConfirm(
                    editor.getSite().getShell(),
                    Messages.getString( "EditorImportConfigurationAction.OverwriteExistingConfiguration" ), //$NON-NLS-1$
                    Messages
                        .getString( "EditorImportConfigurationAction.AreYouSureYouWantToOverwriteTheExistingConfiguration" ) ) ) //$NON-NLS-1$
            {
                return;
            }

            // Reading the configuration of the file
            ConfigBean configBean = LoadConfigurationRunnable.readConfiguration( inputStream );

            // Resetting the configuration back to the editor
            editor.resetConfiguration( configBean );
        }
        catch ( Exception e )
        {
            ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                new Status( Status.ERROR, "org.apache.directory.studio.apacheds.configuration.v2", 
                    e.getMessage() ) );

            MessageDialog
                .openError(
                    editor.getSite().getShell(),
                    Messages.getString( "EditorImportConfigurationAction.ErrorImportingConfigurationFile" ), //$NON-NLS-1$
                    NLS.bind(
                        Messages
                            .getString( "EditorImportConfigurationAction.AnErrorOccurredWhenImportingTheSelectedFile" ), //$NON-NLS-1$
                        e.getMessage() ) );
        }
    }


    /**
     * Creates a {@link Dialog} to select a single file in the workspace.
     *
     * @return a {@link Dialog} to select a single file in the workspace
     */
    private ElementTreeSelectionDialog createWorkspaceFileSelectionDialog()
    {
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog( editor.getSite().getShell(),
            new WorkbenchLabelProvider(), new WorkbenchContentProvider() );
        dialog.setInput( ResourcesPlugin.getWorkspace().getRoot() );
        dialog.setMessage( Messages.getString( "EditorImportConfigurationAction.SelectConfigurationFileToImport" ) ); //$NON-NLS-1$
        dialog.setTitle( DIALOG_TITLE );
        dialog.setAllowMultiple( false );
        dialog.setStatusLineAboveButtons( false );
        dialog.setValidator( new ISelectionStatusValidator()
        {
            /** The validated status */
            private Status validated = new Status( IStatus.OK, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID,
                IStatus.OK, "", null ); //$NON-NLS-1$

            /** The not validated status */
            private Status notValidated = new Status( IStatus.ERROR, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID,
                IStatus.ERROR, "", null ); //$NON-NLS-1$

            public IStatus validate( Object[] selection )
            {
                if ( selection != null )
                {
                    if ( selection.length > 0 )
                    {
                        Object selectedObject = selection[0];
                        if ( selectedObject instanceof IFile )
                        {
                            return validated;
                        }
                    }
                }

                return notValidated;
            }
        } );

        return dialog;
    }
}
