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
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent.Reason;
import org.apache.directory.ldapstudio.schemas.model.Schema.SchemaType;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;


/**
 * This class represents the Schema Pool.
 * 
 * A pool of schema is a common repository for all the currently loaded
 * schemas in LDAP Studio Schemas Editor Plugin.
 */
public class SchemaPool implements SchemaListener
{
    /** The logger */
    private static Logger logger = Logger.getLogger( SchemaPool.class );

    /** The SchemaPool instance */
    private static SchemaPool schemaPool;

    /** The Listeners List */
    private List<PoolListener> listeners;

    /** The Schema List */
    private List<Schema> schemaList;

    /** The Attribute Type List */
    private List<AttributeType> attributeTypes;

    /** The Object Class List */
    private List<ObjectClass> objectClasses;

    /** The Attribute Type Map*/
    private Map<String, AttributeType> attributeTypesMap;

    /** The Object Class Map*/
    private Map<String, ObjectClass> objectClassesMap;

    /** The Schemas Tag */
    private static final String SCHEMAS_TAG = "Schemas"; //$NON-NLS-1$

    /** The Schema Tag */
    private static final String SCHEMA_TAG = "Schema"; //$NON-NLS-1$

    /** the Path Tag */
    private static final String PATH_TAG = "path"; //$NON-NLS-1$


    /**
     * Creates a new instance of SchemaPool.
     */
    private SchemaPool()
    {
        listeners = new ArrayList<PoolListener>();
        schemaList = new ArrayList<Schema>();
        attributeTypes = new ArrayList<AttributeType>();
        objectClasses = new ArrayList<ObjectClass>();
        attributeTypesMap = new HashMap<String, AttributeType>();
        objectClassesMap = new HashMap<String, ObjectClass>();
    }


    /**
     * Returns the unique initialized pool.
     * 
     * @return 
     *      the pool
     */
    public static SchemaPool getInstance()
    {
        if ( schemaPool == null )
        {
            schemaPool = new SchemaPool();
            synchronized ( schemaPool )
            {
                schemaPool.loadSchemas();
            }
        }

        return schemaPool;
    }


    /**
     * Loads the Schemas (Core and User Schemas)
     */
    private void loadSchemas()
    {
        schemaPool.loadCoreSchemas();
        schemaPool.loadUserSchemas();
    }


    /**
     * Loads the Core Schemas Files.
     */
    private void loadCoreSchemas()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        boolean useSpecificCore = store.getBoolean( PluginConstants.PREFS_SCHEMAS_EDITOR_SPECIFIC_CORE );
        if ( useSpecificCore )
        {
            schemaPool.loadCoreSchemasFromSpecifiedLocation();
        }
        else
        {
            schemaPool.loadCoreSchemasFromBundle();
        }
    }


    /**
     * Loads the User Schemas Files.
     */
    public void loadUserSchemas()
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
                    addSchema( Schema.localPathToURL( child.getString( PATH_TAG ) ), //$NON-NLS-1$
                        Schema.SchemaType.userSchema, false );
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


    /**
     * Saves the User Schemas Paths to a XML File.
     */
    public void saveUserSchemasPaths()
    {
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
     * Loads the Core Schemas From the plugin's Bundle.
     */
    private void loadCoreSchemasFromBundle()
    {
        URL urlApache = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/apache.schema" ); //$NON-NLS-1$
        URL urlApachedns = Platform.getBundle( Activator.PLUGIN_ID )
            .getResource( "ressources/schemas/apachedns.schema" ); //$NON-NLS-1$
        URL urlApachemeta = Platform.getBundle( Activator.PLUGIN_ID ).getResource(
            "ressources/schemas/apachemeta.schema" ); //$NON-NLS-1$
        URL urlAutofs = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/autofs.schema" ); //$NON-NLS-1$
        URL urlCollective = Platform.getBundle( Activator.PLUGIN_ID ).getResource(
            "ressources/schemas/collective.schema" ); //$NON-NLS-1$
        URL urlCorba = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/corba.schema" ); //$NON-NLS-1$
        URL urlCore = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/core.schema" ); //$NON-NLS-1$
        URL urlCosine = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/cosine.schema" ); //$NON-NLS-1$
        URL urlDhcp = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/dhcp.schema" ); //$NON-NLS-1$
        URL urlInetorgperson = Platform.getBundle( Activator.PLUGIN_ID ).getResource(
            "ressources/schemas/inetorgperson.schema" ); //$NON-NLS-1$
        URL urlJava = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/java.schema" ); //$NON-NLS-1$
        URL urlKrb5kdc = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/krb5kdc.schema" ); //$NON-NLS-1$
        URL urlNis = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/nis.schema" ); //$NON-NLS-1$
        URL urlSamba = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/samba.schema" ); //$NON-NLS-1$
        URL urlSystem = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/schemas/system.schema" ); //$NON-NLS-1$

        schemaPool.addSchema( urlApache, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlApachedns, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlApachemeta, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlAutofs, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlCollective, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlCorba, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlCore, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlCosine, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlDhcp, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlInetorgperson, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlJava, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlKrb5kdc, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlNis, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlSamba, SchemaType.coreSchema, false );
        schemaPool.addSchema( urlSystem, SchemaType.coreSchema, false );
    }


    /**
     * Loads the Core Schemas Files from the Location specified in the preferences.
     */
    private void loadCoreSchemasFromSpecifiedLocation()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String specificPath = store.getString( PluginConstants.PREFS_SCHEMAS_EDITOR_SPECIFIC_CORE_DIRECTORY );

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
                            schemaPool.addSchema( fileURL, Schema.SchemaType.coreSchema, false );
                        }
                    }
                    catch ( MalformedURLException e )
                    {
                        logger.debug( "error whith the content of the specified core schema directory" ); //$NON-NLS-1$
                    }
                }
            }
        }
    }


    /**
     * Returns all the schemas contained in the pool.
     * 
     * @return 
     *      the schemas stored in a Schema array 
     */
    public Schema[] getSchemas()
    {
        return schemaList.toArray( new Schema[0] );
    }


    /**
     * Returns the List of Object Classes.
     * 
     * @return
     *      the List of Object Classes
     */
    public List<ObjectClass> getObjectClasses()
    {
        return objectClasses;
    }


    /**
     * Return the List of Attribute Types.
     * 
     * @return
     *      the List of Attribute Types
     */
    public List<AttributeType> getAttributeTypes()
    {
        return attributeTypes;
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
     * Returns the schema specified by the given name.
     * 
     * @param name 
     *      the name of the schema to find
     * @return
     *      the schema specified by the given name.
     */
    public Schema getSchema( String name )
    {
        for ( Schema schema : schemaList )
        {
            if ( schema.getName().equals( name ) )
            {
                return schema;
            }
        }

        return null;
    }


    /**
     * Tests if a schema with the given name is inside the pool.
     * 
     * @param name 
     *      the name of the schema 
     * @return 
     *      true if inside
     */
    public boolean containsSchema( String name )
    {
        return ( getSchema( name ) != null );
    }


    /**
     * Tests if the given schema is inside the pool.
     * 
     * @param schema
     *      the name of the schema to test
     * @return
     *      true if inside, false if not
     */
    public boolean containsSchema( Schema schema )
    {
        return schemaList.contains( schema );
    }


    /**
     * Tests if an objectClass with the given name is inside the pool.
     * 
     * 	@param name
     *      the name of the object class to test
     * @return
     *      true if inside, false if not
     */
    public boolean containsObjectClass( String name )
    {
        return objectClassesMap.containsKey( name.toLowerCase() );
    }


    /**
     * Tests if an attribute type with the given name is inside the pool.
     * 
     * @param name
     *      the name of the attribute type to test
     * @return
     *      true if inside, false if not
     */
    public boolean containsAttributeType( String name )
    {
        return attributeTypesMap.containsKey( name.toLowerCase() );
    }


    /**
     * Tests if an element with the given name is inside the pool.
     * 
     * @param name
     *      the name of the eleme,t to test
     * @return
     *      true if inside, false if not
     */
    public boolean containsSchemaElement( String name )
    {
        return getSchemaElements().containsKey( name.toLowerCase() );
    }


    /**
     * Tests if the given object class is inside the pool.
     * 
     * @param objectClass
     *      the objectClass to test
     * @return
     *      if inside the pool, false if not
     */
    public boolean containsObjectClass( ObjectClass objectClass )
    {
        return objectClasses.contains( objectClass );
    }


    /**
     * Tests if the given attributeType is inside the pool.
     * 
     * @param attributeType
     *      the attributeType to test
     * @return
     *      if inside the pool, false if not
     */
    public boolean containsAttributeType( AttributeType attributeType )
    {
        return attributeTypes.contains( attributeType );
    }


    /**
     * Tests if the given element exists in the pool.
     * 
     * @param schemaElement
     *      the Schema Element to test
     * @return
     *      if inside the pool, false if not
     */
    public boolean containsSchemaElement( SchemaElement schemaElement )
    {
        return getSchemaElements().containsKey( schemaElement );
    }


    /**
     * Returns the object class corresponding to the given name.
     * 
     * @param name
     *      the name of the object class to return
     * @return
     *      null if the name is not mapped
     */
    public ObjectClass getObjectClass( String name )
    {

        return objectClassesMap.get( name.toLowerCase() );
    }


    /**
     * Returns the attribute type corresponding to the given name.
     * 
     * @param name
     *      the name of the attriute type to return
     * @return
     *      null if the name is not mapped
     */
    public AttributeType getAttributeType( String name )
    {
        return attributeTypesMap.get( name.toLowerCase() );
    }


    /**
     * Adds a schema to the pool.
     * 
     * @param s
     *      the schema to add
     * 
     * @param notifyListeners
     *      a boolean to indicate if the listeners need to be notified
     */
    private void addSchema( Schema s, boolean notifyListeners )
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

                if ( notifyListeners )
                {
                    notifyChanged( LDAPModelEvent.Reason.SchemaAdded, s );
                }
            }
        }
    }


    /**
     * Adds a new schema to the Schema Pool.
     * 
     * @param name
     *      the name of the new schema
     */
    public void addNewSchema( String name )
    {
        addSchema( new Schema( SchemaType.userSchema, name ), true );
    }


    /**
     * Adds a schema to the Schema Pool.
     *
     * @param url
     *      the url of the schema
     */
    public void addSchema( URL url )
    {
        addSchema( url, SchemaType.userSchema, true );
    }


    /**
     * Adds an already existing schema into the pool (load the schema from a file)
     * @param url the URL to the .schema file
     * @param type the schema type
     * @return the schema that has been added to the pool, null if not added or already in the pool
     * @throws SchemaCreationException if no schema was found at the specified
     * URL or if any error occurs during its initialization.
     */
    /**
     * Adds a schema to the Schema Pool.
     *
     * @param url
     *      the url of the schema
     * @param type
     *      the type of the schema
     * @param notifyListeners
     *      true if the listeners should be notified
     */
    private void addSchema( URL url, SchemaType type, boolean notifyListeners )
    {
        try
        {
            addSchema( new Schema( url, type ), notifyListeners );
        }
        catch ( SchemaCreationException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Removes a schema from the pool.
     * 
     * @param s
     *      the schema to be removed
     */
    public void removeSchema( Schema s )
    {
        removeSchema( s, true );
    }


    /**
     * Removes a schema from the pool.
     * 
     * @param s
     *      the schema to be removed
     * @param notifyListeners
     *      a boolean to indicate if the listeners need to be notified
     */
    private void removeSchema( Schema s, boolean notifyListeners )
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

            if ( notifyListeners )
            {
                notifyChanged( LDAPModelEvent.Reason.SchemaRemoved, s );
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
     * Reloads the Schema Pool.
     */
    public void reload()
    {
        // Removing Core Schemas
        Schema[] schemas = schemaList.toArray( new Schema[0] );
        for ( int i = 0; i < schemas.length; i++ )
        {
            if ( schemas[i].type == SchemaType.coreSchema )
            {
                removeSchema( schemas[i], false );
            }
        }

        // Loading Schemas
        schemaPool.loadCoreSchemas();

        // Notifying Listeners
        for ( PoolListener listener : listeners )
        {
            listener.poolChanged( this, new LDAPModelEvent( Reason.PoolReloaded ) );
        }
    }


    /**
     * Adds a listener to the Schema Pool.
     *
     * @param listener
     *      the listener to add
     */
    public void addListener( PoolListener listener )
    {
        if ( !listeners.contains( listener ) )
            listeners.add( listener );
    }


    /**
     * Removed a Lister from the Schema Pool.
     *
     * @param listener
     *      the listener to remove
     */
    public void removeListener( PoolListener listener )
    {
        listeners.remove( listener );
    }


    /**
     * Notifies listeners of Schema Pool notification
     *
     * @param reason
     *      the reson
     * @param sc
     *      the schema
     */
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


    /**
     * Gets the Schema Pool File (where is store information about the loaded User Schemas).
     *
     * @return
     *      the Schema Pool File
     */
    private File getSchemaPoolFile()
    {
        return Activator.getDefault().getStateLocation().append( "schemaPool.xml" ).toFile(); //$NON-NLS-1$
    }
}
