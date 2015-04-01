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
package org.apache.directory.studio.openldap.config;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.util.tree.DnNode;
import org.apache.directory.studio.openldap.config.model.io.ConfigurationException;
import org.apache.directory.studio.openldap.config.model.io.ConfigurationUtils;

/**
 * This class implements an reader for an expanded LDIF format.
 * <p>
 * This format consists in a hierarchy ldif files, each one representing an entry,
 * and hierarchically organized in associated directories.
 * <p>
 * NOTE: This implementation is specific to the OpenLDAP "slapd.d" directory.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExpandedLdifUtils 
{
    /** The LDIF file extension (.ldif) */
    private static final String LDIF_FILE_EXTENSION = ".ldif";

    /** A filter used to pick all the LDIF files */
    private static FileFilter ldifFileFilter = new FileFilter()
    {
        public boolean accept( File dir )
        {
            if ( dir.getName().endsWith( LDIF_FILE_EXTENSION ) )
            {
                return dir.isFile();
            }
            else
            {
                return false;
            }
        }
    };


    /**
     * Reads the given directory location.
     *
     * @param directory the directory
     * @return the corresponding node hierarchy
     * @throws IOException if an error occurred
     */
    public static DnNode<Entry> read( File directory ) throws IOException, LdapException
    {
        DnNode<Entry> tree = new DnNode<Entry>();

        readDirectory( directory, Dn.EMPTY_DN, tree );

        return tree;
    }


    /**
     * Reads the given directory.
     *
     * @param directory the directory
     * @param tree the tree
     * @return the corresponding node
     * @throws IOException
     * @throws LdapException
     */
    private static void readDirectory( File directory, Dn parentDn, DnNode<Entry> tree ) throws IOException,
        LdapException
    {
        if ( directory != null )
        {
            // Checking if the directory exists
            if ( !directory.exists() )
            {
                throw new IOException( "Location '" + directory + "' does not exist." );
            }

            // Checking if the directory is a directory
            if ( !directory.isDirectory() )
            {
                throw new IOException( "Location '" + directory + "' is not a directory." );
            }

            // Checking if the directory is readable
            if ( !directory.canRead() )
            {
                throw new IOException( "Directory '" + directory + "' can not be read." );
            }

            // Getting the array of ldif files
            File[] ldifFiles = directory.listFiles( ldifFileFilter );

            if ( ( ldifFiles != null ) && ( ldifFiles.length != 0 ) )
            {
                LdifReader ldifReader = new LdifReader();

                // Looping on LDIF files
                for ( File ldifFile : ldifFiles )
                {
                    // Checking if the LDIF file is a file
                    if ( !ldifFile.isFile() )
                    {
                        throw new IOException( "Location '" + ldifFile + "' is not a file." );
                    }

                    // Checking if the LDIF file is readable
                    if ( !ldifFile.canRead() )
                    {
                        throw new IOException( "LDIF file '" + ldifFile + "' can not be read." );
                    }

                    // Computing the DN of the entry
                    Dn entryDn = parentDn.add( stripExtension( ldifFile.getName() ) );

                    // Reading the LDIF file
                    List<LdifEntry> ldifEntries = null;

                    try
                    {
                        ldifEntries = ldifReader.parseLdifFile( ldifFile.getAbsolutePath() );
                    }
                    finally
                    {
                        ldifReader.close();
                    }

                    // The LDIF file should have only one entry
                    if ( ( ldifEntries != null ) && ( ldifEntries.size() == 1 ) )
                    {
                        // Getting the LDIF entry
                        LdifEntry ldifEntry = ldifEntries.get( 0 );

                        if ( ldifEntry != null )
                        {
                            // Getting the entry
                            Entry entry = ldifEntry.getEntry();

                            if ( entry != null )
                            {
                                // Refactoring the DN to set the "FULL" DN of the entry
                                entry.setDn( entryDn );

                                // Creating the new entry node
                                tree.add( entryDn, entry );

                                // Creating a file without the LDIF extension (corresponding to children directory)
                                File childrenDirectoryFile = new File( stripExtension( ldifFile.getAbsolutePath() ) );

                                // If the directory exists, recursively read it
                                if ( childrenDirectoryFile.exists() )
                                {
                                    readDirectory( childrenDirectoryFile, entryDn, tree );
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Strips the file extension.
     *
     * @param path the path
     * @return the path without the file extension
     */
    private static String stripExtension( String path )
    {
        if ( ( path != null ) && ( path.length() > 0 ) )
        {
            return path.substring( 0, path.lastIndexOf( '.' ) );
        }

        return null;
    }


    /**
     * Writes the given tree to the directory.
     *
     * @param tree the tree
     * @param directory the directory
     * @throws IOException if an error occurs
     */
    public static void write( DnNode<Entry> tree, File directory ) throws IOException
    {
        try
        {
            write( tree, ConfigurationUtils.getDefaultConfigurationDn(), directory );
        }
        catch ( ConfigurationException e )
        {
            throw new IOException( e );
        }
    }


    /**
     * Writes the entry and its children from the given tree and DN, to the directory.
     *
     * @param tree the tree
     * @param dn the dn of the entry
     * @param directory the directory
     * @throws IOException if an error occurs
     */
    public static void write( DnNode<Entry> tree, Dn dn, File directory ) throws IOException
    {
        // Checking if the directory is null
        if ( directory == null )
        {
            throw new IOException( "Location is 'null'." );
        }

        // Checking if the directory exists
        if ( !directory.exists() )
        {
            throw new IOException( "Location '" + directory + "' does not exist." );
        }

        // Checking if the directory is a directory
        if ( !directory.isDirectory() )
        {
            throw new IOException( "Location '" + directory + "' is not a directory." );
        }

        // Checking if the directory is writable
        if ( !directory.canWrite() )
        {
            throw new IOException( "Directory '" + directory + "' can not be written." );
        }

        // Getting the entry node
        DnNode<Entry> node = tree.getNode( dn );

        // Only creating a file if the node contains an entry
        if ( node.hasElement() )
        {
            // Getting the entry
            Entry entry = node.getElement();

            // Getting the DN of the entry
            Dn entryDn = entry.getDn();

            // Setting the RDN as DN (specific to OpenLDAP implementation)
            try
            {
                entry.setDn( new Dn( entryDn.getRdn() ) );
            }
            catch ( LdapInvalidDnException e )
            {
                throw new IOException( e );
            }

            // Writing the LDIF file to the disk
            FileWriter fw = null;
            try
            {
                fw = new FileWriter( new File( directory, getLdifFilename( entry.getDn() ) ) );
                fw.write( new LdifEntry( entry ).toString() );
            }
            finally
            {
                // Closing the file write in any case
                if ( fw != null )
                {
                    fw.close();
                }
            }

            // Checking if the entry has children
            if ( node.hasChildren() )
            {
                // Creating the child directory on disk
                File childDirectory = new File( directory, getFilename( entry.getDn() ) );
                childDirectory.mkdir();

                // Iterating on all children
                for ( DnNode<Entry> childNode : node.getChildren().values() )
                {
                    if ( childNode.hasElement() )
                    {
                        // Recursively call the method with the child node and directory
                        write( tree, childNode.getDn(), childDirectory );
                    }
                }
            }
        }
    }


    /**
     * Gets the filename for the given DN.
     *
     * @param dn the DN
     * @return the associated LDIF filename
     */
    private static String getLdifFilename( Dn dn )
    {
        String filename = getFilename( dn );

        if ( filename != null )
        {
            return filename + LDIF_FILE_EXTENSION;
        }

        return null;
    }


    /**
     * Gets the filename for the given DN.
     *
     * @param dn the DN
     * @return the associated LDIF filename
     */
    private static String getFilename( Dn dn )
    {
        if ( ( dn != null ) && ( dn.size() > 0 ) )
        {
            return dn.getRdn().toString();
        }

        return null;
    }
}
