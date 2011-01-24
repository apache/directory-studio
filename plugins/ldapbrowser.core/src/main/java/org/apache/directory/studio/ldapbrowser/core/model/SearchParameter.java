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

package org.apache.directory.studio.ldapbrowser.core.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.model.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;


/**
 * A Bean class to hold the search parameters.
 * It is used to make searches persistent.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchParameter implements Serializable
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = 2447490121520960805L;

    /** The symbolic name. */
    private String name;

    /** The search base. */
    private Dn searchBase;

    /** The filter. */
    private String filter;

    /** The returning attributes. */
    private String[] returningAttributes;

    /** The search scope. */
    private SearchScope scope;

    /** The time limit in seconds, 0 means no limit. */
    private int timeLimit;

    /** The count limit, 0 means no limit. */
    private int countLimit;

    /** The alias dereferencing method. */
    private AliasDereferencingMethod aliasesDereferencingMethod;

    /** The referrals handling method. */
    private ReferralHandlingMethod referralsHandlingMethod;

    /** The controls */
    private List<StudioControl> controls;

    /** The response controls */
    private List<StudioControl> responseControls;

    /** Flag indicating weather the hasChildren flag of IEntry should be initialized */
    private boolean initHasChildrenFlag;


    /**
     * Creates a new instance of SearchParameter with default search parameters:
     * <ul>
     * <li>null search name
     * <li>null search base
     * <li>default filter (objectClass=*)
     * <li>no returning attributes
     * <li>search scope one level
     * <li>no count limit
     * <li>no time limit
     * <li>always dereference aliases
     * <li>follow referrals
     * <li>no initialization of hasChildren flag
     * <li>no initialization of isAlias and isReferral flag
     * <li>no controls
     * <li>no response controls
     * </ul>
     */
    public SearchParameter()
    {
        name = null;
        searchBase = null;
        filter = ISearch.FILTER_TRUE;
        returningAttributes = ISearch.NO_ATTRIBUTES;
        scope = SearchScope.ONELEVEL;
        timeLimit = 0;
        countLimit = 0;
        aliasesDereferencingMethod = AliasDereferencingMethod.ALWAYS;
        referralsHandlingMethod = ReferralHandlingMethod.FOLLOW;
        controls = new ArrayList<StudioControl>();
        responseControls = new ArrayList<StudioControl>();
        initHasChildrenFlag = false;
    }


    /**
     * Gets the count limit, 0 means no limit.
     * 
     * @return the count limit
     */
    public int getCountLimit()
    {
        return countLimit;
    }


    /**
     * Sets the count limit, 0 means no limit.
     * 
     * @param countLimit the count limit
     */
    public void setCountLimit( int countLimit )
    {
        this.countLimit = countLimit;
    }


    /**
     * Gets the filter.
     * 
     * @return the filter
     */
    public String getFilter()
    {
        return filter;
    }


    /**
     * Sets the filter, a null or empty filter will be
     * transformed to (objectClass=*).
     * 
     * @param filter the filter
     */
    public void setFilter( String filter )
    {
        if ( filter == null || "".equals( filter ) ) //$NON-NLS-1$
        {
            filter = ISearch.FILTER_TRUE;
        }
        this.filter = filter;
    }


    /**
     * Gets the symbolic name.
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the symbolic name.
     * 
     * @param name the name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Gets the returning attributes.
     * 
     * @return the returning attributes
     */
    public String[] getReturningAttributes()
    {
        return returningAttributes;
    }


    /**
     * Sets the returning attributes, an empty array indicates none,
     * null will be transformed to '*' (all user attributes).
     * 
     * @param returningAttributes the returning attributes
     */
    public void setReturningAttributes( String[] returningAttributes )
    {
        if ( returningAttributes == null )
        {
            returningAttributes = new String[]
                { SchemaConstants.ALL_USER_ATTRIBUTES };
        }
        this.returningAttributes = returningAttributes;
    }


    /**
     * Gets the scope.
     * 
     * @return the scope
     */
    public SearchScope getScope()
    {
        return scope;
    }


    /**
     * Sets the scope.
     * 
     * @param scope the scope
     */
    public void setScope( SearchScope scope )
    {
        this.scope = scope;
    }


    /**
     * Gets the aliases dereferencing method.
     * 
     * @return the aliases dereferencing method
     */
    public AliasDereferencingMethod getAliasesDereferencingMethod()
    {
        return aliasesDereferencingMethod;
    }


    /**
     * Sets the aliases dereferencing method.
     * 
     * @param aliasesDereferencingMethod the aliases dereferencing method
     */
    public void setAliasesDereferencingMethod( AliasDereferencingMethod aliasesDereferencingMethod )
    {
        this.aliasesDereferencingMethod = aliasesDereferencingMethod;
    }


    /**
     * Gets the referrals handling method.
     * 
     * @return the referrals handling method
     */
    public ReferralHandlingMethod getReferralsHandlingMethod()
    {
        return referralsHandlingMethod;
    }


    /**
     * Sets the referrals handling method.
     * 
     * @param referralsHandlingMethod the referrals handling method
     */
    public void setReferralsHandlingMethod( ReferralHandlingMethod referralsHandlingMethod )
    {
        this.referralsHandlingMethod = referralsHandlingMethod;
    }


    /**
     * Gets the search base.
     * 
     * @return the search base
     */
    public Dn getSearchBase()
    {
        return searchBase;
    }


    /**
     * Sets the search base, a null search base is not allowed.
     * 
     * @param searchBase the search base
     */
    public void setSearchBase( Dn searchBase )
    {
        assert searchBase != null;
        this.searchBase = searchBase;
    }


    /**
     * Gets the time limit in seconds, 0 means no limit.
     * 
     * @return the time limit
     */
    public int getTimeLimit()
    {
        return timeLimit;
    }


    /**
     * Sets the time limit in seconds, 0 means no limit.
     * 
     * @param timeLimit the time limit
     */
    public void setTimeLimit( int timeLimit )
    {
        this.timeLimit = timeLimit;
    }


    /**
     * {@inheritDoc}
     */
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
        clone.setInitHasChildrenFlag( isInitHasChildrenFlag() );
        clone.getControls().addAll( getControls() );
        return clone;
    }


    /**
     * Checks if the hasChildren flag of IEntry should be initialized.
     * 
     * @return true, if the hasChildren flag of IEntry should be initialized
     */
    public boolean isInitHasChildrenFlag()
    {
        return initHasChildrenFlag;
    }


    /**
     * Sets if the hasChildren flag of IEntry should be initialized.
     * 
     * @param initHasChildrenFlag the init hasChildren flag
     */
    public void setInitHasChildrenFlag( boolean initHasChildrenFlag )
    {
        this.initHasChildrenFlag = initHasChildrenFlag;
    }


    /**
     * Gets the controls.
     * 
     * @return the controls
     */
    public List<StudioControl> getControls()
    {
        return controls;
    }


    /**
     * Gets the response controls.
     * 
     * @return the response controls
     */
    public List<StudioControl> getResponseControls()
    {
        return responseControls;
    }

}
