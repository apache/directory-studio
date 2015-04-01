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


/**
 * Java bean for the 'olcPPolicyConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcRwmConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcRwmMap' attribute.
     */
    @ConfigurationElement(attributeType = "olcRwmMap")
    private List<String> olcRwmMap = new ArrayList<String>();

    /**
     * Field for the 'olcRwmNormalizeMapped' attribute.
     */
    @ConfigurationElement(attributeType = "olcRwmNormalizeMapped")
    private Boolean olcRwmNormalizeMapped;

    /**
     * Field for the 'olcRwmRewrite' attribute.
     */
    @ConfigurationElement(attributeType = "olcRwmRewrite")
    private List<String> olcRwmRewrite = new ArrayList<String>();

    /**
     * Field for the 'olcRwmTFSupport' attribute.
     */
    @ConfigurationElement(attributeType = "olcRwmTFSupport")
    private String olcRwmTFSupport;


    /**
     * Creates a new instance of OlcPPolicyConfig.
     */
    public OlcRwmConfig()
    {
        super();
        olcOverlay = "rwm";
    }


    /**
     * Creates a copy instance of OlcPPolicyConfig.
     *
     * @param o the initial object
     */
    public OlcRwmConfig( OlcRwmConfig o )
    {
        super( o );
        olcRwmMap = o.olcRwmMap;
        olcRwmNormalizeMapped = o.olcRwmNormalizeMapped;
        olcRwmRewrite = o.olcRwmRewrite;
        olcRwmTFSupport = o.olcRwmTFSupport;
    }


    /**
     * @param strings
     */
    public void addOlcRwmMap( String... strings )
    {

        for ( String string : strings )
        {
            olcRwmMap.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcRwmRewrite( String... strings )
    {
        for ( String string : strings )
        {
            olcRwmRewrite.add( string );
        }
    }


    /**
     */
    public void clearOlcRwmMap()
    {
        olcRwmMap.clear();
    }


    /**
     */
    public void clearOlcRwmRewrite()
    {
        olcRwmRewrite.clear();
    }


    /**
     * @return
     */
    public List<String> getOlcRwmMap()
    {
        return olcRwmMap;
    }


    /**
     * @return
     */
    public Boolean getOlcRwmNormalizeMapped()
    {
        return olcRwmNormalizeMapped;
    }


    /**
     * @return
     */
    public List<String> getOlcRwmRewrite()
    {
        return olcRwmRewrite;
    }


    /**
     * @return
     */
    public String getOlcRwmTFSupport()
    {
        return olcRwmTFSupport;
    }


    /**
     * @param olcRwmMap
     */
    public void setOlcRwmMap( List<String> olcRwmMap )
    {
        this.olcRwmMap = olcRwmMap;
    }


    /**
     * @param olcRwmNormalizeMapped
     */
    public void setOlcRwmNormalizeMapped( Boolean olcRwmNormalizeMapped )
    {
        this.olcRwmNormalizeMapped = olcRwmNormalizeMapped;
    }


    /**
     * @param olcRwmRewrite
     */
    public void setOlcRwmRewrite( List<String> olcRwmRewrite )
    {
        this.olcRwmRewrite = olcRwmRewrite;
    }


    /**
     * @param olcRwmTFSupport
     */
    public void setOlcRwmTFSupport( String olcRwmTFSupport )
    {
        this.olcRwmTFSupport = olcRwmTFSupport;
    }


    /**
     * {@inheritDoc}
     */
    public OlcRwmConfig copy()
    {
        return new OlcRwmConfig( this );
    }
}
