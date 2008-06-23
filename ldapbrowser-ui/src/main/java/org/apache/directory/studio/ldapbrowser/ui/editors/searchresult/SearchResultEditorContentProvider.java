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


import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;


public class SearchResultEditorContentProvider implements ILazyContentProvider
{

    private SearchResultEditorWidget mainWidget;

    private SearchResultEditorConfiguration configuration;

    private Object input;

    private Object[] elements;

    private Object[] filteredAndSortedElements;


    public SearchResultEditorContentProvider( SearchResultEditorWidget mainWidget,
        SearchResultEditorConfiguration configuration )
    {
        this.mainWidget = mainWidget;
        this.configuration = configuration;

        this.configuration.getFilter().connect( this );
        this.configuration.getSorter().connect( this );
    }


    public void dispose()
    {
        this.mainWidget = null;
        this.configuration = null;
        this.elements = null;
        this.filteredAndSortedElements = null;
    }


    public void refresh()
    {
        this.filterAndSort();
        this.mainWidget.getViewer().refresh();
    }


    private void filterAndSort()
    {

        this.filteredAndSortedElements = elements;

        // filter and sort, use Job if too much elements
        if ( this.configuration.getFilter().isFiltered() || this.configuration.getSorter().isSorted() )
        {
            if ( elements.length > 1000 && this.mainWidget.getViewer() != null
                && !this.mainWidget.getViewer().getTable().isDisposed() )
            {
                FilterAndSortRunnable runnable = new FilterAndSortRunnable( this.configuration, this.mainWidget, this.elements );
                //RunnableContextJobAdapter.execute( job, new TimeTriggeredProgressMonitorDialog( Display.getCurrent()
                //    .getActiveShell(), 5000 ) );
                RunnableContextRunner.execute( runnable, null, true );
                this.filteredAndSortedElements = runnable.getFilteredAndSortedElements();
            }
            else if ( elements.length > 0 && this.mainWidget.getViewer() != null
                && !this.mainWidget.getViewer().getTable().isDisposed() )
            {
                this.filteredAndSortedElements = this.configuration.getFilter().filter( this.mainWidget.getViewer(),
                    "", elements );
                this.configuration.getSorter().sort( this.mainWidget.getViewer(), this.filteredAndSortedElements );
            }
        }

        // update virtual table
        this.mainWidget.getViewer().setItemCount( this.filteredAndSortedElements.length );

        // update state
        String url = "";
        boolean enabled = true;
        if ( input != null && input instanceof ISearch )
        {
            ISearch search = ( ISearch ) input;

            if ( filteredAndSortedElements.length < elements.length )
            {
                url += filteredAndSortedElements.length + " of ";
            }

            if ( search.getSearchResults() == null )
            {
                url += "Search not performed  -  ";
                enabled = false;
            }
            else if ( search.getSearchResults().length == 1 )
            {
                url += search.getSearchResults().length + " Result  -  ";
            }
            else
            {
                url += search.getSearchResults().length + " Results  -  ";
            }

            // url += search.getURL();
            url += "Search Base: " + search.getSearchBase().getUpName() + "  -  ";
            url += "Filter: " + search.getFilter();

            boolean showDn = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN )
                || search.getReturningAttributes().length == 0;
            this.configuration.getFilter().inputChanged( search, showDn );
            this.configuration.getSorter().inputChanged( search, showDn );
        }
        else
        {
            url = "No search selected";;
            enabled = false;
        }

        if ( this.mainWidget.getInfoText() != null && !this.mainWidget.getInfoText().isDisposed() )
        {
            this.mainWidget.getInfoText().setText( url );
        }
        if ( this.mainWidget.getQuickFilterWidget() != null )
        {
            this.mainWidget.getQuickFilterWidget().setEnabled( enabled );
        }
        if ( this.mainWidget.getViewer() != null && !this.mainWidget.getViewer().getTable().isDisposed() )
        {
            this.mainWidget.getViewer().getTable().setEnabled( enabled );
        }

    }


    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        this.input = newInput;
        this.elements = getElements( newInput );
        // this.filterAndSort();
    }


    public Object[] getElements( Object inputElement )
    {
        if ( inputElement != null && inputElement instanceof ISearch )
        {
            ISearch search = ( ISearch ) inputElement;
            return search.getSearchResults() != null ? search.getSearchResults() : new Object[0];
        }
        else
        {
            return new Object[]
                {};
            // return new Object[]{inputElement};
        }
    }


    public TableViewer getViewer()
    {
        return this.mainWidget.getViewer();
    }


    public void updateElement( int index )
    {
        // if(sortedAndSortedElements instanceof ISearchResult[]) {
        if ( filteredAndSortedElements != null && filteredAndSortedElements.length > 0
            && index < filteredAndSortedElements.length )
        {
            // System.out.println(index);
            mainWidget.getViewer().replace( filteredAndSortedElements[index], index );
        }
    }

}
