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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.wizards.NewBookmarkWizard;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


/**
 * This Action creates a new Bookmark.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewBookmarkAction extends BrowserAction
{
    /**
     * Creates a new instance of NewBookmarkAction.
     */
    public NewBookmarkAction()
    {
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "NewBookmarkAction.NewBookmark" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_BOOKMARK_ADD );
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
        return true;
    }
}
