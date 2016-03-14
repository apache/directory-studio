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
package org.apache.directory.studio.openldap.syncrepl;


import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.api.util.Strings;


/**
 * This class implements a provider.
 * <p>
 * Format: "ldap[s]://<hostname>[:port]"
 */
public class Provider
{
    /** Constant used as port value when no port is provided */
    public static final int NO_PORT = -1;

    /** The pattern used for parsing */
    private static final Pattern pattern = Pattern
        .compile( "^[l|L][d|D][a|A][p|P]([s|S]?)://([^:]+)([:]([0-9]{1,5}))?$" );

    /** The LDAPS flag */
    private boolean isLdaps;

    /** The host */
    private String host;

    /** The port */
    private int port = NO_PORT;


    /**
     * Creates a new instance of Provider.
     */
    public Provider()
    {
        // TODO Auto-generated constructor stub
    }


    /**
     * Creates a new instance of Provider.
     *
     * @param isLdaps the LDAPS flag
     * @param host the host
     * @param port the port
     */
    public Provider( boolean isLdaps, String host, int port )
    {
        this.isLdaps = isLdaps;
        this.host = host;
        this.port = port;
    }


    /**
     * Gets a copy of a Provider object.
     *
     * @param provier the initial Provider object
     * @return a copy of the given Provider object
     */
    public static Provider copy( Provider provider )
    {
        if ( provider != null )
        {
            Provider providerCopy = new Provider();

            providerCopy.setHost( provider.getHost() );
            providerCopy.setPort( provider.getPort() );
            providerCopy.setLdaps( provider.isLdaps() );

            return providerCopy;
        }

        return null;
    }


    /**
     * Gets a copy of the Provider object.
     *
     * @return a copy of the Provider object
     */
    public Provider copy()
    {
        return Provider.copy( this );
    }


    /**
     * Parses a provider string.
     *
     * @param s the string
     * @return a provider
     * @throws ParseException if an error occurs during parsing
     */
    public static Provider parse( String s ) throws ParseException
    {
        // Creating the provider
        Provider provider = new Provider();

        // Matching the string
        Matcher matcher = pattern.matcher( s );

        // Checking the result
        if ( matcher.find() )
        {
            // LDAPS
            provider.setLdaps( "s".equalsIgnoreCase( matcher.group( 1 ) ) );

            // Host
            String host = matcher.group( 2 );

            if ( !Strings.isEmpty( host ) )
            {
                provider.setHost( host );
            }

            // Port
            String port = matcher.group( 4 );

            if ( !Strings.isEmpty( port ) )
            {
                try
                {
                    provider.setPort( Integer.parseInt( port ) );
                }
                catch ( NumberFormatException e )
                {
                    throw new ParseException( "Unable to convert port value '" + port + "' as an integer.", 0 );
                }
            }
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid provider.", 0 );
        }

        return provider;
    }


    public boolean isLdaps()
    {
        return isLdaps;
    }


    public String getHost()
    {
        return host;
    }


    public int getPort()
    {
        return port;
    }


    public void setLdaps( boolean isLdaps )
    {
        this.isLdaps = isLdaps;
    }


    public void setHost( String host )
    {
        this.host = host;
    }


    public void setPort( int port )
    {
        this.port = port;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "ldap" );

        if ( isLdaps )
        {
            sb.append( "s" );
        }

        sb.append( "://" );
        sb.append( host );

        if ( port != NO_PORT )
        {
            sb.append( ":" );
            sb.append( port );
        }

        return sb.toString();
    }
}
