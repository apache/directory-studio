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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This action is used to locate the search result or bookmark in the LDAP Browser view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LocateSearchResultOrBookmarkAction extends LocateEntryInLdapBrowserAction
{
    /**
     * Creates a new instance of LocateSearchResultOrBookmarkAction.
     *
     * @param entryEditor the entry editor
     * @param showInMenuManager the menu manager
     */
    public LocateSearchResultOrBookmarkAction( EntryEditor entryEditor, EntryEditorShowInMenuManager showInMenuManager )
    {
        super( entryEditor, showInMenuManager );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        Object input = showInMenuManager.getInput();

        if ( input != null )
        {
            select( input );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        Object input = showInMenuManager.getInput();

        if ( input != null )
        {
            if ( input instanceof ISearchResult )
            {
                return Messages.getString( "LocateSearchResultOrBookmarkAction.Searches" ); //$NON-NLS-1$
            }
            else if ( input instanceof IBookmark )
            {
                return Messages.getString( "LocateSearchResultOrBookmarkAction.Bookmarks" ); //$NON-NLS-1$
            }
        }

        return super.getText();
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        Object input = showInMenuManager.getInput();

        if ( input != null )
        {
            if ( input instanceof ISearchResult )
            {
                return BrowserUIPlugin.getDefault().getImageDescriptor(
                    BrowserUIConstants.IMG_LOCATE_SEARCHRESULT_IN_DIT );
            }
            else if ( input instanceof IBookmark )
            {
                return BrowserUIPlugin.getDefault().getImageDescriptor(
                    BrowserUIConstants.IMG_LOCATE_BOOKMARK_IN_DIT );
            }
        }

        return super.getImageDescriptor();
    }
}
