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

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.BookmarkManager;
import org.apache.directory.studio.ldapbrowser.core.SearchManager;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ModificationLogger;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.eclipse.core.runtime.IAdaptable;


public interface IBrowserConnection extends Serializable, IAdaptable, ConnectionPropertyPageProvider
{

    public static String CONNECTION_PARAMETER_FETCH_BASE_DNS = "ldapbrowser.fetchBaseDns";
    public static String CONNECTION_PARAMETER_BASE_DN = "ldapbrowser.baseDn";
    public static String CONNECTION_PARAMETER_COUNT_LIMIT = "ldapbrowser.countLimit";
    public static String CONNECTION_PARAMETER_TIME_LIMIT = "ldapbrowser.timeLimit";
    public static String CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD = "ldapbrowser.aliasesDereferencingMethod";
    public static String CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD = "ldapbrowser.referralsHandlingMethod";
    
    
    public static final String[] ROOT_DSE_ATTRIBUTES =
        { IRootDSE.ROOTDSE_ATTRIBUTE_MONITORCONTEXT, IRootDSE.ROOTDSE_ATTRIBUTE_NAMINGCONTEXTS,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDLDAPVERSION, IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY,
            IRootDSE.ROOTDSE_ATTRIBUTE_ALTSERVER, IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDEXTENSION,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL, IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDFEATURES,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDSASLMECHANISM, ISearch.ALL_OPERATIONAL_ATTRIBUTES,
            ISearch.ALL_USER_ATTRIBUTES };

    
    /**
     * Enum for the used alias dereferencing method.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public enum AliasDereferencingMethod
    {

        /** Never. */
        NEVER(0),

        /** Always. */
        ALWAYS(1),

        /** Finding. */
        FINDING(2),

        /** Search. */
        SEARCH(3);

        private final int ordinal;


        private AliasDereferencingMethod( int ordinal )
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
         * Gets the AliasDereferencingMethod by ordinal.
         * 
         * @param ordinal the ordinal
         * 
         * @return the AliasDereferencingMethod
         */
        public static AliasDereferencingMethod getByOrdinal( int ordinal )
        {
            switch ( ordinal )
            {
                case 0:
                    return NEVER;
                case 1:
                    return ALWAYS;
                case 2:
                    return FINDING;
                case 3:
                    return SEARCH;
                default:
                    return null;
            }
        }
    }

    /**
     * Enum for the used referral handling method.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public enum ReferralHandlingMethod
    {

        /** Ignore. */
        IGNORE(0),

        /** Follow. */
        FOLLOW(1),

        /** Manual. */
        MANUAL(2);

        private final int ordinal;


        private ReferralHandlingMethod( int ordinal )
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
         * Gets the ReferralHandlingMethod by ordinal.
         * 
         * @param ordinal the ordinal
         * 
         * @return the ReferralHandlingMethod
         */
        public static ReferralHandlingMethod getByOrdinal( int ordinal )
        {
            switch ( ordinal )
            {
                case 0:
                    return IGNORE;
                case 1:
                    return FOLLOW;
                case 2:
                    return MANUAL;
                default:
                    return null;
            }
        }
    }
    
//    public static final int DEREFERENCE_ALIASES_NEVER = 0;
//
//    public static final int DEREFERENCE_ALIASES_ALWAYS = 1;
//
//    public static final int DEREFERENCE_ALIASES_FINDING = 2;
//
//    public static final int DEREFERENCE_ALIASES_SEARCH = 3;
//
//    public static final int HANDLE_REFERRALS_IGNORE = 0;
//
//    public static final int HANDLE_REFERRALS_FOLLOW = 1;

    public static final String CONTROL_MANAGEDSAIT = "2.16.840.1.113730.3.4.2"; //$NON-NLS-1$


    public abstract URL getUrl();


    public abstract boolean isFetchBaseDNs();


    public abstract void setFetchBaseDNs( boolean fetchBaseDNs );


    public abstract DN getBaseDN();


    public abstract void setBaseDN( DN baseDN );


    public abstract int getCountLimit();


    public abstract void setCountLimit( int countLimit );


    public abstract AliasDereferencingMethod getAliasesDereferencingMethod();


    public abstract void setAliasesDereferencingMethod( AliasDereferencingMethod aliasesDereferencingMethod );


    public abstract ReferralHandlingMethod getReferralsHandlingMethod();


    public abstract void setReferralsHandlingMethod( ReferralHandlingMethod referralsHandlingMethod );


    public abstract int getTimeLimit();


    public abstract void setTimeLimit( int timeLimit );


    /**
     * Gets the root DSE.
     * 
     * @return the root DSE
     */
    public abstract IRootDSE getRootDSE();


    public abstract Schema getSchema();


    public abstract void setSchema( Schema schema );


    public abstract SearchManager getSearchManager();


    public abstract BookmarkManager getBookmarkManager();


    public abstract ModificationLogger getModificationLogger();


    public abstract IEntry getEntryFromCache( DN dn );


    public abstract Connection getConnection();
    
    public abstract int hashCode();

    public abstract boolean equals( Object obj );
    
    public void cacheEntry( IEntry entry );
    public abstract void uncacheEntryRecursive( IEntry entry );
}