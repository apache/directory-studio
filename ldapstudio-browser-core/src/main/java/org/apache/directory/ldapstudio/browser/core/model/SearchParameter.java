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


/**
 * A Bean class to hold the search parameters.
 * It is used to make searches persistent.
 * 
 * TODO: check problems with null searchBase, filter and returningAttributes!
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchParameter implements Serializable
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = 2447490121520960805L;

    /** The symbolic name. */
    private String name;

    /** The search base. */
    private DN searchBase;

    /** The filter. */
    private String filter;

    /** The returning attributes. */
    private String[] returningAttributes;

    /** The search scope, one of ISearch.SCOPE_OBJECT, ISearch.SCOPE_ONELEVEL or ISearch.SCOPE_SUBTREE. */
    private int scope;

    /** The time limit in milliseconds, 0 means no limit. */
    private int timeLimit;

    /** The count limit, 0 means no limit. */
    private int countLimit;

    /** The alias dereferencing method, one of IConnection.DEREFERENCE_ALIASES_NEVER, IConnection.DEREFERENCE_ALIASES_ALWAYS, IConnection.DEREFERENCE_ALIASES_FINDING or IConnection.DEREFERENCE_ALIASES_SEARCH. */
    private int aliasesDereferencingMethod;

    /** The referrals handling method, one of IConnection.HANDLE_REFERRALS_IGNORE or IConnection.HANDLE_REFERRALS_FOLLOW. */
    private int referralsHandlingMethod;

    /** The controls */
    private Control[] controls;

    /** Flag indicating wether the hasChildren flag of IEntry should be initialized */
    private boolean initChildrenFlag;

    /** Flag indicating wether the isAlias and isReferral flag of IEntry should be initialized */
    private boolean initAliasAndReferralFlag;


    /**
     * Creates a new instance of SearchParameter with default search parameters:
     * <ul>
     * <li>Filter (objectClass=*)
     * <li>Object scope
     * <li>No time limit
     * <li<No count limit
     * <li>Never dereference aliases
     * <li>Ignore referrals
     * <li>No controls
     * <li>Don't initialize hasChildren flag
     * <li>Don't initialize isAlias and isReferral flag
     * </ul>
     */
    public SearchParameter()
    {
        filter = ISearch.FILTER_TRUE;
        scope = ISearch.SCOPE_OBJECT;
        timeLimit = 0;
        countLimit = 0;
        aliasesDereferencingMethod = IConnection.DEREFERENCE_ALIASES_NEVER;
        referralsHandlingMethod = IConnection.HANDLE_REFERRALS_IGNORE;
        controls = null;
        initChildrenFlag = false;
        initAliasAndReferralFlag = false;
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
     * Sets the filter.
     * 
     * @param filter the filter
     */
    public void setFilter( String filter )
    {
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
     * Sets the returning attributes.
     * 
     * @param returningAttributes the returning attributes
     */
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


    /**
     * Gets the scope, one of ISearch.SCOPE_OBJECT, 
     * ISearch.SCOPE_ONELEVEL or ISearch.SCOPE_SUBTREE.
     * 
     * @return the scope
     */
    public int getScope()
    {
        return scope;
    }


    /**
     * Sets the scope, one of ISearch.SCOPE_OBJECT, 
     * ISearch.SCOPE_ONELEVEL or ISearch.SCOPE_SUBTREE.
     * 
     * @param scope the scope
     */
    public void setScope( int scope )
    {
        this.scope = scope;
    }


    /**
     * Gets the aliases dereferencing method, one of IConnection.DEREFERENCE_ALIASES_NEVER, 
     * IConnection.DEREFERENCE_ALIASES_ALWAYS, IConnection.DEREFERENCE_ALIASES_FINDING 
     * or IConnection.DEREFERENCE_ALIASES_SEARCH.
     * 
     * @return the aliases dereferencing method
     */
    public int getAliasesDereferencingMethod()
    {
        return aliasesDereferencingMethod;
    }


    /**
     * Sets the aliases dereferencing method, one of IConnection.DEREFERENCE_ALIASES_NEVER, 
     * IConnection.DEREFERENCE_ALIASES_ALWAYS, IConnection.DEREFERENCE_ALIASES_FINDING 
     * or IConnection.DEREFERENCE_ALIASES_SEARCH.
     * 
     * @param aliasesDereferencingMethod the aliases dereferencing method
     */
    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        this.aliasesDereferencingMethod = aliasesDereferencingMethod;
    }


    /**
     * Gets the referrals handling method, one of IConnection.HANDLE_REFERRALS_IGNORE
     *  or IConnection.HANDLE_REFERRALS_FOLLOW.
     * 
     * @return the referrals handling method
     */
    public int getReferralsHandlingMethod()
    {
        return referralsHandlingMethod;
    }


    /**
     * Sets the referrals handling method, one of IConnection.HANDLE_REFERRALS_IGNORE or 
     * IConnection.HANDLE_REFERRALS_FOLLOW.
     * 
     * @param referralsHandlingMethod the referrals handling method
     */
    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        this.referralsHandlingMethod = referralsHandlingMethod;
    }


    /**
     * Gets the search base.
     * 
     * @return the search base
     */
    public DN getSearchBase()
    {
        return searchBase;
    }


    /**
     * Sets the search base.
     * 
     * @param searchBase the search base
     */
    public void setSearchBase( DN searchBase )
    {
        this.searchBase = searchBase;
    }


    /**
     * Gets the time limit in milliseconds, 0 means no limit.
     * 
     * @return the time limit
     */
    public int getTimeLimit()
    {
        return timeLimit;
    }


    /**
     * Sets the time limit in milliseconds, 0 means no limit.
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
        clone.setInitChildrenFlag( isInitChildrenFlag() );
        clone.setInitAliasAndReferralFlag( isInitAliasAndReferralFlag() );
        clone.setControls( getControls() );
        return clone;
    }


    /**
     * Checks if the hasChildren flag of IEntry should be initialized.
     * 
     * @return true, if the hasChildren flag of IEntry should be initialized
     */
    public boolean isInitAliasAndReferralFlag()
    {
        return initAliasAndReferralFlag;
    }


    /**
     * Sets if the hasChildren flag of IEntry should be initialized.
     * 
     * @param initAliasAndReferralFlag the init alias and referral flag
     */
    public void setInitAliasAndReferralFlag( boolean initAliasAndReferralFlag )
    {
        this.initAliasAndReferralFlag = initAliasAndReferralFlag;
    }


    /**
     * Checks if the hasChildren flag of IEntry should be initialized.
     * 
     * @return true, if the hasChildren flag of IEntry should be initialized
     */
    public boolean isInitChildrenFlag()
    {
        return initChildrenFlag;
    }


    /**
     * Sets if the hasChildren flag of IEntry should be initialized.
     * 
     * @param initChildrenFlag the init children flag
     */
    public void setInitChildrenFlag( boolean initChildrenFlag )
    {
        this.initChildrenFlag = initChildrenFlag;
    }


    /**
     * Gets the controls.
     * 
     * @return the controls
     */
    public Control[] getControls()
    {
        return controls;
    }


    /**
     * Sets the controls.
     * 
     * @param controls the controls
     */
    public void setControls( Control[] controls )
    {
        this.controls = controls;
    }

}
