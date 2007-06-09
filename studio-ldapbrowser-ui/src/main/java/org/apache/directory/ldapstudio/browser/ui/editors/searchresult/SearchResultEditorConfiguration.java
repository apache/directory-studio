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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.ldapstudio.valueeditors.ValueEditorManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPart;


public class SearchResultEditorConfiguration
{

    private boolean disposed = false;

    protected SearchResultEditorCursor cursor;

    protected SearchResultEditorSorter sorter;

    protected SearchResultEditorFilter filter;

    protected SearchResultEditorContentProvider contentProvider;

    protected SearchResultEditorLabelProvider labelProvider;

    protected SearchResultEditorCellModifier cellModifier;

    protected ValueEditorManager valueEditorManager;

    protected MenuManager contextMenuManager;


    public SearchResultEditorConfiguration( IWorkbenchPart part )
    {
        super();
    }


    public void dispose()
    {
        if ( !this.disposed )
        {

            if ( this.contentProvider != null )
                this.contentProvider.dispose();
            this.contentProvider = null;

            if ( this.labelProvider != null )
                this.labelProvider.dispose();
            this.labelProvider = null;

            if ( this.cellModifier != null )
                this.cellModifier.dispose();
            this.cellModifier = null;

            if ( this.valueEditorManager != null )
                this.valueEditorManager.dispose();
            this.valueEditorManager = null;

            if ( this.contextMenuManager != null )
                this.contextMenuManager.dispose();
            this.contextMenuManager = null;

            if ( this.cursor != null )
                this.cursor.dispose();
            this.cursor = null;

            this.disposed = true;
        }
    }


    public SearchResultEditorSorter getSorter()
    {
        if ( this.sorter == null )
            this.sorter = new SearchResultEditorSorter();

        return sorter;
    }


    public SearchResultEditorFilter getFilter()
    {
        if ( this.filter == null )
            this.filter = new SearchResultEditorFilter();

        return filter;
    }


    public IMenuManager getContextMenuManager( TableViewer viewer )
    {
        if ( this.contextMenuManager == null )
        {
            this.contextMenuManager = new MenuManager();
            Menu menu = this.contextMenuManager.createContextMenu( viewer.getControl() );
            getCursor( viewer ).setMenu( menu );
        }
        return this.contextMenuManager;
    }


    public SearchResultEditorContentProvider getContentProvider( SearchResultEditorWidget mainWidget )
    {
        if ( this.contentProvider == null )
            this.contentProvider = new SearchResultEditorContentProvider( mainWidget, this );

        return contentProvider;
    }


    public SearchResultEditorLabelProvider getLabelProvider( TableViewer viewer )
    {
        if ( this.labelProvider == null )
            this.labelProvider = new SearchResultEditorLabelProvider( viewer, this.getValueEditorManager( viewer ) );

        return labelProvider;
    }


    public SearchResultEditorCellModifier getCellModifier( TableViewer viewer )
    {
        if ( this.cellModifier == null )
            this.cellModifier = new SearchResultEditorCellModifier( viewer, this.getValueEditorManager( viewer ) );

        return cellModifier;
    }


    public SearchResultEditorCursor getCursor( TableViewer viewer )
    {
        if ( this.cursor == null )
            this.cursor = new SearchResultEditorCursor( viewer );

        return cursor;
    }


    public ValueEditorManager getValueEditorManager( TableViewer viewer )
    {
        if ( this.valueEditorManager == null )
            this.valueEditorManager = new ValueEditorManager( viewer.getTable() );

        return valueEditorManager;
    }

}
