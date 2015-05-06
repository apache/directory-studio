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
package org.apache.directory.studio.openldap.config.editor.pages;

/**
 * A wrapper for a ServerID which can be either an integer between 0 and 4095 
 * (or from 0x0 to 0xFFF), and may be followed by an URL. We can't have both format,
 * and if it's not an URL, then only one value is accepted
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerIdWrapper
{
    /** The server ID */
    int serverId;
    
    /** The URL, if any */
    String url;

    /**
     * Creates a new instance of ServerIdWrapper.
     *
     * @param serverId the serverID
     */
    public ServerIdWrapper( int serverId )
    {
        this.serverId = serverId;
    }

    
    /**
     * Creates a new instance of ServerIdWrapper.
     *
     * @param serverId the serverID
     * @param url the URL
     */
    public ServerIdWrapper( int serverId, String url )
    {
        this.serverId = serverId;
        this.url = url;
    }

    
    /**
     * Creates a new instance of ServerIdWrapper.
     *
     * @param serverIdStr the serverID
     */
    public ServerIdWrapper( String serverIdStr )
    {
        this.serverId = Integer.parseInt( serverIdStr );
    }

    
    /**
     * @return the serverId
     */
    public int getServerId()
    {
        return serverId;
    }

    /**
     * @param serverId the serverId to set
     */
    public void setServerId( int serverId )
    {
        this.serverId = serverId;
    }
    
    
    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl( String url )
    {
        this.url = url;
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        if ( url == null )
        {
            return Integer.toString( serverId );
        }
        else
        {
            return Integer.toString( serverId ) + " " + url;
        }
    }
}
