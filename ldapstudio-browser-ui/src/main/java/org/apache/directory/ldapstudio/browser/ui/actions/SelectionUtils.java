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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.internal.model.Search;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.utils.LdapFilterUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserCategory;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserEntryPage;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserSearchResultPage;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;


/**
 * TODO DOCUMENT ME! SelectionUtils.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class SelectionUtils
{
    /**
     * TODO DOCUMENT ME! getExampleSearch.
     *
     * @param selection
     * @return
     */
    public static ISearch getExampleSearch( ISelection selection )
    {

        ISearch exampleSearch = new Search();
        String oldName = exampleSearch.getSearchParameter().getName();
        exampleSearch.getSearchParameter().setName( null );
        exampleSearch.setScope( ISearch.SCOPE_SUBTREE );

        if ( selection != null && !selection.isEmpty() && selection instanceof StructuredSelection )
        {

            Object[] objects = ( ( IStructuredSelection ) selection ).toArray();
            Comparator comparator = new Comparator()
            {
                public int compare( Object o1, Object o2 )
                {
                    if ( ( o1 instanceof IValue ) && !( o2 instanceof IValue ) )
                    {
                        return -1;
                    }
                    else if ( !( o1 instanceof IValue ) && ( o2 instanceof IValue ) )
                    {
                        return 1;
                    }
                    else if ( ( o1 instanceof IAttribute ) && !( o2 instanceof IAttribute ) )
                    {
                        return -1;
                    }
                    else if ( !( o1 instanceof IAttribute ) && ( o2 instanceof IAttribute ) )
                    {
                        return 1;
                    }
                    else if ( ( o1 instanceof AttributeHierarchy ) && !( o2 instanceof AttributeHierarchy ) )
                    {
                        return -1;
                    }
                    else if ( !( o1 instanceof AttributeHierarchy ) && ( o2 instanceof AttributeHierarchy ) )
                    {
                        return 1;
                    }
                    return 0;
                }
            };
            Arrays.sort( objects, comparator );
            Object obj = objects[0];

            if ( obj instanceof ISearch )
            {
                ISearch search = ( ISearch ) obj;
                exampleSearch = ( ISearch ) search.clone();
                exampleSearch.setName( null );
            }
            else if ( obj instanceof IEntry )
            {
                IEntry entry = ( IEntry ) obj;
                exampleSearch.setConnection( entry.getConnection() );
                exampleSearch.setSearchBase( entry.getDn() );
            }
            else if ( obj instanceof ISearchResult )
            {
                ISearchResult searchResult = ( ISearchResult ) obj;
                exampleSearch.setConnection( searchResult.getEntry().getConnection() );
                exampleSearch.setSearchBase( searchResult.getEntry().getDn() );
            }
            else if ( obj instanceof IBookmark )
            {
                IBookmark bookmark = ( IBookmark ) obj;
                exampleSearch.setConnection( bookmark.getConnection() );
                exampleSearch.setSearchBase( bookmark.getDn() );
            }

            else if ( obj instanceof AttributeHierarchy || obj instanceof IAttribute || obj instanceof IValue )
            {

                IEntry entry = null;
                Set filterSet = new LinkedHashSet();
                for ( int i = 0; i < objects.length; i++ )
                {
                    Object object = objects[i];
                    if ( object instanceof AttributeHierarchy )
                    {
                        AttributeHierarchy ah = ( AttributeHierarchy ) object;
                        for ( Iterator it = ah.iterator(); it.hasNext(); )
                        {
                            IAttribute attribute = ( IAttribute ) it.next();
                            entry = attribute.getEntry();
                            IValue[] values = attribute.getValues();
                            for ( int v = 0; v < values.length; v++ )
                            {
                                filterSet.add( LdapFilterUtils.getFilter( values[v] ) );
                            }
                        }
                    }
                    else if ( object instanceof IAttribute )
                    {
                        IAttribute attribute = ( IAttribute ) object;
                        entry = attribute.getEntry();
                        IValue[] values = attribute.getValues();
                        for ( int v = 0; v < values.length; v++ )
                        {
                            filterSet.add( LdapFilterUtils.getFilter( values[v] ) );
                        }
                    }
                    else if ( object instanceof IValue )
                    {
                        IValue value = ( IValue ) object;
                        entry = value.getAttribute().getEntry();
                        filterSet.add( LdapFilterUtils.getFilter( value ) );
                    }
                }

                exampleSearch.setConnection( entry.getConnection() );
                exampleSearch.setSearchBase( entry.getDn() );
                StringBuffer filter = new StringBuffer();
                if ( filterSet.size() > 1 )
                {
                    filter.append( "(&" );
                    for ( Iterator filterIterator = filterSet.iterator(); filterIterator.hasNext(); )
                    {
                        filter.append( filterIterator.next() );
                    }
                    filter.append( ")" );
                }
                else if ( filterSet.size() == 1 )
                {
                    filter.append( filterSet.toArray()[0] );
                }
                else
                {
                    filter.append( ISearch.FILTER_TRUE );
                }
                exampleSearch.setFilter( filter.toString() );
            }

            else if ( obj instanceof IConnection )
            {
                IConnection connection = ( IConnection ) obj;
                exampleSearch.setConnection( connection );
                if ( connection.getBaseDNEntries().length > 0 )
                {
                    exampleSearch.setSearchBase( connection.getBaseDNEntries()[0].getDn() );
                }
            }
            else if ( obj instanceof BrowserCategory )
            {
                BrowserCategory cat = ( BrowserCategory ) obj;
                exampleSearch.setConnection( cat.getParent() );
                if ( cat.getParent().getBaseDNEntries().length > 0 )
                {
                    exampleSearch.setSearchBase( cat.getParent().getBaseDNEntries()[0].getDn() );
                }
            }

        }

        exampleSearch.getSearchParameter().setName( oldName );
        return exampleSearch;
    }


    /**
     * TODO DOCUMENT ME! getBrowserViewCategories.
     *
     * @param selection
     * @return
     */
    public static BrowserCategory[] getBrowserViewCategories( ISelection selection )
    {
        List list = getTypes( selection, BrowserCategory.class );
        return ( BrowserCategory[] ) list.toArray( new BrowserCategory[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getValues.
     *
     * @param selection
     * @return
     */
    public static IValue[] getValues( ISelection selection )
    {
        List list = getTypes( selection, IValue.class );
        return ( IValue[] ) list.toArray( new IValue[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getAttributes.
     *
     * @param selection
     * @return
     */
    public static IAttribute[] getAttributes( ISelection selection )
    {
        List list = getTypes( selection, IAttribute.class );
        return ( IAttribute[] ) list.toArray( new IAttribute[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getAttributeHierarchie.
     *
     * @param selection
     * @return
     */
    public static AttributeHierarchy[] getAttributeHierarchie( ISelection selection )
    {
        List list = getTypes( selection, AttributeHierarchy.class );
        return ( AttributeHierarchy[] ) list.toArray( new AttributeHierarchy[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getProperties.
     *
     * @param selection
     * @return
     */
    public static String[] getProperties( ISelection selection )
    {
        List list = getTypes( selection, String.class );
        return ( String[] ) list.toArray( new String[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getAttributeTypeDescription.
     *
     * @param selection
     * @return
     */
    public static AttributeTypeDescription[] getAttributeTypeDescription( ISelection selection )
    {
        List list = getTypes( selection, AttributeTypeDescription.class );
        return ( AttributeTypeDescription[] ) list.toArray( new AttributeTypeDescription[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getEntries.
     *
     * @param selection
     * @return
     */
    public static IEntry[] getEntries( ISelection selection )
    {
        List list = getTypes( selection, IEntry.class );
        return ( IEntry[] ) list.toArray( new IEntry[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getBookmarks.
     *
     * @param selection
     * @return
     */
    public static IBookmark[] getBookmarks( ISelection selection )
    {
        List list = getTypes( selection, IBookmark.class );
        return ( IBookmark[] ) list.toArray( new IBookmark[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getSearchResults.
     *
     * @param selection
     * @return
     */
    public static ISearchResult[] getSearchResults( ISelection selection )
    {
        List list = getTypes( selection, ISearchResult.class );
        return ( ISearchResult[] ) list.toArray( new ISearchResult[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getTypes.
     *
     * @param selection
     * @param type
     * @return
     */
    private static List getTypes( ISelection selection, Class type )
    {
        List list = new ArrayList();
        if ( selection instanceof IStructuredSelection )
        {
            IStructuredSelection structuredSelection = ( IStructuredSelection ) selection;
            Iterator it = structuredSelection.iterator();
            while ( it.hasNext() )
            {
                Object o = it.next();
                if ( type.isInstance( o ) )
                {
                    list.add( o );
                }
            }
        }
        return list;
    }


    /**
     * TODO DOCUMENT ME! getSearches.
     *
     * @param selection
     * @return
     */
    public static ISearch[] getSearches( ISelection selection )
    {
        List list = getTypes( selection, ISearch.class );
        return ( ISearch[] ) list.toArray( new ISearch[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getConnections.
     *
     * @param selection
     * @return
     */
    public static IConnection[] getConnections( ISelection selection )
    {
        List list = getTypes( selection, IConnection.class );
        return ( IConnection[] ) list.toArray( new IConnection[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getBrowserEntryPages.
     *
     * @param selection
     * @return
     */
    public static BrowserEntryPage[] getBrowserEntryPages( ISelection selection )
    {
        List list = getTypes( selection, BrowserEntryPage.class );
        return ( BrowserEntryPage[] ) list.toArray( new BrowserEntryPage[list.size()] );
    }


    /**
     * TODO DOCUMENT ME! getBrowserSearchResultPages.
     *
     * @param selection
     * @return
     */
    public static BrowserSearchResultPage[] getBrowserSearchResultPages( ISelection selection )
    {
        List list = getTypes( selection, BrowserSearchResultPage.class );
        return ( BrowserSearchResultPage[] ) list.toArray( new BrowserSearchResultPage[list.size()] );
    }

}
