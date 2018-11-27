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


import org.apache.directory.api.ldap.model.name.Dn;

/**
 * A wrapper for DNs.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DnWrapper implements Cloneable, Comparable<DnWrapper>
{
    /** The interned DN */
    private Dn dn;
    
    /**
     * Build a DnWrapper from a String containing the DN
     *  
     * @param dn The DN to store
     */
    public DnWrapper( Dn dn ) 
    {
        this.dn = dn;
    }
    
    
    /**
     * @return the dn
     */
    public Dn getDn()
    {
        return dn;
    }


    /**
     * @param dn the dn to set
     */
    public void setDn( Dn dn )
    {
        this.dn = dn;
    }


    /**
     * @see Object#clone()
     */
    public DnWrapper clone()
    {
        // No need to clone, DN is immutable
        return this;
    }

    
    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        return dn.hashCode();
    }


    /**
     * @see Object#equals()
     */
    public boolean equals( Object that )
    {
        if ( that == this )
        {
            return true;
        }
        
        if ( ! ( that instanceof DnWrapper ) )
        {
            return false;
        }
        
        return compareTo( (DnWrapper)that ) == 0;
    }


    /**
     * @see Comparable#compareTo()
     */
    public int compareTo( DnWrapper that )
    {
        if ( that == null )
        {
            return 1;
        }

        if ( dn.equals( that.dn ) )
        {
            return 0;
        }
        
        if ( dn.isDescendantOf( that.dn ) )
        {
            return 1;
        }
        else if ( that.dn.isDescendantOf( dn ) )
        {
            return -1;
        }
        else
        {
            // Find the common ancestor, if any
            int upperBound = Math.min( dn.size(), that.dn.size() );
            int result = 0;
            
            for ( int i = 0; i < upperBound; i++ )
            {
                result = dn.getRdn( i ).compareTo( that.dn.getRdn( i ) );
                
                if ( result != 0 )
                {
                    return result;
                }
            }
            // We have exhausted one of the DN
            if ( dn.size() > upperBound )
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return dn.toString();
    }
}
