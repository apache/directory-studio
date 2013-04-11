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
package org.apache.directory.studio.connection.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.io.FileUtils;


/**
 * A wrapper around {@link KeyStore} for storing passwords.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordsKeyStoreManager
{
    /** The default filename */
    private static final String KEYSTORE_DEFAULT_FILENAME = "passwords.jks";

    /** The keystore file name */
    private String filename = KEYSTORE_DEFAULT_FILENAME;

    /** The master password */
    private String masterPassword;

    /** The keystore */
    private KeyStore keystore;


    /**
     * Creates a new instance of PasswordsKeyStoreManager.
     */
    public PasswordsKeyStoreManager()
    {
    }


    /**
     * Creates a new instance of PasswordsKeyStoreManager.
     *
     * @param filename the filename
     */
    public PasswordsKeyStoreManager( String filename )
    {
        this.filename = filename;
    }


    /**
     * Indicates if the keystore is loaded.
     *
     * @return <code>true</code> if the keystore is loaded,
     *         <code>false</code> if not.
     */
    public boolean isLoaded()
    {
        return keystore != null;
    }


    public void load( String masterPassword ) throws KeyStoreException
    {
        this.masterPassword = masterPassword;

        try
        {
            keystore = KeyStore.getInstance( "JCEKS" ); //$NON-NLS-1$

            // Getting the keystore file
            File keystoreFile = getKeyStoreFile();

            // Checking if the keystore file is available on disk
            if ( keystoreFile.exists() && keystoreFile.isFile() && keystoreFile.canRead() )
            {
                keystore.load( new FileInputStream( keystoreFile ), masterPassword.toCharArray() );
            }
            else
            {
                keystore.load( null, null );
            }
        }
        // Catch for the following exceptions that may be raised while
        // handling the keystore:
        // - java.security.KeyStoreException
        // - java.security.NoSuchAlgorithmException
        // - java.security.cert.CertificateException
        catch ( GeneralSecurityException e )
        {
            this.masterPassword = null;
            this.keystore = null;

            throw new KeyStoreException( e );
        }
        // Catch for the following exceptions that may be raised while
        // handling the file:
        // - java.io.IOException
        // - java.io.FileNotFoundException
        catch ( IOException e )
        {
            this.masterPassword = null;
            this.keystore = null;

            throw new KeyStoreException( e );
        }
    }


    /**
     * Saves the keystore on disk.
     */
    public void save() throws KeyStoreException
    {
        if ( isLoaded() && ( masterPassword != null ) )
        {
            try
            {
                keystore.store( new FileOutputStream( getKeyStoreFile() ), masterPassword.toCharArray() );
            }
            // Catch for the following exceptions that may be raised while
            // handling the keystore:
            // - java.security.KeyStoreException
            // - java.security.NoSuchAlgorithmException
            // - java.security.cert.CertificateException
            catch ( GeneralSecurityException e )
            {
                throw new KeyStoreException( e );
            }
            // Catch for the following exceptions that may be raised while
            // handling the file:
            // - java.io.IOException
            // - java.io.FileNotFoundException
            catch ( IOException e )
            {
                throw new KeyStoreException( e );
            }
        }
    }


    /**
     * Checks the master password.
     *
     * @param masterPassword the master password
     * @return <code>true</code> if the master password is correct,
     *         <code>false</code> if not.
     * @throws KeyStoreException if an error occurs
     */
    public boolean checkMasterPassword( String masterPassword ) throws KeyStoreException
    {
        // If the keystore is already loaded, we compare the master password directly
        if ( isLoaded() )
        {
            return ( ( this.masterPassword != null ) && ( this.masterPassword.equals( masterPassword ) ) );
        }
        // The keystore is not loaded yet 
        else
        {
            try
            {
                // Loading the keystore
                load( masterPassword );

                // Returning the check value
                return isLoaded();
            }
            catch ( KeyStoreException e )
            {
                throw e;
            }
        }
    }


    /**
     * Sets the master password.
     *
     * @param masterPassword the master password
     */
    public void setMasterPassword( String masterPassword )
    {
        // Creating a map to store previously stored passwords
        Map<String, String> passwordsMap = new HashMap<String, String>();

        if ( isLoaded() )
        {
            // Getting the connection IDs
            String[] connectionIds = getConnectionIds();

            // Storing the password of each connection in the map
            for ( String connectionId : connectionIds )
            {
                // Getting the connection password
                String connectionPassword = getConnectionPassword( connectionId );

                // Checking if we got a password
                if ( connectionPassword != null )
                {
                    // Storing the password of the connection in the map
                    passwordsMap.put( connectionId, connectionPassword );
                }

                // Removing the password from the keystore
                storeConnectionPassword( connectionId, null, false );
            }
        }

        // Assigning the new master password
        this.masterPassword = masterPassword;

        // Storing the previous passwords back in the keystore
        if ( passwordsMap.size() > 0 )
        {
            Set<String> connectionIds = passwordsMap.keySet();

            // Storing the password of each connection in the keystore
            if ( connectionIds != null )
            {
                for ( String connectionId : connectionIds )
                {
                    String connectionPassword = passwordsMap.get( connectionId );

                    if ( connectionPassword != null )
                    {
                        // Storing the password of the connection in the keystore
                        storeConnectionPassword( connectionId, connectionPassword, false );
                    }
                }
            }
        }
    }


    /**
     * Gets the keystore file.
     *
     * @return the keystore file
     */
    public File getKeyStoreFile()
    {
        return ConnectionCorePlugin.getDefault().getStateLocation().append( filename ).toFile();
    }


    /**
     * Deletes the keystore.
     */
    public void deleteKeystoreFile()
    {
        // Getting the keystore file
        File keystoreFile = getKeyStoreFile();

        // Checking if the keystore file is available on disk
        if ( keystoreFile.exists() && keystoreFile.isFile() && keystoreFile.canRead() && keystoreFile.canWrite() )
        {
            keystoreFile.delete();
        }
    }


    /**
     * Gets the connections IDs contained in the keystore.
     *
     * @return the connection IDs contained in the keystore
     */
    public String[] getConnectionIds()
    {
        if ( keystore != null )
        {
            try
            {
                return Collections.list( keystore.aliases() ).toArray( new String[0] );
            }
            catch ( KeyStoreException e )
            {
                // Silent
            }
        }

        return new String[0];
    }


    /**
     * Stores a connection password.
     *
     * @param connection the connection
     * @param password the password
     */
    public void storeConnectionPassword( Connection connection, String password )
    {
        if ( connection != null )
        {
            storeConnectionPassword( connection.getId(), password );
        }
    }


    /**
     * Stores a connection password.
     *
     * @param connection the connection
     * @param password the password
     * @param saveKeystore if the keystore needs to be saved
     */
    public void storeConnectionPassword( Connection connection, String password, boolean saveKeystore )
    {
        if ( connection != null )
        {
            storeConnectionPassword( connection.getId(), password, true );
        }
    }


    /**
     * Stores a connection password.
     *
     * @param connectionId the connection id
     * @param password the password
     */
    public void storeConnectionPassword( String connectionId, String password )
    {
        storeConnectionPassword( connectionId, password, true );
    }


    /**
     * Stores a connection password.
     *
     * @param connectionId the connection id
     * @param password the password
     * @param saveKeystore if the keystore needs to be saved
     */
    public void storeConnectionPassword( String connectionId, String password, boolean saveKeystore )
    {
        if ( isLoaded() && ( connectionId != null ) )
        {
            try
            {
                // Checking if the password is null
                if ( password == null )
                {
                    // We need to remove the corresponding entry in the keystore
                    if ( keystore.containsAlias( connectionId ) )
                    {
                        keystore.deleteEntry( connectionId );
                    }
                }
                else
                {
                    // Generating a secret key from the password
                    SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBE" );
                    SecretKey generatedSecret = factory.generateSecret( new PBEKeySpec( password.toCharArray() ) );

                    // Setting the entry in the keystore
                    keystore.setEntry( connectionId, new KeyStore.SecretKeyEntry( generatedSecret ),
                        new KeyStore.PasswordProtection( masterPassword.toCharArray() ) );
                }

                // Saving
                if ( saveKeystore )
                {
                    save();
                }
            }
            catch ( Exception e )
            {
                // Silent
            }
        }
    }


    /**
     * Gets a connection password.
     *
     * @param connection the connection
     * @return the password for the connection or <code>null</code>.
     */
    public String getConnectionPassword( Connection connection )
    {
        if ( connection != null )
        {
            return getConnectionPassword( connection.getId() );
        }

        return null;
    }


    /**
     * Gets a connection password.
     *
     * @param connectionId the connection id
     * @return the password for the connection id or <code>null</code>.
     */
    public String getConnectionPassword( String connectionId )
    {
        if ( isLoaded() && ( connectionId != null ) )
        {
            try
            {
                SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBE" );
                SecretKeyEntry ske = ( SecretKeyEntry ) keystore.getEntry( connectionId,
                    new KeyStore.PasswordProtection( masterPassword.toCharArray() ) );

                if ( ske != null )
                {
                    PBEKeySpec keySpec = ( PBEKeySpec ) factory.getKeySpec( ske.getSecretKey(), PBEKeySpec.class );

                    if ( keySpec != null )
                    {
                        char[] password = keySpec.getPassword();

                        if ( password != null )
                        {
                            return new String( password );
                        }
                    }
                }
            }
            catch ( Exception e )
            {
                return null;
            }
        }

        return null;
    }


    /**
     * Resets the keystore manager.
     */
    public void reset()
    {
        // Reseting the fields
        this.keystore = null;
        this.masterPassword = null;

        // Getting the keystore file
        File keystoreFile = getKeyStoreFile();

        // If the keystore file exists, we need to remove it
        if ( keystoreFile.exists() )
        {
            // Deleting the file
            FileUtils.deleteQuietly( keystoreFile );
        }
    }


    public void reload( String masterPassword ) throws KeyStoreException
    {
        // Reseting the fields
        this.keystore = null;
        this.masterPassword = null;

        load( masterPassword );
    }


    /**
     * Gets the master password.
     *
     * @return the master password
     */
    public String getMasterPassword()
    {
        return masterPassword;
    }
}