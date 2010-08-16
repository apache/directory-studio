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
package org.apache.directory.studio.apacheds.configuration.model.v156;


/**
 * This enum contains all the interceptors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum InterceptorEnum
{
    /** The Normalization Interceptor */
    NORMALIZATION(
        Messages.getString( "InterceptorEnum.Normalization" ), Messages.getString( "InterceptorEnum.NormalizationDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Authentication Interceptor */
    AUTHENTICATION(
        Messages.getString( "InterceptorEnum.Authentication" ), Messages.getString( "InterceptorEnum.AuthenticationDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Authentication Interceptor */
    REFERRAL(
        Messages.getString( "InterceptorEnum.Referral" ), Messages.getString( "InterceptorEnum.ReferralDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The ACI Authorization Interceptor */
    ACI_AUTHORIZATION(
        Messages.getString( "InterceptorEnum.ACIAuthorization" ), Messages.getString( "InterceptorEnum.ACIAuthorizationDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Default Authorization Interceptor */
    DEFAULT_AUTHORIZATION(
        Messages.getString( "InterceptorEnum.DefaultAuthorization" ), Messages.getString( "InterceptorEnum.DefaultAuthorizationDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Exception Interceptor */
    EXCEPTION(
        Messages.getString( "InterceptorEnum.Exception" ), Messages.getString( "InterceptorEnum.ExceptionDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Operational Attribute Interceptor */
    OPERATIONAL_ATTRIBUTE(
        Messages.getString( "InterceptorEnum.OperationalAttribute" ), Messages.getString( "InterceptorEnum.OperationalAttributeDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Schema Interceptor */
    SCHEMA(Messages.getString( "InterceptorEnum.Schema" ), Messages.getString( "InterceptorEnum.SchemaDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Sub-Entry Interceptor */
    SUBENTRY(
        Messages.getString( "InterceptorEnum.SubEntry" ), Messages.getString( "InterceptorEnum.SubEntryDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Collective Attribute Interceptor */
    COLLECTIVE_ATTRIBUTE(
        Messages.getString( "InterceptorEnum.CollectiveAttribute" ), Messages.getString( "InterceptorEnum.CollectiveAttributeDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Event Interceptor */
    EVENT(Messages.getString( "InterceptorEnum.Event" ), Messages.getString( "InterceptorEnum.EventDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Trigger Interceptor */
    TRIGGER(Messages.getString( "InterceptorEnum.Trigger" ), Messages.getString( "InterceptorEnum.TriggerDescription" )), //$NON-NLS-1$ //$NON-NLS-2$

    /** The Replication Interceptor */
    REPLICATION(
        Messages.getString( "InterceptorEnum.Replication" ), Messages.getString( "InterceptorEnum.ReplicationDescription" ), //$NON-NLS-1$ //$NON-NLS-2$
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
