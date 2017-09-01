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
package org.apache.directory.studio.openldap.config.model.overlay;


import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.openldap.config.model.ConfigurationElement;
import org.apache.directory.studio.openldap.config.model.OlcOverlayConfig;


/**
 * Java bean for the 'olcPPolicyConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcPPolicyConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcPPolicyDefault' attribute.
     */
    @ConfigurationElement(attributeType = "olcPPolicyDefault", version="2.4.0")
    private Dn olcPPolicyDefault;

    /**
     * Field for the 'olcPPolicyForwardUpdates' attribute.
     */
    @ConfigurationElement(attributeType = "olcPPolicyForwardUpdates", version="2.4.17")
    private Boolean olcPPolicyForwardUpdates;

    /**
     * Field for the 'olcPPolicyHashCleartext' attribute.
     */
    @ConfigurationElement(attributeType = "olcPPolicyHashCleartext", version="2.4.0")
    private Boolean olcPPolicyHashCleartext;

    /**
     * Field for the 'olcPPolicyUseLockout' attribute.
     */
    @ConfigurationElement(attributeType = "olcPPolicyUseLockout", version="2.4.0")
    private Boolean olcPPolicyUseLockout;


    /**
     * Creates a new instance of OlcPPolicyConfig.
     */
    public OlcPPolicyConfig()
    {
        super();
        olcOverlay = "ppolicy";
    }


    /**
     * Creates a copy instance of OlcPPolicyConfig.
     *
     * @param o the initial object
     */
    public OlcPPolicyConfig( OlcPPolicyConfig o )
    {
        super( o );
        olcPPolicyDefault = o.olcPPolicyDefault;
        olcPPolicyForwardUpdates = o.olcPPolicyForwardUpdates;
        olcPPolicyHashCleartext = o.olcPPolicyHashCleartext;
        olcPPolicyUseLockout = o.olcPPolicyUseLockout;
    }


    /**
     * @return the olcPPolicyDefault
     */
    public Dn getOlcPPolicyDefault()
    {
        return olcPPolicyDefault;
    }


    /**
     * @return the olcPPolicyForwardUpdates
     */
    public Boolean getOlcPPolicyForwardUpdates()
    {
        return olcPPolicyForwardUpdates;
    }


    /**
     * @return the olcPPolicyHashCleartext
     */
    public Boolean getOlcPPolicyHashCleartext()
    {
        return olcPPolicyHashCleartext;
    }


    /**
     * @return the olcPPolicyUseLockout
     */
    public Boolean getOlcPPolicyUseLockout()
    {
        return olcPPolicyUseLockout;
    }


    /**
     * @param olcPPolicyDefault the olcPPolicyDefault to set
     */
    public void setOlcPPolicyDefault( Dn olcPPolicyDefault )
    {
        this.olcPPolicyDefault = olcPPolicyDefault;
    }


    /**
     * @param olcPPolicyForwardUpdates the olcPPolicyForwardUpdates to set
     */
    public void setOlcPPolicyForwardUpdates( Boolean olcPPolicyForwardUpdates )
    {
        this.olcPPolicyForwardUpdates = olcPPolicyForwardUpdates;
    }


    /**
     * @param olcPPolicyHashCleartext the olcPPolicyHashCleartext to set
     */
    public void setOlcPPolicyHashCleartext( Boolean olcPPolicyHashCleartext )
    {
        this.olcPPolicyHashCleartext = olcPPolicyHashCleartext;
    }


    /**
     * @param olcPPolicyUseLockout the olcPPolicyUseLockout to set
     */
    public void setOlcPPolicyUseLockout( Boolean olcPPolicyUseLockout )
    {
        this.olcPPolicyUseLockout = olcPPolicyUseLockout;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public OlcPPolicyConfig copy()
    {
        return new OlcPPolicyConfig( this );
    }
}
