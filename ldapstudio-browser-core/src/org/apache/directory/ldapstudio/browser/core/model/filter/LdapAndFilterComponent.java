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

package org.apache.directory.ldapstudio.browser.core.model.filter;


import java.util.Iterator;

import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterToken;


public class LdapAndFilterComponent extends LdapFilterComponent
{

    public LdapAndFilterComponent( LdapFilter parent )
    {
        super( parent );
    }


    public boolean setStartToken( LdapFilterToken andToken )
    {
        if ( andToken != null && andToken.getType() == LdapFilterToken.AND )
        {
            return super.setStartToken( andToken );
        }
        else
        {
            return false;
        }
    }


    public String getInvalidCause()
    {
        if ( this.startToken == null )
        {
            return "Missing AND character '&'";
        }
        else if ( this.filterList == null || this.filterList.isEmpty() )
        {
            return "Missing filters";
        }
        else
        {
            return "Invalid AND filter";
        }
    }


    public String toString()
    {
        String s = this.startToken != null ? "&" : "";
        for ( Iterator it = filterList.iterator(); it.hasNext(); )
        {
            LdapFilter filter = ( LdapFilter ) it.next();
            if ( filter != null )
            {
                s += filter.toString();
            }
        }
        return s;
    }

}
