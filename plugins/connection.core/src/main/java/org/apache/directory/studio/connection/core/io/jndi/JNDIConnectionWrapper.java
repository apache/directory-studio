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


import java.io.File;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InsufficientResourcesException;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.ReferralException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.shared.ldap.codec.util.LdapURLEncodingException;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.IAuthHandler;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.ICredentials;
import org.apache.directory.studio.connection.core.IJndiLogger;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.jndi.ReferralsInfo.Referral;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.osgi.util.NLS;


/**
 * A connection wrapper that uses JNDI.
 * 
 * - asychron + cancelable
 * - SSL certificate
 * - manages broken/closed connections
 * - delete old RDN
 * - exception handling 
 * - referral handling
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class JNDIConnectionWrapper implements ConnectionWrapper
{

    private static final String JAVA_NAMING_LDAP_DELETE_RDN = "java.naming.ldap.deleteRDN"; //$NON-NLS-1$

    private static final String AUTHMETHOD_NONE = "none"; //$NON-NLS-1$

    private static final String AUTHMETHOD_SIMPLE = "simple"; //$NON-NLS-1$

    private static final String AUTHMETHOD_DIGEST_MD5 = "DIGEST-MD5"; //$NON-NLS-1$

    private static final String AUTHMETHOD_CRAM_MD5 = "CRAM-MD5"; //$NON-NLS-1$

    private static final String AUTHMETHOD_GSSAPI = "GSSAPI"; //$NON-NLS-1$

    private static final String NO_CONNECTION = "No connection"; //$NON-NLS-1$

    private static final String JAVA_NAMING_SECURITY_SASL_REALM = "java.naming.security.sasl.realm"; //$NON-NLS-1$

    private static final String JAVA_NAMING_LDAP_FACTORY_SOCKET = "java.naming.ldap.factory.socket"; //$NON-NLS-1$

    private static final String COM_SUN_JNDI_DNS_TIMEOUT_RETRIES = "com.sun.jndi.dns.timeout.retries"; //$NON-NLS-1$

    private static final String COM_SUN_JNDI_DNS_TIMEOUT_INITIAL = "com.sun.jndi.dns.timeout.initial"; //$NON-NLS-1$

    private static final String COM_SUN_JNDI_LDAP_CONNECT_TIMEOUT = "com.sun.jndi.ldap.connect.timeout"; //$NON-NLS-1$

    private static final String JAVA_NAMING_LDAP_VERSION = "java.naming.ldap.version"; //$NON-NLS-1$

    private static final String JAVA_NAMING_LDAP_DEREF_ALIASES = "java.naming.ldap.derefAliases"; //$NON-NLS-1$

    private static final String JAVA_NAMING_LDAP_ATTRIBUTES_BINARY = "java.naming.ldap.attributes.binary"; //$NON-NLS-1$

    private static int SEARCH_RESQUEST_NUM = 0;

    private Connection connection;

    private boolean useLdaps;

    private boolean useStartTLS;

    private String authMethod;

    private String bindPrincipal;

    private String bindCredentials;

    private String saslRealm;

    private Hashtable<String, String> environment;

    private InitialLdapContext context;

    private boolean isConnected;

    private Thread jobThread;

    private Collection<String> binaryAttributes;

    /** JNDI constant for "throw" referrals handling */
    public static final String REFERRAL_THROW = "throw"; //$NON-NLS-1$

    /** JNDI constant for "follow" referrals handling */
    public static final String REFERRAL_FOLLOW = "follow"; //$NON-NLS-1$

    /** JNDI constant for "ignore" referrals handling */
    public static final String REFERRAL_IGNORE = "ignore"; //$NON-NLS-1$

    /** JNDI constant for "searching" alias dereferencing */
    public static final String ALIAS_SEARCHING = "searching"; //$NON-NLS-1$

    /** JNDI constant for "finding" alias dereferencing */
    public static final String ALIAS_FINDING = "finding"; //$NON-NLS-1$

    /** JNDI constant for "always" alias dereferencing */
    public static final String ALIAS_ALWAYS = "always"; //$NON-NLS-1$

    /** JNDI constant for "never" alias dereferencing */
    public static final String ALIAS_NEVER = "never"; //$NON-NLS-1$


    /**
     * Creates a new instance of JNDIConnectionContext.
     * 
     * @param connection the connection
     */
    public JNDIConnectionWrapper( Connection connection )
    {
        this.connection = connection;
    }


    /**
     * {@inheritDoc}
     */
    public void connect( StudioProgressMonitor monitor )
    {
        context = null;
        isConnected = false;
        jobThread = null;

        try
        {
            doConnect( monitor );
        }
        catch ( NamingException ne )
        {
            disconnect();
            monitor.reportError( ne );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void disconnect()
    {
        if ( jobThread != null )
        {
            Thread t = jobThread;
            jobThread = null;
            t.interrupt();
        }
        if ( context != null )
        {
            try
            {
                context.close();
            }
            catch ( NamingException e )
            {
                // ignore
            }
            context = null;
        }
        isConnected = false;
        System.gc();
    }


    /**
     * {@inheritDoc}
     */
    public void bind( StudioProgressMonitor monitor )
    {
        try
        {
            doBind( monitor );
        }
        catch ( NamingException ne )
        {
            disconnect();
            monitor.reportError( ne );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void unbind()
    {
        disconnect();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isConnected()
    {
        return context != null;
    }


    /**
     * Sets the binary attributes.
     * 
     * @param binaryAttributes the binary attributes
     */
    public void setBinaryAttributes( Collection<String> binaryAttributes )
    {
        this.binaryAttributes = binaryAttributes;
        String binaryAttributesString = StringUtils.EMPTY;
        for ( String string : binaryAttributes )
        {
            binaryAttributesString += string + ' ';
        }

        if ( environment != null )
        {
            environment.put( JAVA_NAMING_LDAP_ATTRIBUTES_BINARY, binaryAttributesString );
        }

        if ( context != null )
        {
            try
            {
                context.addToEnvironment( JAVA_NAMING_LDAP_ATTRIBUTES_BINARY, binaryAttributesString );
            }
            catch ( NamingException e )
            {
                // TODO: logging
                e.printStackTrace();
            }
        }
    }


    /**
     * Search.
     * 
     * @param searchBase the search base
     * @param filter the filter
     * @param searchControls the controls
     * @param aliasesDereferencingMethod the aliases dereferencing method
     * @param referralsHandlingMethod the referrals handling method
     * @param controls the LDAP controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     * 
     * @return the naming enumeration or null if an exception occurs.
     */
    public StudioNamingEnumeration search( final String searchBase, final String filter,
        final SearchControls searchControls, final AliasDereferencingMethod aliasesDereferencingMethod,
        final ReferralHandlingMethod referralsHandlingMethod, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        final long requestNum = SEARCH_RESQUEST_NUM++;

        // start
        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                LdapContext searchCtx = context;
                try
                {
                    // create the search context
                    searchCtx = context.newInstance( controls );

                    // translate alias dereferencing method
                    searchCtx.addToEnvironment( JAVA_NAMING_LDAP_DEREF_ALIASES,
                        translateDerefAliasMethod( aliasesDereferencingMethod ) );

                    // use "throw" as we handle referrals manually
                    searchCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // perform the search
                    NamingEnumeration<SearchResult> result = searchCtx.search( JNDIConnectionWrapper
                        .getSaveJndiName( searchBase ), filter, searchControls );
                    namingEnumeration = new StudioNamingEnumeration( connection, searchCtx, result, null, searchBase,
                        filter, searchControls, aliasesDereferencingMethod, referralsHandlingMethod, controls,
                        requestNum, monitor, referralsInfo );
                }
                catch ( PartialResultException e )
                {
                    namingEnumeration = new StudioNamingEnumeration( connection, searchCtx, null, e, searchBase,
                        filter, searchControls, aliasesDereferencingMethod, referralsHandlingMethod, controls,
                        requestNum, monitor, referralsInfo );
                }
                catch ( ReferralException e )
                {
                    namingEnumeration = new StudioNamingEnumeration( connection, searchCtx, null, e, searchBase,
                        filter, searchControls, aliasesDereferencingMethod, referralsHandlingMethod, controls,
                        requestNum, monitor, referralsInfo );
                }
                catch ( NamingException e )
                {
                    namingException = e;
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    if ( namingEnumeration != null )
                    {
                        logger.logSearchRequest( connection, searchBase, filter, searchControls,
                            aliasesDereferencingMethod, controls, requestNum, namingException );
                    }
                    else
                    {
                        logger.logSearchRequest( connection, searchBase, filter, searchControls,
                            aliasesDereferencingMethod, controls, requestNum, namingException );
                        logger.logSearchResultDone( connection, 0, requestNum, namingException );
                    }
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne );
            return null;
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
            return null;
        }
        else
        {
            return runnable.getResult();
        }
    }


    /**
     * Modifies attributes of an entry.
     * 
     * @param dn the DN
     * @param modificationItems the modification items
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void modifyEntry( final String dn, final ModificationItem[] modificationItems, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor.reportError( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                boolean logModifycation = true;
                try
                {
                    // create modify context
                    LdapContext modCtx = context.newInstance( controls );

                    // use "throw" as we handle referrals manually
                    modCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // perform modification
                    modCtx.modifyAttributes( getSaveJndiName( dn ), modificationItems );
                }
                catch ( ReferralException re )
                {
                    logModifycation = false;
                    try
                    {
                        ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                        Referral referral = newReferralsInfo.getNextReferral();
                        if ( referral != null )
                        {
                            Connection referralConnection = getReferralConnection( referral, monitor, this );
                            if ( referralConnection != null )
                            {
                                String referralDn = referral.getLdapURLs().get( 0 ).getDn().getName();
                                referralConnection.getConnectionWrapper().modifyEntry( referralDn,
                                    modificationItems, controls, monitor, newReferralsInfo );
                            }
                            else
                            {
                                canceled = true;
                            }
                        }

                        return;
                    }
                    catch ( NamingException ne )
                    {
                        namingException = ne;
                    }
                }
                catch ( NamingException ne )
                {
                    namingException = ne;
                }

                if ( logModifycation )
                {
                    for ( IJndiLogger logger : getJndiLoggers() )
                    {
                        logger.logChangetypeModify( connection, dn, modificationItems, controls, namingException );
                    }
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne );
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
        }
    }


    /**
     * Renames an entry.
     * 
     * @param oldDn the old DN
     * @param newDn the new DN
     * @param deleteOldRdn true to delete the old RDN
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void renameEntry( final String oldDn, final String newDn, final boolean deleteOldRdn,
        final Control[] controls, final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor.reportError( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                boolean logModifycation = true;
                try
                {
                    // create modify context
                    LdapContext modCtx = context.newInstance( controls );

                    // use "throw" as we handle referrals manually
                    modCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // delete old RDN
                    if ( deleteOldRdn )
                    {
                        modCtx.addToEnvironment( JAVA_NAMING_LDAP_DELETE_RDN, "true" ); //$NON-NLS-1$
                    }
                    else
                    {
                        modCtx.addToEnvironment( JAVA_NAMING_LDAP_DELETE_RDN, "false" ); //$NON-NLS-1$
                    }

                    // rename entry
                    modCtx.rename( getSaveJndiName( oldDn ), getSaveJndiName( newDn ) );
                }
                catch ( ReferralException re )
                {
                    logModifycation = false;
                    try
                    {
                        ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                        Referral referral = newReferralsInfo.getNextReferral();
                        if ( referral != null )
                        {
                            Connection referralConnection = getReferralConnection( referral, monitor, this );
                            if ( referralConnection != null )
                            {
                                referralConnection.getConnectionWrapper().renameEntry( oldDn, newDn, deleteOldRdn,
                                    controls, monitor, newReferralsInfo );
                            }
                            else
                            {
                                canceled = true;
                            }
                        }
                    }
                    catch ( NamingException ne )
                    {
                        namingException = ne;
                    }
                }
                catch ( NamingException ne )
                {
                    namingException = ne;
                }

                if ( logModifycation )
                {
                    for ( IJndiLogger logger : getJndiLoggers() )
                    {
                        logger.logChangetypeModDn( connection, oldDn, newDn, deleteOldRdn, controls, namingException );
                    }
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne );
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
        }
    }


    /**
     * Creates an entry.
     * 
     * @param dn the entry's DN
     * @param attributes the entry's attributes
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void createEntry( final String dn, final Attributes attributes, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor.reportError( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                boolean logModifycation = true;
                try
                {
                    // create modify context
                    LdapContext modCtx = context.newInstance( controls );

                    // use "throw" as we handle referrals manually
                    modCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // create entry
                    modCtx.createSubcontext( getSaveJndiName( dn ), attributes );
                }
                catch ( ReferralException re )
                {
                    logModifycation = false;
                    try
                    {
                        ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                        Referral referral = newReferralsInfo.getNextReferral();
                        if ( referral != null )
                        {
                            Connection referralConnection = getReferralConnection( referral, monitor, this );
                            if ( referralConnection != null )
                            {
                                String referralDn = referral.getLdapURLs().get( 0 ).getDn().getName();
                                referralConnection.getConnectionWrapper().createEntry( referralDn, attributes,
                                    controls, monitor, newReferralsInfo );
                            }
                            else
                            {
                                canceled = true;
                            }
                        }
                    }
                    catch ( NamingException ne )
                    {
                        namingException = ne;
                    }
                }
                catch ( NamingException ne )
                {
                    namingException = ne;
                }

                if ( logModifycation )
                {
                    for ( IJndiLogger logger : getJndiLoggers() )
                    {
                        logger.logChangetypeAdd( connection, dn, attributes, controls, namingException );
                    }
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne );
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
        }
    }


    /**
     * Deletes an entry.
     * 
     * @param dn the DN of the entry to delete
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void deleteEntry( final String dn, final Control[] controls, final StudioProgressMonitor monitor,
        final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor.reportError( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                boolean logModifycation = true;
                try
                {
                    // create modify context
                    LdapContext modCtx = context.newInstance( controls );

                    // use "throw" as we handle referrals manually
                    modCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // delete entry
                    modCtx.destroySubcontext( getSaveJndiName( dn ) );
                }
                catch ( ReferralException re )
                {
                    logModifycation = false;
                    try
                    {
                        ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                        Referral referral = newReferralsInfo.getNextReferral();
                        if ( referral != null )
                        {
                            Connection referralConnection = getReferralConnection( referral, monitor, this );
                            if ( referralConnection != null )
                            {
                                String referralDn = referral.getLdapURLs().get( 0 ).getDn().getName();
                                referralConnection.getConnectionWrapper().deleteEntry( referralDn, controls,
                                    monitor, newReferralsInfo );
                            }
                            else
                            {
                                canceled = true;
                            }
                        }
                    }
                    catch ( NamingException ne )
                    {
                        namingException = ne;
                    }
                }
                catch ( NamingException ne )
                {
                    namingException = ne;
                }

                if ( logModifycation )
                {
                    for ( IJndiLogger logger : getJndiLoggers() )
                    {
                        logger.logChangetypeDelete( connection, dn, controls, namingException );
                    }
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne );
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
        }
    }


    private void doConnect( final StudioProgressMonitor monitor ) throws NamingException
    {
        context = null;
        isConnected = true;

        // setup connection parameters
        String host = connection.getConnectionParameter().getHost();
        int port = connection.getConnectionParameter().getPort();

        useLdaps = connection.getConnectionParameter().getEncryptionMethod() == ConnectionParameter.EncryptionMethod.LDAPS;
        useStartTLS = connection.getConnectionParameter().getEncryptionMethod() == ConnectionParameter.EncryptionMethod.START_TLS;

        environment = new Hashtable<String, String>();
        Preferences preferences = ConnectionCorePlugin.getDefault().getPluginPreferences();
        final boolean validateCertificates = preferences
            .getBoolean( ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES );
        String ldapCtxFactory = preferences.getString( ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY );
        environment.put( Context.INITIAL_CONTEXT_FACTORY, ldapCtxFactory );
        environment.put( JAVA_NAMING_LDAP_VERSION, "3" ); //$NON-NLS-1$

        // timeouts
        // Don't use a timeout when using ldaps: JNDI throws a SocketException 
        // when setting a timeout on SSL connections.
        if ( !useLdaps )
        {
            environment.put( COM_SUN_JNDI_LDAP_CONNECT_TIMEOUT, "10000" ); //$NON-NLS-1$
        }
        environment.put( COM_SUN_JNDI_DNS_TIMEOUT_INITIAL, "2000" ); //$NON-NLS-1$
        environment.put( COM_SUN_JNDI_DNS_TIMEOUT_RETRIES, "3" ); //$NON-NLS-1$

        // ldaps://
        if ( useLdaps )
        {
            environment.put( Context.PROVIDER_URL, LdapURL.LDAPS_SCHEME + host + ':' + port );
            environment.put( Context.SECURITY_PROTOCOL, "ssl" ); //$NON-NLS-1$
            // host name verification is done in StudioTrustManager
            environment.put( JAVA_NAMING_LDAP_FACTORY_SOCKET, validateCertificates ? StudioSSLSocketFactory.class
                .getName() : DummySSLSocketFactory.class.getName() );
        }
        else
        {
            environment.put( Context.PROVIDER_URL, LdapURL.LDAP_SCHEME + host + ':' + port );
        }

        if ( binaryAttributes != null )
        {
            setBinaryAttributes( binaryAttributes );
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                try
                {
                    context = new InitialLdapContext( environment, null );

                    if ( useStartTLS )
                    {
                        try
                        {
                            StartTlsResponse tls = ( StartTlsResponse ) context
                                .extendedOperation( new StartTlsRequest() );
                            // deactivate host name verification at this level,
                            // host name verification is done in StudioTrustManager
                            tls.setHostnameVerifier( new HostnameVerifier()
                            {
                                public boolean verify( String hostname, SSLSession session )
                                {
                                    return true;
                                }
                            } );
                            if ( validateCertificates )
                            {
                                tls.negotiate( StudioSSLSocketFactory.getDefault() );
                            }
                            else
                            {
                                tls.negotiate( DummySSLSocketFactory.getDefault() );
                            }
                        }
                        catch ( Exception e )
                        {
                            namingException = new NamingException( e.getMessage() != null ? e.getMessage()
                                : "Error while establishing TLS session" ); //$NON-NLS-1$
                            namingException.setRootCause( e );
                            context.close();
                        }
                    }
                }
                catch ( NamingException ne )
                {
                    namingException = ne;
                }
            }
        };

        runAndMonitor( runnable, monitor );

        if ( runnable.getException() != null )
        {
            throw runnable.getException();
        }
        else if ( context != null )
        {
            // all OK
        }
        else
        {
            throw new NamingException( "???" ); //$NON-NLS-1$
        }
    }


    private void doBind( final StudioProgressMonitor monitor ) throws NamingException
    {
        if ( context != null && isConnected )
        {
            // setup authentication methdod
            authMethod = AUTHMETHOD_NONE;
            if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SIMPLE )
            {
                authMethod = AUTHMETHOD_SIMPLE;
            }
            else if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_DIGEST_MD5 )
            {
                authMethod = AUTHMETHOD_DIGEST_MD5;
                saslRealm = connection.getConnectionParameter().getSaslRealm();
            }
            else if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_CRAM_MD5 )
            {
                authMethod = AUTHMETHOD_CRAM_MD5;
            }
            else if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_GSSAPI )
            {
                authMethod = AUTHMETHOD_GSSAPI;
            }

            // setup credentials
            IAuthHandler authHandler = ConnectionCorePlugin.getDefault().getAuthHandler();
            if ( authHandler == null )
            {
                NamingException namingException = new NamingException( Messages.model__no_auth_handler );
                monitor.reportError( Messages.model__no_auth_handler, namingException );
                throw namingException;
            }
            ICredentials credentials = authHandler.getCredentials( connection.getConnectionParameter() );
            if ( credentials == null )
            {
                CancelException cancelException = new CancelException();
                monitor.setCanceled( true );
                monitor.reportError( Messages.model__no_credentials, cancelException );
                throw cancelException;
            }
            if ( credentials.getBindPrincipal() == null || credentials.getBindPassword() == null )
            {
                NamingException namingException = new NamingException( Messages.model__no_credentials );
                monitor.reportError( Messages.model__no_credentials, namingException );
                throw namingException;
            }
            bindPrincipal = credentials.getBindPrincipal();
            bindCredentials = credentials.getBindPassword();

            InnerRunnable runnable = new InnerRunnable()
            {
                public void run()
                {
                    try
                    {
                        context.removeFromEnvironment( Context.SECURITY_AUTHENTICATION );
                        context.removeFromEnvironment( Context.SECURITY_PRINCIPAL );
                        context.removeFromEnvironment( Context.SECURITY_CREDENTIALS );
                        context.removeFromEnvironment( JAVA_NAMING_SECURITY_SASL_REALM );

                        context.addToEnvironment( Context.SECURITY_AUTHENTICATION, authMethod );

                        // SASL options
                        if ( connection.getConnectionParameter().getAuthMethod() == AuthenticationMethod.SASL_CRAM_MD5
                            || connection.getConnectionParameter().getAuthMethod() == AuthenticationMethod.SASL_DIGEST_MD5
                            || connection.getConnectionParameter().getAuthMethod() == AuthenticationMethod.SASL_GSSAPI )
                        {
                            // Request quality of protection
                            switch ( connection.getConnectionParameter().getSaslQop() )
                            {
                                case AUTH:
                                    context.addToEnvironment( "javax.security.sasl.qop", "auth" );
                                    break;
                                case AUTH_INT:
                                    context.addToEnvironment( "javax.security.sasl.qop", "auth-int" );
                                    break;
                                case AUTH_INT_PRIV:
                                    context.addToEnvironment( "javax.security.sasl.qop", "auth-conf" );
                                    break;
                            }

                            // Request mutual authentication
                            if ( connection.getConnectionParameter().isSaslMutualAuthentication() )
                            {
                                context.addToEnvironment( "javax.security.sasl.server.authentication", "true" );
                            }
                            else
                            {
                                context.removeFromEnvironment( "javax.security.sasl.server.authentication" );
                            }

                            // Request cryptographic protection strength
                            switch ( connection.getConnectionParameter().getSaslSecurityStrength() )
                            {
                                case HIGH:
                                    context.addToEnvironment( "javax.security.sasl.strength", "high" );
                                    break;
                                case MEDIUM:
                                    context.addToEnvironment( "javax.security.sasl.strength", "medium" );
                                    break;
                                case LOW:
                                    context.addToEnvironment( "javax.security.sasl.strength", "low" );
                                    break;
                            }
                        }

                        // Bind
                        if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_GSSAPI )
                        {
                            // GSSAPI
                            doGssapiBind( this );
                        }
                        else
                        {
                            // no GSSAPI
                            context.addToEnvironment( Context.SECURITY_PRINCIPAL, bindPrincipal );
                            context.addToEnvironment( Context.SECURITY_CREDENTIALS, bindCredentials );

                            if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_DIGEST_MD5
                                && StringUtils.isNotEmpty( saslRealm ) )
                            {
                                context.addToEnvironment( JAVA_NAMING_SECURITY_SASL_REALM, saslRealm );
                            }

                            context.reconnect( context.getConnectControls() );
                        }
                    }
                    catch ( NamingException ne )
                    {
                        namingException = ne;
                    }
                }
            };

            runAndMonitor( runnable, monitor );

            if ( runnable.getException() != null )
            {
                throw runnable.getException();
            }
            else if ( context != null )
            {
                // all OK
            }
            else
            {
                throw new NamingException( "???" ); //$NON-NLS-1$
            }
        }
        else
        {
            throw new NamingException( NO_CONNECTION );
        }
    }


    private void doGssapiBind( final InnerRunnable innerRunnable ) throws NamingException
    {
        File configFile = null;
        try
        {
            Preferences preferences = ConnectionCorePlugin.getDefault().getPluginPreferences();
            boolean useKrb5SystemProperties = preferences
                .getBoolean( ConnectionCoreConstants.PREFERENCE_USE_KRB5_SYSTEM_PROPERTIES );
            String krb5LoginModule = preferences.getString( ConnectionCoreConstants.PREFERENCE_KRB5_LOGIN_MODULE );

            if ( !useKrb5SystemProperties )
            {

                // Kerberos Configuration
                switch ( connection.getConnectionParameter().getKrb5Configuration() )
                {
                    case DEFAULT:
                        // nothing 
                        System.clearProperty( "java.security.krb5.conf" );
                        break;
                    case FILE:
                        // use specified krb5.conf
                        System.setProperty( "java.security.krb5.conf", connection.getConnectionParameter()
                            .getKrb5ConfigurationFile() );
                        break;
                    case MANUAL:
                        // write manual config parameters to connection specific krb5.conf file
                        String fileName = Utils.getFilenameString( connection.getId() ) + ".krb5.conf";
                        configFile = ConnectionCorePlugin.getDefault().getStateLocation().append( fileName ).toFile();
                        String realm = connection.getConnectionParameter().getKrb5Realm();
                        String host = connection.getConnectionParameter().getKrb5KdcHost();
                        int port = connection.getConnectionParameter().getKrb5KdcPort();
                        StringBuilder sb = new StringBuilder();
                        sb.append( "[libdefaults]" ).append( ConnectionCoreConstants.LINE_SEPARATOR );
                        sb.append( "default_realm = " ).append( realm ).append( ConnectionCoreConstants.LINE_SEPARATOR );
                        sb.append( "[realms]" ).append( ConnectionCoreConstants.LINE_SEPARATOR );
                        sb.append( realm ).append( " = {" ).append( ConnectionCoreConstants.LINE_SEPARATOR );
                        sb.append( "kdc = " ).append( host ).append( ":" ).append( port ).append(
                            ConnectionCoreConstants.LINE_SEPARATOR );
                        sb.append( "}" ).append( ConnectionCoreConstants.LINE_SEPARATOR );
                        try
                        {
                            FileUtils.writeStringToFile( configFile, sb.toString() );
                        }
                        catch ( IOException ioe )
                        {
                            NamingException ne = new NamingException();
                            ne.setRootCause( ioe );
                            throw ne;
                        }
                        System.setProperty( "java.security.krb5.conf", configFile.getAbsolutePath() );
                }

                // Use our custom configuration so we don't need to mess with external configuration
                Configuration.setConfiguration( new InnerConfiguration( krb5LoginModule ) );
            }

            // Gets the TGT, either from native ticket cache or obtain new from KDC
            LoginContext lc = null;
            try
            {
                lc = new LoginContext( this.getClass().getName(), new InnerCallbackHandler() );
                lc.login();
            }
            catch ( LoginException le )
            {
                NamingException ne = new NamingException();
                ne.setRootCause( le );
                throw ne;
            }

            // Login to LDAP server, obtains a service ticket from KDC
            Subject.doAs( lc.getSubject(), new PrivilegedAction<Object>()
            {
                public Object run()
                {
                    try
                    {
                        context.reconnect( context.getConnectControls() );
                    }
                    catch ( NamingException ne )
                    {
                        innerRunnable.namingException = ne;
                    }
                    return null;
                }
            } );
        }
        finally
        {
            // delete temporary config file
            if ( configFile != null && configFile.exists() )
            {
                configFile.delete();
            }
        }
    }


    private void checkConnectionAndRunAndMonitor( final InnerRunnable runnable, final StudioProgressMonitor monitor )
        throws NamingException
    {
        // check connection
        if ( !isConnected || context == null )
        {
            doConnect( monitor );
            doBind( monitor );
        }
        if ( context == null )
        {
            throw new NamingException( NO_CONNECTION );
        }

        // loop for reconnection
        for ( int i = 0; i <= 1; i++ )
        {
            runAndMonitor( runnable, monitor );

            // check reconnection
            if ( i == 0
                && runnable.getException() != null
                && ( ( runnable.getException() instanceof CommunicationException )
                    || ( runnable.getException() instanceof ServiceUnavailableException ) || ( runnable.getException() instanceof InsufficientResourcesException ) ) )
            {

                doConnect( monitor );
                doBind( monitor );
                runnable.reset();
            }
            else
            {
                break;
            }
        }
    }


    private void runAndMonitor( final InnerRunnable runnable, final StudioProgressMonitor monitor )
        throws CancelException
    {
        if ( !monitor.isCanceled() )
        {
            // monitor
            StudioProgressMonitor.CancelListener listener = new StudioProgressMonitor.CancelListener()
            {
                public void cancelRequested( StudioProgressMonitor.CancelEvent event )
                {
                    if ( monitor.isCanceled() )
                    {
                        if ( jobThread != null && jobThread.isAlive() )
                        {
                            jobThread.interrupt();
                        }
                        if ( context != null )
                        {
                            try
                            {
                                context.close();
                            }
                            catch ( NamingException ne )
                            {
                            }
                            isConnected = false;
                            context = null;
                            System.gc();
                        }
                        isConnected = false;
                    }
                }
            };
            monitor.addCancelListener( listener );
            jobThread = Thread.currentThread();

            // run
            try
            {
                // try {
                // Thread.sleep(5000);
                // } catch (InterruptedException e) {
                // System.out.println(System.currentTimeMillis() + ": sleep
                // interrupted!");
                // }
                // System.out.println(System.currentTimeMillis() + ": " +
                // runnable);

                runnable.run();
            }
            finally
            {
                monitor.removeCancelListener( listener );
                jobThread = null;
            }

            if ( monitor.isCanceled() )
            {
                throw new CancelException();
            }
        }
    }

    private final class InnerConfiguration extends Configuration
    {
        private String krb5LoginModule;
        private AppConfigurationEntry[] configList = null;


        public InnerConfiguration( String krb5LoginModule )
        {
            this.krb5LoginModule = krb5LoginModule;
        }


        public AppConfigurationEntry[] getAppConfigurationEntry( String applicationName )
        {
            if ( configList == null )
            {
                HashMap<String, Object> options = new HashMap<String, Object>();

                // TODO: this only works for Sun JVM
                options.put( "refreshKrb5Config", "true" );
                switch ( connection.getConnectionParameter().getKrb5CredentialConfiguration() )
                {
                    case USE_NATIVE:
                        options.put( "useTicketCache", "true" );
                        options.put( "doNotPrompt", "true" );
                        break;
                    case OBTAIN_TGT:
                        options.put( "doNotPrompt", "false" );
                        break;
                }

                configList = new AppConfigurationEntry[1];
                configList[0] = new AppConfigurationEntry( krb5LoginModule, LoginModuleControlFlag.REQUIRED, options );
            }
            return configList;
        }


        public void refresh()
        {
        }
    }

    private final class InnerCallbackHandler implements CallbackHandler
    {
        public void handle( Callback[] callbacks ) throws UnsupportedCallbackException, IOException
        {
            for ( int ii = 0; ii < callbacks.length; ii++ )
            {
                Callback callBack = callbacks[ii];

                if ( callBack instanceof NameCallback )
                {
                    // Handles username callback.
                    NameCallback nameCallback = ( NameCallback ) callBack;
                    nameCallback.setName( bindPrincipal );
                }
                else if ( callBack instanceof PasswordCallback )
                {
                    // Handles password callback.
                    PasswordCallback passwordCallback = ( PasswordCallback ) callBack;
                    passwordCallback.setPassword( bindCredentials.toCharArray() );
                }
                else
                {
                    throw new UnsupportedCallbackException( callBack, "Callback not supported" );
                }
            }
        }
    }

    abstract class InnerRunnable implements Runnable
    {
        protected StudioNamingEnumeration namingEnumeration = null;
        protected NamingException namingException = null;
        protected boolean canceled = false;


        /**
         * Gets the exception.
         * 
         * @return the exception
         */
        public NamingException getException()
        {
            return namingException;
        }


        /**
         * Gets the result.
         * 
         * @return the result
         */
        public StudioNamingEnumeration getResult()
        {
            return namingEnumeration;
        }


        /**
         * Checks if is canceled.
         * 
         * @return true, if is canceled
         */
        public boolean isCanceled()
        {
            return canceled;
        }


        /**
         * Reset.
         */
        public void reset()
        {
            namingEnumeration = null;
            namingException = null;
            canceled = false;
        }
    }


    private List<IJndiLogger> getJndiLoggers()
    {
        return ConnectionCorePlugin.getDefault().getJndiLoggers();
    }


    /**
     * Translates the alias dereferencing method to its JNDI specific string.
     * 
     * @param aliasDereferencingMethod the alias dereferencing method
     * 
     * @return the JNDI specific alias dereferencing method string
     */
    private String translateDerefAliasMethod( AliasDereferencingMethod aliasDereferencingMethod )
    {
        String m = ALIAS_ALWAYS;

        switch ( aliasDereferencingMethod )
        {
            case NEVER:
                m = ALIAS_NEVER;
                break;
            case ALWAYS:
                m = ALIAS_ALWAYS;
                break;
            case FINDING:
                m = ALIAS_FINDING;
                break;
            case SEARCH:
                m = ALIAS_SEARCHING;
                break;
        }

        return m;
    }


    /**
     * Gets a Name object that is save for JNDI operations.
     * <p>
     * In JNDI we have could use the following classes for names:
     * <ul>
     * <li>DN as String</li>
     * <li>javax.naming.CompositeName</li>
     * <li>javax.naming.ldap.LdapName (since Java5)</li>
     * <li>org.apache.directory.shared.ldap.name.LdapDN</li>
     * </ul>
     * <p>
     * There are some drawbacks when using this classes:
     * <ul>
     * <li>When passing DN as String, JNDI doesn't handle slashes '/' correctly.
     * So we must use a Name object here.</li>
     * <li>With CompositeName we have the same problem with slashes '/'.</li>
     * <li>When using LdapDN from shared-ldap, JNDI uses the toString() method
     * and LdapDN.toString() returns the normalized ATAV, but we need the
     * user provided ATAV.</li>
     * <li>When using LdapName for the empty DN (Root DSE) JNDI _sometimes_ throws
     * an Exception (java.lang.IndexOutOfBoundsException: Posn: -1, Size: 0
     * at javax.naming.ldap.LdapName.getPrefix(LdapName.java:240)).</li>
     * <li>Using LdapDN for the RootDSE doesn't work with Apache Harmony because
     * its JNDI provider only accepts intstances of CompositeName or LdapName.</li>
     * </ul>
     * <p>
     * So we use LdapName as default and the CompositeName for the empty DN.
     * 
     * @param name the DN
     * 
     * @return the save JNDI name
     * 
     * @throws InvalidNameException the invalid name exception
     */
    static Name getSaveJndiName( String name ) throws InvalidNameException
    {
        if ( name == null || StringUtils.isEmpty( name ) ) //$NON-NLS-1$
        {
            return new CompositeName();
        }
        else
        {
            return new LdapName( name );
        }
    }


    /**
     * Gets the referral connection from the given URL.
     * 
     * @param url the URL
     * @param monitor the progress monitor
     * @param source the source
     * 
     * @return the referral connection
     */
    public static Connection getReferralConnection( Referral referral, StudioProgressMonitor monitor, Object source )
    {
        Connection referralConnection = null;
        IReferralHandler referralHandler = ConnectionCorePlugin.getDefault().getReferralHandler();
        if ( referralHandler != null )
        {
            referralConnection = referralHandler.getReferralConnection( referral.getLdapURLs() );

            // open connection if not yet open
            if ( referralConnection != null && !referralConnection.getConnectionWrapper().isConnected() )
            {
                referralConnection.getConnectionWrapper().connect( monitor );
                referralConnection.getConnectionWrapper().bind( monitor );
                for ( IConnectionListener listener : ConnectionCorePlugin.getDefault().getConnectionListeners() )
                {
                    listener.connectionOpened( referralConnection, monitor );
                }
                ConnectionEventRegistry.fireConnectionOpened( referralConnection, source );
            }
        }
        return referralConnection;
    }


    /**
     * Retrieves all referrals from the ReferralException and
     * creates or updates the ReferralsInfo.
     * 
     * @param referralException the referral exception
     * @param initialReferralsInfo the initial referrals info, may be null
     * 
     * @return the created or updated referrals info
     * 
     * @throws NamingException if a loop was encountered.
     */
    static ReferralsInfo handleReferralException( ReferralException referralException,
        ReferralsInfo initialReferralsInfo ) throws NamingException
    {
        if ( initialReferralsInfo == null )
        {
            initialReferralsInfo = new ReferralsInfo();
        }

        Referral referral = handleReferralException( referralException, initialReferralsInfo, null );

        while ( referralException.skipReferral() )
        {
            try
            {
                Context ctx = referralException.getReferralContext();
                ctx.list( StringUtils.EMPTY ); //$NON-NLS-1$
            }
            catch ( NamingException ne )
            {
                if ( ne instanceof ReferralException )
                {
                    if ( ne != referralException )
                    {
                        // what a hack: 
                        // if the same exception is throw, we have another URL for the same Referral/SearchResultReference
                        // if another exception is throws, we have a new Referral/SearchResultReference
                        // in the latter case we null out the reference, a new one will be created by handleReferral()
                        referral = null;
                    }
                    referralException = ( ReferralException ) ne;

                    referral = handleReferralException( referralException, initialReferralsInfo, referral );
                }
                else
                {
                    break;
                }
            }
        }

        return initialReferralsInfo;
    }


    private static Referral handleReferralException( ReferralException referralException,
        ReferralsInfo initialReferralsInfo, Referral referral ) throws NamingException
    {
        try
        {
            String info = ( String ) referralException.getReferralInfo();
            String name = referralException.getRemainingName().toString();
            LdapURL url = new LdapURL( info );
            DN dn = new DN( name );

            if ( referral == null )
            {
                referral = initialReferralsInfo.new Referral( dn );
                initialReferralsInfo.addReferral( referral );
            }
            referral.addUrl( url );
        }
        catch ( LdapURLEncodingException e )
        {
        }
        catch ( LdapInvalidDnException e )
        {
        }

        return referral;
    }

}
