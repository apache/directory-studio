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

import org.apache.directory.shared.ldap.codec.util.LdapURL;
import org.apache.directory.shared.ldap.name.LdapDN;
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
 * @version $Rev$, $Date$
 */
public interface IBrowserConnection extends Serializable, IAdaptable, ConnectionPropertyPageProvider
{

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

    /** The MangageDsaIT control OID. */
    public static final String CONTROL_MANAGEDSAIT = "2.16.840.1.113730.3.4.2"; //$NON-NLS-1$


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
    public abstract LdapDN getBaseDN();


    /**
     * Sets the manually defined base DN.
     * 
     * @param baseDN the new base DN
     */
    public abstract void setBaseDN( LdapDN baseDN );


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
    public abstract IEntry getEntryFromCache( LdapDN dn );


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