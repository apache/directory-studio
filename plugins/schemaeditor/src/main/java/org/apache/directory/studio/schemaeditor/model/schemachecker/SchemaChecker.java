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
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandlerAdapter;
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

    /** The errors List */
    private List<Throwable> errorsList;

    /** The errors MultiMap */
    private MultiMap errorsMap;

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

                notifyListeners();
            }
        }


        public void attributeTypeModified( AttributeType at )
        {
            synchronized ( this )
            {

                notifyListeners();
            }
        }


        public void attributeTypeRemoved( AttributeType at )
        {
            synchronized ( this )
            {

                notifyListeners();
            }
        }


        public void objectClassAdded( ObjectClass oc )
        {
            synchronized ( this )
            {

                notifyListeners();
            }
        }


        public void objectClassModified( ObjectClass oc )
        {
            synchronized ( this )
            {

                notifyListeners();
            }
        }


        public void objectClassRemoved( ObjectClass oc )
        {
            synchronized ( this )
            {

                notifyListeners();
            }
        }


        public void schemaAdded( Schema schema )
        {
            synchronized ( this )
            {

                notifyListeners();
            }
        }


        public void schemaRemoved( Schema schema )
        {
            synchronized ( this )
            {

                notifyListeners();
            }
        }
    };


    /**
     * Creates a new instance of SchemaChecker.
     */
    private SchemaChecker()
    {
        listeners = new ArrayList<SchemaCheckerListener>();
        
        schemaManager = new DefaultSchemaManager( new SchemaEditorSchemaLoader() );
        errorsMap = new MultiValueMap();

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
            schemaManager = new DefaultSchemaManager( new SchemaEditorSchemaLoader() );
            errorsMap = new MultiValueMap();
            
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
     * Checks the whole schema.
     */
    private synchronized void checkWholeSchema()
    {
        Job job = new Job( "Checking Schema" )
        {
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {
                    schemaManager.loadAllEnabled();
                }
                catch ( Exception e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                errorsList = schemaManager.getErrors();
                for ( Throwable error : errorsList )
                {
                    if ( error instanceof LdapSchemaException )
                    {
                        LdapSchemaException ldapSchemaException = (LdapSchemaException) error;
                        SchemaObject source = ldapSchemaException.getSource();
                        if ( source != null )
                        {
                            errorsMap.put( source, ldapSchemaException );
                        }
                    }
                }

                notifyListeners();
                monitor.done();

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }


    /**
     * Gets the errors.
     *
     * @return
     *      the errors
     */
    public List<Throwable> getErrors()
    {
        return errorsList;
    }


    /**
     * Gets the warnings.
     *
     * @return
     *      the warnings
     */
    public List<Object> getWarnings()
    {
        // TODO
        return new ArrayList<Object>();
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
        return new ArrayList<Object>();
     //   return ( List<?> ) warningsMap.get( so );
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
