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
package org.apache.directory.studio.apacheds.configuration.v2.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The supported ciphers, and their status (enabled or not) for each Java version. We use a Boolean
 * object to store three states :
 * <ul>
 * <li>Boolean.TRUE : the cipher is enabled for this java version</li>
 * <li>Boolean.FALSE : the cipher is disabled for this java version</li>
 * <li>null : the cipher is not supportted by this java version</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum SupportedCipher
{
    // Enabled ciphers
    TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384( "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384( "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", Boolean.TRUE, Boolean.TRUE),
    TLS_RSA_WITH_AES_256_CBC_SHA256( "TLS_RSA_WITH_AES_256_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384( "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384( "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", Boolean.TRUE, Boolean.TRUE),
    TLS_DHE_RSA_WITH_AES_256_CBC_SHA256( "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_DHE_DSS_WITH_AES_256_CBC_SHA256( "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA( "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA( "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_RSA_WITH_AES_256_CBC_SHA( "TLS_RSA_WITH_AES_256_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA( "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_RSA_WITH_AES_256_CBC_SHA( "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_DHE_RSA_WITH_AES_256_CBC_SHA( "TLS_DHE_RSA_WITH_AES_256_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_DHE_DSS_WITH_AES_256_CBC_SHA( "TLS_DHE_DSS_WITH_AES_256_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256( "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256( "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_RSA_WITH_AES_128_CBC_SHA256( "TLS_RSA_WITH_AES_128_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256( "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256( "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_DHE_RSA_WITH_AES_128_CBC_SHA256( "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_DHE_DSS_WITH_AES_128_CBC_SHA256( "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA( "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA( "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_RSA_WITH_AES_128_CBC_SHA( "TLS_RSA_WITH_AES_128_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA( "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_RSA_WITH_AES_128_CBC_SHA( "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_DHE_RSA_WITH_AES_128_CBC_SHA( "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_DHE_DSS_WITH_AES_128_CBC_SHA( "TLS_DHE_DSS_WITH_AES_128_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_ECDSA_WITH_RC4_128_SHA( "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_RSA_WITH_RC4_128_SHA( "TLS_ECDHE_RSA_WITH_RC4_128_SHA", Boolean.TRUE, Boolean.TRUE),
    SSL_RSA_WITH_RC4_128_SHA( "SSL_RSA_WITH_RC4_128_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_ECDSA_WITH_RC4_128_SHA( "TLS_ECDH_ECDSA_WITH_RC4_128_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_RSA_WITH_RC4_128_SHA( "TLS_ECDH_RSA_WITH_RC4_128_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384( "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", null, Boolean.TRUE),
    TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256( "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", null, Boolean.TRUE),
    TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384( "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", null, Boolean.TRUE),
    TLS_RSA_WITH_AES_256_GCM_SHA384( "TLS_RSA_WITH_AES_256_GCM_SHA384", null, Boolean.TRUE),
    TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384( "TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", null, Boolean.TRUE),
    TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384( "TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", null, Boolean.TRUE),
    TLS_DHE_RSA_WITH_AES_256_GCM_SHA384( "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", null, Boolean.TRUE),
    TLS_DHE_DSS_WITH_AES_256_GCM_SHA384( "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", null, Boolean.TRUE),
    TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256( "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", null, Boolean.TRUE),
    TLS_RSA_WITH_AES_128_GCM_SHA256( "TLS_RSA_WITH_AES_128_GCM_SHA256", null, Boolean.TRUE),
    TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256( "TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", null, Boolean.TRUE),
    TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256( "TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", null, Boolean.TRUE),
    TLS_DHE_RSA_WITH_AES_128_GCM_SHA256( "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", null, Boolean.TRUE),
    TLS_DHE_DSS_WITH_AES_128_GCM_SHA256( "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", null, Boolean.TRUE),
    TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA( "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA( "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    SSL_RSA_WITH_3DES_EDE_CBC_SHA( "SSL_RSA_WITH_3DES_EDE_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA( "TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA( "TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA( "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA( "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA", Boolean.TRUE, Boolean.TRUE),
    SSL_RSA_WITH_RC4_128_MD5( "SSL_RSA_WITH_RC4_128_MD5", Boolean.TRUE, Boolean.TRUE),
    TLS_EMPTY_RENEGOTIATION_INFO_SCSV( "TLS_EMPTY_RENEGOTIATION_INFO_SCSV", Boolean.TRUE, Boolean.TRUE),

    // Disabled ciphers
    TLS_DH_anon_WITH_AES_256_GCM_SHA384( "TLS_DH_anon_WITH_AES_256_GCM_SHA384", null, Boolean.FALSE ),
    TLS_DH_anon_WITH_AES_128_GCM_SHA256( "TLS_DH_anon_WITH_AES_128_GCM_SHA256", null, Boolean.FALSE ),
    TLS_DH_anon_WITH_AES_256_CBC_SHA256( "TLS_DH_anon_WITH_AES_256_CBC_SHA256", Boolean.FALSE, Boolean.FALSE ),
    TLS_ECDH_anon_WITH_AES_256_CBC_SHA( "TLS_ECDH_anon_WITH_AES_256_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_DH_anon_WITH_AES_256_CBC_SHA( "TLS_DH_anon_WITH_AES_256_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_DH_anon_WITH_AES_128_CBC_SHA256( "TLS_DH_anon_WITH_AES_128_CBC_SHA256", Boolean.FALSE, Boolean.FALSE ),
    TLS_ECDH_anon_WITH_AES_128_CBC_SHA( "TLS_ECDH_anon_WITH_AES_128_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_DH_anon_WITH_AES_128_CBC_SHA( "TLS_DH_anon_WITH_AES_128_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_ECDH_anon_WITH_RC4_128_SHA( "TLS_ECDH_anon_WITH_RC4_128_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_DH_anon_WITH_RC4_128_MD5( "SSL_DH_anon_WITH_RC4_128_MD5", Boolean.FALSE, Boolean.FALSE ),
    TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA( "TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_DH_anon_WITH_3DES_EDE_CBC_SHA( "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_RSA_WITH_NULL_SHA256( "TLS_RSA_WITH_NULL_SHA256", Boolean.FALSE, Boolean.FALSE ),
    TLS_ECDHE_ECDSA_WITH_NULL_SHA( "TLS_ECDHE_ECDSA_WITH_NULL_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_ECDHE_RSA_WITH_NULL_SHA( "TLS_ECDHE_RSA_WITH_NULL_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_RSA_WITH_NULL_SHA( "SSL_RSA_WITH_NULL_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_ECDH_ECDSA_WITH_NULL_SHA( "TLS_ECDH_ECDSA_WITH_NULL_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_ECDH_RSA_WITH_NULL_SHA( "TLS_ECDH_RSA_WITH_NULL_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_ECDH_anon_WITH_NULL_SHA( "TLS_ECDH_anon_WITH_NULL_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_RSA_WITH_NULL_MD5( "SSL_RSA_WITH_NULL_MD5", Boolean.FALSE, Boolean.FALSE ),
    SSL_RSA_WITH_DES_CBC_SHA( "SSL_RSA_WITH_DES_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_DHE_RSA_WITH_DES_CBC_SHA( "SSL_DHE_RSA_WITH_DES_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_DHE_DSS_WITH_DES_CBC_SHA( "SSL_DHE_DSS_WITH_DES_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_DH_anon_WITH_DES_CBC_SHA( "SSL_DH_anon_WITH_DES_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_RSA_EXPORT_WITH_RC4_40_MD5( "SSL_RSA_EXPORT_WITH_RC4_40_MD5", Boolean.FALSE, Boolean.FALSE ),
    SSL_DH_anon_EXPORT_WITH_RC4_40_MD5( "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", Boolean.FALSE, Boolean.FALSE ),
    SSL_RSA_EXPORT_WITH_DES40_CBC_SHA( "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA( "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA( "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA( "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_WITH_RC4_128_SHA( "TLS_KRB5_WITH_RC4_128_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_WITH_RC4_128_MD5( "TLS_KRB5_WITH_RC4_128_MD5", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_WITH_3DES_EDE_CBC_SHA( "TLS_KRB5_WITH_3DES_EDE_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_WITH_3DES_EDE_CBC_MD5( "TLS_KRB5_WITH_3DES_EDE_CBC_MD5", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_WITH_DES_CBC_SHA( "TLS_KRB5_WITH_DES_CBC_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_WITH_DES_CBC_MD5( "TLS_KRB5_WITH_DES_CBC_MD5", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_EXPORT_WITH_RC4_40_SHA( "TLS_KRB5_EXPORT_WITH_RC4_40_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_EXPORT_WITH_RC4_40_MD5( "TLS_KRB5_EXPORT_WITH_RC4_40_MD5", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA( "TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", Boolean.FALSE, Boolean.FALSE ),
    TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5( "TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", Boolean.FALSE, Boolean.FALSE );

    /**
     * The list of supported ciphers for JAVA 8
     */
    public static final SupportedCipher[] SUPPORTED_CIPHERS = 
    {
        TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384,
        TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,
        TLS_RSA_WITH_AES_256_CBC_SHA256,
        TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384,
        TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384,
        TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,
        TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,
        TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
        TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
        TLS_RSA_WITH_AES_256_CBC_SHA,
        TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA,
        TLS_ECDH_RSA_WITH_AES_256_CBC_SHA,
        TLS_DHE_RSA_WITH_AES_256_CBC_SHA,
        TLS_DHE_DSS_WITH_AES_256_CBC_SHA,
        TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,
        TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
        TLS_RSA_WITH_AES_128_CBC_SHA256,
        TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256,
        TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256,
        TLS_DHE_RSA_WITH_AES_128_CBC_SHA256,
        TLS_DHE_DSS_WITH_AES_128_CBC_SHA256,
        TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
        TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
        TLS_RSA_WITH_AES_128_CBC_SHA,
        TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA,
        TLS_ECDH_RSA_WITH_AES_128_CBC_SHA,
        TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
        TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
        TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
        TLS_ECDHE_RSA_WITH_RC4_128_SHA,
        SSL_RSA_WITH_RC4_128_SHA,
        TLS_ECDH_ECDSA_WITH_RC4_128_SHA,
        TLS_ECDH_RSA_WITH_RC4_128_SHA,
        TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
        TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
        TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
        TLS_RSA_WITH_AES_256_GCM_SHA384,
        TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384,
        TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384,
        TLS_DHE_RSA_WITH_AES_256_GCM_SHA384,
        TLS_DHE_DSS_WITH_AES_256_GCM_SHA384,
        TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
        TLS_RSA_WITH_AES_128_GCM_SHA256,
        TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256,
        TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256,
        TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
        TLS_DHE_DSS_WITH_AES_128_GCM_SHA256,
        TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA,
        TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA,
        SSL_RSA_WITH_3DES_EDE_CBC_SHA,
        TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA,
        TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA,
        SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA,
        SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA,
        SSL_RSA_WITH_RC4_128_MD5,
        TLS_EMPTY_RENEGOTIATION_INFO_SCSV,
        TLS_DH_anon_WITH_AES_256_GCM_SHA384,
        TLS_DH_anon_WITH_AES_128_GCM_SHA256,
        TLS_DH_anon_WITH_AES_256_CBC_SHA256,
        TLS_ECDH_anon_WITH_AES_256_CBC_SHA,
        TLS_DH_anon_WITH_AES_256_CBC_SHA,
        TLS_DH_anon_WITH_AES_128_CBC_SHA256,
        TLS_ECDH_anon_WITH_AES_128_CBC_SHA,
        TLS_DH_anon_WITH_AES_128_CBC_SHA,
        TLS_ECDH_anon_WITH_RC4_128_SHA,
        SSL_DH_anon_WITH_RC4_128_MD5,
        TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA,
        SSL_DH_anon_WITH_3DES_EDE_CBC_SHA,
        TLS_RSA_WITH_NULL_SHA256,
        TLS_ECDHE_ECDSA_WITH_NULL_SHA,
        TLS_ECDHE_RSA_WITH_NULL_SHA,
        SSL_RSA_WITH_NULL_SHA,
        TLS_ECDH_ECDSA_WITH_NULL_SHA,
        TLS_ECDH_RSA_WITH_NULL_SHA,
        TLS_ECDH_anon_WITH_NULL_SHA,
        SSL_RSA_WITH_NULL_MD5,
        SSL_RSA_WITH_DES_CBC_SHA,
        SSL_DHE_RSA_WITH_DES_CBC_SHA,
        SSL_DHE_DSS_WITH_DES_CBC_SHA,
        SSL_DH_anon_WITH_DES_CBC_SHA,
        SSL_RSA_EXPORT_WITH_RC4_40_MD5,
        SSL_DH_anon_EXPORT_WITH_RC4_40_MD5,
        SSL_RSA_EXPORT_WITH_DES40_CBC_SHA,
        SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA,
        SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA,
        SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA,
        TLS_KRB5_WITH_RC4_128_SHA,
        TLS_KRB5_WITH_RC4_128_MD5,
        TLS_KRB5_WITH_3DES_EDE_CBC_SHA,
        TLS_KRB5_WITH_3DES_EDE_CBC_MD5,
        TLS_KRB5_WITH_DES_CBC_SHA,
        TLS_KRB5_WITH_DES_CBC_MD5,
        TLS_KRB5_EXPORT_WITH_RC4_40_SHA,
        TLS_KRB5_EXPORT_WITH_RC4_40_MD5,
        TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA,
        TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5
    };


    /** The supported cipher name */
    private String cipher;

    /** A flag that tells if the cipher is supported in Java 7 */
    private Boolean java7; 
    
    /** A flag that tells if the cipher is supported in Java 8 */
    private Boolean java8; 
    
    /** A map containing all the values */
    private static Map<String, SupportedCipher> supportedCiphersByName = new HashMap<String, SupportedCipher>();

    /** A list of all the supported ciphers for JAVA 7 */
    public static List<SupportedCipher> supportedCiphersJava7 = new ArrayList<SupportedCipher>();

    /** A list of all the supported cipher names for JAVA 7 */
    public static List<String> supportedCipherNamesJava7 = new ArrayList<String>();

    /** A list of all the supported ciphers for JAVA 8 */
    public static List<SupportedCipher> supportedCiphersJava8 = new ArrayList<SupportedCipher>();

    /** A list of all the supported cipher names for JAVA 8 */
    public static List<String> supportedCipherNamesJava8 = new ArrayList<String>();

    /** Initialization of the previous maps and lists */
    static
    {
        for ( SupportedCipher cipher : SupportedCipher.values() )
        {
            supportedCiphersByName.put( cipher.getCipher(), cipher );
            
            if ( cipher.isJava7Implemented() )
            {
                supportedCiphersJava7.add( cipher );
                supportedCipherNamesJava7.add( cipher.cipher );
            }
            
            if ( cipher.isJava8Implemented() )
            {
                supportedCiphersJava8.add( cipher );
                supportedCipherNamesJava8.add( cipher.cipher );
            }
        }
    }

    /**
     * A private constructor used to initialize the enum values
     */
    private SupportedCipher( String cipher, Boolean java7, Boolean java8 )
    {
        this.cipher = cipher;
        this.java7 = java7;
        this.java8 = java8;
    }
    
    
    /**
     * @return the cipher
     */
    public String getCipher()
    {
        return cipher;
    }

    
    /**
     * @return <code>true</code> if the cipher is enabled on Java 7, <code>false</code> if
     * the cipher is disabled in Java 7, or not implemented.
     */
    public Boolean isJava7Enabled()
    {
        return java7 != null && java7;
    }

    
    /**
     * @return <code>true</code> if the cipher is implemented in Java 7, regardless of it's enabled or disabled
     */
    public boolean isJava7Implemented()
    {
        return java7 != null;
    }

    
    /**
     * @return <code>true</code> if the cipher is enabled on Java 8, <code>false</code> if
     * the cipher is disabled in Java 8, or not implemented.
     */
    public boolean isJava8Enabled()
    {
        return java8!= null && java8;
    }

    
    /**
     * @return <code>true</code> if the cipher is implemented in Java 8, regardless of it's enabled or disabled
     */
    public boolean isJava8Implemented()
    {
        return java8 != null;
    }


    /**
     * Get the SupportedCipher given a String.
     * @param type The supported cipher string we want to find
     * @return The found SupportedCipher, or null
     */
    public static SupportedCipher getByName( String type )
    {
        if ( type == null )
        {
            return null;
        }

        return supportedCiphersByName.get( type.toUpperCase() );
    }
}
