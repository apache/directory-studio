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
import org.apache.directory.ldapstudio.browser.ui.search.SearchPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.PlatformUI;


public class NewSearchAction extends BrowserAction
{

    public NewSearchAction()
    {
        super();
    }


    public void run()
    {
        NewSearchUI.openSearchDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow(), SearchPage.getId() );
    }


    public String getText()
    {
        return "New Search...";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_SEARCH_NEW );
    }


    public String getCommandId()
    {
        return "org.eclipse.search.ui.openSearchDialog";
    }


    public boolean isEnabled()
    {
        return getSelectedEntries().length + getSelectedSearchResults().length + getSelectedSearches().length
            + getSelectedBookmarks().length + getSelectedConnections().length + getSelectedAttributes().length
            + getSelectedAttributeHierarchies().length + getSelectedValues().length > 0;

    }

}
