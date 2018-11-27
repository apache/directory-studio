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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.directory.api.ldap.model.constants.SaslQoP;
import org.apache.directory.api.ldap.model.constants.SaslSecurityStrength;


/**
 * A Bean class to hold the connection parameters.
 * It is used to make connections persistent.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionParameter
{
    /**
     * Enum for the used encryption method.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum EncryptionMethod
    {
        /** No encryption. */
        NONE,

        /** SSL encryption. */
        LDAPS,

        /** Encryption using Start TLS extension. */
        START_TLS
    }

    /**
     * Enum for the network provider.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum NetworkProvider
    {
        /** JNDI */
        JNDI,

        /** Apache Directory LDAP API */
        APACHE_DIRECTORY_LDAP_API
    }

    /**
     * Enum for the used authentication method.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum AuthenticationMethod
    {
        /** No authentication, anonymous bind. */
        NONE(0),

        /** Simple authentication, simple bind. */
        SIMPLE(1),

        /** SASL authentication using DIGEST-MD5. */
        SASL_DIGEST_MD5(2),

        /** SASL authentication using CRAM-MD5. */
        SASL_CRAM_MD5(3),

        /** SASL authentication using GSSAPI. */
        SASL_GSSAPI(4),
        
        /** SASL PLAIN authentication */
        SASL_PLAIN(5);
        
        
        private int value;
        
        private AuthenticationMethod( int value )
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public enum Krb5CredentialConfiguration
    {
        USE_NATIVE, OBTAIN_TGT
    }

    public enum Krb5Configuration
    {
        DEFAULT, FILE, MANUAL
    }

    /** The unique id. */
    private String id;

    /** The symbolic name. */
    private String name;

    /** The host name or IP address of the LDAP server. */
    private String host;

    /** The port of the LDAP server. */
    private int port;

    /** The encryption method. */
    private EncryptionMethod encryptionMethod;

    /** The network provider. */
    private NetworkProvider networkProvider;

    /** The authentication method. */
    private AuthenticationMethod authMethod;

    /** The bind principal, typically a Dn. */
    private String bindPrincipal;

    /** The bind password. */
    private String bindPassword;

    /** The SASL realm. */
    private String saslRealm;

    /** The SASL qualitiy of protection. */
    private SaslQoP saslQop = SaslQoP.AUTH;

    /** The SASL security strength. */
    private SaslSecurityStrength saslSecurityStrength = SaslSecurityStrength.HIGH;

    /** The SASL mutual authentication flag. */
    private boolean saslMutualAuthentication = true;

    /** The Kerberos credential configuration. */
    private Krb5CredentialConfiguration krb5CredentialConfiguration = Krb5CredentialConfiguration.USE_NATIVE;

    /** The Kerberos configuration. */
    private Krb5Configuration krb5Configuration = Krb5Configuration.DEFAULT;

    /** The Kerberos configuration file. */
    private String krb5ConfigurationFile;

    /** The Kerberos realm. */
    private String krb5Realm;

    /** The Kerberos KDC host. */
    private String krb5KdcHost;

    /** The Kerberos KDC port. */
    private int krb5KdcPort = 88;

    /** The read only flag. */
    private boolean isReadOnly;

    /** The extended properties. */
    private Map<String, String> extendedProperties;
    
    /** The connection timeout. Default to 30 seconds */
    private long timeout = 30000L;


    /**
     * Creates a new instance of ConnectionParameter.
     */
    public ConnectionParameter()
    {
        this.extendedProperties = new HashMap<>();
    }


    /**
     * Creates a new instance of ConnectionParameter.
     * 
     * @param name the connection name
     * @param host the host
     * @param port the port
     * @param encryptionMethod the encryption method
     * @param authMethod the authentication method
     * @param bindPrincipal the bind principal
     * @param bindPassword the bind password
     * @param saslRealm the SASL realm
     * @param isReadOnly the read only flag
     * @param extendedProperties the extended properties
     */
    public ConnectionParameter( String name, String host, int port, EncryptionMethod encryptionMethod,
        NetworkProvider networkProvider, AuthenticationMethod authMethod, String bindPrincipal, String bindPassword,
        String saslRealm, boolean isReadOnly, Map<String, String> extendedProperties, long timeout )
    {
        this.id = createId();
        this.name = name;
        this.host = host;
        this.port = port;
        this.encryptionMethod = encryptionMethod;
        this.networkProvider = networkProvider;
        this.authMethod = authMethod;
        this.bindPrincipal = bindPrincipal;
        this.bindPassword = bindPassword;
        this.saslRealm = saslRealm;
        this.isReadOnly = isReadOnly;
        this.extendedProperties = new HashMap<>();

        if ( extendedProperties != null )
        {
            this.extendedProperties.putAll( extendedProperties );
        }

        this.timeout = timeout;
    }


    /**
     * Gets the auth method.
     * 
     * @return the auth method
     */
    public AuthenticationMethod getAuthMethod()
    {
        return authMethod;
    }


    /**
     * Sets the auth method.
     * 
     * @param authMethod the auth method
     */
    public void setAuthMethod( AuthenticationMethod authMethod )
    {
        this.authMethod = authMethod;
    }


    /**
     * Gets the bind password.
     * 
     * @return the bind password
     */
    public String getBindPassword()
    {
        return bindPassword;
    }


    /**
     * Sets the bind password.
     * 
     * @param bindPassword the bind password
     */
    public void setBindPassword( String bindPassword )
    {
        this.bindPassword = bindPassword;
    }


    /**
     * Gets the SASL realm
     * 
     * @return the SASL realm
     */
    public String getSaslRealm()
    {
        return saslRealm;
    }


    /**
     * Sets the SASL realm
     * 
     * @param saslRealm the SASL realm
     */
    public void setSaslRealm( String saslRealm )
    {
        this.saslRealm = saslRealm;
    }


    /**
     * Checks if this connection is read only.
     * 
     * @return true, if this connection is read only
     */
    public boolean isReadOnly()
    {
        return isReadOnly;
    }


    /**
     * Sets the read only flag.
     * 
     * @param isReadOnly the new read only flag
     */
    public void setReadOnly( boolean isReadOnly )
    {
        this.isReadOnly = isReadOnly;
    }


    /**
     * Gets the bind principal.
     * 
     * @return the bind principal
     */
    public String getBindPrincipal()
    {
        return bindPrincipal;
    }


    /**
     * Sets the bind principal.
     * 
     * @param bindPrincipal the bind principal
     */
    public void setBindPrincipal( String bindPrincipal )
    {
        this.bindPrincipal = bindPrincipal;
    }


    /**
     * Gets the encryption method.
     * 
     * @return the encryption method
     */
    public EncryptionMethod getEncryptionMethod()
    {
        return encryptionMethod;
    }


    /**
     * Sets the encryption method.
     * 
     * @param encryptionMethod the encryption method
     */
    public void setEncryptionMethod( EncryptionMethod encryptionMethod )
    {
        this.encryptionMethod = encryptionMethod;
    }


    /**
     * Gets the network provider.
     *
     * @return the network provider
     */
    public NetworkProvider getNetworkProvider()
    {
        return networkProvider;
    }


    /**
     * Sets the network provider.
     *
     * @param networkProvider the network provider
     */
    public void setNetworkProvider( NetworkProvider networkProvider )
    {
        this.networkProvider = networkProvider;
    }


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        if ( id == null )
        {
            id = createId();
        }
        return id;
    }


    /**
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * Gets the host.
     * 
     * @return the host
     */
    public String getHost()
    {
        return host;
    }


    /**
     * Sets the host.
     * 
     * @param host the host
     */
    public void setHost( String host )
    {
        this.host = host;
    }


    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the name.
     * 
     * @param name the name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort()
    {
        return port;
    }


    /**
     * Sets the port.
     * 
     * @param port the port
     */
    public void setPort( int port )
    {
        this.port = port;
    }


    /**
     * Gets the SASL quality of protection.
     * 
     * @return the SASL quality of protection
     */
    public SaslQoP getSaslQop()
    {
        return saslQop;
    }


    /**
     * Sets the SASL qualitiy of protection.
     * 
     * @param saslQop the new SASL qualitiy of protection
     */
    public void setSaslQop( SaslQoP saslQop )
    {
        this.saslQop = saslQop;
    }


    /**
     * Gets the SASL security strength.
     * 
     * @return the SASL security strength
     */
    public SaslSecurityStrength getSaslSecurityStrength()
    {
        return saslSecurityStrength;
    }


    /**
     * Sets the SASL security strength.
     * 
     * @param saslSecurityStrength the new SASL security strength
     */
    public void setSaslSecurityStrength( SaslSecurityStrength saslSecurityStrength )
    {
        this.saslSecurityStrength = saslSecurityStrength;
    }


    /**
     * Checks if is SASL mutual authentication.
     * 
     * @return true, if is SASL mutual authentication
     */
    public boolean isSaslMutualAuthentication()
    {
        return saslMutualAuthentication;
    }


    /**
     * Sets the SASL mutual authentication.
     * 
     * @param saslMutualAuthentication the new SASL mutual authentication
     */
    public void setSaslMutualAuthentication( boolean saslMutualAuthentication )
    {
        this.saslMutualAuthentication = saslMutualAuthentication;
    }


    /**
     * Gets the Kerberos credential configuration.
     * 
     * @return the Kerberos credential configuration
     */
    public Krb5CredentialConfiguration getKrb5CredentialConfiguration()
    {
        return krb5CredentialConfiguration;
    }


    /**
     * Sets the Kerberos credential configuration.
     * 
     * @param krb5CredentialConfiguration the new Kerberos credential configuration
     */
    public void setKrb5CredentialConfiguration( Krb5CredentialConfiguration krb5CredentialConfiguration )
    {
        this.krb5CredentialConfiguration = krb5CredentialConfiguration;
    }


    /**
     * Gets the Kerberos configuration.
     * 
     * @return the Kerberos configuration
     */
    public Krb5Configuration getKrb5Configuration()
    {
        return krb5Configuration;
    }


    /**
     * Sets the Kerberos configuration.
     * 
     * @param krb5Configuration the new Kerberos configuration
     */
    public void setKrb5Configuration( Krb5Configuration krb5Configuration )
    {
        this.krb5Configuration = krb5Configuration;
    }


    /**
     * Gets the Kerberos configuration file.
     * 
     * @return the Kerberos configuration file
     */
    public String getKrb5ConfigurationFile()
    {
        return krb5ConfigurationFile;
    }


    /**
     * Sets the Kerberos configuration file.
     * 
     * @param krb5ConfigurationFile the new Kerberos configuration file
     */
    public void setKrb5ConfigurationFile( String krb5ConfigurationFile )
    {
        this.krb5ConfigurationFile = krb5ConfigurationFile;
    }


    /**
     * Gets the Kerberos realm.
     * 
     * @return the Kerberos realm
     */
    public String getKrb5Realm()
    {
        return krb5Realm;
    }


    /**
     * Sets the Kerberos realm.
     * 
     * @param krb5Realm the new Kerberos realm
     */
    public void setKrb5Realm( String krb5Realm )
    {
        this.krb5Realm = krb5Realm;
    }


    /**
     * Gets the Kerberos KDC host.
     * 
     * @return the Kerberos KDC host
     */
    public String getKrb5KdcHost()
    {
        return krb5KdcHost;
    }


    /**
     * Sets the Kerberos KDC host.
     * 
     * @param krb5KdcHost the new Kerberos KDC host
     */
    public void setKrb5KdcHost( String krb5KdcHost )
    {
        this.krb5KdcHost = krb5KdcHost;
    }


    /**
     * Gets the Kerberos KDC port.
     * 
     * @return the Kerberos KDCport
     */
    public int getKrb5KdcPort()
    {
        return krb5KdcPort;
    }


    /**
     * Sets the Kerberos KDC port.
     * 
     * @param krb5KdcPort the new Kerberos KDC port
     */
    public void setKrb5KdcPort( int krb5KdcPort )
    {
        this.krb5KdcPort = krb5KdcPort;
    }


    /**
     * Gets the extended properties.
     * 
     * @return the extended properties
     */
    public Map<String, String> getExtendedProperties()
    {
        return extendedProperties;
    }


    /**
     * Sets the extended properties.
     * 
     * @param extendedProperties the extended properties
     */
    public void setExtendedProperties( Map<String, String> extendedProperties )
    {
        this.extendedProperties = extendedProperties;
    }


    /**
     * Sets the extended property.
     * 
     * @param key the key
     * @param value the value
     */
    public void setExtendedProperty( String key, String value )
    {
        extendedProperties.put( key, value );
    }


    /**
     * Gets the extended property.
     * 
     * @param key the key
     * 
     * @return the extended property or null if the property doesn't exist
     */
    public String getExtendedProperty( String key )
    {
        return extendedProperties.get( key );
    }


    /**
     * Sets the extended list string property.
     * 
     * @param key the key
     * @param value the value
     */
    public void setExtendedListStringProperty( String key, List<String> value )
    {
        StringBuilder sb = new StringBuilder();
        if ( ( value != null ) && ( !value.isEmpty() ) )
        {
            for ( String string : value )
            {
                sb.append( string );
                sb.append( ';' );
            }
            sb.deleteCharAt( sb.length() - 1 );
        }

        extendedProperties.put( key, sb.toString() );
    }


    /**
     * Gets the extended list string property.
     * 
     * @param key the key
     * 
     * @return the extended list string property or <code>null</code> if the property doesn't exist
     */
    public List<String> getExtendedListStringProperty( String key )
    {
        String s = extendedProperties.get( key );
        if ( s != null )
        {
            String[] array = s.split( ";" ); //$NON-NLS-1$
            if ( ( array != null ) && ( array.length > 0 ) )
            {
                return new ArrayList<>( Arrays.asList( array ) );
            }
        }

        return null;
    }


    /**
     * Sets the extended int property.
     * 
     * @param key the key
     * @param value the value
     */
    public void setExtendedIntProperty( String key, int value )
    {
        extendedProperties.put( key, Integer.toString( value ) );
    }


    /**
     * Gets the extended int property.
     * 
     * @param key the key
     * 
     * @return the extended int property or -1 if the property doesn't exist
     */
    public int getExtendedIntProperty( String key )
    {
        String s = extendedProperties.get( key );
        
        if ( s != null )
        {
            return Integer.parseInt( s );
        }
        else
        {
            return -1;
        }
    }


    /**
     * Sets the extended bool property.
     * 
     * @param key the key
     * @param value the value
     */
    public void setExtendedBoolProperty( String key, boolean value )
    {
        extendedProperties.put( key, Boolean.toString( value ) );
    }


    /**
     * Gets the extended bool property.
     * 
     * @param key the key
     * 
     * @return the extended bool property or false if the property doesn'T exist
     */
    public boolean getExtendedBoolProperty( String key )
    {
        String s = extendedProperties.get( key );
        
        if ( s != null )
        {
            return Boolean.parseBoolean( s );
        }
        else
        {
            return false;
        }
    }


    /**
     * Gets the timeout.
     * 
     * @return the timeout
     */
    public long getTimeout()
    {
        return timeout;
    }


    /**
     * Sets the timeout.
     * 
     * @param timeout the timeout
     */
    public void setTimeout( long timeout )
    {
        this.timeout = timeout;
    }


    /**
     * Creates a unique id.
     * 
     * @return the created id
     */
    private String createId()
    {
        return UUID.randomUUID().toString();
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getId().hashCode();
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof ConnectionParameter )
        {
            ConnectionParameter other = ( ConnectionParameter ) obj;
            return getId().equals( other.getId() );
        }
        return false;
    }

}
