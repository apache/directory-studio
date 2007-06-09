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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import java.util.Hashtable;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InsufficientResourcesException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor;


public class JNDIConnectionContext
{

    private Control[] connCtls;

    private boolean useStartTLS;

    private String authMethod;

    private String principal;

    private String credentials;

    private Hashtable<String, String> environment;

    private InitialLdapContext context;

    private boolean isConnected;

    private Thread jobThread;


    public JNDIConnectionContext() throws NamingException
    {
    }


    public void connect( String host, int port, boolean useLdaps, boolean useStartTLS, Control[] connCtls,
        ExtendedProgressMonitor monitor ) throws NamingException
    {

        this.environment = new Hashtable<String, String>();
        this.environment.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" ); //$NON-NLS-1$
        this.environment.put( "java.naming.ldap.version", "3" ); //$NON-NLS-1$ //$NON-NLS-2$

        // timeouts
        if ( !useLdaps )
        {
            this.environment.put( "com.sun.jndi.ldap.connect.timeout", "10000" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        this.environment.put( "com.sun.jndi.dns.timeout.initial", "2000" ); //$NON-NLS-1$ //$NON-NLS-2$
        this.environment.put( "com.sun.jndi.dns.timeout.retries", "3" ); //$NON-NLS-1$ //$NON-NLS-2$

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

        this.useStartTLS = useStartTLS;

        this.connCtls = connCtls;

        this.context = null;
        this.isConnected = false;
        this.jobThread = null;

        try
        {
            this.doConnect( monitor );
        }
        catch ( NamingException ne )
        {
            this.close();
            throw ne;
        }
    }


    public void bindAnonymous( ExtendedProgressMonitor monitor ) throws NamingException
    {
        this.authMethod = "none"; //$NON-NLS-1$
        this.principal = ""; //$NON-NLS-1$
        this.credentials = ""; //$NON-NLS-1$

        try
        {
            this.doBind( monitor );
        }
        catch ( NamingException ne )
        {
            this.close();
            throw ne;
        }
    }


    public void bindSimple( String user, String password, ExtendedProgressMonitor monitor ) throws NamingException
    {
        this.authMethod = "simple"; //$NON-NLS-1$
        this.principal = user;
        this.credentials = password;

        try
        {
            this.doBind( monitor );
        }
        catch ( NamingException ne )
        {
            this.close();
            throw ne;
        }
    }


    public void close() throws NamingException
    {
        if ( this.jobThread != null )
        {
            Thread t = this.jobThread;
            this.jobThread = null;
            t.interrupt();
        }
        if ( this.context != null )
        {
            this.context.close();
            this.context = null;
        }
        this.isConnected = false;
        System.gc();
    }
    
    
    public NamingEnumeration search( final String searchBase, final String filter, final SearchControls controls,
        final String derefAliasMethod, final String handleReferralsMethod, final Control[] ldapControls,
        final ExtendedProgressMonitor monitor ) throws NamingException
    {
        // start
        InnerRunnable runnable = new InnerRunnable()
        {
            private NamingEnumeration namingEnumeration = null;

            private NamingException namingException = null;


            public void run()
            {

                try
                {

                    // Control[] ldapControls = null;
                    // try {
                    // //Control subEntryControl = new
                    // JNDISubentriesControl();
                    // Control subEntryControl = new
                    // JNDIControl("1.3.6.1.4.1.4203.1.10.1", false, new
                    // byte[]{0x01, 0x01, ( byte ) 0xFF});
                    // ldapControls = new Control[]{subEntryControl};
                    // }
                    // catch(Exception e) {
                    // e.printStackTrace();
                    // }

                    LdapContext searchCtx = context.newInstance( ldapControls );
                    try
                    {
                        searchCtx.addToEnvironment( "java.naming.ldap.derefAliases", derefAliasMethod ); //$NON-NLS-1$
                        searchCtx.addToEnvironment( Context.REFERRAL, handleReferralsMethod );

                    }
                    catch ( NamingException e )
                    {
                        this.namingException = e;
                    }

                    try
                    {
                        this.namingEnumeration = searchCtx.search( searchBase, filter, controls );
                    }
                    catch ( NamingException ne )
                    {
                        this.namingException = ne;
                    }

                }
                catch ( NamingException e )
                {
                    this.namingException = e;
                }

            }


            public NamingException getException()
            {
                return this.namingException;
            }


            public Object getResult()
            {
                return this.namingEnumeration;
            }


            public void reset()
            {
                this.namingEnumeration = null;
                this.namingException = null;
            }

        };
        this.checkConnectionAndRunAndMonitor( runnable, monitor );

        if ( runnable.getException() != null )
        {
            throw runnable.getException();
        }
        else if ( runnable.getResult() != null && runnable.getResult() instanceof NamingEnumeration )
        {
            return ( NamingEnumeration ) runnable.getResult();
        }
        else
        {
            return null;
        }
    }


    void modifyAttributes( final String dn, final ModificationItem[] modificationItems, final Control[] controls,
        final ExtendedProgressMonitor monitor ) throws NamingException
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

                    modCtx.modifyAttributes( dn, modificationItems );
                }
                catch ( NamingException ne )
                {
                    this.namingException = ne;
                }
            }


            public NamingException getException()
            {
                return this.namingException;
            }


            public Object getResult()
            {
                return null;
            }


            public void reset()
            {
                this.namingException = null;
            }
        };
        this.checkConnectionAndRunAndMonitor( runnable, monitor );

        if ( runnable.getException() != null )
        {
            throw runnable.getException();
        }
    }


    void rename( final String oldDn, final String newDn, final boolean deleteOldRdn, final Control[] controls,
        final ExtendedProgressMonitor monitor ) throws NamingException
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

                    modCtx.rename( oldDn, newDn );

                }
                catch ( NamingException ne )
                {
                    this.namingException = ne;
                }
            }


            public NamingException getException()
            {
                return this.namingException;
            }


            public Object getResult()
            {
                return null;
            }


            public void reset()
            {
                this.namingException = null;
            }
        };
        this.checkConnectionAndRunAndMonitor( runnable, monitor );

        if ( runnable.getException() != null )
        {
            throw runnable.getException();
        }
    }


    void createSubcontext( final String dn, final Attributes attributes, final Control[] controls,
        final ExtendedProgressMonitor monitor ) throws NamingException
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

                    modCtx.createSubcontext( dn, attributes );
                }
                catch ( NamingException ne )
                {
                    this.namingException = ne;
                }
            }


            public NamingException getException()
            {
                return this.namingException;
            }


            public Object getResult()
            {
                return null;
            }


            public void reset()
            {
                this.namingException = null;
            }
        };
        this.checkConnectionAndRunAndMonitor( runnable, monitor );

        if ( runnable.getException() != null )
        {
            throw runnable.getException();
        }
    }


    void destroySubcontext( final String dn, final Control[] controls, final ExtendedProgressMonitor monitor )
        throws NamingException
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

                    modCtx.destroySubcontext( dn );
                }
                catch ( NamingException ne )
                {
                    this.namingException = ne;
                }
            }


            public NamingException getException()
            {
                return this.namingException;
            }


            public Object getResult()
            {
                return null;
            }


            public void reset()
            {
                this.namingException = null;
            }
        };
        this.checkConnectionAndRunAndMonitor( runnable, monitor );

        if ( runnable.getException() != null )
        {
            throw runnable.getException();
        }
    }


    private void doConnect( final ExtendedProgressMonitor monitor ) throws NamingException
    {

        this.context = null;
        this.isConnected = true;

        InnerRunnable runnable = new InnerRunnable()
        {
            private NamingException namingException = null;


            public void run()
            {
                try
                {
                    context = new InitialLdapContext( environment, connCtls );

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
                            this.namingException = new NamingException( e.getMessage() != null ? e.getMessage()
                                : "Error while establishing TLS session" ); //$NON-NLS-1$
                            this.namingException.setRootCause( e );
                            context.close();
                        }
                    }

                }
                catch ( NamingException ne )
                {
                    this.namingException = ne;
                }
            }


            public NamingException getException()
            {
                return this.namingException;
            }


            public Object getResult()
            {
                return null;
            }


            public void reset()
            {
                this.namingException = null;
            }
        };
        this.runAndMonitor( runnable, monitor );

        if ( runnable.getException() != null )
        {
            throw runnable.getException();
        }
        else if ( this.context != null )
        {
            // all OK
        }
        else
        {
            throw new NamingException( "???" ); //$NON-NLS-1$
        }
    }


    private void doBind( final ExtendedProgressMonitor monitor ) throws NamingException
    {

        if ( this.context != null && this.isConnected )
        {

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

                        context.addToEnvironment( Context.SECURITY_PRINCIPAL, principal );
                        context.addToEnvironment( Context.SECURITY_CREDENTIALS, credentials );
                        context.addToEnvironment( Context.SECURITY_AUTHENTICATION, authMethod );

                        context.reconnect( context.getConnectControls() );
                    }
                    catch ( NamingException ne )
                    {
                        this.namingException = ne;
                    }
                }


                public NamingException getException()
                {
                    return this.namingException;
                }


                public Object getResult()
                {
                    return null;
                }


                public void reset()
                {
                    this.namingException = null;
                }
            };
            this.runAndMonitor( runnable, monitor );

            if ( runnable.getException() != null )
            {
                throw runnable.getException();
            }
            else if ( this.context != null )
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
            throw new NamingException( BrowserCoreMessages.model__no_connection );
        }
    }


    private void checkConnectionAndRunAndMonitor( final InnerRunnable runnable, final ExtendedProgressMonitor monitor )
        throws NamingException
    {

        // System.out.println("Context: " + this.context);

        // check connection
        if ( !this.isConnected || this.context == null )
        {
            this.doConnect( monitor );
            this.doBind( monitor );
        }
        if ( this.context == null )
        {
            throw new NamingException( BrowserCoreMessages.model__no_connection );
        }

        // loop for reconnection
        for ( int i = 0; i <= 1; i++ )
        {

            this.runAndMonitor( runnable, monitor );

            // check reconnection
            if ( i == 0
                && runnable.getException() != null
                && ( ( runnable.getException() instanceof CommunicationException )
                    || ( runnable.getException() instanceof ServiceUnavailableException ) || ( runnable.getException() instanceof InsufficientResourcesException ) ) )
            {

                this.doConnect( monitor );
                this.doBind( monitor );
                runnable.reset();
            }
            else
            {
                break;
            }
        }
    }


    private void runAndMonitor( final InnerRunnable runnable, final ExtendedProgressMonitor monitor )
        throws CancelException
    {

        if ( !monitor.isCanceled() )
        {

            // monitor
            ExtendedProgressMonitor.CancelListener listener = new ExtendedProgressMonitor.CancelListener()
            {
                public void cancelRequested( ExtendedProgressMonitor.CancelEvent event )
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
            this.jobThread = Thread.currentThread();

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
                this.jobThread = null;
            }

            if ( monitor.isCanceled() )
            {
                throw new CancelException();
            }

        }
    }

    interface InnerRunnable extends Runnable
    {
        public NamingException getException();


        public Object getResult();


        public void reset();
    }

}
