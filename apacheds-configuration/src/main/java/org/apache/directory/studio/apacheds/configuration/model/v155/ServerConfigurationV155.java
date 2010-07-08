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
package org.apache.directory.studio.apacheds.configuration.model.v155;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.configuration.model.AbstractServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationVersionEnum;


/**
 * This class represents a Server Configuration.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerConfigurationV155 extends AbstractServerConfiguration implements ServerConfiguration
{
    // LDAP Configuration

    /** The port */
    private int ldapPort;

    /** The flag for Enable LDAP */
    private boolean enableLdap;

    // Limits

    /** The Max Time Limit */
    private int maxTimeLimit;

    /** the Max Size Limit */
    private int maxSizeLimit;

    /** The Synchronization Period */
    private long synchronizationPeriod;

    /** The Maximum number of Threads */
    private int maxThreads;

    /** The Supported Mechanisms */
    private List<SupportedMechanismEnum> supportedMechanisms;

    // SASL Properties

    /** The SASL Host */
    private String saslHost;

    /** The SASL Principal */
    private String saslPrincipal;

    /** The SASL Realms */
    private List<String> saslRealms;

    /** The Search Base DN */
    private String searchBaseDn;

    // Protocols

    /** The flag for Enable Access Control */
    private boolean enableAccessControl;

    /** The flag for Enable Kerberos */
    private boolean enableKerberos;

    /** The port for Kerberos */
    private int kerberosPort;

    /** The flag for Enable NTP */
    private boolean enableNtp;

    /** The port for NTP */
    private int ntpPort;

    /** The flag for Enable DNS */
    private boolean enableDns;

    /** The port for DNS */
    private int dnsPort;

    /** The flag for Enable LDAPS */
    private boolean enableLdaps;

    /** The port for LDAPS */
    private int ldapsPort;

    /** The flag for Enable Change Password */
    private boolean enableChangePassword;

    /** The port for Change Password */
    private int changePasswordPort;

    // Options

    /** The flag for Denormalize Operational Attributes */
    private boolean denormalizeOpAttr;

    /** The flag for Allow Anonymous Access */
    private boolean allowAnonymousAccess;

    // Other configuration elements

    /** The Partitions */
    private List<Partition> partitions;

    /** The Interceptors */
    private List<InterceptorEnum> interceptors;

    /** The Extended Operations */
    private List<ExtendedOperationEnum> extendedOperations;


    /**
     * Creates a new instance of ServerConfiguration.
     */
    public ServerConfigurationV155()
    {
        super( ServerConfigurationVersionEnum.VERSION_1_5_5 );

        supportedMechanisms = new ArrayList<SupportedMechanismEnum>();
        saslRealms = new ArrayList<String>();
        partitions = new ArrayList<Partition>();
        interceptors = new ArrayList<InterceptorEnum>();
        extendedOperations = new ArrayList<ExtendedOperationEnum>();
    }


    /**
     * Adds an Extended Operation.
     *
     * @param extendedOperation
     *      the Extended Operation to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addExtendedOperation( ExtendedOperationEnum extendedOperation )
    {
        return extendedOperations.add( extendedOperation );
    }


    /**
     * Adds an Interceptor.
     *
     * @param interceptor
     *      the Interceptor to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addInterceptor( InterceptorEnum interceptor )
    {
        return interceptors.add( interceptor );
    }


    /**
     * Adds a Partition.
     *
     * @param partition
     *      the Partition to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addPartition( Partition partition )
    {
        return partitions.add( partition );
    }


    /**
    * Adds a SASL Realm.
    *
    * @param qop
    *      the SASL Realm to add
    * @return
    *      true (as per the general contract of the Collection.add method).
    */
    public boolean addSaslRealm( String saslRealm )
    {
        return saslRealms.add( saslRealm );
    }


    /**
     * Adds a Supported Mechanism.
     *
     * @param supportedMechanism
     *      the Supported Mechanism to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addSupportedMechanism( SupportedMechanismEnum supportedMechanism )
    {
        return supportedMechanisms.add( supportedMechanism );
    }


    /**
     * Removes all ExtendedOperations.
     */
    public void clearExtendedOperations()
    {
        extendedOperations.clear();
    }


    /**
     * Removes all interceptors.
     */
    public void clearInterceptors()
    {
        interceptors.clear();
    }


    /**
     * Removes all partitions.
     */
    public void clearPartitions()
    {
        partitions.clear();
    }


    /**
     * Gets the Change Password port.
     *
     * @return
     *      the Change Password port
     */
    public int getChangePasswordPort()
    {
        return changePasswordPort;
    }


    /**
     * Gets the DNS port.
     *
     * @return
     *      the DNS port
     */
    public int getDnsPort()
    {
        return dnsPort;
    }


    /**
     * Gets the Extended Operations List.
     *
     * @return
     *      the Extended Operations List
     */
    public List<ExtendedOperationEnum> getExtendedOperations()
    {
        return extendedOperations;
    }


    /**
     * Gets the Interceptors List.
     *
     * @return
     *      the Interceptors List
     */
    public List<InterceptorEnum> getInterceptors()
    {
        return interceptors;
    }


    /**
     * Gets the Kerberos port.
     *
     * @return
     *      the Kerberos port
     */
    public int getKerberosPort()
    {
        return kerberosPort;
    }


    /**
     * Gets the LDAPS port.
     *
     * @return
     *      the LDAPS port
     */
    public int getLdapsPort()
    {
        return ldapsPort;
    }


    /**
     * Gets the Maximum Size Limit.
     *
     * @return
     *      the Maximum Size Limit
     */
    public int getMaxSizeLimit()
    {
        return maxSizeLimit;
    }


    /**
     * Gets the Maximum number of Threads.
     *
     * @return
     *      the Maximum number of Threads
     */
    public int getMaxThreads()
    {
        return maxThreads;
    }


    /**
     * Gets the Maximum Time Limit.
     *
     * @return
     *      the Maximum Time Limit
     */
    public int getMaxTimeLimit()
    {
        return maxTimeLimit;
    }


    /**
     * Gets the NTP port.
     *
     * @return
     *      the NTP port
     */
    public int getNtpPort()
    {
        return ntpPort;
    }


    /**
     * Gets the Partitions List.
     *
     * @return
     *      the Partitions List
     */
    public List<Partition> getPartitions()
    {
        return partitions;
    }


    /**
     * Gets the LDAP Port.
     *
     * @return
     *      the LDAP Port
     */
    public int getLdapPort()
    {
        return ldapPort;
    }


    /**
     * Gets the SASL Host.
     *
     * @return
     *       the SASL Host
     */
    public String getSaslHost()
    {
        return saslHost;
    }


    /**
     * Gets the SASL Principal.
     *
     * @return
     *      the SASL Principal
     */
    public String getSaslPrincipal()
    {
        return saslPrincipal;
    }


    /**
     * Gets the SASL Realms List.
     *
     * @return
     *      the SASL Realms List
     */
    public List<String> getSaslRealms()
    {
        return saslRealms;
    }


    /**
     * Gets the Search Base DN.
     *
     * @return
     *      the Search Base DN
     */
    public String getSearchBaseDn()
    {
        return searchBaseDn;
    }


    /**
     * Gets the Supported Mechanisms List.
     * 
     * @return
     *      the Supported Mechanisms List
     */
    public List<SupportedMechanismEnum> getSupportedMechanisms()
    {
        return supportedMechanisms;
    }


    /**
     * Gets the Synchronization Period.
     *
     * @return
     *      the Synchronization Period
     */
    public long getSynchronizationPeriod()
    {
        return synchronizationPeriod;
    }


    /**
     * Gets the Allow Anonymous flag.
     *
     * @return
     *      true if the server configuration allows Anonymous Access
     */
    public boolean isAllowAnonymousAccess()
    {
        return allowAnonymousAccess;
    }


    /**
     * Gets the Denormalize Operational Attributes flag.
     *
     * @return
     *      the Denormalize Operational Attributes flag
     */
    public boolean isDenormalizeOpAttr()
    {
        return denormalizeOpAttr;
    }


    /**
     * Gets the Enable Access Control flag.
     *
     * @return
     *      true if Access Control is enabled
     */
    public boolean isEnableAccessControl()
    {
        return enableAccessControl;
    }


    /**
     * Gets the Enable Change Password flag.
     *
     * @return
     *      true if Change Password is enabled
     */
    public boolean isEnableChangePassword()
    {
        return enableChangePassword;
    }


    /**
     * Gets the Enable DNS flag.
     *
     * @return
     *      true if DNS is enabled
     */
    public boolean isEnableDns()
    {
        return enableDns;
    }


    /**
     * Gets the Enable Kerberos flag.
     *
     * @return
     *      true if Kerberos is enabled
     */
    public boolean isEnableKerberos()
    {
        return enableKerberos;
    }


    /**
     * Gets the Enable LDAP flag.
     *
     * @return
     *      true if LDAP is enabled
     */
    public boolean isEnableLdap()
    {
        return enableLdap;
    }


    /**
     * Gets the Enable LDAPS flag.
     *
     * @return
     *      true if LDAPS is enabled
     */
    public boolean isEnableLdaps()
    {
        return enableLdaps;
    }


    /**
     * Gets the Enable NTP flag.
     *
     * @return
     *      true if NTP is enabled
     */
    public boolean isEnableNtp()
    {
        return enableNtp;
    }


    /**
     * Removes an Extended Operation.
     *
     * @param extendedOperation
     *      the Extended Operation to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeExtendedOperation( ExtendedOperationEnum extendedOperation )
    {
        return extendedOperations.remove( extendedOperation );
    }


    /**
     * Removes an Supported Mechanism.
     *
     * @param supportedMechanism
     *      the Supported Mechanism to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeExtendedOperation( String supportedMechanism )
    {
        return supportedMechanisms.remove( supportedMechanism );
    }


    /**
     * Removes an Interceptor.
     *
     * @param interceptor
     *      the Interceptor to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeInterceptor( InterceptorEnum interceptor )
    {
        return interceptors.remove( interceptor );
    }


    /**
     * Removes a Partition.
     *
     * @param partition
     *      the partition to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removePartition( Partition partition )
    {
        return partitions.remove( partition );
    }


    /**
     * Removes a SASL Realm.
     *
     * @param saslRealm
     *      the SASL Realm to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeSaslRealm( String saslRealm )
    {
        return saslRealms.remove( saslRealm );
    }


    /**
     * Sets the Allow Anonymous flag.
     *
     * @param allowAnonymousAccess
     *      the new value
     */
    public void setAllowAnonymousAccess( boolean allowAnonymousAccess )
    {
        this.allowAnonymousAccess = allowAnonymousAccess;
    }


    /**
     * Sets the Change Password port.
     *
     * @param changePasswordPort
     *      the Change Password port
     */
    public void setChangePasswordPort( int changePasswordPort )
    {
        this.changePasswordPort = changePasswordPort;
    }


    /**
     * Sets the Denormalize Operational Attributes flag.
     *
     * @param denormalizeOpAttr
     *      the new Denormalize Operational Attributes flag
     */
    public void setDenormalizeOpAttr( boolean denormalizeOpAttr )
    {
        this.denormalizeOpAttr = denormalizeOpAttr;
    }


    /**
     * Sets the DNS port.
     *
     * @param dnsPort
     *      the DNS port
     */
    public void setDnsPort( int dnsPort )
    {
        this.dnsPort = dnsPort;
    }


    /**
     * Sets the Enable Access Control flag.
     *
     * @param enableAccessControl
     *      the new value
     */
    public void setEnableAccessControl( boolean enableAccessControl )
    {
        this.enableAccessControl = enableAccessControl;
    }


    /**
     * Sets the Enable Change Password flag.
     *
     * @param enableChangePassword
     *      the new value
     */
    public void setEnableChangePassword( boolean enableChangePassword )
    {
        this.enableChangePassword = enableChangePassword;
    }


    /**
     * Sets Enable DNS flag.
     *
     * @param enableDns
     *      the new value
     */
    public void setEnableDns( boolean enableDns )
    {
        this.enableDns = enableDns;
    }


    /**
     * Sets the Enable Kerberos flag.
     *
     * @param enableKerberos
     *      the new value
     */
    public void setEnableKerberos( boolean enableKerberos )
    {
        this.enableKerberos = enableKerberos;
    }


    /**
     * Sets the Enable LDAPS flag.
     *
     * @param enableLdaps
     *      the new value
     */
    public void setEnableLdaps( boolean enableLdaps )
    {
        this.enableLdaps = enableLdaps;
    }


    /**
     * Sets the Enable LDAP flag.
     *
     * @param enableLdap
     *      the new value
     */
    public void setEnableLdap( boolean enableLdap )
    {
        this.enableLdap = enableLdap;
    }


    /**
     * Sets the Enable NTP flag.
     *
     * @param enableNtp
     *      the new value
     */
    public void setEnableNtp( boolean enableNtp )
    {
        this.enableNtp = enableNtp;
    }


    /**
     * Sets the Extended Operations List.
     *
     * @param extendedOperations
     *      the new value
     */
    public void setExtendedOperations( List<ExtendedOperationEnum> extendedOperations )
    {
        this.extendedOperations = extendedOperations;
    }


    /**
     * Sets the Interceptors List.
     *
     * @param interceptors
     *      the new value
     */
    public void setInterceptors( List<InterceptorEnum> interceptors )
    {
        this.interceptors = interceptors;
    }


    /**
     * Sets the Kerberos port.
     *
     * @param kerberosPort
     *      the new value
     */
    public void setKerberosPort( int kerberosPort )
    {
        this.kerberosPort = kerberosPort;
    }


    /**
     * Sets The LDAPS port.
     *
     * @param ldapsPort
     */
    public void setLdapsPort( int ldapsPort )
    {
        this.ldapsPort = ldapsPort;
    }


    /**
     * Sets the Maximum Size Limit.
     *
     * @param maxSizeLimit
     *      the new value
     */
    public void setMaxSizeLimit( int maxSizeLimit )
    {
        this.maxSizeLimit = maxSizeLimit;
    }


    /**
     * Sets the Maximum number of Threads
     *
     * @param maxThreads
     *      the new value
     */
    public void setMaxThreads( int maxThreads )
    {
        this.maxThreads = maxThreads;
    }


    /**
     * Sets the Maximum Time Limit.
     *
     * @param maxTimeLimit
     *      the new value
     */
    public void setMaxTimeLimit( int maxTimeLimit )
    {
        this.maxTimeLimit = maxTimeLimit;
    }


    /**
     * Sets the NTP port.
     *
     * @param ntpPort
     *      the new value
     */
    public void setNtpPort( int ntpPort )
    {
        this.ntpPort = ntpPort;
    }


    /**
     * Sets the Partitions List.
     *
     * @param partitions
     *      the new value
     */
    public void setPartitions( List<Partition> partitions )
    {
        this.partitions = partitions;
    }


    /**
     * Sets the LDAP Port
     *
     * @param ldapPort
     *      the new value
     */
    public void setLdapPort( int ldapPort )
    {
        this.ldapPort = ldapPort;
    }


    /**
     * Sets the SASL Host.
     *
     * @param saslHost
     *      the new value
     */
    public void setSaslHost( String saslHost )
    {
        this.saslHost = saslHost;
    }


    /**
     * Sets the SASL Principal.
     *
     * @param saslPrincipal
     *      the new value
     */
    public void setSaslPrincipal( String saslPrincipal )
    {
        this.saslPrincipal = saslPrincipal;
    }


    /**
     * Sets the SASL Realms List.
     * 
     * @param saslRealms
     *      the new value
     */
    public void setSaslRealms( List<String> saslRealms )
    {
        this.saslRealms = saslRealms;
    }


    /**
     * Sets the Search Base DN
     *
     * @param searchBaseDn
     *      the new value
     */
    public void setSearchBaseDn( String searchBaseDn )
    {
        this.searchBaseDn = searchBaseDn;
    }


    /**
     * Sets the Supported Mechanisms List.
     *
     * @param supportedMechanisms
     *      the new value
     */
    public void setSupportedMechanisms( List<SupportedMechanismEnum> supportedMechanisms )
    {
        this.supportedMechanisms = supportedMechanisms;
    }


    /**
     * Sets the Synchonization Period.
     *
     * @param synchronizationPeriod
     *      the new value
     */
    public void setSynchronizationPeriod( long synchronizationPeriod )
    {
        this.synchronizationPeriod = synchronizationPeriod;
    }
}
