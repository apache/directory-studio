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
package org.apache.directory.studio.openldap.config.model.io;


import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeRootDSERunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;


/**
 * This class implements a configuration reader for OpenLDAP.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConfigurationUtils
{
    /** The default OpenLDAP configuration DN */
    public static final String DEFAULT_CONFIG_DN = "cn=config";


    /**
     * Gets the configuration DN.
     *
     * @param browserConnection the browser connection
     * @return the configuration DN
     * @throws ConfigurationException if the configuration DN couldn't be found
     */
    public static Dn getConfigurationDn( IBrowserConnection browserConnection )
        throws ConfigurationException
    {
        IProgressMonitor progressMonitor = new NullProgressMonitor();
        StudioProgressMonitor monitor = new StudioProgressMonitor( progressMonitor );

        // Opening the connection (if needed)
        openConnection( browserConnection.getConnection(), monitor );

        // Load Root DSE (if needed)
        if ( browserConnection.getRootDSE() == null )
        {
            InitializeRootDSERunnable.loadRootDSE( browserConnection, monitor );
        }

        // Getting the Root DSE
        IRootDSE rootDse = browserConnection.getRootDSE();

        try
        {
            // Getting the 'configcontext' attribute
            IAttribute configContextAttribute = rootDse.getAttribute( "configcontext" );
            if ( ( configContextAttribute != null ) && ( configContextAttribute.getValueSize() > 0 ) )
            {
                return new Dn( configContextAttribute.getStringValue() );
            }
            else
            {
                return getDefaultConfigurationDn();
            }
        }
        catch ( LdapInvalidDnException e )
        {
            throw new ConfigurationException( e );
        }
    }


    /**
     * Gets the default configuration DN.
     *
     * @return the default configuration DN
     * @throws ConfigurationException if an error occurred
     */
    public static Dn getDefaultConfigurationDn() throws ConfigurationException
    {
        try
        {
            return new Dn( DEFAULT_CONFIG_DN );
        }
        catch ( LdapInvalidDnException e )
        {
            throw new ConfigurationException( e );
        }
    }


    /**
     * Opens the connection.
     *
     * @param connection the connection
     * @param monitor the monitor
     */
    public static void openConnection( Connection connection, StudioProgressMonitor monitor )
    {
        if ( connection != null && !connection.getConnectionWrapper().isConnected() )
        {
            connection.getConnectionWrapper().connect( monitor );
            if ( connection.getConnectionWrapper().isConnected() )
            {
                connection.getConnectionWrapper().bind( monitor );
            }

            if ( connection.getConnectionWrapper().isConnected() )
            {
                for ( IConnectionListener listener : ConnectionCorePlugin.getDefault()
                    .getConnectionListeners() )
                {
                    listener.connectionOpened( connection, monitor );
                }
                ConnectionEventRegistry.fireConnectionOpened( connection, null );
            }
        }
    }
}
