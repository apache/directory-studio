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


/**
 * This class implements a keep alive.
 * <p>
 * Format: "&lt;idle&gt;:&lt;probes&gt;:&lt;interval&gt;"
 */
public class KeepAlive
{
    /** The pattern used for parsing */
    private static final Pattern pattern = Pattern.compile( "^([0-9]+):([0-9]+):([0-9]+)$" );

    /** The idle */
    private int idle;

    /** The probes */
    private int probes;

    /** The interval */
    private int interval;


    /**
     * Creates a new instance of KeepAlive.
     */
    public KeepAlive()
    {
    }


    /**
     * Creates a new instance of KeepAlive.
     *
     * @param idle the idle
     * @param probes the probes
     * @param interval the interval
     */
    public KeepAlive( int idle, int probes, int interval )
    {
        this.idle = idle;
        this.probes = probes;
        this.interval = interval;
    }


    /**
     * Gets a copy of a KeepAlive object.
     *
     * @param syncRepl the initial KeepAlive object
     * @return a copy of the given KeepAlive object
     */
    public static KeepAlive copy( KeepAlive keepAlive )
    {
        if ( keepAlive != null )
        {
            KeepAlive keepAliveCopy = new KeepAlive();

            keepAliveCopy.setIdle( keepAlive.getIdle() );
            keepAliveCopy.setProbes( keepAlive.getProbes() );
            keepAliveCopy.setInterval( keepAlive.getInterval() );

            return keepAliveCopy;
        }

        return null;
    }


    /**
     * Gets a copy of the KeepAlive object.
     *
     * @return a copy of the KeepAlive object
     */
    public KeepAlive copy()
    {
        return KeepAlive.copy( this );
    }


    /**
     * Parses a keep alive string.
     *
     * @param s the string
     * @return a keep alive
     * @throws ParseException if an error occurs during parsing
     */
    public static KeepAlive parse( String s ) throws ParseException
    {
        // Creating the keep alive
        KeepAlive keepAlive = new KeepAlive();

        // Matching the string
        Matcher matcher = pattern.matcher( s );

        // Checking the result
        if ( matcher.find() )
        {
            // Idle
            String idle = matcher.group( 1 );

            try
            {
                keepAlive.setIdle( Integer.parseInt( idle ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ParseException( "Unable to convert idle value '" + idle + "' as an integer.", 0 );
            }

            // Probes
            String probes = matcher.group( 2 );

            try
            {
                keepAlive.setProbes( Integer.parseInt( probes ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ParseException( "Unable to convert probes value '" + probes + "' as an integer.", 0 );
            }

            // Interval
            String interval = matcher.group( 3 );

            try
            {
                keepAlive.setInterval( Integer.parseInt( interval ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ParseException( "Unable to convert interval value '" + interval + "' as an integer.", 0 );
            }
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid keep alive.", 0 );
        }

        return keepAlive;
    }


    public int getIdle()
    {
        return idle;
    }


    public int getProbes()
    {
        return probes;
    }


    public int getInterval()
    {
        return interval;
    }


    public void setIdle( int idle )
    {
        this.idle = idle;
    }


    public void setProbes( int probes )
    {
        this.probes = probes;
    }


    public void setInterval( int interval )
    {
        this.interval = interval;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( idle );
        sb.append( ":" );
        sb.append( probes );
        sb.append( ":" );
        sb.append( interval );

        return sb.toString();
    }
}
