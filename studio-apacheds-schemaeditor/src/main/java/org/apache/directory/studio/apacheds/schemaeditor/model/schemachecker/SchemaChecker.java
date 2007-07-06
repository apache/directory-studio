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
package org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.NonExistingMatchingRuleError.NonExistingMatchingRuleErrorEnum;


/**
 * This class represents the SchemaChecker.
 * <p>
 * It is used to check the schema integrity.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaChecker
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The errors List */
    private List<SchemaError> errorsList;

    /** The errors MultiMap */
    private MultiMap errorsMap;

    /** The warnings List */
    private List<SchemaWarning> warningsList;

    /** The warnings MultiMap */
    private MultiMap warningsMap;

    /** The 'listening to modifications' flag*/
    private boolean listeningToModifications = false;

    /** The SchemaHandlerListener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerListener()
    {
        public void attributeTypeAdded( AttributeTypeImpl at )
        {
            // TODO Auto-generated method stub
            System.out.println( "AT Added" );
            checkAttributeType( at );
        }


        public void attributeTypeModified( AttributeTypeImpl at )
        {
            // TODO Auto-generated method stub

        }


        public void attributeTypeRemoved( AttributeTypeImpl at )
        {
            // TODO Auto-generated method stub

        }


        public void matchingRuleAdded( MatchingRuleImpl mr )
        {
            // TODO Auto-generated method stub

        }


        public void matchingRuleModified( MatchingRuleImpl mr )
        {
            // TODO Auto-generated method stub

        }


        public void matchingRuleRemoved( MatchingRuleImpl mr )
        {
            // TODO Auto-generated method stub

        }


        public void objectClassAdded( ObjectClassImpl oc )
        {
            // TODO Auto-generated method stub

        }


        public void objectClassModified( ObjectClassImpl oc )
        {
            // TODO Auto-generated method stub

        }


        public void objectClassRemoved( ObjectClassImpl oc )
        {
            // TODO Auto-generated method stub

        }


        public void schemaAdded( Schema schema )
        {
            // TODO Auto-generated method stub

        }


        public void schemaRemoved( Schema schema )
        {
            // TODO Auto-generated method stub

        }


        public void syntaxAdded( SyntaxImpl syntax )
        {
            // TODO Auto-generated method stub

        }


        public void syntaxModified( SyntaxImpl syntax )
        {
            // TODO Auto-generated method stub

        }


        public void syntaxRemoved( SyntaxImpl syntax )
        {
            // TODO Auto-generated method stub

        }

    };


    /**
     * Creates a new instance of SchemaChecker.
     */
    public SchemaChecker()
    {
        schemaHandler = Activator.getDefault().getSchemaHandler();
        errorsList = new ArrayList<SchemaError>();
        errorsMap = new MultiValueMap();
        warningsList = new ArrayList<SchemaWarning>();
        warningsMap = new MultiValueMap();
    }


    /**
     * Enables modifications listening.
     */
    public void enableModificationsListening()
    {
        if ( !listeningToModifications )
        {
            schemaHandler.addListener( schemaHandlerListener );
            listeningToModifications = true;
            checkWholeSchema();
        }
    }


    /**
     * Disables modifications listening.
     */
    public void disableModificationsListening()
    {
        if ( listeningToModifications )
        {
            schemaHandler.removeListener( schemaHandlerListener );
            listeningToModifications = false;
            clearErrorsAndWarnings();
        }
    }


    /**
     * Returns true if the SchemaChecker is listening to modifications, 
     * false if not.
     *
     * @return
     *      true if the SchemaChecker is listening to modifications, 
     * false if not
     */
    public boolean isListeningToModifications()
    {
        return listeningToModifications;
    }


    /**
     * Clears all the errors and warnings.
     */
    private void clearErrorsAndWarnings()
    {
        errorsList.clear();
        errorsMap.clear();
        warningsList.clear();
        warningsMap.clear();
    }


    /**
     * Checks the whole schema.
     */
    private void checkWholeSchema()
    {
        List<Schema> schemas = schemaHandler.getSchemas();
        for ( Schema schema : schemas )
        {
            List<AttributeTypeImpl> ats = schema.getAttributeTypes();
            for ( AttributeTypeImpl at : ats )
            {
                checkAttributeType( at );
            }

            List<ObjectClassImpl> ocs = schema.getObjectClasses();
            for ( ObjectClassImpl oc : ocs )
            {
                checkObjectClass( oc );
            }
        }
    }


    /**
     * Checks the given attribute type.
     *
     * @param at
     *      an attribute type
     */
    private void checkAttributeType( AttributeTypeImpl at )
    {
        removeSchemaObject( at );

        // Checking OID
        String oid = at.getOid();
        if ( ( oid != null ) && ( !"".equals( oid ) ) )
        {
            List<?> list = getSchemaElementList( oid );
            if ( ( list != null ) && ( list.size() >= 2 ) )
            {
                int counter = 0;
                Object o = list.get( counter );
                while ( ( at.equals( o ) ) && ( counter < ( list.size() - 1 ) ) )
                {
                    counter++;
                    o = list.get( counter );
                }
                SchemaError error = new DuplicateOidError( at, oid, ( SchemaObject ) o );
                errorsList.add( error );
                errorsMap.put( at, error );
            }
        }

        // Checking aliases
        String[] aliases = at.getNames();
        if ( ( aliases != null ) && ( aliases.length >= 1 ) )
        {
            for ( String alias : aliases )
            {
                List<?> list = getSchemaElementList( alias );
                if ( ( list != null ) && ( list.size() >= 2 ) )
                {
                    int counter = 0;
                    Object o = list.get( counter );
                    while ( ( at.equals( o ) ) && ( counter < ( list.size() - 1 ) ) )
                    {
                        counter++;
                        o = list.get( counter );
                    }
                    SchemaError error = new DuplicateAliasError( at, oid, ( SchemaObject ) o );
                    errorsList.add( error );
                    errorsMap.put( at, error );
                }
            }
        }

        // Checking superior
        String superior = at.getSuperiorName();
        if ( ( superior != null ) && ( !"".equals( superior ) ) )
        {
            if ( schemaHandler.getAttributeType( superior ) == null )
            {
                SchemaError error = new NonExistingATSuperiorError( at, superior );
                errorsList.add( error );
                errorsMap.put( at, error );
            }
        }

        // Checking syntax
        String syntaxOid = at.getSyntaxOid();
        if ( ( syntaxOid != null ) && ( !"".equals( syntaxOid ) ) )
        {
            if ( schemaHandler.getSyntax( syntaxOid ) == null )
            {
                SchemaError error = new NonExistingSyntaxError( at, syntaxOid );
                errorsList.add( error );
                errorsMap.put( at, error );
            }
        }

        // Equality matching rule
        String equality = at.getEqualityName();
        if ( ( equality != null ) && ( !"".equals( equality ) ) )
        {
            if ( schemaHandler.getMatchingRule( equality ) == null )
            {
                SchemaError error = new NonExistingMatchingRuleError( at, equality,
                    NonExistingMatchingRuleErrorEnum.EQUALITY );
                errorsList.add( error );
                errorsMap.put( at, error );
            }
        }

        // Ordering matching rule
        String ordering = at.getOrderingName();
        if ( ( ordering != null ) && ( !"".equals( ordering ) ) )
        {
            if ( schemaHandler.getMatchingRule( ordering ) == null )
            {
                SchemaError error = new NonExistingMatchingRuleError( at, ordering,
                    NonExistingMatchingRuleErrorEnum.ORDERING );
                errorsList.add( error );
                errorsMap.put( at, error );
            }
        }

        // Substring matching rule
        String substring = at.getSubstrName();
        if ( ( substring != null ) && ( !"".equals( substring ) ) )
        {
            if ( schemaHandler.getMatchingRule( substring ) == null )
            {
                SchemaError error = new NonExistingMatchingRuleError( at, substring,
                    NonExistingMatchingRuleErrorEnum.SUBSTRING );
                errorsList.add( error );
                errorsMap.put( at, error );
            }
        }
    }


    /**
     * Checks the given object class.
     *
     * @param oc
     *      an object class
     */
    private void checkObjectClass( ObjectClassImpl oc )
    {
        removeSchemaObject( oc );

        // Checking OID
        String oid = oc.getOid();
        if ( ( oid != null ) && ( !"".equals( oid ) ) )
        {
            List<?> list = getSchemaElementList( oid );
            if ( ( list != null ) && ( list.size() >= 2 ) )
            {
                int counter = 0;
                Object o = list.get( counter );
                while ( ( oc.equals( o ) ) && ( counter < ( list.size() - 1 ) ) )
                {
                    counter++;
                    o = list.get( counter );
                }
                SchemaError error = new DuplicateOidError( oc, oid, ( SchemaObject ) o );
                errorsList.add( error );
                errorsMap.put( oc, error );
            }
        }

        // Checking aliases
        String[] aliases = oc.getNames();
        if ( ( aliases != null ) && ( aliases.length >= 1 ) )
        {
            for ( String alias : aliases )
            {
                List<?> list = getSchemaElementList( alias );
                if ( ( list != null ) && ( list.size() >= 2 ) )
                {
                    int counter = 0;
                    Object o = list.get( counter );
                    while ( ( oc.equals( o ) ) && ( counter < ( list.size() - 1 ) ) )
                    {
                        counter++;
                        o = list.get( counter );
                    }
                    SchemaError error = new DuplicateAliasError( oc, oid, ( SchemaObject ) o );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                }
            }
        }

        // Checking superiors
        String[] superiors = oc.getSuperClassesNames();
        if ( ( superiors != null ) && ( superiors.length >= 1 ) )
        {
            for ( String superior : superiors )
            {
                if ( schemaHandler.getObjectClass( superior ) == null )
                {
                    SchemaError error = new NonExistingOCSuperiorError( oc, superior );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                }
            }
        }

        // Checking mandatory and optional attributes
        String[] mandatoryATs = oc.getMustNamesList();
        String[] optionalATs = oc.getMayNamesList();
        if ( ( mandatoryATs != null ) && ( optionalATs != null ) )
        {
            List<String> mandatoryATsList = Arrays.asList( mandatoryATs );
            List<String> optionalATsList = Arrays.asList( optionalATs );

            for ( String mandatoryAT : mandatoryATsList )
            {
                if ( optionalATsList.contains( mandatoryAT ) )
                {
                    SchemaError error = new DuplicateMandatoryOptionalAttributeError( oc, mandatoryAT );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                }
            }

        }
    }


    /**
     * Remove the errors and warnings for the given schema element.
     *
     * @param element
     *      a schema element
     */
    private void removeSchemaObject( SchemaObject element )
    {
        // Removing old errors and warnings
        List<?> errors = ( List<?> ) errorsMap.get( element );
        if ( ( errors != null ) && ( errors.size() >= 1 ) )
        {
            for ( Object error : errors )
            {
                errorsList.remove( error );
            }
        }
        errorsMap.remove( element );
        List<?> warnings = ( List<?> ) warningsMap.get( element );
        if ( ( warnings != null ) && ( warnings.size() >= 1 ) )
        {
            for ( Object warning : warnings )
            {
                warningsList.remove( warning );
            }
        }
        warningsMap.remove( element );
    }


    @SuppressWarnings("unchecked")
    private List<?> getSchemaElementList( String id )
    {
        List results = new ArrayList<Object>();

        // Attribute types
        List<?> atList = schemaHandler.getAttributeTypeList( id );
        if ( ( atList != null ) && ( atList.size() >= 1 ) )
        {
            results.addAll( atList );
        }

        // Object classes
        List<?> ocList = schemaHandler.getObjectClassList( id );
        if ( ( ocList != null ) && ( ocList.size() >= 1 ) )
        {
            results.addAll( ocList );
        }

        // Matching rules
        List<?> mrList = schemaHandler.getMatchingRuleList( id );
        if ( ( mrList != null ) && ( mrList.size() >= 1 ) )
        {
            results.addAll( mrList );
        }

        // Syntaxes
        List<?> syntaxesList = schemaHandler.getSyntaxList( id );
        if ( ( syntaxesList != null ) && ( syntaxesList.size() >= 1 ) )
        {
            results.addAll( syntaxesList );
        }

        return results;
    }


    /**
     * Gets the errors.
     *
     * @return
     *      the errors
     */
    public List<SchemaError> getErrors()
    {
        return errorsList;
    }


    /**
     * Gets the warnings.
     *
     * @return
     *      the warnings
     */
    public List<SchemaWarning> getWarnings()
    {
        return warningsList;
    }
}
