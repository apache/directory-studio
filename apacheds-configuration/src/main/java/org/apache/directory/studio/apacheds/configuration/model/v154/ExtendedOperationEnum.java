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
package org.apache.directory.studio.apacheds.configuration.model.v154;


/**
 * This enum contains all the extended operations.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public enum ExtendedOperationEnum
{
    /** The Start TLS Extended Operation */
    START_TLS("Start TLS", "The Start TLS extended operation allows an application to serialize secure and "
        + "plain requests against an LDAP server on a single connection."),

    /** The Graceful Shutdown Extended Operation */
    GRACEFUL_SHUTDOWN("Graceful Shutdown", "The Graceful Shutdown TLS extended operation allows an application to "
        + "gracefully manage server's that must go offline or shutdown with proper notification to bound clients."),

    /** The Launch Diagnostic UI Extended Operation */
    LAUNCH_DIAGNOSTIC_UI("Launch Diagnostic UI",
        "The Launch Diagnostic UI extended operation allows an application to "
            + "launch the diagnostic user interface which can be used to look at "
            + "the master table and the indices.");

    /** The name */
    private String name;

    /** The description */
    private String description;


    /**
     * Creates a new instance of InterceptorEnum.
     *
     * @param name
     *      the name
     * @param description
     *      the description
     */
    private ExtendedOperationEnum( String name, String description )
    {
        this.name = name;
        this.description = description;
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
}
