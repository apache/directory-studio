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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.ui.actions.SelectionUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserCategory;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserEntryPage;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserSearchResultPage;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.core.utils.LdapFilterUtils;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;


/**
 * The SelectionUtils are used to extract specific beans from the current
 * selection (org.eclipse.jface.viewers.ISelection).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class BrowserSelectionUtils extends SelectionUtils
{

    /**
     * This method creates a prototype search from the given selection.
     * 
     * Depended on the selected element it determines the best connection,
     * search base and filter:
     * <ul>
     *   <li>ISearch: all parameters are copied to the prototype search (clone)
     *   <li>IEntry or ISearchResult or IBookmark: Dn is used as search base
     *   <li>IEntry: children filter is used as filter
     *   <li>IAttribute or IValue: the entry's Dn is used as search base,
     *       the filter is built using the name-value-pairs (query by example). 
     * </ul>
     * 
     * @param selection the current selection
     * @return a prototype search
     */
    public static ISearch getExampleSearch( ISelection selection )
    {
        ISearch exampleSearch = new Search();
        String oldName = exampleSearch.getSearchParameter().getName();
        exampleSearch.getSearchParameter().setName( null );
        exampleSearch.setScope( SearchScope.SUBTREE );

        if ( ( selection instanceof StructuredSelection ) && !selection.isEmpty() )
        {
            Object[] objects = ( ( IStructuredSelection ) selection ).toArray();
            Comparator<Object> comparator = new Comparator<Object>()
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
                exampleSearch.setBrowserConnection( entry.getBrowserConnection() );
                exampleSearch.setSearchBase( entry.getDn() );
                exampleSearch.setFilter( entry.getChildrenFilter() );
            }
            else if ( obj instanceof ISearchResult )
            {
                ISearchResult searchResult = ( ISearchResult ) obj;
                exampleSearch.setBrowserConnection( searchResult.getEntry().getBrowserConnection() );
                exampleSearch.setSearchBase( searchResult.getEntry().getDn() );
            }
            else if ( obj instanceof IBookmark )
            {
                IBookmark bookmark = ( IBookmark ) obj;
                exampleSearch.setBrowserConnection( bookmark.getBrowserConnection() );
                exampleSearch.setSearchBase( bookmark.getDn() );
            }

            else if ( obj instanceof AttributeHierarchy || obj instanceof IAttribute || obj instanceof IValue )
            {
                IEntry entry = null;
                Set<String> filterSet = new LinkedHashSet<String>();
                for ( int i = 0; i < objects.length; i++ )
                {
                    Object object = objects[i];
                    if ( object instanceof AttributeHierarchy )
                    {
                        AttributeHierarchy ah = ( AttributeHierarchy ) object;
                        for ( IAttribute attribute : ah )
                        {
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

                exampleSearch.setBrowserConnection( entry.getBrowserConnection() );
                exampleSearch.setSearchBase( entry.getDn() );
                StringBuffer filter = new StringBuffer();
                if ( filterSet.size() > 1 )
                {
                    filter.append( "(&" ); //$NON-NLS-1$
                    for ( Iterator<String> filterIterator = filterSet.iterator(); filterIterator.hasNext(); )
                    {
                        filter.append( filterIterator.next() );
                    }
                    filter.append( ")" ); //$NON-NLS-1$
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
            else if ( obj instanceof Connection )
            {
                IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager()
                    .getBrowserConnection( ( Connection ) obj );
                exampleSearch.setBrowserConnection( connection );
                if ( connection.getRootDSE().getChildrenCount() > 0 )
                {
                    exampleSearch.setSearchBase( connection.getRootDSE().getChildren()[0].getDn() );
                }
                else
                {
                    exampleSearch.setSearchBase( connection.getRootDSE().getDn() );
                }
            }
            else if ( obj instanceof IBrowserConnection )
            {
                IBrowserConnection connection = ( IBrowserConnection ) obj;
                exampleSearch.setBrowserConnection( connection );
                if ( connection.getRootDSE().getChildrenCount() > 0 )
                {
                    exampleSearch.setSearchBase( connection.getRootDSE().getChildren()[0].getDn() );
                }
                else
                {
                    exampleSearch.setSearchBase( connection.getRootDSE().getDn() );
                }
            }
            else if ( obj instanceof BrowserCategory )
            {
                BrowserCategory cat = ( BrowserCategory ) obj;
                exampleSearch.setBrowserConnection( cat.getParent() );
                if ( cat.getParent().getRootDSE().getChildrenCount() > 0 )
                {
                    exampleSearch.setSearchBase( cat.getParent().getRootDSE().getChildren()[0].getDn() );
                }
                else
                {
                    exampleSearch.setSearchBase( cat.getParent().getRootDSE().getDn() );
                }
            }

        }

        exampleSearch.getSearchParameter().setName( oldName );
        return exampleSearch;
    }


    /**
     * Gets the BrowserCategory beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with BrowserCategory beans, may be empty.
     */
    public static BrowserCategory[] getBrowserViewCategories( ISelection selection )
    {
        List<Object> list = getTypes( selection, BrowserCategory.class );
        return list.toArray( new BrowserCategory[list.size()] );
    }


    /**
     * Gets the IValue beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with IValue beans, may be empty.
     */
    public static IValue[] getValues( ISelection selection )
    {
        List<Object> list = getTypes( selection, IValue.class );
        return list.toArray( new IValue[list.size()] );
    }


    /**
     * Gets the IAttribute beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with IAttribute beans, may be empty.
     */
    public static IAttribute[] getAttributes( ISelection selection )
    {
        List<Object> list = getTypes( selection, IAttribute.class );
        return list.toArray( new IAttribute[list.size()] );
    }


    /**
     * Gets the AttributeHierarchy beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with AttributeHierarchy beans, may be empty.
     */
    public static AttributeHierarchy[] getAttributeHierarchie( ISelection selection )
    {
        List<Object> list = getTypes( selection, AttributeHierarchy.class );
        return list.toArray( new AttributeHierarchy[list.size()] );
    }


    /**
     * Gets the Strings contained in the given selection.
     *
     * @param selection the selection
     * @return an array with Strings, may be empty.
     */
    public static String[] getProperties( ISelection selection )
    {
        List<Object> list = getTypes( selection, String.class );
        return list.toArray( new String[list.size()] );
    }


    /**
     * Gets the AttributeTypeDescription beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with AttributeTypeDescription beans, may be empty.
     */
    public static AttributeType[] getAttributeTypeDescription( ISelection selection )
    {
        List<Object> list = getTypes( selection, AttributeType.class );
        return list.toArray( new AttributeType[list.size()] );
    }


    /**
     * Gets the IEntry beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with IEntry beans, may be empty.
     */
    public static IEntry[] getEntries( ISelection selection )
    {
        List<Object> list = getTypes( selection, IEntry.class );
        return list.toArray( new IEntry[list.size()] );
    }


    /**
     * Gets the IBookmark beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with IBookmark beans, may be empty.
     */
    public static IBookmark[] getBookmarks( ISelection selection )
    {
        List<Object> list = getTypes( selection, IBookmark.class );
        return list.toArray( new IBookmark[list.size()] );
    }


    /**
     * Gets the ISearchResult beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with ISearchResult beans, may be empty.
     */
    public static ISearchResult[] getSearchResults( ISelection selection )
    {
        List<Object> list = getTypes( selection, ISearchResult.class );
        return list.toArray( new ISearchResult[list.size()] );
    }


    /**
     * Gets all beans of the requested type contained in the given selection.
     *
     * @param selection the selection
     * @param type the requested type
     * @return a list containing beans of the requested type
     */
    private static List<Object> getTypes( ISelection selection, Class<?> type )
    {
        List<Object> list = new ArrayList<Object>();
        
        if ( selection instanceof IStructuredSelection )
        {
            IStructuredSelection structuredSelection = ( IStructuredSelection ) selection;
            
            for ( Object element : structuredSelection.toArray() )
            {
                if ( type.isInstance( element ) )
                {
                    list.add( element );
                }
            }
        }
        
        return list;
    }


    /**
     * Gets the ISearch beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with ISearch beans, may be empty.
     */
    public static ISearch[] getSearches( ISelection selection )
    {
        List<Object> list = getTypes( selection, ISearch.class );
        return list.toArray( new ISearch[list.size()] );
    }


    /**
     * Gets the BrowserEntryPage beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with BrowserEntryPage beans, may be empty.
     */
    public static BrowserEntryPage[] getBrowserEntryPages( ISelection selection )
    {
        List<Object> list = getTypes( selection, BrowserEntryPage.class );
        return list.toArray( new BrowserEntryPage[list.size()] );
    }


    /**
     * Gets the BrowserSearchResultPage beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with BrowserSearchResultPage beans, may be empty.
     */
    public static BrowserSearchResultPage[] getBrowserSearchResultPages( ISelection selection )
    {
        List<Object> list = getTypes( selection, BrowserSearchResultPage.class );
        return list.toArray( new BrowserSearchResultPage[list.size()] );
    }


    /**
     * Gets the objects contained in the given selection.
     *
     * @param selection the selection
     * @return an array with object, may be empty.
     */
    public static Object[] getObjects( ISelection selection )
    {
        List<Object> list = getTypes( selection, Object.class );
        return list.toArray( new Object[list.size()] );
    }
}
