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


import java.util.Collection;
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
import javax.naming.ldap.ManageReferralControl;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.shared.ldap.codec.util.LdapURLEncodingException;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.IAuthHandler;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.ICredentials;
import org.apache.directory.studio.connection.core.IJndiLogger;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.jndi.ReferralsInfo.UrlAndDn;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.eclipse.core.runtime.Preferences;


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
 * @version $Rev$, $Date$
 */
public class JNDIConnectionWrapper implements ConnectionWrapper
{

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
    public static final String REFERRAL_THROW = "throw";

    /** JNDI constant for "follow" referrals handling */
    public static final String REFERRAL_FOLLOW = "follow";

    /** JNDI constant for "ignore" referrals handling */
    public static final String REFERRAL_IGNORE = "ignore";

    /** JNDI constant for "searching" alias dereferencing */
    public static final String ALIAS_SEARCHING = "searching";

    /** JNDI constant for "finding" alias dereferencing */
    public static final String ALIAS_FINDING = "finding";

    /** JNDI constant for "always" alias dereferencing */
    public static final String ALIAS_ALWAYS = "always";

    /** JNDI constant for "never" alias dereferencing */
    public static final String ALIAS_NEVER = "never";


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
            monitor.reportError( ne.getMessage(), ne );
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
            monitor.reportError( ne.getMessage(), ne );
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
        String binaryAttributesString = "";
        for ( String string : binaryAttributes )
        {
            binaryAttributesString += string + " ";
        }

        if ( environment != null )
        {
            environment.put( "java.naming.ldap.attributes.binary", binaryAttributesString );
        }

        if ( context != null )
        {
            try
            {
                context.addToEnvironment( "java.naming.ldap.attributes.binary", binaryAttributesString );
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
                // add ManageDsaIT control
                Control[] localControls = addManageDsaItControls( controls, referralsHandlingMethod );

                try
                {
                    // create the search context
                    LdapContext searchCtx = context.newInstance( localControls );

                    // translate alias dereferencing method
                    searchCtx.addToEnvironment(
                        "java.naming.ldap.derefAliases", translateDerefAliasMethod( aliasesDereferencingMethod ) ); //$NON-NLS-1$

                    // use "throw" as we handle referrals manually
                    searchCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // perform the search
                    NamingEnumeration<SearchResult> ne = searchCtx.search( getSaveJndiName( searchBase ), filter,
                        searchControls );
                    namingEnumeration = new StudioNamingEnumeration( connection, searchCtx, ne, searchBase, filter,
                        searchControls, aliasesDereferencingMethod, referralsHandlingMethod, controls, requestNum,
                        monitor, referralsInfo );
                }
                catch ( PartialResultException pre )
                {
                    // ignore exception if referrals handling method is IGNORE
                    // report exception if referrals handling method is FOLLOW or MANGAGE
                    if ( referralsHandlingMethod == ReferralHandlingMethod.FOLLOW
                        || referralsHandlingMethod == ReferralHandlingMethod.MANAGE )
                    {
                        namingException = pre;
                    }
                }
                catch ( ReferralException re )
                {
                    // ignore exception if referrals handling method is IGNORE
                    // report exception if referrals handling method is MANGAGE
                    // follow referral if referrals handling method is FOLLOW
                    if ( referralsHandlingMethod == ReferralHandlingMethod.MANAGE )
                    {
                        namingException = re;
                    }
                    else if ( referralsHandlingMethod == ReferralHandlingMethod.FOLLOW )
                    {
                        try
                        {
                            ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                            UrlAndDn urlAndDn = newReferralsInfo.getNext();
                            if ( urlAndDn != null )
                            {
                                LdapURL url = urlAndDn.getUrl();
                                Connection referralConnection = getReferralConnection( url, monitor, this );
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
                                    referralSearchControls.setReturningAttributes( url.getAttributes() != null
                                        && url.getAttributes().size() > 0 ? url.getAttributes().toArray(
                                        new String[url.getAttributes().size()] ) : searchControls
                                        .getReturningAttributes() );
                                    referralSearchControls.setCountLimit( searchControls.getCountLimit() );
                                    referralSearchControls.setTimeLimit( searchControls.getTimeLimit() );
                                    referralSearchControls.setDerefLinkFlag( searchControls.getDerefLinkFlag() );
                                    referralSearchControls.setReturningObjFlag( searchControls.getReturningObjFlag() );

                                    namingEnumeration = ( StudioNamingEnumeration ) referralConnection
                                        .getJNDIConnectionWrapper().search( referralSearchBase, referralFilter,
                                            referralSearchControls, aliasesDereferencingMethod,
                                            referralsHandlingMethod, controls, monitor, newReferralsInfo );
                                }
                            }
                        }
                        catch ( NamingException ne )
                        {
                            namingException = ne;
                        }
                    }
                }
                catch ( NamingException e )
                {
                    // report each other naming exception
                    namingException = e;
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    if ( namingEnumeration != null )
                    {
                        logger.logSearchRequest( connection, searchBase, filter, searchControls,
                            aliasesDereferencingMethod, localControls, requestNum, namingException );
                    }
                    else
                    {
                        logger.logSearchRequest( connection, searchBase, filter, searchControls,
                            aliasesDereferencingMethod, localControls, requestNum, namingException );
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
            return new StudioNamingEnumeration( connection, null, null, searchBase, filter, searchControls,
                aliasesDereferencingMethod, referralsHandlingMethod, controls, requestNum, monitor, referralsInfo );
        }
    }


    /**
     * Modifies attributes of an entry.
     * 
     * @param dn the DN
     * @param modificationItems the modification items
     * @param referralsHandlingMethod the referrals handling method
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void modifyEntry( final String dn, final ModificationItem[] modificationItems,
        final ReferralHandlingMethod referralsHandlingMethod, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor.reportError( "Connection '" + connection.getName() + "' is read only." );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                // add ManageDsaIT control
                Control[] localControls = addManageDsaItControls( controls, referralsHandlingMethod );

                try
                {
                    // create modify context
                    LdapContext modCtx = context.newInstance( localControls );

                    // use "throw" as we handle referrals manually
                    modCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // perform modification
                    modCtx.modifyAttributes( getSaveJndiName( dn ), modificationItems );
                }
                catch ( ReferralException re )
                {
                    try
                    {
                        ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                        UrlAndDn urlAndDn = newReferralsInfo.getNext();
                        if ( urlAndDn != null )
                        {
                            Connection referralConnection = getReferralConnection( urlAndDn.getUrl(), monitor, this );
                            if ( referralConnection != null )
                            {
                                String referralDn = urlAndDn.getDn() != null && !urlAndDn.getDn().isEmpty() ? urlAndDn
                                    .getDn().getUpName() : dn;

                                referralConnection.getJNDIConnectionWrapper().modifyEntry( referralDn,
                                    modificationItems, referralsHandlingMethod, controls, monitor, newReferralsInfo );
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

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeModify( connection, dn, modificationItems, localControls, namingException );
                }
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
     * @param oldDn the old DN
     * @param newDn the new DN
     * @param deleteOldRdn true to delete the old RDN
     * @param referralsHandlingMethod the referrals handling method
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void renameEntry( final String oldDn, final String newDn, final boolean deleteOldRdn,
        final ReferralHandlingMethod referralsHandlingMethod, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor.reportError( "Connection '" + connection.getName() + "' is read only." );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                // add ManageDsaIT control
                Control[] localControls = addManageDsaItControls( controls, referralsHandlingMethod );

                try
                {
                    // create modify context
                    LdapContext modCtx = context.newInstance( localControls );

                    // use "throw" as we handle referrals manually
                    modCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // delete old RDN
                    if ( deleteOldRdn )
                    {
                        modCtx.addToEnvironment( "java.naming.ldap.deleteRDN", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        modCtx.addToEnvironment( "java.naming.ldap.deleteRDN", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    // rename entry
                    modCtx.rename( getSaveJndiName( oldDn ), getSaveJndiName( newDn ) );
                }
                catch ( ReferralException re )
                {
                    try
                    {
                        ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                        UrlAndDn urlAndDn = newReferralsInfo.getNext();
                        if ( urlAndDn != null )
                        {
                            Connection referralConnection = getReferralConnection( urlAndDn.getUrl(), monitor, this );
                            if ( referralConnection != null )
                            {
                                //                                String referralDn = url.getDn() != null && !url.getDn().isEmpty() ? url.getDn()
                                //                                    .getUpName() : dn;
                                // TODO: referral DN???
                                referralConnection.getJNDIConnectionWrapper().renameEntry( oldDn, newDn, deleteOldRdn,
                                    referralsHandlingMethod, controls, monitor, newReferralsInfo );
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

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeModDn( connection, oldDn, newDn, deleteOldRdn, localControls, namingException );
                }
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
     * @param dn the entry's DN
     * @param attributes the entry's attributes
     * @param referralsHandlingMethod the referrals handling method
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void createEntry( final String dn, final Attributes attributes,
        final ReferralHandlingMethod referralsHandlingMethod, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor.reportError( "Connection '" + connection.getName() + "' is read only." );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                // add ManageDsaIT control
                Control[] localControls = addManageDsaItControls( controls, referralsHandlingMethod );

                try
                {
                    // create modify context
                    LdapContext modCtx = context.newInstance( localControls );

                    // use "throw" as we handle referrals manually
                    modCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // create entry
                    modCtx.createSubcontext( getSaveJndiName( dn ), attributes );
                }
                catch ( ReferralException re )
                {
                    try
                    {
                        ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                        UrlAndDn urlAndDn = newReferralsInfo.getNext();
                        if ( urlAndDn != null )
                        {
                            Connection referralConnection = getReferralConnection( urlAndDn.getUrl(), monitor, this );
                            if ( referralConnection != null )
                            {
                                String referralDn = urlAndDn.getDn() != null && !urlAndDn.getDn().isEmpty() ? urlAndDn
                                    .getDn().getUpName() : dn;

                                referralConnection.getJNDIConnectionWrapper().createEntry( referralDn, attributes,
                                    referralsHandlingMethod, controls, monitor, newReferralsInfo );
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

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeAdd( connection, dn, attributes, localControls, namingException );
                }
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
     * @param dn the DN of the entry to delete
     * @param referralsHandlingMethod the referrals handling method
     * @param controls the controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public void deleteEntry( final String dn, final ReferralHandlingMethod referralsHandlingMethod,
        final Control[] controls, final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor.reportError( "Connection '" + connection.getName() + "' is read only." );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                // add ManageDsaIT control
                Control[] localControls = addManageDsaItControls( controls, referralsHandlingMethod );

                try
                {
                    // create modify context
                    LdapContext modCtx = context.newInstance( localControls );

                    // use "throw" as we handle referrals manually
                    modCtx.addToEnvironment( Context.REFERRAL, REFERRAL_THROW );

                    // delete entry
                    modCtx.destroySubcontext( getSaveJndiName( dn ) );
                }
                catch ( ReferralException re )
                {
                    try
                    {
                        ReferralsInfo newReferralsInfo = handleReferralException( re, referralsInfo );
                        UrlAndDn urlAndDn = newReferralsInfo.getNext();
                        if ( urlAndDn != null )
                        {
                            Connection referralConnection = getReferralConnection( urlAndDn.getUrl(), monitor, this );
                            if ( referralConnection != null )
                            {
                                String referralDn = urlAndDn.getDn() != null && !urlAndDn.getDn().isEmpty() ? urlAndDn
                                    .getDn().getUpName() : dn;

                                referralConnection.getJNDIConnectionWrapper().deleteEntry( referralDn,
                                    referralsHandlingMethod, controls, monitor, newReferralsInfo );
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

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeDelete( connection, dn, localControls, namingException );
                }
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
        Preferences preferences = ConnectionCorePlugin.getDefault().getPluginPreferences();
        String ldapCtxFactory = preferences.getString( ConnectionCoreConstants.PREFERENCE_LDAP_CONTEXT_FACTORY );
        environment.put( Context.INITIAL_CONTEXT_FACTORY, ldapCtxFactory ); //$NON-NLS-1$
        environment.put( "java.naming.ldap.version", "3" ); //$NON-NLS-1$ //$NON-NLS-2$

        // timeouts
        // Don't use a timeout when using ldaps: JNDI throws a SocketException 
        // when setting a timeout on SSL connections.
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
                        context.removeFromEnvironment( "java.naming.security.sasl.realm" );

                        context.addToEnvironment( Context.SECURITY_PRINCIPAL, bindPrincipal );
                        context.addToEnvironment( Context.SECURITY_CREDENTIALS, bindCredentials );
                        context.addToEnvironment( Context.SECURITY_AUTHENTICATION, authMethod );

                        if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_DIGEST_MD5
                            && StringUtils.isNotEmpty( saslRealm ) )
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

    abstract class InnerRunnable implements Runnable
    {
        protected StudioNamingEnumeration namingEnumeration = null;
        protected NamingException namingException = null;


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
         * Reset.
         */
        public void reset()
        {
            namingEnumeration = null;
            namingException = null;
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
        String m = ALIAS_ALWAYS; //$NON-NLS-1$

        switch ( aliasDereferencingMethod )
        {
            case NEVER:
                m = ALIAS_NEVER; //$NON-NLS-1$
                break;
            case ALWAYS:
                m = ALIAS_ALWAYS; //$NON-NLS-1$
                break;
            case FINDING:
                m = ALIAS_FINDING; //$NON-NLS-1$
                break;
            case SEARCH:
                m = ALIAS_SEARCHING; //$NON-NLS-1$
                break;
        }

        return m;
    }


    /**
     * Adds the ManageDsaIT controls if the referrals handling method is MANAGE and 
     * if the current controls don't contain the ManageDsaIT control.
     * 
     * @param currentControls the current controls
     * @param referralsHandlingMethod the referrals handling method
     * 
     * @return the new controls
     */
    private Control[] addManageDsaItControls( final Control[] currentControls,
        final ReferralHandlingMethod referralsHandlingMethod )
    {
        Control[] localControls = currentControls;
        if ( referralsHandlingMethod == ReferralHandlingMethod.MANAGE )
        {
            if ( currentControls == null )
            {
                localControls = new Control[]
                    { new ManageReferralControl( false ) };
            }
            else
            {
                boolean manageDsaItControlAlreadyContained = false;
                for ( Control control : currentControls )
                {
                    if ( ManageReferralControl.OID.equals( control.getID() ) )
                    {
                        manageDsaItControlAlreadyContained = true;
                        break;
                    }
                }
                if ( !manageDsaItControlAlreadyContained )
                {
                    localControls = new Control[currentControls.length + 1];
                    System.arraycopy( currentControls, 0, localControls, 0, currentControls.length );
                    localControls[localControls.length - 1] = new ManageReferralControl( false );
                }
            }
        }
        return localControls;
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
    private Name getSaveJndiName( String name ) throws InvalidNameException
    {
        if ( name == null || "".equals( name ) )
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
    static Connection getReferralConnection( LdapURL url, StudioProgressMonitor monitor, Object source )
    {
        Connection referralConnection = null;
        IReferralHandler referralHandler = ConnectionCorePlugin.getDefault().getReferralHandler();
        if ( referralHandler != null )
        {
            referralConnection = referralHandler.getReferralConnection( url );

            if ( referralConnection != null && !referralConnection.getJNDIConnectionWrapper().isConnected() )
            {
                referralConnection.getJNDIConnectionWrapper().connect( monitor );
                referralConnection.getJNDIConnectionWrapper().bind( monitor );
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

        try
        {
            String info = ( String ) referralException.getReferralInfo();
            String name = referralException.getRemainingName().toString();
            LdapURL url = new LdapURL( info );
            LdapDN dn = new LdapDN( name );
            initialReferralsInfo.addReferralUrl( url, dn );
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
                        String info = ( String ) referralException.getReferralInfo();
                        String name = referralException.getRemainingName().toString();
                        LdapURL url = new LdapURL( info );
                        LdapDN dn = new LdapDN( name );
                        initialReferralsInfo.addReferralUrl( url, dn );
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

}
