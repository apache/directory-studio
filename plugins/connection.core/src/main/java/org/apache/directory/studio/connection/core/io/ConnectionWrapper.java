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
package org.apache.directory.studio.connection.core.io;


import java.util.Collection;

import javax.naming.directory.SearchControls;
import javax.naming.ldap.Control;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.io.api.StudioSearchResultEnumeration;
import org.apache.directory.studio.connection.core.io.jndi.ReferralsInfo;


/**
 * A ConnectionWrapper is a wrapper for a real directory connection implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ConnectionWrapper
{
    /**
     * Connects to the directory server.
     * 
     * @param monitor the progres monitor
     */
    void connect( StudioProgressMonitor monitor );


    /**
     * Disconnects from the directory server.
     */
    void disconnect();


    /**
     * Binds to the directory server.
     * 
     * @param monitor the progress monitor
     */
    void bind( StudioProgressMonitor monitor );


    /**
     * Unbinds from the directory server.
     */
    void unbind();


    /**
     * Checks if is connected.
     * 
     * @return true, if is connected
     */
    boolean isConnected();


    /**
     * Sets the binary attributes.
     * 
     * @param binaryAttributes the binary attributes
     */
    void setBinaryAttributes( Collection<String> binaryAttributes );


    /**
     * Search.
     * 
     * @param searchBase the search base
     * @param filter the filter
     * @param searchControls the controls
     * @param aliasesDereferencingMethod the aliases dereferencing method
     * @param referralsHandlingMethod the referrals handling method
     * @param controls the LDAP controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     * 
     * @return the naming enumeration or null if an exception occurs.
     */
    StudioSearchResultEnumeration search( final String searchBase, final String filter,
        final SearchControls searchControls, final AliasDereferencingMethod aliasesDereferencingMethod,
        final ReferralHandlingMethod referralsHandlingMethod, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo );


    /**
     * Modifies attributes of an entry.
     * 
     * @param dn the Dn
     * @param modifications the modification items
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    void modifyEntry( final Dn dn, final Collection<Modification> modifications, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo );


    /**
     * Renames an entry.
     * 
     * @param oldDn the old Dn
     * @param newDn the new Dn
     * @param deleteOldRdn true to delete the old Rdn
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    void renameEntry( final Dn oldDn, final Dn newDn, final boolean deleteOldRdn,
        final Control[] controls, final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo );


    /**
     * Creates an entry.
     * 
     * @param entry the entry
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    void createEntry( final Entry entry, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo );


    /**
     * Deletes an entry.
     * 
     * @param dn the Dn of the entry to delete
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    void deleteEntry( final Dn dn, final Control[] controls, final StudioProgressMonitor monitor,
        final ReferralsInfo referralsInfo );
}
