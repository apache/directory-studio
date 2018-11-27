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
 * Java bean for the 'olcSyncProvConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcMemberOf extends OlcOverlayConfig
{
    /**
     * Field for the 'olcMemberOfDangling' attribute.
     */
    @ConfigurationElement(attributeType = "olcMemberOfDangling", version="2.4.0")
    private String olcMemberOfDangling;

    /**
     * Field for the 'olcMemberOfDanglingError' attribute.
     */
    @ConfigurationElement(attributeType = "olcMemberOfDanglingError", version="2.4.8")
    private String olcMemberOfDanglingError;

    /**
     * Field for the 'olcMemberOfDN' attribute.
     */
    @ConfigurationElement(attributeType = "olcMemberOfDN", version="2.4.0")
    private Dn olcMemberOfDN;

    /**
     * Field for the 'olcMemberOfGroupOC' attribute.
     */
    @ConfigurationElement(attributeType = "olcMemberOfGroupOC", version="2.4.0")
    private String olcMemberOfGroupOC;

    /**
     * Field for the 'olcMemberOfMemberAD' attribute.
     */
    @ConfigurationElement(attributeType = "olcMemberOfMemberAD", version="2.4.0")
    private String olcMemberOfMemberAD;

    /**
     * Field for the 'olcMemberOfMemberOfAD' attribute.
     */
    @ConfigurationElement(attributeType = "olcMemberOfMemberOfAD", version="2.4.0")
    private String olcMemberOfMemberOfAD;

    /**
     * Field for the 'olcMemberOfRefInt' attribute.
     */
    @ConfigurationElement(attributeType = "olcMemberOfRefInt", version="2.4.0")
    private Boolean olcMemberOfRefInt;


    /**
     * Creates a new instance of OlcMemberOf.
     */
    public OlcMemberOf()
    {
        super();
        olcOverlay = "memberof";
    }


    /**
     * Creates a copy instance of OlcMemberOf.
     *
     * @param o the initial object
     */
    public OlcMemberOf( OlcMemberOf o )
    {
        super();
        olcMemberOfDangling = o.olcMemberOfDangling;
        olcMemberOfDanglingError = o.olcMemberOfDanglingError;
        olcMemberOfDN = o.olcMemberOfDN;
        olcMemberOfGroupOC = o.olcMemberOfGroupOC;
        olcMemberOfMemberAD = o.olcMemberOfMemberAD;
        olcMemberOfMemberOfAD = o.olcMemberOfMemberOfAD;
        olcMemberOfRefInt = o.olcMemberOfRefInt;
    }


    /**
     * @return
     */
    public String getOlcMemberOfDangling()
    {
        return olcMemberOfDangling;
    }


    /**
     * @return
     */
    public String getOlcMemberOfDanglingError()
    {
        return olcMemberOfDanglingError;
    }


    /**
     * @return
     */
    public Dn getOlcMemberOfDN()
    {
        return olcMemberOfDN;
    }


    /**
     * @return
     */
    public String getOlcMemberOfGroupOC()
    {
        return olcMemberOfGroupOC;
    }


    /**
     * @return
     */
    public String getOlcMemberOfMemberAD()
    {
        return olcMemberOfMemberAD;
    }


    /**
     * @return
     */
    public String getOlcMemberOfMemberOfAD()
    {
        return olcMemberOfMemberOfAD;
    }


    /**
     * @return
     */
    public Boolean getOlcMemberOfRefInt()
    {
        return olcMemberOfRefInt;
    }


    /**
     * @param olcMemberOfDangling
     */
    public void setOlcMemberOfDangling( String olcMemberOfDangling )
    {
        this.olcMemberOfDangling = olcMemberOfDangling;
    }


    /**
     * @param olcMemberOfDanglingError
     */
    public void setOlcMemberOfDanglingError( String olcMemberOfDanglingError )
    {
        this.olcMemberOfDanglingError = olcMemberOfDanglingError;
    }


    /**
     * @param olcMemberOfDN
     */
    public void setOlcMemberOfDN( Dn olcMemberOfDN )
    {
        this.olcMemberOfDN = olcMemberOfDN;
    }


    /**
     * @param olcMemberOfGroupOC
     */
    public void setOlcMemberOfGroupOC( String olcMemberOfGroupOC )
    {
        this.olcMemberOfGroupOC = olcMemberOfGroupOC;
    }


    /**
     * @param olcMemberOfMemberAD
     */
    public void setOlcMemberOfMemberAD( String olcMemberOfMemberAD )
    {
        this.olcMemberOfMemberAD = olcMemberOfMemberAD;
    }


    /**
     * @param olcMemberOfMemberOfAD
     */
    public void setOlcMemberOfMemberOfAD( String olcMemberOfMemberOfAD )
    {
        this.olcMemberOfMemberOfAD = olcMemberOfMemberOfAD;
    }


    /**
     * @param olcMemberOfRefInt
     */
    public void setOlcMemberOfRefInt( Boolean olcMemberOfRefInt )
    {
        this.olcMemberOfRefInt = olcMemberOfRefInt;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public OlcMemberOf copy()
    {
        return new OlcMemberOf( this );
    }
}
