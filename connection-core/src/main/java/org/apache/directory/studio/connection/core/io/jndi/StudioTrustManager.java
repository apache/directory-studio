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


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
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
            // ask for confirmation
            ICertificateHandler ch = ConnectionCorePlugin.getDefault().getCertificateHandler();
            ICertificateHandler.TrustLevel trustLevel = ch.verifyTrustLevel( chain );
            switch ( trustLevel )
            {
                case Permanent:
                    // TODO: put to permanent trust store
                    break;
                case Session:
                    // TODO: put to session trust store???
                    break;
                case Not:
                    throw new CertificateException( Messages.error__untrusted_certificate, e1 );
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

}
