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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;


/**
 * Java bean for the 'olcRefintConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcRefintConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcRefintAttribute' attribute.
     */
    @ConfigurationElement(attributeType = "olcRefintAttribute")
    private List<String> olcRefintAttribute = new ArrayList<String>();

    /**
     * Field for the 'olcRefintNothing' attribute.
     */
    @ConfigurationElement(attributeType = "olcRefintNothing")
    private Dn olcRefintNothing;

    /**
     * Field for the 'olcRefintModifiersName' attribute.
     */
    @ConfigurationElement(attributeType = "olcRefintModifiersName")
    private Dn olcRefintModifiersName;


    /**
     * Creates a new instance of OlcRefintConfig.
     */
    public OlcRefintConfig()
    {
        super();
        olcOverlay = "refint";
    }


    /**
     * Creates a copy instance of OlcRefintConfig.
     *
     * @param o the initial object
     */
    public OlcRefintConfig( OlcRefintConfig o )
    {
        super();
        olcRefintAttribute = o.olcRefintAttribute;
        olcRefintNothing = o.olcRefintNothing;
        olcRefintModifiersName = o.olcRefintModifiersName;
    }


    /**
     * @param strings
     */
    public void addOlcRefintAttribute( String... strings )
    {
        for ( String string : strings )
        {
            olcRefintAttribute.add( string );
        }
    }


    /**
     * @return
     */
    public List<String> getOlcRefintAttribute()
    {
        return olcRefintAttribute;
    }


    /**
     * @return
     */
    public Dn getOlcRefintNothing()
    {
        return olcRefintNothing;
    }


    /**
     * @return
     */
    public Dn getOlcRefintModifiersName()
    {
        return olcRefintModifiersName;
    }


    /**
     * @param olcRefintAttribute
     */
    public void setOlcRefintAttribute( List<String> olcRefintAttribute )
    {
        this.olcRefintAttribute = olcRefintAttribute;
    }


    /**
     * @param olcRefintNothing
     */
    public void setOlcRefintNothing( Dn olcRefintNothing )
    {
        this.olcRefintNothing = olcRefintNothing;
    }


    /**
     * @param olcRefintModifiersName
     */
    public void setOlcRefintModifiersName( Dn olcRefintModifiersName )
    {
        this.olcRefintModifiersName = olcRefintModifiersName;
    }


    /**
     * {@inheritDoc}
     */
    public OlcRefintConfig copy()
    {
        return new OlcRefintConfig( this );
    }
}
