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
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.ldap.client.api.NoVerificationTrustManager;
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
    protected final int portSSL;
    protected final String adminDn;
    protected final String adminPassword;

    protected TestLdapServer( LdapServerType type, String host, int port, int portSSL, String adminDn,
        String adminPassword )
    {
        this.type = type;
        this.host = host;
        this.port = port;
        this.portSSL = portSSL;
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
        LdapConnection connection = openConnection();
        connection.bind( adminDn, adminPassword );
        return connection;
    }


    public LdapNetworkConnection openConnection() throws LdapException
    {
        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost( host );
        config.setLdapPort( port );
        config.setUseTls( true );
        config.setTrustManagers( new NoVerificationTrustManager() );
        LdapNetworkConnection connection = new LdapNetworkConnection( config );
        connection.connect();
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
        setConfidentialityRequired( false );

        String serverSpecificLdif = getType().name() + ".ldif";
        if ( TestFixture.class.getResource( serverSpecificLdif ) != null )
        {
            withAdminConnection( connection -> {
                try ( LdifReader ldifReader = new LdifReader(
                    TestFixture.class.getResourceAsStream( serverSpecificLdif ) ) )
                {
                    for ( LdifEntry entry : ldifReader )
                    {
                        if ( entry.isChangeModify() )
                        {
                            connection.modify( entry.getDn(), entry.getModificationArray() );
                        }
                        if ( entry.isChangeAdd() )
                        {
                            connection.add( entry.getEntry() );
                        }
                    }
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( "Unexpected exception: " + e, e );
                }
            } );
        }
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


    public int getPortSSL()
    {
        return portSSL;
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


    public abstract void setConfidentialityRequired( boolean confidentialityRequired );


    @Override
    public String toString()
    {
        return type.name();
    }
}
