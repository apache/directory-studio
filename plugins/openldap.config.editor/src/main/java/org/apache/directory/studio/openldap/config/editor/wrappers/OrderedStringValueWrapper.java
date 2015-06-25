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
 * A wrapper for an ordered String value. The value is prefixed by "{n}" where n is an integer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OrderedStringValueWrapper implements Cloneable, Comparable<OrderedStringValueWrapper>
{
    /** The value */
    private String value;
    
    /** A flag to tell if the compare should be case sensitive or not */
    private boolean caseSensitive = true;

    /** The prefix, used to order the values */
    private int prefix;
    
    /**
     * Creates a new instance of StringValueWrapper.
     *
     * @param value the value
     */
    public OrderedStringValueWrapper( int prefix, String value, boolean caseSensitive )
    {
        this.value = value;
        this.caseSensitive = caseSensitive;
        this.prefix = prefix;
    }

    
    /**
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    
    /**
     * Sets a new value
     * 
     * @param value the value to set
     */
    public void setValue( String value )
    {
        this.value = value;
    }

    
    /**
     * Sets a new prefix
     * 
     * @param prefix the prefix to set
     */
    public void setPrefix( int prefix )
    {
        this.prefix = prefix;
    }

    
    /**
     * @return the prefix
     */
    public int getPrefix()
    {
        return prefix;
    }

    
    /**
     * Clone the current object
     */
    public OrderedStringValueWrapper clone()
    {
        try
        {
            return (OrderedStringValueWrapper)super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            return null;
        }
    }


    /**
     * @see Comparable#compareTo()
     */
    public int compareTo( OrderedStringValueWrapper that )
    {
        if ( that == null )
        {
            return 1;
        }
        
        // Check the prefix
        if ( prefix < that.prefix )
        {
            return -1;
        }
        else if ( prefix > that.prefix )
        {
            return 1;
        }
        
        // Check the value
        if ( Strings.isEmpty( value ) )
        {
            return -1;
        }
        else
        {
            return value.compareToIgnoreCase( that.value );
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
        
        if ( that instanceof OrderedStringValueWrapper )
        {
            OrderedStringValueWrapper thatInstance = (OrderedStringValueWrapper)that;
            
            if ( prefix != thatInstance.prefix )
            {
                return false;
            }
            
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
        
        h += h*17 + prefix;
        
        if ( value != null )
        {
            h += h*17 + value.hashCode();
        }
        
        return h;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return '{' + Integer.toString( prefix ) + '}' + value;
    }
}
