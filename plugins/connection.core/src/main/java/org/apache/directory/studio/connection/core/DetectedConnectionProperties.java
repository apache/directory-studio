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


import java.util.List;


/**
 * This class contains all the properties that were detected for a connection
 * during the first connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DetectedConnectionProperties
{
    /** The key for the connection parameter "Vendor name" */
    public static final String CONNECTION_PARAMETER_VENDOR_NAME = "detectedProperties.vendorName"; //$NON-NLS-1$

    /** The key for the connection parameter "Vendor version" */
    public static final String CONNECTION_PARAMETER_VENDOR_VERSION = "detectedProperties.vendorVersion"; //$NON-NLS-1$

    /** The key for the connection parameter "Server type" */
    public static final String CONNECTION_PARAMETER_SERVER_TYPE = "detectedProperties.serverType"; //$NON-NLS-1$

    /** The key for the connection parameter "Supported LDAP versions" */
    public static final String CONNECTION_PARAMETER_SUPPORTED_LDAP_VERSIONS = "detectedProperties.supportedLdapVersions"; //$NON-NLS-1$

    /** The key for the connection parameter "Supported SASL mechanisms" */
    public static final String CONNECTION_PARAMETER_SUPPORTED_SASL_MECHANISMS = "detectedProperties.supportedSaslMechanisms"; //$NON-NLS-1$

    /** The key for the connection parameter "Supported controls" */
    public static final String CONNECTION_PARAMETER_SUPPORTED_CONTROLS = "detectedProperties.supportedControls"; //$NON-NLS-1$

    /** The key for the connection parameter "Supported extensions" */
    public static final String CONNECTION_PARAMETER_SUPPORTED_EXTENSIONS = "detectedProperties.supportedExtensions"; //$NON-NLS-1$

    /** The key for the connection parameter "Supported features" */
    public static final String CONNECTION_PARAMETER_SUPPORTED_FEATURES = "detectedProperties.supportedFeatures"; //$NON-NLS-1$

    /** The connection */
    public Connection connection;


    /**
     * Creates a new instance of DetectedConnectionProperties.
     *
     * @param connection the associated connection
     */
    public DetectedConnectionProperties( Connection connection )
    {
        this.connection = connection;
    }


    /**
     * Gets the server type.
     *
     * @return the server type
     */
    public ConnectionServerType getServerType()
    {
        try
        {
            String serverType = connection.getConnectionParameter().getExtendedProperty( CONNECTION_PARAMETER_SERVER_TYPE );
            
            if ( serverType != null )
            {
                return ConnectionServerType.valueOf( serverType );
            }
            else
            {
                return ConnectionServerType.UNKNOWN;
            }
        }
        catch ( IllegalArgumentException e )
        {
            return ConnectionServerType.UNKNOWN;
        }
    }


    /**
     * Gets the supported controls.
     *
     * @return the supported controls
     */
    public List<String> getSupportedControls()
    {
        return connection.getConnectionParameter().getExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_CONTROLS );
    }


    /**
     * Gets the supported extensions.
     *
     * @return the supported extensions
     */
    public List<String> getSupportedExtensions()
    {
        return connection.getConnectionParameter().getExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_EXTENSIONS );
    }


    /**
     * Gets the supported features.
     *
     * @return the supported features
     */
    public List<String> getSupportedFeatures()
    {
        return connection.getConnectionParameter().getExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_FEATURES );
    }


    /**
     * Gets the supported LDAP versions.
     *
     * @return the supported LDAP versions
     */
    public List<String> getSupportedLdapVersions()
    {
        return connection.getConnectionParameter().getExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_LDAP_VERSIONS );
    }


    /**
     * Gets the supported SASL mechanisms.
     *
     * @return the supported SASL mechanisms
     */
    public List<String> getSupportedSaslMechanisms()
    {
        return connection.getConnectionParameter().getExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_SASL_MECHANISMS );
    }


    /**
     * Gets the vendor name.
     *
     * @return the vendor name
     */
    public String getVendorName()
    {
        return connection.getConnectionParameter().getExtendedProperty( CONNECTION_PARAMETER_VENDOR_NAME );
    }


    /**
     * Gets the vendor version.
     *
     * @return the vendor version
     */
    public String getVendorVersion()
    {
        return connection.getConnectionParameter().getExtendedProperty( CONNECTION_PARAMETER_VENDOR_VERSION );
    }


    /**
     * Sets the server type.
     *
     * @param serverType the server type
     */
    public void setServerType( Object serverType )
    {
        connection.getConnectionParameter().setExtendedProperty( CONNECTION_PARAMETER_SERVER_TYPE,
            serverType.toString() );
    }


    /**
     * Sets the supported controls.
     *
     * @param supportedControls the supported controls
     */
    public void setSupportedControls( List<String> supportedControls )
    {
        connection.getConnectionParameter().setExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_CONTROLS,
            supportedControls );
    }


    /**
     * Sets the supported extensions.
     *
     * @param supportedExtensions the supported extensions
     */
    public void setSupportedExtensions( List<String> supportedExtensions )
    {
        connection.getConnectionParameter().setExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_EXTENSIONS,
            supportedExtensions );
    }


    /**
     * Sets the supported features.
     *
     * @param supportedFeatures the supported features
     */
    public void setSupportedFeatures( List<String> supportedFeatures )
    {
        connection.getConnectionParameter().setExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_FEATURES,
            supportedFeatures );
    }


    /**
     * Sets the supported LDAP versions.
     *
     * @param supportedLdapVersions the supported LDAP versions
     */
    public void setSupportedLdapVersions( List<String> supportedLdapVersions )
    {
        connection.getConnectionParameter().setExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_LDAP_VERSIONS,
            supportedLdapVersions );
    }


    /**
     * Sets the supported SASL mechanisms.
     *
     * @param supportedSaslMechanisms
     *      the supported SASL mechanisms
     */
    public void setSupportedSaslMechanisms( List<String> supportedSaslMechanisms )
    {
        connection.getConnectionParameter().setExtendedListStringProperty(
            CONNECTION_PARAMETER_SUPPORTED_SASL_MECHANISMS,
            supportedSaslMechanisms );
    }


    /**
     * Sets the vendor name.
     *
     * @param vendorName the vendor name
     */
    public void setVendorName( String vendorName )
    {
        connection.getConnectionParameter().setExtendedProperty( CONNECTION_PARAMETER_VENDOR_NAME, vendorName );
    }


    /**
     * Sets the vendor version.
     *
     * @param vendorVersion the vendor version
     */
    public void setVendorVersion( String vendorVersion )
    {
        connection.getConnectionParameter().setExtendedProperty( CONNECTION_PARAMETER_VENDOR_VERSION, vendorVersion );
    }
}
