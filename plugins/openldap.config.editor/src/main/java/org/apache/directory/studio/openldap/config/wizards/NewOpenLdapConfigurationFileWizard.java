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
package org.apache.directory.studio.openldap.config.wizards;


import org.apache.directory.studio.ldapbrowser.common.dialogs.preferences.AttributeValueEditorDialog;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.editor.NewServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.OpenLdapServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.dialogs.OpenLdapConfigDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the New OpenLDAP Configuration File Wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewOpenLdapConfigurationFileWizard extends Wizard implements INewWizard
{
    /** The window. */
    private IWorkbenchWindow window;

    /**
     * Creates a new instance of NewOpenLDAPConfigurationFileWizard.
     */
    public NewOpenLdapConfigurationFileWizard()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        window = workbench.getActiveWorkbenchWindow();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        window = null;
    }


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public static String getId()
    {
        return OpenLdapConfigurationPluginConstants.WIZARD_NEW_OPENLDAP_CONFIG;
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        // This wizard has no page
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        try
        {
            OpenLdapConfigDialog dialog = new OpenLdapConfigDialog( getShell() );

            if ( dialog.open() == AttributeValueEditorDialog.OK )
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                NewServerConfigurationInput configInput = new NewServerConfigurationInput();
                configInput.setOpenLdapConfigFormat( dialog.getOpenLdapConfigFormat() );
                configInput.setOpenLdapVersion( dialog.getOpenLdapVersion() );
                
                page.openEditor( configInput, OpenLdapServerConfigurationEditor.ID );
            }
        }
        catch ( PartInitException e )
        {
            // Should never happen
            return false;
        }

        return true;
    }
}
