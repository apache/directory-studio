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
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken;


/**
 * The LdapFilter class represents an LDAP filter.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapFilter
{

    private LdapFilterToken startToken;

    private LdapFilterComponent filterComponent;

    private LdapFilterToken stopToken;

    private List<LdapFilterToken> otherTokens;


    /**
     * Creates a new instance of LdapFilter.
     */
    public LdapFilter()
    {
        this.startToken = null;
        this.filterComponent = null;
        this.stopToken = null;
        this.otherTokens = new ArrayList<LdapFilterToken>( 2 );
    }


    /**
     * Sets the start token.
     * 
     * @param startToken the start token
     * 
     * @return true, if setting the start token was successful, false otherwise
     */
    public boolean setStartToken( LdapFilterToken startToken )
    {
        if ( this.startToken == null && startToken != null && startToken.getType() == LdapFilterToken.LPAR )
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
     * Sets the filter component.
     * 
     * @param filterComponent the filter component
     * 
     * @return true, if setting the filter component was successful, false otherwise
     */
    public boolean setFilterComponent( LdapFilterComponent filterComponent )
    {
        if ( this.startToken != null && this.filterComponent == null && filterComponent != null )
        {
            this.filterComponent = filterComponent;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Sets the stop token.
     * 
     * @param stopToken the stop token
     * 
     * @return true, if setting the stop token was successful, false otherwise
     */
    public boolean setStopToken( LdapFilterToken stopToken )
    {
        if ( this.startToken != null && this.stopToken == null && stopToken != null
            && stopToken.getType() == LdapFilterToken.RPAR )
        {
            this.stopToken = stopToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Adds another token.
     * 
     * @param otherToken the other token
     */
    public void addOtherToken( LdapFilterToken otherToken )
    {
        otherTokens.add( otherToken );
    }


    /**
     * Gets the start token.
     * 
     * @return the start token, or null if not set
     */
    public LdapFilterToken getStartToken()
    {
        return startToken;
    }


    /**
     * Gets the filter component.
     * 
     * @return the filter component, or null if not set
     */
    public LdapFilterComponent getFilterComponent()
    {
        return filterComponent;
    }


    /**
     * Gets the stop token.
     * 
     * @return the stop token or null if not set
     */
    public LdapFilterToken getStopToken()
    {
        return stopToken;
    }


    /**
     * Gets all the tokens.
     * 
     * @return the tokens
     */
    public LdapFilterToken[] getTokens()
    {
        // collect tokens
        List<LdapFilterToken> tokenList = new ArrayList<LdapFilterToken>();
        if ( startToken != null )
        {
            tokenList.add( startToken );
        }
        if ( stopToken != null )
        {
            tokenList.add( stopToken );
        }
        if ( filterComponent != null )
        {
            tokenList.addAll( Arrays.asList( filterComponent.getTokens() ) );
        }
        tokenList.addAll( otherTokens );

        // sort tokens
        LdapFilterToken[] tokens = tokenList.toArray( new LdapFilterToken[tokenList.size()] );
        Arrays.sort( tokens );

        // return
        return tokens;
    }


    /**
     * Checks if this filter and all its subfilters are valid.
     * 
     * @return true, if this filter and all its subfilters is valid
     */
    public boolean isValid()
    {
        return startToken != null && filterComponent != null && filterComponent.isValid() && stopToken != null && otherTokens.isEmpty();
    }


    /**
     * Gets the invalid filters. This may be this filter itself or any of the subfilters.
     * 
     * @return an array of invalid filters or an empty array if all subfilters
     *         are valid.
     */
    public LdapFilter[] getInvalidFilters()
    {
        if ( startToken == null || filterComponent == null || stopToken == null )
        {
            return new LdapFilter[]
                { this };
        }
        else
        {
            return filterComponent.getInvalidFilters();
        }
    }


    /**
     * Gets the filter at the given offset. This may be this filter
     * or one of the subfilters.
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
            return this;
        }
        else if ( stopToken != null && stopToken.getOffset() == offset )
        {
            return this;
        }

        if ( otherTokens != null && otherTokens.size() > 0 )
        {
            for ( int i = 0; i < otherTokens.size(); i++ )
            {
                LdapFilterToken otherToken = otherTokens.get( i );
                if ( otherToken != null && otherToken.getOffset() <= offset
                    && offset < otherToken.getOffset() + otherToken.getLength() )
                {
                    return this;
                }
            }
        }

        if ( filterComponent != null )
        {
            return filterComponent.getFilter( offset );
        }

        return this;
    }


    /**
     * Gets the invalid cause.
     * 
     * @return the invalid cause, or null if this filter is valid.
     */
    public String getInvalidCause()
    {
        if ( stopToken == null )
        {
            return BrowserCoreMessages.model_filter_missing_closing_parenthesis;
        }
        else if ( filterComponent == null )
        {
            return BrowserCoreMessages.model_filter_missing_filter_expression;
        }
        else
        {
            return filterComponent.getInvalidCause();
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        LdapFilterToken[] tokens = getTokens();
        for ( LdapFilterToken token : tokens )
        {
            sb.append( token.getValue() );
        }
        return sb.toString();
//        return ( startToken != null ? "(" : "" ) + 
//        ( filterComponent != null ? filterComponent.toString() : "" ) + 
//        ( stopToken != null ? ")" : "" ); 
//        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }

}
