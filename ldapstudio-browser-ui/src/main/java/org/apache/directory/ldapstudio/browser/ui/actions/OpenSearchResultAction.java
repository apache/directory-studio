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
import org.apache.directory.ldapstudio.browser.ui.views.browser.BrowserView;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class OpenSearchResultAction extends BrowserAction
{

    public OpenSearchResultAction()
    {
        super();
    }


    public void run()
    {
        if ( getSelectedSearchResults().length == 1 )
        {
            String targetId = BrowserView.getId();
            IViewPart targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                targetId );
            if ( targetView == null )
            {
                try
                {
                    targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                        targetId, null, IWorkbenchPage.VIEW_ACTIVATE );
                }
                catch ( PartInitException e )
                {
                }
            }
            if ( targetView != null && targetView instanceof BrowserView )
            {
                ( ( BrowserView ) targetView ).select( getSelectedSearchResults()[0] );
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate( targetView );
            }
        }
    }


    public String getText()
    {
        return "Open Search Result";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_OPEN_SEARCHRESULT );
    }


    public String getCommandId()
    {
        return "org.apache.directory.ldapstudio.browser.action.openSearchResult";
    }


    public boolean isEnabled()
    {
        return getSelectedSearchResults().length == 1;
    }

}
