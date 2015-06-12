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


import java.util.ArrayList;
import java.util.List;


/**
 * Java bean for the 'OlcGlobal' object class. There are many attributes that have been
 * added in some of the latest revisions :
 * 
 * <ul>
 * <li>olcTCPBuffer (List<String>) : 2.4.18</li>
 * <li>olcSaslAuxpropsDontUseCopy (String) : 2.4.22</li>
 * <li>olcSaslAuxpropsDontUseCopyIgnore (Boolean) : 2.4.22</li>
 * <li>olcIndexHash64 (Boolean) : 2.4.34</li>
 * <li>olcListenerThreads (Integer) : 2.4.36</li>
 * <li>olcThreadQueues (Integer) : 2.4.36</li>
 * <li>olcTLSProtocolMin (String) : 2.4.37</li>
 * <li>olcTLSECName (String) : 2.4.??? (not yet released)</li>
 * </ul> 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcGlobal extends OlcConfig
{
    /**
     * Field for the 'cn' attribute.
     */
    @ConfigurationElement(attributeType = "cn", isRdn = true)
    private List<String> cn = new ArrayList<String>();

    /**
     * Field for the 'olcAllows' attribute.
     */
    @ConfigurationElement(attributeType = "olcAllows")
    private List<String> olcAllows = new ArrayList<String>();

    /**
     * Field for the 'olcArgsFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcArgsFile")
    private String olcArgsFile;

    /**
     * Field for the 'olcAttributeOptions' attribute.
     */
    @ConfigurationElement(attributeType = "olcAttributeOptions")
    private List<String> olcAttributeOptions = new ArrayList<String>();

    /**
     * Field for the 'olcAttributeTypes' attribute.
     */
    @ConfigurationElement(attributeType = "olcAttributeTypes")
    private List<String> olcAttributeTypes = new ArrayList<String>();

    /**
     * Field for the 'olcAuthIDRewrite' attribute.
     */
    @ConfigurationElement(attributeType = "olcAuthIDRewrite")
    private List<String> olcAuthIDRewrite = new ArrayList<String>();

    /**
     * Field for the 'olcAuthzPolicy' attribute.
     */
    @ConfigurationElement(attributeType = "olcAuthzPolicy")
    private String olcAuthzPolicy;

    /**
     * Field for the 'olcAuthzRegexp' attribute.
     */
    @ConfigurationElement(attributeType = "olcAuthzRegexp")
    private List<String> olcAuthzRegexp = new ArrayList<String>();

    /**
     * Field for the 'olcConcurrency' attribute.
     */
    @ConfigurationElement(attributeType = "olcConcurrency")
    private Integer olcConcurrency;

    /**
     * Field for the 'olcConfigDir' attribute.
     */
    @ConfigurationElement(attributeType = "olcConfigDir")
    private String olcConfigDir;

    /**
     * Field for the 'olcConfigFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcConfigFile")
    private String olcConfigFile;

    /**
     * Field for the 'olcConnMaxPending' attribute.
     */
    @ConfigurationElement(attributeType = "olcConnMaxPending")
    private Integer olcConnMaxPending;

    /**
     * Field for the 'olcConnMaxPendingAuth' attribute.
     */
    @ConfigurationElement(attributeType = "olcConnMaxPendingAuth")
    private Integer olcConnMaxPendingAuth;

    /**
     * Field for the 'olcDisallows' attribute.
     */
    @ConfigurationElement(attributeType = "olcDisallows")
    private List<String> olcDisallows = new ArrayList<String>();

    /**
     * Field for the 'olcDitContentRules' attribute.
     */
    @ConfigurationElement(attributeType = "olcDitContentRules")
    private List<String> olcDitContentRules = new ArrayList<String>();

    /**
     * Field for the 'olcGentleHUP' attribute.
     */
    @ConfigurationElement(attributeType = "olcGentleHUP")
    private Boolean olcGentleHUP;

    /**
     * Field for the 'olcIdleTimeout' attribute.
     */
    @ConfigurationElement(attributeType = "olcIdleTimeout")
    private Integer olcIdleTimeout;

    /**
     * Field for the 'olcIndexHash64' attribute. (Added in OpenLDAP 2.4.34)
     */
    @ConfigurationElement(attributeType = "olcIndexHash64")
    private Boolean olcIndexHash64;

    /**
     * Field for the 'olcIndexIntLen' attribute.
     */
    @ConfigurationElement(attributeType = "olcIndexIntLen")
    private Integer olcIndexIntLen;

    /**
     * Field for the 'olcIndexSubstrAnyLen' attribute.
     */
    @ConfigurationElement(attributeType = "olcIndexSubstrAnyLen")
    private Integer olcIndexSubstrAnyLen;

    /**
     * Field for the 'olcIndexSubstrAnyStep' attribute.
     */
    @ConfigurationElement(attributeType = "olcIndexSubstrAnyStep")
    private Integer olcIndexSubstrAnyStep;

    /**
     * Field for the 'olcIndexSubstrIfMaxLen' attribute.
     */
    @ConfigurationElement(attributeType = "olcIndexSubstrIfMaxLen")
    private Integer olcIndexSubstrIfMaxLen;

    /**
     * Field for the 'olcIndexSubstrIfMinLen' attribute.
     */
    @ConfigurationElement(attributeType = "olcIndexSubstrIfMinLen")
    private Integer olcIndexSubstrIfMinLen;

    /**
     * Field for the 'olcLdapSyntaxes' attribute.
     */
    @ConfigurationElement(attributeType = "olcLdapSyntaxes")
    private List<String> olcLdapSyntaxes = new ArrayList<String>();

    /**
     * Field for the 'olcListenerThreads' attribute.  (Added in OpenLDAP 2.4.36)
     */
    @ConfigurationElement(attributeType = "olcListenerThreads")
    private Integer olcListenerThreads;

    /**
     * Field for the 'olcLocalSSF' attribute.
     */
    @ConfigurationElement(attributeType = "olcLocalSSF")
    private Integer olcLocalSSF;

    /**
     * Field for the 'olcLogFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcLogFile")
    private String olcLogFile;

    /**
     * Field for the 'olcLogLevel' attribute.
     */
    @ConfigurationElement(attributeType = "olcLogLevel")
    private List<String> olcLogLevel = new ArrayList<String>();

    /**
     * Field for the 'olcObjectClasses' attribute.
     */
    @ConfigurationElement(attributeType = "olcObjectClasses")
    private List<String> olcObjectClasses = new ArrayList<String>();

    /**
     * Field for the 'olcObjectIdentifier' attribute.
     */
    @ConfigurationElement(attributeType = "olcObjectIdentifier")
    private List<String> olcObjectIdentifier = new ArrayList<String>();

    /**
     * Field for the 'olcPasswordCryptSaltFormat' attribute.
     */
    @ConfigurationElement(attributeType = "olcPasswordCryptSaltFormat")
    private String olcPasswordCryptSaltFormat;

    /**
     * Field for the 'olcPasswordHash' attribute.
     */
    @ConfigurationElement(attributeType = "olcPasswordHash")
    private List<String> olcPasswordHash = new ArrayList<String>();

    /**
     * Field for the 'olcPidFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcPidFile")
    private String olcPidFile;

    /**
     * Field for the 'olcPluginLogFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcPluginLogFile")
    private String olcPluginLogFile;

    /**
     * Field for the 'olcReadOnly' attribute.
     */
    @ConfigurationElement(attributeType = "olcReadOnly")
    private Boolean olcReadOnly;

    /**
     * Field for the 'olcReferral' attribute.
     */
    @ConfigurationElement(attributeType = "olcReferral")
    private String olcReferral;

    /**
     * Field for the 'olcReplogFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcReplogFile")
    private String olcReplogFile;

    /**
     * Field for the 'olcRequires' attribute.
     */
    @ConfigurationElement(attributeType = "olcRequires")
    private List<String> olcRequires = new ArrayList<String>();

    /**
     * Field for the 'olcRestrict' attribute.
     */
    @ConfigurationElement(attributeType = "olcRestrict")
    private List<String> olcRestrict = new ArrayList<String>();

    /**
     * Field for the 'olcReserveLookup' attribute.
     */
    @ConfigurationElement(attributeType = "olcReserveLookup")
    private String olcReserveLookup;

    /**
     * Field for the 'olcRootDSE' attribute.
     */
    @ConfigurationElement(attributeType = "olcRootDSE")
    private List<String> olcRootDSE = new ArrayList<String>();

    /**
     * Field for the 'olcSaslAuxprops' attribute.
     */
    @ConfigurationElement(attributeType = "olcSaslAuxprops")
    private String olcSaslAuxprops;

    /**
     * Field for the 'olcSaslAuxpropsDontUseCopy' attribute. (Added in OpenLDAP 2.4.22)
     */
    @ConfigurationElement(attributeType = "olcSaslAuxpropsDontUseCopy")
    private String olcSaslAuxpropsDontUseCopy;

    /**
     * Field for the 'olcSaslAuxpropsDontUseCopyIgnore' attribute. (Added in OpenLDAP 2.4.22)
     */
    @ConfigurationElement(attributeType = "olcSaslAuxpropsDontUseCopyIgnore")
    private Boolean olcSaslAuxpropsDontUseCopyIgnore;

    /**
     * Field for the 'olcSaslHost' attribute.
     */
    @ConfigurationElement(attributeType = "olcSaslHost")
    private String olcSaslHost;

    /**
     * Field for the 'olcSaslRealm' attribute.
     */
    @ConfigurationElement(attributeType = "olcSaslRealm")
    private String olcSaslRealm;

    /**
     * Field for the 'olcSaslSecProps' attribute.
     */
    @ConfigurationElement(attributeType = "olcSaslSecProps")
    private String olcSaslSecProps;

    /**
     * Field for the 'olcSecurity' attribute.
     */
    @ConfigurationElement(attributeType = "olcSecurity")
    private List<String> olcSecurity = new ArrayList<String>();

    /**
     * Field for the 'olcServerID' attribute.
     */
    @ConfigurationElement(attributeType = "olcServerID")
    private List<String> olcServerID = new ArrayList<String>();

    /**
     * Field for the 'olcSizeLimit' attribute.
     */
    @ConfigurationElement(attributeType = "olcSizeLimit")
    private String olcSizeLimit;

    /**
     * Field for the 'olcSockbufMaxIncoming' attribute.
     */
    @ConfigurationElement(attributeType = "olcSockbufMaxIncoming")
    private Integer olcSockbufMaxIncoming;

    /**
     * Field for the 'olcSockbufMaxIncomingAuth' attribute.
     */
    @ConfigurationElement(attributeType = "olcSockbufMaxIncomingAuth")
    private String olcSockbufMaxIncomingAuth;

    /**
     * Field for the 'olcTCPBuffer' attribute. (Added in OpenLDAP 2.4.18)
     */
    @ConfigurationElement(attributeType = "olcTCPBuffer")
    private List<String> olcTCPBuffer = new ArrayList<String>();

    /**
     * Field for the 'olcThreads' attribute. (Added in OpenLDAP 2.4.36)
     */
    @ConfigurationElement(attributeType = "olcThreads")
    private Integer olcThreads;

    /**
     * Field for the 'olcThreadQueues' attribute.
     */
    @ConfigurationElement(attributeType = "olcThreadQueues")
    private Integer olcThreadQueues;

    /**
     * Field for the 'olcTimeLimit' attribute.
     */
    @ConfigurationElement(attributeType = "olcTimeLimit")
    private List<String> olcTimeLimit = new ArrayList<String>();

    /**
     * Field for the 'olcTLSCACertificateFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSCACertificateFile")
    private String olcTLSCACertificateFile;

    /**
     * Field for the 'olcTLSCACertificatePath' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSCACertificatePath")
    private String olcTLSCACertificatePath;

    /**
     * Field for the 'olcTLSCertificateFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSCertificateFile")
    private String olcTLSCertificateFile;

    /**
     * Field for the 'olcTLSCertificateKeyFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSCertificateKeyFile")
    private String olcTLSCertificateKeyFile;

    /**
     * Field for the 'olcTLSCipherSuite' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSCipherSuite")
    private String olcTLSCipherSuite;

    /**
     * Field for the 'olcTLSCRLCheck' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSCRLCheck")
    private String olcTLSCRLCheck;

    /**
     * Field for the 'olcTLSCRLFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSCRLFile")
    private String olcTLSCRLFile;

    /**
     * Field for the 'olcTLSDHParamFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSDHParamFile")
    private String olcTLSDHParamFile;

    /**
     * Field for the 'olcTLSECName' attribute. (Added in OpenLDAP 2.4.41)
     */
    @ConfigurationElement(attributeType = "olcTLSECName")
    private String olcTLSECName;

    /**
     * Field for the 'olcTLSProtocolMin' attribute. (Added in OpenLDAP 2.4.37)
     */
    @ConfigurationElement(attributeType = "olcTLSProtocolMin")
    private String olcTLSProtocolMin;

    /**
     * Field for the 'olcTLSRandFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSRandFile")
    private String olcTLSRandFile;

    /**
     * Field for the 'olcTLSVerifyClient' attribute.
     */
    @ConfigurationElement(attributeType = "olcTLSVerifyClient")
    private String olcTLSVerifyClient;

    /**
     * Field for the 'olcToolThreads' attribute.
     */
    @ConfigurationElement(attributeType = "olcToolThreads")
    private Integer olcToolThreads;

    /**
     * Field for the 'olcWriteTimeout' attribute.
     */
    @ConfigurationElement(attributeType = "olcWriteTimeout")
    private Integer olcWriteTimeout;


    /**
     * @param strings
     */
    public void addCn( String... strings )
    {
        for ( String string : strings )
        {
            cn.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcAllows( String... strings )
    {
        for ( String string : strings )
        {
            olcAllows.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcAttributeOptions( String... strings )
    {
        for ( String string : strings )
        {
            olcAttributeOptions.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcAttributeTypes( String... strings )
    {
        for ( String string : strings )
        {
            olcAttributeTypes.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcAuthIDRewrite( String... strings )
    {
        for ( String string : strings )
        {
            olcAuthIDRewrite.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcAuthzRegexp( String... strings )
    {
        for ( String string : strings )
        {
            olcAuthzRegexp.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcDisallows( String... strings )
    {
        for ( String string : strings )
        {
            olcDisallows.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcDitContentRules( String... strings )
    {
        for ( String string : strings )
        {
            olcDitContentRules.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcLdapSyntaxes( String... strings )
    {
        for ( String string : strings )
        {
            olcLdapSyntaxes.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcLogLevel( String... strings )
    {
        for ( String string : strings )
        {
            olcLogLevel.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcObjectClasses( String... strings )
    {
        for ( String string : strings )
        {
            olcObjectClasses.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcObjectIdentifier( String... strings )
    {
        for ( String string : strings )
        {
            olcObjectIdentifier.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcPasswordHash( String... strings )
    {
        for ( String string : strings )
        {
            olcPasswordHash.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcRequires( String... strings )
    {
        for ( String string : strings )
        {
            olcRequires.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcRestrict( String... strings )
    {
        for ( String string : strings )
        {
            olcRestrict.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcRootDSE( String... strings )
    {
        for ( String string : strings )
        {
            olcRootDSE.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcSecurity( String... strings )
    {
        for ( String string : strings )
        {
            olcSecurity.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcServerID( String... strings )
    {
        for ( String string : strings )
        {
            olcServerID.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcTCPBuffer( String... strings )
    {
        for ( String string : strings )
        {
            olcTCPBuffer.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcTimeLimit( String... strings )
    {
        for ( String string : strings )
        {
            olcTimeLimit.add( string );
        }
    }


    public void clearCn()
    {
        cn.clear();
    }


    public void clearOlcAllows()
    {
        olcAllows.clear();
    }


    public void clearOlcAttributeOptions()
    {
        olcAttributeOptions.clear();
    }


    public void clearOlcAttributeTypes()
    {
        olcAttributeTypes.clear();
    }


    public void clearOlcAuthIDRewrite()
    {
        olcAuthIDRewrite.clear();
    }


    public void clearOlcAuthzRegexp()
    {
        olcAuthzRegexp.clear();
    }


    public void clearOlcDisallows()
    {
        olcDisallows.clear();
    }


    public void clearOlcDitContentRules()
    {
        olcDitContentRules.clear();
    }


    public void clearOlcLdapSyntaxes()
    {
        olcLdapSyntaxes.clear();
    }


    public void clearOlcLogLevel()
    {
        olcLogLevel.clear();
    }


    public void clearOlcObjectClasses()
    {
        olcObjectClasses.clear();
    }


    public void clearOlcObjectIdentifier()
    {
        olcObjectIdentifier.clear();
    }


    public void clearOlcPasswordHash()
    {
        olcPasswordHash.clear();
    }


    public void clearOlcRequires()
    {
        olcRequires.clear();
    }


    public void clearOlcRestrict()
    {
        olcRestrict.clear();
    }


    public void clearOlcRootDSE()
    {
        olcRootDSE.clear();
    }


    public void clearOlcSecurity()
    {
        olcSecurity.clear();
    }


    public void clearOlcServerID()
    {
        olcServerID.clear();
    }


    public void clearOlcTCPBuffer()
    {
        olcTCPBuffer.clear();
    }


    public void clearOlcTimeLimit()
    {
        olcTimeLimit.clear();
    }


    /**
     * @return the cn
     */
    public List<String> getCn()
    {
        return copyListString( cn );
    }


    /**
     * @return the olcAllows
     */
    public List<String> getOlcAllows()
    {
        return copyListString( olcAllows );
    }


    /**
     * @return the olcArgsFile
     */
    public String getOlcArgsFile()
    {
        return olcArgsFile;
    }


    /**
     * @return the olcAttributeOptions
     */
    public List<String> getOlcAttributeOptions()
    {
        return copyListString( olcAttributeOptions );
    }


    /**
     * @return the olcAttributeTypes
     */
    public List<String> getOlcAttributeTypes()
    {
        return copyListString( olcAttributeTypes );
    }


    /**
     * @return the olcAuthIDRewrite
     */
    public List<String> getOlcAuthIDRewrite()
    {
        return copyListString( olcAuthIDRewrite );
    }


    /**
     * @return the olcAuthzPolicy
     */
    public String getOlcAuthzPolicy()
    {
        return olcAuthzPolicy;
    }


    /**
     * @return the olcAuthzRegexp
     */
    public List<String> getOlcAuthzRegexp()
    {
        return copyListString( olcAuthzRegexp );
    }


    /**
     * @return the olcConcurrency
     */
    public Integer getOlcConcurrency()
    {
        return olcConcurrency;
    }


    /**
     * @return the olcConfigDir
     */
    public String getOlcConfigDir()
    {
        return olcConfigDir;
    }


    /**
     * @return the olcConfigFile
     */
    public String getOlcConfigFile()
    {
        return olcConfigFile;
    }


    /**
     * @return the olcConnMaxPending
     */
    public Integer getOlcConnMaxPending()
    {
        return olcConnMaxPending;
    }


    /**
     * @return the olcConnMaxPendingAuth
     */
    public Integer getOlcConnMaxPendingAuth()
    {
        return olcConnMaxPendingAuth;
    }


    /**
     * @return the olcDisallows
     */
    public List<String> getOlcDisallows()
    {
        return copyListString( olcDisallows );
    }


    /**
     * @return the olcDitContentRules
     */
    public List<String> getOlcDitContentRules()
    {
        return copyListString( olcDitContentRules );
    }


    /**
     * @return the olcGentleHUP
     */
    public Boolean getOlcGentleHUP()
    {
        return olcGentleHUP;
    }


    /**
     * @return the olcIdleTimeout
     */
    public Integer getOlcIdleTimeout()
    {
        return olcIdleTimeout;
    }


    /**
     * @return the olcIndexHash64
     */
    public Boolean getOlcIndexHash64()
    {
        return olcIndexHash64;
    }


    /**
     * @return the olcIndexIntLen
     */
    public Integer getOlcIndexIntLen()
    {
        return olcIndexIntLen;
    }


    /**
     * @return the olcIndexSubstrAnyLen
     */
    public Integer getOlcIndexSubstrAnyLen()
    {
        return olcIndexSubstrAnyLen;
    }


    /**
     * @return the olcIndexSubstrAnyStep
     */
    public Integer getOlcIndexSubstrAnyStep()
    {
        return olcIndexSubstrAnyStep;
    }


    /**
     * @return the olcIndexSubstrIfMaxLen
     */
    public Integer getOlcIndexSubstrIfMaxLen()
    {
        return olcIndexSubstrIfMaxLen;
    }


    /**
     * @return the olcIndexSubstrIfMinLen
     */
    public Integer getOlcIndexSubstrIfMinLen()
    {
        return olcIndexSubstrIfMinLen;
    }


    /**
     * @return the olcLdapSyntaxes
     */
    public List<String> getOlcLdapSyntaxes()
    {
        return copyListString( olcLdapSyntaxes );
    }


    /**
     * @return the olcListenerThreads
     */
    public Integer getOlcListenerThreads()
    {
        return olcListenerThreads;
    }


    /**
     * @return the olcLocalSSF
     */
    public Integer getOlcLocalSSF()
    {
        return olcLocalSSF;
    }


    /**
     * @return the olcLogFile
     */
    public String getOlcLogFile()
    {
        return olcLogFile;
    }


    /**
     * @return the olcLogLevel
     */
    public List<String> getOlcLogLevel()
    {
        return copyListString( olcLogLevel );
    }


    /**
     * @return the olcObjectClasses
     */
    public List<String> getOlcObjectClasses()
    {
        return copyListString( olcObjectClasses );
    }


    /**
     * @return the olcObjectIdentifier
     */
    public List<String> getOlcObjectIdentifier()
    {
        return copyListString( olcObjectIdentifier );
    }


    /**
     * @return the olcPasswordCryptSaltFormat
     */
    public String getOlcPasswordCryptSaltFormat()
    {
        return olcPasswordCryptSaltFormat;
    }


    /**
     * @return the olcPasswordHash
     */
    public List<String> getOlcPasswordHash()
    {
        return copyListString( olcPasswordHash );
    }


    /**
     * @return the olcPidFile
     */
    public String getOlcPidFile()
    {
        return olcPidFile;
    }


    /**
     * @return the olcPluginLogFile
     */
    public String getOlcPluginLogFile()
    {
        return olcPluginLogFile;
    }


    /**
     * @return the olcReadOnly
     */
    public Boolean getOlcReadOnly()
    {
        return olcReadOnly;
    }


    /**
     * @return the olcReferral
     */
    public String getOlcReferral()
    {
        return olcReferral;
    }


    /**
     * @return the olcReplogFile
     */
    public String getOlcReplogFile()
    {
        return olcReplogFile;
    }


    /**
     * @return the olcRequires
     */
    public List<String> getOlcRequires()
    {
        return copyListString( olcRequires );
    }


    /**
     * @return the olcReserveLookup
     */
    public String getOlcReserveLookup()
    {
        return olcReserveLookup;
    }


    /**
     * @return the olcRestrict
     */
    public List<String> getOlcRestrict()
    {
        return copyListString( olcRestrict );
    }


    /**
     * @return the olcRootDSE
     */
    public List<String> getOlcRootDSE()
    {
        return copyListString( olcRootDSE );
    }


    /**
     * @return the olcSaslAuxprops
     */
    public String getOlcSaslAuxprops()
    {
        return olcSaslAuxprops;
    }


    /**
     * @return the olcSaslAuxpropsDontUseCopy
     */
    public String getOlcSaslAuxpropsDontUseCopy()
    {
        return olcSaslAuxpropsDontUseCopy;
    }


    /**
     * @return the olcSaslAuxpropsDontUseCopyIgnore
     */
    public Boolean getOlcSaslAuxpropsDontUseCopyIgnore()
    {
        return olcSaslAuxpropsDontUseCopyIgnore;
    }


    /**
     * @return the olcSaslHost
     */
    public String getOlcSaslHost()
    {
        return olcSaslHost;
    }


    /**
     * @return the olcSaslRealm
     */
    public String getOlcSaslRealm()
    {
        return olcSaslRealm;
    }


    /**
     * @return the olcSaslSecProps
     */
    public String getOlcSaslSecProps()
    {
        return olcSaslSecProps;
    }


    /**
     * @return the olcSecurity
     */
    public List<String> getOlcSecurity()
    {
        return copyListString( olcSecurity );
    }


    /**
     * @return the olcServerID
     */
    public List<String> getOlcServerID()
    {
        return copyListString( olcServerID );
    }


    /**
     * @return the olcSizeLimit
     */
    public String getOlcSizeLimit()
    {
        return olcSizeLimit;
    }


    /**
     * @return the olcSockbufMaxIncoming
     */
    public Integer getOlcSockbufMaxIncoming()
    {
        return olcSockbufMaxIncoming;
    }


    /**
     * @return the olcSockbufMaxIncomingAuth
     */
    public String getOlcSockbufMaxIncomingAuth()
    {
        return olcSockbufMaxIncomingAuth;
    }


    /**
     * @return the olcTCPBuffer
     */
    public List<String> getOlcTCPBuffer()
    {
        return copyListString( olcTCPBuffer );
    }


    /**
     * @return the olcThreads
     */
    public Integer getOlcThreads()
    {
        return olcThreads;
    }


    /**
     * @return the olcThreadQueues
     */
    public Integer getOlcThreadQueues()
    {
        return olcThreadQueues;
    }


    /**
     * @return the olcTimeLimit
     */
    public List<String> getOlcTimeLimit()
    {
        return copyListString( olcTimeLimit );
    }


    /**
     * @return the olcTLSCACertificateFile
     */
    public String getOlcTLSCACertificateFile()
    {
        return olcTLSCACertificateFile;
    }


    /**
     * @return the olcTLSCACertificatePath
     */
    public String getOlcTLSCACertificatePath()
    {
        return olcTLSCACertificatePath;
    }


    /**
     * @return the olcTLSCertificateFile
     */
    public String getOlcTLSCertificateFile()
    {
        return olcTLSCertificateFile;
    }


    /**
     * @return the olcTLSCertificateKeyFile
     */
    public String getOlcTLSCertificateKeyFile()
    {
        return olcTLSCertificateKeyFile;
    }


    /**
     * @return the olcTLSCipherSuite
     */
    public String getOlcTLSCipherSuite()
    {
        return olcTLSCipherSuite;
    }


    /**
     * @return the olcTLSCRLCheck
     */
    public String getOlcTLSCRLCheck()
    {
        return olcTLSCRLCheck;
    }


    /**
     * @return the olcTLSCRLFile
     */
    public String getOlcTLSCRLFile()
    {
        return olcTLSCRLFile;
    }


    /**
     * @return the olcTLSECName
     */
    public String getOlcTLSECName()
    {
        return olcTLSECName;
    }


    /**
     * @return the olcTLSDHParamFile
     */
    public String getOlcTLSDHParamFile()
    {
        return olcTLSDHParamFile;
    }


    /**
     * @return the olcTLSProtocolMin
     */
    public String getOlcTLSProtocolMin()
    {
        return olcTLSProtocolMin;
    }


    /**
     * @return the olcTLSRandFile
     */
    public String getOlcTLSRandFile()
    {
        return olcTLSRandFile;
    }


    /**
     * @return the olcTLSVerifyClient
     */
    public String getOlcTLSVerifyClient()
    {
        return olcTLSVerifyClient;
    }


    /**
     * @return the olcToolThreads
     */
    public Integer getOlcToolThreads()
    {
        return olcToolThreads;
    }


    /**
     * @return the olcWriteTimeout
     */
    public Integer getOlcWriteTimeout()
    {
        return olcWriteTimeout;
    }


    /**
     * @param cn the cn to set
     */
    public void setCn( List<String> cn )
    {
        this.cn = copyListString( cn );
    }


    /**
     * @param olcAllows the olcAllows to set
     */
    public void setOlcAllows( List<String> olcAllows )
    {
        this.olcAllows = copyListString( olcAllows );
    }


    /**
     * @param olcArgsFile the olcArgsFile to set
     */
    public void setOlcArgsFile( String olcArgsFile )
    {
        this.olcArgsFile = olcArgsFile;
    }


    /**
     * @param olcAttributeOptions the olcAttributeOptions to set
     */
    public void setOlcAttributeOptions( List<String> olcAttributeOptions )
    {
        this.olcAttributeOptions = copyListString( olcAttributeOptions );
    }


    /**
     * @param olcAttributeTypes the olcAttributeTypes to set
     */
    public void setOlcAttributeTypes( List<String> olcAttributeTypes )
    {
        this.olcAttributeTypes = copyListString( olcAttributeTypes );
    }


    /**
     * @param olcAuthIDRewrite the olcAuthIDRewrite to set
     */
    public void setOlcAuthIDRewrite( List<String> olcAuthIDRewrite )
    {
        this.olcAuthIDRewrite = copyListString( olcAuthIDRewrite );
    }


    /**
     * @param olcAuthzPolicy the olcAuthzPolicy to set
     */
    public void setOlcAuthzPolicy( String olcAuthzPolicy )
    {
        this.olcAuthzPolicy = olcAuthzPolicy;
    }


    /**
     * @param olcAuthzRegexp the olcAuthzRegexp to set
     */
    public void setOlcAuthzRegexp( List<String> olcAuthzRegexp )
    {
        this.olcAuthzRegexp = copyListString( olcAuthzRegexp );
    }


    /**
     * @param olcConcurrency the olcConcurrency to set
     */
    public void setOlcConcurrency( Integer olcConcurrency )
    {
        this.olcConcurrency = olcConcurrency;
    }


    /**
     * @param olcConfigDir the olcConfigDir to set
     */
    public void setOlcConfigDir( String olcConfigDir )
    {
        this.olcConfigDir = olcConfigDir;
    }


    /**
     * @param olcConfigFile the olcConfigFile to set
     */
    public void setOlcConfigFile( String olcConfigFile )
    {
        this.olcConfigFile = olcConfigFile;
    }


    /**
     * @param olcConnMaxPending the olcConnMaxPending to set
     */
    public void setOlcConnMaxPending( Integer olcConnMaxPending )
    {
        this.olcConnMaxPending = olcConnMaxPending;
    }


    /**
     * @param olcConnMaxPendingAuth the olcConnMaxPendingAuth to set
     */
    public void setOlcConnMaxPendingAuth( Integer olcConnMaxPendingAuth )
    {
        this.olcConnMaxPendingAuth = olcConnMaxPendingAuth;
    }


    /**
     * @param olcDisallows the olcDisallows to set
     */
    public void setOlcDisallows( List<String> olcDisallows )
    {
        this.olcDisallows = copyListString( olcDisallows );
    }


    /**
     * @param olcDitContentRules the olcDitContentRules to set
     */
    public void setOlcDitContentRules( List<String> olcDitContentRules )
    {
        this.olcDitContentRules = copyListString( olcDitContentRules );
    }


    /**
     * @param olcGentleHUP the olcGentleHUP to set
     */
    public void setOlcGentleHUP( Boolean olcGentleHUP )
    {
        this.olcGentleHUP = olcGentleHUP;
    }


    /**
     * @param olcIdleTimeout the olcIdleTimeout to set
     */
    public void setOlcIdleTimeout( Integer olcIdleTimeout )
    {
        this.olcIdleTimeout = olcIdleTimeout;
    }


    /**
     * @param olcIndexHash64 the olcIndexHash64 to set
     */
    public void setOlcIndexHash64( Boolean olcIndexHash64 )
    {
        this.olcIndexHash64 = olcIndexHash64;
    }


    /**
     * @param olcIndexIntLen the olcIndexIntLen to set
     */
    public void setOlcIndexIntLen( Integer olcIndexIntLen )
    {
        this.olcIndexIntLen = olcIndexIntLen;
    }


    /**
     * @param olcIndexSubstrAnyLen the olcIndexSubstrAnyLen to set
     */
    public void setOlcIndexSubstrAnyLen( Integer olcIndexSubstrAnyLen )
    {
        this.olcIndexSubstrAnyLen = olcIndexSubstrAnyLen;
    }


    /**
     * @param olcIndexSubstrAnyStep the olcIndexSubstrAnyStep to set
     */
    public void setOlcIndexSubstrAnyStep( Integer olcIndexSubstrAnyStep )
    {
        this.olcIndexSubstrAnyStep = olcIndexSubstrAnyStep;
    }


    /**
     * @param olcIndexSubstrIfMaxLen the olcIndexSubstrIfMaxLen to set
     */
    public void setOlcIndexSubstrIfMaxLen( Integer olcIndexSubstrIfMaxLen )
    {
        this.olcIndexSubstrIfMaxLen = olcIndexSubstrIfMaxLen;
    }


    /**
     * @param olcIndexSubstrIfMinLen the olcIndexSubstrIfMinLen to set
     */
    public void setOlcIndexSubstrIfMinLen( Integer olcIndexSubstrIfMinLen )
    {
        this.olcIndexSubstrIfMinLen = olcIndexSubstrIfMinLen;
    }


    /**
     * @param olcLdapSyntaxes the olcLdapSyntaxes to set
     */
    public void setOlcLdapSyntaxes( List<String> olcLdapSyntaxes )
    {
        this.olcLdapSyntaxes = copyListString( olcLdapSyntaxes );
    }


    /**
     * @param olcListenerThreads the olcListenerThreads to set
     */
    public void setOlcListenerThreads( Integer olcListenerThreads )
    {
        this.olcListenerThreads = olcListenerThreads;
    }


    /**
     * @param olcLocalSSF the olcLocalSSF to set
     */
    public void setOlcLocalSSF( Integer olcLocalSSF )
    {
        this.olcLocalSSF = olcLocalSSF;
    }


    /**
     * @param olcLogFile the olcLogFile to set
     */
    public void setOlcLogFile( String olcLogFile )
    {
        this.olcLogFile = olcLogFile;
    }


    /**
     * @param olcLogLevel the olcLogLevel to set
     */
    public void setOlcLogLevel( List<String> olcLogLevel )
    {
        this.olcLogLevel = copyListString( olcLogLevel );
    }


    /**
     * @param olcObjectClasses the olcObjectClasses to set
     */
    public void setOlcObjectClasses( List<String> olcObjectClasses )
    {
        this.olcObjectClasses = copyListString( olcObjectClasses );
    }


    /**
     * @param olcObjectIdentifier the olcObjectIdentifier to set
     */
    public void setOlcObjectIdentifier( List<String> olcObjectIdentifier )
    {
        this.olcObjectIdentifier = copyListString( olcObjectIdentifier );
    }


    /**
     * @param olcPasswordCryptSaltFormat the olcPasswordCryptSaltFormat to set
     */
    public void setOlcPasswordCryptSaltFormat( String olcPasswordCryptSaltFormat )
    {
        this.olcPasswordCryptSaltFormat = olcPasswordCryptSaltFormat;
    }


    /**
     * @param olcPasswordHash the olcPasswordHash to set
     */
    public void setOlcPasswordHash( List<String> olcPasswordHash )
    {
        this.olcPasswordHash = copyListString( olcPasswordHash );
    }


    /**
     * @param olcPidFile the olcPidFile to set
     */
    public void setOlcPidFile( String olcPidFile )
    {
        this.olcPidFile = olcPidFile;
    }


    /**
     * @param olcPluginLogFile the olcPluginLogFile to set
     */
    public void setOlcPluginLogFile( String olcPluginLogFile )
    {
        this.olcPluginLogFile = olcPluginLogFile;
    }


    /**
     * @param olcReadOnly the olcReadOnly to set
     */
    public void setOlcReadOnly( Boolean olcReadOnly )
    {
        this.olcReadOnly = olcReadOnly;
    }


    /**
     * @param olcReferral the olcReferral to set
     */
    public void setOlcReferral( String olcReferral )
    {
        this.olcReferral = olcReferral;
    }


    /**
     * @param olcReplogFile the olcReplogFile to set
     */
    public void setOlcReplogFile( String olcReplogFile )
    {
        this.olcReplogFile = olcReplogFile;
    }


    /**
     * @param olcRequires the olcRequires to set
     */
    public void setOlcRequires( List<String> olcRequires )
    {
        this.olcRequires = copyListString( olcRequires );
    }


    /**
     * @param olcReserveLookup the olcReserveLookup to set
     */
    public void setOlcReserveLookup( String olcReserveLookup )
    {
        this.olcReserveLookup = olcReserveLookup;
    }


    /**
     * @param olcRestrict the olcRestrict to set
     */
    public void setOlcRestrict( List<String> olcRestrict )
    {
        this.olcRestrict = copyListString( olcRestrict );
    }


    /**
     * @param olcRootDSE the olcRootDSE to set
     */
    public void setOlcRootDSE( List<String> olcRootDSE )
    {
        this.olcRootDSE = copyListString( olcRootDSE );
    }


    /**
     * @param olcSaslAuxprops the olcSaslAuxprops to set
     */
    public void setOlcSaslAuxprops( String olcSaslAuxprops )
    {
        this.olcSaslAuxprops = olcSaslAuxprops;
    }


    /**
     * @param olcSaslAuxpropsDontUseCopy the olcSaslAuxpropsDontUseCopy to set
     */
    public void setOlcSaslAuxpropsDontUseCopy( String olcSaslAuxpropsDontUseCopy )
    {
        this.olcSaslAuxpropsDontUseCopy = olcSaslAuxpropsDontUseCopy;
    }


    /**
     * @param olcSaslAuxpropsDontUseCopyIgnore the olcSaslAuxpropsDontUseCopyIgnore to set
     */
    public void setOlcSaslAuxpropsDontUseCopyIgnore( Boolean olcSaslAuxpropsDontUseCopyIgnore )
    {
        this.olcSaslAuxpropsDontUseCopyIgnore = olcSaslAuxpropsDontUseCopyIgnore;
    }


    /**
     * @param olcSaslHost the olcSaslHost to set
     */
    public void setOlcSaslHost( String olcSaslHost )
    {
        this.olcSaslHost = olcSaslHost;
    }


    /**
     * @param olcSaslRealm the olcSaslRealm to set
     */
    public void setOlcSaslRealm( String olcSaslRealm )
    {
        this.olcSaslRealm = olcSaslRealm;
    }


    /**
     * @param olcSaslSecProps the olcSaslSecProps to set
     */
    public void setOlcSaslSecProps( String olcSaslSecProps )
    {
        this.olcSaslSecProps = olcSaslSecProps;
    }


    /**
     * @param olcSecurity the olcSecurity to set
     */
    public void setOlcSecurity( List<String> olcSecurity )
    {
        this.olcSecurity = copyListString( olcSecurity );
    }


    /**
     * @param olcServerID the olcServerID to set
     */
    public void setOlcServerID( List<String> olcServerID )
    {
        this.olcServerID = copyListString( olcServerID );
    }


    /**
     * @param olcSizeLimit the olcSizeLimit to set
     */
    public void setOlcSizeLimit( String olcSizeLimit )
    {
        this.olcSizeLimit = olcSizeLimit;
    }


    /**
     * @param olcSockbufMaxIncoming the olcSockbufMaxIncoming to set
     */
    public void setOlcSockbufMaxIncoming( int olcSockbufMaxIncoming )
    {
        this.olcSockbufMaxIncoming = olcSockbufMaxIncoming;
    }


    /**
     * @param olcSockbufMaxIncomingAuth the olcSockbufMaxIncomingAuth to set
     */
    public void setOlcSockbufMaxIncomingAuth( String olcSockbufMaxIncomingAuth )
    {
        this.olcSockbufMaxIncomingAuth = olcSockbufMaxIncomingAuth;
    }


    /**
     * @param olcTCPBuffer the olcTCPBuffer to set
     */
    public void setOlcTCPBuffer( List<String> olcTCPBuffer )
    {
        this.olcTCPBuffer = copyListString( olcTCPBuffer );
    }


    /**
     * @param olcThreads the olcThreads to set
     */
    public void setOlcThreads( Integer olcThreads )
    {
        this.olcThreads = olcThreads;
    }


    /**
     * @param olcThreadQueues the olcThreadQueues to set
     */
    public void setOlcThreadQueues( Integer olcThreadQueues )
    {
        this.olcThreadQueues = olcThreadQueues;
    }


    /**
     * @param olcTimeLimit the olcTimeLimit to set
     */
    public void setOlcTimeLimit( List<String> olcTimeLimit )
    {
        this.olcTimeLimit = copyListString( olcTimeLimit );
    }


    /**
     * @param olcTLSCACertificateFile the olcTLSCACertificateFile to set
     */
    public void setOlcTLSCACertificateFile( String olcTLSCACertificateFile )
    {
        this.olcTLSCACertificateFile = olcTLSCACertificateFile;
    }


    /**
     * @param olcTLSCACertificatePath the olcTLSCACertificatePath to set
     */
    public void setOlcTLSCACertificatePath( String olcTLSCACertificatePath )
    {
        this.olcTLSCACertificatePath = olcTLSCACertificatePath;
    }


    /**
     * @param olcTLSCertificateFile the olcTLSCertificateFile to set
     */
    public void setOlcTLSCertificateFile( String olcTLSCertificateFile )
    {
        this.olcTLSCertificateFile = olcTLSCertificateFile;
    }


    /**
     * @param olcTLSCertificateKeyFile the olcTLSCertificateKeyFile to set
     */
    public void setOlcTLSCertificateKeyFile( String olcTLSCertificateKeyFile )
    {
        this.olcTLSCertificateKeyFile = olcTLSCertificateKeyFile;
    }


    /**
     * @param olcTLSCipherSuite the olcTLSCipherSuite to set
     */
    public void setOlcTLSCipherSuite( String olcTLSCipherSuite )
    {
        this.olcTLSCipherSuite = olcTLSCipherSuite;
    }


    /**
     * @param olcTLSCRLCheck the olcTLSCRLCheck to set
     */
    public void setOlcTLSCRLCheck( String olcTLSCRLCheck )
    {
        this.olcTLSCRLCheck = olcTLSCRLCheck;
    }


    /**
     * @param olcTLSCRLFile the olcTLSCRLFile to set
     */
    public void setOlcTLSCRLFile( String olcTLSCRLFile )
    {
        this.olcTLSCRLFile = olcTLSCRLFile;
    }


    /**
     * @param olcTLSDHParamFile the olcTLSDHParamFile to set
     */
    public void setOlcTLSDHParamFile( String olcTLSDHParamFile )
    {
        this.olcTLSDHParamFile = olcTLSDHParamFile;
    }


    /**
     * @param olcTLSECName the olcTLSECName to set
     */
    public void setOlcTLSECName( String olcTLSECName )
    {
        this.olcTLSECName = olcTLSECName;
    }


    /**
     * @param olcTLSProtocolMin the olcTLSProtocolMin to set
     */
    public void setOlcTLSProtocolMin( String olcTLSProtocolMin )
    {
        this.olcTLSProtocolMin = olcTLSProtocolMin;
    }


    /**
     * @param olcTLSRandFile the olcTLSRandFile to set
     */
    public void setOlcTLSRandFile( String olcTLSRandFile )
    {
        this.olcTLSRandFile = olcTLSRandFile;
    }


    /**
     * @param olcTLSVerifyClient the olcTLSVerifyClient to set
     */
    public void setOlcTLSVerifyClient( String olcTLSVerifyClient )
    {
        this.olcTLSVerifyClient = olcTLSVerifyClient;
    }


    /**
     * @param olcToolThreads the olcToolThreads to set
     */
    public void setOlcToolThreads( Integer olcToolThreads )
    {
        this.olcToolThreads = olcToolThreads;
    }


    /**
     * @param olcWriteTimeout the olcWriteTimeout to set
     */
    public void setOlcWriteTimeout( Integer olcWriteTimeout )
    {
        this.olcWriteTimeout = olcWriteTimeout;
    }
}
