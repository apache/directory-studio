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

package org.apache.directory.studio.ldapbrowser.ui.views.modificationlogs;


import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.wizards.ExportModificationLogsWizard;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;


/**
 * Action to save the log files to a place outside the workspace.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportAction extends BrowserAction
{

    /**
     * Creates a new instance of SaveAction.
     */
    public ExportAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPORT );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "ExportAction.ExportModificationLogs" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return getInput() != null && ( getInput() instanceof ModificationLogsViewInput )
            && ( ( ModificationLogsViewInput ) getInput() ).getBrowserConnection().getConnection() != null;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        ModificationLogsViewInput input = ( ModificationLogsViewInput ) getInput();
        if ( input.getBrowserConnection().getConnection() != null )
        {
            ExportModificationLogsWizard wizard = new ExportModificationLogsWizard();
            wizard.getSearch().setBrowserConnection( input.getBrowserConnection() );
            WizardDialog dialog = new WizardDialog( getShell(), wizard );
            dialog.setBlockOnOpen( true );
            dialog.create();
            dialog.open();
        }
    }

}
