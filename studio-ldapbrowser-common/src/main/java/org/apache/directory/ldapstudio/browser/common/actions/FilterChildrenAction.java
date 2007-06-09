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

package org.apache.directory.ldapstudio.browser.common.actions;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.ldapstudio.browser.common.dialogs.FilterWidgetDialog;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeChildrenJob;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This action opens the Filter Children Dialog and sets the children filter to the
 * currently selected entry. It is useful when browsing the DIT and entries with 
 * many child nodes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterChildrenAction extends BrowserAction
{

    /**
     * Creates a new instance of FilterChildrenAction.
     */
    public FilterChildrenAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( getSelectedEntries().length == 1 )
        {
            FilterWidgetDialog dialog = new FilterWidgetDialog( getShell(), "Filter Children", getSelectedEntries()[0]
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


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Filter Children...";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_FILTER_DIT );
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
        return getSelectedSearches().length + getSelectedSearchResults().length + getSelectedBookmarks().length == 0
            && getSelectedEntries().length == 1
            && ( getSelectedEntries()[0].hasChildren() || getSelectedEntries()[0].getChildrenFilter() != null );
    }
}
