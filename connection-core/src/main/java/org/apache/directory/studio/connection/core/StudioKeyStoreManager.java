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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * A wrapper around {@link KeyStore}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StudioKeyStoreManager
{
    public enum Type
    {
        File, Memory
    }

    /** The type */
    private Type type;

    /** The filename of the underlying key store, only relevant for type File */
    private String filename;

    /** The password of the underlying key store, only relevant for type File */
    private String password;

    /** The in-memory key store, only relevant for type Memory */
    private KeyStore memoryKeyStore;


    /**
     * Creates a key store manager, backed by a key store on disk.
     * 
     * @param filename the filename
     * @param password the password
     * 
     * @return the key store manager
     */
    public static StudioKeyStoreManager createFileKeyStoreManager( String filename, String password )
    {
        StudioKeyStoreManager manager = new StudioKeyStoreManager( Type.File, filename, password );
        manager.filename = filename;
        manager.password = password;
        return manager;
    }


    /**
     * Creates a key store manager, backed by an in-memory key store.
     * 
     * @return the key store manager
     */
    public static StudioKeyStoreManager createMemoryKeyStoreManager()
    {
        StudioKeyStoreManager manager = new StudioKeyStoreManager( Type.Memory, null, null );
        return manager;
    }


    private StudioKeyStoreManager( Type type, String filename, String password )
    {
        this.type = type;
        this.filename = filename;
        this.password = password;
    }


    /**
     * Gets the underlying key store.
     * 
     * @return the key store
     */
    public synchronized KeyStore getKeyStore() throws CertificateException
    {
        if ( type == Type.File )
        {
            return getFileKeyStore();
        }
        else
        {
            return getMemoryKeyStore();
        }
    }


    /**
     * Gets the memory key store.
     * 
     * @return the memory key store
     */
    private KeyStore getMemoryKeyStore() throws CertificateException
    {
        if ( memoryKeyStore == null )
        {
            try
            {
                memoryKeyStore = KeyStore.getInstance( "JKS" ); //$NON-NLS-1$
                memoryKeyStore.load( null, null );
            }
            catch ( Exception e )
            {
                throw new CertificateException( Messages.StudioKeyStoreManager_CantReadTrustStore, e );
            }
        }
        return memoryKeyStore;
    }


    /**
     * Loads the file key store.
     * 
     * @return the file key store
     */
    private KeyStore getFileKeyStore() throws CertificateException
    {
        try
        {
            KeyStore fileKeyStore = KeyStore.getInstance( "JKS" ); //$NON-NLS-1$
            File file = ConnectionCorePlugin.getDefault().getStateLocation().append( filename ).toFile();
            if ( file.exists() && file.isFile() && file.canRead() )
            {
                fileKeyStore.load( new FileInputStream( file ), password.toCharArray() );
            }
            else
            {
                fileKeyStore.load( null, null );
            }

            return fileKeyStore;
        }
        catch ( Exception e )
        {
            throw new CertificateException( Messages.StudioKeyStoreManager_CantReadTrustStore, e );
        }
    }


    /**
     * Adds the certificate to the key store.
     * 
     * @param certificate the certificate
     */
    public synchronized void addCertificate( X509Certificate certificate ) throws CertificateException
    {
        if ( type == Type.File )
        {
            addToFileKeyStore( certificate );
        }
        else
        {
            addToMemoryKeyStore( certificate );
        }
    }


    /**
     * Adds the certificate to the memory key store.
     * 
     * @param certificate the certificate
     */
    private void addToMemoryKeyStore( X509Certificate certificate ) throws CertificateException
    {
        try
        {
            KeyStore memoryKeyStore = getMemoryKeyStore();
            addToKeyStore( certificate, memoryKeyStore );
        }
        catch ( Exception e )
        {
            throw new CertificateException( Messages.StudioKeyStoreManager_CantAddCertificateToTrustStore, e );
        }
    }


    /**
     * Adds the certificate to the file key store.
     * 
     * @param certificate the certificate
     */
    private void addToFileKeyStore( X509Certificate certificate ) throws CertificateException
    {
        try
        {
            KeyStore fileKeyStore = getFileKeyStore();
            addToKeyStore( certificate, fileKeyStore );
            File file = ConnectionCorePlugin.getDefault().getStateLocation().append( filename ).toFile();
            fileKeyStore.store( new FileOutputStream( file ), password.toCharArray() );
        }
        catch ( Exception e )
        {
            throw new CertificateException( Messages.StudioKeyStoreManager_CantAddCertificateToTrustStore, e );
        }
    }


    private void addToKeyStore( X509Certificate certificate, KeyStore keyStore ) throws Exception
    {
        String alias = certificate.getSubjectX500Principal().getName();
        keyStore.setCertificateEntry( alias, certificate );
    }


    /**
     * Gets the certificates contained in the key store.
     * 
     * @return the certificates
     */
    public X509Certificate[] getCertificates() throws CertificateException
    {
        try
        {
            List<X509Certificate> certificateList = new ArrayList<X509Certificate>();
            KeyStore keyStore = getKeyStore();
            Enumeration<String> aliases = keyStore.aliases();
            while ( aliases.hasMoreElements() )
            {
                String alias = aliases.nextElement();
                Certificate certificate = keyStore.getCertificate( alias );
                if ( certificate instanceof X509Certificate )
                {
                    certificateList.add( ( X509Certificate ) certificate );
                }
            }
            return certificateList.toArray( new X509Certificate[0] );
        }
        catch ( KeyStoreException e )
        {
            throw new CertificateException( Messages.StudioKeyStoreManager_CantReadTrustStore, e );
        }
    }


    /**
     * Removes the certificate from the key store.
     * 
     * @param certificate the certificate
     */
    public synchronized void removeCertificate( X509Certificate certificate ) throws CertificateException
    {
        if ( type == Type.File )
        {
            removeFromFileKeyStore( certificate );
        }
        else
        {
            removeFromMemoryKeyStore( certificate );
        }
    }


    /**
     * Removes the certificate from the memory key store.
     * 
     * @param certificate the certificate
     */
    private void removeFromMemoryKeyStore( X509Certificate certificate ) throws CertificateException
    {
        try
        {
            KeyStore memoryKeyStore = getMemoryKeyStore();
            removeFromKeyStore( certificate, memoryKeyStore );
        }
        catch ( Exception e )
        {
            throw new CertificateException( Messages.StudioKeyStoreManager_CantRemoveCertificateFromTrustStore, e );
        }
    }


    /**
     * Removes the certificate from the file key store.
     * 
     * @param certificate the certificate
     */
    private void removeFromFileKeyStore( X509Certificate certificate ) throws CertificateException
    {
        try
        {
            KeyStore fileKeyStore = getFileKeyStore();
            removeFromKeyStore( certificate, fileKeyStore );
            File file = ConnectionCorePlugin.getDefault().getStateLocation().append( filename ).toFile();
            fileKeyStore.store( new FileOutputStream( file ), password.toCharArray() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new CertificateException( Messages.StudioKeyStoreManager_CantRemoveCertificateFromTrustStore, e );
        }
    }


    private void removeFromKeyStore( X509Certificate certificate, KeyStore keyStore ) throws Exception
    {
        String alias = keyStore.getCertificateAlias( certificate );
        if ( alias != null )
        {
            keyStore.deleteEntry( alias );
        }
    }
}
