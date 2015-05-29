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
package org.apache.directory.studio.openldap.config.editor.wrappers;

import org.apache.directory.api.util.Strings;

/**
 * This class wraps the TimeLimit parameter :
 * <pre>
 * time      ::= 'time' timeLimit time-e
 * time-e    ::= ' time' timeLimit time-e | e
 * timeLimit ::= '.soft=' limit | '.hard=' hardLimit | '=' limit
 * limit     ::= 'unlimited' | 'none' | INT
 * hardLimit ::= 'soft' | limit
 * </pre>
 * 
 * Note : each of the limt is an Integer, so that we can have two states :
 * <ul>
 * <li>not existent</li>
 * <li>has a value</li>
 * </ul>
 * A -1 value means unlimited. Any other value is accepted, if > 0.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TimeLimitWrapper
{
    /** The global limit */
    private Integer globalLimit;

    /** The soft limit */
    private Integer softLimit;
    
    /** The hard limit */
    private Integer hardLimit;
    
    //Define some of the used constants
    private static final Integer UNLIMITED = Integer.valueOf( -1 );
    private static final Integer HARD_SOFT = Integer.valueOf( -3 );
    
    /**
     * Create a TimeLimitWrapper instance
     */
    private TimeLimitWrapper()
    {
    }
    
    
    /**
     * Create a TimeLimitWrapper instance
     * 
     * @param globalLimit The global limit
     * @param hardLimit The hard limit
     * @param softLimit The soft limit
     */
    public TimeLimitWrapper( Integer globalLimit, Integer hardLimit, Integer softLimit )
    {
        this.globalLimit = globalLimit;
        this.hardLimit = hardLimit;
        this.softLimit = softLimit;
    }
    
    
    /**
     * Create a TimeLimitWrapper instance from a String. 
     * 
     * @param timeLimitStr The String that contain the value
     */
    public TimeLimitWrapper( String timeLimitStr )
    {
        if ( timeLimitStr != null )
        {
            // use a lowercase version of the string
            String lowerCaseTimeLimitStr = timeLimitStr.toLowerCase();
            
            TimeLimitWrapper tmp = new TimeLimitWrapper();
            
            // Split the strings
            String[] limits = lowerCaseTimeLimitStr.split( " " );
            
            if ( limits != null )
            {
                // Parse each limit
                for ( String limit : limits )
                {
                    tmp.clear();
                    boolean result = parseLimit( tmp, limit );
                    
                    if ( !result )
                    {
                        globalLimit = null;
                        hardLimit = null;
                        softLimit = null;
                        break;
                    }
                    else
                    {
                        if ( tmp.globalLimit != null )
                        {
                            // replace the existing global limit, nullify the hard and soft limit
                            globalLimit = tmp.globalLimit;
                            hardLimit = null;
                            softLimit = null;
                        }
                        else
                        {
                            // We don't set the soft and hard limit of the global limit is not null
                            if ( globalLimit == null )
                            {
                                if ( tmp.softLimit != null )
                                {
                                    softLimit = tmp.softLimit;
                                    
                                    if ( hardLimit != null )
                                    {
                                        if ( hardLimit.equals( HARD_SOFT ) || hardLimit.equals( softLimit ) )
                                        {
                                            // Special case : we have had a time.hard=soft before,
                                            // or the hard and soft limit are equals : we set the global limit
                                            globalLimit = softLimit;
                                            softLimit = null;
                                            hardLimit = null;
                                        }
                                    }
                                }
                                else if ( tmp.hardLimit != null )
                                {
                                    if ( ( tmp.hardLimit.equals( HARD_SOFT ) && ( softLimit != null ) ) || tmp.hardLimit.equals( softLimit ) )
                                    {
                                        // special case, softLimit was set and hardLimit was time.hard=soft,
                                        // or is equal to softLimit
                                        globalLimit = softLimit;
                                        softLimit = null;
                                        hardLimit = null;
                                    }
                                    else
                                    {
                                        hardLimit = tmp.hardLimit;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    /**
     * Clear the TimeLimitWrapper (reset all the values to null)
     */
    public void clear()
    {
        globalLimit = null;
        softLimit = null;
        hardLimit = null;
    }
    
    
    /**
     * Parse a single limit :
     * <pre>
     * timeLimit ::= 'size' ( '.hard=' hardLimit | '.soft=' limit | '=' limit )
     * limit     ::= 'unlimited' | 'none' | INT
     * hardLimit ::= 'soft' | limit
     * </pre>
     * @param tlw
     * @param limitStr
     */
    private static boolean parseLimit( TimeLimitWrapper tlw, String limitStr )
    {
        int pos = 0;
        
        // The timelimit always starts with a "time"
        if ( limitStr.startsWith( "time" ) )
        {
            pos += 4;
            
            // A global or hard/soft ?
            if ( limitStr.startsWith( "=", pos ) )
            {
                // Global : get the limit
                pos++;
                
                if ( limitStr.startsWith( "unlimited", pos ) )
                {
                    pos += 9;
                    tlw.globalLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( "none", pos ) )
                {
                    pos += 4;
                    tlw.globalLimit = UNLIMITED;
                }
                else
                {
                    String integer = getInteger( limitStr, pos );
                    
                    if ( integer != null )
                    {
                        pos += integer.length();
                        
                        Integer value = Integer.valueOf( integer );
                        
                        if ( value > UNLIMITED )
                        {
                            tlw.globalLimit = value;
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            else if ( limitStr.startsWith( ".hard=", pos ) )
            {
                // Hard limit : get the hard limit
                pos += 6;
                
                if ( limitStr.startsWith( "unlimited", pos ) )
                {
                    pos += 9;
                    
                    tlw.hardLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( "none", pos ) )
                {
                    pos += 4;
                    
                    tlw.hardLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( "soft", pos ) )
                {
                    pos += 4;
                    tlw.globalLimit = HARD_SOFT;
                }
                else
                {
                    String integer = getInteger( limitStr, pos );
                    
                    if ( integer != null )
                    {
                        pos += integer.length();
                        Integer value =  Integer.valueOf( integer );
                        
                        if ( value >= UNLIMITED )
                        {
                            tlw.hardLimit = value;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            else if ( limitStr.startsWith( ".soft=", pos ) )
            {
                // Soft limit : get the limit
                pos += 6;

                if ( limitStr.startsWith( "unlimited", pos ) )
                {
                    pos += 9;
                    
                    tlw.softLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( "none", pos ) )
                {
                    pos += 4;
                    
                    tlw.softLimit = UNLIMITED;
                }
                else
                {
                    String integer = getInteger( limitStr, pos );
                    
                    if ( integer != null )
                    {
                        pos += integer.length();
                        Integer value = Integer.valueOf( integer );

                        if ( value > UNLIMITED )
                        {
                            tlw.softLimit = value;
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            else
            {
                // This is wrong
                return false;
            }
        }
        else
        {
            // This is wrong
            return false;
        }
        
        // last check : the pos should be equal to the limitStr length
        return ( pos == limitStr.length() );
    }
    
    
    /**
     * Get an integer out of a String. Return null if we don't find any.
     */
    private static String getInteger( String str, int pos )
    {
        for ( int i = pos; i < str.length(); i++ )
        {
            char c = str.charAt( i );
            
            if ( ( c < '0') && ( c > '9' ) )
            {
                if ( i == pos )
                {
                    return null;
                }
                else
                {
                    return str.substring( pos, i );
                }
            }
        }
        
        return str.substring( pos );
    }
    
    
    /**
     * Tells if the TimeLimit element is valid or not
     * @param timeLimitStr the timeLimit String to check
     * @return true if the values are correct, false otherwise
     */
    public static boolean isValid( String timeLimitStr )
    {
        if ( !Strings.isEmpty( timeLimitStr ) )
        {
            // use a lowercase version of the string
            String lowerCaseTimeLimitStr = timeLimitStr.toLowerCase();
            
            TimeLimitWrapper tmp = new TimeLimitWrapper();
            
            // Split the strings
            String[] limits = lowerCaseTimeLimitStr.split( " " );
            
            if ( limits != null )
            {
                // Parse each limit
                for ( String limit : limits )
                {
                    boolean result = parseLimit( tmp, limit );
                    
                    if ( !result )
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }
    
    
    /**
     * @return the globalLimit
     */
    public Integer getGlobalLimit()
    {
        return globalLimit;
    }


    /**
     * @param globalLimit the globalLimit to set
     */
    public void setGlobalLimit( Integer globalLimit )
    {
        this.globalLimit = globalLimit;
    }


    /**
     * @return the softLimit
     */
    public Integer getSoftLimit()
    {
        return softLimit;
    }


    /**
     * @param softLimit the softLimit to set
     */
    public void setSoftLimit( Integer softLimit )
    {
        this.softLimit = softLimit;
    }


    /**
     * @return the hardLimit
     */
    public Integer getHardLimit()
    {
        return hardLimit;
    }


    /**
     * @param hardLimit the hardLimit to set
     */
    public void setHardLimit( Integer hardLimit )
    {
        this.hardLimit = hardLimit;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        if ( globalLimit != null )
        {
            // The globalLimoit overrides the soft and hard limit
            sb.append( "time=" );
            
            if ( globalLimit == -1 )
            {
                sb.append( "unlimited" );
            }
            else if ( globalLimit >= 0 )
            {
                sb.append( globalLimit );
            }
        }
        else
        {
            if ( hardLimit != null )
            {
                // First check the hard limit, has it can be set to be equal to soft limit
                if ( softLimit != null )
                {
                    if ( hardLimit.equals( softLimit ) )
                    {
                        // If hard and soft are set and equals, e use the global limit instead
                        sb.append( "time=" );
                        
                        if ( hardLimit == -1 )
                        {
                            sb.append( "unlimited" );
                        }
                        else if ( hardLimit >= 0 )
                        {
                            sb.append( hardLimit );
                        }
                    }
                    else
                    {
                        // We have both values
                        sb.append( "time.hard=" );
                        
                        if ( hardLimit == -1 )
                        {
                            sb.append( "unlimited" );
                        }
                        else if ( hardLimit >= 0 )
                        {
                            sb.append( hardLimit );
                        }

                        sb.append( " time.soft=" );
                        
                        if ( softLimit == -1 )
                        {
                            sb.append( "unlimited" );
                        }
                        else if ( softLimit >= 0 )
                        {
                            sb.append( softLimit );
                        }
                    }
                }
                else
                {
                    // Only an hard limit
                    sb.append( "time.hard=" );
                    
                    if ( hardLimit == -1 )
                    {
                        sb.append( "unlimited" );
                    }
                    else if ( hardLimit >= 0 )
                    {
                        sb.append( hardLimit );
                    }
                }
            }
            else if ( softLimit != null )
            {
                // Only a soft limit
                sb.append( "time.soft=" );
                
                if ( softLimit == -1 )
                {
                    sb.append( "unlimited" );
                }
                else if ( softLimit >= 0 )
                {
                    sb.append( softLimit );
                }
            }
        }
        
        return sb.toString();
    }
}
