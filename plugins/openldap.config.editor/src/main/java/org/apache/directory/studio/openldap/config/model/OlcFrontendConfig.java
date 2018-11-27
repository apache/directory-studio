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
 * Java bean for the 'OlcFrontendConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcFrontendConfig implements  AuxiliaryObjectClass
{
    /**
     * Field for the 'olcDefaultSearchBase' attribute.
     */
    @ConfigurationElement(attributeType = "olcDefaultSearchBase", version="2.4.0")
    private String olcDefaultSearchBase;

    /**
     * Field for the 'olcPasswordHash' attribute.
     */
    @ConfigurationElement(attributeType = "olcPasswordHash", version="2.4.0")
    private List<String> olcPasswordHash = new ArrayList<>();

    /**
     * Field for the 'olcSortVals' attribute.
     */
    @ConfigurationElement(attributeType = "olcSortVals", version="2.4.6")
    private List<String> olcSortVals = new ArrayList<>();


    /**
     * @param strings
     */
    public void addOlcPasswordHash( String... strings )
    {
        for ( String string : strings )
        {
            olcPasswordHash.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcSortVals( String... strings )
    {
        for ( String string : strings )
        {
            olcSortVals.add( string );
        }
    }


    /**
     * @param strings
     */
    public void clearOlcPasswordHash()
    {
        olcPasswordHash.clear();
    }


    public void clearOlcSortVals()
    {
        olcSortVals.clear();
    }


    /**
     * @return the olcDefaultSearchBase
     */
    public String getOlcDefaultSearchBase()
    {
        return olcDefaultSearchBase;
    }


    /**
     * @return the olcPasswordHash
     */
    public List<String> getOlcPasswordHash()
    {
        return olcPasswordHash;
    }


    /**
     * @return the olcSortVals
     */
    public List<String> getOlcSortVals()
    {
        return olcSortVals;
    }


    /**
     * @param olcDefaultSearchBase the olcDefaultSearchBase to set
     */
    public void setOlcDefaultSearchBase( String olcDefaultSearchBase )
    {
        this.olcDefaultSearchBase = olcDefaultSearchBase;
    }


    /**
     * @param olcPasswordHash the setOlcPasswordHash to set
     */
    public void setOlcPasswordHash( List<String> olcPasswordHash )
    {
        this.olcPasswordHash = olcPasswordHash;
    }


    /**
     * @param olcSortVals the olcSortVals to set
     */
    public void setOlcSortVals( List<String> olcSortVals )
    {
        this.olcSortVals = olcSortVals;
    }
}
