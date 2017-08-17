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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import org.apache.directory.studio.connection.core.ConnectionServerType;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;


/**
 * Utility class for detecting the type of server based on its Root DSE.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerTypeDetector
{
    /**
     * Detects the type of server we are talking to. We can detect the following servers :
     * <ul>
     * <li>ApacheDS</li>
     * <li>IBM IDS</li>
     * <li>Netscape</li>
     * <li>Novell</li>
     * <li>SUN DS</li>
     * <li>Microsoft Active Directory</li>
     * <li>OpenLDAP</li>
     * <li>Siemens</li>
     * <li>RedHat 389</li>
     * </ul>
     * 
     * NOTE : We should add detectors for RedHAT i389 and Forgerock OpenDJ
     *
     * @param rootDSE the Root DSE
     * @return the corresponding server type or 'UNKNOWN'
     */
    public static ConnectionServerType detectServerType( IRootDSE rootDSE )
    {
        ConnectionServerType serverType;

        IAttribute vnAttribute = rootDSE.getAttribute( "vendorName" ); //$NON-NLS-1$
        IAttribute vvAttribute = rootDSE.getAttribute( "vendorVersion" ); //$NON-NLS-1$

        if ( vnAttribute != null && vnAttribute.getStringValues().length > 0 && vvAttribute != null
            && vvAttribute.getStringValues().length > 0 )
        {
            String vendorName = vnAttribute.getStringValues()[0];
            String vendorVersion = vvAttribute.getStringValues()[0];

            // ApacheDS
            serverType = detectApacheDS( vendorName );
            
            if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
            {
                return serverType;
            }

            // IBM
            serverType = detectIbm( vendorName, vendorVersion );
            
            if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
            {
                return serverType;
            }

            // Netscape
            serverType = detectNetscape( vendorName, vendorVersion );
            
            if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
            {
                return serverType;
            }

            // Novell
            serverType = detectNovell( vendorName, vendorVersion );
            
            if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
            {
                return serverType;
            }

            // Sun
            serverType = detectSun( vendorName, vendorVersion );
            
            if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
            {
                return serverType;
            }

            // RedHat 389
            serverType = detectRedHat389( vendorName, vendorVersion );
            
            if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
            {
                return serverType;
            }

            // FrgeRock OpenDJ
            serverType = detectOpenDJ( vendorName, vendorVersion );
            
            if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
            {
                return serverType;
            }
        }

        // Microsoft
        serverType = detectMicrosoft( rootDSE );
        
        if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
        {
            return serverType;
        }

        // OpenLDAP
        serverType = detectOpenLdap( rootDSE );
        
        if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
        {
            return serverType;
        }

        // Siemens
        serverType = detectSiemens( rootDSE );
        
        if ( !ConnectionServerType.UNKNOWN.equals( serverType ) )
        {
            return serverType;
        }

        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects ApacheDS.
     *
     * @param vendorName the vendor name
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectApacheDS( String vendorName )
    {
        if ( vendorName.indexOf( "Apache Software Foundation" ) > -1 ) //$NON-NLS-1$
        {
            return ConnectionServerType.APACHEDS;
        }

        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects the following IBM directory servers:
     * <ul>
     *   <li>IBM Directory Server</li>
     *   <li>IBM Secureway Directory</li>
     *   <li>IBM Tivoli Directory Server</li>
     * </ul>
     *
     * @param vendorName the vendor name
     * @param vendorVersion the vendor version
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectIbm( String vendorName, String vendorVersion )
    {
        // IBM
        if ( vendorName.indexOf( "International Business Machines" ) > -1 ) //$NON-NLS-1$
        {
            // IBM SecureWay Directory
            String[] iswVersions = { "3.2", "3.2.1", "3.2.2" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            
            for ( String version : iswVersions )
            {
                if ( vendorVersion.indexOf( version ) > -1 )
                {
                    return ConnectionServerType.IBM_SECUREWAY_DIRECTORY;
                }
            }

            // IBM Directory Server
            String[] idsVersions = { "4.1", "5.1" }; //$NON-NLS-1$ //$NON-NLS-2$
            
            for ( String version : idsVersions )
            {
                if ( vendorVersion.indexOf( version ) > -1 )
                {
                    return ConnectionServerType.IBM_DIRECTORY_SERVER;
                }
            }

            // IBM Tivoli Directory Server
            String[] tdsVersions = { "5.2", "6.0", "6.1", "6.2" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            
            for ( String version : tdsVersions )
            {
                if ( vendorVersion.indexOf( version ) > -1 )
                {
                    return ConnectionServerType.IBM_TIVOLI_DIRECTORY_SERVER;
                }
            }
        }

        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects the following Microsoft directory servers:
     * <ul>
     *   <li>Microsoft Active Directory 2000</li>
     *   <li>Microsoft Active Directory 2003</li>
     * </ul>
     * 
     * @param rootDSE the Root DSE
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectMicrosoft( IRootDSE rootDSE )
    {
        if ( rootDSE.getAttribute( "rootDomainNamingContext" ) != null )
        {
            if ( rootDSE.getAttribute( "forestFunctionality" ) != null )
            {
                return ConnectionServerType.MICROSOFT_ACTIVE_DIRECTORY_2003;
            }
            else
            {
                return ConnectionServerType.MICROSOFT_ACTIVE_DIRECTORY_2000;
            }
        }

        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects Netscape directory server.
     *
     * @param vendorName the vendor name
     * @param vendorVersion the vendor version
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectNetscape( String vendorName, String vendorVersion )
    {
        if ( vendorName.indexOf( "Netscape" ) > -1 //$NON-NLS-1$
            || vendorVersion.indexOf( "Netscape" ) > -1 ) //$NON-NLS-1$
        {
            return ConnectionServerType.NETSCAPE;
        }

        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects Novell directory server.
     *
     * @param vendorName the vendor name
     * @param vendorVersion the vendor version
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectNovell( String vendorName, String vendorVersion )
    {
        if ( vendorName.indexOf( "Novell" ) > -1 //$NON-NLS-1$
            || vendorVersion.indexOf( "eDirectory" ) > -1 ) //$NON-NLS-1$
        {
            return ConnectionServerType.NOVELL;
        }

        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects the following OpenLDAP directory servers:
     * <ul>
     *   <li>OpenLDAP 2.4</li>
     *   <li>OpenLDAP 2.3</li>
     *   <li>OpenLDAP 2.2</li>
     *   <li>OpenLDAP 2.1</li>
     *   <li>OpenLDAP 2.0</li>
     *   <li>OpenLDAP</li>
     * </ul>
     * 
     * @param rootDSE the Root DSE
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectOpenLdap( IRootDSE rootDSE )
    {
        IAttribute ocAttribute = rootDSE.getAttribute( "objectClass" ); //$NON-NLS-1$
        
        if ( ocAttribute != null )
        {
            for ( int i = 0; i < ocAttribute.getStringValues().length; i++ )
            {
                if ( "OpenLDAProotDSE".equals( ocAttribute.getStringValues()[i] ) ) //$NON-NLS-1$
                {
                    IAttribute scAttribute = rootDSE.getAttribute( "supportedControl" ); //$NON-NLS-1$

                    // Check for the new "Don't Use Copy" Control (RFC 6171) that has been added in OpenLDAP 2.4
                    if ( scAttribute != null )
                    {
                        for ( int sci = 0; sci < scAttribute.getStringValues().length; sci++ )
                        {
                            if ( "1.3.6.1.1.22".equals( scAttribute.getStringValues()[sci] ) ) //$NON-NLS-1$
                            {
                                return ConnectionServerType.OPENLDAP_2_4;
                            }
                        }
                    }
                    
                    // ConfigContext has been added in OpenLDAP 2.3
                    if ( rootDSE.getAttribute( "configContext" ) != null )
                    {
                        return ConnectionServerType.OPENLDAP_2_3;
                    }

                    // Proxy Auth control has been added in OpenLDAP 2.0
                    if ( scAttribute != null )
                    {
                        for ( int sci = 0; sci < scAttribute.getStringValues().length; sci++ )
                        {
                            if ( "2.16.840.1.113730.3.4.18".equals( scAttribute.getStringValues()[sci] ) ) //$NON-NLS-1$
                            {
                                return ConnectionServerType.OPENLDAP_2_2;
                            }
                        }
                    }

                    // Check for the 'Who Am I' extended operation, added in OpenLDAP 2.1
                    IAttribute seAttribute = rootDSE.getAttribute( "supportedExtension" ); //$NON-NLS-1$
                    
                    if ( seAttribute != null )
                    {
                        for ( int sei = 0; sei < seAttribute.getStringValues().length; sei++ )
                        {
                            if ( "1.3.6.1.4.1.4203.1.11.3".equals( seAttribute.getStringValues()[sei] ) ) //$NON-NLS-1$
                            {
                                return ConnectionServerType.OPENLDAP_2_1;
                            }
                        }
                    }

                    // The 'Language Tag' feature has been added in OpenLDAP 2.0
                    IAttribute sfAttribute = rootDSE.getAttribute( "supportedFeatures" ); //$NON-NLS-1$
                    
                    if ( sfAttribute != null )
                    {
                        for ( int sfi = 0; sfi < sfAttribute.getStringValues().length; sfi++ )
                        {
                            if ( "1.3.6.1.4.1.4203.1.5.4".equals( sfAttribute.getStringValues()[sfi] ) ) //$NON-NLS-1$
                            {
                                return ConnectionServerType.OPENLDAP_2_0;
                            }
                        }
                    }

                    return ConnectionServerType.OPENLDAP;
                }
            }
        }

        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects Siemens directory server.
     *
     * @param rootDSE the Root DSE
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectSiemens( IRootDSE rootDSE )
    {
        IAttribute ssseAttribute = rootDSE.getAttribute( "subSchemaSubentry" ); //$NON-NLS-1$
        
        if ( ssseAttribute != null )
        {
            for ( int i = 0; i < ssseAttribute.getStringValues().length; i++ )
            {
                if ( "cn=LDAPGlobalSchemaSubentry".equals( ssseAttribute.getStringValues()[i] ) ) //$NON-NLS-1$
                {
                    return ConnectionServerType.SIEMENS_DIRX;
                }
            }
        }

        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects Sun directory server.
     *
     * @param vendorName the vendor name
     * @param vendorVersion the vendor version
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectSun( String vendorName, String vendorVersion )
    {
        if ( vendorName.indexOf( "Sun" ) > -1 //$NON-NLS-1$
            || vendorVersion.indexOf( "Sun" ) > -1 ) //$NON-NLS-1$
        {
            return ConnectionServerType.SUN_DIRECTORY_SERVER;
        }
        
        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects RedHat 389 directory server.
     *
     * @param vendorName the vendor name
     * @param vendorVersion the vendor version
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectRedHat389( String vendorName, String vendorVersion )
    {
        if ( vendorName.indexOf( "389 Project" ) > -1 //$NON-NLS-1$
            || vendorVersion.indexOf( "389-Directory" ) > -1 ) //$NON-NLS-1$
        {
            return ConnectionServerType.RED_HAT_389;
        }
        
        return ConnectionServerType.UNKNOWN;
    }


    /**
     * Detects ForgeRock OpenDJ directory server.
     *
     * @param vendorName the vendor name
     * @param vendorVersion the vendor version
     * @return the corresponding server type or 'UNKNOWN'
     */
    private static ConnectionServerType detectOpenDJ( String vendorName, String vendorVersion )
    {
        if ( vendorName.indexOf( "ForgeRock" ) > -1 //$NON-NLS-1$
            || vendorVersion.indexOf( "OpenDJ" ) > -1 ) //$NON-NLS-1$
        {
            return ConnectionServerType.FORGEROCK_OPEN_DJ;
        }
        
        return ConnectionServerType.UNKNOWN;
    }
}
