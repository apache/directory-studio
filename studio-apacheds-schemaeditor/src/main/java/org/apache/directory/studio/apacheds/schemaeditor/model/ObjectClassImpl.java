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

    /** The object class type */
    private ObjectClassTypeEnum type;

    /** The optional attribute type names list */
    private String[] mayNamesList;

    /** The mandatory attribute type names list */
    private String[] mustNamesList;

    /** The super class names list */
    private String[] superClassesNames;

    /** The listeners */
    private List<ObjectClassListener> listeners;


    /**
     * Creates a new instance of ObjectClassImpl.
     *
     * @param oid
     *      the OID of the object class
     */
    public ObjectClassImpl( String oid )
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


    /**
     * Adds an ObjectClassListener.
     *
     * @param listener
     *      the ObjectClassListener
     */
    public void addListener( ObjectClassListener listener )
    {
        if ( listeners == null )
        {
            listeners = new ArrayList<ObjectClassListener>();
        }

        listeners.add( listener );
    }


    /**
     * Removes an ObjectClassListener
     *
     * @param listener
     *      the ObjectClassListener
     */
    public void removeListener( ObjectClassListener listener )
    {
        if ( listeners != null )
        {
            listeners.remove( listener );
        }
    }
}
