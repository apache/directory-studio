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
package org.apache.directory.studio.schemaeditor.model.schemachecker;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.shared.ldap.model.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandlerAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NonExistingMatchingRuleError.NonExistingMatchingRuleErrorEnum;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * This class represents the SchemaChecker.
 * <p>
 * It is used to check the schema integrity.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaChecker
{
    /** The SchemaChecker instance */
    private static SchemaChecker instance;

    /** The errors List */
    private List<SchemaError> errorsList;

    /** The errors MultiMap */
    private MultiMap errorsMap;

    /** The warnings List */
    private List<SchemaWarning> warningsList;

    /** The warnings MultiMap */
    private MultiMap warningsMap;

    /** The Dependencies MultiMap */
    private MultiMap dependenciesMap;

    /** The Depends On MultiMap */
    private MultiMap dependsOnMap;

    /** The 'listening to modifications' flag*/
    private boolean listeningToModifications = false;

    /** The listeners List */
    private List<SchemaCheckerListener> listeners;

    /** The SchemaHandlerListener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        public void attributeTypeAdded( AttributeType at )
        {
            synchronized ( this )
            {
                List<?> deps = getAndDeleteDependencies( at );

                checkAttributeType( at );

                checkDependencies( deps );

                notifyListeners();
            }
        }


        public void attributeTypeModified( AttributeType at )
        {
            synchronized ( this )
            {
                List<Object> deps = new ArrayList<Object>();
                List<?> atDeps = ( List<?> ) dependenciesMap.get( at );
                if ( atDeps != null )
                {
                    deps.addAll( atDeps );
                }

                checkAttributeType( at );

                checkDependencies( deps );

                notifyListeners();
            }
        }


        public void attributeTypeRemoved( AttributeType at )
        {
            synchronized ( this )
            {
                List<Object> deps = new ArrayList<Object>();
                List<?> atDeps = ( List<?> ) dependenciesMap.get( at );
                if ( atDeps != null )
                {
                    deps.addAll( atDeps );
                }

                removeSchemaObject( at );

                checkDependencies( deps );

                notifyListeners();
            }
        }


        public void objectClassAdded( ObjectClass oc )
        {
            synchronized ( this )
            {
                List<?> deps = getAndDeleteDependencies( oc );

                checkObjectClass( oc );

                checkDependencies( deps );

                notifyListeners();
            }
        }


        public void objectClassModified( ObjectClass oc )
        {
            synchronized ( this )
            {
                List<Object> deps = new ArrayList<Object>();
                List<?> ocDeps = ( List<?> ) dependenciesMap.get( oc );
                if ( ocDeps != null )
                {
                    deps.addAll( ocDeps );
                }

                checkObjectClass( oc );

                checkDependencies( deps );

                notifyListeners();
            }
        }


        public void objectClassRemoved( ObjectClass oc )
        {
            synchronized ( this )
            {
                List<Object> deps = new ArrayList<Object>();
                List<?> ocDeps = ( List<?> ) dependenciesMap.get( oc );
                if ( ocDeps != null )
                {
                    deps.addAll( ocDeps );
                }

                removeSchemaObject( oc );

                checkDependencies( deps );

                notifyListeners();
            }
        }


        public void schemaAdded( Schema schema )
        {
            synchronized ( this )
            {
                List<AttributeType> ats = schema.getAttributeTypes();
                for ( AttributeType at : ats )
                {
                    checkAttributeType( at );
                }

                List<ObjectClass> ocs = schema.getObjectClasses();
                for ( ObjectClass oc : ocs )
                {
                    checkObjectClass( oc );
                }

                notifyListeners();
            }
        }


        public void schemaRemoved( Schema schema )
        {
            synchronized ( this )
            {
                List<AttributeType> ats = schema.getAttributeTypes();
                for ( AttributeType at : ats )
                {
                    removeSchemaObject( at );
                }

                List<ObjectClass> ocs = schema.getObjectClasses();
                for ( ObjectClass oc : ocs )
                {
                    removeSchemaObject( oc );
                }

                notifyListeners();
            }
        }
    };


    /**
     * Creates a new instance of SchemaChecker.
     */
    private SchemaChecker()
    {
        errorsList = new ArrayList<SchemaError>();
        errorsMap = new MultiValueMap();
        warningsList = new ArrayList<SchemaWarning>();
        warningsMap = new MultiValueMap();
        dependenciesMap = new MultiValueMap();
        dependsOnMap = new MultiValueMap();
        listeners = new ArrayList<SchemaCheckerListener>();

        Activator.getDefault().getProjectsHandler().addListener( new ProjectsHandlerAdapter()
        {
            public void openProjectChanged( Project oldProject, Project newProject )
            {
                if ( oldProject != null )
                {
                    oldProject.getSchemaHandler().removeListener( schemaHandlerListener );
                }

                if ( newProject != null )
                {
                    newProject.getSchemaHandler().addListener( schemaHandlerListener );
                }
            }
        } );
    }


    /**
     * Gets the singleton instance of the ProjectsHandler.
     *
     * @return
     *      the singleton instance of the ProjectsHandler
     */
    public static SchemaChecker getInstance()
    {
        if ( instance == null )
        {
            instance = new SchemaChecker();
        }

        return instance;
    }


    /**
     * Enables modifications listening.
     */
    public void enableModificationsListening()
    {
        synchronized ( this )
        {
            if ( !listeningToModifications )
            {
                Activator.getDefault().getSchemaHandler().addListener( schemaHandlerListener );
                listeningToModifications = true;
                checkWholeSchema();
                notifyListeners();
            }
        }
    }


    /**
     * Disables modifications listening.
     */
    public void disableModificationsListening()
    {
        synchronized ( this )
        {
            if ( listeningToModifications )
            {
                Activator.getDefault().getSchemaHandler().removeListener( schemaHandlerListener );
                listeningToModifications = false;
                clearErrorsAndWarnings();
            }
        }
    }


    /**
     * Reloads the content of the schema checker
     */
    public void reload()
    {
        synchronized ( this )
        {
            clearErrorsAndWarnings();
            checkWholeSchema();
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
        dependenciesMap.clear();
        dependsOnMap.clear();
    }


    /**
     * Checks the whole schema.
     */
    private synchronized void checkWholeSchema()
    {
        Job job = new Job( "Checking Schema" )
        {
            protected IStatus run( IProgressMonitor monitor )
            {
                SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
                if ( schemaHandler != null )
                {
                    List<Schema> schemas = schemaHandler.getSchemas();

                    monitor.beginTask( "Checking schemas: ", schemas.size() );

                    for ( Schema schema : schemas )
                    {
                        monitor.subTask( schema.getSchemaName() );

                        List<AttributeType> ats = schema.getAttributeTypes();
                        for ( AttributeType at : ats )
                        {
                            checkAttributeType( at );
                        }

                        List<ObjectClass> ocs = schema.getObjectClasses();
                        for ( ObjectClass oc : ocs )
                        {
                            checkObjectClass( oc );
                        }

                        monitor.worked( 1 );
                    }
                }

                notifyListeners();
                monitor.done();

                return Status.OK_STATUS;
            }
        };

        job.setUser( true );
        job.schedule();
    }


    /**
     * Checks the given attribute type.
     *
     * @param at
     *      an attribute type
     */
    private void checkAttributeType( AttributeType at )
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
        List<String> aliases = at.getNames();
        if ( ( aliases == null ) || ( aliases.size() == 0 ) )
        {
            SchemaWarning warning = new NoAliasWarning( at );
            warningsList.add( warning );
            warningsMap.put( at, warning );
        }
        else if ( ( aliases != null ) && ( aliases.size() >= 1 ) )
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
                    SchemaError error = new DuplicateAliasError( at, alias, ( SchemaObject ) o );
                    errorsList.add( error );
                    errorsMap.put( at, error );
                }
            }
        }

        // Checking superior
        String superior = at.getSuperiorOid();
        if ( ( superior != null ) && ( !"".equals( superior ) ) )
        {
            AttributeType superiorAT = Activator.getDefault().getSchemaHandler().getAttributeType( superior );
            if ( superiorAT == null )
            {
                SchemaError error = new NonExistingATSuperiorError( at, superior );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( superior, at );
                dependsOnMap.put( at, superior );
            }
            else
            {
                dependenciesMap.put( superiorAT, at );
                dependsOnMap.put( at, superiorAT );

                // Checking Usage with superior's
                UsageEnum usage = at.getUsage();
                UsageEnum superiorATUsage = superiorAT.getUsage();
                if ( !usage.equals( superiorATUsage ) )
                {
                    SchemaError error = new DifferentUsageAsSuperiorError( at, superiorAT );
                    errorsList.add( error );
                    errorsMap.put( at, error );
                }

                // Checking Collective with superior's
                boolean collective = at.isCollective();
                boolean superiorATCollective = superiorAT.isCollective();
                if ( superiorATCollective && !collective )
                {
                    SchemaError error = new DifferentCollectiveAsSuperiorError( at, superiorAT );
                    errorsList.add( error );
                    errorsMap.put( at, error );
                }
            }
        }

        // Checking syntax
        String syntaxOid = at.getSyntaxOid();
        if ( ( syntaxOid != null ) && ( !"".equals( syntaxOid ) ) )
        {
            LdapSyntax syntax = Activator.getDefault().getSchemaHandler().getSyntax( syntaxOid );
            if ( syntax == null )
            {
                SchemaError error = new NonExistingSyntaxError( at, syntaxOid );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( syntaxOid, at );
                dependsOnMap.put( at, syntaxOid );
            }
            else
            {
                dependenciesMap.put( syntax, at );
                dependsOnMap.put( at, syntax );
            }
        }

        // Equality matching rule
        String equality = at.getEqualityOid();
        if ( ( equality != null ) && ( !"".equals( equality ) ) )
        {
            MatchingRule equalityMR = Activator.getDefault().getSchemaHandler().getMatchingRule( equality );
            if ( equalityMR == null )
            {
                SchemaError error = new NonExistingMatchingRuleError( at, equality,
                    NonExistingMatchingRuleErrorEnum.EQUALITY );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( equality, at );
                dependsOnMap.put( at, equality );
            }
            else
            {
                dependenciesMap.put( equalityMR, at );
                dependsOnMap.put( at, equalityMR );
            }
        }

        // Ordering matching rule
        String ordering = at.getOrderingOid();
        if ( ( ordering != null ) && ( !"".equals( ordering ) ) )
        {
            MatchingRule orderingMR = Activator.getDefault().getSchemaHandler().getMatchingRule( ordering );
            if ( orderingMR == null )
            {
                SchemaError error = new NonExistingMatchingRuleError( at, ordering,
                    NonExistingMatchingRuleErrorEnum.ORDERING );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( ordering, at );
                dependsOnMap.put( at, ordering );
            }
            else
            {
                dependenciesMap.put( orderingMR, at );
                dependsOnMap.put( at, orderingMR );
            }
        }

        // Substring matching rule
        String substring = at.getSubstringOid();
        if ( ( substring != null ) && ( !"".equals( substring ) ) )
        {
            MatchingRule substringMR = Activator.getDefault().getSchemaHandler().getMatchingRule( substring );
            if ( substringMR == null )
            {
                SchemaError error = new NonExistingMatchingRuleError( at, substring,
                    NonExistingMatchingRuleErrorEnum.SUBSTRING );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( substring, at );
                dependsOnMap.put( at, substring );
            }
            else
            {
                dependenciesMap.put( substringMR, at );
                dependsOnMap.put( at, substringMR );
            }
        }
    }


    /**
     * Checks the given object class.
     *
     * @param oc
     *      an object class
     */
    private void checkObjectClass( ObjectClass oc )
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
        List<String> aliases = oc.getNames();
        if ( ( aliases == null ) || ( aliases.size() == 0 ) )
        {
            SchemaWarning warning = new NoAliasWarning( oc );
            warningsList.add( warning );
            warningsMap.put( oc, warning );
        }
        else if ( ( aliases != null ) && ( aliases.size() >= 1 ) )
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
        List<String> superiors = oc.getSuperiorOids();
        if ( ( superiors != null ) && ( superiors.size() >= 1 ) )
        {
            ObjectClassTypeEnum type = oc.getType();

            for ( String superior : superiors )
            {
                ObjectClass superiorOC = Activator.getDefault().getSchemaHandler().getObjectClass( superior );
                if ( superiorOC == null )
                {
                    SchemaError error = new NonExistingOCSuperiorError( oc, superior );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                    dependenciesMap.put( superior, oc );
                    dependsOnMap.put( oc, superior );
                }
                else
                {
                    dependenciesMap.put( superiorOC, oc );
                    dependsOnMap.put( oc, superiorOC );

                    // Checking Type of Superior Hierarchy
                    ObjectClassTypeEnum superiorOCType = superiorOC.getType();
                    switch ( type )
                    {
                        case ABSTRACT:
                            if ( ( !superiorOCType.equals( ObjectClassTypeEnum.ABSTRACT ) )
                                && ( !superiorOC.getOid().equals( "2.5.6.0" ) ) )
                            {
                                SchemaError error = new ClassTypeHierarchyError( oc, superiorOC );
                                errorsList.add( error );
                                errorsMap.put( oc, error );
                            }
                            break;
                        case AUXILIARY:
                            if ( ( superiorOCType.equals( ObjectClassTypeEnum.STRUCTURAL ) )
                                && ( !superiorOC.getOid().equals( "2.5.6.0" ) ) )
                            {
                                SchemaError error = new ClassTypeHierarchyError( oc, superiorOC );
                                errorsList.add( error );
                                errorsMap.put( oc, error );
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        // Checking mandatory and optional attributes
        List<String> mandatoryATNames = oc.getMustAttributeTypeOids();
        List<String> optionalATNames = oc.getMayAttributeTypeOids();
        if ( ( mandatoryATNames != null ) && ( optionalATNames != null ) )
        {
            for ( String mandatoryATName : mandatoryATNames )
            {
                AttributeType mandatoryAT = Activator.getDefault().getSchemaHandler().getAttributeType(
                    mandatoryATName );
                if ( mandatoryAT == null )
                {
                    SchemaError error = new NonExistingMandatoryATError( oc, mandatoryATName );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                    dependenciesMap.put( mandatoryATName, oc );
                    dependsOnMap.put( oc, mandatoryATName );
                }
                else
                {
                    dependenciesMap.put( mandatoryAT, oc );
                    dependsOnMap.put( oc, mandatoryAT );
                }
            }

            for ( String optionalATName : optionalATNames )
            {
                AttributeType optionalAT = Activator.getDefault().getSchemaHandler().getAttributeType(
                    optionalATName );
                if ( optionalAT == null )
                {
                    SchemaError error = new NonExistingOptionalATError( oc, optionalATName );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                    dependenciesMap.put( optionalATName, oc );
                    dependsOnMap.put( oc, optionalATName );
                }
                else
                {
                    dependenciesMap.put( optionalAT, oc );
                    dependsOnMap.put( oc, optionalAT );
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
            errorsMap.remove( element );
        }
        List<?> warnings = ( List<?> ) warningsMap.get( element );
        if ( ( warnings != null ) && ( warnings.size() >= 1 ) )
        {
            for ( Object warning : warnings )
            {
                warningsList.remove( warning );
            }
            warningsMap.remove( element );
        }

        // Removing 'depends on' and dependencies
        List<?> dependsOn = ( List<?> ) dependsOnMap.get( element );
        if ( dependsOn != null )
        {
            for ( Object dep : dependsOn )
            {
                dependenciesMap.remove( dep, element );
            }
            dependsOnMap.remove( element );
        }
    }


    @SuppressWarnings("unchecked")
    private List<?> getSchemaElementList( String id )
    {
        List results = new ArrayList<Object>();

        // Attribute types
        List<?> atList = Activator.getDefault().getSchemaHandler().getAttributeTypeList( id );
        if ( ( atList != null ) && ( atList.size() >= 1 ) )
        {
            results.addAll( atList );
        }

        // Object classes
        List<?> ocList = Activator.getDefault().getSchemaHandler().getObjectClassList( id );
        if ( ( ocList != null ) && ( ocList.size() >= 1 ) )
        {
            results.addAll( ocList );
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


    /**
     * Adds a SchemaCheckerListener.
     *
     * @param listener
     *      the listener
     */
    public void addListener( SchemaCheckerListener listener )
    {
        if ( !listeners.contains( listener ) )
        {
            listeners.add( listener );
        }
    }


    /**
     * Removes a SchemaCheckerListener.
     *
     * @param listener
     *      the listener
     */
    public void removeListener( SchemaCheckerListener listener )
    {
        listeners.remove( listener );
    }


    /**
     * Notifies the listeners.
     */
    private void notifyListeners()
    {
        for ( SchemaCheckerListener listener : listeners )
        {
            listener.schemaCheckerUpdated();
        }
    }


    /**
     * Gets the errors associated with the given Schema Object
     *
     * @param so
     *      the Schema Object
     * @return
     *      the associated errors
     */
    public List<?> getErrors( SchemaObject so )
    {
        return ( List<?> ) errorsMap.get( so );
    }


    /**
     * Returns whether the given Schema Object has errors.
     *
     * @param so
     *      the Schema Object
     * @return
     *      true if the given Schema Object has errors.
     */
    public boolean hasErrors( SchemaObject so )
    {
        List<?> errors = getErrors( so );

        if ( errors == null )
        {
            return false;
        }
        else
        {
            return errors.size() > 0;
        }
    }


    /**
     * Gets the warnings associated with the given Schema Object
     *
     * @param so
     *      the Schema Object
     * @return
     *      the associated warnings
     */
    public List<?> getWarnings( SchemaObject so )
    {
        return ( List<?> ) warningsMap.get( so );
    }


    /**
     * Returns whether the given Schema Object has warnings.
     *
     * @param so
     *      the Schema Object
     * @return
     *      true if the given Schema Object has errors.
     */
    public boolean hasWarnings( SchemaObject so )
    {
        List<?> warnings = getWarnings( so );

        if ( warnings == null )
        {
            return false;
        }
        else
        {
            return warnings.size() > 0;
        }
    }


    /**
     * Checks the given list of dependencies.
     * 
     * @param deps
     *      the list of dependencies
     */
    public void checkDependencies( List<?> deps )
    {
        if ( deps != null )
        {
            for ( Object object : deps )
            {
                if ( object instanceof AttributeType )
                {
                    checkAttributeType( ( AttributeType ) object );
                }
                else if ( object instanceof ObjectClass )
                {
                    checkObjectClass( ( ObjectClass ) object );
                }
            }
        }
    }


    /**
     * Gets the dependencies for the given schema object
     * and deletes them from the tables.
     *
     * @param sc
     *      the schema object
     * @return
     *      the dependencies for the given schema object
     * and deletes them from the tables.
     */
    @SuppressWarnings("unchecked")
    private List<Object> getAndDeleteDependencies( SchemaObject sc )
    {
        List<Object> deps = new ArrayList<Object>();

        // Checking OID
        String oid = sc.getOid();
        if ( ( oid != null ) && ( !"".equals( oid ) ) )
        {
            List<Object> oidDependencies = ( List<Object> ) dependenciesMap.get( oid );
            if ( oidDependencies != null )
            {
                deps.addAll( oidDependencies );
                dependenciesMap.remove( oid );
                for ( Object oidDependency : oidDependencies )
                {
                    dependsOnMap.remove( oidDependency, oid );
                }
            }
        }

        // Checking aliases
        List<String> aliases = sc.getNames();
        if ( ( aliases != null ) && ( aliases.size() > 0 ) )
        {
            for ( String alias : aliases )
            {
                List<Object> aliasDependencies = ( List<Object> ) dependenciesMap.get( alias );
                if ( aliasDependencies != null )
                {
                    deps.addAll( aliasDependencies );
                    dependenciesMap.remove( alias );
                    for ( Object aliasDependency : aliasDependencies )
                    {
                        dependsOnMap.remove( aliasDependency, alias );
                    }
                }
            }
        }

        return deps;
    }
}
