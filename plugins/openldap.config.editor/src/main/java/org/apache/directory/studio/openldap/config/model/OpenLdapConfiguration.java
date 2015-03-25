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
 * This class implements the basic class for an OpenLDAP configuration.
 * <p>
 * It contains all the configuration objects found under the "cn=config" branch. 
 */
public class OpenLdapConfiguration
{
    private List<OlcConfig> configurationElements = new ArrayList<OlcConfig>();


    /**
     * @return the configurationElements
     */
    public List<OlcConfig> getConfigurationElements()
    {
        return configurationElements;
    }


    /**
     * @param e
     * @return
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean add( OlcConfig o )
    {
        return configurationElements.add( o );
    }


    /**
     * @param o
     * @return
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains( OlcConfig o )
    {
        return configurationElements.contains( o );
    }


    /**
     * @param o
     * @return
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove( OlcConfig o )
    {
        return configurationElements.remove( o );
    }


    /**
     * @return
     * @see java.util.List#size()
     */
    public int size()
    {
        return configurationElements.size();
    }
}
