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


import org.apache.directory.shared.ldap.schema.ObjectClass;


/**
 * This class implements an object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ObjectClassImpl extends ObjectClass
{
    private static final long serialVersionUID = 1L;

    /** The schema object */
    private Schema schemaObject;


    /**
     * Creates a new instance of ObjectClassImpl.
     * 
     * @param oid
     *            the OID of the object class
     */
    public ObjectClassImpl( String oid )
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


    /*
     * (non-Javadoc)
     * 
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof ObjectClassImpl )
        {
            ObjectClassImpl oc = ( ObjectClassImpl ) obj;

            // OID
            if ( ( getOid() == null ) && ( oc.getOid() != null ) )
            {
                return false;
            }
            else if ( ( getOid() != null ) && ( oc.getOid() == null ) )
            {
                return false;
            }
            else if ( ( getOid() != null ) && ( oc.getOid() != null ) )
            {
                if ( !getOid().equals( oc.getOid() ) )
                {
                    return false;
                }
            }

            // Aliases
            if ( ( getNames() == null ) && ( oc.getNames() != null ) )
            {
                return false;
            }
            else if ( ( getNames() != null ) && ( oc.getNames() == null ) )
            {
                return false;
            }
            else if ( ( getNames() != null ) && ( oc.getNames() != null ) )
            {
                if ( !getNames().equals( oc.getNames() ) )
                {
                    return false;
                }
            }

            // Description
            if ( ( getDescription() == null ) && ( oc.getDescription() != null ) )
            {
                return false;
            }
            else if ( ( getDescription() != null ) && ( oc.getDescription() == null ) )
            {
                return false;
            }
            else if ( ( getDescription() != null ) && ( oc.getDescription() != null ) )
            {
                if ( !getDescription().equals( oc.getDescription() ) )
                {
                    return false;
                }
            }

            // Superiors
            if ( ( getSuperiorOids() == null ) && ( oc.getSuperiorOids() != null ) )
            {
                return false;
            }
            else if ( ( getSuperiorOids() != null ) && ( oc.getSuperiorOids() == null ) )
            {
                return false;
            }
            else if ( ( getSuperiorOids() != null ) && ( oc.getSuperiorOids() != null ) )
            {
                if ( !getSuperiorOids().equals( oc.getSuperiorOids() ) )
                {
                    return false;
                }
            }

            // Type
            if ( getType() != oc.getType() )
            {
                return false;
            }

            // Obsolete
            if ( isObsolete() != oc.isObsolete() )
            {
                return false;
            }

            // Mandatory attributes
            if ( ( getMustAttributeTypeOids() == null ) && ( oc.getMustAttributeTypeOids() != null ) )
            {
                return false;
            }
            else if ( ( getMustAttributeTypeOids() != null ) && ( oc.getMustAttributeTypeOids() == null ) )
            {
                return false;
            }
            else if ( ( getMustAttributeTypeOids() != null ) && ( oc.getMustAttributeTypeOids() != null ) )
            {
                if ( !getMustAttributeTypeOids().equals( oc.getMustAttributeTypeOids() ) )
                {
                    return false;
                }
            }

            // Optional attributes
            if ( ( getMayAttributeTypeOids() == null ) && ( oc.getMayAttributeTypeOids() != null ) )
            {
                return false;
            }
            else if ( ( getMayAttributeTypeOids() != null ) && ( oc.getMayAttributeTypeOids() == null ) )
            {
                return false;
            }
            else if ( ( getMayAttributeTypeOids() != null ) && ( oc.getMayAttributeTypeOids() != null ) )
            {
                if ( !getMayAttributeTypeOids().equals( oc.getMayAttributeTypeOids() ) )
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
