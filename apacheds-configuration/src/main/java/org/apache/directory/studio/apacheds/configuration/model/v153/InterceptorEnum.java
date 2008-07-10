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
package org.apache.directory.studio.apacheds.configuration.model.v153;




/**
 * This enum contains all the interceptors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public enum InterceptorEnum
{
    /** The Normalization Interceptor */
    NORMALIZATION("Normalization", "A name normalization interceptor. This interceptor makes sure all relative "
        + "and distinguished names are normalized before calls are made against "
        + "the respective interface methods on PartitionNexus."),

    /** The Authentication Interceptor */
    AUTHENTICATION("Authentication", "An interceptor that authenticates users."),

    /** The Referral Interceptor */
    REFERRAL("Referral", "An interceptor which is responsible referral handling behaviors.  It "
        + "manages  referral handling behavior when the Context#REFERRAL "
        + "is implicitly or explicitly set to \"ignore\", when set to \"throw\" " + "and when set to \"follow\"."),

    /** The ACI Authorization Interceptor */
    ACI_AUTHORIZATION("ACI Authorization", "An ACI based authorization interceptor."),

    /** The Default Authorization Interceptor */
    DEFAULT_AUTHORIZATION("Default Authorization", "An interceptor that controls access to PartitionNexus. If a user "
        + "tries to perform any operations that requires permission he or she "
        + "doesn't have, NoPermissionException will be thrown and therefore the "
        + "current invocation chain will terminate."),

    /** The Exception Interceptor */
    EXCEPTION("Exception", "An interceptor that detects any operations that breaks integrity of "
        + "Partition and terminates the current invocation chain by throwing a "
        + "NamingException. Those operations include when an entry already "
        + "exists at a DN and is added once again to the same DN."),

    /** The Operational Attribute Interceptor */
    OPERATIONAL_ATTRIBUTE("Operational Attribute", "An interceptor that adds or modifies the default attributes of "
        + "entries. There are four default attributes for now; 'creatorsName', "
        + "'createTimestamp', 'modifiersName', 'modifyTimestamp'."),

    /** The Schema Interceptor */
    SCHEMA("Schema", "An interceptor that manages and enforces schemas."),

    /** The Sub-Entry Interceptor */
    SUBENTRY("Sub-Entry", "The sub-entry interceptor service which is responsible for filtering "
        + "out sub-entries on search operations and injecting operational attributes"),

    /** The Collective Attribute Interceptor */
    COLLECTIVE_ATTRIBUTE("Collective Attribute", "An interceptor based service dealing with collective attribute "
        + "management. This service intercepts read operations on entries to "
        + "inject collective attribute value pairs into the response based on "
        + "the entires inclusion within collectiveAttributeSpecificAreas and collectiveAttributeInnerAreas."),

    /** The Event Interceptor */
    EVENT("Event", "An interceptor based serivice for notifying NamingListeners of "
        + "EventContext and EventDirContext changes."),

    /** The Trigger Interceptor */
    TRIGGER("Trigger", "The trigger interceptor based on the Trigger Specification."),

    /** The Replication Interceptor */
    REPLICATION("Replication", "An interceptor that intercepts LDAP operations and propagates the "
        + "changes occurred by the operations into other ReplicaIds so the DIT "
        + "of each ReplicaId in the cluster has the same content without any conflict.",
        new ReplicationInterceptorConfiguration());

    /** The name */
    private String name;

    /** The description */
    private String description;

    /** The interceptor configuration */
    private InterceptorConfiguration configuration;


    /**
     * Creates a new instance of InterceptorEnum.
     *
     * @param name
     *      the name
     * @param description
     *      the description
     */
    private InterceptorEnum( String name, String description )
    {
        this.name = name;
        this.description = description;
    }


    /**
     * Creates a new instance of InterceptorEnum.
     *
     * @param name
     *      the name
     * @param description
     *      the description
     */
    private InterceptorEnum( String name, String description, InterceptorConfiguration configuration )
    {
        this.name = name;
        this.description = description;
        this.configuration = configuration;
    }


    /**
     * Gets the name.
     *
     * @return
     *      the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the name.
     *
     * @param name
     *      the name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Gets the description.
     *
     * @return
     *      the description
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Sets the description.
     *
     * @param description
     *      the description
     */
    public void setDescription( String description )
    {
        this.description = description;
    }


    /**
     * Gets the configuration.
     *
     * @return
     *      the configuration
     */
    public InterceptorConfiguration getConfiguration()
    {
        return configuration;
    }


    /**
     * Sets the configuration
     *
     * @param configuration
     *      the configuration
     */
    public void setConfiguration( InterceptorConfiguration configuration )
    {
        this.configuration = configuration;
    }
}
