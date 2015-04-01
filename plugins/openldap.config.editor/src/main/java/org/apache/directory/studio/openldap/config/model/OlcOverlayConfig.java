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
 * Java bean for the 'olcOverlayConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcOverlayConfig extends OlcConfig
{
    /**
     * Field for the 'olcOverlay' attribute.
     */
    @ConfigurationElement(attributeType = "olcOverlay", isOptional = false, isRdn = true)
    protected String olcOverlay;


    /**
     * Creates a new instance of OlcOverlayConfig.
     */
    public OlcOverlayConfig()
    {
    }


    /**
     * Creates a copy instance of OlcOverlayConfig.
     *
     * @param o the initial object
     */
    public OlcOverlayConfig( OlcOverlayConfig o )
    {
        olcOverlay = o.olcOverlay;
    }


    /**
     * @return the olcOverlay
     */
    public String getOlcOverlay()
    {
        return olcOverlay;
    }


    /**
     * @param olcOverlay the olcOverlay to set
     */
    public void setOlcOverlay( String olcOverlay )
    {
        this.olcOverlay = olcOverlay;
    }


    /**
     * Gets a copy of this object.
     *
     * @return a copy of this object
     */
    public OlcOverlayConfig copy()
    {
        return new OlcOverlayConfig( this );
    }
}
