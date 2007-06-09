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

package org.apache.directory.studio.ldapbrowser.core.model.filter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken;


public abstract class LdapFilterComponent
{

    protected final LdapFilter parent;

    protected LdapFilterToken startToken;

    protected List filterList;


    /**
     * 
     * 
     * @param parent
     *                the parent filter of this filter component, not null.
     */
    public LdapFilterComponent( LdapFilter parent )
    {
        if ( parent == null )
            throw new IllegalArgumentException( "parent is null" );

        this.parent = parent;
        this.startToken = null;
        this.filterList = new ArrayList( 2 );
    }


    /**
     * Returns the parent filter of this filter component.
     * 
     * @return the parent filter, never null.
     */
    public final LdapFilter getParent()
    {
        return this.parent;
    }


    /**
     * Sets the start token of the filter component. Checks if start token
     * isn't set yet and if the given start token isn't null.
     * 
     * @param startToken
     * @return true if setting the start token was successful, false
     *         otherwise.
     */
    public boolean setStartToken( LdapFilterToken startToken )
    {
        if ( this.startToken == null && startToken != null )
        {
            this.startToken = startToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Returns the start token of this filter component.
     * 
     * @return the start token or null if not set.
     */
    public final LdapFilterToken getStartToken()
    {
        return this.startToken;
    }


    /**
     * Adds a filter to the list of subfilters. Checks if the start token
     * was set before and if the filter isn't null.
     * 
     * @param filter
     * @return true if adding the filter was successful, false otherwise.
     */
    public boolean addFilter( LdapFilter filter )
    {
        if ( this.startToken != null && filter != null )
        {
            this.filterList.add( filter );
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Returns the subfilters of this filter component.
     * 
     * @return an array of subfilters or an empty array.
     */
    public LdapFilter[] getFilters()
    {
        LdapFilter[] filters = new LdapFilter[this.filterList.size()];
        this.filterList.toArray( filters );
        return filters;
    }


    /**
     * Checks if this filter component including all subfilters is valid.
     * 
     * @return true if this filter component is valid.
     */
    public boolean isValid()
    {
        if ( this.startToken == null )
        {
            return false;
        }

        if ( this.filterList.isEmpty() )
        {
            return false;
        }

        for ( Iterator it = filterList.iterator(); it.hasNext(); )
        {
            LdapFilter filter = ( LdapFilter ) it.next();
            if ( filter == null || !filter.isValid() )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Returns the invalid cause.
     * 
     * @return the invalid cause.
     */
    public abstract String getInvalidCause();


    /**
     * Returns the invalid filters. This may be the whole parent filter or
     * any of the subfilters.
     * 
     * @return an array of invalid filters or an empty array if all filters
     *         are valid.
     */
    public LdapFilter[] getInvalidFilters()
    {
        if ( this.startToken == null || this.filterList.isEmpty() )
        {
            return new LdapFilter[]
                { this.parent };
        }
        else
        {
            List invalidFilterList = new ArrayList();
            for ( Iterator it = this.filterList.iterator(); it.hasNext(); )
            {
                LdapFilter filter = ( LdapFilter ) it.next();
                if ( filter != null )
                {
                    invalidFilterList.addAll( Arrays.asList( filter.getInvalidFilters() ) );
                }
            }
            return ( LdapFilter[] ) invalidFilterList.toArray( new LdapFilter[invalidFilterList.size()] );
        }
    }


    /**
     * Returns all tokens of the filter component including all subfilters.
     * 
     * @return an array of tokens of an empty array.
     */
    public LdapFilterToken[] getTokens()
    {
        // collect tokens
        List tokenList = new ArrayList();
        if ( this.startToken != null )
        {
            tokenList.add( this.startToken );
        }
        for ( Iterator it = this.filterList.iterator(); it.hasNext(); )
        {
            LdapFilter filter = ( LdapFilter ) it.next();
            if ( filter != null )
            {
                tokenList.addAll( Arrays.asList( filter.getTokens() ) );
            }
        }

        // sort tokens
        LdapFilterToken[] tokens = ( LdapFilterToken[] ) tokenList.toArray( new LdapFilterToken[tokenList.size()] );
        Arrays.sort( tokens );

        // return
        return tokens;
    }


    /**
     * Returns the filter at the given offset. This may be the whole parent
     * filter or one of the subfilters.
     * 
     * @param offset
     * @return the filter at the given offset or null is offset is out of
     *         range.
     */
    public LdapFilter getFilter( int offset )
    {
        if ( this.startToken != null && this.startToken.getOffset() == offset )
        {
            return this.parent;
        }
        else if ( this.filterList != null || !this.filterList.isEmpty() )
        {
            for ( Iterator it = this.filterList.iterator(); it.hasNext(); )
            {
                LdapFilter filter = ( LdapFilter ) it.next();
                if ( filter != null && filter.getFilter( offset ) != null )
                {
                    return filter.getFilter( offset );
                }
            }
            return null;
        }
        else
        {
            return null;
        }
    }

}
