/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.wrapper;

import org.apache.directory.studio.openldap.config.acl.model.AclAttribute;

/**
 * The wrapper around an AclAttribute class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AclAttributeWrapper implements Cloneable, Comparable<AclAttributeWrapper>
{
    /** The AclAttribute */
    private AclAttribute aclAttribute;
    
    /**
     * Creates a new instance of AclAttributeWrapper.
     */
    public AclAttributeWrapper()
    {
        // Default to ExtensibleObject
        aclAttribute = new AclAttribute( "extensibleObject", null );
    }
    
    
    /**
     * Creates a new instance of AclAttributeWrapper.
     *
     * @param aclAttribute the aclAttribute
     */
    public AclAttributeWrapper( AclAttribute aclAttribute )
    {
        this.aclAttribute = aclAttribute;
    }


    /**
     * @return the value
     */
    public AclAttribute getAclAttribute()
    {
        return aclAttribute;
    }

    
    /**
     * @param aclAttribute the aclAttribute to set
     */
    public void setAclAttribute( AclAttribute aclAttribute )
    {
        this.aclAttribute = aclAttribute;
    }

    
    /**
     * @param aclAttribute the aclAttribute to set
     */
    public void setAclAttribute( String name )
    {
        this.aclAttribute = new AclAttribute( name, null );
    }


    /**
     * Clone the current object
     */
    public AclAttributeWrapper clone()
    {
        try
        {
            return (AclAttributeWrapper)super.clone();
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

        if ( that instanceof AclAttributeWrapper )
        {
            AclAttributeWrapper thatInstance = (AclAttributeWrapper)that;

            return aclAttribute.getName().equalsIgnoreCase( thatInstance.aclAttribute.getName() ) && 
                   ( aclAttribute.isAttributeType() && thatInstance.aclAttribute.isAttributeType() ||
                     ( ( aclAttribute.isObjectClass() || aclAttribute.isObjectClassNotAllowed() ) && 
                         ( thatInstance.aclAttribute.isObjectClass() || thatInstance.aclAttribute.isObjectClassNotAllowed() ) ) );
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

        if ( aclAttribute != null )
        {
            h += h*17 + aclAttribute.getName().hashCode();
        }

        return h;
    }


    /**
     * @see Comparable#compareTo()
     */
    public int compareTo( AclAttributeWrapper that )
    {
        if ( that == null )
        {
            return 1;
        }

        // Check the AclAttribute
        return aclAttribute.getName().compareToIgnoreCase( that.getAclAttribute().getName() );
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return aclAttribute.toString();
    }
}
