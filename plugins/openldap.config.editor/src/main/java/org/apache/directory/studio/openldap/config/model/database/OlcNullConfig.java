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

import org.apache.directory.studio.openldap.config.model.ConfigurationElement;
import org.apache.directory.studio.openldap.config.model.OlcDatabaseConfig;


/**
 * Java bean for the 'olcNullConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcNullConfig extends OlcDatabaseConfig
{
    /**
     * Field for the 'olcDbBindAllowed' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbBindAllowed")
    private Boolean olcDbBindAllowed;


    /**
     * @return the olcDbBindAllowed
     */
    public Boolean getOlcDbBindAllowed()
    {
        return olcDbBindAllowed;
    }


    /**
     * @param olcDbBindAllowed the olcDbBindAllowed to set
     */
    public void setOlcDbBindAllowed( Boolean olcDbBindAllowed )
    {
        this.olcDbBindAllowed = olcDbBindAllowed;
    }


    /**
     * {@inheritDoc}
     */
    public String getOlcDatabaseType()
    {
        return "null";
    };
}
