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
package org.apache.directory.studio.apacheds.configuration.model.v152;


/**
 * This enum contains all the interceptors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public enum InterceptorEnum
{
    /** The Normalization Interceptor */
    NORMALIZATION("Normalization", "Description"),

    /** The Authentication Interceptor */
    AUTHENTICATION("Authentication", "Description"),

    /** The Referral Interceptor */
    REFERRAL("Referral", "Description"),

    /** The ACI Authorization Interceptor */
    ACI_AUTHORIZATION("ACI Authorization", "Description"),

    /** The Default Authorization Interceptor */
    DEFAULT_AUTHORIZATION("Default Authorization", "Description"),

    /** The Exception Interceptor */
    EXCEPTION("Exception", "Description"),

    /** The Operational Attribute Interceptor */
    OPERATIONAL_ATTRIBUTE("Operational Attribute", "Description"),

    /** The Schema Interceptor */
    SCHEMA("Schema", "Description"),

    /** The Sub-Entry Interceptor */
    SUBENTRY("Sub-Entry", "Description"),

    /** The Collective Attribute Interceptor */
    COLLECTIVE_ATTRIBUTE("Collective Attribute", "Description"),

    /** The Event Interceptor */
    EVENT("Event", "Description"),

    /** The Trigger Interceptor */
    TRIGGER("Trigger", "Description"),

    /** The Replication Interceptor */
    REPLICATION("Replication", "Description", new ReplicationInterceptorConfiguration());

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
