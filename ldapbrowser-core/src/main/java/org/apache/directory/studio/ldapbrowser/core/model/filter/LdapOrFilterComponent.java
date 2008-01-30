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
 * The LdapOrFilterComponent represents an OR filter branch.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapOrFilterComponent extends LdapFilterComponent
{

    /**
     * Creates a new instance of LdapOrFilterComponent.
     * 
     * @param parent the parent filter
     */
    public LdapOrFilterComponent( LdapFilter parent )
    {
        super( parent );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent#setStartToken(org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken)
     */
    public boolean setStartToken( LdapFilterToken orToken )
    {
        if ( orToken != null && orToken.getType() == LdapFilterToken.OR )
        {
            return super.setStartToken( orToken );
        }
        else
        {
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
            return "Missing OR character '|'";
        }
        else if ( filterList == null || filterList.isEmpty() )
        {
            return "Missing filters";
        }
        else
        {
            return "Invalid OR filter";
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        String s = startToken != null ? "|" : "";
        for ( LdapFilter filter : filterList )
        {
            if ( filter != null )
            {
                s += filter.toString();
            }
        }
        return s;
    }

}
