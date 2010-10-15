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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;

import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.shared.ldap.cursor.Cursor;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.message.ArrayNamingEnumeration;
import org.apache.directory.shared.ldap.message.Response;
import org.apache.directory.shared.ldap.message.SearchResultEntry;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.apache.directory.shared.ldap.util.JndiUtils;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.io.jndi.ReferralsInfo;
import org.apache.directory.studio.connection.core.io.jndi.StudioNamingEnumeration;


/**
 * A ConnectionWrapper is a wrapper for a real directory connection implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DirectoryApiConnectionWrapper implements ConnectionWrapper
{
    private Connection connection;

    private LdapNetworkConnection ldapConnection;


    /**
     * Creates a new instance of JNDIConnectionContext.
     * 
     * @param connection the connection
     */
    public DirectoryApiConnectionWrapper( Connection connection )
    {
        this.connection = connection;
    }


    private LdapNetworkConnection getLdapConnection()
    {
        if ( ldapConnection != null )
        {
            return ldapConnection;
        }

        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost( connection.getHost() );
        config.setLdapPort( connection.getPort() );
        config.setName( connection.getBindPrincipal() );
        config.setCredentials( connection.getBindPassword() );

        ldapConnection = new LdapNetworkConnection( config );

        return ldapConnection;
    }


    /**
     * Connects to the directory server.
     * 
     * @param monitor the progres monitor
     */
    public void connect( StudioProgressMonitor monitor )
    {
        try
        {
            System.out.println( "connect" );
            boolean bool = getLdapConnection().connect();
            System.out.println( "connect done " + bool );
        }
        catch ( LdapException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Disconnects from the directory server.
     */
    public void disconnect()
    {
        try
        {
            getLdapConnection().close();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Binds to the directory server.
     * 
     * @param monitor the progress monitor
     */
    public void bind( StudioProgressMonitor monitor )
    {
        try
        {
            System.out.println( "Bind" );
            getLdapConnection().bind( getLdapConnection().getConfig().getName(),
                getLdapConnection().getConfig().getCredentials() );
            System.out.println( "Bind done" );
        }
        catch ( LdapException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Unbinds from the directory server.
     */
    public void unbind()
    {
        try
        {
            getLdapConnection().unBind();
        }
        catch ( LdapException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Checks if is connected.
     * 
     * @return true, if is connected
     */
    public boolean isConnected()
    {
        return getLdapConnection().isConnected();
    }


    /**
     * Sets the binary attributes.
     * 
     * @param binaryAttributes the binary attributes
     */
    public void setBinaryAttributes( Collection<String> binaryAttributes )
    {
    }


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
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        System.out.println( "search" );

        try
        {
            Cursor<Response> cursor = getLdapConnection().search( new DN( searchBase ), filter,
                getSearchScope( searchControls ), searchControls.getReturningAttributes() );

            List<SearchResult> searchResults = new ArrayList<SearchResult>();

            System.out.println( "base: " + searchBase );
            System.out.println( "filter: " + filter );
            System.out.println( "searchControls: " + searchControls );

            while ( cursor.next() )
            {
                System.out.println( "cursor.next()" );
                Response response = cursor.get();
                if ( response instanceof SearchResultEntry )
                {
                    SearchResultEntry sre = ( SearchResultEntry ) response;
                    SearchResult sr = new SearchResult( sre.getObjectName().toString(), null,
                        AttributeUtils.toAttributes( sre.getEntry() ) );
                    sr.setNameInNamespace( sre.getObjectName().toString() );
                    searchResults.add( sr );
                }
            }

            NamingEnumeration<SearchResult> ne = new ArrayNamingEnumeration<SearchResult>(
                searchResults.toArray( new SearchResult[0] ) );

            return new StudioNamingEnumeration( connection, null, ne, null, searchBase, filter, searchControls,
                aliasesDereferencingMethod, referralsHandlingMethod, controls, 1, monitor, referralsInfo );

        }
        catch ( LdapInvalidDnException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( LdapException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }


    private SearchScope getSearchScope( SearchControls searchControls )
    {
        int scope = searchControls.getSearchScope();
        if ( scope == SearchControls.OBJECT_SCOPE )
        {
            return SearchScope.OBJECT;
        }
        else if ( scope == SearchControls.ONELEVEL_SCOPE )
        {
            return SearchScope.ONELEVEL;
        }
        else if ( scope == SearchControls.SUBTREE_SCOPE )
        {
            return SearchScope.SUBTREE;
        }
        else
        {
            return SearchScope.SUBTREE;
        }
    }


    /**
     * Modifies attributes of an entry.
     * 
     * @param dn the DN
     * @param modificationItems the modification items
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void modifyEntry( final String dn, final ModificationItem[] modificationItems, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
    }


    /**
     * Renames an entry.
     * 
     * @param oldDn the old DN
     * @param newDn the new DN
     * @param deleteOldRdn true to delete the old RDN
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void renameEntry( final String oldDn, final String newDn, final boolean deleteOldRdn,
        final Control[] controls, final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
    }


    /**
     * Creates an entry.
     * 
     * @param dn the entry's DN
     * @param attributes the entry's attributes
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void createEntry( final String dn, final Attributes attributes, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
    }


    /**
     * Deletes an entry.
     * 
     * @param dn the DN of the entry to delete
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void deleteEntry( final String dn, final Control[] controls, final StudioProgressMonitor monitor,
        final ReferralsInfo referralsInfo )
    {
    }
}
