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


import org.apache.directory.shared.ldap.schema.AttributeType;


/**
 * This class represents an attribute type.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeTypeImpl extends AttributeType
{
    private static final long serialVersionUID = 1L;

    /** The schema object */
    private Schema schemaObject;


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


    public Schema getSchemaObject()
    {
        return schemaObject;
    }


    public void setSchemaObject( Schema schemaObject )
    {
        this.schemaObject = schemaObject;
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
            if ( ( getSuperiorOid() == null ) && ( at.getSuperiorOid() != null ) )
            {
                return false;
            }
            else if ( ( getSuperiorOid() != null ) && ( at.getSuperiorOid() == null ) )
            {
                return false;
            }
            else if ( ( getSuperiorOid() != null ) && ( at.getSuperiorOid() != null ) )
            {
                if ( !getSuperiorOid().equals( at.getSuperiorOid() ) )
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
            if ( getSyntaxLength() != at.getSyntaxLength() )
            {
                return false;
            }

            // Obsolete
            if ( isObsolete() != at.isObsolete() )
            {
                return false;
            }

            // Single value
            if ( isSingleValued() != at.isSingleValued() )
            {
                return false;
            }

            // Collective
            if ( isCollective() != at.isCollective() )
            {
                return false;
            }

            // No User Modification
            if ( isUserModifiable() != at.isUserModifiable() )
            {
                return false;
            }

            // Equality matching rule
            if ( ( getEqualityOid() == null ) && ( at.getEqualityOid() != null ) )
            {
                return false;
            }
            else if ( ( getEqualityOid() != null ) && ( at.getEqualityOid() == null ) )
            {
                return false;
            }
            else if ( ( getEqualityOid() != null ) && ( at.getEqualityOid() != null ) )
            {
                if ( !getEqualityOid().equals( at.getEqualityOid() ) )
                {
                    return false;
                }
            }

            // Ordering matching rule
            if ( ( getOrderingOid() == null ) && ( at.getOrderingOid() != null ) )
            {
                return false;
            }
            else if ( ( getOrderingOid() != null ) && ( at.getOrderingOid() == null ) )
            {
                return false;
            }
            else if ( ( getOrderingOid() != null ) && ( at.getOrderingOid() != null ) )
            {
                if ( !getOrderingOid().equals( at.getOrderingOid() ) )
                {
                    return false;
                }
            }

            // Substring matching rule
            if ( ( getSubstringOid() == null ) && ( at.getSubstringOid() != null ) )
            {
                return false;
            }
            else if ( ( getSubstringOid() != null ) && ( at.getSubstringOid() == null ) )
            {
                return false;
            }
            else if ( ( getSubstringOid() != null ) && ( at.getSubstringOid() != null ) )
            {
                if ( !getSubstringOid().equals( at.getSubstringOid() ) )
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
