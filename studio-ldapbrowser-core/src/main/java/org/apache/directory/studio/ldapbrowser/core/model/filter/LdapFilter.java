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


public class LdapFilter
{

    private LdapFilterToken startToken;

    private LdapFilterComponent filterComponent;

    private LdapFilterToken stopToken;

    private List otherTokens;


    public LdapFilter()
    {
        this.startToken = null;
        this.filterComponent = null;
        this.stopToken = null;
        this.otherTokens = new ArrayList( 2 );
    }


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


    public void addOtherToken( LdapFilterToken otherToken )
    {
        this.otherTokens.add( otherToken );
    }


    public LdapFilterToken getStartToken()
    {
        return this.startToken;
    }


    public LdapFilterComponent getFilterComponent()
    {
        return this.filterComponent;
    }


    public LdapFilterToken getStopToken()
    {
        return this.stopToken;
    }


    public LdapFilterToken[] getTokens()
    {

        // collect tokens
        List tokenList = new ArrayList();
        if ( this.startToken != null )
        {
            tokenList.add( this.startToken );
        }
        if ( this.stopToken != null )
        {
            tokenList.add( this.stopToken );
        }
        if ( this.filterComponent != null )
        {
            tokenList.addAll( Arrays.asList( this.filterComponent.getTokens() ) );
        }
        tokenList.addAll( this.otherTokens );

        // sort tokens
        LdapFilterToken[] tokens = ( LdapFilterToken[] ) tokenList.toArray( new LdapFilterToken[tokenList.size()] );
        Arrays.sort( tokens );

        // return
        return tokens;
    }


    public boolean isValid()
    {
        return this.startToken != null && this.filterComponent != null && this.filterComponent.isValid()
            && this.stopToken != null;
    }


    public LdapFilter[] getInvalidFilters()
    {
        if ( this.startToken == null || this.filterComponent == null || this.stopToken == null )
        {
            return new LdapFilter[]
                { this };
        }
        else
        {
            return this.filterComponent.getInvalidFilters();
        }
    }


    public LdapFilter getFilter( int offset )
    {
        if ( this.startToken != null && this.startToken.getOffset() == offset )
        {
            return this;
        }
        else if ( this.stopToken != null && this.stopToken.getOffset() == offset )
        {
            return this;
        }

        if ( this.otherTokens != null && this.otherTokens.size() > 0 )
        {
            for ( int i = 0; i < this.otherTokens.size(); i++ )
            {
                LdapFilterToken otherToken = ( LdapFilterToken ) this.otherTokens.get( i );
                if ( otherToken != null && otherToken.getOffset() <= offset
                    && offset < otherToken.getOffset() + otherToken.getLength() )
                {
                    return this;
                }
            }
        }

        if ( this.filterComponent != null )
        {
            return this.filterComponent.getFilter( offset );
        }

        return this;
    }


    public String getInvalidCause()
    {
        if ( this.stopToken == null )
        {
            return BrowserCoreMessages.model_filter_missing_closing_parenthesis;
        }
        else if ( this.filterComponent == null )
        {
            return BrowserCoreMessages.model_filter_missing_filter_expression;
        }
        else
        {
            return this.filterComponent.getInvalidCause();
        }
    }


    public String toString()
    {
        return ( this.startToken != null ? "(" : "" ) + ( filterComponent != null ? filterComponent.toString() : "" ) + ( this.stopToken != null ? ")" : "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }

}
