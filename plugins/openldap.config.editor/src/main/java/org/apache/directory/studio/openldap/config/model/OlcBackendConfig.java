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
 * Java bean for the 'olcBackendConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcBackendConfig extends OlcConfig
{
    /**
     * Field for the 'olcBackend' attribute.
     */
    @ConfigurationElement(attributeType = "olcBackend", isOptional = false)
    private String olcBackend;


    /**
     * Creates a new instance of olcBackendConfig.
     */
    public OlcBackendConfig()
    {
    }


    /**
     * Creates a copy instance of olcBackendConfig.
     *
     * @param o the initial object
     */
    public OlcBackendConfig( OlcBackendConfig o )
    {
        olcBackend = o.olcBackend;
    }


    /**
     * @return the olcBackend
     */
    public String getOlcBackend()
    {
        return olcBackend;
    }


    /**
     * @param olcBackend the olcBackend to set
     */
    public void setOlcBackend( String olcBackend )
    {
        this.olcBackend = olcBackend;
    }


    /**
     * Gets a copy of this object.
     *
     * @return a copy of this object
     */
    public OlcBackendConfig copy()
    {
        return new OlcBackendConfig( this );
    }
}
