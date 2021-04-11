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

package org.apache.directory.studio.test.integration.junit5;


import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;


/**
 * An abstraction around an LDAP server that can be used for testing.
 * Provides the LDAP server type and connection parameters.
 */
public abstract class TestLdapServer
{
    public static String getEnvOrDefault( String key, String defaultValue )
    {
        return System.getenv().getOrDefault( key, defaultValue );
    }

    protected final LdapServerType type;
    protected final String host;
    protected final int port;
    protected final String adminDn;
    protected final String adminPassword;

    protected TestLdapServer( LdapServerType type, String host, int port, String adminDn, String adminPassword )
    {
        this.type = type;
        this.host = host;
        this.port = port;
        this.adminDn = adminDn;
        this.adminPassword = adminPassword;
    }


    public boolean isAvailable()
    {
        try ( LdapConnection connection = openAdminConnection() )
        {
        }
        catch ( InvalidConnectionException e )
        {
            return false;
        }
        catch ( LdapAuthenticationException e )
        {
            return false;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Unexpected exception: " + e, e );
        }
        return true;
    }


    public LdapConnection openAdminConnection() throws LdapException
    {
        LdapConnection connection = new LdapNetworkConnection( host, port );
        connection.connect();
        connection.bind( adminDn, adminPassword );
        return connection;
    }


    public void withAdminConnection( LdapConnectionConsumer consumer )
    {
        try ( LdapConnection connection = openAdminConnection() )
        {
            consumer.accept( connection );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Unexpected exception: " + e, e );
        }
    }

    public static interface LdapConnectionConsumer
    {
        void accept( LdapConnection connection ) throws Exception;
    }

    public <T> T withAdminConnectionAndGet( LdapConnectionFunction<T> function )
    {
        try ( LdapConnection connection = openAdminConnection() )
        {
            return function.apply( connection );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Unexpected exception: " + e, e );
        }
    }

    public static interface LdapConnectionFunction<T>
    {
        T apply( LdapConnection connection ) throws Exception;
    }

    public void prepare()
    {
        TestFixture.createContextEntry( this );
        TestFixture.cleanup( this );
        TestFixture.importData( this );
        TestFixture.importReferrals( this );
    }


    public LdapServerType getType()
    {
        return type;
    }


    public String getHost()
    {
        return host;
    }


    public int getPort()
    {
        return port;
    }


    public String getLdapUrl()
    {
        return "ldap://" + host + ":" + port;
    }


    public String getAdminDn()
    {
        return adminDn;
    }


    public String getAdminPassword()
    {
        return adminPassword;
    }


    @Override
    public String toString()
    {
        return type.name();
    }
}
