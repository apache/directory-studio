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
 * This class implements an interval.
 * <p>
 * Format: "dd:hh:mm:ss"
 */
public class Interval
{
    /** The pattern used for parsing */
    private static final Pattern pattern = Pattern.compile( "^([0-9]{2}):([0-9]{2}):([0-9]{2}):([0-9]{2})$" );

    /** The days */
    private int days;

    /** The hours */
    private int hours;

    /** The minutes */
    private int minutes;

    /** The seconds */
    private int seconds;


    /**
     * Creates a new instance of Interval.
     *
     */
    public Interval()
    {
    }


    /**
     * Creates a new instance of Interval.
     *
     * @param days the days
     * @param hours the hours
     * @param minutes the minutes
     * @param seconds the seconds
     */
    public Interval( int days, int hours, int minutes, int seconds )
    {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }


    /**
     * Gets a copy of a Interval object.
     *
     * @param syncRepl the initial Interval object
     * @return a copy of the given Interval object
     */
    public static Interval copy( Interval interval )
    {
        if ( interval != null )
        {
            Interval intervalCopy = new Interval();

            intervalCopy.setDays( interval.getDays() );
            intervalCopy.setHours( interval.getHours() );
            intervalCopy.setMinutes( interval.getMinutes() );
            intervalCopy.setSeconds( interval.getSeconds() );

            return intervalCopy;
        }

        return null;
    }


    /**
     * Gets a copy of the Interval object.
     *
     * @return a copy of the Interval object
     */
    public Interval copy()
    {
        return Interval.copy( this );
    }


    /**
     * Parses an interval string.
     *
     * @param s the string
     * @return an interval
     * @throws ParseException if an error occurs during parsing
     */
    public static Interval parse( String s ) throws ParseException
    {
        // Creating the interval
        Interval interval = new Interval();

        // Matching the string
        Matcher matcher = pattern.matcher( s );

        // Checking the result
        if ( matcher.find() )
        {
            // Days
            String days = matcher.group( 1 );

            try
            {
                interval.setDays( Integer.parseInt( days ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ParseException( "Unable to convert days value '" + days + "' as an integer.", 0 );
            }

            // Hours
            String hours = matcher.group( 2 );

            try
            {
                interval.setHours( Integer.parseInt( hours ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ParseException( "Unable to convert hours value '" + hours + "' as an integer.", 0 );
            }

            // Minutes
            String minutes = matcher.group( 3 );

            try
            {
                interval.setMinutes( Integer.parseInt( minutes ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ParseException( "Unable to convert minutes value '" + minutes + "' as an integer.", 0 );
            }

            // Seconds
            String seconds = matcher.group( 4 );

            try
            {
                interval.setSeconds( Integer.parseInt( seconds ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ParseException( "Unable to convert seconds value '" + seconds + "' as an integer.", 0 );
            }
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid interval.", 0 );
        }

        return interval;
    }


    public int getDays()
    {
        return days;
    }


    public int getHours()
    {
        return hours;
    }


    public int getMinutes()
    {
        return minutes;
    }


    public int getSeconds()
    {
        return seconds;
    }


    public void setDays( int days )
    {
        this.days = days;
    }


    public void setHours( int hours )
    {
        this.hours = hours;
    }


    public void setMinutes( int minutes )
    {
        this.minutes = minutes;
    }


    public void setSeconds( int seconds )
    {
        this.seconds = seconds;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( intValToString( days ) );
        sb.append( ":" );
        sb.append( intValToString( hours ) );
        sb.append( ":" );
        sb.append( intValToString( minutes ) );
        sb.append( ":" );
        sb.append( intValToString( seconds ) );

        return sb.toString();

    }


    /**
     * Gets the string value for the given integer.
     * <p>
     * Makes sure the int is printed with two letters.
     *
     * @param val the integer
     * @return the string value for the given integer
     */
    private String intValToString( int val )
    {
        if ( val < 10 )
        {
            return "0" + val;

        }
        else
        {
            return "" + val;
        }
    }
}
