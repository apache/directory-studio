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
package org.apache.directory.studio.apacheds.schemaeditor.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl;


/**
 * This class represents the SchemaHandler.
 * <p>
 * It used to handle the whole Schema (including schemas, attribute types, 
 * object classes, matching rules and syntaxes).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaHandler
{
    /** The SchemaHandler instance */
    private static SchemaHandler instance;

    //
    // The Lists
    //
    /** The schemas List */
    private List<Schema> schemasList;
    /** The attribute types List */
    private List<AttributeTypeImpl> attributeTypesList;
    /** The matching rules List */
    private List<MatchingRuleImpl> matchingRulesList;
    /** The object classes List */
    private List<ObjectClassImpl> objectClassesList;
    /** The syntaxes List */
    private List<SyntaxImpl> syntaxesList;

    //
    // The Maps (for fast searching)
    //
    /** The schemas Map */
    private Map<String, Schema> schemasMap;
    /** The attribute types Map */
    private Map<String, AttributeTypeImpl> attributeTypesMap;
    /** The matching rules Map */
    private Map<String, MatchingRuleImpl> matchingRulesMap;
    /** The object classes Map */
    private Map<String, ObjectClassImpl> objectClassesMap;
    /** The syntaxes Map */
    private Map<String, SyntaxImpl> syntaxesMap;

    //
    // The Listeners Lists
    //
    private List<SchemaHandlerListener> schemaHandlerListeners;
    private MultiValueMap schemaListeners;
    private MultiValueMap attributeTypeListeners;
    private MultiValueMap objectClassListeners;


    /**
     * Creates a new instance of SchemaHandler.
     */
    private SchemaHandler()
    {
        // Lists
        schemasList = new ArrayList<Schema>();
        attributeTypesList = new ArrayList<AttributeTypeImpl>();
        matchingRulesList = new ArrayList<MatchingRuleImpl>();;
        objectClassesList = new ArrayList<ObjectClassImpl>();
        syntaxesList = new ArrayList<SyntaxImpl>();

        // Maps
        schemasMap = new HashMap<String, Schema>();
        attributeTypesMap = new HashMap<String, AttributeTypeImpl>();
        matchingRulesMap = new HashMap<String, MatchingRuleImpl>();
        objectClassesMap = new HashMap<String, ObjectClassImpl>();
        syntaxesMap = new HashMap<String, SyntaxImpl>();

        // Listeners
        schemaHandlerListeners = new ArrayList<SchemaHandlerListener>();
        schemaListeners = new MultiValueMap();
        attributeTypeListeners = new MultiValueMap();
        objectClassListeners = new MultiValueMap();
    }


    /**
     * Gets the singleton instance of the SchemaHandler.
     *
     * @return
     *      the singleton instance of the SchemaHandler
     */
    public static SchemaHandler getInstance()
    {
        if ( instance == null )
        {
            instance = new SchemaHandler();
        }

        return instance;
    }


    /**
     * Gets the List of all the attribute types.
     *
     * @return
     *      the List of all the attribute types
     */
    public List<AttributeTypeImpl> getAttributeTypes()
    {
        return attributeTypesList;
    }


    /**
     * Gets the List of all the matching rules.
     *
     * @return
     *      the List of all the matching rules
     */
    public List<MatchingRuleImpl> getMatchingRules()
    {
        return matchingRulesList;
    }


    /**
     * Gets the List of all the object classes.
     *
     * @return
     *      the List of all the object classes
     */
    public List<ObjectClassImpl> getObjectClasses()
    {
        return objectClassesList;
    }


    /**
     * Gets the List of all the schemas.
     *
     * @return
     *      the List of all the schemas
     */
    public List<Schema> getSchemas()
    {
        return schemasList;
    }


    /**
     * Gets the List of all the matching rules.
     *
     * @return
     *      the List of all the matching rules
     */
    public List<SyntaxImpl> getSyntaxes()
    {
        return syntaxesList;
    }


    /**
     * Gets an attribute type identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding attribute type, or null if no one is found
     */
    public AttributeTypeImpl getAttributeType( String id )
    {
        return attributeTypesMap.get( id );
    }


    /**
     * Gets a matching rule identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding matching rule, or null if no one is found
     */
    public MatchingRuleImpl getMatchingRule( String id )
    {
        return matchingRulesMap.get( id );
    }


    /**
     * Gets an object class identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding object class, or null if no one is found
     */
    public ObjectClassImpl getObjectClass( String id )
    {
        return objectClassesMap.get( id );
    }


    /**
     * Gets a schema identified by a name.
     *
     * @param name
     *      a name
     * @return
     *      the corresponding schema, or null if no one is found
     */
    public Schema getSchema( String name )
    {
        return schemasMap.get( name );
    }


    /**
     * Gets a syntax identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding syntax, or null if no one is found
     */
    public SyntaxImpl getSyntax( String id )
    {
        return syntaxesMap.get( id );
    }


    /**
     * Adds a SchemaListener to the given schema.
     *
     * @param schema
     *      the schema
     * @param listener
     *      the listener
     */
    public void addListener( SchemaHandlerListener listener )
    {
        if ( !schemaHandlerListeners.contains( listener ) )
        {
            schemaHandlerListeners.add( listener );
        }
    }


    /**
     * Adds a SchemaListener to the given schema.
     *
     * @param schema
     *      the schema
     * @param listener
     *      the listener
     */
    public void addListener( Schema schema, SchemaListener listener )
    {
        if ( !schemaListeners.containsValue( schema, listener ) )
        {
            schemaListeners.put( schema, listener );
        }
    }


    /**
     * Adds a AttributeTypeListener to the given attribute type.
     *
     * @param at
     *      the attribute type
     * @param listener
     *      the listener
     */
    public void addListener( AttributeTypeImpl at, AttributeTypeListener listener )
    {
        if ( !attributeTypeListeners.containsValue( at, listener ) )
        {
            attributeTypeListeners.put( at, listener );
        }
    }


    /**
     * Adds a AttributeTypeListener to the given object class.
     *
     * @param oc
     *      the object class
     * @param listener
     *      the listener
     */
    public void addListener( ObjectClassImpl oc, ObjectClassListener listener )
    {
        if ( !objectClassListeners.containsValue( oc, listener ) )
        {
            objectClassListeners.put( oc, listener );
        }
    }


    /**
     * Adds a schema
     *
     * @param schema
     *      the schema
     */
    public void addSchema( Schema schema )
    {
        // Adding the schema
        schemasList.add( schema );
        schemasMap.put( schema.getName(), schema );

        // Adding its attribute types
        for ( AttributeTypeImpl at : schema.getAttributeTypes() )
        {
            addSchemaObject( at );

        }

        // Adding its matching rules
        for ( MatchingRuleImpl mr : schema.getMatchingRules() )
        {
            addSchemaObject( mr );
        }

        // Adding its object classes
        for ( ObjectClassImpl oc : schema.getObjectClasses() )
        {
            addSchemaObject( oc );
        }

        // Adding its syntaxes
        for ( SyntaxImpl syntax : schema.getSyntaxes() )
        {
            addSchemaObject( syntax );
        }

        notifySchemaAdded( schema );
    }


    /**
     * Adds the given SchemaObject to the corresponding List and Map
     *
     * @param object
     *      the SchemaObject
     */
    private void addSchemaObject( SchemaObject object )
    {
        if ( object instanceof AttributeTypeImpl )
        {
            AttributeTypeImpl at = ( AttributeTypeImpl ) object;
            attributeTypesList.add( at );
            for ( String name : at.getNames() )
            {
                attributeTypesMap.put( name, at );
            }
            attributeTypesMap.put( at.getOid(), at );
        }
        else if ( object instanceof MatchingRuleImpl )
        {
            MatchingRuleImpl mr = ( MatchingRuleImpl ) object;
            matchingRulesList.add( mr );
            for ( String name : mr.getNames() )
            {
                matchingRulesMap.put( name, mr );
            }
            matchingRulesMap.put( mr.getOid(), mr );
        }
        else if ( object instanceof ObjectClassImpl )
        {
            ObjectClassImpl oc = ( ObjectClassImpl ) object;
            objectClassesList.add( oc );
            for ( String name : oc.getNames() )
            {
                objectClassesMap.put( name, oc );
            }
            objectClassesMap.put( oc.getOid(), oc );
        }
        else if ( object instanceof SyntaxImpl )
        {
            SyntaxImpl syntax = ( SyntaxImpl ) object;
            syntaxesList.add( syntax );
            for ( String name : syntax.getNames() )
            {
                syntaxesMap.put( name, syntax );
            }
            syntaxesMap.put( syntax.getOid(), syntax );
        }
    }


    /**
     * Removes the given schema. 
     *
     * @param schema
     *      the schema
     */
    public void removeSchema( Schema schema )
    {
        // Removing the schema
        schemasList.remove( schema );
        schemasMap.remove( schema.getName() );

        // Removing its attribute types
        for ( AttributeTypeImpl at : schema.getAttributeTypes() )
        {
            removeSchemaObject( at );
        }

        // Removing its matching rules
        for ( MatchingRuleImpl mr : schema.getMatchingRules() )
        {
            removeSchemaObject( mr );
        }

        // Removing its object classes
        for ( ObjectClassImpl oc : schema.getObjectClasses() )
        {
            removeSchemaObject( oc );
        }

        // Removing its syntaxes
        for ( SyntaxImpl syntax : schema.getSyntaxes() )
        {
            removeSchemaObject( syntax );
        }

        notifySchemaRemoved( schema );
    }


    /**
     * Removes the given SchemaObject to the corresponding List and Map
     *
     * @param object
     *      the SchemaObject
     */
    private void removeSchemaObject( SchemaObject object )
    {
        if ( object instanceof AttributeTypeImpl )
        {
            AttributeTypeImpl at = ( AttributeTypeImpl ) object;
            attributeTypesList.remove( at );
            for ( String name : at.getNames() )
            {
                attributeTypesMap.remove( name );
            }
            attributeTypesMap.remove( at.getOid() );
        }
        else if ( object instanceof MatchingRuleImpl )
        {
            MatchingRuleImpl mr = ( MatchingRuleImpl ) object;
            matchingRulesList.remove( mr );
            for ( String name : mr.getNames() )
            {
                matchingRulesMap.remove( name );
            }
            matchingRulesMap.remove( mr.getOid() );
        }
        else if ( object instanceof ObjectClassImpl )
        {
            ObjectClassImpl oc = ( ObjectClassImpl ) object;
            objectClassesList.remove( oc );
            for ( String name : oc.getNames() )
            {
                objectClassesMap.remove( name );
            }
            objectClassesMap.remove( oc.getOid() );
        }
        else if ( object instanceof SyntaxImpl )
        {
            SyntaxImpl syntax = ( SyntaxImpl ) object;
            syntaxesList.remove( syntax );
            for ( String name : syntax.getNames() )
            {
                syntaxesMap.remove( name );
            }
            syntaxesMap.remove( syntax.getOid() );
        }
    }


    /**
     * Adds the given attribute type.
     *
     * @param at
     *      the attribute type
     */
    public void addAttributeType( AttributeTypeImpl at )
    {
        Schema schema = getSchema( at.getSchema() );

        if ( schema == null )
        {
            // TODO Throw an exception
        }

        schema.addAttributeType( at );
        addSchemaObject( at );

        // Notifying the listeners
        notifyAttributeTypeAdded( at );
    }


    /**
     * Update the source attribute type with the values of the 
     * destination attribute type.
     *
     * @param at1
     *      the source attribute type
     * @param at2
     *      the destination attribute type
     */
    public void modifyAttributeType( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        // Removing the references (in case of the names or oid have changed)
        removeSchemaObject( at1 );

        // Updating the attribute type
        at1.setNames( at2.getNames() );
        at1.setOid( at2.getOid() );
        at1.setDescription( at2.getDescription() );
        at1.setSuperiorName( at2.getSuperiorName() );
        at1.setUsage( at2.getUsage() );
        at1.setSyntaxOid( at2.getSyntaxOid() );
        at1.setLength( at2.getLength() );
        at1.setObsolete( at2.isObsolete() );
        at1.setSingleValue( at2.isSingleValue() );
        at1.setCollective( at2.isCollective() );
        at1.setCanUserModify( at2.isCanUserModify() );
        at1.setEqualityName( at1.getEqualityName() );
        at1.setOrderingName( at2.getOrderingName() );
        at1.setSubstrName( at2.getSubstrName() );

        // Adding the references (in case of the names or oid have changed)
        addSchemaObject( at1 );

        // Notifying the listeners
        notifyAttributeTypeModified( at1 );
    }


    /**
     * Removes the given attribute type.
     *
     * @param at
     *      the attribute type
     */
    public void removeAttributeType( AttributeTypeImpl at )
    {
        Schema schema = getSchema( at.getSchema() );

        if ( schema == null )
        {
            // TODO Throw an exception
        }

        schema.removeAttributeType( at );
        removeSchemaObject( at );

        // Notifying the listeners
        notifyAttributeTypeRemoved( at );
    }


    /**
     * Adds the given object class.
     *
     * @param oc
     *      the object class
     */
    public void addObjectClass( ObjectClassImpl oc )
    {
        Schema schema = getSchema( oc.getSchema() );

        if ( schema == null )
        {
            // TODO Throw an exception
        }

        schema.addObjectClass( oc );
        addSchemaObject( oc );

        // Notifying the listeners
        notifyObjectClassAdded( oc );
    }


    /**
     * Update the source object class with the values of the 
     * destination object class.
     *
     * @param oc1
     *      the source object class
     * @param oc2
     *      the destination object class  
     */
    public void modifyObjectClass( ObjectClassImpl oc1, ObjectClassImpl oc2 )
    {
        // Removing the references (in case of the names or oid have changed)
        removeSchemaObject( oc1 );

        // Updating the object class
        oc1.setNames( oc2.getNames() );
        oc1.setOid( oc2.getOid() );
        oc1.setDescription( oc2.getDescription() );
        oc1.setSuperClassesNames( oc2.getSuperClassesNames() );
        oc1.setType( oc2.getType() );
        oc1.setObsolete( oc2.isObsolete() );
        oc1.setMustNamesList( oc2.getMustNamesList() );
        oc1.setMayNamesList( oc2.getMayNamesList() );

        // Adding the references (in case of the names or oid have changed)
        addSchemaObject( oc1 );

        // Notifying the listeners
        notifyObjectClassModified( oc1 );
    }


    /**
     * Removes the given object class.
     *
     * @param oc
     *      the object class
     */
    public void removeObjectClass( ObjectClassImpl oc )
    {
        Schema schema = getSchema( oc.getSchema() );

        if ( schema == null )
        {
            // TODO Throw an exception
        }

        schema.removeObjectClass( oc );
        removeSchemaObject( oc );

        notifyObjectClassRemoved( oc );
    }


    /**
     * Notifies the SchemaHandler listeners that a schema has been added.
     *
     * @param schema
     *      the added schema
     */
    private void notifySchemaAdded( Schema schema )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.schemaAdded( schema );
        }
    }


    /**
     * Notifies the given listeners that a schema has been removed.
     *
     * @param schema
     *      the added schema
     */
    private void notifySchemaRemoved( Schema schema )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.schemaRemoved( schema );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that an attribute type has been added.
     *
     * @param at
     *      the added attribute type
     */
    private void notifyAttributeTypeAdded( AttributeTypeImpl at )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.attributeTypeAdded( at );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that an attribute type has been modified.
     *
     * @param at
     *      the modified attribute type
     */
    private void notifyAttributeTypeModified( AttributeTypeImpl at )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.attributeTypeModified( at );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that an attribute type has been removed.
     *
     * @param at
     *      the removed attribute type
     */
    private void notifyAttributeTypeRemoved( AttributeTypeImpl at )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.attributeTypeRemoved( at );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that an object class has been added.
     *
     * @param oc
     *      the added object class
     */
    private void notifyObjectClassAdded( ObjectClassImpl oc )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.objectClassAdded( oc );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that an object class has been modified.
     *
     * @param oc
     *      the modified object class
     */
    private void notifyObjectClassModified( ObjectClassImpl oc )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.objectClassModified( oc );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that an object class has been removed.
     *
     * @param oc
     *      the removed object class
     */
    private void notifyObjectClassRemoved( ObjectClassImpl oc )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.objectClassRemoved( oc );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that a matching rule has been added.
     *
     * @param mr
     *      the added matching rule
     */
    private void notifyMatchingRuleAdded( MatchingRuleImpl mr )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.matchingRuleAdded( mr );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that a matching rule has been modified.
     *
     * @param mr
     *      the modified matching rule
     */
    private void notifyMatchingRuleModified( MatchingRuleImpl mr )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.matchingRuleModified( mr );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that a matching rule has been removed.
     *
     * @param mr
     *      the removed matching rule
     */
    private void notifyMatchingRuleRemoved( MatchingRuleImpl mr )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.matchingRuleRemoved( mr );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that a syntax has been added.
     *
     * @param syntax
     *      the added syntax
     */
    private void notifySyntaxRuleAdded( SyntaxImpl syntax )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.syntaxAdded( syntax );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that a syntax has been modified.
     *
     * @param syntax
     *      the modified syntax
     */
    private void notifySyntaxRuleModified( SyntaxImpl syntax )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.syntaxModified( syntax );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that a syntax has been removed.
     *
     * @param syntax
     *      the removed syntax
     */
    private void notifySyntaxRemoved( SyntaxImpl syntax )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners )
        {
            listener.syntaxRemoved( syntax );
        }
    }
}
