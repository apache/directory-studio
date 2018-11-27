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
package org.apache.directory.studio.schemaeditor.model.hierarchy;


import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.SchemaObject;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * This class represents the HierarchyManager.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class HierarchyManager
{
    /** The parents map is used to store for each element its parents */
    private MultiValueMap parentsMap;

    /** The parents map is used to store for each element its children */
    private MultiValueMap childrenMap;

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The RootObject of the Hierarchy */
    private RootObject root;


    /**
     * Creates a new instance of HierarchyManager.
     */
    public HierarchyManager()
    {
        // Initializing the maps
        parentsMap = new MultiValueMap();
        childrenMap = new MultiValueMap();

        // Getting the SchemaHandler
        schemaHandler = Activator.getDefault().getSchemaHandler();

        // Loading the complete Schema
        loadSchema();
    }


    /**
     * Adds an attribute type.
     *
     * @param at
     *      the attribute type
     */
    private void addAttributeType( AttributeType at )
    {
        // Checking Aliases and OID
        checkAliasesAndOID( at );

        String superiorName = at.getSuperiorOid();
        if ( superiorName != null )
        // The attribute type has a superior
        {
            AttributeType superior = schemaHandler.getAttributeType( superiorName );
            if ( superior != null )
            // The superior attribute type object exists
            {
                parentsMap.put( at, superior );
                childrenMap.put( superior, at );
            }
            else
            // The superior attribute type object does not exist
            {
                // Then, its parent is the name of its superior and
                // it becomes the children of it and the RootObject
                parentsMap.put( at, Strings.toLowerCase( superiorName ) );
                childrenMap.put( Strings.toLowerCase( superiorName ), at );
                childrenMap.put( root, at );
            }
        }
        else
        // The attribute type does not have a superior
        {
            // Then, its parent is the RootObject
            parentsMap.put( at, root );
            childrenMap.put( root, at );
        }
    }


    /**
     * Adds an object class.
     *
     * @param oc
     *      the object class
     */
    private void addObjectClass( ObjectClass oc )
    {
        // Checking Aliases and OID
        checkAliasesAndOID( oc );

        List<String> superClasseNames = oc.getSuperiorOids();
        if ( ( superClasseNames != null ) && ( superClasseNames.size() > 0 ) )
        // The object class has one or more superiors
        {
            for ( String superClassName : superClasseNames )
            {
                ObjectClass superClass = schemaHandler.getObjectClass( superClassName );
                if ( superClass == null )
                {
                    parentsMap.put( oc, Strings.toLowerCase( superClassName ) );
                    childrenMap.put( Strings.toLowerCase( superClassName ), oc );
                    childrenMap.put( root, oc );
                }
                else
                {
                    parentsMap.put( oc, superClass );
                    childrenMap.put( superClass, oc );
                }
            }
        }
        else
        // The object class does not have any declared superior
        // Then, it is a child of the "top (2.5.6.0)" object class
        // (Unless it is the "top (2.5.6.0)" object class itself)
        {
            ObjectClass topOC = schemaHandler.getObjectClass( "2.5.6.0" ); //$NON-NLS-1$
            if ( oc.equals( topOC ) )
            // The given object class is the "top (2.5.6.0)" object class
            {
                parentsMap.put( oc, root );
                childrenMap.put( root, oc );
            }
            else
            {
                if ( topOC != null )
                // The "top (2.5.6.0)" object class exists
                {
                    parentsMap.put( oc, topOC );
                    childrenMap.put( topOC, oc );
                }
                else
                // The "top (2.5.6.0)" object class does not exist
                {
                    parentsMap.put( oc, "2.5.6.0" ); //$NON-NLS-1$
                    childrenMap.put( "2.5.6.0", oc ); //$NON-NLS-1$
                    childrenMap.put( root, oc );
                }
            }
        }
    }


    /**
     * This method is called when an attribute type is added.
     *
     * @param at
     *      the added attribute type
     */
    public void attributeTypeAdded( AttributeType at )
    {
        addAttributeType( at );
    }


    /**
     * This method is called when an attribute type is modified.
     *
     * @param at
     *      the modified attribute type
     */
    public void attributeTypeModified( AttributeType at )
    {
        // Removing the attribute type
        List<Object> parents = getParents( at );
        if ( parents != null )
        {
            for ( Object parent : parents )
            {
                childrenMap.remove( parent, at );
            }

            parentsMap.remove( at );
        }

        // Adding the attribute type again
        addAttributeType( at );
    }


    /**
     * This method is called when an attribute type is removed.
     *
     * @param at
     *      the removed attribute type
     */
    public void attributeTypeRemoved( AttributeType at )
    {
        removeAttributeType( at );
    }


    /**
     * Checks the Aliases and OID of an attribute type or an object class.
     *
     * @param object
     *      an attribute type or an object class.
     */
    private void checkAliasesAndOID( SchemaObject object )
    {
        // Aliases
        List<String> aliases = object.getNames();
        if ( aliases != null )
        {
            for ( String alias : aliases )
            {
                // Looking for children objects for this alias value
                @SuppressWarnings("unchecked")
                List<Object> children = ( List<Object> ) childrenMap.get( Strings.toLowerCase( alias ) );
                if ( children != null )
                {
                    for ( Object value : children )
                    {
                        childrenMap.put( object, value );
                        parentsMap.remove( value, Strings.toLowerCase( alias ) );
                        parentsMap.put( value, object );
                    }
                    childrenMap.remove( Strings.toLowerCase( alias ) );
                }
            }
        }

        // OID
        String oid = object.getOid();
        if ( oid != null )
        {
            // Looking for children objects for this OID value
            @SuppressWarnings("unchecked")
            List<Object> children = ( List<Object> ) childrenMap.get( Strings.toLowerCase( oid ) );
            if ( children != null )
            {
                for ( Object value : children )
                {
                    childrenMap.put( object, value );
                    if ( oid.equals( "2.5.6.0" ) ) //$NON-NLS-1$
                    {
                        childrenMap.remove( root, value );
                    }
                    parentsMap.remove( value, Strings.toLowerCase( oid ) );
                    parentsMap.put( value, object );

                }
                childrenMap.remove( Strings.toLowerCase( oid ) );
            }
        }
    }


    /**
     * Gets the children of the given object.
     *
     * @param o
     *      the object
     * @return
     *      the children of the given object
     */
    @SuppressWarnings("unchecked")
    public List<Object> getChildren( Object o )
    {
        return ( List<Object> ) childrenMap.get( o );
    }


    /**
     * Gets the parents of the given object.
     *
     * @param o
     *      the object
     * @return
     *      the parents of the given object
     */
    @SuppressWarnings("unchecked")
    public List<Object> getParents( Object o )
    {
        return ( List<Object> ) parentsMap.get( o );
    }


    /**
     * Gets the RootObject of the Hierarchy.
     *
     * @return
     *      the RootObject of the Hierarchy
     */
    public RootObject getRootObject()
    {
        return root;
    }


    /**
     * Loads the Schema.
     */
    private void loadSchema()
    {
        if ( schemaHandler != null )
        {
            // Creating the root element
            root = new RootObject();

            // Looping on the schemas
            for ( Schema schema : schemaHandler.getSchemas() )
            {
                // Looping on the attribute types
                for ( AttributeType at : schema.getAttributeTypes() )
                {
                    addAttributeType( at );
                }

                // Looping on the object classes
                for ( ObjectClass oc : schema.getObjectClasses() )
                {
                    addObjectClass( oc );
                }
            }
        }
    }


    /**
     * This method is called when an object class is added.
     *
     * @param oc
     *      the added object class
     */
    public void objectClassAdded( ObjectClass oc )
    {
        addObjectClass( oc );
    }


    /**
     * This method is called when an object class is modified.
     *
     * @param oc
     *      the modified object class
     */
    public void objectClassModified( ObjectClass oc )
    {
        // Removing the object class type
        List<Object> parents = getParents( oc );
        if ( parents != null )
        {
            for ( Object parent : parents )
            {
                childrenMap.remove( parent, oc );
            }

            parentsMap.remove( oc );
        }

        // Adding the object class again
        addObjectClass( oc );
    }


    /**
     * This method is called when an object class is removed.
     *
     * @param oc
     *      the removed object class
     */
    public void objectClassRemoved( ObjectClass oc )
    {
        removeObjectClass( oc );
    }


    /**
     * Removes an attribute type.
     *
     * @param at
     *      the attribute type
     */
    private void removeAttributeType( AttributeType at )
    {
        // Removing the attribute type as child of its superior
        String superiorName = at.getSuperiorOid();
        if ( ( superiorName != null ) && ( !"".equals( superiorName ) ) ) //$NON-NLS-1$
        {
            AttributeType superiorAT = schemaHandler.getAttributeType( superiorName );
            if ( superiorAT == null )
            {
                childrenMap.remove( Strings.toLowerCase( superiorName ), at );
            }
            else
            {
                childrenMap.remove( superiorAT, at );
            }
        }
        else
        {
            childrenMap.remove( root, at );
        }

        // Attaching each child (if there are children) to the RootObject
        List<Object> children = getChildren( at );
        if ( children != null )
        {
            for ( Object child : children )
            {
                AttributeType childAT = ( AttributeType ) child;

                parentsMap.remove( child, at );

                parentsMap.put( child, root );
                childrenMap.put( root, child );
                String childSuperiorName = childAT.getSuperiorOid();
                if ( ( childSuperiorName != null ) && ( !"".equals( childSuperiorName ) ) ) //$NON-NLS-1$
                {
                    parentsMap.put( child, Strings.toLowerCase( childSuperiorName ) );
                    childrenMap.put( Strings.toLowerCase( childSuperiorName ), child );
                }
            }
        }

        childrenMap.remove( at );
        parentsMap.remove( at );
    }


    private void removeObjectClass( ObjectClass oc )
    {
        // Removing the object class as child of its superiors
        List<String> superClassesNames = oc.getSuperiorOids();
        if ( ( superClassesNames != null ) && ( superClassesNames.size() > 0 ) )
        {
            for ( String superClassName : superClassesNames )
            {
                if ( !"".equals( superClassName ) ) //$NON-NLS-1$
                {
                    ObjectClass superClassOC = schemaHandler.getObjectClass( superClassName );
                    if ( superClassOC == null )
                    {
                        childrenMap.remove( Strings.toLowerCase( superClassName ), oc );
                        childrenMap.remove( root, oc );
                    }
                    else
                    {
                        childrenMap.remove( superClassOC, oc );
                    }
                }
            }
        }
        else
        {
            if ( oc.getOid().equals( "2.5.6.0" ) ) //$NON-NLS-1$
            // The given object class is the "top (2.5.6.0)" object class
            {
                childrenMap.remove( root, oc );
            }
            else
            {
                ObjectClass topOC = schemaHandler.getObjectClass( "2.5.6.0" ); //$NON-NLS-1$
                if ( topOC != null )
                // The "top (2.5.6.0)" object class exists
                {
                    childrenMap.remove( topOC, oc );
                }
                else
                // The "top (2.5.6.0)" object class does not exist
                {
                    childrenMap.remove( "2.5.6.0", oc ); //$NON-NLS-1$
                }
            }
        }

        // Attaching each child (if there are children) to the RootObject
        List<Object> children = getChildren( oc );
        if ( children != null )
        {
            for ( Object child : children )
            {
                ObjectClass childOC = ( ObjectClass ) child;

                parentsMap.remove( child, oc );

                parentsMap.put( child, root );
                childrenMap.put( root, child );
                List<String> childSuperClassesNames = childOC.getSuperiorOids();
                if ( ( childSuperClassesNames != null ) && ( childSuperClassesNames.size() > 0 ) )
                {
                    String correctSuperClassName = getCorrectSuperClassName( oc, childSuperClassesNames );
                    if ( correctSuperClassName != null )
                    {
                        parentsMap.put( child, Strings.toLowerCase( correctSuperClassName ) );
                        childrenMap.put( Strings.toLowerCase( correctSuperClassName ), child );
                    }
                }
                else
                {
                    parentsMap.put( child, "2.5.6.0" ); //$NON-NLS-1$
                    childrenMap.put( "2.5.6.0", child ); //$NON-NLS-1$
                }
            }
        }

        childrenMap.remove( oc );
        parentsMap.remove( oc );
    }


    private String getCorrectSuperClassName( ObjectClass oc, List<String> childSuperClassesNames )
    {
        if ( childSuperClassesNames != null )
        {
            List<String> aliases = oc.getNames();
            if ( aliases != null )
            {
                for ( String childSuperClassName : childSuperClassesNames )
                {
                    if ( aliases.contains( childSuperClassName ) )
                    {
                        return childSuperClassName;
                    }
                }
            }
        }

        // Default
        return null;
    }
}
