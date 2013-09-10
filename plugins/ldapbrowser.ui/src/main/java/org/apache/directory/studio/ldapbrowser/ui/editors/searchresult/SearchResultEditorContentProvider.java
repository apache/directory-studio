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


import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * The SearchResultEditorContentProvider implements the content provider for
 * the search resutl editor. It accepts an {@link ISearch} as input.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEditorContentProvider implements ILazyContentProvider
{

    /** The main widget. */
    private SearchResultEditorWidget mainWidget;

    /** The configuration. */
    private SearchResultEditorConfiguration configuration;

    /** The input. */
    private Object input;

    /** The elements. */
    private Object[] elements;

    /** The filtered and sorted elements. */
    private Object[] filteredAndSortedElements;


    /**
     * Creates a new instance of SearchResultEditorContentProvider.
     * 
     * @param mainWidget the main widget
     * @param configuration the configuration
     */
    public SearchResultEditorContentProvider( SearchResultEditorWidget mainWidget,
        SearchResultEditorConfiguration configuration )
    {
        this.mainWidget = mainWidget;
        this.configuration = configuration;

        this.configuration.getFilter().connect( this );
        this.configuration.getSorter().connect( this );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        mainWidget = null;
        configuration = null;
        elements = null;
        filteredAndSortedElements = null;
    }


    /**
     * Refreshes the viewer.
     */
    public void refresh()
    {
        filterAndSort();
        mainWidget.getViewer().refresh();
    }


    /**
     * Filters and sorts the viewer.
     */
    private void filterAndSort()
    {
        filteredAndSortedElements = elements;

        // filter and sort, use Job if too much elements
        if ( configuration.getFilter().isFiltered() || configuration.getSorter().isSorted() )
        {
            if ( elements.length > BrowserUIPlugin.getDefault().getPreferenceStore()
                .getInt( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SORT_FILTER_LIMIT )
                && mainWidget.getViewer() != null && !mainWidget.getViewer().getTable().isDisposed() )
            {
                // deactivate fitering and sorting for large data set
                // FilterAndSortRunnable runnable = new FilterAndSortRunnable( configuration, mainWidget, elements );
                // RunnableContextRunner.execute( runnable, null, true );
                // filteredAndSortedElements = runnable.getFilteredAndSortedElements();
            }
            else if ( elements.length > 0 && mainWidget.getViewer() != null
                && !mainWidget.getViewer().getTable().isDisposed() )
            {
                filteredAndSortedElements = configuration.getFilter().filter( mainWidget.getViewer(), "", elements ); //$NON-NLS-1$
                configuration.getSorter().sort( mainWidget.getViewer(), filteredAndSortedElements );
            }
        }

        // update virtual table
        mainWidget.getViewer().setItemCount( filteredAndSortedElements.length );

        // update state
        String url = ""; //$NON-NLS-1$
        boolean enabled = true;
        if ( input != null && input instanceof ISearch )
        {
            ISearch search = ( ISearch ) input;

            if ( filteredAndSortedElements.length < elements.length )
            {
                url += filteredAndSortedElements.length + Messages.getString( "SearchResultEditorContentProvider.Of" ); //$NON-NLS-1$
            }

            if ( search.getSearchResults() == null )
            {
                url += Messages.getString( "SearchResultEditorContentProvider.SearchNotPerformed" ); //$NON-NLS-1$
                enabled = false;
            }
            else if ( search.getSearchResults().length == 1 )
            {
                url += search.getSearchResults().length
                    + Messages.getString( "SearchResultEditorContentProvider.Result" ); //$NON-NLS-1$
            }
            else
            {
                url += search.getSearchResults().length
                    + Messages.getString( "SearchResultEditorContentProvider.Results" ); //$NON-NLS-1$
            }

            // url += search.getURL();
            url += Messages.getString( "SearchResultEditorContentProvider.SearchBase" ) + search.getSearchBase().getName() + "  -  "; //$NON-NLS-1$ //$NON-NLS-2$
            url += Messages.getString( "SearchResultEditorContentProvider.Filter" ) + search.getFilter(); //$NON-NLS-1$

            boolean showDn = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN )
                || search.getReturningAttributes().length == 0;
            configuration.getFilter().inputChanged( search, showDn );
            configuration.getSorter().inputChanged( search, showDn );
        }
        else
        {
            url = Messages.getString( "SearchResultEditorContentProvider.NoSearchSelected" ); //$NON-NLS-1$
            enabled = false;
        }

        if ( mainWidget.getInfoText() != null && !mainWidget.getInfoText().isDisposed() )
        {
            mainWidget.getInfoText().setText( url );
        }
        if ( mainWidget.getQuickFilterWidget() != null )
        {
            mainWidget.getQuickFilterWidget().setEnabled( enabled );
        }
        if ( mainWidget.getViewer() != null && !mainWidget.getViewer().getTable().isDisposed() )
        {
            mainWidget.getViewer().getTable().setEnabled( enabled );
        }

    }


    /**
     * {@inheritDoc}
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        this.input = newInput;
        this.elements = getElements( newInput );
    }


    /**
     * Gets the elements.
     * 
     * @param inputElement the input element
     * 
     * @return the elements
     */
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
        }
    }


    /**
     * Gets the viewer.
     * 
     * @return the viewer
     */
    public TableViewer getViewer()
    {
        return mainWidget.getViewer();
    }


    /**
     * {@inheritDoc}
     */
    public void updateElement( int index )
    {
        if ( filteredAndSortedElements != null && filteredAndSortedElements.length > 0
            && index < filteredAndSortedElements.length )
        {
            mainWidget.getViewer().replace( filteredAndSortedElements[index], index );
        }
    }

}
