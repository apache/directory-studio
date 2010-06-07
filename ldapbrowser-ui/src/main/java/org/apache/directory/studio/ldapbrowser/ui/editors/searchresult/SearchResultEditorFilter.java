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


import java.util.ArrayList;

import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * The SearchResultEditorFilter implements the filter for the search result editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEditorFilter extends ViewerFilter
{

    /** The content provider. */
    protected SearchResultEditorContentProvider contentProvider;

    /** The quick filter value. */
    protected String quickFilterValue;

    /** The show DN flag. */
    private boolean showDn;


    /**
     * Creates a new instance of SearchResultEditorFilter.
     */
    public SearchResultEditorFilter()
    {
        this.quickFilterValue = ""; //$NON-NLS-1$
    }


    /**
     * Connects this filter with the given content provider.
     * 
     * @param viewer the viewer
     */
    public void connect( SearchResultEditorContentProvider contentProvider )
    {
        this.contentProvider = contentProvider;
    }


    /**
     * Called when the input of the viewer changes.
     * 
     * @param newSearch the new search
     * @param showDn the show DN flag
     */
    public void inputChanged( ISearch newSearch, boolean showDn )
    {
        this.showDn = showDn;
    }


    /**
     * Checks if is filtered.
     * 
     * @return true, if is filtered
     */
    public boolean isFiltered()
    {
        return quickFilterValue != null && !"".equals( quickFilterValue ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public Object[] filter( Viewer viewer, Object parent, Object[] elements )
    {
        if ( isFiltered() )
        {
            int size = elements.length;
            ArrayList<Object> out = new ArrayList<Object>( size );
            for ( int i = 0; i < size; ++i )
            {
                Object element = elements[i];
                if ( select( viewer, parent, element ) )
                {
                    out.add( element );
                }
            }

            return out.toArray();
        }
        else
        {
            return elements;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean select( Viewer viewer, Object parentElement, Object element )
    {
        if ( element instanceof ISearchResult )
        {
            ISearchResult searchResult = ( ISearchResult ) element;

            String[] returningAttributes = searchResult.getSearch().getReturningAttributes();
            for ( int r = 0; r < returningAttributes.length; r++ )
            {
                String ra = returningAttributes[r];
                AttributeHierarchy ah = searchResult.getAttributeWithSubtypes( ra );
                if ( ah != null )
                {
                    IAttribute[] attributes = ah.getAttributes();
                    for ( int i = 0; i < attributes.length; i++ )
                    {
                        IValue[] values = attributes[i].getValues();
                        for ( int k = 0; k < values.length; k++ )
                        {
                            if ( this.goesThroughQuickFilter( values[k] ) )
                            {
                                return true;
                            }
                        }
                    }
                }
            }

            if ( showDn
                && searchResult.getDn().getUpName().toUpperCase().indexOf( quickFilterValue.toUpperCase() ) > -1 )
            {
                return true;
            }

            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     * Checks if the value goes through quick filter.
     * 
     * @param value the value
     * 
     * @return true, if successful
     */
    private boolean goesThroughQuickFilter( IValue value )
    {
        if ( value.isString() && value.getStringValue().toUpperCase().indexOf( quickFilterValue.toUpperCase() ) == -1 )
        {
            return false;
        }
        else if ( value.isBinary() )
        {
            return false;
        }

        return true;
    }


    /**
     * Disposes this filter.
     */
    public void dispose()
    {
        contentProvider = null;
    }


    /**
     * Gets the quick filter value.
     * 
     * @return the quick filter value
     */
    public String getQuickFilterValue()
    {
        return quickFilterValue;
    }


    /**
     * Sets the quick filter value.
     * 
     * @param quickFilterValue the new quick filter value
     */
    public void setQuickFilterValue( String quickFilterValue )
    {
        if ( !this.quickFilterValue.equals( quickFilterValue ) )
        {
            this.quickFilterValue = quickFilterValue;
            if ( contentProvider != null )
            {
                contentProvider.refresh();
            }
        }
    }

}
