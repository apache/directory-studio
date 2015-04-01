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
package org.apache.directory.studio.openldap.config.editor;


import org.apache.directory.studio.connection.core.Connection;
import org.eclipse.osgi.util.NLS;


/**
 * This class represents the Non Existing Server Configuration Input.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionServerConfigurationInput extends AbstractServerConfigurationInput
{
    /** The connection */
    private Connection connection;


    /**
     * Creates a new instance of ConnectionServerConfigurationInput.
     *
     * @param connection
     *      the connection
     */
    public ConnectionServerConfigurationInput( Connection connection )
    {
        this.connection = connection;
    }


    /**
     * Gets the connection.
     *
     * @return
     *      the connection
     */
    public Connection getConnection()
    {
        return connection;
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        return NLS.bind( "{0} - Configuration", connection.getName() );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return NLS.bind( "{0} - Configuration", connection.getName() );
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return connection != null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }

        if ( obj instanceof ConnectionServerConfigurationInput )
        {
            ConnectionServerConfigurationInput input = ( ConnectionServerConfigurationInput ) obj;
            if ( input.exists() && exists() )
            {
                Connection inputConnection = input.getConnection();

                if ( ( inputConnection != null ) && ( inputConnection != null ) )
                {
                    return inputConnection.equals( connection );
                }
            }
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return connection.hashCode();
    }
}
