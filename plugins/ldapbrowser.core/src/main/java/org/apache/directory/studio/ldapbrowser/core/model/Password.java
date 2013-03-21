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

package org.apache.directory.studio.ldapbrowser.core.model;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.utils.UnixCrypt;
import org.apache.directory.studio.ldifparser.LdifUtils;


/**
 * The Password class is used to represent a hashed or plain text password.
 * It provides methods to retrieve information about the used hash method. 
 * And it provides a verify method to check if the hashed password is equal to 
 * a given plain text password.  
 * 
 * The following hash methods are supported:
 * <ul>
 *   <li>SHA</li>
 *   <li>SSHA</li>
 *   <li>MD5</li>
 *   <li>SMD5</li>
 *   <li>CRYPT</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Password
{
    /** The constant used for the SHA hash, value <code>SHA</code> */
    public static final String HASH_METHOD_SHA = "SHA"; //$NON-NLS-1$

    /** The constant used for the salted SHA hash, value <code>SSHA</code> */
    public static final String HASH_METHOD_SSHA = "SSHA"; //$NON-NLS-1$

    /** The constant used for the SHA-256 hash, value <code>SHA-256</code> */
    public static final String HASH_METHOD_SHA_256 = "SHA-256"; //$NON-NLS-1$

    /** The constant used for the salted SHA-256 hash, value <code>SSHA-256</code> */
    public static final String HASH_METHOD_SSHA_256 = "SSHA-256"; //$NON-NLS-1$

    /** The constant used for the SHA-384 hash, value <code>SHA-384</code> */
    public static final String HASH_METHOD_SHA_384 = "SHA-384"; //$NON-NLS-1$

    /** The constant used for the salted SHA-384 hash, value <code>SSHA-384</code> */
    public static final String HASH_METHOD_SSHA_384 = "SSHA-384"; //$NON-NLS-1$

    /** The constant used for the SHA-512 hash, value <code>SHA-512</code> */
    public static final String HASH_METHOD_SHA_512 = "SHA-512"; //$NON-NLS-1$

    /** The constant used for the salted SHA-512 hash, value <code>SSHA-512</code> */
    public static final String HASH_METHOD_SSHA_512 = "SSHA-512"; //$NON-NLS-1$

    /** The constant used for the MD5 hash, value <code>MD5</code> */
    public static final String HASH_METHOD_MD5 = "MD5"; //$NON-NLS-1$

    /** The constant used for the salted MD5 hash, value <code>SMD5</code> */
    public static final String HASH_METHOD_SMD5 = "SMD5"; //$NON-NLS-1$

    /** The constant used for the crypt hash, value <code>CRYPT</code> */
    public static final String HASH_METHOD_CRYPT = "CRYPT"; //$NON-NLS-1$

    /** The constant used for plain text passwords */
    public static final String HASH_METHOD_NO = BrowserCoreMessages.model__no_hash;

    /** The constant used for unsupported hash methods */
    public static final String HASH_METHOD_UNSUPPORTED = BrowserCoreMessages.model__unsupported_hash;

    /** The constant used for invalid password hashes */
    public static final String HASH_METHOD_INVALID = BrowserCoreMessages.model__invalid_hash;

    /** The hash method. */
    private String hashMethod;

    /** The hashed password. */
    private byte[] hashedPassword;

    /** The salt. */
    private byte[] salt;

    /** The trash, used for unknown hash methods. */
    private String trash;


    /**
     * Creates a new instance of Password.
     *
     * @param password the password, either hashed or plain text
     */
    public Password( byte[] password )
    {
        this( LdifUtils.utf8decode( password ) );
    }


    /**
     * Creates a new instance of Password.
     *
     * @param password the password, either hashed or plain text
     */
    public Password( String password )
    {
        if ( password == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_password );
        }
        else if ( password.indexOf( '{' ) == 0 && password.indexOf( '}' ) > 0 )
        {
            try
            {
                hashMethod = password.substring( password.indexOf( '{' ) + 1, password.indexOf( '}' ) );
                String rest = password.substring( hashMethod.length() + 2 );

                if ( HASH_METHOD_SHA.equalsIgnoreCase( hashMethod )
                    || HASH_METHOD_SHA_256.equalsIgnoreCase( hashMethod )
                    || HASH_METHOD_SHA_384.equalsIgnoreCase( hashMethod )
                    || HASH_METHOD_SHA_512.equalsIgnoreCase( hashMethod )
                    || HASH_METHOD_MD5.equalsIgnoreCase( hashMethod ) )
                {
                    hashedPassword = LdifUtils.base64decodeToByteArray( rest );
                    salt = null;
                }
                else if ( HASH_METHOD_SSHA.equalsIgnoreCase( hashMethod )
                    || HASH_METHOD_SSHA_256.equalsIgnoreCase( hashMethod )
                    || HASH_METHOD_SSHA_384.equalsIgnoreCase( hashMethod )
                    || HASH_METHOD_SSHA_512.equalsIgnoreCase( hashMethod )
                    || HASH_METHOD_SMD5.equalsIgnoreCase( hashMethod ) )
                {
                    if ( HASH_METHOD_SSHA.equalsIgnoreCase( hashMethod ) )
                    {
                        hashedPassword = new byte[20];
                    }
                    else if ( HASH_METHOD_SSHA_256.equalsIgnoreCase( hashMethod ) )
                    {
                        hashedPassword = new byte[32];
                    }
                    else if ( HASH_METHOD_SSHA_384.equalsIgnoreCase( hashMethod ) )
                    {
                        hashedPassword = new byte[48];
                    }
                    else if ( HASH_METHOD_SSHA_512.equalsIgnoreCase( hashMethod ) )
                    {
                        hashedPassword = new byte[64];
                    }
                    else if ( HASH_METHOD_SMD5.equalsIgnoreCase( hashMethod ) )
                    {
                        hashedPassword = new byte[16];
                    }

                    byte[] hashedPasswordWithSalt = LdifUtils.base64decodeToByteArray( rest );
                    salt = new byte[hashedPasswordWithSalt.length - hashedPassword.length];
                    split( hashedPasswordWithSalt, hashedPassword, salt );
                }
                else if ( HASH_METHOD_CRYPT.equalsIgnoreCase( hashMethod ) )
                {
                    byte[] saltWithPassword = LdifUtils.utf8encode( rest );
                    salt = new byte[2];
                    hashedPassword = new byte[saltWithPassword.length - salt.length];
                    split( saltWithPassword, salt, hashedPassword );
                }
                else
                {
                    hashMethod = HASH_METHOD_UNSUPPORTED;
                    trash = password;
                }
            }
            catch ( RuntimeException e )
            {
                // happens if 'rest' is not valid BASE64
                hashMethod = HASH_METHOD_INVALID;
                trash = password;
            }
        }
        else
        {
            // plain text
            hashMethod = null;
            hashedPassword = LdifUtils.utf8encode( password );
            salt = null;
        }
    }


    /**
     * Creates a new instance of Password and calculates the hashed password.
     *
     * @param hashMethod the hash method to use
     * @param passwordAsPlaintext the plain text password
     * 
     * @throws IllegalArgumentException if the given hash method is not
     *         supported of if the given password is null
     */
    public Password( String hashMethod, String passwordAsPlaintext )
    {
        if ( !( hashMethod == null || HASH_METHOD_NO.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SHA.equalsIgnoreCase( hashMethod ) || HASH_METHOD_SSHA.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SHA_256.equalsIgnoreCase( hashMethod ) || HASH_METHOD_SSHA_256.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SHA_384.equalsIgnoreCase( hashMethod ) || HASH_METHOD_SSHA_384.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SHA_512.equalsIgnoreCase( hashMethod ) || HASH_METHOD_SSHA_512.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_MD5.equalsIgnoreCase( hashMethod ) || HASH_METHOD_SMD5.equalsIgnoreCase( hashMethod ) || HASH_METHOD_CRYPT
                .equalsIgnoreCase( hashMethod ) ) )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__unsupported_hash );
        }
        if ( passwordAsPlaintext == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_password );
        }

        // set hash method
        if ( HASH_METHOD_NO.equalsIgnoreCase( hashMethod ) )
        {
            hashMethod = null;
        }
        this.hashMethod = hashMethod;

        // set salt
        if ( HASH_METHOD_SSHA.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA_256.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA_384.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA_512.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SMD5.equalsIgnoreCase( hashMethod ) )
        {
            this.salt = new byte[8];
            new SecureRandom().nextBytes( this.salt );
        }
        else if ( HASH_METHOD_CRYPT.equalsIgnoreCase( hashMethod ) )
        {
            this.salt = new byte[2];
            SecureRandom sr = new SecureRandom();
            int i1 = sr.nextInt( 64 );
            int i2 = sr.nextInt( 64 );
            this.salt[0] = ( byte ) ( i1 < 12 ? ( i1 + '.' ) : i1 < 38 ? ( i1 + 'A' - 12 ) : ( i1 + 'a' - 38 ) );
            this.salt[1] = ( byte ) ( i2 < 12 ? ( i2 + '.' ) : i2 < 38 ? ( i2 + 'A' - 12 ) : ( i2 + 'a' - 38 ) );
        }
        else
        {
            this.salt = null;
        }

        // digest
        if ( HASH_METHOD_SHA.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA.equalsIgnoreCase( hashMethod ) )
        {
            this.hashedPassword = digest( HASH_METHOD_SHA, passwordAsPlaintext, this.salt );
        }
        else if ( HASH_METHOD_SHA_256.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA_256.equalsIgnoreCase( hashMethod ) )
        {
            this.hashedPassword = digest( HASH_METHOD_SHA_256, passwordAsPlaintext, this.salt );
        }
        else if ( HASH_METHOD_SHA_384.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA_384.equalsIgnoreCase( hashMethod ) )
        {
            this.hashedPassword = digest( HASH_METHOD_SHA_384, passwordAsPlaintext, this.salt );
        }
        else if ( HASH_METHOD_SHA_512.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA_512.equalsIgnoreCase( hashMethod ) )
        {
            this.hashedPassword = digest( HASH_METHOD_SHA_512, passwordAsPlaintext, this.salt );
        }
        else if ( HASH_METHOD_MD5.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SMD5.equalsIgnoreCase( hashMethod ) )
        {
            this.hashedPassword = digest( HASH_METHOD_MD5, passwordAsPlaintext, this.salt );
        }
        else if ( HASH_METHOD_CRYPT.equalsIgnoreCase( hashMethod ) )
        {
            this.hashedPassword = crypt( passwordAsPlaintext, this.salt );
        }
        else if ( hashMethod == null )
        {
            this.hashedPassword = LdifUtils.utf8encode( passwordAsPlaintext );
        }
    }


    /**
     * Verifies if this password is equal to the given test password.
     * 
     * @param testPasswordAsPlaintext the test password as plaintext
     * 
     * @return true, if equal
     */
    public boolean verify( String testPasswordAsPlaintext )
    {
        if ( testPasswordAsPlaintext == null )
        {
            return false;
        }

        boolean verified = false;
        if ( hashMethod == null )
        {
            verified = testPasswordAsPlaintext.equals( LdifUtils.utf8decode( hashedPassword ) );
        }
        else if ( HASH_METHOD_SHA.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA.equalsIgnoreCase( hashMethod ) )
        {
            byte[] hash = digest( HASH_METHOD_SHA, testPasswordAsPlaintext, salt );
            verified = equals( hash, hashedPassword );
        }
        else if ( HASH_METHOD_SHA_256.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA_256.equalsIgnoreCase( hashMethod ) )
        {
            byte[] hash = digest( HASH_METHOD_SHA_256, testPasswordAsPlaintext, salt );
            verified = equals( hash, hashedPassword );
        }
        else if ( HASH_METHOD_SHA_384.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA_384.equalsIgnoreCase( hashMethod ) )
        {
            byte[] hash = digest( HASH_METHOD_SHA_384, testPasswordAsPlaintext, salt );
            verified = equals( hash, hashedPassword );
        }
        else if ( HASH_METHOD_SHA_512.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SSHA_512.equalsIgnoreCase( hashMethod ) )
        {
            byte[] hash = digest( HASH_METHOD_SHA_512, testPasswordAsPlaintext, salt );
            verified = equals( hash, hashedPassword );
        }
        else if ( HASH_METHOD_MD5.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_SMD5.equalsIgnoreCase( hashMethod ) )
        {
            byte[] hash = digest( HASH_METHOD_MD5, testPasswordAsPlaintext, salt );
            verified = equals( hash, hashedPassword );
        }
        else if ( HASH_METHOD_CRYPT.equalsIgnoreCase( hashMethod ) )
        {
            byte[] crypted = crypt( testPasswordAsPlaintext, salt );
            verified = equals( crypted, hashedPassword );
        }

        return verified;
    }


    /**
     * Gets the hash method.
     * 
     * @return the hash method
     */
    public String getHashMethod()
    {
        return hashMethod;
    }


    /**
     * Gets the hashed password.
     * 
     * @return the hashed password
     */
    public byte[] getHashedPassword()
    {
        return hashedPassword;
    }


    /**
     * Gets the hashed password as hex string.
     * 
     * @return the hashed password as hex string
     */
    public String getHashedPasswordAsHexString()
    {
        return LdifUtils.hexEncode( hashedPassword );
    }


    /**
     * Gets the salt.
     * 
     * @return the salt
     */
    public byte[] getSalt()
    {
        return salt;
    }


    /**
     * Gets the salt as hex string.
     * 
     * @return the salt as hex string
     */
    public String getSaltAsHexString()
    {
        return LdifUtils.hexEncode( salt );
    }


    /**
     * Gets the 
     * 
     * @return the byte[]
     */
    public byte[] toBytes()
    {
        return LdifUtils.utf8encode( toString() );
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        if ( HASH_METHOD_UNSUPPORTED.equalsIgnoreCase( hashMethod )
            || HASH_METHOD_INVALID.equalsIgnoreCase( hashMethod ) )
        {
            sb.append( trash );
        }
        else if ( HASH_METHOD_CRYPT.equalsIgnoreCase( hashMethod ) )
        {
            sb.append( '{' ).append( hashMethod ).append( '}' );
            sb.append( LdifUtils.utf8decode( salt ) );
            sb.append( LdifUtils.utf8decode( hashedPassword ) );
        }
        else if ( hashMethod != null )
        {
            sb.append( '{' ).append( hashMethod ).append( '}' );
            if ( salt != null )
            {
                byte[] hashedPasswordWithSaltBytes = new byte[hashedPassword.length + salt.length];
                merge( hashedPasswordWithSaltBytes, hashedPassword, salt );
                sb.append( LdifUtils.base64encode( hashedPasswordWithSaltBytes ) );
            }
            else
            {
                sb.append( LdifUtils.base64encode( hashedPassword ) );
            }
        }
        else
        {
            sb.append( LdifUtils.utf8decode( hashedPassword ) );
        }

        return sb.toString();
    }


    private static void split( byte[] all, byte[] left, byte[] right )
    {
        System.arraycopy( all, 0, left, 0, left.length );
        System.arraycopy( all, left.length, right, 0, right.length );
    }


    private static void merge( byte[] all, byte[] left, byte[] right )
    {
        System.arraycopy( left, 0, all, 0, left.length );
        System.arraycopy( right, 0, all, left.length, right.length );
    }


    private static boolean equals( byte[] data1, byte[] data2 )
    {
        if ( data1 == data2 )
        {
            return true;
        }
        if ( data1 == null || data2 == null )
        {
            return false;
        }
        if ( data1.length != data2.length )
        {
            return false;
        }
        for ( int i = 0; i < data1.length; i++ )
        {
            if ( data1[i] != data2[i] )
            {
                return false;
            }
        }
        return true;
    }


    private static byte[] digest( String hashMethod, String password, byte[] salt )
    {

        byte[] passwordBytes = LdifUtils.utf8encode( password );
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance( hashMethod );
        }
        catch ( NoSuchAlgorithmException e1 )
        {
            return null;
        }

        if ( salt != null )
        {
            digest.update( passwordBytes );
            digest.update( salt );
            byte[] hashedPasswordBytes = digest.digest();
            return hashedPasswordBytes;
        }
        else
        {
            byte[] hashedPasswordBytes = digest.digest( passwordBytes );
            return hashedPasswordBytes;
        }
    }


    private static byte[] crypt( String password, byte[] salt )
    {
        String saltWithCrypted = UnixCrypt.crypt( password, LdifUtils.utf8decode( salt ) );
        String crypted = saltWithCrypted.substring( 2 );
        return LdifUtils.utf8encode( crypted );
    }

}
