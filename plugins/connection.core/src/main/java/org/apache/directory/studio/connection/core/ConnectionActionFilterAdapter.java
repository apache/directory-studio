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


import org.eclipse.ui.IActionFilter;


/**
 * This class implements an {@link IActionFilter} adapter for the {@link LdapServer} class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionActionFilterAdapter implements IActionFilter
{
    // Identifier and value strings
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String ENCRYPTION_METHOD = "encryptionMethod";
    private static final String NETWORK_PROVIDER = "networkProvider";
    private static final String AUTH_METHOD = "authMethod";
    private static final String BIND_PRINCIPAL = "bindPrincipal";
    private static final String BIND_PASSWORD = "bindPassword";
    private static final String SASL_REALM = "saslRealm";
    private static final String SASL_QOP = "saslQop";
    private static final String SASL_SECURITY_STRENGTH = "saslSecurityStrength";
    private static final String SASL_MUTUAL_AUTHENTICATION = "saslMutualAuthentication";
    private static final String KRB5_CREDENTIAL_CONFIGURATION = "krb5CredentialConfiguration";
    private static final String KRB5_CONFIGURATION = "krb5Configuration";
    private static final String KRB5_CONFIGURATION_FILE = "krb5ConfigurationFile";
    private static final String KRB5_REALM = "krb5Realm";
    private static final String KRB5_KDC_HOST = "krb5KdcHost";
    private static final String KRB5_KDC_PORT = "krb5KdcPort";
    private static final String VENDOR_NAME = "vendorName";
    private static final String VENDOR_VERSION = "vendorVersion";
    private static final String SERVER_TYPE = "serverType";
    private static final String SUPPORTED_LDAP_VERSIONS = "supportedLdapVersions";
    private static final String SUPPORTED_CONTROLS = "supportedControls";
    private static final String SUPPORTED_EXTENSIONS = "supportedExtensions";
    private static final String SUPPORTED_FEATURES = "supportedFeatures";

    /** The class instance */
    private static ConnectionActionFilterAdapter INSTANCE = new ConnectionActionFilterAdapter();


    /**
     * Private constructor.
     */
    private ConnectionActionFilterAdapter()
    {
        // Nothing to initialize
    }


    /**
     * Returns an instance of {@link ConnectionActionFilterAdapter}.
     *
     * @return
     *      an instance of {@link ConnectionActionFilterAdapter}
     */
    public static ConnectionActionFilterAdapter getInstance()
    {
        return INSTANCE;
    }


    /**
     * {@inheritDoc}
     */
    public boolean testAttribute( Object target, String name, String value )
    {
        if ( target instanceof Connection )
        {
            Connection connection = ( Connection ) target;

            // ID
            if ( ID.equals( name ) )
            {
                return value.equals( connection.getId() );
            }
            // NAME
            else if ( NAME.equals( name ) )
            {
                return value.equals( connection.getName() );
            }
            // HOST
            else if ( HOST.equals( name ) )
            {
                return value.equals( connection.getHost() );
            }
            // PORT
            else if ( PORT.equals( name ) )
            {
                return value.equals( "" + connection.getPort() );
            }
            // ENCRYPTION METHOD
            else if ( ENCRYPTION_METHOD.equals( name ) )
            {
                return value.equals( connection.getEncryptionMethod().toString() );
            }
            // NETWORK PROVIDER
            else if ( NETWORK_PROVIDER.equals( name ) )
            {
                return value.equals( connection.getNetworkProvider().toString() );
            }
            // AUTH METHOD
            else if ( AUTH_METHOD.equals( name ) )
            {
                return value.equals( connection.getAuthMethod().toString() );
            }
            // BIND PRINCIPAL
            else if ( BIND_PRINCIPAL.equals( name ) )
            {
                return value.equals( connection.getBindPrincipal() );
            }
            // BIND PASSWORD
            else if ( BIND_PASSWORD.equals( name ) )
            {
                return value.equals( connection.getBindPassword() );
            }
            // SASL REALM
            else if ( SASL_REALM.equals( name ) )
            {
                return value.equals( connection.getSaslRealm() );
            }
            // SASL QOP
            else if ( SASL_QOP.equals( name ) )
            {
                return value.equals( connection.getSaslQop().toString() );
            }
            // SASL SECURITY STRENGTH
            else if ( SASL_SECURITY_STRENGTH.equals( name ) )
            {
                return value.equals( connection.getSaslSecurityStrength().toString() );
            }
            // SASL MUTUAL AUTHENTICATION
            else if ( SASL_MUTUAL_AUTHENTICATION.equals( name ) )
            {
                return value.equals( connection.isSaslMutualAuthentication() ? "true" : "false" );
            }
            // KRB5 CREDENTIAL CONFIGURATION
            else if ( KRB5_CREDENTIAL_CONFIGURATION.equals( name ) )
            {
                return value.equals( connection.getKrb5CredentialConfiguration().toString() );
            }
            // KRB5 CONFIGURATION
            else if ( KRB5_CONFIGURATION.equals( name ) )
            {
                return value.equals( connection.getKrb5Configuration().toString() );
            }
            // KRB5 CONFIGURATION FILE
            else if ( KRB5_CONFIGURATION_FILE.equals( name ) )
            {
                return value.equals( connection.getKrb5ConfigurationFile() );
            }
            // KRB5 REALM
            else if ( KRB5_REALM.equals( name ) )
            {
                return value.equals( connection.getKrb5Realm() );
            }
            // KRB5 KDC HOST
            else if ( KRB5_KDC_HOST.equals( name ) )
            {
                return value.equals( connection.getKrb5KdcHost() );
            }
            // KRB5 KDC PORT
            else if ( KRB5_KDC_PORT.equals( name ) )
            {
                return value.equals( "" + connection.getKrb5KdcPort() );
            }
            // VENDOR NAME
            else if ( VENDOR_NAME.equals( name ) )
            {
                return value.equals( "" + connection.getDetectedConnectionProperties().getVendorName() );
            }
            // VENDOR VERSION
            else if ( VENDOR_VERSION.equals( name ) )
            {
                if ( connection.getDetectedConnectionProperties().getVendorVersion() != null )
                {
                    return connection.getDetectedConnectionProperties().getVendorVersion().indexOf( value ) != -1;
                }
            }
            // SERVER TYPE
            else if ( SERVER_TYPE.equals( name ) )
            {
                return value.equals( "" + connection.getDetectedConnectionProperties().getServerType().toString() );
            }
            // SUPPORTED LDAP VERSIONS
            else if ( SUPPORTED_LDAP_VERSIONS.equals( name ) )
            {
                connection.getDetectedConnectionProperties().getSupportedLdapVersions().contains( value );
            }
            // SUPPORTED CONTROLS
            else if ( SUPPORTED_CONTROLS.equals( name ) )
            {
                connection.getDetectedConnectionProperties().getSupportedControls().contains( value );
            }
            // SUPPORTED EXTENSIONS
            else if ( SUPPORTED_EXTENSIONS.equals( name ) )
            {
                connection.getDetectedConnectionProperties().getSupportedExtensions().contains( value );
            }
            // SUPPORTED FEATURES
            else if ( SUPPORTED_FEATURES.equals( name ) )
            {
                connection.getDetectedConnectionProperties().getSupportedFeatures().contains( value );
            }
        }

        return false;
    }
}
