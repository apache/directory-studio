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
package org.apache.directory.studio.apacheds.configuration.model.v150;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.configuration.model.AbstractServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationVersionEnum;


/**
 * This class represents a Server Configuration.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerConfigurationV150 extends AbstractServerConfiguration implements ServerConfiguration
{
    /** The port */
    private int port;

    /** The principal */
    private String principal;

    /** The password */
    private String password;

    /** The flag for Allow Anonymous Access */
    private boolean allowAnonymousAccess;

    /** The Max Time Limit */
    private int maxTimeLimit;

    /** the Max Size Limit */
    private int maxSizeLimit;

    /** The Synchonization Period */
    private long synchronizationPeriod;

    /** The Maximum number of Threads */
    private int maxThreads;

    /** The flag for Enable Access Control */
    private boolean enableAccessControl;

    /** The flag for Enable Kerberos */
    private boolean enableKerberos;

    /** The flag for Enable NTP */
    private boolean enableNTP;

    /** The flag for Enable Change Password */
    private boolean enableChangePassword;

    /** The flag for Denormalize Operational Attributes */
    private boolean denormalizeOpAttr;

    /** The Binary Attributes */
    private List<String> binaryAttributes;

    /** The Partitions */
    private List<Partition> partitions;

    /** The Interceptors */
    private List<Interceptor> interceptors;

    /** The Extended Operations */
    private List<ExtendedOperation> extendedOperations;


    /**
     * Creates a new instance of ServerConfiguration.
     */
    public ServerConfigurationV150()
    {
        super( ServerConfigurationVersionEnum.VERSION_1_5_0 );

        partitions = new ArrayList<Partition>();
        interceptors = new ArrayList<Interceptor>();
        extendedOperations = new ArrayList<ExtendedOperation>();
        binaryAttributes = new ArrayList<String>();
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
     * Gets the Enable NTP flag.
     *
     * @return
     *      true if NTP is enabled
     */
    public boolean isEnableNTP()
    {
        return enableNTP;
    }


    /**
     * Sets the Enable NTP flag.
     *
     * @param enableNTP
     *      the new value
     */
    public void setEnableNTP( boolean enableNTP )
    {
        this.enableNTP = enableNTP;
    }


    /**
     * Gets the Extended Operations List.
     *
     * @return
     *      the Extended Operations List
     */
    public List<ExtendedOperation> getExtendedOperations()
    {
        return extendedOperations;
    }


    /**
     * Sets the Extended Operations List.
     *
     * @param extendedOperations
     *      the new value
     */
    public void setExtendedOperations( List<ExtendedOperation> extendedOperations )
    {
        this.extendedOperations = extendedOperations;
    }


    /**
     * Adds ab Extended Operation.
     *
     * @param extendedOperation
     *      the Extended Operation to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addExtendedOperation( ExtendedOperation extendedOperation )
    {
        return extendedOperations.add( extendedOperation );
    }


    /**
     * Removes an Extended Operation.
     *
     * @param extendedOperation
     *      the Extended Operation to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeExtendedOperation( ExtendedOperation extendedOperation )
    {
        return extendedOperations.remove( extendedOperation );
    }


    /**
     * Removes all ExtendedOperations.
     */
    public void clearExtendedOperations()
    {
        extendedOperations.clear();
    }


    /**
     * Gets the Interceptors List.
     *
     * @return
     *      the Interceptors List
     */
    public List<Interceptor> getInterceptors()
    {
        return interceptors;
    }


    /**
     * Sets the Interceptors List.
     *
     * @param interceptors
     *      the new value
     */
    public void setInterceptors( List<Interceptor> interceptors )
    {
        this.interceptors = interceptors;
    }


    /**
     * Adds an Interceptor.
     *
     * @param interceptor
     *      the Interceptor to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addInterceptor( Interceptor interceptor )
    {
        return interceptors.add( interceptor );
    }


    /**
     * Removes an Interceptor.
     *
     * @param interceptor
     *      the Interceptor to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeInterceptor( Interceptor interceptor )
    {
        return interceptors.remove( interceptor );
    }


    /**
     * Removes all interceptors.
     */
    public void clearInterceptors()
    {
        interceptors.clear();
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
     * Removes all partitions.
     */
    public void clearPartitions()
    {
        partitions.clear();
    }


    /**
     * Gets the password.
     *
     * @return
     *      the password
     */
    public String getPassword()
    {
        return password;
    }


    /**
     * Sets the password.
     *
     * @param password
     *      the new password
     */
    public void setPassword( String password )
    {
        this.password = password;
    }


    /**
     * Gets the Port.
     *
     * @return
     *      the Port
     */
    public int getPort()
    {
        return port;
    }


    /**
     * Sets the Port
     *
     * @param port
     *      the new value
     */
    public void setPort( int port )
    {
        this.port = port;
    }


    /**
     * Gets the Principal
     *
     * @return
     *      the Principal
     */
    public String getPrincipal()
    {
        return principal;
    }


    /**
     * Sets the Principal
     *
     * @param principal
     *      the new value
     */
    public void setPrincipal( String principal )
    {
        this.principal = principal;
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
     * Sets the Synchonization Period.
     *
     * @param synchronizationPeriod
     *      the new value
     */
    public void setSynchronizationPeriod( long synchronizationPeriod )
    {
        this.synchronizationPeriod = synchronizationPeriod;
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
     * Gets the Binary Attributes List.
     *
     * @return
     *      the Binary Attributes  List
     */
    public List<String> getBinaryAttributes()
    {
        return binaryAttributes;
    }


    /**
     * Sets the Binary Attributes  List.
     *
     * @param binaryAttributes
     *      the new value
     */
    public void setBinaryAttributes( List<String> binaryAttributes )
    {
        this.binaryAttributes = binaryAttributes;
    }


    /**
     * Adds a Binary Attribute.
     *
     * @param binaryAttribute
     *      the Partition to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addBinaryAttribute( String binaryAttribute )
    {
        return binaryAttributes.add( binaryAttribute );
    }


    /**
     * Removes a Binary Attribute.
     *
     * @param binaryAttribute
     *      the Binary Attribute to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeBinaryAttribute( String binaryAttribute )
    {
        return binaryAttributes.remove( binaryAttribute );
    }


    /**
     * Removes all Binary Attributes.
     */
    public void clearBinaryAttributes()
    {
        binaryAttributes.clear();
    }

}
