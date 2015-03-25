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
 * This class implements a retry pair.
 * <p>
 * Format: "&lt;retry interval&gt; &lt;# of retries&gt;"
 */
public class RetryPair
{
    /** The '+' retries value */
    public static final int PLUS = -1;

    /** The pattern used for parsing */
    private static final Pattern pattern = Pattern.compile( "^([0-9]+) ([0-9]+|\\+)$" );

    /** The interval */
    private int interval;

    /** The retries */
    private int retries;


    /**
     * Gets a copy of a RetryPair object.
     *
     * @param syncRepl the initial RetryPair object
     * @return a copy of the given RetryPair object
     */
    public static RetryPair copy( RetryPair retryPair )
    {
        if ( retryPair != null )
        {
            RetryPair retryPairCopy = new RetryPair();

            retryPairCopy.setInterval( retryPair.getInterval() );
            retryPairCopy.setRetries( retryPair.getRetries() );

            return retryPairCopy;
        }

        return null;
    }


    /**
     * Gets a copy of the RetryPair object.
     *
     * @return a copy of the RetryPair object
     */
    public RetryPair copy()
    {
        return RetryPair.copy( this );
    }


    /**
     * Parses a retry pair string.
     *
     * @param s the string
     * @return a retry pair
     * @throws ParseException if an error occurs during parsing
     */
    public static RetryPair parse( String s ) throws ParseException
    {
        // Creating the retry pair
        RetryPair retryPair = new RetryPair();

        // Matching the string
        Matcher matcher = pattern.matcher( s );

        // Checking the result
        if ( matcher.find() )
        {
            // Interval
            String interval = matcher.group( 1 );

            try
            {
                retryPair.setInterval( Integer.parseInt( interval ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ParseException( "Unable to convert interval value '" + interval + "' as an integer.", 0 );
            }

            // Retries
            String retries = matcher.group( 2 );

            if ( "+".equalsIgnoreCase( retries ) )
            {
                retryPair.setRetries( PLUS );
            }
            else
            {
                try
                {
                    retryPair.setRetries( Integer.parseInt( retries ) );
                }
                catch ( NumberFormatException e )
                {
                    throw new ParseException( "Unable to convert retries value '" + retries + "' as an integer.", 0 );
                }
            }
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid retry pair.", 0 );
        }

        return retryPair;
    }


    public int getInterval()
    {
        return interval;
    }


    public int getRetries()
    {
        return retries;
    }


    public void setInterval( int interval )
    {
        this.interval = interval;
    }


    public void setRetries( int retries )
    {
        this.retries = retries;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( interval );
        sb.append( " " );

        if ( retries == PLUS )
        {
            sb.append( "+" );
        }
        else
        {
            sb.append( retries );
        }

        return sb.toString();
    }
}
