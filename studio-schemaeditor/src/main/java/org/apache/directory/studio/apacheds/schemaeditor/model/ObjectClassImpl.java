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


import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractSchemaObject;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MutableSchemaObject;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;


/**
 * This class implements an object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassImpl extends AbstractSchemaObject implements MutableSchemaObject, ObjectClass
{
    private static final long serialVersionUID = 1L;

    /** The object OID */
    private String objectOid;

    /** The object class type */
    private ObjectClassTypeEnum type;

    /** The optional attribute type names list */
    private String[] mayNamesList = new String[0];

    /** The mandatory attribute type names list */
    private String[] mustNamesList = new String[0];

    /** The super class names list */
    private String[] superClassesNames = new String[0];


    /**
     * Creates a new instance of ObjectClassImpl.
     *
     * @param oid
     *      the OID of the object class
     */
    public ObjectClassImpl( String oid )
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
     * @see org.apache.directory.shared.ldap.schema.ObjectClass#getType()
     */
    public ObjectClassTypeEnum getType()
    {
        return type;
    }


    /**
     * Sets the type of the object class.
     *
     * @param objectClassTypeEnum
     *      the type of the object class
     */
    public void setType( ObjectClassTypeEnum objectClassTypeEnum )
    {
        this.type = objectClassTypeEnum;
    }


    /**
     * gets the names of the super classes.
     *
     * @return
     *      the names of the super classes
     */
    public String[] getSuperClassesNames()
    {
        return superClassesNames;
    }


    /**
     * Sets the names of the super classes.
     *
     * @param superClassesNames
     *      the names of the super classes
     */
    public void setSuperClassesNames( String[] superClassesNames )
    {
        this.superClassesNames = superClassesNames;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.ObjectClass#getSuperClasses()
     */
    public ObjectClass[] getSuperClasses() throws NamingException
    {
        return null;
    }


    /**
     * Gets the names of the mandatory attribute types.
     *
     * @return
     *      the names of the mandatory attribute types
     */
    public String[] getMustNamesList()
    {
        return mustNamesList;
    }


    /**
     * Set the names of the mandatory attribute types.
     *
     * @param mustNamesList
     *      the names of the mandatory attribute types
     */
    public void setMustNamesList( String[] mustNamesList )
    {
        this.mustNamesList = mustNamesList;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.ObjectClass#getMustList()
     */
    public AttributeType[] getMustList() throws NamingException
    {
        return null;
    }


    /**
     * Gets the names of the optional attribute types.
     *
     * @return
     *      the names of the optional attribute types
     */
    public String[] getMayNamesList()
    {
        return mayNamesList;
    }


    /**
     * Sets the names of the optional attribute types.
     *
     * @param mayNamesList
     *      the names of the optional attribute types
     */
    public void setMayNamesList( String[] mayNamesList )
    {
        this.mayNamesList = mayNamesList;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.ObjectClass#getMayList()
     */
    public AttributeType[] getMayList() throws NamingException
    {
        return null;
    }


    /* (non-Javadoc)
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
            if ( ( getSuperClassesNames() == null ) && ( oc.getSuperClassesNames() != null ) )
            {
                return false;
            }
            else if ( ( getSuperClassesNames() != null ) && ( oc.getSuperClassesNames() == null ) )
            {
                return false;
            }
            else if ( ( getSuperClassesNames() != null ) && ( oc.getSuperClassesNames() != null ) )
            {
                if ( !getSuperClassesNames().equals( oc.getSuperClassesNames() ) )
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
            if ( ( getMustNamesList() == null ) && ( oc.getMustNamesList() != null ) )
            {
                return false;
            }
            else if ( ( getMustNamesList() != null ) && ( oc.getMustNamesList() == null ) )
            {
                return false;
            }
            else if ( ( getMustNamesList() != null ) && ( oc.getMustNamesList() != null ) )
            {
                if ( !getMustNamesList().equals( oc.getMustNamesList() ) )
                {
                    return false;
                }
            }

            // Optional attributes
            if ( ( getMayNamesList() == null ) && ( oc.getMayNamesList() != null ) )
            {
                return false;
            }
            else if ( ( getMayNamesList() != null ) && ( oc.getMayNamesList() == null ) )
            {
                return false;
            }
            else if ( ( getMayNamesList() != null ) && ( oc.getMayNamesList() != null ) )
            {
                if ( !getMayNamesList().equals( oc.getMayNamesList() ) )
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
