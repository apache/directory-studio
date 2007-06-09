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
 * The LdapFilterExtensibleComponent represents a extensible filter.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapFilterExtensibleComponent extends LdapFilterComponent
{
    private LdapFilterToken attributeToken;

    private LdapFilterToken dnAttrColonToken;
    private LdapFilterToken dnAttrToken;

    private LdapFilterToken matchingRuleColonToken;
    private LdapFilterToken matchingRuleToken;

    private LdapFilterToken equalsColonToken;
    private LdapFilterToken equalsToken;

    private LdapFilterToken valueToken;


    /**
     * Creates a new instance of LdapFilterExtensibleComponent.
     * 
     * @param parent the parent filter
     */
    public LdapFilterExtensibleComponent( LdapFilter parent )
    {
        super( parent );
    }


    /**
     * Sets the attribute token.
     * 
     * @param attributeToken the attribute token
     * 
     * @return true, if set attribute token
     */
    public boolean setAttributeToken( LdapFilterToken attributeToken )
    {
        if ( this.attributeToken == null && attributeToken != null
            && attributeToken.getType() == LdapFilterToken.EXTENSIBLE_ATTRIBUTE )
        {
            if ( super.getStartToken() == null )
            {
                super.setStartToken( attributeToken );
            }
            this.attributeToken = attributeToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the attribute token.
     * 
     * @return the attribute token
     */
    public LdapFilterToken getAttributeToken()
    {
        return attributeToken;
    }


    /**
     * Sets the dn attr colon token.
     * 
     * @param dnAttrColonToken the dn attr colon token
     * 
     * @return true, if set dn attr colon token
     */
    public boolean setDnAttrColonToken( LdapFilterToken dnAttrColonToken )
    {
        if ( this.dnAttrColonToken == null && dnAttrColonToken != null
            && dnAttrColonToken.getType() == LdapFilterToken.EXTENSIBLE_DNATTR_COLON )
        {
            if ( super.getStartToken() == null )
            {
                super.setStartToken( dnAttrColonToken );
            }
            this.dnAttrColonToken = dnAttrColonToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the dn attr colon token.
     * 
     * @return the dn attr colon token
     */
    public LdapFilterToken getDnAttrColonToken()
    {
        return dnAttrColonToken;
    }


    /**
     * Sets the dn attr token.
     * 
     * @param dnAttrToken the dn attr token
     * 
     * @return true, if set dn attr token
     */
    public boolean setDnAttrToken( LdapFilterToken dnAttrToken )
    {
        if ( this.dnAttrToken == null && dnAttrToken != null
            && dnAttrToken.getType() == LdapFilterToken.EXTENSIBLE_DNATTR )
        {
            this.dnAttrToken = dnAttrToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the dn attr token.
     * 
     * @return the dn attr token
     */
    public LdapFilterToken getDnAttrToken()
    {
        return dnAttrToken;
    }


    /**
     * Sets the matching rule colon token.
     * 
     * @param matchingRuleColonToken the matching rule colon token
     * 
     * @return true, if set matching rule colon token
     */
    public boolean setMatchingRuleColonToken( LdapFilterToken matchingRuleColonToken )
    {
        if ( this.matchingRuleColonToken == null && matchingRuleColonToken != null
            && matchingRuleColonToken.getType() == LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID_COLON )
        {
            if ( super.getStartToken() == null )
            {
                super.setStartToken( matchingRuleColonToken );
            }
            this.matchingRuleColonToken = matchingRuleColonToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the matching rule colon token.
     * 
     * @return the matching rule colon token
     */
    public LdapFilterToken getMatchingRuleColonToken()
    {
        return matchingRuleColonToken;
    }


    /**
     * Sets the matching rule token.
     * 
     * @param matchingRuleToken the matching rule token
     * 
     * @return true, if set matching rule token
     */
    public boolean setMatchingRuleToken( LdapFilterToken matchingRuleToken )
    {
        if ( this.matchingRuleToken == null && matchingRuleToken != null
            && matchingRuleToken.getType() == LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID )
        {
            this.matchingRuleToken = matchingRuleToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the matching rule token.
     * 
     * @return the matching rule token
     */
    public LdapFilterToken getMatchingRuleToken()
    {
        return matchingRuleToken;
    }


    /**
     * Sets the equals colon token.
     * 
     * @param equalsColonToken the equals colon token
     * 
     * @return true, if set equals colon token
     */
    public boolean setEqualsColonToken( LdapFilterToken equalsColonToken )
    {
        if ( this.equalsColonToken == null && equalsColonToken != null
            && equalsColonToken.getType() == LdapFilterToken.EXTENSIBLE_EQUALS_COLON )
        {
            this.equalsColonToken = equalsColonToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the equals colon token.
     * 
     * @return the equals colon token
     */
    public LdapFilterToken getEqualsColonToken()
    {
        return equalsColonToken;
    }


    /**
     * Sets the equals token.
     * 
     * @param equalsToken the equals token
     * 
     * @return true, if set equals token
     */
    public boolean setEqualsToken( LdapFilterToken equalsToken )
    {
        if ( this.equalsToken == null && equalsToken != null && equalsToken.getType() == LdapFilterToken.EQUAL )
        {
            this.equalsToken = equalsToken;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the equals token.
     * 
     * @return the equals token
     */
    public LdapFilterToken getEqualsToken()
    {
        return equalsToken;
    }


    /**
     * Sets the value token.
     * 
     * @param valueToken the value token
     * 
     * @return true, if set value token
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
     * @return the value token
     */
    public LdapFilterToken getValueToken()
    {
        return this.valueToken;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#isValid()
     */
    public boolean isValid()
    {
        return startToken != null
            && equalsColonToken != null
            & equalsToken != null
            && valueToken != null
            &&

            ( ( attributeToken != null
                && ( ( dnAttrColonToken == null && dnAttrToken == null ) || ( dnAttrColonToken != null && dnAttrToken != null ) ) && ( ( matchingRuleColonToken == null && matchingRuleToken == null ) || ( matchingRuleColonToken != null && matchingRuleToken != null ) ) ) || ( attributeToken == null
                && ( ( dnAttrColonToken == null && dnAttrToken == null ) || ( dnAttrColonToken != null && dnAttrToken != null ) )
                && matchingRuleColonToken != null && matchingRuleToken != null ) );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#getTokens()
     */
    public LdapFilterToken[] getTokens()
    {
        // collect tokens
        List<LdapFilterToken> tokenList = new ArrayList<LdapFilterToken>();
        if ( this.attributeToken != null )
        {
            tokenList.add( this.attributeToken );
        }
        if ( this.dnAttrColonToken != null )
        {
            tokenList.add( this.dnAttrColonToken );
        }
        if ( this.dnAttrToken != null )
        {
            tokenList.add( this.dnAttrToken );
        }
        if ( this.matchingRuleColonToken != null )
        {
            tokenList.add( this.matchingRuleColonToken );
        }
        if ( this.matchingRuleToken != null )
        {
            tokenList.add( this.matchingRuleToken );
        }
        if ( this.equalsColonToken != null )
        {
            tokenList.add( this.equalsColonToken );
        }
        if ( this.equalsToken != null )
        {
            tokenList.add( this.equalsToken );
        }
        if ( this.valueToken != null )
        {
            tokenList.add( this.valueToken );
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
        return ( attributeToken != null ? startToken.getValue() : "" )
            + ( dnAttrColonToken != null ? dnAttrColonToken.getValue() : "" )
            + ( dnAttrToken != null ? dnAttrToken.getValue() : "" )
            + ( matchingRuleColonToken != null ? matchingRuleColonToken.getValue() : "" )
            + ( matchingRuleToken != null ? matchingRuleToken.getValue() : "" )
            + ( equalsColonToken != null ? equalsColonToken.getValue() : "" )
            + ( equalsToken != null ? equalsToken.getValue() : "" )
            + ( valueToken != null ? valueToken.getValue() : "" );
    }


    /**
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
            && offset < startToken.getOffset() + toString().length() )
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
        if ( dnAttrColonToken != null && dnAttrToken == null )
        {
            return "Missing dn";
        }
        else if ( matchingRuleColonToken != null && matchingRuleToken == null )
        {
            return "Missing matching rule";
        }
        else if ( equalsColonToken == null )
        {
            return "Missing colon";
        }
        else if ( equalsToken == null )
        {
            return "Missing equals";
        }
        else if ( attributeToken == null )
        {
            return "Missing attribute type";
        }
        else if ( valueToken != null )
        {
            return "Missing value";
        }
        else
        {
            return null;
        }
    }

}
