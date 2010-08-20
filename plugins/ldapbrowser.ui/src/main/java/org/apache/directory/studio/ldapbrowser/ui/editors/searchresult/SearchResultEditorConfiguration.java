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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPart;


/**
 * The SearchResultEditorConfiguration contains the content provider, 
 * label provider, cursor, sorter, filter, the context menu manager and the
 * preferences for the search result editor. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEditorConfiguration
{

    /** The disposed flag. */
    private boolean disposed = false;

    /** The cursor. */
    protected SearchResultEditorCursor cursor;

    protected SearchResultEditorSorter sorter;

    protected SearchResultEditorFilter filter;

    protected SearchResultEditorContentProvider contentProvider;

    protected SearchResultEditorLabelProvider labelProvider;

    protected SearchResultEditorCellModifier cellModifier;

    protected ValueEditorManager valueEditorManager;

    protected MenuManager contextMenuManager;


    /**
     * Creates a new instance of SearchResultEditorConfiguration.
     * 
     * @param part the workbench part
     */
    public SearchResultEditorConfiguration( IWorkbenchPart part )
    {
        super();
    }


    /**
     * Disposes this configuration.
     */
    public void dispose()
    {
        if ( !disposed )
        {
            if ( contentProvider != null )
            {
                contentProvider.dispose();
                contentProvider = null;
            }
            if ( labelProvider != null )
            {
                labelProvider.dispose();
                labelProvider = null;
            }
            if ( cellModifier != null )
            {
                cellModifier.dispose();
                cellModifier = null;
            }
            if ( valueEditorManager != null )
            {
                valueEditorManager.dispose();
                valueEditorManager = null;
            }
            if ( contextMenuManager != null )
            {
                contextMenuManager.dispose();
                contextMenuManager = null;
            }
            if ( cursor != null )
            {
//                cursor.dispose();
//                cursor = null;
            }
            disposed = true;
        }
    }


    /**
     * Gets the sorter.
     * 
     * @return the sorter
     */
    public SearchResultEditorSorter getSorter()
    {
        if ( sorter == null )
        {
            sorter = new SearchResultEditorSorter();
        }
        return sorter;
    }


    /**
     * Gets the filter.
     * 
     * @return the filter
     */
    public SearchResultEditorFilter getFilter()
    {
        if ( filter == null )
        {
            filter = new SearchResultEditorFilter();
        }
        return filter;
    }


    /**
     * Gets the context menu manager.
     * 
     * @param viewer the viewer
     * 
     * @return the context menu manager
     */
    public IMenuManager getContextMenuManager( TableViewer viewer )
    {
        if ( contextMenuManager == null )
        {
            contextMenuManager = new MenuManager();
            Menu menu = contextMenuManager.createContextMenu( viewer.getControl() );
//            getCursor( viewer ).setMenu( menu );
        }
        return contextMenuManager;
    }


    /**
     * Gets the content provider.
     * 
     * @param mainWidget the main widget
     * 
     * @return the content provider
     */
    public SearchResultEditorContentProvider getContentProvider( SearchResultEditorWidget mainWidget )
    {
        if ( contentProvider == null )
        {
            contentProvider = new SearchResultEditorContentProvider( mainWidget, this );
        }
        return contentProvider;
    }


    /**
     * Gets the label provider.
     * 
     * @param viewer the viewer
     * 
     * @return the label provider
     */
    public SearchResultEditorLabelProvider getLabelProvider( TableViewer viewer )
    {
        if ( labelProvider == null )
        {
            labelProvider = new SearchResultEditorLabelProvider( getValueEditorManager( viewer ) );
        }
        return labelProvider;
    }


    /**
     * Gets the cell modifier.
     * 
     * @param viewer the viewer
     * 
     * @return the cell modifier
     */
    public SearchResultEditorCellModifier getCellModifier( TableViewer viewer )
    {
        if ( cellModifier == null )
        {
            cellModifier = new SearchResultEditorCellModifier( getValueEditorManager( viewer ), getCursor( viewer ) );
        }
        return cellModifier;
    }


    /**
     * Gets the cursor.
     * 
     * @param viewer the viewer
     * 
     * @return the cursor
     */
    public SearchResultEditorCursor getCursor( TableViewer viewer )
    {
//        if ( cursor == null )
//        {
//            cursor = new SearchResultEditorCursor( viewer );
//        }
        return cursor;
    }


    /**
     * Gets the value editor manager.
     * 
     * @param viewer the viewer
     * 
     * @return the value editor manager
     */
    public ValueEditorManager getValueEditorManager( TableViewer viewer )
    {
        if ( valueEditorManager == null )
        {
            valueEditorManager = new ValueEditorManager( viewer.getTable(), true, true );
        }
        return valueEditorManager;
    }

}
