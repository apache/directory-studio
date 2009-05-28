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


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * A {@link SSLSocketFactory} that uses a custom {@link TrustManager} ({@link StudioTrustManager}).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StudioSSLSocketFactory extends SSLSocketFactory
{

    /** The default instance. */
    private static StudioSSLSocketFactory instance;


    /**
     * Gets the default instance.
     * 
     * Note: This method is invoked from the JNDI (Sun) when 
     * creating a ldaps:// connection. Must be public static!
     * 
     * @return the default instance
     */
    public static SSLSocketFactory getDefault()
    {
        if ( instance == null )
        {
            instance = new StudioSSLSocketFactory();
        }
        return instance;
    }

    /** The delegate. */
    private SSLSocketFactory delegate;

    /** The trust managers. */
    private StudioTrustManager[] trustManagers;


    /**
     * Creates a new instance of StudioSSLSocketFactory.
     * 
     * Note: This method is invoked from the JNDI (Apache Harmony) when 
     * creating a ldaps:// connection. Must be public!
     */
    public StudioSSLSocketFactory()
    {
        try
        {
            // get default trust managers (using JVM "cacerts" key store)
            TrustManagerFactory factory = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
            factory.init( ( KeyStore ) null );
            TrustManager[] defaultTrustManagers = factory.getTrustManagers();

            // create wrappers around the trust managers
            trustManagers = new StudioTrustManager[defaultTrustManagers.length];
            for ( int i = 0; i < defaultTrustManagers.length; i++ )
            {
                trustManagers[i] = new StudioTrustManager( ( X509TrustManager ) defaultTrustManagers[i] );
            }

            // create the real socket factory
            SSLContext sc = SSLContext.getInstance( "TLS" ); //$NON-NLS-1$
            sc.init( null, trustManagers, null );
            delegate = sc.getSocketFactory();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String[] getDefaultCipherSuites()
    {
        return delegate.getDefaultCipherSuites();
    }


    /**
     * {@inheritDoc}
     */
    public String[] getSupportedCipherSuites()
    {
        return delegate.getSupportedCipherSuites();
    }


    /**
     * {@inheritDoc}
     */
    public Socket createSocket( Socket s, String host, int port, boolean autoClose ) throws IOException
    {
        try
        {
            updateTrustManagers( host );
            return delegate.createSocket( s, host, port, autoClose );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Socket createSocket( String host, int port ) throws IOException, UnknownHostException
    {
        try
        {
            updateTrustManagers( host );
            return delegate.createSocket( host, port );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Socket createSocket( InetAddress host, int port ) throws IOException
    {
        try
        {
            updateTrustManagers( host );
            return delegate.createSocket( host, port );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Socket createSocket( String host, int port, InetAddress localHost, int localPort ) throws IOException,
        UnknownHostException
    {
        try
        {
            updateTrustManagers( host );
            return delegate.createSocket( host, port, localHost, localPort );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Socket createSocket( InetAddress address, int port, InetAddress localAddress, int localPort )
        throws IOException
    {
        try
        {
            updateTrustManagers( address );
            return delegate.createSocket( address, port, localAddress, localPort );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            throw e;
        }
    }


    private void updateTrustManagers( InetAddress address )
    {
        updateTrustManagers( address.getHostName() );
    }


    private void updateTrustManagers( String host )
    {
        for ( StudioTrustManager trustManager : trustManagers )
        {
            trustManager.setHost( host );
        }
    }
}
