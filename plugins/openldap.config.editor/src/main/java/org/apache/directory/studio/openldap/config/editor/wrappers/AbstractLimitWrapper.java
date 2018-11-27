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

/**
 * A shared class with the TimeLimitWrapper and SizeLimitWrapper
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractLimitWrapper implements LimitWrapper, Comparable<LimitWrapper>
{
    /** The global limit */
    protected Integer globalLimit;

    /** The soft limit */
    protected Integer softLimit;
    
    /** The hard limit */
    protected Integer hardLimit;

    /** A flag that tells if the Limit is valid */
    protected boolean isValid = true;
    
    /** The length of the parsed String, if any */
    protected int parsedLength = 0;

    /**
     * Create a AbstractLimitWrapper instance
     */
    public AbstractLimitWrapper()
    {
    }
    

    /**
     * Create an AbstractLimitWrapper instance
     * 
     * @param globalLimit The global limit
     * @param hardLimit The hard limit
     * @param softLimit The soft limit
     */
    public AbstractLimitWrapper( Integer globalLimit, Integer hardLimit, Integer softLimit )
    {
        this.globalLimit = globalLimit; 
        this.hardLimit = hardLimit;
        this.softLimit = softLimit;
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
     * Get an integer out of a String. Return null if we don't find any.
     */
    protected static String getInteger( String str, int pos )
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
        
        String limitType = getType();
        
        if ( globalLimit != null )
        {
            // The globalLimit overrides the soft and hard limit
            sb.append( limitType );
            
            if ( globalLimit.intValue() >= 0 )
            {
                sb.append( "=" ).append( globalLimit );
            }
            else if ( globalLimit.equals( UNLIMITED ) )
            {
                sb.append( "=" ).append( UNLIMITED_STR );
            }
            else if ( globalLimit.equals( HARD_SOFT ) )
            {
                sb.append( ".hard=" ).append( SOFT_STR ); 
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
                        sb.append( limitType ).append( "=" );
                        
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
                            sb.append( limitType ).append( ".hard=unlimited " );
                            sb.append( limitType ).append( ".soft=" );
                            sb.append( softLimit );
                        }
                        else if ( hardLimit.intValue() == 0 )
                        {
                            // Special cases : hard = soft
                            sb.append( limitType ).append( "=" ).append( softLimit );
                        }
                        else if ( hardLimit.intValue() < softLimit.intValue() )
                        {
                            // when the hard limit is lower than the soft limit : use the hard limit
                            sb.append( limitType ).append( "=" ).append( hardLimit );
                        }
                        else 
                        {
                            // Special case : softLimit is -1
                            if ( softLimit.equals( UNLIMITED ) )
                            {
                                // We use the hard limit
                                sb.append( limitType ).append( "=" ).append( hardLimit );
                            }
                            else
                            {
                                sb.append( limitType ).append( ".hard=" );
                                
                                if ( hardLimit.equals( UNLIMITED ) )
                                {
                                    sb.append( UNLIMITED_STR );
                                }
                                else if ( hardLimit.intValue() > 0 )
                                {
                                    sb.append( hardLimit );
                                }
        
                                sb.append( ' ' ).append( limitType ).append( ".soft=" );
                                
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
                    sb.append( limitType ).append( ".hard=" );
                    
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
                sb.append( limitType ).append( ".soft=" );
                
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
        
        return sb.toString();
    }


    /**
     * @see Comparable#compareTo()
     */
    public int compareTo( LimitWrapper that )
    {
        if ( that == null )
        {
            return 1;
        }
        
        return toString().compareTo( that.toString() );
    }
    
    
    /**
     * Tells if the TimeLimit element is valid or not
     * @return true if the values are correct, false otherwise
     */
    public boolean isValid()
    {
        return isValid;
    }
}
