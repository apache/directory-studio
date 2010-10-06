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

import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
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
    public enum ModifyMode
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
    public enum ModifyOrder
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
    public static String CONNECTION_PARAMETER_FETCH_BASE_DNS = "ldapbrowser.fetchBaseDns";

    /** The key for the connection parameter "Base DN". */
    public static String CONNECTION_PARAMETER_BASE_DN = "ldapbrowser.baseDn";

    /** The key for the connection parameter "Count Limit". */
    public static String CONNECTION_PARAMETER_COUNT_LIMIT = "ldapbrowser.countLimit";

    /** The key for the connection parameter "Time Limit". */
    public static String CONNECTION_PARAMETER_TIME_LIMIT = "ldapbrowser.timeLimit";

    /** The key for the connection parameter "Alias Dereferencing". */
    public static String CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD = "ldapbrowser.aliasesDereferencingMethod";

    /** The key for the connection parameter "Referrals Handling". */
    public static String CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD = "ldapbrowser.referralsHandlingMethod";

    /** The key for the connection parameter "Fetch Operational Attributes. */
    public static String CONNECTION_PARAMETER_FETCH_OPERATIONAL_ATTRIBUTES = "ldapbrowser.fetchOperationalAttributes";

    /** The key for the connection parameter "Fetch Sub-entries". */
    public static String CONNECTION_PARAMETER_FETCH_SUBENTRIES = "ldapbrowser.fetchSubentries";

    /** The key for the connection parameter "Paged Search". */
    public static String CONNECTION_PARAMETER_PAGED_SEARCH = "ldapbrowser.pagedSearch";

    /** The key for the connection parameter "Paged Search Size". */
    public static String CONNECTION_PARAMETER_PAGED_SEARCH_SIZE = "ldapbrowser.pagedSearchSize";

    /** The key for the connection parameter "Paged Search Scroll Mode". */
    public static String CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE = "ldapbrowser.pagedSearchScrollMode";

    /** The key for the connection parameter "Modify Mode for attributes with equality matching rule". */
    public static String CONNECTION_PARAMETER_MODIFY_MODE = "ldapbrowser.modifyMode";

    /** The key for the connection parameter "Modify Mode for attributes without equality matching rule". */
    public static String CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR = "ldapbrowser.modifyModeNoEMR";

    /** The key for the connection parameter "Modify add delete order". */
    public static String CONNECTION_PARAMETER_MODIFY_ORDER = "ldapbrowser.modifyOrder";

    /** The key for the connection parameter "Use ManageDsaIT Control" */
    public static String CONNECTION_PARAMETER_MANAGE_DSA_IT = "ldapbrowser.manageDsaIT";


    /**
     * Gets the URL of this connection.
     * 
     * @return the URL of this connection
     */
    public abstract LdapURL getUrl();


    /**
     * Gets the flag whether the base DNs is retrieved
     * from Root DSE or whether the base DN is defined manually.
     * 
     * @return true, if the base DNs are fetched from Root DSE, 
     *         false, if the base DN is defined manually
     */
    public abstract boolean isFetchBaseDNs();


    /**
     * Sets the flag whether the base DNs should be retrieved
     * from Root DSE or whether the base DN is defined manually.
     * 
     * @param fetchBaseDNs true to get the base DNs from Root DSE,
     *                     false to define one manually
     */
    public abstract void setFetchBaseDNs( boolean fetchBaseDNs );


    /**
     * Gets the manually defined base DN.
     * 
     * @return the manually defined base ND
     */
    public abstract DN getBaseDN();


    /**
     * Sets the manually defined base DN.
     * 
     * @param baseDN the new base DN
     */
    public abstract void setBaseDN( DN baseDN );


    /**
     * Gets the count limit.
     * 
     * @return the count limit
     */
    public abstract int getCountLimit();


    /**
     * Sets the count limit.
     * 
     * @param countLimit the new count limit
     */
    public abstract void setCountLimit( int countLimit );


    /**
     * Gets the aliases dereferencing method.
     * 
     * @return the aliases dereferencing method
     */
    public abstract AliasDereferencingMethod getAliasesDereferencingMethod();


    /**
     * Sets the aliases dereferencing method.
     * 
     * @param aliasesDereferencingMethod the new aliases dereferencing method
     */
    public abstract void setAliasesDereferencingMethod( AliasDereferencingMethod aliasesDereferencingMethod );


    /**
     * Gets the referrals handling method.
     * 
     * @return the referrals handling method
     */
    public abstract ReferralHandlingMethod getReferralsHandlingMethod();


    /**
     * Sets the referrals handling method.
     * 
     * @param referralsHandlingMethod the new referrals handling method
     */
    public abstract void setReferralsHandlingMethod( ReferralHandlingMethod referralsHandlingMethod );


    /**
     * Gets the time limit.
     * 
     * @return the time limit
     */
    public abstract int getTimeLimit();


    /**
     * Sets the time limit.
     * 
     * @param timeLimit the new time limit
     */
    public abstract void setTimeLimit( int timeLimit );


    /**
     * Checks if subentries should be fetched.
     * 
     * @return the true if subentries should be fetched
     */
    public abstract boolean isFetchSubentries();


    /**
     * Sets if subentries should be fetched.
     * 
     * @param fetchSubentries true to fetch subentries
     */
    public abstract void setFetchSubentries( boolean fetchSubentries );


    /**
     * Checks if ManageDsaIT control should be used.
     * 
     * @return true if ManageDsaIT control should be used
     */
    public abstract boolean isManageDsaIT();


    /**
     * Sets if ManageDsaIT control should be used.
     * 
     * @param manageDsaIT true to use ManageDsaIT control
     */
    public abstract void setManageDsaIT( boolean manageDsaIT );


    /**
     * Checks if operational attributes should be fetched.
     * 
     * @return the true if operational attributes should be fetched
     */
    public abstract boolean isFetchOperationalAttributes();


    /**
     * Sets if operational attributes should be fetched.
     * 
     * @param fetchSubentries true to fetch operational attributes
     */
    public abstract void setFetchOperationalAttributes( boolean fetchOperationalAttributes );


    /**
     * Checks if paged search should be used.
     * 
     * @return the true if paged search should be used
     */
    public abstract boolean isPagedSearch();


    /**
     * Sets if paged search should be used.
     * 
     * @param pagedSearch true to use paged search
     */
    public abstract void setPagedSearch( boolean pagedSearch );


    /**
     * Gets the paged search size.
     * 
     * @return the paged search size
     */
    public abstract int getPagedSearchSize();


    /**
     * Sets the paged search size.
     * 
     * @param pagedSearchSize the new paged search size
     */
    public abstract void setPagedSearchSize( int pagedSearchSize );


    /**
     * Checks if paged search scroll mode should be used.
     * 
     * @return the true if paged search scroll mode should be used
     */
    public abstract boolean isPagedSearchScrollMode();


    /**
     * Sets if paged search scroll mode should be used.
     * 
     * @param pagedSearch true to use paged search scroll mode
     */
    public abstract void setPagedSearchScrollMode( boolean pagedSearchScrollMode );


    /**
     * Gets the modify mode for attributes.
     * 
     * @return the modify mode for attributes
     */
    public abstract ModifyMode getModifyMode();


    /**
     * Sets the modify mode for attributes.
     * 
     * @param mode the modify mode for attributes
     */
    public abstract void setModifyMode( ModifyMode mode );


    /**
     * Gets the modify mode for attributes without equality matching rule.
     * 
     * @return the modify mode for attributes without equality matching rule
     */
    public abstract ModifyMode getModifyModeNoEMR();


    /**
     * Sets the modify mode for attributes without equality matching rule.
     * 
     * @param mode the modify mode for attributes without equality matching rule
     */
    public abstract void setModifyModeNoEMR( ModifyMode mode );


    /**
     * Gets the modify add/delete order.
     * 
     * @return the modify add/delete order
     */
    public abstract ModifyOrder getModifyAddDeleteOrder();


    /**
     * Sets the modify add/delete order.
     * 
     * @param mode the modify add/delete order
     */
    public abstract void setModifyAddDeleteOrder( ModifyOrder mode );


    /**
     * Gets the root DSE.
     * 
     * @return the root DSE
     */
    public abstract IRootDSE getRootDSE();


    /**
     * Gets the schema.
     * 
     * @return the schema, never null
     */
    public abstract Schema getSchema();


    /**
     * Sets the schema.
     * 
     * @param schema the new schema
     */
    public abstract void setSchema( Schema schema );


    /**
     * Gets the search manager.
     * 
     * @return the search manager
     */
    public abstract SearchManager getSearchManager();


    /**
     * Gets the bookmark manager.
     * 
     * @return the bookmark manager
     */
    public abstract BookmarkManager getBookmarkManager();


    /**
     * Gets the entry from cache.
     * 
     * @param dn the DN of the entry
     * 
     * @return the entry from cache or null if the entry isn't cached
     */
    public abstract IEntry getEntryFromCache( DN dn );


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    public abstract Connection getConnection();


    /**
     * Puts the entry to the cache.
     * 
     * @param entry the entry to cache
     */
    public void cacheEntry( IEntry entry );


    /**
     * Removes the entry and all children recursively from the cache.
     * 
     * @param entry the entry to remove from cache
     */
    public abstract void uncacheEntryRecursive( IEntry entry );


    /**
     * Clears all caches.
     */
    public abstract void clearCaches();

}