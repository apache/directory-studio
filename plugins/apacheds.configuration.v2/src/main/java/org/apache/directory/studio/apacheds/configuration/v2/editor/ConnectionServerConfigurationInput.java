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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import org.apache.directory.studio.apacheds.configuration.v2.jobs.EntryBasedConfigurationPartition;
import org.apache.directory.studio.connection.core.Connection;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class represents the Non Existing Server Configuration Input.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionServerConfigurationInput implements IEditorInput
{
    /** The connection */
    private Connection connection;

    /** The original configuration partition */
    private EntryBasedConfigurationPartition originalPartition;


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
     * Gets the original configuration partition.
     *
     * @return
     *      the original configuration partition
     */
    public EntryBasedConfigurationPartition getOriginalPartition()
    {
        return originalPartition;
    }


    /**
     * Sets the original configuration partition.
     *
     * @param originalPartition
     *      the original configuration 
     */
    public void setOriginalPartition( EntryBasedConfigurationPartition originalPartition )
    {
        this.originalPartition = originalPartition;
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        return NLS.bind( Messages.getString("ConnectionServerConfigurationInput.ConnectionConfiguration"), connection.getName() ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return NLS.bind( Messages.getString("ConnectionServerConfigurationInput.ConnectionConfiguration"), connection.getName() ); //$NON-NLS-1$
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
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        return null;
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
