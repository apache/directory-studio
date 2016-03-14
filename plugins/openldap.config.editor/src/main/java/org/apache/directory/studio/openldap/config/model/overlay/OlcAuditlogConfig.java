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
 * Java bean for the 'olcAuditlogConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcAuditlogConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcAuditlogFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcAuditlogFile")
    private List<String> olcAuditlogFile = new ArrayList<String>();


    /**
     * Creates a new instance of OlcAuditlogConfig.
     */
    public OlcAuditlogConfig()
    {
        super();
        olcOverlay = "auditlog";
    }


    /**
     * Creates a copy instance of OlcAuditlogConfig.
     *
     * @param o the initial object
     */
    public OlcAuditlogConfig( OlcAuditlogConfig o )
    {
        super( o );
        olcAuditlogFile = new ArrayList<String>( olcAuditlogFile );
    }


    /**
     * @param strings
     */
    public void addOlcAuditlogFile( String... strings )
    {
        for ( String string : strings )
        {
            olcAuditlogFile.add( string );
        }
    }


    public void clearOlcAuditlogFile()
    {
        olcAuditlogFile.clear();
    }


    /**
     * @return the olcAuditlogFile
     */
    public List<String> getOlcAuditlogFile()
    {
        return olcAuditlogFile;
    }


    /**
     * @param olcAuditlogFile the olcAuditlogFile to set
     */
    public void setOlcAuditlogFile( List<String> olcAuditlogFile )
    {
        this.olcAuditlogFile = olcAuditlogFile;
    }


    /**
     * {@inheritDoc}
     */
    public OlcAuditlogConfig copy()
    {
        return new OlcAuditlogConfig( this );
    }
}
