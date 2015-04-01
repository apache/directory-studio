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


/**
 * Java bean for the 'olcSyncProvConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcSyncProvConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcSpCheckpoint' attribute.
     */
    @ConfigurationElement(attributeType = "olcSpCheckpoint")
    private String olcSpCheckpoint;

    /**
     * Field for the 'olcSpNoPresent' attribute.
     */
    @ConfigurationElement(attributeType = "olcSpNoPresent", defaultValue = "FALSE")
    private Boolean olcSpNoPresent = false;

    /**
     * Field for the 'olcSpReloadHint' attribute.
     */
    @ConfigurationElement(attributeType = "olcSpReloadHint", defaultValue = "FALSE")
    private Boolean olcSpReloadHint = false;

    /**
     * Field for the 'olcSpSessionlog' attribute.
     */
    @ConfigurationElement(attributeType = "olcSpSessionlog")
    private Integer olcSpSessionlog;


    /**
     * Creates a new instance of OlcSyncProvConfig.
     */
    public OlcSyncProvConfig()
    {
        super();
        olcOverlay = "syncprov";
    }


    /**
     * Creates a copy instance of OlcSyncProvConfig.
     *
     * @param o the initial object
     */
    public OlcSyncProvConfig( OlcSyncProvConfig o )
    {
        super();
        olcSpCheckpoint = o.olcSpCheckpoint;
        olcSpNoPresent = o.olcSpNoPresent;
        olcSpReloadHint = o.olcSpReloadHint;
        olcSpSessionlog = o.olcSpSessionlog;
    }


    /**
     * @return the olcSpCheckpoint
     */
    public String getOlcSpCheckpoint()
    {
        return olcSpCheckpoint;
    }


    /**
     * @return the olcSpNoPresent
     */
    public Boolean getOlcSpNoPresent()
    {
        return olcSpNoPresent;
    }


    /**
     * @return the olcSpReloadHint
     */
    public Boolean getOlcSpReloadHint()
    {
        return olcSpReloadHint;
    }


    /**
     * @return the olcSpSessionlog
     */
    public Integer getOlcSpSessionlog()
    {
        return olcSpSessionlog;
    }


    /**
     * @param olcSpCheckpoint the olcSpCheckpoint to set
     */
    public void setOlcSpCheckpoint( String olcSpCheckpoint )
    {
        this.olcSpCheckpoint = olcSpCheckpoint;
    }


    /**
     * @param olcSpNoPresent the olcSpNoPresent to set
     */
    public void setOlcSpNoPresent( Boolean olcSpNoPresent )
    {
        this.olcSpNoPresent = olcSpNoPresent;
    }


    /**
     * @param olcSpReloadHint the olcSpReloadHint to set
     */
    public void setOlcSpReloadHint( Boolean olcSpReloadHint )
    {
        this.olcSpReloadHint = olcSpReloadHint;
    }


    /**
     * @param olcSpSessionlog the olcSpSessionlog to set
     */
    public void setOlcSpSessionlog( Integer olcSpSessionlog )
    {
        this.olcSpSessionlog = olcSpSessionlog;
    }


    /**
     * {@inheritDoc}
     */
    public OlcSyncProvConfig copy()
    {
        return new OlcSyncProvConfig( this );
    }
}
