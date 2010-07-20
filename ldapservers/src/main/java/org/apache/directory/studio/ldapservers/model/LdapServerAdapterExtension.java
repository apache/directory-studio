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
    /** The ID */
    private String id;

    /** The name*/
    private String name;

    /** The version */
    private String version;

    /** The vendor */
    private String vendor;

    /** The class name */
    private String className;

    /** The {@link LdapServerAdapter} instance */
    private LdapServerAdapter instance;

    /** The description */
    private String description;

    /** The icon */
    private ImageDescriptor icon;


    /**
     * Gets the class name.
     *
     * @return
     *      the class name
     */
    public String getClassName()
    {
        return className;
    }


    /**
     * Gets the description.
     *
     * @return
     *      the description
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Gets the icon.
     *
     * @return
     *      the icon
     */
    public ImageDescriptor getIcon()
    {
        return icon;
    }


    /**
     * Gets the ID.
     *
     * @return
     *      the ID
     */
    public String getId()
    {
        return id;
    }


    /**
     * Gets the {@link LdapServerAdapter} instance.
     *
     * @return
     *      the {@link LdapServerAdapter} instance
     */
    public LdapServerAdapter getInstance()
    {
        return instance;
    }


    /**
     * Gets the name.
     *
     * @return
     *      the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Gets the vendor.
     *
     * @return
     *      the vendor
     */
    public String getVendor()
    {
        return vendor;
    }


    /**
     * Gets the version.
     *
     * @return
     *      the version
     */
    public String getVersion()
    {
        return version;
    }


    /**
     * Sets the class name.
     *
     * @param className
     *      the class name
     */
    public void setClassName( String className )
    {
        this.className = className;
    }


    /**
     * Sets the description.
     *
     * @param description
     *      the description
     */
    public void setDescription( String description )
    {
        this.description = description;
    }


    /**
     * Sets the icon.
     *
     * @param icon
     *      the icon
     */
    public void setIcon( ImageDescriptor icon )
    {
        this.icon = icon;
    }


    /**
     * Sets the ID.
     *
     * @param id
     *      the ID
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * Sets the {@link LdapServerAdapter} instance.
     *
     * @param instance
     *      the {@link LdapServerAdapter} instance
     */
    public void setInstance( LdapServerAdapter instance )
    {
        this.instance = instance;
    }


    /**
     * Sets the name.
     *
     * @param name
     *      the name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Sets the vendor.
     *
     * @param vendor
     *      the vendor
     */
    public void setVendor( String vendor )
    {
        this.vendor = vendor;
    }


    /**
     * Sets the version.
     *
     * @param version
     *      the version
     */
    public void setVersion( String version )
    {
        this.version = version;
    }
}
