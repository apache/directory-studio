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
package org.apache.directory.studio.apacheds.configuration.v2;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.name.Dn;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;


/**
 * This class implements a ContentDescriber for Apache DS Configuration file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDS2ConfigurationContentDescriber implements ITextContentDescriber
{
    /** The maximum number of entries to search before determining the file as invalid */
    private static int MAX_NUMBER_ENTRIES_SEARCH = 10;

    /** The Dn of the config entry ('ou=config')*/
    private Dn configEntryDn;

    /** The Dn of the directory service entry ('ads-directoryServiceId=default,ou=config') */
    private Dn directoryServiceDn;


    /**
     * Creates a new instance of ApacheDS2ConfigurationContentDescriber.
     */
    public ApacheDS2ConfigurationContentDescriber()
    {
        // Initializing DNs
        try
        {
            configEntryDn = new Dn( "ou=config" );
            directoryServiceDn = new Dn( "ads-directoryServiceId=default,ou=config" );
        }
        catch ( LdapInvalidDnException e )
        {
            // Will never occur.
        }

    }


    /**
     * {@inheritDoc}
     */
    public int describe( Reader contents, IContentDescription description ) throws IOException
    {
        LdifReader reader = null;
        try
        {
            reader = new LdifReader( contents );
            return isValid( reader );
        }
        catch ( LdapException e )
        {
            return ITextContentDescriber.INVALID;
        }
        finally
        {
            if ( reader != null )
            {
                reader.close();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public int describe( InputStream contents, IContentDescription description ) throws IOException
    {
        LdifReader reader = null;
        try
        {
            reader = new LdifReader( contents );
            return isValid( reader );
        }
        catch ( LdapException e )
        {
            return ITextContentDescriber.INVALID;
        }
        finally
        {
            if ( reader != null )
            {
                reader.close();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public QualifiedName[] getSupportedOptions()
    {
        return new QualifiedName[0];
    }


    /**
     * Indicates if the given {@link Reader} is a valid server configuration.
     *
     * @param reader
     *      the LDIF reader
     * @return
     *      <code>ITextContentDescriber.VALID</code> if the given LDIF reader is a valid server 
     *      configuration, <code>ITextContentDescriber.INVALID</code> if not
     */
    private int isValid( LdifReader reader )
    {
        int checkedEntries = 0;
        boolean configEntryFound = false;
        boolean directoryServiceEntryFound = false;

        while ( reader.hasNext() && ( checkedEntries < MAX_NUMBER_ENTRIES_SEARCH ) )
        {
            if ( configEntryFound && directoryServiceEntryFound )
            {
                // Getting out of the loop if we found both entries
                break;
            }

            LdifEntry entry = reader.next();
            checkedEntries++;

            // Checking if this is the config entry
            if ( !configEntryFound )
            {
                if ( configEntryDn.getName().equalsIgnoreCase( entry.getDn().getNormName() ) )
                {
                    configEntryFound = true;
                    continue;
                }
            }

            // Checking if this is the directory service entry
            if ( !directoryServiceEntryFound )
            {
                if ( directoryServiceDn.getName().equalsIgnoreCase( entry.getDn().getNormName() ) )
                {
                    directoryServiceEntryFound = true;
                    continue;
                }
            }
        }

        // Checking if we found both entries
        if ( configEntryFound && directoryServiceEntryFound )
        {
            return ITextContentDescriber.VALID;
        }
        else
        {
            return ITextContentDescriber.INVALID;
        }
    }
}
