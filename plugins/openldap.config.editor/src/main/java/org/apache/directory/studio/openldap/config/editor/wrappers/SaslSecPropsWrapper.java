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

import java.util.HashSet;
import java.util.Set;

import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.openldap.common.ui.model.SaslSecPropEnum;
/**
 * A wrapper for the olcSaslSecProps parameter. The syntax is the following :
 * 
 * <pre>
 * saslSecProp ::= ( 'none' | 'noplain' | 'noactive' | 'nodict' | 'noanonymous' | 'forwardsec' |
 *                      'passcred' | 'minssf' '=' INT | 'maxssf' '=' INT | 'maxbufsuze' '=' INT )*
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SaslSecPropsWrapper implements Cloneable
{
    /** The flags for properties with no arguments */
    private Set<SaslSecPropEnum> flags = new HashSet<SaslSecPropEnum>();

    /** The value of the minSSF parameter */
    private Integer minSsf;
    
    /** The value of the maxSSF parameter */
    private Integer maxSsf;
    
    /** The max buffer size parameter */
    private Integer maxBufSize;

    /**
     * Creates an instance of a SaslSecProps
     **/
    public SaslSecPropsWrapper()
    {
    }

     
    /**
     * Creates an instance of a SaslSecProps parameter using a String.
     * 
     * @param parameters The list of parameters to parse
     */
    public SaslSecPropsWrapper( String parameters )
    {
        if ( !Strings.isEmpty( Strings.trim( parameters ) ) )
        {
            // Split the string along the spaces
            String[] properties = parameters.split( "," );
            
            for ( String property : properties )
            {
                if ( Strings.isEmpty( Strings.trim( property ) ) )
                {
                    continue;
                }
                
                int pos = property.indexOf( '=' );
                
                if ( pos == -1 )
                {
                    // No value
                    SaslSecPropEnum flag = SaslSecPropEnum.getFlag( Strings.trim( property ) );
                    
                    switch ( flag )
                    {
                        case FORWARD_SEC :
                        case NO_ACTIVE :
                        case NO_ANONYMOUS :
                        case NO_DICT :
                        case NO_PLAIN :
                        case PASS_CRED :
                        case NONE :
                            flags.add( flag );
                            break;
                            
                        case MAX_BUF_SIZE :
                        case MAX_SSF :
                        case MIN_SSF :
                        case UNKNOWN :
                            // Nothing to do...
                    }
                }
                else
                {
                    // Fetch the name
                    String name = property.substring( 0, pos );
                    SaslSecPropEnum flag = SaslSecPropEnum.getFlag( Strings.trim( name ) );
                    
                    try
                    {
                        int value = Integer.valueOf( Strings.trim( property.substring( pos + 1 ) ) );
                        
                        if ( value >= 0 )
                        {
                            switch ( flag )
                            {
                                case MAX_BUF_SIZE :
                                    maxBufSize = Integer.valueOf( value );
                                    break;
                                    
                                case MAX_SSF :
                                    maxSsf = Integer.valueOf( value );
                                    break;
                                    
                                case MIN_SSF :
                                    minSsf = Integer.valueOf( value );
                                    break;
                                    
                                case FORWARD_SEC :
                                case NO_ACTIVE :
                                case NO_ANONYMOUS :
                                case NO_DICT :
                                case NO_PLAIN :
                                case PASS_CRED :
                                case NONE :
                                case UNKNOWN :
                                    // Nothing to do... This is an error
                            }
                        }
                    }
                    catch ( NumberFormatException nfe )
                    {
                        // Nothing to do
                    }
                }
            }
        }
    }
    
    
    /**
     * Check if a given String is a valid SaslSecProp parameter
     * 
     * @param str The string to check
     * @return true if teh string is a valid SaslSecProp parameter
     */
    public static boolean isValid( String str )
    {
        if ( !Strings.isEmpty( Strings.trim( str ) ) )
        {
            // Split the string along the spaces
            String[] properties = str.split( "," );
            
            if ( ( properties == null ) || ( properties.length == 0 ) )
            {
                return true;
            }
            
            for ( String property : properties )
            {
                if ( Strings.isEmpty( Strings.trim( property ) ) )
                {
                    continue;
                }
                
                int pos = property.indexOf( '=' );
                
                if ( pos == -1 )
                {
                    // No value
                    SaslSecPropEnum flag = SaslSecPropEnum.getFlag( Strings.trim( property ) );
                    
                    switch ( flag )
                    {
                        case FORWARD_SEC :
                        case NO_ACTIVE :
                        case NO_ANONYMOUS :
                        case NO_DICT :
                        case NO_PLAIN :
                        case PASS_CRED :
                        case NONE :
                            break;
                         
                        default :
                            return false;
                    }
                }
                else
                {
                    // Fetch the name
                    String name = property.substring( 0, pos );
                    SaslSecPropEnum flag = SaslSecPropEnum.getFlag( Strings.trim( name ) );
                    
                    try
                    {
                        int value = Integer.valueOf( Strings.trim( property.substring( pos + 1 ) ) );
                        
                        if ( value < 0 )
                        {
                            return false;
                        }
                        
                        switch ( flag )
                        {
                            case MAX_BUF_SIZE :
                            case MAX_SSF :
                            case MIN_SSF :
                                break;
                                
                            default :
                                return false;
                        }
                    }
                    catch ( NumberFormatException nfe )
                    {
                        // wrong
                        return false;
                    }
                }
            }
            
            return true;
        }
        else
        {
            return true;
        }
    }


    /**
     * @return the flag
     */
    public Set<SaslSecPropEnum> getFlags()
    {
        return flags;
    }

    
    /**
     * @param flag the flag to set
     */
    public void addFlag( SaslSecPropEnum flag )
    {
        this.flags.add( flag );
    }

    
    /**
     * @param flag the flag to remove
     */
    public void removeFlag( SaslSecPropEnum flag )
    {
        this.flags.remove( flag );
    }

    
    /**
     * Clear the flag's set
     */
    public void clearFlags()
    {
        this.flags.clear();
    }
    

    /**
     * @return the minSsf
     */
    public Integer getMinSsf()
    {
        return minSsf;
    }

    /**
     * @param minSsf the minSsf to set
     */
    public void setMinSsf( Integer minSsf )
    {
        this.minSsf = minSsf;
    }

    /**
     * @return the maxSsf
     */
    public Integer getMaxSsf()
    {
        return maxSsf;
    }

    /**
     * @param maxSsf the maxSsf to set
     */
    public void setMaxSsf( Integer maxSsf )
    {
        this.maxSsf = maxSsf;
    }

    /**
     * @return the maxBufSize
     */
    public Integer getMaxBufSize()
    {
        return maxBufSize;
    }

    /**
     * @param maxBufSize the maxBufSize to set
     */
    public void setMaxBufSize( Integer maxBufSize )
    {
        this.maxBufSize = maxBufSize;
    }
    
    
    /**
     * Compare two Integer instance and return true if they are equal 
     */
    private boolean equals( Integer int1, Integer int2 )
    {
        if ( int1 == null )
        {
            return int2 == null;
        }
        
        return int1.equals( int2 );
    }
    
    
    /**
     * @see Object#equals(Object)
     */
    public boolean equals( Object that )
    {
        if ( this == that )
        {
            return true;
        }
        
        if ( !( that instanceof SaslSecPropsWrapper ) )
        {
            return false;
        }
        
        SaslSecPropsWrapper thatInstance = (SaslSecPropsWrapper)that;
        
        return ( ( flags.size() == thatInstance.flags.size() ) &&
                 ( thatInstance.flags.containsAll( flags ) ) &&
                 ( equals( minSsf, thatInstance.minSsf ) ) &&
                 ( equals( maxSsf, thatInstance.maxSsf ) ) &&
                 ( equals( maxBufSize, thatInstance.maxBufSize ) ) );
    }
    
    
    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        int h = 37;
        
        if ( minSsf != null )
        {
            h += h*17 + minSsf.intValue();
        }
        
        if ( maxSsf != null )
        {
            h += h*17 + maxSsf.intValue();
        }
        
        if ( maxBufSize != null )
        {
            h += h*17 + maxBufSize.intValue();
        }
        
        for ( SaslSecPropEnum saslSecProp : flags )
        {
            h += h*17 + saslSecProp.hashCode();
        }
        
        return h;
    }
    
    
    /**
     * Clone the current object
     */
    public SaslSecPropsWrapper clone()
    {
        try
        {
            return (SaslSecPropsWrapper)super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            return null;
        }
    }

    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        boolean isFirst = true;
        
        for ( SaslSecPropEnum saslSecProp : flags )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( ',' );
            }
            
            sb.append( saslSecProp.getName() );
        }
        
        // The minSSF properties
        if ( minSsf != null )
        {
            if ( sb.length() > 0 )
            {
                sb.append( ',' );
            }
            
            sb.append( SaslSecPropEnum.MIN_SSF.getName() ).append( '=' ).append( minSsf.intValue() );
        }
        
        
        // The maxSSF properties
        if ( maxSsf != null )
        {
            if ( sb.length() > 0 )
            {
                sb.append( ',' );
            }
            
            sb.append( SaslSecPropEnum.MAX_SSF.getName() ).append( '=' ).append( maxSsf.intValue() );
        }
        
        // The maxbufsize properties
        if ( maxBufSize != null )
        {
            if ( sb.length() > 0 )
            {
                sb.append( ',' );
            }
            
            sb.append( SaslSecPropEnum.MAX_BUF_SIZE.getName() ).append( '=' ).append( maxBufSize.intValue() );
        }
        
        return sb.toString();
    }
}
