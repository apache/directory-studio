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
package org.apache.directory.studio.connection.core.io.jndi;


import javax.naming.directory.SearchResult;

import org.apache.directory.studio.connection.core.Connection;


/**
 * Extension of {@link SearchResult} that holds a reference to the 
 * underlying connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StudioSearchResult extends SearchResult
{

    private static final long serialVersionUID = 1L;
    
    
    /** The connection. */
    private Connection connection;
    
    
    /** The is referral flag. */
    private boolean isReferral;


    /**
     * Creates a new instance of StudioSearchResult.
     * 
     * @param searchResult the original search result
     * @param connection the connection
     * @param isReferral the is referral flag
     */
    public StudioSearchResult( SearchResult searchResult, Connection connection, boolean isReferral )
    {
        super( searchResult.getName(), searchResult.getClassName(), searchResult.getObject(), searchResult.getAttributes(), searchResult.isRelative() );
        super.setNameInNamespace( searchResult.getNameInNamespace() );
        this.connection = connection;
        this.isReferral = isReferral;
    }


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    public Connection getConnection()
    {
        return connection;
    }


    /**
     * Sets the connection.
     * 
     * @param connection the new connection
     */
    public void setConnection( Connection connection )
    {
        this.connection = connection;
    }


    /**
     * Checks if is referral.
     * 
     * @return true, if is referral
     */
    public boolean isReferral()
    {
        return isReferral;
    }


    /**
     * Sets the referral flag.
     * 
     * @param isReferral the new referral flag
     */
    public void setReferral( boolean isReferral )
    {
        this.isReferral = isReferral;
    }

}
