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


public class SearchResultEditorFilter extends ViewerFilter
{

    protected SearchResultEditorContentProvider contentProvider;

    protected String quickFilterValue;

    private boolean showDn;


    public SearchResultEditorFilter()
    {
        this.quickFilterValue = "";
    }


    public void connect( SearchResultEditorContentProvider contentProvider )
    {
        this.contentProvider = contentProvider;
    }


    public void inputChanged( ISearch newSearch, boolean showDn )
    {
        this.showDn = showDn;
    }


    public boolean isFiltered()
    {
        return this.quickFilterValue != null && !"".equals( quickFilterValue );
    }


    public Object[] filter( Viewer viewer, Object parent, Object[] elements )
    {

        if ( isFiltered() )
        {
            int size = elements.length;
            ArrayList out = new ArrayList( size );
            for ( int i = 0; i < size; ++i )
            {
                Object element = elements[i];
                if ( select( viewer, parent, element ) )
                    out.add( element );
            }

            return out.toArray();
        }
        else
        {
            return elements;
        }

    }


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

            // IAttribute[] attributes = searchResult.getAttributes();
            // for (int i = 0; i < attributes.length; i++) {
            // IValue[] values = attributes[i].getValues();
            // for (int k = 0; k < values.length; k++) {
            // if (this.goesThroughQuickFilter(values[k])) {
            // return true;
            // }
            // }
            // }
            if ( this.showDn
                && searchResult.getDn().getUpName().toUpperCase().indexOf( this.quickFilterValue.toUpperCase() ) > -1 )
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


    private boolean goesThroughQuickFilter( IValue value )
    {

        // fitler value
        // if(this.quickFilterValue != null &&
        // !"".equals(this.quickFilterValue)) {
        if ( value.isString()
            && value.getStringValue().toUpperCase().indexOf( this.quickFilterValue.toUpperCase() ) == -1 )
        {
            return false;
        }
        else if ( value.isBinary() )
        {
            return false;
        }
        // }

        return true;
    }


    public void dispose()
    {
        this.contentProvider = null;
    }


    public String getQuickFilterValue()
    {
        return quickFilterValue;
    }


    public void setQuickFilterValue( String quickFilterValue )
    {
        if ( !this.quickFilterValue.equals( quickFilterValue ) )
        {
            this.quickFilterValue = quickFilterValue;
            if ( this.contentProvider != null )
                this.contentProvider.refresh();
        }
    }

}
