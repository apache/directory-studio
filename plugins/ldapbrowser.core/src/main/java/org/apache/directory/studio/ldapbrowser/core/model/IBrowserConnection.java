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

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.BookmarkManager;
import org.apache.directory.studio.ldapbrowser.core.SearchManager;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.eclipse.core.runtime.IAdaptable;


/**
 * An IBrowserConnection represents a connection for the LDAP browser.
 * It holds an instance to the underlying connection of the connection plugin,
 * additional it includes advanced connection parameters for the LDAP browser. 
 * It also provides an entry cache.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface IBrowserConnection extends Serializable, IAdaptable, ConnectionPropertyPageProvider
{
    /**
     * Enum for the modify mode of attributes
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    enum ModifyMode
    {
        /** Default mode */
        DEFAULT(0),

        /** Always use replace operation */
        REPLACE(1),

        /** Always use add/delete operation */
        ADD_DELETE(2);

        private final int ordinal;


        private ModifyMode( int ordinal )
        {
            this.ordinal = ordinal;
        }


        /**
         * Gets the ordinal.
         * 
         * @return the ordinal
         */
        public int getOrdinal()
        {
            return ordinal;
        }


        /**
         * Gets the ModifyMode by ordinal.
         * 
         * @param ordinal the ordinal
         * 
         * @return the ModifyMode
         */
        public static ModifyMode getByOrdinal( int ordinal )
        {
            switch ( ordinal )
            {
                case 0:
                    return DEFAULT;
                case 1:
                    return REPLACE;
                case 2:
                    return ADD_DELETE;
                default:
                    return null;
            }
        }
    }

    /**
     * Enum for modify order when using add/delete operations
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    enum ModifyOrder
    {
        /** Delete first */
        DELETE_FIRST(0),

        /** Add first */
        ADD_FIRST(1);

        private final int ordinal;


        private ModifyOrder( int ordinal )
        {
            this.ordinal = ordinal;
        }


        /**
         * Gets the ordinal.
         * 
         * @return the ordinal
         */
        public int getOrdinal()
        {
            return ordinal;
        }


        /**
         * Gets the ModifyOrder by ordinal.
         * 
         * @param ordinal the ordinal
         * 
         * @return the ModifyOrder
         */
        public static ModifyOrder getByOrdinal( int ordinal )
        {
            switch ( ordinal )
            {
                case 0:
                    return DELETE_FIRST;
                case 1:
                    return ADD_FIRST;
                default:
                    return null;
            }
        }
    }

    /** The key for the connection parameter "Get Base DNs from Root DSE". */
    String CONNECTION_PARAMETER_FETCH_BASE_DNS = "ldapbrowser.fetchBaseDns"; //$NON-NLS-1$

    /** The key for the connection parameter "Base Dn". */
    String CONNECTION_PARAMETER_BASE_DN = "ldapbrowser.baseDn"; //$NON-NLS-1$

    /** The key for the connection parameter "Count Limit". */
    String CONNECTION_PARAMETER_COUNT_LIMIT = "ldapbrowser.countLimit"; //$NON-NLS-1$

    /** The key for the connection parameter "Time Limit". */
    String CONNECTION_PARAMETER_TIME_LIMIT = "ldapbrowser.timeLimit"; //$NON-NLS-1$

    /** The key for the connection parameter "Alias Dereferencing". */
    String CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD = "ldapbrowser.aliasesDereferencingMethod"; //$NON-NLS-1$

    /** The key for the connection parameter "Referrals Handling". */
    String CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD = "ldapbrowser.referralsHandlingMethod"; //$NON-NLS-1$

    /** The key for the connection parameter "Fetch Operational Attributes. */
    String CONNECTION_PARAMETER_FETCH_OPERATIONAL_ATTRIBUTES = "ldapbrowser.fetchOperationalAttributes"; //$NON-NLS-1$

    /** The key for the connection parameter "Fetch Sub-entries". */
    String CONNECTION_PARAMETER_FETCH_SUBENTRIES = "ldapbrowser.fetchSubentries"; //$NON-NLS-1$

    /** The key for the connection parameter "Paged Search". */
    String CONNECTION_PARAMETER_PAGED_SEARCH = "ldapbrowser.pagedSearch"; //$NON-NLS-1$

    /** The key for the connection parameter "Paged Search Size". */
    String CONNECTION_PARAMETER_PAGED_SEARCH_SIZE = "ldapbrowser.pagedSearchSize"; //$NON-NLS-1$

    /** The key for the connection parameter "Paged Search Scroll Mode". */
    String CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE = "ldapbrowser.pagedSearchScrollMode"; //$NON-NLS-1$

    /** The key for the connection parameter "Modify Mode for attributes with equality matching rule". */
    String CONNECTION_PARAMETER_MODIFY_MODE = "ldapbrowser.modifyMode"; //$NON-NLS-1$

    /** The key for the connection parameter "Modify Mode for attributes without equality matching rule". */
    String CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR = "ldapbrowser.modifyModeNoEMR"; //$NON-NLS-1$

    /** The key for the connection parameter "Modify add delete order". */
    String CONNECTION_PARAMETER_MODIFY_ORDER = "ldapbrowser.modifyOrder"; //$NON-NLS-1$

    /** The key for the connection parameter "Use ManageDsaIT Control" */
    String CONNECTION_PARAMETER_MANAGE_DSA_IT = "ldapbrowser.manageDsaIT"; //$NON-NLS-1$

    /**
     * Gets the URL of this connection.
     * 
     * @return the URL of this connection
     */
    LdapUrl getUrl();


    /**
     * Gets the flag whether the base DNs is retrieved
     * from Root DSE or whether the base Dn is defined manually.
     * 
     * @return true, if the base DNs are fetched from Root DSE, 
     *         false, if the base Dn is defined manually
     */
    boolean isFetchBaseDNs();


    /**
     * Sets the flag whether the base DNs should be retrieved
     * from Root DSE or whether the base Dn is defined manually.
     * 
     * @param fetchBaseDNs true to get the base DNs from Root DSE,
     *                     false to define one manually
     */
    void setFetchBaseDNs( boolean fetchBaseDNs );


    /**
     * Gets the manually defined base Dn.
     * 
     * @return the manually defined base ND
     */
    Dn getBaseDN();


    /**
     * Sets the manually defined base Dn.
     * 
     * @param baseDn the new base Dn
     */
    void setBaseDN( Dn baseDn );


    /**
     * Gets the count limit.
     * 
     * @return the count limit
     */
    int getCountLimit();


    /**
     * Sets the count limit.
     * 
     * @param countLimit the new count limit
     */
    void setCountLimit( int countLimit );


    /**
     * Gets the aliases dereferencing method.
     * 
     * @return the aliases dereferencing method
     */
    AliasDereferencingMethod getAliasesDereferencingMethod();


    /**
     * Sets the aliases dereferencing method.
     * 
     * @param aliasesDereferencingMethod the new aliases dereferencing method
     */
    void setAliasesDereferencingMethod( AliasDereferencingMethod aliasesDereferencingMethod );


    /**
     * Gets the referrals handling method.
     * 
     * @return the referrals handling method
     */
    ReferralHandlingMethod getReferralsHandlingMethod();


    /**
     * Sets the referrals handling method.
     * 
     * @param referralsHandlingMethod the new referrals handling method
     */
    void setReferralsHandlingMethod( ReferralHandlingMethod referralsHandlingMethod );


    /**
     * Gets the time limit.
     * 
     * @return the time limit
     */
    int getTimeLimit();


    /**
     * Sets the time limit.
     * 
     * @param timeLimit the new time limit
     */
    void setTimeLimit( int timeLimit );


    /**
     * Checks if subentries should be fetched.
     * 
     * @return the true if subentries should be fetched
     */
    boolean isFetchSubentries();


    /**
     * Sets if subentries should be fetched.
     * 
     * @param fetchSubentries true to fetch subentries
     */
    void setFetchSubentries( boolean fetchSubentries );


    /**
     * Checks if ManageDsaIT control should be used.
     * 
     * @return true if ManageDsaIT control should be used
     */
    boolean isManageDsaIT();


    /**
     * Sets if ManageDsaIT control should be used.
     * 
     * @param manageDsaIT true to use ManageDsaIT control
     */
    void setManageDsaIT( boolean manageDsaIT );


    /**
     * Checks if operational attributes should be fetched.
     * 
     * @return the true if operational attributes should be fetched
     */
    boolean isFetchOperationalAttributes();


    /**
     * Sets if operational attributes should be fetched.
     * 
     * @param fetchSubentries true to fetch operational attributes
     */
    void setFetchOperationalAttributes( boolean fetchOperationalAttributes );


    /**
     * Checks if paged search should be used.
     * 
     * @return the true if paged search should be used
     */
    boolean isPagedSearch();


    /**
     * Sets if paged search should be used.
     * 
     * @param pagedSearch true to use paged search
     */
    void setPagedSearch( boolean pagedSearch );


    /**
     * Gets the paged search size.
     * 
     * @return the paged search size
     */
    int getPagedSearchSize();


    /**
     * Sets the paged search size.
     * 
     * @param pagedSearchSize the new paged search size
     */
    void setPagedSearchSize( int pagedSearchSize );


    /**
     * Checks if paged search scroll mode should be used.
     * 
     * @return the true if paged search scroll mode should be used
     */
    boolean isPagedSearchScrollMode();


    /**
     * Sets if paged search scroll mode should be used.
     * 
     * @param pagedSearch true to use paged search scroll mode
     */
    void setPagedSearchScrollMode( boolean pagedSearchScrollMode );


    /**
     * Gets the modify mode for attributes.
     * 
     * @return the modify mode for attributes
     */
    ModifyMode getModifyMode();


    /**
     * Sets the modify mode for attributes.
     * 
     * @param mode the modify mode for attributes
     */
    void setModifyMode( ModifyMode mode );


    /**
     * Gets the modify mode for attributes without equality matching rule.
     * 
     * @return the modify mode for attributes without equality matching rule
     */
    ModifyMode getModifyModeNoEMR();


    /**
     * Sets the modify mode for attributes without equality matching rule.
     * 
     * @param mode the modify mode for attributes without equality matching rule
     */
    void setModifyModeNoEMR( ModifyMode mode );


    /**
     * Gets the modify add/delete order.
     * 
     * @return the modify add/delete order
     */
    ModifyOrder getModifyAddDeleteOrder();


    /**
     * Sets the modify add/delete order.
     * 
     * @param mode the modify add/delete order
     */
    void setModifyAddDeleteOrder( ModifyOrder mode );


    /**
     * Gets the root DSE.
     * 
     * @return the root DSE
     */
    IRootDSE getRootDSE();


    /**
     * Gets the schema.
     * 
     * @return the schema, never null
     */
    Schema getSchema();


    /**
     * Sets the schema.
     * 
     * @param schema the new schema
     */
    void setSchema( Schema schema );


    /**
     * Gets the search manager.
     * 
     * @return the search manager
     */
    SearchManager getSearchManager();


    /**
     * Gets the bookmark manager.
     * 
     * @return the bookmark manager
     */
    BookmarkManager getBookmarkManager();


    /**
     * Gets the entry from cache.
     * 
     * @param dn the Dn of the entry
     * 
     * @return the entry from cache or null if the entry isn't cached
     */
    IEntry getEntryFromCache( Dn dn );


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    Connection getConnection();


    /**
     * Puts the entry to the cache.
     * 
     * @param entry the entry to cache
     */
    void cacheEntry( IEntry entry );


    /**
     * Removes the entry and all children recursively from the cache.
     * 
     * @param entry the entry to remove from cache
     */
    void uncacheEntryRecursive( IEntry entry );


    /**
     * Clears all caches.
     */
    void clearCaches();
}