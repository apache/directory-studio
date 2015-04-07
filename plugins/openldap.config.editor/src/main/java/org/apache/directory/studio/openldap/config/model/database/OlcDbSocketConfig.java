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
package org.apache.directory.studio.openldap.config.model.database;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.openldap.config.model.ConfigurationElement;
import org.apache.directory.studio.openldap.config.model.OlcDatabaseConfig;


/**
 * Java bean for the 'olcDbSocketConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcDbSocketConfig extends OlcDatabaseConfig
{
    /**
     * Field for the 'olcDbSocketPath' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbSocketPath", isOptional = false)
    private String olcDbSocketPath;

    /**
     * Field for the 'olcDbSocketExtensions' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbSocketExtensions")
    private List<String> olcDbSocketExtensions = new ArrayList<String>();


    /**
     * @param strings
     */
    public void addOlcDbSocketExtensions( String... strings )
    {
        for ( String string : strings )
        {
            olcDbSocketExtensions.add( string );
        }
    }


    public void clearOlcDbSocketExtensions()
    {
        olcDbSocketExtensions.clear();
    }


    /**
     * @return the olcDbSocketExtensions
     */
    public List<String> getOlcDbSocketExtensions()
    {
        return copyListString( olcDbSocketExtensions );
    }


    /**
     * @return the olcDbSocketPath
     */
    public String getOlcDbSocketPath()
    {
        return olcDbSocketPath;
    }


    /**
     * @param olcDbSocketExtensions the olcDbSocketExtensions to set
     */
    public void setOlcDbSocketExtensions( List<String> olcDbSocketExtensions )
    {
        this.olcDbSocketExtensions = copyListString( olcDbSocketExtensions );
    }


    /**
     * @param olcDbSocketPath the olcDbSocketPath to set
     */
    public void setOlcDbSocketPath( String olcDbSocketPath )
    {
        this.olcDbSocketPath = olcDbSocketPath;
    }
}
