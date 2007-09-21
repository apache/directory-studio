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

    private RootObject root;


    /**
     * Creates a new instance of HierarchyManager.
     */
    public HierarchyManager()
    {
        // Initializing the maps
        parentsMap = new MultiValueMap();
        childrenMap = new MultiValueMap();

        schemaHandler = Activator.getDefault().getSchemaHandler();

        loadSchema();
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


    private void addAttributeType( AttributeTypeImpl at )
    {
        checkAliasesAndOID( at );

        String superiorName = at.getSuperiorName();
        if ( superiorName != null )
        {
            AttributeTypeImpl superior = schemaHandler.getAttributeType( superiorName );
            if ( superior == null )
            {
                parentsMap.put( at, superiorName.toLowerCase() );
                childrenMap.put( superiorName.toLowerCase(), at );
                childrenMap.put( root, at );
            }
            else
            {
                parentsMap.put( at, superior );
                childrenMap.put( superior, at );
            }
        }
        else
        {
            parentsMap.put( at, root );
            childrenMap.put( root, at );
        }
    }


    private void addObjectClass( ObjectClassImpl oc )
    {
        checkAliasesAndOID( oc );

        String[] superClasseNames = oc.getSuperClassesNames();
        if ( ( superClasseNames != null ) && ( superClasseNames.length > 0 ) )
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
        {
            ObjectClassImpl topOC = schemaHandler.getObjectClass( "2.5.6.0" );

            if ( oc.equals( topOC ) )
            {
                parentsMap.put( oc, root );
                childrenMap.put( root, oc );
            }
            else
            {
                if ( topOC != null )
                {
                    parentsMap.put( oc, topOC );
                    childrenMap.put( topOC, oc );
                }
                else
                {
                    parentsMap.put( oc, "2.5.6.0" );
                    childrenMap.put( "2.5.6.0", oc );
                }
            }
        }
    }


    private void checkAliasesAndOID( SchemaObject object )
    {
        // Aliases
        String[] aliases = object.getNames();
        if ( aliases != null )
        {
            for ( String alias : aliases )
            {
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
            @SuppressWarnings("unchecked")
            List<Object> children = ( List<Object> ) childrenMap.get( oid.toLowerCase() );
            if ( children != null )
            {
                for ( Object value : children )
                {
                    childrenMap.remove( oid.toLowerCase(), value );
                    childrenMap.put( object, value );
                    parentsMap.remove( value, object );
                    parentsMap.put( value, object );
                }
            }
        }
    }


    public RootObject getRootObject()
    {
        return root;
    }


    @SuppressWarnings("unchecked")
    public List<Object> getChildren( Object o )
    {
        return ( List<Object> ) childrenMap.get( o );
    }


    @SuppressWarnings("unchecked")
    public List<Object> getParents( Object o )
    {
        return ( List<Object> ) parentsMap.get( o );
    }


    public void attributeTypeAdded( AttributeTypeImpl at )
    {
        addAttributeType( at );
    }


    public void attributeTypeModified( AttributeTypeImpl at )
    {
    }


    public void attributeTypeRemoved( AttributeTypeImpl at )
    {
        removeAttributeType( at );
    }


    private void removeAttributeType( AttributeTypeImpl at )
    {
        String superiorName = at.getSuperiorName();
        if ( ( superiorName != null ) && ( !"".equals( superiorName ) ) )
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
                if ( ( childSuperiorName != null ) && ( !"".equals( childSuperiorName ) ) )
                {
                    parentsMap.put( child, childSuperiorName.toLowerCase() );
                    childrenMap.put( childSuperiorName.toLowerCase(), child );
                }
            }

            childrenMap.remove( at );
            parentsMap.remove( at );
        }
    }


    public void objectClassAdded( ObjectClassImpl oc )
    {
    }


    public void objectClassModified( ObjectClassImpl oc )
    {
    }


    public void objectClassRemoved( ObjectClassImpl oc )
    {
    }


    public void schemaAdded( Schema schema )
    {
    }


    public void schemaRemoved( Schema schema )
    {
    }

}
