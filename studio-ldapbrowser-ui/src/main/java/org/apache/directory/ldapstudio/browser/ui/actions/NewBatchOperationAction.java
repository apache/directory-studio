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

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.wizards.BatchOperationWizard;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;


/**
 * This Action opens the New Batch Operation Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewBatchOperationAction extends BrowserAction
{

    /**
     * Creates a new instance of NewBatchOperationAction.
     */
    public NewBatchOperationAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "New Batch Operation...";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_BATCH );
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
    public boolean isEnabled()
    {
        return getSelectedSearches().length == 1
            && getSelectedSearches()[0].getSearchResults() != null
            || getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length
                + getSelectedAttributes().length + getSelectedValues().length > 0;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        BatchOperationWizard wizard = new BatchOperationWizard();
        WizardDialog dialog = new WizardDialog( getShell(), wizard );
        dialog.setBlockOnOpen( true );
        dialog.create();
        dialog.open();
    }
}
