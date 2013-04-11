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


import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditorUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;


/**
 * This class implements the create connection action for an ApacheDS 1.5.7 server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorExportConfigurationAction extends Action
{
    /** The associated editor */
    private ServerConfigurationEditor editor;


    /**
     * Creates a new instance of EditorExportConfigurationAction.
     *
     * @param editor
     *      the associated editor
     */
    public EditorExportConfigurationAction( ServerConfigurationEditor editor )
    {
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return ApacheDS2ConfigurationPlugin.getDefault().getImageDescriptor(
            ApacheDS2ConfigurationPluginConstants.IMG_EXPORT );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "EditorExportConfigurationAction.ExportConfiguration" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            ServerConfigurationEditorUtils.saveAs( new NullProgressMonitor(), editor.getSite()
                .getShell(), editor.getEditorInput(), editor.getConfigWriter() );
        }
        catch ( Exception e )
        {
            MessageDialog
                .openError( editor.getSite().getShell(),
                    Messages.getString( "EditorExportConfigurationAction.ErrorExportingConfigurationFile" ), //$NON-NLS-1$
                    NLS.bind(
                        Messages
                            .getString( "EditorExportConfigurationAction.AnErrorOccurredWhenExportingTheSelectedFile" ), e.getMessage() ) ); //$NON-NLS-1$
        }
    }
}
