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


import org.apache.directory.ldapstudio.browser.core.model.URL;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


public class CopyUrlAction extends BrowserAction
{

    public CopyUrlAction()
    {
    }


    public void run()
    {
        URL url = null;
        if ( getSelectedSearches().length > 0 )
        {
            url = getSelectedSearches()[0].getUrl();
        }
        else if ( getSelectedEntries().length > 0 )
        {
            url = getSelectedEntries()[0].getUrl();
        }
        else if ( getSelectedAttributes().length > 0 )
        {
            url = getSelectedAttributes()[0].getEntry().getUrl();
        }
        else if ( getSelectedAttributeHierarchies().length > 0 )
        {
            url = getSelectedAttributeHierarchies()[0].getAttribute().getEntry().getUrl();
        }
        else if ( getSelectedValues().length > 0 )
        {
            url = getSelectedValues()[0].getAttribute().getEntry().getUrl();
        }
        else if ( getSelectedSearchResults().length > 0 )
        {
            url = getSelectedSearchResults()[0].getEntry().getUrl();
        }
        else if ( getSelectedBookmarks().length > 0 )
        {
            url = getSelectedBookmarks()[0].getEntry().getUrl();
        }

        if ( url != null )
        {
            CopyAction.copyToClipboard( new Object[]
                { url.toString() }, new Transfer[]
                { TextTransfer.getInstance() } );
        }
    }


    public String getText()
    {
        return "Copy URL";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_URL );
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return getSelectedSearches().length + getSelectedEntries().length + getSelectedSearchResults().length
            + getSelectedBookmarks().length == 1
            || getSelectedAttributes().length + getSelectedValues().length > 0;
    }

}
