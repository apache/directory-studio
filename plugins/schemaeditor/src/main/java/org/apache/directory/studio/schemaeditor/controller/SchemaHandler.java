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
package org.apache.directory.studio.schemaeditor.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.MutableAttributeType;
import org.apache.directory.shared.ldap.model.schema.MutableObjectClass;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.shared.util.Strings;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * This class represents the SchemaHandler.
 * <p>
 * It used to handle the whole Schema (including schemas, attribute types,
 * object classes, matching rules and syntaxes).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaHandler
{
    //
    // The Lists
    //
    /** The schemas List */
    private List<Schema> schemasList;
    /** The attribute types List */
    private List<AttributeType> attributeTypesList;
    /** The matching rules List */
    private List<MatchingRule> matchingRulesList;
    /** The object classes List */
    private List<ObjectClass> objectClassesList;
    /** The syntaxes List */
    private List<LdapSyntax> syntaxesList;

    //
    // The MultiMap (for fast searching)
    //
    /** The schemas MultiMap */
    private MultiMap schemasMap;
    /** The attribute types MultiMap */
    private MultiMap attributeTypesMap;
    /** The matching rules MultiMap */
    private MultiMap matchingRulesMap;
    /** The object classes MultiMap */
    private MultiMap objectClassesMap;
    /** The syntaxes MultiMap */
    private MultiMap syntaxesMap;

    //
    // The Listeners Lists
    //
    private List<SchemaHandlerListener> schemaHandlerListeners;


    /**
     * Creates a new instance of SchemaHandler.
     */
    public SchemaHandler()
    {
        // Lists
        schemasList = new ArrayList<Schema>();
        attributeTypesList = new ArrayList<AttributeType>();
        matchingRulesList = new ArrayList<MatchingRule>();;
        objectClassesList = new ArrayList<ObjectClass>();
        syntaxesList = new ArrayList<LdapSyntax>();

        // Maps
        schemasMap = new MultiValueMap();
        attributeTypesMap = new MultiValueMap();
        matchingRulesMap = new MultiValueMap();
        objectClassesMap = new MultiValueMap();
        syntaxesMap = new MultiValueMap();

        // Listeners
        schemaHandlerListeners = new ArrayList<SchemaHandlerListener>();
    }


    /**
     * Gets the List of all the attribute types.
     *
     * @return
     *      the List of all the attribute types
     */
    public List<AttributeType> getAttributeTypes()
    {
        return attributeTypesList;
    }


    /**
     * Gets the List of all the matching rules.
     *
     * @return
     *      the List of all the matching rules
     */
    public List<MatchingRule> getMatchingRules()
    {
        return matchingRulesList;
    }


    /**
     * Gets the List of all the object classes.
     *
     * @return
     *      the List of all the object classes
     */
    public List<ObjectClass> getObjectClasses()
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
    public List<LdapSyntax> getSyntaxes()
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
    public AttributeType getAttributeType( String id )
    {
        List<?> list = getAttributeTypeList( Strings.toLowerCase( id ) );

        if ( ( list != null ) && ( list.size() >= 1 ) )
        {
            return ( AttributeType ) list.get( 0 );
        }
        else
        {
            return null;
        }
    }


    /**
     * Get the attribute type(s) List identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding attribute type(s) List or null if no one is found
     */
    public List<?> getAttributeTypeList( String id )
    {
        return ( List<?> ) attributeTypesMap.get( Strings.toLowerCase( id ) );
    }


    /**
     * Gets a matching rule identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding matching rule, or null if no one is found
     */
    public MatchingRule getMatchingRule( String id )
    {
        List<?> list = getMatchingRuleList( Strings.toLowerCase( id ) );

        if ( ( list != null ) && ( list.size() >= 1 ) )
        {
            return ( MatchingRule ) list.get( 0 );
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets a matching rule(s) List identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding matching rule(s) List, or null if no one is found
     */
    public List<?> getMatchingRuleList( String id )
    {
        return ( List<?> ) matchingRulesMap.get( Strings.toLowerCase( id ) );
    }


    /**
     * Gets an object class identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding object class, or null if no one is found
     */
    public MutableObjectClass getObjectClass( String id )
    {
        List<?> list = getObjectClassList( Strings.toLowerCase( id ) );

        if ( ( list != null ) && ( list.size() >= 1 ) )
        {
            return ( MutableObjectClass ) list.get( 0 );
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets an object class(es) List identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding object class(es) List, or null if no one is found
     */
    public List<?> getObjectClassList( String id )
    {
        return ( List<?> ) objectClassesMap.get( Strings.toLowerCase( id ) );
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
        List<?> list = getSchemaList( Strings.toLowerCase( name ) );

        if ( ( list != null ) && ( list.size() >= 1 ) )
        {
            return ( Schema ) list.get( 0 );
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets a schema(s) List identified by a name.
     *
     * @param name
     *      a name
     * @return
     *      the corresponding schema(s) List, or null if no one is found
     */
    public List<?> getSchemaList( String name )
    {
        return ( List<?> ) schemasMap.get( Strings.toLowerCase( name ) );
    }


    /**
     * Gets a syntax identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding syntax, or null if no one is found
     */
    public LdapSyntax getSyntax( String id )
    {
        List<?> list = getSyntaxList( Strings.toLowerCase( id ) );

        if ( ( list != null ) && ( list.size() >= 1 ) )
        {
            return ( LdapSyntax ) list.get( 0 );
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets a syntax(es) List identified by an OID, or an alias.
     *
     * @param id
     *      an OID or an alias
     * @return
     *      the corresponding syntax(es) List, or null if no one is found
     */
    public List<?> getSyntaxList( String id )
    {
        return ( List<?> ) syntaxesMap.get( Strings.toLowerCase( id ) );
    }


    /**
     * Adds a SchemaHandlerListener.
     *
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
     * Removes a SchemaHandlerListener.
     *
     * @param listener
     *      the listener
     */
    public void removeListener( SchemaHandlerListener listener )
    {
        schemaHandlerListeners.remove( listener );
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
        schemasMap.put( Strings.toLowerCase( schema.getSchemaName() ), schema );

        // Adding its attribute types
        for ( AttributeType at : schema.getAttributeTypes() )
        {
            addSchemaObject( at );
        }

        // Adding its matching rules
        for ( MatchingRule mr : schema.getMatchingRules() )
        {
            addSchemaObject( mr );
        }

        // Adding its object classes
        for ( ObjectClass oc : schema.getObjectClasses() )
        {
            addSchemaObject( oc );
        }

        // Adding its syntaxes
        for ( LdapSyntax syntax : schema.getSyntaxes() )
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
        if ( object instanceof AttributeType )
        {
            AttributeType at = ( AttributeType ) object;
            attributeTypesList.add( at );
            List<String> names = at.getNames();
            if ( names != null )
            {
                for ( String name : names )
                {
                    attributeTypesMap.put( Strings.toLowerCase( name ), at );
                }
            }
            attributeTypesMap.put( at.getOid(), at );
        }
        else if ( object instanceof MatchingRule )
        {
            MatchingRule mr = ( MatchingRule ) object;
            matchingRulesList.add( mr );
            List<String> names = mr.getNames();
            if ( names != null )
            {
                for ( String name : names )
                {
                    matchingRulesMap.put( Strings.toLowerCase( name ), mr );
                }
            }
            matchingRulesMap.put( mr.getOid(), mr );
        }
        else if ( object instanceof ObjectClass )
        {
            ObjectClass oc = ( ObjectClass ) object;
            objectClassesList.add( oc );
            List<String> names = oc.getNames();
            if ( names != null )
            {
                for ( String name : names )
                {
                    objectClassesMap.put( Strings.toLowerCase( name ), oc );
                }
            }
            objectClassesMap.put( oc.getOid(), oc );
        }
        else if ( object instanceof LdapSyntax )
        {
            LdapSyntax syntax = ( LdapSyntax ) object;
            syntaxesList.add( syntax );
            List<String> names = syntax.getNames();
            if ( names != null )
            {
                for ( String name : names )
                {
                    syntaxesMap.put( Strings.toLowerCase( name ), syntax );
                }
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
        schemasMap.remove( Strings.toLowerCase( schema.getSchemaName() ) );

        // Removing its attribute types
        for ( AttributeType at : schema.getAttributeTypes() )
        {
            removeSchemaObject( at );
        }

        // Removing its matching rules
        for ( MatchingRule mr : schema.getMatchingRules() )
        {
            removeSchemaObject( mr );
        }

        // Removing its object classes
        for ( ObjectClass oc : schema.getObjectClasses() )
        {
            removeSchemaObject( oc );
        }

        // Removing its syntaxes
        for ( LdapSyntax syntax : schema.getSyntaxes() )
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
        if ( object instanceof AttributeType )
        {
            AttributeType at = ( AttributeType ) object;
            attributeTypesList.remove( at );
            List<String> names = at.getNames();
            if ( names != null )
            {
                for ( String name : names )
                {
                    attributeTypesMap.remove( Strings.toLowerCase( name ) );
                }
            }
            attributeTypesMap.remove( at.getOid() );
        }
        else if ( object instanceof MatchingRule )
        {
            MatchingRule mr = ( MatchingRule ) object;
            matchingRulesList.remove( mr );
            List<String> names = mr.getNames();
            if ( names != null )
            {
                for ( String name : names )
                {
                    matchingRulesMap.remove( Strings.toLowerCase( name ) );
                }
            }
            matchingRulesMap.remove( mr.getOid() );
        }
        else if ( object instanceof ObjectClass )
        {
            ObjectClass oc = ( ObjectClass ) object;
            objectClassesList.remove( oc );
            List<String> names = oc.getNames();
            if ( names != null )
            {
                for ( String name : names )
                {
                    objectClassesMap.remove( Strings.toLowerCase( name ) );
                }
            }
            objectClassesMap.remove( oc.getOid() );
        }
        else if ( object instanceof LdapSyntax )
        {
            LdapSyntax syntax = ( LdapSyntax ) object;
            syntaxesList.remove( syntax );
            List<String> names = syntax.getNames();
            if ( names != null )
            {
                for ( String name : names )
                {
                    syntaxesMap.remove( Strings.toLowerCase( name ) );
                }
            }
            syntaxesMap.remove( syntax.getOid() );
        }
    }


    /**
     * Renames the given schema.
     *
     * @param schema the schema
     * @param newName the new name
     */
    public void renameSchema( Schema schema, String newName )
    {
        schemasMap.remove( Strings.toLowerCase( schema.getSchemaName() ) );
        schema.setSchemaName( newName );
        schemasMap.put( Strings.toLowerCase( schema.getSchemaName() ), schema );

        // Removing its attribute types
        for ( AttributeType at : schema.getAttributeTypes() )
        {
            at.setSchemaName( newName );
        }

        // Removing its matching rules
        for ( MatchingRule mr : schema.getMatchingRules() )
        {
            mr.setSchemaName( newName );
        }

        // Removing its object classes
        for ( ObjectClass oc : schema.getObjectClasses() )
        {
            oc.setSchemaName( newName );
        }

        // Removing its syntaxes
        for ( LdapSyntax syntax : schema.getSyntaxes() )
        {
            syntax.setSchemaName( newName );
        }

        notifySchemaRenamed( schema );
    }


    /**
     * Adds the given attribute type.
     *
     * @param at
     *      the attribute type
     */
    public void addAttributeType( AttributeType at )
    {
        Schema schema = getSchema( at.getSchemaName() );

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
    public void modifyAttributeType( MutableAttributeType at1, MutableAttributeType at2 )
    {
        // Removing the references (in case of the names or oid have changed)
        removeSchemaObject( at1 );

        // Updating the attribute type
        at1.setNames( at2.getNames() );
        at1.setOid( at2.getOid() );
        at1.setDescription( at2.getDescription() );
        at1.setSuperiorOid( at2.getSuperiorOid() );
        at1.setUsage( at2.getUsage() );
        at1.setSyntaxOid( at2.getSyntaxOid() );
        at1.setSyntaxLength( at2.getSyntaxLength() );
        at1.setObsolete( at2.isObsolete() );
        at1.setSingleValued( at2.isSingleValued() );
        at1.setCollective( at2.isCollective() );
        at1.setUserModifiable( at2.isUserModifiable() );
        at1.setEqualityOid( at2.getEqualityOid() );
        at1.setOrderingOid( at2.getOrderingOid() );
        at1.setSubstringOid( at2.getSubstringOid() );

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
    public void removeAttributeType( AttributeType at )
    {
        Schema schema = getSchema( at.getSchemaName() );

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
    public void addObjectClass( MutableObjectClass oc )
    {
        Schema schema = getSchema( oc.getSchemaName() );

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
    public void modifyObjectClass( MutableObjectClass oc1, ObjectClass oc2 )
    {
        // Removing the references (in case of the names or oid have changed)
        removeSchemaObject( oc1 );

        // Updating the object class
        oc1.setNames( oc2.getNames() );
        oc1.setOid( oc2.getOid() );
        oc1.setDescription( oc2.getDescription() );
        oc1.setSuperiorOids( oc2.getSuperiorOids() );
        oc1.setType( oc2.getType() );
        oc1.setObsolete( oc2.isObsolete() );
        oc1.setMustAttributeTypeOids( oc2.getMustAttributeTypeOids() );
        oc1.setMayAttributeTypeOids( oc2.getMayAttributeTypeOids() );

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
    public void removeObjectClass( ObjectClass oc )
    {
        Schema schema = getSchema( oc.getSchemaName() );

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
        for ( SchemaHandlerListener listener : schemaHandlerListeners.toArray( new SchemaHandlerListener[0] ) )
        {
            listener.schemaAdded( schema );
        }
    }


    /**
     * Notifies the given listeners that a schema has been removed.
     *
     * @param schema
     *      the removed schema
     */
    private void notifySchemaRemoved( Schema schema )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners.toArray( new SchemaHandlerListener[0] ) )
        {
            listener.schemaRemoved( schema );
        }
    }


    /**
     * Notifies the given listeners that a schema has been renamed.
     *
     * @param schema
     *      the renamed schema
     */
    private void notifySchemaRenamed( Schema schema )
    {
        for ( SchemaHandlerListener listener : schemaHandlerListeners.toArray( new SchemaHandlerListener[0] ) )
        {
            listener.schemaRenamed( schema );
        }
    }


    /**
     * Notifies the SchemaHandler listeners that an attribute type has been added.
     *
     * @param at
     *      the added attribute type
     */
    private void notifyAttributeTypeAdded( AttributeType at )
    {
        // SchemaHandler Listeners
        for ( SchemaHandlerListener listener : schemaHandlerListeners.toArray( new SchemaHandlerListener[0] ) )
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
    private void notifyAttributeTypeModified( AttributeType at )
    {
        // SchemaHandler Listeners
        for ( SchemaHandlerListener listener : schemaHandlerListeners.toArray( new SchemaHandlerListener[0] ) )
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
    private void notifyAttributeTypeRemoved( AttributeType at )
    {
        // SchemaHandler Listeners
        for ( SchemaHandlerListener listener : schemaHandlerListeners.toArray( new SchemaHandlerListener[0] ) )
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
    private void notifyObjectClassAdded( ObjectClass oc )
    {
        // SchemaHandler Listeners
        for ( SchemaHandlerListener listener : schemaHandlerListeners.toArray( new SchemaHandlerListener[0] ) )
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
    private void notifyObjectClassModified( ObjectClass oc )
    {
        // SchemaHandler Listeners
        for ( SchemaHandlerListener listener : schemaHandlerListeners.toArray( new SchemaHandlerListener[0] ) )
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
    private void notifyObjectClassRemoved( ObjectClass oc )
    {
        // SchemaHandler Listeners
        for ( SchemaHandlerListener listener : schemaHandlerListeners.toArray( new SchemaHandlerListener[0] ) )
        {
            listener.objectClassRemoved( oc );
        }
    }


    /**
     * Verifies if the given oid is already taken by a schema object.
     *
     * @param oid the oid
     * @return <code>true</code> if the the oid is already taken
     */
    public boolean isOidAlreadyTaken( String oid )
    {
        String lowerCasedOid = Strings.toLowerCase( oid );
        if ( attributeTypesMap.containsKey( lowerCasedOid ) )
        {
            return true;
        }
        else if ( objectClassesMap.containsKey( lowerCasedOid ) )
        {
            return true;
        }
        else if ( matchingRulesMap.containsKey( lowerCasedOid ) )
        {
            return true;
        }
        else if ( syntaxesMap.containsKey( lowerCasedOid ) )
        {
            return true;
        }

        return false;
    }


    /**
     * Verifies if the given alias is already taken by an attribute type.
     *
     * @param alias the alias
     * @return <code>true</code> if the the alias is already taken
     */
    public boolean isAliasAlreadyTakenForAttributeType( String alias )
    {
        return attributeTypesMap.containsKey( Strings.toLowerCase( alias ) );
    }


    /**
     * Verifies if the given alias is already taken by an object class.
     *
     * @param alias the alias
     * @return <code>true</code> if the the alias is already taken
     */
    public boolean isAliasAlreadyTakenForObjectClass( String alias )
    {
        return objectClassesMap.containsKey( Strings.toLowerCase( alias ) );
    }


    /**
     * Verifies if the given name for a schema is already taken by another schema.
     *
     * @param name
     *      the name
     * @return
     *      true if the the name is already taken
     */
    public boolean isSchemaNameAlreadyTaken( String name )
    {
        return schemasMap.containsKey( Strings.toLowerCase( name ) );
    }
}
