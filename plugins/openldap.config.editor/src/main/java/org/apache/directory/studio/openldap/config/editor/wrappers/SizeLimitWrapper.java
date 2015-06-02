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
 * This class wraps the SizeLimit parameter :
 * <pre>
 * size      ::= 'size' sizeLimit size-e
 * size-e    ::= ' size' sizeLimit size-e | e
 * sizeLimit ::= '.soft=' limit | '.hard=' hardLimit | '.pr=' prLimit | '.prtotal=' prTLimit
 *                  | '.unchecked=' uLimit | '=' limit
 * limit     ::= 'unlimited' | 'none' | INT
 * hardLimit ::= 'soft' | limit
 * ulimit    ::= 'disabled' | limit
 * prLimit   ::= 'noEstimate' | limit
 * prTLimit  ::= ulimit | 'hard'
 * </pre>
 * 
 * Note : each of the limit is an Integer, so that we can have two states :
 * <ul>
 * <li>not existent</li>
 * <li>has a value</li>
 * </ul>
 * A -1 value means unlimited. Any other value is accepted, if > 0.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SizeLimitWrapper
{
    /** The global limit */
    private Integer globalLimit;

    /** The soft limit */
    private Integer softLimit;
    
    /** The hard limit */
    private Integer hardLimit;
    
    /** The unchecked limit */
    private Integer uncheckedLimit;
    
    /** The PR limit */
    private Integer prLimit;
    
    /** The PRTotal limit */
    private Integer prTotalLimit;

    /** The noEstimate flag */
    private boolean noEstimate;
    
    //Define some of the used constants
    public static final Integer HARD_SOFT = Integer.valueOf( -3 );
    public static final Integer PR_DISABLED = Integer.valueOf( -2 );
    public static final Integer PR_HARD = Integer.valueOf( 0 );
    public static final Integer UC_DISABLED = Integer.valueOf( 0 );
    public static final Integer UNLIMITED = Integer.valueOf( -1 );
    
    public static final String DISABLED_STR = "disabled";
    public static final String HARD_STR = "hard";
    public static final String NONE_STR = "none";
    public static final String SOFT_STR = "soft";
    public static final String UNCHECKED_STR = "unchecked";
    public static final String UNLIMITED_STR = "unlimited";



    /**
     * Create a SizeLimitWrapper instance
     */
    private SizeLimitWrapper()
    {
    }
    
    
    /**
     * Create a SizeLimitWrapper instance
     * 
     * @param globalLimit The global limit
     * @param hardLimit The hard limit
     * @param softLimit The soft limit
     * @param uncheckedLimit The unchecked limit
     * @param prLimit The pr limit
     * @param prTotalLimit The prTotal limit
     */
    public SizeLimitWrapper( Integer globalLimit, Integer hardLimit, Integer softLimit, Integer uncheckedLimit, 
        Integer prLimit, Integer prTotalLimit, boolean noEstimate )
    {
        this.globalLimit = globalLimit;
        this.hardLimit = hardLimit;
        this.softLimit = softLimit;
        this.uncheckedLimit = uncheckedLimit;
        this.prLimit = prLimit;
        this.prTotalLimit = prTotalLimit;
        this.noEstimate = noEstimate;
    }
    
    
    /**
     * Create a SizeLimitWrapper instance from a String. 
     * 
     * @param sizeLimitStr The String that contain the value
     */
    public SizeLimitWrapper( String sizeLimitStr )
    {
        if ( sizeLimitStr != null )
        {
            // use a lowercase version of the string
            String lowerCaseSizeLimitStr = sizeLimitStr.toLowerCase();
            
            SizeLimitWrapper tmp = new SizeLimitWrapper();
            
            // Split the strings
            String[] limits = lowerCaseSizeLimitStr.split( " " );
            
            if ( limits != null )
            {
                // Parse each limit
                for ( String limit : limits )
                {
                    tmp.clear();
                    boolean result = parseLimit( tmp, limit );
                    
                    if ( !result )
                    {
                        // No need to continue if the value is wrong
                        clear();
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
                                            // Special case : we have had a size.hard=soft before,
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
                                        // special case, softLimit was set and hardLimit was size.hard=soft,
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
                            
                            // Deal with the unchecked parameter
                            if ( tmp.uncheckedLimit != null )
                            {
                                uncheckedLimit = tmp.uncheckedLimit;
                            }
                            
                            // Deal with the PR limit
                            if ( tmp.prLimit != null )
                            {
                                prLimit = tmp.prLimit;
                            }
                            
                            // Special case for noEstimate
                            noEstimate = tmp.noEstimate;
                            
                            // Last, not least, prTotalLimit
                            if ( tmp.prTotalLimit != null )
                            {
                                prTotalLimit = tmp.prTotalLimit;
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    /**
     * Clear the SizeLimitWrapper (reset all the values to null)
     */
    public void clear()
    {
        globalLimit = null;
        softLimit = null;
        hardLimit = null;
        uncheckedLimit = null;
        prLimit = null;
        prTotalLimit = null;
        noEstimate = false;
    }
    
    
    /**
     * Parse a single limit :
     * <pre>
     * size      ::= 'size' sizeLimit size-e
     * size-e    ::= ' size' sizeLimit size-e | e
     * sizeLimit ::= '.soft=' limit | '.hard=' hardLimit | '.pr=' prLimit | '.prtotal=' prTLimit
     *                  | '.unchecked=' uLimit | '=' limit
     * limit     ::= 'unlimited' | 'none' | INT
     * ulimit    ::= 'disabled' | limit
     * prLimit   ::= 'noEstimate' | limit
     * prTLimit  ::= ulimit | 'hard'
     * </pre>
     * @param slw
     * @param limitStr
     */
    private static boolean parseLimit( SizeLimitWrapper slw, String limitStr )
    {
        int pos = 0;
        
        // The sizelimit always starts with a "size"
        if ( limitStr.startsWith( "size" ) )
        {
            pos += 4;
            
            // A global or hard/soft/pr/prtotal ?
            if ( limitStr.startsWith( "=", pos ) )
            {
                // Global : get the limit
                pos++;
                
                if ( limitStr.startsWith( UNLIMITED_STR, pos ) )
                {
                    pos += 9;
                    slw.globalLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( NONE_STR, pos ) )
                {
                    pos += 4;
                    slw.globalLimit = UNLIMITED;
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
                            slw.globalLimit = value;
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
                
                if ( limitStr.startsWith( UNLIMITED_STR, pos ) )
                {
                    pos += 9;
                    
                    slw.hardLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( NONE_STR, pos ) )
                {
                    pos += 4;
                    
                    slw.hardLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( SOFT_STR, pos ) )
                {
                    pos += 4;
                    slw.globalLimit = HARD_SOFT;
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
                            slw.hardLimit = value;
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

                if ( limitStr.startsWith( UNLIMITED_STR, pos ) )
                {
                    pos += 9;
                    
                    slw.softLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( NONE_STR, pos ) )
                {
                    pos += 4;
                    
                    slw.softLimit = UNLIMITED;
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
                            slw.softLimit = value;
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
            else if ( limitStr.startsWith( ".unchecked=", pos ) )
            {
                // Unchecked limit : get the limit
                pos += 11;

                if ( limitStr.startsWith( UNLIMITED_STR, pos ) )
                {
                    pos += 9;
                    
                    slw.uncheckedLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( NONE_STR, pos ) )
                {
                    pos += 4;
                    
                    slw.uncheckedLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( DISABLED_STR, pos ) )
                {
                    pos += 8;
                    
                    slw.uncheckedLimit = UC_DISABLED;
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
                            slw.uncheckedLimit = value;
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
            else if ( limitStr.startsWith( ".pr=", pos ) )
            {
                // pr limit : get the limit
                pos += 4;

                if ( limitStr.startsWith( UNLIMITED_STR, pos ) )
                {
                    pos += 9;
                    
                    slw.prLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( NONE_STR, pos ) )
                {
                    pos += 4;
                    
                    slw.prLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( "noestimate", pos ) )
                {
                    pos += 10;
                    
                    slw.noEstimate = true;
                }
                else if ( limitStr.startsWith( DISABLED_STR, pos ) )
                {
                    pos += 8;
                    
                    slw.prLimit = PR_DISABLED;
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
                            slw.prLimit = value;
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
            else if ( limitStr.startsWith( ".prtotal=", pos ) )
            {
                // prTotal limit : get the limit
                pos += 9;

                if ( limitStr.startsWith( UNLIMITED_STR, pos ) )
                {
                    pos += 9;
                    
                    slw.prTotalLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( NONE_STR, pos ) )
                {
                    pos += 4;
                    
                    slw.prTotalLimit = UNLIMITED;
                }
                else if ( limitStr.startsWith( DISABLED_STR, pos ) )
                {
                    pos += 8;
                    
                    slw.prTotalLimit = PR_DISABLED;
                }
                else if ( limitStr.startsWith( HARD_STR, pos ) )
                {
                    pos += 4;
                    
                    slw.prTotalLimit = PR_HARD;
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
                            slw.prTotalLimit = value;
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
     * Tells if the SizeLimit element is valid or not
     * @param sizeLimitStr the sizeLimit String to check
     * @return true if the values are correct, false otherwise
     */
    public static boolean isValid( String sizeLimitStr )
    {
        if ( !Strings.isEmpty( sizeLimitStr ) )
        {
            // use a lowercase version of the string
            String lowerCaseSizeLimitStr = sizeLimitStr.toLowerCase();
            
            SizeLimitWrapper tmp = new SizeLimitWrapper();
            
            // Split the strings
            String[] limits = lowerCaseSizeLimitStr.split( " " );
            
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
     * @return the prLimit
     */
    public Integer getPrLimit()
    {
        return prLimit;
    }


    /**
     * @param prLimit the prLimit to set
     */
    public void setPrLimit( Integer prLimit )
    {
        this.prLimit = prLimit;
    }


    /**
     * @return the prTotalLimit
     */
    public Integer getPrTotalLimit()
    {
        return prTotalLimit;
    }


    /**
     * @param prTotalLimit the prTotalLimit to set
     */
    public void setPrTotalLimit( Integer prTotalLimit )
    {
        this.prTotalLimit = prTotalLimit;
    }


    /**
     * @return the uncheckedLimit
     */
    public Integer getUncheckedLimit()
    {
        return uncheckedLimit;
    }


    /**
     * @param uncheckedLimit the uncheckedLimit to set
     */
    public void setUncheckedLimit( Integer uncheckedLimit )
    {
        this.uncheckedLimit = uncheckedLimit;
    }
    
    /**
     * @return the noEstimate
     */
    public boolean isNoEstimate()
    {
        return noEstimate;
    }


    /**
     * @param noEstimate the noEstimate to set
     */
    public void setNoEstimate( boolean noEstimate )
    {
        this.noEstimate = noEstimate;
    }



    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        // Deal with global/hard/soft limits first
        if ( globalLimit != null )
        {
            // The globalLimit overrides the soft and hard limit
            sb.append( "size=" );
            
            if ( globalLimit.equals( UNLIMITED ) )
            {
                sb.append( UNLIMITED_STR );
            }
            else if ( globalLimit.intValue() >= 0 )
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
                        // If hard and soft are set and equals, we use the global limit instead
                        sb.append( "size=" );
                        
                        if ( hardLimit.equals( UNLIMITED ) )
                        {
                            sb.append( UNLIMITED_STR );
                        }
                        else if ( hardLimit.intValue() >= 0 )
                        {
                            sb.append( hardLimit );
                        }
                    }
                    else
                    {
                        // We have both values, the aren't equal. 
                        if ( hardLimit.equals( UNLIMITED ) )
                        {
                            sb.append( "size.hard=unlimited size.soft=" );
                            sb.append( softLimit );
                        }
                        else if ( hardLimit.intValue() == 0 )
                        {
                            // Special cases : hard = soft
                            sb.append( "size=" ).append( softLimit );
                        }
                        else if ( hardLimit.intValue() < softLimit.intValue() )
                        {
                            // when the hard limit is lower than the soft limit : use the hard limit
                            sb.append( "size=" ).append( hardLimit );
                        }
                        else 
                        {
                            // Special case : softLimit is -1
                            if ( softLimit.equals( UNLIMITED ) )
                            {
                                // We use the hard limit
                                sb.append( "size=" ).append( hardLimit );
                            }
                            else
                            {
                                sb.append( "size.hard=" );
                                
                                if ( hardLimit.equals( UNLIMITED ) )
                                {
                                    sb.append( UNLIMITED_STR );
                                }
                                else if ( hardLimit.intValue() > 0 )
                                {
                                    sb.append( hardLimit );
                                }
        
                                sb.append( " size.soft=" );
                                
                                if ( softLimit.equals( UNLIMITED ) )
                                {
                                    sb.append( UNLIMITED_STR );
                                }
                                else if ( softLimit.intValue() >= 0 )
                                {
                                    sb.append( softLimit );
                                }
                            }
                        }
                    }
                }
                else
                {
                    // Only an hard limit
                    sb.append( "size.hard=" );
                    
                    if ( hardLimit.equals( UNLIMITED ) )
                    {
                        sb.append( UNLIMITED_STR );
                    }
                    else if ( hardLimit.intValue() >= 0 )
                    {
                        sb.append( hardLimit );
                    }
                }
            }
            else if ( softLimit != null )
            {
                // Only a soft limit
                sb.append( "size.soft=" );
                
                if ( softLimit.equals( UNLIMITED ) )
                {
                    sb.append( UNLIMITED_STR );
                }
                else if ( softLimit.intValue() >= 0 )
                {
                    sb.append( softLimit );
                }
            }
        }

        // Eventually add a space at the end if we have had some size limit
        if ( sb.length() > 0 )
        {
            sb.append( ' ' );
        }
        
        // process the unchecked limit
        if ( uncheckedLimit != null )
        {
            sb.append( "size.unchecked=" );
            
            if ( uncheckedLimit.equals( UNLIMITED ) )
            {
                sb.append( UNLIMITED_STR );
            }
            else if ( uncheckedLimit.equals( UC_DISABLED ) )
            {
                sb.append( DISABLED_STR );
            }
            else
            {
                sb.append( uncheckedLimit );
            }
        }
        
        // Process the pr limit
        if ( prLimit != null )
        {
            // Add a space if we have had some unchecked limit
            if ( uncheckedLimit != null )
            {
                sb.append( ' ' );
            }
            
            sb.append( "size.pr=" );
            
            if ( prLimit.equals( UNLIMITED ) )
            {
                sb.append( UNLIMITED_STR );
            }
            else
            {
                sb.append( prLimit );
            }
        }

        // Process the prTotal limit
        if ( prTotalLimit != null )
        {
            // Add a space if we have had some unchecked limit or some pr limit
            if ( ( uncheckedLimit != null ) || ( prLimit != null ) )
            {
                sb.append( ' ' );
            }

            sb.append( "size.prtotal=" );
            
            if ( prTotalLimit.equals( UNLIMITED ) )
            {
                sb.append( UNLIMITED_STR );
            }
            else if ( prTotalLimit.intValue() == PR_HARD )
            {
                sb.append( HARD_STR );
            }
            else
            {
                sb.append( prTotalLimit );
            }
            
        }
        
        // Last, not least, the noEstimate flag
        if ( noEstimate )
        {
            // Add a space if we have had some unchecked, pr or prTotal limit
            if ( ( uncheckedLimit != null ) || ( prLimit != null ) || ( prTotalLimit != null ) )
            {
                sb.append( ' ' );
            }

            sb.append( "size.pr=noEstimate" );
        }
        
        return sb.toString();
    }
}
