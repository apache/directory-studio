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

package org.apache.directory.studio.connection.core;


/**
 * This class holds information about a directory server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DirectoryServerInfo
{
    /**
     * This enum contains all detectable directory server types.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public static enum DirectoryServerType
    {
        APACHEDS,
        IBM_DIRECTORY_SERVER,
        IBM_SECUREWAY_DIRECTORY,
        IBM_TIVOLI_DIRECTORY_SERVER,
        MICROSOFT_ACTIVE_DIRECTORY_2000,
        MICROSOFT_ACTIVE_DIRECTORY_2003,
        NETSCAPE,
        NOVELL,
        OPENLDAP,
        OPENDS,
        OPENDJ,
        SIEMENS_DIRX,
        SUN_DIRECTORY_SERVER,
        UNKNOWN
    }

    /** The type */
    private DirectoryServerType type;

    /** The version */
    private String version;


    /**
     * Gets the type.
     *
     * @return
     *      the type
     */
    public DirectoryServerType getType()
    {
        return type;
    }


    /**
     * Sets the type.
     *
     * @param type
     *      the type
     */
    public void setType( DirectoryServerType type )
    {
        this.type = type;
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
