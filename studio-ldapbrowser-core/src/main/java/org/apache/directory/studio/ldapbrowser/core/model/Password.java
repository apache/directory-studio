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
import org.apache.directory.studio.ldapbrowser.core.utils.LdifUtils;
import org.apache.directory.studio.ldapbrowser.core.utils.UnixCrypt;


public class Password
{

    public static final String HASH_METHOD_SHA = "SHA"; //$NON-NLS-1$

    public static final String HASH_METHOD_SSHA = "SSHA"; //$NON-NLS-1$

    public static final String HASH_METHOD_MD5 = "MD5"; //$NON-NLS-1$

    public static final String HASH_METHOD_SMD5 = "SMD5"; //$NON-NLS-1$

    public static final String HASH_METHOD_CRYPT = "CRYPT"; //$NON-NLS-1$

    public static final String HASH_METHOD_NO = BrowserCoreMessages.model__no_hash;

    public static final String HASH_METHOD_UNSUPPORTED = BrowserCoreMessages.model__unsupported_hash;

    String hashMethod;

    byte[] hashedPassword;

    byte[] salt;

    String trash;


    public Password( byte[] password )
    {
        this( LdifUtils.utf8decode( password ) );
    }


    public Password( String password )
    {

        if ( password == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_password );
        }
        else if ( password.indexOf( '{' ) == 0 && password.indexOf( '}' ) > 0 )
        {
            hashMethod = password.substring( password.indexOf( '{' ) + 1, password.indexOf( '}' ) );
            String rest = password.substring( hashMethod.length() + 2 );

            if ( HASH_METHOD_SHA.equals( hashMethod ) || HASH_METHOD_MD5.equals( hashMethod ) )
            {
                hashedPassword = LdifUtils.base64decodeToByteArray( rest );
                salt = null;
            }
            else if ( HASH_METHOD_SSHA.equals( hashMethod ) )
            {
                byte[] hashedPasswordWithSalt = LdifUtils.base64decodeToByteArray( rest );
                hashedPassword = new byte[20];
                salt = new byte[hashedPasswordWithSalt.length - hashedPassword.length];
                split( hashedPasswordWithSalt, hashedPassword, salt );
            }
            else if ( HASH_METHOD_SMD5.equals( hashMethod ) )
            {
                byte[] hashedPasswordWithSalt = LdifUtils.base64decodeToByteArray( rest );
                hashedPassword = new byte[16];
                salt = new byte[hashedPasswordWithSalt.length - hashedPassword.length];
                split( hashedPasswordWithSalt, hashedPassword, salt );
            }
            else if ( HASH_METHOD_CRYPT.equals( hashMethod ) )
            {
                byte[] saltWithPassword = LdifUtils.utf8encode( rest );
                salt = new byte[2];
                hashedPassword = new byte[saltWithPassword.length - salt.length];
                split( saltWithPassword, salt, hashedPassword );
            }
            else
            {
                // throw new IllegalArgumentException("Unsupported hash method
                // '"+hashMethod+"'");
                // handle as plain text?
                hashMethod = HASH_METHOD_UNSUPPORTED;
                trash = password;
                // salt = null;
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


    public Password( String hashMethod, String passwordAsPlaintext )
    {

        if ( !( hashMethod == null || HASH_METHOD_NO.equals( hashMethod ) || HASH_METHOD_SHA.equals( hashMethod )
            || HASH_METHOD_SSHA.equals( hashMethod ) || HASH_METHOD_MD5.equals( hashMethod )
            || HASH_METHOD_SMD5.equals( hashMethod ) || HASH_METHOD_CRYPT.equals( hashMethod ) ) )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__unsupported_hash );
        }
        if ( passwordAsPlaintext == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_password );
        }

        // set hash method
        if ( HASH_METHOD_NO.equals( hashMethod ) )
        {
            hashMethod = null;
        }
        this.hashMethod = hashMethod;

        // set salt
        if ( HASH_METHOD_SSHA.equals( hashMethod ) || HASH_METHOD_SMD5.equals( hashMethod ) )
        {
            this.salt = new byte[8];
            new SecureRandom().nextBytes( this.salt );
        }
        else if ( HASH_METHOD_CRYPT.equals( hashMethod ) )
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
        if ( HASH_METHOD_SHA.equals( hashMethod ) || HASH_METHOD_SSHA.equals( hashMethod ) )
        {
            this.hashedPassword = digest( HASH_METHOD_SHA, passwordAsPlaintext, this.salt );
        }
        else if ( HASH_METHOD_MD5.equals( hashMethod ) || HASH_METHOD_SMD5.equals( hashMethod ) )
        {
            this.hashedPassword = digest( HASH_METHOD_MD5, passwordAsPlaintext, this.salt );
        }
        else if ( HASH_METHOD_CRYPT.equals( hashMethod ) )
        {
            this.hashedPassword = crypt( passwordAsPlaintext, this.salt );
        }
        else if ( hashMethod == null )
        {
            this.hashedPassword = LdifUtils.utf8encode( passwordAsPlaintext );
        }
    }


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
        else if ( HASH_METHOD_SHA.equals( hashMethod ) || HASH_METHOD_SSHA.equals( hashMethod ) )
        {
            byte[] hash = digest( HASH_METHOD_SHA, testPasswordAsPlaintext, this.salt );
            verified = equals( hash, this.hashedPassword );
        }
        else if ( HASH_METHOD_MD5.equals( hashMethod ) || HASH_METHOD_SMD5.equals( hashMethod ) )
        {
            byte[] hash = digest( HASH_METHOD_MD5, testPasswordAsPlaintext, this.salt );
            verified = equals( hash, this.hashedPassword );
        }
        else if ( HASH_METHOD_CRYPT.equals( hashMethod ) )
        {
            byte[] crypted = crypt( testPasswordAsPlaintext, this.salt );
            verified = equals( crypted, this.hashedPassword );
        }

        return verified;
    }


    public String getHashMethod()
    {
        return this.hashMethod;
    }


    public byte[] getHashedPassword()
    {
        return hashedPassword;
    }


    public String getHashedPasswordAsHexString()
    {
        return LdifUtils.hexEncode( hashedPassword );
    }


    public byte[] getSalt()
    {
        return salt;
    }


    public String getSaltAsHexString()
    {
        return LdifUtils.hexEncode( salt );
    }


    public byte[] toBytes()
    {
        return LdifUtils.utf8encode( toString() );
    }


    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        if ( HASH_METHOD_UNSUPPORTED.equals( hashMethod ) )
        {
            sb.append( trash );
        }
        else if ( HASH_METHOD_CRYPT.equals( hashMethod ) )
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
            return true;
        if ( data1 == null || data2 == null )
            return false;
        if ( data1.length != data2.length )
            return false;
        for ( int i = 0; i < data1.length; i++ )
        {
            if ( data1[i] != data2[i] )
                return false;
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
