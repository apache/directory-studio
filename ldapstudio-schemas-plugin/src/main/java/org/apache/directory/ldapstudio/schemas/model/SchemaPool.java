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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.view.preferences.SchemasEditorPreferencePage;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;


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

    private static SchemaPool instance_;
    private static Object syncObject_ = new Object();

    private List<Schema> schemaList;
    private List<PoolListener> listeners;

    private List<AttributeType> attributeTypes = new ArrayList<AttributeType>();
    private List<ObjectClass> objectClasses = new ArrayList<ObjectClass>();

    private Map<String, AttributeType> attributeTypesMap = new HashMap<String, AttributeType>();
    private Map<String, ObjectClass> objectClassesMap = new HashMap<String, ObjectClass>();

    private static final String SCHEMAS_TAG = "Schemas";
    private static final String SCHEMA_TAG = "Schema";
    private static final String PATH_TAG = "path";


    /**
     * Write the pool configuration to the LDAPStudio preferences backing store.
     * It consists of all the non-core schemas that have been added by the user.
     */
    public void savePool()
    {
        //we only store the references to schemas that have ALREADY
        //been saved. -> url != null
        XMLMemento memento = XMLMemento.createWriteRoot( SCHEMAS_TAG );

        for ( Schema schema : schemaList )
        {
            if ( ( schema.type == Schema.SchemaType.userSchema ) && ( schema.getURL() != null ) )
            {
                IMemento child = memento.createChild( SCHEMA_TAG );
                child.putString( PATH_TAG, schema.getURL().getPath() );
            }
        }

        try
        {
            FileWriter writer = new FileWriter( getSchemaPoolFile() );
            memento.save( writer );
            writer.close();
        }
        catch ( IOException e )
        {
            logger.debug( "Error when saving opened schemas.", e ); //$NON-NLS-1$
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

            FileReader reader = new FileReader( getSchemaPoolFile() );
            XMLMemento memento = XMLMemento.createReadRoot( reader );
            IMemento[] children = memento.getChildren( SCHEMA_TAG );
            for ( IMemento child : children )
            {
                try
                {
                    addAlreadyExistingSchema( Schema.localPathToURL( child.getString( PATH_TAG ) ), //$NON-NLS-1$
                        Schema.SchemaType.userSchema );
                }
                catch ( SchemaCreationException e )
                {
                    logger.debug( "Error loading schema " + child.getString( PATH_TAG ) + " in the pool." ); //$NON-NLS-1$ //$NON-NLS-2$
                }

            }
        }
        catch ( FileNotFoundException e )
        {
            logger.debug( "Error when loading previously opened schemas.", e ); //$NON-NLS-1$
        }
        catch ( WorkbenchException e )
        {
            logger.debug( "Error when loading previously opened schemas.", e ); //$NON-NLS-1$
        }
    }


    private static void initializeWithBundled( SchemaPool pool )
    {
        URL urlcore = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/core.schema" ); //$NON-NLS-1$
        URL urljava = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/java.schema" ); //$NON-NLS-1$
        URL urlnis = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/nis.schema" ); //$NON-NLS-1$
        URL urlsystem = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/system.schema" ); //$NON-NLS-1$
        URL urlautofs = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/autofs.schema" ); //$NON-NLS-1$
        URL urlcorba = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/corba.schema" ); //$NON-NLS-1$
        URL urlcosine = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/cosine.schema" ); //$NON-NLS-1$
        URL urlinetorgperson = Platform.getBundle( Activator.PLUGIN_ID ).getResource(
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
        IEclipsePreferences prefs = new ConfigurationScope().getNode( Activator.PLUGIN_ID );
        String specificPath = prefs.get( SchemasEditorPreferencePage.SPECIFIC_CORE_DIRECTORY, System
            .getProperty( "user.home" ) ); //$NON-NLS-1$

        File dir = new File( specificPath );
        String sCurPath = dir.getAbsolutePath() + File.separator;
        // Get the directorie entries
        String[] safiles = dir.list();
        if ( safiles != null )
        {
            for ( String safile : safiles )
            {
                File curFile = new File( sCurPath + safile );
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

                    IEclipsePreferences prefs = new ConfigurationScope().getNode( Activator.PLUGIN_ID );

                    //2) initialize the pool
                    boolean initialize_with_specified = prefs.getBoolean( SchemasEditorPreferencePage.SPECIFIC_CORE,
                        false );
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
                    boolean save_workspace = prefs.getBoolean( SchemasEditorPreferencePage.SAVE_WORKSPACE, true );
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
        return schemaList.toArray( new Schema[0] );
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
        return objectClassesMap.containsKey( name.toLowerCase() );
    }


    /**
     * Tests if the following attribute type is inside the pool
     * @param name the name of the attribute type to test
     * @return true if inside, false if not
     */
    public boolean containsAttributeType( String name )
    {
        return attributeTypesMap.containsKey( name.toLowerCase() );
    }


    /**
     * Tests if the following element is inside the pool
     * @param name the name of the eleme,t to test
     * @return true if inside, false if not
     */
    public boolean containsSchemaElement( String name )
    {
        return getSchemaElements().containsKey( name.toLowerCase() );
    }


    /**
     * Tests if an objectClass of the following name(s) exists inside the pool
     * -> test each alias of the following objectClass against the content of the pool
     * @param objectClass the objectClass to test
     * @return if inside the pool, false if not
     */
    public boolean containsObjectClass( ObjectClass objectClass )
    {
        return objectClasses.contains( objectClass );
    }


    /**
     * Tests if an attributeType of the following name(s) exists inside the pool
     * -> test each alias of the following attributeType against the content of the pool
     * @param attributeType the attributeType to test
     * @return if inside the pool, false if not
     */
    public boolean containsAttributeType( AttributeType attributeType )
    {
        return attributeTypes.contains( attributeType );
    }


    /**
     * Tests if the given element exists in the pool
     * @param schemaElement the Schema Element to test
     * @return if inside the pool, false if not
     */
    public boolean containsSchemaElement( SchemaElement schemaElement )
    {
        return getSchemaElements().containsKey( schemaElement );
    }


    /**
     * Returns a specific object class
     * @param name the name of the object class to return
     * @return null if the name is not mapped
     */
    public ObjectClass getObjectClass( String name )
    {

        return objectClassesMap.get( name.toLowerCase() );
    }


    /**
     * Returns a specific attribute type
     * @param name the name of the attriute type to return
     * @return null if the name is not mapped
     */
    public AttributeType getAttributeType( String name )
    {
        return attributeTypesMap.get( name.toLowerCase() );
    }


    /**
     * Returns the Object Classes as a Map.
     *
     * @return
     *      the Object Classes as a Map
     */
    public Map<String, ObjectClass> getObjectClassesAsMap()
    {
        return objectClassesMap;
    }


    /**
     * Gets the Attribute Types as a Map.
     * 
     * @return
     *      the Attribute Types as a Map
     */
    public Map<String, AttributeType> getAttributeTypesAsMap()
    {
        return attributeTypesMap;
    }


    /**
     * Get the Schema Elements as a Map
     * 
     * @return
     *      the Schema Elements as a Map
     */
    public Map<String, SchemaElement> getSchemaElements()
    {
        Map<String, SchemaElement> elementsTable = new HashMap<String, SchemaElement>();

        elementsTable.putAll( attributeTypesMap );
        elementsTable.putAll( objectClassesMap );

        return elementsTable;
    }


    /**
     * Accessor to all the objectClasses defined by the schemas stored in the pool
     * @return as an array
     */
    public List<ObjectClass> getObjectClasses()
    {
        return objectClasses;
    }


    /**
     * Accessor to all the attributeType defined by the schemas stored in the pool
     * @return as an array
     */
    public List<AttributeType> getAttributeTypes()
    {
        return attributeTypes;
    }


    /**
     * Adds a bunch of already initialized schemas into the pool
     * @param schemaArray the schema array
     */
    public void addSchemas( Schema[] schemaArray )
    {
        for ( Schema schema : schemaArray )
        {
            addSchema( schema );
        }

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
                s.addListener( this );

                AttributeType[] ats = s.getAttributeTypesAsArray();
                for ( AttributeType at : ats )
                {
                    addAttributeType( at );
                }

                ObjectClass[] ocs = s.getObjectClassesAsArray();
                for ( ObjectClass oc : ocs )
                {
                    addObjectClass( oc );
                }

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
        if ( ( schemaArray != null ) && ( schemaArray.length > 0 ) )
        {
            for ( Schema schema : schemaArray )
            {
                removeSchema( schema );
                schema.removeListener( this );
            }

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

            AttributeType[] ats = s.getAttributeTypesAsArray();
            for ( AttributeType at : ats )
            {
                removeAttributeType( at );
            }

            ObjectClass[] ocs = s.getObjectClassesAsArray();
            for ( ObjectClass oc : ocs )
            {
                removeObjectClass( oc );
            }

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


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.model.SchemaListener#schemaChanged(org.apache.directory.ldapstudio.schemas.model.Schema, org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent)
     */
    public void schemaChanged( Schema originatingSchema, LDAPModelEvent e )
    {
        switch ( e.getReason() )
        {
            case ATAdded:
            case OCAdded:
                elementAdded( e );
                break;

            case ATModified:
                attributeTypeModified( e );
                break;

            case OCModified:
                objectClassModified( e );
                break;

            case ATRemoved:
            case OCRemoved:
                elementRemoved( e );
                break;

            default:
                break;
        }

        for ( PoolListener listener : listeners )
        {
            try
            {
                listener.poolChanged( this, e );
            }
            catch ( Exception e1 )
            {
                logger.debug( "error when notifying " + listener + " of pool modification" ); //$NON-NLS-1$ //$NON-NLS-2$
                e1.printStackTrace(); // TODO remove this
            }
        }
    }


    /**
     * Updates the SchemaPool after an event of type ATAdded or case OCAdded.
     *
     * @param e
     *      the associated event
     */
    private void elementAdded( LDAPModelEvent e )
    {
        SchemaElement schemaElement = ( SchemaElement ) e.getNewValue();

        if ( schemaElement instanceof AttributeType )
        {
            addAttributeType( ( AttributeType ) schemaElement );
        }
        else if ( schemaElement instanceof ObjectClass )
        {
            addObjectClass( ( ObjectClass ) schemaElement );
        }
    }


    /**
     * Updates the SchemaPool after an event of type ATModified.
     *
     * @param e
     *      the associated event
     */
    private void attributeTypeModified( LDAPModelEvent e )
    {
        AttributeType oldAttributeType = ( AttributeType ) e.getOldValue();
        AttributeType newAttributeType = ( AttributeType ) e.getNewValue();

        String[] names = oldAttributeType.getNames();
        for ( int j = 0; j < names.length; j++ )
        {
            attributeTypesMap.remove( names[j].toLowerCase() );
        }
        attributeTypesMap.remove( oldAttributeType.getOid() );

        names = newAttributeType.getNames();
        for ( int j = 0; j < names.length; j++ )
        {
            attributeTypesMap.put( names[j].toLowerCase(), newAttributeType );
        }
        attributeTypesMap.put( newAttributeType.getOid(), newAttributeType );
    }


    /**
     * Updates the SchemaPool after an event of type OCModified.
     *
     * @param e
     *      the associated event
     */
    private void objectClassModified( LDAPModelEvent e )
    {
        ObjectClass oldObjectClass = ( ObjectClass ) e.getOldValue();
        ObjectClass newObjectClass = ( ObjectClass ) e.getNewValue();

        String[] names = oldObjectClass.getNames();
        for ( int j = 0; j < names.length; j++ )
        {
            objectClassesMap.remove( names[j].toLowerCase() );
        }
        objectClassesMap.remove( oldObjectClass.getOid() );

        names = newObjectClass.getNames();
        for ( int j = 0; j < names.length; j++ )
        {
            objectClassesMap.put( names[j].toLowerCase(), newObjectClass );
        }
        objectClassesMap.put( newObjectClass.getOid(), newObjectClass );
    }


    /**
     * Updates the SchemaPool after an event of type ATRemoved or case OCRemoved.
     *
     * @param e
     *      the associated event
     */
    private void elementRemoved( LDAPModelEvent e )
    {
        SchemaElement schemaElement = ( SchemaElement ) e.getOldValue();

        if ( schemaElement instanceof AttributeType )
        {
            removeAttributeType( ( AttributeType ) schemaElement );
        }
        else if ( schemaElement instanceof ObjectClass )
        {
            removeObjectClass( ( ObjectClass ) schemaElement );
        }
    }


    /**
     * Adds an attribute type.
     *
     * @param at
     *      the attribute type to add
     */
    private void addAttributeType( AttributeType at )
    {
        String[] names = at.getNames();
        for ( int j = 0; j < names.length; j++ )
        {
            attributeTypesMap.put( names[j].toLowerCase(), at );
        }
        attributeTypesMap.put( at.getOid(), at );
        attributeTypes.add( at );
    }


    /**
     * Removes an attribute type.
     *
     * @param at
     *      the attribute type to remove
     */
    private void removeAttributeType( AttributeType at )
    {
        String[] names = at.getNames();
        for ( int j = 0; j < names.length; j++ )
        {
            attributeTypesMap.remove( names[j].toLowerCase() );
        }
        attributeTypesMap.remove( at.getOid() );
        attributeTypes.remove( at );
    }


    /**
     * Adds an object class.
     *
     * @param oc
     *      the object class to add
     */
    private void addObjectClass( ObjectClass oc )
    {
        String[] names = oc.getNames();
        for ( int j = 0; j < names.length; j++ )
        {
            objectClassesMap.put( names[j].toLowerCase(), oc );
        }
        objectClassesMap.put( oc.getOid(), oc );
        objectClasses.add( oc );
    }


    /**
     * Removes an object class
     *
     * @param oc
     *      the object class to remove
     */
    private void removeObjectClass( ObjectClass oc )
    {
        String[] names = oc.getNames();
        for ( int j = 0; j < names.length; j++ )
        {
            objectClassesMap.remove( names[j].toLowerCase() );
        }
        objectClassesMap.remove( oc.getOid() );
        objectClasses.remove( oc );
    }


    private File getSchemaPoolFile()
    {
        return Activator.getDefault().getStateLocation().append( "schemaPool.xml" ).toFile();
    }
}
