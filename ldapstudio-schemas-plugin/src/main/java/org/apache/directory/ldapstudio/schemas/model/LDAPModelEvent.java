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

    public enum Reason
    {
        SchemaAdded, SchemaRemoved, multipleSchemaRemoved, multipleSchemaAdded, SchemaSaved, OCAdded, OCRemoved, ATAdded, ATRemoved, OCModified, ATModified, poolCleared
    }

    private Reason reason = null;
    private Object element = null;


    /******************************************
     *               Constructors             *
     ******************************************/

    /**
     * Default constructor, takes only a reason as argument
     */
    public LDAPModelEvent( Reason reason )
    {
        this.reason = reason;
    }


    /**
     * Constructor for objectClass motivated events
     * @param reason takes only OCAdded, OCRemoved or OCModified reasons
     * @param objectClass the incriminated objectClass
     * @throws Exception if bad reason
     */
    public LDAPModelEvent( Reason reason, ObjectClass objectClass ) throws Exception
    {
        this( reason );
        if ( ( reason == Reason.OCAdded ) || ( reason == Reason.OCModified ) || ( reason == Reason.OCRemoved ) )
        {
            this.element = objectClass;
        }
        else
            throw new Exception( "Event creation exception " + reason + " " + objectClass ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Constructor for attributeType motivated events
     * @param reason takes only ATAdded, ATRemoved or ATModified reasons
     * @param attributeType the incriminated attributeType
     * @throws Exception if bad reason
     */
    public LDAPModelEvent( Reason reason, AttributeType attributeType ) throws Exception
    {
        this( reason );
        if ( ( reason == Reason.ATAdded ) || ( reason == Reason.ATModified ) || ( reason == Reason.ATRemoved ) )
        {
            this.element = attributeType;
        }
        else
            throw new Exception( "Event creation exception " + reason + " " + attributeType ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Constructor for schema motivated events
     * @param reason takes only SchemaAdded or SchemaRemoved reasons
     * @param schema the incriminated schema
     * @throws Exception if bad reason
     */
    public LDAPModelEvent( Reason reason, Schema schema ) throws Exception
    {
        this( reason );

        if ( ( reason == Reason.SchemaAdded ) || ( reason == Reason.SchemaRemoved ) )
        {
            this.element = schema;
        }
        else
            throw new Exception( "Event creation exception " + reason + " " + schema ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /******************************************
     *              Accessors                 *
     ******************************************/

    /**
     * If specified, returns the element that was the cause of the Event
     * @return an ObjectClass, an AttributeType, a Schema or null if not specified
     */
    public Object getElement()
    {
        return element;
    }


    /**
     * @return Returns the reason of the event
     */
    public Reason getReason()
    {
        return reason;
    }
}
