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
package org.apache.directory.studio.openldap.config.model;

/**
 * The various OpenLDAP versions
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum OpenLdapVersion
{
    VERSION_2_4_0( "2.4.0" ),       // Most of the configuration AT were already defined in 2.4.0
                                    // Version prior to 2.4.6 were beta or alpha
    VERSION_2_4_6( "2.4.6" ),       // olcSortVals, olcServerID, olcTLSCRLFile
    VERSION_2_4_7( "2.4.7" ),       // olcIndexIntLen
    VERSION_2_4_8( "2.4.8" ),       //olcDbCryptFile, olcDbCryptKey, olcDbSocketPath, olcDbSocketExtensions, olcMemberOfDanglingError
    VERSION_2_4_9( "2.4.9" ),
    VERSION_2_4_10( "2.4.10" ),     // olcRefintModifiersName
    VERSION_2_4_11( "2.4.11" ),
    VERSION_2_4_12( "2.4.12" ),     // olcDbNoRefs, olcDbNoUndefFilter, olcLdapSyntaxes
    VERSION_2_4_13( "2.4.13" ),     // olcDbPageSize, olcAddContentAcl
    VERSION_2_4_14( "2.4.14" ),
    VERSION_2_4_15( "2.4.15" ),
    VERSION_2_4_16( "2.4.16" ),
    VERSION_2_4_17( "2.4.17" ),     // olcPPolicyForwardUpdates, olcSaslAuxprops, olcWriteTimeout
    VERSION_2_4_18( "2.4.18" ),     // olcTCPBuffer
    VERSION_2_4_19( "2.4.19" ),
    VERSION_2_4_20( "2.4.20" ),     // olcSyncUseSubentry
    VERSION_2_4_21( "2.4.21" ),
    VERSION_2_4_22( "2.4.22" ),     // olcExtraAttrs, olcDbIDAssertPassThru, olcSaslAuxpropsDontUseCopy, olcSaslAuxpropsDontUseCopyIgnore
    VERSION_2_4_23( "2.4.23" ),
    VERSION_2_4_24( "2.4.24" ),     // olcDbBindAllowed
    VERSION_2_4_25( "2.4.25" ),
    VERSION_2_4_26( "2.4.26" ),
    VERSION_2_4_27( "2.4.27" ),     // olcDbMaxReaders, olcDbMaxSize, olcDbMode, olcDbNoSync, olcDbSearchStack
    VERSION_2_4_28( "2.4.28" ),
    VERSION_2_4_29( "2.4.29" ),
    VERSION_2_4_30( "2.4.30" ),
    VERSION_2_4_31( "2.4.31" ),
    VERSION_2_4_32( "2.4.32" ),
    VERSION_2_4_33( "2.4.33" ),     // olcDbEnvFlags
    VERSION_2_4_34( "2.4.34" ),     // olcDbKeepalive, olcDbOnErr, olcIndexHash64
    VERSION_2_4_35( "2.4.35" ),
    VERSION_2_4_36( "2.4.36" ),     // olcDisabled, olcListenerThreads, olcThreadQueues
    VERSION_2_4_37( "2.4.37" ),     // olcTLSProtocolMin
    VERSION_2_4_38( "2.4.38" ),
    VERSION_2_4_39( "2.4.39" ),
    VERSION_2_4_40( "2.4.40" ),
    VERSION_2_4_41( "2.4.41" ),
    VERSION_2_4_42( "2.4.42" ),
    VERSION_2_4_43( "2.4.43" ),
    VERSION_2_4_44( "2.4.44" ),
    VERSION_2_4_45( "2.4.45" );
    
    /** The interned version */
    private String version;
    
    /**
     * A private constructor
     * @param version
     */
    private OpenLdapVersion( String version )
    {
        this.version = version;
    }
    
    
    /**
     * Get the enum associated to a String
     * 
     * @param version The version we are looking at
     * @return The found version, or VERSION_2_4_0 of not found.
     */
    public static OpenLdapVersion getVersion( String version )
    {
        for ( OpenLdapVersion openLDAPVersion : OpenLdapVersion.values() )
        {
            if ( openLDAPVersion.version.equalsIgnoreCase( version ) )
            {
                return openLDAPVersion;
            }
        }
        
        return OpenLdapVersion.VERSION_2_4_0;
    }
    
    
    /**
     * @return The interned String representation for this value
     */
    public String getValue()
    {
        return version;
    }
    
    
    /**
     * @return An array containing all the interned versions as String in reverse order (newest first)
     */
    public static String[] getVersions()
    {
        OpenLdapVersion[] values = OpenLdapVersion.values();
        String[] versions = new String[values.length];
        int i = values.length - 1;
        
        for ( OpenLdapVersion value : values )
        {
            versions[i--] = value.version;
        }
        
        return versions;
    }
}
