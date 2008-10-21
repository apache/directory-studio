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


/**
 * The LdapNotFilterComponent represents an NOT filter branch.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapNotFilterComponent extends LdapFilterComponent
{

    /**
     * Creates a new instance of LdapNotFilterComponent.
     * 
     * @param parent the parent filter
     */
    public LdapNotFilterComponent( LdapFilter parent )
    {
        super( parent );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#setStartToken(org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken)
     */
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
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#addFilter(org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter)
     */
    public boolean addFilter( LdapFilter filter )
    {
        if ( filterList.isEmpty() )
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


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#getInvalidCause()
     */
    public String getInvalidCause()
    {
        if ( startToken == null )
        {
            return "Missing NOT character '!'";
        }
        else if ( filterList == null || filterList.isEmpty() )
        {
            return "Missing filter expression";
        }
        else
        {
            return "Invalid NOT filter";
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return ( startToken != null ? "!" : "" ) + ( !filterList.isEmpty() ? filterList.get( 0 ).toString() : "" );
    }

}
