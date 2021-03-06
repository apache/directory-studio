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


import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.directory.api.ldap.model.exception.LdapTlsHandshakeFailCause;
import org.apache.directory.studio.connection.core.event.CoreEventRunner;
import org.apache.directory.studio.connection.core.event.EventRunner;
import org.apache.directory.studio.connection.core.io.api.LdifModificationLogger;
import org.apache.directory.studio.connection.core.io.api.LdifSearchLogger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;


/**
 * The activator class controls the plug-in life cycle
 */
public class ConnectionCorePlugin extends Plugin
{

    /** The file name of the permanent trust store */
    private static final String PERMANENT_TRUST_STORE = "permanent.jks"; //$NON-NLS-1$

    /** The password of the permanent trust store */
    private static final String PERMANENT_TRUST_STORE_PASSWORD = "changeit"; //$NON-NLS-1$

    /** The shared instance */
    private static ConnectionCorePlugin plugin;

    /** The connection manager */
    private ConnectionManager connectionManager;

    /** The connection folder manager */
    private ConnectionFolderManager connectionFolderManager;

    /** The passwords keystore manager */
    private PasswordsKeyStoreManager passwordsKeyStoreManager;

    /** The permanent trust store */
    private StudioKeyStoreManager permanentTrustStoreManager;

    /** The session trust store */
    private StudioKeyStoreManager sessionTrustStoreManager;

    /** The event runner. */
    private EventRunner eventRunner;

    /** The authentication handler */
    private IAuthHandler authHandler;

    /** The referral handler */
    private IReferralHandler referralHandler;

    /** The certificate handler */
    private ICertificateHandler certificateHandler;

    /** The LDAP loggers. */
    private List<ILdapLogger> ldapLoggers;

    /** The connection listeners. */
    private List<IConnectionListener> connectionListeners;

    /** The plugin properties */
    private PropertyResourceBundle properties;

    /**
     * The constructor
     */
    public ConnectionCorePlugin()
    {
        plugin = this;
    }


    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        if ( eventRunner == null )
        {
            eventRunner = new CoreEventRunner();
        }

        if ( connectionManager == null )
        {
            connectionManager = new ConnectionManager();
        }

        if ( connectionFolderManager == null )
        {
            connectionFolderManager = new ConnectionFolderManager();
        }

        if ( passwordsKeyStoreManager == null )
        {
            passwordsKeyStoreManager = new PasswordsKeyStoreManager();
        }

        if ( permanentTrustStoreManager == null )
        {
            permanentTrustStoreManager = StudioKeyStoreManager.createFileKeyStoreManager( PERMANENT_TRUST_STORE,
                PERMANENT_TRUST_STORE_PASSWORD );
        }

        if ( sessionTrustStoreManager == null )
        {
            sessionTrustStoreManager = StudioKeyStoreManager.createMemoryKeyStoreManager();
        }

        // Nasty hack to get the API bundles started. DO NOT REMOVE
        Platform.getBundle( "org.apache.directory.api.ldap.codec.core" ).start();
        Platform.getBundle( "org.apache.directory.api.ldap.extras.codec" ).start();
        Platform.getBundle( "org.apache.directory.api.ldap.net.mina" ).start();
    }


    /**
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );

        if ( eventRunner != null )
        {
            eventRunner = null;
        }

        if ( connectionManager != null )
        {
            Connection[] connections = connectionManager.getConnections();
            for ( int i = 0; i < connections.length; i++ )
            {
                connections[i].getConnectionWrapper().disconnect();
            }
            connectionManager = null;
        }

        if ( connectionFolderManager != null )
        {
            connectionFolderManager = null;
        }

        if ( permanentTrustStoreManager != null )
        {
            permanentTrustStoreManager = null;
        }

        if ( sessionTrustStoreManager != null )
        {
            sessionTrustStoreManager = null;
        }
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ConnectionCorePlugin getDefault()
    {
        return plugin;
    }


    /**
     * Gets the Connection Manager
     *
     * @return
     *      the connection manager
     */
    public ConnectionManager getConnectionManager()
    {
        return connectionManager;
    }


    /**
     * Gets the connection folder manager.
     *
     * @return the connection folder manager
     */
    public ConnectionFolderManager getConnectionFolderManager()
    {
        return connectionFolderManager;
    }


    /**
     * Gets the event runner.
     *
     * @return the event runner
     */
    public EventRunner getEventRunner()
    {
        return eventRunner;
    }


    /**
     * Gets the password keystore manager.
     *
     * @return the password keystore manager
     */
    public PasswordsKeyStoreManager getPasswordsKeyStoreManager()
    {
        return passwordsKeyStoreManager;
    }


    /**
     * Gets the permanent trust store manager.
     *
     * @return the permanent trust store manager
     */
    public StudioKeyStoreManager getPermanentTrustStoreManager()
    {
        return permanentTrustStoreManager;
    }


    /**
     * Gets the session trust store manager.
     *
     * @return the session trust store manager
     */
    public StudioKeyStoreManager getSessionTrustStoreManager()
    {
        return sessionTrustStoreManager;
    }


    /**
     * Gets the authentication handler
     *
     * @return
     *      the authentication handler
     */
    public IAuthHandler getAuthHandler()
    {
        if ( authHandler == null )
        {
            // if no authentication handler was set a default authentication handler is used
            // that only works if the bind password is stored within the connection parameters.
            authHandler = new IAuthHandler()
            {
                public ICredentials getCredentials( ConnectionParameter connectionParameter )
                {
                    if ( connectionParameter.getBindPrincipal() == null
                        || "".equals( connectionParameter.getBindPrincipal() ) ) //$NON-NLS-1$
                    {
                        return new Credentials( "", "", connectionParameter ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else if ( connectionParameter.getBindPassword() != null
                        && !"".equals( connectionParameter.getBindPassword() ) ) //$NON-NLS-1$
                    {
                        return new Credentials( connectionParameter.getBindPrincipal(), connectionParameter
                            .getBindPassword(), connectionParameter );
                    }
                    else
                    {
                        // no credentials provided in connection parameters
                        // returning null cancel the authentication
                        return null;
                    }
                }
            };
        }
        return authHandler;
    }


    /**
     * Sets the authentication handler
     *
     * @param authHandler
     *      the authentication handler to set
     */
    public void setAuthHandler( IAuthHandler authHandler )
    {
        this.authHandler = authHandler;
    }


    /**
     * Gets the referral handler
     *
     * @return
     *      the referral handler
     */
    public IReferralHandler getReferralHandler()
    {
        if ( referralHandler == null )
        {
            // if no referral handler was set a default referral handler is used
            // that just cancels referral chasing
            referralHandler = new IReferralHandler()
            {
                public Connection getReferralConnection( List<String> referralUrls )
                {
                    // null cancels referral chasing
                    return null;
                }
            };
        }
        return referralHandler;
    }


    /**
     * Sets the referral handler
     *
     * @param referralHandler
     *      the referral handler to set
     */
    public void setReferralHandler( IReferralHandler referralHandler )
    {
        this.referralHandler = referralHandler;
    }


    /**
     * Gets the certificate handler
     *
     * @return
     *      the certificate handler
     */
    public ICertificateHandler getCertificateHandler()
    {
        if ( certificateHandler == null )
        {
            // if no certificate handler was set a default certificate handler is used
            // that just returns "No"
            certificateHandler = new ICertificateHandler()
            {
                public TrustLevel verifyTrustLevel( String host, X509Certificate[] certChain,
                    Collection<LdapTlsHandshakeFailCause> failCauses )
                {
                    return TrustLevel.Not;
                }
            };
        }
        return certificateHandler;
    }


    /**
     * Sets the certificate handler
     *
     * @param certificateHandler
     *      the certificate handler to set
     */
    public void setCertificateHandler( ICertificateHandler certificateHandler )
    {
        this.certificateHandler = certificateHandler;
    }


    /**
     * Gets the LDIF modification logger.
     * 
     * @return the LDIF modification logger, null if none found.
     */
    public LdifModificationLogger getLdifModificationLogger()
    {
        List<ILdapLogger> ldapLoggers = getLdapLoggers();
        for ( ILdapLogger ldapLogger : ldapLoggers )
        {
            if ( ldapLogger instanceof LdifModificationLogger )
            {
                return ( LdifModificationLogger ) ldapLogger;
            }
        }
        return null;
    }


    /**
     * Gets the LDIF search logger.
     * 
     * @return the LDIF search logger, null if none found.
     */
    public LdifSearchLogger getLdifSearchLogger()
    {
        List<ILdapLogger> ldapLoggers = getLdapLoggers();
        for ( ILdapLogger ldapLogger : ldapLoggers )
        {
            if ( ldapLogger instanceof LdifSearchLogger )
            {
                return ( LdifSearchLogger ) ldapLogger;
            }
        }
        return null;
    }


    /**
     * Gets the LDAP loggers.
     * 
     * @return the LDAP loggers
     */
    public List<ILdapLogger> getLdapLoggers()
    {
        if ( ldapLoggers == null )
        {
            ldapLoggers = new ArrayList<ILdapLogger>();

            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IExtensionPoint extensionPoint = registry.getExtensionPoint( "org.apache.directory.studio.ldaplogger" ); //$NON-NLS-1$
            IConfigurationElement[] members = extensionPoint.getConfigurationElements();
            for ( IConfigurationElement member : members )
            {
                try
                {
                    ILdapLogger logger = ( ILdapLogger ) member.createExecutableExtension( "class" ); //$NON-NLS-1$
                    logger.setId( member.getAttribute( "id" ) ); //$NON-NLS-1$
                    logger.setName( member.getAttribute( "name" ) ); //$NON-NLS-1$
                    logger.setDescription( member.getAttribute( "description" ) ); //$NON-NLS-1$
                    ldapLoggers.add( logger );
                }
                catch ( Exception e )
                {
                    getLog().log(
                        new Status( IStatus.ERROR, ConnectionCoreConstants.PLUGIN_ID, 1,
                            Messages.error__unable_to_create_ldap_logger + member.getAttribute( "class" ), e ) ); //$NON-NLS-1$
                }
            }
        }

        return ldapLoggers;
    }


    /**
     * Gets the connection listeners.
     * 
     * @return the connection listeners
     */
    public List<IConnectionListener> getConnectionListeners()
    {
        if ( connectionListeners == null )
        {
            connectionListeners = new ArrayList<IConnectionListener>();

            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IExtensionPoint extensionPoint = registry
                .getExtensionPoint( "org.apache.directory.studio.connectionlistener" ); //$NON-NLS-1$
            IConfigurationElement[] members = extensionPoint.getConfigurationElements();
            for ( IConfigurationElement member : members )
            {
                try
                {
                    IConnectionListener listener = ( IConnectionListener ) member.createExecutableExtension( "class" ); //$NON-NLS-1$
                    connectionListeners.add( listener );
                }
                catch ( Exception e )
                {
                    getLog().log(
                        new Status( IStatus.ERROR, ConnectionCoreConstants.PLUGIN_ID, 1,
                            Messages.error__unable_to_create_connection_listener + member.getAttribute( "class" ), //$NON-NLS-1$
                            e ) );
                }
            }
        }

        return connectionListeners;
    }


    /**
     * Gets the plugin properties.
     *
     * @return
     *      the plugin properties
     */
    public PropertyResourceBundle getPluginProperties()
    {
        if ( properties == null )
        {
            try
            {
                properties = new PropertyResourceBundle( FileLocator.openStream( this.getBundle(), new Path(
                    "plugin.properties" ), false ) ); //$NON-NLS-1$
            }
            catch ( IOException e )
            {
                // We can't use the PLUGIN_ID constant since loading the plugin.properties file has failed,
                // So we're using a default plugin id.
                getLog().log( new Status( Status.ERROR, "org.apache.directory.studio.connection.core", Status.OK, //$NON-NLS-1$
                    Messages.error__unable_to_get_plugin_properties, e ) );
            }
        }

        return properties;
    }


    /**
     * Gets the default KRB5 login module.
     * 
     * Right now the following context factories are supported:
     * <ul>
     * <li>com.sun.security.auth.module.Krb5LoginModule</li>
     * <li>org.apache.harmony.auth.module.Krb5LoginModule</li>
     * </ul>
     * 
     * @return the default KRB5 login module
     */
    public String getDefaultKrb5LoginModule()
    {
        String defaultKrb5LoginModule = ""; //$NON-NLS-1$

        try
        {
            String sun = "com.sun.security.auth.module.Krb5LoginModule"; //$NON-NLS-1$
            Class.forName( sun );
            defaultKrb5LoginModule = sun;
        }
        catch ( ClassNotFoundException e )
        {
        }
        try
        {
            String apache = "org.apache.harmony.auth.module.Krb5LoginModule"; //$NON-NLS-1$
            Class.forName( apache );
            defaultKrb5LoginModule = apache;
        }
        catch ( ClassNotFoundException e )
        {
        }

        return defaultKrb5LoginModule;
    }


    public IEclipsePreferences getDefaultScopePreferences()
    {
        return DefaultScope.INSTANCE.getNode( ConnectionCoreConstants.PLUGIN_ID );
    }


    public void flushDefaultScopePreferences()
    {
        try
        {
            getDefaultScopePreferences().flush();
        }
        catch ( BackingStoreException e )
        {
            throw new RuntimeException( e );
        }
    }


    public IEclipsePreferences getInstanceScopePreferences()
    {
        return InstanceScope.INSTANCE.getNode( ConnectionCoreConstants.PLUGIN_ID );
    }


    public void flushInstanceScopePreferences()
    {
        try
        {
            getInstanceScopePreferences().flush();
        }
        catch ( BackingStoreException e )
        {
            throw new RuntimeException( e );
        }
    }


    public int getModificationLogsFileCount()
    {
        return Platform.getPreferencesService().getInt( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_COUNT, -1, null );
    }


    public int getModificationLogsFileSize()
    {
        return Platform.getPreferencesService().getInt( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_SIZE, -1, null );
    }


    public boolean isModificationLogsEnabled()
    {
        return Platform.getPreferencesService().getBoolean( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE, true, null );
    }


    public String getMModificationLogsMaskedAttributes()
    {
        return Platform.getPreferencesService().getString( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES, null, null );
    }


    public int getSearchLogsFileCount()
    {
        return Platform.getPreferencesService().getInt( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_COUNT, -1, null );
    }


    public int getSearchLogsFileSize()
    {
        return Platform.getPreferencesService().getInt( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_SIZE, -1, null );
    }


    public boolean isSearchRequestLogsEnabled()
    {
        return Platform.getPreferencesService().getBoolean( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_SEARCHREQUESTLOGS_ENABLE, true, null );
    }


    public boolean isSearchResultEntryLogsEnabled()
    {
        return Platform.getPreferencesService().getBoolean( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_SEARCHRESULTENTRYLOGS_ENABLE, false, null );
    }

}
