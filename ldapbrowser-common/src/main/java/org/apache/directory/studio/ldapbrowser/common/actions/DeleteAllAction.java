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

package org.apache.directory.studio.ldapbrowser.common.actions;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserCategory;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action deletes all the Searches or Bookmarks.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DeleteAllAction extends DeleteAction
{
    private static final Collection<IEntry> EMPTY_ENTRIES = new HashSet<IEntry>();
    private static final ISearch[] EMPTY_SEARCHES = new ISearch[0];
    private static final IBookmark[] EMPTY_BOOKMARKS = new IBookmark[0];


    /**
     * Creates a new instance of DeleteAllAction.
     */
    public DeleteAllAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        super.run();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( getSelectedEntries().length >= 1 )
        {
            return Messages.getString( "DeleteAllAction.DeleteAllChildEntries" ); //$NON-NLS-1$
        }
        else if ( ( getSelectedSearches().length >= 1 )
            || ( ( getSelectedBrowserViewCategories().length == 1 ) && ( getSelectedBrowserViewCategories()[0]
                .getType() == BrowserCategory.TYPE_SEARCHES ) ) )
        {
            return Messages.getString( "DeleteAllAction.DeleteAllSearches" ); //$NON-NLS-1$
        }
        else if ( ( getSelectedBookmarks().length >= 1 )
            || ( ( getSelectedBrowserViewCategories().length == 1 ) && ( getSelectedBrowserViewCategories()[0]
                .getType() == BrowserCategory.TYPE_BOOKMARKS ) ) )
        {
            return Messages.getString( "DeleteAllAction.DeleteAllBookmarks" ); //$NON-NLS-1$
        }
        else
        {
            return Messages.getString( "DeleteAllAction.DeleteAll" ); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_DELETE_ALL );
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
    protected Collection<IEntry> getEntries()
    {
        if ( getSelectedEntries().length >= 1 )
        {
            Collection<IEntry> values = new HashSet<IEntry>();
            values.addAll( Arrays.asList( getSelectedEntries()[0].getChildren() ) );
            return values;
        }
        else
        {
            return EMPTY_ENTRIES;
        }
    }


    /**
     * {@inheritDoc}
     */
    protected ISearch[] getSearches()
    {
        if ( getSelectedSearches().length >= 1 )
        {
            return getSelectedSearches()[0].getBrowserConnection().getSearchManager().getSearches().toArray(
                new ISearch[0] );
        }
        else if ( ( getSelectedBrowserViewCategories().length == 1 )
            && ( getSelectedBrowserViewCategories()[0].getType() == BrowserCategory.TYPE_SEARCHES ) )
        {
            return getSelectedBrowserViewCategories()[0].getParent().getSearchManager().getSearches().toArray(
                new ISearch[0] );
        }
        else
        {
            return EMPTY_SEARCHES;
        }
    }


    /**
     * {@inheritDoc}
     */
    protected IBookmark[] getBookmarks()
    {
        if ( getSelectedBookmarks().length >= 1 )
        {
            return getSelectedBookmarks()[0].getBrowserConnection().getBookmarkManager().getBookmarks();
        }
        else if ( ( getSelectedBrowserViewCategories().length == 1 )
            && ( getSelectedBrowserViewCategories()[0].getType() == BrowserCategory.TYPE_BOOKMARKS ) )
        {
            return getSelectedBrowserViewCategories()[0].getParent().getBookmarkManager().getBookmarks();
        }
        else
        {
            return EMPTY_BOOKMARKS;
        }
    }
}
