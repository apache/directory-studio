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
 * Java bean for the 'olcPBindConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcPBindConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcDbURI' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbURI", isOptional = false)
    private String olcDbURI;

    /**
     * Field for the 'olcDbNetworkTimeout' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbNetworkTimeout")
    private String olcDbNetworkTimeout;

    /**
     * Field for the 'olcDbQuarantine' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbQuarantine")
    private String olcDbQuarantine;

    /**
     * Field for the 'olcStartTLS' attribute.
     */
    @ConfigurationElement(attributeType = "olcStartTLS")
    private String olcStartTLS;


    /**
     * Creates a new instance of OlcPBindConfig.
     */
    public OlcPBindConfig()
    {
        super();
    }


    /**
     * Creates a copy instance of OlcPBindConfig.
     *
     * @param o the initial object
     */
    public OlcPBindConfig( OlcPBindConfig o )
    {
        super( o );
        olcDbURI = o.olcDbURI;
        olcDbNetworkTimeout = o.olcDbNetworkTimeout;
        olcDbQuarantine = o.olcDbQuarantine;
        olcStartTLS = o.olcStartTLS;
    }


    /**
     * @return the olcDbNetworkTimeout
     */
    public String getOlcDbNetworkTimeout()
    {
        return olcDbNetworkTimeout;
    }


    /**
     * @return the olcDbQuarantine
     */
    public String getOlcDbQuarantine()
    {
        return olcDbQuarantine;
    }


    /**
     * @return the olcDbURI
     */
    public String getOlcDbURI()
    {
        return olcDbURI;
    }


    /**
     * @return the olcStartTLS
     */
    public String getOlcStartTLS()
    {
        return olcStartTLS;
    }


    /**
     * @param olcDbNetworkTimeout the olcDbNetworkTimeout to set
     */
    public void setOlcDbNetworkTimeout( String olcDbNetworkTimeout )
    {
        this.olcDbNetworkTimeout = olcDbNetworkTimeout;
    }


    /**
     * @param olcDbQuarantine the olcDbQuarantine to set
     */
    public void setOlcDbQuarantine( String olcDbQuarantine )
    {
        this.olcDbQuarantine = olcDbQuarantine;
    }


    /**
     * @param olcDbURI the olcDbURI to set
     */
    public void setOlcDbURI( String olcDbURI )
    {
        this.olcDbURI = olcDbURI;
    }


    /**
     * @param olcStartTLS the olcStartTLS to set
     */
    public void setOlcStartTLS( String olcStartTLS )
    {
        this.olcStartTLS = olcStartTLS;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public OlcPBindConfig copy()
    {
        return new OlcPBindConfig( this );
    }
}
