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

import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken;


/**
 * The LdapFilterExtensibleComponent represents an simple filter.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */

public class LdapFilterItemComponent extends LdapFilterComponent
{

    /** The filtertype token. */
    private LdapFilterToken filtertypeToken;

    /** The value token. */
    private LdapFilterToken valueToken;


    /**
     * Creates a new instance of LdapFilterItemComponent.
     * 
     * @param parent the parent filter
     */
    public LdapFilterItemComponent( LdapFilter parent )
    {
        super( parent );
        this.filtertypeToken = null;
        this.valueToken = null;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#setStartToken(org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken)
     */
    public boolean setStartToken( LdapFilterToken attributeToken )
    {
        if ( attributeToken != null && attributeToken.getType() == LdapFilterToken.ATTRIBUTE )
        {
            super.setStartToken( attributeToken );
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Sets the attribute token.
     * 
     * @param attributeToken the attribute token
     * 
     * @return true, if setting the attribute token was successful, false otherwise
     */
    public boolean setAttributeToken( LdapFilterToken attributeToken )
    {
        return this.setStartToken( attributeToken );
    }


    /**
     * Gets the attribute token.
     * 
     * @return the attribute token, null if not set
     */
    public LdapFilterToken getAttributeToken()
    {
        return getStartToken();
    }


    /**
     * Sets the filtertype token.
     * 
     * @param filtertypeToken the filtertype token
     * 
     * @return true, if setting the filtertype token was successful, false otherwise
     */
    public boolean setFiltertypeToken( LdapFilterToken filtertypeToken )
    {
        if ( this.filtertypeToken == null
            && filtertypeToken != null
            && ( filtertypeToken.getType() == LdapFilterToken.EQUAL
                || filtertypeToken.getType() == LdapFilterToken.GREATER
                || filtertypeToken.getType() == LdapFilterToken.LESS
                || filtertypeToken.getType() == LdapFilterToken.APROX
                || filtertypeToken.getType() == LdapFilterToken.PRESENT || filtertypeToken.getType() == LdapFilterToken.SUBSTRING ) )
        {
            this.filtertypeToken = filtertypeToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the filter token.
     * 
     * @return the filter token, null if not set
     */
    public LdapFilterToken getFilterToken()
    {
        return filtertypeToken;
    }


    /**
     * Sets the value token.
     * 
     * @param valueToken the value token
     * 
     * @return true, if setting the value token was successful, false otherwise
     */
    public boolean setValueToken( LdapFilterToken valueToken )
    {
        if ( this.valueToken == null && valueToken != null && valueToken.getType() == LdapFilterToken.VALUE )
        {
            this.valueToken = valueToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the value token.
     * 
     * @return the value token, null if not set
     */
    public LdapFilterToken getValueToken()
    {
        return valueToken;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#isValid()
     */
    public boolean isValid()
    {
        return startToken != null && filtertypeToken != null
            && ( valueToken != null || filtertypeToken.getType() == LdapFilterToken.PRESENT );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#getTokens()
     */
    public LdapFilterToken[] getTokens()
    {
        // collect tokens
        List<LdapFilterToken> tokenList = new ArrayList<LdapFilterToken>();
        if ( startToken != null )
        {
            tokenList.add( startToken );
        }
        if ( filtertypeToken != null )
        {
            tokenList.add( filtertypeToken );
        }
        if ( valueToken != null )
        {
            tokenList.add( valueToken );
        }

        // sort tokens
        LdapFilterToken[] tokens = tokenList.toArray( new LdapFilterToken[tokenList.size()] );
        Arrays.sort( tokens );

        // return
        return tokens;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return ( startToken != null ? startToken.getValue() : "" )
            + ( filtertypeToken != null ? filtertypeToken.getValue() : "" )
            + ( valueToken != null ? valueToken.getValue() : "" );
    }


    /**
     * This implementation does nothing and returns always false.
     * 
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#addFilter(org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter)
     */
    public boolean addFilter( LdapFilter filter )
    {
        return false;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#getInvalidFilters()
     */
    public LdapFilter[] getInvalidFilters()
    {
        if ( isValid() )
        {
            return new LdapFilter[0];
        }
        else
        {
            return new LdapFilter[]
                { parent };
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#getFilter(int)
     */
    public LdapFilter getFilter( int offset )
    {
        if ( startToken != null && startToken.getOffset() <= offset
            && offset < startToken.getOffset() + startToken.getLength() )
        {
            return parent;
        }
        else if ( filtertypeToken != null && filtertypeToken.getOffset() <= offset
            && offset < filtertypeToken.getOffset() + filtertypeToken.getLength() )
        {
            return parent;
        }
        else if ( valueToken != null && valueToken.getOffset() <= offset
            && offset < valueToken.getOffset() + valueToken.getLength() )
        {
            return parent;
        }
        else
        {
            return null;
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#getInvalidCause()
     */
    public String getInvalidCause()
    {
        if ( startToken == null )
        {
            return "Missing attribute name";
        }
        else if ( filtertypeToken == null )
        {
            return "Missing filter type, select one of '=', '>=', '<=','~='";
        }
        else if ( valueToken == null )
        {
            return "Missing value";
        }
        else
        {
            return null;
        }
    }

}
