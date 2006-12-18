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


import org.apache.directory.ldapstudio.browser.core.jobs.InitializeChildrenJob;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.dialogs.FilterSubtreeDialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;


public class FilterSubtreeAction extends BrowserAction
{

    public FilterSubtreeAction()
    {
        super();
    }


    public void run()
    {
        if ( getSelectedEntries().length == 1 )
        {
            // InputDialog dialog = new
            // InputDialog(this.part.getSite().getShell(), "Filter", "Enter
            // filter:", this.selectedEntry.getFilter(), null);
            FilterSubtreeDialog dialog = new FilterSubtreeDialog( getShell(), getSelectedEntries()[0]
                .getChildrenFilter(), getSelectedEntries()[0].getConnection() );
            if ( dialog.open() == Dialog.OK )
            {
                String newFilter = dialog.getFilter();

                if ( newFilter == null || "".equals( newFilter.trim() ) )
                {
                    getSelectedEntries()[0].setChildrenFilter( null );
                }
                else
                {
                    getSelectedEntries()[0].setChildrenFilter( newFilter.trim() );
                }
                new InitializeChildrenJob( new IEntry[]
                    { getSelectedEntries()[0] } ).execute();

            }
        }
    }


    public String getText()
    {
        return "Filter Subtree...";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_FILTER_DIT );
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return getSelectedSearches().length + getSelectedSearchResults().length + getSelectedBookmarks().length == 0
            && getSelectedEntries().length == 1
            && ( getSelectedEntries()[0].hasChildren() || getSelectedEntries()[0].getChildrenFilter() != null );
    }

}
