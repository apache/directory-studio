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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class implements a retry.
 * <p>
 * Format: "[&lt;retry interval&gt; &lt;# of retries&gt;]+"
 */
public class Retry
{
    /** The pattern used for parsing */
    private static final Pattern pattern = Pattern.compile( "^(([0-9]+) ([0-9]+|\\+))( ([0-9]+) ([0-9]+|\\+))*$" );

    /** The pairs */
    private List<RetryPair> pairs = new ArrayList<RetryPair>();

    /**
     * Gets a copy of a Retry object.
     *
     * @param syncRepl the initial Retry object
     * @return a copy of the given Retry object
     */
    public static Retry copy( Retry retry )
    {
        if ( retry != null )
        {
            Retry retryCopy = new Retry();
            
            for ( RetryPair retryPair : retry.getPairs() )
            {
                retryCopy.addPair( RetryPair.copy( retryPair ) );
            }
            
            return retryCopy;
        }
        
        return null;
    }


    /**
     * Gets a copy of the Retry object.
     *
     * @return a copy of the Retry object
     */
    public Retry copy()
    {
        return Retry.copy( this );
    }


    /**
     * Parses a retry string.
     *
     * @param s the string
     * @return a retry
     * @throws ParseException if an error occurs during parsing
     */
    public static Retry parse( String s ) throws ParseException
    {
        // Creating the retry 
        Retry retry = new Retry();

        // Matching the string
        Matcher matcher = pattern.matcher( s );

        // Checking the result
        if ( matcher.find() )
        {
            // Splitting the string into pieces
            String[] pieces = s.split( " " );

            // Checking we got a even number of pieces
            if ( ( pieces.length % 2 ) == 0 )
            {
                for ( int i = 0; i < pieces.length; i = i + 2 )
                {
                    retry.addPair( RetryPair.parse( pieces[i] + " " + pieces[i + 1] ) );
                }
            }
            else
            {
                throw new ParseException( "Unable to parse string '" + s + "' as a valid retry.", 0 );
            }
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid retry.", 0 );
        }

        return retry;
    }


    public void addPair( RetryPair pair )
    {
        pairs.add( pair );
    }


    public RetryPair[] getPairs()
    {
        return pairs.toArray( new RetryPair[0] );
    }


    public void removePair( RetryPair pair )
    {
        pairs.remove( pair );
    }


    public void setPairs( RetryPair[] pairs )
    {
        this.pairs = new ArrayList<RetryPair>();
        this.pairs.addAll( Arrays.asList( pairs ) );
    }


    public int size()
    {
        return pairs.size();
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for ( int i = 0; i < pairs.size(); i++ )
        {
            sb.append( pairs.get( i ).toString() );

            if ( i != ( pairs.size() - 1 ) )
            {
                sb.append( " " );
            }
        }

        return sb.toString();
    }
}
