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
package org.apache.directory.studio.openldap.common.ui;

/**
 * The various LogLevel values :
 * <ul>
 * <li>none        0</li>
 * <li>trace       1</li>
 * <li>packets     2</li>
 * <li>args        4</li>
 * <li>conns       8</li>
 * <li>BER        16</li>
 * <li>filter     32</li>
 * <li>config     64</li>
 * <li>ACL       128</li>
 * <li>stats     256</li>
 * <li>stats2    512</li>
 * <li>shell    1024</li>
 * <li>parse    2048</li>
 * <li>sync    16384</li>
 * <li>any       -1</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum LogLevel
{
    NONE(0),
    TRACE(1),
    PACKETS(2),
    ARGS(4),
    CONNS(8),
    BER(16),
    FILTER(32),
    CONFIG(64),
    ACL(128),
    STATS(256),
    STATS2(512),
    SHELL(1024),
    PARSE(2048),
    // 4096 not used
    // 8196 not used
    SYNC(16384),
    // 327168 and -1 are equivalent
    ANY(-1);
    
    /** The inner value */
    private int value;
    
    
    /**
     * Creates a new instance of LogLevel.
     *
     * @param value The internal value
     */
    private LogLevel( int value )
    {
        this.value = value;
    }
    
    
    /**
     * @return The internal integer value
     */
    public int getValue()
    {
        return value;
    }
    
    
    /**
     * @param logLevel The integer value of the LogLevel
     * @return A String representation of the Log Level
     */
    public static String getLogLevelText( int logLevel )
    {
        if ( logLevel == NONE.value )
        {
            return "none";
        }

        if ( logLevel == ANY.value )
        {
            return "any";
        }
        
        StringBuilder sb = new StringBuilder();
        
        if ( ( logLevel & ACL.value ) != 0 )
        {
            sb.append( "ACL " );
        }
        
        if ( ( logLevel & ARGS.value ) != 0 )
        {
            sb.append( "args " );
        }
        
        if ( ( logLevel & BER.value ) != 0 )
        {
            sb.append( "BER " );
        }
        
        if ( ( logLevel & CONFIG.value ) != 0 )
        {
            sb.append( "config " );
        }
        
        if ( ( logLevel & CONNS.value ) != 0 )
        {
            sb.append( "conns " );
        }
        
        if ( ( logLevel & FILTER.value ) != 0 )
        {
            sb.append( "filter " );
        }
        
        if ( ( logLevel & PACKETS.value ) != 0 )
        {
            sb.append( "packets " );
        }
        
        if ( ( logLevel & PARSE.value ) != 0 )
        {
            sb.append( "parse " );
        }
        
        if ( ( logLevel & SHELL.value ) != 0 )
        {
            sb.append( "shell " );
        }
        
        if ( ( logLevel & STATS.value ) != 0 )
        {
            sb.append( "stats " );
        }
        
        if ( ( logLevel & STATS2.value ) != 0 )
        {
            sb.append( "stats2 " );
        }
        
        if ( ( logLevel & SYNC.value ) != 0 )
        {
            sb.append( "sync " );
        }
        
        if ( ( logLevel & TRACE.value ) != 0 )
        {
            sb.append( "trace " );
        }
        
        return sb.toString();
    }
    
    
    /**
     * Get the integer value associated with a name
     *
     * @param name The name we are looking for
     * @return The associated integer
     */
    public static int getIntegerValue( String name )
    {
        if ( ( name == null ) || ( name.length() == 0 ) )
        {
            throw new IllegalArgumentException( "Wrong LogLevel name : " + name );
        }
        
        if ( "acl".equalsIgnoreCase( name ) )
        {
            return ACL.value;
        }
        
        if ( "any".equalsIgnoreCase( name ) )
        {
            return ANY.value;
        }
        
        if ( "args".equalsIgnoreCase( name ) )
        {
            return ARGS.value;
        }
        
        if ( "ber".equalsIgnoreCase( name ) )
        {
            return BER.value;
        }
        
        if ( "config".equalsIgnoreCase( name ) )
        {
            return CONFIG.value;
        }
        
        if ( "conns".equalsIgnoreCase( name ) )
        {
            return CONNS.value;
        }
        
        if ( "filter".equalsIgnoreCase( name ) )
        {
            return FILTER.value;
        }
        
        if ( "none".equalsIgnoreCase( name ) )
        {
            return NONE.value;
        }
        
        if ( "packets".equalsIgnoreCase( name ) )
        {
            return PACKETS.value;
        }
        
        if ( "parse".equalsIgnoreCase( name ) )
        {
            return PARSE.value;
        }
        
        if ( "shell".equalsIgnoreCase( name ) )
        {
            return SHELL.value;
        }
        
        if ( "stats".equalsIgnoreCase( name ) )
        {
            return STATS.value;
        }
        
        if ( "stats2".equalsIgnoreCase( name ) )
        {
            return STATS2.value;
        }

        if ( "sync".equalsIgnoreCase( name ) )
        {
            return SYNC.value;
        }

        if ( "trace".equalsIgnoreCase( name ) )
        {
            return TRACE.value;
        }
        
        throw new IllegalArgumentException( "Wrong LogLevel name : " + name );
    }
    
    
    /**
     * Parses a LogLevel provided as a String. The format is the following :
     * <pre>
     * <logLevel> ::= ( Integer | Hex | <Name> )*
     * <name> ::= 'none' | 'any' | 'ACL' | 'args' | 'BER' | 'config' | 'conns' |
     *              'filter' | 'packets' | 'parse' | 'stats' | 'stats2' | 'sync' | 'trace'
     *              ;; Nore : those names are case insensitive
     * </pre>
     * TODO parseLogLevel.
     *
     * @param logLevelString
     * @return
     */
    public static int parseLogLevel( String logLevelString )
    {
        if ( ( logLevelString == null ) || ( logLevelString.length() == 0 ) )
        {
            return 0;
        }
        
        int currentPos = 0;
        char[] chars = logLevelString.toCharArray();
        int logLevel = 0;
        
        while ( currentPos < chars.length )
        {
            // Skip the ' ' at the beginning
            while ( chars[currentPos] == ' ' )
            {
                currentPos++;
            }
            
            // Now, start analysing what's next
            switch ( chars[currentPos] )
            {
                case 'a' :
                case 'A' :
                    // ACL, ANY or ARGS
                    if ( parseName( chars, currentPos, "ACL" ) )
                    {
                        // ACL
                        currentPos += 3;
                        logLevel |= ACL.value;
                    }
                    else if ( parseName( chars, currentPos, "ANY" ) )
                    {
                        // ANY
                        currentPos += 3;
                        logLevel |= ANY.value;
                    }
                    else if ( parseName( chars, currentPos, "ARGS" ) )
                    {
                        // ARGS
                        currentPos += 4;
                        logLevel |= ARGS.value;
                    }
                    else
                    {
                        // Wrong name
                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                    }
                    
                    break;
                    
                case 'b' :
                case 'B' :
                    // BER
                    if ( parseName( chars, currentPos, "BER" ) )
                    {
                        // BER
                        currentPos += 3;
                        logLevel |= BER.value;
                    }
                    else
                    {
                        // Wrong name
                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                    }
    
                    break;
                    
                case 'c' :
                case 'C' :
                    // CONFIG or CONNS
                    if ( parseName( chars, currentPos, "CONFIG" ) )
                    {
                        // CONFIG
                        currentPos += 6;
                        logLevel |= CONFIG.value;
                    }
                    else if ( parseName( chars, currentPos, "CONNS" ) )
                    {
                        // CONNS
                        currentPos += 5;
                        logLevel |= CONNS.value;
                    }
                    else
                    {
                        // Wrong name
                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                    }
    
                    break;
                    
                case 'f' :
                case 'F' :
                    // FILTER
                    if ( parseName( chars, currentPos, "FILTER" ) )
                    {
                        // FILTER
                        currentPos += 6;
                        logLevel |= FILTER.value;
                    }
                    else
                    {
                        // Wrong name
                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                    }
    
                    break;
                    
                case 'n' :
                case 'N' :
                    // NONE
                    if ( parseName( chars, currentPos, "NONE" ) )
                    {
                        // NONE
                        currentPos += 4;
                        logLevel |= NONE.value;
                    }
                    else
                    {
                        // Wrong name
                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                    }
    
                    break;
                    
                case 'p' :
                case 'P' :
                    // PACKETS or PARSE 
                    if ( parseName( chars, currentPos, "PACKETS" ) )
                    {
                        // PACKETS
                        currentPos += 7;
                        logLevel |= PACKETS.value;
                    }
                    else if ( parseName( chars, currentPos, "PARSE" ) )
                    {
                        // PARSE
                        currentPos += 5;
                        logLevel |= PARSE.value;
                    }
                    else
                    {
                        // Wrong name
                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                    }
    
                    break;
                    
                case 's' :
                case 'S' :
                    // SHELL, STATS, STATS2 or SYNC
                    if ( parseName( chars, currentPos, "SHELL" ) )
                    {
                        // SHELL
                        currentPos += 5;
                        logLevel |= SHELL.value;
                    }
                    else if ( parseName( chars, currentPos, "STATS" ) )
                    {
                        // STATS
                        currentPos += 5;
                        logLevel |= STATS.value;
                    }
                    else if ( parseName( chars, currentPos, "STATS2" ) )
                    {
                        // STATS2
                        currentPos += 6;
                        logLevel |= STATS2.value;
                    }
                    else if ( parseName( chars, currentPos, "SYNC" ) )
                    {
                        // SYNC
                        currentPos += 4;
                        logLevel |= SYNC.value;
                    }
                    else
                    {
                        // Wrong name
                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                    }
    
                    break;
                    
                case 't' :
                case 'T' :
                    // TRACE
                    if ( parseName( chars, currentPos, "TRACE" ) )
                    {
                        // TRACE
                        currentPos += 5;
                        logLevel |= TRACE.value;
                    }
                    else
                    {
                        // Wrong name
                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                    }
    
                    break;
                    
                case '0' :
                    // Numeric or hexa ?
                    currentPos++;
                    
                    if ( currentPos < chars.length )
                    {
                        if ( ( chars[currentPos] == 'x' ) || ( chars[currentPos] == 'X' ) )
                        {
                            // Hex
                            currentPos++;
                            boolean done = false;
                            int numValue = 0;

                            while ( ( currentPos < chars.length ) && !done )
                            {
                                switch ( chars[currentPos] )
                                {
                                    case '0' :
                                    case '1' :
                                    case '2' :
                                    case '3' :
                                    case '4' :
                                    case '5' :
                                    case '6' :
                                    case '7' :
                                    case '8' :
                                    case '9' :
                                        numValue = numValue*16 + chars[currentPos] - '0';
                                        currentPos++;
                                        break;
                                        
                                    case 'a' :
                                    case 'b' :
                                    case 'c' :
                                    case 'd' :
                                    case 'e' :
                                    case 'f' :
                                        numValue = numValue*16 + 10 + chars[currentPos] - 'a';
                                        currentPos++;
                                        break;
                                        
                                    case 'A' :
                                    case 'B' :
                                    case 'C' :
                                    case 'D' :
                                    case 'E' :
                                    case 'F' :
                                        numValue = numValue*16 + 10 + chars[currentPos] - 'A';
                                        currentPos++;
                                        break;
                                    
                                    case ' ' :
                                        logLevel |= numValue;
                                        done = true;
                                        break;
                                        
                                    default :
                                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                                }

                                // Special case : we are at the end of the STring
                                if ( !done )
                                {
                                    logLevel |= numValue;
                                }
                            }
                        }
                        else
                        {
                            // decimal value
                            boolean done = false;
                            int numValue = 0;

                            while ( ( currentPos < chars.length ) && !done )
                            {
                                switch ( chars[currentPos] )
                                {
                                    case '0' :
                                    case '1' :
                                    case '2' :
                                    case '3' :
                                    case '4' :
                                    case '5' :
                                    case '6' :
                                    case '7' :
                                    case '8' :
                                    case '9' :
                                        numValue = numValue*10 + chars[currentPos] - '0';
                                        currentPos++;
                                        break;
                                        
                                    case ' ' :
                                        logLevel |= numValue;
                                        done = true;
                                        break;
                                        
                                    default :
                                        throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                                }
                            }

                            // Special case : we are at the end of the STring
                            if ( !done )
                            {
                                logLevel |= numValue;
                            }
                        }
                    }
                    
                    break;
                    
                case '1' :
                case '2' :
                case '3' :
                case '4' :
                case '5' :
                case '6' :
                case '7' :
                case '8' :
                case '9' :
                    // Numeric
                    int numValue = chars[currentPos] - '0';
                    
                    currentPos++;
                    boolean done = false;
                    
                    while ( ( currentPos < chars.length ) && !done )
                    {
                        switch ( chars[currentPos] )
                        {
                            case '0' :
                            case '1' :
                            case '2' :
                            case '3' :
                            case '4' :
                            case '5' :
                            case '6' :
                            case '7' :
                            case '8' :
                            case '9' :
                                numValue = numValue*10 + chars[currentPos] - '0';
                                currentPos++;
                                break;
                                
                            case ' ' :
                                logLevel |= numValue;
                                done = true;
                                break;
                                
                            default :
                                throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
                        }
                        
                    }

                    // Special case : we are at the end of the STring
                    if ( !done )
                    {
                        logLevel |= numValue;
                    }
                    
                    break;
                    
                default :
                    throw new IllegalArgumentException( "Wrong LogLevel at " + currentPos + " : " + logLevelString );
            }
        }
        
        return logLevel;
    }
    
    
    /**
     * Checks that a LogLevel name is correct
     */
    private static boolean parseName( char[] chars, int pos, String expected )
    {
        for ( int current = 0; current < expected.length(); current++ )
        {
            if ( pos + current < chars.length )
            {
                char c = chars[pos+ current];
                char e = expected.charAt( current );
                
                if ( ( c != e ) && ( c != e + ( 'a' - 'A' ) ) )
                {
                    return false;
                }
            }
        }
        
        return true;
    }
}
