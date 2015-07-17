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
import org.apache.directory.studio.common.ui.widgets.OrderedElement;

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
 * Note : each of the limit is an Integer, so that we can have two states :
 * <ul>
 * <li>not existent</li>
 * <li>has a value</li>
 * </ul>
 * A -1 value means unlimited. Any other value is accepted, if > 0.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OrderedTimeLimitWrapper extends TimeLimitWrapper implements OrderedElement
{
    /** The prefix, used to order the values */
    private int prefix;

    /**
     * Create a TimeLimitWrapper instance
     * 
     * @param globalLimit The global limit
     * @param hardLimit The hard limit
     * @param softLimit The soft limit
     */
    public OrderedTimeLimitWrapper( Integer globalLimit, Integer hardLimit, Integer softLimit )
    {
        super( globalLimit, hardLimit, softLimit );
    }
    
    
    /**
     * Create a TimeLimitWrapper instance from a String. 
     * 
     * @param timeLimitStr The String that contain the value
     */
    public OrderedTimeLimitWrapper( String timeLimitStr )
    {
        super( timeLimitStr );
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
    public int compareTo( OrderedTimeLimitWrapper that )
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
        
        if ( that instanceof OrderedTimeLimitWrapper )
        {
            OrderedTimeLimitWrapper thatInstance = (OrderedTimeLimitWrapper)that;
            
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
