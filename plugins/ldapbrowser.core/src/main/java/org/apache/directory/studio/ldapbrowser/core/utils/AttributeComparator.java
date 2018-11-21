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

package org.apache.directory.studio.ldapbrowser.core.utils;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;


public class AttributeComparator implements Comparator<Object>
{
    private final int sortBy;
    private final int defaultSortBy;
    private final int sortOrder;
    private final int defaultSortOrder;
    private final boolean objectClassAndMustAttributesFirst;
    private final boolean operationalAttributesLast;


    public AttributeComparator()
    {
        this.sortBy = BrowserCoreConstants.SORT_BY_NONE;
        this.defaultSortBy =BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION;
        this.sortOrder = BrowserCoreConstants.SORT_ORDER_NONE;
        this.defaultSortOrder = BrowserCoreConstants.SORT_ORDER_ASCENDING;
        this.objectClassAndMustAttributesFirst = true;
        this.operationalAttributesLast = true;
    }


    public AttributeComparator( int sortBy, int defaultSortBy, int sortOrder, int defaultSortOrder, boolean objectClassAndMustAttributesFirst,
        boolean operationalAttributesLast )
    {
        this.sortBy = sortBy;
        this.defaultSortBy = defaultSortBy;
        this.sortOrder = sortOrder;
        this.defaultSortOrder = defaultSortOrder;
        this.objectClassAndMustAttributesFirst = objectClassAndMustAttributesFirst;
        this.operationalAttributesLast = operationalAttributesLast;
    }


    public int compare( Object o1, Object o2 )
    {
        IAttribute attribute1 = null;
        IValue value1 = null;
        if ( o1 instanceof IAttribute )
        {
            attribute1 = ( IAttribute ) o1;
        }
        else if ( o1 instanceof IValue )
        {
            value1 = ( IValue ) o1;
            attribute1 = value1.getAttribute();
        }

        IAttribute attribute2 = null;
        IValue value2 = null;
        if ( o2 instanceof IAttribute )
        {
            attribute2 = ( IAttribute ) o2;
        }
        else if ( o2 instanceof IValue )
        {
            value2 = ( IValue ) o2;
            attribute2 = value2.getAttribute();
        }

        if ( value1 != null && value2 != null )
        {
            if ( getSortByOrDefault() == BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION )
            {
                if ( value1.getAttribute() != value2.getAttribute() )
                {
                    return this.compareAttributes( value1.getAttribute(), value2.getAttribute() );
                }
                else
                {
                    return this.compareValues( value1, value2 );
                }
            }
            else if ( getSortByOrDefault() == BrowserCoreConstants.SORT_BY_VALUE )
            {
                return this.compareValues( value1, value2 );
            }
            else
            {
                return this.equal();
            }
        }
        else if ( attribute1 != null && attribute2 != null )
        {
            return this.compareAttributes( attribute1, attribute2 );
        }
        else
        {
            throw new ClassCastException( "Can only compare two values or two attributes" );
        }
    }


    private int compareAttributes( IAttribute attribute1, IAttribute attribute2 )
    {
        if ( this.sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
        {
            if ( objectClassAndMustAttributesFirst )
            {
                if ( attribute1.isObjectClassAttribute() && !attribute2.isObjectClassAttribute() )
                {
                    return lessThan();
                }
                else if ( attribute2.isObjectClassAttribute() && !attribute1.isObjectClassAttribute() )
                {
                    return greaterThan();
                }

                if ( attribute1.isMustAttribute() && !attribute2.isMustAttribute() )
                {
                    return lessThan();
                }
                else if ( attribute2.isMustAttribute() && !attribute1.isMustAttribute() )
                {
                    return greaterThan();
                }
            }

            if ( operationalAttributesLast )
            {
                if ( attribute1.isOperationalAttribute() && !attribute2.isOperationalAttribute() )
                {
                    return greaterThan();
                }
                else if ( attribute2.isOperationalAttribute() && !attribute1.isOperationalAttribute() )
                {
                    return lessThan();
                }
            }
        }

        return compare( attribute1.getDescription(), attribute2.getDescription() );
    }


    private int compareValues( IValue value1, IValue value2 )
    {
        if ( value1.isEmpty() && value2.isEmpty() )
        {
            return equal();
        }

        if ( value1.isEmpty() && !value2.isEmpty() )
        {
            return greaterThan();
        }
        if ( !value1.isEmpty() && value2.isEmpty() )
        {
            return lessThan();
        }

        return compare( value1.getStringValue(), value2.getStringValue() );
    }

    /**
     * Gets the current sort by property or the default sort by property (from the preferences).
     */
    private int getSortByOrDefault()
    {
        if ( sortBy == BrowserCoreConstants.SORT_BY_NONE )
        {
            return defaultSortBy;
        }
        else
        {
            return sortBy;
        }
    }

    /**
     * Gets the current sort order or the default sort order (from the preferences).
     */
    private int getSortOrderOrDefault()
    {
        if ( sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
        {
            return defaultSortOrder;
        }
        else
        {
            return sortOrder;
        }
    }

    private int lessThan()
    {
        return getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
    }


    private int equal()
    {
        return 0;
    }


    private int greaterThan()
    {
        return getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    /**
     * Compares the two strings using the Strings's compareToIgnoreCase method, 
     * pays attention for the sort order.
     *
     * @param s1 the first string to compare
     * @param s2 the second string to compare
     * @return a negative integer, zero, or a positive integer
     * @see java.lang.String#compareToIgnoreCase(String)
     */
    private int compare( String s1, String s2 )
    {
        return getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1.compareToIgnoreCase( s2 )
            : s2.compareToIgnoreCase( s1 );
    }


    public static List<IValue> toSortedValues( IEntry entry )
    {
        return Arrays.stream( entry.getAttributes() ).flatMap( a -> Arrays.stream( a.getValues() ) )
            .sorted( new AttributeComparator() )
            .collect( Collectors.toList() );
    }

}
