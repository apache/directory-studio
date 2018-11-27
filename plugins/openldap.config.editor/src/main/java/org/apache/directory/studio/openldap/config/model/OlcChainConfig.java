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
 * Java bean for the 'olcChainConfig' object class.
  * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
*/
public class OlcChainConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcChainCacheURI' attribute.
     */
    @ConfigurationElement(attributeType = "olcChainCacheURI", version="2.4.0")
    private Boolean olcChainCacheURI;

    /**
     * Field for the 'olcChainingBehavior' attribute.
     */
    @ConfigurationElement(attributeType = "olcChainingBehavior", version="2.4.0")
    private String olcChainingBehavior;

    /**
     * Field for the 'olcChainMaxReferralDepth' attribute.
     */
    @ConfigurationElement(attributeType = "olcChainMaxReferralDepth", version="2.4.0")
    private Integer olcChainMaxReferralDepth;

    /**
     * Field for the 'olcChainReturnError' attribute.
     */
    @ConfigurationElement(attributeType = "olcChainReturnError", version="2.4.0")
    private Boolean olcChainReturnError;


    /**
     * Creates a new instance of OlcChainConfig.
     */
    public OlcChainConfig()
    {
        super();
    }


    /**
     * Creates a copy instance of OlcChainConfig.
     *
     * @param o the initial object
     */
    public OlcChainConfig( OlcChainConfig o )
    {
        super( o );
        olcChainCacheURI = o.olcChainCacheURI;
        olcChainingBehavior = o.olcChainingBehavior;
        olcChainMaxReferralDepth = o.olcChainMaxReferralDepth;
        olcChainReturnError = o.olcChainReturnError;
    }


    /**
     * @return the olcChainCacheURI
     */
    public Boolean getOlcChainCacheURI()
    {
        return olcChainCacheURI;
    }


    /**
     * @return the olcChainingBehavior
     */
    public String getOlcChainingBehavior()
    {
        return olcChainingBehavior;
    }


    /**
     * @return the olcChainMaxReferralDepth
     */
    public Integer getOlcChainMaxReferralDepth()
    {
        return olcChainMaxReferralDepth;
    }


    /**
     * @return the olcChainReturnError
     */
    public Boolean getOlcChainReturnError()
    {
        return olcChainReturnError;
    }


    /**
     * @param olcChainCacheURI the olcChainCacheURI to set
     */
    public void setOlcChainCacheURI( Boolean olcChainCacheURI )
    {
        this.olcChainCacheURI = olcChainCacheURI;
    }


    /**
     * @param olcChainingBehavior the olcChainingBehavior to set
     */
    public void setOlcChainingBehavior( String olcChainingBehavior )
    {
        this.olcChainingBehavior = olcChainingBehavior;
    }


    /**
     * @param olcChainMaxReferralDepth the olcChainMaxReferralDepth to set
     */
    public void setOlcChainMaxReferralDepth( Integer olcChainMaxReferralDepth )
    {
        this.olcChainMaxReferralDepth = olcChainMaxReferralDepth;
    }


    /**
     * @param olcChainReturnError the olcChainReturnError to set
     */
    public void setOlcChainReturnError( Boolean olcChainReturnError )
    {
        this.olcChainReturnError = olcChainReturnError;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public OlcChainConfig copy()
    {
        return new OlcChainConfig( this );
    }
}
