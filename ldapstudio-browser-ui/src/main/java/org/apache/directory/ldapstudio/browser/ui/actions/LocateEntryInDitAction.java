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
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This action is used within the browser view to locate and open the selected 
 * search result or bookmark in DIT.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LocateEntryInDitAction extends LocateInDitAction
{

    /**
     * Creates a new instance of LocateEntryInDitAction.
     */
    public LocateEntryInDitAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( getSelectedSearchResults().length == 1
            && getSelectedBookmarks().length + getSelectedEntries().length + getSelectedBrowserViewCategories().length == 0 )
        {
            return "Open Search Result in DIT";
        }
        else if ( getSelectedBookmarks().length == 1
            && getSelectedSearchResults().length + getSelectedEntries().length
                + getSelectedBrowserViewCategories().length == 0 )
        {
            return "Open Bookmark in DIT";
        }
        else
        {
            return "Open Entry in DIT";
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        if ( getSelectedSearchResults().length == 1
            && getSelectedBookmarks().length + getSelectedEntries().length + getSelectedBrowserViewCategories().length == 0 )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LOCATE_SEARCHRESULT_IN_DIT );
        }
        else if ( getSelectedBookmarks().length == 1
            && getSelectedSearchResults().length + getSelectedEntries().length
                + getSelectedBrowserViewCategories().length == 0 )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LOCATE_BOOKMARK_IN_DIT );
        }
        else
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LOCATE_ENTRY_IN_DIT );
        }
    }


    /**
     * This implementation returns a connection and DN if the a search result or bookmark
     * is selected.
     */
    protected Object[] getConnectionAndDn()
    {
        if ( getSelectedSearchResults().length == 1
            && getSelectedBookmarks().length + getSelectedEntries().length + getSelectedBrowserViewCategories().length == 0 )
        {
            return new Object[]
                { getSelectedSearchResults()[0].getEntry().getConnection(),
                    getSelectedSearchResults()[0].getEntry().getDn() };
        }
        else if ( getSelectedBookmarks().length == 1
            && getSelectedSearchResults().length + getSelectedEntries().length
                + getSelectedBrowserViewCategories().length == 0 )
        {
            return new Object[]
                { getSelectedBookmarks()[0].getConnection(), getSelectedBookmarks()[0].getDn() };
        }
        else
        {
            return null;
        }
    }
}
