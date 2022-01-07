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
package org.apache.directory.studio.connection.core;


import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.naming.directory.SearchControls;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.message.Control;
import org.apache.directory.api.ldap.model.message.Referral;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.io.StudioLdapException;
import org.apache.directory.studio.connection.core.io.api.StudioSearchResult;
import org.eclipse.core.runtime.Platform;


/**
 * Callback interface to log modifications.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ILdapLogger
{

    /**
     * Logs a changetype:add.
     * 
     * @param connection the connection
     * @param entry the entry
     * @param controls the controls
     * @param ex the LDAP exception if an error occurred, null otherwise
     */
    default void logChangetypeAdd( Connection connection, final Entry entry, final Control[] controls,
        StudioLdapException ex )
    {
    }


    /**
     * Logs a changetype:delete.
     * 
     * @param connection the connection
     * @param dn the Dn
     * @param controls the controls
     * @param ex the LDAP exception if an error occurred, null otherwise
     * 
     */
    default void logChangetypeDelete( Connection connection, final Dn dn, final Control[] controls,
        StudioLdapException ex )
    {

    }


    /**
     * Logs a changetype:modify.
     * 
     * @param connection the connection
     * @param dn the Dn
     * @param modifications the modification items
     * @param ex the LDAP exception if an error occurred, null otherwise
     * @param controls the controls
     */
    default void logChangetypeModify( Connection connection, final Dn dn,
        final Collection<Modification> modifications, final Control[] controls, StudioLdapException ex )
    {
    }


    /**
     * Logs a changetype:moddn.
     * 
     * @param connection the connection
     * @param oldDn the old Dn
     * @param newDn the new Dn
     * @param deleteOldRdn the delete old Rdn
     * @param controls the controls
     * @param ex the LDAP exception if an error occurred, null otherwise
     */
    default void logChangetypeModDn( Connection connection, final Dn oldDn, final Dn newDn,
        final boolean deleteOldRdn, final Control[] controls, StudioLdapException ex )
    {
    }


    /**
     * Sets the logger ID.
     * 
     * @param id the new logger ID
     */
    void setId( String id );


    /**
     * Gets the logger ID.
     * 
     * @return the logger ID
     */
    String getId();


    /**
     * Sets the logger name.
     * 
     * @param name the new logger name
     */
    void setName( String name );


    /**
     * Gets the logger name.
     * 
     * @return the logger name
     */
    String getName();


    /**
     * Sets the logger description.
     * 
     * @param description the new logger description
     */
    void setDescription( String description );


    /**
     * Gets the logger description.
     * 
     * @return the logger description
     */
    String getDescription();


    /**
     * Logs a search request.
     *
     * @param connection the connection
     * @param searchBase the search base
     * @param filter the filter
     * @param searchControls the search controls
     * @param aliasesDereferencingMethod the aliases dereferncing method
     * @param controls the LDAP controls 
     * @param requestNum the request number
     * @param ex the LDAP exception if an error occurred, null otherwise
     */
    default void logSearchRequest( Connection connection, String searchBase, String filter,
        SearchControls searchControls, AliasDereferencingMethod aliasesDereferencingMethod,
        Control[] controls, long requestNum, StudioLdapException ex )
    {
    }


    /**
     * Logs a search result entry.
     * 
     * @param connection the connection
     * @param studioSearchResult the search result entry
     * @param requestNum the request number
     * @param ex the LDAP exception if an error occurred, null otherwise
     */
    default void logSearchResultEntry( Connection connection, StudioSearchResult studioSearchResult, long requestNum,
        StudioLdapException ex )
    {
    }


    /**
     * Logs a search result reference.
     *
     * @param connection the connection
     * @param referral the referral
     * @param referralsInfo the referrals info containing further URLs and DNs
     * @param requestNum the request number
     * @param ex the LDAP exception if an error occurred, null otherwise
     */
    default void logSearchResultReference( Connection connection, Referral referral,
        ReferralsInfo referralsInfo, long requestNum, StudioLdapException ex )
    {
    }


    /**
     * Logs a search result done.
     *
     * @param connection the connection
     * @param count the number of received entries
     * @param requestNum the request number
     * @param ex the LDAP exception if an error occurred, null otherwise
     */
    default void logSearchResultDone( Connection connection, long count, long requestNum, StudioLdapException ex )
    {
    }

    /**
     * Gets the masked attributes.
     * 
     * @return the masked attributes
     */
    default Set<String> getMaskedAttributes()
    {
        Set<String> maskedAttributes = new HashSet<String>();

        String maskedAttributeString = Platform.getPreferencesService().getString( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES, "", null );
        String[] splitted = maskedAttributeString.split( "," ); //$NON-NLS-1$

        for ( String s : splitted )
        {
            maskedAttributes.add( Strings.toLowerCaseAscii( s ) );
        }

        return maskedAttributes;
    }


    /**
     * Deletes a file. Retries up to 5 times to work around Windows file delete issues.
     */
    default void deleteFileWithRetry( File file )
    {
        for ( int i = 0; i < 6; i++ )
        {
            if ( file != null && file.exists() )
            {
                if ( file.delete() )
                {
                    break;
                }
                try
                {
                    Thread.sleep( 500L );
                }
                catch ( InterruptedException e )
                {
                }
            }
        }
    }
}
