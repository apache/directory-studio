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

package org.apache.directory.ldapstudio.browser.core.model;


import java.io.Serializable;

import org.apache.directory.ldapstudio.browser.core.utils.LdifUtils;


public class SearchParameter implements Serializable
{

    private static final long serialVersionUID = 2447490121520960805L;

    private String name;

    private DN searchBase;

    private String filter = ISearch.FILTER_TRUE;

    private String[] returningAttributes = null;

    private int scope = ISearch.SCOPE_OBJECT;

    private int timeLimit = 0;

    private int countLimit = 0;

    private int aliasesDereferencingMethod = IConnection.DEREFERENCE_ALIASES_NEVER;

    private int referralsHandlingMethod = IConnection.HANDLE_REFERRALS_IGNORE;

    private Control[] controls = null;

    private boolean initChildrenFlag = false;

    private boolean initAliasAndReferralFlag = false;


    public SearchParameter()
    {
    }


    public int getCountLimit()
    {
        return countLimit;
    }


    public void setCountLimit( int countLimit )
    {
        this.countLimit = countLimit;
    }


    public String getFilter()
    {
        return filter;
    }


    public void setFilter( String filter )
    {
        this.filter = filter;
    }


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public String[] getReturningAttributes()
    {
        return returningAttributes;
    }


    public void setReturningAttributes( String[] returningAttributes )
    {
        if ( returningAttributes == null )
        {
            IllegalArgumentException e = new IllegalArgumentException( "Argument returningAttributes is null" ); //$NON-NLS-1$
            e.printStackTrace();
            throw e;
        }
        this.returningAttributes = returningAttributes;
    }


    public int getScope()
    {
        return scope;
    }


    public void setScope( int scope )
    {
        this.scope = scope;
    }


    public int getAliasesDereferencingMethod()
    {
        return aliasesDereferencingMethod;
    }


    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        this.aliasesDereferencingMethod = aliasesDereferencingMethod;
    }


    public int getReferralsHandlingMethod()
    {
        return referralsHandlingMethod;
    }


    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        this.referralsHandlingMethod = referralsHandlingMethod;
    }


    public DN getSearchBase()
    {
        return searchBase;
    }


    public void setSearchBase( DN searchBase )
    {
        this.searchBase = searchBase;
    }


    public int getTimeLimit()
    {
        return timeLimit;
    }


    public void setTimeLimit( int timeLimit )
    {
        this.timeLimit = timeLimit;
    }


    public Object clone()
    {
        SearchParameter clone = new SearchParameter();
        clone.setName( getName() );
        clone.setSearchBase( getSearchBase() );
        clone.setFilter( getFilter() );
        clone.setReturningAttributes( getReturningAttributes() );
        clone.setScope( getScope() );
        clone.setTimeLimit( getTimeLimit() );
        clone.setCountLimit( getCountLimit() );
        clone.setAliasesDereferencingMethod( getAliasesDereferencingMethod() );
        clone.setReferralsHandlingMethod( getReferralsHandlingMethod() );
        clone.setInitChildrenFlag( isInitChildrenFlag() );
        clone.setInitAliasAndReferralFlag( isInitAliasAndReferralFlag() );
        clone.setControls( getControls() );
        return clone;
    }


    public String getURL()
    {
        // ldap://host:port/dn?attributes?scope?filter?extensions
        StringBuffer sb = new StringBuffer();

        sb.append( LdifUtils.urlEncode( searchBase.toString() ) );

        sb.append( '?' );

        for ( int i = 0; returningAttributes != null && i < returningAttributes.length; i++ )
        {
            sb.append( LdifUtils.urlEncode( returningAttributes[i] ) );
            if ( i + 1 < returningAttributes.length )
                sb.append( ',' );
        }

        sb.append( '?' );

        if ( scope == ISearch.SCOPE_OBJECT )
        {
            sb.append( "base" ); //$NON-NLS-1$
        }
        else if ( scope == ISearch.SCOPE_ONELEVEL )
        {
            sb.append( "one" ); //$NON-NLS-1$
        }
        else if ( scope == ISearch.SCOPE_SUBTREE )
        {
            sb.append( "sub" ); //$NON-NLS-1$
        }

        sb.append( '?' );

        if ( filter != null )
        {
            sb.append( LdifUtils.urlEncode( filter ) );
        }

        return sb.toString();
    }


    public boolean isInitAliasAndReferralFlag()
    {
        return initAliasAndReferralFlag;
    }


    public void setInitAliasAndReferralFlag( boolean initAliasAndReferralFlag )
    {
        this.initAliasAndReferralFlag = initAliasAndReferralFlag;
    }


    public boolean isInitChildrenFlag()
    {
        return initChildrenFlag;
    }


    public void setInitChildrenFlag( boolean initChildrenFlag )
    {
        this.initChildrenFlag = initChildrenFlag;
    }


    public Control[] getControls()
    {
        return controls;
    }


    public void setControls( Control[] controls )
    {
        this.controls = controls;
    }

}
