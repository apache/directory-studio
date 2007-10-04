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
import java.io.Writer;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.ldapbrowser.core.BookmarkManager;
import org.apache.directory.studio.ldapbrowser.core.SearchManager;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ModificationLogger;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
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

    public static final int DEREFERENCE_ALIASES_NEVER = 0;

    public static final int DEREFERENCE_ALIASES_ALWAYS = 1;

    public static final int DEREFERENCE_ALIASES_FINDING = 2;

    public static final int DEREFERENCE_ALIASES_SEARCH = 3;

    public static final int HANDLE_REFERRALS_IGNORE = 0;

    public static final int HANDLE_REFERRALS_FOLLOW = 1;

    public static final String CONTROL_MANAGEDSAIT = "2.16.840.1.113730.3.4.2"; //$NON-NLS-1$


    public abstract URL getUrl();


    public abstract boolean isFetchBaseDNs();


    public abstract void setFetchBaseDNs( boolean fetchBaseDNs );


    public abstract DN getBaseDN();


    public abstract void setBaseDN( DN baseDN );


    public abstract int getCountLimit();


    public abstract void setCountLimit( int countLimit );


    public abstract int getAliasesDereferencingMethod();


    public abstract void setAliasesDereferencingMethod( int aliasesDereferencingMethod );


    public abstract int getReferralsHandlingMethod();


    public abstract void setReferralsHandlingMethod( int referralsHandlingMethod );


    public abstract int getTimeLimit();


    public abstract void setTimeLimit( int timeLimit );


    public abstract IRootDSE getRootDSE();


    public abstract Schema getSchema();


    public abstract void setSchema( Schema schema );


    public abstract SearchManager getSearchManager();


    public abstract BookmarkManager getBookmarkManager();


    public abstract ModificationLogger getModificationLogger();


    public abstract void reloadSchema( StudioProgressMonitor monitor );


    /**
     * Fetches the Root DSE and tries to get the base DNs from Root DSE.
     * Depends on bind().
     * 
     * @param pm
     *                The progress monitor
     */
    public abstract void fetchRootDSE( StudioProgressMonitor monitor );


    public abstract boolean existsEntry( DN dn, StudioProgressMonitor monitor );


    public abstract IEntry getEntry( DN dn, StudioProgressMonitor monitor );


    public abstract IEntry getEntryFromCache( DN dn );


    public abstract void search( ISearch searchRequest, StudioProgressMonitor monitor );


    public abstract void delete( IEntry entryToDelete, StudioProgressMonitor monitor );


    public abstract void delete( IValue valuesToDelete[], StudioProgressMonitor monitor );


    public abstract void delete( IAttribute attriubtesToDelete[], StudioProgressMonitor monitor );


    public abstract void create( IEntry entryToCreate, StudioProgressMonitor monitor );


    public abstract void create( IValue valuesToCreate[], StudioProgressMonitor monitor );


    public abstract void modify( IValue oldValue, IValue newVaue, StudioProgressMonitor monitor );


    public abstract void rename( IEntry entryToRename, DN newDn, boolean deleteOldRdn, StudioProgressMonitor monitor );


    public abstract void move( IEntry entryToMove, DN newSuperior, StudioProgressMonitor monitor );


    public abstract void importLdif( LdifEnumeration enumeration, Writer logWriter, boolean continueOnError,
        StudioProgressMonitor monitor );


    public abstract LdifEnumeration exportLdif( SearchParameter searchParameter, StudioProgressMonitor pm )
        throws ConnectionException;


    public abstract Connection getConnection();
    
    public abstract int hashCode();


    public abstract boolean equals( Object obj );
}