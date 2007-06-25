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
package org.apache.directory.studio.apacheds.schemaeditor.model;


import java.util.ArrayList;
import java.util.List;

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

    /** The listeners */
    private List<AttributeTypeListener> listeners;


    /**
     * Creates a new instance of AttributeTypeImpl.
     *
     * @param oid
     *      the OID of the attribute type
     */
    public AttributeTypeImpl( String oid )
    {
        super( oid );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#setNames(java.lang.String[])
     */
    public void setNames( String[] names )
    {
        super.setNames( names );
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


    /**
     * Adds an AttributeTypeListener.
     *
     * @param listener
     *      the AttributeTypeListener
     */
    public void addListener( AttributeTypeListener listener )
    {
        if ( listeners == null )
        {
            listeners = new ArrayList<AttributeTypeListener>();
        }

        listeners.add( listener );
    }


    /**
     * Removes an AttributeTypeListener
     *
     * @param listener
     *      the AttributeTypeListener
     */
    public void removeListener( AttributeTypeListener listener )
    {
        if ( listeners != null )
        {
            listeners.remove( listener );
        }
    }
}
