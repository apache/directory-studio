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
package org.apache.directory.studio.common.ui.wrappers;

/**
 * A wrapper for a String value.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StringValueWrapper implements Cloneable, Comparable<StringValueWrapper>
{
    /** The value */
    private String value;
    
    /** A flag to tell if the compare should be case sensitive or not */
    private boolean caseSensitive = true;

    /**
     * Creates a new instance of StringValueWrapper.
     *
     * @param value the value
     */
    public StringValueWrapper( String value, boolean caseSensitive )
    {
        this.value = value;
        this.caseSensitive = caseSensitive;
    }

    
    /**
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue( String value )
    {
        this.value = value;
    }
    
    
    /**
     * Clone the current object
     */
    public StringValueWrapper clone()
    {
        try
        {
            return (StringValueWrapper)super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            return null;
        }
    }

    
    /**
     * @see Object#equals(Object)
     */
    public boolean equals( Object that )
    {
        // Quick test
        if ( this == that )
        {
            return true;
        }
        
        if ( that instanceof StringValueWrapper )
        {
            StringValueWrapper thatInstance = (StringValueWrapper)that;
            
            if ( caseSensitive )
            {
                return value.equals( thatInstance.value );
            }
            else
            {
                return value.equalsIgnoreCase( thatInstance.value );
            }
        }
        else
        {
            return false;
        }
    }

    
    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        int h = 37;
        
        if ( value != null )
        {
            h += h*17 + value.hashCode();
        }
        
        return h;
    }


    /**
     * @see Comparable#compareTo()
     */
    public int compareTo( StringValueWrapper that )
    {
        if ( that == null )
        {
            return 1;
        }
        
        // Check the value
        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            return -1;
        }
        else
        {
            return value.compareToIgnoreCase( that.value );
        }
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return value;
    }
}
