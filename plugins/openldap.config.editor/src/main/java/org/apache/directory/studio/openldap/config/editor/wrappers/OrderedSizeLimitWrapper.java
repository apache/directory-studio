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

import org.apache.directory.studio.common.ui.widgets.OrderedElement;

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
public class OrderedSizeLimitWrapper extends SizeLimitWrapper implements OrderedElement
{
    /** The prefix, used to order the values */
    private int prefix;


    /**
     * Create a SizeLimitWrapper instance from a String. 
     * 
     * @param sizeLimitStr The String that contain the value
     */
    public OrderedSizeLimitWrapper( String sizeLimitStr )
    {
        super( sizeLimitStr );
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
     * {@inheritDoc}
     */
    public void decrementPrefix()
    {
        prefix--;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void incrementPrefix()
    {
        prefix++;
    }
    
    
    /**
     * @see Comparable#compareTo()
     */
    public int compareTo( OrderedSizeLimitWrapper that )
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
        
        return super.compareTo( that );
    }

    
    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        int h = 37;
        
        h += h*17 + prefix;
        
        h += h*17 + super.hashCode();
        
        return h;
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
        
        if ( that instanceof OrderedSizeLimitWrapper )
        {
            OrderedSizeLimitWrapper thatInstance = (OrderedSizeLimitWrapper)that;
            
            if ( prefix != thatInstance.prefix )
            {
                return false;
            }

            return super.equals( that );
        }
        else
        {
            return false;
        }
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return '{' + Integer.toString( prefix ) + '}' + super.toString();
    }
}
