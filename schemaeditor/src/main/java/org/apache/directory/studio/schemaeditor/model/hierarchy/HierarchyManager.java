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


import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * This class represents the HierarchyManager.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
    private void addAttributeType( AttributeTypeImpl at )
    {
        // Checking Aliases and OID
        checkAliasesAndOID( at );

        String superiorName = at.getSuperiorName();
        if ( superiorName != null )
        // The attribute type has a superior
        {
            AttributeTypeImpl superior = schemaHandler.getAttributeType( superiorName );
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
                parentsMap.put( at, superiorName.toLowerCase() );
                childrenMap.put( superiorName.toLowerCase(), at );
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
    private void addObjectClass( ObjectClassImpl oc )
    {
        // Checking Aliases and OID
        checkAliasesAndOID( oc );

        String[] superClasseNames = oc.getSuperClassesNames();
        if ( ( superClasseNames != null ) && ( superClasseNames.length > 0 ) )
        // The object class has one or more superiors
        {
            for ( String superClassName : superClasseNames )
            {
                ObjectClassImpl superClass = schemaHandler.getObjectClass( superClassName );
                if ( superClass == null )
                {
                    parentsMap.put( oc, superClassName.toLowerCase() );
                    childrenMap.put( superClassName.toLowerCase(), oc );
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
            ObjectClassImpl topOC = schemaHandler.getObjectClass( "2.5.6.0" );
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
                    parentsMap.put( oc, "2.5.6.0" );
                    childrenMap.put( "2.5.6.0", oc );
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
    public void attributeTypeAdded( AttributeTypeImpl at )
    {
        addAttributeType( at );
    }


    /**
     * This method is called when an attribute type is modified.
     *
     * @param at
     *      the modified attribute type
     */
    public void attributeTypeModified( AttributeTypeImpl at )
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
    public void attributeTypeRemoved( AttributeTypeImpl at )
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
        String[] aliases = object.getNamesRef();
        if ( aliases != null )
        {
            for ( String alias : aliases )
            {
                // Looking for children objects for this alias value
                @SuppressWarnings("unchecked")
                List<Object> children = ( List<Object> ) childrenMap.get( alias.toLowerCase() );
                if ( children != null )
                {
                    for ( Object value : children )
                    {
                        childrenMap.put( object, value );
                        parentsMap.remove( value, alias.toLowerCase() );
                        parentsMap.put( value, object );
                    }
                    childrenMap.remove( alias.toLowerCase() );
                }
            }
        }

        // OID
        String oid = object.getOid();
        if ( oid != null )
        {
            // Looking for children objects for this OID value
            @SuppressWarnings("unchecked")
            List<Object> children = ( List<Object> ) childrenMap.get( oid.toLowerCase() );
            if ( children != null )
            {
                for ( Object value : children )
                {
                    childrenMap.put( object, value );
                    if ( oid.equals( "2.5.6.0" ) )
                    {
                        childrenMap.remove( root, value );
                    }
                    parentsMap.remove( value, oid.toLowerCase() );
                    parentsMap.put( value, object );

                }
                childrenMap.remove( oid.toLowerCase() );
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
                for ( AttributeTypeImpl at : schema.getAttributeTypes() )
                {
                    addAttributeType( at );
                }

                // Looping on the object classes
                for ( ObjectClassImpl oc : schema.getObjectClasses() )
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
    public void objectClassAdded( ObjectClassImpl oc )
    {
        addObjectClass( oc );
    }


    /**
     * This method is called when an object class is modified.
     *
     * @param oc
     *      the modified object class
     */
    public void objectClassModified( ObjectClassImpl oc )
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
    public void objectClassRemoved( ObjectClassImpl oc )
    {
        removeObjectClass( oc );
    }


    /**
     * Removes an attribute type.
     *
     * @param at
     *      the attribute type
     */
    private void removeAttributeType( AttributeTypeImpl at )
    {
        // Removing the attribute type as child of its superior
        String superiorName = at.getSuperiorName();
        if ( ( superiorName != null ) && ( !"".equals( superiorName ) ) ) //$NON-NLS-1$
        {
            AttributeTypeImpl superiorAT = schemaHandler.getAttributeType( superiorName );
            if ( superiorAT == null )
            {
                childrenMap.remove( superiorName.toLowerCase(), at );
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
                AttributeTypeImpl childAT = ( AttributeTypeImpl ) child;

                parentsMap.remove( child, at );

                parentsMap.put( child, root );
                childrenMap.put( root, child );
                String childSuperiorName = childAT.getSuperiorName();
                if ( ( childSuperiorName != null ) && ( !"".equals( childSuperiorName ) ) ) //$NON-NLS-1$
                {
                    parentsMap.put( child, childSuperiorName.toLowerCase() );
                    childrenMap.put( childSuperiorName.toLowerCase(), child );
                }
            }
        }

        childrenMap.remove( at );
        parentsMap.remove( at );
    }


    private void removeObjectClass( ObjectClassImpl oc )
    {
        // Removing the object class as child of its superiors
        String[] superClassesNames = oc.getSuperClassesNames();
        if ( ( superClassesNames != null ) && ( superClassesNames.length > 0 ) )
        {
            for ( String superClassName : superClassesNames )
            {
                if ( !"".equals( superClassName ) ) //$NON-NLS-1$
                {
                    ObjectClassImpl superClassOC = schemaHandler.getObjectClass( superClassName );
                    if ( superClassOC == null )
                    {
                        childrenMap.remove( superClassName.toLowerCase(), oc );
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
            if ( oc.getOid().equals( "2.5.6.0" ) )
            // The given object class is the "top (2.5.6.0)" object class
            {
                childrenMap.remove( root, oc );
            }
            else
            {
                ObjectClassImpl topOC = schemaHandler.getObjectClass( "2.5.6.0" );
                if ( topOC != null )
                // The "top (2.5.6.0)" object class exists
                {
                    childrenMap.remove( topOC, oc );
                }
                else
                // The "top (2.5.6.0)" object class does not exist
                {
                    childrenMap.remove( "2.5.6.0", oc );
                }
            }
        }

        // Attaching each child (if there are children) to the RootObject
        List<Object> children = getChildren( oc );
        if ( children != null )
        {
            for ( Object child : children )
            {
                ObjectClassImpl childOC = ( ObjectClassImpl ) child;

                parentsMap.remove( child, oc );

                parentsMap.put( child, root );
                childrenMap.put( root, child );
                String[] childSuperClassesNames = childOC.getSuperClassesNames();
                if ( ( childSuperClassesNames != null ) && ( childSuperClassesNames.length > 0 ) )
                {
                    String correctSuperClassName = getCorrectSuperClassName( oc, childSuperClassesNames );
                    if ( correctSuperClassName != null )
                    {
                        parentsMap.put( child, correctSuperClassName.toLowerCase() );
                        childrenMap.put( correctSuperClassName.toLowerCase(), child );
                    }
                }
                else
                {
                    parentsMap.put( child, "2.5.6.0" );
                    childrenMap.put( "2.5.6.0", child );
                }
            }
        }

        childrenMap.remove( oc );
        parentsMap.remove( oc );
    }


    private String getCorrectSuperClassName( ObjectClassImpl oc, String[] childSuperClassesNames )
    {
        if ( childSuperClassesNames != null )
        {
            List<String> aliases = Arrays.asList( oc.getNamesRef() );
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
