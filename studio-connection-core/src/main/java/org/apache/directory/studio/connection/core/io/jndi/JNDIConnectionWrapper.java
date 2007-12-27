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


import java.util.Hashtable;
import java.util.List;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InsufficientResourcesException;
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

import org.apache.directory.shared.ldap.codec.util.LdapURL;
import org.apache.directory.shared.ldap.codec.util.LdapURLEncodingException;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.IAuthHandler;
import org.apache.directory.studio.connection.core.ICredentials;
import org.apache.directory.studio.connection.core.IJndiLogger;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;


/**
 * A connection wrapper that uses JNDI.
 * 
 * - asychron + cancelable
 * - SSL certificate
 * - manages broken/closed connections
 * - delete old RDN
 * - exception handling 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class JNDIConnectionWrapper implements ConnectionWrapper
{
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

    /** JNDI constant for "throw" referrals handling */
    public static final String REFERRAL_THROW = "throw";
    
    /** JNDI constant for "follow" referrals handling */
    public static final String REFERRAL_FOLLOW = "follow";

    /** JNDI constant for "ignore" referrals handling */
    public static final String REFERRAL_IGNORE = "ignore";
    
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
     * @see org.apache.directory.studio.connection.core.io.ConnectionWrapper#connect(org.apache.directory.studio.connection.core.StudioProgressMonitor)
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
            monitor.reportError( ne.getMessage(), ne );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.io.ConnectionWrapper#disconnect()
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
     * @see org.apache.directory.studio.connection.core.io.ConnectionWrapper#bind(org.apache.directory.studio.connection.core.StudioProgressMonitor)
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
            monitor.reportError( ne.getMessage(), ne );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.io.ConnectionWrapper#unbind()
     */
    public void unbind()
    {
        disconnect();
    }


    /**
     * @see org.apache.directory.studio.connection.core.io.ConnectionWrapper#isConnected()
     */
    public boolean isConnected()
    {
        return context != null;
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
    public NamingEnumeration<SearchResult> search( final String searchBase, final String filter,
        final SearchControls searchControls, final String aliasesDereferencingMethod, final String referralsHandlingMethod,
        final Control[] controls, final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        // start
        InnerRunnable runnable = new InnerRunnable()
        {
            private NamingEnumeration<SearchResult> namingEnumeration = null;
            private NamingException namingException = null;


            public void run()
            {
                try
                {
                    LdapContext searchCtx = context.newInstance( controls );
                    searchCtx.addToEnvironment( "java.naming.ldap.derefAliases", aliasesDereferencingMethod ); //$NON-NLS-1$
                    
                    // use "throw" if referrals handling is set to "follow" as we handle referrals manually
                    String localReferralsHandlingMethod = REFERRAL_FOLLOW.equals( referralsHandlingMethod ) ? REFERRAL_THROW : referralsHandlingMethod;
                    searchCtx.addToEnvironment( Context.REFERRAL, localReferralsHandlingMethod );

                    namingEnumeration = searchCtx.search( new LdapName( searchBase ), filter, searchControls );
                    namingEnumeration = new StudioNamingEnumeration( connection, namingEnumeration, searchBase, filter,
                        searchControls, aliasesDereferencingMethod, referralsHandlingMethod, controls, monitor,
                        referralsInfo );
                }
                catch ( PartialResultException pre )
                {
                    if ( REFERRAL_IGNORE.equals( referralsHandlingMethod ) )
                    {
                        // ignore
                    }
                    else
                    {
                        namingException = pre;
                    }
                }
                catch ( ReferralException re )
                {
                    if ( REFERRAL_IGNORE.equals( referralsHandlingMethod ) )
                    {
                        // ignore
                    }
                    else if ( REFERRAL_FOLLOW.equals( referralsHandlingMethod ) )
                    {
                        try
                        {
                            ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                            LdapURL url = newReferralsInfo.getNext();
                            if ( url != null )
                            {
                                Connection referralConnection = getReferralConnection( url );
                                if ( referralConnection != null )
                                {
                                    String referralSearchBase = url.getDn() != null && !url.getDn().isEmpty() ? url
                                        .getDn().getUpName() : searchBase;
                                    String referralFilter = url.getFilter() != null && url.getFilter().length() == 0 ? url
                                        .getFilter()
                                        : filter;
                                    SearchControls referralSearchControls = new SearchControls();
                                    referralSearchControls.setSearchScope( url.getScope() > -1 ? url.getScope()
                                        : searchControls.getSearchScope() );
                                    referralSearchControls.setReturningAttributes( url.getAttributes() != null ? url
                                        .getAttributes().toArray( new String[url.getAttributes().size()] )
                                        : searchControls.getReturningAttributes() );
                                    referralSearchControls.setCountLimit( searchControls.getCountLimit() );
                                    referralSearchControls.setTimeLimit( searchControls.getTimeLimit() );
                                    referralSearchControls.setDerefLinkFlag( searchControls.getDerefLinkFlag() );
                                    referralSearchControls.setReturningObjFlag( searchControls.getReturningObjFlag() );

                                    if ( referralConnection.getJNDIConnectionWrapper().isConnected() )
                                    {
                                        referralConnection.getJNDIConnectionWrapper().connect( monitor );
                                        referralConnection.getJNDIConnectionWrapper().bind( monitor );
                                        ConnectionEventRegistry.fireConnectionOpened( referralConnection, this );
                                    }

                                    namingEnumeration = referralConnection.getJNDIConnectionWrapper().search(
                                        referralSearchBase, referralFilter, referralSearchControls,
                                        aliasesDereferencingMethod, referralsHandlingMethod, controls, monitor,
                                        newReferralsInfo );
                                }
                            }
                        }
                        catch ( NamingException e )
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        namingException = re;
                    }
                }
                catch ( NamingException e )
                {
                    namingException = e;
                }
            }


            public NamingException getException()
            {
                return namingException;
            }


            public NamingEnumeration<SearchResult> getResult()
            {
                return namingEnumeration;
            }


            public void reset()
            {
                namingEnumeration = null;
                namingException = null;
            }

        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne.getMessage(), ne );
            return null;
        }

        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException().getMessage(), runnable.getException() );
            return null;
        }
        else if ( runnable.getResult() != null )
        {
            return runnable.getResult();
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets the referral connection from the given URL.
     * 
     * @param url the URL
     * 
     * @return the referral connection
     */
    static Connection getReferralConnection( LdapURL url )
    {
        Connection referralConnection = null;
        IReferralHandler referralHandler = ConnectionCorePlugin.getDefault().getReferralHandler();
        if ( referralHandler != null )
        {
            referralConnection = referralHandler.getReferralConnection( url );
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
     */
    static ReferralsInfo handleReferralException( ReferralException referralException,
        ReferralsInfo initialReferralsInfo ) throws NamingException
    {
        if ( initialReferralsInfo == null )
        {
            initialReferralsInfo = new ReferralsInfo();
        }

        try
        {
            initialReferralsInfo.addReferralUrl( new LdapURL( ( String ) referralException.getReferralInfo() ) );
        }
        catch ( LdapURLEncodingException e )
        {
        }

        while ( referralException.skipReferral() )
        {
            try
            {
                Context ctx = referralException.getReferralContext();
                ctx.list( "" ); //$NON-NLS-1$
            }
            catch ( NamingException ne )
            {
                if ( ne instanceof ReferralException )
                {
                    referralException = ( ReferralException ) ne;
                    try
                    {
                        initialReferralsInfo.addReferralUrl( new LdapURL( ( String ) referralException
                            .getReferralInfo() ) );
                    }
                    catch ( LdapURLEncodingException e )
                    {
                    }
                }
                else
                {
                    break;
                }
            }
        }

        return initialReferralsInfo;
    }


    /**
     * Modify attributes.
     * 
     * @param dn the DN
     * @param modificationItems the modification items
     * @param controls the controls
     * @param monitor the progress monitor
     */
    public void modifyAttributes( final String dn, final ModificationItem[] modificationItems,
        final Control[] controls, final StudioProgressMonitor monitor )
    {
        InnerRunnable runnable = new InnerRunnable()
        {
            private NamingException namingException = null;


            public void run()
            {
                try
                {
                    LdapContext modCtx = context.newInstance( controls );
                    modCtx.addToEnvironment( Context.REFERRAL, "throw" ); //$NON-NLS-1$

                    modCtx.modifyAttributes( new LdapName( dn ), modificationItems );
                }
                catch ( NamingException ne )
                {
                    namingException = ne;
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeModify( connection, dn, modificationItems, controls, namingException );
                }
            }


            public NamingException getException()
            {
                return namingException;
            }


            public NamingEnumeration<SearchResult> getResult()
            {
                return null;
            }


            public void reset()
            {
                namingException = null;
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne.getMessage(), ne );
        }

        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException().getMessage(), runnable.getException() );
        }
    }


    /**
     * Renames an entry.
     * 
     * @param oldDn the old dn
     * @param newDn the new dn
     * @param deleteOldRdn the delete old rdn flag
     * @param controls the controls
     * @param monitor the monitor
     */
    public void rename( final String oldDn, final String newDn, final boolean deleteOldRdn, final Control[] controls,
        final StudioProgressMonitor monitor )
    {
        InnerRunnable runnable = new InnerRunnable()
        {
            private NamingException namingException = null;


            public void run()
            {
                try
                {
                    LdapContext modCtx = context.newInstance( controls );
                    modCtx.addToEnvironment( Context.REFERRAL, "throw" ); //$NON-NLS-1$

                    if ( deleteOldRdn )
                    {
                        modCtx.addToEnvironment( "java.naming.ldap.deleteRDN", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        modCtx.addToEnvironment( "java.naming.ldap.deleteRDN", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    modCtx.rename( new LdapName( oldDn ), new LdapName( newDn ) );
                }
                catch ( NamingException ne )
                {
                    namingException = ne;
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeModDn( connection, oldDn, newDn, deleteOldRdn, controls, namingException );
                }
            }


            public NamingException getException()
            {
                return namingException;
            }


            public NamingEnumeration<SearchResult> getResult()
            {
                return null;
            }


            public void reset()
            {
                namingException = null;
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne.getMessage(), ne );
        }

        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException().getMessage(), runnable.getException() );
        }
    }


    /**
     * Creates an entry.
     * 
     * @param dn the dn
     * @param attributes the attributes
     * @param controls the controls
     * @param monitor the monitor
     */
    public void createEntry( final String dn, final Attributes attributes, final Control[] controls,
        final StudioProgressMonitor monitor )
    {
        InnerRunnable runnable = new InnerRunnable()
        {
            private NamingException namingException = null;


            public void run()
            {

                try
                {
                    LdapContext modCtx = context.newInstance( controls );
                    modCtx.addToEnvironment( Context.REFERRAL, "throw" ); //$NON-NLS-1$

                    modCtx.createSubcontext( new LdapName( dn ), attributes );
                }
                catch ( NamingException ne )
                {
                    namingException = ne;
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeAdd( connection, dn, attributes, controls, namingException );
                }
            }


            public NamingException getException()
            {
                return namingException;
            }


            public NamingEnumeration<SearchResult> getResult()
            {
                return null;
            }


            public void reset()
            {
                namingException = null;
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne.getMessage(), ne );
        }

        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException().getMessage(), runnable.getException() );
        }
    }


    /**
     * Deletes an entry.
     * 
     * @param dn the dn
     * @param controls the controls
     * @param monitor the monitor
     */
    public void deleteEntry( final String dn, final Control[] controls, final StudioProgressMonitor monitor )
    {
        InnerRunnable runnable = new InnerRunnable()
        {
            private NamingException namingException = null;


            public void run()
            {
                try
                {
                    LdapContext modCtx = context.newInstance( controls );
                    modCtx.addToEnvironment( Context.REFERRAL, "throw" ); //$NON-NLS-1$

                    modCtx.destroySubcontext( new LdapName( dn ) );
                }
                catch ( NamingException ne )
                {
                    namingException = ne;
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeDelete( connection, dn, controls, namingException );
                }
            }


            public NamingException getException()
            {
                return namingException;
            }


            public NamingEnumeration<SearchResult> getResult()
            {
                return null;
            }


            public void reset()
            {
                namingException = null;
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( NamingException ne )
        {
            monitor.reportError( ne.getMessage(), ne );
        }

        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException().getMessage(), runnable.getException() );
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
        environment.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" ); //$NON-NLS-1$
        environment.put( "java.naming.ldap.version", "3" ); //$NON-NLS-1$ //$NON-NLS-2$

        // timeouts
        if ( !useLdaps )
        {
            environment.put( "com.sun.jndi.ldap.connect.timeout", "10000" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        environment.put( "com.sun.jndi.dns.timeout.initial", "2000" ); //$NON-NLS-1$ //$NON-NLS-2$
        environment.put( "com.sun.jndi.dns.timeout.retries", "3" ); //$NON-NLS-1$ //$NON-NLS-2$

        // ldaps://
        if ( useLdaps )
        {
            environment.put( Context.PROVIDER_URL, "ldaps://" + host + ":" + port ); //$NON-NLS-1$ //$NON-NLS-2$
            environment.put( Context.SECURITY_PROTOCOL, "ssl" ); //$NON-NLS-1$
            environment.put( "java.naming.ldap.factory.socket", DummySSLSocketFactory.class.getName() ); //$NON-NLS-1$
        }
        else
        {
            environment.put( Context.PROVIDER_URL, "ldap://" + host + ":" + port ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            private NamingException namingException = null;


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
                            tls.setHostnameVerifier( new HostnameVerifier()
                            {
                                public boolean verify( String arg0, SSLSession arg1 )
                                {
                                    return true;
                                }
                            } );
                            tls.negotiate( new DummySSLSocketFactory() );

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


            public NamingException getException()
            {
                return namingException;
            }


            public NamingEnumeration<SearchResult> getResult()
            {
                return null;
            }


            public void reset()
            {
                namingException = null;
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
            authMethod = "none";
            if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SIMPLE )
            {
                authMethod = "simple";
            }
            else if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_DIGEST_MD5 )
            {
                authMethod = "DIGEST-MD5";
                saslRealm = connection.getConnectionParameter().getSaslRealm();
            }
            else if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_CRAM_MD5 )
            {
                authMethod = "CRAM-MD5";
            }
            else if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_GSSAPI )
            {
                authMethod = "GSSAPI";
            }

            // setup credentials
            IAuthHandler authHandler = ConnectionCorePlugin.getDefault().getAuthHandler();
            if ( authHandler == null )
            {
                monitor.reportError( Messages.model__no_auth_handler, new Exception() );
            }
            ICredentials credentials = authHandler.getCredentials( connection.getConnectionParameter() );
            if ( credentials == null )
            {
                monitor.reportError( Messages.model__no_credentials, new Exception() );
            }
            bindPrincipal = credentials.getBindPrincipal();
            bindCredentials = credentials.getBindPassword();

            InnerRunnable runnable = new InnerRunnable()
            {
                private NamingException namingException = null;


                public void run()
                {
                    try
                    {
                        context.removeFromEnvironment( Context.SECURITY_AUTHENTICATION );
                        context.removeFromEnvironment( Context.SECURITY_PRINCIPAL );
                        context.removeFromEnvironment( Context.SECURITY_CREDENTIALS );
                        context.removeFromEnvironment( "java.naming.security.sasl.realm" );

                        context.addToEnvironment( Context.SECURITY_PRINCIPAL, bindPrincipal );
                        context.addToEnvironment( Context.SECURITY_CREDENTIALS, bindCredentials );
                        context.addToEnvironment( Context.SECURITY_AUTHENTICATION, authMethod );

                        if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_DIGEST_MD5 )
                        {
                            context.addToEnvironment( "java.naming.security.sasl.realm", saslRealm );
                        }
                        context.reconnect( context.getConnectControls() );
                    }
                    catch ( NamingException ne )
                    {
                        namingException = ne;
                    }
                }


                public NamingException getException()
                {
                    return namingException;
                }


                public NamingEnumeration<SearchResult> getResult()
                {
                    return null;
                }


                public void reset()
                {
                    namingException = null;
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
            throw new NamingException( "No connection" );
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
            throw new NamingException( "No connection" );
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
                        if ( jobThread.isAlive() )
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

    interface InnerRunnable extends Runnable
    {

        /**
         * Gets the exception.
         * 
         * @return the exception
         */
        NamingException getException();


        /**
         * Gets the result.
         * 
         * @return the result
         */
        NamingEnumeration<SearchResult> getResult();


        /**
         * Reset.
         */
        void reset();
    }

    private List<IJndiLogger> getJndiLoggers()
    {
        return ConnectionCorePlugin.getDefault().getJndiLoggers();
    }
}
