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

package org.apache.directory.studio.test.integration.ui;


import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.x509.X509V1CertificateGenerator;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CertificateUtils
{

    public static File createCertificateInKeyStoreFile( String issuerDN, String subjectDN, Date startDate,
        Date expiryDate ) throws Exception
    {
        KeyPair keypair = createKeyPair();
        X509Certificate cert = createCertificate( issuerDN, subjectDN, startDate, expiryDate, keypair );

        // write key store file
        File ksFile = File.createTempFile( "testStore", "ks" );
        KeyStore ks = KeyStore.getInstance( KeyStore.getDefaultType() );
        ks.load( null, null );
        ks.setCertificateEntry( "apacheds", cert );
        ks.setKeyEntry( "apacheds", keypair.getPrivate(), "changeit".toCharArray(), new Certificate[]
            { cert } );
        ks.store( new FileOutputStream( ksFile ), "changeit".toCharArray() );

        return ksFile;
    }


    public static X509Certificate createCertificate( String issuerDN, String subjectDN, Date startDate, Date expiryDate,
        KeyPair keypair ) throws CertificateEncodingException, NoSuchProviderException, NoSuchAlgorithmException,
            SignatureException, InvalidKeyException
    {
        BigInteger serialNumber = BigInteger.valueOf( System.currentTimeMillis() );
        X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
        X500Principal issuerName = new X500Principal( issuerDN );
        X500Principal subjectName = new X500Principal( subjectDN );
        certGen.setSerialNumber( serialNumber );
        certGen.setIssuerDN( issuerName );
        certGen.setNotBefore( startDate );
        certGen.setNotAfter( expiryDate );
        certGen.setSubjectDN( subjectName );
        certGen.setPublicKey( keypair.getPublic() );
        certGen.setSignatureAlgorithm( "SHA1WithRSA" );
        X509Certificate cert = certGen.generate( keypair.getPrivate(), "BC" );
        return cert;
    }


    public static KeyPair createKeyPair() throws NoSuchAlgorithmException
    {
        KeyPairGenerator generator = KeyPairGenerator.getInstance( "RSA" );
        generator.initialize( 1024 );
        KeyPair keypair = generator.genKeyPair();
        return keypair;
    }

}
