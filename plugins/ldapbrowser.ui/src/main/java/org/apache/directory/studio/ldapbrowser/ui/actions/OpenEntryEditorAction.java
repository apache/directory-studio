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


import org.apache.directory.studio.entryeditors.EntryEditorManager;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This class implements the Open Entry Editor Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenEntryEditorAction extends BrowserAction
{

    /**
     * Creates a new instance of OpenEntryEditorAction.
     */
    public OpenEntryEditorAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        EntryEditorManager entryEditorManager = BrowserUIPlugin.getDefault().getEntryEditorManager();
        entryEditorManager.openEntryEditor( getSelectedEntries(), getSelectedSearchResults(), getSelectedBookmarks() );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( getSelectedSearchResults().length == 1
            && getSelectedBookmarks().length + getSelectedEntries().length + getSelectedBrowserViewCategories().length == 0 )
        {
            return Messages.getString( "OpenEntryEditorAction.OpenSearchResult" ); //$NON-NLS-1$
        }
        else if ( getSelectedBookmarks().length == 1
            && getSelectedSearchResults().length + getSelectedEntries().length
                + getSelectedBrowserViewCategories().length == 0 )
        {
            return Messages.getString( "OpenEntryEditorAction.OpenBookmark" ); //$NON-NLS-1$
        }
        else
        {
            return Messages.getString( "OpenEntryEditorAction.OpenEntry" ); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
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
        return getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length == 1
            || getSelectedAttributes().length + getSelectedValues().length > 0;
    }
}
