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


import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditorUtils;


/**
 * This class implements the create connection action for an OpenLDAP server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorExportConfigurationAction extends Action
{
    /** The associated editor */
    private OpenLDAPServerConfigurationEditor editor;


    /**
     * Creates a new instance of EditorExportConfigurationAction.
     *
     * @param editor
     *      the associated editor
     */
    public EditorExportConfigurationAction( OpenLDAPServerConfigurationEditor editor )
    {
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_EXPORT );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Export Configuration";
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            OpenLDAPServerConfigurationEditorUtils.saveAs( editor.getConfiguration(), false );
        }
        catch ( Exception e )
        {
            MessageDialog.openError( editor.getSite().getShell(), "Error Exporting Configuration File",
                NLS.bind( "An error occurred when exporting the selected file:\n{0}", e.getMessage() ) );
        }
    }
}
