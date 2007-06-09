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


import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken;


public class LdapNotFilterComponent extends LdapFilterComponent
{

    public LdapNotFilterComponent( LdapFilter parent )
    {
        super( parent );
    }


    public boolean setStartToken( LdapFilterToken notToken )
    {
        if ( notToken != null && notToken.getType() == LdapFilterToken.NOT )
        {
            return super.setStartToken( notToken );
        }
        else
        {
            return false;
        }
    }


    /**
     * Checks additionally if the the filter wasn't set before.
     * 
     * @see LdapFilterComponent#addFilter(LdapFilter)
     */
    public boolean addFilter( LdapFilter filter )
    {
        if ( this.filterList.isEmpty() )
        {
            return super.addFilter( filter );
        }
        else
        {
            // There is already a filter in the list. A NOT filter
            // can only contain one filter.
            return false;
        }
    }


    public String getInvalidCause()
    {
        if ( this.startToken == null )
        {
            return "Missing NOT character '!'";
        }
        else if ( this.filterList == null || this.filterList.isEmpty() )
        {
            return "Missing filter expression";
        }
        else
        {
            return "Invalid NOT filter";
        }
    }


    public String toString()
    {
        return ( this.startToken != null ? "!" : "" )
            + ( !this.filterList.isEmpty() ? this.filterList.get( 0 ).toString() : "" );
    }

}
