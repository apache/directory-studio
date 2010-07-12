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

package org.apache.directory.studio.ldapservers.model;


import org.eclipse.jface.resource.ImageDescriptor;


/**
 * The {@link LdapServerAdapterExtension} class represents an extension to the 
 * LDAP Server Adapters extension point.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapServerAdapterExtension
{
    private String id;
    private String name;
    private String version;
    private String vendor;
    private String className;
    private String description;
    private ImageDescriptor icon;


    /**
     * TODO getClassName.
     *
     * @return
     */
    public String getClassName()
    {
        return className;
    }


    /**
     * TODO getDescription.
     *
     * @return
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * TODO getIcon.
     *
     * @return
     */
    public ImageDescriptor getIcon()
    {
        return icon;
    }


    /**
     * TODO getId.
     *
     * @return
     */
    public String getId()
    {
        return id;
    }


    /**
     * TODO getName.
     *
     * @return
     */
    public String getName()
    {
        return name;
    }


    /**
     * TODO vendor.
     *
     * @return
     */
    public String getVendor()
    {
        return vendor;
    }


    /**
     * TODO getVersion.
     *
     * @return
     */
    public String getVersion()
    {
        return version;
    }


    /**
     * TODO setClassName.
     *
     * @param className
     */
    public void setClassName( String className )
    {
        this.className = className;
    }


    /**
     * TODO setDescription.
     *
     * @param description
     */
    public void setDescription( String description )
    {
        this.description = description;
    }


    /**
     * TODO setIcon.
     *
     * @param icon
     */
    public void setIcon( ImageDescriptor icon )
    {
        this.icon = icon;
    }


    /**
     * TODO setId.
     *
     * @param id
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * TODO setName.
     *
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * TODO setVendor.
     *
     * @param vendor
     */
    public void setVendor( String vendor )
    {
        this.vendor = vendor;
    }


    /**
     * TODO setVersion.
     *
     * @param version
     */
    public void setVersion( String version )
    {
        this.version = version;
    }
}
