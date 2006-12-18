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


import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.wizards.NewBookmarkWizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class NewBookmarkAction extends BrowserAction
{

    public NewBookmarkAction()
    {
    }


    public void run()
    {
        NewBookmarkWizard wizard = new NewBookmarkWizard();
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        wizard.init( window.getWorkbench(), ( IStructuredSelection ) window.getSelectionService().getSelection() );
        WizardDialog dialog = new WizardDialog( getShell(), wizard );
        dialog.setBlockOnOpen( true );
        dialog.create();
        dialog.open();
    }


    private IEntry getEntry()
    {

        if ( this.getSelectedEntries().length + this.getSelectedSearchResults().length
            + this.getSelectedBookmarks().length != 1 )
        {
            return null;
        }

        if ( getSelectedEntries().length == 1 )
        {
            return getSelectedEntries()[0];
        }
        else if ( getSelectedSearchResults().length == 1 )
        {
            return getSelectedSearchResults()[0].getEntry();
        }
        else if ( getSelectedBookmarks().length == 1 )
        {
            return getSelectedBookmarks()[0].getEntry();
        }

        return null;
    }


    public String getText()
    {
        return "New Bookmark...";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_BOOKMARK_ADD );
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return getEntry() != null && getEntry().getConnection() != null && getEntry().getConnection().isOpened();
    }

}
