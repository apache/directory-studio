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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ICertificateHandler;
import org.apache.directory.studio.connection.core.Messages;


/**
 * A wrapper for a real {@link TrustManager}. If the certificate chain is not trusted
 * then ask the user.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
class StudioTrustManager implements X509TrustManager
{
    private static final char[] PERMANENT_TRUST_STORE_PASSWORD = "changeit".toCharArray(); //$NON-NLS-1$
    private static final String PERMANENT_TRUST_STORE = "permanent.jks"; //$NON-NLS-1$
    private X509TrustManager jvmTrustManager;


    /**
     * Creates a new instance of StudioTrustManager.
     * 
     * @param jvmTrustManager the JVM trust manager
     * 
     * @throws Exception the exception
     */
    StudioTrustManager( X509TrustManager jvmTrustManager ) throws Exception
    {
        this.jvmTrustManager = jvmTrustManager;
    }


    /**
     * {@inheritDoc}
     */
    public void checkClientTrusted( X509Certificate[] chain, String authType ) throws CertificateException
    {
        jvmTrustManager.checkClientTrusted( chain, authType );
    }


    /**
     * {@inheritDoc}
     */
    public void checkServerTrusted( X509Certificate[] chain, String authType ) throws CertificateException
    {
        try
        {
            jvmTrustManager.checkServerTrusted( chain, authType );
        }
        catch ( CertificateException e1 )
        {
            try
            {
                X509TrustManager permanentTrustManager = getPermanentTrustManager();
                if ( permanentTrustManager == null )
                {
                    throw e1;
                }
                permanentTrustManager.checkServerTrusted( chain, authType );
            }
            catch ( CertificateException e2 )
            {
                // ask for confirmation
                ICertificateHandler ch = ConnectionCorePlugin.getDefault().getCertificateHandler();
                ICertificateHandler.TrustLevel trustLevel = ch.verifyTrustLevel( chain );
                switch ( trustLevel )
                {
                    case Permanent:
                        addToPermanentTrustStore( chain );
                        break;
                    case Session:
                        // TODO: put to session trust store???
                        break;
                    case Not:
                        throw new CertificateException( Messages.error__untrusted_certificate, e1 );
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public X509Certificate[] getAcceptedIssuers()
    {
        return jvmTrustManager.getAcceptedIssuers();
    }


    /**
     * Gets the permanent trust manager, based on the permanent trust store.
     * 
     * @return the permanent trust manager
     * 
     * @throws CertificateException the certificate exception
     */
    private X509TrustManager getPermanentTrustManager() throws CertificateException
    {
        KeyStore permanentKeyStore = loadPermanentTrustStore();
        try
        {
            Enumeration<String> aliases = permanentKeyStore.aliases();
            if ( aliases.hasMoreElements() )
            {
                TrustManagerFactory factory = TrustManagerFactory.getInstance( TrustManagerFactory
                    .getDefaultAlgorithm() );
                factory.init( permanentKeyStore );
                TrustManager[] permanentTrustManagers = factory.getTrustManagers();
                TrustManager permanentTrustManager = permanentTrustManagers[0];
                return ( X509TrustManager ) permanentTrustManager;
            }
        }
        catch ( Exception e )
        {
            throw new CertificateException( Messages.StudioTrustManager_CantCreatePermanentTrustManager, e );
        }

        return null;
    }


    /**
     * Loads the permanent trust store.
     * 
     * @return the permanent trust store
     */
    private KeyStore loadPermanentTrustStore() throws CertificateException
    {
        try
        {
            KeyStore permanentKeyStore = KeyStore.getInstance( "JKS" ); //$NON-NLS-1$
            File file = ConnectionCorePlugin.getDefault().getStateLocation().append( PERMANENT_TRUST_STORE ).toFile();
            if ( file.exists() && file.isFile() && file.canRead() )
            {
                permanentKeyStore.load( new FileInputStream( file ), PERMANENT_TRUST_STORE_PASSWORD );
            }
            else
            {
                permanentKeyStore.load( null, null );
            }

            return permanentKeyStore;
        }
        catch ( Exception e )
        {
            throw new CertificateException( Messages.StudioTrustManager_CantLoadPermanentTrustStore, e );
        }
    }


    /**
     * Adds the certificate to the permanent trust store.
     * 
     * @param chain the certificate chain
     */
    private void addToPermanentTrustStore( X509Certificate[] chain ) throws CertificateException
    {
        try
        {
            KeyStore permanentKeyStore = loadPermanentTrustStore();
            String alias = chain[0].getSubjectX500Principal().getName();
            permanentKeyStore.setCertificateEntry( alias, chain[0] );
            File file = ConnectionCorePlugin.getDefault().getStateLocation().append( PERMANENT_TRUST_STORE ).toFile();
            permanentKeyStore.store( new FileOutputStream( file ), PERMANENT_TRUST_STORE_PASSWORD );
        }
        catch ( Exception e )
        {
            throw new CertificateException( Messages.StudioTrustManager_CantAddCertificateToPermanentTrustStore, e );
        }
    }

}
