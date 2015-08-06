package org.apache.directory.studio.openldap.config.acl.model;

import org.apache.directory.api.util.Strings;

public class AclAttributeWrapper implements Cloneable, Comparable<AclAttributeWrapper>
{
    /** The AclAttribute */
    private AclAttribute aclAttribute;
    
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

            return aclAttribute.getName().equalsIgnoreCase( thatInstance.aclAttribute.getName() );
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
