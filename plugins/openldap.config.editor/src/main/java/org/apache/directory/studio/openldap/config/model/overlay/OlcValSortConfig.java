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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.openldap.config.model.ConfigurationElement;
import org.apache.directory.studio.openldap.config.model.OlcOverlayConfig;


/**
 * Java bean for the 'olcRefintConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcValSortConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcValSortAttr' attribute.
     */
    @ConfigurationElement(attributeType = "olcValSortAttr", isOptional = false, version="2.4.0")
    private List<String> olcValSortAttr = new ArrayList<>();


    /**
     * Creates a new instance of OlcValSortConfig.
     */
    public OlcValSortConfig()
    {
        super();
        olcOverlay = "valsort";
    }


    /**
     * Creates a copy instance of OlcValSortConfig.
     *
     * @param o the initial object
     */
    public OlcValSortConfig( OlcValSortConfig o )
    {
        super();
        olcValSortAttr = o.olcValSortAttr;
    }


    /**
     * @param strings
     */
    public void addOlcValSortAttr( String... strings )
    {
        for ( String string : strings )
        {
            olcValSortAttr.add( string );
        }
    }


    /**
     */
    public void clearOlcValSortAttr()
    {
        olcValSortAttr.clear();
    }


    /**
     * @return
     */
    public List<String> getOlcValSortAttr()
    {
        return olcValSortAttr;
    }


    /**
     * @param olcValSortAttr
     */
    public void setOlcValSortAttr( List<String> olcValSortAttr )
    {
        this.olcValSortAttr = olcValSortAttr;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public OlcValSortConfig copy()
    {
        return new OlcValSortConfig( this );
    }
}
