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

package org.apache.directory.ldapstudio.schemas.model;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.view.preferences.SchemaPreferencePage;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


/**
 * A pool of schema is a common repository for all the currently loaded
 * schemas in LDAP Studio.
 * -> You can add or remove schema from the pool.
 * -> You can obtain a complete list of all the objectClasses and attribute
 * Type currently loaded (even if they are squattered in various schema files).
 * -> the pool of schema can be seen as ONE big schema containing all the 
 * definitions of the currently loaded schemas.
 *
 * NOW USING A SINGLETON DESIGN PATTERN
 * -> use the getPool() static method to get the schema pool
 *
 */
public class SchemaPool implements SchemaListener
{
    private static Logger logger = Logger.getLogger( SchemaPool.class );
    private static final String SCHEMA_URL = "schema_url"; //$NON-NLS-1$
    private static final String SAVED_WORKSPACE = "prefs_saved_workspace"; //$NON-NLS-1$

    private static SchemaPool instance_;
    private static Object syncObject_ = new Object();

    private ArrayList<Schema> schemaList;
    private ArrayList<PoolListener> listeners;


    /**
     * Write the pool configuration to the LDAPStudio preferences backing store.
     * It consists of all the non-core schemas that have been added by the user.
     */
    public void savePool()
    {
        try
        {
            Preferences prefs = new ConfigurationScope().getNode( Application.PLUGIN_ID );
            Preferences saved_workspace = prefs.node( SAVED_WORKSPACE );

            //we only store the references to schemas that have ALREADY
            //been saved. -> url != null
            for ( Schema schema : schemaList )
            {
                if ( ( schema.type == Schema.SchemaType.userSchema ) && ( schema.getURL() != null ) )
                {
                    Preferences schemaPref = saved_workspace.node( schema.getName() );
                    String url = schema.getURL().getPath();
                    schemaPref.put( SCHEMA_URL, url );
                }
            }

            saved_workspace.flush();
        }
        catch ( BackingStoreException e )
        {
            logger.debug( "error when accessing the preferences backing store" ); //$NON-NLS-1$
        }
    }


    /**
     * Read the pool configuration from the LDAPStudio preferences backing store.
     * It consists of all the non-core schemas that have been added by the user.
     */
    public void loadPool()
    {
        try
        {
            Preferences prefs = new ConfigurationScope().getNode( Application.PLUGIN_ID );
            Preferences saved_workspace = prefs.node( SAVED_WORKSPACE );
            String[] schemaNames = saved_workspace.childrenNames();
            for ( String name : schemaNames )
            {
                Preferences node = saved_workspace.node( name );
                try
                {
                    addAlreadyExistingSchema( Schema.localPathToURL( node.get( SCHEMA_URL, "" ) ), //$NON-NLS-1$
                        Schema.SchemaType.userSchema );
                }
                catch ( SchemaCreationException e )
                {
                    logger.debug( "error loading schema " + node.get( SCHEMA_URL, "" ) + " in the pool" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
                finally
                {
                    node.removeNode();
                }
            }
            saved_workspace.flush();
        }
        catch ( BackingStoreException e )
        {
            logger.debug( "error when accessing the preferences backing store" ); //$NON-NLS-1$
        }

    }


    private static void initializeWithBundled( SchemaPool pool )
    {
        URL urlcore = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/schemas/core.schema" ); //$NON-NLS-1$
        URL urljava = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/schemas/java.schema" ); //$NON-NLS-1$
        URL urlnis = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/schemas/nis.schema" ); //$NON-NLS-1$
        URL urlsystem = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/schemas/system.schema" ); //$NON-NLS-1$
        URL urlautofs = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/schemas/autofs.schema" ); //$NON-NLS-1$
        URL urlcorba = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/schemas/corba.schema" ); //$NON-NLS-1$
        URL urlcosine = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/schemas/cosine.schema" ); //$NON-NLS-1$
        URL urlinetorgperson = Platform.getBundle( Application.PLUGIN_ID ).getResource(
            "ressources/schemas/inetorgperson.schema" ); //$NON-NLS-1$

        try
        {
            pool.addAlreadyExistingSchema( urlcore, Schema.SchemaType.coreSchema );
            pool.addAlreadyExistingSchema( urljava, Schema.SchemaType.coreSchema );
            pool.addAlreadyExistingSchema( urlnis, Schema.SchemaType.coreSchema );
            pool.addAlreadyExistingSchema( urlsystem, Schema.SchemaType.coreSchema );
            pool.addAlreadyExistingSchema( urlautofs, Schema.SchemaType.coreSchema );
            pool.addAlreadyExistingSchema( urlcorba, Schema.SchemaType.coreSchema );
            pool.addAlreadyExistingSchema( urlcosine, Schema.SchemaType.coreSchema );
            pool.addAlreadyExistingSchema( urlinetorgperson, Schema.SchemaType.coreSchema );
        }
        catch ( SchemaCreationException e )
        {
            logger
                .debug( "error when inializing the schema pool with bundled schemas. One of the core schemas is not accessible:" + e ); //$NON-NLS-1$
        }
    }


    private static void initializeWithSpecified( SchemaPool pool )
    {
        IEclipsePreferences prefs = new ConfigurationScope().getNode( Application.PLUGIN_ID );
        String specificPath = prefs
            .get( SchemaPreferencePage.SPECIFIC_CORE_DIRECTORY, System.getProperty( "user.home" ) ); //$NON-NLS-1$

        File dir = new File( specificPath );
        String sCurPath = dir.getAbsolutePath() + File.separator;
        // Get the directorie entries
        String[] safiles = dir.list();
        if ( safiles != null )
        {
            for ( int i = 0; i < safiles.length; i++ )
            {
                File curFile = new File( sCurPath + safiles[i] );
                if ( !curFile.isDirectory() )
                {
                    try
                    {
                        URL fileURL = curFile.toURL();
                        if ( Schema.URLtoFileName( fileURL ) != null )
                        {
                            pool.addAlreadyExistingSchema( fileURL, Schema.SchemaType.coreSchema );
                        }
                    }
                    catch ( MalformedURLException e )
                    {
                        logger.debug( "error whith the content of the specified core schema directory" ); //$NON-NLS-1$
                    }
                    catch ( SchemaCreationException e )
                    {
                        logger
                            .debug( "error when inializing the schema pool with specified schemas. One of the core schemas is not accessible:" //$NON-NLS-1$
                                + e );
                    }
                }
            }
        }
    }


    /**
     * Returns the unique initialized pool with all the core schemas pre-loaded
     * @return the pool
     */
    public static SchemaPool getInstance()
    {
        //thread-safe version but not as good as with the static bloc method. Here it would have
        //made a too big static block.
        if ( instance_ == null )
        {
            synchronized ( syncObject_ )
            {
                if ( instance_ == null )
                {
                    //1) create the pool instance
                    SchemaPool pool = new SchemaPool();

                    IEclipsePreferences prefs = new ConfigurationScope().getNode( Application.PLUGIN_ID );

                    //2) initialize the pool
                    boolean initialize_with_specified = prefs.getBoolean( SchemaPreferencePage.SPECIFIC_CORE, false );
                    if ( initialize_with_specified )
                    {
                        //2a) with user-specified core schemas
                        initializeWithSpecified( pool );
                    }
                    else
                    {
                        //2b) or with bundled core schemas
                        initializeWithBundled( pool );
                    }

                    //3) the unique instance is this initialized pool
                    instance_ = pool;

                    //4) load the pool with all the schemas that the user did select the last time
                    //LDAPStudio was launched
                    boolean save_workspace = prefs.getBoolean( SchemaPreferencePage.SAVE_WORKSPACE, true );
                    if ( save_workspace )
                        instance_.loadPool();
                }
            }
        }

        //1) or 5) returns the unique pool instance
        return instance_;
    }


    /**
     * Default constructor, no pre-loaded schemas. Despite the fact that we are using a
     * singleton design pattern, it's a public constructor. It allows you to create
     * temporary unitialized pools (for testing purposes for example). 
     */
    public SchemaPool()
    {
        schemaList = new ArrayList<Schema>();
        listeners = new ArrayList<PoolListener>();
    }


    /**
     * @return the number of schemas in the pool
     */
    public int count()
    {
        return schemaList.size();
    }


    /**
     * Returns all the schemas contained in the pool
     * @return the schemas stored in a Schema array 
     */
    public Schema[] getSchemas()
    {
        return schemaList.toArray( new Schema[]
            {} );
    }


    /**
     * Returns the schema specified by the following name.
     * @param name the name of the schema to find
     * @return
     */
    public Schema getSchema( String name )
    {
        for ( Schema schema : schemaList )
        {
            if ( schema.getName().equals( name ) )
                return schema;
        }
        return null;
    }


    /**
     * Tests if a schema OF THE FOLLOWING NAME is inside the pool, it does NOT test
     * against the schema instances.
     * @param name the name of the schema 
     * @return true if inside
     * @see containsSchema(Schema schema) if you want to test instances
     */
    public boolean containsSchema( String name )
    {
        return ( getSchema( name ) != null );
    }


    /**
     * Tests if the following schema is inside the pool
     * @param schema the name of the schema to test
     * @return true if inside, false if not
     */
    public boolean containsSchema( Schema schema )
    {
        return schemaList.contains( schema );
    }


    /**
     * Tests if the following objectClass is inside the pool
     * 	@param name the name of the object class to test
     * @return true if inside, false if not
     */
    public boolean containsObjectClass( String name )
    {
        return getObjectClass( name ) != null;
    }


    /**
     * Tests if the following attribute type is inside the pool
     * @param name the name of the attribute type to test
     * @return true if inside, false if not
     */
    public boolean containsAttributeType( String name )
    {
        return getAttributeType( name ) != null;
    }


    /**
     * Tests if an objectClass of the following name(s) exists inside the pool
     * -> test each alias of the following objectClass against the content of the pool
     * @param objectClass the objectClass to test
     * @return if inside the pool, false if not
     */
    public boolean containsObjectClass( ObjectClass objectClass )
    {
        String[] names = objectClass.getNames();
        for ( String name : names )
        {
            if ( getObjectClass( name ) != null )
                return true;
        }
        return false;
    }


    /**
     * Tests if an attributeType of the following name(s) exists inside the pool
     * -> test each alias of the following attributeType against the content of the pool
     * @param attributeType the attributeType to test
     * @return if inside the pool, false if not
     */
    public boolean containsAttributeType( AttributeType attributeType )
    {
        String[] names = attributeType.getNames();
        for ( String name : names )
        {
            if ( getAttributeType( name ) != null )
                return true;
        }
        return false;
    }


    /**
     * Returns a specific object class
     * @param name the name of the object class to return
     * @return null if the name is not mapped
     */
    public ObjectClass getObjectClass( String name )
    {
        Hashtable<String, ObjectClass> objectClassTable = getObjectClassesAsHashTableByName();

        return objectClassTable.get( name );
    }


    /**
     * Returns a specific attribute type
     * @param name the name of the attriute type to return
     * @return null if the name is not mapped
     */
    public AttributeType getAttributeType( String name )
    {
        Hashtable<String, AttributeType> attributeTypeTable = getAttributeTypesAsHashTableByName();

        return attributeTypeTable.get( name );
    }


    /**
     * Accessor to all the objectClasses defined by the schemas stored in the pool
     * @return as an (name, objectClass) hashtable 
     */
    public Hashtable<String, ObjectClass> getObjectClassesAsHashTableByName()
    {
        Hashtable<String, ObjectClass> objectClassTable = new Hashtable<String, ObjectClass>();

        for ( Schema schema : schemaList )
        {
            Hashtable<String, ObjectClass> temp = schema.getObjectClassesAsHashTable();
            if ( temp != null )
                objectClassTable.putAll( temp );
        }
        return objectClassTable;
    }


    /**
     * Accessor to all the objectClasses defined by the schemas stored in the pool
     * @return as an (oid, ObjectClass) hashtable
     */
    public Hashtable<String, ObjectClass> getObjectClassesAsHashTableByOID()
    {
        Hashtable<String, ObjectClass> classesTable = new Hashtable<String, ObjectClass>();

        ObjectClass[] ObjectClasses = getObjectClassesAsArray();
        for ( ObjectClass class1 : ObjectClasses )
        {
            classesTable.put( class1.getOid(), class1 );
        }

        return classesTable;
    }


    /**
     * Accessor to all the attributeType defined by the schemas stored in the pool
     * @return as an (name, attributeType) hashtable
     */
    public Hashtable<String, AttributeType> getAttributeTypesAsHashTableByName()
    {
        Hashtable<String, AttributeType> attributeTypeTable = new Hashtable<String, AttributeType>();

        for ( Schema schema : schemaList )
        {
            Hashtable<String, AttributeType> temp = schema.getAttributeTypesAsHashTable();
            if ( temp != null )
                attributeTypeTable.putAll( temp );
        }
        return attributeTypeTable;
    }


    /**
     * Accessor to all the attributeType defined by the schemas stored in the pool
     * @return as an (oid, attributeType) hashtable
     */
    public Hashtable<String, AttributeType> getAttributeTypesAsHashTableByOID()
    {
        Hashtable<String, AttributeType> attributeTypeTable = new Hashtable<String, AttributeType>();

        AttributeType[] attributeTypes = getAttributeTypesAsArray();
        for ( AttributeType type : attributeTypes )
        {
            attributeTypeTable.put( type.getOid(), type );
        }

        return attributeTypeTable;
    }


    /**
     * Accessor to all the schema elements (attribute types and object classes) defined by 
     * the schemas stored in the pool
     * @return as an (oid, SchemaElement) hashtable
     */
    public Hashtable<String, SchemaElement> getSchemaElementsAsHashTableByOID()
    {
        Hashtable<String, SchemaElement> elementsTable = new Hashtable<String, SchemaElement>();

        AttributeType[] attributeTypes = getAttributeTypesAsArray();
        ObjectClass[] objectClasses = getObjectClassesAsArray();

        for ( ObjectClass class1 : objectClasses )
        {
            elementsTable.put( class1.getOid(), class1 );
        }

        for ( AttributeType type : attributeTypes )
        {
            elementsTable.put( type.getOid(), type );
        }

        return elementsTable;
    }


    /**
     * Accessor to all the objectClasses defined by the schemas stored in the pool
     * @return as an array
     */
    public ObjectClass[] getObjectClassesAsArray()
    {
        Set<ObjectClass> set = new HashSet<ObjectClass>();
        set.addAll(getObjectClassesAsHashTableByName().values());
        return set.toArray(new ObjectClass[0]);
    }


    /**
     * Accessor to all the attributeType defined by the schemas stored in the pool
     * @return as an array
     */
    public AttributeType[] getAttributeTypesAsArray()
    {
        Set<AttributeType> set = new HashSet<AttributeType>();
        set.addAll(getAttributeTypesAsHashTableByName().values());
        return set.toArray(new AttributeType[0]);
    }


    /**
     * Adds a bunch of already initialized schemas into the pool
     * @param schemaArray the schema array
     */
    public void addSchemas( Schema[] schemaArray )
    {
        for ( int i = 0; i < schemaArray.length; i++ )
            addSchema( schemaArray[i] );

        //notify of the changement
        //notifyChanged(LDAPModelEvent.Reason.multipleSchemaAdded,null);
    }


    /**
     * Adds an already initialized schema into the pool
     * @param s the schema to be added
     * @return true if the schema has been added
     */
    public boolean addSchema( Schema s )
    {
        if ( s != null )
        {
            if ( !containsSchema( s.getName() ) )
            {
                schemaList.add( s );
                //we register as a listener of the schema
                s.addListener( this );
                //we notify our listeners that a schema has been added
                notifyChanged( LDAPModelEvent.Reason.SchemaAdded, s );
                return true;
            }
        }
        return false;
    }


    /**
     * Adds a new schema into the pool (not loaded from file)
     * @param name the name of the new schema
     * @param type the schema type
     * @return the schema that has been added to the pool, null if not added or already in the pool
     */
    public Schema addSchema( String name, Schema.SchemaType type )
    {
        Schema temp = new Schema( type, name );

        if ( addSchema( temp ) )
            return temp;

        return null;
    }


    /**
     * Adds an already existing schema into the pool (load the schema from a file)
     * @param path the path to the .schema file
     * @param type the schema type
     * @return the schema that has been added to the pool, null if not added or already in the pool
     * @throws SchemaCreationException if no schema was found at the specified
     * path or if any error occurs during its initialization.
     */
    public Schema addAlreadyExistingSchema( String path, Schema.SchemaType type ) throws SchemaCreationException
    {
        try
        {
            return addAlreadyExistingSchema( new URL( "file", "localhost", -1, path ), type ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch ( MalformedURLException e )
        {
            throw new SchemaCreationException( "error opening " + path, e ); //$NON-NLS-1$
        }
    }


    /**
     * Adds an already existing schema into the pool (load the schema from a file)
     * @param url the URL to the .schema file
     * @param type the schema type
     * @return the schema that has been added to the pool, null if not added or already in the pool
     * @throws SchemaCreationException if no schema was found at the specified
     * URL or if any error occurs during its initialization.
     */
    public Schema addAlreadyExistingSchema( URL url, Schema.SchemaType type ) throws SchemaCreationException
    {
        Schema temp;
        temp = new Schema( url, type );

        if ( addSchema( temp ) )
            return temp;

        return null;
    }


    /**
     * Removes a bunch of schemas from the pool
     * @param schemaArray the schemas to remove
     */
    public void removeSchemas( Schema[] schemaArray )
    {
        for ( int i = 0; i < schemaArray.length; i++ )
        {
            removeSchema( schemaArray[i] );
            schemaArray[i].removeListener( this );
        }

        if ( schemaArray.length > 0 )
        {
            //notify of the changement
            notifyChanged( LDAPModelEvent.Reason.multipleSchemaRemoved, null );
        }
    }


    /**
     * Removes a schema from the pool
     * @param s the schema to be removed
     */
    public void removeSchema( Schema s )
    {
        if ( s != null )
        {
            schemaList.remove( s );
            s.removeListener( this );
            s.closeAssociatedEditors();
            //notify of the changement
            notifyChanged( LDAPModelEvent.Reason.SchemaRemoved, s );
        }
    }


    /**
     * Removes a schema from the pool
     * @param name the name of the schema to be removed
     */
    public void removeSchema( String name )
    {
        for ( Schema schema : schemaList )
        {
            if ( schema.getName().equals( name ) )
            {
                removeSchema( schema );
                return;
            }
        }
    }


    /**
     * Saves all the schemas contained in the pool
     * @throws Exception if error during the writting process
     */
    public void saveAll() throws Exception
    {
        saveAll( false );
    }


    /**
     * Saves all the schemas contained in the pool
     * @param askForConfirmation if true, will ask form confirmation before saving	
     * @throws Exception if error during the writting process
     */
    public void saveAll( boolean askForConfirmation ) throws Exception
    {
        for ( Schema schema : schemaList )
        {
            schema.save( askForConfirmation );
        }
    }


    /**
     * Clears the pool from all the stored schemas
     * @param saveBefore if true, all the schemas are saved before the pool
     * is cleared
     */
    public void clearPool( boolean saveBefore )
    {
        //save the pool
        if ( saveBefore )
        {
            try
            {
                saveAll();
            }
            catch ( Exception e )
            {
                logger.debug( "error when clearing the pool" ); //$NON-NLS-1$
            }
        }

        //remove all the associations (listeners,...)
        for ( Schema schema : schemaList )
        {
            removeSchema( schema );
        }

        //make sure we have an empty list
        schemaList = new ArrayList<Schema>();

        //notify of the changement
        notifyChanged( LDAPModelEvent.Reason.poolCleared, null );
    }


    /******************************************
     *            Events emmiting             *
     ******************************************/

    public void addListener( PoolListener listener )
    {
        if ( !listeners.contains( listener ) )
            listeners.add( listener );
    }


    public void removeListener( PoolListener listener )
    {
        listeners.remove( listener );
    }


    private void notifyChanged( LDAPModelEvent.Reason reason, Schema sc )
    {
        for ( PoolListener listener : listeners )
        {
            try
            {
                listener.poolChanged( this, new LDAPModelEvent( reason, sc ) );
            }
            catch ( Exception e )
            {
                logger.debug( "error when notifying " + listener + " of pool modification" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }


    /******************************************
     *           Schema Listener Impl         *
     ******************************************/

    public void schemaChanged( Schema originatingSchema, LDAPModelEvent e )
    {
        for ( PoolListener listener : listeners )
        {
            try
            {
                listener.poolChanged( this, e );
            }
            catch ( Exception e1 )
            {
                logger.debug( "error when notifying " + listener + " of pool modification" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
}
