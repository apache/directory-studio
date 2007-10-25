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


import java.util.Comparator;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifAttrValLine;


public class AttributeComparator implements Comparator
{

    private IEntry dummyEntry;


    public AttributeComparator( IBrowserConnection connection )
    {
        this.dummyEntry = new DummyEntry( new DN(), connection );
    }


    public AttributeComparator( IEntry entry )
    {
        this.dummyEntry = entry;
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
        else if ( o1 instanceof LdifAttrValLine )
        {
            LdifAttrValLine line1 = ( LdifAttrValLine ) o1;
            value1 = ModelConverter.ldifAttrValLineToValue( line1, dummyEntry );
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
        else if ( o2 instanceof LdifAttrValLine )
        {
            LdifAttrValLine line2 = ( LdifAttrValLine ) o2;
            value2 = ModelConverter.ldifAttrValLineToValue( line2, dummyEntry );
            attribute2 = value2.getAttribute();
        }

        if ( value1 != null && value2 != null )
        {
            if ( this.getSortByOrDefault() == BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION )
            {
                if ( value1.getAttribute() != value2.getAttribute() )
                {
                    return this.compareAttributeNames( value1.getAttribute(), value2.getAttribute() );
                }
                else
                {
                    return this.compareValues( value1, value2 );
                }
            }
            else if ( this.getSortByOrDefault() == BrowserCoreConstants.SORT_BY_VALUE )
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
            return this.compareAttributeNames( attribute1, attribute2 );
        }
        else
        {
            return this.equal();
        }
    }


    private int compareAttributeNames( IAttribute attribute1, IAttribute attribute2 )
    {

        if ( attribute1.isObjectClassAttribute() )
        {
            return lessThan();
        }
        else if ( attribute2.isObjectClassAttribute() )
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

        if ( attribute1.isOperationalAttribute() && !attribute2.isOperationalAttribute() )
        {
            return greaterThan();
        }
        else if ( attribute2.isOperationalAttribute() && !attribute1.isOperationalAttribute() )
        {
            return lessThan();
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


    private int getSortOrderOrDefault()
    {
        return BrowserCoreConstants.SORT_ORDER_ASCENDING;
    }


    private int getSortByOrDefault()
    {
        return BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION;
    }


    private int lessThan()
    {
        return this.getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
    }


    private int equal()
    {
        return 0;
    }


    private int greaterThan()
    {
        return this.getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    private int compare( String s1, String s2 )
    {
        return this.getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1.compareToIgnoreCase( s2 )
            : s2.compareToIgnoreCase( s1 );
    }


}
