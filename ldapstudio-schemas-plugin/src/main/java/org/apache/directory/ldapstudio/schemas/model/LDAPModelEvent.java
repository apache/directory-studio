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

package org.apache.directory.ldapstudio.schemas.model;


/**
 * This class is a model for creation of events related to modifications of the model elements
 * manipulated by this application. Like when a schema is added to the pool, or when the properties
 * of an attributeType are modified.
 * When creating an event, the reason is mandatory, but not the originating element.
 * 
 * @see the Reason enumeration for the complete list of supported events
 *
 */
public class LDAPModelEvent
{

    /**
     * This Enum is used to indicate the reason of the launch of the event.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public enum Reason
    {
        SchemaAdded, SchemaRemoved, multipleSchemaRemoved, multipleSchemaAdded, SchemaSaved, OCAdded, OCRemoved, ATAdded, ATRemoved, OCModified, ATModified, poolCleared
    }

    /** The reason */
    private Reason reason;

    /** The old value of the element */
    private Object oldValue;

    /** The new value of the element */
    private Object newValue;


    /**
     * Creates a new instance of LDAPModelEvent.
     *
     * @param reason
     *      the reason
     */
    public LDAPModelEvent( Reason reason )
    {
        this.reason = reason;
    }


    /**
     * Creates a new instance of LDAPModelEvent for object class motivated events.
     *
     * @param reason
     *      the reason (must be OCAdded, OCRemoved or OCModified)
     * @param oldObjectClass
     *      the old object class
     * @param newObjectClass
     *      the new object class
     * @throws Exception
     *      if bad reason
     */
    public LDAPModelEvent( Reason reason, ObjectClass oldObjectClass, ObjectClass newObjectClass ) throws Exception
    {
        this( reason );
        if ( ( reason == Reason.OCAdded ) || ( reason == Reason.OCModified ) || ( reason == Reason.OCRemoved ) )
        {
            newValue = newObjectClass;
            oldValue = oldObjectClass;
        }
        else
        {
            throw new Exception( "Event creation exception " + reason + " " + newObjectClass ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }


    /**
     * Creates a new instance of LDAPModelEvent for attribute type motivated events.
     *
     * @param reason
     *      the reason (must be ATAdded, ATRemoved or ATModified)
     * @param oldAttributeType
     *      the old attribute type
     * @param newAttributeType
     *      the new attribute type
     * @throws Exception
     *      if bad reason
     */
    public LDAPModelEvent( Reason reason, AttributeType oldAttributeType, AttributeType newAttributeType )
        throws Exception
    {
        this( reason );
        if ( ( reason == Reason.ATAdded ) || ( reason == Reason.ATModified ) || ( reason == Reason.ATRemoved ) )
        {
            newValue = newAttributeType;
            oldValue = oldAttributeType;
        }
        else
        {
            throw new Exception( "Event creation exception " + reason + " " + oldAttributeType ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }


    /**
     * Creates a new instance of LDAPModelEvent for schema motivated events.
     *
     * @param reason
     *      the reason (must be SchemaAdded or SchemaRemoved)
     * @param schema
     *      the associated schema
     * @throws Exception
     *      if bad reason
     */
    public LDAPModelEvent( Reason reason, Schema schema ) throws Exception
    {
        this( reason );

        if ( ( reason == Reason.SchemaAdded ) || ( reason == Reason.SchemaRemoved ) )
        {
            newValue = schema;
        }
        else
        {
            throw new Exception( "Event creation exception " + reason + " " + schema ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }


    /**
     * Gets the reason of the the event.
     * 
     * @return
     *      the reason of the event
     */
    public Reason getReason()
    {
        return reason;
    }


    /**
     * Gets the new value of the element.
     *
     * @return
     *      the new value of the element.
     */
    public Object getNewValue()
    {
        return newValue;
    }


    /**
     * Gets the new value of the element.
     *
     * @return
     *      the new value of the element.
     */
    public Object getOldValue()
    {
        return oldValue;
    }
}
