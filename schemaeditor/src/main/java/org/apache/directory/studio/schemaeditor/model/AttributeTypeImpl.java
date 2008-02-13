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
package org.apache.directory.studio.schemaeditor.model;


import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractAttributeType;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.MutableSchemaObject;
import org.apache.directory.shared.ldap.schema.Syntax;
import org.apache.directory.shared.ldap.schema.UsageEnum;


/**
 * This class represents an attribute type.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeImpl extends AbstractAttributeType implements MutableSchemaObject
{
    private static final long serialVersionUID = 1L;

    /** The object OID */
    private String objectOid;

    /** The name of the superior */
    private String superiorName;

    /** The OID of the syntax */
    private String syntaxOid;

    /** The name of the equality matching rule */
    private String equalityName;

    /** The name of the ordering matching rule */
    private String orderingName;

    /** The name of the substr matching rule */
    private String substrName;


    /**
     * Creates a new instance of AttributeTypeImpl.
     *
     * @param oid
     *      the OID of the attribute type
     */
    public AttributeTypeImpl( String oid )
    {
        super( oid );
        objectOid = oid;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#setNames(java.lang.String[])
     */
    public void setNames( String[] names )
    {
        super.setNames( names );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#getOid()
     */
    public String getOid()
    {
        return objectOid;
    }


    /**
     * Set the OID.
     *
     * @param oid
     *      the OID value
     */
    public void setOid( String oid )
    {
        objectOid = oid;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#setDescription(java.lang.String)
     */
    public void setDescription( String description )
    {
        super.setDescription( description );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#setObsolete(boolean)
     */
    public void setObsolete( boolean obsolete )
    {
        super.setObsolete( obsolete );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractAttributeType#setCanUserModify(boolean)
     */
    public void setCanUserModify( boolean canUserModify )
    {
        super.setCanUserModify( canUserModify );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractAttributeType#setCollective(boolean)
     */
    public void setCollective( boolean collective )
    {
        super.setCollective( collective );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractAttributeType#setSingleValue(boolean)
     */
    public void setSingleValue( boolean singleValue )
    {
        super.setSingleValue( singleValue );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractAttributeType#setUsage(org.apache.directory.shared.ldap.schema.UsageEnum)
     */
    public void setUsage( UsageEnum usage )
    {
        super.setUsage( usage );
    }


    /**
     * Gets the superior name.
     *
     * @return
     *      the superior name
     */
    public String getSuperiorName()
    {
        return superiorName;
    }


    /**
     * Sets the superior name.
     *
     * @param superiorName
     *      the superior name
     */
    public void setSuperiorName( String superiorName )
    {
        this.superiorName = superiorName;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AttributeType#getSuperior()
     */
    public AttributeType getSuperior() throws NamingException
    {
        return null;
    }


    /**
     * Gets the OID of the syntax.
     * 
     * @return
     *      the OID of the syntax
     */
    public String getSyntaxOid()
    {
        return syntaxOid;
    }


    /**
     * Sets the OID of the syntax.
     *
     * @param syntaxOid
     *      the OID of the syntax
     */
    public void setSyntaxOid( String syntaxOid )
    {
        this.syntaxOid = syntaxOid;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AttributeType#getSyntax()
     */
    public Syntax getSyntax() throws NamingException
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractAttributeType#setLength(int)
     */
    public void setLength( int length )
    {
        super.setLength( length );
    }


    /**
     * Gets the equality matching rule name.
     * 
     * @return
     *      the equality matching rule name
     */
    public String getEqualityName()
    {
        return equalityName;
    }


    /**
     * Sets the equality matching rule name.
     * 
     * @param equalityName
     *      the equality matching rule name
     */
    public void setEqualityName( String equalityName )
    {
        this.equalityName = equalityName;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AttributeType#getEquality()
     */
    public MatchingRule getEquality() throws NamingException
    {
        return null;
    }


    /**
     * Gets the ordering matching rule name.
     *
     * @return
     *      the ordering matching rule name
     */
    public String getOrderingName()
    {
        return orderingName;
    }


    /**
     * Sets the ordering matching rule name.
     *
     * @param orderingName
     *      the ordering matching rule name
     */
    public void setOrderingName( String orderingName )
    {
        this.orderingName = orderingName;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AttributeType#getOrdering()
     */
    public MatchingRule getOrdering() throws NamingException
    {
        return null;
    }


    /**
     * Gets the substring matching rule name.
     *
     * @return
     *      the substring matching rule name
     */
    public String getSubstrName()
    {
        return substrName;
    }


    /**
     * Sets the substring matching rule name.
     *
     * @param substrName
     *      the substring matching rule name
     */
    public void setSubstrName( String substrName )
    {
        this.substrName = substrName;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AttributeType#getSubstr()
     */
    public MatchingRule getSubstr() throws NamingException
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof AttributeTypeImpl )
        {
            AttributeTypeImpl at = ( AttributeTypeImpl ) obj;

            // OID
            if ( ( getOid() == null ) && ( at.getOid() != null ) )
            {
                return false;
            }
            else if ( ( getOid() != null ) && ( at.getOid() == null ) )
            {
                return false;
            }
            else if ( ( getOid() != null ) && ( at.getOid() != null ) )
            {
                if ( !getOid().equals( at.getOid() ) )
                {
                    return false;
                }
            }

            // Aliases
            if ( ( getNames() == null ) && ( at.getNames() != null ) )
            {
                return false;
            }
            else if ( ( getNames() != null ) && ( at.getNames() == null ) )
            {
                return false;
            }
            else if ( ( getNames() != null ) && ( at.getNames() != null ) )
            {
                if ( !getNames().equals( at.getNames() ) )
                {
                    return false;
                }
            }

            // Description
            if ( ( getDescription() == null ) && ( at.getDescription() != null ) )
            {
                return false;
            }
            else if ( ( getDescription() != null ) && ( at.getDescription() == null ) )
            {
                return false;
            }
            else if ( ( getDescription() != null ) && ( at.getDescription() != null ) )
            {
                if ( !getDescription().equals( at.getDescription() ) )
                {
                    return false;
                }
            }

            // Superior
            if ( ( getSuperiorName() == null ) && ( at.getSuperiorName() != null ) )
            {
                return false;
            }
            else if ( ( getSuperiorName() != null ) && ( at.getSuperiorName() == null ) )
            {
                return false;
            }
            else if ( ( getSuperiorName() != null ) && ( at.getSuperiorName() != null ) )
            {
                if ( !getSuperiorName().equals( at.getSuperiorName() ) )
                {
                    return false;
                }
            }

            // Usage
            if ( ( getUsage() == null ) && ( at.getUsage() != null ) )
            {
                return false;
            }
            else if ( ( getUsage() != null ) && ( at.getUsage() == null ) )
            {
                return false;
            }
            else if ( ( getUsage() != null ) && ( at.getUsage() != null ) )
            {
                if ( !getUsage().equals( at.getUsage() ) )
                {
                    return false;
                }
            }

            // Syntax
            if ( ( getSyntaxOid() == null ) && ( at.getSyntaxOid() != null ) )
            {
                return false;
            }
            else if ( ( getSyntaxOid() != null ) && ( at.getSyntaxOid() == null ) )
            {
                return false;
            }
            else if ( ( getSyntaxOid() != null ) && ( at.getSyntaxOid() != null ) )
            {
                if ( !getSyntaxOid().equals( at.getSyntaxOid() ) )
                {
                    return false;
                }
            }

            // Syntax length
            if ( getLength() != at.getLength() )
            {
                return false;
            }

            // Obsolete
            if ( isObsolete() != at.isObsolete() )
            {
                return false;
            }

            // Single value
            if ( isSingleValue() != at.isSingleValue() )
            {
                return false;
            }

            // Collective
            if ( isCollective() != at.isCollective() )
            {
                return false;
            }

            // No User Modification
            if ( isCanUserModify() != at.isCanUserModify() )
            {
                return false;
            }

            // Equality matching rule
            if ( ( getEqualityName() == null ) && ( at.getEqualityName() != null ) )
            {
                return false;
            }
            else if ( ( getEqualityName() != null ) && ( at.getEqualityName() == null ) )
            {
                return false;
            }
            else if ( ( getEqualityName() != null ) && ( at.getEqualityName() != null ) )
            {
                if ( !getEqualityName().equals( at.getEqualityName() ) )
                {
                    return false;
                }
            }

            // Ordering matching rule
            if ( ( getOrderingName() == null ) && ( at.getOrderingName() != null ) )
            {
                return false;
            }
            else if ( ( getOrderingName() != null ) && ( at.getOrderingName() == null ) )
            {
                return false;
            }
            else if ( ( getOrderingName() != null ) && ( at.getOrderingName() != null ) )
            {
                if ( !getOrderingName().equals( at.getOrderingName() ) )
                {
                    return false;
                }
            }

            // Substring matching rule
            if ( ( getSubstrName() == null ) && ( at.getSubstrName() != null ) )
            {
                return false;
            }
            else if ( ( getSubstrName() != null ) && ( at.getSubstrName() == null ) )
            {
                return false;
            }
            else if ( ( getSubstrName() != null ) && ( at.getSubstrName() != null ) )
            {
                if ( !getSubstrName().equals( at.getSubstrName() ) )
                {
                    return false;
                }
            }

            // If we've reached here, the two objects are equal.
            return true;
        }
        else
        {
            return false;
        }
    }
}
