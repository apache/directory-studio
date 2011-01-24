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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.eclipse.osgi.util.NLS;


/**
 * This class represents the DependenciesComputer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DependenciesComputer
{
    /** The schemas List */
    private List<Schema> schemasList;

    /** The dependency ordered schemas List */
    private List<Schema> dependencyOrderedSchemasList;

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    // The dependencies MultiMaps
    private MultiMap schemasDependencies;
    private MultiMap attributeTypesDependencies;
    private MultiMap objectClassesDependencies;


    /**
     * Creates a new instance of DependenciesComputer.
     *
     * @param schemasList
     *      the schemasList
     * @throws DependencyComputerException 
     */
    public DependenciesComputer( List<Schema> schemas ) throws DependencyComputerException
    {
        this.schemasList = schemas;

        // Creating the SchemaHandler
        schemaHandler = new SchemaHandler();

        // Creating the dependencies MultiMaps
        schemasDependencies = new MultiValueMap();
        attributeTypesDependencies = new MultiValueMap();
        objectClassesDependencies = new MultiValueMap();

        if ( schemas != null )
        {
            // Adding the schemasList in the SchemaHandler
            for ( Schema schema : this.schemasList )
            {
                schemaHandler.addSchema( schema );
            }

            // Computing dependencies
            for ( Schema schema : this.schemasList )
            {
                List<AttributeTypeImpl> attributeTypes = schema.getAttributeTypes();
                if ( attributeTypes != null )
                {
                    for ( AttributeTypeImpl attributeType : attributeTypes )
                    {
                        computeDependencies( schema, attributeType );
                    }
                }

                List<ObjectClassImpl> objectClasses = schema.getObjectClasses();
                if ( objectClasses != null )
                {
                    for ( ObjectClassImpl objectClass : objectClasses )
                    {
                        computeDependencies( schema, objectClass );
                    }
                }
            }

            // Ordering the schemas
            orderSchemasBasedOnDependencies();
        }
    }


    /**
     * Computes the dependencies for the given attribute type.
     *
     * @param schema
     *      the schema
     * @param attributeType
     *      the attribute type
     * @throws DependencyComputerException 
     */
    private void computeDependencies( Schema schema, AttributeTypeImpl attributeType )
        throws DependencyComputerException
    {
        // Superior
        String superior = attributeType.getSuperiorOid();
        if ( superior != null )
        {
            AttributeTypeImpl superiorAT = schemaHandler.getAttributeType( superior );
            if ( superiorAT == null )
            {
                throw new DependencyComputerException( NLS.bind( Messages
                    .getString( "DependenciesComputer.SuperiorAttribute" ), new String[] { superior } ) ); //$NON-NLS-1$
            }
            else
            {
                // Adding a dependency on the superior attribute type
                attributeTypesDependencies.put( attributeType, superiorAT );

                // Computing the schema dependency
                computeSchemaDependency( schema, superiorAT );
            }
        }

        // Syntax OID
        String syntaxOID = attributeType.getSyntaxOid();
        if ( syntaxOID != null )
        {
            SyntaxImpl syntax = schemaHandler.getSyntax( syntaxOID );
            if ( syntax == null )
            {
                throw new DependencyComputerException( NLS.bind(
                    Messages.getString( "DependenciesComputer.SyntaxOID" ), new String[] { syntaxOID } ) ); //$NON-NLS-1$
            }
            else
            {
                // Adding a dependency on the syntax
                attributeTypesDependencies.put( attributeType, syntax );

                // Computing the schema dependency
                computeSchemaDependency( schema, syntax );
            }
        }

        // Equality Matching Rule
        String equalityName = attributeType.getEqualityOid();
        if ( equalityName != null )
        {
            MatchingRuleImpl equalityMatchingRule = schemaHandler.getMatchingRule( equalityName );
            if ( equalityMatchingRule == null )
            {
                throw new DependencyComputerException( NLS.bind(
                    Messages.getString( "DependenciesComputer.Equality" ), new String[] { equalityName } ) ); //$NON-NLS-1$
            }
            else
            {
                // Adding a dependency on the syntax
                attributeTypesDependencies.put( attributeType, equalityMatchingRule );

                // Computing the schema dependency
                computeSchemaDependency( schema, equalityMatchingRule );
            }
        }

        // Ordering Matching Rule
        String orderingName = attributeType.getOrderingOid();
        if ( orderingName != null )
        {
            MatchingRuleImpl orderingMatchingRule = schemaHandler.getMatchingRule( orderingName );
            if ( orderingMatchingRule == null )
            {
                throw new DependencyComputerException( NLS.bind(
                    Messages.getString( "DependenciesComputer.Ordering" ), new String[] { orderingName } ) ); //$NON-NLS-1$
            }
            else
            {
                // Adding a dependency on the syntax
                attributeTypesDependencies.put( attributeType, orderingMatchingRule );

                // Computing the schema dependency
                computeSchemaDependency( schema, orderingMatchingRule );
            }
        }

        // Substring Matching Rule
        String substringName = attributeType.getSubstringOid();
        if ( substringName != null )
        {
            MatchingRuleImpl substringMatchingRule = schemaHandler.getMatchingRule( substringName );
            if ( substringMatchingRule == null )
            {
                throw new DependencyComputerException( NLS.bind(
                    Messages.getString( "DependenciesComputer.Substring" ), new String[] { substringName } ) ); //$NON-NLS-1$
            }
            else
            {
                // Adding a dependency on the syntax
                attributeTypesDependencies.put( attributeType, substringMatchingRule );

                // Computing the schema dependency
                computeSchemaDependency( schema, substringMatchingRule );
            }
        }
    }


    /**
     * Computes the dependencies for the given object Class.
     *
     * @param schema
     *      the schema
     * @param objectClass
     *      the object class
     * @throws DependencyComputerException 
     */
    private void computeDependencies( Schema schema, ObjectClassImpl objectClass ) throws DependencyComputerException
    {
        // Super Classes
        List<String> superClassesNames = objectClass.getSuperiorOids();
        if ( superClassesNames != null )
        {
            for ( String superClassName : superClassesNames )
            {
                ObjectClassImpl superObjectClass = schemaHandler.getObjectClass( superClassName );
                if ( superObjectClass == null )
                {
                    throw new DependencyComputerException( NLS.bind( Messages
                        .getString( "DependenciesComputer.SuperiorObject" ), new String[] { superClassName } ) ); //$NON-NLS-1$
                }
                else
                {
                    // Adding a dependency on the syntax
                    objectClassesDependencies.put( objectClass, superObjectClass );

                    // Computing the schema dependency
                    computeSchemaDependency( schema, superObjectClass );
                }
            }
        }

        // Optional attribute types
        List<String> optionalAttributeTypes = objectClass.getMayAttributeTypeOids();
        if ( optionalAttributeTypes != null )
        {
            for ( String optionalAttributeTypeName : optionalAttributeTypes )
            {
                AttributeTypeImpl optionalAttributeType = schemaHandler.getAttributeType( optionalAttributeTypeName );
                if ( optionalAttributeType == null )
                {
                    throw new DependencyComputerException( NLS.bind( Messages
                        .getString( "DependenciesComputer.Optional" ), new Object[] { optionalAttributeType } ) ); //$NON-NLS-1$
                }
                else
                {
                    // Adding a dependency on the syntax
                    objectClassesDependencies.put( objectClass, optionalAttributeType );

                    // Computing the schema dependency
                    computeSchemaDependency( schema, optionalAttributeType );
                }
            }
        }

        // Mandatory attribute types
        List<String> mandatoryAttributeTypes = objectClass.getMustAttributeTypeOids();
        if ( mandatoryAttributeTypes != null )
        {
            for ( String mandatoryAttributeTypeName : mandatoryAttributeTypes )
            {
                AttributeTypeImpl mandatoryAttributeType = schemaHandler.getAttributeType( mandatoryAttributeTypeName );
                if ( mandatoryAttributeType == null )
                {
                    throw new DependencyComputerException( NLS.bind( Messages
                        .getString( "DependenciesComputer.Mandatory" ), new String[] //$NON-NLS-1$
                        { mandatoryAttributeTypeName } ) ); //$NON-NLS-1$
                }
                else
                {
                    // Adding a dependency on the syntax
                    objectClassesDependencies.put( objectClass, mandatoryAttributeType );

                    // Computing the schema dependency
                    computeSchemaDependency( schema, mandatoryAttributeType );
                }
            }
        }
    }


    /**
     * Computes the Schema Dependency.
     *
     * @param schema
     *      the schema
     * @param object
     *      the SchemaObject
     * @throws DependencyComputerException 
     */
    private void computeSchemaDependency( Schema schema, SchemaObject object ) throws DependencyComputerException
    {
        String schemaName = object.getSchemaName();
        if ( !schemaName.equalsIgnoreCase( schema.getName() ) )
        {
            Schema schemaFromSuperiorAT = schemaHandler.getSchema( schemaName );
            if ( schemaFromSuperiorAT == null )
            {
                throw new DependencyComputerException( NLS.bind(
                    Messages.getString( "DependenciesComputer.Schema" ), new String[] { schemaName } ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                // Adding a dependency on the schema of schema object
                schemasDependencies.put( schema, schemaFromSuperiorAT );
            }
        }
    }


    /**
     * Orders the schemas based on their dependencies.
     */
    private void orderSchemasBasedOnDependencies()
    {
        dependencyOrderedSchemasList = new ArrayList<Schema>();

        int counter = 0;
        schemasLoop: while ( dependencyOrderedSchemasList.size() != schemasList.size() )
        {
            Schema schema = schemasList.get( counter );

            if ( !dependencyOrderedSchemasList.contains( schema ) )
            {

                List<Schema> dependencies = getDependencies( schema );
                if ( dependencies == null )
                {
                    dependencyOrderedSchemasList.add( schema );
                }
                else
                {
                    for ( Schema dependency : dependencies )
                    {
                        if ( !dependencyOrderedSchemasList.contains( dependency ) )
                        {
                            counter = ++counter % schemasList.size();

                            continue schemasLoop;
                        }
                    }

                    dependencyOrderedSchemasList.add( schema );
                }
            }

            counter = ++counter % schemasList.size();
        }

    }


    /**
     * Gets the dependencies of the given schema.
     *
     * @param schema
     *      the schema
     * @return
     *      the dependencies of the schema
     */
    @SuppressWarnings("unchecked")
    public List<Schema> getDependencies( Schema schema )
    {
        List<Schema> dependencies = ( List<Schema> ) schemasDependencies.get( schema );

        HashSet<Schema> set = new HashSet<Schema>();

        if ( dependencies != null )
        {
            set.addAll( dependencies );
        }

        return Arrays.asList( set.toArray( new Schema[0] ) );
    }


    /**
     * Gets the List of the schemas ordered according to their
     * dependencies.
     *
     * @return
     *      the List of the schemas ordered according to their
     * dependencies
     */
    public List<Schema> getDependencyOrderedSchemasList()
    {
        return dependencyOrderedSchemasList;
    }


    /**
     * Gets the schemasList dependencies MultiMap.
     *
     * @return
     *      the schemasList dependencies MultiMap
     */
    public MultiMap getSchemasDependencies()
    {
        return schemasDependencies;
    }


    /**
     * Get the attribute types dependencies MultiMap.
     *
     * @return
     *      the attribute types dependencies MultiMap
     */
    public MultiMap getAttributeTypesDependencies()
    {
        return attributeTypesDependencies;
    }


    /**
     * Gets the object classes dependencies MultiMap.
     *
     * @return
     *      the object classes dependencies MultiMap
     */
    public MultiMap getObjectClassesDependencies()
    {
        return objectClassesDependencies;
    }

    /**
     * This class represents the DependencyComputerException.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public class DependencyComputerException extends Exception
    {
        private static final long serialVersionUID = 1L;


        /**
         * Creates a new instance of DependencyComputerException.
         *
         * @param message
         *      the message
         */
        public DependencyComputerException( String message )
        {
            super( message );
        }
    }
}
