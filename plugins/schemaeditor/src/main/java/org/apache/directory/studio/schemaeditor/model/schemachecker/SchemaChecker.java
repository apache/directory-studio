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
import org.apache.directory.shared.ldap.model.exception.LdapSchemaException;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandlerAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.schemamanager.SchemaEditorSchemaLoader;
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

    /** The schema manager */
    private SchemaManager schemaManager;

    /** The errors map */
    private MultiMap errorsMap = new MultiValueMap();;

    /** The warnings list */
    private List<SchemaWarning> warningsList = new ArrayList<SchemaWarning>();

    /** The warnings map */
    private MultiMap warningsMap = new MultiValueMap();;

    /** The 'listening to modifications' flag*/
    private boolean listeningToModifications = false;

    /** The listeners List */
    private List<SchemaCheckerListener> listeners = new ArrayList<SchemaCheckerListener>();

    /** The SchemaHandlerListener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        public void attributeTypeAdded( AttributeType at )
        {
            synchronized ( this )
            {
                recheckWholeSchema();
            }
        }


        public void attributeTypeModified( AttributeType at )
        {
            synchronized ( this )
            {
                recheckWholeSchema();
            }
        }


        public void attributeTypeRemoved( AttributeType at )
        {
            synchronized ( this )
            {
                recheckWholeSchema();
            }
        }


        public void objectClassAdded( ObjectClass oc )
        {
            synchronized ( this )
            {
                recheckWholeSchema();
            }
        }


        public void objectClassModified( ObjectClass oc )
        {
            synchronized ( this )
            {
                recheckWholeSchema();
            }
        }


        public void objectClassRemoved( ObjectClass oc )
        {
            synchronized ( this )
            {
                recheckWholeSchema();
            }
        }


        public void schemaAdded( Schema schema )
        {
            synchronized ( this )
            {
                recheckWholeSchema();
            }
        }


        public void schemaRemoved( Schema schema )
        {
            synchronized ( this )
            {
                recheckWholeSchema();
            }
        }
    };


    /**
     * Creates a new instance of SchemaChecker.
     */
    private SchemaChecker()
    {
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
                recheckWholeSchema();
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
            recheckWholeSchema();
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
     * Checks the whole schema.
     */
    private synchronized void recheckWholeSchema()
    {
        Job job = new Job( "Checking Schema" )
        {
            protected IStatus run( IProgressMonitor monitor )
            {
                // Checks the whole schema via the schema manager
                try
                {
                    schemaManager = new DefaultSchemaManager( new SchemaEditorSchemaLoader() );
                    schemaManager.loadAllEnabled();
                }
                catch ( Exception e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // Updates errors and warnings
                updateErrorsAndWarnings();

                // Notify listeners
                notifyListeners();

                monitor.done();

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }


    /**
     * Updates the errors and warnings. 
     */
    private void updateErrorsAndWarnings()
    {
        // Errors
        errorsMap.clear();
        indexErrors();

        // Warnings
        createWarnings();
        warningsMap.clear();
        indexWarnings();
    }


    /**
     * Indexes the errors.
     */
    private void indexErrors()
    {
        for ( Throwable error : schemaManager.getErrors() )
        {
            if ( error instanceof LdapSchemaException )
            {
                LdapSchemaException ldapSchemaException = ( LdapSchemaException ) error;
                SchemaObject source = ldapSchemaException.getSourceObject();
                if ( source != null )
                {
                    SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();

                    if ( source instanceof AttributeType )
                    {
                        source = schemaHandler.getAttributeType( source.getOid() );
                    }
                    else if ( source instanceof LdapSyntax )
                    {
                        source = schemaHandler.getSyntax( source.getOid() );
                    }
                    else if ( source instanceof MatchingRule )
                    {
                        source = schemaHandler.getMatchingRule( source.getOid() );
                    }
                    else if ( source instanceof ObjectClass )
                    {
                        source = schemaHandler.getObjectClass( source.getOid() );
                    }

                    errorsMap.put( source, ldapSchemaException );
                }
            }
        }
    }


    /**
     * Creates the warnings.
     */
    private void createWarnings()
    {
        // Clearing previous warnings
        warningsList.clear();

        // Getting the schema handler to check for schema objects without names (aliases)
        SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();

        if ( schemaHandler != null )
        {
            // Checking attribute types
            for ( AttributeType attributeType : schemaHandler.getAttributeTypes() )
            {
                checkSchemaObjectNames( attributeType );
            }

            // Checking object classes
            for ( ObjectClass objectClass : schemaHandler.getObjectClasses() )
            {
                checkSchemaObjectNames( objectClass );
            }
        }
    }


    /**
     * Checks the names of the given schema object.
     *
     * @param schemaObject the schema object to check
     */
    private void checkSchemaObjectNames( SchemaObject schemaObject )
    {
        if ( ( schemaObject.getNames() == null ) || ( schemaObject.getNames().size() == 0 ) )
        {
            warningsList.add( new NoAliasWarning( schemaObject ) );
        }
    }


    /**
     * Indexes the warnings.
     */
    private void indexWarnings()
    {
        for ( SchemaWarning warning : warningsList )
        {
            warningsMap.put( warning.getSource(), warning );
        }
    }


    /**
     * Gets the errors.
     *
     * @return
     *      the errors
     */
    public List<Throwable> getErrors()
    {
        if ( schemaManager != null )
        {
            return schemaManager.getErrors();
        }
        else
        {
            return new ArrayList<Throwable>();
        }
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
    public List<Object> getWarnings( SchemaObject so )
    {
        return ( List<Object> ) warningsMap.get( so );
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

}
