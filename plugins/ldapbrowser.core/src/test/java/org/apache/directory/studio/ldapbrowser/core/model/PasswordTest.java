/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.studio.ldapbrowser.core.model;


import junit.framework.TestCase;


/**
 * Test all the encryption algorithmes
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordTest extends TestCase
{
    /**
     * Null Password should not be accepted
     */
    public void testNullPassword()
    {
        try
        {
            new Password( ( String ) null );
            fail();
        }
        catch ( IllegalArgumentException iae )
        {
            assertTrue( true );
        }
    }


    /**
     * 
     */
    public void testPasswordSHAEncrypted()
    {
        Password password = new Password( "{SHA}5en6G6MezRroT3XKqkdPOmY/BfQ=" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordSHAEncryptedLowercase()
    {
        Password password = new Password( "{sha}5en6G6MezRroT3XKqkdPOmY/BfQ=" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordSSHAEncrypted()
    {
        Password password = new Password( "{SSHA}mjVVxasFkk59wMW4L1Ldt+YCblfhULHs03WW7g==" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordSSHAEncryptedLowercase()
    {
        Password password = new Password( "{ssha}mjVVxasFkk59wMW4L1Ldt+YCblfhULHs03WW7g==" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordMD5Encrypted()
    {
        Password password = new Password( "{MD5}Xr4ilOzQ4PCOq3aQ0qbuaQ==" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordMD5EncryptedLowercase()
    {
        Password password = new Password( "{md5}Xr4ilOzQ4PCOq3aQ0qbuaQ==" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordSMD5Encrypted()
    {
        Password password = new Password( "{SMD5}tQ9wo/VBuKsqBtylMMCcORbnYOJFMyDJ" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordSMD5EncryptedLowercase()
    {
        Password password = new Password( "{smd5}tQ9wo/VBuKsqBtylMMCcORbnYOJFMyDJ" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordCRYPTEncrypted()
    {
        Password password = new Password( "{CRYPT}qFkH8Z1woBlXw" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordCRYPTEncryptedLowercase()
    {
        Password password = new Password( "{crypt}qFkH8Z1woBlXw" ); //$NON-NLS-1$

        assertTrue( password.verify( "secret" ) ); //$NON-NLS-1$
    }


    /**
     * 
     */
    public void testPasswordBadAlgorithm()
    {
        Password password = new Password( "{CRYPTE}qFkH8Z1woBlXw" ); //$NON-NLS-1$

        assertFalse( password.verify( "secret" ) ); //$NON-NLS-1$
    }
}
