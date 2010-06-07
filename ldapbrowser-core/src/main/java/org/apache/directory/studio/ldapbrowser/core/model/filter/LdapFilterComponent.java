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


/**
 * The LdapFilterComponent is the base class for all filter components.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class LdapFilterComponent
{

    /** The parent filter. */
    protected final LdapFilter parent;

    /** The start token. */
    protected LdapFilterToken startToken;

    /** The filter list. */
    protected List<LdapFilter> filterList;


    /**
     * The Constructor.
     * 
     * @param parent the parent filter, not null
     */
    protected LdapFilterComponent( LdapFilter parent )
    {
        if ( parent == null )
        {
            throw new IllegalArgumentException( "parent is null" );
        }

        this.parent = parent;
        this.startToken = null;
        this.filterList = new ArrayList<LdapFilter>( 2 );
    }


    /**
     * Returns the parent filter of this filter component.
     * 
     * @return the parent filter, never null.
     */
    public final LdapFilter getParent()
    {
        return parent;
    }


    /**
     * Sets the start token of the filter component. Checks if start token
     * isn't set yet and if the given start token isn't null.
     * 
     * @param startToken
     * 
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
        return startToken;
    }


    /**
     * Adds a filter to the list of subfilters. Checks if the start token
     * was set before and if the filter isn't null.
     * 
     * @param filter
     * 
     * @return true if adding the filter was successful, false otherwise.
     */
    public boolean addFilter( LdapFilter filter )
    {
        if ( startToken != null && filter != null )
        {
            filterList.add( filter );
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
        LdapFilter[] filters = new LdapFilter[filterList.size()];
        filterList.toArray( filters );
        return filters;
    }


    /**
     * Checks if this filter component including all subfilters is valid.
     * 
     * @return true if this filter component is valid.
     */
    public boolean isValid()
    {
        if ( startToken == null )
        {
            return false;
        }

        if ( filterList.isEmpty() )
        {
            return false;
        }

        for ( Iterator<LdapFilter> it = filterList.iterator(); it.hasNext(); )
        {
            LdapFilter filter = it.next();
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
     * @return the invalid cause, or null if this filter is valid.
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
        if ( startToken == null || filterList.isEmpty() )
        {
            return new LdapFilter[]
                { parent };
        }
        else
        {
            List<LdapFilter> invalidFilterList = new ArrayList<LdapFilter>();
            for ( Iterator<LdapFilter> it = filterList.iterator(); it.hasNext(); )
            {
                LdapFilter filter = it.next();
                if ( filter != null )
                {
                    invalidFilterList.addAll( Arrays.asList( filter.getInvalidFilters() ) );
                }
            }
            return invalidFilterList.toArray( new LdapFilter[invalidFilterList.size()] );
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
        List<LdapFilterToken> tokenList = new ArrayList<LdapFilterToken>();
        if ( startToken != null )
        {
            tokenList.add( startToken );
        }
        for ( Iterator<LdapFilter> it = filterList.iterator(); it.hasNext(); )
        {
            LdapFilter filter = it.next();
            if ( filter != null )
            {
                tokenList.addAll( Arrays.asList( filter.getTokens() ) );
            }
        }

        // sort tokens
        LdapFilterToken[] tokens = tokenList.toArray( new LdapFilterToken[tokenList.size()] );
        Arrays.sort( tokens );

        // return
        return tokens;
    }


    /**
     * Returns the filter at the given offset. This may be the whole parent
     * filter or one of the subfilters.
     * 
     * @param offset the offset
     * 
     * @return the filter at the given offset or null is offset is out of
     *         range.
     */
    public LdapFilter getFilter( int offset )
    {
        if ( startToken != null && startToken.getOffset() == offset )
        {
            return parent;
        }
        else if ( filterList != null || !filterList.isEmpty() )
        {
            for ( Iterator<LdapFilter> it = filterList.iterator(); it.hasNext(); )
            {
                LdapFilter filter = it.next();
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
