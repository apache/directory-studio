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

package org.apache.directory.studio.connection.core.io;


import java.security.KeyStore;
import java.security.cert.CertPathValidatorException.Reason;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.directory.api.ldap.model.exception.LdapTlsHandshakeExceptionClassifier;
import org.apache.directory.api.ldap.model.exception.LdapTlsHandshakeFailCause;
import org.apache.directory.api.ldap.model.exception.LdapTlsHandshakeFailCause.LdapApiReason;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ICertificateHandler;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;


/**
 * A wrapper for a real {@link TrustManager}. If the certificate chain is not trusted
 * then ask the user.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioTrustManager implements X509TrustManager
{
    private X509TrustManager jvmTrustManager;
    private String host;


    /**
     * Creates a new instance of StudioTrustManager.
     * 
     * @param jvmTrustManager the JVM trust manager
     * 
     * @throws Exception the exception
     */
    public StudioTrustManager( X509TrustManager jvmTrustManager ) throws Exception
    {
        this.jvmTrustManager = jvmTrustManager;
    }


    /**
     * Sets the host, used to verify the hostname of the certificate.
     * 
     * @param host the new host
     */
    public void setHost( String host )
    {
        this.host = host;
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
        // check permanent trusted certificates, return on success
        try
        {
            X509TrustManager permanentTrustManager = getPermanentTrustManager();
            if ( permanentTrustManager != null )
            {
                permanentTrustManager.checkServerTrusted( chain, authType );
                return;
            }
        }
        catch ( CertificateException ce )
        {
        }

        // check temporary trusted certificates, return on success
        try
        {
            X509TrustManager sessionTrustManager = getSessionTrustManager();
            if ( sessionTrustManager != null )
            {
                sessionTrustManager.checkServerTrusted( chain, authType );
                return;
            }
        }
        catch ( CertificateException ce )
        {
        }

        // below here no manually trusted certificate (either permanent or temporary) matched
        Map<Reason, LdapTlsHandshakeFailCause> failCauses = new LinkedHashMap<>();
        CertificateException certificateException = null;

        // perform trust check of JVM trust manager
        try
        {
            jvmTrustManager.checkServerTrusted( chain, authType );
        }
        catch ( CertificateException ce )
        {
            certificateException = ce;
            LdapTlsHandshakeFailCause failCause = LdapTlsHandshakeExceptionClassifier.classify( ce, chain[0] );
            failCauses.put( failCause.getReason(), failCause );
        }

        // perform a certificate validity check
        try
        {
            chain[0].checkValidity();
        }
        catch ( CertificateException ce )
        {
            certificateException = ce;
            LdapTlsHandshakeFailCause failCause = LdapTlsHandshakeExceptionClassifier.classify( ce, chain[0] );
            failCauses.put( failCause.getReason(), failCause );
        }

        // perform host name verification
        try
        {
            DefaultHostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();
            hostnameVerifier.verify( host, chain[0] );
        }
        catch ( SSLException ssle )
        {
            certificateException = new CertificateException( ssle );
            LdapTlsHandshakeFailCause failCause = new LdapTlsHandshakeFailCause( ssle, ssle,
                LdapApiReason.HOST_NAME_VERIFICATION_FAILED, "Hostname verification failed" );
            failCauses.put( failCause.getReason(), failCause );
        }

        if ( !failCauses.isEmpty() )
        {
            // either trust check or host name verification
            // ask for confirmation
            ICertificateHandler ch = ConnectionCorePlugin.getDefault().getCertificateHandler();
            ICertificateHandler.TrustLevel trustLevel = ch.verifyTrustLevel( host, chain, failCauses.values() );
            switch ( trustLevel )
            {
                case Permanent:
                    ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().addCertificate( chain[0] );
                    break;
                case Session:
                    ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().addCertificate( chain[0] );
                    break;
                case Not:
                    throw certificateException;
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
     * @return the permanent trust manager, null if the trust store is empty
     * 
     * @throws CertificateException the certificate exception
     */
    private X509TrustManager getPermanentTrustManager() throws CertificateException
    {
        KeyStore permanentTrustStore = ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getKeyStore();
        X509TrustManager permanentTrustManager = getTrustManager( permanentTrustStore );
        return permanentTrustManager;
    }


    /**
     * Gets the session trust manager, based on the session trust store.
     * 
     * @return the session trust manager, null if the trust store is empty
     * 
     * @throws CertificateException the certificate exception
     */
    private X509TrustManager getSessionTrustManager() throws CertificateException
    {
        KeyStore sessionTrustStore = ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getKeyStore();
        X509TrustManager sessionTrustManager = getTrustManager( sessionTrustStore );
        return sessionTrustManager;
    }


    private X509TrustManager getTrustManager( KeyStore trustStore ) throws CertificateException
    {
        try
        {
            Enumeration<String> aliases = trustStore.aliases();
            if ( aliases.hasMoreElements() )
            {
                TrustManagerFactory factory = TrustManagerFactory.getInstance( TrustManagerFactory
                    .getDefaultAlgorithm() );
                factory.init( trustStore );
                TrustManager[] permanentTrustManagers = factory.getTrustManagers();
                TrustManager permanentTrustManager = permanentTrustManagers[0];
                return ( X509TrustManager ) permanentTrustManager;
            }
        }
        catch ( Exception e )
        {
            throw new CertificateException( Messages.StudioTrustManager_CantCreateTrustManager, e );
        }

        return null;
    }

}
