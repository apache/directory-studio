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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import java.text.ParseException;


/**
 * This class represents the time span used for purge age and interval.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PurgeTimeSpan
{
    /** The days */
    protected int days = 0;

    /** The hours */
    protected int hours = 0;

    /** The minutes */
    protected int minutes = 0;

    /** The seconds */
    protected int seconds = 0;


    /**
     * Creates a new instance of PurgeTimeSpan.
     */
    public PurgeTimeSpan()
    {
    }


    /**
     * Creates a new instance of PurgeTimeSpan.
     *
     * @param s the string
     */
    public PurgeTimeSpan( String s ) throws ParseException
    {
        parse( s );
    }


    /**
     * Creates a new instance of PurgeTimeSpan.
     *
     * @param days the days
     * @param hours the hours
     * @param minutes the minutes
     * @param seconds the seconds
     */
    public PurgeTimeSpan( int days, int hours, int minutes, int seconds )
    {
        checkDaysArgument( days );
        checkHoursArgument( hours );
        checkMinutesSecondsArgument( minutes );
        checkMinutesSecondsArgument( seconds );

        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }


    /**
     * Checks the days argument.
     *
     * @param value the days argument
     * @throws IllegalArgumentException
     */
    private void checkDaysArgument( int value ) throws IllegalArgumentException
    {
        if ( checkDays( value ) )
        {
            throw new IllegalArgumentException( "Days need to be comprised between 0 and 99999." );
        }
    }


    /**
     * Checks the days value.
     *
     * @param value the days value
     * @return <code>true</code> if the days value is correct,
     *         <code>false</code> if not.
     */
    private boolean checkDays( int value )
    {
        return ( value < 0 ) || ( value > 99999 );
    }


    /**
     * Checks the hours argument.
     *
     * @param value the hours argument
     */
    private void checkHoursArgument( int value )
    {
        if ( checkHours( value ) )
        {
            throw new IllegalArgumentException( "Hours, minutes, or seconds need to be comprised between 0 and 99999." );
        }
    }


    /**
     * Checks the hours value.
     *
     * @param value the hours value
     * @return <code>true</code> if the hours value is correct,
     *         <code>false</code> if not.
     */
    private boolean checkHours( int value )
    {
        return ( value < 0 ) || ( value > 23 );
    }


    /**
     * Checks the minutes or seconds argument.
     *
     * @param value the minutes or seconds argument
     */
    private void checkMinutesSecondsArgument( int value )
    {
        if ( checkMinutesSeconds( value ) )
        {
            throw new IllegalArgumentException( "Hours, minutes, or seconds need to be comprised between 0 and 99999." );
        }
    }


    /**
     * Checks the minutes or seconds value.
     *
     * @param value the minutes or seconds value
     * @return <code>true</code> if the minutes or seconds value is correct,
     *         <code>false</code> if not.
     */
    private boolean checkMinutesSeconds( int value )
    {
        return ( value < 0 ) || ( value > 59 );
    }


    /**
     * Parse the given string.
     *
     * @param s the string
     * @throws ParseException in case of error during parsing.
     */
    private void parse( String s ) throws ParseException
    {
        if ( s == null )
        {
            throw new ParseException( "The string is null.", 0 );
        }

        // Removing leading and trailing whitespaces
        s = s.trim();

        // Checking the minimum size of the string
        // It should be at least 5 chars ("HH:MM")
        if ( s.length() < 5 )
        {
            throw new ParseException( "The string is too short.", 0 );
        }

        // Initializing parsing objects
        int position = 0;
        char c;
        StringBuilder buffer = new StringBuilder();
        boolean hoursParsed = false;
        boolean minutesParsed = false;

        try
        {
            while ( ( position < s.length() ) )
            {
                c = s.charAt( position );

                // Figure
                if ( ( '0' <= c ) && ( c <= '9' ) )
                {
                    buffer.append( c );
                }
                // Plus sign
                else if ( '+' == c )
                {
                    int days = Integer.parseInt( buffer.toString() );

                    if ( checkDays( days ) )
                    {
                        throw new ParseException( "Days need to be comprised between 0 and 99999.", position );
                    }
                    else
                    {
                        this.days = days;
                    }

                    buffer = new StringBuilder();
                }
                // Colon sign
                else if ( ':' == c )
                {
                    if ( !hoursParsed )
                    {
                        int hours = Integer.parseInt( buffer.toString() );

                        if ( checkHours( hours ) )
                        {
                            throw new ParseException( "Hours need to be comprised between 0 and 23.", position );
                        }
                        else
                        {
                            this.hours = hours;
                        }

                        hoursParsed = true;
                        buffer = new StringBuilder();
                    }
                    else
                    {
                        int minutes = Integer.parseInt( buffer.toString() );

                        if ( checkMinutesSeconds( minutes ) )
                        {
                            throw new ParseException( "Minutes need to be comprised between 0 and 59.", position );
                        }
                        else
                        {
                            this.minutes = minutes;
                        }

                        minutesParsed = true;
                        buffer = new StringBuilder();
                    }
                }
                else
                {
                    throw new ParseException( "Illegal character", position );
                }

                position++;
            }
        }
        catch ( NumberFormatException e )
        {
            throw new ParseException( e.getMessage(), position );
        }

        if ( !hoursParsed )
        {
            throw new ParseException( "Hours need to be comprised between 0 and 23.", position );
        }
        else if ( !minutesParsed )
        {
            int minutes = Integer.parseInt( buffer.toString() );

            if ( checkMinutesSeconds( minutes ) )
            {
                throw new ParseException( "Minutes need to be comprised between 0 and 59.", position );
            }
            else
            {
                this.minutes = minutes;
            }
        }
        else
        {
            int seconds = Integer.parseInt( buffer.toString() );

            if ( checkMinutesSeconds( seconds ) )
            {
                throw new ParseException( "Seconds need to be comprised between 0 and 59.", position );
            }
            else
            {
                this.seconds = seconds;
            }
        }
    }


    /**
     * @return the days
     */
    public int getDays()
    {
        return days;
    }


    /**
     * @return the hours
     */
    public int getHours()
    {
        return hours;
    }


    /**
     * @return the minutes
     */
    public int getMinutes()
    {
        return minutes;
    }


    /**
     * @return the seconds
     */
    public int getSeconds()
    {
        return seconds;
    }


    /**
     * @param days the days to set
     */
    public void setDays( int days )
    {
        this.days = days;
    }


    /**
     * @param hours the hours to set
     */
    public void setHours( int hours )
    {
        this.hours = hours;
    }


    /**
     * @param minutes the minutes to set
     */
    public void setMinutes( int minutes )
    {
        this.minutes = minutes;
    }


    /**
     * @param seconds the seconds to set
     */
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

        // Days (if needed)
        if ( days > 0 )
        {
            sb.append( days );
            sb.append( '+' );
        }

        // Hours
        sb.append( toString( hours ) );
        sb.append( ':' );

        // Minutes
        sb.append( toString( minutes ) );

        // Seconds (if needed)
        if ( seconds > 0 )
        {
            sb.append( ':' );
            sb.append( toString( seconds ) );
        }

        return sb.toString();
    }


    /**
     * Gets the string representation of an int
     * (prefixed with a 0 if needed).
     *
     * @param value the value
     * @return the string equivalent
     */
    private String toString( int value )
    {
        if ( ( value < 0 ) || ( value > 60 ) )
        {
            return "00";
        }
        else if ( value < 10 )
        {
            return "0" + value;
        }
        else
        {
            return Integer.toString( value );
        }
    }

}
