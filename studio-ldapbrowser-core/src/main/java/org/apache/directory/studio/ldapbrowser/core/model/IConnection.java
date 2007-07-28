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

import org.apache.directory.studio.ldapbrowser.core.BookmarkManager;
import org.apache.directory.studio.ldapbrowser.core.SearchManager;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ModificationLogger;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.ConnectionPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


public interface IConnection extends Serializable, IAdaptable, ConnectionPropertyPageProvider
{

    public static final String[] ROOT_DSE_ATTRIBUTES =
        { IRootDSE.ROOTDSE_ATTRIBUTE_MONITORCONTEXT, IRootDSE.ROOTDSE_ATTRIBUTE_NAMINGCONTEXTS,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDLDAPVERSION, IRootDSE.ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY,
            IRootDSE.ROOTDSE_ATTRIBUTE_ALTSERVER, IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDEXTENSION,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL, IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDFEATURES,
            IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDSASLMECHANISM, ISearch.ALL_OPERATIONAL_ATTRIBUTES,
            ISearch.ALL_USER_ATTRIBUTES };

    public static final int AUTH_ANONYMOUS = 0;

    public static final int AUTH_SIMPLE = 1;
    
    public static final int AUTH_SASL_DIGMD5 = 2;
    
    public static final int AUTH_SASL_CRAMD5 = 3;

    public static final int ENCYRPTION_NONE = 0;

    public static final int ENCYRPTION_LDAPS = 1;

    public static final int ENCYRPTION_STARTTLS = 2;

    public static final int DEREFERENCE_ALIASES_NEVER = 0;

    public static final int DEREFERENCE_ALIASES_ALWAYS = 1;

    public static final int DEREFERENCE_ALIASES_FINDING = 2;

    public static final int DEREFERENCE_ALIASES_SEARCH = 3;

    public static final int HANDLE_REFERRALS_IGNORE = 0;

    public static final int HANDLE_REFERRALS_FOLLOW = 1;

    public static final String CONTROL_MANAGEDSAIT = "2.16.840.1.113730.3.4.2"; //$NON-NLS-1$


    public abstract URL getUrl();


    public abstract String getName();


    public abstract void setName( String name );


    public abstract boolean isFetchBaseDNs();


    public abstract void setFetchBaseDNs( boolean fetchBaseDNs );


    public abstract DN getBaseDN();


    public abstract void setBaseDN( DN baseDN );


    public abstract int getCountLimit();


    public abstract void setCountLimit( int countLimit );


    public abstract String getHost();


    public abstract void setHost( String host );


    public abstract int getPort();


    public abstract void setPort( int port );


    public abstract int getEncryptionMethod();


    public abstract void setEncryptionMethod( int encryptionMethod );


    public abstract int getAliasesDereferencingMethod();


    public abstract void setAliasesDereferencingMethod( int aliasesDereferencingMethod );


    public abstract int getReferralsHandlingMethod();


    public abstract void setReferralsHandlingMethod( int referralsHandlingMethod );


    public abstract int getTimeLimit();


    public abstract void setTimeLimit( int timeLimit );


    public String getBindPrincipal();


    public void setBindPrincipal( String bindPrincipal );


    public String getBindPassword();


    public void setBindPassword( String bindPassword );


    public int getAuthMethod();


    public void setAuthMethod( int authMethod );


    public abstract IRootDSE getRootDSE();


    public abstract Schema getSchema();


    public abstract void setSchema( Schema schema );


    public abstract ConnectionParameter getConnectionParameter();


    public abstract void setConnectionParameter( ConnectionParameter connectionParameter );


    public abstract SearchManager getSearchManager();


    public abstract BookmarkManager getBookmarkManager();


    public abstract ModificationLogger getModificationLogger();


    public abstract void reloadSchema( ExtendedProgressMonitor monitor );


    /**
     * Connects to the LDAP server without any authentification:
     * 
     * @param pm
     *                The progress monitor
     */
    public abstract void connect( ExtendedProgressMonitor monitor );


    /**
     * Binds to the LDAP server using the required authentification. Depends
     * on connect().
     * 
     * @param pm
     *                The progress monitor
     */
    public abstract void bind( ExtendedProgressMonitor monitor );


    /**
     * Fetches the Root DSE and tries to get the base DNs from Root DSE.
     * Depends on bind().
     * 
     * @param pm
     *                The progress monitor
     */
    public abstract void fetchRootDSE( ExtendedProgressMonitor monitor );


    /**
     * Opens the connection to the LDAP server and loads the schema if
     * required. Depends on fetchRootDSE().
     * 
     * @param pm
     *                The progress monitor
     */
    public abstract void open( ExtendedProgressMonitor monitor );


    public abstract boolean isOpened();


    public abstract boolean canOpen();


    public abstract boolean canClose();


    public abstract void close();


    public abstract boolean existsEntry( DN dn, ExtendedProgressMonitor monitor );


    public abstract IEntry getEntry( DN dn, ExtendedProgressMonitor monitor );


    public abstract IEntry getEntryFromCache( DN dn );


    public abstract void search( ISearch searchRequest, ExtendedProgressMonitor monitor );


    public abstract void delete( IEntry entryToDelete, ExtendedProgressMonitor monitor );


    public abstract void delete( IValue valuesToDelete[], ExtendedProgressMonitor monitor );


    public abstract void delete( IAttribute attriubtesToDelete[], ExtendedProgressMonitor monitor );


    public abstract void create( IEntry entryToCreate, ExtendedProgressMonitor monitor );


    public abstract void create( IValue valuesToCreate[], ExtendedProgressMonitor monitor );


    public abstract void modify( IValue oldValue, IValue newVaue, ExtendedProgressMonitor monitor );


    public abstract void rename( IEntry entryToRename, DN newDn, boolean deleteOldRdn, ExtendedProgressMonitor monitor );


    public abstract void move( IEntry entryToMove, DN newSuperior, ExtendedProgressMonitor monitor );


    public abstract Object clone();


    public abstract void importLdif( LdifEnumeration enumeration, Writer logWriter, boolean continueOnError,
        ExtendedProgressMonitor monitor );


    public abstract LdifEnumeration exportLdif( SearchParameter searchParameter, ExtendedProgressMonitor pm )
        throws ConnectionException;


    /**
     * Suspends the commitment of modifications.
     * 
     */
    public abstract void suspend();


    /**
     * Resume the commitment of modifications.
     * 
     */
    public abstract void resume( ExtendedProgressMonitor monitor );


    /**
     * Resets the state and deletes all uncommitted modifications
     * 
     */
    public abstract void reset();


    public abstract boolean isSuspended();


    public abstract int hashCode();


    public abstract boolean equals( Object obj );
}