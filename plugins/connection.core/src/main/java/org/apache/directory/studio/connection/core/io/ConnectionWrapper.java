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

import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.Control;

import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.io.jndi.ReferralsInfo;
import org.apache.directory.studio.connection.core.io.jndi.JndiStudioNamingEnumeration;


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
    public void connect( StudioProgressMonitor monitor );


    /**
     * Disconnects from the directory server.
     */
    public void disconnect();


    /**
     * Binds to the directory server.
     * 
     * @param monitor the progress monitor
     */
    public void bind( StudioProgressMonitor monitor );


    /**
     * Unbinds from the directory server.
     */
    public void unbind();


    /**
     * Checks if is connected.
     * 
     * @return true, if is connected
     */
    public boolean isConnected();


    /**
     * Sets the binary attributes.
     * 
     * @param binaryAttributes the binary attributes
     */
    public void setBinaryAttributes( Collection<String> binaryAttributes );


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
    public StudioNamingEnumeration search( final String searchBase, final String filter,
        final SearchControls searchControls, final AliasDereferencingMethod aliasesDereferencingMethod,
        final ReferralHandlingMethod referralsHandlingMethod, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo );


    /**
     * Modifies attributes of an entry.
     * 
     * @param dn the Dn
     * @param modificationItems the modification items
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void modifyEntry( final String dn, final ModificationItem[] modificationItems, final Control[] controls,
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
    public void renameEntry( final String oldDn, final String newDn, final boolean deleteOldRdn,
        final Control[] controls, final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo );


    /**
     * Creates an entry.
     * 
     * @param dn the entry's Dn
     * @param attributes the entry's attributes
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void createEntry( final String dn, final Attributes attributes, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo );


    /**
     * Deletes an entry.
     * 
     * @param dn the Dn of the entry to delete
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void deleteEntry( final String dn, final Control[] controls, final StudioProgressMonitor monitor,
        final ReferralsInfo referralsInfo );
}
